package local;

/* $Id$ */
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JFrame;
import debug.DEBUG;
import jscope.AsynchDataSource;
import jscope.ConnectionListener;
import jscope.DataProvider;
import jscope.DataServerItem;
import jscope.FrameData;
import jscope.UpdateEvent;
import jscope.UpdateEventListener;
import jscope.WaveData;
import jscope.WaveDataListener;
import jscope.XYData;
import jscope.jScopeFacade;
import jscope.Array.RealArray;

public final class LocalDataProvider implements DataProvider{
    private static final class EventDescriptor{
        String              event;
        int                 evId;
        UpdateEventListener listener;

        EventDescriptor(final UpdateEventListener listener, final String event, final int evId){
            this.listener = listener;
            this.event = event;
            this.evId = evId;
        }

        @Override
        public final boolean equals(final Object obj) {
            if(!(obj instanceof EventDescriptor)) return false;
            final EventDescriptor evDescr = (EventDescriptor)obj;
            return this.listener == evDescr.getListener() && this.event.equals(evDescr.getEvent());
        }

        String getEvent() {
            return this.event;
        }

        int getEvId() {
            return this.evId;
        }

        UpdateEventListener getListener() {
            return this.listener;
        }

        @Override
        public final int hashCode() {
            if(DEBUG.M) System.out.println("# hashCode() is not defined for LocalDataProvider.EventDescriptor");
            return this.listener.hashCode();
        }
    } // EventDescriptor
    final class LocalFrameData implements FrameData{
        byte[]   allFrames;
        byte[][] frames;
        // If the frames are stored in non segmented data, all the frames are read at the same time
        // otherwise they are read when needed
        boolean  isSegmented;
        String   nodeName;
        int      pixelSize;
        int      segIdxs[];
        int      startIdx, endIdx;
        float[]  times;
        int      width, height;

        private final void configure(final String nodeName, String timeName, final float timeMin, final float timeMax) throws IOException {
            if(DEBUG.M) System.out.println("LocalDataProvider.LocalFrameData.configure(\"" + this.nodeName + "\", \"" + timeName + "\", " + timeMin + ", " + timeMax + ")");
            this.nodeName = nodeName;
            this.isSegmented = LocalDataProvider.nativeIsSegmentedNode(this.nodeName);
            if(this.isSegmented){
                this.times = LocalDataProvider.nativeGetSegmentTimes(this.nodeName, timeName, timeMin, timeMax);
                if(this.times == null) throw new IOException(LocalDataProvider.this.errorString());
                this.frames = new byte[this.times.length][];
                this.segIdxs = LocalDataProvider.nativeGetSegmentIdxs(this.nodeName, timeMin, timeMax);
                if(this.segIdxs == null) throw new IOException(LocalDataProvider.this.errorString());
            }else{
                try{
                    LocalDataProvider.this.getString("_jscope_frames = ( " + this.nodeName + " );\"\""); // Caching
                    this.nodeName = "_jscope_frames";
                }catch(final Exception exc){
                    System.out.println("# error " + exc);
                }
            }
            final LocalDataProviderInfo info = LocalDataProvider.nativeGetInfo(this.nodeName, this.isSegmented);
            if(DEBUG.M) System.out.println("LocalDataProvider.getAllTimes.nativeGetInfo() info=" + info);
            if(info == null) throw new IOException(LocalDataProvider.this.errorString());
            this.width = info.dims[0];
            this.height = info.dims[1];
            this.pixelSize = info.pixelSize;
            if(DEBUG.D) System.out.println(">> pixelSize = " + this.pixelSize);
            if(!this.isSegmented){
                if(timeName == null || timeName.trim().equals("")) timeName = "DIM_OF(" + this.nodeName + ")";
                if(DEBUG.D) System.out.println(">> timeName = " + timeName);
                final int[] segs = LocalDataProvider.nativeGetSegmentIdxs(this.nodeName, timeMin, timeMax);
                this.startIdx = segs[0];
                this.endIdx = segs[-1];
                this.times = LocalDataProvider.nativeGetSegmentTimes(this.nodeName, timeName, timeMin, timeMax);
                this.allFrames = LocalDataProvider.nativeGetAllFrames(this.nodeName, this.startIdx, this.endIdx);
                if(DEBUG.A) DEBUG.printByteArray(this.allFrames, this.pixelSize, this.width, this.height, this.times.length);
            }
        }

        @Override
        public final byte[] getFrameAt(final int idx) throws IOException {
            if(DEBUG.M) System.out.println("LocalDataProvider.LocalFrameData.GetFrameAt(" + idx + ")");
            if(this.isSegmented){
                if(this.frames[idx] == null){
                    final int segIdx = this.segIdxs[idx];
                    int segOffset = 0;
                    for(int i = idx - 1; i >= 0 && this.segIdxs[i] == segIdx; i--, segOffset++);
                    this.frames[idx] = LocalDataProvider.nativeGetSegment(this.nodeName, segIdx, segOffset);
                }
                return this.frames[idx];
            }
            final int img_size = this.pixelSize * this.width * this.height;
            final byte[] outFrame = new byte[img_size];
            System.arraycopy(this.allFrames, idx * img_size, outFrame, 0, img_size);
            return outFrame;
        }

        @Override
        public final Dimension getFrameDimension() throws IOException {
            return new Dimension(this.width, this.height);
        }

        @Override
        public final float[] getFrameTimes() throws IOException {
            return this.times;
        }

        @Override
        public final int getFrameType() throws IOException {
            if(DEBUG.M) System.out.println("LocalDataProvider.LocalFrameData.GetFrameType()");
            switch(this.pixelSize){
                case 1:
                    return FrameData.BITMAP_IMAGE_8;
                case 2:
                    return FrameData.BITMAP_IMAGE_16;
                case 4:
                    return FrameData.BITMAP_IMAGE_32;
                default:// 8
                    return FrameData.BITMAP_IMAGE_FLOAT;
            }
        }

        @Override
        public final int getNumFrames() throws IOException {
            if(DEBUG.M) System.out.println("LocalDataProvider.LocalFrameData.GetNumFrames()");
            if(DEBUG.D) System.out.println(">> NumFrames = " + this.times.length);
            return this.times.length;
        }
    } // LocalFrameData
    final class SimpleWaveData implements WaveData{
        public static final int                SEGMENTED_NO       = 2;
        public static final int                SEGMENTED_UNKNOWN  = 3;
        public static final int                SEGMENTED_YES      = 1;
        public static final int                UNKNOWN            = -1;
        private final AsynchDataSource         asynchSource       = null;
        private final tdicache                 c;
        DataProvider                           dp;
        private boolean                        isXLong            = false;
        private int                            numDimensions      = SimpleWaveData.UNKNOWN;
        private int                            segmentMode        = SimpleWaveData.SEGMENTED_UNKNOWN;
        private String                         title              = null;
        private boolean                        titleEvaluated     = false;
        private final Vector<WaveDataListener> waveDataListenersV = new Vector<WaveDataListener>();
        private long                           x2DLong[];
        private String                         xLabel             = null;
        private boolean                        xLabelEvaluated    = false;
        private String                         yLabel             = null;
        private boolean                        yLabelEvaluated    = false;

        public SimpleWaveData(final String _in_y, final String experiment, final long shot){
            this(_in_y, null, experiment, shot);
        }

        public SimpleWaveData(String in_y, final String in_x, final String experiment, final long shot){
            if(DEBUG.M) System.out.println("SimpleWaveData(\"" + in_y + "\", \"" + in_x + "\", \"" + experiment + "\", " + shot + ")");
            in_y = this.checkForAsynchRequest(in_y);
            this.c = new tdicache(in_y, in_x, LocalDataProvider.var_idx++);
            this.segmentMode();
        }

        @Override
        public final void addWaveDataListener(final WaveDataListener listener) {
            if(DEBUG.M) System.out.println("SimpleWaveData.addWaveDataListener()");
            this.waveDataListenersV.addElement(listener);
            if(this.asynchSource != null) this.asynchSource.addDataListener(listener);
        }

        // Check if the passed Y expression specifies also an asynchronous part (separated by the pattern &&&)
        // in case get an implementation of AsynchDataSource
        private final String checkForAsynchRequest(final String expression) {
            if(DEBUG.M) System.out.println("SimpleWaveData.checkForAsynchRequest(\"" + expression + "\")");
            return expression.startsWith("ASYNCH::") ? expression.substring(8) : expression;
        }

        // GAB JULY 2014 NEW WAVEDATA INTERFACE RAFFAZZONATA
        @Override
        public final XYData getData(final double xmin, final double xmax, final int numPoints) throws Exception {
            return this.getData(xmin, xmax, numPoints, false);
        }

        public final XYData getData(final double xmin, final double xmax, final int numPoints, final boolean isLong) throws Exception {
            if(DEBUG.M) System.out.println("SimpleWaveData.XYData(" + xmin + ", " + xmax + ", " + numPoints + ", " + isLong + ")");
            if(this.segmentMode == SimpleWaveData.SEGMENTED_UNKNOWN){
                try{
                    final byte[] retData = LocalDataProvider.nativeGetByteArray("byte(mdsMisc->IsSegmented(" + this.c.yo() + "))");
                    if(retData[0] > 0) this.segmentMode = SimpleWaveData.SEGMENTED_YES;
                    else this.segmentMode = SimpleWaveData.SEGMENTED_NO;
                }catch(final Exception exc){// mdsMisc->IsSegmented failed
                    this.segmentMode = SimpleWaveData.SEGMENTED_NO;
                }
            }
            final String setTimeContext = "";
            final float y[] = LocalDataProvider.getFloatArray(setTimeContext + this.c.y());
            if(DEBUG.D) System.out.println(">> y = " + y);
            if(DEBUG.A) DEBUG.printFloatArray(y, y.length, 1, 1);
            final RealArray xReal = LocalDataProvider.getRealArray(this.c.x());
            if(xReal.isLong){
                this.isXLong = true;
                return new XYData(xReal.getLongArray(), y, 1E12);
            }
            this.isXLong = false;
            final double x[] = xReal.getDoubleArray();
            if(DEBUG.D) System.out.println(">> x = " + x);
            if(DEBUG.A) DEBUG.printDoubleArray(x, x.length, 1, 1);
            return new XYData(x, y, 1E12);
        }

        @Override
        public final XYData getData(final int numPoints) throws Exception {
            if(DEBUG.M) System.out.println("SimpleWaveData.getData(" + numPoints + ")");
            return this.getData(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, numPoints);
        }

        @Override
        public final void getDataAsync(final double lowerBound, final double upperBound, final int numPoints) {}

        @Override
        public final int getNumDimension() throws IOException {
            if(DEBUG.M) System.out.println("SimpleWaveData.getNumDimension()");
            if(this.numDimensions != SimpleWaveData.UNKNOWN) return this.numDimensions;
            String expr;
            if(this.segmentMode == SimpleWaveData.SEGMENTED_YES) expr = "GetSegment(" + this.c.yo() + ",0)";
            else expr = this.c.y();
            final int shape[] = LocalDataProvider.getNumDimensions(expr);
            if(DEBUG.D){
                String msg = ">> shape =";
                for(final int element : shape)
                    msg += " " + element;
                System.out.println(msg);
            }
            this.numDimensions = shape.length;
            return shape.length;
        }

        @Override
        public final String getTitle() throws IOException {
            if(DEBUG.M) System.out.println("SimpleWaveData.GetTitle()");
            if(!this.titleEvaluated){
                this.titleEvaluated = true;
                this.title = LocalDataProvider.this.getString("help_of(" + this.c.y() + ")");
            }
            return this.title;
        }

        public final float[] getX_X2D() {
            if(DEBUG.M) System.out.println("SimpleWaveData.getX_X2D()");
            try{
                return LocalDataProvider.getFloatArray("DIM_OF(" + this.c.x() + ", 0)");
            }catch(final Exception exc){
                return null;
            }
        }

        public final float[] getX_Y2D() {
            if(DEBUG.M) System.out.println("SimpleWaveData.getX_Y2D()");
            try{
                return LocalDataProvider.getFloatArray("DIM_OF(" + this.c.x() + ", 1)");
            }catch(final Exception exc){
                return null;
            }
        }

        public final float[] getX_Z() {
            if(DEBUG.M) System.out.println("SimpleWaveData.getX_Z()");
            try{
                return LocalDataProvider.getFloatArray("(" + this.c.x() + ")");
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public final double[] getX2D() {
            if(DEBUG.M) System.out.println("SimpleWaveData.getX2D()");
            try{
                final RealArray realArray = LocalDataProvider.getRealArray("DIM_OF(" + this.c.y() + ", 0)");
                if(realArray.isLong){
                    this.isXLong = true;
                    this.x2DLong = realArray.getLongArray();
                    return null;
                }
                this.x2DLong = null;
                return realArray.getDoubleArray();
                // return GetFloatArray(in);
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public final long[] getX2DLong() {
            if(DEBUG.M) System.out.println("SimpleWaveData.getX2DLong()");
            return this.x2DLong;
        }

        @Override
        public final String getXLabel() throws IOException {
            if(DEBUG.M){
                System.out.println("SimpleWaveData.GetXLabel()");
            }
            if(!this.xLabelEvaluated){
                this.xLabelEvaluated = true;
                this.xLabel = LocalDataProvider.this.getString("Units(" + this.c.x() + ")");
            }
            return this.xLabel;
        }

        @Override
        public final float[] getY2D() {
            if(DEBUG.M) System.out.println("SimpleWaveData.getY2D()");
            try{
                return LocalDataProvider.nativeGetFloatArray("DIM_OF(" + this.c.y() + ", 1)");
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public final String getYLabel() throws IOException {
            if(DEBUG.M) System.out.println("SimpleWaveData.GetYLabel()");
            if(!this.yLabelEvaluated){
                this.yLabelEvaluated = true;
                if(this.getNumDimension() > 1){
                    if(this.segmentMode == SimpleWaveData.SEGMENTED_YES) this.yLabel = LocalDataProvider.this.getString("Units(Dim_of(GetSegment(" + this.c.yo() + ",0),1))");
                    else this.yLabel = LocalDataProvider.this.getString("Units(Dim_of(" + this.c.y() + ",1))");
                }else{
                    if(this.segmentMode == SimpleWaveData.SEGMENTED_YES) this.yLabel = LocalDataProvider.this.getString("Units(GetSegment(" + this.c.yo() + ",0))");
                    else this.yLabel = LocalDataProvider.this.getString("Units(" + this.c.y() + ")");
                }
            }
            return this.yLabel;
        }

        @Override
        public final float[] getZ() {
            if(DEBUG.M) System.out.println("SimpleWaveData.getZ()");
            try{
                return LocalDataProvider.getFloatArray(this.c.y());
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public final String getZLabel() throws IOException {
            if(DEBUG.M) System.out.println("SimpleWaveData.GetZLabel()");
            return LocalDataProvider.this.getString("Units(" + this.c.y() + ")");
        }

        @Override
        public final boolean isXLong() {
            return this.isXLong;
        }

        private final void segmentMode() {
            if(DEBUG.M) System.out.println("SimpleWaveData.SegmentMode()");
            if(this.segmentMode == SimpleWaveData.SEGMENTED_UNKNOWN){
                final String expr = "[GetNumSegments(" + this.c.yo() + ")]";
                try{// fast using in_y as NumSegments is a node property
                    final int[] numSegments = LocalDataProvider.nativeGetIntArray(expr);
                    if(numSegments == null) this.segmentMode = SimpleWaveData.SEGMENTED_UNKNOWN;
                    else if(numSegments[0] > 0) this.segmentMode = SimpleWaveData.SEGMENTED_YES;
                    else this.segmentMode = SimpleWaveData.SEGMENTED_NO;
                }catch(final Exception exc){// happens if expression is not a plain node path
                    if(DEBUG.M) System.err.println("# SimpleWaveData.SegmentMode, \"" + expr + "\": " + exc);
                    this.segmentMode = SimpleWaveData.SEGMENTED_UNKNOWN;
                }
            }
        }

        @Override
        public final void setContinuousUpdate(final boolean continuousUpdate) {}
    } // END Inner Class SimpleWaveData
    private final class tdicache{
        private final String  in_x;
        private final String  in_y;
        private boolean       useCache = true;
        private final String  var;
        private String        xc;
        private final boolean xdim;
        private String        yc;
        private byte          ykind;

        public tdicache(final String _in_y, final String _in_x, final int _var){
            this(_in_y, _in_x, Integer.toString(_var));
        }

        public tdicache(final String _in_y, final String _in_x, final String _var){
            this.in_y = _in_y;
            this.var = "_jscope_" + _var;
            this.xdim = _in_x == null;
            if(this.xdim) this.in_x = "DIM_OF(" + this.in_y + ")";
            else this.in_x = _in_x;
            if(this.in_y != "[]"){
                this.y();
                this.useCache = this.useCache & (this.ykind == -62); // only use on "EXT_FUNCTIONS" such as the archive interface
                if(!this.useCache){
                    this.yc = this.in_y;
                    this.xc = this.in_x;
                }else System.out.println("Caching " + _in_y);
                return;
            }
            this.ykind = -1;
            this.useCache = false;
        }

        @Override
        protected final void finalize() {
            LocalDataProvider.this.mdsValue("DEALLOCATE(['" + this.xc + "','" + this.yc + "'])");
        }

        public final String x() {
            if(this.xc == null){
                if(this.xdim) this.y();
                else{
                    this.xc = this.var + "x";
                    final String expr = "STATEMENT(" + this.xc + "=" + this.in_x + ",[KIND(" + this.xc + ")])";
                    if(LocalDataProvider.nativeGetByteArray(expr) == null){
                        if(DEBUG.D) System.out.println(">> tdicache x (" + expr + ")");
                        System.err.println(this.in_x + ": " + LocalDataProvider.nativeErrorString());
                        this.xc = this.in_x;
                    }
                }
            }
            return this.xc;
        }

        @SuppressWarnings("unused")
        public final String xo() {
            return this.in_x;
        }

        public final String y() {
            if(!this.useCache) return this.in_y;
            if(this.yc == null){
                this.yc = this.var + "y";
                final String expr = "STATEMENT(" + this.yc + "=" + this.in_y + ",[KIND(" + this.yc + ")])";
                try{
                    this.ykind = LocalDataProvider.nativeGetByteArray(expr)[0];
                    if(DEBUG.D) System.out.println(">> tdicache y ( " + expr + " -> " + this.ykind + " ) )");
                }catch(final NullPointerException e){
                    System.err.println(this.in_y + ": " + LocalDataProvider.nativeErrorString());
                    this.yc = "";
                }
                if(this.xdim) this.xc = "DIM_OF(" + this.yc + ")";
            }
            return this.yc;
        }

        public final String yo() {
            return this.in_y;
        }
    }
    private final static int MAX_PIXELS        = 2000;
    private final static int RESAMPLE_TRESHOLD = 1000000000;
    private static int       var_idx           = 0;

    static{
        final boolean is64bit = System.getProperty("sun.arch.data.model", "").contains("64");
        final boolean isWin = System.getProperty("os.name", "").startsWith("Win");
        final String libpath = " /local/lib/" + (is64bit ? "amd64" : "i386") + (isWin ? "/" : "/lib");
        final ArrayList<String> libsfilename = new ArrayList<String>(9);
        final ArrayList<File> libsXported = new ArrayList<File>(libsfilename.size());
        final String suffix = isWin ? ".dll" : ".so";
        libsfilename.add("LocalDataProvider");
        libsfilename.add("MdsShr");
        libsfilename.add("TdiShr");
        libsfilename.add("TreeShr");
        if(isWin){
            libsfilename.add("iconv");
            libsfilename.add("libdl");
            libsfilename.add("libwinpthread-1");
        }
        try{
            final byte[] buffer = new byte[1024];
            final File tempdir = new File(System.getProperty("user.dir"));
            for(final String libfilename : libsfilename)
                try{
                    final String path = libpath + libfilename + suffix;
                    final File out = new File(tempdir.getAbsolutePath(), libfilename + suffix);
                    if(out.exists()) out.delete();
                    if(out.exists()) continue;
                    out.createNewFile();
                    if(!out.exists()){ throw new FileNotFoundException("Could not create file '" + out.getAbsolutePath() + "'."); }
                    libsXported.add(out);
                    out.deleteOnExit();
                    int readBytes;
                    final InputStream is = LocalDataProvider.class.getResourceAsStream(path);
                    if(is == null) throw new FileNotFoundException("File '" + path + "' was not found inside JAR");
                    final OutputStream os = new FileOutputStream(out.getAbsolutePath());
                    try{
                        while((readBytes = is.read(buffer)) != -1)
                            os.write(buffer, 0, readBytes);
                    }finally{
                        os.close();
                        is.close();
                    }
                }catch(final Exception e){
                    e.printStackTrace();
                }
            System.loadLibrary(libsfilename.get(0));
        }catch(final Exception e){
            e.printStackTrace();
        }
        for(final File out : libsXported)
            out.delete();
    }

    public synchronized static final float[] getFloatArray(final String in) throws IOException {
        return LocalDataProvider.nativeGetFloatArray(in);
    }

    public static final int[] getNumDimensions(final String expr) {
        final int[] fullDims = LocalDataProvider.nativeGetIntArray("SHAPE( " + expr + " )");
        if(fullDims == null) return null;
        if(fullDims.length == 1) return fullDims;
        // count dimensions == 1
        int numDimensions = 0;
        for(final int fullDim : fullDims){
            if(fullDim != 1) numDimensions++;
        }
        final int[] retDims = new int[numDimensions];
        int j = 0;
        for(final int fullDim : fullDims){
            if(fullDim != 1) retDims[j++] = fullDim;
        }
        return retDims;
    }

    public static synchronized final RealArray getRealArray(final String in) throws IOException {
        final long longArray[] = LocalDataProvider.nativeGetLongArray(in);
        if(longArray != null) return new RealArray(longArray);
        return new RealArray(LocalDataProvider.nativeGetDoubleArray(in));
    }

    /*
    public static final void main(final String[] args) {//TODO:main
        final LocalDataProvider dp = new LocalDataProvider();
        try{
            System.out.println(dp.GetString("TCL('DIR',_out);_out"));
        }catch(final Exception e){
            System.err.println(e);
        }
    }
    */
    private static native String nativeErrorString();

    private static native byte[] nativeGetAllFrames(String nodeName, int startIdx, int endIdx);

    private static native float[] nativeGetAllTimes(String nodeName, String timeNames);

    private static native byte[] nativeGetByteArray(String in);

    private static native double[] nativeGetDoubleArray(String in);

    private static native double nativeGetFloat(String in);

    private static native float[] nativeGetFloatArray(String in);

    private static native LocalDataProviderInfo nativeGetInfo(String nodeName, boolean isSegmented); // returned: width, height, bytesPerPixel

    private static native int[] nativeGetIntArray(String in);

    private static native long[] nativeGetLongArray(String in);

    private static native byte[] nativeGetSegment(String nodeName, int segIdx, int segOffset);

    private static native int[] nativeGetSegmentIdxs(String nodeName, float timeMin, float timeMax);

    private static native float[] nativeGetSegmentTimes(String nodeName, String timeNames, float timeMin, float timeMax);

    private static native String nativeGetString(String in);

    private static native boolean nativeIsSegmentedNode(String nodeName);

    private static native int nativeRegisterEvent(String event, int idx);

    private static native void nativeSetEnvironmentSpecific(String name, String value);

    private static native void nativeUnregisterEvent(int evId);

    private static native void nativeUpdate(String exp, long s);

    private final static void setResampleLimits(final double min, final double max) {
        if(DEBUG.M) System.out.println("LocalDataProvider.setResampleLimits(" + min + ", " + max + ")");
        String limitsExpr;
        if(Math.abs(min) > LocalDataProvider.RESAMPLE_TRESHOLD || Math.abs(max) > LocalDataProvider.RESAMPLE_TRESHOLD){
            final long maxSpecific = jScopeFacade.convertToSpecificTime((long)max);
            final long minSpecific = jScopeFacade.convertToSpecificTime((long)min);
            final long dt = (maxSpecific - minSpecific) / LocalDataProvider.MAX_PIXELS;
            limitsExpr = "JavaSetResampleLimits(" + minSpecific + "UQ," + maxSpecific + "UQ," + dt + "UQ)";
        }else{
            final double dt = (max - min) / LocalDataProvider.MAX_PIXELS;
            limitsExpr = "JavaSetResampleLimits(" + min + "," + max + "," + dt + ")";
        }
        LocalDataProvider.nativeGetFloat(limitsExpr);
    }
    // Subclass LocalDataProvider will return false
    String                  error      = null;
    Vector<String>          eventNames = new Vector<String>();
    String                  experiment;
    Vector<EventDescriptor> listeners  = new Vector<EventDescriptor>();
    long                    shot;
    private File            tempdir;
    public Object           updateWorker;

    @Override
    public void abort() {
        // TODO Auto-generated method stub
    }

    @Override
    public final void addConnectionListener(final ConnectionListener l) {}

    @Override
    public final void addUpdateEventListener(final UpdateEventListener l, final String event) {
        if(DEBUG.M) System.out.println("LocalDataProvider.AddUpdateEventListener(" + l + ", \"" + event + "\")");
        int evId;
        int idx;
        try{
            evId = LocalDataProvider.this.getEventId(event);
            idx = this.eventNames.indexOf(event);
        }catch(final Exception exc){
            idx = this.eventNames.size();
            this.eventNames.addElement(event);
            evId = LocalDataProvider.nativeRegisterEvent(event, idx);
        }
        this.listeners.addElement(new EventDescriptor(l, event, evId));
    }

    @Override
    public final boolean checkProvider() {
        try{
            this.getShots("0");
            return true;
        }catch(final IOException exc){}
        return false;
    }

    @Override
    public void dispose() {}

    @Override
    public final String errorString() {
        return LocalDataProvider.nativeErrorString();
    }

    public final void fireEvent(final int nameIdx) {
        if(DEBUG.M) System.out.println("LocalDataProvider.fireEvent(" + nameIdx + ")");
        final String event = this.eventNames.elementAt(nameIdx);
        for(int idx = 0; idx < this.listeners.size(); idx++){
            final EventDescriptor evDescr = this.listeners.elementAt(idx);
            if(evDescr.getEvent().equals(event)) evDescr.getListener().processUpdateEvent(new UpdateEvent(this, event));
        }
    }

    @Override
    public final Class getDefaultBrowser() {
        return null;
    }

    public final int getEventId(final String event) throws Exception {
        if(DEBUG.M) System.out.println("LocalDataProvider.getEventId(\"" + event + "\")");
        for(int idx = 0; idx < this.listeners.size(); idx++){
            final EventDescriptor evDescr = this.listeners.elementAt(idx);
            if(event.equals(evDescr.getEvent())) return evDescr.getEvId();
        }
        throw(new Exception());
    }

    @Override
    public synchronized final float getFloat(final String in) throws IOException {
        if(DEBUG.M) System.out.println("LocalDataProvider.GetFloat(\"" + in + "\")");
        return (float)LocalDataProvider.nativeGetFloat(in);
    }

    @Override
    public final FrameData getFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        if(DEBUG.M) System.out.println("LocalDataProvider.GetFrameData(\"" + in_y + "\", \"" + in_x + "\", " + time_min + ", " + time_max + ")");
        final LocalFrameData frameData = new LocalFrameData();
        frameData.configure(in_y, in_x, time_min, time_max);
        return frameData;
    }

    @Override
    public final String getLegendString(final String s) {
        return s;
    }

    @Override
    public final long[] getShots(final String in) throws IOException {
        if(DEBUG.M) System.out.println("LocalDataProvider.GetShots(\"" + in + "\")");
        try{
            final int shots[] = LocalDataProvider.nativeGetIntArray(in.trim());
            final long lshots[] = new long[shots.length];
            for(int i = 0; i < shots.length; i++)
                lshots[i] = shots[i];
            return lshots;
        }catch(final UnsatisfiedLinkError e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public final String getString(final String in) throws IOException {
        return LocalDataProvider.nativeGetString(in);
    }

    @Override
    public final WaveData getWaveData(final String in) {
        return new SimpleWaveData(in, this.experiment, this.shot);
    }

    @Override
    public final WaveData getWaveData(final String in_y, final String in_x) {
        return new SimpleWaveData(in_y, in_x, this.experiment, this.shot);
    }

    @Override
    public final int inquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    @Override
    public void join() {
        // TODO Auto-generated method stub
    }

    public final void mdsValue(final String exp) {
        try{
            this.getFloat(exp + ";1.");
        }catch(final Exception e){}
    }

    @Override
    public final void removeConnectionListener(final ConnectionListener l) {}

    @Override
    public final void removeUpdateEventListener(final UpdateEventListener l, final String event) {
        if(DEBUG.M) System.out.println("LocalDataProvider.RemoveUpdateEventListener(" + l + ", \"" + event + "\")");
        final int idx = this.listeners.indexOf(new EventDescriptor(l, event, 0));
        if(idx != -1){
            final int evId = this.listeners.elementAt(idx).getEvId();
            this.listeners.removeElementAt(idx);
            try{
                this.getEventId(event);
            }catch(final Exception exc){
                LocalDataProvider.nativeUnregisterEvent(evId);
            }
        }
    }

    @Override
    public final void setArgument(final String arg) {}

    @Override
    public final void setEnvironment(final String exp) throws IOException {
        if(exp.contains("=")){
            final String[] parts = exp.split("=", 2);
            LocalDataProvider.nativeSetEnvironmentSpecific(parts[0], parts[1]);
        }else this.mdsValue(exp);
    }

    @Override
    public final boolean supportsTunneling() {
        return false;
    }

    @Override
    public final void update(final String exp, final long s) {
        if(DEBUG.M) System.out.println("LocalDataProvider.Update(\"" + exp + "\", " + s + ")");
        LocalDataProvider.var_idx = 0;
        LocalDataProvider.nativeUpdate(exp, s);
    }
}

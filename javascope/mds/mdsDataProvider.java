package mds;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import debug.DEBUG;
import jScope.Array;
import jScope.Array.AllFrames;
import jScope.Array.ByteArray;
import jScope.Array.RealArray;
import jScope.AsynchDataSource;
import jScope.ConnectionEvent;
import jScope.ConnectionListener;
import jScope.DataProvider;
import jScope.DataServerItem;
import jScope.FrameData;
import jScope.Frames;
import jScope.UpdateEventListener;
import jScope.WaveData;
import jScope.WaveDataListener;
import jScope.XYData;
import jScope.jScopeFacade;

public class mdsDataProvider implements DataProvider{
    class SegmentedFrameData implements FrameData{
        int                          bytesPerPixel;
        Dimension                    dim;
        int                          framesPerSegment;
        int                          mode;
        final mdsDataProvider.Signal sig;
        int                          startSegment, endSegment, actSegments;
        float                        time_max, time_min;
        float                        times[];

        public SegmentedFrameData(final mdsDataProvider.Signal sig, final float time_min, final float time_max) throws IOException{
            if(DEBUG.M) System.out.println("mdsDataProvider.SegmentedFrameData(\"" + sig + "\", " + time_min + ", " + time_max + ")");
            this.sig = sig;
            this.time_min = time_min;
            this.time_max = time_max;
            this.findSegments();
            this.actSegments = this.endSegment - this.startSegment + 1;
            if(DEBUG.D) System.out.println(this.actSegments + " : form " + this.startSegment + " upto " + this.endSegment);
            // Get Frame Dimension and frames per segment
            final Signal.Segment seg = sig.getSegment(0);
            if(seg.shape.length != 3 && (seg.shape.length != 4 || seg.shape[2] != 3)) throw new IOException("Invalid number of segment dimensions: " + Arrays.toString(seg.shape));
            this.dim = new Dimension(seg.shape[0], seg.shape[1]);
            this.framesPerSegment = seg.dim.length;
            this.bytesPerPixel = seg.shape.length == 4 ? Integer.BYTES : sig.dataSize;
            this.mode = sig.frameType;
            // Get Frame times
            try{
                if(this.framesPerSegment == 1){ // We assume in this case that start time is the same of the frame time
                    this.times = new float[this.actSegments];
                    for(int i = 0; i < this.actSegments; i++)
                        this.times[i] = sig.seglimits[(this.startSegment + i) * 2];
                }else{// Get segment times. We assume that the same number of frames is contained in every segment
                    this.times = new float[this.actSegments * this.framesPerSegment];
                    final float segt[] = new float[seg.dim.length];
                    System.arraycopy(seg.dim, 0, segt, 0, seg.dim.length);
                    for(int i = 0; i < segt.length; i++)
                        segt[i] = segt[i] - sig.seglimits[0];
                    for(int i = 0; i < this.actSegments; i++){
                        for(int j = 0; j < seg.dim.length; j++)
                            this.times[i * seg.dim.length + j] = segt[j] + sig.seglimits[(this.startSegment + i) * 2];
                    }
                }
            }catch(final Exception e){
                System.err.println(e);
            }
            if(DEBUG.D) System.out.println(this.times[0] + "," + this.times[1] + ", ... ," + this.times[this.times.length - 1]);
        }

        private void findSegments() throws IOException {
            if(DEBUG.M) System.out.println("mdsDataProvider.SegmentedFrameData.findSegments() @ " + this.sig.y);
            try{
                final float[] limits = this.sig.seglimits;
                for(this.startSegment = 0; this.startSegment < this.sig.numSegments - 1; this.startSegment++)
                    if(limits[this.startSegment * 2 + 1] > this.time_min) break;
                for(this.endSegment = this.sig.numSegments - 1; this.endSegment > this.startSegment; this.endSegment--)
                    if(limits[this.endSegment * 2] < this.time_max) break;
            }catch(final Exception e){
                System.err.println("findSegments " + e);
            }
        }

        @Override
        public byte[] GetFrameAt(final int idx) throws IOException {
            if(DEBUG.M) System.out.println("mdsDataProvider.SegmentedFrameData.GetFrameAt(" + idx + ")");
            final int segmentIdx = this.startSegment + idx / this.framesPerSegment;
            final int segmentOffset = (idx % this.framesPerSegment) * this.dim.width * this.dim.height * this.bytesPerPixel;
            final byte[] segment = ((ByteArray)this.sig.getSegment(segmentIdx).dat).buf;
            if(this.framesPerSegment == 1) return segment;
            final byte[] outFrame = new byte[this.dim.width * this.dim.height * this.bytesPerPixel];
            System.arraycopy(segment, segmentOffset, outFrame, 0, this.dim.width * this.dim.height * this.bytesPerPixel);
            return outFrame;
        }

        @Override
        public Dimension GetFrameDimension() {
            return this.dim;
        }

        @Override
        public float[] GetFrameTimes() {
            return this.times;
        }

        @Override
        public int GetFrameType() throws IOException {
            return this.mode;
        }

        @Override
        public int GetNumFrames() {
            return this.actSegments * this.framesPerSegment;
        }
    }
    private final class Signal{
        private final class Segment{
            public final Object  dat;
            public final float[] dim;
            private final int    idx;
            private final String node;
            public final int[]   shape;

            public Segment(final String node, final int idx) throws IOException{
                final String expr = new StringBuilder(255).append("SHAPE(_seg=GetSegment(").append(node).append(',').append(idx).append("))").toString();
                int[] shape = mdsDataProvider.this.GetIntegerArray(expr);
                if(Signal.this.isframe){
                    if(shape.length == 4 && shape[2] == 3){
                        this.dat = this.getRGB("_seg", shape);
                        shape = new int[]{shape[0], shape[1], shape[3]};
                    }else this.dat = mdsDataProvider.this.getByteArray("_seg");
                }else this.dat = mdsDataProvider.this.GetRealArray("_seg");
                this.shape = shape;
                this.dim = mdsDataProvider.this.GetFloatArray("DIM_OF(_seg)");
                this.idx = idx;
                this.node = node;
            }

            @SuppressWarnings("unused")
            public final boolean equals(final String node, final int idx) {
                return this.idx == idx && this.node.equals(node);
            }

            private ByteArray getRGB(final String expr, final int[] shape) throws IOException {
                final int[] ibuf = mdsDataProvider.this.GetIntArray(expr);
                final int imlen = shape[0] * shape[1];
                final ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES * imlen * shape[3]);
                final IntBuffer r = IntBuffer.wrap(ibuf);
                final IntBuffer g = IntBuffer.wrap(ibuf);
                final IntBuffer b = IntBuffer.wrap(ibuf);
                for(int ti = 0; ti < shape[3]; ti++){
                    r.position(imlen * (ti * 3 + 0));
                    g.position(imlen * (ti * 3 + 1));
                    b.position(imlen * (ti * 3 + 2));
                    for(int ip = 0; ip < imlen; ip++)
                        bb.putInt(((r.get() & 0xFF) << 16) | ((g.get() & 0xFF) << 8) | (b.get() & 0xFF));
                }
                return new ByteArray(bb.array(), Descriptor.DTYPE_LONG);
            }
        }
        public final Object   dat, dim;
        public final int      dataSize;
        public final int      frameType;
        public final Object   help_of_dat;
        private final boolean isframe;
        public final int      numSegments;
        public final float[]  seglimits;
        public final Segment  segs[];
        public final int[]    shape;
        public final Object   units_of_dat;
        public final Object   units_of_dim;
        public final String   y, x;

        public Signal(final String in_y, final String in_x, final boolean isframe){
            if(DEBUG.M) System.out.println("mdsDataProvider.Signal(\"" + in_y + "\",\"" + in_x + "\"," + isframe + ")");
            mdsDataProvider.this.error = null;
            this.y = this.getNode(in_y);
            this.isframe = isframe;
            this.x = (in_x == null || in_x == "") ? null : this.getNode(in_x);
            this.numSegments = this.getNumSegments(this.y);
            if(this.numSegments > 0){
                this.seglimits = this.getLimits();
                this.segs = new Segment[this.numSegments];
                this.getSegment(0);
                if(isframe && this.segs[0].dat instanceof ByteArray){
                    this.dataSize = ((ByteArray)this.segs[0].dat).dataSize;
                    this.frameType = ((ByteArray)this.segs[0].dat).getFrameType();
                }else{
                    this.dataSize = -1;
                    this.frameType = -1;
                }
                this.dim = null;
                this.dat = null;
                this.shape = new int[this.segs[0].shape.length];
                System.arraycopy(this.segs[0].shape, 0, this.shape, 0, this.shape.length);
                this.shape[this.shape.length - 1] = this.shape[this.shape.length - 1] * this.numSegments;
                this.units_of_dat = this.getStringOrError("UNITS_OF(_seg)");
                this.units_of_dim = this.getStringOrError("UNITS_OF(DIM_OF(_seg))");
                this.help_of_dat = this.getStringOrError("HELP_OF(_seg)");
                this.deallocate_seg();
            }else{
                if(this.isframe) this.dat = this.getByteArrayOrError("_yyy=COMMA(*," + this.y + ")", this.y);
                else this.dat = this.getRealArrayOrError("_yyy=COMMA(*," + this.y + ")", this.y);
                if(isframe && this.dat instanceof ByteArray){
                    this.dataSize = ((ByteArray)this.dat).dataSize;
                    this.frameType = ((ByteArray)this.dat).getFrameType();
                }else{
                    this.dataSize = -1;
                    this.frameType = -1;
                }
                this.seglimits = null;
                this.segs = null;
                final String xxx = (this.x == null) ? "DIM_OF(_yyy)" : "_xxx";
                this.dim = this.getRealArrayOrError(xxx + ((this.x == null) ? "" : "=COMMA(*," + this.x + ")"), this.x);
                this.units_of_dat = this.getStringOrError("UNITS_OF(_yyy)");
                this.units_of_dim = this.getStringOrError("UNITS_OF(" + xxx + ")");
                this.help_of_dat = this.getStringOrError("HELP_OF(_yyy)");
                this.shape = this.getIntArray("SHAPE(_yyy)");
                mdsDataProvider.this.mds.mdsValue("DEALLOCATE('_yyy')");
                if(this.x != null) mdsDataProvider.this.mds.mdsValue("DEALLOCATE('_xxx')");
            }
        }

        public final void deallocate_seg() {
            mdsDataProvider.this.mds.mdsValue("DEALLOCATE('_seg')");
        }

        private final Object getByteArrayOrError(final String expr, final String orig) {
            try{
                return mdsDataProvider.this.getByteArray(expr);
            }catch(final IOException e){
                final String error = e.getMessage();
                mdsDataProvider.this.error = error;
                if(orig == null) return e;
                return new IOException(error.replaceFirst(Pattern.quote(expr), Matcher.quoteReplacement(orig)));
            }
        }

        private final int[] getIntArray(final String expr) {
            try{
                return mdsDataProvider.this.GetIntArray(expr);
            }catch(final Exception e){
                return new int[]{0};
            }
        }

        private final float[] getLimits() {
            try{ // try using GetLimits
                return mdsDataProvider.this.GetFloatArray("GetLimits(" + this.y + ")");
            }catch(final Exception e1){ // readout the limits manually
                try{
                    return mdsDataProvider.this.GetFloatArray("COMMA(_N=GETNCI(" + this.y + ",'NID_NUMBER'),_L=[],FOR(_I=0,_I<20,_I++,COMMA(_S=0,_E=0,_R=TreeShr->TreeGetSegmentLimits(VAL(_N),VAL(_I),XD(_S),XD(_E)),IF(_R&1,_L=[_L,_S,_E],_L=[_L,$ROPRAND,$ROPRAND]))),_L)");
                }catch(final Exception e2){
                    System.err.println("GetLimits: " + e2);
                    return null;
                }
            }
        }

        private final String getNode(String node) {
            if(DEBUG.M) System.out.println("mdsDataProvider.Signal.getNode(\"" + node + "\")");
            try{
                while((mdsDataProvider.this.GetIntArray("[GETNCI(" + node + ",'DTYPE')]")[0] & 0xFE) == 192)// is nid or path
                    node = mdsDataProvider.this.GetString("GETNCI(GETNCI(" + node + ",'RECORD'),'PATH')");
            }catch(final Exception e){
                if(DEBUG.D) System.err.println(node + " : " + e);
            }
            return node;
        }

        private final int getNumSegments(final String node) {
            try{
                return mdsDataProvider.this.GetIntArray("[GetNumSegments(" + node + ")]")[0];
            }catch(final Exception e){
                if(DEBUG.D) System.err.println(node + " not segmented : " + e);
            }
            return 0;
        }

        private final Object getRealArrayOrError(final String expr, final String orig) {
            try{
                return mdsDataProvider.this.GetRealArray(expr);
            }catch(final IOException e){
                final String error = e.getMessage();
                mdsDataProvider.this.error = error;
                if(orig == null) return e;
                return new IOException(error.replaceFirst(Pattern.quote(expr), Matcher.quoteReplacement(orig)));
            }
        }

        public final Segment getSegment(final int idx) {
            if(this.segs[idx] == null) try{
                this.segs[idx] = new Segment(this.y, idx);
            }catch(final IOException e){
                e.printStackTrace();
                return null;
            }
            return this.segs[idx];
        }

        private final Object getStringOrError(final String expr) {
            try{
                return mdsDataProvider.this.mds.mdsValue(expr).strdata;
            }catch(final Exception e){
                return e;
            }
        }

        @Override
        public final String toString() {
            final StringBuilder sb = new StringBuilder("Signal(").append(this.y);
            if(this.numSegments > 0) sb.append("<").append(this.numSegments).append(">");
            return sb.append(")").toString();
        }
    }
    class SimpleFrameData implements FrameData{
        byte              buf[];
        private Dimension dim             = null;
        private int       end_idx         = -1;
        String            error;
        int               first_frame_idx = -1;
        int               mode            = -1;
        private int       n_frames        = 0;
        int               pixel_size;
        private int       st_idx          = -1;
        double            time_max, time_min;
        private float[]   times           = null;

        public SimpleFrameData(final Signal sig, final double time_min, final double time_max) throws Exception{
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleFrameData(" + sig + ", " + time_min + ", " + time_max + ")");
            int i;
            final float all_times[];
            this.time_min = time_min;
            this.time_max = time_max;
            if(!(sig.dat instanceof ByteArray)) throw(Exception)sig.dat;
            if(!(sig.dim instanceof RealArray)) throw(Exception)sig.dim;
            this.buf = ((ByteArray)sig.dat).buf;
            this.mode = sig.frameType;
            this.pixel_size = sig.dataSize * 8;
            this.dim = new Dimension(sig.shape[0], sig.shape[1]);
            all_times = ((RealArray)sig.dim).getFloatArray();
            for(i = 0; i < all_times.length; i++){
                if(all_times[i] > time_max) break;
                if(this.st_idx == -1 && all_times[i] >= time_min) this.st_idx = i;
            }
            this.end_idx = i;
            if(this.st_idx == -1) throw(new IOException("No frames found between " + time_min + " - " + time_max));
            this.n_frames = this.end_idx - this.st_idx;
            this.times = new float[this.n_frames];
            int j = 0;
            for(i = this.st_idx; i < this.end_idx; i++)
                this.times[j++] = all_times[i];
        }

        @Override
        public byte[] GetFrameAt(final int idx) throws IOException {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleFrameData.GetFrameAt(" + idx + ")");
            byte[] b_img = null;
            if(this.mode == FrameData.BITMAP_IMAGE_8 || this.mode == FrameData.BITMAP_IMAGE_16 || this.mode == FrameData.BITMAP_IMAGE_32 || this.mode == FrameData.BITMAP_IMAGE_FLOAT){
                if(this.buf == null) throw(new IOException("Frames not loaded"));
                final ByteArrayInputStream b = new ByteArrayInputStream(this.buf);
                final DataInputStream d = new DataInputStream(b);
                if(this.buf == null) throw(new IOException("Frames dimension not evaluated"));
                final int img_size = this.dim.width * this.dim.height * this.pixel_size / 8;
                if(d.available() < img_size){
                    if(DEBUG.M) System.err.println("# >> insufficient bytes: " + d.available() + "/" + img_size);
                    return null;
                }
                d.skip(img_size * idx);
                b_img = new byte[img_size];
                d.readFully(b_img);
                return b_img;
            }
            return this.buf;
        }

        @Override
        public Dimension GetFrameDimension() {
            return this.dim;
        }

        @Override
        public float[] GetFrameTimes() {
            return this.times;
        }

        @Override
        public int GetFrameType() throws IOException {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleFrameData.GetFrameType()");
            if(this.mode != -1) return this.mode;
            int i;
            for(i = 0; i < this.n_frames; i++){
                this.buf = this.GetFrameAt(i);
                if(this.buf != null) break;
            }
            this.first_frame_idx = i;
            this.mode = Frames.DecodeImageType(this.buf);
            return this.mode;
        }

        @Override
        public int GetNumFrames() {
            return this.n_frames;
        }
    } // END Inner Class SimpleFrameData
    class SimpleWaveData implements WaveData{
        public static final int                DIMENSION_UNKNOWN  = -1;
        public static final int                SEGMENTED_NO       = 2;
        public static final int                SEGMENTED_UNKNOWN  = 3;
        public static final int                SEGMENTED_YES      = 1;
        private AsynchDataSource               asynchSource       = null;
        private boolean                        continuousUpdate   = false;
        private boolean                        isXLong            = false;
        private int                            numDimensions      = SimpleWaveData.DIMENSION_UNKNOWN;
        private int                            segmentMode        = SimpleWaveData.SEGMENTED_UNKNOWN;
        private final mdsDataProvider.Signal   signal;
        private String                         title              = null;
        private final Vector<WaveDataListener> waveDataListenersV = new Vector<WaveDataListener>();
        private long                           x2DLong[];
        private String                         xLabel             = null;
        private String                         yLabel             = null;

        public SimpleWaveData(final String in_y, final String experiment, final long shot){
            this(in_y, null, experiment, shot);
        }

        public SimpleWaveData(final String in_y, final String in_x, final String experiment, final long shot){
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData(\"" + in_y + "\", \"" + in_x + "\", \"" + experiment + "\", " + shot + ")");
            this.signal = new mdsDataProvider.Signal(in_y, in_x, false);
            mdsDataProvider.this.error = null;
            this.SegmentMode();
        }

        @Override
        public void addWaveDataListener(final WaveDataListener listener) {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.addWaveDataListener()");
            this.waveDataListenersV.addElement(listener);
            if(this.asynchSource != null) this.asynchSource.addDataListener(listener);
        }

        // Check if the passed Y expression specifies also an asynchronous part (separated by the pattern &&&)
        // in case get an implementation of AsynchDataSource
        boolean checkForAsynchRequest(final String expression) {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.checkForAsynchRequest(\"" + expression + "\")");
            if(expression.startsWith("ASYNCH::")){
                this.asynchSource = mdsDataProvider.this.getAsynchSource();
                if(this.asynchSource != null){
                    this.asynchSource.startGeneration(expression.substring("ASYNCH::".length()));
                }
                return true;
            }
            return false;
        }

        // GAB JULY 2014 NEW WAVEDATA INTERFACE RAFFAZZONATA
        @Override
        public final XYData getData(final double xmin, final double xmax, final int numPoints) throws Exception {
            return this.getData(xmin, xmax, numPoints, false);
        }

        public final XYData getData(final double xmin, final double xmax, final int numPoints, final boolean isLong) throws Exception {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.XYData(" + xmin + ", " + xmax + ", " + numPoints + ", " + isLong + ")");
            if(this.signal.dat == null || this.signal.dim == null) return null;
            if(this.signal.dat instanceof IOException){
                this.title = ((IOException)this.signal.dat).getMessage();
                return null;
            }else if(!(this.signal.dat instanceof RealArray)) throw new Exception("signal.dat expected to be RealArray");
            final float[] y = ((RealArray)this.signal.dat).getFloatArray();
            if(DEBUG.D) System.out.println(">> y = " + y);
            if(DEBUG.A) DEBUG.printFloatArray(y, y.length, 1, 1);
            final double[] x;
            if(this.signal.dim instanceof RealArray){
                final RealArray xReal = (RealArray)this.signal.dim;
                if(xReal.isLong){
                    this.isXLong = true;
                    return new XYData(xReal.getLongArray(), y, 1E12);
                }
                this.isXLong = false;
                x = xReal.getDoubleArray();
                if(DEBUG.D) System.out.println(">> x = " + x);
                if(DEBUG.A) DEBUG.printDoubleArray(x, x.length, 1, 1);
            }else if(this.signal.dim instanceof Exception){
                this.isXLong = false;
                x = new double[y.length];
                for(int i = 0; i < x.length; x[i] = i++);
                this.title = ((IOException)this.signal.dim).getMessage();
            }else throw new Exception("signal.dim expected to be RealArray");
            return new XYData(x, y, 1E12);
        }

        @Override
        public final XYData getData(final int numPoints) throws Exception {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getData(" + numPoints + ")");
            return this.getData(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, numPoints);
        }

        @Override
        public final void getDataAsync(final double lowerBound, final double upperBound, final int numPoints) {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getDataAsync(" + lowerBound + ", " + upperBound + ", " + numPoints + ")");
            mdsDataProvider.this.updateWorker.updateInfo(lowerBound, upperBound, numPoints, this.waveDataListenersV, this, this.isXLong);
        }

        @Override
        public final int getNumDimension() throws IOException {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getNumDimension()");
            if(this.numDimensions != SimpleWaveData.DIMENSION_UNKNOWN) return this.numDimensions;
            return this.numDimensions = this.signal.shape.length;
        }

        @Override
        public final String GetTitle() throws IOException {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.GetTitle()");
            if(this.title == null && this.signal.help_of_dat != null) this.title = this.signal.help_of_dat.toString();
            return this.title;
        }

        public final float[] getX_X2D() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getX_X2D()");
            if(this.signal.dim instanceof RealArray) return ((RealArray)this.signal.dim).getFloatArray();
            return null;
        }

        public final float[] getX_Y2D() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getX_Y2D()");
            try{
                return mdsDataProvider.this.GetFloatArray("DIM_OF(" + this.signal.y + ", 1)");
            }catch(final Exception exc){
                return null;
            }
        }

        public final float[] getX_Z() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getX_Z()");
            if(this.signal.dat instanceof RealArray) return ((RealArray)this.signal.dat).getFloatArray();
            return null;
        }

        @Override
        public final double[] getX2D() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getX2D()");
            if(this.signal.dim instanceof RealArray) try{
                final RealArray realArray = ((RealArray)this.signal.dim);
                if(realArray.isLong){
                    this.isXLong = true;
                    this.x2DLong = realArray.getLongArray();
                    return null;
                }
                this.x2DLong = null;
                return realArray.getDoubleArray();
            }catch(final Exception exc){}
            return null;
        }

        @Override
        public final long[] getX2DLong() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getX2DLong()");
            return this.x2DLong;
        }

        @Override
        public final String GetXLabel() throws IOException {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.GetXLabel()");
            if(this.xLabel == null && this.signal.units_of_dim != null) this.xLabel = this.signal.units_of_dim.toString();
            return this.xLabel;
        }

        /*
        private String getTimeContext(double xmin, double xmax, boolean isLong) throws Exception {
        if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.setTimeContext(" + xmin + ", " + xmax + ", " + isLong + ")");
        String res;
        try{
        if(xmin == Double.NEGATIVE_INFINITY && xmax == Double.POSITIVE_INFINITY) res = "SetTimeContext(*,*,*);";
        else if(xmin == Double.NEGATIVE_INFINITY){
            if(isLong) res = "SetTimeContext(*, QUADWORD(" + (long)xmax + "Q), *);";
            else res = "SetTimeContext(*, " + xmax + ", *);";
        }else if(xmax == Double.POSITIVE_INFINITY){
            if(isLong) res = "SetTimeContext(QUADWORD(" + (long)xmin + "Q),*, *);";
            else res = "SetTimeContext(" + xmin + ",*, *);";
        }else{
            if(isLong) res = "SetTimeContext(QUADWORD(" + (long)xmin + "Q),QUADWORD(" + (long)xmax + "Q), *);";
            else res = "SetTimeContext(" + xmin + "," + xmax + ", *);";
        }
        }catch(Exception exc){
        if(DEBUG.M){
            System.err.println("# mdsDataProvider.SimpleWaveData.setTimeContext: " + exc);
        }
        res = "";
        }
        return res;
        }
         */
        @SuppressWarnings("unused")
        private final XYData getXYSignal(final double xmin, final double xmax, final int numPoints, boolean isLong, final String setTimeContext) throws Exception {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getXYSignal(" + xmin + ", " + xmax + ", " + numPoints + ", " + isLong + ", \"" + setTimeContext + "\")");
            // If the requested number of samples is Integer.MAX_VALUE, force the old way of getting data
            if(numPoints == Integer.MAX_VALUE) throw new Exception("Use Old Method for getting data");
            XYData res = null;
            double maxX = 0;
            final Vector<Descriptor> args = new Vector<Descriptor>();
            args.addElement(new Descriptor(null, this.signal.y));
            args.addElement(new Descriptor(null, this.signal.x));
            if(isLong){
                args.addElement(new Descriptor(null, new long[]{(xmin == Double.NEGATIVE_INFINITY) ? 0 : (long)xmin}));
                args.addElement(new Descriptor(null, new long[]{(xmax == Double.POSITIVE_INFINITY) ? 0 : (long)xmax}));
            }else{
                args.addElement(new Descriptor(null, new float[]{(float)xmin}));
                args.addElement(new Descriptor(null, new float[]{(float)xmax}));
            }
            args.addElement(new Descriptor(null, new int[]{numPoints}));
            byte[] retData;
            int nSamples;
            // all fine if setTimeContext is an empty string
            // if a space is required between ; and further code setTimeContext sould have it
            try{
                if(isLong) retData = mdsDataProvider.this.GetByteArray(setTimeContext + "mdsMisc->GetXYSignalLongTimes:DSC", args);
                else retData = mdsDataProvider.this.GetByteArray(setTimeContext + "mdsMisc->GetXYSignal:DSC", args);
            }catch(final IOException exc){
                throw(new Exception(exc.getMessage()));
            }
            if(DEBUG.D) System.out.println(">> mdsMisc->GetXYSignal*Long*Times:DSC");
            if(DEBUG.A) DEBUG.printByteArray(retData, 1, 8, 8, 1);
            /*Decode data: Format:
               -retResolution(float)
               -number of samples (minimum between X and Y)
               -type of X xamples (byte: long(1), double(2) or float(3))
               -y samples
               -x Samples
             */
            if(DEBUG.D) System.out.println(">> retData " + retData.length + " " + retData);
            final ByteArrayInputStream bis = new ByteArrayInputStream(retData);
            final DataInputStream dis = new DataInputStream(bis);
            float fRes;
            double dRes;
            fRes = dis.readFloat();
            if(fRes >= 1E10) dRes = Double.POSITIVE_INFINITY;
            else dRes = fRes;
            nSamples = dis.readInt();
            if(nSamples <= 0){
                mdsDataProvider.this.error = "No Samples returned";
                return null;
            }
            final byte type = dis.readByte();
            final float y[] = new float[nSamples];
            for(int i = 0; i < nSamples; i++)
                y[i] = dis.readFloat();
            if(type == 1) // Long X (i.e. absolute times
            {
                final long[] longX = new long[nSamples];
                for(int i = 0; i < nSamples; i++)
                    longX[i] = dis.readLong();
                this.isXLong = true;
                res = new XYData(longX, y, dRes);
                if(longX.length > 0) maxX = longX[longX.length - 1];
                else maxX = 0;
            }else if(type == 2) // double X
            {
                final double[] x = new double[nSamples];
                for(int i = 0; i < nSamples; i++)
                    x[i] = dis.readDouble();
                res = new XYData(x, y, dRes);
                if(x.length > 0) maxX = x[x.length - 1];
                else maxX = 0;
            }else // float X
            {
                final double[] x = new double[nSamples];
                for(int i = 0; i < nSamples; i++)
                    x[i] = dis.readFloat();
                res = new XYData(x, y, dRes);
                if(x.length > 0) maxX = x[x.length - 1];
                else maxX = 0;
            }
            // Get title, xLabel and yLabel
            final int titleLen = dis.readInt();
            if(titleLen > 0){
                final byte[] titleBuf = new byte[titleLen];
                dis.readFully(titleBuf);
                this.title = new String(titleBuf, "UTF-8");
            }
            final int xLabelLen = dis.readInt();
            if(xLabelLen > 0){
                final byte[] xLabelBuf = new byte[xLabelLen];
                dis.readFully(xLabelBuf);
                this.xLabel = new String(xLabelBuf, "UTF-8");
            }
            final int yLabelLen = dis.readInt();
            if(yLabelLen > 0){
                final byte[] yLabelBuf = new byte[yLabelLen];
                dis.readFully(yLabelBuf);
                this.yLabel = new String(yLabelBuf, "UTF-8");
            }
            if(type == 1) isLong = true;
            if(this.segmentMode == SimpleWaveData.SEGMENTED_YES && this.continuousUpdate){
                long refreshPeriod = jScopeFacade.getRefreshPeriod();
                if(refreshPeriod <= 0) refreshPeriod = 1000; // default 1 s refresh
                mdsDataProvider.this.updateWorker.updateInfo(/*xmin*/maxX, Double.POSITIVE_INFINITY, 2000, this.waveDataListenersV, this, isLong, refreshPeriod);
            }
            return res;
        }

        @Override
        public final float[] getY2D() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getY2D()");
            try{
                return mdsDataProvider.this.GetFloatArray("DIM_OF(" + this.signal.y + ", 1)");
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public final String GetYLabel() throws IOException {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.GetYLabel()");
            if(this.yLabel == null) try{
                if(this.getNumDimension() > 1){
                    if(this.segmentMode == SimpleWaveData.SEGMENTED_YES) this.yLabel = mdsDataProvider.this.GetStringValue("Units(Dim_of(GetSegment(" + this.signal.y + ",0),1))");
                    else this.yLabel = mdsDataProvider.this.GetStringValue("Units(Dim_of(" + this.signal.y + ",1))");
                }else{
                    if(this.yLabel == null && this.signal.units_of_dat != null) ;
                }
            }catch(final Exception e){
                this.yLabel = e.toString();
            }
            return this.yLabel;
        }

        @Override
        public float[] getZ() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.getZ()");
            try{
                return (float[])this.signal.dat;
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public String GetZLabel() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.GetZLabel()");
            return this.signal.units_of_dat.toString();
        }

        // End
        // public double[] getXLimits(){System.out.println("BADABUM!!"); return null;}
        // public long []getXLong(){System.out.println("BADABUM!!"); return null;}
        @Override
        public final boolean isXLong() {
            return this.isXLong;
        }

        private final boolean SegmentMode() {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.SegmentMode()");
            if(this.segmentMode == SimpleWaveData.SEGMENTED_UNKNOWN) this.segmentMode = this.signal.numSegments > 0 ? SimpleWaveData.SEGMENTED_YES : SimpleWaveData.SEGMENTED_NO;
            return this.segmentMode == SimpleWaveData.SEGMENTED_YES;
        }

        @Override
        public final void setContinuousUpdate(final boolean continuousUpdate) {
            if(DEBUG.M) System.out.println("mdsDataProvider.SimpleWaveData.setContinuousUpdate(" + continuousUpdate + ")");
            this.continuousUpdate = continuousUpdate;
        }
    } // END Inner Class SimpleWaveData
      // Inner class UpdateWorker handler asynchronous requests for getting (portions of) data
    private final class UpdateWorker extends Thread{
        private final class UpdateDescriptor{
            boolean                  isXLong;
            SimpleWaveData           simpleWaveData;
            double                   updateLowerBound;
            int                      updatePoints;
            long                     updateTime;
            double                   updateUpperBound;
            Vector<WaveDataListener> waveDataListenersV;

            UpdateDescriptor(final double updateLowerBound, final double updateUpperBound, final int updatePoints, final Vector<WaveDataListener> waveDataListenersV, final SimpleWaveData simpleWaveData, final boolean isXLong, final long updateTime){
                this.updateLowerBound = updateLowerBound;
                this.updateUpperBound = updateUpperBound;
                this.updatePoints = updatePoints;
                this.waveDataListenersV = waveDataListenersV;
                this.simpleWaveData = simpleWaveData;
                this.isXLong = isXLong;
                this.updateTime = updateTime;
            }
        }
        boolean                  enabled    = true;
        Vector<UpdateDescriptor> requestsV  = new Vector<UpdateDescriptor>();
        boolean                  stopWorker = false;

        private final synchronized void enableAsyncUpdate(final boolean enabled) {
            this.enabled = enabled;
            if(enabled) this.notify();
        }

        private final synchronized void intUpdateInfo(final double updateLowerBound, final double updateUpperBound, final int updatePoints, final Vector<WaveDataListener> waveDataListenersV, final SimpleWaveData simpleWaveData, final boolean isXLong, final long updateTime) {
            if(updateTime > 0) // If a delayed request for update
            {
                for(int i = 0; i < this.requestsV.size(); i++){
                    final UpdateDescriptor currUpdateDescr = this.requestsV.elementAt(i);
                    if(currUpdateDescr.updateTime > 0 && currUpdateDescr.simpleWaveData == simpleWaveData) return; // Another delayed update request for that signal is already in the pending list
                }
            }
            this.requestsV.add(new UpdateDescriptor(updateLowerBound, updateUpperBound, updatePoints, waveDataListenersV, simpleWaveData, isXLong, updateTime));
            this.notify();
        }

        @Override
        public final void run() {
            if(DEBUG.M) System.out.println("run()");
            this.setName("UpdateWorker");
            while(true){
                synchronized(this){
                    try{
                        this.wait();
                        if(this.stopWorker) return;
                    }catch(final InterruptedException exc){
                        exc.printStackTrace();
                    }
                }
                if(!this.enabled) continue;
                long currTime = Calendar.getInstance().getTimeInMillis();
                long nextTime = -1;
                int i = 0;
                while(i < this.requestsV.size()){
                    System.out.println("loop");
                    if(!this.enabled) break;
                    final UpdateDescriptor currUpdate = this.requestsV.elementAt(i);
                    if(currUpdate.updateTime < currTime){
                        try{
                            this.requestsV.removeElementAt(i);
                            final XYData currData = currUpdate.simpleWaveData.getData(currUpdate.updateLowerBound, currUpdate.updateUpperBound, currUpdate.updatePoints, currUpdate.isXLong);
                            if(currData == null || currData.isEmpty()) continue;
                            for(int j = 0; j < currUpdate.waveDataListenersV.size(); j++){
                                currUpdate.waveDataListenersV.elementAt(j).dataRegionUpdated(currData);
                            }
                        }catch(final Exception exc){
                            final Date d = new Date();
                            if(DEBUG.M) System.err.println(d + " Error in asynchUpdate: " + exc);
                        }
                    }else{
                        if(nextTime == -1 || nextTime > currUpdate.updateTime) // It will always be nextTime != -1
                        nextTime = currUpdate.updateTime;
                        i++;
                    }
                }
                if(nextTime != -1) // If a pending request for which time did not expire, schedule a new notification
                {
                    currTime = Calendar.getInstance().getTimeInMillis();
                    final java.util.Timer timer = new java.util.Timer();
                    timer.schedule(new TimerTask(){
                        @Override
                        public void run() {
                            synchronized(UpdateWorker.this){
                                UpdateWorker.this.notify();
                            }
                        }
                    }, (nextTime < currTime + 50) ? 50 : (nextTime - currTime + 1));
                }
            }
        }

        private final synchronized void stopUpdateWorker() {
            this.stopWorker = true;
            this.notify();
        }

        private final void updateInfo(final double updateLowerBound, final double updateUpperBound, final int updatePoints, final Vector<WaveDataListener> waveDataListenersV, final SimpleWaveData simpleWaveData, final boolean isXLong) {
            // intUpdateInfo(updateLowerBound, updateUpperBound, updatePoints, waveDataListenersV, simpleWaveData, isXLong,
            // Calendar.getInstance().getTimeInMillis());
            this.intUpdateInfo(updateLowerBound, updateUpperBound, updatePoints, waveDataListenersV, simpleWaveData, isXLong, -1);
        }

        private final void updateInfo(final double updateLowerBound, final double updateUpperBound, final int updatePoints, final Vector<WaveDataListener> waveDataListenersV, final SimpleWaveData simpleWaveData, final boolean isXLong, final long delay) {
            this.intUpdateInfo(updateLowerBound, updateUpperBound, updatePoints, waveDataListenersV, simpleWaveData, isXLong, Calendar.getInstance().getTimeInMillis() + delay);
        }
    } // End Inner class UpdateWorker
    protected static final int  MAX_PIXELS        = 20000;
    protected static final long RESAMPLE_TRESHOLD = 1000000;

    private static final double GetDate(final String in) throws Exception {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetDate(\"" + in + "\")");
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        final Date date = df.parse(in + " UTC");
        cal.setTime(date);
        final long javaTime = cal.getTime().getTime();
        return javaTime;
    }

    private static final double GetNow(final String in) throws Exception {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetNow(\"" + in + "\")");
        boolean isPlus = true;
        int hours = 0, minutes = 0, seconds = 0;
        String currStr = in.trim().toUpperCase();
        if(!currStr.startsWith("NOW")) throw new Exception();
        currStr = currStr.substring(3).trim();
        if(currStr.length() > 0) // Not only NOW
        {
            if(currStr.startsWith("+")) isPlus = true;
            else if(currStr.startsWith("-")) isPlus = false;
            else throw new Exception();
            currStr = currStr.substring(1).trim();
            final StringTokenizer st = new StringTokenizer(currStr, ":", true);
            String currTok = st.nextToken();
            if(currTok.equals(":")) hours = 0;
            else{
                hours = Integer.parseInt(currTok);
                currTok = st.nextToken();
            }
            if(!currTok.equals(":")) throw new Exception();
            currTok = st.nextToken();
            if(currTok.equals(":")) minutes = 0;
            else{
                minutes = Integer.parseInt(currTok);
                currTok = st.nextToken();
            }
            if(!currTok.equals(":")) throw new Exception();
            if(st.hasMoreTokens()){
                seconds = Integer.parseInt(st.nextToken());
            }
        }
        if(!isPlus){
            hours = -hours;
            minutes = -minutes;
            seconds = -seconds;
        }
        final Calendar cal = Calendar.getInstance();
        // cal.setTimeZone(TimeZone.getTimeZone("GMT+00"));
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
        final long javaTime = cal.getTime().getTime();
        return javaTime;
    }

    /*
    @SuppressWarnings("all")
    public static final void main(final String[] args) {//TODO:main
        final mdsDataProvider dp = new mdsDataProvider(args[0]);
        try{
            dp.CheckOpen("W7X", 0);
            System.out.println(dp.GetString("TCL('DIR',_out);_out"));
            final mdsDataProvider.Signal signal = dp.GetSignal("sqrt(QSS.HARDWARE.ARRAY:TUBE01)", null, false);
            System.out.println(signal);
            final SimpleWaveData wd = (SimpleWaveData)dp.GetWaveData("sqrt(QSS.HARDWARE.ARRAY:TUBE01)");
            System.out.println(wd.getData(-1, 2, 1000));
            System.out.println(dp.error);
            System.exit(0);
        }catch(final Exception e){
            e.printStackTrace();
        }
    }
    */
    protected static final boolean NotYetNumber(final String in) {
        if(DEBUG.M) System.out.println("mdsDataProvider.NotYetNumber(\"" + in + "\")");
        try{
            Float.parseFloat(in);
        }catch(final NumberFormatException e){
            return false;
        }
        return true;
    }

    protected static final boolean NotYetString(final String in) {
        if(DEBUG.M) System.out.println("mdsDataProvider.NotYetString(\"" + in + "\")");
        int i;
        if(in.charAt(0) == '\"' || in.charAt(0) == '\''){
            for(i = 1; i < in.length() && (in.charAt(i) != '\"' && in.charAt(i) != '\'' || ((in.charAt(i) == '\"' || in.charAt(i) == '\'') && in.charAt(i - 1) == '\\')); i++);
            if(i == (in.length() - 1)) return false;
        }
        return true;
    }

    public static boolean SupportsCompression() {
        return true;
    }

    public static boolean SupportsFastNetwork() {
        return true;
    }
    private boolean        def_node_changed = false;
    protected String       default_node;
    protected String       environment_vars;
    protected String       error;
    protected String       experiment;
    protected boolean      is_tunneling     = false;
    public mdsConnection   mds;
    protected boolean      open, connected;
    protected String       provider;
    public long            shot;
    protected sshTunneling ssh_tunneling;
    protected String       tunnel_provider  = "127.0.0.1:8000";
    protected UpdateWorker updateWorker;
    private boolean        use_compression  = false;

    public mdsDataProvider(){
        this(null);
    }

    public mdsDataProvider(final String provider){
        if(DEBUG.M) System.out.println("mdsDataProvider(\"" + provider + "\")");
        jScope.DataAccessURL.addProtocol(new mdsAccess());
        this.setProvider(provider);
        this.experiment = null;
        this.shot = 0;
        this.open = this.connected = false;
        this.mds = new mdsConnection(this.provider);
        this.error = null;
        // updateWorker = new UpdateWorker();
        // updateWorker.start();
    }

    @Override
    public void abort() {
        // TODO Auto-generated method stub
    }

    @Override
    public synchronized void AddConnectionListener(final ConnectionListener l) {
        if(DEBUG.M) System.out.println("mdsDataProvider.AddConnectionListener(" + l + ")");
        if(this.mds == null){ return; }
        this.mds.addConnectionListener(l);
    }

    @Override
    public synchronized void AddUpdateEventListener(final UpdateEventListener l, final String event_name) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.AddUpdateEventListener(" + l + "," + event_name + ")");
        if(event_name == null || event_name.trim().length() == 0) return;
        this.CheckConnection();
        this.mds.mdsSetEvent(l, event_name);
    }

    protected synchronized void CheckConnection() throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.CheckConnection()");
        if(!this.connected){
            if(!this.mds.ConnectToMds(this.use_compression)){
                if(this.mds.error != null) throw new IOException(this.mds.error);
                throw new IOException("Could not get IO for " + this.provider);
            }
            if(DEBUG.M) System.out.println("connected");
            this.connected = true;
            this.updateWorker = new UpdateWorker();
            this.updateWorker.start();
        }
    }

    public synchronized boolean CheckOpen() throws IOException {
        return this.CheckOpen(this.experiment, this.shot);
    }

    public synchronized boolean CheckOpen(final String experiment, final long shot) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.CheckOpen(\"" + experiment + "\", " + shot + ")");
        if(!this.connected){
            if(!this.mds.ConnectToMds(this.use_compression)){
                if(this.mds.error != null) throw new IOException("Cannot connect to data server : " + this.mds.error);
                this.error = "Cannot connect to data server";
                return false;
            }
            if(DEBUG.D) System.out.println(">> connected");
            this.connected = true;
            this.updateWorker = new UpdateWorker();
            this.updateWorker.start();
        }
        if(!this.open && experiment != null || this.shot != shot || experiment != null && !experiment.equalsIgnoreCase(this.experiment)){
            // System.out.println("\n-->\nOpen tree "+experiment+ " shot "+ shot +"\n<--\n");
            final Descriptor descr = this.mds.mdsValue("JavaOpen(\"" + experiment + "\"," + shot + ")");
            if(descr.dtype != Descriptor.DTYPE_CSTRING && descr.dtype == Descriptor.DTYPE_LONG && descr.int_data != null && descr.int_data.length > 0 && (descr.int_data[0] % 2 == 1)){
                this.open = true;
                this.def_node_changed = true;
                this.shot = shot;
                this.experiment = experiment;
                if(this.environment_vars != null && this.environment_vars.length() > 0){
                    this.SetEnvironmentSpecific(this.environment_vars);
                    if(this.error != null){
                        this.error = "Public variable evaluation error " + experiment + " shot " + shot + " : " + this.error;
                        return false;
                    }
                }
            }else{
                if(this.mds.error != null) this.error = "Cannot open experiment " + experiment + " shot " + shot + " : " + this.mds.error;
                else this.error = "Cannot open experiment " + experiment + " shot " + shot;
                return false;
            }
        }
        if(this.open && this.def_node_changed){
            Descriptor descr;
            if(this.default_node != null){
                descr = this.mds.mdsValue("TreeSetDefault(\"\\\\" + this.default_node + "\")");
                if((descr.int_data[0] & 1) == 0) this.mds.mdsValue("TreeSetDefault(\"\\\\" + experiment + "::TOP\")");
            }else descr = this.mds.mdsValue("TreeSetDefault(\"\\\\" + experiment + "::TOP\")");
            this.def_node_changed = false;
        }
        return true;
    }

    @Override
    public final boolean checkProvider() {
        try{
            this.GetShots("0");
            return true;
        }catch(final IOException exc){}
        return false;
    }

    protected void DispatchConnectionEvent(final ConnectionEvent e) {
        if(DEBUG.M) System.out.println("mdsDataProvider.DispatchConnectionEvent(" + e + ")");
        if(this.mds == null){ return; }
        this.mds.dispatchConnectionEvent(e);
    }

    @Override
    public synchronized void Dispose() {
        if(DEBUG.M) System.out.println("mdsDataProvider.Dispose()");
        if(this.is_tunneling && this.ssh_tunneling != null) this.ssh_tunneling.Dispose();
        if(this.connected){
            this.connected = false;
            this.mds.DisconnectFromMds();
            if(DEBUG.D) System.out.println(">> disconnected");
            final ConnectionEvent ce = new ConnectionEvent(this, ConnectionEvent.LOST_CONNECTION, "Lost connection from : " + this.provider);
            this.mds.dispatchConnectionEvent(ce);
        }
        if(this.updateWorker != null && this.updateWorker.isAlive()){
            this.updateWorker.stopUpdateWorker();
        }
    }

    public void enableAsyncUpdate(final boolean enable) {
        this.updateWorker.enableAsyncUpdate(enable);
    }

    @Override
    public synchronized String ErrorString() {
        return this.error;
    }

    @Override
    protected void finalize() {
        if(DEBUG.M) System.out.println("mdsDataProvider.finalize()");
        if(this.open) this.mds.mdsValue("JavaClose(\"" + this.experiment + "\"," + this.shot + ")");
        if(this.connected) this.mds.DisconnectFromMds();
        if(DEBUG.D) System.out.println(">> disconnected");
    }

    public synchronized AllFrames GetAllFrames(final String in_frame) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetAllFrames(" + in_frame + ")");
        ByteArray img = null;
        float[] time = null;
        int[] shape;
        img = this.getByteArray("_jScope_img = IF_ERROR(COMMA(*," + in_frame + "),*)");
        if(img == null) return null;
        if(DEBUG.D) System.out.println(">> mdsDataProvider.getByteArray: " + img.buf.length);
        shape = this.GetIntArray("SHAPE( _jScope_img )");
        time = this.GetFloatArray("DIM_OF( _jScope_img )");
        this.mds.mdsValue("DEALLOCATE('_jScope_img')");
        if(time == null || shape == null) return null;
        if(DEBUG.D) System.out.println(">> mdsDataProvider.GetDoubleArray: " + time.length);
        /*
        if(shape.length == 3){
        num_time = shape[2];
        pixel_size = img.buf.length / (shape[0] * shape[1] * shape[2]) * 8;
        }else{
        if(shape.length == 2){
            num_time = 1;
            pixel_size = img.buf.length / (shape[0] * shape[1]) * 8;
        }else if(shape.length == 1){ throw(new IOException("The evaluated signal is not an image")); }
        }
         */
        if(shape.length == 1) throw(new IOException("The evaluated signal is not an image"));
        return new Array.AllFrames(img, shape[0], shape[1], time);
    }

    // To be overridden by any DataProvider implementation with added dynamic generation
    @SuppressWarnings("static-method")
    public jScope.AsynchDataSource getAsynchSource() {
        return null;
    }

    public synchronized ByteArray getByteArray(final String in) throws IOException {
        return this.getByteArray(in, null);
    }

    public synchronized ByteArray getByteArray(final String in, final Vector<Descriptor> args) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.getByteArray(\"" + in + "\", " + args + ")");
        if(!this.CheckOpen()) throw new IOException("TreeNotOpen");
        if(DEBUG.D) System.out.println(">> mds = " + this.mds);
        final Descriptor desc = this.mds.mdsValue(in, args);
        if(desc == null || desc.dtype == 0) throw new IOException("mdsValue: no result (" + in + ")");
        if(DEBUG.D) System.out.println(">> desc.dtype = " + desc.dtype);
        final ByteArrayOutputStream dosb = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(dosb);
        switch(desc.dtype){
            case Descriptor.DTYPE_DOUBLE:
                for(final double element : desc.double_data)
                    dos.writeFloat((float)element);
                return new ByteArray(dosb.toByteArray(), Descriptor.DTYPE_FLOAT);
            case Descriptor.DTYPE_FLOAT:
                for(final float element : desc.float_data)
                    dos.writeFloat(element);
                dos.close();
                return new ByteArray(dosb.toByteArray(), desc.dtype);
            case Descriptor.DTYPE_USHORT:
            case Descriptor.DTYPE_SHORT: // bdb hacked this to try to make profile dialog read true data values, not normalised
                for(final short element : desc.short_data)
                    dos.writeShort(element);
                dos.close();
                return new ByteArray(dosb.toByteArray(), desc.dtype);
            case Descriptor.DTYPE_ULONG:
            case Descriptor.DTYPE_LONG:
                for(final int element : desc.int_data)
                    dos.writeInt(element);
                dos.close();
                return new ByteArray(dosb.toByteArray(), desc.dtype);
            case Descriptor.DTYPE_UBYTE:
            case Descriptor.DTYPE_BYTE:
                return new ByteArray(desc.byte_data, desc.dtype);
            case Descriptor.DTYPE_CSTRING:
                if(DEBUG.D) System.out.println("# " + desc.strdata);
                if((desc.status & 1) == 0) this.error = desc.error;
                throw new IOException(this.error);
        }
        throw new IOException(this.error);
    }

    public synchronized byte[] GetByteArray(final String in) throws IOException {
        return this.getByteArray(in, null).buf;
    }

    public synchronized byte[] GetByteArray(final String in, final Vector<Descriptor> args) throws IOException {
        return this.getByteArray(in, args).buf;
    }

    protected mdsConnection getConnection() {
        if(DEBUG.M) System.out.println("mdsDataProvider(\"" + this.provider + "\")");
        return new mdsConnection();
    }

    @Override
    public Class getDefaultBrowser() {
        // TODO Add jTraverser plug-in
        return null;
    }

    public double[] GetDoubleArray(final String in) throws IOException {
        final RealArray realArray = this.GetRealArray(in);
        if(realArray == null) return null;
        return realArray.getDoubleArray();
    }

    protected String GetExperimentName(final String in_frame) {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetExperimentName(\"" + in_frame + "\")");
        if(this.experiment == null){
            if(in_frame.indexOf(".") == -1) return in_frame;
            return in_frame.substring(0, in_frame.indexOf("."));
        }
        return this.experiment;
    }

    @Override
    public synchronized float GetFloat(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetFloat(\"" + in + "\")");
        this.error = null;
        // First check Whether this is a date
        try{
            return (float)mdsDataProvider.GetDate(in);
        }catch(final Exception excD){}
        try{
            return (float)mdsDataProvider.GetNow(in);
        }catch(final Exception excN){}
        if(mdsDataProvider.NotYetNumber(in)){
            if(!this.CheckOpen()) return 0;
            final Descriptor desc = this.mds.mdsValue(in);
            if(desc.error != null) this.error = desc.error;
            switch(desc.dtype){
                case Descriptor.DTYPE_DOUBLE:
                    return (float)desc.double_data[0];
                case Descriptor.DTYPE_FLOAT:
                    return desc.float_data[0];
                case Descriptor.DTYPE_LONG:
                    return desc.int_data[0];
                case Descriptor.DTYPE_BYTE:
                case Descriptor.DTYPE_UBYTE:
                    return desc.byte_data[0];
                case Descriptor.DTYPE_CSTRING:
                    if((desc.status & 1) == 0){
                        this.error = desc.error;
                        throw(new IOException(this.error));
                    }
                    return 0;
            }
        }else return new Float(in).floatValue();
        return 0;
    }

    public float[] GetFloatArray(final String in) throws IOException {
        final RealArray realArray = this.GetRealArray(in);
        if(realArray == null) return null;
        return realArray.getFloatArray();
    }

    @Override
    public FrameData GetFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetFrameData(\"" + in_y + "\", \"" + in_x + "\", " + time_min + ", " + time_max + ")");
        final Signal sig = new mdsDataProvider.Signal(in_y, in_x, true);
        this.error = null;
        if(sig.numSegments > 0) return new SegmentedFrameData(sig, time_min, time_max);
        try{
            return(new SimpleFrameData(sig, time_min, time_max));
        }catch(final Exception e){
            if(DEBUG.D) System.err.println("# mdsDataProvider.SimpleFrameData(" + in_y + "): " + e);
        }
        return null;
    }

    public synchronized float[] GetFrameTimes(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetFrameTimes(\"" + in + "\")");
        final String exp = this.GetExperimentName(in);
        final String expr = "JavaGetFrameTimes(\"" + exp + "\",\"" + in + "\"," + this.shot + " )";
        final Descriptor desc = this.mds.mdsValue(expr);
        float[] out_data;
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                return desc.float_data;
            case Descriptor.DTYPE_DOUBLE:
                out_data = new float[desc.double_data.length];
                for(int i = 0; i < desc.double_data.length; i++)
                    out_data[i] = (float)desc.double_data[i];
                return out_data;
            case Descriptor.DTYPE_LONG:
                out_data = new float[desc.int_data.length];
                for(int i = 0; i < desc.int_data.length; i++)
                    out_data[i] = desc.int_data[i];
                return out_data;
            case Descriptor.DTYPE_BYTE:
                this.error = "Cannot convert byte array to double array";
                break;
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) this.error = desc.error;
        }
        throw new IOException(in + " : " + this.error);
    }

    public int[] GetIntArray(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetIntArray(\"" + in + "\")");
        if(!this.CheckOpen()) throw new IOException("Tree not open");
        return this.GetIntegerArray(in);
    }

    private synchronized int[] GetIntegerArray(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetIntegerArray(\"" + in + "\")");
        int out_data[];
        final Descriptor desc = this.mds.mdsValue(in);
        switch(desc.dtype){
            case Descriptor.DTYPE_LONG:
                return desc.int_data;
            case Descriptor.DTYPE_FLOAT:
                out_data = new int[desc.float_data.length];
                for(int i = 0; i < desc.float_data.length; i++)
                    out_data[i] = (int)(desc.float_data[i] + 0.5);
                return out_data;
            case Descriptor.DTYPE_SHORT:
            case Descriptor.DTYPE_USHORT:
                out_data = new int[desc.short_data.length];
                for(int i = 0; i < desc.short_data.length; i++)
                    out_data[i] = desc.short_data[i];
                return out_data;
            case Descriptor.DTYPE_BYTE:
            case Descriptor.DTYPE_UBYTE:
                out_data = new int[desc.byte_data.length];
                for(int i = 0; i < desc.byte_data.length; i++)
                    out_data[i] = desc.byte_data[i];
                return out_data;
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) this.error = desc.error;
                throw new IOException(this.error);
            default:
                this.error = "Data type code : " + desc.dtype + " not yet supported (GetIntegerArray)";
        }
        throw new IOException(in + " : " + this.error);
    }

    @Override
    public final String GetLegendString(final String s) {
        return s;
    }

    public synchronized long[] GetLongArray(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetLongArray(\"" + in + "\")");
        long out_data[];
        final Descriptor desc = this.mds.mdsValue(in);
        switch(desc.dtype){
            case Descriptor.DTYPE_ULONGLONG:
            case Descriptor.DTYPE_LONGLONG:
                return desc.long_data;
            case Descriptor.DTYPE_LONG:
                out_data = new long[desc.int_data.length];
                for(int i = 0; i < desc.int_data.length; i++){
                    out_data[i] = (desc.int_data[i]);
                }
                return out_data;
            case Descriptor.DTYPE_FLOAT:
                out_data = new long[desc.float_data.length];
                for(int i = 0; i < desc.float_data.length; i++)
                    out_data[i] = (long)(desc.float_data[i] + 0.5);
                return out_data;
            case Descriptor.DTYPE_BYTE:
            case Descriptor.DTYPE_UBYTE:
                out_data = new long[desc.byte_data.length];
                for(int i = 0; i < desc.byte_data.length; i++)
                    out_data[i] = (long)(desc.byte_data[i] + 0.5);
                return out_data;
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) this.error = desc.error;
                throw new IOException(this.error);
            default:
                this.error = "Data type code : " + desc.dtype + " not yet supported (GetLongArray)";
        }
        throw new IOException(in + " : " + this.error);
    }

    public int[] GetNumDimensions(final String expression) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetNumDimensions(\"" + expression + "\")");
        // return GetIntArray(in_y);
        // Gabriele June 2013: reduce dimension if one component is 1
        final int[] fullDims = this.GetIntArray("SHAPE( " + expression + " )");
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

    public final String getProvider() {
        return this.provider;
    }

    public synchronized RealArray GetRealArray(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetRealArray(\"" + in + "\")");
        final ConnectionEvent e = new ConnectionEvent(this, 1, 0);
        this.DispatchConnectionEvent(e);
        if(!this.CheckOpen()) return null;
        final Descriptor desc = this.mds.mdsValue(in);
        if(DEBUG.D) System.out.println(">> mdsDataProvider.GetRealArray: " + desc.dtype);
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                return new Array.RealArray(desc.float_data);
            case Descriptor.DTYPE_DOUBLE:
                return new Array.RealArray(desc.double_data);
            case Descriptor.DTYPE_ULONG:
            case Descriptor.DTYPE_LONG:
                final float[] outl = new float[desc.int_data.length];
                for(int i = 0; i < desc.int_data.length; i++)
                    outl[i] = desc.int_data[i];
                return new Array.RealArray(outl);
            case Descriptor.DTYPE_USHORT:
            case Descriptor.DTYPE_SHORT:
                final float[] outs = new float[desc.short_data.length];
                for(int i = 0; i < desc.short_data.length; i++)
                    outs[i] = desc.short_data[i];
                return new Array.RealArray(outs);
            case Descriptor.DTYPE_UBYTE:
            case Descriptor.DTYPE_BYTE:
                final float[] outb = new float[desc.byte_data.length];
                for(int i = 0; i < desc.byte_data.length; i++)
                    outb[i] = desc.byte_data[i];
                return new Array.RealArray(outb);
            case Descriptor.DTYPE_ULONGLONG:
            case Descriptor.DTYPE_LONGLONG:
                return new Array.RealArray(desc.long_data);
            case Descriptor.DTYPE_CSTRING:
                if(DEBUG.D) System.out.println(">> mdsDataProvider.GetRealArray: " + desc.dtype);
                if((desc.status & 1) == 0) this.error = desc.error;
                break;
            default:
                this.error = "Data type code : " + desc.dtype + " not yet supported (RealArray.GetRealArray)";
        }
        throw new IOException(in + " : " + this.error);
    }

    @Override
    public long[] GetShots(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetShots(\"" + in + "\")");
        // To shot evaluation don't execute check
        // if a pulse file is open
        this.CheckConnection();
        return this.GetLongArray(in);
    }

    public Signal GetSignal(final String in_y, final String in_x, final boolean isframes) {
        return new Signal(in_y, in_x, isframes);
    }

    @Override
    public synchronized String GetString(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetString(\"" + in + "\")");
        if(in == null) return null;
        this.error = null;
        if(mdsDataProvider.NotYetString(in)){
            if(!this.CheckOpen()) return null;
            final Descriptor desc = this.mds.mdsValue(in);
            switch(desc.dtype){
                case Descriptor.DTYPE_BYTE:
                case Descriptor.DTYPE_UBYTE:
                    return new String(desc.byte_data, "UTF-8");
                case Descriptor.DTYPE_FLOAT:
                    this.error = "Cannot convert a float to string";
                    throw new IOException(this.error);
                case Descriptor.DTYPE_CSTRING:
                    if((desc.status & 1) == 1) return desc.strdata;
                    return(this.error = desc.error);
            }
            if(desc.error == null) return "Undefined error";
            return(this.error = desc.error);
        }
        return new String(in.getBytes(), 1, in.length() - 2, "UTF-8");
    }

    protected String GetStringValue(final String expression) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.GetStringValue(\"" + expression + "\")");
        String out = this.GetString(expression);
        if(out == null || out.length() == 0 || this.error != null){
            this.error = null;
            return null;
        }
        if(out.indexOf(0) > 0) out = out.substring(0, out.indexOf(0));
        return out;
    }

    @Override
    public WaveData GetWaveData(final String in) {
        return new SimpleWaveData(in, this.experiment, this.shot);
    }

    @Override
    public WaveData GetWaveData(final String in_y, final String in_x) {
        return new SimpleWaveData(in_y, in_x, this.experiment, this.shot);
    }

    @Override
    public int InquireCredentials(final JFrame f, final DataServerItem server_item) {
        if(DEBUG.M) System.out.println("mdsDataProvider.InquireCredentials(" + f + ", " + server_item + ")");
        this.mds.setUser(server_item.user);
        this.is_tunneling = false;
        if(server_item.tunnel_port != null && server_item.tunnel_port.trim().length() != 0){
            final StringTokenizer st = new StringTokenizer(server_item.argument, ":");
            String ip;
            String remote_port = "" + mdsConnection.DEFAULT_PORT;
            ip = st.nextToken();
            if(st.hasMoreTokens()) remote_port = st.nextToken();
            this.is_tunneling = true;
            try{
                this.ssh_tunneling = new sshTunneling(f, this, ip, remote_port, server_item.user, server_item.tunnel_port);
                this.ssh_tunneling.start();
                this.tunnel_provider = "127.0.0.1:" + server_item.tunnel_port;
            }catch(final Throwable exc){
                if(exc instanceof NoClassDefFoundError) JOptionPane.showMessageDialog(f, "The MindTerm.jar library is required for ssh tunneling.You can download it from \nhttp://www.appgate.com/mindterm/download.php\n" + exc, "alert", JOptionPane.ERROR_MESSAGE);
                return DataProvider.LOGIN_ERROR;
            }
        }
        return DataProvider.LOGIN_OK;
    }

    public final boolean isPresent(final String expression) {
        try{
            final int out = this.GetIntegerArray("PRESENT( " + expression + " )")[0];
            System.out.println(">> " + expression + " present = " + out);
            return out > 0;
        }catch(final IOException exc){
            return false;
        }
    }

    @Override
    public void join() {
        // TODO Auto-generated method stub
    }

    @Override
    public final synchronized void RemoveConnectionListener(final ConnectionListener l) {
        if(DEBUG.M) System.out.println("mdsDataProvider.RemoveConnectionListener(" + l + ")");
        if(this.mds == null) return;
        this.mds.removeConnectionListener(l);
    }

    @Override
    public final synchronized void RemoveUpdateEventListener(final UpdateEventListener l, final String event_name) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.RemoveUpdateEventListener(" + l + "," + event_name + ")");
        if(event_name == null || event_name.trim().length() == 0) return;
        this.CheckConnection();
        this.mds.mdsRemoveEvent(l, event_name);
    }

    @Override
    public void SetArgument(final String arg) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.SetArgument(" + arg + ")");
        this.setProvider(arg);
        this.mds.setProvider(this.provider);
    }

    public void SetCompression(final boolean state) {
        if(DEBUG.M) System.out.println("mdsDataProvider.SetCompression(" + state + ")");
        if(this.connected) this.Dispose();
        this.use_compression = state;
    }

    @Override
    public final synchronized void SetEnvironment(final String in) throws IOException {
        if(DEBUG.M) System.out.println("mdsDataProvider.SetEnvironment(\"" + in + "\")");
        if(in == null || in.length() == 0) return;
        final Properties pr = new Properties();
        pr.load(new ByteArrayInputStream(in.getBytes()));
        String def_node = pr.getProperty("__default_node");
        if(def_node != null){
            def_node = def_node.trim();
            if(!(this.default_node != null && def_node.equals(this.default_node)) || (def_node.length() == 0 && this.default_node != null)){
                this.default_node = (def_node.length() == 0) ? null : def_node;
                this.def_node_changed = true;
            }
            return;
        }
        if(in.indexOf("pulseSetVer") >= 0) this.open = false;
        if(this.environment_vars == null || !this.environment_vars.equalsIgnoreCase(in)){
            this.open = false;
            this.environment_vars = in;
        }
    }

    private final void SetEnvironmentSpecific(final String in) {
        if(DEBUG.M) System.out.println("mdsDataProvider.SetEnvironmentSpecific(\"" + in + "\")");
        final Descriptor desc = this.mds.mdsValue(in);
        if(desc.dtype == Descriptor.DTYPE_CSTRING) if((desc.status & 1) == 0) this.error = desc.error;
    }

    public final void setProvider(final String arg) {
        if(DEBUG.M) System.out.println("mdsDataProvider.setProvider(" + arg + ")");
        if(this.is_tunneling) this.provider = this.tunnel_provider;
        else this.provider = arg;
    }

    @Override
    public boolean SupportsTunneling() {
        return true;
    }

    @Override
    public synchronized void Update(final String experiment, final long shot) {
        this.Update(experiment, shot, false);
    }

    public final synchronized void Update(final String experiment, final long shot, final boolean resetExperiment) {
        if(DEBUG.M) System.out.println("mdsDataProvider.Update(\"" + experiment + "\", " + shot + ", " + resetExperiment + ")");
        this.error = null;
        if(resetExperiment) this.experiment = null;
        if((shot != this.shot) || (shot == 0L) || (this.experiment == null) || (this.experiment.length() == 0) || (!this.experiment.equalsIgnoreCase(experiment))){
            this.experiment = ((experiment != null) && (experiment.trim().length() > 0) ? experiment : null);
            this.shot = shot;
            this.open = false;
        }
    }
}

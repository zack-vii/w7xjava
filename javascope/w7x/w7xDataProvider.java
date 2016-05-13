package w7x;

/* $Id$ */
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;
import javax.swing.JFrame;
import de.mpg.ipp.codac.signalaccess.Signal;
import de.mpg.ipp.codac.signalaccess.SignalReader;
import de.mpg.ipp.codac.signalaccess.readoptions.ReadOptions;
import de.mpg.ipp.codac.w7xtime.TimeInterval;
import debug.DEBUG;
import jScope.ConnectionListener;
import jScope.DataProvider;
import jScope.DataServerItem;
import jScope.FrameData;
import jScope.UpdateEventListener;
import jScope.WaveData;
import jScope.WaveDataListener;
import jScope.XYData;
import mds.Descriptor;
import mds.mdsDataProvider;

public final class w7xDataProvider implements DataProvider{
    private final class SimpleFrameData implements FrameData{
        private final class UpdateWorker extends Thread{
            @Override
            public final synchronized void run() {
                try{
                    final int abort = w7xDataProvider.this.abort;
                    this.setName(SimpleFrameData.this.in_y);
                    try{
                        final SignalReader sr_x = (SimpleFrameData.this.in_x == null) ? null : signalaccess.GetReader(SimpleFrameData.this.in_x);
                        final SignalReader sr_y = signalaccess.GetReader(SimpleFrameData.this.in_y);
                        if(sr_y != null) try{
                            while(!SimpleFrameData.this.frameQueue.isEmpty() && (abort == w7xDataProvider.this.abort)){
                                final int idx = SimpleFrameData.this.frameQueue.peek();
                                SimpleFrameData.this.getSignal(idx);
                                this.notify();
                            }
                        }finally{
                            if(sr_y != null) sr_y.close();
                            if(sr_x != null) sr_x.close();
                        }
                    }catch(final Exception e){
                        System.err.println("Error in asynchUpdate: " + e);
                    }
                }finally{
                    SimpleFrameData.this.updateWorker = null;
                    w7xDataProvider.threads.remove(this);
                }
            }
        }
        private PriorityQueue<Integer> frameQueue;
        int                            frameType = 0;
        private long                   from      = 0, upto = 0, orig = 0;
        String                         in_y, in_x;
        private final ReadOptions      ro        = ReadOptions.firstNSamples(1);
        private Signal                 sig_x;
        private List<Signal>           sig_y;
        private TimeInterval           TI;
        private List<TimeInterval>     todoList;
        private UpdateWorker           updateWorker;

        public SimpleFrameData(final String in_y, final String in_x, final float time_min, final float time_max){
            this.in_x = null;// in_x;
            this.in_y = in_y;
            try{
                final long[] timing = w7xDataProvider.this.getTiming();
                this.orig = timing.length > 2 ? timing[2] : 0L;
                this.from = timing[0];
                this.upto = timing[1];
                this.TI = signalaccess.getTimeInterval(this.from, this.upto);
                SimpleFrameData.this.updateLists();
                if(SimpleFrameData.this.frameQueue == null) return;
                this.updateWorker = new UpdateWorker();
                w7xDataProvider.threads.add(this.updateWorker);
                this.updateWorker.start();
            }catch(final Exception e){
                System.err.println(e);
            }
        }

        private final byte[] getByteAt(final Signal signal, final int index, final int frameType) throws IOException {
            if(signal == null) return new byte[]{};
            final int w = signal.getDimensionSize(1);
            final int h = signal.getDimensionSize(2);
            if(frameType == FrameData.BITMAP_IMAGE_8){
                final byte[] data = new byte[w * h];
                for(int iw = 0; iw < w; iw++)
                    for(int ih = 0; ih < h; ih++)
                        signal.getValue(Byte.class, new int[]{index, iw, ih});
                return data;
            }
            final ByteArrayOutputStream dosb = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(dosb);
            try{
                if(frameType == FrameData.BITMAP_IMAGE_16) for(int iw = 0; iw < w; iw++)
                    for(int ih = 0; ih < h; ih++)
                        dos.writeShort(signal.getValue(Integer.class, new int[]{index, iw, ih}));
                else if(frameType == FrameData.BITMAP_IMAGE_32) for(int iw = 0; iw < w; iw++)
                    for(int ih = 0; ih < h; ih++)
                        dos.writeInt(signal.getValue(Integer.class, new int[]{index, iw, ih}));
                else if(frameType == FrameData.BITMAP_IMAGE_FLOAT) for(int iw = 0; iw < w; iw++)
                    for(int ih = 0; ih < h; ih++)
                        dos.writeFloat(signal.getValue(Float.class, new int[]{index, iw, ih}));
                dos.close();
                return dosb.toByteArray();
            }catch(final IOException e){}
            return null;
        }

        @Override
        public final byte[] GetFrameAt(final int idx) throws IOException {
            this.getSignal(idx);
            return this.getByteAt(this.sig_y.get(idx), 0, this.GetFrameType());
        }

        @Override
        public final Dimension GetFrameDimension() throws IOException {
            return new Dimension(this.sig_y.get(0).getDimensionSize(2), this.sig_y.get(0).getDimensionSize(1));
        }

        @Override
        public final float[] GetFrameTimes() throws IOException {
            if(this.in_x == null){
                final long[] x = new long[this.todoList.size()];
                final float[] xd = new float[this.todoList.size()];
                for(int i = 0; i < x.length; i++){
                    x[i] = this.todoList.get(i).upto();
                    xd[i] = (x[i] - this.orig) / 1E9f;
                }
                return xd;
            }
            return signalaccess.GetFloat(this.sig_x);
        }

        @Override
        public final int GetFrameType() throws IOException {
            if(this.sig_y == null) return -1;
            if(this.frameType == 0){
                final Signal sig = this.getSignal(0);
                if(sig == null) return -1;
                final Class type = sig.getComponentType();
                if(type.equals(Byte.class)) this.frameType = FrameData.BITMAP_IMAGE_8;
                else if(type.equals(Short.class)) this.frameType = FrameData.BITMAP_IMAGE_16;
                else if(type.equals(Integer.class)) this.frameType = FrameData.BITMAP_IMAGE_32;
                else this.frameType = FrameData.BITMAP_IMAGE_FLOAT;
            }
            return this.frameType;
        }

        @Override
        public final int GetNumFrames() throws IOException {
            return this.todoList.size();
        }

        private final synchronized Signal getSignal(final int idx) throws IOException {
            final Signal sig;
            if(this.sig_x == null && this.in_x != null) this.sig_x = signalaccess.GetSignal(this.in_x, SimpleFrameData.this.from, SimpleFrameData.this.upto);
            if(idx >= this.todoList.size()) this.updateLists();
            if(!this.frameQueue.remove(idx)){
                if(idx >= this.todoList.size()) return null;
                if(this.sig_y.get(idx) == null) try{
                    this.wait();
                }catch(final InterruptedException e){
                    return null;
                }
                return this.sig_y.get(idx);
            }
            final TimeInterval ti = this.todoList.get(idx);
            SimpleFrameData.this.sig_y.set(idx, sig = signalaccess.GetSignal(this.in_y, ti, this.ro));
            return sig;
        }

        private final boolean updateLists() {
            SignalReader sr_y;
            try{
                sr_y = signalaccess.GetReader(this.in_y);
                try{
                    if(this.todoList == null){
                        this.todoList = sr_y.availableIntervals(this.TI);
                        if(this.todoList.isEmpty()) return false;
                        this.frameQueue = new PriorityQueue<Integer>(SimpleFrameData.this.todoList.size());
                        this.sig_y = new ArrayList<Signal>(this.todoList.size());
                        for(int i = 0; i < this.todoList.size(); i++){
                            this.frameQueue.add(i);
                            this.sig_y.add(null);
                        }
                    }else{
                        final int oldlength = this.todoList.size();
                        this.todoList.addAll(sr_y.availableIntervals(this.TI.restAfter(this.todoList.get(oldlength - 1))));
                        for(int i = oldlength; i < this.todoList.size(); i++){
                            this.frameQueue.add(i);
                            this.sig_y.add(null);
                        }
                    }
                }finally{
                    sr_y.close();
                }
                return true;
            }catch(final IOException e){}
            return false;
        }
    }
    private final class SimpleWaveData implements WaveData{
        private final class UpdateWorker extends Thread{
            SimpleWaveData swd;

            public UpdateWorker(final SimpleWaveData swd){
                this.swd = swd;
            }

            @Override
            public final synchronized void run() {
                try{
                    final int abort = w7xDataProvider.this.abort;
                    this.setName(this.swd.in_y);
                    try{
                        final SignalReader sr_y = signalaccess.GetReader(this.swd.in_y);
                        final SignalReader sr_x;
                        if(this.swd.in_x == null) sr_x = null;
                        else sr_x = signalaccess.GetReader(this.swd.in_x);
                        try{
                            while(this.swd.getSignals(sr_y, sr_x) && (abort == w7xDataProvider.this.abort)){
                                final XYData xydata = this.swd.getData(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, false);
                                for(int j = 0; j < this.swd.getWaveDataListeners().size(); j++)
                                    this.swd.getWaveDataListeners().elementAt(j).dataRegionUpdated(xydata);
                            }
                        }finally{
                            sr_y.close();
                            if(sr_x != null) sr_x.close();
                        }
                    }catch(final Exception e){
                        System.err.println("Error in asynchUpdate: " + e);
                    }
                }finally{
                    this.swd.updateWorker = null;
                    w7xDataProvider.threads.remove(this);
                }
            }
        }
        long                                   from, upto, orig = 0;
        String                                 in_x, in_y;
        private final ReadOptions              ro                 = ReadOptions.firstNSamples(100000);
        public Signal                          sig_x, sig_y;
        TimeInterval                           ti;
        UpdateWorker                           updateWorker;
        private final Vector<WaveDataListener> waveDataListenersV = new Vector<WaveDataListener>();

        public SimpleWaveData(final String in_y){
            this(in_y, null);
        }

        public SimpleWaveData(final String in_y, final String in_x){
            this.in_x = in_x;
            this.in_y = in_y;
            try{
                final long[] timing = w7xDataProvider.this.getTiming();
                this.orig = timing.length > 2 ? timing[2] : 0L;
                this.from = timing[0];
                this.upto = timing[1];
                this.ti = signalaccess.getTimeInterval(this.from, this.upto);
                this.updateWorker = new UpdateWorker(this);
                w7xDataProvider.threads.add(this.updateWorker);
                this.updateWorker.start();
            }catch(final IOException e){
                System.err.println(e);
            }
        }

        @Override
        public final void addWaveDataListener(final WaveDataListener listener) {
            this.waveDataListenersV.addElement(listener);
        }

        @Override
        public final void finalize() {
            if(this.updateWorker == null) return;
            if(this.updateWorker.isAlive()) this.updateWorker.interrupt();
        }

        @Override
        public final XYData getData(final double xmin, final double xmax, final int numPoints) throws Exception {
            return this.getData(xmin, xmax, numPoints, false);
        }

        public final XYData getData(final double xmin, final double xmax, final int numPoints, final boolean isXLong) throws Exception {
            if(this.in_x == null){
                final long[] x = this.getX2DLong();
                if(this.isXLong()) return new XYData(x, this.getZ(), Double.POSITIVE_INFINITY, true, xmin, xmax);
                final double[] xd = new double[x.length];
                for(int i = 0; i < x.length; i++)
                    xd[i] = (x[i] - this.orig) / 1E9;
                return new XYData(xd, this.getZ(), Double.POSITIVE_INFINITY, true, xmin, xmax);
            }
            if(this.isXLong()) return new XYData(this.getX2DLong(), this.getZ(), Double.POSITIVE_INFINITY);
            return new XYData(this.getX2D(), this.getZ(), Double.POSITIVE_INFINITY, false, xmin, xmax);
        }

        @Override
        public final XYData getData(final int numPoints) throws Exception {
            return this.getData(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, numPoints, false);
        }

        @Override
        public final void getDataAsync(final double xmin, final double xmax, final int numPoints) {
            if(DEBUG.M) System.out.println("w7xDataProvider.SimpleWaveData.getDataAsync" + xmin + ", " + xmax + ", " + numPoints + "");
            return;
        }

        @Override
        public final int getNumDimension() throws IOException {
            return (this.sig_y != null) ? this.sig_y.getDimensionCount() : 0;
        }

        private final boolean getSignals(final SignalReader sr_y, final SignalReader sr_x) throws IOException {
            long starttime = 0L;
            if(this.sig_y != null) this.ti = this.ti.restAfter(this.sig_y.getLastSampleTime());
            if(DEBUG.D) starttime = -System.nanoTime();
            final Signal sig = sr_y.readSignal(this.ti, this.ro);
            if(sig.isEmpty()) return false;
            this.sig_y = sig;
            this.sig_x = (sr_x == null) ? this.sig_y.getDimensionSignal(0) : sr_x.readSignal(this.ti, this.ro);
            if(DEBUG.D) System.out.println("getSignals took " + (System.nanoTime() - starttime) / 1E9 + "s for " + this.sig_y.getSampleCount() + " samples");
            return true;
        }

        @Override
        public final String GetTitle() throws IOException {
            return (this.sig_y != null) ? this.sig_y.getLabel() : null;
        }

        public final Vector<WaveDataListener> getWaveDataListeners() {
            return this.waveDataListenersV;
        }

        @Override
        public final double[] getX2D() {
            return signalaccess.GetDouble(this.sig_x);
        }

        @Override
        public final long[] getX2DLong() {
            return signalaccess.GetLong(this.sig_x);
        }

        @Override
        public final String GetXLabel() throws IOException {
            return (this.in_x != null) ? ((this.sig_x != null) ? this.sig_x.getUnit() : null) : (this.isXLong() ? "time" : "s");
        }

        @Override
        public final float[] getY2D() {
            return signalaccess.GetFloat(this.sig_y.getDimensionSignal(1));
        }

        @Override
        public final String GetYLabel() throws IOException {
            return (this.sig_y != null) ? ((this.getNumDimension() > 1) ? this.sig_y.getDimensionSignal(1).getUnit() : this.sig_y.getUnit()) : null;
        }

        @Override
        public final float[] getZ() {
            return signalaccess.GetFloat(this.sig_y);
        }

        @Override
        public final String GetZLabel() throws IOException {
            return (this.sig_y != null) ? this.sig_y.getUnit() : null;
        }

        @Override
        public final boolean isXLong() {
            return this.orig == 0L;
        }

        @Override
        public final void setContinuousUpdate(final boolean state) {}
    }
    public static final DateFormat      format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static w7xDataProvider       instance;
    private static final Vector<Thread> threads = new Vector<Thread>();
    private static long[]               Timing  = null;

    private static boolean isW7X(final String in) {
        return in.startsWith("/");
    }

    /*public static final void main(final String[] args) throws IOException {
    final w7xDataProvider dp = new w7xDataProvider();
    dp.mds.SetArgument("mds-data-1");
    dp.mds.CheckOpen("w7x", 160303007);
    final WaveData wd = dp.GetWaveData("/codac/W7X/CoDaStationDesc.16007/DataReductionProcessDesc.17547_DATASTREAM/8");
    try{
        wd.getData(0., .2, 1000);
    }catch(final Exception e){
        e.printStackTrace();
    }
    System.exit(0);
    }*/
    public static final void setTiming() {
        w7xDataProvider.instance.mds.mds.mdsValue("TIME(0)");
    }

    public static final void setTiming(final long from, final long upto) {
        w7xDataProvider.setTiming(from, upto, 0L);
    }

    public static final void setTiming(final long from, final long upto, final long orig) {
        w7xDataProvider.Timing = new long[]{from * 1000000L, upto * 1000000L, orig * 1000000L};
        final Vector<Descriptor> args = new Vector<Descriptor>();
        args.addElement(new Descriptor(null, new long[]{w7xDataProvider.Timing[0]}));
        args.addElement(new Descriptor(null, new long[]{w7xDataProvider.Timing[1]}));
        args.addElement(new Descriptor(null, new long[]{w7xDataProvider.Timing[2]}));
        System.out.println(w7xDataProvider.instance.mds.mds.mdsValue("T2STR(TIME($,$,$))", args).strdata);
    }

    public static final boolean SupportsCompression() {
        return mdsDataProvider.SupportsCompression();
    }

    public static final boolean SupportsFastNetwork() {
        return mdsDataProvider.SupportsFastNetwork();
    }
    private int                  abort = 0;
    private String               error;
    public final mdsDataProvider mds;
    private String               shot_cache_in;
    private long[]               shot_cache_out;

    public w7xDataProvider() throws IOException{
        w7xDataProvider.instance = this;
        mdsDataProvider mds = null;
        try{
            mds = new mdsDataProvider();
        }catch(final Exception e){
            System.err.println("Error loading mds.mdsDataProvider: " + e);
        }
        this.mds = mds;
    }

    @Override
    public final synchronized void abort() {
        this.abort++;
        this.notifyAll();
        for(final Thread th : w7xDataProvider.threads)
            if(th != null) th.interrupt();
    }

    @Override
    public final synchronized void AddConnectionListener(final ConnectionListener l) {
        if(this.mds != null) this.mds.AddConnectionListener(l);
    }

    @Override
    public final synchronized void AddUpdateEventListener(final UpdateEventListener l, final String event_name) throws IOException {
        if(this.mds != null) this.mds.AddUpdateEventListener(l, event_name);
    }

    @Override
    public final boolean checkProvider() {
        if(this.mds == null) if(!this.mds.checkProvider()) System.err.println(this.mds.ErrorString());
        return signalaccess.isConnected();
    }

    @Override
    public final synchronized void Dispose() {
        if(this.mds != null) this.mds.Dispose();
    }

    @Override
    public final synchronized String ErrorString() {
        String outerror = (this.mds == null) ? null : this.mds.ErrorString();
        if(this.error == null) return outerror;
        outerror = this.error;
        this.error = null;
        return this.error;
    }

    @Override
    public final Class getDefaultBrowser() {
        return w7xBrowseSignals.class;
    }

    @Override
    public final synchronized float GetFloat(final String in) throws IOException {
        return (this.mds == null) ? Float.NaN : this.mds.GetFloat(in);
    }

    @Override
    public final FrameData GetFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        if(w7xDataProvider.isW7X(in_y)) return new SimpleFrameData(in_y, in_x, time_min, time_max);
        if(this.mds != null) return this.mds.GetFrameData(in_y, in_x, time_min, time_max);
        return null;
    }

    @Override
    public final String GetLegendString(final String s) {
        return s;
    }

    public final int[] GetNumDimensions(final String in) throws IOException {
        if(w7xDataProvider.isW7X(in)){
            System.err.println("w7xDataProvider.GetNumDimensions not implemented!");
            return new int[]{1};
        }
        return this.mds.GetNumDimensions(in);
    }

    @Override
    public final long[] GetShots(final String in) throws IOException {
        if(DEBUG.M) System.out.println("GetShots(" + in + ")");
        if(in == null) return null;
        if(this.shot_cache_in != null && this.shot_cache_in == in) return this.shot_cache_out;
        if(in.startsWith("XP:")){
            final TimeInterval ti = signalaccess.getTimeInterval(in);
            if(ti.isNull()) return this.shot_cache_out = null;
            w7xDataProvider.Timing = new long[]{ti.from(), ti.upto(), ti.from()};
            return this.shot_cache_out = new long[]{-1l};
        }
        if(this.mds != null) return this.shot_cache_out = this.mds.GetShots(in);
        return this.shot_cache_out = null;
    }

    @Override
    public final synchronized String GetString(final String in) throws IOException {
        if(in == "TimeInterval") return String.format("from %s upto %s t0 %s", //
        w7xDataProvider.format.format(new Date(w7xDataProvider.Timing[0])), //
        w7xDataProvider.format.format(new Date(w7xDataProvider.Timing[1])), //
        w7xDataProvider.format.format(new Date(w7xDataProvider.Timing[2])));
        return (this.mds == null) ? in : this.mds.GetString(in);
    }

    public final long[] getTiming() throws IOException {
        try{
            final long shot = w7xDataProvider.this.mds.shot;
            if(shot < 1) return w7xDataProvider.this.mds.GetLongArray("TIME()");
            return w7xDataProvider.this.mds.GetLongArray("TIME(" + shot + ")");
        }catch(final Exception e){}
        if(w7xDataProvider.Timing != null) return w7xDataProvider.Timing;
        this.error = "Time not set! Use TIME(from,upto,origin) or specify a valid shot number.";
        throw new IOException(this.error);
    }

    @Override
    public final WaveData GetWaveData(final String in) {
        return w7xDataProvider.isW7X(in) ? new SimpleWaveData(in) : this.mds.GetWaveData(in);
    }

    @Override
    public final WaveData GetWaveData(final String in_y, final String in_x) {
        return w7xDataProvider.isW7X(in_y) ? new SimpleWaveData(in_y, in_x) : this.mds.GetWaveData(in_y, in_x);
    }

    @Override
    public final int InquireCredentials(final JFrame f, final DataServerItem si) {
        return (this.mds == null) ? 0 : this.mds.InquireCredentials(f, si);
    }

    @Override
    public final synchronized void join() {
        try{
            while(w7xDataProvider.threads.size() > 0)
                w7xDataProvider.threads.firstElement().join();
        }catch(final InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public final synchronized void RemoveConnectionListener(final ConnectionListener l) {
        if(this.mds != null) this.mds.RemoveConnectionListener(l);
    }

    @Override
    public final synchronized void RemoveUpdateEventListener(final UpdateEventListener l, final String event_name) throws IOException {
        if(this.mds != null) this.mds.RemoveUpdateEventListener(l, event_name);
    }

    @Override
    public final void SetArgument(final String arg) throws IOException {
        if(this.mds != null) this.mds.SetArgument(arg);
    }

    public final void SetCompression(final boolean state) {
        if(this.mds != null) this.mds.SetCompression(state);
    }

    @Override
    public final synchronized void SetEnvironment(final String in) throws IOException {
        if(this.mds != null) this.mds.SetEnvironment(in);
    }

    @Override
    public final boolean SupportsTunneling() {
        return (this.mds == null) ? false : this.mds.SupportsTunneling();
    }

    @Override
    public final synchronized void Update(final String expt, final long shot) {
        if(this.mds != null) this.mds.Update(expt, shot);
    }
}

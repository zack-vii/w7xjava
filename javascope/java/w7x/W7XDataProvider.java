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
import jscope.ConnectionListener;
import jscope.DataProvider;
import jscope.DataServerItem;
import jscope.FrameData;
import jscope.UpdateEventListener;
import jscope.WaveData;
import jscope.WaveDataListener;
import jscope.XYData;
import mds.Descriptor;
import mds.MdsDataProvider;

public final class W7XDataProvider implements DataProvider{
    private final class SimpleFrameData implements FrameData{
        private final class UpdateWorker extends Thread{
            private final int abort;

            public UpdateWorker(){
                super();
                this.abort = W7XDataProvider.this.abort;
            }

            @Override
            public final void run() {
                try{
                    this.setName(SimpleFrameData.this.in_y);
                    try{
                        final SignalReader sr_x = (SimpleFrameData.this.in_x == null) ? null : W7XSignalAccess.getReader(SimpleFrameData.this.in_x);
                        final SignalReader sr_y = W7XSignalAccess.getReader(SimpleFrameData.this.in_y);
                        if(sr_y != null) try{
                            while(!SimpleFrameData.this.frameQueue.isEmpty() && this.abort == W7XDataProvider.this.abort){
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
                    W7XDataProvider.threads.remove(this);
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
                final long[] timing = W7XDataProvider.this.getTiming();
                this.orig = timing.length > 2 ? timing[2] : 0L;
                this.from = timing[0];
                this.upto = timing[1];
                this.TI = W7XSignalAccess.getTimeInterval(this.from, this.upto);
                SimpleFrameData.this.updateLists();
                if(SimpleFrameData.this.frameQueue == null) return;
                this.updateWorker = new UpdateWorker();
                W7XDataProvider.threads.add(this.updateWorker);
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
        public final byte[] getFrameAt(final int idx) throws IOException {
            this.getSignal(idx);
            return this.getByteAt(this.sig_y.get(idx), 0, this.getFrameType());
        }

        @Override
        public final Dimension getFrameDimension() throws IOException {
            return new Dimension(this.sig_y.get(0).getDimensionSize(2), this.sig_y.get(0).getDimensionSize(1));
        }

        @Override
        public final float[] getFrameTimes() throws IOException {
            if(this.in_x == null){
                final long[] x = new long[this.todoList.size()];
                final float[] xd = new float[this.todoList.size()];
                for(int i = 0; i < x.length; i++){
                    x[i] = this.todoList.get(i).upto();
                    xd[i] = (x[i] - this.orig) / 1E9f;
                }
                return xd;
            }
            return W7XSignalAccess.getFloat(this.sig_x);
        }

        @Override
        public final int getFrameType() throws IOException {
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
        public final int getNumFrames() throws IOException {
            return this.todoList.size();
        }

        private final synchronized Signal getSignal(final int idx) throws IOException {
            final Signal sig;
            if(this.sig_x == null && this.in_x != null) this.sig_x = W7XSignalAccess.getSignal(this.in_x, SimpleFrameData.this.from, SimpleFrameData.this.upto);
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
            SimpleFrameData.this.sig_y.set(idx, sig = W7XSignalAccess.getSignal(this.in_y, ti, this.ro));
            return sig;
        }

        private final boolean updateLists() {
            SignalReader sr_y;
            try{
                sr_y = W7XSignalAccess.getReader(this.in_y);
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
            private final SimpleWaveData swd;
            private final int            abort;

            public UpdateWorker(final SimpleWaveData swd){
                this.swd = swd;
                this.abort = W7XDataProvider.this.abort;
            }

            @Override
            public final void run() {
                try{
                    this.setName(this.swd.in_y);
                    try{
                        final SignalReader sr_y = W7XSignalAccess.getReader(this.swd.in_y);
                        final SignalReader sr_x;
                        if(this.swd.in_x == null) sr_x = null;
                        else sr_x = W7XSignalAccess.getReader(this.swd.in_x);
                        try{
                            while(this.swd.getSignals(sr_y, sr_x) && this.abort == W7XDataProvider.this.abort){
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
                    W7XDataProvider.threads.remove(this);
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
                final long[] timing = W7XDataProvider.this.getTiming();
                this.orig = timing.length > 2 ? timing[2] : 0L;
                this.from = timing[0];
                this.upto = timing[1];
                this.ti = W7XSignalAccess.getTimeInterval(this.from, this.upto);
                this.updateWorker = new UpdateWorker(this);
                W7XDataProvider.threads.add(this.updateWorker);
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
            if(DEBUG.M) System.out.println("W7XDataProvider.SimpleWaveData.getDataAsync" + xmin + ", " + xmax + ", " + numPoints + "");
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
        public final String getTitle() throws IOException {
            return (this.sig_y != null) ? this.sig_y.getLabel() : null;
        }

        public final Vector<WaveDataListener> getWaveDataListeners() {
            return this.waveDataListenersV;
        }

        @Override
        public final double[] getX2D() {
            return W7XSignalAccess.getDouble(this.sig_x);
        }

        @Override
        public final long[] getX2DLong() {
            return W7XSignalAccess.getLong(this.sig_x);
        }

        @Override
        public final String getXLabel() throws IOException {
            return (this.in_x != null) ? ((this.sig_x != null) ? this.sig_x.getUnit() : null) : (this.isXLong() ? "time" : "s");
        }

        @Override
        public final float[] getY2D() {
            return W7XSignalAccess.getFloat(this.sig_y.getDimensionSignal(1));
        }

        @Override
        public final String getYLabel() throws IOException {
            return (this.sig_y != null) ? ((this.getNumDimension() > 1) ? this.sig_y.getDimensionSignal(1).getUnit() : this.sig_y.getUnit()) : null;
        }

        @Override
        public final float[] getZ() {
            return W7XSignalAccess.getFloat(this.sig_y);
        }

        @Override
        public final String getZLabel() throws IOException {
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
    public static W7XDataProvider       instance;
    private static final Vector<Thread> threads = new Vector<Thread>();
    private static long[]               Timing  = null;

    private static boolean isW7X(final String in) {
        return in.startsWith("/");
    }

    public static final void setTiming() {
        W7XDataProvider.instance.mds.mds.mdsValue("TIME(0)");
    }

    public static final void setTiming(final long from, final long upto) {
        W7XDataProvider.setTiming(from, upto, 0L);
    }

    public static final void setTiming(final long from, final long upto, final long orig) {
        W7XDataProvider.Timing = new long[]{from * 1000000L, upto * 1000000L, orig * 1000000L};
        final Vector<Descriptor> args = new Vector<Descriptor>();
        args.addElement(new Descriptor(null, new long[]{W7XDataProvider.Timing[0]}));
        args.addElement(new Descriptor(null, new long[]{W7XDataProvider.Timing[1]}));
        args.addElement(new Descriptor(null, new long[]{W7XDataProvider.Timing[2]}));
        if(W7XDataProvider.instance.mds.mds.connected) System.out.println(W7XDataProvider.instance.mds.mds.mdsValue("T2STR(TIME($,$,$))", args).strdata);
    }
    private int                  abort = 0;
    private String               error;
    public final MdsDataProvider mds;
    private String               shot_cache_in;
    private long[]               shot_cache_out;

    public W7XDataProvider() throws IOException{
        W7XDataProvider.instance = this;
        MdsDataProvider mds = null;
        try{
            mds = new MdsDataProvider();
        }catch(final Exception e){
            System.err.println("Error loading mds.MdsDataProvider: " + e);
        }
        this.mds = mds;
    }

    @Override
    synchronized public final void abort() {
        this.abort++;
        this.notifyAll();
        this.mds.abort();
        for(final Thread th : W7XDataProvider.threads)
            if(th != null) th.interrupt();
    }

    @Override
    synchronized public final void addConnectionListener(final ConnectionListener l) {
        if(this.mds != null) this.mds.addConnectionListener(l);
    }

    @Override
    synchronized public final void addUpdateEventListener(final UpdateEventListener l, final String event_name) throws IOException {
        if(this.mds != null) this.mds.addUpdateEventListener(l, event_name);
    }

    @Override
    public final boolean checkProvider() {
        if(this.mds == null) if(!this.mds.checkProvider()) System.err.println(this.mds.errorString());
        return W7XSignalAccess.isConnected();
    }

    @Override
    public final synchronized void dispose() {
        if(this.mds != null) this.mds.dispose();
    }

    @Override
    public final synchronized String errorString() {
        String outerror = (this.mds == null) ? null : this.mds.errorString();
        if(this.error == null) return outerror;
        outerror = this.error;
        this.error = null;
        return this.error;
    }

    @Override
    public final Class getDefaultBrowser() {
        return W7XBrowseSignals.class;
    }

    @Override
    public final float getFloat(final String in) throws IOException {
        return (this.mds == null) ? Float.NaN : this.mds.getFloat(in);
    }

    @Override
    public final FrameData getFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        if(W7XDataProvider.isW7X(in_y)) return new SimpleFrameData(in_y, in_x, time_min, time_max);
        if(this.mds != null) return this.mds.getFrameData(in_y, in_x, time_min, time_max);
        return null;
    }

    @Override
    public final String getLegendString(final String s) {
        return s;
    }

    public final int[] getNumDimensions(final String in) throws IOException {
        if(W7XDataProvider.isW7X(in)){
            System.err.println("W7XDataProvider.getNumDimensions not implemented!");
            return new int[]{1};
        }
        return this.mds.getNumDimensions(in);
    }

    @Override
    public final long[] getShots(final String in) throws IOException {
        if(DEBUG.M) System.out.println("GetShots(" + in + ")");
        if(in == null) return new long[]{-1};
        if(this.shot_cache_in != null && this.shot_cache_in == in) return this.shot_cache_out;
        if(in.startsWith("XP:")){
            final TimeInterval ti = W7XSignalAccess.getTimeInterval(in);
            if(ti.isNull()) return this.shot_cache_out = null;
            W7XDataProvider.Timing = new long[]{ti.from(), ti.upto(), ti.from()};
            return this.shot_cache_out = new long[]{-1l};
        }
        if(this.mds != null) return this.shot_cache_out = this.mds.getShots(in);
        return this.shot_cache_out = null;
    }

    @Override
    public final String getString(final String in) throws IOException {
        if(in == "TimeInterval") return String.format("from %s upto %s t0 %s", //
                W7XDataProvider.format.format(new Date(W7XDataProvider.Timing[0])), //
                W7XDataProvider.format.format(new Date(W7XDataProvider.Timing[1])), //
                W7XDataProvider.format.format(new Date(W7XDataProvider.Timing[2])));
        return (this.mds == null) ? in : this.mds.getString(in);
    }

    public final long[] getTiming() throws IOException {
        try{
            final long shot = W7XDataProvider.this.mds.shot;
            if(shot < 1) return W7XDataProvider.this.mds.getLongArray("TIME()");
            return W7XDataProvider.this.mds.getLongArray("TIME(" + shot + ")");
        }catch(final Exception e){}
        if(W7XDataProvider.Timing != null) return W7XDataProvider.Timing;
        this.error = "Time not set! Use TIME(from,upto,origin) or specify a valid shot number.";
        throw new IOException(this.error);
    }

    @Override
    public final WaveData getWaveData(final String in) {
        return W7XDataProvider.isW7X(in) ? new SimpleWaveData(in) : this.mds.getWaveData(in);
    }

    @Override
    public final WaveData getWaveData(final String in_y, final String in_x) {
        return W7XDataProvider.isW7X(in_y) ? new SimpleWaveData(in_y, in_x) : this.mds.getWaveData(in_y, in_x);
    }

    @Override
    public final int inquireCredentials(final JFrame f, final DataServerItem si) {
        return (this.mds == null) ? 0 : this.mds.inquireCredentials(f, si);
    }

    @Override
    public final void join() {
        try{
            while(W7XDataProvider.threads.size() > 0)
                W7XDataProvider.threads.firstElement().join();
        }catch(final InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public final synchronized void removeConnectionListener(final ConnectionListener l) {
        if(this.mds != null) this.mds.removeConnectionListener(l);
    }

    @Override
    public final synchronized void removeUpdateEventListener(final UpdateEventListener l, final String event_name) throws IOException {
        if(this.mds != null) this.mds.removeUpdateEventListener(l, event_name);
    }

    @Override
    public final void setArgument(final String arg) throws IOException {
        if(this.mds != null) this.mds.setArgument(arg);
    }

    @Override
    public final void setEnvironment(final String in) throws IOException {
        if(this.mds != null) this.mds.setEnvironment(in);
    }

    @Override
    public final boolean supportsTunneling() {
        return (this.mds == null) ? false : this.mds.supportsTunneling();
    }

    @Override
    public final void update(final String expt, final long shot) {
        if(this.mds != null) this.mds.update(expt, shot);
    }
}

package w7x;
// by Timo Schroeder 2016.03.16

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.mpg.ipp.codac.archive.XPtools;
import de.mpg.ipp.codac.signalaccess.IndexIterator;
import de.mpg.ipp.codac.signalaccess.Signal;
import de.mpg.ipp.codac.signalaccess.SignalAddress;
import de.mpg.ipp.codac.signalaccess.SignalAddressBuilder;
import de.mpg.ipp.codac.signalaccess.SignalReader;
import de.mpg.ipp.codac.signalaccess.SignalToolsFactory;
import de.mpg.ipp.codac.signalaccess.SignalsTreeLister;
import de.mpg.ipp.codac.signalaccess.objylike.ArchieToolsFactory;
import de.mpg.ipp.codac.signalaccess.readoptions.ReadOptions;
import de.mpg.ipp.codac.w7xtime.TimeInterval;

public final class W7XSignalAccess{
    /** Helper class to asynchronously read Signals **/
    private final static class SignalFetcher extends Thread{
        private final String       path;
        private final ReadOptions  options;
        private final TimeInterval interval;
        private Signal             signal;

        public SignalFetcher(final String path, final TimeInterval interval, final ReadOptions options){
            this.path = path;
            this.interval = interval;
            this.options = options;
        }

        /** Returns the Signal object **/
        public final Signal getSignal() {
            try{
                this.join();
                return this.signal;
            }catch(final InterruptedException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public final void run() {
            SignalReader reader;
            try{
                reader = W7XSignalAccess.getReader(this.path);
                try{
                    this.signal = reader.readSignal(this.interval, this.options);
                }finally{
                    reader.close();
                }
            }catch(final IOException e){
                e.printStackTrace();
            }
            System.out.println(this.signal.getLastSampleTime() + " : " + this.signal.getDimensionSize(0));
        }
    }
    public static final String                        help         = "usage:\nString   path = \"/ArchiveDB/codac/W7X/CoDaStationDesc.111/DataModuleDesc.250_DATASTREAM/15/L1_ECA63/scaled\";\nString     xp = \"XP:20160310.7\";\nSignal signal = W7XSignalAccess.getSignal(path,xp);\ndouble[] data = W7XSignalAccess.getDouble(signal);\nint[]   shape = W7XSignalAccess.getShape(signal);\nlong[]   time = W7XSignalAccess.getDimension(signal);";
    private static final Map<String, W7XSignalAccess> access       = new HashMap<String, W7XSignalAccess>(2);
    private static final int                          MAX_SAMPLES  = 64000000;
    private static final List<String>                 databaselist = new ArrayList<String>(2);
    static{
        W7XSignalAccess.databaselist.add("ArchiveDB");
        W7XSignalAccess.databaselist.add("Test");
    }

    /** Returns an instance of W7XSignalAccess based the given path String **/
    /*package*/static final W7XSignalAccess getAccess(final String path) {
        String name;
        if(path.startsWith("/")) name = path.split("/", 3)[1];
        else name = path.split("/", 2)[0];
        if(!W7XSignalAccess.databaselist.contains(name)) name = W7XSignalAccess.databaselist.get(0);
        if(!W7XSignalAccess.access.containsKey(name)) W7XSignalAccess.access.put(name, W7XSignalAccess.newInstance(name));
        return W7XSignalAccess.access.get(name);
    }

    /** Returns the data vector of the given Signal object as byte[] **/
    public static final byte[] getByte(final Signal signal) {
        if(signal == null) return new byte[]{};
        final int count = signal.getSampleCount();
        final byte[] data = new byte[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Byte.class, iter.next()).byteValue();
        return data;
    }

    /** Returns the list of known data bases **/
    public static final List<String> getDataBaseList() {
        return W7XSignalAccess.databaselist;
    }

    /** Returns the time vector of the given Signal object as long[] **/
    public static final long[] getDimension(final Signal signal) throws IOException {
        return W7XSignalAccess.getLong(signal.getDimensionSignal(0));
    }

    /** Returns the data vector of the given Signal object as double[] **/
    public static final double[] getDouble(final Signal signal) {
        if(signal == null) return new double[]{};
        final int count = signal.getSampleCount();
        final double[] data = new double[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Double.class, iter.next());
        return data;
    }

    /** Returns the data vector of the given Signal object as float[] **/
    public static final float[] getFloat(final Signal signal) {
        if(signal == null) return new float[]{};
        final int count = signal.getSampleCount();
        final float[] data = new float[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Double.class, iter.next()).floatValue();
        return data;
    }

    /** Returns the data vector of the given Signal object as int[] **/
    public static final int[] getInteger(final Signal signal) {
        if(signal == null) return new int[]{};
        final int count = signal.getSampleCount();
        final int[] data = new int[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Integer.class, iter.next()).intValue();
        return data;
    }

    /** Returns the addresses of available children of the given path String in the given TimeInterval **/
    public static final List<SignalAddress> getList(final String path, final TimeInterval interval) {
        return W7XSignalAccess.getAccess(path).getList_(path, interval);
    }

    /** Returns the data vector of the given Signal object as long[] **/
    public static final long[] getLong(final Signal signal) {
        if(signal == null) return new long[]{};
        final int count = signal.getSampleCount();
        final long[] data = new long[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Long.class, iter.next());
        return data;
    }

    /** Returns the SignalReader object of the given path String **/
    public static final SignalReader getReader(final String path) throws IOException {
        return W7XSignalAccess.getAccess(path).getReader_(path);
    }

    /** Returns the shape of the given Signal object **/
    public static final int[] getShape(final Signal signal) {
        final int ndims = signal.getDimensionCount();
        final int[] shape = new int[ndims];
        for(int i = 0; i < ndims; i++)
            shape[i] = signal.getDimensionSize(i);
        return shape;
    }

    /** Returns the data vector of the given Signal object as short[] **/
    public static final short[] getShort(final Signal signal) {
        if(signal == null) return new short[]{};
        final int count = signal.getSampleCount();
        final short[] data = new short[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Short.class, iter.next()).shortValue();
        return data;
    }

    /** Returns the Signal object based on path String and from- and upto time stamps **/
    public static final Signal getSignal(final String path, final long from, final long upto) throws IOException {
        return W7XSignalAccess.getSignal(path, from, upto, W7XSignalAccess.MAX_SAMPLES);
    }

    /** Returns the Signal object based on path String and from- and upto time stamps limited to a number of samples **/
    public static final Signal getSignal(final String path, final long from, final long upto, final int samples) throws IOException {
        final TimeInterval interval = W7XSignalAccess.getTimeInterval(from, upto);
        final ReadOptions options = ReadOptions.firstNSamples(samples);
        return W7XSignalAccess.getSignal(path, interval, options);
    }

    /** Returns the Signal object based on path String and XP number **/
    public static final Signal getSignal(final String path, final String xp) throws IOException {
        return W7XSignalAccess.getSignal(path, W7XSignalAccess.getTimeInterval(xp));
    }

    /** Returns the Signal object based on path String and TimeInterval **/
    public static final Signal getSignal(final String path, final TimeInterval interval) throws IOException {
        final ReadOptions options = ReadOptions.firstNSamples(W7XSignalAccess.MAX_SAMPLES);
        return W7XSignalAccess.getSignal(path, interval, options);
    }

    /** Returns the Signal object based on path String and TimeInterval with ReadOptions **/
    public static final Signal getSignal(final String path, final TimeInterval interval, final ReadOptions options) throws IOException {
        return W7XSignalAccess.getAccess(path).getSignal_(path, interval, options);
    }

    /** Returns the data vector of the given Signal object as String[] **/
    public static final String[] getString(final Signal signal) {
        if(signal == null) return new String[]{};
        final int count = signal.getSampleCount();
        final String[] data = new String[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(String.class, iter.next());
        return data;
    }

    /** Returns the TimeInterval defined by from and upto **/
    public static final TimeInterval getTimeInterval(final long from, final long upto) {
        return TimeInterval.ALL.withStart(from).withEnd(upto);
    }

    /** Returns the TimeInterval defined by the XP number **/
    public static final TimeInterval getTimeInterval(final String xp) {
        return XPtools.xp2TimeInterval(xp);
    }

    /** Prints the 'help' example **/
    public static final String help() {
        System.out.println(W7XSignalAccess.help);
        return W7XSignalAccess.help;
    }

    /** Returns true if W7XSignalAccess is properly connected to the W7X-Archive **/
    public static final boolean isConnected() {
        return W7XSignalAccess.getAccess(W7XSignalAccess.databaselist.get(0)) != null;
    }

    /** Returns a new instance of W7XSignalAccess linked to the given data base **/
    private static final W7XSignalAccess newInstance(final String database) {
        try{
            return new W7XSignalAccess(database);
        }catch(final Exception e){}
        return null;
    }

    /** Returns a list of Signal chunks multi-threaded read based on path String and TimeInterval **/
    public static final Signal[] readBoxes(final String path, final TimeInterval interval) throws IOException {
        final ReadOptions options = ReadOptions.firstNSamples(W7XSignalAccess.MAX_SAMPLES);
        final SignalReader reader = W7XSignalAccess.getReader(path);
        final TimeInterval[] interval_array;
        try{
            interval_array = reader.availableIntervals(interval).toArray(new TimeInterval[0]);
        }finally{
            reader.close();
        }
        final SignalFetcher[] fetchers = new SignalFetcher[interval_array.length];
        final Signal[] signals = new Signal[interval_array.length];
        for(int i = 0; i < fetchers.length; i++)
            fetchers[i] = new SignalFetcher(path, interval_array[i], options);
        for(final SignalFetcher fetcher : fetchers)
            fetcher.start();
        for(int i = 0; i < fetchers.length; i++)
            signals[i] = fetchers[i].getSignal();
        return signals;
    }
    public final String                database;
    private final SignalAddressBuilder sab;
    private final SignalToolsFactory   stf;

    /** Constructs an instance of W7XSignalAccess linked to the given database **/
    public W7XSignalAccess(final String database){
        this.database = database;
        this.stf = ArchieToolsFactory.remoteArchive(database);
        this.sab = this.stf.makeSignalAddressBuilder(new String[0]);
    }

    private final boolean checkPath(final String path) {
        return path.startsWith(String.format("/%s/", this.database));
    }

    @Override
    public final void finalize() {
        this.stf.close();
    }

    /*package*/final SignalAddress getAddress(String path) {
        if(this.checkPath(path)) path = path.substring(this.database.length() + 1);
        return this.sab.newBuilder().path(path).build();
    }

    @SuppressWarnings("unchecked")
    private final List<SignalAddress> getList_(final String path, final TimeInterval interval) {
        final SignalsTreeLister stl = this.stf.makeSignalsTreeLister();
        try{
            return (List<SignalAddress>)stl.listFor(interval, path);
        }finally{
            stl.close();
        }
    }

    private final SignalReader getReader_(final String path) {
        return this.stf.makeSignalReader(this.getAddress(path));
    }

    private final Signal getSignal_(final String path, final TimeInterval interval, final ReadOptions options) throws IOException {
        final SignalReader sr = this.getReader_(path);
        try{
            return sr.readSignal(interval, options);
        }finally{
            sr.close();
        }
    }
}

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

public final class Signalaccess{
    public final static class SignalFetcher extends Thread{
        public static final Signal[] readBoxes(final String path, final TimeInterval TI) throws IOException {
            final ReadOptions ro = ReadOptions.firstNSamples(Signalaccess.MAX_SAMPLES);
            final SignalReader R = Signalaccess.getReader(path);
            final TimeInterval[] tis;
            try{
                tis = R.availableIntervals(TI).toArray(new TimeInterval[0]);
            }finally{
                R.close();
            }
            final SignalFetcher[] fetchers = new SignalFetcher[tis.length];
            final Signal[] signals = new Signal[tis.length];
            for(int i = 0; i < fetchers.length; i++)
                fetchers[i] = new SignalFetcher(path, tis[i], ro);
            for(final SignalFetcher fetcher : fetchers)
                fetcher.start();
            for(int i = 0; i < fetchers.length; i++)
                signals[i] = fetchers[i].getSignal();
            return signals;
        }
        private final String       path;
        private final ReadOptions  ro;
        private Signal             s;
        private final TimeInterval ti;

        public SignalFetcher(final String path, final TimeInterval ti, final ReadOptions ro){
            this.path = path;
            this.ti = ti;
            this.ro = ro;
        }

        public final Signal getSignal() {
            try{
                this.join();
                return this.s;
            }catch(final InterruptedException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public final void run() {
            SignalReader sr;
            try{
                sr = Signalaccess.getReader(this.path);
                try{
                    this.s = sr.readSignal(this.ti, this.ro);
                }finally{
                    sr.close();
                }
            }catch(final IOException e){
                e.printStackTrace();
            }
            System.out.println(this.s.getLastSampleTime() + " : " + this.s.getDimensionSize(0));
        }
    }
    private static final Map<String, Signalaccess> access       = new HashMap<String, Signalaccess>(2);
    private static final List<String>              databaselist = new ArrayList<String>(2);
    public static final String                     help         = "usage:\nString   path = \"/ArchiveDB/codac/W7X/CoDaStationDesc.111/DataModuleDesc.250_DATASTREAM/15/L1_ECA63/scaled\";\nString     xp = \"XP:20160310.7\";\nSignal signal = Signalaccess.getSignal(path,xp);\ndouble[] data = Signalaccess.getDouble(signal);\nint[]   shape = Signalaccess.getShape(signal);\nlong[]   time = Signalaccess.getDimension(signal);";
    public static final int                        MAX_SAMPLES  = 64000000;

    static{
        Signalaccess.databaselist.add("ArchiveDB");
        Signalaccess.databaselist.add("Test");
    }

    public static final Signalaccess getAccess(final String path) {
        String name;
        if(path.startsWith("/")) name = path.split("/", 3)[1];
        else name = path.split("/", 2)[0];
        if(!Signalaccess.databaselist.contains(name)) name = Signalaccess.databaselist.get(0);
        if(!Signalaccess.access.containsKey(name)) Signalaccess.access.put(name, Signalaccess.NewInstance(name));
        return Signalaccess.access.get(name);
    }

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

    public static final List<String> getDataBaseList() {
        return Signalaccess.databaselist;
    }

    public static final long[] getDimension(final Signal signal) throws IOException {
        return Signalaccess.getLong(signal.getDimensionSignal(0));
    }

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

    public static final List<SignalAddress> getList(final String path, final TimeInterval ti) {
        return Signalaccess.getAccess(path).getList_(path, ti);
    }

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

    public static final SignalReader getReader(final String path) throws IOException {
        return Signalaccess.getAccess(path).getReader_(path);
    }

    public static final int[] getShape(final Signal signal) {
        final int ndims = signal.getDimensionCount();
        final int[] shape = new int[ndims];
        for(int i = 0; i < ndims; i++)
            shape[i] = signal.getDimensionSize(i);
        return shape;
    }

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

    public static final Signal getSignal(final String path, final long from, final long upto) throws IOException {
        return Signalaccess.getSignal(path, from, upto, Signalaccess.MAX_SAMPLES);
    }

    public static final Signal getSignal(final String path, final long from, final long upto, final int nSamples) throws IOException {
        final TimeInterval ti = Signalaccess.getTimeInterval(from, upto);
        final ReadOptions ro = ReadOptions.firstNSamples(nSamples);
        return Signalaccess.getSignal(path, ti, ro);
    }

    public static final Signal getSignal(final String path, final String XP) throws IOException {
        return Signalaccess.getSignal(path, Signalaccess.getTimeInterval(XP));
    }

    public static final Signal getSignal(final String path, final TimeInterval ti) throws IOException {
        final ReadOptions ro = ReadOptions.firstNSamples(Signalaccess.MAX_SAMPLES);
        return Signalaccess.getSignal(path, ti, ro);
    }

    public static final Signal getSignal(final String path, final TimeInterval ti, final ReadOptions ro) throws IOException {
        return Signalaccess.getAccess(path).getSignal_(path, ti, ro);
    }

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

    public static final TimeInterval getTimeInterval(final long from, final long upto) {
        return TimeInterval.ALL.withStart(from).withEnd(upto);
    }

    public static final TimeInterval getTimeInterval(final String XP) {
        return XPtools.xp2TimeInterval(XP);
    }

    public static final String help() {
        System.out.println(Signalaccess.help);
        return Signalaccess.help;
    }

    public static final boolean isConnected() {
        return Signalaccess.getAccess(Signalaccess.databaselist.get(0)) != null;
    }

    /*
    public static void main(final String[] args) {//TODO:main
        final String path = "/ArchiveDB/codac/W7X/CoDaStationDesc.111/DataModuleDesc.250_DATASTREAM/0";// "/ArchiveDB/codac/W7X/CoDaStationDesc.14823/DataModuleDesc.14833_DATASTREAM/0/image/scaled";
        final String XP = "XP:20160310.7";// "XP:20160210.5";
        final TimeInterval TI = Signalaccess.getTimeInterval(XP);
        try{
            final ReadOptions ro = ReadOptions.fetchAll();
            final SignalReader R = Signalaccess.getReader(path);
            final List<TimeInterval> T = R.availableIntervals(TI);
            try{
                long time = -System.nanoTime();
                System.out.println(R.readSignal(TI, ro));// SignalFetcher.readBoxes(path, TI);
                time += System.nanoTime();
                System.out.println(time / 1e9);
                for(int i = 0; i < T.size(); i++){
                    time = -System.nanoTime();
                    System.out.println(R.readSignal(T.get(i), ro));
                    time += System.nanoTime();
                    System.out.println(time / 1e9);
                }
            }finally{
                R.close();
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
    }
    */
    public static final Signalaccess NewInstance(final String DataBase) {
        try{
            return new Signalaccess(DataBase);
        }catch(final Exception e){}
        return null;
    }
    public final String                dbName;
    private final SignalAddressBuilder sab;
    private final SignalToolsFactory   stf;

    public Signalaccess(final String dbName){
        this.dbName = dbName;
        this.stf = ArchieToolsFactory.remoteArchive(dbName);
        this.sab = this.stf.makeSignalAddressBuilder(new String[0]);
    }

    private final boolean checkPath(final String path) {
        return path.startsWith(String.format("/%s/", this.dbName));
    }

    @Override
    public final void finalize() {
        this.stf.close();
    }

    final SignalAddress getAddress(String path) {
        if(this.checkPath(path)) path = path.substring(this.dbName.length() + 1);
        return this.sab.newBuilder().path(path).build();
    }

    public final List<SignalAddress> getList_(final String path) {
        return this.getList_(path, TimeInterval.ALL);
    }

    @SuppressWarnings("unchecked")
    public final List<SignalAddress> getList_(final String path, final TimeInterval ti) {
        final SignalsTreeLister stl = this.stf.makeSignalsTreeLister();
        try{
            return (List<SignalAddress>)stl.listFor(ti, path);
        }finally{
            stl.close();
        }
    }

    private final SignalReader getReader_(final String path) {
        return this.stf.makeSignalReader(this.getAddress(path));
    }

    private final Signal getSignal_(final String path, final TimeInterval ti, final ReadOptions ro) throws IOException {
        final SignalReader sr = this.getReader_(path);
        try{
            return sr.readSignal(ti, ro);
        }finally{
            sr.close();
        }
    }
}

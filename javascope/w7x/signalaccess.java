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

public final class signalaccess{
    public final static class SignalFetcher extends Thread{
        public static final Signal[] readBoxes(final String path, final TimeInterval TI) throws IOException {
            final ReadOptions ro = ReadOptions.firstNSamples(signalaccess.MAX_SAMPLES);
            final SignalReader R = signalaccess.GetReader(path);
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
                sr = signalaccess.GetReader(this.path);
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
    private static final Map<String, signalaccess> access       = new HashMap<String, signalaccess>(2);
    private static final List<String>              databaselist = new ArrayList<String>(2);
    public static final String                     help         = "usage:\nString   path = \"/ArchiveDB/codac/W7X/CoDaStationDesc.111/DataModuleDesc.250_DATASTREAM/15/L1_ECA63/scaled\";\nString     xp = \"XP:20160310.7\";\nSignal signal = signalaccess.GetSignal(path,xp);\ndouble[] data = signalaccess.GetDouble(signal);\nint[]   shape = signalaccess.GetShape(signal);\nlong[]   time = signalaccess.GetDimension(signal);";
    public static final int                        MAX_SAMPLES  = 64000000;

    static{
        signalaccess.databaselist.add("ArchiveDB");
        signalaccess.databaselist.add("Test");
    }

    public static final signalaccess getAccess(final String path) {
        String name;
        if(path.startsWith("/")) name = path.split("/", 3)[1];
        else name = path.split("/", 2)[0];
        if(!signalaccess.databaselist.contains(name)) name = signalaccess.databaselist.get(0);
        if(!signalaccess.access.containsKey(name)) signalaccess.access.put(name, signalaccess.NewInstance(name));
        return signalaccess.access.get(name);
    }

    public static final byte[] GetByte(final Signal signal) {
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
        return signalaccess.databaselist;
    }

    public static final long[] GetDimension(final Signal signal) throws IOException {
        return signalaccess.GetLong(signal.getDimensionSignal(0));
    }

    public static final double[] GetDouble(final Signal signal) {
        if(signal == null) return new double[]{};
        final int count = signal.getSampleCount();
        final double[] data = new double[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Double.class, iter.next());
        return data;
    }

    public static final float[] GetFloat(final Signal signal) {
        if(signal == null) return new float[]{};
        final int count = signal.getSampleCount();
        final float[] data = new float[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Double.class, iter.next()).floatValue();
        return data;
    }

    public static final int[] GetInteger(final Signal signal) {
        if(signal == null) return new int[]{};
        final int count = signal.getSampleCount();
        final int[] data = new int[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Integer.class, iter.next()).intValue();
        return data;
    }

    public static final List<SignalAddress> GetList(final String path, final TimeInterval ti) {
        return signalaccess.getAccess(path).getList(path, ti);
    }

    public static final long[] GetLong(final Signal signal) {
        if(signal == null) return new long[]{};
        final int count = signal.getSampleCount();
        final long[] data = new long[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Long.class, iter.next());
        return data;
    }

    public static final SignalReader GetReader(final String path) throws IOException {
        return signalaccess.getAccess(path).getReader(path);
    }

    public static final int[] GetShape(final Signal signal) {
        final int ndims = signal.getDimensionCount();
        final int[] shape = new int[ndims];
        for(int i = 0; i < ndims; i++)
            shape[i] = signal.getDimensionSize(i);
        return shape;
    }

    public static final short[] GetShort(final Signal signal) {
        if(signal == null) return new short[]{};
        final int count = signal.getSampleCount();
        final short[] data = new short[count];
        if(count == 0) return data;
        final IndexIterator iter = IndexIterator.of(signal);
        for(int i = 0; i < count; i++)
            data[i] = signal.getValue(Short.class, iter.next()).shortValue();
        return data;
    }

    public static final Signal GetSignal(final String path, final long from, final long upto) throws IOException {
        return signalaccess.GetSignal(path, from, upto, signalaccess.MAX_SAMPLES);
    }

    public static final Signal GetSignal(final String path, final long from, final long upto, final int nSamples) throws IOException {
        final TimeInterval ti = signalaccess.getTimeInterval(from, upto);
        final ReadOptions ro = ReadOptions.firstNSamples(nSamples);
        return signalaccess.GetSignal(path, ti, ro);
    }

    public static final Signal GetSignal(final String path, final String XP) throws IOException {
        return signalaccess.GetSignal(path, signalaccess.getTimeInterval(XP));
    }

    public static final Signal GetSignal(final String path, final TimeInterval ti) throws IOException {
        final ReadOptions ro = ReadOptions.firstNSamples(signalaccess.MAX_SAMPLES);
        return signalaccess.GetSignal(path, ti, ro);
    }

    public static final Signal GetSignal(final String path, final TimeInterval ti, final ReadOptions ro) throws IOException {
        return signalaccess.getAccess(path).getSignal(path, ti, ro);
    }

    public static final String[] GetString(final Signal signal) {
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
        System.out.println(signalaccess.help);
        return signalaccess.help;
    }

    public static final boolean isConnected() {
        return signalaccess.getAccess(signalaccess.databaselist.get(0)) != null;
    }

    public static void main(final String[] args) {
        final String path = "/ArchiveDB/codac/W7X/CoDaStationDesc.111/DataModuleDesc.250_DATASTREAM/0";// "/ArchiveDB/codac/W7X/CoDaStationDesc.14823/DataModuleDesc.14833_DATASTREAM/0/image/scaled";
        final String XP = "XP:20160310.7";// "XP:20160210.5";
        final TimeInterval TI = signalaccess.getTimeInterval(XP);
        try{
            final ReadOptions ro = ReadOptions.fetchAll();
            final SignalReader R = signalaccess.GetReader(path);
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

    public static final signalaccess NewInstance(final String DataBase) {
        try{
            return new signalaccess(DataBase);
        }catch(final Exception e){}
        return null;
    }
    public final String                dbName;
    private final SignalAddressBuilder sab;
    private final SignalToolsFactory   stf;

    public signalaccess(final String dbName){
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

    public final List<SignalAddress> getList(final String path) {
        return this.getList(path, TimeInterval.ALL);
    }

    @SuppressWarnings("unchecked")
    public final List<SignalAddress> getList(final String path, final TimeInterval ti) {
        final SignalsTreeLister stl = this.stf.makeSignalsTreeLister();
        try{
            return (List<SignalAddress>)stl.listFor(ti, path);
        }finally{
            stl.close();
        }
    }

    private final SignalReader getReader(final String path) {
        return this.stf.makeSignalReader(this.getAddress(path));
    }

    private final Signal getSignal(final String path, final TimeInterval ti, final ReadOptions ro) throws IOException {
        final SignalReader sr = this.getReader(path);
        try{
            return sr.readSignal(ti, ro);
        }finally{
            sr.close();
        }
    }
}

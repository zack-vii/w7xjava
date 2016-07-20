package misc;

/* $Id$ */
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import jet.JetDataProvider;
import jet.JetMdsDataProvider;
import jscope.ConnectionListener;
import jscope.DataProvider;
import jscope.DataServerItem;
import jscope.FrameData;
import jscope.UpdateEventListener;
import jscope.WaveData;
import mds.MdsDataProvider;
import twu.TwuDataProvider;

public final class UniversalDataProvider implements DataProvider{
    public static boolean DataPending() {
        return false;
    }

    public static byte[] getAllFrames(final String in_frame) {
        return null;
    }

    public static byte[] getFrameAt(final String in_expr, final int frame_idx) {
        return null;
    }

    public static float[] getFrameTimes(final String in_expr) {
        return null;
    }

    public static WaveData getResampledWaveData(final String in, final double start, final double end, final int n_points) {
        return null;
    }

    public static WaveData getResampledWaveData(final String in_y, final String in_x, final double start, final double end, final int n_points) {
        return null;
    }

    protected static String RemoveExp(final String spec) {
        if(spec.startsWith("jetmds:")) return spec.substring(7);
        if(spec.startsWith("ts:")) return spec.substring(3);
        return spec.substring(4);
    }

    public static boolean SupportsCompression() {
        return false;
    }

    public static boolean SupportsContinuous() {
        return true;
    }

    public static boolean SupportsFastNetwork() {
        return false;
    }
    AsdexDataProvider  asd;
    String             error = "Unknown experiment";
    FtuDataProvider    ftu;
    JetDataProvider    jet;
    JetMdsDataProvider jetmds;
    MdsDataProvider    rfx;
    TsDataProvider     ts;
    TwuDataProvider    twu;

    public UniversalDataProvider() throws IOException{
        this.rfx = new MdsDataProvider();
        try{
            this.rfx.setArgument("150.178.3.80");
        }catch(final Exception exc){
            this.rfx = null;
        }
        this.ftu = new FtuDataProvider();
        try{
            this.ftu.setArgument("192.107.51.84:8100");
        }catch(final Exception exc){
            this.ftu = null;
        }
        this.twu = new TwuDataProvider();
        this.jet = new JetDataProvider();
        this.jetmds = new JetMdsDataProvider();
        this.ts = new TsDataProvider();
        try{
            this.ts.setArgument("132.169.8.164:8000");
        }catch(final Exception exc){
            this.ts = null;
        }
        this.asd = new AsdexDataProvider();
        try{
            this.asd.setArgument("localhost:8000");
        }catch(final Exception exc){
            this.asd = null;
        }
    }

    @Override
    public void abort() {
        if(this.twu != null) this.twu.abort();
        if(this.rfx != null) this.rfx.abort();
        if(this.ftu != null) this.ftu.abort();
        if(this.jet != null) this.jet.abort();
        if(this.jetmds != null) this.jetmds.abort();
        if(this.ts != null) this.ts.abort();
        if(this.asd != null) this.asd.abort();
    }

    @Override
    public void addConnectionListener(final ConnectionListener l) {
        if(this.twu != null) this.twu.addConnectionListener(l);
        if(this.rfx != null) this.rfx.addConnectionListener(l);
        if(this.ftu != null) this.ftu.addConnectionListener(l);
        if(this.jet != null) this.jet.addConnectionListener(l);
        if(this.jetmds != null) this.jetmds.addConnectionListener(l);
        if(this.ts != null) this.ts.addConnectionListener(l);
        if(this.asd != null) this.asd.addConnectionListener(l);
    }

    @Override
    public void addUpdateEventListener(final UpdateEventListener l, final String event) throws IOException {
        if(this.twu != null) this.twu.addUpdateEventListener(l, event);
        if(this.rfx != null) this.rfx.addUpdateEventListener(l, event);
        if(this.ftu != null) this.ftu.addUpdateEventListener(l, event);
        if(this.jet != null) this.jet.addUpdateEventListener(l, event);
        if(this.jetmds != null) this.jetmds.addUpdateEventListener(l, event);
        if(this.ts != null) this.ts.addUpdateEventListener(l, event);
        if(this.asd != null) this.asd.addUpdateEventListener(l, event);
    }

    @Override
    public final boolean checkProvider() {
        boolean ok = true;
        if(this.twu != null) ok &= this.twu.checkProvider();
        if(this.rfx != null) this.rfx.checkProvider();
        if(this.ftu != null) this.ftu.checkProvider();
        if(this.jet != null) this.jet.checkProvider();
        if(this.jetmds != null) this.jetmds.checkProvider();
        if(this.ts != null) this.ts.checkProvider();
        if(this.asd != null) this.asd.checkProvider();
        return ok;
    }

    @Override
    public void dispose() {
        if(this.rfx != null) this.rfx.dispose();
        if(this.ftu != null) this.ftu.dispose();
        if(this.twu != null) this.twu.dispose();
        if(this.jet != null) this.jet.dispose();
        if(this.jetmds != null) this.jetmds.dispose();
        if(this.ts != null) this.ts.dispose();
        if(this.asd != null) this.asd.dispose();
    }

    public void enableAsyncUpdate(final boolean enable) {}

    @Override
    public String errorString() {
        return this.error;
    }

    @Override
    public Class getDefaultBrowser() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getFloat(final String in) {
        this.error = null;
        return Float.parseFloat(in);
    }

    @Override
    public FrameData getFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        return null;
    }

    @Override
    public final String getLegendString(final String s) {
        return s;
    }

    @Override
    public long[] getShots(final String in) {
        final long d[] = new long[1];
        try{
            return this.rfx.getShots(in);
        }catch(final Exception exc){
            try{
                final StringTokenizer st = new StringTokenizer(in, ":");
                final String shotStr = st.nextToken();
                d[0] = Long.parseLong(shotStr);
            }catch(final Exception exc1){
                d[0] = 0;
            }
        }
        return d;
    }

    @Override
    public String getString(final String in) {
        this.error = null;
        return new String(in);
    }

    @Override
    public WaveData getWaveData(final String in) {
        try{
            return this.selectProvider(in).getWaveData(UniversalDataProvider.RemoveExp(in));
        }catch(final Exception exc){
            return null;
        }
    }

    @Override
    public WaveData getWaveData(final String in_y, final String in_x) {
        try{
            return this.selectProvider(in_y).getWaveData(UniversalDataProvider.RemoveExp(in_y), in_x);
        }catch(final Exception exc){
            return null;
        }
    }

    @Override
    public int inquireCredentials(final JFrame f, final DataServerItem server_item) {
        if(this.rfx != null) this.rfx.inquireCredentials(f, new DataServerItem("java_user_ext"));
        return this.jet.inquireCredentials(f, server_item);
    }

    @Override
    public void join() {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeConnectionListener(final ConnectionListener l) {
        if(this.twu != null) this.twu.removeConnectionListener(l);
        if(this.rfx != null) this.rfx.removeConnectionListener(l);
        if(this.ftu != null) this.ftu.removeConnectionListener(l);
        if(this.jet != null) this.jet.removeConnectionListener(l);
        if(this.jetmds != null) this.jetmds.removeConnectionListener(l);
        if(this.ts != null) this.ts.removeConnectionListener(l);
        if(this.asd != null) this.asd.removeConnectionListener(l);
    }

    @Override
    public void removeUpdateEventListener(final UpdateEventListener l, final String event) throws IOException {
        if(this.twu != null) this.twu.removeUpdateEventListener(l, event);
        if(this.rfx != null) this.rfx.removeUpdateEventListener(l, event);
        if(this.ftu != null) this.ftu.removeUpdateEventListener(l, event);
        if(this.jet != null) this.jet.removeUpdateEventListener(l, event);
        if(this.jetmds != null) this.jetmds.removeUpdateEventListener(l, event);
        if(this.ts != null) this.ts.removeUpdateEventListener(l, event);
        if(this.asd != null) this.asd.removeUpdateEventListener(l, event);
    }

    private DataProvider selectProvider(final String spec) {
        if(spec.startsWith("rfx:")) return this.rfx;
        if(spec.startsWith("ftu:")) return this.ftu;
        if(spec.startsWith("twu:")) return this.twu;
        if(spec.startsWith("jet:")) return this.jet;
        if(spec.startsWith("jetmds:")) return this.jetmds;
        if(spec.startsWith("ts:")) return this.ts;
        if(spec.startsWith("asd:")) return this.asd;
        this.error = "Unknown experiment";
        return null;
    }

    @Override
    public void setArgument(final String arg) {}

    public void setCompression(final boolean state) {}

    public void setContinuousUpdate() {}

    @Override
    public void setEnvironment(final String exp) {
        this.error = null;
    }

    @Override
    public boolean supportsTunneling() {
        return false;
    }

    @Override
    public void update(final String exp, final long s) {
        if(exp == null) return;
        if(exp.equals("rfx") && this.rfx != null) this.rfx.update(exp, s);
        else if(exp.equals("ftu") && this.ftu != null) this.ftu.update(exp, s);
        else if(exp.equals("twu") && this.twu != null) this.twu.update(exp, s);
        else if(exp.equals("jet") && this.jet != null) this.jet.update(null, s);
        else if(exp.equals("jetmds") && this.jetmds != null) this.jetmds.update(null, s);
        else if(exp.equals("ts") && this.ts != null) this.ts.update(null, s);
        else if(exp.equals("asd") && this.asd != null) this.asd.update(null, s);
        this.error = null;
    }
}

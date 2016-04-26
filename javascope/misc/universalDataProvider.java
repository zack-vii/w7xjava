package misc;

/* $Id$ */
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import jScope.ConnectionListener;
import jScope.DataProvider;
import jScope.DataServerItem;
import jScope.FrameData;
import jScope.UpdateEventListener;
import jScope.WaveData;
import jet.jetDataProvider;
import jet.jetMdsDataProvider;
import mds.mdsDataProvider;
import twu.twuDataProvider;

public final class universalDataProvider implements DataProvider{
    public static boolean DataPending() {
        return false;
    }

    public static byte[] GetAllFrames(final String in_frame) {
        return null;
    }

    public static byte[] GetFrameAt(final String in_expr, final int frame_idx) {
        return null;
    }

    public static float[] GetFrameTimes(final String in_expr) {
        return null;
    }

    public static WaveData GetResampledWaveData(final String in, final double start, final double end, final int n_points) {
        return null;
    }

    public static WaveData GetResampledWaveData(final String in_y, final String in_x, final double start, final double end, final int n_points) {
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
    asdexDataProvider  asd;
    String             error = "Unknown experiment";
    ftuDataProvider    ftu;
    jetDataProvider    jet;
    jetMdsDataProvider jetmds;
    mdsDataProvider    rfx;
    tsDataProvider     ts;
    twuDataProvider    twu;

    public universalDataProvider() throws IOException{
        this.rfx = new mdsDataProvider();
        try{
            this.rfx.SetArgument("150.178.3.80");
        }catch(final Exception exc){
            this.rfx = null;
        }
        this.ftu = new ftuDataProvider();
        try{
            this.ftu.SetArgument("192.107.51.84:8100");
        }catch(final Exception exc){
            this.ftu = null;
        }
        this.twu = new twuDataProvider();
        this.jet = new jetDataProvider();
        this.jetmds = new jetMdsDataProvider();
        this.ts = new tsDataProvider();
        try{
            this.ts.SetArgument("132.169.8.164:8000");
        }catch(final Exception exc){
            this.ts = null;
        }
        this.asd = new asdexDataProvider();
        try{
            this.asd.SetArgument("localhost:8000");
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
    public void AddConnectionListener(final ConnectionListener l) {
        if(this.twu != null) this.twu.AddConnectionListener(l);
        if(this.rfx != null) this.rfx.AddConnectionListener(l);
        if(this.ftu != null) this.ftu.AddConnectionListener(l);
        if(this.jet != null) this.jet.AddConnectionListener(l);
        if(this.jetmds != null) this.jetmds.AddConnectionListener(l);
        if(this.ts != null) this.ts.AddConnectionListener(l);
        if(this.asd != null) this.asd.AddConnectionListener(l);
    }

    @Override
    public void AddUpdateEventListener(final UpdateEventListener l, final String event) throws IOException {
        if(this.twu != null) this.twu.AddUpdateEventListener(l, event);
        if(this.rfx != null) this.rfx.AddUpdateEventListener(l, event);
        if(this.ftu != null) this.ftu.AddUpdateEventListener(l, event);
        if(this.jet != null) this.jet.AddUpdateEventListener(l, event);
        if(this.jetmds != null) this.jetmds.AddUpdateEventListener(l, event);
        if(this.ts != null) this.ts.AddUpdateEventListener(l, event);
        if(this.asd != null) this.asd.AddUpdateEventListener(l, event);
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
    public void Dispose() {
        if(this.rfx != null) this.rfx.Dispose();
        if(this.ftu != null) this.ftu.Dispose();
        if(this.twu != null) this.twu.Dispose();
        if(this.jet != null) this.jet.Dispose();
        if(this.jetmds != null) this.jetmds.Dispose();
        if(this.ts != null) this.ts.Dispose();
        if(this.asd != null) this.asd.Dispose();
    }

    public void enableAsyncUpdate(final boolean enable) {}

    @Override
    public String ErrorString() {
        return this.error;
    }

    @Override
    public Class getDefaultBrowser() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float GetFloat(final String in) {
        this.error = null;
        return Float.parseFloat(in);
    }

    @Override
    public FrameData GetFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        return null;
    }

    @Override
    public final String GetLegendString(final String s) {
        return s;
    }

    @Override
    public long[] GetShots(final String in) {
        final long d[] = new long[1];
        try{
            return this.rfx.GetShots(in);
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
    public String GetString(final String in) {
        this.error = null;
        return new String(in);
    }

    @Override
    public WaveData GetWaveData(final String in) {
        try{
            return this.SelectProvider(in).GetWaveData(universalDataProvider.RemoveExp(in));
        }catch(final Exception exc){
            return null;
        }
    }

    @Override
    public WaveData GetWaveData(final String in_y, final String in_x) {
        try{
            return this.SelectProvider(in_y).GetWaveData(universalDataProvider.RemoveExp(in_y), in_x);
        }catch(final Exception exc){
            return null;
        }
    }

    @Override
    public int InquireCredentials(final JFrame f, final DataServerItem server_item) {
        if(this.rfx != null) this.rfx.InquireCredentials(f, new DataServerItem("java_user_ext"));
        return this.jet.InquireCredentials(f, server_item);
    }

    @Override
    public void join() {
        // TODO Auto-generated method stub
    }

    @Override
    public void RemoveConnectionListener(final ConnectionListener l) {
        if(this.twu != null) this.twu.RemoveConnectionListener(l);
        if(this.rfx != null) this.rfx.RemoveConnectionListener(l);
        if(this.ftu != null) this.ftu.RemoveConnectionListener(l);
        if(this.jet != null) this.jet.RemoveConnectionListener(l);
        if(this.jetmds != null) this.jetmds.RemoveConnectionListener(l);
        if(this.ts != null) this.ts.RemoveConnectionListener(l);
        if(this.asd != null) this.asd.RemoveConnectionListener(l);
    }

    @Override
    public void RemoveUpdateEventListener(final UpdateEventListener l, final String event) throws IOException {
        if(this.twu != null) this.twu.RemoveUpdateEventListener(l, event);
        if(this.rfx != null) this.rfx.RemoveUpdateEventListener(l, event);
        if(this.ftu != null) this.ftu.RemoveUpdateEventListener(l, event);
        if(this.jet != null) this.jet.RemoveUpdateEventListener(l, event);
        if(this.jetmds != null) this.jetmds.RemoveUpdateEventListener(l, event);
        if(this.ts != null) this.ts.RemoveUpdateEventListener(l, event);
        if(this.asd != null) this.asd.RemoveUpdateEventListener(l, event);
    }

    protected DataProvider SelectProvider(final String spec) {
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
    public void SetArgument(final String arg) {}

    public void SetCompression(final boolean state) {}

    public void setContinuousUpdate() {}

    @Override
    public void SetEnvironment(final String exp) {
        this.error = null;
    }

    @Override
    public boolean SupportsTunneling() {
        return false;
    }

    @Override
    public void Update(final String exp, final long s) {
        if(exp == null) return;
        if(exp.equals("rfx") && this.rfx != null) this.rfx.Update(exp, s);
        else if(exp.equals("ftu") && this.ftu != null) this.ftu.Update(exp, s);
        else if(exp.equals("twu") && this.twu != null) this.twu.Update(exp, s);
        else if(exp.equals("jet") && this.jet != null) this.jet.Update(null, s);
        else if(exp.equals("jetmds") && this.jetmds != null) this.jetmds.Update(null, s);
        else if(exp.equals("ts") && this.ts != null) this.ts.Update(null, s);
        else if(exp.equals("asd") && this.asd != null) this.asd.Update(null, s);
        this.error = null;
    }
}

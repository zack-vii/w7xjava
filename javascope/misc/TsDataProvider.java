package misc;

/* $Id$ */
import java.io.IOException;
import javax.swing.JFrame;
import jscope.DataProvider;
import jscope.DataServerItem;
import mds.MdsDataProvider;
import mds.MdsParser;

public final class TsDataProvider extends MdsDataProvider{
    public static boolean SupportsCompression() {
        return false;
    }

    public static boolean SupportsContinuous() {
        return false;
    }

    public static boolean SupportsFastNetwork() {
        return false;
    }

    public TsDataProvider(){
        super();
    }

    public TsDataProvider(final String provider) throws IOException{
        super(provider);
    }

    @SuppressWarnings("static-method")
    public boolean DataPending() {
        return false;
    }

    protected String getDefaultXLabel(final String in_y) throws IOException {
        this.error = null;
        return this.getString("GetTSUnit(0)");
    }

    protected String getDefaultYLabel() throws IOException {
        this.error = null;
        return this.getString("GetTSUnit(1)");
    }

    @Override
    public synchronized float[] getFloatArray(final String in) throws IOException {
        final String parsed = this.ParseExpression(in);
        if(parsed == null) return null;
        this.error = null;
        final float[] out_array = super.getFloatArray(parsed);
        if(out_array == null && this.error == null) this.error = "Cannot evaluate " + in + " for shot " + this.shot;
        if(out_array != null && out_array.length <= 1){
            this.error = "Cannot evaluate " + in + " for shot " + this.shot;
            return null;
        }
        return out_array;
    }

    @Override
    public synchronized int[] getIntArray(final String in) throws IOException {
        final String parsed = this.ParseExpression(in);
        if(parsed == null) return null;
        return super.getIntArray(parsed);
    }

    @Override
    public int[] getNumDimensions(final String spec) {
        return new int[]{1};
    }

    @Override
    public int inquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    protected String ParseExpression(final String in) {
        // if(in.startsWith("DIM_OF("))
        // return in;
        final String res = MdsParser.parseFun(in, "GetTsBase(" + this.shot + ", \"", "\")");
        /*
         * StringTokenizer st = new StringTokenizer(in, ":"); String res = "GetTSData(\""; try{ String name = st.nextToken();
         */
        /*
         * String rang0 = st.nextToken(); String rang1 = st.nextToken(); res = "GetTSData(\"" + name + "\", " + shot + ", " + rang0 + ", " + rang1 + ")";
         */
        // res = "GetTsBase(" + shot + ", \"" + name + "\")";
        /*
         * }catch(Exception e) { error = "Wrong signal format: must be <signal_name>:<rangs[0]>:<rangs[1]>"; return null; }
         */
        // System.out.println(res);
        return res;
    }

    @Override
    public void setArgument(final String arg) throws IOException {
        this.mds.setProvider(arg);
        this.mds.setUser("mdsplus");
    }

    @Override
    public synchronized void update(final String exp, final long s) {
        this.error = null;
        this.shot = (int)s;
    }
}

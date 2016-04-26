package misc;

/* $Id$ */
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import jScope.DataProvider;
import jScope.DataServerItem;
import mds.mdsDataProvider;

public final class ftuDataProvider extends mdsDataProvider{
    public static final boolean DataPending() {
        return false;
    }

    protected static String GetDefaultYLabel() throws IOException {
        return null;
    }

    private static String GetFirstSignal(final String in_y) {
        if(in_y == null) return null;
        String curr_str;
        final StringTokenizer st = new StringTokenizer(in_y, "\\", true);
        while(st.hasMoreTokens()){
            curr_str = st.nextToken();
            if(curr_str.equals("\\") && st.hasMoreTokens()) return st.nextToken();
        }
        return null;
    }

    public static boolean SupportsCompression() {
        return false;
    }

    public static boolean SupportsContinuous() {
        return false;
    }

    public static boolean SupportsFastNetwork() {
        return false;
    }

    public ftuDataProvider(){
        super();
    }

    public ftuDataProvider(final String provider) throws IOException{
        super(provider);
        this.SetEnvironment("public _IMODE = 0;");
    }

    protected String GetDefaultTitle(final String in_y) throws IOException {
        this.error = null;
        String first_sig = ftuDataProvider.GetFirstSignal(in_y);
        if(first_sig != null && first_sig.startsWith("$")) first_sig = "_" + first_sig.substring(1);
        if(first_sig == null) return null;
        final String parsed = "ftuyl(" + this.shot + ",\"" + first_sig + "\")";
        // System.out.println(parsed);
        return this.GetString(parsed);
    }

    protected String GetDefaultXLabel(final String in_y) throws IOException {
        this.error = null;
        String first_sig = ftuDataProvider.GetFirstSignal(in_y);
        if(first_sig == null) return null;
        if(first_sig != null && first_sig.startsWith("$")) first_sig = "_" + first_sig.substring(1);
        return this.GetString("ftuxl(" + this.shot + ",\"" + first_sig + "\")");
    }

    @Override
    public synchronized float[] GetFloatArray(final String in) throws IOException {
        this.error = null;
        final float[] out_array = super.GetFloatArray(this.ParseExpression(in));
        if(out_array == null && this.error == null) this.error = "Cannot evaluate " + in + " for shot " + this.shot;
        if(out_array != null && out_array.length <= 1){
            this.error = "Cannot evaluate " + in + " for shot " + this.shot;
            return null;
        }
        return out_array;
    }

    @Override
    public synchronized int[] GetIntArray(final String in) throws IOException {
        return super.GetIntArray(this.ParseExpression(in));
    }

    @Override
    public int InquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    protected String ParseExpression(final String in) {
        final StringTokenizer st = new StringTokenizer(in, "\\", true);
        final StringBuffer parsed = new StringBuffer();
        int state = 0;
        try{
            while(st.hasMoreTokens()){
                final String curr_str = st.nextToken();
                // System.out.println("Token: "+curr_str);
                switch(state){
                    case 0:
                        if(curr_str.equals("\\")) state = 1;
                        else parsed.append(curr_str);
                        break;
                    case 1:
                        if(curr_str.equals("\\")){
                            parsed.append("\\");
                            state = 0;
                        }else{
                            if(curr_str.startsWith("$")) parsed.append("ftu(" + this.shot + ",\"_" + curr_str.substring(1));
                            else parsed.append("ftu(" + this.shot + ",\"" + curr_str);
                            state = 2;
                        }
                        break;
                    case 2:
                        if(!st.hasMoreTokens()) parsed.append("\", _IMODE)");
                        state = 3;
                        break;
                    case 3:
                        if(!curr_str.equals("\\") || !st.hasMoreTokens()){
                            parsed.append("\", _IMODE) " + curr_str);
                            state = 0;
                        }else{
                            parsed.append("\\");
                            state = 4;
                        }
                        break;
                    case 4:
                        if(curr_str.equals("\\")){
                            parsed.append("\\");
                            state = 4;
                        }else{
                            parsed.append(curr_str);
                            state = 2;
                        }
                        break;
                }
            }
        }catch(final Exception e){
            System.out.println(e);
        }
        // System.out.println("parsed: "+ parsed);
        return parsed.toString();
    }

    @Override
    public void SetArgument(final String arg) throws IOException {
        this.mds.setProvider(arg);
        this.SetEnvironment("public _IMODE = 0;");
    }

    @Override
    public void SetCompression(final boolean state) {}

    @Override
    public synchronized void Update(final String exp, final long s) {
        this.error = null;
        this.shot = s;
    }
}

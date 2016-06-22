package misc;

/* $Id$ */
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import jscope.DataProvider;
import jscope.DataServerItem;
import mds.MdsDataProvider;

public final class FtuDataProvider extends MdsDataProvider{
    public static final boolean DataPending() {
        return false;
    }

    protected static String getDefaultYLabel() throws IOException {
        return null;
    }

    private static String getFirstSignal(final String in_y) {
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

    public FtuDataProvider(){
        super();
    }

    public FtuDataProvider(final String provider) throws IOException{
        super(provider);
        this.setEnvironment("public _IMODE = 0;");
    }

    protected String getDefaultTitle(final String in_y) throws IOException {
        this.error = null;
        String first_sig = FtuDataProvider.getFirstSignal(in_y);
        if(first_sig != null && first_sig.startsWith("$")) first_sig = "_" + first_sig.substring(1);
        if(first_sig == null) return null;
        final String parsed = "ftuyl(" + this.shot + ",\"" + first_sig + "\")";
        // System.out.println(parsed);
        return this.getString(parsed);
    }

    protected String getDefaultXLabel(final String in_y) throws IOException {
        this.error = null;
        String first_sig = FtuDataProvider.getFirstSignal(in_y);
        if(first_sig == null) return null;
        if(first_sig != null && first_sig.startsWith("$")) first_sig = "_" + first_sig.substring(1);
        return this.getString("ftuxl(" + this.shot + ",\"" + first_sig + "\")");
    }

    @Override
    public synchronized float[] getFloatArray(final String in) throws IOException {
        this.error = null;
        final float[] out_array = super.getFloatArray(this.parseExpression(in));
        if(out_array == null && this.error == null) this.error = "Cannot evaluate " + in + " for shot " + this.shot;
        if(out_array != null && out_array.length <= 1){
            this.error = "Cannot evaluate " + in + " for shot " + this.shot;
            return null;
        }
        return out_array;
    }

    @Override
    public synchronized int[] getIntArray(final String in) throws IOException {
        return super.getIntArray(this.parseExpression(in));
    }

    @Override
    public int inquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    protected String parseExpression(final String in) {
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
    public void setArgument(final String arg) throws IOException {
        this.mds.setProvider(arg);
        this.setEnvironment("public _IMODE = 0;");
    }

    @Override
    public synchronized void update(final String exp, final long s) {
        this.error = null;
        this.shot = s;
    }
}

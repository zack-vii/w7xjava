package jet;

/* $Id$ */
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import jScope.Array.RealArray;
import jScope.DataProvider;
import jScope.DataServerItem;
import mds.mdsDataProvider;

public final class jetMdsDataProvider extends mdsDataProvider{
    public static boolean DataPending() {
        return false;
    }

    public static boolean SupportsCompression() {
        return false;
    };

    public static boolean SupportsContinuous() {
        return false;
    }

    public static boolean SupportsFastNetwork() {
        return true;
    }

    public jetMdsDataProvider(){
        super("mdsplus.jet.efda.org");
    }

    @Override
    public synchronized int[] GetIntArray(final String in) throws IOException {
        return super.GetIntArray(this.ParseExpression(in));
    }

    /*
        public synchronized float[] GetFloatArray(String in) throws IOException
        {
        //System.out.println("parsed: "+ parsed);
        float [] out_array = super.GetFloatArray(ParseExpression(in));
        if(out_array == null && error == null)
            error = "Cannot evaluate " + in + " for shot " + shot;
    
        //if(out_array != null && out_array.length <= 1)
        //{
        //    error = "Cannot evaluate " + in + " for shot " + shot;
        //    return null;
        //}
    
        return out_array;
        }
     */
    @Override
    public synchronized RealArray GetRealArray(final String in) throws IOException {
        return super.GetRealArray(this.ParseExpression(in));
    }

    @Override
    public int InquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    // Syntax: ppf/<signal> or jpf/<signal>
    // Ex: ppf/magn/ipla
    protected String ParseExpression(final String in) {
        this.error = null;
        final StringTokenizer st = new StringTokenizer(in, " /(){}[]*+,:;", true);
        String parsed = "", signal = "";
        int state = 0;
        try{
            while(st.hasMoreTokens()){
                final String curr_str = st.nextToken();
                // System.out.println("Token: "+curr_str);
                switch(state){
                    case 0:
                        if(curr_str.equals("/")){
                            if(parsed.endsWith("PPF") || parsed.endsWith("ppf") || parsed.endsWith("JPF") || parsed.endsWith("jpf")){
                                signal = parsed.substring(parsed.length() - 3) + "/";
                                parsed = parsed.substring(0, parsed.length() - 3);
                                state = 1;
                            }else parsed += curr_str;
                        }else parsed += curr_str;
                        break;
                    case 1:
                        signal += curr_str;
                        state = 2;
                        break;
                    case 2:
                        signal += curr_str;
                        state = 3;
                        break;
                    case 3:
                        parsed += ("(jet(\"" + signal + curr_str + "\", " + this.shot + ")) ");
                        signal = "";
                        state = 0;
                        break;
                }
            }
        }catch(final Exception e){
            System.out.println(e);
        }
        return parsed;
    }

    @Override
    public void SetArgument(final String arg) {}

    @Override
    public void SetCompression(final boolean state) {}

    @Override
    public synchronized void Update(final String exp, final long s) {
        this.error = null;
        this.shot = s;
    }
}

package twu;

/* $Id$ */
import java.io.IOException;
import java.util.StringTokenizer;
import jScope.DataAccess;
import jScope.DataProvider;
import jScope.FrameData;
import jScope.Signal;

final public class twuAccess implements DataAccess{
    public static void main(final String args[]) {
        final twuAccess access = new twuAccess();
        final String url = "twu://ipptwu.ipp.kfa-juelich.de/textor/all/86858/RT2/IVD/IBT2P-star";
        try{
            final float y[] = access.getY(url);
            final float x[] = access.getX(url);
            for(int i = 0; i < x.length; i++)
                System.out.println(x[i] + "  " + y[i]);
            System.out.println("Num. points: " + y.length);
        }catch(final IOException exc){}
    }
    String          experiment = null;
    String          ip_addr    = null;
    String          shot_str   = null;
    String          signal     = null;
    twuDataProvider tw         = null;

    @Override
    public void close() {}

    @Override
    public DataProvider getDataProvider() {
        return this.tw;
    }

    @Override
    public String getError() {
        if(this.tw == null) return("Cannot create TwuDataProvider");
        return this.tw.ErrorString();
    }

    @Override
    public String getExperiment() {
        return this.experiment;
    }

    @Override
    public String getExpression(final String paramString) {
        return paramString;
    }

    @Override
    public FrameData getFrameData(final String url) {
        return null;
    }

    @Override
    public String getShot() {
        return this.shot_str;
    }

    @Override
    public Signal getSignal(final String url) throws IOException {
        this.setProvider(url);
        if(this.signal == null) return null;
        Signal s = null;
        final float y[] = this.tw.GetFloatArray(this.signal, false);
        final float x[] = this.tw.GetFloatArray(this.signal, true);
        if(x == null || y == null) return null;
        s = new Signal(x, y);
        s.setName(this.tw.GetSignalProperty("SignalName", this.signal));
        // System.out.println(tw.getSignalProperty("SignalName", signal));
        return s;
    }

    @Override
    public String getSignalName() {
        return this.signal;
    }

    @Override
    public float[] getX(final String url) throws IOException {
        this.setProvider(url);
        if(this.signal == null) return null;
        return this.tw.GetFloatArray(this.signal, true);
    }

    @Override
    public float[] getY(final String url) throws IOException {
        this.setProvider(url);
        if(this.signal == null) return null;
        return this.tw.GetFloatArray(this.signal, false);
    }

    @Override
    public void setPassword(final String encoded_credentials) {}

    @Override
    public void setProvider(final String url) throws IOException {
        this.signal = "http" + url.substring(url.indexOf(":"));
        final StringTokenizer st = new StringTokenizer(url, "/");
        st.nextToken(); // skip
        st.nextToken(); // skip
        st.nextToken(); // skip
        st.nextToken(); // skip
        this.shot_str = st.nextToken();
        if(this.tw == null){
            this.tw = new twuDataProvider("jScope applet (Version 7.2.2)");
        }
    }

    @Override
    public boolean supports(final String url) {
        final StringTokenizer st = new StringTokenizer(url, ":");
        if(st.countTokens() < 2) return false;
        return st.nextToken().equals("twu");
    }
}

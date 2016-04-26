package mds;

/* $Id$ */
import java.io.IOException;
import java.util.StringTokenizer;
import jScope.DataAccess;
import jScope.DataProvider;
import jScope.FrameData;
import jScope.Signal;

public class mdsAccess implements DataAccess{
    String          encoded_credentials = null;
    String          error               = null;
    String          experiment          = null;
    String          ip_addr             = null;
    mdsDataProvider np                  = null;
    String          prevUrl             = null;
    String          shot_str            = null;
    String          signal              = null;

    @Override
    public void close() {
        if(this.np != null) this.np.Dispose();
        this.np = null;
        this.ip_addr = null;
    }

    @Override
    public DataProvider getDataProvider() {
        return this.np;
    }

    @Override
    public String getError() {
        if(this.np == null) return("Cannot create mdsDataProvider");
        if(this.error != null) return this.error;
        return this.np.ErrorString();
    }

    @Override
    public String getExperiment() {
        return this.experiment;
    }

    public String getExpression(final String paramString) throws IOException {
        System.out.println("Expr URL = " + paramString);
        this.setProvider(paramString);
        if(this.signal == null) return null;
        return this.np.GetStringValue(this.signal);
    }

    @Override
    public FrameData getFrameData(final String url) throws IOException {
        this.setProvider(url);
        return this.np.GetFrameData(this.signal, null, (float)-1E8, (float)1E8);
    }

    @Override
    public String getShot() {
        return this.shot_str;
    }

    @Override
    public Signal getSignal(final String url) throws IOException {
        Signal s = null;
        this.error = null;
        final float y[] = this.getY(url);
        final float x[] = this.getX(url);
        System.out.println("URL = " + url);
        if(x == null || y == null){
            this.error = this.np.ErrorString();
            return null;
        }
        s = new Signal(x, y);
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
        return this.np.GetFloatArray("DIM_OF(" + this.signal + ")");
    }

    @Override
    public float[] getY(final String url) throws IOException {
        this.setProvider(url);
        if(this.signal == null) return null;
        return this.np.GetFloatArray(this.signal);
    }

    @Override
    public void setPassword(final String encoded_credentials) {
        this.encoded_credentials = encoded_credentials;
    }

    @Override
    public void setProvider(final String url) throws IOException {
        if((url != null) && (this.prevUrl != null) && (this.prevUrl.equals(url))){ return; }
        final StringTokenizer st = new StringTokenizer(url, ":");
        String urlPath = st.nextToken();
        urlPath = st.nextToken("");
        urlPath = urlPath.substring(2);
        final StringTokenizer st1 = new StringTokenizer(urlPath, "/");
        if(st1.countTokens() < 4){ return; }
        final String ipAddress = st1.nextToken();
        if(ipAddress == null) return;
        if((this.ip_addr == null) || (!this.ip_addr.equals(ipAddress))){
            this.np = new mdsDataProvider(ipAddress);
            this.ip_addr = ipAddress;
        }
        String region = null;
        this.experiment = st1.nextToken();
        final StringTokenizer localStringTokenizer3 = new StringTokenizer(this.experiment, "~");
        if(localStringTokenizer3.countTokens() == 2){
            this.experiment = localStringTokenizer3.nextToken();
            region = localStringTokenizer3.nextToken();
        }
        if((this.experiment != null) && (!this.experiment.equals(""))){
            this.shot_str = st1.nextToken();
            final int shot = new Integer(this.shot_str).intValue();
            if(region != null){
                final int out[] = this.np.GetIntArray("treeSetSource('" + this.experiment + "','" + region + "')");
                if(out == null) return;
            }
            this.np.Update(this.experiment, shot, true);
        }
        this.signal = st1.nextToken();
        this.prevUrl = url;
    }

    public void setProviderOld(final String url) throws IOException {
        final StringTokenizer st1 = new StringTokenizer(url, ":");
        String content = st1.nextToken();
        content = st1.nextToken("");
        content = content.substring(2);
        final StringTokenizer st2 = new StringTokenizer(content, "/");
        if(st2.countTokens() < 4) // ip addr/exp/shot/signal
        return;
        final String addr = st2.nextToken();
        if(addr == null) return;
        if(this.ip_addr == null || !this.ip_addr.equals(addr)){
            this.np = new mdsDataProvider(addr);
            /*
            if(encoded_credentials == null ||( ip_addr != null && !ip_addr.equals(addr)))
            {
                encoded_credentials = new String();
                np.InquireCredentials(null, encoded_credentials);
            }
             */
            this.ip_addr = addr;
        }
        this.experiment = st2.nextToken();
        if(this.experiment != null && !this.experiment.equals("")){
            // String shot_str = st2.nextToken();
            this.shot_str = st2.nextToken();
            final int shot = (new Integer(this.shot_str)).intValue();
            this.np.Update(this.experiment, shot);
        }
        this.signal = st2.nextToken();
    }

    @Override
    public boolean supports(final String url) {
        final StringTokenizer st = new StringTokenizer(url, ":");
        if(st.countTokens() < 2) return false;
        return st.nextToken().equals("mds");
    }
}

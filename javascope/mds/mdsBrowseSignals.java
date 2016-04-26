package mds;

/* $Id$ */
import java.util.Properties;
import java.util.StringTokenizer;
import jScope.jScopeBrowseSignals;

public class mdsBrowseSignals extends jScopeBrowseSignals{
    public static final long serialVersionUID = 8643838486846L;
    private String           server_url;
    private String           shot;
    private String           tree;

    @Override
    protected String getServerAddr() {
        return this.server_url;
    }

    @Override
    protected String getShot() {
        return this.shot;
    }

    @Override
    protected String getSignal(final String url_name) {
        String sig_path = null;
        try{
            if(url_name != null){
                String name;
                String value;
                int st_idx;
                final Properties pr = new Properties();
                if((st_idx = url_name.indexOf("?")) != -1){
                    final String param = url_name.substring(st_idx + 1);
                    final StringTokenizer st = new StringTokenizer(param, "&");
                    name = st.nextToken("=");
                    value = st.nextToken("&").substring(1);
                    pr.put(name, value);
                    name = st.nextToken("=").substring(1);
                    value = st.nextToken("&").substring(1);
                    pr.put(name, value);
                    name = st.nextToken("=").substring(1);
                    value = st.nextToken("&").substring(1);
                    pr.put(name, value);
                    this.tree = pr.getProperty("experiment");
                    this.shot = pr.getProperty("shot");
                    sig_path = pr.getProperty("path");
                }
            }
        }catch(final Exception exc){
            sig_path = null;
        }
        return sig_path;
    }

    @Override
    protected String getTree() {
        return this.tree;
    }
}

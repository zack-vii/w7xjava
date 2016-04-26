package twu;

/* $Id$ */
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import jScope.jScopeBrowseSignals;

public final class textorBrowseSignals extends jScopeBrowseSignals{
    static final long serialVersionUID = 2434234236534654L;

    static private boolean reasonableShotNr(final String shot) {
        try{
            new Integer(shot);
            return true;
        }catch(final NumberFormatException e){
            return false;
        }
    }
    String path;
    String server_url;
    String shot = null;
    String tree = null;

    @Override
    protected String getServerAddr() {
        return this.server_url;
    }

    @Override
    protected String getShot() {
        return this.shot == null ? "0" : this.shot;
    }

    @Override
    protected String getSignal(final String url_name) {
        String sig_path = null, curr_line;
        // Assume (like browsers) that missing mime-type indicates text/html.
        final boolean is_image = (this.mime_type != null && this.mime_type.indexOf("image") != -1);
        try{
            if(is_image) sig_path = url_name;
            else{
                final BufferedReader br = new BufferedReader(new StringReader(this.html.getText()));
                while(sig_path == null){
                    try{
                        curr_line = br.readLine();
                        if(curr_line.startsWith("SignalURL")) sig_path = curr_line.substring(curr_line.indexOf("http:"));
                    }catch(final Exception exc){
                        JOptionPane.showMessageDialog(this, "Error reading URL " + url_name + " : Missing \"SignalURL\" property", "alert", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                }
                if(sig_path != null){
                    String group;
                    final StringTokenizer st = new StringTokenizer(sig_path, "/");
                    st.nextToken(); // skip
                    this.server_url = st.nextToken();
                    this.tree = st.nextToken();
                    group = st.nextToken();
                    this.shot = st.nextToken();
                    // Hashed_URLs
                    // If the URL refers to a TWU signal, we would like it to be hanlded
                    // (displayed and so) as a URL. I hope that this does not clash with
                    // other jScope codes. If so, tell me!
                    // J.G.Krom (Textor, Juelich, Germany) <J.Krom@fz-juelich.de>
                    if(textorBrowseSignals.reasonableShotNr(this.shot)){
                        sig_path = "//" + this.server_url + "/" + this.tree + "/" + group + "/#####" + st.nextToken("");
                        // The hashes field should map on the shotnumber field. The rest of the
                        // URL should be as normal.
                    }else this.shot = null;
                }
            }
        }catch(final Exception exc){
            sig_path = null;
        }
        return sig_path;
    }

    @Override
    protected String getTree() {
        return this.tree == null ? "" : this.tree;
    }
}

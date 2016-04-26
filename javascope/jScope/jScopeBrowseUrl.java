package jScope;

/* $Id$ */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import debug.DEBUG;

public class jScopeBrowseUrl extends JDialog{
    static final long     serialVersionUID = 156468466436846L;
    final static String   u_agent          = "jScopeBrowseUrl.java ($Revision$) for " + jScopeFacade.VERSION;
    JButton               back;
    boolean               connected        = false;
    int                   curr_url         = 0;
    JButton               forward;
    JButton               home;
    protected JEditorPane html;
    protected String      mime_type;
    JPanel                p;
    URLConnection         url_con;
    Vector<URL>           url_list         = new Vector<URL>();

    public jScopeBrowseUrl(final JFrame owner){
        super(owner);
        this.html = new JEditorPane();
        this.html.setEditable(false);
        this.html.addHyperlinkListener(this.createHyperLinkListener());
        final JScrollPane scroller = new JScrollPane();
        final JViewport vp = scroller.getViewport();
        vp.add(this.html);
        this.getContentPane().add(scroller, BorderLayout.CENTER);
        this.p = new JPanel();
        this.back = new JButton("Back");
        this.back.setSelected(true);
        this.p.add(this.back);
        this.back.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(jScopeBrowseUrl.this.curr_url - 1 >= 0){
                    try{
                        jScopeBrowseUrl.this.curr_url--;
                        // html.setPage((URL)url_list.elementAt(curr_url));
                        jScopeBrowseUrl.this.setPage(jScopeBrowseUrl.this.url_list.elementAt(jScopeBrowseUrl.this.curr_url));
                    }catch(final IOException ioe){
                        System.out.println("IOE: " + ioe);
                    }
                }
            }
        });
        this.forward = new JButton("Forward");
        this.back.setSelected(true);
        this.p.add(this.forward);
        this.forward.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(jScopeBrowseUrl.this.curr_url + 1 < jScopeBrowseUrl.this.url_list.size()){
                    try{
                        jScopeBrowseUrl.this.curr_url++;
                        jScopeBrowseUrl.this.html.setPage(jScopeBrowseUrl.this.url_list.elementAt(jScopeBrowseUrl.this.curr_url));
                    }catch(final IOException ioe){
                        System.out.println("IOE: " + ioe);
                    }
                }
            }
        });
        this.home = new JButton("Home");
        this.home.setSelected(true);
        this.p.add(this.home);
        this.home.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(jScopeBrowseUrl.this.url_list.size() != 0){
                    try{
                        jScopeBrowseUrl.this.curr_url = 0;
                        jScopeBrowseUrl.this.html.setPage(jScopeBrowseUrl.this.url_list.elementAt(0));
                    }catch(final IOException ioe){
                        System.out.println("IOE: " + ioe);
                    }
                }
            }
        });
        this.getContentPane().add(this.p, BorderLayout.NORTH);
        this.pack();
        this.setSize(680, 700);
    }

    public void connectToBrowser(final String url_path) throws Exception {
        try{
            URL url = null;
            url = new URL(url_path);
            this.connectToBrowser(url);
            this.connected = true;
        }catch(final Exception e){
            this.connected = false;
            throw(new IOException("Unable to locate the signal server " + url_path + " : " + e.getMessage()));
        }
    }

    public void connectToBrowser(final URL url) throws Exception {
        if(DEBUG.M) System.out.println("connectToBrowser(" + url + ")");
        if(url != null){
            this.url_list.addElement(url);
            this.setPage(url);
        }
    }

    public final HyperlinkListener createHyperLinkListener() {
        return new HyperlinkListener(){
            @Override
            public void hyperlinkUpdate(final HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
                    if(e instanceof HTMLFrameHyperlinkEvent){
                        ((HTMLDocument)jScopeBrowseUrl.this.html.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)e);
                    }else{
                        try{
                            URL u = e.getURL();
                            // To fix JVM 1.1 Bug
                            if(u == null){
                                final HTMLDocument hdoc = (HTMLDocument)jScopeBrowseUrl.this.html.getDocument();
                                try{
                                    final StringTokenizer st = new StringTokenizer(hdoc.getBase().toString(), "/");
                                    final int num_token = st.countTokens();
                                    String base = st.nextToken() + "//";
                                    for(int i1 = 0; i1 < num_token - 2; i1++)
                                        base = base + st.nextToken() + "/";
                                    if(jScopeFacade.is_debug) System.out.println("JDK1.1 url = " + base + e.getDescription());
                                    u = new URL(base + e.getDescription());
                                }catch(final MalformedURLException m){
                                    u = null;
                                }
                            }
                            // end fix bug JVM 1.1
                            jScopeBrowseUrl.this.setPage(u);
                            final int sz = jScopeBrowseUrl.this.url_list.size();
                            for(int i2 = jScopeBrowseUrl.this.curr_url + 1; i2 < sz; i2++)
                                jScopeBrowseUrl.this.url_list.removeElementAt(jScopeBrowseUrl.this.curr_url + 1);
                            jScopeBrowseUrl.this.url_list.addElement(u);
                            jScopeBrowseUrl.this.curr_url++;
                        }catch(final IOException ioe){
                            JOptionPane.showMessageDialog(jScopeBrowseUrl.this, "IOE: " + ioe, "alert", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        };
    }

    @SuppressWarnings("static-method")
    public String getDefaultURL() {
        return null;
    }

    public boolean isConnected() {
        return this.connected;
    }

    protected void setPage(final URL url) throws IOException {
        this.url_con = url.openConnection();
        this.url_con.setRequestProperty("User-Agent", jScopeBrowseUrl.u_agent);
        this.mime_type = this.url_con.getContentType();
        // Assume (like browsers) that missing mime-type indicates text/html.
        if(this.mime_type == null || this.mime_type.indexOf("text") != -1) this.html.setPage(url);
        else{
            final String path = "TWU_image_message.html";
            final URL u = this.getClass().getClassLoader().getResource(path);
            this.html.setPage(u);
        }
    }
}

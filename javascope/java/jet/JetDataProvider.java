package jet;

/* $Id$ */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import jet.ji.JiDim;
import jet.ji.JiNcSource;
import jet.ji.JiVar;
import jscope.Base64;
import jscope.ConnectionEvent;
import jscope.ConnectionListener;
import jscope.DataProvider;
import jscope.DataServerItem;
import jscope.FrameData;
import jscope.RandomAccessData;
import jscope.UpdateEventListener;
import jscope.WaveData;
import jscope.WaveDataListener;
import jscope.XYData;

public final class JetDataProvider implements DataProvider{
    class SimpleWaveData implements WaveData{
        String in_x, in_y;

        public SimpleWaveData(final String in_y){
            this.in_y = in_y;
        }

        public SimpleWaveData(final String in_y, final String in_x){
            this.in_y = in_y;
            this.in_x = in_x;
        }

        @Override
        public void addWaveDataListener(final WaveDataListener listener) {}

        @Override
        public XYData getData(final double xmin, final double xmax, final int numPoints) throws Exception {
            final double x[] = this.getXDoubleData();
            final float y[] = this.getFloatData();
            return new XYData(x, y, Double.POSITIVE_INFINITY);
        }

        @Override
        public XYData getData(final int numPoints) throws Exception {
            final double x[] = this.getXDoubleData();
            final float y[] = this.getFloatData();
            return new XYData(x, y, Double.POSITIVE_INFINITY);
        }

        @Override
        public void getDataAsync(final double lowerBound, final double upperBound, final int numPoints) {}

        public float[] getFloatData() throws IOException {
            return JetDataProvider.this.getFloatArray(this.in_y, JetDataProvider.DATA);
        }

        @Override
        public int getNumDimension() throws IOException {
            JetDataProvider.this.getFloatArray(this.in_y, JetDataProvider.DATA);
            return JetDataProvider.this.dimension;
        }

        @Override
        public String getTitle() throws IOException {
            return null;
        }

        @Override
        public double[] getX2D() {
            System.out.println("BADABUM!!");
            return null;
        }

        @Override
        public long[] getX2DLong() {
            System.out.println("BADABUM!!");
            return null;
        }

        public float[] getXData() throws IOException {
            if(this.in_x != null) return JetDataProvider.this.getFloatArray(this.in_x, JetDataProvider.X);
            return JetDataProvider.this.getFloatArray(this.in_y, JetDataProvider.X);
        }

        public double[] getXDoubleData() {
            return null;
        }

        @Override
        public String getXLabel() throws IOException {
            return null;
        }

        public double[] getXLimits() {
            System.out.println("BADABUM!!");
            return null;
        }

        public long[] getXLong() {
            System.out.println("BADABUM!!");
            return null;
        }

        public long[] getXLongData() {
            return null;
        }

        @Override
        public float[] getY2D() {
            System.out.println("BADABUM!!");
            return null;
        }

        public float[] getYData() throws IOException {
            return JetDataProvider.this.getFloatArray(this.in_y, JetDataProvider.Y);
        }

        @Override
        public String getYLabel() throws IOException {
            return null;
        }

        @Override
        public float[] getZ() {
            System.out.println("BADABUM!!");
            return null;
        }

        @Override
        public String getZLabel() throws IOException {
            return null;
        }

        @Override
        public boolean isXLong() {
            return false;
        }

        @Override
        public void setContinuousUpdate(final boolean continuopusUpdate) {}
    }
    static final int                         DATA                = 0, X = 1, Y = 2;
    BufferedInputStream                      br;
    byte[]                                   buffer;
    private final Vector<ConnectionListener> connection_listener = new Vector<ConnectionListener>();
    int                                      content_len;
    private int                              dimension;
    String                                   encoded_credentials;
    String                                   error_string;
    private boolean                          evaluate_url        = false;
    String                                   experiment;
    JDialog                                  inquiry_dialog;
    private float[]                          last_data, last_x, last_y;
    private String                           last_url_name;
    private int                              login_status;
    JFrame                                   owner_f;
    JPasswordField                           passwd_text;
    String                                   provider;
    long                                     shot;
    URL                                      url;
    private String                           url_source          = "http://data.jet.uk/";
    JTextField                               user_text;
    String                                   username, passwd;

    public JetDataProvider(){
        this(null, null);
    }

    JetDataProvider(final String username, final String passwd){
        this.provider = "Jet Data";
        final String credentials = username + ":" + passwd;
        try{
            this.encoded_credentials = Base64.encode(credentials);
        }catch(final Exception e){}
    }

    @Override
    public void abort() {
        // TODO Auto-generated method stub
    }

    @Override
    public void addConnectionListener(final ConnectionListener l) {
        if(l == null){ return; }
        this.connection_listener.addElement(l);
    }

    @Override
    public void addUpdateEventListener(final UpdateEventListener l, final String event) {}

    public boolean CheckPasswd(final String encoded_credentials) {
        this.encoded_credentials = encoded_credentials;
        // System.out.println(encoded_credentials);
        try{
            URLConnection urlcon;
            this.url = new URL(this.url_source);
            urlcon = this.url.openConnection();
            urlcon.setRequestProperty("Authorization", "Basic " + encoded_credentials);
            this.br = new BufferedInputStream(urlcon.getInputStream());
            this.content_len = urlcon.getContentLength();
            if(this.content_len <= 0) return false;
            this.buffer = new byte[this.content_len];
            int num_read_bytes = 0;
            while(num_read_bytes < this.content_len)
                num_read_bytes += this.br.read(this.buffer, num_read_bytes, this.buffer.length - num_read_bytes);
            this.br.close();
            this.br = null;
        }catch(final Exception e){
            this.error_string = e.getMessage();
            return false;
        }
        final String out = new String(this.buffer);
        if(out.indexOf("incorrect password") != -1){
            this.error_string = "Incorrect password";
            return false;
        }
        return true;
    }

    boolean CheckPasswd(final String username, final String passwd) {
        final String credentials = username + ":" + passwd;
        this.encoded_credentials = Base64.encode(credentials);
        return this.CheckPasswd(this.encoded_credentials);
    }

    @Override
    public final boolean checkProvider() {
        try{
            this.getShots("0");
            return true;
        }catch(final IOException exc){}
        return false;
    };

    protected void DispatchConnectionEvent(final ConnectionEvent e) {
        if(this.connection_listener != null){
            for(int i = 0; i < this.connection_listener.size(); i++){
                this.connection_listener.elementAt(i).processConnectionEvent(e);
            }
        }
    }

    @Override
    public void dispose() {}

    public void enableAsyncUpdate(final boolean enable) {}

    @Override
    public String errorString() {
        return this.error_string;
    }

    @Override
    public Class getDefaultBrowser() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getFloat(final String in) {
        return Float.parseFloat(in);
    }

    public float[] getFloatArray(final String in, final int type) throws IOException {
        float out[] = null;
        this.error_string = null;
        final boolean is_time = (type == JetDataProvider.X);
        final boolean is_y = (type == JetDataProvider.Y);
        String url_name;
        if(this.evaluate_url){
            url_name = in;
        }else{
            if(this.experiment == null){
                final StringTokenizer st = new StringTokenizer(in, "/", true);
                url_name = st.nextToken() + "/" + this.shot;
                while(st.hasMoreTokens())
                    url_name = url_name + st.nextToken();
            }else url_name = this.experiment + "/" + this.shot + "/" + in;
        }
        out = null;
        final ConnectionEvent e = new ConnectionEvent(this, "Network");
        this.DispatchConnectionEvent(e);
        if((this.last_url_name != null && url_name.equals(this.last_url_name)) || out != null){
            if(out != null) return out;
            if(is_time) return this.last_x;
            if(is_y) return this.last_y;
            return this.last_data;
        }
        this.last_x = this.last_data = this.last_y = null;
        try{
            this.dimension = 1;
            this.last_url_name = url_name;
            URLConnection urlcon;
            this.url = new URL(this.url_source + url_name);
            urlcon = this.url.openConnection();
            // urlcon.setRequestProperty("Connection", "Keep-Alive");
            urlcon.setRequestProperty("Authorization", "Basic " + this.encoded_credentials);
            final InputStream is = urlcon.getInputStream();
            this.br = new BufferedInputStream(is);
            this.content_len = urlcon.getContentLength();
            if(this.content_len <= 0){
                this.last_url_name = null;
                this.error_string = "Error reading URL " + url_name + " : null content length";
                throw(new IOException(this.error_string));
                // return null;
            }
            this.buffer = new byte[this.content_len];
            int num_read_bytes = 0;
            while(num_read_bytes < this.content_len)
                num_read_bytes += this.br.read(this.buffer, num_read_bytes, this.buffer.length - num_read_bytes);
            this.br.close();
            this.br = null;
            final JiNcSource jns = new JiNcSource("myname", new RandomAccessData(this.buffer));
            final JiVar jvarData = jns.getVar("SIGNAL");
            final int ndims = jvarData.getDims().length;
            final JiDim jdimTime = jvarData.getDims()[ndims - 1];
            final JiVar jvarTime = jns.getVar(jdimTime.mName);
            JiDim jdimXData = null;
            JiVar jvarXData = null;
            if(ndims >= 2){
                jdimXData = jvarData.getDims()[ndims - 2];
                if(jdimXData != null){
                    jvarXData = jns.getVar(jdimXData.mName);
                }
            }
            JiDim[] dims = jvarTime.getDims();
            double[] time = jvarTime.readDouble(dims);
            this.last_x = new float[time.length];
            for(int i = 0; i < time.length; i++)
                this.last_x[i] = (float)time[i];
            time = null;
            dims = jvarData.getDims();
            this.last_data = jvarData.readFloat(dims);
            if(jvarXData != null){
                this.dimension = 2;
                dims = jvarXData.getDims();
                this.last_y = jvarXData.readFloat(dims);
            }
        }catch(final Exception ex){
            this.error_string = "Error reading URL " + url_name + " : " + ex;
            this.last_url_name = null;
            throw(new IOException(this.error_string));
        }
        if(is_time) return this.last_x;
        if(is_y) return this.last_y;
        return this.last_data;
    }

    // DataProvider implementation
    // public float[] GetFrameTimes(String in_frame){return null;}
    // public byte[] GetFrameAt(String in_frame, int frame_idx){return null;}
    // public byte[] GetAllFrames(String in_frame){return null;}
    @Override
    public FrameData getFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        throw(new IOException("Frames visualization on JetDataProvider not implemented"));
    }

    @Override
    public final String getLegendString(final String s) {
        return s;
    }

    public int getLoginStatus() {
        return this.login_status;
    }

    @Override
    public long[] getShots(final String in) throws IOException {
        this.error_string = null;
        long[] result;
        String curr_in = in.trim();
        if(curr_in.startsWith("[", 0)){
            if(curr_in.endsWith("]")){
                curr_in = curr_in.substring(1, curr_in.length() - 1);
                final StringTokenizer st = new StringTokenizer(curr_in, ",", false);
                result = new long[st.countTokens()];
                int i = 0;
                try{
                    while(st.hasMoreTokens())
                        result[i++] = Long.parseLong(st.nextToken());
                    return result;
                }catch(final Exception e){}
            }
        }else{
            if(curr_in.indexOf(":") != -1){
                final StringTokenizer st = new StringTokenizer(curr_in, ":");
                int start, end;
                if(st.countTokens() == 2){
                    try{
                        start = Integer.parseInt(st.nextToken());
                        end = Integer.parseInt(st.nextToken());
                        if(end < start) end = start;
                        result = new long[end - start + 1];
                        for(int i = 0; i < end - start + 1; i++)
                            result[i] = start + i;
                        return result;
                    }catch(final Exception e){}
                }
            }else{
                result = new long[1];
                try{
                    result[0] = Long.parseLong(curr_in);
                    return result;
                }catch(final Exception e){}
            }
        }
        this.error_string = "Error parsing shot number(s)";
        throw(new IOException(this.error_string));
    }

    @Override
    public String getString(final String in) {
        return in;
    }

    @Override
    public WaveData getWaveData(final String in) {
        return new SimpleWaveData(in);
    }

    @Override
    public WaveData getWaveData(final String in_y, final String in_x) {
        return new SimpleWaveData(in_y, in_x);
    }

    @Override
    public int inquireCredentials(final JFrame f, final DataServerItem server_item) {
        final String user = server_item.user;
        this.login_status = DataProvider.LOGIN_OK;
        this.owner_f = f;
        this.inquiry_dialog = new JDialog(f, "JET data server login", true);
        this.inquiry_dialog.getContentPane().setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.add(new JLabel("Username: "));
        this.user_text = new JTextField(15);
        p.add(this.user_text);
        if(user != null) this.user_text.setText(user);
        this.inquiry_dialog.getContentPane().add(p, "North");
        p = new JPanel();
        p.add(new JLabel("Password: "));
        this.passwd_text = new JPasswordField(15);
        this.passwd_text.setEchoChar('*');
        p.add(this.passwd_text);
        this.inquiry_dialog.getContentPane().add(p, "Center");
        p = new JPanel();
        final JButton ok_b = new JButton("Ok");
        ok_b.setDefaultCapable(true);
        ok_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                JetDataProvider.this.username = JetDataProvider.this.user_text.getText();
                JetDataProvider.this.passwd = new String(JetDataProvider.this.passwd_text.getPassword());
                if(!JetDataProvider.this.CheckPasswd(JetDataProvider.this.username, JetDataProvider.this.passwd)){
                    JOptionPane.showMessageDialog(JetDataProvider.this.inquiry_dialog, "Login ERROR : " + ((JetDataProvider.this.error_string != null) ? JetDataProvider.this.error_string : "no further information"), "alert", JOptionPane.ERROR_MESSAGE);
                    JetDataProvider.this.login_status = DataProvider.LOGIN_ERROR;
                }else{
                    JetDataProvider.this.inquiry_dialog.setVisible(false);
                    JetDataProvider.this.login_status = DataProvider.LOGIN_OK;
                }
            }
        });
        p.add(ok_b);
        final JButton clear_b = new JButton("Clear");
        clear_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                JetDataProvider.this.user_text.setText("");
                JetDataProvider.this.passwd_text.setText("");
            }
        });
        p.add(clear_b);
        final JButton cancel_b = new JButton("Cancel");
        cancel_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                JetDataProvider.this.login_status = DataProvider.LOGIN_CANCEL;
                JetDataProvider.this.inquiry_dialog.setVisible(false);
            }
        });
        p.add(cancel_b);
        this.inquiry_dialog.getContentPane().add(p, "South");
        this.inquiry_dialog.pack();
        if(f != null){
            final Rectangle r = f.getBounds();
            this.inquiry_dialog.setLocation(r.x + r.width / 2 - this.inquiry_dialog.getBounds().width / 2, r.y + r.height / 2 - this.inquiry_dialog.getBounds().height / 2);
        }else{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.inquiry_dialog.setLocation(screenSize.width / 2 - this.inquiry_dialog.getSize().width / 2, screenSize.height / 2 - this.inquiry_dialog.getSize().height / 2);
        }
        this.inquiry_dialog.setVisible(true);
        return this.login_status;
    }

    @Override
    public void join() {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeConnectionListener(final ConnectionListener l) {
        if(l == null){ return; }
        this.connection_listener.removeElement(l);
    }

    @Override
    public void removeUpdateEventListener(final UpdateEventListener l, final String event) {}

    @Override
    public void setArgument(final String arg) {}

    public void setCompression(final boolean state) {}

    public void setContinuousUpdate() {}

    @Override
    public void setEnvironment(final String s) {}

    public void setEvaluateUrl(final boolean state) {
        this.evaluate_url = state;
    }

    public void setUrlSource(final String url_source) {
        this.url_source = url_source;
        // System.out.println(url_source);
    }

    @Override
    public boolean supportsTunneling() {
        return false;
    }

    @Override
    public void update(final String experiment, final long shot) {
        this.experiment = experiment;
        this.shot = shot;
        this.error_string = null;
    }
}

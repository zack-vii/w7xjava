package jScope;

/* $Id$ */
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.SimpleDoc;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import debug.DEBUG;

@SuppressWarnings("serial")
public final class jScopeWaveContainer extends WaveformContainer{
    class UpdW extends Thread{
        boolean pending = false;

        @Override
        synchronized public void run() {
            WaveContainerEvent wce = null;
            this.setName("Update Thread");
            while(true){
                try{
                    while(!this.pending)
                        this.wait(3);
                    this.pending = false;
                    wce = new WaveContainerEvent(this, WaveContainerEvent.START_UPDATE, "Start Update");
                    jScopeWaveContainer.this.dispatchWaveContainerEvent(wce);
                    try{
                        long time = System.nanoTime();
                        jScopeWaveContainer.this.UpdateAllWave();
                        time = System.nanoTime() - time;
                        final String msg;
                        if(!jScopeWaveContainer.this.abort) msg = String.format("All waveforms are up to date < %d ms >", time / 1000000);
                        else msg = " Aborted ";
                        wce = new WaveContainerEvent(this, WaveContainerEvent.END_UPDATE, msg);
                        jScopeWaveContainer.this.dispatchWaveContainerEvent(wce);
                    }catch(final Throwable e){
                        e.printStackTrace();
                        wce = new WaveContainerEvent(this, WaveContainerEvent.KILL_UPDATE, e.getMessage());
                        jScopeWaveContainer.this.dispatchWaveContainerEvent(wce);
                    }
                }catch(final InterruptedException e){}
            }
        }

        synchronized public void StartUpdate() {
            this.pending = true;
            this.notify();
        }
    }
    private static final int MAX_COLUMN = 4;

    public static DataServerItem buildDataServerItem(final Properties pr, final String prompt) {
        DataServerItem server_item = null;
        if(pr != null){
            final String prop = pr.getProperty(prompt + ".data_server_name");
            if(prop == null) return null;
            server_item = new DataServerItem();
            server_item.name = prop;
            server_item.argument = pr.getProperty(prompt + ".data_server_argument");
            server_item.user = pr.getProperty(prompt + ".data_server_user");
            server_item.class_name = pr.getProperty(prompt + ".data_server_class");
            server_item.browse_class = pr.getProperty(prompt + ".data_server_browse_class");
            server_item.browse_url = pr.getProperty(prompt + ".data_server_browse_url");
            server_item.tunnel_port = pr.getProperty(prompt + ".data_server_tunnel_port");
            try{
                server_item.fast_network_access = new Boolean(pr.getProperty(prompt + ".fast_network_access")).booleanValue();
            }catch(final Exception exc){
                server_item.fast_network_access = false;
            }
            try{
                server_item.enable_cache = new Boolean(pr.getProperty(prompt + ".enable_cache")).booleanValue();
            }catch(final Exception exc){
                server_item.enable_cache = false;
            }
        }
        return server_item;
    }

    private static boolean checkIpMask(final String ip, final String Mask) {
        boolean found = false;
        final StringTokenizer tokenLocalIp = new StringTokenizer(ip, ".");
        final StringTokenizer clientMaskIp = new StringTokenizer(Mask, ".");
        if(tokenLocalIp.countTokens() != clientMaskIp.countTokens()) return false;
        while(tokenLocalIp.hasMoreElements() && clientMaskIp.hasMoreTokens()){
            final String tl = tokenLocalIp.nextToken();
            final String tm = clientMaskIp.nextToken();
            if(tl.equals(tm) || tm.equals("*")){
                found = true;
                continue;
            }
            found = false;
            break;
        }
        return found;
    }

    public static DataServerItem DataServerFromClient(final DataServerItem dataServerIn) {
        int c = 0;
        final boolean found = false;
        String clientMask;
        DataServerItem out = null;
        String prompt;
        try{
            final InetAddress localaddr = InetAddress.getLocalHost();
            final String localIpAdddress = localaddr.getHostAddress();
            final String f_name = System.getProperty("user.home") + File.separator + "jScope" + File.separator + "jScope_servers.conf";
            if(dataServerIn != null && !dataServerIn.class_name.equals("mdsDataProvider")) return null;
            if(dataServerIn != null){
                final InetAddress dsiInet = InetAddress.getByName(dataServerIn.argument);
                final String disIpAdddress = dsiInet.getHostAddress();
                final StringTokenizer st = new StringTokenizer(localIpAdddress, ".");
                clientMask = st.nextToken() + "." + st.nextToken() + ".*.*";
                if(!jScopeWaveContainer.checkIpMask(disIpAdddress, clientMask)) return dataServerIn;
            }
            if((new File(f_name)).exists()){
                final Properties srvFromClientProp = new Properties();
                final FileInputStream fis = new FileInputStream(f_name);
                srvFromClientProp.load(fis);
                fis.close();
                while(!found){
                    c++;
                    prompt = "jScope.server_from_client_" + c;
                    clientMask = srvFromClientProp.getProperty(prompt + ".client_mask");
                    if(clientMask == null) break;
                    /*
                    StringTokenizer tokenLocalIp = new StringTokenizer(localIpAdddress, ".");
                    StringTokenizer clientMaskIp = new StringTokenizer(clientMask, ".");
                    if(tokenLocalIp.countTokens() != clientMaskIp.countTokens()) continue;
                    while(tokenLocalIp.hasMoreElements() && clientMaskIp.hasMoreTokens())
                    {
                        String tl = tokenLocalIp.nextToken();
                        String tm = clientMaskIp.nextToken();
                        if(found = (tl.equals(tm) || tm.equals("*"))) continue;
                        found = false;
                        break;
                    }
                     */
                    if(jScopeWaveContainer.checkIpMask(localIpAdddress, clientMask)){
                        out = jScopeWaveContainer.buildDataServerItem(srvFromClientProp, prompt);
                    }
                }
            }
        }catch(final Exception exc){
            out = null;
        }
        return out;
    }

    public static void FreeCache() {
        WaveInterface.FreeCache();
    }
    private boolean                   abort;
    private boolean                   add_sig               = false;
    private jScopeBrowseSignals       browse_sig            = null;
    // private Color colors[] = Waveform.COLOR_SET;
    // private String colors_name[] = Waveform.COLOR_NAME;
    // private int grid_mode = 0, x_lines_grid = 3, y_lines_grid = 3;
    // private boolean brief_error = true;
    private int                       columns;
    private final jScopeDefaultValues def_vals;
    DataProvider                      dp;
    // private String server_infor = null;
    private String                    event                 = null;
    private String                    main_shot_error       = null;
    private String                    main_shot_str         = null;
    private long                      main_shots[]          = null;
    private final Object              mainShotLock          = new Object();
    private String                    prev_add_signal       = null;
    private String                    print_event           = null;
    ProgressMonitor                   progressMonitor;
    private String                    save_as_txt_directory = null;
    private DataServerItem            server_item           = null;
    // private boolean supports_local = true;
    private String                    title                 = null;
    private UpdW                      updateThread;
    jScopeMultiWave                   wave_all[];

    public jScopeWaveContainer(final int rows[], final DataProvider dp, final jScopeDefaultValues def_vals){
        super(rows, false);
        this.def_vals = def_vals;
        this.dp = dp;
        final Component c[] = this.CreateWaveComponents(this.getComponentNumber());
        this.AddComponents(c);
        this.updateThread = new UpdW();
        this.updateThread.start();
        this.setBackground(Color.white);
        this.save_as_txt_directory = System.getProperty("jScope.curr_directory");
    }

    public jScopeWaveContainer(final int rows[], final jScopeDefaultValues def_vals){
        this(rows, new NotConnectedDataProvider(), def_vals);
        this.server_item = new DataServerItem("Not Connected", null, null, "NotConnectedDataProvider", null, null, null, false);
    }

    public void AbortUpdate() {
        this.abort = true;
        this.dp.abort();
    }

    public synchronized void AddAllEvents(final UpdateEventListener l) throws IOException {
        jScopeMultiWave w;
        if(this.dp == null) return;
        if(this.event != null && this.event.length() != 0) this.dp.AddUpdateEventListener(l, this.event);
        if(this.print_event != null && this.print_event.length() != 0) this.dp.AddUpdateEventListener(l, this.print_event);
        for(int i = 0, k = 0; i < 4; i++){
            for(int j = 0; j < this.getComponentsInColumn(i); j++, k++){
                w = (jScopeMultiWave)this.getGridComponent(k);
                w.AddEvent();
            }
        }
    }

    private String AddRemoveEvent(final UpdateEventListener l, final String curr_event, final String event) throws IOException {
        if(curr_event != null && curr_event.length() != 0){
            if(event == null || event.length() == 0){
                this.dp.RemoveUpdateEventListener(l, curr_event);
                return null;
            }
            if(!curr_event.equals(event)){
                this.dp.RemoveUpdateEventListener(l, curr_event);
                this.dp.AddUpdateEventListener(l, event);
            }
            return event;
        }
        if(event != null && event.length() != 0) this.dp.AddUpdateEventListener(l, event);
        return event;
    }

    public void AddSignal(final String expr, final boolean check_prev_signal) {
        if(expr != null && expr.length() != 0) if(!check_prev_signal || (check_prev_signal && (this.prev_add_signal == null || !this.prev_add_signal.equals(expr)))){
            this.prev_add_signal = expr;
            this.AddSignal(null, null, "", expr, false, false);
        }
    }

    public void AddSignal(final String tree, final String shot, final String x_expr, final String y_expr, final boolean with_error, final boolean is_image) {
        final String x[] = new String[1];
        final String y[] = new String[1];
        x[0] = x_expr;
        y[0] = y_expr;
        this.AddSignals(tree, shot, x, y, with_error, is_image);
    }

    // with_error == true => Signals is added also if an error occurs
    // during its evaluations
    public void AddSignals(final String tree, final String shot, final String x_expr[], final String y_expr[], final boolean with_error, final boolean is_image) {
        jScopeWaveInterface new_wi = null;
        final jScopeMultiWave sel_wave = (jScopeMultiWave)this.GetSelectPanel();
        if(sel_wave.wi == null || is_image){
            sel_wave.wi = new jScopeWaveInterface(sel_wave, this.dp, this.def_vals, this.IsCacheEnabled());
            sel_wave.wi.SetAsImage(is_image);
            if(!with_error) ((jScopeWaveInterface)sel_wave.wi).prev_wi = new jScopeWaveInterface(sel_wave, this.dp, this.def_vals, this.IsCacheEnabled());
        }else{
            new_wi = new jScopeWaveInterface((jScopeWaveInterface)sel_wave.wi);
            new_wi.wave = sel_wave;
            if(!with_error) new_wi.prev_wi = (jScopeWaveInterface)sel_wave.wi;
            sel_wave.wi = new_wi;
        }
        if(tree != null && (((jScopeWaveInterface)sel_wave.wi).cexperiment == null || ((jScopeWaveInterface)sel_wave.wi).cexperiment.trim().length() == 0)){
            ((jScopeWaveInterface)sel_wave.wi).cexperiment = new String(tree);
            ((jScopeWaveInterface)sel_wave.wi).defaults &= ~(1 << jScopeWaveInterface.B_exp);
        }
        if(shot != null && (((jScopeWaveInterface)sel_wave.wi).cin_shot == null || ((jScopeWaveInterface)sel_wave.wi).cin_shot.trim().length() == 0)){
            ((jScopeWaveInterface)sel_wave.wi).cin_shot = new String(shot);
            ((jScopeWaveInterface)sel_wave.wi).defaults &= ~(1 << jScopeWaveInterface.B_shot);
        }
        if(sel_wave.wi.AddSignals(x_expr, y_expr)){
            this.add_sig = true;
            this.Refresh(sel_wave, "Add signal to");
            this.update();
            this.add_sig = false;
        }
    }

    protected jScopeMultiWave BuildjScopeMultiWave(final DataProvider dp, final jScopeDefaultValues def_vals) {
        return new jScopeMultiWave(dp, def_vals, this.IsCacheEnabled());
    }

    public void ChangeDataProvider(final DataProvider dp) {
        jScopeMultiWave w;
        this.main_shot_str = null;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = (jScopeMultiWave)this.GetWavePanel(i);
            if(w != null){
                if(w.wi != null) w.wi.SetDataProvider(dp);
                w.Erase();
                w.SetTitle(null);
            }
        }
    }

    @Override
    protected Component[] CreateWaveComponents(final int num) {
        final Component c[] = new Component[num];
        jScopeMultiWave wave = null;
        for(int i = 0; i < c.length; i++){
            wave = this.BuildjScopeMultiWave(this.dp, this.def_vals);
            wave.addWaveformListener(this);
            this.SetWaveParams(wave);
            c[i] = wave;
        }
        return c;
    }

    public void EraseAllWave() {
        jScopeMultiWave w;
        for(int i = 0; i < this.getComponentNumber(); i++){
            w = (jScopeMultiWave)this.getGridComponent(i);
            if(w != null) w.Erase();
        }
    }

    public void EvaluateMainShot(final String in_shots) throws IOException {
        long long_data[] = null;
        synchronized(this.mainShotLock){
            this.main_shot_error = null;
            this.main_shots = null;
            this.main_shot_str = null;
            if(in_shots == null || in_shots.trim().length() == 0){
                this.main_shot_error = "Main shot value Undefine";
                return;
            }
            long_data = WaveInterface.GetShotArray(in_shots, this.def_vals.experiment_str, this.dp);
            if(this.main_shot_error == null) this.main_shots = long_data;
            this.main_shot_str = in_shots.trim();
        }
    }

    public void FromFile(final Properties pr, final String prompt, final int colorMapping[], final ColorMapDialog cmd) throws IOException {
        String prop;
        final int read_rows[] = {0, 0, 0, 0};
        this.resetMaximizeComponent();
        prop = pr.getProperty(prompt + ".columns");
        if(prop == null) throw(new IOException("missing columns keyword"));
        this.columns = new Integer(prop).intValue();
        this.pw = new float[jScopeWaveContainer.MAX_COLUMN];
        this.title = pr.getProperty(prompt + ".title");
        this.event = pr.getProperty(prompt + ".update_event");
        this.print_event = pr.getProperty(prompt + ".print_event");
        final DataServerItem server_item = jScopeWaveContainer.buildDataServerItem(pr, prompt);
        if(server_item != null){
            final DataServerItem server_item_conf = jScopeWaveContainer.DataServerFromClient(server_item);
            if(server_item_conf != null) this.server_item = server_item_conf;
            else this.server_item = server_item;
        }
        for(int c = 1; c <= jScopeWaveContainer.MAX_COLUMN; c++){
            prop = pr.getProperty(prompt + ".rows_in_column_" + c);
            if(prop == null){
                if(c == 1) throw(new IOException("missing rows_in_column_1 keyword"));
                break;
            }
            final int r = new Integer(prop).intValue();
            read_rows[c - 1] = r;
        }
        if(this.columns > 1){
            for(int c = 1; c < this.columns; c++){
                prop = pr.getProperty(prompt + ".vpane_" + c);
                if(prop == null){ throw(new IOException("missing vpane_" + c + " keyword")); }
                final int w = new Integer(prop).intValue();
                this.pw[c - 1] = w;
            }
        }
        prop = pr.getProperty(prompt + ".reversed");
        if(prop != null) this.reversed = Boolean.parseBoolean(prop);
        else this.reversed = false;
        this.def_vals.xmax = pr.getProperty(prompt + ".global_1_1.xmax");
        this.def_vals.xmin = pr.getProperty(prompt + ".global_1_1.xmin");
        this.def_vals.xlabel = pr.getProperty(prompt + ".global_1_1.x_label");
        this.def_vals.ymax = pr.getProperty(prompt + ".global_1_1.ymax");
        this.def_vals.ymin = pr.getProperty(prompt + ".global_1_1.ymin");
        this.def_vals.ylabel = pr.getProperty(prompt + ".global_1_1.y_label");
        this.def_vals.experiment_str = pr.getProperty(prompt + ".global_1_1.experiment");
        this.def_vals.title_str = pr.getProperty(prompt + ".global_1_1.title");
        this.def_vals.upd_event_str = pr.getProperty(prompt + ".global_1_1.event");
        this.def_vals.def_node_str = pr.getProperty(prompt + ".global_1_1.default_node");
        prop = pr.getProperty(prompt + ".global_1_1.horizontal_offset");
        {
            if(prop != null){
                int v = 0;
                try{
                    v = Integer.parseInt(prop);
                }catch(final NumberFormatException exc){}
                Waveform.SetHorizontalOffset(v);
            }
        }
        prop = pr.getProperty(prompt + ".global_1_1.vertical_offset");
        {
            if(prop != null){
                int v = 0;
                try{
                    v = Integer.parseInt(prop);
                }catch(final NumberFormatException exc){}
                Waveform.SetVerticalOffset(v);
            }
        }
        prop = pr.getProperty(prompt + ".global_1_1.shot");
        if(prop != null){
            if(prop.indexOf("_shots") != -1) this.def_vals.shot_str = prop.substring(prop.indexOf("[") + 1, prop.indexOf("]"));
            else this.def_vals.shot_str = prop;
            this.def_vals.setIsEvaluated(false);
        }
        this.ResetDrawPanel(read_rows);
        jScopeMultiWave w;
        for(int c = 0, k = 0; c < 4; c++){
            for(int r = 0; r < read_rows[c]; r++){
                w = (jScopeMultiWave)this.getGridComponent(k);
                ((jScopeWaveInterface)w.wi).FromFile(pr, "Scope.plot_" + (r + 1) + "_" + (c + 1), cmd);
                ((jScopeWaveInterface)w.wi).mapColorIndex(colorMapping);
                this.SetWaveParams(w);
                k++;
            }
        }
        // Evaluate real number of columns
        int r_columns = 0;
        for(int i = 0; i < 4; i++)
            if(read_rows[i] != 0) r_columns = i + 1;
        // Silent file configuration correction
        // possible define same warning information
        if(this.columns != r_columns){
            this.columns = r_columns;
            this.pw = new float[jScopeWaveContainer.MAX_COLUMN];
            for(int i = 0; i < this.columns; i++)
                this.pw[i] = (float)1. / this.columns;
        }else{
            if(this.columns == 4) this.pw[3] = Math.abs((float)((1000 - this.pw[2]) / 1000.));
            if(this.columns >= 3) this.pw[2] = Math.abs((float)(((this.pw[2] != 0) ? (this.pw[2] - this.pw[1]) : 1000 - this.pw[1]) / 1000.));
            if(this.columns >= 2) this.pw[1] = Math.abs((float)(((this.pw[1] != 0) ? (this.pw[1] - this.pw[0]) : 1000 - this.pw[0]) / 1000.));
            if(this.columns >= 1) this.pw[0] = Math.abs((float)(((this.pw[0] == 0) ? 1000 : this.pw[0]) / 1000.));
        }
        this.UpdateHeight();
    }

    public String GetBrowseClass() {
        return(this.server_item != null ? this.server_item.browse_class : "");
    }

    public String GetBrowseUrl() {
        return(this.server_item != null ? this.server_item.browse_url : "");
    }

    public String GetEvaluatedTitle() {
        if(this.title == null || this.title.length() == 0 || this.dp == null) return "";
        try{
            final String t = this.dp.GetString(this.title);
            final String err = this.dp.ErrorString();
            if(err == null || err.length() == 0) return t;
            return "< evaluation error >";
        }catch(final Exception e){
            return "";
        }
    }

    public String GetEvent() {
        return this.event;
    }

    public boolean GetFastNetworkState() {
        return(this.server_item != null ? this.server_item.fast_network_access : false);
    }

    /*
    private static boolean IsIpAddress(final String addr) {
        return(addr.trim().indexOf(".") != -1 && addr.trim().indexOf(" ") == -1);
    }
     */
    private JFrame GetFrameParent() {
        Container c = this;
        while(c != null && !(c instanceof JFrame))
            c = c.getParent();
        return (JFrame)c;
    }

    public synchronized void getjScopeMultiWave() {
        this.wave_all = new jScopeMultiWave[this.getGridComponentCount()];
        for(int i = 0, k = 0; i < 4; i++)
            for(int j = 0; j < this.rows[i]; j++, k++)
                this.wave_all[k] = (jScopeMultiWave)this.getGridComponent(k);
    }

    public String getMainShotError(final boolean brief) {
        // if(brief)
        // return main_shot_error.substring(0, main_shot_error.indexOf("\n"));
        // else
        return this.main_shot_error;
    }

    public long[] getMainShots() {
        return this.main_shots;
    }

    public String getMainShotStr() {
        return this.main_shot_str;
    }

    public String GetPrintEvent() {
        return this.print_event;
    }

    // public String GetServerLabel(){return (server_item != null ? server_item.name : "");}
    public String GetServerArgument() {
        return(this.server_item != null ? this.server_item.argument : "");
    }

    public DataServerItem GetServerItem() {
        return this.server_item;
    }

    /*
    remove 28/06/2005
        public void SetServerItem(DataServerItem dsi)
        {
            server_item = dsi;
        }
     */
    public String GetServerLabel() {
        if(this.dp == null && this.server_item != null && this.server_item.name != null) return "Can't connect to " + this.server_item.name;
        if(this.dp == null && this.server_item == null) return "Not connected";
        return this.server_item.name;
    }

    public String GetTitle() {
        return this.title;
    }

    public void InvalidateDefaults() {
        jScopeMultiWave w;
        for(int i = 0, k = 0; i < this.rows.length; i++)
            for(int j = 0; j < this.rows[i]; j++, k++){
                w = (jScopeMultiWave)this.getGridComponent(k);
                if(w != null) ((jScopeWaveInterface)w.wi).default_is_update = false;
            }
    }

    public boolean IsCacheEnabled() {
        return(this.server_item != null ? this.server_item.enable_cache : false);
    }

    @Override
    public void maximizeComponent(final Waveform w) {
        super.maximizeComponent(w);
        if(w == null) this.StartUpdate();
    }

    @Override
    public void NotifyChange(final Waveform dest, final Waveform source) {
        final jScopeMultiWave w = ((jScopeMultiWave)source);
        final jScopeWaveInterface mwi = new jScopeWaveInterface(((jScopeWaveInterface)w.wi));
        mwi.setDefaultsValues(this.def_vals);
        ((jScopeMultiWave)dest).wi = mwi;
        ((jScopeMultiWave)dest).wi.SetDataProvider(this.dp);
        ((jScopeMultiWave)dest).wi.wave = dest;
        this.Refresh((jScopeMultiWave)dest, "Copy in");
    }

    @Override
    public int print(final Graphics g, final PageFormat pf, final int pageIndex) throws PrinterException {
        final int st_x = 0, st_y = 0;
        final double height = pf.getImageableHeight();
        final double width = pf.getImageableWidth();
        final Graphics2D g2 = (Graphics2D)g;
        if(pageIndex == 0){
            g2.translate(pf.getImageableX(), pf.getImageableY());
            this.PrintAll(g2, st_x, st_y, (int)height, (int)width);
            return Printable.PAGE_EXISTS;
        }
        return Printable.NO_SUCH_PAGE;
    }

    public void PrintAll(final Graphics g, final int height, final int width) {
        if(this.font == null){
            this.font = g.getFont();
            this.font = new Font(this.font.getName(), this.font.getStyle(), 18);
            g.setFont(this.font);
        }else{
            this.font = new Font(this.font.getName(), this.font.getStyle(), 18);
            g.setFont(this.font);
        }
        super.PrintAll(g, 0, 0, height, width);
    }

    @Override
    public void PrintAll(final Graphics g, final int st_x, int st_y, int height, final int width) {
        final String title = this.GetEvaluatedTitle();
        if(title != null && title.length() != 0){
            FontMetrics fm;
            int s_width;
            if(this.font == null){
                this.font = g.getFont();
                this.font = new Font(this.font.getName(), this.font.getStyle(), 18);
                g.setFont(this.font);
            }else{
                this.font = new Font(this.font.getName(), this.font.getStyle(), 18);
                g.setFont(this.font);
            }
            fm = g.getFontMetrics();
            s_width = fm.stringWidth(title);
            st_y += fm.getHeight() / 2 + 2;
            g.drawString(title, st_x + (width - s_width) / 2, st_y);
            st_y += 2;
            height -= st_y;
        }
        super.PrintAll(g, st_x, st_y, height, width);
    }

    public void PrintAllWaves(final DocPrintJob prnJob, final PrintRequestAttributeSet attrs) throws PrinterException, PrintException {
        final DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        final Doc doc = new SimpleDoc(this, flavor, null);
        prnJob.print(doc, attrs);
    }

    @Override
    public void processWaveformEvent(final WaveformEvent e) {
        super.processWaveformEvent(e);
        final jScopeMultiWave w = (jScopeMultiWave)e.getSource();
        switch(e.getID()){
            case WaveformEvent.END_UPDATE:
                final Point p = this.getComponentPosition(w);
                if(w.wi.isAddSignal()){
                    String er;
                    if(!w.wi.IsSignalAdded()) this.prev_add_signal = null;
                    if(w.wi.error != null) er = w.wi.error;
                    else er = ((jScopeWaveInterface)w.wi).getErrorString(); // this.brief_error);
                    if(er != null) JOptionPane.showMessageDialog(this, er, "alert processWaveformEvent", JOptionPane.ERROR_MESSAGE);
                    w.wi.setAddSignal(false);
                }
                final WaveContainerEvent wce = new WaveContainerEvent(this, WaveContainerEvent.END_UPDATE, "Wave column " + p.x + " row " + p.y + " is updated");
                jScopeWaveContainer.this.dispatchWaveContainerEvent(wce);
                break;
        }
    }

    public synchronized void Refresh(final jScopeMultiWave w, final String label) {
        Point p = null;
        if(this.add_sig) p = this.getSplitPosition();
        if(p == null) p = this.getComponentPosition(w);
        // Disable signal cache if public variable
        // are set
        /*
        if (def_vals != null &&
            def_vals.public_variables != null &&
            def_vals.public_variables.trim().length() != 0 &&
            IsCacheEnabled())
        {
            this.SetCacheState(false);
        }
         */
        if(this.def_vals != null && this.def_vals.isSet()) this.SetCacheState(false);
        final WaveContainerEvent wce = new WaveContainerEvent(this, WaveContainerEvent.START_UPDATE, label + " wave column " + p.x + " row " + p.y);
        jScopeWaveContainer.this.dispatchWaveContainerEvent(wce);
        // If is added a signal to the waveform only signal added
        // is evaluated, in the other cases, refresh from popup menu
        // or event update, all signals in the waveform must be
        // evaluated
        /*
        if (!add_sig)
            ( (jScopeMultiWave) w).wi.setModified(true);
         */
        w.Refresh();
        if(this.add_sig) this.resetSplitPosition();
    }

    public void RemoveAllEvents(final UpdateEventListener l) throws IOException {
        jScopeMultiWave w;
        if(this.dp == null) return;
        if(this.event != null && this.event.length() != 0) this.dp.RemoveUpdateEventListener(l, this.event);
        if(this.print_event != null && this.print_event.length() != 0) this.dp.RemoveUpdateEventListener(l, this.print_event);
        for(int i = 0, k = 0; i < 4; i++){
            for(int j = 0; j < this.getComponentsInColumn(i); j++, k++){
                w = (jScopeMultiWave)this.getGridComponent(k);
                w.RemoveEvent();
            }
        }
    }

    private void RepaintAllWave() {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                jScopeMultiWave wx;
                for(int i = 0, k = 0; i < 4; i++){
                    for(int j = 0; j < jScopeWaveContainer.this.rows[i]; j++, k++){
                        wx = (jScopeMultiWave)jScopeWaveContainer.this.getGridComponent(k);
                        if(wx.wi != null) wx.Update(wx.wi);
                    }
                }
            }
        });
    }

    public void Reset() {
        final int reset_rows[] = {1, 0, 0, 0};
        this.ph = null;
        this.pw = null;
        this.SetTitle(null);
        this.event = null;
        this.print_event = null;
        this.ResetDrawPanel(reset_rows);
        this.update();
        final jScopeMultiWave w = (jScopeMultiWave)this.GetWavePanel(0);
        w.jScopeErase();
        this.def_vals.Reset();
    }

    public void SaveAsText(final jScopeMultiWave w, final boolean all) {
        final Vector<jScopeMultiWave> panel = new Vector<jScopeMultiWave>();
        jScopeWaveInterface wi;
        jScopeMultiWave wave;
        if(!all && (w == null || w.wi == null || w.wi.signals == null || w.wi.signals.length == 0)) return;
        String title = "Save";
        if(all) title = "Save all signals in text format";
        else{
            final Point p = this.getWavePosition(w);
            if(p != null) title = "Save signals on panel (" + p.x + ", " + p.y + ") in text format";
        }
        JFileChooser file_diag = new JFileChooser();
        if(this.save_as_txt_directory != null && this.save_as_txt_directory.trim().length() != 0) file_diag.setCurrentDirectory(new File(this.save_as_txt_directory));
        file_diag.setDialogTitle(title);
        int returnVal = JFileChooser.CANCEL_OPTION;
        String txtsig_file = null;
        while(true){
            returnVal = file_diag.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                final File file = file_diag.getSelectedFile();
                txtsig_file = file.getAbsolutePath();
                if(file.exists()){
                    final Object[] options = {"Yes", "No"};
                    final int val = JOptionPane.showOptionDialog(this, txtsig_file + " already exists.\nDo you want to replace it?", "Save as", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    if(val == JOptionPane.YES_OPTION) break;
                }else break;
            }else break;
        }
        if(returnVal == JFileChooser.APPROVE_OPTION){
            if(txtsig_file != null){
                this.save_as_txt_directory = new String(txtsig_file);
                if(all){
                    for(int i = 0; i < this.GetWaveformCount(); i++)
                        panel.addElement((jScopeMultiWave)this.GetWavePanel(i));
                }else panel.addElement(w);
                try{
                    final StringBuffer space = new StringBuffer();
                    String s = "", s1 = "", s2 = "";
                    final BufferedWriter out = new BufferedWriter(new FileWriter(txtsig_file));
                    for(int l = 0; l < 3; l++){
                        s = "%";
                        for(int k = 0; k < panel.size(); k++){
                            wave = panel.elementAt(k);
                            wi = (jScopeWaveInterface)wave.wi;
                            if(wi == null || wi.signals == null) continue;
                            for(int i = 0; i < wi.signals.length; i++){
                                switch(l){
                                    case 0:
                                        s += "x : " + ((wi.in_x != null && wi.in_x.length > 0) ? wi.in_x[i] : "None");
                                        break;
                                    case 1:
                                        s += "y : " + ((wi.in_y != null && wi.in_y.length > 0) ? wi.in_y[i] : "None");
                                        break;
                                    case 2:
                                        s += "Shot : " + ((wi.shots != null && wi.shots.length > 0) ? "" + wi.shots[i] : "None");
                                        break;
                                }
                                out.write(s, 0, (s.length() < 50) ? s.length() : 50);
                                space.setLength(0);
                                for(int u = 0; u < 52 - s.length(); u++)
                                    space.append(' ');
                                out.write(space.toString());
                                s = "";
                            }
                        }
                        out.newLine();
                    }
                    int n_max_sig = 0;
                    final boolean more_point[] = new boolean[panel.size()];
                    for(int k = 0; k < panel.size(); k++){
                        more_point[k] = true;
                        wave = panel.elementAt(k);
                        wi = (jScopeWaveInterface)wave.wi;
                        if(wi == null || wi.signals == null) continue;
                        if(wi.signals.length > n_max_sig) n_max_sig = wi.signals.length;
                    }
                    boolean g_more_point = true;
                    final int start_idx[][] = new int[panel.size()][n_max_sig];
                    while(g_more_point){
                        g_more_point = false;
                        for(int k = 0; k < panel.size(); k++){
                            wave = panel.elementAt(k);
                            wi = (jScopeWaveInterface)wave.wi;
                            if(wi == null || wi.signals == null) continue;
                            if(!more_point[k]){
                                for(@SuppressWarnings("unused")
                                final Signal signal : wi.signals)
                                    out.write("                                   ");
                                continue;
                            }
                            g_more_point = true;
                            int j = 0;
                            final double xmax = wave.GetWaveformMetrics().XMax();
                            final double xmin = wave.GetWaveformMetrics().XMin();
                            more_point[k] = false;
                            for(int i = 0; i < wi.signals.length; i++){
                                s1 = "";
                                s2 = "";
                                if(wi.signals[i] != null && wi.signals[i].hasX()){
                                    for(j = start_idx[k][i]; j < wi.signals[i].getNumPoints(); j++){
                                        if(wi.signals[i].getX(j) > xmin && wi.signals[i].getX(j) < xmax){
                                            more_point[k] = true;
                                            s1 = "" + wi.signals[i].getX(j);
                                            s2 = "" + wi.signals[i].getY(j);
                                            start_idx[k][i] = j + 1;
                                            break;
                                        }
                                    }
                                }
                                out.write(s1);
                                space.setLength(0);
                                for(int u = 0; u < 25 - s1.length(); u++)
                                    space.append(' ');
                                space.append(' ');
                                out.write(space.toString());
                                out.write(" ");
                                out.write(s2);
                                space.setLength(0);
                                for(int u = 0; u < 25 - s2.length(); u++)
                                    space.append(' ');
                                out.write(space.toString());
                            }
                        }
                        out.newLine();
                    }
                    out.close();
                }catch(final IOException e){
                    System.out.println(e);
                }
            }
        }
        file_diag = null;
    }

    public void SetCacheState(final boolean state) {
        jScopeMultiWave w;
        this.server_item.enable_cache = state;
        for(int i = 0; i < this.getComponentNumber(); i++){
            w = (jScopeMultiWave)this.getGridComponent(i);
            if(w != null && w.wi != null){
                w.wi.EnableCache(state);
                w.wi.setModified(true);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void SetDataServer(DataServerItem server_item, final UpdateEventListener l) throws Exception {
        DataProvider new_dp = null;
        if(DEBUG.D){
            System.out.println("server_item.name " + server_item.name);
        }
        if(server_item == null || server_item.name.trim().length() == 0) throw(new Exception("Defined null or empty data server name"));
        if(DEBUG.D){
            System.out.println("server_item.class_name " + server_item.class_name);
        }
        if(server_item.class_name != null){
            try{
                final Class cl = Class.forName(server_item.class_name);
                new_dp = (DataProvider)cl.newInstance();
            }catch(final Exception e){
                throw(new Exception("Can't load data provider class : " + server_item.class_name + "\n" + e));
            }
        }else{
            throw(new Exception("Undefine data provider class for " + server_item.name));
        }
        if(this.browse_sig != null && this.browse_sig.isShowing()) this.browse_sig.setVisible(false);
        try{
            // Current data server Disconnection
            this.RemoveAllEvents(l);
            if(this.dp != null){
                this.dp.RemoveConnectionListener((ConnectionListener)l);
                this.dp.Dispose();
            }
            this.dp = new_dp;
            final int option = this.dp.InquireCredentials(this.GetFrameParent(), server_item);
            switch(option){
                case DataProvider.LOGIN_OK:
                    this.dp.SetArgument(server_item.argument);
                    break;
                case DataProvider.LOGIN_ERROR:
                case DataProvider.LOGIN_CANCEL:
                    server_item = new DataServerItem("Not Connected", null, null, "NotConnectedDataProvider", null, null, null, false);
                    this.dp = new NotConnectedDataProvider();
            }
            if(this.dp != null) this.dp.AddConnectionListener((ConnectionListener)l);
            if(!server_item.class_name.equals("NotConnectedDataProvider")){
                // Check data server connection
                if(!this.dp.checkProvider()) throw(new Exception("Cannot connect to " + server_item.class_name + " data server"));
            }
            this.ChangeDataProvider(this.dp);
            this.AddAllEvents(l);
            // create browse panel if defined
            final Class cl;
            if(server_item.browse_class != null) cl = Class.forName(server_item.browse_class);
            else cl = this.dp.getDefaultBrowser();
            try{
                this.browse_sig = (jScopeBrowseSignals)cl.newInstance();
                if(server_item.browse_url == null) server_item.browse_url = this.browse_sig.getDefaultURL();
                this.browse_sig.setWaveContainer(this);
            }catch(final Exception e){
                this.browse_sig = null;
                if(server_item.browse_url != null) JOptionPane.showMessageDialog(this, "Unable to locate the signal server " + server_item.browse_url + " : " + e, "alert", JOptionPane.ERROR_MESSAGE);
            }
            this.server_item = server_item;
        }catch(final IOException e){
            this.server_item = new DataServerItem("Not Connected", null, null, "NotConnectedDataProvider", null, null, null, false);
            this.dp = new NotConnectedDataProvider();
            this.ChangeDataProvider(this.dp);
            throw(e);
        }
    }

    public void SetEvent(final UpdateEventListener l, final String event) throws IOException {
        this.event = this.AddRemoveEvent(l, this.event, event);
    }

    public void SetFastNetworkState(final boolean state) {
        jScopeMultiWave w;
        this.server_item.fast_network_access = state;
        for(int i = 0; i < this.getComponentNumber(); i++){
            w = (jScopeMultiWave)this.getGridComponent(i);
            if(w != null && w.wi != null) w.wi.setModified(true);
        }
    }

    public void SetMainShot(final String shot_str) {
        if(shot_str != null){
            try{
                this.EvaluateMainShot(shot_str.trim());
            }catch(final IOException exc){
                this.main_shot_str = null;
                this.main_shot_error = "Main Shots evaluations error : \n" + exc.getMessage();
                JOptionPane.showMessageDialog(this, this.main_shot_error, "alert SetMainShot", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void SetModifiedState(final boolean state) {
        jScopeMultiWave w;
        for(int i = 0; i < this.getComponentNumber(); i++){
            w = (jScopeMultiWave)this.getGridComponent(i);
            if(w != null && w.wi != null) w.wi.setModified(state);
        }
    }

    public void SetPrintEvent(final UpdateEventListener l, final String print_event) throws IOException {
        this.print_event = this.AddRemoveEvent(l, this.print_event, print_event);
    }

    public void SetTitle(final String title) {
        this.title = title;
    }

    public void ShowBrowseSignals() {
        if(this.browse_sig != null){
            if(!this.browse_sig.isConnected()) try{
                this.browse_sig.connectToBrowser(this.server_item.browse_url);
                this.browse_sig.setTitle("URL : " + this.server_item.browse_url);
            }catch(final Exception e){
                JOptionPane.showMessageDialog(this, e.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
            }
            this.browse_sig.setLocation(this.getLocation());
            this.browse_sig.setVisible(true);
        }else{
            String msg;
            if(this.GetBrowseUrl() == null) msg = "Signals browser not yet implemented on this data server";
            else msg = "jScope is unable to locate the signal server page at " + this.GetBrowseUrl() + "\nModify browse_url property for this data server in jScope.properties file.";
            JOptionPane.showMessageDialog(this, msg, "alert", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void StartPrint(final DocPrintJob prnJob, final PrintRequestAttributeSet attrs) {
        try{
            this.getjScopeMultiWave();
            this.UpdateAllWave();
            this.PrintAllWaves(prnJob, attrs);
        }catch(final InterruptedException e){}catch(final PrinterException e){}catch(final Exception e){}
    }

    public void StartUpdate() {
        if(!this.updateThread.isAlive()){
            this.updateThread = new UpdW();
            this.updateThread.start();
        }
        this.getjScopeMultiWave();
        this.updateThread.StartUpdate();
    }

    public void startUpdatingPanel() {
        this.progressMonitor = new ProgressMonitor(this, "Running a Long Task", "", 0, 0);
    }

    public void stopUpdatingPanel() {
        this.progressMonitor.close();
    }

    public void ToFile(final PrintWriter out, final String prompt) throws IOException {
        jScopeMultiWave w;
        jScopeWaveInterface wi;
        WaveInterface.WriteLine(out, prompt + "title: ", this.title);
        if(this.server_item != null){
            WaveInterface.WriteLine(out, prompt + "data_server_name: ", this.server_item.name);
            WaveInterface.WriteLine(out, prompt + "data_server_class: ", this.server_item.class_name);
            if(this.server_item.argument != null) WaveInterface.WriteLine(out, prompt + "data_server_argument: ", this.server_item.argument);
            if(this.server_item.user != null) WaveInterface.WriteLine(out, prompt + "data_server_user: ", this.server_item.user);
            if(this.server_item.browse_class != null) WaveInterface.WriteLine(out, prompt + "data_server_browse_class: ", this.server_item.browse_class);
            if(this.server_item.browse_url != null) WaveInterface.WriteLine(out, prompt + "data_server_browse_url: ", this.server_item.browse_url);
            if(this.server_item.tunnel_port != null) WaveInterface.WriteLine(out, prompt + "data_server_tunnel_port: ", this.server_item.tunnel_port);
            WaveInterface.WriteLine(out, prompt + "fast_network_access: ", "" + this.server_item.fast_network_access);
            if(this.server_item.enable_cache) WaveInterface.WriteLine(out, prompt + "enable_cache: ", "" + this.server_item.enable_cache);
        }
        WaveInterface.WriteLine(out, prompt + "update_event: ", this.event);
        WaveInterface.WriteLine(out, prompt + "print_event: ", this.print_event);
        WaveInterface.WriteLine(out, prompt + "reversed: ", "" + this.reversed);
        out.println();
        WaveInterface.WriteLine(out, prompt + "global_1_1.experiment: ", this.def_vals.experiment_str);
        WaveInterface.WriteLine(out, prompt + "global_1_1.event: ", this.def_vals.upd_event_str);
        WaveInterface.WriteLine(out, prompt + "global_1_1.default_node: ", this.def_vals.def_node_str);
        WaveInterface.WriteLine(out, prompt + "global_1_1.shot: ", this.def_vals.shot_str);
        WaveInterface.WriteLine(out, prompt + "global_1_1.title: ", this.def_vals.title_str);
        WaveInterface.WriteLine(out, prompt + "global_1_1.xmax: ", this.def_vals.xmax);
        WaveInterface.WriteLine(out, prompt + "global_1_1.xmin: ", this.def_vals.xmin);
        WaveInterface.WriteLine(out, prompt + "global_1_1.x_label: ", this.def_vals.xlabel);
        WaveInterface.WriteLine(out, prompt + "global_1_1.ymax: ", this.def_vals.ymax);
        WaveInterface.WriteLine(out, prompt + "global_1_1.ymin: ", this.def_vals.ymin);
        WaveInterface.WriteLine(out, prompt + "global_1_1.y_label: ", this.def_vals.ylabel);
        WaveInterface.WriteLine(out, prompt + "global_1_1.horizontal_offset: ", "" + Waveform.GetHorizontalOffset());
        WaveInterface.WriteLine(out, prompt + "global_1_1.vertical_offset: ", "" + Waveform.GetVerticalOffset());
        out.println();
        out.println("Scope.columns: " + this.getColumns());
        final float normHeight[] = this.getNormalizedHeight();
        final float normWidth[] = this.getNormalizedWidth();
        final Dimension dim = this.getSize();
        for(int i = 0, c = 1, k = 0; i < this.getColumns(); i++, c++){
            WaveInterface.WriteLine(out, prompt + "rows_in_column_" + c + ": ", "" + this.getComponentsInColumn(i));
            for(int j = 0, r = 1; j < this.getComponentsInColumn(i); j++, r++){
                w = (jScopeMultiWave)this.getGridComponent(k);
                wi = (jScopeWaveInterface)w.wi;
                out.println("\n");
                // WaveInterface.WriteLine(out, prompt + "plot_" + r + "_" + c + ".height: " , ""+w.getSize().height );
                WaveInterface.WriteLine(out, prompt + "plot_" + r + "_" + c + ".height: ", "" + (int)(dim.height * normHeight[k]));
                WaveInterface.WriteLine(out, prompt + "plot_" + r + "_" + c + ".grid_mode: ", "" + w.grid_mode);
                if(wi != null) wi.ToFile(out, prompt + "plot_" + r + "_" + c + ".");
                k++;
            }
        }
        out.println();
        for(int i = 1, pos = 0; i < this.getColumns(); i++){ // , k = 0
            // w = (jScopeMultiWave)getGridComponent(k);
            // wi = (jScopeWaveInterface)w.wi;
            // pos += (int)(((float)w.getSize().width/ getSize().width) * 1000.);
            pos += (int)(normWidth[i - 1] * 1000.);
            WaveInterface.WriteLine(out, prompt + "vpane_" + i + ": ", "" + pos);
            // k += getComponentsInColumn(i);
        }
    }

    public synchronized void UpdateAllWave() throws Exception {
        WaveContainerEvent wce;
        try{
            if(this.wave_all == null) this.abort = true;
            else this.abort = false;
            if(this.def_vals != null && !this.def_vals.getIsEvaluated()){
                this.dp.SetEnvironment(this.def_vals.getPublicVariables());
                /*
                if (IsCacheEnabled())
                {
                    JOptionPane.showMessageDialog(this,
                        "Signal cache must be disabled when public varibles are set.\n" +
                        "Cache operation is automatically disabled.",
                        "alert", JOptionPane.WARNING_MESSAGE);
                    SetCacheState(false);
                }
                 */
                this.def_vals.setIsEvaluated(true);
            }
            for(int i = 0, k = 0; i < 4 && !this.abort; i++){
                for(int j = 0; j < this.rows[i]; j++, k++){
                    if(this.wave_all[k].wi != null && this.wave_all[k].isWaveformVisible()) ((jScopeWaveInterface)this.wave_all[k].wi).Update();
                }
            }
            // Initialize wave evaluation
            for(int i = 0, k = 0; i < 4 && !this.abort; i++){
                for(int j = 0; j < this.rows[i] && !this.abort; j++, k++){
                    if(this.wave_all[k].wi != null && this.wave_all[k].wi.error == null && this.wave_all[k].isWaveformVisible()){
                        wce = new WaveContainerEvent(this, WaveContainerEvent.START_UPDATE, "Start Evaluate column " + (i + 1) + " row " + (j + 1));
                        try{
                            this.dispatchWaveContainerEvent(wce);
                            ((jScopeWaveInterface)this.wave_all[k].wi).StartEvaluate();
                        }catch(final Exception exc){
                            exc.printStackTrace();
                        }
                    }
                }
            }
            synchronized(this.mainShotLock){
                if(this.main_shots != null){
                    for(int l = 0; l < this.main_shots.length && !this.abort; l++){
                        for(int i = 0, k = 0; i < 4 && !this.abort; i++){
                            for(int j = 0; j < this.rows[i] && !this.abort; j++, k++){
                                if(this.wave_all[k].wi != null && this.wave_all[k].wi.error == null && this.wave_all[k].isWaveformVisible() && this.wave_all[k].wi.num_waves != 0 && ((jScopeWaveInterface)this.wave_all[k].wi).UseDefaultShot()){
                                    wce = new WaveContainerEvent(this, WaveContainerEvent.START_UPDATE, "Update signal column " + (i + 1) + " row " + (j + 1) + " main shot " + this.main_shots[l]);
                                    this.dispatchWaveContainerEvent(wce);
                                    ((jScopeWaveInterface)this.wave_all[k].wi).EvaluateShot(this.main_shots[l]);
                                    if(((jScopeWaveInterface)this.wave_all[k].wi).allEvaluated()){
                                        if(this.wave_all[k].wi != null) this.wave_all[k].Update(this.wave_all[k].wi);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Evaluate evaluate other shot
            if(this.wave_all != null) for(int i = 0, k = 0; i < 4 && k < this.wave_all.length && !this.abort; i++){
                for(int j = 0; j < this.rows[i] && k < this.wave_all.length && !this.abort; j++, k++){
                    if(this.wave_all[k] != null && this.wave_all[k].isWaveformVisible()){
                        if(this.wave_all[k].wi.error == null && this.wave_all[k].wi.num_waves != 0){
                            if(((jScopeWaveInterface)this.wave_all[k].wi).allEvaluated()) continue;
                            wce = new WaveContainerEvent(this, WaveContainerEvent.START_UPDATE, "Evaluate wave column " + (i + 1) + " row " + (j + 1));
                            this.dispatchWaveContainerEvent(wce);
                            ((jScopeWaveInterface)this.wave_all[k].wi).EvaluateOthers();
                        }
                        this.wave_all[k].Update(this.wave_all[k].wi);
                    }
                }
            }
            for(int i = 0, k = 0; i < 4; i++){
                for(int j = 0; j < this.rows[i]; j++, k++){
                    if(this.wave_all != null && this.wave_all[k] != null && this.wave_all[k].wi != null){
                        ((jScopeWaveInterface)this.wave_all[k].wi).allEvaluated();
                    }
                }
            }
            this.wave_all = null;
        }catch(final Exception e){
            e.printStackTrace();
            this.RepaintAllWave();
            // throw (e);
        }
        this.dp.join();
    }

    public void UpdateHeight() {
        float height = 0;
        jScopeMultiWave w;
        this.ph = new float[this.getComponentNumber()];
        for(int j = 0, k = 0; j < this.columns; j++){
            height = 0;
            for(int i = 0; i < this.rows[j]; i++){
                w = (jScopeMultiWave)this.getGridComponent(k + i);
                height += w.wi.height;
            }
            for(int i = 0; i < this.rows[j]; i++, k++){
                w = (jScopeMultiWave)this.getGridComponent(k);
                if(height == 0){
                    k -= i;
                    for(i = 0; i < this.rows[j]; i++)
                        this.ph[k++] = (float)1. / this.rows[j];
                    break;
                }
                this.ph[k] = (w.wi.height / height);
            }
        }
        this.invalidate();
    }
}

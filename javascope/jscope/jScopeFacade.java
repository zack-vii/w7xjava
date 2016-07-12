package jscope;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.RepaintManager;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import debug.DEBUG;

@SuppressWarnings("serial")
public final class jScopeFacade extends JFrame implements ActionListener, ItemListener, WindowListener, WaveContainerListener, UpdateEventListener, ConnectionListener, Printable{
    class FileFilter implements FilenameFilter{
        String fname = null;

        FileFilter(final String fname){
            this.fname = fname;
        }

        @Override
        public boolean accept(final File dir, final String name) {
            if(name.indexOf(this.fname) == 0) return true;
            return false;
        }
    }
    class MonMemory extends Thread{
        @Override
        public void run() {
            this.setName("Monitor Thread");
            try{
                while(true){
                    jScopeFacade.this.setWindowTitle("Free :" + (int)(Runtime.getRuntime().freeMemory() / 1024) + " " + "Total :" + (int)(Runtime.getRuntime().totalMemory()) / 1024 + " " + "USED :" + (int)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.));
                    Thread.sleep(2000, 0);
                }
            }catch(final InterruptedException e){}
        }

        synchronized void waitTime(final long t) throws InterruptedException {
            this.wait(t);
        }
    }
    class PubVarDialog extends JDialog implements ActionListener{
        private final JButton        apply, cancel, save, reset;
        jScopeFacade                 dw;
        private final Vector<String> expr_list   = new Vector<String>();
        boolean                      is_pv_apply = false;
        private final Vector<String> name_list   = new Vector<String>();

        PubVarDialog(final Frame fw){
            super(fw, "Public Variables", false);
            this.dw = (jScopeFacade)fw;
            final GridBagConstraints c = new GridBagConstraints();
            final GridBagLayout gridbag = new GridBagLayout();
            this.getContentPane().setLayout(gridbag);
            c.insets = new Insets(2, 2, 2, 2);
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.CENTER;
            c.gridwidth = 1;
            JLabel lab = new JLabel("Name");
            gridbag.setConstraints(lab, c);
            this.getContentPane().add(lab);
            c.gridwidth = GridBagConstraints.REMAINDER;
            lab = new JLabel("Expression");
            gridbag.setConstraints(lab, c);
            this.getContentPane().add(lab);
            JTextField txt;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.BOTH;
            for(int i = 0; i < jScopeFacade.MAX_VARIABLE; i++){
                c.gridwidth = 1;
                txt = new JTextField(10);
                gridbag.setConstraints(txt, c);
                this.getContentPane().add(txt);
                c.gridwidth = GridBagConstraints.REMAINDER;
                txt = new JTextField(30);
                gridbag.setConstraints(txt, c);
                this.getContentPane().add(txt);
            }
            final JPanel p = new JPanel();
            p.setLayout(new FlowLayout(FlowLayout.CENTER));
            this.apply = new JButton("Apply");
            this.apply.addActionListener(this);
            p.add(this.apply);
            this.save = new JButton("Save");
            this.save.addActionListener(this);
            p.add(this.save);
            this.reset = new JButton("Reset");
            this.reset.addActionListener(this);
            p.add(this.reset);
            this.cancel = new JButton("Cancel");
            this.cancel.addActionListener(this);
            p.add(this.cancel);
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(p, c);
            this.getContentPane().add(p);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Object ob = e.getSource();
            if(ob == this.apply){
                this.dw.setPublicVariables(this.getPublicVar());
                this.dw.updateAllWaves();
            }
            if(ob == this.save){
                this.savePubVar();
            }
            if(ob == this.reset){
                this.setPubVar();
            }
            if(ob == this.cancel){
                this.is_pv_apply = false;
                this.setVisible(false);
            }
        }

        public void fromFile(final Properties pr, final String prompt) throws IOException {
            String prop;
            int idx = 0;
            this.name_list.removeAllElements();
            this.expr_list.removeAllElements();
            while((prop = pr.getProperty(prompt + idx)) != null && idx < jScopeFacade.MAX_VARIABLE){
                final StringTokenizer st = new StringTokenizer(prop, "=");
                final String name = st.nextToken();
                String expr = st.nextToken("");
                expr = expr.substring(expr.indexOf('=') + 1);// remove first = character in the expression
                this.name_list.insertElementAt(name.trim(), idx);
                this.expr_list.insertElementAt(expr.trim(), idx);
                idx++;
            }
        }

        public String getCurrentPublicVar() {
            String txt1, txt2, str;
            final StringBuffer buf = new StringBuffer();
            final Container p = this.getContentPane();
            for(int i = 2; i < jScopeFacade.MAX_VARIABLE * 2; i += 2){
                txt1 = ((JTextField)p.getComponent(i)).getText();
                txt2 = ((JTextField)p.getComponent(i + 1)).getText();
                if(txt1.length() != 0 && txt2.length() != 0){
                    if(txt1.indexOf("_") != 0) str = "public _" + txt1 + " = ( " + txt2 + ";)";
                    else str = "public " + txt1 + " = (" + txt2 + ";)";
                    buf.append(str);
                }
            }
            return(new String(buf));
        }

        public String getPublicVar() {
            if(this.is_pv_apply) return this.getCurrentPublicVar();
            String txt1, txt2, str;
            final StringBuffer buf = new StringBuffer();
            for(int i = 0; i < this.name_list.size() && i < jScopeFacade.MAX_VARIABLE; i++){
                txt1 = this.name_list.elementAt(i);
                txt2 = this.expr_list.elementAt(i);
                if(txt1.length() != 0 && txt2.length() != 0){
                    if(txt1.indexOf("_") != 0) str = "public _" + txt1 + " = (" + txt2 + ");";
                    else str = "public " + txt1 + " = (" + txt2 + ");";
                    buf.append(str);
                }
            }
            return(new String(buf));
        }

        private void savePubVar() {
            String txt1, txt2;
            if(this.name_list.size() != 0) this.name_list.removeAllElements();
            if(this.expr_list.size() != 0) this.expr_list.removeAllElements();
            final Container p = this.getContentPane();
            for(int i = 2, j = 0; j < jScopeFacade.MAX_VARIABLE; i += 2, j++){
                txt1 = ((JTextField)p.getComponent(i)).getText();
                txt2 = ((JTextField)p.getComponent(i + 1)).getText();
                if(txt1.length() != 0 && txt2.length() != 0){
                    this.name_list.insertElementAt(new String(txt1), j);
                    this.expr_list.insertElementAt(new String(txt2), j);
                }
            }
            this.dw.setChange(true);
        }

        private void setPubVar() {
            final Container p = this.getContentPane();
            for(int i = 2, j = 0; j < this.name_list.size() && j < jScopeFacade.MAX_VARIABLE; i += 2, j++){
                ((JTextField)p.getComponent(i)).setText(this.name_list.elementAt(j));
                ((JTextField)p.getComponent(i + 1)).setText(this.expr_list.elementAt(j));
            }
        }

        public void toFile(final PrintWriter out, final String prompt) {
            for(int i = 0; i < this.name_list.size() && i < jScopeFacade.MAX_VARIABLE; i++){
                out.println(prompt + i + ": " + this.name_list.elementAt(i) + " = " + this.expr_list.elementAt(i));
            }
            out.println("");
        }
    }
    /**
     * Switch the between the Windows, Motif, Mac, and the Java Look and Feel
     */
    private final class ToggleUIListener implements ItemListener{
        @Override
        public void itemStateChanged(final ItemEvent e) {
            final Component root = jScopeFacade.this;
            root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            final JRadioButtonMenuItem rb = (JRadioButtonMenuItem)e.getSource();
            try{
                if(rb.isSelected() && rb.getText().equals("Windows Style Look and Feel")){
                    // currentUI = "Windows";
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(root);
                }else if(rb.isSelected() && rb.getText().equals("Macintosh Look and Feel")){
                    // currentUI = "Macintosh";
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(root);
                }else if(rb.isSelected() && rb.getText().equals("Motif Look and Feel")){
                    // currentUI = "Motif";
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(root);
                }else if(rb.isSelected() && rb.getText().equals("Java Look and Feel")){
                    // currentUI = "Metal";
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(root);
                }
                jScopeFacade.this.jScopeUpdateUI();
                /*
                                 jScope.jScopeSetUI(font_dialog);
                                 jScope.jScopeSetUI(setup_default);
                                 jScope.jScopeSetUI(color_dialog);
                                 jScope.jScopeSetUI(pub_var_diag);
                                 jScope.jScopeSetUI(server_diag);
                                 jScope.jScopeSetUI(file_diag);
                 */
            }catch(final UnsupportedLookAndFeelException exc){
                // Error - unsupported L&F
                rb.setEnabled(false);
                System.err.println("# Unsupported LookAndFeel: " + rb.getText());
                // Set L&F to JLF
                try{
                    // currentUI = "Metal";
                    jScopeFacade.this.metalMenuItem.setSelected(true);
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    SwingUtilities.updateComponentTreeUI(jScopeFacade.this);
                }catch(final Exception exc2){
                    exc2.printStackTrace();
                    System.err.println("# Could not load LookAndFeel: " + exc2);
                    exc2.printStackTrace();
                }
            }catch(final Exception exc){
                rb.setEnabled(false);
                exc.printStackTrace();
                System.err.println("# Could not load LookAndFeel: " + rb.getText());
                exc.printStackTrace();
            }
            root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    // private static final long EPICS_BASE = 631062000000L;
    // private static final long EPICS_BASE = 631148400000L;
    private static final long      EPICS_BASE       = 631152000000L;
    public static jScopeFacade     instance;
    static public boolean          is_debug         = false;
    public static final int        JAVA_TIME        = 1, VMS_TIME = 2, EPICS_TIME = 3;
    private static String          macClassName     = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
    public static final int        MAX_NUM_SHOT     = 30;
    public static final int        MAX_VARIABLE     = 10;
    private static String          metalClassName   = "javax.swing.plaf.metal.MetalLookAndFeel";
    private static String          motifClassName   = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    public static boolean          not_sup_local    = false;
    private static int             num_scope        = 0;
    private static long            refreshPeriod    = -1;
    public static DataServerItem[] server_ip_list;
    private static Object          T_message;
    private static int             T_messageType;
    private static Component       T_parentComponent;
    private static String          T_title;
    private static int             timeMode         = jScopeFacade.JAVA_TIME;
    public static final String     VERSION;
    private static final long      VMS_BASE         = 0x7c95674beb4000L;
    static String                  windowsClassName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    static{
        String builddate = "unknown";
        try{
            final Class clazz = jScopeFacade.class;
            final String className = clazz.getSimpleName() + ".class";
            final String classPath = clazz.getResource(className).toString();
            if(classPath.startsWith("jar")){
                final String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
                final Manifest manifest = new Manifest(new URL(manifestPath).openStream());
                final Attributes attr = manifest.getMainAttributes();
                builddate = attr.getValue("Built-Date").substring(0, 10);
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
        VERSION = new StringBuilder(64).append("Build-Date ").append(builddate).toString();
    }

    public static boolean busy() {
        return jScopeFacade.instance.executing_update;
    }

    public static long convertFromSpecificTime(final long inTime) {
        if(jScopeFacade.timeMode == jScopeFacade.VMS_TIME) return (inTime - jScopeFacade.VMS_BASE) / 10000L;
        else if(jScopeFacade.timeMode == jScopeFacade.EPICS_TIME){
            final long currTime = inTime / 1000000L + jScopeFacade.EPICS_BASE;
            return currTime;
        }else return inTime;
    }

    public static final long convertToSpecificTime(final long inTime) {
        if(jScopeFacade.timeMode == jScopeFacade.VMS_TIME) return(inTime * 10000L + jScopeFacade.VMS_BASE);
        else if(jScopeFacade.timeMode == jScopeFacade.EPICS_TIME) return (inTime - jScopeFacade.EPICS_BASE) * 1000000L;
        else return inTime;
    }

    public static final void displayPageFormatAttributes(final int idx, final PageFormat myPageFormat) {
        System.out.println("+----------------------------------------------------------+");
        System.out.println("Index = " + idx);
        System.out.println("Width = " + myPageFormat.getWidth());
        System.out.println("Height = " + myPageFormat.getHeight());
        System.out.println("ImageableX = " + myPageFormat.getImageableX());
        System.out.println("ImageableY = " + myPageFormat.getImageableY());
        System.out.println("ImageableWidth = " + myPageFormat.getImageableWidth());
        System.out.println("ImageableHeight = " + myPageFormat.getImageableHeight());
        final int o = myPageFormat.getOrientation();
        System.out.println("Orientation = " + (o == PageFormat.PORTRAIT ? "PORTRAIT" : o == PageFormat.LANDSCAPE ? "LANDSCAPE" : o == PageFormat.REVERSE_LANDSCAPE ? "REVERSE_LANDSCAPE" : "<invalid>"));
        System.out.println("+----------------------------------------------------------+");
    }

    public static final boolean equalsString(final String s1, final String s2) {
        if(s1 == null) return s2 == null || s2.length() == 0;
        if(s2 == null) return s1.length() == 0;
        return s1.equals(s2);
    }

    public static final String findFileInClassPath(final String file) {
        final StringTokenizer path = new StringTokenizer(System.getProperty("java.class.path"), File.pathSeparator);
        String p, f_name;
        File f;
        while(path.hasMoreTokens()){
            p = path.nextToken();
            f = new File(p);
            if(!f.isDirectory()) continue;
            f_name = p + File.separator + file;
            f = new File(f_name);
            if(f.exists()) return f_name;
        }
        return null;
    }

    public static final Rectangle getRectangle() {
        return jScopeFacade.instance.getBounds();
    }

    public static final long getRefreshPeriod() {
        return jScopeFacade.refreshPeriod;
    }

    private static final DataServerItem getServerItem(final String server) {
        for(final DataServerItem element : jScopeFacade.server_ip_list)
            if(element.equals(server)) return element;
        return null;
    }

    static int getTimeMode() {
        return jScopeFacade.timeMode;
    }

    /**
     * A utility function that layers on top of the LookAndFeel's
     * isSupportedLookAndFeel() method. Returns true if the LookAndFeel
     * is supported. Returns false if the LookAndFeel is not supported
     * and/or if there is any kind of error checking if the LookAndFeel
     * is supported.
     * The L&F menu will use this method to detemine whether the various
     * L&F options should be active or inactive.
     */
    @SuppressWarnings("rawtypes")
    protected static final boolean isAvailableLookAndFeel(final String classname) {
        try{ // Try to create a L&F given a String
            final Class lnfClass = Class.forName(classname);
            final LookAndFeel newLAF = (LookAndFeel)(lnfClass.newInstance());
            return newLAF.isSupportedLookAndFeel();
        }catch(final Exception e){ // If ANYTHING weird happens, return false
            return false;
        }
    }

    /**********************
     * jScope Main
     ***********************/
    private static final boolean isNewJVMVersion() {
        final String ver = System.getProperty("java.version");
        return(!(ver.indexOf("1.0") != -1 || ver.indexOf("1.1") != -1));
    }

    protected static void jScopeSetUI(final Component c) {
        if(c != null) SwingUtilities.updateComponentTreeUI(c);
    }

    public static void main(final String args[]) {
        jScopeFacade.startApplication(args);
    }

    public static void showMessage(final Component parentComponent, final Object message, final String title, final int messageType) {
        jScopeFacade.T_parentComponent = parentComponent;
        jScopeFacade.T_message = message;
        jScopeFacade.T_title = title;
        jScopeFacade.T_messageType = messageType;
        // do the following on the gui thread
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                JOptionPane.showMessageDialog(jScopeFacade.T_parentComponent, jScopeFacade.T_message, jScopeFacade.T_title, jScopeFacade.T_messageType);
            }
        });
    }

    public static void startApplication(final String args[]) {
        String file = null;
        String propertiesFile = null;
        if(args.length != 0){
            for(int i = 0; i < args.length; i++){
                if(args[i].equals("-fp")){
                    if(i + 1 < args.length) propertiesFile = args[i + 1];
                    i++;
                }else{
                    file = new String(args[i]);
                }
            }
        }
        if(jScopeFacade.isNewJVMVersion()) jScopeFacade.instance = new jScopeFacade(100, 100, propertiesFile);
        else{
            System.out.println("jScope application required JDK version 1.2 or later");
            System.exit(1);
        }
        jScopeFacade.instance.pack();
        jScopeFacade.instance.setSize(750, 550);
        jScopeFacade.num_scope++;
        jScopeFacade.instance.startScope(file);
    }
    JWindow                   aboutScreen;
    /** Menu item on menu autoscale_m */
    private JMenuItem         all_i, allY_i;
    private JButton           apply_b;
    // PageFormat pageFormat;
    PrintRequestAttributeSet  attrs;
    private JCheckBoxMenuItem brief_error_i;
    public ColorDialog        color_dialog;
    public ColorMapDialog     colorMapDialog;
    private String            config_file;
    protected String          curr_directory;
    jScopeDefaultValues       def_values         = new jScopeDefaultValues();
    protected JMenuItem       default_i, use_i, pub_variables_i, save_as_i, use_last_i, save_i, color_i, print_all_i, open_i, close_i, server_list_i, font_i, save_all_as_text_i, free_cache_i;
    int                       default_server_idx;
    /** Menus on menu bar */
    protected JMenu           edit_m, look_and_feel_m, pointer_mode_m, customize_m, autoscale_m, network_m, help_m;
    private boolean           executing_update   = false;
    /** Menu items on menu edit_m */
    private JMenuItem         exit_i, win_i;
    private JFileChooser      file_diag;
    public FontSelection      font_dialog;
    int                       height             = 500, width = 700, xpos = 50, ypos = 50;
    private jScopeBrowseUrl   help_dialog;
    BasicArrowButton          incShot, decShot;
    int                       incShotValue       = 0;
    private JTextField        info_text, net_text;
    boolean                   is_playing         = false;
    Properties                js_prop            = null;
    protected String          last_directory;
    // L&F radio buttons
    JRadioButtonMenuItem      macMenuItem;
    /** Main menu bar */
    protected JMenuBar        mb;
    JRadioButtonMenuItem      metalMenuItem;
    private boolean           modified           = false;
    JRadioButtonMenuItem      motifMenuItem;
    private JPanel            panel, panel1;
    private JLabel            point_pos;
    private final ButtonGroup pointer_mode       = new ButtonGroup();
    private JMenuItem         print_i, properties_i;
    PrintService              printerSelection;
    PrintService[]            printersServices;
    DocPrintJob               prnJob             = null;
    JProgressBar              progress_bar;
    private String            propertiesFilePath = null;
    private PubVarDialog      pub_var_diag;
    ServerDialog              server_diag;
    JMenu                     servers_m, updates_m;
    SetupDefaults             setup_default;
    SetupDataDialog           setup_dialog;
    private JLabel            shot_l;
    private JTextField        shot_t, signal_expr;
    private JCheckBoxMenuItem update_i, update_when_icon_i;
    jScopeWaveContainer       wave_panel;
    private WindowDialog      win_diag;
    JRadioButtonMenuItem      windowsMenuItem;
    private JRadioButton      zoom, point, copy, pan;
    /** Menu item on menu pointer_mode_m */
    private JMenuItem         zoom_i, point_i, copy_i, pan_i;

    public jScopeFacade(final int spos_x, final int spos_y, final String propFile){
        if(jScopeFacade.num_scope == 0){
            this.createAboutScreen();
            // do the following on the gui thread
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    jScopeFacade.this.showAboutScreen();
                }
            });
        }
        this.setPropertiesFile(propFile);
        this.jScopeCreate(spos_x, spos_y);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object ob = e.getSource();
        String action_cmd = null;
        if(ob != this.open_i) this.wave_panel.removeSelection();
        if(ob instanceof AbstractButton) action_cmd = ((AbstractButton)ob).getModel().getActionCommand();
        if(action_cmd != null){
            final StringTokenizer act = new StringTokenizer(action_cmd);
            final String action = act.nextToken();
            if(action.equals("SET_SERVER")){
                final String value = action_cmd.substring(action.length() + 1);
                if(!this.wave_panel.getServerLabel().equals(value)) this.setDataServer(jScopeFacade.getServerItem(value));
            }
        }
        if(ob == this.signal_expr){
            final String sig = this.signal_expr.getText().trim();
            if(sig != null && sig.length() != 0){
                this.setMainShot();
                this.wave_panel.addSignal(sig, false);
                this.setChange(true);
            }
        }
        if(ob == this.apply_b || ob == this.shot_t){
            this.incShotValue = 0;
            if(this.executing_update){
                if(ob == this.apply_b) this.wave_panel.abortUpdate();
            }else{
                if(ob == this.shot_t){
                    this.setMainShot();
                }
                final String sig = this.signal_expr.getText().trim();
                if(sig != null && sig.length() != 0){
                    this.wave_panel.addSignal(sig, true);
                    this.setChange(true);
                }
                this.updateAllWaves();
            }
        }
        if(ob == this.color_i){
            final javax.swing.Timer t = new javax.swing.Timer(20, new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent ae) {
                    jScopeFacade.this.color_dialog.showColorDialog(jScopeFacade.this.wave_panel);
                }
            });
            t.setRepeats(false);
            t.start();
        }
        if(ob == this.font_i){
            this.font_dialog.setLocationRelativeTo(this);
            final javax.swing.Timer t = new javax.swing.Timer(20, new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent ae) {
                    jScopeFacade.this.font_dialog.setVisible(true);
                }
            });
            t.setRepeats(false);
            t.start();
        }
        if(ob == this.default_i){
            final javax.swing.Timer t = new javax.swing.Timer(20, new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent ae) {
                    jScopeFacade.this.setup_default.show(jScopeFacade.this, jScopeFacade.this.def_values);
                }
            });
            t.setRepeats(false);
            t.start();
        }
        if(ob == this.win_i){
            if(this.win_diag == null) this.win_diag = new WindowDialog(this, "Window");
            final javax.swing.Timer t = new javax.swing.Timer(20, new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent ae) {
                    final boolean returnFlag = jScopeFacade.this.win_diag.ShowWindowDialog();
                    if(returnFlag){
                        jScopeFacade.this.wave_panel.resetDrawPanel(jScopeFacade.this.win_diag.out_row);
                        // wave_panel.update();
                        jScopeFacade.this.updateColors();
                        jScopeFacade.this.updateFont();
                        jScopeFacade.this.setChange(true);
                    }
                }
            });
            t.setRepeats(false);
            t.start();
        }
        if(ob == this.use_last_i){
            if(this.last_directory != null && this.last_directory.trim().length() != 0){
                this.curr_directory = this.last_directory;
                this.config_file = this.curr_directory;
                this.setChange(false);
                this.loadConfiguration();
            }
        }
        if(ob == this.use_i){
            this.loadConfigurationFrom();
        }
        if(ob == this.save_i){
            this.saveConfiguration(this.config_file);
        }
        if(ob == this.save_as_i){
            this.saveAs();
        }
        if(ob == this.save_all_as_text_i){
            this.wave_panel.saveAsText(null, true);
        }
        if(ob == this.exit_i){
            if(jScopeFacade.num_scope > 1){
                final Object[] options = {"Close this", "Close all", "Cancel"};
                final int opt = JOptionPane.showOptionDialog(this, "Close all open scopes?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                switch(opt){
                    case JOptionPane.YES_OPTION:
                        this.closeScope();
                        break;
                    case JOptionPane.NO_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        break;
                }
            }else{
                this.closeScope();
                if(jScopeFacade.num_scope == 0) System.exit(0);
                // System.gc();
            }
        }
        if(ob == this.close_i && jScopeFacade.num_scope != 1){
            this.closeScope();
        }
        if(ob == this.open_i){
            jScopeFacade.num_scope++;
            final Rectangle r = this.getBounds();
            final jScopeFacade new_scope = this.buildNewScope(r.x + 5, r.y + 40);
            new_scope.wave_panel.setCopySource(this.wave_panel.getCopySource());
            new_scope.startScope(null);
        }
        if(ob == this.all_i){
            this.wave_panel.autoscaleAll();
        }
        if(ob == this.allY_i){
            this.wave_panel.autoscaleAllY();
        }
        if(ob == this.copy_i){
            this.wave_panel.setMode(Waveform.MODE_COPY);
            this.copy.getModel().setSelected(true);
        }
        if(ob == this.zoom_i){
            this.wave_panel.setMode(Waveform.MODE_ZOOM);
            this.zoom.getModel().setSelected(true);
        }
        if(ob == this.point_i){
            this.wave_panel.setMode(Waveform.MODE_POINT);
            this.point.getModel().setSelected(true);
        }
        if(ob == this.pan_i){
            this.wave_panel.setMode(Waveform.MODE_PAN);
            this.pan.getModel().setSelected(true);
        }
        if(ob == this.server_list_i){
            final javax.swing.Timer t = new javax.swing.Timer(20, new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent ae) {
                    jScopeFacade.this.server_diag.Show();
                    jScopeFacade.server_ip_list = jScopeFacade.this.server_diag.getServerIpList();
                }
            });
            t.setRepeats(false);
            t.start();
        }
        if(ob == this.pub_variables_i){
            final javax.swing.Timer t = new javax.swing.Timer(20, new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent ae) {// show
                    jScopeFacade.this.pub_var_diag.is_pv_apply = true;
                    jScopeFacade.this.pub_var_diag.setPubVar();
                    jScopeFacade.this.pub_var_diag.pack();
                    jScopeFacade.this.pub_var_diag.setLocationRelativeTo(jScopeFacade.this.pub_var_diag.dw);
                    jScopeFacade.this.pub_var_diag.setVisible(true);
                }
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private final void arrowsIncDecShot() {
        int idx;
        String sh = this.shot_t.getText();
        if((idx = sh.lastIndexOf("+")) > 1 || (idx = sh.lastIndexOf("-")) > 1) sh = sh.substring(0, idx).trim();
        if(this.incShotValue != 0) this.shot_t.setText(sh + (this.incShotValue > 0 ? " + " : " - ") + Math.abs(this.incShotValue));
        else this.shot_t.setText(sh);
    }

    public final boolean briefError() {
        return this.brief_error_i.getState();
    }

    protected final jScopeFacade buildNewScope(final int x, final int y) {
        return new jScopeFacade(x, y, this.propertiesFilePath);
    }

    protected final jScopeWaveContainer buildWaveContainer() {
        final int rows[] = {1, 0, 0, 0};
        return(new jScopeWaveContainer(rows, this.def_values));
    }

    public final void closeScope() {
        if(this.isChange()){
            switch(this.saveWarning()){
                case JOptionPane.YES_OPTION:
                    if(this.config_file == null) this.saveAs();
                    else this.saveConfiguration(this.config_file);
                    break;
                case JOptionPane.NO_OPTION:
                    // exitScope();
                    break;
            }
        }
        this.exitScope();
    }

    /**
     * Show the splash screen while the rest of the demo loads
     */
    public void createAboutScreen() {
        final JLabel aboutLabel = new AboutWindow();
        this.aboutScreen = new JWindow();
        this.aboutScreen.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(final MouseEvent e) {
                jScopeFacade.this.hideAbout();
            }
        });
        this.aboutScreen.getContentPane().add(aboutLabel);
        this.aboutScreen.pack();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.aboutScreen.setLocation(screenSize.width / 2 - this.aboutScreen.getSize().width / 2, screenSize.height / 2 - this.aboutScreen.getSize().height / 2);
    }

    private void createHistoryFile(final File f) {
        int idx = 0, maxIdx = 0;
        int maxHistory = 2;
        final String config_file_history = this.js_prop.getProperty("jScope.config_file_history_length");
        try{
            maxHistory = Integer.parseInt(config_file_history);
        }catch(final Exception exc){};
        final File pf = f.getParentFile();
        final String list[] = pf.list(new FileFilter(f.getName()));
        for(final String element : list){
            final StringTokenizer st = new StringTokenizer(element, ";");
            try{
                String s = st.nextToken();
                s = st.nextToken();
                if(s != null){
                    idx = Integer.parseInt(s);
                    if(idx > maxIdx) maxIdx = idx;
                }
            }catch(final Exception exc){};
        }
        maxIdx++;
        final String f_ap = f.getAbsolutePath();
        if(maxIdx > maxHistory){
            final File fd = new File(f_ap + ";" + (maxIdx - maxHistory));
            fd.delete();
        }
        final File fr = new File(f_ap + ";" + maxIdx);
        f.renameTo(fr);
    }

    private boolean eventUpdateEnabled() {
        if(this.update_i.getState()){
            this.setStatusLabel("Disable event update");
            return false;
        }
        if(this.getExtendedState() == Frame.ICONIFIED && this.update_when_icon_i.getState()){
            this.setStatusLabel("Event update is disabled when iconified");
            return false;
        }
        return true;
    }

    private void exitScope() {
        try{
            this.wave_panel.removeAllEvents(this);
        }catch(final IOException e){}
        this.dispose();
        jScopeFacade.num_scope--;
        // System.gc();
    }

    public void fromFile(final Properties pr) throws IOException {
        String prop = "";
        try{
            if((prop = pr.getProperty("Scope.update.disable")) != null){
                final boolean b = new Boolean(prop).booleanValue();
                this.update_i.setState(b);
            }
            if((prop = pr.getProperty("Scope.update.disable_when_icon")) != null){
                final boolean b = new Boolean(prop).booleanValue();
                this.update_when_icon_i.setState(b);
            }
            if((prop = pr.getProperty("Scope.geometry")) != null){
                final StringTokenizer st = new StringTokenizer(prop);
                this.width = new Integer(st.nextToken("x")).intValue();
                this.height = new Integer(st.nextToken("x+")).intValue();
                this.xpos = new Integer(st.nextToken("+")).intValue();
                this.ypos = new Integer(st.nextToken("+")).intValue();
                final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                if(this.height > screenSize.height) this.height = screenSize.height;
                if(this.width > screenSize.width) this.width = screenSize.width;
                if(this.ypos + this.height > screenSize.height) this.ypos = screenSize.height - this.height;
                if(this.xpos + this.width > screenSize.width) this.xpos = screenSize.width - this.width;
            }
        }catch(final Exception exc){
            throw(new IOException(prop));
        }
    }

    protected void getPropertiesValue() {
        if(this.js_prop == null) return;
        // jScope configurations file directory can be defined
        // with decrease priority order:
        // 1) by system property jScope.config_directory;
        // in this case jScope must be started with
        // -DjScope.config_directory=<directory> option.
        // 2) in jScope.properties using jScope.directory property
        // If the previous properties are not defined jScope create
        // configuration folder in <home directory>/jScope/configurations, if
        // for some abnormal reason the directory creation failed
        // <home directory> is used as configuration directory
        this.curr_directory = System.getProperty("jScope.config_directory");
        if(this.curr_directory == null || this.curr_directory.trim().length() == 0){
            this.curr_directory = this.js_prop.getProperty("jScope.directory");
            if(this.curr_directory == null || this.curr_directory.trim().length() == 0){
                // Store default jScope configuration file in local
                // directory <home directory>/jScope/configurations.
                // Default configuration are stored in jScope jar file.
                // If configuration directory already exist the configurations
                // copy is not performed.
                try{
                    this.curr_directory = System.getProperty("user.home") + File.separator + "jScope" + File.separator + "configurations" + File.separator;
                    final File jScopeUserDir = new File(this.curr_directory);
                    if(!jScopeUserDir.exists()){
                        final byte b[] = new byte[1024];
                        jScopeUserDir.mkdirs();
                        final String configList[] = {}; // "FTU_plasma_current.jscp", "fusion.jscp", "JET_plasma_current.jscp", "RFX_plasma_current.jscp", "TS_plasma_current.jscp", "TWU_plasma_current.jscp"};
                        for(final String element : configList){
                            final InputStream fis = this.getClass().getClassLoader().getResourceAsStream("configurations/" + element);
                            final FileOutputStream fos = new FileOutputStream(this.curr_directory + element);
                            for(int len = fis.read(b); len > 0; len = fis.read(b))
                                fos.write(b, 0, len);
                            fos.close();
                            fis.close();
                        }
                    }
                }catch(final Exception exc){
                    this.curr_directory = System.getProperty("user.home");
                }
            }
        }
        this.default_server_idx = -1;
        String prop = this.js_prop.getProperty("jScope.default_server");
        if(prop != null){
            try{
                this.default_server_idx = Integer.parseInt(prop) - 1;
            }catch(final NumberFormatException e){}
        }
        final String cache_directory = this.js_prop.getProperty("jScope.cache_directory");
        final String cache_size = this.js_prop.getProperty("jScope.cache_size");
        final String f_name = this.js_prop.getProperty("jScope.save_selected_points");
        final String proxy_host = this.js_prop.getProperty("jScope.http_proxy_host");
        final String proxy_port = this.js_prop.getProperty("jScope.http_proxy_port");
        prop = this.js_prop.getProperty("jScope.vertical_offset");
        int val = 0;
        if(prop != null){
            try{
                val = Integer.parseInt(prop);
            }catch(final NumberFormatException e){}
            Waveform.setVerticalOffset(val);
        }
        val = 0;
        prop = this.js_prop.getProperty("jScope.horizontal_offset");
        if(prop != null){
            try{
                val = Integer.parseInt(prop);
            }catch(final NumberFormatException e){}
            Waveform.setHorizontalOffset(val);
        }
        final Properties p = System.getProperties();
        if(cache_directory != null) p.put("Signal.cache_directory", cache_directory);
        if(cache_size != null) p.put("Signal.cache_size", cache_size);
        if(f_name != null) p.put("jScope.save_selected_points", f_name);
        if(this.curr_directory != null) p.put("jScope.curr_directory", this.curr_directory);
        if(proxy_port != null && proxy_host != null){
            p.setProperty("http.proxyHost", proxy_host);
            p.setProperty("http.proxyPort", proxy_port);
        }
        final String timeConversion = this.js_prop.getProperty("jScope.time_format");
        if(timeConversion != null){
            if(timeConversion.toUpperCase().equals("VMS")) jScopeFacade.timeMode = jScopeFacade.VMS_TIME;
            else if(timeConversion.toUpperCase().equals("EPICS")) jScopeFacade.timeMode = jScopeFacade.EPICS_TIME;
            // Add here new time formats
        }
        final String refreshPeriodStr = this.js_prop.getProperty("jScope.refresh_period");
        if(refreshPeriodStr != null){
            try{
                jScopeFacade.refreshPeriod = Integer.parseInt(refreshPeriodStr);
                // Not shorted than 0.5 s
                if(jScopeFacade.refreshPeriod < 500) jScopeFacade.refreshPeriod = 500;
            }catch(final Exception exc){
                jScopeFacade.refreshPeriod = -1;
            }
        }
    }

    /**
     * pop down the splash screen
     */
    public void hideAbout() {
        this.aboutScreen.setVisible(false);
        this.aboutScreen.dispose();
        this.aboutScreen = null;
    }

    /*
    private static boolean IsIpAddress(String addr) {
        return(addr.trim().indexOf(".") != -1 && addr.trim().indexOf(" ") == -1);
    }
     */
    private void initDataServer() {
        if(DEBUG.M) System.out.println("InitDataServer()");
        String ip_addr = null;
        String dp_class = null;
        DataServerItem srv_item = null;
        final Properties props = System.getProperties();
        ip_addr = props.getProperty("data.address");
        dp_class = props.getProperty("data.class");
        this.server_diag = new ServerDialog(this, "Server list");
        if(ip_addr != null && dp_class != null) // || is_local == null || (is_local != null && is_local.equals("no")))
        {
            srv_item = new DataServerItem(ip_addr, ip_addr, null, dp_class, null, null, null, false);
            // Add server to the server list and if present browse class and
            // url browse signal set it into srv_item
            this.server_diag.addServerIp(srv_item);
        }
        if(srv_item == null || !this.setDataServer(srv_item)){
            srv_item = jScopeWaveContainer.dataServerFromClient(srv_item);
            if(srv_item == null || !this.setDataServer(srv_item)){
                if(jScopeFacade.server_ip_list != null && this.default_server_idx >= 0 && this.default_server_idx < jScopeFacade.server_ip_list.length){
                    try{
                        srv_item = jScopeFacade.server_ip_list[this.default_server_idx];
                        this.setDataServer(srv_item);
                    }catch(final Exception exc){
                        this.setDataServerLabel();
                        exc.printStackTrace();
                    }
                }else this.setDataServerLabel();
            }
        }
    }

    public void initProperties() {
        String f_name = this.propertiesFilePath;
        if(f_name == null){
            f_name = System.getProperty("user.home") + File.separator + "jScope" + File.separator + "jScope.properties";
        }
        try{
            if(jScopeFacade.is_debug){
                System.out.println("jScope.properties " + System.getProperty("jScope.properties"));
                System.out.println("jScope.config_directory " + System.getProperty("jScope.config_directory"));
            }
            if(((new File(f_name)).exists()) || (f_name = System.getProperty("jScope.properties")) != null){
                this.js_prop = new Properties();
                this.js_prop.load(new FileInputStream(f_name));
            }else{
                f_name = System.getProperty("user.home") + File.separator + "jScope" + File.separator;
                final File jScopeUserDir = new File(f_name);
                if(!jScopeUserDir.exists()) jScopeUserDir.mkdirs();
                f_name = f_name + "jScope.properties";
                this.js_prop = new Properties();
                InputStream pis = this.getClass().getClassLoader().getResourceAsStream("jScope.properties");
                if(pis != null){
                    this.js_prop.load(pis);
                    pis.close();
                    pis = this.getClass().getClassLoader().getResourceAsStream("jScope.properties");
                    f_name = System.getProperty("user.home") + File.separator + "jScope" + File.separator + "jScope.properties";
                    final FileOutputStream fos = new FileOutputStream(f_name);
                    final byte b[] = new byte[1024];
                    for(int len = pis.read(b); len > 0; len = pis.read(b))
                        fos.write(b, 0, len);
                    fos.close();
                    pis.close();
                }else{
                    System.err.println("Not found jScope.properties file");
                }
            }
            this.propertiesFilePath = new String(f_name);
            f_name = System.getProperty("user.home") + File.separator + "jScope" + File.separator + "jScope_servers.conf";
            if(!(new File(f_name)).exists()){
                final InputStream pis = this.getClass().getClassLoader().getResourceAsStream("jScope_servers.conf");
                if(pis != null){
                    final FileOutputStream fos = new FileOutputStream(f_name);
                    final byte b[] = new byte[1024];
                    for(int len = pis.read(b); len > 0; len = pis.read(b))
                        fos.write(b, 0, len);
                    fos.close();
                    pis.close();
                }
            }
        }catch(final FileNotFoundException e){
            System.err.println(e);
        }catch(final IOException e){
            System.err.println(e);
        }
    }

    public void invalidateDefaults() {
        this.wave_panel.invalidateDefaults();
    }

    public boolean isChange() {
        return this.modified;
    }

    public boolean isShotDefinedXX() {
        String s = this.shot_t.getText();
        if(s != null && s.trim().length() > 0) return true;
        s = this.def_values.shot_str;
        if(s != null && s.trim().length() > 0){
            this.shot_t.setText(s);
            return true;
        }
        return false;
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        final Object ob = e.getSource();
        if(ob == this.brief_error_i) WaveInterface.brief_error = this.brief_error_i.getState();
        if(e.getStateChange() != ItemEvent.SELECTED) return;
        if(ob == this.copy) this.wave_panel.setMode(Waveform.MODE_COPY);
        if(ob == this.zoom) this.wave_panel.setMode(Waveform.MODE_ZOOM);
        if(ob == this.point) this.wave_panel.setMode(Waveform.MODE_POINT);
        if(ob == this.pan) this.wave_panel.setMode(Waveform.MODE_PAN);
    }

    public void jScopeCreate(final int spos_x, final int spos_y) {
        this.printersServices = PrintServiceLookup.lookupPrintServices(null, null);
        this.printerSelection = PrintServiceLookup.lookupDefaultPrintService();
        this.attrs = new HashPrintRequestAttributeSet();
        final PrinterResolution res = new PrinterResolution(600, 600, ResolutionSyntax.DPI);
        this.attrs.add(MediaSizeName.ISO_A4);
        this.attrs.add(OrientationRequested.LANDSCAPE);
        this.attrs.add(new MediaPrintableArea(5, 5, MediaSize.ISO.A4.getX(Size2DSyntax.MM) - 5, MediaSize.ISO.A4.getY(Size2DSyntax.MM) - 5, MediaPrintableArea.MM));
        this.attrs.add(res);
        if(this.printerSelection != null) this.prnJob = this.printerSelection.createPrintJob();
        this.setBounds(spos_x, spos_y, 750, 550);
        this.initProperties();
        this.getPropertiesValue();
        this.font_dialog = new FontSelection(this, "Waveform Font Selection");
        this.setup_default = new SetupDefaults(this, "Default Setup", this.def_values);
        this.color_dialog = new ColorDialog(this, "Color Configuration Dialog");
        this.colorMapDialog = new ColorMapDialog(this, this.js_prop.getProperty("jScope.color_palette_file"));
        this.pub_var_diag = new PubVarDialog(this);
        this.getContentPane().setLayout(new BorderLayout());
        this.setBackground(Color.lightGray);
        this.addWindowListener(this);
        this.file_diag = new JFileChooser();
        this.file_diag.addChoosableFileFilter(new javax.swing.filechooser.FileFilter(){
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".dat");
            }

            @Override
            public String getDescription() {
                return ".dat files";
            }
        });
        this.file_diag.addChoosableFileFilter(new javax.swing.filechooser.FileFilter(){
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jscp");
            }

            @Override
            public String getDescription() {
                return ".jscp files";
            }
        });
        this.mb = new JMenuBar();
        this.setJMenuBar(this.mb);
        this.edit_m = new JMenu("File");
        this.mb.add(this.edit_m);
        final JMenuItem browse_signals_i = new JMenuItem("Browse signals");
        this.edit_m.add(browse_signals_i);
        browse_signals_i.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                jScopeFacade.this.wave_panel.showBrowseSignals();
            }
        });
        this.open_i = new JMenuItem("New Window");
        this.open_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        this.edit_m.add(this.open_i);
        this.open_i.addActionListener(this);
        this.look_and_feel_m = new JMenu("Look & Feel");
        this.edit_m.add(this.look_and_feel_m);
        final JMenuItem sign = new JMenuItem("History...");
        sign.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final SignalsBoxDialog sig_box_diag = new SignalsBoxDialog(jScopeFacade.this, "Visited Signals", false);
                sig_box_diag.setVisible(true);
            }
        });
        this.edit_m.add(sign);
        this.edit_m.addSeparator();
        // Copy image to clipborad can be done only with
        // java release 1.4
        // if(AboutWindow.javaVersion.indexOf("1.4") != -1)
        {
            final JMenuItem cb_copy = new JMenuItem("Copy to Clipboard");
            cb_copy.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final Dimension dim = jScopeFacade.this.wave_panel.getSize();
                    final BufferedImage ri = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
                    final Graphics2D g2d = (Graphics2D)ri.getGraphics();
                    g2d.setBackground(Color.white);
                    jScopeFacade.this.wave_panel.printAll(g2d, dim.height, dim.width);
                    try{
                        final ImageTransferable imageTransferable = new ImageTransferable(ri);
                        final Clipboard cli = Toolkit.getDefaultToolkit().getSystemClipboard();
                        cli.setContents(imageTransferable, imageTransferable);
                    }catch(final Exception exc){
                        System.out.println("Exception " + exc);
                    }
                }
            });
            this.edit_m.add(cb_copy);
            this.edit_m.addSeparator();
        }
        this.print_i = new JMenuItem("Print Setup ...");
        this.print_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        this.print_i.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Thread print_cnf = new Thread(){
                    @Override
                    public void run() {
                        this.setName("Print Dialog Thread");
                        final PrintService svc = PrintServiceLookup.lookupDefaultPrintService();
                        jScopeFacade.this.printerSelection = ServiceUI.printDialog(jScopeFacade.this.getGraphicsConfiguration(), 100, 100, jScopeFacade.this.printersServices, svc, null, jScopeFacade.this.attrs);
                        if(jScopeFacade.this.printerSelection != null){
                            System.out.println(jScopeFacade.this.printerSelection.getName() + " |||| " + jScopeFacade.this.printerSelection.getSupportedDocFlavors() + " |||| " + jScopeFacade.this.printerSelection.hashCode());
                            jScopeFacade.this.prnJob = jScopeFacade.this.printerSelection.createPrintJob();
                            jScopeFacade.this.printAllWaves(jScopeFacade.this.attrs);
                            /*
                            try
                            {
                                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
                                Doc doc = new SimpleDoc(jScope.this, flavor, null);
                                prnJob.print(doc, attrs);
                            }
                            catch(Exception exc)
                            {
                                System.out.println(exc);
                            }
                             */
                        }
                    }
                };
                print_cnf.start();
            }
        });
        this.edit_m.add(this.print_i);
        /*****************************************************************************************
         * page_i = new JMenuItem("Page Setup ...");
         * page_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
         * page_i.addActionListener(new ActionListener()
         * {
         * public void actionPerformed(ActionEvent e)
         * {
         * Thread page_cnf = new Thread()
         * {
         * public void run()
         * {
         * setName("Page  Dialog Thread");
         * //pageFormat = prnJob.pageDialog(pageFormat);
         * //prnJob.validatePage(pageFormat);
         * //displayPageFormatAttributes(pageFormat);
         * }
         * };
         * page_cnf.start();
         * }
         * });
         * edit_m.add(page_i);
         *********************************************************************************************/
        this.print_all_i = new JMenuItem("Print");
        this.print_all_i.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Thread print_page = new Thread(){
                    @Override
                    public void run() {
                        this.setName("Print All Thread");
                        jScopeFacade.this.printAllWaves(jScopeFacade.this.attrs);
                    }
                };
                print_page.start();
            }
        });
        this.edit_m.add(this.print_all_i);
        this.edit_m.addSeparator();
        this.properties_i = new JMenuItem("Properties...");
        this.properties_i.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final PropertiesEditor pe = new PropertiesEditor(jScopeFacade.this, jScopeFacade.this.propertiesFilePath);
                pe.setVisible(true);
            }
        });
        this.edit_m.add(this.properties_i);
        this.edit_m.addSeparator();
        this.close_i = new JMenuItem("Close");
        this.close_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        this.edit_m.add(this.close_i);
        this.close_i.addActionListener(this);
        this.exit_i = new JMenuItem("Exit");
        this.exit_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        this.edit_m.add(this.exit_i);
        this.exit_i.addActionListener(this);
        // Look and Feel Radio control
        final ButtonGroup group = new ButtonGroup();
        final ToggleUIListener toggleUIListener = new ToggleUIListener();
        this.metalMenuItem = (JRadioButtonMenuItem)this.look_and_feel_m.add(new JRadioButtonMenuItem("Java Look and Feel"));
        this.metalMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("Metal"));
        this.metalMenuItem.setSelected(true);
        this.metalMenuItem.setEnabled(jScopeFacade.isAvailableLookAndFeel(jScopeFacade.metalClassName));
        group.add(this.metalMenuItem);
        this.metalMenuItem.addItemListener(toggleUIListener);
        // metalMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        this.motifMenuItem = (JRadioButtonMenuItem)this.look_and_feel_m.add(new JRadioButtonMenuItem("Motif Look and Feel"));
        this.motifMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("CDE/Motif"));
        this.motifMenuItem.setEnabled(jScopeFacade.isAvailableLookAndFeel(jScopeFacade.motifClassName));
        group.add(this.motifMenuItem);
        this.motifMenuItem.addItemListener(toggleUIListener);
        // motifMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        this.windowsMenuItem = (JRadioButtonMenuItem)this.look_and_feel_m.add(new JRadioButtonMenuItem("Windows Style Look and Feel"));
        this.windowsMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("Windows"));
        this.windowsMenuItem.setEnabled(jScopeFacade.isAvailableLookAndFeel(jScopeFacade.windowsClassName));
        group.add(this.windowsMenuItem);
        this.windowsMenuItem.addItemListener(toggleUIListener);
        // windowsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
        this.macMenuItem = (JRadioButtonMenuItem)this.look_and_feel_m.add(new JRadioButtonMenuItem("Macintosh Look and Feel"));
        this.macMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("Macintosh"));
        this.macMenuItem.setEnabled(jScopeFacade.isAvailableLookAndFeel(jScopeFacade.macClassName));
        group.add(this.macMenuItem);
        this.macMenuItem.addItemListener(toggleUIListener);
        // macMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
        this.pointer_mode_m = new JMenu("Pointer mode");
        this.mb.add(this.pointer_mode_m);
        this.point_i = new JMenuItem("Point");
        this.point_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        this.point_i.addActionListener(this);
        this.pointer_mode_m.add(this.point_i);
        this.zoom_i = new JMenuItem("Zoom");
        this.zoom_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        this.pointer_mode_m.add(this.zoom_i);
        this.zoom_i.addActionListener(this);
        this.pan_i = new JMenuItem("Pan");
        this.pan_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
        this.pointer_mode_m.add(this.pan_i);
        this.pan_i.addActionListener(this);
        this.copy_i = new JMenuItem("Copy");
        this.copy_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
        this.pointer_mode_m.add(this.copy_i);
        this.copy_i.addActionListener(this);
        this.pointer_mode_m.add(this.copy_i);
        this.customize_m = new JMenu("Customize");
        this.mb.add(this.customize_m);
        this.default_i = new JMenuItem("Global Settings ...");
        this.default_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        this.customize_m.add(this.default_i);
        this.default_i.addActionListener(this);
        this.win_i = new JMenuItem("Window ...");
        this.win_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        this.win_i.addActionListener(this);
        this.customize_m.add(this.win_i);
        this.font_i = new JMenuItem("Font selection ...");
        this.font_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        this.font_i.addActionListener(this);
        this.customize_m.add(this.font_i);
        this.color_i = new JMenuItem("Colors List ...");
        this.color_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        this.color_i.addActionListener(this);
        this.customize_m.add(this.color_i);
        this.pub_variables_i = new JMenuItem("Public variables ...");
        this.pub_variables_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        this.pub_variables_i.addActionListener(this);
        this.customize_m.add(this.pub_variables_i);
        this.brief_error_i = new JCheckBoxMenuItem("Brief Error", true);
        this.brief_error_i.addItemListener(this);
        this.customize_m.add(this.brief_error_i);
        this.customize_m.add(new JSeparator());
        this.use_last_i = new JMenuItem("Use last saved settings");
        this.use_last_i.addActionListener(this);
        this.use_last_i.setEnabled(false);
        this.customize_m.add(this.use_last_i);
        this.use_i = new JMenuItem("Use saved settings from ...");
        this.use_i.addActionListener(this);
        this.customize_m.add(this.use_i);
        this.customize_m.add(new JSeparator());
        this.save_i = new JMenuItem("Save current settings");
        this.save_i.setEnabled(false);
        this.save_i.addActionListener(this);
        this.customize_m.add(this.save_i);
        this.save_as_i = new JMenuItem("Save current settings as ...");
        this.customize_m.add(this.save_as_i);
        this.save_as_i.addActionListener(this);
        this.customize_m.add(new JSeparator());
        this.save_all_as_text_i = new JMenuItem("Save all as text ...");
        this.save_all_as_text_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        this.customize_m.add(this.save_all_as_text_i);
        this.save_all_as_text_i.addActionListener(this);
        this.updates_m = new JMenu("Updates");
        this.mb.add(this.updates_m);
        this.update_i = new JCheckBoxMenuItem("Disable", false);
        this.update_when_icon_i = new JCheckBoxMenuItem("Disable when icon", true);
        this.updates_m.add(this.update_i);
        this.updates_m.add(this.update_when_icon_i);
        this.autoscale_m = new JMenu("Autoscale");
        this.mb.add(this.autoscale_m);
        this.all_i = new JMenuItem("All");
        this.all_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
        this.all_i.addActionListener(this);
        this.autoscale_m.add(this.all_i);
        this.allY_i = new JMenuItem("All Y");
        this.allY_i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        this.autoscale_m.add(this.allY_i);
        this.allY_i.addActionListener(this);
        this.network_m = new JMenu("Network");
        this.mb.add(this.network_m);
        this.servers_m = new JMenu("Servers");
        this.network_m.add(this.servers_m);
        // servers_m.addActionListener(this);
        this.servers_m.addMenuListener(new MenuListener(){
            @Override
            public void menuCanceled(final MenuEvent e) {}

            @Override
            public void menuDeselected(final MenuEvent e) {}

            @Override
            public void menuSelected(final MenuEvent e) {
                jScopeFacade.this.server_diag.addServerIpList(jScopeFacade.server_ip_list);
            }
        });
        this.server_list_i = new JMenuItem("Edit server list ...");
        this.network_m.add(this.server_list_i);
        this.server_list_i.addActionListener(this);
        this.point_pos = new JLabel("[0.000000000, 0.000000000]");
        this.point_pos.setFont(new Font("Courier", Font.PLAIN, 12));
        this.info_text = new JTextField(" Status : ", 85);
        this.info_text.setBorder(BorderFactory.createLoweredBevelBorder());
        // ImageIcon icon = new ImageIcon("printer1.gif");
        final JPanel progress_pan = new JPanel(new FlowLayout(2, 0, 0));
        this.progress_bar = new JProgressBar(0, 100);
        this.progress_bar.setBorder(BorderFactory.createLoweredBevelBorder());
        this.progress_bar.setStringPainted(true);
        progress_pan.add(this.progress_bar);
        /*
             print_icon = new JLabel(icon)
             {
            // overrides imageUpdate to control the animated gif's animation
            public boolean imageUpdate(Image img, int infoflags,
                        int x, int y, int width, int height)
            {
                System.out.println("Update image "+ infoflags);
             if (isShowing() && (infoflags & ALLBITS) != 0 && (infoflags & FRAMEBITS) == 0)
                    repaint();
                if (isShowing() && (infoflags & FRAMEBITS) != 0 && false)
                    repaint();
                return isShowing();
            }
             };
             print_icon.setBorder(BorderFactory.createLoweredBevelBorder());
          //   print_icon.setSize(20, 30);
             icon.setImageObserver(print_icon);
             progress_pan.add(print_icon);
         */
        this.help_m = new JMenu("Help");
        this.mb.add(this.help_m);
        final JMenuItem about_i = new JMenuItem("About jScope");
        this.help_m.add(about_i);
        about_i.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(jScopeFacade.this.help_dialog == null){
                    jScopeFacade.this.help_dialog = new jScopeBrowseUrl(jScopeFacade.this);
                    try{
                        final String path = "docs/jScope.html";
                        final URL url = this.getClass().getClassLoader().getResource(path);
                        jScopeFacade.this.help_dialog.connectToBrowser(url);
                    }catch(final Exception exc){
                        JOptionPane.showMessageDialog(jScopeFacade.this, "The jScope tutorial is available at www.mdsplus.org in \"Documentation/The MDSplus tutorial\" section", "", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                jScopeFacade.this.help_dialog.setVisible(true);
            }
        });
        this.setup_dialog = new SetupDataDialog(this, "Setup");
        this.wave_panel = this.buildWaveContainer();
        this.wave_panel.addWaveContainerListener(this);
        this.wave_panel.setParams(Waveform.MODE_ZOOM, this.setup_default.getGridMode(), this.setup_default.getLegendMode(), this.setup_default.getXLines(), this.setup_default.getYLines(), this.setup_default.getReversed());
        this.wave_panel.setPopupMenu(new jScopeWavePopup(this.setup_dialog, this.colorMapDialog));
        this.getContentPane().add("Center", this.wave_panel);
        this.panel = new JPanel();
        this.panel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 3));
        this.shot_l = new JLabel("Shot");
        this.shot_t = new JTextField(10);
        this.shot_t.addActionListener(this);
        /*
             shot_t.addFocusListener( new FocusAdapter()
            {
               public void focusLost(FocusEvent e)
               {
                    wave_panel.setMainShot(shot_t.getText());
               }
            }
             );
         */
        this.apply_b = new JButton("Apply");
        this.apply_b.addActionListener(this);
        this.point = new JRadioButton("Point");
        this.point.addItemListener(this);
        this.zoom = new JRadioButton("Zoom", true);
        this.zoom.addItemListener(this);
        this.pan = new JRadioButton("Pan");
        this.pan.addItemListener(this);
        this.copy = new JRadioButton("Copy");
        this.copy.addItemListener(this);
        this.pointer_mode.add(this.point);
        this.pointer_mode.add(this.zoom);
        this.pointer_mode.add(this.pan);
        this.pointer_mode.add(this.copy);
        this.panel.add(this.point);
        this.panel.add(this.zoom);
        this.panel.add(this.pan);
        this.panel.add(this.copy);
        this.panel.add(this.shot_l);
        this.panel.add(this.decShot = new BasicArrowButton(SwingConstants.WEST));
        this.panel.add(this.shot_t);
        this.panel.add(this.incShot = new BasicArrowButton(SwingConstants.EAST));
        this.decShot.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(jScopeFacade.this.shot_t.getText() != null && jScopeFacade.this.shot_t.getText().trim().length() != 0){
                    if(!jScopeFacade.this.executing_update){
                        jScopeFacade.this.incShotValue--;
                        jScopeFacade.this.arrowsIncDecShot();
                        jScopeFacade.this.updateAllWaves();
                    }
                }
            }
        });
        this.incShot.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(jScopeFacade.this.shot_t.getText() != null && jScopeFacade.this.shot_t.getText().trim().length() != 0){
                    if(!jScopeFacade.this.executing_update){
                        jScopeFacade.this.incShotValue++;
                        jScopeFacade.this.arrowsIncDecShot();
                        jScopeFacade.this.updateAllWaves();
                    }
                }
            }
        });
        this.panel.add(this.apply_b);
        this.panel.add(new JLabel(" Signal: "));
        final JPanel panel3 = new JPanel();
        final GridBagLayout gridbag = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        final Insets insets = new Insets(2, 2, 2, 2);
        panel3.setLayout(gridbag);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = insets;
        c.weightx = 1.0;
        c.gridwidth = 1;
        this.signal_expr = new JTextField(25);
        this.signal_expr.addActionListener(this);
        gridbag.setConstraints(this.signal_expr, c);
        panel3.add(this.signal_expr);
        final JPanel panel4 = new JPanel(new BorderLayout());
        panel4.add("West", this.panel);
        panel4.add("Center", panel3);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.add(BorderLayout.WEST, progress_pan);
        panel2.add(BorderLayout.CENTER, this.info_text);
        panel2.add(BorderLayout.EAST, this.net_text = new JTextField(" Data server :", 25));
        this.net_text.setBorder(BorderFactory.createLoweredBevelBorder());
        this.info_text.setEditable(false);
        this.net_text.setEditable(false);
        this.panel1 = new JPanel();
        this.panel1.setLayout(new BorderLayout());
        this.panel1.add("North", panel4);
        this.panel1.add("Center", this.point_pos);
        this.panel1.add("South", panel2);
        this.getContentPane().add("South", this.panel1);
        this.color_dialog.setReversed(this.setup_default.getReversed());
        if(jScopeFacade.is_debug){
            final Thread mon_mem = new MonMemory();
            mon_mem.start();
            final JButton exec_gc = new JButton("Exec gc");
            exec_gc.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    System.gc();
                }
            });
            this.panel1.add("West", exec_gc);
        }
        this.initDataServer();
        this.updateFont();
        this.updateColors();
    }

    /*    public void handleQuit()
        {
            System.exit(0);
        }
     */
    protected void jScopeUpdateUI() {
        jScopeFacade.jScopeSetUI(this);
        jScopeFacade.jScopeSetUI(this.font_dialog);
        jScopeFacade.jScopeSetUI(this.setup_default);
        jScopeFacade.jScopeSetUI(this.color_dialog);
        jScopeFacade.jScopeSetUI(this.pub_var_diag);
        jScopeFacade.jScopeSetUI(this.server_diag);
        jScopeFacade.jScopeSetUI(this.file_diag);
    }

    public void loadConfiguration() {
        if(this.config_file == null) return;
        this.incShotValue = 0;
        try{
            final jScopeProperties pr = new jScopeProperties();
            pr.load(new FileInputStream(this.config_file));
            this.loadConfiguration(pr);
        }catch(final IOException e){
            this.reset();
            JOptionPane.showMessageDialog(this, e.getMessage(), "alert LoadConfiguration", JOptionPane.ERROR_MESSAGE);
        }
        this.save_i.setEnabled(true);
    }

    public void loadConfiguration(final Properties pr) {
        this.wave_panel.eraseAllWave();
        try{
            this.loadFromFile(pr);
            this.setBounds(this.xpos, this.ypos, this.width, this.height);
            this.updateColors();
            this.updateFont();
            this.wave_panel.update();
            this.validate();
            DataServerItem dsi = this.wave_panel.getServerItem();
            dsi = this.server_diag.addServerIp(dsi);
            /*
            remove 28/06/2005
                        wave_panel.setServerItem(dsi);
             */
            if(!this.setDataServer(dsi)) this.setDataServer(new DataServerItem("Not Connected", null, null, "jscope.NotConnectedDataProvider", null, null, null, false));
            // SetFastNetworkState(wave_panel.getFastNetworkState());
            this.shot_t.setText("");
            this.updateAllWaves();
        }catch(final Exception e){
            this.reset();
            JOptionPane.showMessageDialog(this, e.getMessage(), "alert LoadConfiguration", JOptionPane.ERROR_MESSAGE);
        }
        // SetWindowTitle("");
        // System.gc();
    }

    private void loadConfigurationFrom() {
        if(this.isChange()){
            switch(this.saveWarning()){
                case JOptionPane.YES_OPTION:
                    if(this.config_file == null){
                        this.saveAs();
                        return;
                    }
                    this.saveConfiguration(this.config_file);
                    break;
                case JOptionPane.CANCEL_OPTION:
                    return;
            }
        }
        this.setChange(false);
        if(this.curr_directory != null && this.curr_directory.trim().length() != 0) this.file_diag.setCurrentDirectory(new File(this.curr_directory));
        final javax.swing.Timer t = new javax.swing.Timer(20, new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent ae) {
                final int returnVal = jScopeFacade.this.file_diag.showOpenDialog(jScopeFacade.this);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    final File file = jScopeFacade.this.file_diag.getSelectedFile();
                    final String d = file.getAbsolutePath();
                    final String f = file.getName();
                    if(f != null && f.trim().length() != 0 && d != null && d.trim().length() != 0){
                        jScopeFacade.this.curr_directory = d;
                        jScopeFacade.this.config_file = jScopeFacade.this.curr_directory;
                        jScopeFacade.this.loadConfiguration();
                    }
                }
            }
        });
        t.setRepeats(false);
        t.start();
    }

    public void loadFromFile(final Properties pr) throws IOException {
        try{
            this.fromFile(pr);
            this.font_dialog.fromFile(pr, "Scope.font");
            this.color_dialog.fromFile(pr, "Scope.color_");
            this.pub_var_diag.fromFile(pr, "Scope.public_variable_");
            this.wave_panel.fromFile(pr, "Scope", this.color_dialog.getColorMapIndex(), this.colorMapDialog);
            this.def_values.fromFile(pr, "Scope");
        }catch(final Exception e){
            throw(new IOException("Configuration file syntax error : " + e.getMessage()));
        }
    }

    @Override
    public int print(final Graphics g, final PageFormat pf, final int pageIndex) throws PrinterException {
        /*
            int st_x = 0, st_y = 0;
            double height = pf.getImageableHeight();
            double width  = pf.getImageableWidth();
         */
        final Graphics2D g2 = (Graphics2D)g;
        // jScope.displayPageFormatAttributes(4,pf);
        if(pageIndex == 0){
            g2.translate(pf.getImageableX(), pf.getImageableY());
            final RepaintManager currentManager = RepaintManager.currentManager(this);
            currentManager.setDoubleBufferingEnabled(false);
            g2.scale(72.0 / 600, 72.0 / 600);
            // Dimension d = this.getSize();
            // this.setSize((int)(d.width*600.0/72.0), (int)(d.height*600.0/72.0));
            // this.printAll(g2);
            g2.translate(pf.getImageableWidth() / 2, pf.getImageableHeight() / 2);
            g2.translate(pf.getWidth() / 2, pf.getHeight() / 2);
            g.drawOval(0, 0, 100, 100);
            g.drawOval(0, 0, 200, 200);
            g.drawOval(0, 0, 300, 300);
            g.drawOval(0, 0, 400, 400);
            g.drawOval(0, 0, 500, 500);
            g.drawOval(0, 0, 600, 600);
            currentManager.setDoubleBufferingEnabled(true);
            return Printable.PAGE_EXISTS;
        }
        return Printable.NO_SUCH_PAGE;
    }

    protected void printAllWaves(final PrintRequestAttributeSet attrs) {
        try{
            this.setStatusLabel("Executing print");
            this.wave_panel.printAllWaves(this.prnJob, attrs);
            this.setStatusLabel("End print operation");
        }catch(final PrinterException er){
            System.out.println(er);
            JOptionPane.showMessageDialog(this, "Error on print operation", "alert PrintAllWaves", JOptionPane.ERROR_MESSAGE);
        }catch(final PrintException er){
            System.out.println(er);
            JOptionPane.showMessageDialog(this, "Error on print operation", "alert PrintAllWaves", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void processConnectionEvent(final ConnectionEvent e) {
        this.progress_bar.setString("");
        if(e.getID() == ConnectionEvent.LOST_CONNECTION){
            JOptionPane.showMessageDialog(this, e.info, "alert processConnectionEvent", JOptionPane.ERROR_MESSAGE);
            this.setDataServer(new DataServerItem("Not Connected", null, null, "NotConnectedDataProvider", null, null, null, false));
            return;
        }
        if(e.current_size == 0 && e.total_size == 0){
            if(e.info != null) this.progress_bar.setString(e.info);
            else this.progress_bar.setString("");
            this.progress_bar.setValue(0);
        }else{
            final int v = (int)((float)e.current_size / e.total_size * 100.);
            this.progress_bar.setValue(v);
            this.progress_bar.setString((e.info != null ? e.info : "") + v + "%");
        }
    }

    @Override
    public void processUpdateEvent(final UpdateEvent e) {
        if(this.eventUpdateEnabled()){
            final String print_event = this.wave_panel.getPrintEvent();
            final String event = this.wave_panel.getEvent();
            if(e.name.equals(event)){
                SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        jScopeFacade.this.updateAllWaves();
                    }
                });
            }
            // wave_panel.StartUpdate();
            if(e.name.equals(print_event)) this.wave_panel.startPrint(this.prnJob, this.attrs);
        }
    }

    @Override
    public void processWaveContainerEvent(final WaveContainerEvent e) {
        String s = null;
        final int event_id = e.getID();
        switch(event_id){
            case WaveContainerEvent.END_UPDATE:
            case WaveContainerEvent.KILL_UPDATE:
                this.apply_b.setText("Apply");
                this.executing_update = false;
                if(event_id == WaveContainerEvent.KILL_UPDATE){
                    // JOptionPane.showMessageDialog(this, e.info,"alert processWaveContainerEvent", JOptionPane.ERROR_MESSAGE);
                    System.out.println(" processWaveContainerEvent " + e.getInfo());
                    this.setStatusLabel(" Aborted ");
                }else this.setStatusLabel(e.getInfo());
                this.setWindowTitle("");
                break;
            case WaveContainerEvent.START_UPDATE:
                this.setStatusLabel(e.getInfo());
                break;
            case WaveContainerEvent.WAVEFORM_EVENT:
                final WaveformEvent we = (WaveformEvent)e.getEvent();
                final jScopeMultiWave w = (jScopeMultiWave)we.getSource();
                final WaveInterface wi = w.wi;
                final int we_id = we.getID();
                switch(we_id){
                    case WaveformEvent.EVENT_UPDATE:
                        if(this.eventUpdateEnabled()){
                            this.setPublicVariables(this.pub_var_diag.getPublicVar());
                            this.setMainShot();
                            /*
                            wave_panel.Refresh(w, we.status_info);
                             */
                            w.refreshOnEvent();
                            // ??????*******
                        }
                        break;
                    case WaveformEvent.MEASURE_UPDATE:
                    case WaveformEvent.POINT_UPDATE:
                    case WaveformEvent.POINT_IMAGE_UPDATE:
                        s = we.toString();
                        if(wi.shots != null){
                            this.point_pos.setText(s + " Expr : " + w.getSignalName(we.signal_idx) + " Shot = " + wi.shots[we.signal_idx]);
                        }else{
                            this.point_pos.setText(s + " Expr : " + w.getSignalName(we.signal_idx));
                        }
                        break;
                    case WaveformEvent.STATUS_INFO:
                        this.setStatusLabel(we.status_info);
                        break;
                    case WaveformEvent.CACHE_DATA:
                        if(we.status_info != null) this.progress_bar.setString(we.status_info);
                        else this.progress_bar.setString("");
                        this.progress_bar.setValue(0);
                        break;
                }
                break;
        }
    }

    public void repaintAllWaves() {
        final int wave_mode = this.wave_panel.getMode();
        jScopeMultiWave w;
        this.wave_panel.setMode(Waveform.MODE_WAIT);
        for(int i = 0, k = 0; i < 4; i++){
            for(int j = 0; j < this.wave_panel.getComponentsInColumn(i); j++, k++){
                w = (jScopeMultiWave)this.wave_panel.getWavePanel(k);
                if(w.wi != null){
                    this.setStatusLabel("Repaint signal column " + (i + 1) + " row " + (j + 1));
                    this.setColor(w.wi);
                    w.update(w.wi);
                }
            }
        }
        this.wave_panel.repaintAllWaves();
        this.wave_panel.setMode(wave_mode);
    }

    private void reset() {
        this.config_file = null;
        this.incShotValue = 0;
        this.setWindowTitle("");
        this.wave_panel.reset();
    }

    private void saveAs() {
        if(this.curr_directory != null && this.curr_directory.trim().length() != 0) this.file_diag.setCurrentDirectory(new File(this.curr_directory));
        int returnVal = JFileChooser.CANCEL_OPTION;
        boolean done = false;
        while(!done){
            returnVal = this.file_diag.showSaveDialog(jScopeFacade.this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                final File file = this.file_diag.getSelectedFile();
                final String txtsig_file = file.getAbsolutePath();
                if(file.exists()){
                    final Object[] options = {"Yes", "No"};
                    final int val = JOptionPane.showOptionDialog(this, txtsig_file + " already exists.\nDo you want to replace it?", "Save as", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    if(val == JOptionPane.YES_OPTION) done = true;
                }else done = true;
            }else done = true;
        }
        if(returnVal == JFileChooser.APPROVE_OPTION){
            final File file = this.file_diag.getSelectedFile();
            final String d = file.getAbsolutePath();
            final String f = file.getName();
            if(f != null && f.trim().length() != 0 && d != null && d.trim().length() != 0){
                this.curr_directory = d;
                this.config_file = this.curr_directory;
            }else this.config_file = null;
            if(this.config_file != null){
                this.saveConfiguration(this.config_file);
            }
        }
    }

    public void saveConfiguration(String conf_file) {
        PrintWriter out;
        File ftmp, fok;
        if(conf_file == null || conf_file.length() == 0) return;
        final int pPos = conf_file.lastIndexOf('.');
        final int sPos = conf_file.lastIndexOf(File.separatorChar);
        if(pPos == -1 || pPos < sPos) conf_file = conf_file + ".jscp";
        this.last_directory = new String(conf_file);
        this.save_i.setEnabled(true);
        this.use_last_i.setEnabled(true);
        fok = new File(conf_file);
        ftmp = new File(conf_file + "_tmp");
        try{
            out = new PrintWriter(new FileWriter(ftmp));
            this.toFile(out);
            out.close();
            if(fok.exists()) this.createHistoryFile(fok);
            ftmp.renameTo(fok);
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this, e, "alert", JOptionPane.ERROR_MESSAGE);
        }
        ftmp.delete();
    }

    private int saveWarning() {
        final Object[] options = {"Save", "Don't Save", "Cancel"};
        final int val = JOptionPane.showOptionDialog(this, "Save change to the configuration file before closing ?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return val;
    }

    public void setApplicationFonts(final Font font) {
        final Font userEntryFont = font;
        final Font defaultFont = font;
        final Font boldFont = font;
        // User entry widgets
        UIManager.put("Text.font", new FontUIResource(userEntryFont));
        UIManager.put("TextField.font", new FontUIResource(userEntryFont));
        UIManager.put("TextArea.font", new FontUIResource(userEntryFont));
        UIManager.put("TextPane.font", new FontUIResource(userEntryFont));
        UIManager.put("List.font", new FontUIResource(userEntryFont));
        UIManager.put("Table.font", new FontUIResource(userEntryFont));
        UIManager.put("ComboBox.font", new FontUIResource(userEntryFont));
        // Non-user entry widgets
        UIManager.put("Button.font", new FontUIResource(defaultFont));
        UIManager.put("Label.font", new FontUIResource(defaultFont));
        UIManager.put("Menu.font", new FontUIResource(defaultFont));
        UIManager.put("MenuItem.font", new FontUIResource(defaultFont));
        UIManager.put("ToolTip.font", new FontUIResource(defaultFont));
        UIManager.put("ToggleButton.font", new FontUIResource(defaultFont));
        UIManager.put("TitledBorder.font", new FontUIResource(boldFont));
        UIManager.put("PopupMenu.font", new FontUIResource(defaultFont));
        UIManager.put("TableHeader.font", new FontUIResource(defaultFont));
        UIManager.put("PasswordField.font", new FontUIResource(defaultFont));
        UIManager.put("CheckBoxMenuItem.font", new FontUIResource(defaultFont));
        UIManager.put("CheckBox.font", new FontUIResource(defaultFont));
        UIManager.put("RadioButtonMenuItem.font", new FontUIResource(defaultFont));
        UIManager.put("RadioButton.font", new FontUIResource(defaultFont));
        // Containters
        UIManager.put("ToolBar.font", new FontUIResource(defaultFont));
        UIManager.put("MenuBar.font", new FontUIResource(defaultFont));
        UIManager.put("Panel.font", new FontUIResource(defaultFont));
        UIManager.put("ProgressBar.font", new FontUIResource(defaultFont));
        UIManager.put("TextPane.font", new FontUIResource(defaultFont));
        UIManager.put("OptionPane.font", new FontUIResource(defaultFont));
        UIManager.put("ScrollPane.font", new FontUIResource(defaultFont));
        UIManager.put("EditorPane.font", new FontUIResource(defaultFont));
        UIManager.put("ColorChooser.font", new FontUIResource(defaultFont));
        this.jScopeUpdateUI();
    }

    public void setApplicationFonts(final String font, final int style, final int size) {
        this.setApplicationFonts(new Font(font, style, size));
    }

    public void setChange(final boolean change) {
        if(this.modified == change) return;
        this.modified = change;
        this.setWindowTitle("");
    }

    private void setColor(final WaveInterface wi) {
        // if(wi == null || wi.colors_idx == null) return;
        // for(int i = 0; i < wi.colors_idx.length; i++)
        // wi.colors[i] = color_dialog.getColorAt(wi.colors_idx[i]);
    }

    public boolean setDataServer(final DataServerItem new_srv_item) {
        try{
            this.wave_panel.setDataServer(new_srv_item, this);
            this.wave_panel.setCacheState(new_srv_item.enable_cache);
            this.setDataServerLabel();
            return true;
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this, e.toString(), "alert SetDataServer", JOptionPane.ERROR_MESSAGE);
            this.setDataServerLabel();
        }
        return false;
    }

    public void setDataServerLabel() {
        this.net_text.setText("Data Server:" + this.wave_panel.getServerLabel());
    }

    public void setMainShot() {
        this.wave_panel.setMainShot(this.shot_t.getText());
    }

    public void setPropertiesFile(final String propFile) {
        this.propertiesFilePath = propFile;
    }

    public void setPublicVariables(final String public_variables) {
        this.def_values.setPublicVariables(public_variables);
        // Force update in all waveform
        if(!this.def_values.getIsEvaluated()) this.wave_panel.setModifiedState(true);
    }

    public void setStatusLabel(final String msg) {
        this.info_text.setText(" Status: " + msg);
    }

    public void setWindowTitle(final String info) {
        String f_name = this.config_file;
        if(f_name == null) f_name = "Untitled";
        if(this.wave_panel.getTitle() != null) this.setTitle(" - " + this.wave_panel.getEvaluatedTitle() + " - " + f_name + (this.isChange() ? " (changed)" : "") + " " + info);
        else this.setTitle("- Scope - " + f_name + (this.isChange() ? " (changed)" : "") + " " + info);
    }

    public void showAboutScreen() {
        this.aboutScreen.setVisible(true);
    }

    public void startScope(final String file) {
        if(file != null){
            this.config_file = new String(file);
            this.loadConfiguration();
        }
        this.setWindowTitle("");
        this.setVisible(true);
    }

    private void toFile(final PrintWriter out) throws IOException {
        final Rectangle r = this.getBounds();
        this.setChange(false);
        this.setWindowTitle("");
        out.println("Scope.geometry: " + r.width + "x" + r.height + "+" + r.x + "+" + r.y);
        out.println("Scope.update.disable: " + this.update_i.getState());
        out.println("Scope.update.disable_when_icon: " + this.update_when_icon_i.getState());
        this.font_dialog.toFile(out, "Scope.font");
        this.pub_var_diag.toFile(out, "Scope.public_variable_");
        this.color_dialog.toFile(out, "Scope.color_");
        this.wave_panel.toFile(out, "Scope.");
    }

    public void updateAllWaves() {
        final String s = this.shot_t.getText();
        final String s1 = this.def_values.shot_str;
        // Set main shot text field with global setting shot if defined.
        if((s == null || s.trim().length() == 0) && (s1 != null && s1.trim().length() != 0)) this.shot_t.setText(s1);
        this.executing_update = true;
        this.apply_b.setText("Abort");
        this.setPublicVariables(this.pub_var_diag.getPublicVar());
        this.setMainShot();
        this.wave_panel.startUpdate();
    }

    public void updateColors() {
        this.wave_panel.setColors(this.color_dialog.getColors(), this.color_dialog.getColorsName());
        this.setup_dialog.setColorList();
    }

    public void updateDefaultValues() {
        boolean is_changed = false;
        try{
            if((is_changed = this.setup_default.isChanged(this.def_values))){
                this.setChange(true);
                this.wave_panel.removeAllEvents(this);
                this.setup_default.saveDefaultConfiguration(this.def_values);
                this.invalidateDefaults();
                this.wave_panel.addAllEvents(this);
                this.updateAllWaves();
            }else this.setup_default.saveDefaultConfiguration(this.def_values);
            this.wave_panel.setParams(this.wave_panel.getMode(), this.setup_default.getGridMode(), this.setup_default.getLegendMode(), this.setup_default.getXLines(), this.setup_default.getYLines(), this.setup_default.getReversed());
            this.color_dialog.setReversed(this.setup_default.getReversed());
            this.updateColors();
            if(!is_changed) this.repaintAllWaves();
        }catch(final IOException e){
            JOptionPane.showMessageDialog(this, e.toString(), "alert UpdateDefaultValues", JOptionPane.ERROR_MESSAGE);
        }
    }

    // private void updateServerMenu() {}
    public void updateFont() {
        this.wave_panel.setFont(this.font_dialog.getFont());
    }

    @Override
    public void windowActivated(final WindowEvent e) {}

    @Override
    public void windowClosed(final WindowEvent e) {}

    @Override
    public void windowClosing(final WindowEvent e) {
        if(jScopeFacade.num_scope == 1) System.exit(0);
        else this.exitScope();
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {}

    @Override
    public void windowDeiconified(final WindowEvent e) {}

    @Override
    public void windowIconified(final WindowEvent e) {}

    @Override
    public void windowOpened(final WindowEvent e) {
        if(this.aboutScreen != null) this.hideAbout();
    }
}

@SuppressWarnings("serial")
class ServerDialog extends JDialog implements ActionListener{
    private static String                   know_provider[]   = {"w7x.W7XDataProvider", "mds.MdsDataProvider",
            // "mds.MdsDataProviderUdt",
            // "jet.JetMdsDataProvider",
            // "jet.JetDataProvider",
            // "twu.TwuDataProvider",
            // "ftu.FtuDataProvider",
            // "ts.TsDataProvider",
            // "asdex.AsdexDataProvider",
            // "ascii.AsciiDataProvider",
            "local.LocalDataProvider", "mds.MdsAsynchDataProvider"};
    static private JList                    server_list;
    private final JButton                   add_b, remove_b, exit_b, connect_b, modify_b;
    JCheckBox                               automatic;
    JComboBox                               data_provider_list;
    private final Hashtable<String, String> data_server_class = new Hashtable<String, String>();
    jScopeFacade                            dw;
    private final DefaultListModel          list_model        = new DefaultListModel();
    JTextField                              server_l, server_a, server_u;
    JLabel                                  server_label, user_label;
    JTextField                              tunnel_port;
    JCheckBox                               tunneling;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ServerDialog(final JFrame _dw, final String title){
        super(_dw, title, true);
        this.dw = (jScopeFacade)_dw;
        // setResizable(false);
        final GridBagLayout gridbag = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        final Insets insets = new Insets(4, 4, 4, 4);
        this.getContentPane().setLayout(gridbag);
        c.insets = insets;
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 10;
        ServerDialog.server_list = new JList(this.list_model);
        final JScrollPane scrollServerList = new JScrollPane(ServerDialog.server_list);
        ServerDialog.server_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ServerDialog.server_list.addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                final int idx = ServerDialog.server_list.getSelectedIndex();
                if(idx != -1){
                    ServerDialog.this.remove_b.setEnabled(true);
                    ServerDialog.this.modify_b.setEnabled(true);
                    ServerDialog.this.server_l.setText(jScopeFacade.server_ip_list[idx].name);
                    ServerDialog.this.server_a.setText(jScopeFacade.server_ip_list[idx].argument);
                    ServerDialog.this.server_u.setText(jScopeFacade.server_ip_list[idx].user);
                    ServerDialog.this.data_provider_list.setSelectedItem(jScopeFacade.server_ip_list[idx].class_name);
                    if(jScopeFacade.server_ip_list[idx].tunnel_port != null){
                        if(jScopeFacade.server_ip_list[idx].tunnel_port.trim().length() == 0) jScopeFacade.server_ip_list[idx].tunnel_port = null;
                        else{
                            ServerDialog.this.tunneling.setSelected(true);
                            ServerDialog.this.tunnel_port.setText(jScopeFacade.server_ip_list[idx].tunnel_port);
                            ServerDialog.this.tunnel_port.setEditable(true);
                        }
                    }
                    if(jScopeFacade.server_ip_list[idx].tunnel_port == null){
                        ServerDialog.this.tunnel_port.setText("");
                        ServerDialog.this.tunneling.setSelected(false);
                        ServerDialog.this.tunnel_port.setEditable(false);
                    }
                }else{
                    ServerDialog.this.remove_b.setEnabled(false);
                    ServerDialog.this.modify_b.setEnabled(false);
                }
            }
        });
        // server_list.addKeyListener(this);
        gridbag.setConstraints(scrollServerList, c);
        this.getContentPane().add(scrollServerList);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 1;
        c.gridwidth = 1;
        this.server_label = new JLabel("Server label ");
        gridbag.setConstraints(this.server_label, c);
        this.getContentPane().add(this.server_label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        this.server_l = new JTextField(20);
        gridbag.setConstraints(this.server_l, c);
        this.getContentPane().add(this.server_l);
        c.gridwidth = 1;
        this.server_label = new JLabel("Server argument ");
        gridbag.setConstraints(this.server_label, c);
        this.getContentPane().add(this.server_label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        this.server_a = new JTextField(20);
        gridbag.setConstraints(this.server_a, c);
        this.getContentPane().add(this.server_a);
        c.gridwidth = 1;
        this.tunneling = new JCheckBox("Tunneling local Port:");
        this.tunneling.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(ServerDialog.this.tunneling.isSelected()){
                    ServerDialog.this.tunnel_port.setEditable(true);
                }else{
                    ServerDialog.this.tunnel_port.setEditable(false);
                }
            }
        });
        gridbag.setConstraints(this.tunneling, c);
        this.getContentPane().add(this.tunneling);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        this.tunnel_port = new JTextField(6);
        this.tunnel_port.setEditable(false);
        gridbag.setConstraints(this.tunnel_port, c);
        this.getContentPane().add(this.tunnel_port);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        this.automatic = new JCheckBox("Get user name from host");
        this.automatic.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(ServerDialog.this.automatic.isSelected()){
                    ServerDialog.this.server_u.setText(System.getProperty("user.name"));
                    ServerDialog.this.server_u.setEditable(false);
                }else{
                    ServerDialog.this.server_u.setText("");
                    ServerDialog.this.server_u.setEditable(true);
                }
            }
        });
        gridbag.setConstraints(this.automatic, c);
        this.getContentPane().add(this.automatic);
        c.gridwidth = 1;
        this.server_label = new JLabel("User name ");
        gridbag.setConstraints(this.server_label, c);
        this.getContentPane().add(this.server_label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        this.server_u = new JTextField(20);
        gridbag.setConstraints(this.server_u, c);
        this.getContentPane().add(this.server_u);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        final JLabel lab = new JLabel("Server Class : ");
        gridbag.setConstraints(lab, c);
        this.getContentPane().add(lab);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        this.data_provider_list = new JComboBox();
        gridbag.setConstraints(this.data_provider_list, c);
        this.getContentPane().add(this.data_provider_list);
        this.data_provider_list.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                try{
                    if(e.getStateChange() == ItemEvent.SELECTED){
                        final String srv = (String)ServerDialog.this.data_provider_list.getSelectedItem();
                        if(srv != null){
                            final Class cl = Class.forName("jScope." + srv);
                            final DataProvider dp = ((DataProvider)cl.newInstance());
                            final boolean state = dp.supportsTunneling();
                            ServerDialog.this.tunneling.setEnabled(state);
                            ServerDialog.this.tunnel_port.setEnabled(state);
                        }
                    }
                }catch(final Exception exc){}
            }
        });
        final JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.add_b = new JButton("Add");
        this.add_b.addActionListener(this);
        p.add(this.add_b);
        this.modify_b = new JButton("Modify");
        this.modify_b.addActionListener(this);
        this.modify_b.setEnabled(false);
        p.add(this.modify_b);
        this.remove_b = new JButton("Remove");
        this.remove_b.addActionListener(this);
        this.remove_b.setEnabled(false);
        p.add(this.remove_b);
        this.connect_b = new JButton("Connect");
        this.connect_b.addActionListener(this);
        p.add(this.connect_b);
        this.exit_b = new JButton("Close");
        this.exit_b.addActionListener(this);
        p.add(this.exit_b);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(p, c);
        this.getContentPane().add(p);
        this.addKnowProvider();
        if(jScopeFacade.server_ip_list == null) this.getPropertiesValue();
        else this.addServerIpList(jScopeFacade.server_ip_list);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final Object ob = event.getSource();
        if(ob == this.exit_b) this.setVisible(false);
        if(ob == this.add_b){
            final String srv = this.server_l.getText().trim();
            if(srv != null && srv.length() != 0){
                this.addServerIp(new DataServerItem(srv, this.server_a.getText().trim(), this.server_u.getText().trim(), (String)this.data_provider_list.getSelectedItem(), null, null, this.tunnel_port.getText(), false));
            }
        }
        if(ob == this.remove_b){
            final int idx = ServerDialog.server_list.getSelectedIndex();
            if(idx >= 0){
                this.list_model.removeElementAt(idx);
                this.dw.servers_m.remove(idx);
            }
        }
        if(ob == this.connect_b){
            final int idx = ServerDialog.server_list.getSelectedIndex();
            if(idx >= 0) this.dw.setDataServer(jScopeFacade.server_ip_list[idx]);
        }
        if(ob == this.modify_b){
            final int idx = ServerDialog.server_list.getSelectedIndex();
            if(idx >= 0){
                final String srv = this.server_l.getText().trim();
                if(srv != null && srv.length() != 0){
                    if(!jScopeFacade.server_ip_list[idx].name.equals(srv)){
                        final int itemsCount = this.dw.servers_m.getItemCount();
                        JMenuItem mi;
                        for(int i = 0; i < itemsCount; i++){
                            mi = this.dw.servers_m.getItem(i);
                            if(mi.getText().equals(jScopeFacade.server_ip_list[idx].name)){
                                mi.setText(srv);
                                mi.setActionCommand("SET_SERVER " + srv);
                            }
                        }
                        jScopeFacade.server_ip_list[idx].name = srv;
                    }
                    jScopeFacade.server_ip_list[idx].argument = this.server_a.getText().trim();
                    jScopeFacade.server_ip_list[idx].user = this.server_u.getText().trim();
                    jScopeFacade.server_ip_list[idx].class_name = (String)this.data_provider_list.getSelectedItem();
                    jScopeFacade.server_ip_list[idx].tunnel_port = this.tunnel_port.getText();
                    ServerDialog.server_list.repaint();
                    // It is need to update the current data server if it is
                    // the modified server
                    // if (dw.wave_panel.getServerItem().equals(dw.server_ip_list[idx]))
                    // {
                    // dw.setDataServer(dw.server_ip_list[idx]);
                    // }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addKnowProvider() {
        for(final String element : ServerDialog.know_provider){
            this.data_server_class.put(element, element);
            this.data_provider_list.addItem(element);
        }
    }

    @SuppressWarnings("unchecked")
    public DataServerItem addServerIp(final DataServerItem dsi) {
        JMenuItem new_ip;
        DataServerItem found_dsi = null;
        /*
        23-05-2005
        found = ( (found_dsi = findServer(dsi)) != null);
         */
        found_dsi = this.findServer(dsi);
        if(found_dsi != null) return found_dsi;
        if(dsi.class_name != null && !this.data_server_class.containsValue(dsi.class_name)){
            this.data_server_class.put(dsi.class_name, dsi.class_name);
            this.data_provider_list.addItem(dsi.class_name);
        }
        // if (!found && dsi.class_name == null)
        if(dsi.class_name == null) JOptionPane.showMessageDialog(this, "Undefine data server class for " + dsi.name, "alert addServerIp", JOptionPane.ERROR_MESSAGE);
        // if (!found)
        {
            this.list_model.addElement(dsi);
            new_ip = new JMenuItem(dsi.name);
            this.dw.servers_m.add(new_ip);
            new_ip.setActionCommand("SET_SERVER " + dsi.name);
            new_ip.addActionListener(this.dw);
            jScopeFacade.server_ip_list = this.getServerIpList();
        }
        /*
        23-05-2005
        Ovverride configuration file server definitions
        with property server definition with the same name
        else
        {
            if (found_dsi != null)
            {
                dsi.user = found_dsi.user;
                dsi.class_name = found_dsi.class_name;
                dsi.browse_class = found_dsi.browse_class;
                dsi.browse_url = found_dsi.browse_url;
                dsi.tunnel_port = found_dsi.tunnel_port;
            }
        }
         */
        return dsi;
    }

    public void addServerIpList(final DataServerItem[] dsi_list) {
        if(dsi_list == null) return;
        for(final DataServerItem element : dsi_list)
            this.addServerIp(element);
    }

    private DataServerItem findServer(final DataServerItem dsi) {
        DataServerItem found_dsi = null;
        final Enumeration e = this.list_model.elements();
        while(e.hasMoreElements()){
            found_dsi = (DataServerItem)e.nextElement();
            if(found_dsi.equals(dsi)){ return found_dsi; }
        }
        return null;
    }

    private void getPropertiesValue() {
        final Properties js_prop = this.dw.js_prop;
        DataServerItem dsi;
        int i = 1;
        if(js_prop == null) return;
        while(true){
            dsi = new DataServerItem();
            dsi.name = js_prop.getProperty("jScope.data_server_" + i + ".name");
            if(dsi.name == null) break;
            dsi.argument = js_prop.getProperty("jScope.data_server_" + i + ".argument");
            dsi.user = js_prop.getProperty("jScope.data_server_" + i + ".user");
            dsi.class_name = js_prop.getProperty("jScope.data_server_" + i + ".class");
            dsi.browse_class = js_prop.getProperty("jScope.data_server_" + i + ".browse_class");
            dsi.browse_url = js_prop.getProperty("jScope.data_server_" + i + ".browse_url");
            dsi.tunnel_port = js_prop.getProperty("jScope.data_server_" + i + ".tunnel_port");
            try{
                dsi.fast_network_access = new Boolean(js_prop.getProperty("jScope.data_server_" + i + ".fast_network_access")).booleanValue();
            }catch(final Exception exc){
                dsi.fast_network_access = false;
            }
            this.addServerIp(dsi);
            i++;
        }
    }

    public DataServerItem[] getServerIpList() {
        final Enumeration e = this.list_model.elements();
        final DataServerItem out[] = new DataServerItem[this.list_model.size()];
        for(int i = 0; e.hasMoreElements(); i++)
            out[i] = (DataServerItem)e.nextElement();
        return out;
    }

    private void resetAll() {
        ServerDialog.server_list.clearSelection();
        this.server_l.setText("");
        this.server_a.setText("");
        this.server_u.setText("");
        this.tunnel_port.setText("");
        this.data_provider_list.setSelectedIndex(0);
    }

    public void Show() {
        this.pack();
        this.resetAll();
        final DataServerItem found_dsi = this.findServer(this.dw.wave_panel.getServerItem());
        if(found_dsi != null) ServerDialog.server_list.setSelectedValue(found_dsi, true);
        this.setLocationRelativeTo(this.dw);
        this.setVisible(true);
    }
}

@SuppressWarnings("serial")
class WindowDialog extends JDialog implements ActionListener{
    boolean      changed   = false;
    int          in_row[];
    JLabel       label;
    JButton      ok, apply, cancel;
    int          out_row[] = new int[4];
    jScopeFacade parent;
    JSlider      row_1, row_2, row_3, row_4;
    JTextField   titleText, eventText, printEventText;

    WindowDialog(final JFrame dw, final String title){
        super(dw, title, true);
        this.parent = (jScopeFacade)dw;
        final GridBagConstraints c = new GridBagConstraints();
        final GridBagLayout gridbag = new GridBagLayout();
        this.getContentPane().setLayout(gridbag);
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.label = new JLabel("Rows in Column");
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        final JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        c.gridwidth = GridBagConstraints.BOTH;
        this.row_1 = new JSlider(SwingConstants.VERTICAL, 1, 16, 1);
        // row_1.setMajorTickSpacing(4);
        this.row_1.setMinorTickSpacing(1);
        this.row_1.setPaintTicks(true);
        this.row_1.setPaintLabels(true);
        final Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(1), new JLabel("1"));
        labelTable.put(new Integer(4), new JLabel("4"));
        labelTable.put(new Integer(8), new JLabel("8"));
        labelTable.put(new Integer(12), new JLabel("12"));
        labelTable.put(new Integer(16), new JLabel("16"));
        this.row_1.setLabelTable(labelTable);
        this.row_1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        this.row_2 = new JSlider(SwingConstants.VERTICAL, 0, 16, 0);
        this.row_2.setMajorTickSpacing(4);
        this.row_2.setMinorTickSpacing(1);
        this.row_2.setPaintTicks(true);
        this.row_2.setPaintLabels(true);
        this.row_2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        this.row_3 = new JSlider(SwingConstants.VERTICAL, 0, 16, 0);
        this.row_3.setMajorTickSpacing(4);
        this.row_3.setMinorTickSpacing(1);
        this.row_3.setPaintTicks(true);
        this.row_3.setPaintLabels(true);
        this.row_3.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        this.row_4 = new JSlider(SwingConstants.VERTICAL, 0, 16, 0);
        this.row_4.setMajorTickSpacing(4);
        this.row_4.setMinorTickSpacing(1);
        this.row_4.setPaintTicks(true);
        this.row_4.setPaintLabels(true);
        this.row_4.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(p, c);
        this.getContentPane().add(p);
        p.add(this.row_1);
        p.add(this.row_2);
        p.add(this.row_3);
        p.add(this.row_4);
        c.gridwidth = GridBagConstraints.BOTH;
        this.label = new JLabel("Title");
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.titleText = new JTextField(40);
        gridbag.setConstraints(this.titleText, c);
        this.getContentPane().add(this.titleText);
        c.gridwidth = GridBagConstraints.BOTH;
        this.label = new JLabel("Update event");
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.eventText = new JTextField(40);
        gridbag.setConstraints(this.eventText, c);
        this.getContentPane().add(this.eventText);
        c.gridwidth = GridBagConstraints.BOTH;
        this.label = new JLabel("Print event");
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.printEventText = new JTextField(40);
        gridbag.setConstraints(this.printEventText, c);
        this.getContentPane().add(this.printEventText);
        final JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.ok = new JButton("Ok");
        this.ok.addActionListener(this);
        p1.add(this.ok);
        this.cancel = new JButton("Cancel");
        this.cancel.addActionListener(this);
        p1.add(this.cancel);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(p1, c);
        this.getContentPane().add(p1);
        this.pack();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object ob = e.getSource();
        try{
            if(ob == this.ok || ob == this.apply){
                this.parent.wave_panel.setTitle(new String(this.titleText.getText()));
                String event = new String(this.eventText.getText().trim());
                this.parent.wave_panel.setEvent(this.parent, event);
                event = new String(this.printEventText.getText().trim());
                this.parent.wave_panel.setPrintEvent(this.parent, event);
                this.parent.setWindowTitle("");
                this.out_row[0] = this.row_1.getValue();
                this.out_row[1] = this.row_2.getValue();
                this.out_row[2] = this.row_3.getValue();
                this.out_row[3] = this.row_4.getValue();
                for(int i = 0; i < 4; i++)
                    if(this.out_row[i] != this.in_row[i]){
                        this.changed = true;
                        break;
                    }
                this.in_row = null;
                if(ob == this.ok) this.setVisible(false);
            }
            if(ob == this.cancel){
                this.setVisible(false);
            }
        }catch(final IOException ev){
            JOptionPane.showMessageDialog(this, ev.getMessage(), "alert actionPerformed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean ShowWindowDialog() {
        this.changed = false;
        if(this.parent.wave_panel.getTitle() != null) this.titleText.setText(this.parent.wave_panel.getTitle());
        if(this.parent.wave_panel.getEvent() != null) this.eventText.setText(this.parent.wave_panel.getEvent());
        if(this.parent.wave_panel.getPrintEvent() != null) this.printEventText.setText(this.parent.wave_panel.getPrintEvent());
        final int in_row[] = this.parent.wave_panel.getComponentsInColumns();
        this.row_1.setValue(in_row[0]);
        this.row_2.setValue(in_row[1]);
        this.row_3.setValue(in_row[2]);
        this.row_4.setValue(in_row[3]);
        this.in_row = new int[in_row.length];
        for(int i = 0; i < in_row.length; i++)
            this.in_row[i] = in_row[i];
        this.setLocationRelativeTo(this.parent);
        this.setVisible(true);
        return this.changed;
    }
}

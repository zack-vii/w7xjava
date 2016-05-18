package jScope;

/* $Id$ */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RepaintManager;
import debug.DEBUG;

/**
 * A MultiWaveform container
 *
 * @see RowColumnLayout
 * @see RowColumnContainer
 * @see WaveformManager
 * @see MultiWaveform
 */
@SuppressWarnings("serial")
public class WaveformContainer extends RowColumnContainer implements WaveformManager, WaveformListener, Printable{
    private static Waveform copy_waveform = null;

    public static void disableDoubleBuffering(final Component c) {
        final RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(final Component c) {
        final RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }

    private static String getFileName(final Waveform w) {
        String out;
        final Properties prop = new Properties();
        final String pr = w.getProperties();
        try{
            prop.load(new StringReader(pr));
            out = w.GetTitle();
            out = out + "_" + prop.getProperty("x_pix");
            out = out + "_" + prop.getProperty("y_pix");
            out = out + "_" + prop.getProperty("time");
            out = out.replace('.', '-') + ".txt";
        }catch(final Exception e){
            out = null;
        }
        return out;
    }

    public static void SetFont(final Font font) {
        Waveform.SetFont(font);
    }
    protected Font                              font                    = new Font("Helvetica", Font.PLAIN, 12);
    private int                                 mode                    = Waveform.MODE_ZOOM, grid_mode = Grid.IS_DOTTED, x_grid_lines = 5, y_grid_lines = 5;
    protected boolean                           print_bw                = false;
    protected boolean                           print_with_legend       = false;
    protected boolean                           reversed                = false;
    protected String                            save_as_txt_directory   = null;
    private Waveform                            sel_wave;
    private final Vector<WaveContainerListener> wave_container_listener = new Vector<WaveContainerListener>();
    protected WavePopup                         wave_popup;

    public WaveformContainer(){
        super();
        this.CreateWaveformContainer(true);
    }

    /**
     * Constructs a new WaveformContainer with a number of column and component in column.
     *
     * @param rows
     *            an array of number of component in column
     */
    public WaveformContainer(final int rows[], final boolean add_component){
        super(rows, null);
        this.CreateWaveformContainer(add_component);
    }

    /**
     * Add MultiWaveform to the container
     *
     * @param c
     *            an array of MultiWaveform to add
     */
    public void AddComponents(final Component c[]) {
        super.add(c);
        /*
         * for(int i = 0; i < c.length; i++) if(c[i] instanceof Waveform) { ((Waveform)c[i]).addWaveformListener(this); }
         */
    }

    /**
     * Adds the specified waveform container listener to receive WaveContainerEvent events from this WaveformContainer.
     *
     * @param l
     *            the waveform container listener
     */
    public synchronized void addWaveContainerListener(final WaveContainerListener l) {
        if(l == null){ return; }
        this.wave_container_listener.addElement(l);
    }

    /**
     * Set the same scale factor of the argument waveform to all waveform
     *
     * @param curr_w
     *            a waveform
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void AllSameScale(final Waveform curr_w) {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) if(w != curr_w) w.SetScale(curr_w);
        }
    }

    /**
     * Set x scale factor of all waveform equals to argument waveform
     *
     * @param curr_w
     *            a waveform
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void AllSameXScale(final Waveform curr_w) {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null && w != curr_w) w.SetXScale(curr_w);
        }
    }

    /**
     * Autoscale y axis and set x axis equals to argument waveform
     *
     * @param curr_w
     *            a waveform
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void AllSameXScaleAutoY(final Waveform curr_w) {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.SetXScaleAutoY(curr_w);
        }
    }

    /**
     * Set y scale factor of all waveform equals to argument waveform
     *
     * @param curr_w
     *            a waveform
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void AllSameYScale(final Waveform curr_w) {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) if(w != curr_w) w.SetYScale(curr_w);
        }
    }

    synchronized public void appendUpdateWaveforms() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.appendUpdate();
        }
    }

    /**
     * Autoscale operation on all waveforms
     *
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void AutoscaleAll() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.Autoscale();
        }
    }

    /**
     * Autoscale operation on all images
     *
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void AutoscaleAllImages() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null && w.IsImage()) w.Autoscale();
        }
    }

    /**
     * Autoscale y axis on all waveform
     *
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void AutoscaleAllY() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.AutoscaleY();
        }
    }

    /**
     * Return a new MultiWaveform component
     *
     * @return a MulitiWaveform components
     */
    public Component CreateWaveComponent() {
        final Component[] c = this.CreateWaveComponents(1);
        return c[0];
    }

    /**
     * Create an array of MultiWaveform
     *
     * @param num
     *            dimension of return array
     * @return an array of MultiWaveform
     */
    protected Component[] CreateWaveComponents(final int num) {
        final Component c[] = new Component[num];
        MultiWaveform wave;
        for(int i = 0; i < c.length; i++){
            wave = new MultiWaveform();
            wave.addWaveformListener(this);
            this.SetWaveParams(wave);
            c[i] = wave;
        }
        return c;
    }

    /**
     * Initialize WaveformContaine
     */
    private void CreateWaveformContainer(final boolean create_component) {
        if(create_component){
            final Component c[] = this.CreateWaveComponents(this.getComponentNumber());
            super.add(c);
        }
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(final MouseEvent e) {
                if(WaveformContainer.this.wave_popup != null) WaveformContainer.this.wave_popup.Show((Waveform)e.getSource(), e.getPoint());
            }
        });
    }

    /**
     * Deselect waveform.
     *
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void Deselect() {
        if(this.sel_wave != null) this.sel_wave.DeselectWave();
        this.sel_wave = null;
    }

    /**
     * Processes wave container events occurring on this WaveformContainer by dispatching them to any registered WaveContainerListener objects.
     *
     * @param e
     *            the wave container event
     */
    protected void dispatchWaveContainerEvent(final WaveContainerEvent e) {
        if(this.wave_container_listener != null){
            for(int i = 0; i < this.wave_container_listener.size(); i++){
                this.wave_container_listener.elementAt(i).processWaveContainerEvent(e);
            }
        }
    }

    /**
     * Get current waveform selected as copy source
     *
     * @return copy source waveform
     */
    @Override
    public Waveform GetCopySource() {
        return WaveformContainer.copy_waveform;
    }

    @Override
    public Component getMaximizeComponent() {
        return super.getMaximizeComponent();
    }

    public int GetMode() {
        return this.mode;
    }

    /**
     * Get current selected waveform.
     *
     * @return current selected waveform or null
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public Waveform GetSelected() {
        return this.sel_wave;
    }

    public Waveform GetSelectPanel() {
        Waveform w;
        if(this.sel_wave == null){
            int i;
            for(i = 0; i < this.getGridComponentCount() && this.GetWavePanel(i) != null && (this.GetWavePanel(i).GetShowSignalCount() != 0 || this.GetWavePanel(i).IsImage()); i++);
            if(i == this.getGridComponentCount()){
                final Component c[] = this.CreateWaveComponents(1);
                this.splitContainer(c[0]);
                w = (Waveform)c[0];
            }else{
                w = this.GetWavePanel(i);
            }
        }else return this.sel_wave;
        return w;
    }

    @Override
    public int GetWaveformCount() {
        return this.getGridComponentCount();
    }

    /**
     * Return index of an added MultiWaveform
     *
     * @param w
     *            The MultiWaveform
     * @return the MultiWaveform index
     */
    public int GetWaveIndex(final Waveform w) {
        int idx;
        for(idx = 0; idx < this.getGridComponentCount() && this.GetWavePanel(idx) != w; idx++);
        if(idx < this.getGridComponentCount()) return idx;
        return -1;
    }

    public Waveform GetWavePanel(final int idx) {
        final Component c = this.getGridComponent(idx);
        if(c instanceof MultiWaveform || c instanceof Waveform) return (Waveform)c;
        return null;
    }

    @Override
    public Point getWavePosition(final Waveform w) {
        return this.getComponentPosition(w);
    }

    public boolean isMaximize(final Waveform w) {
        return super.isMaximize();
    }

    public void LoadFileConfiguration() {}

    @Override
    public void maximizeComponent(final Waveform w) {
        super.maximizeComponent(w);
    }

    /**
     * Perform copy operation
     *
     * @param dest
     *            destination waveform
     * @param source
     *            source waveform
     */
    @Override
    public void NotifyChange(final Waveform dest, final Waveform source) {
        dest.Copy(source);
    }

    @Override
    public int print(final Graphics g, final PageFormat pf, final int pageIndex) throws PrinterException {
        int st_x = 0, st_y = 0;
        double height = pf.getImageableHeight();
        double width = pf.getImageableWidth();
        final Graphics2D g2 = (Graphics2D)g;
        final String ver = System.getProperty("java.version");
        if(pageIndex == 0){
            // fix page margin error on jdk 1.2.X
            if(ver.indexOf("1.2") != -1){
                if(pf.getOrientation() == PageFormat.LANDSCAPE){
                    st_y = -13;
                    st_x = 15;
                    width -= 5;
                }else{
                    // st_x = 10;
                    st_y = -5;
                    width -= 25;
                    height -= 25;
                }
            }
            g2.translate(pf.getImageableX(), pf.getImageableY());
            this.PrintAll(g2, st_x, st_y, (int)height, (int)width);
            return Printable.PAGE_EXISTS;
        }
        return Printable.NO_SUCH_PAGE;
    }

    public void PrintAll(final Graphics g, final int st_x, final int st_y, final int height, final int width) {
        if(DEBUG.M){
            System.out.println("WaveformContainer.PrintAll()");
        }
        Waveform w;
        int i, j, k = 0;
        int pix = 1;
        if(this.GetWavePanel(0).grid_mode == 2) // Grid.IS_NONE mode
        pix = 0;
        int curr_height = 0;
        int curr_width = 0;
        int px = 0;
        int py = 0;
        int pos = 0;
        for(i = k = 0, px = st_x; i < this.rows.length; i++){
            if(this.rows[i] == 0) continue;
            g.translate(px, 0);
            curr_width = (int)(width * ((RowColumnLayout)this.getLayout()).getPercentWidth(i) + 0.9);
            if(curr_width == 0){
                k += this.rows[i];
                continue;
            }
            for(j = pos = 0, py = st_y; j < this.rows[i]; j++){
                curr_height = (int)(height * ((RowColumnLayout)this.getLayout()).getPercentHeight(k) + 0.9);
                if(curr_height == 0){
                    k++;
                    continue;
                }
                g.translate(0, py);
                if(j == this.rows[i] - 1 && pos + curr_height != height) curr_height = height - pos;
                g.setClip(0, 0, curr_width, curr_height);
                w = this.GetWavePanel(k);
                if(w != null){
                    int print_mode = Waveform.PRINT;
                    if(this.print_with_legend) print_mode |= MultiWaveform.PRINT_LEGEND;
                    if(this.print_bw) print_mode |= MultiWaveform.PRINT_BW;
                    WaveformContainer.disableDoubleBuffering(w);
                    w.paint(g, new Dimension(curr_width, curr_height), print_mode);
                    WaveformContainer.enableDoubleBuffering(w);
                }
                py = curr_height - pix;
                pos += (curr_height - pix);
                k++;
            }
            px = curr_width - pix;
            g.translate(0, -pos - st_y + py);
        }
    }

    /**
     * process waveform event on this container
     *
     * @param e
     *            the waveform event
     */
    @Override
    public void processWaveformEvent(final WaveformEvent e) {
        final Waveform w = (Waveform)e.getSource();
        switch(e.getID()){
            case WaveformEvent.BROADCAST_SCALE:
                this.AllSameScale(w);
                return;
            case WaveformEvent.COPY_PASTE:
                if(WaveformContainer.copy_waveform != null) this.NotifyChange(w, WaveformContainer.copy_waveform);
                return;
            case WaveformEvent.COPY_CUT:
                this.SetCopySource(w);
                return;
            case WaveformEvent.PROFILE_UPDATE:
                return;
            case WaveformEvent.POINT_UPDATE:
            case WaveformEvent.MEASURE_UPDATE:
                if(w.GetMode() == Waveform.MODE_POINT){
                    final Double tf = new Double(e.time_value);
                    final Double nan_d = new Double(Double.NaN);
                    double x = e.point_x;
                    final double y = e.point_y;
                    if(w.IsImage()) x = e.delta_x;
                    else if(e.is_mb2) this.AllSameXScaleAutoY(w);
                    // Set x to time_value allows pannels synchronization from 2D
                    // signal viewed in MODE_YX
                    if(!tf.equals(nan_d)) x = e.time_value;
                    this.UpdatePoints(x, y, (Waveform)e.getSource());
                }
                /*
                 * if(!w.IsImage() && show_measure) { e = new WaveformEvent(e.getSource(), WaveformEvent.MEASURE_UPDATE, e.point_x, e.point_y, e.delta_x, e.delta_y, 0, e.signal_idx); }
                 */
                break;
            case WaveformEvent.POINT_IMAGE_UPDATE:
                break;
        }
        final WaveContainerEvent we = new WaveContainerEvent(this, e);
        this.dispatchWaveContainerEvent(we);
    }

    public void RemoveAllSignals() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.Erase();
        }
        System.gc();
    }

    /**
     * Removes the specified waveform container listener so that it no longer receives WaveContainerEvent events from this WaveformContainer.
     *
     * @param l
     *            the waveform container listener
     */
    public synchronized void removeContainerListener(final ActionListener l) {
        if(l == null){ return; }
        this.wave_container_listener.removeElement(l);
    }

    /**
     * Remove a waveform.
     *
     * @param w
     *            waveform to remove
     */
    @Override
    public void removePanel(final Waveform w) {
        if(w == this.sel_wave) this.sel_wave = null;
        if(w.IsCopySelected()){
            WaveformContainer.copy_waveform = null;
            w.SetCopySelected(false);
        }
        w.finalize();
        super.removeComponent(w);
    }

    /**
     * Remove current MultiWaveform selected
     */
    public void RemoveSelection() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount() && WaveformContainer.copy_waveform != null; i++){
            w = this.GetWavePanel(i);
            if(w != null){
                if(w.IsCopySelected()){
                    WaveformContainer.copy_waveform = null;
                    w.SetCopySelected(false);
                    break;
                }
            }
        }
    }

    public void RepaintAllWaves() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.repaint();
        }
    }

    /**
     * Reset all waveform scale factor.
     *
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void ResetAllScales() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.ResetScales();
        }
    }

    public void ResetDrawPanel(final int _row[]) {
        int n_wave = 0;
        int num = 0;
        for(int i = 0; i < _row.length; i++){
            n_wave = (_row[i] - this.rows[i]);
            if(n_wave > 0) num += n_wave;
        }
        Component c[] = null;
        if(num > 0) c = this.CreateWaveComponents(num);
        this.update(_row, c);
        if(this.sel_wave != null) this.sel_wave.SelectWave();
    }

    public void SaveAsText(final Waveform w, final boolean all) {
        final Vector<Waveform> panel = new Vector<Waveform>();
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
        boolean done = false;
        String txtsig_file = null;
        while(!done){
            final String fname = WaveformContainer.getFileName(w);
            if(fname != null) file_diag.setSelectedFile(new File(fname));
            returnVal = file_diag.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                final File file = file_diag.getSelectedFile();
                txtsig_file = file.getAbsolutePath();
                if(file.exists()){
                    final Object[] options = {"Yes", "No"};
                    final int val = JOptionPane.showOptionDialog(this, txtsig_file + " already exists.\nDo you want to replace it?", "Save as", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    if(val == JOptionPane.YES_OPTION) done = true;
                }else done = true;
            }else done = true;
        }
        if(returnVal == JFileChooser.APPROVE_OPTION){
            if(txtsig_file != null){
                this.save_as_txt_directory = new String(txtsig_file);
                if(all){
                    for(int i = 0; i < this.GetWaveformCount(); i++)
                        panel.addElement(this.GetWavePanel(i));
                }else panel.addElement(w);
                String s1 = "", s2 = "";
                final StringBuffer space = new StringBuffer();
                try{
                    final BufferedWriter out = new BufferedWriter(new FileWriter(txtsig_file));
                    out.write("% Title: " + w.GetTitle());
                    out.newLine();
                    final Properties prop = new Properties();
                    final String pr = w.getProperties();
                    try{
                        prop.load(new StringReader(pr));
                        out.write("% Expression: " + prop.getProperty("expr"));
                        out.newLine();
                        out.write("% x_pixel: " + prop.getProperty("x_pix"));
                        out.newLine();
                        out.write("% y_pixel: " + prop.getProperty("y_pix"));
                        out.newLine();
                        out.write("% time: " + prop.getProperty("time"));
                        out.newLine();
                    }catch(final Exception e){}
                    final double xmax = w.GetWaveformMetrics().XMax();
                    final double xmin = w.GetWaveformMetrics().XMin();
                    s1 = "";
                    s2 = "";
                    final int nPoint = w.waveform_signal.getNumPoints();
                    for(int j = 0; j < nPoint; j++){
                        final double x = w.waveform_signal.getX(j);
                        if(x > xmin && x < xmax){
                            s1 = "" + x;
                            s2 = "" + w.waveform_signal.getY(j);
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
                            out.newLine();
                        }
                    }
                    out.close();
                }catch(final IOException e){
                    System.err.println(e);
                }
            }
            file_diag = null;
        }
    }

    /**
     * Select a waveform
     *
     * @param w
     *            waveform to select
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void Select(final Waveform w) {
        this.Deselect();
        this.sel_wave = w;
        this.sel_wave.SelectWave();
    }

    public void SetColors(final Color colors[], final String colors_name[]) {
        // Waveform.SetColors(colors, colors_name);
        Waveform w;
        for(int i = 0, k = 0; i < this.rows.length; i++){
            for(int j = 0; j < this.rows[i]; j++, k++){
                w = this.GetWavePanel(k);
                if(w != null) Waveform.SetColors(colors, colors_name);
            }
        }
    }

    /**
     * Set copy source waveform
     *
     * @param w
     *            copy source waveform
     * @see Waveform
     * @see MultiWaveform
     */
    @Override
    public void SetCopySource(final Waveform w) {
        /*
         * if(w != null) w.SetCopySelected(true); else if(copy_waveform != null) copy_waveform.SetCopySelected(false);
         */
        if(w != null){
            if(w == WaveformContainer.copy_waveform){
                w.SetCopySelected(false);
                WaveformContainer.copy_waveform = null;
                return;
            }
            w.SetCopySelected(true);
        }
        if(WaveformContainer.copy_waveform != null) WaveformContainer.copy_waveform.SetCopySelected(false);
        WaveformContainer.copy_waveform = w;
    }

    public void SetGridMode(final int grid_mode) {
        Waveform w;
        this.grid_mode = grid_mode;
        final boolean int_label = (grid_mode == 2 ? false : true);
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.SetGridMode(grid_mode, int_label, int_label);
        }
    }

    public void SetGridStep(final int x_grid_lines, final int y_grid_lines) {
        Waveform w;
        this.x_grid_lines = x_grid_lines;
        this.y_grid_lines = y_grid_lines;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.SetGridSteps(x_grid_lines, y_grid_lines);
        }
    }

    public void setLegendMode(final int legend_mode) {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null && w instanceof MultiWaveform) ((MultiWaveform)w).setLegendMode(legend_mode);
        }
    }

    public void SetMode(final int mode) {
        Waveform w;
        this.mode = mode;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null){
                if(WaveformContainer.copy_waveform == w && w.mode == Waveform.MODE_COPY && mode != Waveform.MODE_COPY){
                    this.RemoveSelection();
                    WaveformContainer.copy_waveform = null;
                }
                w.SetMode(mode);
            }
        }
    }

    public void SetParams(final int mode, final int grid_mode, final int legend_mode, final int x_grid_lines, final int y_grid_lines, final boolean reversed) {
        this.SetReversed(reversed);
        this.SetMode(mode);
        this.SetGridMode(grid_mode);
        this.SetGridStep(x_grid_lines, y_grid_lines);
        this.setLegendMode(legend_mode);
    }

    /**
     * Set popup menu to this container
     *
     * @param wave_popup
     *            the popup menu
     */
    public void setPopupMenu(final WavePopup wave_popup) {
        this.wave_popup = wave_popup;
        wave_popup.setParent(this);
    }

    public void setPrintBW(final boolean print_bw) {
        this.print_bw = print_bw;
    }

    public void setPrintWithLegend(final boolean print_with_legend) {
        this.print_with_legend = print_with_legend;
    }

    public void SetReversed(final boolean reversed) {
        Waveform w;
        this.reversed = reversed;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.SetReversed(reversed);
        }
    }

    /**
     * Enable / disable show measurament
     *
     * @param state
     *            shoe measurament state
     */
    @Override
    public void SetShowMeasure(final boolean state) {
        if(state){
            Waveform w;
            for(int i = 0; i < this.getGridComponentCount(); i++){
                w = this.GetWavePanel(i);
                if(w != null) w.show_measure = false;
            }
        }
    }

    /**
     * Set current MultiWaveform parameters
     *
     * @param w
     *            the MultiWaveform to set params
     */
    public void SetWaveParams(final Waveform w) {
        final boolean int_label = (this.grid_mode == 2 ? false : true);
        w.SetMode(this.mode);
        w.SetReversed(this.reversed);
        w.SetGridMode(this.grid_mode, int_label, int_label);
        w.SetGridSteps(this.x_grid_lines, this.y_grid_lines);
    }

    public void stopPlaying() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.StopFrame();
        }
    }

    /**
     * Update crosshair position
     *
     * @param curr_x
     *            x axis position
     * @param w
     *            a waveform to update cross
     * @see Waveform
     * @see MultiWaveform
     */
    /* synchronized */public void UpdatePoints(final double x, final double y, final Waveform curr_w) {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null && w != curr_w) w.UpdatePoint(x, y);
        }
    }

    /* synchronized */@Override
    public void UpdatePoints(final double x, final Waveform curr_w) {
        this.UpdatePoints(x, Double.NaN, curr_w);
    }

    synchronized public void updateWaveforms() {
        Waveform w;
        for(int i = 0; i < this.getGridComponentCount(); i++){
            w = this.GetWavePanel(i);
            if(w != null) w.Update();
        }
    }
}

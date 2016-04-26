package jScope;

/* $Id$ */
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import debug.DEBUG;

public class WavePopup extends JPopupMenu implements ItemListener{
    static final long serialVersionUID = 247273265246434L;

    protected static void SelectListItem(final ButtonGroup bg, final int idx) {
        int i;
        JRadioButtonMenuItem b = null;
        Enumeration<AbstractButton> e;
        for(e = bg.getElements(), i = 0; e.hasMoreElements() && i <= idx; i++)
            b = (JRadioButtonMenuItem)e.nextElement();
        if(b != null) bg.setSelected(b.getModel(), true);
    }
    ColorMapDialog                 colorMapDialog = null;
    protected JMenu                markerList, colorList, markerStep, mode_2d, mode_1d;
    protected ButtonGroup          markerList_bg, colorList_bg, markerStep_bg, mode_2d_bg, mode_1d_bg;
    protected Container            parent;
    protected JMenuItem            playFrame, remove_panel, set_point, undo_zoom, maximize, cb_copy, profile_dialog, colorMap, saveAsText;
    protected JRadioButtonMenuItem plot_line, plot_no_line, plot_step;
    protected JRadioButtonMenuItem plot_y_time, plot_x_y, plot_contour, plot_image;
    protected JSeparator           sep1, sep2, sep3;
    protected JMenuItem            setup, autoscale, autoscaleY, autoscaleAll, autoscaleAllY, allSameScale, allSameXScale, allSameXScaleAutoY, allSameYScale, resetScales, resetAllScales;
    protected SetupWaveformParams  setup_params;
    protected Waveform             wave           = null;

    public WavePopup(){
        this(null);
    }

    public WavePopup(final SetupWaveformParams setup_params){
        this.setup = new JMenuItem("Set Limits...");
        this.setup.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                WavePopup.this.ShowDialog();
            }
        });
        this.setup_params = setup_params;
        this.remove_panel = new JMenuItem("Remove panel");
        this.remove_panel.setEnabled(false);
        this.remove_panel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Object[] options = {"Yes", "No"};
                final int opt = JOptionPane.showOptionDialog(null, "Are you sure you want to remove this wave panel?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if(opt == JOptionPane.YES_OPTION) ((WaveformManager)WavePopup.this.parent).removePanel(WavePopup.this.wave);
            }
        });
        this.maximize = new JMenuItem("Maximize Panel");
        this.maximize.setEnabled(false);
        this.maximize.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(((WaveformManager)WavePopup.this.parent).isMaximize()) ((WaveformManager)WavePopup.this.parent).maximizeComponent(null);
                else((WaveformManager)WavePopup.this.parent).maximizeComponent(WavePopup.this.wave);
            }
        });
        this.set_point = new JMenuItem("Set Point");
        this.set_point.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                WavePopup.this.SetDeselectPoint(WavePopup.this.wave);
            }
        });
        this.markerList = new JMenu("Markers");
        JRadioButtonMenuItem ob;
        this.markerList_bg = new ButtonGroup();
        for(int i = 0; i < Signal.markerList.length; i++){
            this.markerList_bg.add(ob = new JRadioButtonMenuItem(Signal.markerList[i]));
            ob.getModel().setActionCommand("MARKER " + i);
            this.markerList.add(ob);
            ob.addItemListener(this);
        }
        this.markerList.setEnabled(false);
        this.markerStep_bg = new ButtonGroup();
        this.markerStep = new JMenu("Marker step");
        for(int i = 0; i < Signal.markerStepList.length; i++){
            this.markerStep_bg.add(ob = new JRadioButtonMenuItem("" + Signal.markerStepList[i]));
            ob.getModel().setActionCommand("MARKER_STEP " + i);
            this.markerStep.add(ob);
            ob.addItemListener(this);
        }
        this.markerStep.setEnabled(false);
        this.colorList = new JMenu("Colors");
        this.colorList.setEnabled(false);
        this.mode_1d_bg = new ButtonGroup();
        this.mode_1d = new JMenu("Mode Plot 1D");
        this.mode_1d.add(this.plot_line = new JRadioButtonMenuItem("Line"));
        this.mode_1d_bg.add(this.plot_line);
        this.plot_line.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) WavePopup.this.SetMode1D(Signal.MODE_LINE);
            }
        });
        this.mode_1d.add(this.plot_no_line = new JRadioButtonMenuItem("No Line"));
        this.mode_1d_bg.add(this.plot_no_line);
        this.plot_no_line.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) WavePopup.this.SetMode1D(Signal.MODE_NOLINE);
            }
        });
        this.mode_1d.add(this.plot_step = new JRadioButtonMenuItem("Step Plot"));
        this.mode_1d_bg.add(this.plot_step);
        this.plot_step.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) WavePopup.this.SetMode1D(Signal.MODE_STEP);
                // wave.Update();
            }
        });
        this.mode_2d_bg = new ButtonGroup();
        this.mode_2d = new JMenu("signal 2D");
        this.mode_2d.add(this.plot_y_time = new JRadioButtonMenuItem("Plot xz(y)"));
        this.mode_2d_bg.add(this.plot_y_time);
        this.plot_y_time.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) WavePopup.this.SetMode2D(Signal.MODE_XZ);
            }
        });
        this.mode_2d.add(this.plot_x_y = new JRadioButtonMenuItem("Plot yz(x)"));
        this.mode_2d_bg.add(this.plot_x_y);
        this.plot_x_y.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) WavePopup.this.SetMode2D(Signal.MODE_YZ);
            }
        });
        this.mode_2d.add(this.plot_contour = new JRadioButtonMenuItem("Plot Contour"));
        this.mode_2d_bg.add(this.plot_contour);
        this.plot_contour.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    WavePopup.this.SetMode2D(Signal.MODE_CONTOUR);
                }
            }
        });
        this.mode_2d.add(this.plot_image = new JRadioButtonMenuItem("Plot Image"));
        this.mode_2d_bg.add(this.plot_image);
        this.plot_image.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    WavePopup.this.wave.setShowSigImage(true);
                    WavePopup.this.SetMode2D(Signal.MODE_IMAGE);
                }else WavePopup.this.wave.setShowSigImage(false);
            }
        });
        this.sep1 = new JSeparator();
        this.sep2 = new JSeparator();
        this.autoscale = new JMenuItem("Autoscale");
        this.autoscale.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                WavePopup.this.wave.Autoscale();
            }
        });
        this.autoscaleY = new JMenuItem("Autoscale Y");
        this.autoscaleY.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                WavePopup.this.wave.AutoscaleY();
            }
        });
        this.autoscaleAll = new JMenuItem("Autoscale all");
        this.autoscaleAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
        this.autoscaleAll.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(WavePopup.this.wave.IsImage()) ((WaveformManager)WavePopup.this.parent).AutoscaleAllImages();
                else((WaveformManager)WavePopup.this.parent).AutoscaleAll();
            }
        });
        this.autoscaleAllY = new JMenuItem("Autoscale all Y");
        this.autoscaleAllY.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        this.autoscaleAllY.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ((WaveformManager)WavePopup.this.parent).AutoscaleAllY();
            }
        });
        this.allSameScale = new JMenuItem("All same scale");
        this.allSameScale.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ((WaveformManager)WavePopup.this.parent).AllSameScale(WavePopup.this.wave);
            }
        });
        this.allSameXScale = new JMenuItem("All same X scale");
        this.allSameXScale.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ((WaveformManager)WavePopup.this.parent).AllSameXScale(WavePopup.this.wave);
            }
        });
        this.allSameXScaleAutoY = new JMenuItem("All same X scale (auto Y)");
        this.allSameXScaleAutoY.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ((WaveformManager)WavePopup.this.parent).AllSameXScaleAutoY(WavePopup.this.wave);
            }
        });
        this.allSameYScale = new JMenuItem("All same Y scale");
        this.allSameYScale.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ((WaveformManager)WavePopup.this.parent).AllSameYScale(WavePopup.this.wave);
            }
        });
        this.resetScales = new JMenuItem("Reset scales");
        this.resetScales.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                WavePopup.this.wave.ResetScales();
            }
        });
        this.resetAllScales = new JMenuItem("Reset all scales");
        this.resetAllScales.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ((WaveformManager)WavePopup.this.parent).ResetAllScales();
            }
        });
        this.undo_zoom = new JMenuItem("Undo Zoom");
        this.undo_zoom.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                WavePopup.this.wave.undoZoom();
            }
        });
        this.cb_copy = new JMenuItem("Copy to Clipboard");
        this.cb_copy.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(DEBUG.D) System.out.println("actionPerformed" + e);
                final Dimension dim = WavePopup.this.wave.getSize();
                final BufferedImage ri = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
                final Graphics2D g2d = (Graphics2D)ri.getGraphics();
                g2d.setBackground(Color.white);
                WavePopup.this.wave.paint(g2d, dim, Waveform.PRINT);
                try{
                    final ImageTransferable imageTransferable = new ImageTransferable(ri);
                    final Clipboard cli = Toolkit.getDefaultToolkit().getSystemClipboard();
                    cli.setContents(imageTransferable, imageTransferable);
                }catch(final Exception exc){
                    System.err.println("Exception " + exc);
                }
            }
        });
        this.playFrame = new JMenuItem();
        this.playFrame.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(WavePopup.this.wave.Playing()) WavePopup.this.wave.StopFrame();
                else WavePopup.this.wave.PlayFrame();
            }
        });
        this.profile_dialog = new JMenuItem("Show profile dialog");
        this.profile_dialog.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                WavePopup.this.wave.ShowProfileDialog();
            }
        });
        this.colorMap = new JMenuItem("Color Palette");
        this.colorMap.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                WavePopup.this.ShowColorMapDialog(WavePopup.this.wave);
            }
        });
        this.sep3 = new JSeparator();
        this.saveAsText = new JMenuItem("Save as text ...");
        this.saveAsText.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ((WaveformContainer)WavePopup.this.parent).SaveAsText(WavePopup.this.wave, false);
            }
        });
    }

    protected void InitColorMenu() {
        if(!Waveform.isColorsChanged() && this.colorList_bg != null) return;
        if(this.colorList.getItemCount() != 0) this.colorList.removeAll();
        final String[] colors_name = Waveform.getColorsName();
        JRadioButtonMenuItem ob = null;
        this.colorList_bg = new ButtonGroup();
        if(colors_name != null){
            for(int i = 0; i < colors_name.length; i++){
                this.colorList.add(ob = new JRadioButtonMenuItem(colors_name[i]));
                ob.getModel().setActionCommand("COLOR_LIST " + i);
                this.colorList_bg.add(ob);
                ob.addItemListener(this);
            }
        }
    }

    protected void InitOptionMenu() {
        final boolean state = (this.wave.GetShowSignalCount() == 1);
        this.markerList.setEnabled(state);
        this.colorList.setEnabled(state);
        this.set_point.setEnabled(true);
        if(state){
            final boolean state_m = (this.wave.GetMarker() != Signal.NONE);
            this.markerStep.setEnabled(state_m);
            WavePopup.SelectListItem(this.markerList_bg, this.wave.GetMarker());
            int st;
            for(st = 0; st < Signal.markerStepList.length; st++)
                if(Signal.markerStepList[st] == this.wave.GetMarkerStep()) break;
            WavePopup.SelectListItem(this.markerStep_bg, st);
            WavePopup.SelectListItem(this.colorList_bg, this.wave.GetColorIdx());
        }else this.markerStep.setEnabled(false);
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        final Object target = e.getSource();
        if(target instanceof JRadioButtonMenuItem && e.getStateChange() == ItemEvent.SELECTED){
            final JRadioButtonMenuItem cb = (JRadioButtonMenuItem)target;
            final String action_cmd = cb.getModel().getActionCommand();
            if(action_cmd == null) return;
            final StringTokenizer act = new StringTokenizer(action_cmd);
            final String action = act.nextToken();
            final int idx = Integer.parseInt(act.nextToken());
            if(action.equals("MARKER")){
                this.SetMarker(idx);
                this.markerStep.setEnabled(!(this.wave.GetMarker() == Signal.NONE || this.wave.GetMarker() == Signal.POINT));
                // wave.Repaint(true);
                this.wave.ReportChanges();
                return;
            }
            if(action.equals("MARKER_STEP")){
                this.SetMarkerStep(Signal.markerStepList[idx]);
                this.wave.ReportChanges();
                return;
            }
            if(action.equals("COLOR_LIST")){
                this.SetColor(idx);
                this.wave.ReportChanges();
                return;
            }
        }
    }

    protected void SetColor(final int idx) {
        if(this.wave.GetColorIdx() != idx) this.wave.SetColorIdx(idx);
    }

    public void setColorMapDialog(final ColorMapDialog colorMapDialog) {
        this.colorMapDialog = colorMapDialog;
    }

    public void SetDeselectPoint(final Waveform w) {
        if(w.ShowMeasure()){
            if(this.parent != null && this.parent instanceof WaveformManager) ((WaveformManager)this.parent).SetShowMeasure(false);
            w.SetShowMeasure(false);
        }else{
            if(this.parent != null && this.parent instanceof WaveformManager) ((WaveformManager)this.parent).SetShowMeasure(true);
            w.SetShowMeasure(true);
            w.SetPointMeasure();
        }
        w.repaint();
    }

    protected void SetImageMenu() {
        this.SetMenuItem(true);
        final boolean state = (this.wave.frames != null && this.wave.frames.getNumFrame() != 0);
        this.colorList.setEnabled(state);
        WavePopup.SelectListItem(this.colorList_bg, this.wave.GetColorIdx());
        this.playFrame.setEnabled(state);
        this.set_point.setEnabled(state && ((this.wave.mode == Waveform.MODE_POINT)));
        this.profile_dialog.setEnabled(!this.wave.isSendProfile());
    }

    protected void SetMarker(final int idx) {
        if(this.wave.GetMarker() != idx) this.wave.SetMarker(idx);
    }

    protected void SetMarkerStep(final int step) {
        if(this.wave.GetMarkerStep() != step) this.wave.SetMarkerStep(step);
    }

    protected void SetMenu() {
        this.InitColorMenu();
        if(this.wave.is_image) this.SetImageMenu();
        else this.SetSignalMenu();
        if(this.parent != null && this.parent instanceof WaveformManager) this.remove_panel.setEnabled(((WaveformManager)this.parent).GetWaveformCount() > 1);
    }

    protected void SetMenuItem(final boolean is_image) {
        if(this.getComponentCount() != 0) this.removeAll();
        if(this.parent != null && this.parent instanceof WaveformManager){
            if(((WaveformManager)this.parent).isMaximize()){
                this.maximize.setText("Show All Panels");
            }else{
                this.maximize.setText("Maximize Panel");
            }
        }
        if(is_image){
            this.add(this.setup);
            this.colorList.setText("Colors");
            if(this.parent != null && this.parent instanceof WaveformManager){
                this.add(this.maximize);
                this.add(this.remove_panel);
            }
            this.add(this.colorList);
            this.add(this.colorMap);
            this.add(this.playFrame);
            this.add(this.set_point);
            this.add(this.sep2);
            this.add(this.autoscale);
            if(this.parent != null && this.parent instanceof WaveformManager){
                this.autoscaleAll.setText("Autoscale all images");
                this.add(this.autoscaleAll);
                this.maximize.setEnabled(((WaveformManager)this.parent).GetWaveformCount() > 1);
            }
            this.set_point.setEnabled((this.wave.mode == Waveform.MODE_POINT));
        }else{
            this.add(this.setup);
            this.setup.setEnabled((this.setup_params != null));
            this.add(this.set_point);
            this.set_point.setEnabled((this.wave.mode == Waveform.MODE_POINT));
            this.add(this.sep1);
            this.add(this.markerList);
            this.add(this.markerStep);
            this.colorList.setText("Colors");
            this.add(this.colorList);
            if(this.wave.mode == Waveform.MODE_POINT || this.wave.GetShowSignalCount() == 1){
                if(this.wave.getSignalType() == Signal.TYPE_1D || (this.wave.getSignalType() == Signal.TYPE_2D && (this.wave.getSignalMode2D() == Signal.MODE_XZ || this.wave.getSignalMode2D() == Signal.MODE_YZ))){
                    this.add(this.mode_1d);
                    switch(this.wave.getSignalMode1D()){
                        case Signal.MODE_LINE:
                            this.mode_1d_bg.setSelected(this.plot_line.getModel(), true);
                            break;
                        case Signal.MODE_NOLINE:
                            this.mode_1d_bg.setSelected(this.plot_no_line.getModel(), true);
                            break;
                        case Signal.MODE_STEP:
                            this.mode_1d_bg.setSelected(this.plot_step.getModel(), true);
                            break;
                    }
                }
                if(this.wave.getSignalType() == Signal.TYPE_2D){
                    this.add(this.colorMap);
                    this.add(this.mode_2d);
                    this.mode_2d.setEnabled(this.wave.getSignalMode2D() != Signal.MODE_PROFILE);
                    switch(this.wave.getSignalMode2D()){
                        case Signal.MODE_XZ:
                            this.mode_2d_bg.setSelected(this.plot_y_time.getModel(), true);
                            break;
                        case Signal.MODE_YZ:
                            this.mode_2d_bg.setSelected(this.plot_x_y.getModel(), true);
                            break;
                        case Signal.MODE_CONTOUR:
                            this.mode_2d_bg.setSelected(this.plot_contour.getModel(), true);
                            break;
                        case Signal.MODE_IMAGE:
                            this.mode_2d_bg.setSelected(this.plot_image.getModel(), true);
                            break;
                    }
                    this.plot_image.setEnabled(!this.wave.IsShowSigImage());
                }
            }
            this.add(this.sep2);
            this.add(this.autoscale);
            this.add(this.autoscaleY);
            if(this.parent != null && this.parent instanceof WaveformManager){
                this.insert(this.maximize, 1);
                this.insert(this.remove_panel, 2);
                this.autoscaleAll.setText("Autoscale all");
                this.add(this.autoscaleAll);
                this.add(this.autoscaleAllY);
                this.add(this.allSameScale);
                this.add(this.allSameXScale);
                this.add(this.allSameXScaleAutoY);
                this.add(this.allSameYScale);
                this.add(this.resetAllScales);
                this.maximize.setEnabled(((WaveformManager)this.parent).GetWaveformCount() > 1);
            }
            this.add(this.resetScales);
            this.add(this.undo_zoom);
            // Copy image to clipborad can be done only with
            // java release 1.4
            // if(System.getProperty("java.version").indexOf("1.4") != -1)
            {
                this.add(this.cb_copy);
            }
            this.add(this.sep3);
            this.add(this.saveAsText);
        }
    }

    protected void SetMenuLabel() {
        if(!this.wave.IsImage()){
            if(this.wave.ShowMeasure()){
                this.set_point.setText("Deselect Point");
            }else this.set_point.setText("Set Point");
        }else{
            if(this.wave.ShowMeasure()) // && wave.sendProfile())
            this.set_point.setText("Deselect Point");
            else this.set_point.setText("Set Point");
            if(this.wave.is_playing) this.playFrame.setText("Stop play");
            else this.playFrame.setText("Start play");
        }
    }

    protected void SetMode1D(final int mode) {
        this.wave.setSignalMode1D(mode);
    }

    protected void SetMode2D(final int mode) {
        this.wave.setSignalMode2D(mode);
    }

    public void setParent(final Container parent) {
        this.parent = parent;
    }

    protected void SetSignalMenu() {
        this.SetMenuItem(false);
        if(this.wave.GetShowSignalCount() != 0){
            this.InitOptionMenu();
        }else{
            this.markerList.setEnabled(false);
            this.colorList.setEnabled(false);
            this.markerStep.setEnabled(false);
            this.set_point.setEnabled(false);
        }
        this.undo_zoom.setEnabled(this.wave.undoZoomPendig());
    }

    public void Show(final Waveform wave, final Point p) {
        // parent = (Container)this.getParent();
        // if(wave != w)
        {
            this.wave = wave;
            this.SetMenu();
        }
        // else
        // if(!w.IsImage())
        // InitOptionMenu();
        this.SetMenuLabel();
        this.show(wave, p.x, p.y);
    }

    public void ShowColorMapDialog(final Waveform wave) {
        if(this.colorMapDialog == null) this.colorMapDialog = new ColorMapDialog(null, null);
        else this.colorMapDialog.setWave(wave);
        this.colorMapDialog.setLocationRelativeTo(wave);
        this.colorMapDialog.setVisible(true);
    }

    protected void ShowDialog() {
        if(this.setup_params != null) this.setup_params.Show(this.wave);
    }
}

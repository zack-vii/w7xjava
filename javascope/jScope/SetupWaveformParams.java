package jScope;

/* $Id$ */
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

final public class SetupWaveformParams extends JDialog implements ActionListener{
    static final long serialVersionUID = 2324834354383L;

    private static float convertToFloat(final String s, final boolean min) {
        try{
            return Float.parseFloat(s);
        }catch(final Exception exc){
            return min ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        }
    }

    private static void setTextValue(final JTextField t, final String val) {
        if(val != null){
            t.setText(val);
        }
    }
    private final JComboBox grid_mode, legend_mode;
    JLabel                  lab;
    JButton                 ok, cancel, reset, erase, apply;
    boolean                 reversed;
    private JCheckBox       reversed_b;
    JTextField              title;
    private JTextField      vertical_offset, horizontal_offset;
    Waveform                wave;
    int                     x_curr_lines_grid = 3, y_curr_lines_grid = 3;
    private JTextField      x_grid_lines, y_grid_lines;
    JTextField              x_max, x_min, x_label;
    JTextField              y_max, y_min, y_label;

    @SuppressWarnings("unchecked")
    public SetupWaveformParams(final Frame fw, final String frame_title){
        super(fw, frame_title, true);
        this.setModal(true);
        // GetPropertiesValue();
        final GridBagLayout gridbag = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        final Insets insets = new Insets(4, 4, 4, 4);
        this.getContentPane().setLayout(gridbag);
        c.insets = insets;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridwidth = 1;
        this.lab = new JLabel("Title");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.title = new JTextField(30);
        gridbag.setConstraints(this.title, c);
        this.getContentPane().add(this.title);
        c.gridwidth = 1;
        this.lab = new JLabel("Y Label");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        this.y_label = new JTextField(25);
        gridbag.setConstraints(this.y_label, c);
        this.getContentPane().add(this.y_label);
        this.lab = new JLabel("Y min");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        this.y_min = new JTextField(10);
        gridbag.setConstraints(this.y_min, c);
        this.getContentPane().add(this.y_min);
        this.lab = new JLabel("Y max");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.y_max = new JTextField(10);
        gridbag.setConstraints(this.y_max, c);
        this.getContentPane().add(this.y_max);
        c.gridwidth = 1;
        this.lab = new JLabel("X Label");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        this.x_label = new JTextField(25);
        gridbag.setConstraints(this.x_label, c);
        this.getContentPane().add(this.x_label);
        this.lab = new JLabel("X min");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        this.x_min = new JTextField(10);
        gridbag.setConstraints(this.x_min, c);
        this.getContentPane().add(this.x_min);
        this.lab = new JLabel("X max");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.x_max = new JTextField(10);
        gridbag.setConstraints(this.x_max, c);
        this.getContentPane().add(this.x_max);
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 3));
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("   lines x:"));
        panel.add(this.x_grid_lines = new JTextField(2));
        this.x_grid_lines.addActionListener(this);
        this.x_grid_lines.setText("" + this.x_curr_lines_grid);
        panel.add(new JLabel("   lines y:"));
        panel.add(this.y_grid_lines = new JTextField(2));
        this.y_grid_lines.addActionListener(this);
        this.y_grid_lines.setText("" + this.y_curr_lines_grid);
        panel.add(new JLabel("   Vertical offset:"));
        panel.add(this.vertical_offset = new JTextField(3));
        this.vertical_offset.addActionListener(this);
        this.vertical_offset.setText("");
        panel.add(new JLabel("   Horizontal offset:"));
        panel.add(this.horizontal_offset = new JTextField(3));
        this.horizontal_offset.addActionListener(this);
        this.horizontal_offset.setText("");
        gridbag.setConstraints(panel, c);
        this.getContentPane().add(panel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 3));
        c.fill = GridBagConstraints.NONE;
        panel1.add(this.reversed_b = new JCheckBox("Reversed"));
        this.reversed_b.setHorizontalTextPosition(SwingConstants.LEFT);
        this.lab = new JLabel("Grid: Mode");
        panel1.add(this.lab);
        this.grid_mode = new JComboBox(Grid.GRID_MODE);
        panel1.add(this.grid_mode);
        this.lab = new JLabel("Legend:");
        panel1.add(this.lab);
        this.legend_mode = new JComboBox();
        this.legend_mode.addItem("In Graphics");
        this.legend_mode.addItem("Fixed Bottom");
        this.legend_mode.addItem("Fixed Right");
        panel1.add(this.legend_mode);
        gridbag.setConstraints(panel1, c);
        this.getContentPane().add(panel1);
        final JPanel p1 = new JPanel();
        this.ok = new JButton("Ok");
        this.ok.addActionListener(this);
        p1.add(this.ok);
        this.apply = new JButton("Apply");
        this.apply.addActionListener(this);
        p1.add(this.apply);
        this.reset = new JButton("Reset");
        this.reset.addActionListener(this);
        p1.add(this.reset);
        this.erase = new JButton("Erase");
        this.erase.addActionListener(this);
        p1.add(this.erase);
        this.cancel = new JButton("Cancel");
        this.cancel.addActionListener(this);
        p1.add(this.cancel);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(p1, c);
        this.getContentPane().add(p1);
        this.pack();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object ob = e.getSource();
        if(ob == this.erase) this.eraseForm();
        if(ob == this.cancel) this.setVisible(false);
        if(ob == this.apply || ob == this.ok){
            this.SaveParameters();
            if(ob == this.ok) this.setVisible(false);
        }
        if(ob == this.reset){}
    }

    /*
        private int IsGridMode(String mode) {
            for(int i = 0; i < Grid.GRID_MODE.length; i++)
                if(Grid.GRID_MODE[i].equals(mode)) return i;
            return -1;
        }
     */
    /*
        private void GetPropertiesValue() {
            Properties js_prop = main_scope.js_prop;
            String prop;
            int val = 0;
            if(js_prop == null) return;
            prop = (String)js_prop.getProperty("jScope.reversed");
            if(prop != null && (prop.equals("true") || prop.equals("false")))
                reversed = new Boolean(prop).booleanValue();
            else   reversed = false;
            prop = (String)js_prop.getProperty("jScope.grid_mode");
            if(prop != null && (val = IsGridMode(prop)) > 0) curr_grid_mode = val;
            prop = (String)js_prop.getProperty("jScope.x_grid");
            if(prop != null){
                try{
                    val = Integer.parseInt(prop);
                    x_curr_lines_grid = val > Grid.MAX_GRID ? Grid.MAX_GRID : val;
                }catch(NumberFormatException e){}
            }
            prop = (String)js_prop.getProperty("jScope.y_grid");
            if(prop != null){
                try{
                    val = Integer.parseInt(prop);
                    y_curr_lines_grid = val > Grid.MAX_GRID ? Grid.MAX_GRID : val;
                }catch(NumberFormatException e){}
            }
        }
     */
    public void eraseForm() {
        this.title.setText("");
        this.x_label.setText("");
        this.x_max.setText("");
        this.x_min.setText("");
        this.y_max.setText("");
        this.y_min.setText("");
        this.y_label.setText("");
        this.grid_mode.setSelectedIndex(0);
        this.x_grid_lines.setText("3");
        this.y_grid_lines.setText("3");
        this.horizontal_offset.setText("0");
        this.vertical_offset.setText("0");
        this.reversed_b.setSelected(false);
    }

    private void initialize() {
        this.eraseForm();
        if(this.wave instanceof MultiWaveform && ((MultiWaveform)this.wave).getWaveInterface() != null){
            final WaveInterface wi = ((MultiWaveform)this.wave).getWaveInterface();
            if(wi.in_xmax != null) this.x_max.setText(wi.in_xmax);
            if(wi.in_xmin != null) this.x_min.setText(wi.in_xmin);
            if(wi.in_ymax != null) this.y_max.setText(wi.in_ymax);
            if(wi.in_ymin != null) this.y_min.setText(wi.in_ymin);
        }else{
            if(this.wave.lx_max != Float.POSITIVE_INFINITY) this.x_max.setText("" + this.wave.lx_max);
            if(this.wave.lx_min != Float.NEGATIVE_INFINITY) this.x_min.setText("" + this.wave.lx_min);
            if(this.wave.ly_max != Float.POSITIVE_INFINITY) this.y_max.setText("" + this.wave.ly_max);
            if(this.wave.ly_min != Float.NEGATIVE_INFINITY) this.y_min.setText("" + this.wave.ly_min);
        }
        SetupWaveformParams.setTextValue(this.title, this.wave.GetTitle());
        SetupWaveformParams.setTextValue(this.x_label, this.wave.GetXLabel());
        SetupWaveformParams.setTextValue(this.y_label, this.wave.GetYLabel());
        this.grid_mode.setSelectedIndex(this.wave.GetGridMode());
        if(this.wave instanceof MultiWaveform){
            this.legend_mode.setVisible(true);
            this.legend_mode.setSelectedIndex(((MultiWaveform)this.wave).getLegendMode());
        }else this.legend_mode.setVisible(false);
        SetupWaveformParams.setTextValue(this.x_grid_lines, "" + this.wave.GetGridStepX());
        SetupWaveformParams.setTextValue(this.y_grid_lines, "" + this.wave.GetGridStepX());
        this.reversed_b.setSelected(this.wave.IsReversed());
        this.horizontal_offset.setText("" + Waveform.GetHorizontalOffset());
        this.vertical_offset.setText("" + Waveform.GetVerticalOffset());
    }

    public void SaveParameters() {
        this.wave.SetTitle(this.title.getText());
        this.wave.SetXLabel(this.x_label.getText());
        this.wave.SetYLabel(this.y_label.getText());
        if(this.wave instanceof MultiWaveform && ((MultiWaveform)this.wave).getWaveInterface() != null){
            final WaveInterface wi = ((MultiWaveform)this.wave).getWaveInterface();
            wi.in_xmax = this.x_max.getText();
            wi.in_xmin = this.x_min.getText();
            wi.in_ymax = this.y_max.getText();
            wi.in_ymin = this.y_min.getText();
            try{
                wi.StartEvaluate();
                wi.setLimits();
            }catch(final Exception e){}
        }else{
            this.wave.lx_max = SetupWaveformParams.convertToFloat(this.x_max.getText(), false);
            this.wave.lx_min = SetupWaveformParams.convertToFloat(this.x_min.getText(), true);
            this.wave.ly_max = SetupWaveformParams.convertToFloat(this.y_max.getText(), false);
            this.wave.ly_min = SetupWaveformParams.convertToFloat(this.y_min.getText(), true);
            this.wave.setFixedLimits();
        }
        this.wave.SetGridMode(this.grid_mode.getSelectedIndex(), true, true);
        if(this.wave instanceof MultiWaveform){
            ((MultiWaveform)this.wave).setLegendMode(this.legend_mode.getSelectedIndex());
        }
        this.wave.SetReversed(this.reversed_b.getModel().isSelected());
        int h_ofs = 0, v_ofs = 0;
        try{
            h_ofs = new Integer(this.horizontal_offset.getText().trim()).intValue();
        }catch(final NumberFormatException exc){
            h_ofs = 0;
        }
        Waveform.SetHorizontalOffset(h_ofs);
        this.horizontal_offset.setText("" + h_ofs);
        try{
            v_ofs = new Integer(this.vertical_offset.getText().trim()).intValue();
        }catch(final NumberFormatException exc){
            v_ofs = 0;
        }
        Waveform.SetVerticalOffset(v_ofs);
        this.vertical_offset.setText("" + v_ofs);
        try{
            this.x_curr_lines_grid = new Integer(this.x_grid_lines.getText().trim()).intValue();
        }catch(final NumberFormatException exc){
            this.x_curr_lines_grid = 3;
        }
        if(this.x_curr_lines_grid > Grid.MAX_GRID) this.x_curr_lines_grid = Grid.MAX_GRID;
        try{
            this.y_curr_lines_grid = new Integer(this.y_grid_lines.getText().trim()).intValue();
        }catch(final NumberFormatException exc){
            this.y_curr_lines_grid = 3;
        }
        if(this.y_curr_lines_grid > Grid.MAX_GRID) this.y_curr_lines_grid = Grid.MAX_GRID;
        this.wave.SetGridSteps(this.x_curr_lines_grid, this.y_curr_lines_grid);
        this.wave.Update();
    }

    public void Show(final Waveform w) {
        if(w == null) return;
        if(w.IsImage()){
            JOptionPane.showMessageDialog(this.getParent(), "Not yet implemented", "alert", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.wave = w;
        this.initialize();
        this.setLocationRelativeTo(this.wave.getParent());
        this.setVisible(true);
    }
}

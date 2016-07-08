package jscope;

/* $Id$ */
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public final class SetupDefaults extends JDialog implements ActionListener{
    private static int isGridMode(final String mode) {
        for(int i = 0; i < Grid.GRID_MODE.length; i++)
            if(Grid.GRID_MODE[i].equals(mode)) return i;
        return -1;
    }

    private static void setTextValue(final JTextField t, final String val) {
        if(val != null){
            t.setText(val);
        }
    }
    int                     curr_grid_mode = 0, x_curr_lines_grid = 3, y_curr_lines_grid = 3, curr_legend_mode = 0;
    JTextField              def_node, upd_event;
    jScopeDefaultValues     def_vals;
    private final JComboBox grid_mode, legend_mode, auto_color_mode;
    JLabel                  lab;
    jScopeFacade            main_scope;
    JButton                 ok, cancel, reset, erase, apply;
    boolean                 reversed;
    private JCheckBox       reversed_b;
    JTextField              title, shot, experiment;
    private final JCheckBox upd_limits;
    private JTextField      vertical_offset, horizontal_offset;
    private JTextField      x_grid_lines, y_grid_lines;
    JTextField              x_max, x_min, x_label;
    JTextField              y_max, y_min, y_label;

    @SuppressWarnings("unchecked")
    public SetupDefaults(final Frame fw, final String frame_title, final jScopeDefaultValues def_vals){
        super(fw, frame_title, true);
        this.setModal(true);
        this.main_scope = (jScopeFacade)fw;
        this.getPropertiesValue();
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
        c.gridwidth = 1;
        this.lab = new JLabel("Experiment");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        this.experiment = new JTextField(25);
        gridbag.setConstraints(this.experiment, c);
        this.getContentPane().add(this.experiment);
        this.lab = new JLabel("Shot");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.shot = new JTextField(30);
        gridbag.setConstraints(this.shot, c);
        this.getContentPane().add(this.shot);
        c.gridwidth = 1;
        this.lab = new JLabel("Update event");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        this.upd_event = new JTextField(25);
        gridbag.setConstraints(this.upd_event, c);
        this.getContentPane().add(this.upd_event);
        this.lab = new JLabel("Default node");
        gridbag.setConstraints(this.lab, c);
        this.getContentPane().add(this.lab);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.def_node = new JTextField(30);
        gridbag.setConstraints(this.def_node, c);
        this.getContentPane().add(this.def_node);
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
        final String grd[] = {"Dotted", "Gray"};
        this.grid_mode = new JComboBox(grd);// Grid.GRID_MODE);
        this.grid_mode.setSelectedIndex(this.curr_grid_mode);
        panel1.add(this.grid_mode);
        this.lab = new JLabel("Legend:");
        panel1.add(this.lab);
        this.legend_mode = new JComboBox();
        this.legend_mode.addItem("In Graphics");
        this.legend_mode.addItem("Fixed Bottom");
        this.legend_mode.addItem("Fixed Right");
        this.legend_mode.setSelectedIndex(this.curr_legend_mode);
        panel1.add(this.legend_mode);
        this.lab = new JLabel("Auto color:");
        panel1.add(this.lab);
        this.auto_color_mode = new JComboBox();
        this.auto_color_mode.addItem("on shot");
        this.auto_color_mode.addItem("on expression");
        this.auto_color_mode.setSelectedIndex(WaveInterface.auto_color_on_expr ? 1 : 0);
        panel1.add(this.auto_color_mode);
        this.upd_limits = new JCheckBox("Upd. limits", true);
        panel1.add(this.upd_limits);
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
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object ob = e.getSource();
        if(ob == this.erase) this.eraseForm();
        if(ob == this.cancel) this.setVisible(false);
        if(ob == this.apply || ob == this.ok){
            if(ob == this.ok) this.setVisible(false);
            this.main_scope.updateDefaultValues();
        }
        if(ob == this.reset){
            this.initialize();
        }
    }

    public void eraseForm() {
        this.title.setText("");
        this.x_label.setText("");
        this.x_max.setText("");
        this.x_min.setText("");
        this.y_max.setText("");
        this.y_min.setText("");
        this.y_label.setText("");
        this.experiment.setText("");
        this.shot.setText("");
        this.upd_event.setText("");
        this.def_node.setText("");
        this.grid_mode.setSelectedIndex(0);
        this.x_grid_lines.setText("3");
        this.y_grid_lines.setText("3");
        this.horizontal_offset.setText("0");
        this.vertical_offset.setText("0");
        this.reversed_b.setSelected(false);
        this.upd_limits.setSelected(true);
    }

    public int getGridMode() {
        return this.curr_grid_mode;
    }

    public int getLegendMode() {
        return this.curr_legend_mode;
    }

    void getPropertiesValue() {
        final Properties js_prop = this.main_scope.js_prop;
        String prop;
        int val = 0;
        if(js_prop == null) return;
        prop = js_prop.getProperty("jScope.reversed");
        if(prop != null) this.reversed = Boolean.parseBoolean(prop);
        prop = js_prop.getProperty("jScope.grid_mode");
        if(prop != null && (val = SetupDefaults.isGridMode(prop)) > 0) this.curr_grid_mode = val;
        prop = js_prop.getProperty("jScope.x_grid");
        if(prop != null){
            try{
                val = Integer.parseInt(prop);
                this.x_curr_lines_grid = val > Grid.MAX_GRID ? Grid.MAX_GRID : val;
            }catch(final NumberFormatException e){}
        }
        prop = js_prop.getProperty("jScope.y_grid");
        if(prop != null){
            try{
                val = Integer.parseInt(prop);
                this.y_curr_lines_grid = val > Grid.MAX_GRID ? Grid.MAX_GRID : val;
            }catch(final NumberFormatException e){}
        }
    }

    public boolean getReversed() {
        return this.reversed;
    }

    public int getXLines() {
        return this.x_curr_lines_grid;
    }

    public int getYLines() {
        return this.y_curr_lines_grid;
    }

    private void initialize() {
        this.eraseForm();
        SetupDefaults.setTextValue(this.title, this.def_vals.title_str);
        SetupDefaults.setTextValue(this.y_label, this.def_vals.ylabel);
        SetupDefaults.setTextValue(this.x_label, this.def_vals.xlabel);
        SetupDefaults.setTextValue(this.y_max, this.def_vals.ymax);
        SetupDefaults.setTextValue(this.y_min, this.def_vals.ymin);
        SetupDefaults.setTextValue(this.x_max, this.def_vals.xmax);
        SetupDefaults.setTextValue(this.x_min, this.def_vals.xmin);
        SetupDefaults.setTextValue(this.experiment, this.def_vals.experiment_str);
        SetupDefaults.setTextValue(this.shot, this.def_vals.shot_str);
        SetupDefaults.setTextValue(this.upd_event, this.def_vals.upd_event_str);
        SetupDefaults.setTextValue(this.def_node, this.def_vals.def_node_str);
        this.reversed_b.setSelected(this.def_vals.reversed);
        this.upd_limits.setSelected(this.def_vals.upd_limits);
        this.grid_mode.setSelectedIndex(this.curr_grid_mode);
        this.legend_mode.setSelectedIndex(this.curr_legend_mode);
        this.x_grid_lines.setText("" + this.x_curr_lines_grid);
        this.y_grid_lines.setText("" + this.y_curr_lines_grid);
        this.horizontal_offset.setText("" + Waveform.getHorizontalOffset());
        this.vertical_offset.setText("" + Waveform.getVerticalOffset());
    }

    public boolean isChanged(final jScopeDefaultValues def_vals) {
        if(!jScopeFacade.equalsString(this.shot.getText(), def_vals.shot_str)) return true;
        if(!jScopeFacade.equalsString(this.experiment.getText(), def_vals.experiment_str)) return true;
        if(!jScopeFacade.equalsString(this.upd_event.getText(), def_vals.upd_event_str)) return true;
        if(!jScopeFacade.equalsString(this.def_node.getText(), def_vals.def_node_str)) return true;
        if(!jScopeFacade.equalsString(this.title.getText(), def_vals.title_str)) return true;
        if(!jScopeFacade.equalsString(this.x_max.getText(), def_vals.xmax)) return true;
        if(!jScopeFacade.equalsString(this.x_min.getText(), def_vals.xmin)) return true;
        if(!jScopeFacade.equalsString(this.x_label.getText(), def_vals.xlabel)) return true;
        if(!jScopeFacade.equalsString(this.y_max.getText(), def_vals.ymax)) return true;
        if(!jScopeFacade.equalsString(this.y_min.getText(), def_vals.ymin)) return true;
        if(!jScopeFacade.equalsString(this.y_label.getText(), def_vals.ylabel)) return true;
        if(this.reversed_b.isSelected() != def_vals.reversed) return true;
        if(this.upd_limits.isSelected() != def_vals.upd_limits) return true;
        return false;
    }

    public void saveDefaultConfiguration(final jScopeDefaultValues def_vals) {
        def_vals.experiment_str = new String(this.experiment.getText());
        def_vals.shot_str = new String(this.shot.getText());
        def_vals.xmax = new String(this.x_max.getText());
        def_vals.xmin = new String(this.x_min.getText());
        def_vals.ymax = new String(this.y_max.getText());
        def_vals.ymin = new String(this.y_min.getText());
        def_vals.title_str = new String(this.title.getText());
        def_vals.xlabel = new String(this.x_label.getText());
        def_vals.ylabel = new String(this.y_label.getText());
        def_vals.upd_event_str = new String(this.upd_event.getText());
        def_vals.def_node_str = new String(this.def_node.getText());
        def_vals.upd_limits = this.upd_limits.isSelected();
        this.curr_grid_mode = this.grid_mode.getSelectedIndex();
        this.curr_legend_mode = this.legend_mode.getSelectedIndex();
        this.reversed = this.reversed_b.isSelected();
        int h_ofs = 0, v_ofs = 0;
        try{
            h_ofs = new Integer(this.horizontal_offset.getText().trim()).intValue();
        }catch(final NumberFormatException exc){
            h_ofs = 0;
        }
        Waveform.setHorizontalOffset(h_ofs);
        this.horizontal_offset.setText("" + h_ofs);
        try{
            v_ofs = new Integer(this.vertical_offset.getText().trim()).intValue();
        }catch(final NumberFormatException exc){
            v_ofs = 0;
        }
        Waveform.setVerticalOffset(v_ofs);
        this.vertical_offset.setText("" + v_ofs);
        if(this.auto_color_mode.getSelectedIndex() == 0) WaveInterface.auto_color_on_expr = false;
        else WaveInterface.auto_color_on_expr = true;
        try{
            this.x_curr_lines_grid = new Integer(this.x_grid_lines.getText().trim()).intValue();
        }catch(final NumberFormatException exc){
            this.x_curr_lines_grid = 3;
        }
        if(this.x_curr_lines_grid > Grid.MAX_GRID) this.x_curr_lines_grid = Grid.MAX_GRID;
        this.x_grid_lines.setText("" + this.x_curr_lines_grid);
        try{
            this.y_curr_lines_grid = new Integer(this.y_grid_lines.getText().trim()).intValue();
        }catch(final NumberFormatException exc){
            this.y_curr_lines_grid = 3;
        }
        if(this.y_curr_lines_grid > Grid.MAX_GRID) this.y_curr_lines_grid = Grid.MAX_GRID;
        this.y_grid_lines.setText("" + this.y_curr_lines_grid);
        def_vals.setIsEvaluated(false);
    }

    public void show(final Frame f, final jScopeDefaultValues def_vals) {
        this.def_vals = def_vals;
        this.initialize();
        this.pack();
        this.setLocationRelativeTo(f);
        this.setVisible(true);
    }
}

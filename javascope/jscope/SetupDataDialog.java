package jscope;

/* $Id$ */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
final class SetupDataDialog extends JDialog implements ActionListener, ItemListener, KeyListener, WaveformListener{
    class Data{
        int     color_idx;
        boolean interpolate;
        String  label;
        String  low_err;
        int     marker;
        int     marker_step;
        int     mode1D;
        int     mode2D;
        long    shot;
        String  up_err;
        String  x_expr;
        String  y_expr;

        public void copy(final Data ws) {
            if(ws.label != null) this.label = new String(ws.label);
            if(ws.x_expr != null) this.x_expr = new String(ws.x_expr);
            if(ws.y_expr != null) this.y_expr = new String(ws.y_expr);
            if(ws.up_err != null) this.up_err = new String(ws.up_err);
            if(ws.low_err != null) this.low_err = new String(ws.low_err);
            this.shot = ws.shot;
            this.color_idx = ws.color_idx;
            this.interpolate = ws.interpolate;
            this.marker = ws.marker;
            this.marker_step = ws.marker_step;
        }

        public boolean equals(final Data ws) {
            if(this.x_expr != null){
                if(!this.x_expr.equals(ws.x_expr)) return false;
            }else if(ws.x_expr != null && ws.x_expr.length() != 0) return false;
            if(this.y_expr != null){
                if(!this.y_expr.equals(ws.y_expr)) return false;
            }else if(ws.y_expr != null && ws.y_expr.length() != 0) return false;
            if(this.label != null){
                if(!this.label.equals(ws.label)) return false;
            }else if(ws.label != null && ws.label.length() != 0) return false;
            return true;
        }
    }
    class ExpandExp extends JDialog implements ActionListener{
        private final SetupDataDialog conf_dialog;
        private final JLabel          lab_x, lab_y;
        private final JButton         ok, cancel;
        private final JTextArea       x_expr, y_expr;

        ExpandExp(final Frame _fw, final SetupDataDialog conf_diag){
            super(_fw, "Expand Expression Dialog", false);
            this.setModal(true);
            this.conf_dialog = conf_diag;
            this.getContentPane().setLayout(new BorderLayout());
            final JPanel p1 = new JPanel();
            p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
            final JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            this.lab_y = new JLabel("Y Expression:");
            p2.add(this.lab_y);
            p1.add(p2);
            this.y_expr = new JTextArea(50, 20);
            JScrollPane scroller = new JScrollPane(this.y_expr);
            p1.add(scroller);
            final JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            this.lab_x = new JLabel("X Expression:");
            p3.add(this.lab_x);
            p1.add(p3);
            this.x_expr = new JTextArea(50, 20);
            scroller = new JScrollPane(this.x_expr);
            p1.add(scroller);
            final JPanel p = new JPanel();
            p.setLayout(new FlowLayout(FlowLayout.CENTER));
            this.ok = new JButton("Ok");
            this.ok.addActionListener(this);
            p.add(this.ok);
            this.cancel = new JButton("Cancel");
            this.cancel.addActionListener(this);
            p.add(this.cancel);
            this.getContentPane().add("Center", p1);
            this.getContentPane().add("South", p);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Object ob = e.getSource();
            if(ob == this.ok){
                this.conf_dialog.x_expr.setText(this.x_expr.getText());
                this.conf_dialog.y_expr.setText(this.y_expr.getText());
                SetupDataDialog.this.updateDataSetup();
            }
            this.setVisible(false);
        }

        /**
         ** Set values of x and/or y expression field
         */
        public void setExpressionString(final String x, final String y) {
            if(SetupDataDialog.this.image_b.isSelected()){
                this.lab_x.setText("Times Expression:");
                this.lab_y.setText("Frames Expression:");
            }else{
                this.lab_x.setText("X Expression:");
                this.lab_y.setText("Y Expression:");
            }
            if(x != null) this.x_expr.setText(x);
            if(y != null) this.y_expr.setText(y);
        }
    }
    class SError extends JDialog implements ActionListener{
        private final JTextField e_up, e_low;
        private final JButton    ok, cancel;
        private Data             ws;

        SError(final Frame fw){
            super(fw, "Error Setup", true);
            JLabel label;
            final GridBagConstraints c = new GridBagConstraints();
            final GridBagLayout gridbag = new GridBagLayout();
            this.getContentPane().setLayout(gridbag);
            c.insets = new Insets(4, 4, 4, 4);
            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = GridBagConstraints.BOTH;
            label = new JLabel("Error up");
            gridbag.setConstraints(label, c);
            this.getContentPane().add(label);
            c.gridwidth = GridBagConstraints.REMAINDER;
            this.e_up = new JTextField(40);
            gridbag.setConstraints(this.e_up, c);
            this.getContentPane().add(this.e_up);
            c.gridwidth = GridBagConstraints.BOTH;
            label = new JLabel("Error low");
            gridbag.setConstraints(label, c);
            this.getContentPane().add(label);
            c.gridwidth = GridBagConstraints.REMAINDER;
            this.e_low = new JTextField(40);
            gridbag.setConstraints(this.e_low, c);
            this.getContentPane().add(this.e_low);
            final JPanel p = new JPanel();
            p.setLayout(new FlowLayout(FlowLayout.CENTER));
            this.ok = new JButton("Ok");
            this.ok.addActionListener(this);
            p.add(this.ok);
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
            if(ob == this.ok){
                this.ws.up_err = new String(this.e_up.getText());
                this.ws.low_err = new String(this.e_low.getText());
                this.setVisible(false);
            }
            if(ob == this.cancel) this.setVisible(false);
        }

        public void resetError() {
            this.e_up.setText("");
            this.e_low.setText("");
        }

        public void setError(final Data ws_in) {
            this.ws = ws_in;
            this.resetError();
            if(this.ws.up_err != null) this.e_up.setText(this.ws.up_err);
            if(this.ws.up_err != null) this.e_low.setText(this.ws.low_err);
        }
    }
    class SList extends JPanel implements ItemListener{
        // private DefaultListModel list_model = new DefaultListModel();
        private final DefaultListModel list_model    = new DefaultListModel();
        private int                    list_num_shot = 0;
        private JTextField             marker_step_t;
        private JComboBox              mode1D, mode2D, color, marker;
        private int                    sel_signal    = -1;
        private long                   shots[]       = null;
        private JList                  sig_list;
        private final Vector<Data>     signals       = new Vector<Data>();

        @SuppressWarnings("unchecked")
        public SList(){
            final BorderLayout bl = new BorderLayout(25, 1);
            this.setLayout(bl);
            SetupDataDialog.this.lab = new JLabel("Signals list");
            this.add("North", SetupDataDialog.this.lab);
            this.list_model.addElement("Select this item to add new expression");
            this.sig_list = new JList(this.list_model);
            final JScrollPane scroll_sig_list = new JScrollPane(this.sig_list);
            this.sig_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.sig_list.addListSelectionListener(new ListSelectionListener(){
                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    if(e.getValueIsAdjusting()) return;
                    SList.this.signalSelect(((JList)e.getSource()).getSelectedIndex() - 1);
                }
            });
            this.sig_list.addKeyListener(new KeyAdapter(){
                @Override
                public void keyPressed(final KeyEvent e) {
                    final char key = e.getKeyChar();
                    if(key == KeyEvent.VK_DELETE) SList.this.removeSignalSetup();
                }
            });
            this.add("Center", scroll_sig_list);
            final JPanel p = new JPanel(new GridLayout(4, 1));
            final GridBagLayout gridbag = new GridBagLayout();
            final GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = 1;
            c.gridheight = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 5, 5, 5);
            p.setLayout(gridbag);
            this.mode1D = new JComboBox();
            this.mode1D.addItem("Line");
            this.mode1D.addItem("No Line");
            this.mode1D.addItem("Step Plot");
            this.mode1D.addItemListener(this);
            gridbag.setConstraints(this.mode1D, c);
            p.add(this.mode1D);
            this.mode2D = new JComboBox();
            this.mode2D.addItem("y & time");
            this.mode2D.addItem("x & y");
            this.mode2D.addItem("y & x");
            this.mode2D.addItem("Image");
            this.mode2D.addItemListener(this);
            gridbag.setConstraints(this.mode2D, c);
            p.add(this.mode2D);
            this.color = new JComboBox();
            this.setColorList();
            this.color.addItemListener(this);
            gridbag.setConstraints(this.color, c);
            p.add(this.color);
            this.marker = new JComboBox();
            for(final String element : Signal.markerList)
                this.marker.addItem(element);
            this.marker.addItemListener(this);
            gridbag.setConstraints(this.marker, c);
            p.add(this.marker);
            c.gridwidth = 1;
            SetupDataDialog.this.lab = new JLabel("M.Step");
            gridbag.setConstraints(SetupDataDialog.this.lab, c);
            p.add(SetupDataDialog.this.lab);
            c.fill = GridBagConstraints.NONE;
            c.gridwidth = GridBagConstraints.REMAINDER;
            this.marker_step_t = new JTextField(3);
            this.marker_step_t.addFocusListener(new FocusAdapter(){
                @Override
                public void focusLost(final FocusEvent e) {
                    if(SList.this.getSignalSelect() != -1){
                        try{
                            SList.this.signals.elementAt(SList.this.getSignalSelect()).marker_step = new Integer(SList.this.marker_step_t.getText()).intValue();
                        }catch(final NumberFormatException ex){
                            SList.this.marker_step_t.setText("1");
                        }
                    }
                }
            });
            gridbag.setConstraints(this.marker_step_t, c);
            p.add(this.marker_step_t);
            this.add("East", p);
            SetupDataDialog.this.lab = new JLabel("");
            this.add("South", SetupDataDialog.this.lab);
            this.setOptionState(false);
        }

        public void addSignals() {
            int idx, color_idx = 0;
            Data ws;
            if(SetupDataDialog.this.y_expr.getText().length() == 0) return;
            ws = this.getSignalSetup();
            idx = this.findSignalSetup(ws);
            if(idx == -1){
                if(this.shots != null && this.shots.length != 0){
                    for(int i = 0; i < this.shots.length; i++, ws = this.getSignalSetup()){
                        ws.shot = this.shots[i];
                        ws.color_idx = color_idx;
                        color_idx = (color_idx + 1) % SetupDataDialog.this.main_scope.color_dialog.getNumColor();
                        this.addSignalSetup(ws);
                        this.signalListAdd(ws);
                    }
                }else{
                    ws.shot = SetupDataDialog.UNDEF_SHOT;
                    this.addSignalSetup(ws);
                    this.signalListAdd(ws);
                }
                this.signalSelect(this.findSignalSetup(ws));
            }
        }

        private void addSignalSetup(final Data ws) {
            this.signals.addElement(ws);
        }

        public boolean evaluateShotList(String in_shot) throws IOException {
            if(this.shots != null && this.shots.length != 0) this.list_num_shot = this.shots.length;
            else this.list_num_shot = 1;
            SetupDataDialog.this.main_scope.setMainShot();
            in_shot = jScopeWaveInterface.containMainShot(in_shot, SetupDataDialog.this.main_scope.wave_panel.getMainShotStr());
            final long new_shots[] = SetupDataDialog.this.wi.getShotArray(in_shot);
            if(new_shots == null){
                if(this.shots == null) return false;
                this.shots = null;
                return true;
            }
            if(this.shots == null){
                this.shots = new_shots;
                return true;
            }
            if(this.shots.equals(new_shots)){ return false; }
            this.shots = new_shots;
            if(SetupDataDialog.this.image_b.isSelected()){
                if(this.shots.length > 1){
                    final long sh[] = new long[1];
                    sh[0] = this.shots[0];
                    this.shots = sh;
                }
            }
            return true;
        }

        public int findSignalSetup(final Data ws) {
            for(int i = 0; i < this.signals.size(); i++)
                if((this.signals.elementAt(i)).equals(ws)) return i;
            return -1;
        }

        private String getExpressionList(final String expr) {
            String out = expr;
            try{
                final StringTokenizer st = new StringTokenizer(expr, "\n");
                if(st.countTokens() > 1) out = st.nextToken() + "... ";
            }catch(final Exception e){}
            return out;
        }

        public int getNumShot() {
            if(this.shots != null) return this.shots.length;
            return 0;
        }

        private int getPlotMode1D(final Data ws) {
            switch(ws.mode1D){
                case Signal.MODE_LINE:
                    return 0;
                case Signal.MODE_NOLINE:
                    return 1;
                case Signal.MODE_STEP:
                    return 2;
            }
            return 0;
        }

        private int getPlotMode2D(final Data ws) {
            return ws.mode2D;
        }

        public Data[] getSignals() {
            final Data s[] = new Data[this.signals.size()];
            this.signals.copyInto(s);
            return s;
        }

        public int getSignalSelect() {
            if(this.sig_list.getModel().getSize() == 2 && SetupDataDialog.this.image_b.isSelected()) return 0;
            return this.sel_signal;
        }

        public Data getSignalSetup() {
            final Data ws = new Data();
            ws.label = new String(SetupDataDialog.this.signal_label.getText());
            ws.x_expr = new String(SetupDataDialog.this.x_expr.getText());
            ws.y_expr = new String(SetupDataDialog.this.y_expr.getText());
            // ws.interpolate = (mode1D.getSelectedIndex() == 0 ? true : false);
            this.setPlotMode1D(ws, this.mode1D.getSelectedIndex());
            this.setPlotMode2D(ws, this.mode2D.getSelectedIndex());
            ws.marker = this.marker.getSelectedIndex();
            try{
                ws.marker_step = new Integer(this.marker_step_t.getText()).intValue();
            }catch(final NumberFormatException e){
                ws.marker_step = 1;
            }
            ws.color_idx = this.color.getSelectedIndex();
            return ws;
        }

        public void init(final WaveInterface wi) {
            if(wi != null){
                if(wi.shots != null){
                    this.shots = new long[wi.num_shot];
                    for(int i = 0; i < wi.num_shot; i++)
                        this.shots[i] = wi.shots[i];
                }
                this.list_num_shot = wi.num_shot;
                for(int i = 0; i < wi.num_waves; i++){
                    final Data ws = new Data();
                    ws.label = wi.in_label[i];
                    ws.x_expr = wi.in_x[i];
                    ws.y_expr = wi.in_y[i];
                    ws.up_err = wi.in_up_err[i];
                    ws.low_err = wi.in_low_err[i];
                    ws.interpolate = wi.interpolates[i];
                    ws.mode2D = wi.mode2D[i];
                    ws.mode1D = wi.mode1D[i];
                    ws.marker = wi.markers[i];
                    ws.marker_step = wi.markers_step[i];
                    ws.color_idx = wi.colors_idx[i];
                    if(wi.shots != null) ws.shot = wi.shots[i];
                    else ws.shot = SetupDataDialog.UNDEF_SHOT;
                    this.addSignalSetup(ws);
                }
                this.signalListRefresh();
                if(this.getSignalSelect() == -1 && wi.num_waves > 0) this.setSignalSelect(0);
            }
            this.signalSelect(this.getSignalSelect());
        }

        @Override
        public void itemStateChanged(final ItemEvent e) {
            final Object ob = e.getSource();
            if(ob instanceof JCheckBox) SetupDataDialog.this.defaultButtonChange(ob);
            if(this.getSignalSelect() == -1 || SetupDataDialog.this.image_b.isSelected()) return;
            if(ob == this.marker){
                final int m_idx = this.marker.getSelectedIndex();
                this.signals.elementAt(this.getSignalSelect()).marker = m_idx;
                this.setMarkerTextState(m_idx);
            }
            if(ob == this.mode1D){
                this.setPlotMode1D(this.signals.elementAt(this.getSignalSelect()), this.mode1D.getSelectedIndex());
            }
            if(ob == this.mode2D){
                this.setPlotMode2D(this.signals.elementAt(this.getSignalSelect()), this.mode2D.getSelectedIndex());
            }
            if(ob == this.color){
                this.signals.elementAt(this.getSignalSelect()).color_idx = this.color.getSelectedIndex();
            }
        }

        public void putSignalSetup(final Data ws) {
            if(ws.label != null) SetupDataDialog.this.signal_label.setText(ws.label);
            else SetupDataDialog.this.signal_label.setText("");
            if(ws.x_expr != null) SetupDataDialog.this.x_expr.setText(ws.x_expr);
            else SetupDataDialog.this.x_expr.setText("");
            if(ws.y_expr != null) SetupDataDialog.this.y_expr.setText(ws.y_expr);
            else SetupDataDialog.this.y_expr.setText("");
            this.mode1D.setSelectedIndex(this.getPlotMode1D(ws));
            this.mode2D.setSelectedIndex(this.getPlotMode2D(ws));
            this.marker.setSelectedIndex(ws.marker);
            this.marker_step_t.setText("" + ws.marker_step);
            this.setMarkerTextState(ws.marker);
            try{
                this.color.setSelectedIndex(ws.color_idx);
            }catch(final Exception exc){
                this.color.setSelectedIndex(0);
            }
            if(SetupDataDialog.this.error_w.isVisible()) SetupDataDialog.this.error_w.setError(ws);
        }

        public void removeSignalSetup() {
            int i, start_idx, end_idx;
            int num_shot = 1;
            final int num_signal = this.signals.size();
            if(this.shots != null && this.shots.length > 0) num_shot = this.shots.length;
            if(this.getSignalSelect() != -1){
                start_idx = (this.getSignalSelect() / num_shot) * num_shot; // intern division
                end_idx = start_idx + num_shot;
                for(i = 0; i < num_signal; i++)
                    if(i >= start_idx && i < end_idx){
                        this.list_model.removeElementAt(start_idx + 1);
                        this.signals.removeElementAt(start_idx);
                    }
            }
            this.signalSelect(-1);
            SetupDataDialog.this.signal_label.setText("");
            SetupDataDialog.this.x_expr.setText("");
            SetupDataDialog.this.y_expr.setText("");
        }

        public void reset() {
            this.signalSelect(-1);
            if(this.signals.size() != 0) this.signals.removeAllElements();
        }

        private void resetSignalSetup() {
            SetupDataDialog.this.signal_label.setText("");
            SetupDataDialog.this.x_expr.setText("");
            SetupDataDialog.this.y_expr.setText("");
            SetupDataDialog.this.x_max.setText("");
            SetupDataDialog.this.x_min.setText("");
            SetupDataDialog.this.y_max.setText("");
            SetupDataDialog.this.y_min.setText("");
            SetupDataDialog.this.x_log.setSelected(false);
            SetupDataDialog.this.y_log.setSelected(false);
            SetupDataDialog.this.keep_ratio_b.setSelected(false);
            SetupDataDialog.this.horizontal_flip_b.setSelected(false);
            SetupDataDialog.this.vertical_flip_b.setSelected(false);
            this.mode1D.setSelectedIndex(0);
            this.mode2D.setSelectedIndex(0);
            this.marker.setSelectedIndex(0);
            this.marker_step_t.setText("1");
            this.setMarkerTextState(0);
            this.color.setSelectedIndex(0);
        }

        @SuppressWarnings("unchecked")
        public void setColorList() {
            final String[] colors_name = SetupDataDialog.this.main_scope.color_dialog.getColorsName();
            if(this.color.getItemCount() != 0) this.color.removeAllItems();
            if(colors_name != null){
                for(final String element : colors_name)
                    this.color.addItem(element);
            }
        }

        private void setMarkerTextState(final int marker_idx) {
            if(marker_idx > 0 && marker_idx < 5) this.marker_step_t.setEditable(true);
            else{
                this.marker_step_t.setText("1");
                this.marker_step_t.setEditable(false);
            }
        }

        private void setOptionState(final boolean state) {
            this.marker.setEnabled(state);
            this.mode1D.setEnabled(state);
            this.mode2D.setEnabled(state);
            this.color.setEnabled(state);
            if(this.getSignalSelect() == -1) this.marker_step_t.setEditable(false);
        }

        private void setPlotMode1D(final Data ws, final int mode) {
            if(mode == 2){
                ws.interpolate = true;
                ws.mode1D = Signal.MODE_STEP;
            }else{
                if(mode == 0){
                    ws.interpolate = true;
                    ws.mode1D = Signal.MODE_LINE;
                }else{
                    ws.interpolate = false;
                    ws.mode1D = Signal.MODE_NOLINE;
                }
            }
        }

        private void setPlotMode2D(final Data ws, final int mode) {
            ws.mode2D = mode;
        }

        public void setSignalSelect(final int sig) {
            this.sel_signal = sig;
        }

        @SuppressWarnings("unchecked")
        private void signalListAdd(final Data ws) {
            if(ws.shot != SetupDataDialog.UNDEF_SHOT){
                if(ws.x_expr == null || ws.x_expr.length() == 0) this.list_model.addElement("Y : " + this.getExpressionList(ws.y_expr) + " Shot : " + ws.shot);
                else this.list_model.addElement("Y : " + this.getExpressionList(ws.y_expr) + " X : " + this.getExpressionList(ws.x_expr) + " Shot : " + ws.shot);
            }else{
                if(ws.x_expr == null || ws.x_expr.length() == 0) this.list_model.addElement("Y : " + this.getExpressionList(ws.y_expr) + " Shot : Undef");
                else this.list_model.addElement("Y : " + this.getExpressionList(ws.y_expr) + " X : " + this.getExpressionList(ws.x_expr) + " Shot : Undef");
            }
        }

        public void signalListRefresh() {
            if(this.list_model.size() > 1){
                this.sig_list.setSelectedIndex(0);
                this.list_model.removeRange(1, this.list_model.size() - 1);
            }
            for(int i = 0; i < this.signals.size(); i++)
                this.signalListAdd(this.signals.elementAt(i));
        }

        @SuppressWarnings("unchecked")
        private void signalListReplace(final int idx, final Data ws) {
            if(ws.shot != SetupDataDialog.UNDEF_SHOT){
                if(ws.x_expr == null || ws.x_expr.length() == 0) this.list_model.setElementAt("Y : " + this.getExpressionList(ws.y_expr) + " Shot : " + ws.shot, idx);
                else this.list_model.setElementAt("Y : " + this.getExpressionList(ws.y_expr) + " X : " + this.getExpressionList(ws.x_expr) + " Shot : " + ws.shot, idx);
            }else{
                if(ws.x_expr == null || ws.x_expr.length() == 0) this.list_model.setElementAt("Y : " + this.getExpressionList(ws.y_expr) + " Shot : Undef", idx);
                else this.list_model.setElementAt("Y : " + this.getExpressionList(ws.y_expr) + " X : " + this.getExpressionList(ws.x_expr) + " Shot : Undef", idx);
            }
        }

        private void signalSelect(final int sig) {
            if(sig + 1 >= this.sig_list.getModel().getSize()) return;
            this.setSignalSelect(sig);
            final int id = this.getSignalSelect() + 1;
            this.sig_list.setSelectedIndex(id);
            this.sig_list.ensureIndexIsVisible(id);
            if(sig >= 0) if(this.getSignalSelect() < this.signals.size()) this.putSignalSetup(this.signals.elementAt(this.getSignalSelect()));
            else this.resetSignalSetup();
            this.setOptionState(this.getSignalSelect() >= 0);
        }

        public void signalsRefresh() {
            int color_idx = 0, k = 0, l, num_sig, n_shot;
            n_shot = (this.shots != null ? this.shots.length : 1);
            num_sig = this.signals.size() / this.list_num_shot * n_shot;
            for(int j = 0; j < num_sig; j += n_shot){
                for(int i = 0; i < n_shot; i++){
                    if(i < this.list_num_shot){
                        this.signals.setElementAt(this.signals.elementAt(j + i), k);
                    }else{
                        final Data ws = new Data();
                        ws.copy(this.signals.elementAt(j));
                        color_idx = (color_idx + 1) % SetupDataDialog.this.main_scope.color_dialog.getNumColor();
                        ws.color_idx = color_idx;
                        this.signals.insertElementAt(ws, k);
                    }
                    if(this.shots != null) this.signals.elementAt(k).shot = this.shots[i];
                    else this.signals.elementAt(k).shot = SetupDataDialog.UNDEF_SHOT;
                    k++;
                }
                for(l = n_shot; l < this.list_num_shot; l++)
                    this.signals.removeElementAt(j + n_shot);
            }
        }

        public void updateError() {
            if(this.getSignalSelect() == -1) return;
            SetupDataDialog.this.error_w.setError(this.signals.elementAt(this.getSignalSelect()));
        }

        public void updateList() throws IOException {
            if(this.getSignalSelect() == -1){
                SetupDataDialog.this.signalList.addSignals();
            }else{
                if(SetupDataDialog.this.y_expr.getText().trim().length() != 0) SetupDataDialog.this.signalList.updateSignals();
                else this.removeSignalSetup();
            }
            if(this.evaluateShotList(SetupDataDialog.this.shot.getText())){
                this.signalsRefresh();
                this.signalListRefresh();
            }
        }

        public void updateSignals() {
            int i, start_idx, end_idx;
            int num_shot = 1;
            final int num_signal = this.signals.size();
            if(this.findSignalSetup(this.getSignalSetup()) != -1) return;
            if(this.getSignalSelect() != -1){
                if(this.shots != null && this.shots.length > 0) num_shot = this.shots.length;
                start_idx = (this.getSignalSelect() / num_shot) * num_shot; // Divisione intera
                end_idx = start_idx + num_shot;
                for(i = 0; i < num_signal; i++)
                    if(i >= start_idx && i < end_idx){
                        this.signals.elementAt(i).label = SetupDataDialog.this.signal_label.getText();
                        this.signals.elementAt(i).x_expr = SetupDataDialog.this.x_expr.getText();
                        this.signals.elementAt(i).y_expr = SetupDataDialog.this.y_expr.getText();
                        this.signalListReplace(i + 1, this.signals.elementAt(i));
                    }
                this.signalSelect(start_idx);
            }
        }

        public void updateSignalSetup(final int idx, final Data ws) {
            this.signals.setElementAt(ws, idx);
        }
    }
    class SymContainer extends java.awt.event.ContainerAdapter{
        @Override
        public void componentAdded(final java.awt.event.ContainerEvent event) {
            final Object object = event.getSource();
            if(object == SetupDataDialog.this) SetupDataDialog.this.setupDataDialog_componentAdded(event);
        }
    }
    static final int  BROWSE_X   = 0, BROWSE_Y = 1;
    static final int  LINE       = 0, POINT = 1, BOTH = 2, NONE = 3;
    public static int UNDEF_SHOT = -99999;

    private static void defaultButtonOperation(final Object obj, final boolean state, final String val) {
        if(obj instanceof JTextField){
            final JTextField text = (JTextField)obj;
            if(state){
                text.setForeground(Color.blue);
                text.setEditable(false);
            }else{
                text.setForeground(Color.black);
                text.setEditable(true);
            }
            if(val != null && val.trim().length() != 0) text.setText(val);
            else text.setText("");
        }
        if(obj instanceof JCheckBox){
            final JCheckBox check = (JCheckBox)obj;
            if(state){
                check.setEnabled(false);
                check.setForeground(Color.blue);
            }else{
                check.setForeground(Color.black);
                check.setEnabled(true);
            }
            if(val != null && val.trim().length() != 0 && val.equals("false")) check.setSelected(false);
            else check.setSelected(true);
        }
    }
    JButton                    apply               = new JButton("Apply");
    JButton                    cancel              = new JButton("Cancel");
    // GAB 2014
    JCheckBox                  continuous_update_b = new JCheckBox("Continuous Update");
    JTextField                 def_node            = new JTextField(20);
    JCheckBox                  def_node_b          = new JCheckBox("Default node");
    JButton                    erase               = new JButton("Erase");
    JButton                    error               = new JButton("Error");
    private final SError       error_w;
    JButton                    expand              = new JButton("Expand Expr.");
    private final ExpandExp    expand_expr;
    JTextField                 experiment          = new JTextField(20);
    JCheckBox                  experiment_b        = new JCheckBox("Experiment");
    JCheckBox                  horizontal_flip_b   = new JCheckBox("Horizontal flip");
    JCheckBox                  image_b             = new JCheckBox("Is image");
    JCheckBox                  keep_ratio_b        = new JCheckBox("Keep ratio");
    private JLabel             lab;
    private final jScopeFacade main_scope;
    JButton                    ok                  = new JButton("Ok");
    JPanel                     p9                  = new JPanel(new BorderLayout(2, 2));
    JLabel                     pix_x_max           = new JLabel("Pixel X max");
    JLabel                     pix_x_min           = new JLabel("Pixel X min");
    JLabel                     pix_y_max           = new JLabel("Pixel Y max");
    JLabel                     pix_y_min           = new JLabel("Pixel Y min");
    JButton                    reset               = new JButton("Reset");
    JTextField                 shot                = new JTextField(25);
    JCheckBox                  shot_b              = new JCheckBox("Shot");
    JLabel                     sig_label           = new JLabel();
    JTextField                 signal_label        = new JTextField(50);
    private final SList        signalList;
    JTextField                 time_max            = new JTextField(10);
    JCheckBox                  time_max_b          = new JCheckBox("T max");
    JTextField                 time_min            = new JTextField(10);
    JCheckBox                  time_min_b          = new JCheckBox("T min");
    JTextField                 title               = new JTextField(50);
    // private Point wave_coord;
    JCheckBox                  title_b             = new JCheckBox("Title");
    JTextField                 upd_event           = new JTextField(20);
    JCheckBox                  upd_event_b         = new JCheckBox("Update event");
    JCheckBox                  upd_limits          = new JCheckBox("Upd. Limits");
    JCheckBox                  upd_limits_b        = new JCheckBox("");
    JCheckBox                  vertical_flip_b     = new JCheckBox("Vertical Flip");
    private jScopeMultiWave    wave;
    public jScopeWaveInterface wi;
    JTextArea                  x_expr              = new JTextArea(1, 58);
    JLabel                     x_lab               = new JLabel("X");
    JTextField                 x_label             = new JTextField(20);
    JCheckBox                  x_label_b           = new JCheckBox("X Label");
    JCheckBox                  x_log               = new JCheckBox("Log scale");
    JTextField                 x_max               = new JTextField(10);
    JCheckBox                  x_max_b             = new JCheckBox("X max");
    JTextField                 x_min               = new JTextField(10);
    JCheckBox                  x_min_b             = new JCheckBox("X min");
    JTextArea                  y_expr              = new JTextArea(1, 58);
    JLabel                     y_lab               = new JLabel("Y");
    JTextField                 y_label             = new JTextField(20);
    JCheckBox                  y_label_b           = new JCheckBox("Y Label");
    JCheckBox                  y_log               = new JCheckBox("Log scale");
    JTextField                 y_max               = new JTextField(10);
    JCheckBox                  y_max_b             = new JCheckBox("Y max");
    JTextField                 y_min               = new JTextField(10);
    JCheckBox                  y_min_b             = new JCheckBox("Y min");

    public SetupDataDialog(final Frame fw, final String frame_title){
        super(fw, frame_title, false);
        this.setModal(true);
        this.main_scope = (jScopeFacade)fw;
        this.error_w = new SError(fw);
        this.expand_expr = new ExpandExp(fw, this);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setVisible(false);
        final JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1.add(this.title_b);
        p1.add(this.title);
        p1.add(this.expand);
        final JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.sig_label.setText("Signal Label");
        p2.add(this.sig_label);
        p2.add(this.signal_label);
        p2.add(this.error);
        final JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p3.add(this.y_lab);
        p3.add(this.y_expr);
        p3.add(this.y_log);
        final JPanel p4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p4.add(this.y_label_b);
        p4.add(this.y_label);
        p4.add(this.pix_y_min);
        this.pix_y_min.setVisible(false);
        p4.add(this.y_min_b);
        p4.add(this.y_min);
        p4.add(this.pix_y_max);
        this.pix_y_max.setVisible(false);
        p4.add(this.y_max_b);
        p4.add(this.y_max);
        p4.add(this.image_b);
        final JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p5.add(this.x_lab);
        p5.add(this.x_expr);
        p5.add(this.time_min_b);
        this.time_min_b.setVisible(false);
        p5.add(this.time_min);
        this.time_min.setVisible(false);
        p5.add(this.time_max_b);
        p5.add(this.time_max);
        this.time_max_b.setVisible(false);
        this.time_max.setVisible(false);
        p5.add(this.x_log);
        final JPanel p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p6.add(this.x_label_b);
        p6.add(this.x_label);
        p6.add(this.pix_x_min);
        this.pix_x_min.setVisible(false);
        p6.add(this.x_min_b);
        p6.add(this.x_min);
        p6.add(this.pix_x_max);
        this.pix_x_max.setVisible(false);
        p6.add(this.x_max_b);
        p6.add(this.x_max);
        p6.add(this.continuous_update_b);
        p6.add(this.keep_ratio_b);
        this.keep_ratio_b.setVisible(false);
        final JPanel p7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p7.add(this.experiment_b);
        p7.add(this.experiment);
        p7.add(this.shot_b);
        p7.add(this.shot);
        p7.add(this.upd_limits_b);
        final JPanel pp1 = new JPanel();
        this.upd_limits.setMargin(new Insets(1, 1, 1, 1));
        final BevelBorder bb = (BevelBorder)BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        pp1.setBorder(bb);
        pp1.add(this.upd_limits);
        p7.add(pp1);
        p7.add(this.horizontal_flip_b);
        this.horizontal_flip_b.setVisible(false);
        final JPanel p8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p8.add(this.upd_event_b);
        p8.add(this.upd_event);
        p8.add(this.def_node_b);
        p8.add(this.def_node);
        p8.add(this.vertical_flip_b);
        this.vertical_flip_b.setVisible(false);
        this.p9.setBorder(BorderFactory.createLoweredBevelBorder());
        this.signalList = new SList();
        this.p9.add("Center", this.signalList);
        final JPanel p10 = new JPanel();
        p10.setLayout(new FlowLayout());
        p10.setBounds(12, 347, 660, 40);
        p10.add(this.ok);
        p10.add(this.apply);
        p10.add(this.reset);
        p10.add(this.erase);
        p10.add(this.cancel);
        this.getContentPane().add(p1);
        this.getContentPane().add(p2);
        this.getContentPane().add(p3);
        this.getContentPane().add(p4);
        this.getContentPane().add(p5);
        this.getContentPane().add(p6);
        this.getContentPane().add(p7);
        this.getContentPane().add(p8);
        this.getContentPane().add(this.p9);
        this.getContentPane().add(p10);
        this.title_b.addItemListener(this);
        this.expand.addActionListener(this);
        this.signal_label.addKeyListener(this);
        this.error.addActionListener(this);
        this.y_expr.addKeyListener(this);
        this.y_label_b.addItemListener(this);
        this.y_min_b.addItemListener(this);
        this.y_min.addKeyListener(this);
        this.y_max_b.addItemListener(this);
        this.y_max.addKeyListener(this);
        this.image_b.addItemListener(this);
        this.x_expr.addKeyListener(this);
        this.x_label_b.addItemListener(this);
        this.x_min_b.addItemListener(this);
        this.x_min.addKeyListener(this);
        this.x_max_b.addItemListener(this);
        this.x_max.addKeyListener(this);
        this.upd_limits_b.addItemListener(this);
        this.time_max_b.addItemListener(this);
        this.time_max.addKeyListener(this);
        this.time_min_b.addItemListener(this);
        this.time_min.addKeyListener(this);
        this.experiment_b.addItemListener(this);
        this.experiment.addKeyListener(this);
        this.shot_b.addItemListener(this);
        this.shot.addKeyListener(this);
        // shot.addFocusListener(this);
        this.upd_event_b.addItemListener(this);
        this.def_node_b.addItemListener(this);
        this.ok.addActionListener(this);
        this.apply.addActionListener(this);
        this.reset.addActionListener(this);
        this.erase.addActionListener(this);
        this.cancel.addActionListener(this);
        this.pack();
        this.y_expr.setPreferredSize(this.y_expr.getSize());
        this.x_expr.setPreferredSize(this.x_expr.getSize());
        p3.setPreferredSize(p3.getSize());
        this.p9.setPreferredSize(this.p9.getSize());
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(final WindowEvent e) {
                SetupDataDialog.this.cancelOperation();
            }
        });
        // {{REGISTER_LISTENERS
        final SymContainer aSymContainer = new SymContainer();
        this.addContainerListener(aSymContainer);
        // }}
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object ob = e.getSource();
        if(this.getCursor().getType() == Cursor.WAIT_CURSOR) return;
        if(ob == this.erase){
            this.eraseForm();
            // wave.wi = new jScopeWaveInterface(main_scope.db, main_scope.def_values);
            // wave.wi.is_image = image_b.isSelected();
            // wave.jScopeErase();
        }
        if(ob == this.cancel) this.cancelOperation();
        if(ob == this.apply || ob == this.ok){
            this.wave.addWaveformListener(this);
            this.applyWaveform();
            if(ob == this.ok){
                // wave.removeWaveformListener(this);
                this.setVisible(false);
                this.signalList.reset();
            }
        }
        if(ob == this.reset){
            this.signalList.reset();
            this.putWindowSetup((jScopeWaveInterface)this.wave.wi);
        }
        if(ob == this.error && this.y_expr.getText().trim().length() != 0){
            if(this.signalList.getSignalSelect() == -1)
            /*
            Check if the list is in add mode (signal selectet -1)
            in this case before to add error signal must be added
            to the list
             */
            this.updateDataSetup();
            if(this.signalList.getSignalSelect() != -1){
                /*
                Only if is selected a signal in the list
                the error signal dialog can be shown
                 */
                this.signalList.updateError();
                this.error_w.setLocationRelativeTo(this);
                this.error_w.setVisible(true);
            }
        }
        if(ob == this.expand){
            this.expand_expr.setExpressionString(this.x_expr.getText(), this.y_expr.getText());
            this.expand_expr.setSize(600, 400);
            this.expand_expr.setLocationRelativeTo(this);
            this.expand_expr.setVisible(true);
        }
    }

    private void applyWaveform() {
        this.checkSetup();
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try{
            this.main_scope.wave_panel.refresh(this.wave, "Update ");
        }catch(final Throwable e){
            this.main_scope.setStatusLabel("Error during apply: " + e);
        }finally{
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void cancelOperation() {
        // wave.removeWaveformListener(this);
        this.setVisible(false);
        this.signalList.reset();
    }

    private int checkSetup() {
        int error = 0;
        boolean def_exp = true, def_shot = true;
        this.main_scope.setStatusLabel("");
        if(this.experiment.getText() == null || this.experiment.getText().trim().length() == 0) def_exp = false;
        if(this.shot.getText() == null || this.shot.getText().trim().length() == 0) def_shot = false;
        if(def_exp ^ def_shot){
            if(!def_shot){
                JOptionPane.showMessageDialog(this, "Experiment defined but undefined shot", "alert", JOptionPane.ERROR_MESSAGE);
                error = 1;
            }
        }
        this.updateDataSetup();
        if(this.updateWI() != 0){
            JOptionPane.showMessageDialog(this, "Nothing to evaluate", "alert", JOptionPane.ERROR_MESSAGE);
            error = 1;
        }
        return error;
    }

    private void defaultButtonChange(final Object ob) {
        boolean def_flag;
        if(ob == this.title_b) SetupDataDialog.defaultButtonOperation(this.title, def_flag = this.title_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_title, def_flag));
        if(ob == this.shot_b) this.putShotValue(this.shot_b.isSelected());
        if(ob == this.experiment_b) SetupDataDialog.defaultButtonOperation(this.experiment, def_flag = this.experiment_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_exp, def_flag));
        if(ob == this.upd_event_b) SetupDataDialog.defaultButtonOperation(this.upd_event, def_flag = this.upd_event_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_event, def_flag));
        if(ob == this.def_node_b) SetupDataDialog.defaultButtonOperation(this.def_node, def_flag = this.def_node_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_default_node, def_flag));
        if(ob == this.x_max_b) SetupDataDialog.defaultButtonOperation(this.x_max, def_flag = this.x_max_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_max, def_flag));
        if(ob == this.x_min_b) SetupDataDialog.defaultButtonOperation(this.x_min, def_flag = this.x_min_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_min, def_flag));
        if(ob == this.time_max_b) SetupDataDialog.defaultButtonOperation(this.time_max, def_flag = this.time_max_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_max, def_flag));
        if(ob == this.time_min_b) SetupDataDialog.defaultButtonOperation(this.time_min, def_flag = this.time_min_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_min, def_flag));
        if(ob == this.x_label_b) SetupDataDialog.defaultButtonOperation(this.x_label, def_flag = this.x_label_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_label, def_flag));
        if(ob == this.y_max_b) SetupDataDialog.defaultButtonOperation(this.y_max, def_flag = this.y_max_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_y_max, def_flag));
        if(ob == this.y_min_b) SetupDataDialog.defaultButtonOperation(this.y_min, def_flag = this.y_min_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_y_min, def_flag));
        if(ob == this.y_label_b) SetupDataDialog.defaultButtonOperation(this.y_label, def_flag = this.y_label_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_y_label, def_flag));
        if(ob == this.upd_limits_b) SetupDataDialog.defaultButtonOperation(this.upd_limits, def_flag = this.upd_limits_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_update, def_flag));
    }

    public void eraseForm() {
        this.title.setText("");
        this.signal_label.setText("");
        this.x_expr.setText("");
        this.x_label.setText("");
        this.x_label.setForeground(Color.black);
        this.time_max.setText("");
        this.time_max.setForeground(Color.black);
        this.x_max.setText("");
        this.x_max.setForeground(Color.black);
        this.x_min.setText("");
        this.x_min.setForeground(Color.black);
        this.time_min.setText("");
        this.time_min.setForeground(Color.black);
        this.y_expr.setText("");
        this.y_max.setText("");
        this.y_max.setForeground(Color.black);
        this.y_min.setText("");
        this.y_min.setForeground(Color.black);
        this.y_label.setText("");
        this.y_label.setForeground(Color.black);
        this.experiment.setText("");
        this.shot.setText("");
        this.upd_event.setText("");
        this.def_node.setText("");
        this.shot.setForeground(Color.black);
        this.resetDefaultFlags();
        // if(!wi.is_image)
        {
            this.signalList.reset();
            this.signalList.signalListRefresh();
        }
    }

    private int getDefaultFlags() {
        int value = 0;
        if(this.title_b.isSelected()) value |= 1 << jScopeWaveInterface.B_title;
        else value &= ~(1 << jScopeWaveInterface.B_title);
        if(this.shot_b.isSelected()) value |= 1 << jScopeWaveInterface.B_shot;
        else value &= ~(1 << jScopeWaveInterface.B_shot);
        if(this.experiment_b.isSelected()) value |= 1 << jScopeWaveInterface.B_exp;
        else value &= ~(1 << jScopeWaveInterface.B_exp);
        if(this.image_b.isSelected()){
            if(this.time_max_b.isSelected()) value |= 1 << jScopeWaveInterface.B_x_max;
            else value &= ~(1 << jScopeWaveInterface.B_x_max);
            if(this.time_min_b.isSelected()) value |= 1 << jScopeWaveInterface.B_x_min;
            else value &= ~(1 << jScopeWaveInterface.B_x_min);
        }else{
            if(this.x_max_b.isSelected()) value |= 1 << jScopeWaveInterface.B_x_max;
            else value &= ~(1 << jScopeWaveInterface.B_x_max);
            if(this.x_min_b.isSelected()) value |= 1 << jScopeWaveInterface.B_x_min;
            else value &= ~(1 << jScopeWaveInterface.B_x_min);
        }
        if(this.x_label_b.isSelected()) value |= 1 << jScopeWaveInterface.B_x_label;
        else value &= ~(1 << jScopeWaveInterface.B_x_label);
        if(this.y_max_b.isSelected()) value |= 1 << jScopeWaveInterface.B_y_max;
        else value &= ~(1 << jScopeWaveInterface.B_y_max);
        if(this.y_min_b.isSelected()) value |= 1 << jScopeWaveInterface.B_y_min;
        else value &= ~(1 << jScopeWaveInterface.B_y_min);
        if(this.y_label_b.isSelected()) value |= 1 << jScopeWaveInterface.B_y_label;
        else value &= ~(1 << jScopeWaveInterface.B_y_label);
        if(this.upd_event_b.isSelected()) value |= 1 << jScopeWaveInterface.B_event;
        else value &= ~(1 << jScopeWaveInterface.B_event);
        if(this.def_node_b.isSelected()) value |= 1 << jScopeWaveInterface.B_default_node;
        else value &= ~(1 << jScopeWaveInterface.B_default_node);
        if(this.upd_limits_b.isSelected()) value |= 1 << jScopeWaveInterface.B_update;
        else value &= ~(1 << jScopeWaveInterface.B_update);
        return(value);
    }

    public boolean isChanged(final Data s[]) {
        final jScopeWaveInterface wave_wi = (jScopeWaveInterface)this.wave.wi;
        if(wave_wi == null) return true;
        if(wave_wi.getModified()) return true;
        if(s.length != wave_wi.num_waves) return true;
        if(this.signalList.getNumShot() != wave_wi.num_shot) return true;
        if(!jScopeFacade.equalsString(this.title.getText(), wave_wi.cin_title)) return true;
        if(!jScopeFacade.equalsString(this.x_max.getText(), wave_wi.cin_xmax)) return true;
        if(!jScopeFacade.equalsString(this.x_min.getText(), wave_wi.cin_xmin)) return true;
        if(this.image_b.isSelected()){
            if(!jScopeFacade.equalsString(this.time_max.getText(), wave_wi.cin_timemax)) return true;
            if(!jScopeFacade.equalsString(this.time_min.getText(), wave_wi.cin_timemin)) return true;
        }
        if(!jScopeFacade.equalsString(this.x_label.getText(), wave_wi.cin_xlabel)) return true;
        if(this.x_log.isSelected() != wave_wi.x_log) return true;
        if(this.upd_limits.isSelected() != wave_wi.cin_upd_limits) return true;
        if(!jScopeFacade.equalsString(this.y_max.getText(), wave_wi.cin_ymax)) return true;
        if(!jScopeFacade.equalsString(this.y_min.getText(), wave_wi.cin_ymin)) return true;
        if(!jScopeFacade.equalsString(this.y_label.getText(), wave_wi.cin_ylabel)) return true;
        if(this.y_log.isSelected() != wave_wi.y_log) return true;
        // if(!main_scope.equalsString(shot.getText(), wave_wi.cin_shot)) return true;
        if(!jScopeFacade.equalsString(this.shot.getText(), wave_wi.in_shot)) return true;
        if(!jScopeFacade.equalsString(this.upd_event.getText(), wave_wi.cin_upd_event)) return true;
        if(!jScopeFacade.equalsString(this.def_node.getText(), wave_wi.cin_def_node)) return true;
        if(!jScopeFacade.equalsString(this.experiment.getText(), wave_wi.cexperiment)) return true;
        if(this.getDefaultFlags() != wave_wi.defaults) return true;
        if(this.image_b.isSelected() != wave_wi.is_image) return true;
        if(this.keep_ratio_b.isSelected() != wave_wi.keep_ratio) return true;
        if(this.horizontal_flip_b.isSelected() != wave_wi.horizontal_flip) return true;
        if(this.vertical_flip_b.isSelected() != wave_wi.vertical_flip) return true;
        for(int i = 0; i < wave_wi.num_waves; i++){
            if(!jScopeFacade.equalsString(s[i].x_expr, wave_wi.in_x[i])) return true;
            if(!jScopeFacade.equalsString(s[i].y_expr, wave_wi.in_y[i])) return true;
            if(!jScopeFacade.equalsString(s[i].up_err, wave_wi.in_up_err[i])) return true;
            if(!jScopeFacade.equalsString(s[i].low_err, wave_wi.in_low_err[i])) return true;
        }
        // GAB 2014
        // if(continuous_update_b.isSelected() != wave_wi.isContinuousUpdate) return true;
        return false;
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        final Object ob = e.getSource();
        if(ob instanceof JCheckBox){
            if(ob == this.image_b){
                this.eraseForm();
                this.setImageDialog(this.image_b.isSelected());
                return;
            }
            this.defaultButtonChange(ob);
        }
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        final Object ob = e.getSource();
        final char key = e.getKeyChar();
        if(key == KeyEvent.CHAR_UNDEFINED) return;
        if(key == KeyEvent.VK_ENTER){
            if(ob == this.y_expr || ob == this.x_expr || ob == this.shot || ob == this.experiment || ob == this.signal_label){
                this.updateDataSetup();
                e.consume();
            }
        }
        if(ob instanceof TextField){
            if(ob == this.x_max || ob == this.y_max || ob == this.x_min || ob == this.y_min || ob == this.shot || ob == this.time_max || ob == this.time_min){
                if(!Character.isDigit(key) && key != KeyEvent.VK_DELETE && key != '.' && key != '+' && key != '-') return;
            }
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {}

    @Override
    public void keyTyped(final KeyEvent e) {}

    @Override
    public void processWaveformEvent(final WaveformEvent e) {
        final jScopeMultiWave w = (jScopeMultiWave)e.getSource();
        this.wave.removeWaveformListener(this);
        switch(e.getID()){
            case WaveformEvent.END_UPDATE:
                final String full_error = ((jScopeWaveInterface)w.wi).getErrorString();// main_scope.wave_panel.getBriefError());
                if(full_error != null){
                    JOptionPane.showMessageDialog(this, full_error, "alert", JOptionPane.ERROR_MESSAGE);
                }
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                break;
        }
    }

    public void putDefaultValues() {
        boolean def_flag;
        SetupDataDialog.defaultButtonOperation(this.title, def_flag = this.title_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_title, def_flag));
        this.putShotValue(this.shot_b.isSelected());
        SetupDataDialog.defaultButtonOperation(this.experiment, def_flag = this.experiment_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_exp, def_flag));
        if(this.image_b.isSelected()){
            SetupDataDialog.defaultButtonOperation(this.time_max, def_flag = this.time_max_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_max, def_flag));
            if(!def_flag) this.time_max.setText(this.wi.cin_timemax);
            SetupDataDialog.defaultButtonOperation(this.time_min, def_flag = this.time_min_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_min, def_flag));
            if(!def_flag) this.time_min.setText(this.wi.cin_timemin);
            this.x_min.setText(this.wi.cin_xmin);
            this.x_max.setText(this.wi.cin_xmax);
        }else{
            SetupDataDialog.defaultButtonOperation(this.x_max, def_flag = this.x_max_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_max, def_flag));
            SetupDataDialog.defaultButtonOperation(this.x_min, def_flag = this.x_min_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_min, def_flag));
        }
        SetupDataDialog.defaultButtonOperation(this.x_label, def_flag = this.x_label_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_x_label, def_flag));
        SetupDataDialog.defaultButtonOperation(this.y_max, def_flag = this.y_max_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_y_max, def_flag));
        SetupDataDialog.defaultButtonOperation(this.y_min, def_flag = this.y_min_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_y_min, def_flag));
        SetupDataDialog.defaultButtonOperation(this.y_label, def_flag = this.y_label_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_y_label, def_flag));
        SetupDataDialog.defaultButtonOperation(this.upd_event, def_flag = this.upd_event_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_event, def_flag));
        SetupDataDialog.defaultButtonOperation(this.def_node, def_flag = this.def_node_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_default_node, def_flag));
        SetupDataDialog.defaultButtonOperation(this.upd_limits, def_flag = this.upd_limits_b.isSelected(), this.wi.getDefaultValue(jScopeWaveInterface.B_update, def_flag));
    }

    private void putShotValue(final boolean def_flag) {
        if(def_flag) this.wi.defaults |= (1 << jScopeWaveInterface.B_shot);
        else this.wi.defaults &= ~(1 << jScopeWaveInterface.B_shot);
        switch(this.wi.getShotIdx()){
            case 0:
                this.shot.setForeground(Color.black);
                this.shot.setEditable(true);
                break;
            case 1:
                this.shot.setForeground(Color.blue);
                this.shot.setEditable(false);
                break;
            case 2:
                this.shot.setForeground(Color.red);
                this.shot.setEditable(false);
                break;
        }
        this.shot.setText(this.wi.getUsedShot());
    }

    public void putWindowSetup(final jScopeWaveInterface wi) {
        if(wi == null){
            this.eraseForm();
            return;
        }
        if(wi.is_image) wi.defaults = wi.defaults & ~((1 << jScopeWaveInterface.B_y_max) + (1 << jScopeWaveInterface.B_y_min));
        this.setImageDialog(wi.is_image);
        // this.wi.evaluated_shot = wi.evaluated_shot;
        this.wi.colorProfile = wi.colorProfile;
        this.wi.cexperiment = wi.cexperiment;
        this.wi.cin_shot = wi.cin_shot;
        this.wi.cin_upd_event = wi.cin_upd_event;
        this.wi.in_upd_event = wi.in_upd_event;
        this.wi.last_upd_event = wi.last_upd_event;
        this.wi.cin_def_node = wi.cin_def_node;
        this.wi.cin_xmax = wi.cin_xmax;
        this.wi.cin_xmin = wi.cin_xmin;
        this.wi.cin_ymax = wi.cin_ymax;
        this.wi.cin_ymin = wi.cin_ymin;
        this.wi.cin_upd_limits = wi.cin_upd_limits;
        this.wi.cin_timemin = wi.cin_timemin;
        this.wi.cin_timemax = wi.cin_timemax;
        this.wi.cin_title = wi.cin_title;
        this.wi.cin_xlabel = wi.cin_xlabel;
        this.wi.cin_ylabel = wi.cin_ylabel;
        this.wi.legend_x = wi.legend_x;
        this.wi.legend_y = wi.legend_y;
        this.wi.show_legend = wi.show_legend;
        this.wi.reversed = wi.reversed;
        this.wi.is_image = wi.is_image;
        this.wi.keep_ratio = wi.keep_ratio;
        this.wi.horizontal_flip = wi.horizontal_flip;
        this.wi.vertical_flip = wi.vertical_flip;
        this.image_b.setSelected(wi.is_image);
        this.keep_ratio_b.setSelected(wi.keep_ratio);
        this.horizontal_flip_b.setSelected(wi.horizontal_flip);
        this.vertical_flip_b.setSelected(wi.vertical_flip);
        this.setDefaultFlags(wi.defaults);
        this.putDefaultValues();
        // if(!wi.is_image)
        // {
        this.signal_label.setText("");
        this.x_expr.setText("");
        this.y_expr.setText("");
        this.x_log.setSelected(wi.x_log);
        this.y_log.setSelected(wi.y_log);
        // GAB 2014
        this.continuous_update_b.setSelected(wi.isContinuousUpdate);
        // upd_limits.setSelected(wi.cin_upd_limits);
        // }
        // else {
        // x_expr.setText("");
        // if(wi.in_label != null && wi.in_label[0].trim().length() > 0)
        // signal_label.setText(wi.in_label[0]);
        // if(wi.in_y != null && wi.in_y[0].trim().length() > 0)
        // y_expr.setText(wi.in_y[0]);
        // }
        this.signalList.init(wi);
    }

    private void resetDefaultFlags() {
        final boolean state = true;
        this.wi.defaults = 0xffffffff;
        if(this.wi.is_image) this.wi.defaults = this.wi.defaults & ~((1 << jScopeWaveInterface.B_y_max) + (1 << jScopeWaveInterface.B_y_min));
        this.title_b.setSelected(state);
        this.title.setEditable(!state);
        this.shot_b.setSelected(state);
        this.shot.setEditable(!state);
        this.experiment_b.setSelected(state);
        this.experiment.setEditable(!state);
        if(this.image_b.isSelected()){
            this.time_max_b.setSelected(state);
            this.time_max.setEditable(!state);
            this.time_min_b.setSelected(state);
            this.time_min.setEditable(!state);
            this.y_max_b.setSelected(false);
            this.y_max.setEditable(true);
            this.y_min_b.setSelected(false);
            this.y_min.setEditable(true);
            this.x_max_b.setSelected(false);
            this.x_max.setEditable(true);
            this.x_min_b.setSelected(false);
            this.x_min.setEditable(true);
        }else{
            this.y_max_b.setSelected(state);
            this.y_max.setEditable(!state);
            this.y_min_b.setSelected(state);
            this.y_min.setEditable(!state);
            this.x_max_b.setSelected(state);
            this.x_max.setEditable(!state);
            this.x_min_b.setSelected(state);
            this.x_min.setEditable(!state);
        }
        this.x_label_b.setSelected(state);
        this.x_label.setEditable(!state);
        this.y_label_b.setSelected(state);
        this.y_label.setEditable(!state);
        this.upd_event_b.setSelected(state);
        this.upd_event.setEditable(!state);
        this.def_node_b.setSelected(state);
        this.def_node.setEditable(!state);
        this.upd_limits_b.setSelected(state);
        this.upd_limits.setEnabled(!state);
        this.putDefaultValues();
    }

    public void selectSignal(final int sig) {
        this.signalList.setSignalSelect(sig);
    }

    public void setColorList() {
        this.signalList.setColorList();
    }

    private void setDefaultFlags(final int flags) {
        for(int i = 0; i < 32; i++){
            switch(i){
                case jScopeWaveInterface.B_title:
                    this.title_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_shot:
                    this.shot_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_exp:
                    this.experiment_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_x_max:
                    if(this.image_b.isSelected()) this.time_max_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    else this.x_max_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_x_min:
                    if(this.image_b.isSelected()) this.time_min_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    else this.x_min_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_x_label:
                    this.x_label_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_y_max:
                    this.y_max_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_y_min:
                    this.y_min_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_y_label:
                    this.y_label_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_event:
                    this.upd_event_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_default_node:
                    this.def_node_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
                    break;
                case jScopeWaveInterface.B_update:
                    this.upd_limits_b.setSelected(((flags & (1 << i)) == 1 << i) ? true : false);
            }
        }
    }

    private void setImageDialog(final boolean state) {
        if(state){
            this.sig_label.setVisible(false);
            this.signal_label.setVisible(false);
            this.time_min_b.setVisible(true);
            this.time_min.setVisible(true);
            this.time_max_b.setVisible(true);
            this.time_max.setVisible(true);
            this.pix_x_max.setVisible(true);
            this.pix_x_min.setVisible(true);
            this.x_max_b.setVisible(false);
            this.x_min_b.setVisible(false);
            // GAB 2014
            this.continuous_update_b.setVisible(false);
            this.pix_y_max.setVisible(true);
            this.pix_y_min.setVisible(true);
            this.y_max_b.setVisible(false);
            this.y_min_b.setVisible(false);
            this.upd_limits_b.setVisible(false);
            this.upd_limits.getParent().setVisible(false);
            this.x_log.setVisible(false);
            this.y_log.setVisible(false);
            this.signalList.setVisible(false);
            this.p9.setVisible(false);
            this.error.setVisible(false);
            this.keep_ratio_b.setVisible(true);
            this.horizontal_flip_b.setVisible(true);
            this.vertical_flip_b.setVisible(true);
            this.y_lab.setText("Frames");
            this.x_lab.setText("Times");
            this.x_expr.setPreferredSize(null);
            this.x_expr.setColumns(24);
        }else{
            this.sig_label.setVisible(true);
            this.signal_label.setVisible(true);
            this.time_min_b.setVisible(false);
            this.time_min.setVisible(false);
            this.time_max_b.setVisible(false);
            this.time_max.setVisible(false);
            this.pix_x_max.setVisible(false);
            this.pix_x_min.setVisible(false);
            this.x_max_b.setVisible(true);
            this.x_min_b.setVisible(true);
            // Contunius update is experimental and therefore not shown in production interface
            this.continuous_update_b.setVisible(false);
            this.pix_y_max.setVisible(false);
            this.pix_y_min.setVisible(false);
            this.y_max_b.setVisible(true);
            this.y_min_b.setVisible(true);
            this.upd_limits_b.setVisible(true);
            this.upd_limits.getParent().setVisible(true);
            this.x_log.setVisible(true);
            this.y_log.setVisible(true);
            this.signalList.setVisible(true);
            this.p9.setVisible(true);
            this.error.setVisible(true);
            this.x_lab.setVisible(true);
            this.keep_ratio_b.setVisible(false);
            this.horizontal_flip_b.setVisible(false);
            this.vertical_flip_b.setVisible(false);
            this.y_lab.setText("Y");
            this.x_lab.setText("X");
            this.x_expr.setPreferredSize(null);
            this.x_expr.setColumns(58);
        }
        this.pack();
        this.x_expr.setPreferredSize(this.x_expr.getSize());
        // validate();
    }

    void setupDataDialog_componentAdded(final java.awt.event.ContainerEvent event) {
        // TODO: code goes here.
    }

    public void show(final Waveform w, final int col, final int row) {
        // wave_coord = new Point(row, col);
        this.wave = (jScopeMultiWave)w;
        // wave.addWaveformListener(this);
        this.wi = (jScopeWaveInterface)this.wave.wi;
        this.wi = new jScopeWaveInterface(this.wave, ((jScopeWaveInterface)this.wave.wi).dp, ((jScopeWaveInterface)this.wave.wi).def_vals, this.wave.wi.cache_enabled);
        this.wi.defaults = ((jScopeWaveInterface)this.wave.wi).defaults;
        this.putWindowSetup((jScopeWaveInterface)this.wave.wi);
        this.updateDataSetup();
        this.setLocationRelativeTo(w.getParent());
        this.signalList.signalSelect(this.wave.getSelectedSignal());
        this.setTitle("Wave Setup for column " + col + " row " + row);
        jScopeFacade.jScopeSetUI(this);
        jScopeFacade.jScopeSetUI(this.error_w);
        this.error_w.pack();
        jScopeFacade.jScopeSetUI(this.expand_expr);
        this.expand_expr.pack();
        this.pack();
        this.setVisible(true);
    }

    private void updateDataSetup() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try{
            this.signalList.updateList();
        }catch(final Throwable e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
        }finally{
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void updateGlobalWI() {
        if(!this.experiment_b.isSelected() && !jScopeFacade.equalsString(this.experiment.getText(), this.wi.cexperiment)) this.wi.cexperiment = this.experiment.getText();
        if(!this.shot_b.isSelected() && !jScopeFacade.equalsString(this.shot.getText(), this.wi.cin_shot)) this.wi.cin_shot = this.shot.getText();
        if(!this.upd_event_b.isSelected() && !jScopeFacade.equalsString(this.upd_event.getText(), this.wi.cin_upd_event)) this.wi.cin_upd_event = this.upd_event.getText();
        if(!this.def_node_b.isSelected() && !jScopeFacade.equalsString(this.def_node.getText(), this.wi.cin_def_node)) this.wi.cin_def_node = this.def_node.getText();
        if(!this.time_max_b.isSelected() && !jScopeFacade.equalsString(this.time_max.getText(), this.wi.cin_timemax)) this.wi.cin_timemax = this.time_max.getText();
        if(!this.time_min_b.isSelected() && !jScopeFacade.equalsString(this.time_min.getText(), this.wi.cin_timemin)) this.wi.cin_timemin = this.time_min.getText();
        if(!this.x_max_b.isSelected() && !jScopeFacade.equalsString(this.x_max.getText(), this.wi.cin_xmax)) this.wi.cin_xmax = this.x_max.getText();
        if(!this.x_min_b.isSelected() && !jScopeFacade.equalsString(this.x_min.getText(), this.wi.cin_xmin)) this.wi.cin_xmin = this.x_min.getText();
        if(!this.upd_limits_b.isSelected() && (this.upd_limits.isSelected() != this.wi.cin_upd_limits)) this.wi.cin_upd_limits = this.upd_limits.isSelected();
        if(!this.y_max_b.isSelected() && !jScopeFacade.equalsString(this.y_max.getText(), this.wi.cin_ymax)) this.wi.cin_ymax = this.y_max.getText();
        if(!this.y_min_b.isSelected() && !jScopeFacade.equalsString(this.y_min.getText(), this.wi.cin_ymin)) this.wi.cin_ymin = this.y_min.getText();
        if(!this.title_b.isSelected() && !jScopeFacade.equalsString(this.title.getText(), this.wi.cin_title)) this.wi.cin_title = this.title.getText();
        if(!this.x_label_b.isSelected() && !jScopeFacade.equalsString(this.x_label.getText(), this.wi.cin_xlabel)) this.wi.cin_xlabel = this.x_label.getText();
        if(!this.y_label_b.isSelected() && !jScopeFacade.equalsString(this.y_label.getText(), this.wi.cin_ylabel)) this.wi.cin_ylabel = this.y_label.getText();
        // GAB 2014
        // wi.isContinuousUpdate = continuous_update_b.isSelected();
        this.wi.isContinuousUpdate = false;
    }

    private int updateWI() {
        Data[] s;
        int num_signal;
        s = this.signalList.getSignals();
        num_signal = s.length;
        if(num_signal == 0){
            if(this.wave.wi != null) this.wave.wi.erase();
            return 1;
        }
        this.wi.setModified(this.isChanged(s));
        this.main_scope.setChange(this.wi.getModified());
        this.wi.is_image = this.image_b.isSelected();
        this.wi.keep_ratio = this.keep_ratio_b.isSelected();
        this.wi.horizontal_flip = this.horizontal_flip_b.isSelected();
        this.wi.vertical_flip = this.vertical_flip_b.isSelected();
        if(!this.wi.getModified()){
            /*
            if(wi.is_image)
            {
                if( wave.frames != null )
                {
                    wave.frames.setHorizontalFlip(wi.horizontal_flip);
                    wave.frames.setVerticalFlip(wi.vertical_flip);
                }
                return 0;
            }
            */
            for(int i = 0; i < this.wave.wi.num_waves; i++){
                this.wave.wi.markers[i] = s[i].marker;
                this.wave.wi.markers_step[i] = s[i].marker_step;
                this.wave.wi.interpolates[i] = s[i].interpolate;
                this.wave.wi.mode2D[i] = s[i].mode2D;
                this.wave.wi.mode1D[i] = s[i].mode1D;
                // wave.wi.colors[i] = main_scope.color_dialog.getColorAt(s[i].color_idx);
                this.wave.wi.colors_idx[i] = s[i].color_idx;
                this.wave.wi.in_label[i] = s[i].label;
            }
            return 0;
        }
        this.updateGlobalWI();
        this.wi.num_waves = num_signal;
        this.wi.experiment = new String(this.experiment.getText());
        this.wi.in_shot = new String(this.shot.getText());
        this.wi.in_def_node = new String(this.def_node.getText());
        this.wi.in_upd_event = new String(this.upd_event.getText());
        this.wi.cin_upd_event = new String(this.upd_event.getText());
        this.wi.in_xmax = new String(this.x_max.getText());
        this.wi.in_xmin = new String(this.x_min.getText());
        this.wi.in_timemax = new String(this.time_max.getText());
        this.wi.in_timemin = new String(this.time_min.getText());
        this.wi.in_ymax = new String(this.y_max.getText());
        this.wi.in_ymin = new String(this.y_min.getText());
        this.wi.in_title = new String(this.title.getText());
        this.wi.in_xlabel = new String(this.x_label.getText());
        this.wi.in_ylabel = new String(this.y_label.getText());
        this.wi.x_log = this.x_log.isSelected();
        this.wi.y_log = this.y_log.isSelected();
        this.wi.in_upd_limits = this.upd_limits.isSelected();
        this.wi.num_shot = this.signalList.getNumShot();
        this.wi.defaults = this.getDefaultFlags();
        this.wi.in_label = new String[num_signal];
        this.wi.in_x = new String[num_signal];
        this.wi.in_y = new String[num_signal];
        this.wi.in_up_err = new String[num_signal];
        this.wi.in_low_err = new String[num_signal];
        this.wi.markers = new int[num_signal];
        this.wi.markers_step = new int[num_signal];
        this.wi.colors_idx = new int[num_signal];
        this.wi.interpolates = new boolean[num_signal];
        this.wi.mode2D = new int[num_signal];
        this.wi.mode1D = new int[num_signal];
        // GAB 2014
        // wi.isContinuousUpdate = continuous_update_b.isSelected();
        this.wi.isContinuousUpdate = false;
        if(s[0].shot != SetupDataDialog.UNDEF_SHOT) this.wi.shots = new long[num_signal];
        for(int i = 0; i < num_signal; i++){
            if(s[i].label != null) this.wi.in_label[i] = new String(s[i].label);
            if(s[i].x_expr != null) this.wi.in_x[i] = new String(s[i].x_expr);
            if(s[i].y_expr != null) this.wi.in_y[i] = new String(s[i].y_expr);
            if(this.wi.shots != null) this.wi.shots[i] = s[i].shot;
            if(!this.wi.is_image){
                this.wi.markers[i] = s[i].marker;
                this.wi.markers_step[i] = s[i].marker_step;
                this.wi.interpolates[i] = s[i].interpolate;
                this.wi.mode2D[i] = s[i].mode2D;
                this.wi.mode1D[i] = s[i].mode1D;
                if(s[i].up_err != null) this.wi.in_up_err[i] = new String(s[i].up_err);
                if(s[i].low_err != null) this.wi.in_low_err[i] = new String(s[i].low_err);
                // wi.colors[i] = main_scope.color_dialog.getColorAt(s[i].color_idx);
                this.wi.colors_idx[i] = s[i].color_idx;
            }
        }
        // if(wi.shots[0] == jScope.UNDEF_SHOT)
        // wi.shots = null;
        this.wave.wi = this.wi;
        return 0;
    }
}

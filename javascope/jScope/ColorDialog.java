package jScope;

/* $Id$ */
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

final class ColorDialog extends JDialog implements ActionListener, ItemListener{
    final static class Item{
        Color  color;
        String name;

        Item(final String n, final Color c){
            this.name = new String(n);
            this.color = c;
        }
    }
    static final long serialVersionUID = 4766785678461L;

    private static Vector<Item> CopyColorItemsVector(final Vector<Item> in) {
        final Vector<Item> out = new Vector<Item>(in.size());
        for(int i = 0; i < in.size(); i++)
            out.addElement(new Item(in.elementAt(i).name, in.elementAt(i).color));
        return out;
    }

    private static Color StringToColor(final String str) {
        int pos;
        String tmp = str.substring(str.indexOf("=") + 1, pos = str.indexOf(","));
        final int r = new Integer(tmp).intValue();
        tmp = str.substring(pos + 3, pos = str.indexOf(",", pos + 1));
        final int g = new Integer(tmp).intValue();
        tmp = str.substring(pos + 3, str.indexOf("]", pos + 1));
        final int b = new Integer(tmp).intValue();
        final int c = (r << 16 | g << 8 | b);
        return(new Color(c));
    }
    boolean          changed         = false;
    JComboBox        color;
    String           color_name[];
    Vector<Item>     color_set       = new Vector<Item>();
    Vector<Item>     color_set_clone;
    Canvas           color_test;
    Color            color_vector[];
    JList            colorList;
    int              colorMapIndex[] = null;
    JTextField       colorName;
    JLabel           label;
    DefaultListModel listModel       = new DefaultListModel();
    jScopeFacade     main_scope;
    JButton          ok, reset, cancel, add, erase;
    JSlider          red, green, blue;
    int              red_i, green_i, blue_i;
    private boolean  reversed        = false;

    @SuppressWarnings("unchecked")
    ColorDialog(final Frame dw, final String title){
        super(dw, title, true);
        this.main_scope = (jScopeFacade)dw;
        this.GetPropertiesValue();
        final GridBagConstraints c = new GridBagConstraints();
        final GridBagLayout gridbag = new GridBagLayout();
        this.getContentPane().setLayout(gridbag);
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.label = new JLabel("Color list customization");
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        // Panel p0 = new Panel();
        // p0.setLayout(new FlowLayout(FlowLayout.LEFT));
        c.gridwidth = GridBagConstraints.BOTH;
        this.label = new JLabel("Name");
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        this.colorName = new JTextField(15);
        this.colorName.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(final KeyEvent e) {
                ColorDialog.this.keyPressedAction(e);
            }
        });
        gridbag.setConstraints(this.colorName, c);
        this.getContentPane().add(this.colorName);
        if(this.GetNumColor() == 0) this.ColorSetItems(Waveform.COLOR_NAME, Waveform.COLOR_SET);
        this.SetColorVector();
        this.GetColorsName();
        this.color = new JComboBox();
        for(final String element : this.color_name)
            this.color.addItem(element);
        this.color.addItemListener(this);
        gridbag.setConstraints(this.color, c);
        this.getContentPane().add(this.color);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.color_test = new Canvas();
        // color_test.setBounds(10,10,10,10);
        this.color_test.setBackground(Color.black);
        gridbag.setConstraints(this.color_test, c);
        this.getContentPane().add(this.color_test);
        c.gridwidth = 2;
        c.gridheight = 5;
        this.colorList = new JList(this.listModel);
        final JScrollPane scrollColorList = new JScrollPane(this.colorList);
        this.colorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.colorList.addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e) {
                final int color_idx = ((JList)e.getSource()).getSelectedIndex();
                if(color_idx >= 0 && color_idx < ColorDialog.this.color_set.size()){
                    final Item c_item = ColorDialog.this.color_set.elementAt(color_idx);
                    ColorDialog.this.SetSliderToColor(c_item.color);
                    ColorDialog.this.colorName.setText(c_item.name);
                }
            }
        });
        this.colorList.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(final KeyEvent e) {
                ColorDialog.this.keyPressedAction(e);
            }
        });
        gridbag.setConstraints(scrollColorList, c);
        this.getContentPane().add(scrollColorList);
        this.label = new JLabel("Red");
        // label.setForeground(Color.red);
        c.gridheight = 1;
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        final Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(0), new JLabel("0"));
        labelTable.put(new Integer(64), new JLabel("64"));
        labelTable.put(new Integer(128), new JLabel("128"));
        labelTable.put(new Integer(192), new JLabel("192"));
        labelTable.put(new Integer(255), new JLabel("255"));
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        this.red = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 0);
        this.red.setMinorTickSpacing(8);
        this.red.setPaintTicks(true);
        this.red.setPaintLabels(true);
        this.red.setLabelTable(labelTable);
        this.red.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        this.red.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                ColorDialog.this.colorValueChanged(e);
            }
        });
        gridbag.setConstraints(this.red, c);
        this.getContentPane().add(this.red);
        c.gridwidth = GridBagConstraints.BOTH;
        this.label = new JLabel("Green");
        // label.setForeground(Color.green);
        c.gridheight = 1;
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.green = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 0);
        this.green.setMinorTickSpacing(8);
        this.green.setPaintTicks(true);
        this.green.setPaintLabels(true);
        this.green.setLabelTable(labelTable);
        this.green.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        this.green.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                ColorDialog.this.colorValueChanged(e);
            }
        });
        gridbag.setConstraints(this.green, c);
        this.getContentPane().add(this.green);
        c.gridwidth = GridBagConstraints.BOTH;
        this.label = new JLabel("Blue");
        // label.setForeground(Color.blue);
        c.gridheight = 1;
        gridbag.setConstraints(this.label, c);
        this.getContentPane().add(this.label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.blue = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 0);
        this.blue.setMinorTickSpacing(8);
        this.blue.setPaintTicks(true);
        this.blue.setPaintLabels(true);
        this.blue.setLabelTable(labelTable);
        this.blue.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        this.blue.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                ColorDialog.this.colorValueChanged(e);
            }
        });
        gridbag.setConstraints(this.blue, c);
        this.getContentPane().add(this.blue);
        final JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.ok = new JButton("Ok");
        this.ok.addActionListener(this);
        p1.add(this.ok);
        this.add = new JButton("Add/Apply");
        this.add.addActionListener(this);
        p1.add(this.add);
        this.erase = new JButton("Erase");
        this.erase.addActionListener(this);
        p1.add(this.erase);
        this.reset = new JButton("Reset");
        this.reset.addActionListener(this);
        p1.add(this.reset);
        this.cancel = new JButton("Cancel");
        this.cancel.addActionListener(this);
        p1.add(this.cancel);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(p1, c);
        this.getContentPane().add(p1);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object ob = e.getSource();
        if(ob == this.ok){
            if(ob == this.ok){
                this.color_set_clone = null;
                this.setVisible(false);
            }
            this.AddUpdateItem(this.colorName.getText(), this.getColor());
            this.SetColorVector();
            this.main_scope.UpdateColors();
            this.main_scope.RepaintAllWaves();
            this.main_scope.setChange(true);
        }
        if(ob == this.add) this.AddUpdateItem(this.colorName.getText(), this.getColor());
        if(ob == this.erase){
            this.colorName.setText("");
            this.removeAllColorItems();
            if(this.listModel.getSize() > 0) this.listModel.clear();
            this.AddUpdateItem(Waveform.COLOR_NAME[0], Waveform.COLOR_SET[0]);
            this.SetColorVector();
        }
        if(ob == this.reset){
            this.color_set = ColorDialog.CopyColorItemsVector(this.color_set_clone);
            this.setColorItemToList();
            this.SetColorVector();
        }
        if(ob == this.cancel){
            this.color_set = ColorDialog.CopyColorItemsVector(this.color_set_clone);
            this.setColorItemToList();
            this.SetColorVector();
            this.color_set_clone = null;
            this.setVisible(false);
        }
    }

    @SuppressWarnings("unchecked")
    private void AddUpdateItem(final String name, final Color color) {
        int i;
        if(name == null || name.length() == 0) return;
        final Item c_item = new Item(name, color);
        final String c_name[] = this.GetColorsName();
        for(i = 0; c_name != null && i < c_name.length && !c_name[i].equals(name); i++);
        if(c_name == null || i == c_name.length){
            this.color_set.addElement(c_item);
            this.listModel.addElement(name);
        }else this.color_set.setElementAt(c_item, i);
    }

    public void ColorSetItems(final String[] color_name, final Color[] colors) {
        for(int i = 0; i < color_name.length; i++)
            if(colors[i].equals(this.reversed ? Color.black : Color.white)){
                if(this.reversed) this.color_set.addElement(new Item("White", Color.white));
                else this.color_set.addElement(new Item("Black", Color.black));
            }else this.color_set.addElement(new Item(color_name[i], colors[i]));
    }

    public void colorValueChanged(final ChangeEvent e) {
        this.color_test.setBackground(this.getColor());
        this.color_test.repaint();
    }

    public void FromFile(final Properties pr, final String prompt) throws IOException {
        String prop;
        int idx = 0;
        final Vector<Integer> newColorMap = new Vector<Integer>();
        this.removeAllColorItems();
        // Syntax Scope.color_x: <name>,java.awt.Color[r=xxx,g=xxx,b=xxx]
        while((prop = pr.getProperty(prompt + idx)) != null){
            final StringTokenizer st = new StringTokenizer(prop, ",");
            final String name = st.nextToken();
            st.nextToken("["); // dummy java.awt.Color[
            final Color cr = ColorDialog.StringToColor(st.nextToken("")); // remained string r=xxx,g=xxx,b=xxx]
            // InsertItemAt(name, cr, idx);
            newColorMap.addElement(new Integer(this.InsertItemAt(name, cr)));
            idx++;
        }
        this.colorMapIndex = new int[newColorMap.size()];
        for(int i = 0; i < newColorMap.size(); i++)
            this.colorMapIndex[i] = (newColorMap.elementAt(i).intValue());
        // Set default color list if is not defined color
        // in configuration file
        if(this.GetNumColor() == 0){
            if(this.main_scope.js_prop != null) this.GetPropertiesValue();
            else this.ColorSetItems(Waveform.COLOR_NAME, Waveform.COLOR_SET);
        }
        this.SetColorVector();
        this.GetColorsName();
    }

    private Color getColor() {
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue());
    }

    public Color GetColorAt(final int idx) {
        if(idx >= 0 && idx < this.color_set.size()) return this.color_set.elementAt(idx).color;
        return null;
    }

    public int[] getColorMapIndex() {
        return this.colorMapIndex;
    }

    public Color[] GetColors() {
        return this.color_vector;
    }

    public String[] GetColorsName() {
        this.color_name = null;
        if(this.color_set.size() > 0){
            this.color_name = new String[this.color_set.size()];
            for(int i = 0; i < this.color_set.size(); i++)
                this.color_name[i] = this.color_set.elementAt(i).name;
        }
        return this.color_name;
    }

    public String GetNameAt(final int idx) {
        if(idx >= 0 && idx < this.color_set.size()) return this.color_set.elementAt(idx).name;
        return null;
    }

    public int GetNumColor() {
        return this.color_set.size();
    }

    private void GetPropertiesValue() {
        final Properties js_prop = this.main_scope.js_prop;
        if(js_prop == null) return;
        for(int i = 0; true; i++){
            int len;
            final String prop = js_prop.getProperty("jScope.item_color_" + i);
            if(prop == null) break;
            final String name = new String(prop.substring(0, len = prop.indexOf(",")));
            this.InsertItemAt(name, ColorDialog.StringToColor(new String(prop.substring(len + 2, prop.length()))), i);
        }
    }

    public int InsertItemAt(final String name, final Color color) {
        int i;
        String ext = "";
        int extIdx = 1;
        for(i = 0; i < this.color_set.size(); i++){
            if(this.color_set.elementAt(i).name.equals(name + ext)){
                ext = "_" + extIdx;
                extIdx++;
            }
            if(this.color_set.elementAt(i).color.equals(color)) return i;
        }
        final Item c_item = new Item(name + ext, color);
        this.color_set.insertElementAt(c_item, i);
        return i;
    }

    public void InsertItemAt(final String name, final Color color, final int idx) {
        this.color_set.insertElementAt(new Item(name, color), idx);
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        final Object ob = e.getSource();
        if(ob == this.color){
            final int color_idx = this.color.getSelectedIndex();
            this.colorName.setText(Waveform.COLOR_NAME[color_idx]);
            this.SetSliderToColor(Waveform.COLOR_SET[color_idx]);
        }
    }

    public void keyPressedAction(final KeyEvent e) {
        final Object ob = e.getSource();
        final char key = e.getKeyChar();
        if(key == KeyEvent.CHAR_UNDEFINED) return;
        if(key == KeyEvent.VK_DELETE){
            if(ob == this.colorList){
                final int idx = this.colorList.getSelectedIndex();
                this.listModel.remove(idx);
                this.color_set.removeElementAt(idx);
                this.colorName.setText("");
            }
        }
        if(key == KeyEvent.VK_ENTER){
            if(ob == this.colorName){
                this.AddUpdateItem(this.colorName.getText(), this.getColor());
            }
        }
    }

    public void removeAllColorItems() {
        if(this.color_set.size() != 0) this.color_set.removeAllElements();
    }

    private void ReversedColor(final String[] color_name, final Color[] colors) {
        for(int i = 0; i < color_name.length; i++)
            if(colors[i].equals(this.reversed ? Color.black : Color.white)){
                if(this.reversed) this.color_set.setElementAt(new Item("White", Color.white), i);
                else this.color_set.setElementAt(new Item("Black", Color.black), i);
                return;
            }
    }

    @SuppressWarnings("unchecked")
    public void setColorItemToList() {
        if(this.listModel.getSize() > 0) this.listModel.clear();
        for(int i = 0; i < this.color_set.size(); i++){
            this.listModel.addElement(this.color_set.elementAt(i).name);
        }
    }

    public Color[] SetColorVector() {
        this.color_vector = new Color[this.color_set.size()];
        for(int i = 0; i < this.color_set.size(); i++)
            this.color_vector[i] = this.color_set.elementAt(i).color;
        return this.color_vector;
    }

    public void SetReversed(final boolean reversed) {
        if(this.reversed == reversed) return;
        this.reversed = reversed;
        this.ReversedColor(this.color_name, this.color_vector);
        this.SetColorVector();
        this.GetColorsName();
    }

    private void SetSliderToColor(final Color c) {
        this.red.setValue(c.getRed());
        this.green.setValue(c.getGreen());
        this.blue.setValue(c.getBlue());
        this.color_test.setBackground(c);
        this.color_test.repaint();
    }

    public void ShowColorDialog(final Component f) {
        this.setColorItemToList();
        // color_set_clone = (Vector)color_set.clone();
        this.color_set_clone = ColorDialog.CopyColorItemsVector(this.color_set);
        this.pack();
        this.setLocationRelativeTo(f);
        this.setVisible(true);
    }

    public void toFile(final PrintWriter out, final String prompt) {
        for(int i = 0; i < this.GetNumColor(); i++)
            out.println(prompt + i + ": " + this.GetNameAt(i) + "," + this.GetColorAt(i));
        out.println("");
    }
}
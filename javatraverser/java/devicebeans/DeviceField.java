package devicebeans;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mds.Mds;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.CString;

@SuppressWarnings("serial")
public class DeviceField extends DeviceComponent{
    protected JCheckBox  checkB;
    Descriptor           data;
    public boolean       displayEvaluated = false;
    GridBagLayout        gridbag;
    private boolean      initial_state;
    protected String     initialField;
    protected boolean    initializing     = false;
    protected boolean    isGridBag        = false;
    JPanel               jp;
    protected JLabel     label;
    public String        labelString      = "";
    public int           numCols          = 10;
    protected int        preferredWidth   = -1;
    private boolean      reportingChange  = false;
    public boolean       showState        = false;
    protected JTextField textF;
    public boolean       textOnly         = false;

    public DeviceField(){
        this.initializing = true;
        this.jp = new JPanel();
        this.jp.add(this.checkB = new JCheckBox());
        this.checkB.setVisible(false);
        this.jp.add(this.label = new JLabel());
        this.add(this.jp);
        this.add(this.textF = new JTextField(10));
        this.textF.setEnabled(this.editable);
        this.textF.setEditable(this.editable);
        // setLayout(gridbag = new GridBagLayout());
        this.initializing = false;
    }

    @Override
    public Component add(final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Field. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final Component c, final int intex) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Field. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final String name, final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Field. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    protected void dataChanged(final int offsetNid, final Object data) {
        if(this.reportingChange || this.offsetNid != offsetNid) return;
        try{
            String textData;
            if(data instanceof Descriptor) textData = ((Descriptor)data).toString();
            else textData = (String)data;
            this.textF.setText(textData);
        }catch(final Exception exc){}
    }

    @Override
    protected void displayData(final Descriptor data, final boolean is_on) {
        this.data = data;
        this.initial_state = is_on;
        if(this.showState) this.checkB.setSelected(is_on);
        if(data != null){
            String textString = data.decompile();
            if(this.displayEvaluated) try{
                this.initialField = textString = Mds.getActiveMds().getDescriptor(textString).decompile();
            }catch(final Exception exc){}
            if(textString != null){
                if(this.textOnly && textString.charAt(0) == '"') this.textF.setText(textString.substring(1, textString.length() - 1));
                else this.textF.setText(textString);
            }
        }else this.textF.setText("");
        this.label.setEnabled(is_on);
        this.textF.setEnabled(is_on & this.editable);
        this.textF.setEditable(is_on & this.editable);
    }

    @Override
    protected Descriptor getData() {
        final String dataString = this.textF.getText();
        if(dataString == null) return null;
        try{
            if(this.textOnly && !dataString.trim().startsWith("[")) return new CString(dataString);
            return Mds.getActiveMds().getDescriptor(dataString);
        }catch(final MdsException e){}
        return null;
    }

    public boolean getDisplayEvaluated() {
        return this.displayEvaluated;
    }

    public boolean getEditable() {
        return this.editable;
    }

    public String getLabelString() {
        return this.labelString;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public int getPreferredWidth() {
        return this.preferredWidth;
    }

    public boolean getShowState() {
        return this.showState;
    }

    @Override
    protected boolean getState() {
        if(!this.showState) return this.initial_state;
        return this.checkB.isSelected();
    }

    public boolean getTextOnly() {
        return this.textOnly;
    }

    @Override
    protected void initializeData(final Descriptor data, final boolean is_on) {
        this.initializing = true;
        this.initial_state = is_on;
        // initialField = data.toString();
        final Container parent = this.getParent();
        if(parent.getLayout() == null){
            this.isGridBag = false;
        }else this.isGridBag = true;
        final GridBagConstraints gc;
        if(this.isGridBag){
            this.setLayout(this.gridbag = new GridBagLayout());
            gc = new GridBagConstraints();
            gc.anchor = GridBagConstraints.WEST;
            gc.gridx = gc.gridy = 0;
            // gc.gridwidth = gc.gridheight = 1;
            gc.gridwidth = 1;
            gc.gridheight = 1;
            gc.weightx = gc.weighty = 1.;
            gc.fill = GridBagConstraints.NONE;
            this.gridbag.setConstraints(this.jp, gc);
        }else gc = null;
        if(this.showState){
            // add(checkB = new JCheckBox());
            this.checkB.setVisible(true);
            this.checkB.setSelected(is_on);
            this.checkB.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(final ChangeEvent e) {
                    final boolean state = DeviceField.this.checkB.isSelected();
                    if(DeviceField.this.label != null) DeviceField.this.label.setEnabled(state);
                    if(DeviceField.this.textF != null && DeviceField.this.editable){
                        DeviceField.this.textF.setEnabled(state);
                        DeviceField.this.textF.setEditable(state);
                    }
                }
            });
        }
        if(this.textF != null && gc != null){
            gc.gridx++;
            gc.anchor = GridBagConstraints.EAST;
            gc.gridwidth = 1;
            this.gridbag.setConstraints(this.textF, gc);
        }
        this.displayData(data, is_on);
        this.setEnabled(is_on);
        /*    textF.addKeyListener(new KeyAdapter()
            {
              public void keyTyped(KeyEvent e)
              {
        reportingChange = true;
        reportDataChanged(textF.getText());
        reportingChange = false;
              }
            });
        */ this.textF.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void changedUpdate(final DocumentEvent e) {
                DeviceField.this.reportingChange = true;
                DeviceField.this.reportDataChanged(DeviceField.this.textF.getText());
                DeviceField.this.reportingChange = false;
            }

            @Override
            public void insertUpdate(final DocumentEvent e) {
                DeviceField.this.reportingChange = true;
                DeviceField.this.reportDataChanged(DeviceField.this.textF.getText());
                DeviceField.this.reportingChange = false;
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                DeviceField.this.reportingChange = true;
                DeviceField.this.reportDataChanged(DeviceField.this.textF.getText());
                DeviceField.this.reportingChange = false;
            }
        });
        this.textF.setEnabled(this.editable);
        this.textF.setEditable(this.editable);
        if(this.preferredWidth > 0){
            this.setPreferredSize(new Dimension(this.preferredWidth, this.getPreferredSize().height));
            this.setSize(new Dimension(this.preferredWidth, this.getPreferredSize().height));
        }
        this.redisplay();
        this.initializing = false;
    }

    @Override
    protected boolean isChanged() {
        if(this.displayEvaluated) return false;
        return super.isChanged();
    }

    @Override
    protected boolean isDataChanged() {
        if(this.displayEvaluated && this.initialField != null) return !(this.textF.getText().equals(this.initialField));
        return true;
    }

    @Override
    void postApply() {
        if(this.editable || !this.displayEvaluated || this.data == null) return;
        // Nothing to do if the field is not editable and displays evaluated data
        String textString;
        textString = this.data.decompile();
        if(textString != null){
            if(this.textOnly && textString.charAt(0) == '"') this.textF.setText(textString.substring(1, textString.length() - 1));
            else this.textF.setText(textString);
        }
    }

    @Override
    public void print(final Graphics g) {
        Font prevLabelFont = null;
        Font prevTextFont = null;
        if(this.label != null){
            prevLabelFont = this.label.getFont();
            this.label.setFont(new Font("Serif", Font.BOLD, 10));
        }
        if(this.textF != null){
            prevTextFont = this.textF.getFont();
            this.textF.setFont(new Font("Serif", Font.BOLD, 10));
        }
        super.print(g);
        if(this.label != null) this.label.setFont(prevLabelFont);
        if(this.textF != null) this.textF.setFont(prevTextFont);
    }

    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        this.setPreferredSize(new Dimension(width, height));
    }

    public void setDisplayEvaluated(final boolean displayEvaluated) {
        this.displayEvaluated = displayEvaluated;
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    @Override
    public void setEnabled(final boolean state) {
        if(!this.editable && state) return; // Do not set enabled if not editable
        // if(checkB != null) checkB.setEnabled(state);
        if(this.textF != null){
            this.textF.setEnabled(state);
            this.textF.setEditable(state);
        }
        if(this.label != null) this.label.setEnabled(state);
        // if(checkB != null) checkB.setSelected(state);
        // initial_state = state;
    }

    @Override
    public void setHighlight(final boolean highlighted) {
        if(highlighted){
            if(this.label != null){
                this.label.setEnabled(true);
                this.label.setForeground(Color.red);
            }
        }else{
            if(this.label != null){
                this.label.setForeground(Color.black);
                this.label.setEnabled(this.getState());
            }
        }
        super.setHighlight(highlighted);
    }

    public void setLabelString(final String labelString) {
        this.labelString = labelString;
        this.label.setText(labelString);
        // redisplay();
    }

    public void setNumCols(final int numCols) {
        this.numCols = numCols;
        this.textF.setColumns(numCols);
        // redisplay();
    }

    public void setPreferredWidth(final int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    public void setShowState(final boolean showState) {
        this.showState = showState;
        if(showState) this.checkB.setVisible(true);
        else this.checkB.setVisible(false);
        // redisplay();
    }

    public void setTextOnly(final boolean textOnly) {
        this.textOnly = textOnly;
    }

    @Override
    public boolean supportsState() {
        return this.showState;
    }
}

package devicebeans;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Float32;
import mds.data.descriptor_s.Float64;
import mds.data.descriptor_s.Int32;

@SuppressWarnings("serial")
public class DeviceChoice extends DeviceComponent{
    protected JCheckBox checkB;
    protected double    choiceDoubleValues[] = null;
    protected float     choiceFloatValues[]  = null;
    protected int       choiceIntValues[]    = null;
    protected String    choiceItems[];
    protected JComboBox comboB;
    protected boolean   convert              = false;
    private boolean     initial_state;
    protected boolean   initializing         = false;
    protected JLabel    label;
    protected String    labelString          = null;
    private boolean     reportingChange      = false;
    protected boolean   showState            = false;

    @SuppressWarnings("unchecked")
    public DeviceChoice(){
        this.initializing = true;
        this.add(this.checkB = new JCheckBox());
        this.checkB.setVisible(false);
        this.add(this.label = new JLabel("Choice: "));
        this.add(this.comboB = new JComboBox(new String[]{"Item"}));
        this.initializing = false;
    }

    @Override
    public Component add(final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Choice. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final Component c, final int intex) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Choice. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final String name, final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Choice. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    protected void dataChanged(final int offsetNid, final Object data) {
        if(this.reportingChange || this.offsetNid != offsetNid) return;
        try{
            if(data instanceof Integer) this.comboB.setSelectedIndex(((Integer)data).intValue());
        }catch(final Exception exc){
            System.err.println("DeviceChoice.dataChanged: " + exc);
        }
    }

    @Override
    protected void displayData(final Descriptor data, final boolean is_on) {
        this.initial_state = is_on;
        String data_string;
        int curr_idx, data_value;
        float data_float;
        double data_double;
        if(this.showState) this.checkB.setSelected(is_on);
        if(this.convert){
            try{
                data_value = data.toInt();
            }catch(final Exception e){
                data_value = 0;
            }
            if(this.choiceIntValues != null){
                int i;
                for(i = 0; i < this.choiceIntValues.length && data_value != this.choiceIntValues[i]; i++);
                if(i < this.choiceIntValues.length) this.comboB.setSelectedIndex(i);
            }else this.comboB.setSelectedIndex(data_value);
        }else{
            if(data instanceof CString){
                data_string = ((CString)data).getValue();
                for(curr_idx = 0; curr_idx < this.choiceItems.length && !this.choiceItems[curr_idx].equals(data_string); curr_idx++);
                if(curr_idx < this.choiceItems.length) this.comboB.setSelectedIndex(curr_idx);
            }else if(this.choiceIntValues != null){
                try{
                    data_value = data.toInt();
                    for(curr_idx = 0; curr_idx < this.choiceIntValues.length && data_value != this.choiceIntValues[curr_idx]; curr_idx++);
                    if(curr_idx < this.choiceIntValues.length) this.comboB.setSelectedIndex(curr_idx);
                }catch(final Exception e){}
            }else if(this.choiceFloatValues != null){
                try{
                    data_float = data.toFloat();
                    for(curr_idx = 0; curr_idx < this.choiceFloatValues.length && data_float != this.choiceFloatValues[curr_idx]; curr_idx++);
                    if(curr_idx < this.choiceFloatValues.length) this.comboB.setSelectedIndex(curr_idx);
                }catch(final Exception e){}
            }else if(this.choiceDoubleValues != null){
                try{
                    data_double = data.toDouble();
                    for(curr_idx = 0; curr_idx < this.choiceDoubleValues.length && data_double != this.choiceDoubleValues[curr_idx]; curr_idx++);
                    if(curr_idx < this.choiceDoubleValues.length) this.comboB.setSelectedIndex(curr_idx);
                }catch(final Exception e){}
            }
        }
        this.setEnabled(is_on);
    }

    public double[] getChoiceDoubleValues() {
        return this.choiceDoubleValues;
    }

    public float[] getChoiceFloatValues() {
        return this.choiceFloatValues;
    }

    public int[] getChoiceIntValues() {
        return this.choiceIntValues;
    }

    public String[] getChoiceItems() {
        return this.choiceItems;
    }

    public boolean getConvert() {
        return this.convert;
    }

    @Override
    protected Descriptor getData() {
        final int curr_idx = this.comboB.getSelectedIndex();
        if(this.convert){
            if(this.choiceIntValues != null) return new Int32(this.choiceIntValues[curr_idx]);
            return new Int32(curr_idx);
        }
        if(this.choiceIntValues != null) return new Int32(this.choiceIntValues[curr_idx]);
        if(this.choiceFloatValues != null) return new Float32(this.choiceFloatValues[curr_idx]);
        if(this.choiceDoubleValues != null) return new Float64(this.choiceDoubleValues[curr_idx]);
        return new CString(this.choiceItems[curr_idx]);
    }

    public String getLabelString() {
        return this.labelString;
    }

    public boolean getShowState() {
        return this.showState;
    }

    @Override
    protected boolean getState() {
        if(!this.showState) return this.initial_state;
        return this.checkB.isSelected();
    }

    @Override
    protected void initializeData(final Descriptor data, final boolean is_on) {
        this.initial_state = is_on;
        this.initializing = true;
        this.displayData(data, is_on);
        this.comboB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(DeviceChoice.this.initializing) return;
                DeviceChoice.this.reportingChange = true;
                DeviceChoice.this.reportDataChanged(new Integer(DeviceChoice.this.comboB.getSelectedIndex()));
                DeviceChoice.this.reportingChange = false;
                if(DeviceChoice.this.updateIdentifier == null || DeviceChoice.this.updateIdentifier.equals("")) return;
                final String currItem = (String)DeviceChoice.this.comboB.getSelectedItem();
                DeviceChoice.this.master.fireUpdate(DeviceChoice.this.updateIdentifier, new CString(currItem));
            }
        });
        this.initializing = false;
    }

    @Override
    public void postConfigure() {
        final String currItem = (String)this.comboB.getSelectedItem();
        if(this.master != null && this.updateIdentifier != null) this.master.fireUpdate(this.updateIdentifier, new CString(currItem));
    }

    public void setChoiceDoubleValues(final double choiceDoubleValues[]) {
        this.choiceDoubleValues = choiceDoubleValues;
    }

    public void setChoiceFloatValues(final float choiceFloatValues[]) {
        this.choiceFloatValues = choiceFloatValues;
    }

    public void setChoiceIntValues(final int choiceIntValues[]) {
        this.choiceIntValues = choiceIntValues;
    }

    @SuppressWarnings("unchecked")
    public void setChoiceItems(final String choiceItems[]) {
        this.choiceItems = choiceItems;
        if(this.comboB != null && this.comboB.getItemCount() > 0) this.comboB.removeAllItems();
        if(choiceItems != null){
            for(final String choiceItem : choiceItems){
                this.comboB.addItem(choiceItem);
            }
        }
    }

    public void setConvert(final boolean convert) {
        this.convert = convert;
    }

    @Override
    public void setEnabled(final boolean state) {
        // if(checkB != null) checkB.setEnabled(state);
        if(this.comboB != null) this.comboB.setEnabled(state);
        if(this.label != null) this.label.setEnabled(state);
    }

    @Override
    public void setHighlight(final boolean highlighted) {
        if(highlighted){
            if(this.label != null) this.label.setForeground(Color.red);
        }else{
            if(this.label != null) this.label.setForeground(Color.black);
        }
        super.setHighlight(highlighted);
    }

    public void setLabelString(final String labelString) {
        this.labelString = labelString;
        this.label.setText(labelString);
        this.redisplay();
    }

    public void setShowState(final boolean showState) {
        this.showState = showState;
        if(showState) this.checkB.setVisible(true);
        this.redisplay();
    }
}

package devicebeans;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class DeviceChoiceCustomizer extends DeviceCustomizer implements Customizer{
    protected static float[] convertFloat(final String inText) {
        final String[] items = DeviceChoiceCustomizer.convertText(inText);
        final float out[] = new float[items.length];
        for(int i = 0; i < items.length; i++)
            out[i] = (new Float(items[i])).floatValue();
        return out;
    }

    protected static int[] convertInt(final String inText) {
        final String[] items = DeviceChoiceCustomizer.convertText(inText);
        final int out[] = new int[items.length];
        for(int i = 0; i < items.length; i++)
            out[i] = (new Integer(items[i])).intValue();
        return out;
    }

    protected static String[] convertText(final String inText) {
        int i = 0;
        final StringTokenizer st = new StringTokenizer(inText, "\n\r");
        final String[] items = new String[st.countTokens()];
        while(st.hasMoreTokens())
            items[i++] = st.nextToken();
        return items;
    }
    DeviceChoice          bean      = null;
    Button                doneButton;
    TextArea              itemsArea, codesArea;
    Label                 itemsLabel, codesLabel;
    TextField             labelString, identifier, updateIdentifier;
    PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    Choice                nids, mode;
    Checkbox              showState;

    public DeviceChoiceCustomizer(){}

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        this.listeners.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        this.listeners.removePropertyChangeListener(l);
    }

    @Override
    public void setObject(final Object o) {
        this.bean = (DeviceChoice)o;
        this.setLayout(new BorderLayout());
        Panel jp = new Panel();
        jp.setLayout(new BorderLayout());
        Panel jp1 = new Panel();
        jp1.add(new Label("Label: "));
        jp1.add(this.labelString = new TextField(30));
        this.labelString.setText(this.bean.getLabelString());
        jp1.add(this.showState = new Checkbox("Show state: ", this.bean.getShowState()));
        jp1.add(new Label("Offset nid: "));
        jp1.add(this.nids = new Choice());
        final String names[] = DeviceCustomizer.getDeviceFields();
        if(names != null){
            for(final String name2 : names)
                this.nids.add(name2);
            final int idx = this.bean.getOffsetNid();
            if(idx > 0 && idx < names.length) this.nids.select(idx - 1);
        }
        jp.add(jp1, "North");
        jp1 = new Panel();
        jp1.add(new Label("Mode: "));
        jp1.add(this.mode = new Choice());
        this.mode.add("String");
        this.mode.add("Integer");
        this.mode.add("Float");
        this.mode.add("Code");
        final boolean convert = this.bean.getConvert();
        final String[] items = this.bean.getChoiceItems();
        final int[] choiceIntValues = this.bean.getChoiceIntValues();
        final float[] choiceFloatValues = this.bean.getChoiceFloatValues();
        if(convert) this.mode.select(3);
        else if(choiceIntValues != null) this.mode.select(1);
        else if(choiceFloatValues != null) try{
            this.mode.select(2);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(null, "" + exc, "" + this.mode, JOptionPane.WARNING_MESSAGE);
        }
        else this.mode.select(0);
        this.mode.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                final int curr_idx = DeviceChoiceCustomizer.this.mode.getSelectedIndex();
                if(curr_idx == 3){
                    DeviceChoiceCustomizer.this.codesLabel.setEnabled(true);
                    DeviceChoiceCustomizer.this.codesArea.setEnabled(true);
                }else{
                    DeviceChoiceCustomizer.this.codesLabel.setEnabled(false);
                    DeviceChoiceCustomizer.this.codesArea.setEnabled(false);
                }
            }
        });
        jp1.add(this.itemsLabel = new Label("Items: "));
        jp1.add(this.itemsArea = new TextArea(5, 15));
        if(items != null) for(final String item : items)
            this.itemsArea.append(item + "\n");
        jp1.add(this.codesLabel = new Label("Codes: "));
        jp1.add(this.codesArea = new TextArea(5, 4));
        if(convert && choiceIntValues != null){
            for(final int choiceIntValue : choiceIntValues)
                this.codesArea.append((new Integer(choiceIntValue)).toString() + "\n");
        }else{
            this.codesLabel.setEnabled(false);
            this.codesArea.setEnabled(false);
        }
        jp.add(jp1, "Center");
        jp1 = new Panel();
        jp1.add(new Label("Opt. identifier: "));
        jp1.add(this.identifier = new TextField(this.bean.getIdentifier(), 20));
        jp1.add(new Label("Update identifier: "));
        jp1.add(this.updateIdentifier = new TextField(this.bean.getUpdateIdentifier(), 20));
        jp.add(jp1, "South");
        this.add(jp, "Center");
        jp = new Panel();
        jp.add(this.doneButton = new Button("Apply"));
        this.doneButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String oldLabelString = DeviceChoiceCustomizer.this.bean.getLabelString();
                DeviceChoiceCustomizer.this.bean.setLabelString(DeviceChoiceCustomizer.this.labelString.getText());
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("labelString", oldLabelString, DeviceChoiceCustomizer.this.bean.getLabelString());
                final boolean oldShowState = DeviceChoiceCustomizer.this.bean.getShowState();
                DeviceChoiceCustomizer.this.bean.setShowState(DeviceChoiceCustomizer.this.showState.getState());
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("showState", oldShowState, DeviceChoiceCustomizer.this.bean.getShowState());
                final int oldOffsetNid = DeviceChoiceCustomizer.this.bean.getOffsetNid();
                DeviceChoiceCustomizer.this.bean.setOffsetNid(DeviceChoiceCustomizer.this.nids.getSelectedIndex() + 1);
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("offsetNid", oldOffsetNid, DeviceChoiceCustomizer.this.bean.getOffsetNid());
                final int curr_idx = DeviceChoiceCustomizer.this.mode.getSelectedIndex();
                final boolean oldConvert = DeviceChoiceCustomizer.this.bean.getConvert();
                final String[] oldChoiceItems = DeviceChoiceCustomizer.this.bean.getChoiceItems();
                final float[] oldChoiceFloatValues = DeviceChoiceCustomizer.this.bean.getChoiceFloatValues();
                final int[] oldChoiceIntValues = DeviceChoiceCustomizer.this.bean.getChoiceIntValues();
                switch(curr_idx){
                    case 0: // String
                        DeviceChoiceCustomizer.this.bean.setConvert(false);
                        DeviceChoiceCustomizer.this.bean.setChoiceItems(DeviceChoiceCustomizer.convertText(DeviceChoiceCustomizer.this.itemsArea.getText()));
                        DeviceChoiceCustomizer.this.bean.setChoiceIntValues(null);
                        DeviceChoiceCustomizer.this.bean.setChoiceFloatValues(null);
                        break;
                    case 1: // Integer
                        DeviceChoiceCustomizer.this.bean.setConvert(false);
                        DeviceChoiceCustomizer.this.bean.setChoiceItems(DeviceChoiceCustomizer.convertText(DeviceChoiceCustomizer.this.itemsArea.getText()));
                        DeviceChoiceCustomizer.this.bean.setChoiceIntValues(DeviceChoiceCustomizer.convertInt(DeviceChoiceCustomizer.this.itemsArea.getText()));
                        DeviceChoiceCustomizer.this.bean.setChoiceFloatValues(null);
                        break;
                    case 2: // Float
                        DeviceChoiceCustomizer.this.bean.setConvert(false);
                        DeviceChoiceCustomizer.this.bean.setChoiceItems(DeviceChoiceCustomizer.convertText(DeviceChoiceCustomizer.this.itemsArea.getText()));
                        DeviceChoiceCustomizer.this.bean.setChoiceIntValues(null);
                        DeviceChoiceCustomizer.this.bean.setChoiceFloatValues(DeviceChoiceCustomizer.convertFloat(DeviceChoiceCustomizer.this.itemsArea.getText()));
                        break;
                    case 3: // Code
                        DeviceChoiceCustomizer.this.bean.setConvert(true);
                        DeviceChoiceCustomizer.this.bean.setChoiceItems(DeviceChoiceCustomizer.convertText(DeviceChoiceCustomizer.this.itemsArea.getText()));
                        DeviceChoiceCustomizer.this.bean.setChoiceFloatValues(null);
                        DeviceChoiceCustomizer.this.bean.setChoiceIntValues(DeviceChoiceCustomizer.convertInt(DeviceChoiceCustomizer.this.codesArea.getText()));
                        break;
                }
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("convert", oldConvert, DeviceChoiceCustomizer.this.bean.getConvert());
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("choiceItems", oldChoiceItems, DeviceChoiceCustomizer.this.bean.getChoiceItems());
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("choiceFloatValues", oldChoiceFloatValues, DeviceChoiceCustomizer.this.bean.getChoiceFloatValues());
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("choiceIntValues", oldChoiceIntValues, DeviceChoiceCustomizer.this.bean.getChoiceIntValues());
                final String oldIdentifier = DeviceChoiceCustomizer.this.bean.getIdentifier();
                DeviceChoiceCustomizer.this.bean.setIdentifier(DeviceChoiceCustomizer.this.identifier.getText());
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("identifier", oldIdentifier, DeviceChoiceCustomizer.this.bean.getIdentifier());
                final String oldUpdateIdentifier = DeviceChoiceCustomizer.this.bean.getUpdateIdentifier();
                DeviceChoiceCustomizer.this.bean.setUpdateIdentifier(DeviceChoiceCustomizer.this.updateIdentifier.getText());
                DeviceChoiceCustomizer.this.listeners.firePropertyChange("updateIdentifier", oldUpdateIdentifier, DeviceChoiceCustomizer.this.bean.getUpdateIdentifier());
            }
        });
        this.add(jp, "South");
    }
}

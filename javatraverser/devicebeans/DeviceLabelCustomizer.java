package devicebeans;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DeviceLabelCustomizer extends DeviceCustomizer implements Customizer{
    private static final long serialVersionUID = 5854997476193687362L;
    DeviceLabel               bean             = null;
    // Checkbox editable;
    Checkbox                  displayEvaluated;
    Button                    doneButton;
    TextField                 labelString, identifier, numCols;
    PropertyChangeSupport     listeners        = new PropertyChangeSupport(this);
    Choice                    nids;
    Object                    obj;
    // Checkbox showState;
    Checkbox                  textOnly;

    public DeviceLabelCustomizer(){}

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
        this.bean = (DeviceLabel)o;
        this.setLayout(new BorderLayout());
        Panel jp = new Panel();
        jp.setLayout(new GridLayout(3, 1));
        Panel jp1 = new Panel();
        jp1.add(new Label("Label: "));
        jp1.add(this.labelString = new TextField(30));
        this.labelString.setText(this.bean.getLabelString());
        jp1.add(new Label("Num. Columns: "));
        jp1.add(this.numCols = new TextField(4));
        final int cols = this.bean.getNumCols();
        this.numCols.setText((new Integer(cols)).toString());
        jp.add(jp1);
        jp1 = new Panel();
        // jp1.add(showState = new Checkbox("Show state: ", bean.getShowState()));
        jp1.add(this.textOnly = new Checkbox("Text only: ", this.bean.getTextOnly()));
        // jp1.add(editable = new Checkbox("Editable: ", bean.getEditable()));
        jp1.add(new Label("Offset nid: "));
        jp1.add(this.nids = new Choice());
        final String names[] = DeviceCustomizer.getDeviceFields();
        if(names != null) for(final String name2 : names)
            this.nids.addItem(name2);
        int offsetNid = this.bean.getOffsetNid();
        if(offsetNid > 0) offsetNid--;
        this.nids.select(offsetNid);
        jp.add(jp1);
        jp1 = new Panel();
        jp1.add(this.displayEvaluated = new Checkbox("Display Evaluated: ", this.bean.getDisplayEvaluated()));
        jp1.add(new Label("Opt. identifier: "));
        jp1.add(this.identifier = new TextField(this.bean.getIdentifier(), 20));
        jp.add(jp1);
        this.add(jp, "Center");
        jp = new Panel();
        jp.add(this.doneButton = new Button("Apply"));
        this.doneButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(DeviceLabelCustomizer.this.bean == null) return;
                final String oldLabel = DeviceLabelCustomizer.this.bean.getLabelString();
                DeviceLabelCustomizer.this.bean.setLabelString(DeviceLabelCustomizer.this.labelString.getText());
                DeviceLabelCustomizer.this.listeners.firePropertyChange("labelString", oldLabel, DeviceLabelCustomizer.this.bean.getLabelString());
                final String colStr = DeviceLabelCustomizer.this.numCols.getText();
                final int oldCols = DeviceLabelCustomizer.this.bean.getNumCols();
                DeviceLabelCustomizer.this.bean.setNumCols(Integer.parseInt(colStr));
                DeviceLabelCustomizer.this.listeners.firePropertyChange("numCols", oldCols, DeviceLabelCustomizer.this.bean.getNumCols());
                final boolean oldTextOnly = DeviceLabelCustomizer.this.bean.getTextOnly();
                DeviceLabelCustomizer.this.bean.setTextOnly(DeviceLabelCustomizer.this.textOnly.getState());
                DeviceLabelCustomizer.this.listeners.firePropertyChange("textOnly", oldTextOnly, DeviceLabelCustomizer.this.bean.getTextOnly());
                /*
                            boolean oldEditable = bean.getEditable();
                            bean.setEditable(editable.getState());
                            listeners.firePropertyChange("editable", oldEditable, bean.getEditable());
                            boolean oldShowState = bean.getShowState();
                            bean.setShowState(showState.getState());
                            listeners.firePropertyChange("showState", oldShowState, bean.getShowState());
                */
                final boolean oldDisplayEvaluated = DeviceLabelCustomizer.this.bean.getDisplayEvaluated();
                DeviceLabelCustomizer.this.bean.setDisplayEvaluated(DeviceLabelCustomizer.this.displayEvaluated.getState());
                DeviceLabelCustomizer.this.listeners.firePropertyChange("displayEvaluated", oldDisplayEvaluated, DeviceLabelCustomizer.this.bean.getDisplayEvaluated());
                final int oldOffsetNid = DeviceLabelCustomizer.this.bean.getOffsetNid();
                DeviceLabelCustomizer.this.bean.setOffsetNid(DeviceLabelCustomizer.this.nids.getSelectedIndex() + 1);
                DeviceLabelCustomizer.this.listeners.firePropertyChange("offsetNid", oldOffsetNid, DeviceLabelCustomizer.this.bean.getOffsetNid());
                final String oldIdentifier = DeviceLabelCustomizer.this.bean.getIdentifier();
                DeviceLabelCustomizer.this.bean.setIdentifier(DeviceLabelCustomizer.this.identifier.getText());
                DeviceLabelCustomizer.this.listeners.firePropertyChange("identifier", oldIdentifier, DeviceLabelCustomizer.this.bean.getIdentifier());
                DeviceLabelCustomizer.this.repaint();
            }
        });
        this.add(jp, "South");
    }
}

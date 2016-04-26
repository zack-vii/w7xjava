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

public class DeviceFieldCustomizer extends DeviceCustomizer implements Customizer{
    private static final long serialVersionUID = 4195496221996739378L;
    DeviceField               bean             = null;
    Checkbox                  displayEvaluated;
    Button                    doneButton;
    Checkbox                  editable;
    TextField                 labelString, identifier, numCols;
    PropertyChangeSupport     listeners        = new PropertyChangeSupport(this);
    Choice                    nids;
    Object                    obj;
    Checkbox                  showState;
    Checkbox                  textOnly;

    public DeviceFieldCustomizer(){}

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
        this.bean = (DeviceField)o;
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
        jp1.add(this.showState = new Checkbox("Show state: ", this.bean.getShowState()));
        jp1.add(this.textOnly = new Checkbox("Text only: ", this.bean.getTextOnly()));
        jp1.add(this.editable = new Checkbox("Editable: ", this.bean.getEditable()));
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
                if(DeviceFieldCustomizer.this.bean == null) return;
                final String oldLabel = DeviceFieldCustomizer.this.bean.getLabelString();
                DeviceFieldCustomizer.this.bean.setLabelString(DeviceFieldCustomizer.this.labelString.getText());
                DeviceFieldCustomizer.this.listeners.firePropertyChange("labelString", oldLabel, DeviceFieldCustomizer.this.bean.getLabelString());
                final String colStr = DeviceFieldCustomizer.this.numCols.getText();
                final int oldCols = DeviceFieldCustomizer.this.bean.getNumCols();
                DeviceFieldCustomizer.this.bean.setNumCols(Integer.parseInt(colStr));
                DeviceFieldCustomizer.this.listeners.firePropertyChange("numCols", oldCols, DeviceFieldCustomizer.this.bean.getNumCols());
                final boolean oldTextOnly = DeviceFieldCustomizer.this.bean.getTextOnly();
                DeviceFieldCustomizer.this.bean.setTextOnly(DeviceFieldCustomizer.this.textOnly.getState());
                DeviceFieldCustomizer.this.listeners.firePropertyChange("textOnly", oldTextOnly, DeviceFieldCustomizer.this.bean.getTextOnly());
                final boolean oldEditable = DeviceFieldCustomizer.this.bean.getEditable();
                DeviceFieldCustomizer.this.bean.setEditable(DeviceFieldCustomizer.this.editable.getState());
                DeviceFieldCustomizer.this.listeners.firePropertyChange("editable", oldEditable, DeviceFieldCustomizer.this.bean.getEditable());
                final boolean oldShowState = DeviceFieldCustomizer.this.bean.getShowState();
                DeviceFieldCustomizer.this.bean.setShowState(DeviceFieldCustomizer.this.showState.getState());
                DeviceFieldCustomizer.this.listeners.firePropertyChange("showState", oldShowState, DeviceFieldCustomizer.this.bean.getShowState());
                final boolean oldDisplayEvaluated = DeviceFieldCustomizer.this.bean.getDisplayEvaluated();
                DeviceFieldCustomizer.this.bean.setDisplayEvaluated(DeviceFieldCustomizer.this.displayEvaluated.getState());
                DeviceFieldCustomizer.this.listeners.firePropertyChange("displayEvaluated", oldDisplayEvaluated, DeviceFieldCustomizer.this.bean.getDisplayEvaluated());
                final int oldOffsetNid = DeviceFieldCustomizer.this.bean.getOffsetNid();
                DeviceFieldCustomizer.this.bean.setOffsetNid(DeviceFieldCustomizer.this.nids.getSelectedIndex() + 1);
                DeviceFieldCustomizer.this.listeners.firePropertyChange("offsetNid", oldOffsetNid, DeviceFieldCustomizer.this.bean.getOffsetNid());
                final String oldIdentifier = DeviceFieldCustomizer.this.bean.getIdentifier();
                DeviceFieldCustomizer.this.bean.setIdentifier(DeviceFieldCustomizer.this.identifier.getText());
                DeviceFieldCustomizer.this.listeners.firePropertyChange("identifier", oldIdentifier, DeviceFieldCustomizer.this.bean.getIdentifier());
                DeviceFieldCustomizer.this.repaint();
            }
        });
        this.add(jp, "South");
    }
}

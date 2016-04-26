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

public class DeviceWaveCustomizer extends DeviceCustomizer implements Customizer{
    private static final long serialVersionUID = -2300317292623008002L;
    DeviceWave                bean             = null;
    Button                    doneButton;
    TextField                 identifier, updateIdentifier, updateExpression;
    PropertyChangeSupport     listeners        = new PropertyChangeSupport(this);
    Checkbox                  maxXVisible;
    Checkbox                  maxYVisible;
    Checkbox                  minXVisible;
    Checkbox                  minYVisible;
    Choice                    nids;
    Object                    obj;

    public DeviceWaveCustomizer(){}

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
        this.bean = (DeviceWave)o;
        this.setLayout(new BorderLayout());
        Panel jp = new Panel();
        jp.setLayout(new GridLayout(3, 1));
        Panel jp1 = new Panel();
        jp1.add(new Label("Opt. identifier: "));
        jp1.add(this.identifier = new TextField(this.bean.getIdentifier(), 20));
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
        jp1.add(this.minXVisible = new Checkbox("Min X Visible: ", this.bean.getMinXVisible()));
        jp1.add(this.maxXVisible = new Checkbox("Max X Visible: ", this.bean.getMaxXVisible()));
        jp1 = new Panel();
        jp1.add(this.minYVisible = new Checkbox("Min Y Visible: ", this.bean.getMinYVisible()));
        jp1.add(this.maxYVisible = new Checkbox("Max Y Visible: ", this.bean.getMaxYVisible()));
        jp.add(jp1);
        jp1 = new Panel();
        jp1.add(new Label("Update id: "));
        jp1.add(this.updateIdentifier = new TextField(this.bean.getUpdateIdentifier(), 10));
        jp1.add(new Label("Update expr: "));
        jp1.add(this.updateExpression = new TextField(this.bean.getUpdateExpression(), 30));
        jp.add(jp1);
        this.add(jp, "Center");
        jp = new Panel();
        jp.add(this.doneButton = new Button("Apply"));
        this.doneButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final boolean oldMinXVisible = DeviceWaveCustomizer.this.bean.getMinXVisible();
                DeviceWaveCustomizer.this.bean.setMinXVisible(DeviceWaveCustomizer.this.minXVisible.getState());
                DeviceWaveCustomizer.this.listeners.firePropertyChange("minXVisible", oldMinXVisible, DeviceWaveCustomizer.this.bean.getMinXVisible());
                final boolean oldMaxXVisible = DeviceWaveCustomizer.this.bean.getMaxXVisible();
                DeviceWaveCustomizer.this.bean.setMaxXVisible(DeviceWaveCustomizer.this.maxXVisible.getState());
                DeviceWaveCustomizer.this.listeners.firePropertyChange("maxXVisible", oldMaxXVisible, DeviceWaveCustomizer.this.bean.getMaxXVisible());
                final boolean oldMinYVisible = DeviceWaveCustomizer.this.bean.getMinYVisible();
                DeviceWaveCustomizer.this.bean.setMinYVisible(DeviceWaveCustomizer.this.minYVisible.getState());
                DeviceWaveCustomizer.this.listeners.firePropertyChange("minYVisible", oldMinYVisible, DeviceWaveCustomizer.this.bean.getMinYVisible());
                final boolean oldMaxYVisible = DeviceWaveCustomizer.this.bean.getMaxYVisible();
                DeviceWaveCustomizer.this.bean.setMaxYVisible(DeviceWaveCustomizer.this.maxYVisible.getState());
                DeviceWaveCustomizer.this.listeners.firePropertyChange("maxYVisible", oldMaxYVisible, DeviceWaveCustomizer.this.bean.getMaxYVisible());
                final int oldOffsetNid = DeviceWaveCustomizer.this.bean.getOffsetNid();
                DeviceWaveCustomizer.this.bean.setOffsetNid(DeviceWaveCustomizer.this.nids.getSelectedIndex() + 1);
                DeviceWaveCustomizer.this.listeners.firePropertyChange("offsetNid", oldOffsetNid, DeviceWaveCustomizer.this.bean.getOffsetNid());
                final String oldIdentifier = DeviceWaveCustomizer.this.bean.getIdentifier();
                DeviceWaveCustomizer.this.bean.setIdentifier(DeviceWaveCustomizer.this.identifier.getText());
                DeviceWaveCustomizer.this.listeners.firePropertyChange("identifier", oldIdentifier, DeviceWaveCustomizer.this.bean.getIdentifier());
                final String oldUpdateIdentifier = DeviceWaveCustomizer.this.bean.getIdentifier();
                DeviceWaveCustomizer.this.bean.setUpdateIdentifier(DeviceWaveCustomizer.this.updateIdentifier.getText().trim());
                DeviceWaveCustomizer.this.listeners.firePropertyChange("updateIdentifier", oldUpdateIdentifier, DeviceWaveCustomizer.this.bean.getUpdateIdentifier());
                final String oldUpdateExpression = DeviceWaveCustomizer.this.bean.getUpdateExpression();
                DeviceWaveCustomizer.this.bean.setUpdateExpression(DeviceWaveCustomizer.this.updateExpression.getText().trim());
                DeviceWaveCustomizer.this.listeners.firePropertyChange("updateExpression", oldUpdateExpression, DeviceWaveCustomizer.this.bean.getUpdateExpression());
            }
        });
        this.add(jp, "South");
    }
}

package devicebeans.devicewave;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import devicebeans.DeviceCustomizer;

@SuppressWarnings("serial")
public class DeviceWaveDisplayCustomizer extends DeviceCustomizer implements Customizer{
    DeviceWaveDisplay     bean      = null;
    Button                doneButton;
    PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    Choice                nids;
    Object                obj;

    public DeviceWaveDisplayCustomizer(){}

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
        this.bean = (DeviceWaveDisplay)o;
        this.setLayout(new BorderLayout());
        Panel jp = new Panel();
        jp.add(new Label("Offset nid: "));
        jp.add(this.nids = new Choice());
        final String names[] = DeviceCustomizer.getDeviceFields();
        if(names != null) for(final String name2 : names)
            this.nids.addItem(name2);
        int offsetNid = this.bean.getOffsetNid();
        if(offsetNid > 0) offsetNid--;
        this.nids.select(offsetNid);
        this.add(jp, "Center");
        jp = new Panel();
        jp.add(this.doneButton = new Button("Apply"));
        this.doneButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(DeviceWaveDisplayCustomizer.this.bean == null) return;
                final int oldOffsetNid = DeviceWaveDisplayCustomizer.this.bean.getOffsetNid();
                DeviceWaveDisplayCustomizer.this.bean.setOffsetNid(DeviceWaveDisplayCustomizer.this.nids.getSelectedIndex() + 1);
                DeviceWaveDisplayCustomizer.this.listeners.firePropertyChange("offsetNid", oldOffsetNid, DeviceWaveDisplayCustomizer.this.bean.getOffsetNid());
                DeviceWaveDisplayCustomizer.this.repaint();
            }
        });
        this.add(jp, "South");
    }
}

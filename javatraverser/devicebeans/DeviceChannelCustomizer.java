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

public class DeviceChannelCustomizer extends DeviceCustomizer implements Customizer{
    private static final long serialVersionUID = -5134105662707684010L;
    DeviceChannel             bean             = null;
    Button                    doneButton;
    TextField                 labelString, lines, columns, updateId, showVal;
    PropertyChangeSupport     listeners        = new PropertyChangeSupport(this);
    Choice                    nids;
    Object                    obj;
    Checkbox                  showBorder, inSameLine, showState;

    public DeviceChannelCustomizer(){}

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
        this.bean = (DeviceChannel)o;
        this.setLayout(new BorderLayout());
        Panel jp = new Panel();
        jp.setLayout(new GridLayout(4, 1));
        Panel jp1 = new Panel();
        jp1.add(new Label("Label: "));
        jp1.add(this.labelString = new TextField(30));
        this.labelString.setText(this.bean.getLabelString());
        jp1.add(this.showState = new Checkbox("Show state: ", this.bean.getShowState()));
        jp.add(jp1);
        jp1 = new Panel();
        jp1.add(this.showBorder = new Checkbox("Border visible: ", this.bean.getBorderVisible()));
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
        jp1.add(new Label("Lines: "));
        jp1.add(this.lines = new TextField(4));
        this.lines.setText((new Integer(this.bean.getLines())).toString());
        jp1.add(new Label("Columns: "));
        jp1.add(this.columns = new TextField(4));
        this.columns.setText((new Integer(this.bean.getColumns())).toString());
        jp1.add(this.inSameLine = new Checkbox("Same line: ", this.bean.getInSameLine()));
        jp.add(jp1);
        jp1 = new Panel();
        jp1.add(new Label("Show Id: "));
        jp1.add(this.updateId = new TextField(8));
        this.updateId.setText(this.bean.getUpdateIdentifier());
        jp1.add(new Label("Show value: "));
        jp1.add(this.showVal = new TextField(8));
        this.showVal.setText(this.bean.getShowVal());
        jp.add(jp1);
        this.add(jp, "Center");
        jp = new Panel();
        jp.add(this.doneButton = new Button("Apply"));
        this.doneButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String oldLabelString = DeviceChannelCustomizer.this.bean.getLabelString();
                DeviceChannelCustomizer.this.bean.setLabelString(DeviceChannelCustomizer.this.labelString.getText());
                DeviceChannelCustomizer.this.listeners.firePropertyChange("labelString", oldLabelString, DeviceChannelCustomizer.this.bean.getLabelString());
                final boolean oldBorderVisible = DeviceChannelCustomizer.this.bean.getBorderVisible();
                DeviceChannelCustomizer.this.bean.setBorderVisible(DeviceChannelCustomizer.this.showBorder.getState());
                DeviceChannelCustomizer.this.listeners.firePropertyChange("borderVisible", oldBorderVisible, DeviceChannelCustomizer.this.bean.getBorderVisible());
                final boolean oldShowState = DeviceChannelCustomizer.this.bean.getShowState();
                DeviceChannelCustomizer.this.bean.setShowState(DeviceChannelCustomizer.this.showState.getState());
                DeviceChannelCustomizer.this.listeners.firePropertyChange("showState", oldShowState, DeviceChannelCustomizer.this.bean.getShowState());
                final boolean oldInSameLine = DeviceChannelCustomizer.this.bean.getInSameLine();
                DeviceChannelCustomizer.this.bean.setInSameLine(DeviceChannelCustomizer.this.inSameLine.getState());
                DeviceChannelCustomizer.this.listeners.firePropertyChange("inSameLine", oldInSameLine, DeviceChannelCustomizer.this.bean.getInSameLine());
                final int oldOffsetNid = DeviceChannelCustomizer.this.bean.getOffsetNid();
                DeviceChannelCustomizer.this.bean.setOffsetNid(DeviceChannelCustomizer.this.nids.getSelectedIndex() + 1);
                DeviceChannelCustomizer.this.listeners.firePropertyChange("offsetNid", oldOffsetNid, DeviceChannelCustomizer.this.bean.getOffsetNid());
                final int oldLines = DeviceChannelCustomizer.this.bean.getLines();
                DeviceChannelCustomizer.this.bean.setLines((new Integer(DeviceChannelCustomizer.this.lines.getText())).intValue());
                DeviceChannelCustomizer.this.listeners.firePropertyChange("lines", oldLines, DeviceChannelCustomizer.this.bean.getLines());
                final int oldColumns = DeviceChannelCustomizer.this.bean.getColumns();
                DeviceChannelCustomizer.this.bean.setColumns((new Integer(DeviceChannelCustomizer.this.columns.getText())).intValue());
                DeviceChannelCustomizer.this.listeners.firePropertyChange("columns", oldColumns, DeviceChannelCustomizer.this.bean.getColumns());
                final String oldUpdateIdentifier = DeviceChannelCustomizer.this.bean.getUpdateIdentifier();
                DeviceChannelCustomizer.this.bean.setUpdateIdentifier(DeviceChannelCustomizer.this.updateId.getText());
                DeviceChannelCustomizer.this.listeners.firePropertyChange("updateIdentifier", oldUpdateIdentifier, DeviceChannelCustomizer.this.bean.getUpdateIdentifier());
                final String oldShowVal = DeviceChannelCustomizer.this.bean.getShowVal();
                DeviceChannelCustomizer.this.bean.setShowVal(DeviceChannelCustomizer.this.showVal.getText());
                DeviceChannelCustomizer.this.listeners.firePropertyChange("showVal", oldShowVal, DeviceChannelCustomizer.this.bean.getShowVal());
            }
        });
        this.add(jp, "South");
    }
}

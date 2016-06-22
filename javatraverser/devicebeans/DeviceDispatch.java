package devicebeans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import jtraverser.NodeInfo;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Nid;

@SuppressWarnings("serial")
public class DeviceDispatch extends DeviceComponent{
    class DispatchComboEditor implements ComboBoxEditor{
        int    idx;
        JLabel label = new JLabel("  Dispatch");
        String name;

        @Override
        public void addActionListener(final ActionListener l) {}

        @Override
        public Component getEditorComponent() {
            return this.label;
        }

        @Override
        public Object getItem() {
            return this.label;
        }

        @Override
        public void removeActionListener(final ActionListener l) {}

        @Override
        public void selectAll() {}

        @Override
        public void setItem(final Object obj) {}
    }
    Descriptor          actions[];
    JDialog             dialog       = null;
    DeviceDispatchField dispatch_fields[], active_field;
    int                 i, j, num_actions;
    protected boolean   initializing = false;
    JComboBox           menu;

    public DeviceDispatch(){
        this.menu = new JComboBox();
        this.menu.setEditor(new DispatchComboEditor());
        this.menu.setEditable(true);
        this.menu.setBorder(new LineBorder(Color.black, 1));
        this.add(this.menu);
    }

    protected void activateForm(final DeviceDispatchField field, final String name) {
        if(this.dialog == null){
            this.dialog = new JDialog();
            this.dialog.getContentPane().setLayout(new BorderLayout());
            this.dialog.getContentPane().add(field, "Center");
            final JPanel jp = new JPanel();
            final JButton button = new JButton("Done");
            button.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceDispatch.this.dialog.dispose();
                }
            });
            jp.add(button);
            this.dialog.getContentPane().add(jp, "South");
            this.active_field = field;
        }else{
            this.dialog.getContentPane().remove(this.active_field);
            this.dialog.getContentPane().add(field, "Center");
            this.active_field = field;
        }
        this.dialog.setTitle("Dispatch info for " + name);
        this.dialog.pack();
        this.dialog.repaint();
        this.dialog.setLocation(this.getLocationOnScreen());
        this.dialog.setVisible(true);
    }

    @Override
    public void apply() throws Exception {
        if(this.dispatch_fields == null) return;
        for(final DeviceDispatchField dispatch_field : this.dispatch_fields)
            dispatch_field.apply();
    }

    @Override
    public void apply(final int currBaseNid) {}

    @Override
    protected void displayData(final Descriptor data, final boolean is_on) {}

    @Override
    protected Descriptor getData() {
        return null;
    }

    @Override
    protected boolean getState() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initializeData(final Descriptor data, final boolean is_on)
    // data and is_on arguments are meaningless in this context
    // The class will search actions stored in the device
    // and create and manage their dispatch configurations
    {
        this.initializing = true;
        NodeInfo nodeInfo;
        if(this.subtree == null) return;
        try{
            nodeInfo = this.subtree.getInfo(this.nidData);
        }catch(final Exception e){
            System.out.println("Cannot read device NCI: " + e);
            return;
        }
        Nid currNid = new Nid(this.baseNid.getValue());
        int num_components = nodeInfo.getConglomerateNids();
        final NodeInfo nodeInfos[] = new NodeInfo[num_components + 1];
        for(this.i = this.num_actions = 0; this.i < num_components; this.i++){
            try{
                nodeInfos[this.i] = this.subtree.getInfo(currNid);
            }catch(final Exception e){
                System.out.println("Cannot read device NCI 1: " + e + " " + currNid.getValue() + " " + this.num_actions + " " + num_components);
                num_components = this.i;
                break;
            }
            if(nodeInfos[this.i].getUsage() == NodeInfo.USAGE_ACTION) this.num_actions++;
            currNid = new Nid(currNid.getValue() + 1);
        }
        this.actions = new Descriptor[this.num_actions];
        this.dispatch_fields = new DeviceDispatchField[this.num_actions];
        currNid = new Nid(this.nidData.getValue());
        for(this.i = this.j = this.num_actions = 0; this.i < num_components; this.i++){
            if(nodeInfos[this.i].getUsage() == NodeInfo.USAGE_ACTION){
                try{
                    this.actions[this.j] = this.subtree.evaluate(currNid);
                }catch(final Exception e){
                    System.out.println("Cannot read device actions: " + e);
                    return;
                }
                this.dispatch_fields[this.j] = new DeviceDispatchField();
                this.dispatch_fields[this.j].setSubtree(this.subtree);
                this.dispatch_fields[this.j].setOffsetNid(this.i);
                this.dispatch_fields[this.j].configure(this.nidData.getValue());
                this.j++;
            }
            currNid = new Nid(currNid.getValue() + 1);
        }
        for(this.i = 0; this.i < num_components; this.i++){
            if(nodeInfos[this.i].getUsage() == NodeInfo.USAGE_ACTION){
                final String name = nodeInfos[this.i].getName();
                this.menu.addItem(name);
            }
        }
        this.menu.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int idx = DeviceDispatch.this.menu.getSelectedIndex();
                if(idx < 0 || idx >= DeviceDispatch.this.dispatch_fields.length) return;
                DeviceDispatch.this.activateForm(DeviceDispatch.this.dispatch_fields[DeviceDispatch.this.menu.getSelectedIndex()], (String)DeviceDispatch.this.menu.getSelectedItem());
            }
        });
        this.initializing = false;
    }

    @Override
    public void reset() {
        if(this.dispatch_fields == null) return;
        for(final DeviceDispatchField dispatch_field : this.dispatch_fields)
            dispatch_field.reset();
    }
}

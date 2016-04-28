package jTraverser.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import jTraverser.dialogs.TreeDialog;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Range;
import mds.data.descriptor_r.With_Units;

@SuppressWarnings("serial")
public class AxisEditor extends JPanel implements ActionListener, Editor{
    JComboBox         combo;
    Descriptor        data;
    TreeDialog        dialog;
    boolean           editable = true;
    LabeledExprEditor expr_edit, units_edit;
    int               mode_idx, curr_mode_idx;
    JPanel            mode_panel;
    Range             range;
    RangeEditor       range_edit;
    Descriptor        units;

    @SuppressWarnings("unchecked")
    public AxisEditor(Descriptor data, final TreeDialog dialog){
        this.dialog = dialog;
        this.data = data;
        if(data == null){
            this.mode_idx = 0;
            data = null;
            this.range = null;
            this.units = null;
        }else{
            if(data instanceof With_Units){
                this.units = ((With_Units)data).getUnits();
                this.data = ((With_Units)data).getData();
            }else this.data = data;
            if(this.data instanceof Range){
                this.mode_idx = 1;
                this.range = (Range)this.data;
                this.data = null;
            }else this.mode_idx = 2;
        }
        this.curr_mode_idx = this.mode_idx;
        final String names[] = {"Undefined", "Range", "Expression"};
        this.combo = new JComboBox(names);
        this.combo.setEditable(false);
        this.combo.setSelectedIndex(this.mode_idx);
        this.combo.addActionListener(this);
        this.mode_panel = new JPanel();
        this.mode_panel.add(this.combo);
        this.setLayout(new BorderLayout());
        this.add(this.mode_panel, BorderLayout.NORTH);
        this.addEditor();
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        if(!this.editable){
            this.combo.setSelectedIndex(this.curr_mode_idx);
            return;
        }
        final int idx = this.combo.getSelectedIndex();
        if(idx == this.curr_mode_idx) return;
        switch(this.curr_mode_idx){
            case 1:
                this.mode_panel.remove(this.units_edit);
                this.remove(this.range_edit);
                break;
            case 2:
                this.mode_panel.remove(this.units_edit);
                this.remove(this.expr_edit);
                break;
        }
        this.curr_mode_idx = idx;
        this.addEditor();
        this.validate();
        this.dialog.repack();
    }

    private final void addEditor() {
        switch(this.curr_mode_idx){
            case 0:
                return;
            case 1:
                this.range_edit = new RangeEditor(this.range);
                this.units_edit = new LabeledExprEditor("Units", new ExprEditor(this.units, true));
                this.mode_panel.add(this.units_edit);
                this.add(this.range_edit, BorderLayout.CENTER);
                break;
            case 2:
                this.expr_edit = new LabeledExprEditor(this.data);
                this.units_edit = new LabeledExprEditor("Units", new ExprEditor(this.units, true));
                this.mode_panel.add(this.units_edit);
                this.add(this.expr_edit, BorderLayout.CENTER);
                break;
        }
    }

    @Override
    public final Descriptor getData() throws MdsException {
        switch(this.curr_mode_idx){
            case 0:
                return null;
            case 1:
                Descriptor units = this.units_edit.getData();
                if(units != null) return new With_Units(this.range_edit.getData(), units);
                return this.range_edit.getData();
            case 2:
                units = this.units_edit.getData();
                if(units != null) return new With_Units(this.expr_edit.getData(), units);
                return this.expr_edit.getData();
        }
        return null;
    }

    @Override
    public final boolean isNull() {
        return this.curr_mode_idx == 0;
    }

    @Override
    public final void reset() {
        switch(this.curr_mode_idx){
            case 1:
                this.mode_panel.remove(this.units_edit);
                this.units_edit = null;
                this.remove(this.range_edit);
                this.range_edit = null;
                break;
            case 2:
                this.mode_panel.remove(this.units_edit);
                this.remove(this.expr_edit);
                this.expr_edit = null;
                break;
        }
        this.curr_mode_idx = this.mode_idx;
        this.addEditor();
        this.validate();
        this.repaint();
    }

    public final void setData(Descriptor data) {
        this.data = data;
        if(data == null){
            this.mode_idx = 0;
            data = null;
            this.range = null;
            this.units = null;
        }else{
            if(data instanceof With_Units){
                this.units = ((With_Units)data).getUnits();
                this.data = ((With_Units)data).getData();
            }else this.data = data;
            if(this.data instanceof Range){
                this.mode_idx = 1;
                this.range = (Range)this.data;
                this.data = null;
            }else this.mode_idx = 2;
        }
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
        if(this.range_edit != null) this.range_edit.setEditable(editable);
        if(this.units_edit != null) this.units_edit.setEditable(editable);
    }
}

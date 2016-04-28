package jTraverser.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jTraverser.dialogs.TreeDialog;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Function;
import mds.data.descriptor_r.Param;
import mds.data.descriptor_r.With_Units;
import mds.data.descriptor_s.CString;

@SuppressWarnings("serial")
public class DataEditor extends JPanel implements ActionListener, Editor{
    public static final void main(final String[] args) {// TODO
        final JFrame f = new JFrame();
        final DataEditor ed = new DataEditor(new CString("HELLO_WORLD"), null);
        ed.setEditable(true);
        f.add(ed);
        f.pack();
        f.setVisible(true);
    }
    JComboBox         combo;
    Descriptor        data;
    TreeDialog        dialog;
    boolean           editable = true;
    LabeledExprEditor expr_edit, units_edit;
    int               mode_idx, curr_mode_idx;
    JPanel            panel;
    ParameterEditor   param_edit;
    PythonEditor      python_edit;
    Descriptor        units    = null;

    @SuppressWarnings("unchecked")
    public DataEditor(final Descriptor data, final TreeDialog dialog){
        this.dialog = dialog;
        this.checkData(data);
        this.curr_mode_idx = this.mode_idx;
        final String names[] = {"Undefined", "Expression", "Parameter", "Python Expression"};
        this.combo = new JComboBox(names);
        this.combo.setEditable(false);
        this.combo.setSelectedIndex(this.mode_idx);
        this.combo.addActionListener(this);
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        jp.add(new JLabel("Data: "));
        jp.add(this.combo);
        this.add(jp, BorderLayout.NORTH);
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
        this.remove(this.panel);
        this.curr_mode_idx = idx;
        this.addEditor();
        this.validate();
        this.dialog.repack();
    }

    private final void addEditor() {
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        switch(this.curr_mode_idx){
            case 0:
                return;
            case 1:
                this.panel.add(this.expr_edit = new LabeledExprEditor(this.data));
                break;
            case 2:
                Descriptor data, help = null, validation = null;
                if(this.data != null && this.data instanceof Param){
                    data = ((Param)this.data).getData();
                    data = this.stripUnits(data);
                    help = ((Param)this.data).getHelp();
                    validation = ((Param)this.data).getValidation();
                }else data = this.data;
                this.param_edit = new ParameterEditor(new ExprEditor(data, false, 3, 20), new ExprEditor(help, true, 7, 20), new ExprEditor(validation, false, 1, 20));
                this.panel.add(this.param_edit);
                break;
            case 3:
                if(this.data != null && this.data instanceof Function){
                    this.python_edit = new PythonEditor(((Function)this.data).getArguments());
                }else{
                    this.python_edit = new PythonEditor(null);
                }
                this.panel.add(this.python_edit);
                break;
        }
        this.units_edit = new LabeledExprEditor("Units", new ExprEditor(this.units, true));
        this.panel.add(this.units_edit, BorderLayout.NORTH);
        this.add(this.panel, BorderLayout.CENTER);
    }

    private final void checkData(final Descriptor data) {
        this.data = data;
        this.units = null;
        if(data == null){
            this.mode_idx = 0;
        }else{
            this.data = this.stripUnits(this.data);
            if(this.data instanceof Param) this.mode_idx = 2;
            else if(this.data instanceof Function && ((Function)this.data).getOpCode() == PythonEditor.OPC_FUN){
                final Descriptor[] args = ((Function)data).getArguments();
                try{
                    if(args != null && args.length > 2 && args[1] != null && (args[1] instanceof CString) && ((CString)args[1]).getValue() != null && ((CString)args[1]).getValue().toUpperCase().equals("PY")) this.mode_idx = 3;
                    else this.mode_idx = 1;
                }catch(final Exception exc){
                    this.mode_idx = 1;
                }
            }else this.mode_idx = 1;
        }
    }

    @Override
    public final Descriptor getData() throws MdsException {
        Descriptor units;
        switch(this.curr_mode_idx){
            case 0:
                return null;
            case 1:
                units = this.units_edit.getData();
                if(units == null) return this.expr_edit.getData();
                if(units instanceof CString && ((CString)units).getValue().equals("")) return this.expr_edit.getData();
                return new With_Units(this.expr_edit.getData(), units);
            case 2:
                units = this.units_edit.getData();
                if(units == null) return this.param_edit.getData();
                if(units instanceof CString && ((CString)units).getValue().equals("")) return this.param_edit.getData();
                return new With_Units(this.param_edit.getData(), units);
            case 3:
                units = this.units_edit.getData();
                if(units == null) return this.python_edit.getData();
                if(units instanceof CString && ((CString)units).getValue().equals("")) return this.python_edit.getData();
                return new With_Units(this.python_edit.getData(), units);
        }
        return null;
    }

    @Override
    public final boolean isNull() {
        return this.curr_mode_idx == 0;
    }

    @Override
    public final void reset() {
        if(this.curr_mode_idx > 0) this.remove(this.panel);
        this.curr_mode_idx = this.mode_idx;
        this.combo.setSelectedIndex(this.mode_idx);
        this.addEditor();
        this.validate();
        this.dialog.repack();
    }

    public final void setData(final Descriptor data) {
        this.checkData(data);
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        this.combo.setEnabled(editable);
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
        if(this.python_edit != null) this.python_edit.setEditable(editable);
        if(this.units_edit != null) this.units_edit.setEditable(editable);
    }

    private final Descriptor stripUnits(final Descriptor data) {
        if(!(data instanceof With_Units)) return data;
        if(this.units == null) this.units = ((With_Units)data).getUnits();
        return ((With_Units)data).getData();
    }
}
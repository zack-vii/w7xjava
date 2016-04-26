package jTraverser.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jTraverser.dialogs.TreeDialog;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Method;
import mds.data.descriptor_r.Procedure;
import mds.data.descriptor_r.Program;
import mds.data.descriptor_r.Routine;

@SuppressWarnings("deprecation")
public class TaskEditor extends JPanel implements ActionListener, Editor{
    private static final long serialVersionUID = -5397713086983571561L;
    JComboBox                 combo;
    Descriptor                data;
    TreeDialog                dialog;
    boolean                   editable         = true;
    LabeledExprEditor         expr_edit;
    MethodEditor              method_edit;
    int                       mode_idx, curr_mode_idx;
    ProcedureEditor           procedure_edit;
    ProgramEditor             program_edit;
    RoutineEditor             routine_edit;

    @SuppressWarnings("unchecked")
    public TaskEditor(final Descriptor data, final TreeDialog dialog){
        this.dialog = dialog;
        this.data = data;
        if(data == null) this.mode_idx = 0;
        else{
            if(data instanceof Method) this.mode_idx = 1;
            else if(data instanceof Routine) this.mode_idx = 2;
            else if(data instanceof Procedure) this.mode_idx = 3;
            else if(data instanceof Program) this.mode_idx = 4;
            else this.mode_idx = 5;
        }
        this.curr_mode_idx = this.mode_idx;
        final String names[] = {"Undefined", "Method", "Routine", "Procedure (depreciated)", "Program (depreciated)", "Expression"};
        this.combo = new JComboBox(names);
        this.combo.setEditable(false);
        this.combo.setSelectedIndex(this.mode_idx);
        this.combo.addActionListener(this);
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        jp.add(new JLabel("Task: "));
        jp.add(this.combo);
        this.add(jp, BorderLayout.PAGE_START);
        this.addEditor();
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        final int idx = this.combo.getSelectedIndex();
        if(idx != this.curr_mode_idx) this.setMode(idx);
    }

    private void addEditor() {
        switch(this.curr_mode_idx){
            case 0:
                return;
            case 1:
                if(this.mode_idx == this.curr_mode_idx) this.method_edit = new MethodEditor((Method)this.data);
                else this.method_edit = new MethodEditor(null);
                this.add(this.method_edit);
                break;
            case 2:
                if(this.mode_idx == this.curr_mode_idx) this.routine_edit = new RoutineEditor((Routine)this.data);
                else this.routine_edit = new RoutineEditor(null);
                this.add(this.routine_edit);
                break;
            case 3:
                if(this.mode_idx == this.curr_mode_idx) this.procedure_edit = new ProcedureEditor((Procedure)this.data);
                else this.procedure_edit = new ProcedureEditor(null);
                this.add(this.procedure_edit);
                break;
            case 4:
                if(this.mode_idx == this.curr_mode_idx) this.program_edit = new ProgramEditor((Program)this.data);
                else this.program_edit = new ProgramEditor(null);
                this.add(this.program_edit);
                break;
            case 5:
                this.expr_edit = new LabeledExprEditor(this.data);
                this.add(this.expr_edit);
                break;
        }
    }

    @Override
    public final Descriptor getData() throws MdsException {
        switch(this.curr_mode_idx){
            case 0:
                return null;
            case 1:
                return this.method_edit.getData();
            case 2:
                return this.routine_edit.getData();
            case 3:
                return this.procedure_edit.getData();
            case 4:
                return this.program_edit.getData();
            case 5:
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
        this.combo.setSelectedIndex(this.mode_idx);
        this.setMode(this.mode_idx);
    }

    public void setData(final Descriptor data) {
        this.data = data;
        if(data == null) this.mode_idx = 0;
        else{
            if(data instanceof Method) this.mode_idx = 1;
            else if(data instanceof Routine) this.mode_idx = 2;
            else if(data instanceof Procedure) this.mode_idx = 3;
            else if(data instanceof Program) this.mode_idx = 4;
            else this.mode_idx = 5;
        }
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        this.combo.setEnabled(editable);
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
        if(this.method_edit != null) this.method_edit.setEditable(editable);
        if(this.routine_edit != null) this.routine_edit.setEditable(editable);
        if(this.program_edit != null) this.program_edit.setEditable(editable);
        if(this.procedure_edit != null) this.procedure_edit.setEditable(editable);
    }

    private void setMode(final int idx) {
        switch(this.curr_mode_idx){
            case 1:
                this.remove(this.method_edit);
                break;
            case 2:
                this.remove(this.routine_edit);
                break;
            case 3:
                this.remove(this.procedure_edit);
                break;
            case 4:
                this.remove(this.program_edit);
                break;
            case 5:
                this.remove(this.expr_edit);
                break;
        }
        this.curr_mode_idx = idx;
        this.addEditor();
        this.validate();
        this.dialog.repack();
        this.repaint();
    }
}
package jTraverser.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jTraverser.dialogs.TreeDialog;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Action;

public class ActionEditor extends JPanel implements ActionListener, Editor{
    private static final long serialVersionUID = 5505321760234697543L;
    JPanel                    action_panel, debug_panel;
    JComboBox                 combo;
    LabeledExprEditor         comp_edit, error_edit, expr_edit, perf_edit;
    Descriptor                data;
    TreeDialog                dialog;
    DispatchEditor            dispatch_edit;
    boolean                   editable;
    int                       mode_idx, curr_mode_idx;
    TaskEditor                task_edit;

    @SuppressWarnings("unchecked")
    public ActionEditor(final Descriptor data, final TreeDialog dialog){
        this.dialog = dialog;
        this.data = data;
        if(data == null) this.mode_idx = 0;
        else{
            if(data instanceof Action) this.mode_idx = 1;
            else this.mode_idx = 2;
        }
        if(data == null) this.data = new Action(null, null, null, null, null);
        this.curr_mode_idx = this.mode_idx;
        final String names[] = {"Undefined", "Action", "Expression"};
        this.combo = new JComboBox(names);
        this.combo.setEditable(false);
        this.combo.setSelectedIndex(this.mode_idx);
        this.combo.addActionListener(this);
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        jp.add(new JLabel("Action: "));
        jp.add(this.combo);
        this.add(jp, BorderLayout.NORTH);
        this.addEditor();
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        final int idx = this.combo.getSelectedIndex();
        if(idx != this.curr_mode_idx) this.setMode(idx);
    }

    private final void addEditor() {
        switch(this.curr_mode_idx){
            case 0:
                return;
            case 1:
                if(this.curr_mode_idx == this.mode_idx){
                    this.dispatch_edit = new DispatchEditor(((Action)this.data).getDispatch(), this.dialog);
                    this.task_edit = new TaskEditor(((Action)this.data).getTask(), this.dialog);
                    this.error_edit = new LabeledExprEditor("ErrorLogs", new ExprEditor(((Action)this.data).getErrorLogs(), true));
                    this.comp_edit = new LabeledExprEditor("Completion", new ExprEditor(((Action)this.data).getCompletionMessage(), true));
                    this.perf_edit = new LabeledExprEditor("Performance", new ExprEditor(((Action)this.data).getPerformance(), false));
                }else{
                    this.dispatch_edit = new DispatchEditor(null, this.dialog);
                    this.task_edit = new TaskEditor(null, this.dialog);
                    this.error_edit = new LabeledExprEditor("ErrorLogs", new ExprEditor(null, true));
                    this.comp_edit = new LabeledExprEditor("Completion", new ExprEditor(null, true));
                    this.perf_edit = new LabeledExprEditor("Performance", new ExprEditor(null, false));
                }
                this.action_panel = new JPanel();
                this.action_panel.setLayout(new GridLayout(1, 2));
                this.action_panel.add(this.dispatch_edit);
                this.action_panel.add(this.task_edit);
                this.add(this.action_panel, BorderLayout.CENTER);
                this.debug_panel = new JPanel(new GridLayout(3, 1));
                this.add(this.debug_panel, BorderLayout.SOUTH);
                this.debug_panel.add(this.error_edit);
                this.debug_panel.add(this.comp_edit, BorderLayout.SOUTH);
                this.debug_panel.add(this.perf_edit, BorderLayout.SOUTH);
                break;
            case 2:
                this.expr_edit = new LabeledExprEditor(this.data);
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
                return new Action(this.dispatch_edit.getData(), this.task_edit.getData(), this.error_edit.getData(), this.comp_edit.getData(), this.perf_edit.getData());
            case 2:
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

    public final void setData(final Descriptor data) {
        this.data = data;
        if(data == null) this.mode_idx = 0;
        else if(data instanceof Action) this.mode_idx = 1;
        else this.mode_idx = 2;
        if(data == null) this.data = new Action(null, null, null, null, null);
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        this.combo.setEnabled(editable);
        if(this.task_edit != null) this.task_edit.setEditable(editable);
        if(this.dispatch_edit != null) this.dispatch_edit.setEditable(editable);
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
        if(this.error_edit != null) this.error_edit.setEditable(editable);
        if(this.comp_edit != null) this.comp_edit.setEditable(editable);
        if(this.perf_edit != null) this.perf_edit.setEditable(editable);
    }

    private final void setMode(final int idx) {
        switch(this.curr_mode_idx){
            case 1:
                this.remove(this.action_panel);
                this.remove(this.debug_panel);
                this.action_panel = this.debug_panel = null;
                this.task_edit = null;
                this.dispatch_edit = null;
                this.error_edit = this.comp_edit = this.perf_edit = null;
                break;
            case 2:
                this.remove(this.expr_edit);
                this.expr_edit = null;
                break;
        }
        this.curr_mode_idx = idx;
        this.addEditor();
        this.validate();
        this.dialog.repack();
        this.repaint();
    }
}
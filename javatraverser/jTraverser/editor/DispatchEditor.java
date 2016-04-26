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
import mds.data.descriptor_r.Dispatch;

public class DispatchEditor extends JPanel implements ActionListener, Editor{
    class DispatchEdt extends JPanel{
        /**
         *
         */
        private static final long serialVersionUID = -6076590353205086521L;
        Dispatch                  data;
        LabeledExprEditor         ident_edit, phase_edit, sequence_edit, completion_edit;
        boolean                   is_sequential    = false;

        public DispatchEdt(final Dispatch data, final byte sched){
            this.data = data;
            if(this.data == null) this.data = new Dispatch(sched, null, null, null, null);
            this.ident_edit = new LabeledExprEditor("Ident", new ExprEditor(this.data.getIdent(), true));
            this.phase_edit = new LabeledExprEditor("Phase", new ExprEditor(this.data.getPhase(), true));
            this.ident_edit = new LabeledExprEditor("Ident", new ExprEditor(this.data.getIdent(), true));
            switch(this.data.getType()){
                case Dispatch.SCHED_ASYNC:
                    this.sequence_edit = new LabeledExprEditor("ASYNCH", new ExprEditor(this.data.getWhen(), false));
                    break;
                case Dispatch.SCHED_SEQ:
                    this.sequence_edit = new LabeledExprEditor("Sequence", new ExprEditor(this.data.getWhen(), false));
                    break;
                case Dispatch.SCHED_COND:
                    this.sequence_edit = new LabeledExprEditor("After", new ExprEditor(this.data.getWhen(), false));
                    break;
                default:
                    this.sequence_edit = new LabeledExprEditor("Unknown", new ExprEditor(this.data.getWhen(), false));
                    break;
            }
            this.completion_edit = new LabeledExprEditor("Completion", new ExprEditor(this.data.getCompletion(), true));
            final JPanel jp = new JPanel();
            jp.setLayout(new GridLayout(4, 1));
            jp.add(this.ident_edit);
            jp.add(this.phase_edit);
            jp.add(this.sequence_edit);
            jp.add(this.completion_edit);
            this.setLayout(new BorderLayout());
            this.add(jp, BorderLayout.NORTH);
        }

        public final Dispatch getData() throws MdsException {
            return new Dispatch(this.data.getType(), this.ident_edit.getData(), this.phase_edit.getData(), this.sequence_edit.getData(), this.completion_edit.getData());
        }

        public final void reset() {
            DispatchEditor.this.combo.setSelectedIndex(DispatchEditor.this.mode_idx);
            this.ident_edit.reset();
            this.phase_edit.reset();
            this.sequence_edit.reset();
            this.completion_edit.reset();
        }

        public final void setEditable(final boolean editable) {
            if(this.ident_edit != null) this.ident_edit.setEditable(editable);
            if(this.phase_edit != null) this.phase_edit.setEditable(editable);
            if(this.sequence_edit != null) this.sequence_edit.setEditable(editable);
            if(this.completion_edit != null) this.completion_edit.setEditable(editable);
        }
    }
    private static final long serialVersionUID = -9187606764049896331L;
    JComboBox                 combo;
    Descriptor                data;
    TreeDialog                dialog;
    DispatchEdt               dispatch_edit;
    boolean                   editable         = true;
    LabeledExprEditor         expr_edit;
    byte                      mode_idx, curr_mode_idx;

    @SuppressWarnings("unchecked")
    public DispatchEditor(final Descriptor data, final TreeDialog dialog){
        this.dialog = dialog;
        this.data = data;
        if(data == null) this.mode_idx = 0;
        else{
            final Dispatch ddata = (Dispatch)data;
            this.mode_idx = ddata.getType() > 4 ? 4 : ddata.getType();
        }
        this.curr_mode_idx = this.mode_idx;
        final String[] names = {"Undefined", "Asynchron", "Sequential", "Conditional", "Expression"};
        this.combo = new JComboBox(names);
        this.combo.setEditable(false);
        this.combo.setSelectedIndex(this.mode_idx);
        this.combo.addActionListener(this);
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        jp.add(new JLabel("Dispatch: "));
        jp.add(this.combo);
        this.add(jp, BorderLayout.PAGE_START);
        this.addEditor();
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        final byte idx = (byte)this.combo.getSelectedIndex();
        if(idx != this.curr_mode_idx) this.setMode(idx);
    }

    private final void addEditor() {
        switch(this.curr_mode_idx){
            case 0:
                return;
            case 1:
            case 2:
            case 3:
                if(this.mode_idx == this.curr_mode_idx) this.dispatch_edit = new DispatchEdt((Dispatch)this.data, this.curr_mode_idx);
                else this.dispatch_edit = new DispatchEdt(null, this.curr_mode_idx);
                this.add(this.dispatch_edit);
                break;
            case 4:
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
            case 2:
            case 3:
                return this.dispatch_edit.getData();
            case 4:
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
        else{
            final Dispatch ddata = (Dispatch)data;
            this.mode_idx = ddata.getType() > 4 ? 4 : ddata.getType();
        }
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        this.combo.setEnabled(editable);
        if(this.dispatch_edit != null) this.dispatch_edit.setEditable(editable);
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
    }

    private void setMode(final byte idx) {
        switch(this.curr_mode_idx){
            case 1:
            case 2:
            case 3:
                this.remove(this.dispatch_edit);
                break;
            case 4:
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
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
import mds.data.descriptor_r.Window;

public class WindowEditor extends JPanel implements ActionListener, Editor{
    static class WindowEdt extends JPanel{
        private static final long serialVersionUID = -1087283057943746867L;
        LabeledExprEditor         startidx_edit, endidx_edit, value0_edit;
        Window                    window;

        public WindowEdt(){
            this(null);
        }

        public WindowEdt(final Window window){
            this.window = window;
            if(this.window == null){
                this.window = new Window(null, null, null);
            }
            final GridLayout gl = new GridLayout(3, 1);
            gl.setVgap(0);
            this.setLayout(gl);
            this.startidx_edit = new LabeledExprEditor("Start Idx", new ExprEditor(this.window.getStartingIdx(), false));
            this.add(this.startidx_edit);
            this.endidx_edit = new LabeledExprEditor("End Idx", new ExprEditor(this.window.getEndingIdx(), false));
            this.add(this.endidx_edit);
            this.value0_edit = new LabeledExprEditor("Time of Zero", new ExprEditor(this.window.getValueAtIdx0(), false));
            this.add(this.value0_edit);
        }

        public final Descriptor getData() throws MdsException {
            return new Window(this.startidx_edit.getData(), this.endidx_edit.getData(), this.value0_edit.getData());
        }

        public final void reset() {
            this.startidx_edit.reset();
            this.endidx_edit.reset();
            this.value0_edit.reset();
        }

        public final void setEditable(final boolean editable) {
            if(this.startidx_edit != null) this.startidx_edit.setEditable(editable);
            if(this.endidx_edit != null) this.endidx_edit.setEditable(editable);
            if(this.value0_edit != null) this.value0_edit.setEditable(editable);
        }
    }
    private static final long serialVersionUID = 4057231256009434757L;
    JComboBox                 combo;
    Descriptor                data;
    TreeDialog                dialog;
    boolean                   editable         = true;
    ExprEditor                expr_edit;
    int                       mode_idx, curr_mode_idx;
    WindowEdt                 window_edit;

    @SuppressWarnings("unchecked")
    public WindowEditor(final Descriptor data, final TreeDialog dialog){
        this.dialog = dialog;
        this.data = data;
        if(data == null) this.mode_idx = 0;
        else if(data instanceof Window) this.mode_idx = 1;
        else this.mode_idx = 2;
        this.curr_mode_idx = this.mode_idx;
        final String names[] = {"Undefined", "Window", "Expression"};
        this.combo = new JComboBox(names);
        this.combo.setEditable(false);
        this.combo.setSelectedIndex(this.mode_idx);
        this.combo.addActionListener(this);
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        jp.add(new JLabel("Window: "));
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
        switch(this.curr_mode_idx){
            case 1:
                this.remove(this.window_edit);
                break;
            case 2:
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
                if(this.mode_idx == 1) this.window_edit = new WindowEdt((Window)this.data);
                else this.window_edit = new WindowEdt(null);
                this.add(this.window_edit, BorderLayout.CENTER);
                break;
            case 2:
                if(this.mode_idx == 2) this.expr_edit = new ExprEditor(this.data, false, 8, 30);
                else this.expr_edit = new ExprEditor(null, false, 8, 30);
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
                return this.window_edit.getData();
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
        switch(this.curr_mode_idx){
            case 1:
                this.remove(this.window_edit);
                break;
            case 2:
                this.remove(this.expr_edit);
                break;
        }
        this.curr_mode_idx = this.mode_idx;
        this.addEditor();
        this.validate();
        this.repaint();
    }

    public final void setData(final Descriptor data) {
        this.data = data;
        if(data == null) this.mode_idx = 0;
        else if(data instanceof Window) this.mode_idx = 1;
        else this.mode_idx = 2;
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        this.combo.setEnabled(editable);
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
        if(this.window_edit != null) this.window_edit.setEditable(editable);
    }
}
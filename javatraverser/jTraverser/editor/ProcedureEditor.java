package jTraverser.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Procedure;

@SuppressWarnings("deprecation")
public class ProcedureEditor extends JPanel implements Editor{
    private static final long serialVersionUID = 7305707909433418426L;
    ArgEditor                 arg_edit;
    Procedure                 procedure;
    LabeledExprEditor         procedure_edit, language_edit, timeout_edit;

    public ProcedureEditor(){
        this(null);
    }

    public ProcedureEditor(final Procedure procedure){
        this.procedure = procedure;
        if(this.procedure == null){
            this.procedure = new Procedure(null, null, null, null);
        }
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        final GridLayout gl = new GridLayout(2, 1);
        gl.setVgap(0);
        jp.setLayout(gl);
        this.procedure_edit = new LabeledExprEditor("Procedure", new ExprEditor(this.procedure.getProcedure(), true));
        this.language_edit = new LabeledExprEditor("Language", new ExprEditor(this.procedure.getLanguage(), true));
        jp.add(this.procedure_edit);
        jp.add(this.language_edit);
        this.add(jp, BorderLayout.NORTH);
        this.arg_edit = new ArgEditor(this.procedure.getArguments());
        this.add(this.arg_edit, BorderLayout.CENTER);
        this.timeout_edit = new LabeledExprEditor("Timeout", new ExprEditor(this.procedure.getTimeOut(), false));
        this.add(this.timeout_edit, BorderLayout.SOUTH);
    }

    @Override
    public final Descriptor getData() throws MdsException {
        return new Procedure(this.timeout_edit.getData(), this.language_edit.getData(), this.procedure_edit.getData(), this.arg_edit.getData());
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        this.procedure_edit.reset();
        this.language_edit.reset();
        this.arg_edit.reset();
        this.timeout_edit.reset();
    }

    public final void setData(final Descriptor data) {
        this.procedure = (Procedure)data;
        if(this.procedure == null){
            this.procedure = new Procedure(null, null, null, null);
        }
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        if(this.procedure_edit != null) this.procedure_edit.setEditable(editable);
        if(this.language_edit != null) this.language_edit.setEditable(editable);
        if(this.timeout_edit != null) this.timeout_edit.setEditable(editable);
        if(this.arg_edit != null) this.arg_edit.setEditable(editable);
    }
}
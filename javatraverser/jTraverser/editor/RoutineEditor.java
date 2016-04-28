package jTraverser.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Routine;

@SuppressWarnings("serial")
public class RoutineEditor extends JPanel implements Editor{
    ArgEditor         arg_edit;
    LabeledExprEditor image_edit, routine_edit, timeout_edit;
    Routine           routine;

    public RoutineEditor(){
        this(null);
    }

    public RoutineEditor(final Routine data){
        this.routine = data;
        if(this.routine == null){
            this.routine = new Routine(null, null, null, null);
        }
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        final GridLayout gl = new GridLayout(2, 1);
        gl.setVgap(0);
        jp.setLayout(gl);
        this.image_edit = new LabeledExprEditor("Image", new ExprEditor(this.routine.getImage(), true));
        this.routine_edit = new LabeledExprEditor("Routine", new ExprEditor(this.routine.getRoutine(), true));
        jp.add(this.image_edit);
        jp.add(this.routine_edit);
        this.add(jp, BorderLayout.NORTH);
        this.arg_edit = new ArgEditor(this.routine.getArguments());
        this.add(this.arg_edit, BorderLayout.CENTER);
        this.timeout_edit = new LabeledExprEditor("Timeout", new ExprEditor(this.routine.getTimeOut(), false));
        this.add(this.timeout_edit, BorderLayout.SOUTH);
    }

    @Override
    public final Routine getData() throws MdsException {
        return new Routine(this.timeout_edit.getData(), this.image_edit.getData(), this.routine_edit.getData(), this.arg_edit.getData());
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        this.image_edit.reset();
        this.routine_edit.reset();
        this.arg_edit.reset();
        this.timeout_edit.reset();
    }

    public final void setData(final Descriptor data) {
        this.routine = (Routine)data;
        if(this.routine == null){
            this.routine = new Routine(null, null, null, null);
        }
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        if(this.image_edit != null) this.image_edit.setEditable(editable);
        if(this.routine_edit != null) this.routine_edit.setEditable(editable);
        if(this.timeout_edit != null) this.timeout_edit.setEditable(editable);
        if(this.arg_edit != null) this.arg_edit.setEditable(editable);
    }
}
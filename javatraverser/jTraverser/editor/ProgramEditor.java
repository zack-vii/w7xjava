package jTraverser.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Program;

@SuppressWarnings("deprecation")
public class ProgramEditor extends JPanel implements Editor{
    private static final long serialVersionUID = -7840137374103863094L;
    Program                   program;
    LabeledExprEditor         program_edit, timeout_edit;

    public ProgramEditor(){
        this(null);
    }

    public ProgramEditor(final Program program){
        this.program = program;
        if(this.program == null){
            this.program = new Program(null, null);
        }
        this.program_edit = new LabeledExprEditor("Program", new ExprEditor(this.program.getProgram(), true));
        this.timeout_edit = new LabeledExprEditor("Timeout", new ExprEditor(this.program.getTimeOut(), false));
        final JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(2, 1));
        jp.add(this.program_edit);
        jp.add(this.timeout_edit);
        this.setLayout(new BorderLayout());
        this.add(jp, BorderLayout.NORTH);
    }

    @Override
    public final Descriptor getData() throws MdsException {
        return new Program(this.timeout_edit.getData(), this.program_edit.getData());
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        this.program_edit.reset();
        this.timeout_edit.reset();
    }

    public final void setData(final Descriptor data) {
        this.program = (Program)data;
        if(this.program == null){
            this.program = new Program(null, null);
        }
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        if(this.program_edit != null) this.program_edit.setEditable(editable);
        if(this.timeout_edit != null) this.timeout_edit.setEditable(editable);
    }
}
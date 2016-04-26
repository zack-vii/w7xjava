package jTraverser.editor;

import java.awt.GridLayout;
import javax.swing.JPanel;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Range;

public class RangeEditor extends JPanel implements Editor{
    private static final long serialVersionUID = 8930270158951086998L;
    LabeledExprEditor         begin_edit, end_edit, delta_edit;
    Descriptor                range;

    public RangeEditor(){
        this(null);
    }

    public RangeEditor(final Range range){
        this.range = range;
        if(this.range == null){
            this.range = new Range(null, null, null);
        }
        final GridLayout gl = new GridLayout(3, 1);
        gl.setVgap(0);
        this.setLayout(gl);
        this.begin_edit = new LabeledExprEditor("Start", new ExprEditor(range.getBegin(), false));
        this.add(this.begin_edit);
        this.end_edit = new LabeledExprEditor("End", new ExprEditor(range.getEnding(), false));
        this.add(this.end_edit);
        this.delta_edit = new LabeledExprEditor("Increment", new ExprEditor(range.getDelta(), false));
        this.add(this.delta_edit);
    }

    @Override
    public final Range getData() throws MdsException {
        return new Range(this.begin_edit.getData(), this.end_edit.getData(), this.delta_edit.getData());
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        this.begin_edit.reset();
        this.end_edit.reset();
        this.delta_edit.reset();
    }

    public final void setData(Range range) {
        if(range == null) range = new Range(null, null, null);
        this.begin_edit.setData(range.getBegin());
        this.end_edit.setData(range.getEnding());
        this.delta_edit.setData(range.getDelta());
        this.range = range;
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        if(this.begin_edit != null) this.begin_edit.setEditable(editable);
        if(this.end_edit != null) this.end_edit.setEditable(editable);
        if(this.delta_edit != null) this.delta_edit.setEditable(editable);
    }
}
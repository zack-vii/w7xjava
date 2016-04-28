package jTraverser.editor;

// package jTraverser;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.CString;

@SuppressWarnings("serial")
public class LabeledExprEditor extends JPanel implements Editor{
    ExprEditor expr;

    public LabeledExprEditor(final Descriptor data){
        this("Expression", new ExprEditor(data, (data != null && data instanceof CString), 7, 20));
    }

    public LabeledExprEditor(final String label_str){
        this(label_str, new ExprEditor(null, false));
    }

    public LabeledExprEditor(final String label_str, final ExprEditor expr){
        this.expr = expr;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder(label_str));
        this.add(expr, BorderLayout.CENTER);
    }

    @Override
    public final Descriptor getData() throws MdsException {
        return this.expr.getData();
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        this.expr.reset();
    }

    public final void setData(final Descriptor data) {
        this.expr.setData(data);
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.expr.setEditable(editable);
    }
}
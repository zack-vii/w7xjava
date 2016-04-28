package jTraverser.editor;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Param;

@SuppressWarnings("serial")
public class ParameterEditor extends JPanel implements Editor{
    ExprEditor expr, help, validition;

    public ParameterEditor(){
        this(new ExprEditor(null, false), new ExprEditor(null, true), new ExprEditor(null, false));
    }

    public ParameterEditor(final ExprEditor expr, final ExprEditor help, final ExprEditor validition){
        this.expr = expr;
        this.help = help;
        this.validition = validition;
        this.setLayout(new BorderLayout());
        this.add(new LabeledExprEditor("Data", expr), BorderLayout.CENTER);
        final JPanel jp = new JPanel(new BorderLayout());
        jp.add(new LabeledExprEditor("Help", help), BorderLayout.CENTER);
        jp.add(new LabeledExprEditor("Validition", validition), BorderLayout.PAGE_END);
        this.add(jp, BorderLayout.PAGE_END);
    }

    @Override
    public final Descriptor getData() throws MdsException {
        return new Param(this.expr.getData(), this.help.getData(), this.validition.getData());
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        this.expr.reset();
        this.help.reset();
        this.validition.reset();
    }

    public final void setData(final Descriptor data) {
        if(data instanceof Param){
            this.expr.setData(((Param)data).getData());
            this.help.setData(((Param)data).getHelp());
            this.validition.setData(((Param)data).getValidation());
        }
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.expr.setEditable(editable);
        this.help.setEditable(editable);
        this.validition.setEditable(editable);
    }
}

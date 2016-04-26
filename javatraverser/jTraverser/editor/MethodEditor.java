package jTraverser.editor;

// package jTraverser;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Method;

public class MethodEditor extends JPanel implements Editor{
    private static final long serialVersionUID = -1402796582815519877L;
    ArgEditor                 arg_edit;
    LabeledExprEditor         device_edit, method_edit, timeout_edit;
    Method                    method;

    public MethodEditor(){
        this(null);
    }

    public MethodEditor(final Method method){
        this.method = (method == null) ? new Method(null, null, null, null) : method;
        this.setLayout(new BorderLayout());
        this.device_edit = new LabeledExprEditor("Device", new ExprEditor(this.method.getObject(), true));
        this.method_edit = new LabeledExprEditor("Method", new ExprEditor(this.method.getMethod(), true));
        final JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(2, 1));
        jp.add(this.device_edit);
        jp.add(this.method_edit);
        this.add(jp, BorderLayout.NORTH);
        this.arg_edit = new ArgEditor(this.method.getArguments());
        this.add(this.arg_edit, BorderLayout.CENTER);
        this.timeout_edit = new LabeledExprEditor("Timeout", new ExprEditor(this.method.getTimeOut(), false));
        this.add(this.timeout_edit, BorderLayout.SOUTH);
    }

    @Override
    public final Method getData() throws MdsException {
        return new Method(this.timeout_edit.getData(), this.method_edit.getData(), this.device_edit.getData(), this.arg_edit.getData());
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        this.device_edit.reset();
        this.method_edit.reset();
        this.arg_edit.reset();
        this.timeout_edit.reset();
    }

    public final void setData(final Descriptor data) {
        this.method = (Method)data;
        if(this.method == null) this.method = new Method(null, null, null, null);
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        if(this.device_edit != null) this.device_edit.setEditable(editable);
        if(this.method_edit != null) this.method_edit.setEditable(editable);
        if(this.timeout_edit != null) this.timeout_edit.setEditable(editable);
        if(this.arg_edit != null) this.arg_edit.setEditable(editable);
    }
}
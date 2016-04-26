package devicebeans;

import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import jTraverser.editor.ExprEditor;
import jTraverser.editor.LabeledExprEditor;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Action;
import mds.data.descriptor_r.Dispatch;

public class DeviceDispatchField extends DeviceComponent{
    private static final long   serialVersionUID = 3063498160136657048L;
    protected Action            action;
    protected Dispatch          dispatch;
    protected LabeledExprEditor ident, phase, when, completion;
    protected JCheckBox         state;

    @Override
    protected void displayData(final Descriptor data, final boolean is_on) {
        if(this.ident == null) return;
        if(!(data instanceof Action)){
            System.out.println("\nError: DeviceDispatchField used for non action data");
            return;
        }
        final Dispatch dispatch = (Dispatch)((Action)data).getDispatch();
        this.ident.setData(dispatch.getIdent());
        this.phase.setData(dispatch.getPhase());
        this.when.setData(dispatch.getWhen());
        this.completion.setData(dispatch.getCompletion());
        this.state.setSelected(is_on);
    }

    @Override
    protected Descriptor getData() {
        if(this.dispatch == null) return null;
        try{
            return new Action(new Dispatch(this.dispatch.getType(), this.ident.getData(), this.phase.getData(), this.when.getData(), this.completion.getData()), this.action.getTask(), this.action.getErrorLogs(), this.action.getCompletionMessage(), this.action.getPerformance());
        }catch(final MdsException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    protected boolean getState() {
        return this.state.isSelected();
    }

    @Override
    protected void initializeData(final Descriptor data, final boolean is_on) {
        if(!(data instanceof Action)){
            System.out.println("\nError: DeviceDispatchField used for non action data");
            return;
        }
        this.action = (Action)data;
        this.dispatch = (Dispatch)((Action)data).getDispatch();
        if(this.dispatch == null) return;
        this.setLayout(new GridLayout(4, 1));
        this.add(this.ident = new LabeledExprEditor("Ident:         ", new ExprEditor(this.dispatch.getIdent(), true)));
        this.add(this.phase = new LabeledExprEditor("Phase:        ", new ExprEditor(this.dispatch.getPhase(), true)));
        this.add(this.completion = new LabeledExprEditor("Completion:", new ExprEditor(this.dispatch.getCompletion(), true)));
        final JPanel jp = new JPanel();
        jp.add(this.when = new LabeledExprEditor("Sequence:  ", new ExprEditor(this.dispatch.getWhen(), false, 1, 6)));
        jp.add(this.state = new JCheckBox("Is On", is_on));
        this.add(jp);
    }

    /*Allow writing only if model */
    @Override
    protected boolean isDataChanged() {
        try{
            return(this.subtree.getShot() == -1);
        }catch(final Exception exc){
            return false;
        }
    }

    @Override
    public void setEnabled(final boolean state) {}
}

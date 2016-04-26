package jTraverser.dialogs;

import java.beans.PropertyEditor;
import java.util.Vector;
import jTraverser.Node;
import jTraverser.jTraverserFacade;
import jTraverser.editor.NodeEditor;

public final class DialogSet{
    private final Vector<TreeDialog> dialogs = new Vector<TreeDialog>();

    public TreeDialog getDialog(final Class ed_class, final Node node) {
        int idx;
        TreeDialog curr_dialog = null;
        NodeEditor curr_editor;
        for(idx = 0; idx < this.dialogs.size(); idx++){
            curr_dialog = this.dialogs.elementAt(idx);
            if(!curr_dialog.inUse()) break;
        }
        if(curr_dialog == null){
            try{
                curr_editor = (NodeEditor)((PropertyEditor)ed_class.newInstance()).getCustomEditor();
                curr_dialog = new TreeDialog(curr_editor);
                curr_editor.setFrame(curr_dialog);
                this.dialogs.addElement(curr_dialog);
            }catch(final Exception exc){
                jTraverserFacade.stderr("Error creating node editor", exc);
                return null;
            }
        }
        curr_dialog.setUsed(true);
        curr_dialog.getEditor().setNode(node);
        return curr_dialog;
    }
};
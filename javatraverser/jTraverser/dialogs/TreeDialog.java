package jTraverser.dialogs;

import javax.swing.JFrame;
import jTraverser.editor.NodeEditor;

@SuppressWarnings("serial")
public class TreeDialog extends JFrame{
    boolean    in_use;
    NodeEditor node_editor;

    public TreeDialog(final NodeEditor editor){
        this.in_use = true;
        this.getContentPane().add(editor);
        this.node_editor = editor;
        if(editor instanceof DisplayNci) this.setResizable(false);
    }

    @Override
    public void dispose() {
        this.in_use = false;
        this.setVisible(false);
    }

    public final NodeEditor getEditor() {
        return this.node_editor;
    }

    public final boolean inUse() {
        return this.in_use;
    }

    public void repack() {
        if(!this.isVisible()) return;
        this.pack();
    }

    public final void setUsed(final boolean used) {
        this.in_use = used;
    }
}

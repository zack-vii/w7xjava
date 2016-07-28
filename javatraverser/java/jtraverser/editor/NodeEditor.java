package jtraverser.editor;

// package jtraverser;
import javax.swing.JPanel;
import jtraverser.Node;
import jtraverser.TreeView;
import jtraverser.dialogs.TreeDialog;

@SuppressWarnings("serial")
public class NodeEditor extends JPanel{
    protected TreeDialog frame;
    protected Node       node;
    TreeView                 tree;

    public final void setFrame(final TreeDialog frame) {
        this.frame = frame;
    }

    public void setNode(final Node node) {
        this.node = node;
    }

    public final void setTree(final TreeView tree) {
        this.tree = tree;
    }
}
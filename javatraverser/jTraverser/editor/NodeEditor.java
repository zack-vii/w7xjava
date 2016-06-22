package jtraverser.editor;

// package jtraverser;
import javax.swing.JPanel;
import jtraverser.Node;
import jtraverser.Tree;
import jtraverser.dialogs.TreeDialog;

@SuppressWarnings("serial")
public class NodeEditor extends JPanel{
    protected TreeDialog frame;
    protected Node       node;
    Tree                 tree;

    public final void setFrame(final TreeDialog frame) {
        this.frame = frame;
    }

    public void setNode(final Node node) {
        this.node = node;
    }

    public final void setTree(final Tree tree) {
        this.tree = tree;
    }
}
package jTraverser.editor;

// package jTraverser;
import javax.swing.JPanel;
import jTraverser.Node;
import jTraverser.Tree;
import jTraverser.dialogs.TreeDialog;

public class NodeEditor extends JPanel{
    private static final long serialVersionUID = -621239038738815183L;
    protected TreeDialog      frame;
    protected Node            node;
    Tree                      tree;

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
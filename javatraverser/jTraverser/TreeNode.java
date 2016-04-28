package jTraverser;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TreeNode extends JLabel{
    static Font               plain_f, bold_f;
    private static final long serialVersionUID = -5991143423455919576L;

    static{
        TreeNode.plain_f = new Font("Serif", Font.PLAIN, 12);
        TreeNode.bold_f = new Font("Serif", Font.BOLD, 12);
    }
    final Color CExclude  = new Color(128, 128, 128);
    final Color CInclude  = new Color(0, 0, 0);
    final Color CMSetup   = new Color(0, 0, 128);
    final Color CMSetupO  = new Color(96, 0, 128);
    final Color CNorm     = new Color(0, 0, 0);
    final Color CNormO    = new Color(96, 0, 96);
    final Color CNoWrite  = new Color(128, 0, 0);
    final Color CNoWriteO = new Color(192, 0, 0);
    final Color CSSetup   = new Color(128, 0, 128);
    final Color CSSetupO  = new Color(128, 0, 64);
    final Color CWrite    = new Color(0, 128, 0);
    final Color CWriteO   = new Color(96, 64, 0);
    final Node  node;

    public TreeNode(final Node node, final String name, final Icon icon, final boolean isSelected){
        super((node.isDefault() ? "(" + node.getName() + ")" : node.getName()), icon, SwingConstants.LEFT);
        this.node = node;
        if(node.getUsage() == NodeInfo.USAGE_SUBTREE) this.setForeground(node.isIncludeInPulse() ? this.CInclude : this.CExclude);
        else{
            if(node.isNoWriteModel() & node.isNoWriteModel()) this.setForeground(this.CNoWrite);
            else if(node.isNoWriteModel()) this.setForeground(node.tree.isModel() ? (node.isWriteOnce() ? this.CNoWriteO : this.CNoWrite) : (node.isWriteOnce() ? this.CWriteO : this.CWrite));
            else if(node.isNoWriteShot()) this.setForeground(!node.tree.isModel() ? (node.isWriteOnce() ? this.CNoWriteO : this.CNoWrite) : (node.isWriteOnce() ? this.CWriteO : this.CWrite));
            else if(node.isSetup()) this.setForeground(node.tree.isModel() ? (node.isWriteOnce() ? this.CMSetupO : this.CMSetup) : (node.isWriteOnce() ? this.CSSetupO : this.CSSetup));
            else this.setForeground(node.isWriteOnce() ? this.CNormO : this.CNorm);
        }
        this.setFont(node.isOn() ? TreeNode.bold_f : TreeNode.plain_f);
        this.setBorder(BorderFactory.createLineBorder(isSelected ? Color.black : Color.white, 1));
    }

    @Override
    public final String getToolTipText() {
        return this.node.getToolTipText();
    }
}

package jtraverser;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import mds.data.descriptor_s.TREENODE;
import mds.data.descriptor_s.TREENODE.Flags;

@SuppressWarnings("serial")
public class TreeNodeLabel extends JLabel{
    static Font plain_f, bold_f;
    static{
        TreeNodeLabel.plain_f = new Font("Serif", Font.PLAIN, 12);
        TreeNodeLabel.bold_f = new Font("Serif", Font.BOLD, 12);
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

    public TreeNodeLabel(final Node node, final String name, final Icon icon, final boolean isSelected){
        super((node.isDefault() ? new StringBuilder(node.getName().length() + 2).append('(').append(node).append(')').toString() : node.toString()), icon, SwingConstants.LEFT);
        this.node = node;
        final Flags flags = node.getFlags();
        if(node.getUsage() == TREENODE.USAGE_SUBTREE) this.setForeground(flags.isIncludeInPulse() ? this.CInclude : this.CExclude);
        else{
            if(flags.isNoWriteModel() & flags.isNoWriteModel()) this.setForeground(this.CNoWrite);
            else if(flags.isNoWriteModel()) this.setForeground(node.tree.isModel() ? (flags.isWriteOnce() ? this.CNoWriteO : this.CNoWrite) : (flags.isWriteOnce() ? this.CWriteO : this.CWrite));
            else if(flags.isNoWriteShot()) this.setForeground(!node.tree.isModel() ? (flags.isWriteOnce() ? this.CNoWriteO : this.CNoWrite) : (flags.isWriteOnce() ? this.CWriteO : this.CWrite));
            else if(flags.isSetup()) this.setForeground(node.tree.isModel() ? (flags.isWriteOnce() ? this.CMSetupO : this.CMSetup) : (flags.isWriteOnce() ? this.CSSetupO : this.CSSetup));
            else this.setForeground(flags.isWriteOnce() ? this.CNormO : this.CNorm);
        }
        this.setFont(node.isOn() ? TreeNodeLabel.bold_f : TreeNodeLabel.plain_f);
        this.setBorder(BorderFactory.createLineBorder(isSelected ? Color.black : Color.white, 1));
    }

    @Override
    public final String getToolTipText() {
        return this.node.getToolTipText();
    }
}

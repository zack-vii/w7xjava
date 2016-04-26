package jTraverser;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import mds.MdsException;
import mds.data.descriptor.Descriptor;

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
    public String getToolTipText() {
        /*if(false){
            final String tags[] = this.node.getTags();
            if(tags == null || tags.length == 0) return null;
            final String text;
            if(tags.length > 32) text = String.join("<br>", Arrays.copyOfRange(tags, 0, 32)) + "<br>...";
            else text = String.join("<br>", tags);
            return new StringBuilder("<html>").append(text).append("</html>").toString();
        }else*/{
            String text, info;
            try{
                info = this.node.getInfo().toString();
                if(this.node.getUsage() == NodeInfo.USAGE_STRUCTURE || this.node.getUsage() == NodeInfo.USAGE_SUBTREE) text = null;
                else{
                    final Descriptor data = this.node.getData();
                    if(data == null) text = null;
                    else text = data.toStringX().replace("<", "&lt;").replace(">", "&gt;").replace("\t", "&nbsp&nbsp&nbsp&nbsp ").replace("\n", "<br>");
                }
            }catch(final MdsException e){
                return e.toString();
            }
            if(text == null) return info;
            final StringBuilder sb = new StringBuilder().append(info.substring(0, info.length() - 7)).append("<hr><table");
            if(text.length() > 80) sb.append(" width=\"320\"");
            return sb.append(">").append(text).append("</table></html>").toString();
        }
    }
}

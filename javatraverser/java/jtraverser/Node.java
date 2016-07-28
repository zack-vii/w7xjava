package jtraverser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import devicebeans.DeviceSetup;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Action;
import mds.data.descriptor_r.Conglom;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.TREENODE;
import mds.data.descriptor_s.TREENODE.Flags;

public class Node{
    @SuppressWarnings("serial")
    public static class TreeNodeLabel extends JLabel{
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
                else if(flags.isNoWriteModel()) this.setForeground(node.treeview.isModel() ? (flags.isWriteOnce() ? this.CNoWriteO : this.CNoWrite) : (flags.isWriteOnce() ? this.CWriteO : this.CWrite));
                else if(flags.isNoWriteShot()) this.setForeground(!node.treeview.isModel() ? (flags.isWriteOnce() ? this.CNoWriteO : this.CNoWrite) : (flags.isWriteOnce() ? this.CWriteO : this.CWrite));
                else if(flags.isSetup()) this.setForeground(node.treeview.isModel() ? (flags.isWriteOnce() ? this.CMSetupO : this.CMSetup) : (flags.isWriteOnce() ? this.CSSetupO : this.CSSetup));
                else this.setForeground(flags.isWriteOnce() ? this.CNormO : this.CNorm);
            }
            this.setFont(flags.isOn() && flags.isParentOn() ? TreeNodeLabel.bold_f : TreeNodeLabel.plain_f);
            this.setBorder(BorderFactory.createLineBorder(isSelected ? Color.black : Color.white, 1));
        }

        @Override
        public final String getToolTipText() {
            return this.node.getToolTipText();
        }
    }
    private static Node              copied;
    private static boolean           cut;
    private static final ImageIcon[] ICONS = new ImageIcon[]{   //
            Node.loadIcon("jtraverser/any.gif"),                //
            Node.loadIcon("jtraverser/structure.gif"),          //
            Node.loadIcon("jtraverser/action.gif"),             //
            Node.loadIcon("jtraverser/device.gif"),             //
            Node.loadIcon("jtraverser/dispatch.gif"),           //
            Node.loadIcon("jtraverser/numeric.gif"),            //
            Node.loadIcon("jtraverser/signal.gif"),             //
            Node.loadIcon("jtraverser/task.gif"),               //
            Node.loadIcon("jtraverser/text.gif"),               //
            Node.loadIcon("jtraverser/window.gif"),             //
            Node.loadIcon("jtraverser/axis.gif"),               //
            Node.loadIcon("jtraverser/subtree.gif"),            //
            Node.loadIcon("jtraverser/compound.gif")            //
    };

    public static void copySubtreeContent(final Node fromNode, final Node toNode) {
        try{
            fromNode.expand();
            toNode.expand();
        }catch(final Exception exc){
            MdsException.stderr("Error expanding nodes", exc);
        }
        try{
            final Descriptor data = fromNode.getData();
            if(data != null){
                if(!(data instanceof Action)) toNode.setData(data);
            }
        }catch(final Throwable exc){}
        for(int i = 0; i < fromNode.sons.length; i++)
            Node.copySubtreeContent(fromNode.sons[i], toNode.sons[i]);
        for(int i = 0; i < fromNode.members.length; i++)
            Node.copySubtreeContent(fromNode.members[i], toNode.members[i]);
    }

    public final static Node getNode(final DefaultMutableTreeNode treenode) {
        return (Node)treenode.getUserObject();
    }

    public final static Node getNode(final javax.swing.tree.TreeNode treenode) {
        return Node.getNode((DefaultMutableTreeNode)treenode);
    }

    public final static String getUniqueName(String name, final String[] usedNames) {
        int i;
        for(i = 0; i < usedNames.length && !name.equals(usedNames[i]); i++);
        if(i == usedNames.length) return name;
        for(i = name.length() - 1; i > 0 && (name.charAt(i) >= '0' && name.charAt(i) <= '9'); i--);
        name = name.substring(0, i + 1);
        String prevName;
        if(name.length() < 10) prevName = name;
        else prevName = name.substring(0, 9);
        for(i = 1; i < 1000; i++){
            final String newName = prevName + i;
            int j;
            for(j = 0; j < usedNames.length && !newName.equals(usedNames[j]); j++);
            if(j == usedNames.length) return newName;
        }
        return "XXXXXXX"; // Dummy name, hopefully will never reach this
    }

    public final static boolean isCopied() {
        return Node.copied != null;
    }

    private static final ImageIcon loadIcon(final String gifname) {
        final String base = System.getProperty("icon_base");
        if(base == null) return new ImageIcon(Node.class.getClassLoader().getResource(gifname));
        return new ImageIcon(base + "/" + gifname);
    }

    public static void updateCell() {}
    // private final boolean is_member;
    private boolean                is_on;
    private TreeNodeLabel          label;
    private Node[]                 members;
    private boolean                needsOnCheck = true;
    public final Nid               nid;
    private Node                   parent;
    private Node[]                 sons;
    private int                    ToolTipDefault;
    private long                   ToolTipLife  = 0;
    private String                 ToolTipText  = null;
    public final TreeView          treeview;
    private DefaultMutableTreeNode treenode;
    private int                    length, ownerid;
    private String                 name         = null, minpath = null, path = null, fullpath = null, timeinserted = null;
    private byte                   usage        = -1, dtype = -1, dclass = -1;
    private Flags                  flags        = null;

    public Node(final TreeView treeview, final Nid nid) throws MdsException{
        this.treeview = treeview;
        this.nid = nid;
        this.parent = null;
        // this.is_member = false;
        this.sons = new Node[0];
        this.members = new Node[0];
    }

    public Node(final TreeView treeview, final Nid nid, final Node parent, final boolean is_member){
        this.treeview = treeview;
        this.parent = parent;
        // this.is_member = is_member;
        this.nid = nid;
        this.sons = new Node[0];
        this.members = new Node[0];
    }

    public final Node addChild(final String name) throws MdsException {
        return this.addNode(name, TREENODE.USAGE_STRUCTURE);
    }

    public final Node addDevice(final String name, final String type) throws MdsException {
        Nid new_nid;
        final Nid prev_default = this.nid.getTree().getDefault();
        this.nid.setDefault();
        try{
            new_nid = this.nid.getTree().addDevice(this.path, type);
        }finally{
            prev_default.setDefault();
        }
        return this.addNode(new_nid, TREENODE.USAGE_DEVICE);
    }

    private final Node addNode(final Nid new_nid, final int usage) {
        final boolean ismember = usage != TREENODE.USAGE_STRUCTURE || usage != TREENODE.USAGE_SUBTREE;
        final Node newNode = new Node(this.treeview, new_nid, this, ismember);
        this.expand();
        if(ismember){
            final Node[] newNodes = new Node[this.members.length + 1];
            System.arraycopy(this.members, 0, newNodes, 0, this.members.length);
            newNodes[this.members.length] = newNode;
            this.members = newNodes;
        }else{
            final Node[] newNodes = new Node[this.sons.length + 1];
            System.arraycopy(this.sons, 0, newNodes, 0, this.sons.length);
            newNodes[this.sons.length] = newNode;
            this.sons = newNodes;
        }
        newNode.setTreeNode(new DefaultMutableTreeNode(newNode));
        this.treeview.addNodeToParent(newNode.getTreeNode(), this.getTreeNode());
        this.treeview.setCurrentNode(newNode);
        return newNode;
    }

    public final Node addNode(final String name, final byte usage) throws MdsException {
        Nid new_nid;
        final Nid prev_default = this.nid.getTree().getDefault();
        this.nid.setDefault();
        try{
            new_nid = this.nid.getTree().addNode(name, usage);
        }finally{
            prev_default.setDefault();
        }
        return this.addNode(new_nid, usage);
    }

    private final boolean changePath(final Node newParent, final String newName) {
        if((newParent == this.parent) && (newName == this.getName())) return false; // nothing to do
        if(newName.length() > 12 || newName.length() == 0){
            JOptionPane.showMessageDialog(this.treeview, "Node name lengh must be between 1 and 12 characters", "Error renaming node: " + newName.length(), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try{
            final Nid prev_default = this.nid.getTree().getDefault();
            this.nid.setDefault();
            try{
                this.nid.setPath(newName);
            }finally{
                prev_default.setDefault();
            }
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this.treeview, "Error changing node path: " + exc, "Error changing node path", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(newParent != this.parent){
            this.parent = newParent;
            final DefaultTreeModel tree_model = (DefaultTreeModel)this.treeview.getModel();
            tree_model.removeNodeFromParent(this.getTreeNode());
            this.treeview.addNodeToParent(this.getTreeNode(), this.parent.getTreeNode());
        }
        return true;
    }

    public final void clearFlag(final byte idx) throws MdsException {
        this.clearFlags(1 << idx);
    }

    public final void clearFlags(final int flags) throws MdsException {
        this.nid.clearFlags(flags);
    }

    public final void copy() {
        Node.cut = false;
        Node.copied = this;
        MdsException.stdout("copy: " + Node.copied + " from " + Node.copied.parent);
    }

    public final void copyToClipboard() {
        try{
            final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection content;
            final String path = this.getFullPath();
            content = new StringSelection(path);
            cb.setContents(content, null);
        }catch(final Exception e){
            MdsException.stderr("Cannot copy fullPath to Clipboard", e);
        }
    }

    public final void cut() {
        Node.cut = true;
        Node.copied = this;
        MdsException.stdout("cut: " + Node.copied + " from " + Node.copied.parent);
    }

    public final void delete() {
        if(this.treeview.isEditable()) this.treeview.deleteNode(this);
        else MdsException.stdout("Cannot delete " + this + ". Tree not in edit mode.");
    }

    public final boolean deleteExecute() {
        try{
            this.nid.getTree().deleteExecute();
            return true;
        }catch(final Exception exc){
            MdsException.stderr("Error executing delete", exc);
            return false;
        }
    }

    public final int deleteStart() {
        try{
            return this.nid.getTree().deleteStart(this.nid);
        }catch(final Exception exc){
            MdsException.stderr("Error starting delete", exc);
        }
        return -1;
    }

    public final void doAction() throws MdsException {
        try{
            this.nid.doAction();
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this.treeview, e.getMessage(), "Error executing message", JOptionPane.WARNING_MESSAGE);
        }
    }

    public final void expand() {
        try{
            int i;
            Nid sons_nid[] = this.nid.getNciChildrenNids().toArray();
            if(sons_nid == null) sons_nid = new Nid[0];
            Nid members_nid[] = this.nid.getNciMemberNids().toArray();
            if(members_nid == null) members_nid = new Nid[0];
            this.sons = new Node[sons_nid.length];
            this.members = new Node[members_nid.length];
            for(i = 0; i < sons_nid.length; i++)
                this.sons[i] = new Node(this.treeview, sons_nid[i], this, false);
            for(i = 0; i < members_nid.length; i++)
                this.members[i] = new Node(this.treeview, members_nid[i], this, true);
        }catch(final MdsException e){
            MdsException.stderr("expand", e);
            this.members = new Node[0];
            this.sons = new Node[0];
        }
    }

    public final Descriptor getData() throws MdsException {
        if(this.nid.isSegmented()) return this.nid.getSegment(0);
        return this.nid.getRecord();
    }

    public final String getDate() {
        try{
            return this.timeinserted = this.nid.getNciTimeInsertedStr();
        }catch(final MdsException e){
            MdsException.stderr("Error updating timeinserted", e);
            return this.timeinserted;
        }
    }

    public final byte getDClass() {
        try{
            return this.dclass = this.nid.getNciClass();
        }catch(final MdsException e){
            MdsException.stderr("Error updating class", e);
            return this.dclass;
        }
    }

    public final byte getDType() {
        try{
            return this.dtype = this.nid.getNciDType();
        }catch(final MdsException e){
            MdsException.stderr("Error updating dtype", e);
            return this.dtype;
        }
    }

    public final Flags getFlags() {
        try{
            return this.flags = new Flags(this.nid.getNciFlags());
        }catch(final Exception e){
            MdsException.stderr("Error updating flags", e);
            return this.flags;
        }
    }

    public final String getFullPath() {
        try{
            return this.fullpath = this.nid.getNciFullPath();
        }catch(final MdsException e){
            MdsException.stderr("Error updating fullpath", e);
            return this.fullpath;
        }
    }

    public final Component getIcon(final boolean isSelected) {
        // if(!this.treeview.isUpdating() && this.label != null) return this.label;
        final int usage = this.getUsage();
        final Icon icon = usage <= Node.ICONS.length ? Node.ICONS[usage] : null;
        this.label = new TreeNodeLabel(this, this.getName(), icon, isSelected);
        return this.label;
    }

    public final String getInfoTextBox() {
        final StringBuffer sb = new StringBuffer("<html><table width=\"240\"> <tr><td width=\"60\" align=\"left\"/><nobr>full path:</nobr></td><td align=\"left\">");
        sb.append(this.getFullPath());
        sb.append(" (").append(this.nid.getValue()).append(")");
        sb.append("</td></tr><tr><td align=\"left\" valign=\"top\">Status:</td><td align=\"left\"><nobr>");
        final String sep = "</nobr>, <nobr>";
        Flags flags;
        flags = this.getFlags();
        sb.append(flags.isOn() ? "on" : "off");
        sb.append(sep).append("parent is ").append(flags.isParentOn() ? "on" : "off");
        if(flags.isSetup()) sb.append(sep).append("setup");
        if(flags.isEssential()) sb.append(sep).append("essential");
        if(flags.isCached()) sb.append(sep).append("cached");
        if(flags.isVersion()) sb.append(sep).append("version");
        if(flags.isSegmented()) sb.append(sep).append("segmented");
        if(flags.isWriteOnce()) sb.append(sep).append("write once");
        if(flags.isCompressible()) sb.append(sep).append("compressible");
        if(flags.isDoNotCompress()) sb.append(sep).append("do not compress");
        if(flags.isCompressOnPut()) sb.append(sep).append("compress on put");
        if(flags.isNoWriteModel()) sb.append(sep).append("no write model");
        if(flags.isNoWriteShot()) sb.append(sep).append("no write shot");
        if(flags.isPathReference()) sb.append(sep).append("path reference");
        if(flags.isNidReference()) sb.append(sep).append("nid reference");
        if(flags.isCompressSegments()) sb.append(sep).append("compress segments");
        if(flags.isIncludeInPulse()) sb.append(sep).append("include in pulse");
        sb.append("</nobr></td></tr><tr><td align=\"left\">Data:</td><td align=\"left\">");
        if(this.getLength() == 0) sb.append("<nobr>There is no data stored for this this</nobr>");
        else{
            final String dtype = DTYPE.getName(this.getDType());
            final String dclass = Descriptor.getDClassName(this.getDClass());
            final String tab = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            sb.append("<nobr>").append(dtype).append(tab).append(dclass).append(tab).append(this.getLength()).append(" Bytes</nobr>");
            sb.append("</td></tr><tr><td align=\"left\">Inserted:</td><td align=\"left\">");
            sb.append(this.getDate());
        }
        return sb.append("</td></tr></table></html>").toString();
    }

    public final int getLength() {
        try{
            return this.length = this.nid.getNciLength();
        }catch(final MdsException e){
            return this.length;
        }
    }

    public final Node[] getMembers() {
        return this.members;
    }

    public final String getMinPath() {
        try{
            return this.minpath = this.nid.getNciMinPath();
        }catch(final MdsException e){
            return this.minpath;
        }
    }

    public final String getName() {
        try{
            return this.name = this.nid.getNciNodeName();
        }catch(final MdsException e){
            return this.name;
        }
    }

    public final int getOwner() {
        try{
            return this.ownerid = this.nid.getNciOwnerId();
        }catch(final MdsException e){
            return this.ownerid;
        }
    }

    public final String getPath() {
        try{
            return this.path = this.nid.getNciPath();
        }catch(final MdsException e){
            return this.path;
        }
    }

    public final Descriptor getSignal() throws MdsException {
        final TREENODE res = this.nid.followReference();
        if(res.isSegmented()) return res.getSegment(0);
        return res.getRecord();
    }

    public final Node[] getSons() {
        return this.sons;
    }

    public final String[] getTags() {
        try{
            return this.nid.getTags();
        }catch(final MdsException e){
            MdsException.stderr("getTags", e);
        }
        return new String[0];
    }

    public final String getToolTipText() {
        final long now = System.nanoTime();
        int defaultnid;
        defaultnid = this.treeview.getDefault().getValue();
        if(this.ToolTipText == null || now > this.ToolTipLife || this.ToolTipDefault != defaultnid){
            String text = null;
            final String info = this.getInfoTextBox();
            if(this.getUsage() == TREENODE.USAGE_STRUCTURE || this.getUsage() == TREENODE.USAGE_SUBTREE) return info;
            try{
                final Descriptor data = this.getData();
                if(data == null) return info;
                text = data.toStringX().replace("<", "&lt;").replace(">", "&gt;").replace("\t", "&nbsp&nbsp&nbsp&nbsp ").replace("\n", "<br>");
            }catch(final MdsException e){
                text = e.getMessage();
            }
            final StringBuilder sb = new StringBuilder().append(info.substring(0, info.length() - 7)).append("<hr><table");
            if(text.length() > 80) sb.append(" width=\"320\"");
            this.ToolTipText = sb.append(">").append(text).append("</table></html>").toString();
            this.ToolTipLife = now + 10000000000l;
            this.ToolTipDefault = defaultnid;
        }
        return this.ToolTipText;
    }

    public final DefaultMutableTreeNode getTreeNode() {
        return this.treenode;
    }

    public final byte getUsage() {
        try{
            return this.usage = this.nid.getNciUsage();
        }catch(final MdsException e){
            return this.usage;
        }
    }

    public final boolean isDefault() {
        return this.treeview.getDefault() == this.nid;
    }

    public final boolean isOn() {
        if(this.needsOnCheck){
            this.needsOnCheck = false;
            try{
                return this.is_on = !this.nid.getNciStatus();
            }catch(final Exception exc){
                MdsException.stderr("Error checking state", exc);
            }
        }
        return this.is_on;
    }

    public final boolean isSegmented() {
        if(this.flags.isSegmented()) return true;
        try{
            return this.nid.getNumSegments() > 0;
        }catch(final MdsException e){
            e.printStackTrace();
            return false;
        }
    }

    public final boolean isSubTree() {
        return this.getUsage() == TREENODE.USAGE_SUBTREE;
    }

    final boolean move(final Node newParent) {
        return this.changePath(newParent, this.getName());
    }

    public final void paste() {
        if(!this.treeview.equals(Node.copied.treeview)) MdsException.stderr("Copying between different tree is not yet supported.", null);
        else if(!this.treeview.isEditable()) MdsException.stderr("Cannot paste " + Node.copied + ". Tree not in edit mode.", null);
        else{
            MdsException.stdout((Node.cut ? "moved: " : "copied: ") + Node.copied + " from " + Node.copied.parent + " to " + this);
            if(Node.copied != null && Node.copied != this){
                if(Node.cut){
                    if(Node.copied.move(this)) Node.copied = null;
                }else this.treeview.pasteSubtree(Node.copied, this, true);
            }
        }
    }

    public final boolean rename(final String newName) {
        return this.changePath(this.parent, newName);
    }

    public final Nid resolveRefSimple() throws MdsException {
        return this.nid.followReference().toNid();
    }

    public final void setAllOnUnchecked() {
        Node currNode = this;
        while(currNode.parent != null)
            currNode = currNode.parent;
        currNode.setOnUnchecked();
    }

    public final void setData(final Descriptor data) throws MdsException {
        this.nid.putRecord(data);
    }

    public final void setDefault() throws MdsException {
        this.nid.setDefault();
        this.treeview.updateDefault();
    }

    public final void setFlag(final byte idx) throws MdsException {
        this.setFlags(1 << idx);
    }

    public final void setFlags(final int flags) throws MdsException {
        this.nid.setFlags(flags);
    }

    final void setOnUnchecked() {
        this.needsOnCheck = true;
        for(final Node son : this.sons)
            son.setOnUnchecked();
        for(final Node member : this.members)
            member.setOnUnchecked();
    }

    public void setSubtree() throws MdsException {
        this.nid.setSubtree();
        this.label = null;
    }

    public final void setTags(final String[] tags) throws MdsException {
        this.nid.setTags(tags);
    }

    public final DefaultMutableTreeNode setTreeNode(final DefaultMutableTreeNode treenode) {
        return this.treenode = treenode;
    }

    public final void setupDevice() {
        Conglom conglom = null;
        try{
            conglom = (Conglom)this.nid.getRecord();
        }catch(final MdsException e){
            JOptionPane.showMessageDialog(this.treeview.treeman, e.getMessage(), "Error in device setup 1", JOptionPane.WARNING_MESSAGE);
        }
        if(conglom != null){
            final CString model = (CString)conglom.getModel();
            if(model != null){
                try{
                    DeviceSetup.setup(this, model.toString());
                    return;
                }catch(final Exception e){
                    try{
                        this.nid.doDeviceMethod("dw_setup");
                        return;
                    }catch(final MdsException exc){
                        JOptionPane.showMessageDialog(this.treeview.treeman, e.getMessage(), "Error in device setup 2: " + e, JOptionPane.WARNING_MESSAGE);
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
        JOptionPane.showMessageDialog(this.treeview, "Missing model in descriptor", "Error in device setup 3", JOptionPane.WARNING_MESSAGE);
    }

    public final void toggle() throws MdsException {
        this.nid.setOn(!this.nid.isOn());
        this.setOnUnchecked();
    }

    public final void toggleFlags(final int flags) throws MdsException {
        final int isflags = this.nid.getNciFlags();
        final int clear = isflags & flags, set = (~isflags) & flags;
        if(set != 0) this.nid.setFlags(set);
        if(clear != 0) this.nid.clearFlags(clear);
    }

    @Override
    public final String toString() {
        return this.getName();
    }

    public final void turnOff() {
        try{
            this.nid.setOn(false);
        }catch(final Exception exc){
            MdsException.stderr("Error turning off", exc);
        }
        this.setOnUnchecked();
    }

    public final void turnOn() {
        try{
            this.nid.setOn(true);
        }catch(final Exception exc){
            MdsException.stderr("Error turning on", exc);
        }
        this.setOnUnchecked();
    }
}

package jtraverser;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import devicebeans.DeviceSetup;
import mds.Database;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Action;
import mds.data.descriptor_r.Conglom;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Nid;

public class Node{
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
            jTraverserFacade.stderr("Error expanding nodes", exc);
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
    private Descriptor             data;
    private final Database         database;
    private NodeInfo               info;
    private final boolean          is_member;
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
    public final Tree              tree;
    private DefaultMutableTreeNode treenode;

    public Node(final Database database, final Tree tree, final Node parent, final boolean is_member, final Nid nid){
        this.database = database;
        this.tree = tree;
        this.parent = parent;
        this.is_member = is_member;
        this.nid = nid;
        try{
            this.info = database.getInfo(this.nid);
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error getting info", exc);
        }
        this.sons = new Node[0];
        this.members = new Node[0];
    }

    public Node(final Tree tree) throws MdsException{
        this.database = tree.getDatabase();
        this.tree = tree;
        if(this.database.isRealtime()) this.nid = new Nid(1);
        else this.nid = new Nid(0);
        this.info = this.database.getInfo(this.nid);
        this.parent = null;
        this.is_member = false;
        this.sons = new Node[0];
        this.members = new Node[0];
    }

    public final Node addChild(final String name) throws MdsException {
        return this.addNode(name, NodeInfo.USAGE_STRUCTURE);
    }

    public final Node addDevice(final String name, final String type) throws MdsException {
        Nid new_nid;
        final Nid prev_default = this.database.getDefault();
        this.database.setDefault(this.nid);
        try{
            if(this.info == null) this.info = this.database.getInfo(this.nid);
            new_nid = this.database.addDevice(name, type);
        }finally{
            this.database.setDefault(prev_default);
        }
        return this.addNode(new_nid, NodeInfo.USAGE_DEVICE);
    }

    private final Node addNode(final Nid new_nid, final int usage) {
        final boolean ismember = usage != NodeInfo.USAGE_STRUCTURE || usage != NodeInfo.USAGE_SUBTREE;
        final Node newNode = new Node(this.database, this.tree, this, ismember, new_nid);
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
        this.tree.addNodeToParent(newNode.getTreeNode(), this.getTreeNode());
        this.tree.setCurrentNode(newNode);
        return newNode;
    }

    public final Node addNode(final String name, final byte usage) throws MdsException {
        Nid new_nid;
        final Nid prev_default = this.database.getDefault();
        this.database.setDefault(this.nid);
        try{
            new_nid = this.database.addNode(name, usage);
        }finally{
            this.database.setDefault(prev_default);
        }
        return this.addNode(new_nid, usage);
    }

    private final boolean changePath(final Node newParent, final String newName) {
        if((newParent == this.parent) && (newName == this.getName())) return false; // nothing to do
        if(newName.length() > 12 || newName.length() == 0){
            JOptionPane.showMessageDialog(this.tree, "Node name lengh must be between 1 and 12 characters", "Error renaming node: " + newName.length(), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try{
            final String sep = this.is_member ? ":" : ".";
            this.database.renameNode(this.nid, newParent.getFullPath() + sep + newName);
            this.info = this.database.getInfo(this.nid);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this.tree, "Error changing node path: " + exc, "Error changing node path", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(newParent != this.parent){
            this.parent = newParent;
            final DefaultTreeModel tree_model = (DefaultTreeModel)this.tree.getModel();
            tree_model.removeNodeFromParent(this.getTreeNode());
            this.tree.addNodeToParent(this.getTreeNode(), this.parent.getTreeNode());
        }
        return true;
    }

    public final void clearFlag(final byte idx) throws MdsException {
        this.clearFlags(1 << idx);
    }

    public final void clearFlags(final int flags) throws MdsException {
        this.database.clearFlags(this.nid, flags);
        this.info.setFlags(this.database.getFlags(this.nid));
    }

    public final void copy() {
        Node.cut = false;
        Node.copied = this;
        jTraverserFacade.stdout("copy: " + Node.copied + " from " + Node.copied.parent);
    }

    public final void copyToClipboard() {
        try{
            final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection content;
            final String path = this.getFullPath();
            content = new StringSelection(path);
            cb.setContents(content, null);
        }catch(final Exception e){
            jTraverserFacade.stderr("Cannot copy fullPath to Clipboard", e);
        }
    }

    public final void cut() {
        Node.cut = true;
        Node.copied = this;
        jTraverserFacade.stdout("cut: " + Node.copied + " from " + Node.copied.parent);
    }

    public final void delete() {
        if(this.tree.isEditable()) this.tree.deleteNode(this);
        else jTraverserFacade.stdout("Cannot delete " + this + ". Tree not in edit mode.");
    }

    public final boolean deleteExecute() {
        try{
            this.database.deleteExecute();
            return true;
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error executing delete", exc);
            return false;
        }
    }

    public final int deleteStart() {
        try{
            return this.database.deleteStart(this.nid);
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error starting delete", exc);
        }
        return -1;
    }

    public final void doAction() throws MdsException {
        try{
            this.database.doAction(this.nid);
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this.tree, e.getMessage(), "Error executing message", JOptionPane.WARNING_MESSAGE);
        }
    }

    public final void expand() {
        try{
            int i;
            Nid sons_nid[] = this.database.getSons(this.nid);
            if(sons_nid == null) sons_nid = new Nid[0];
            Nid members_nid[] = this.database.getMembers(this.nid);
            if(members_nid == null) members_nid = new Nid[0];
            this.sons = new Node[sons_nid.length];
            this.members = new Node[members_nid.length];
            for(i = 0; i < sons_nid.length; i++)
                this.sons[i] = new Node(this.database, this.tree, this, false, sons_nid[i]);
            for(i = 0; i < members_nid.length; i++)
                this.members[i] = new Node(this.database, this.tree, this, true, members_nid[i]);
        }catch(final MdsException e){
            jTraverserFacade.stderr("expand", e);
            this.members = new Node[0];
            this.sons = new Node[0];
        }
    }

    public final int getConglomerateElt() {
        return this.info.getConglomerateElt();
    }

    public final int getConglomerateNids() {
        return this.info.getConglomerateNids();
    }

    public final Descriptor getData() throws MdsException {
        if(this.isSegmented()) this.data = this.database.getSegment(this.nid, 0);
        else this.data = this.database.getData(this.nid);
        return this.data;
    }

    public final String getDate() {
        return this.info.getDate();
    }

    public final byte getDClass() {
        return this.info.getDClass();
    }

    // info interface
    public final byte getDType() {
        return this.info.getDType();
    }

    public final int getFlags() {
        try{
            this.info.setFlags(this.database.getFlags(this.nid));
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error updating flags", exc);
        }
        return this.info.getFlags();
    }

    public final String getFullPath() {
        return this.info.getFullPath();
    }

    public final Component getIcon(final boolean isSelected) {
        if(this.info == null) return null;
        final int usage = this.getUsage();
        final Icon icon = usage <= Node.ICONS.length ? Node.ICONS[usage] : null;
        this.label = new TreeNodeLabel(this, this.getName(), icon, isSelected);
        return this.label;
    }

    public final NodeInfo getInfo() throws MdsException {
        try{
            this.info = this.database.getInfo(this.nid);
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error checking info", exc);
        }
        return this.info;
    }

    public final int getLength() {
        return this.info.getLength();
    }

    public final Node[] getMembers() {
        return this.members;
    }

    public final String getMinPath() {
        return this.info.getMinPath();
    }

    public final String getName() {
        return this.info.getName();
    }

    public final int getOwner() {
        return this.info.getOwner();
    }

    public final String getPath() {
        return this.info.getPath();
    }

    public final Node[] getSons() {
        return this.sons;
    }

    public final String[] getTags() {
        try{
            return this.database.getTags(this.nid);
        }catch(final MdsException e){}catch(final Exception e){
            jTraverserFacade.stderr("getTags", e);
        }
        return new String[0];
    }

    public final String getToolTipText() {
        /*if(false){
            final String tags[] = this.node.getTags();
            if(tags == null || tags.length == 0) return null;
            final String text;
            if(tags.length > 32) text = String.join("<br>", Arrays.copyOfRange(tags, 0, 32)) + "<br>...";
            else text = String.join("<br>", tags);
            return new StringBuilder("<html>").append(text).append("</html>").toString();
        }else*/{
            final long now = System.nanoTime();
            if(this.ToolTipText == null || now > this.ToolTipLife || this.ToolTipDefault != this.tree.getDefault()){
                String text, info;
                final int defaultnid = this.tree.getDefault();
                try{
                    info = this.getInfo().toString();
                    if(this.getUsage() == NodeInfo.USAGE_STRUCTURE || this.getUsage() == NodeInfo.USAGE_SUBTREE) text = null;
                    else{
                        final Descriptor data = this.getData();
                        if(data == null) text = null;
                        else text = data.toStringX().replace("<", "&lt;").replace(">", "&gt;").replace("\t", "&nbsp&nbsp&nbsp&nbsp ").replace("\n", "<br>");
                    }
                }catch(final MdsException e){
                    return e.toString();
                }
                if(text == null) return info;
                final StringBuilder sb = new StringBuilder().append(info.substring(0, info.length() - 7)).append("<hr><table");
                if(text.length() > 80) sb.append(" width=\"320\"");
                this.ToolTipText = sb.append(">").append(text).append("</table></html>").toString();
                this.ToolTipLife = now + 30000000000l;
                this.ToolTipDefault = defaultnid;
            }
            return this.ToolTipText;
        }
    }

    public final DefaultMutableTreeNode getTreeNode() {
        return this.treenode;
    }

    public final byte getUsage() {
        return this.info.getUsage();
    }

    public final boolean isCached() {
        return this.info.isCached();
    }

    public final boolean isCompressible() {
        return this.info.isCompressible();
    }

    public final boolean isCompressOnPut() {
        return this.info.isCompressOnPut();
    }

    public final boolean isCompressSegments() {
        return this.info.isCompressSegments();
    }

    public final boolean isDefault() {
        return this.tree.getDefault() == this.nid.getValue();
    }

    public final boolean isDoNotCompress() {
        return this.info.isDoNotCompress();
    }

    public final boolean isEssential() {
        return this.info.isEssential();
    }

    public final boolean isIncludeInPulse() {
        return this.info.isIncludeInPulse();
    }

    public final boolean isNidReference() {
        return this.info.isNidReference();
    }

    public final boolean isNoWriteModel() {
        return this.info.isNoWriteModel();
    }

    public final boolean isNoWriteShot() {
        return this.info.isNoWriteShot();
    }

    public final boolean isOn() {
        if(this.needsOnCheck){
            this.needsOnCheck = false;
            try{
                this.is_on = this.database.isOn(this.nid);
            }catch(final Exception exc){
                jTraverserFacade.stderr("Error checking state", exc);
            }
        }
        return this.is_on;
    }

    public final boolean isParentState() {
        return this.info.isParentState();
    }

    public final boolean isPathReference() {
        return this.info.isPathReference();
    }

    public final boolean isSegmented() {
        if(this.info.isSegmented()) return true;
        return this.nid.getNumSegments() > 0;
    }

    public final boolean isSetup() {
        return this.info.isSetup();
    }

    public final boolean isState() {
        return this.info.isState();
    }

    public final boolean isSubTree() {
        return this.info.isSubTree();
    }

    public final boolean isVersion() {
        return this.info.isVersion();
    }

    public final boolean isWriteOnce() {
        return this.info.isWriteOnce();
    }

    final boolean move(final Node newParent) {
        return this.changePath(newParent, this.getName());
    }

    public final void paste() {
        if(this.tree.isEditable()){
            jTraverserFacade.stdout((Node.cut ? "moved: " : "copied: ") + Node.copied + " from " + Node.copied.parent + " to " + this);
            if(Node.copied != null && Node.copied != this){
                if(Node.cut){
                    if(Node.copied.move(this)) Node.copied = null;
                }else this.tree.pasteSubtree(Node.copied, this, true);
            }
        }else jTraverserFacade.stdout("Cannot paste " + Node.copied + ". Tree not in edit mode.");
    }

    public final boolean rename(final String newName) {
        return this.changePath(this.parent, newName);
    }

    public final Nid resolveRefSimple() throws MdsException {
        final Nid ref_nid = this.database.resolveRefSimple(this.nid);
        return ref_nid;
    }

    public final void setAllOnUnchecked() {
        Node currNode = this;
        while(currNode.parent != null)
            currNode = currNode.parent;
        currNode.setOnUnchecked();
    }

    public final void setData(final Descriptor data) throws MdsException {
        this.data = data;
        this.database.putData(this.nid, data);
    }

    public final void setDefault() throws MdsException {
        this.database.setDefault(this.nid);
        this.tree.updateDefault();
    }

    public final void setFlag(final byte idx) throws MdsException {
        this.setFlags(1 << idx);
    }

    public final void setFlags(final int flags) throws MdsException {
        this.database.setFlags(this.nid, flags);
        this.info.setFlags(this.database.getFlags(this.nid));
    }

    public final void setInfo(final NodeInfo info) throws MdsException {}

    final void setOnUnchecked() {
        this.needsOnCheck = true;
        for(final Node son : this.sons)
            son.setOnUnchecked();
        for(final Node member : this.members)
            member.setOnUnchecked();
    }

    public void setSubtree() throws MdsException {
        this.database.setSubtree(this.nid);
        try{
            this.info = this.database.getInfo(this.nid);
            this.label = null;
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error getting info", exc);
        }
    }

    public final void setTags(final String[] tags) throws MdsException {
        this.database.setTags(this.nid, tags);
    }

    public final DefaultMutableTreeNode setTreeNode(final DefaultMutableTreeNode treenode) {
        return this.treenode = treenode;
    }

    public final void setupDevice() {
        Conglom conglom = null;
        try{
            conglom = (Conglom)this.database.getData(this.nid);
        }catch(final MdsException e){
            JOptionPane.showMessageDialog(this.tree.treeman, e.getMessage(), "Error in device setup 1", JOptionPane.WARNING_MESSAGE);
        }
        if(conglom != null){
            final CString model = (CString)conglom.getModel();
            if(model != null){
                try{
                    DeviceSetup ds = DeviceSetup.getDevice(this.nid.getValue());
                    if(ds == null){
                        final String deviceClassName = model.getValue() + "Setup";
                        final Class deviceClass = Class.forName(deviceClassName);
                        ds = (DeviceSetup)deviceClass.newInstance();
                        ds.setFrame(this.tree.treeman.getFrame());
                        final Dimension prevDim = ds.getSize();
                        ds.addDataChangeListener(this.tree);
                        ds.configure(this.database, this.nid.getValue(), this);
                        if(ds.getContentPane().getLayout() != null) ds.pack();
                        ds.setLocation(this.tree.treeman.dialogLocation());
                        ds.setSize(prevDim);
                        ds.setVisible(true);
                    }else ds.setVisible(true);
                    return;
                }catch(final Exception e){
                    try{
                        this.database.doDeviceMethod(this.nid, "dw_setup");
                    }catch(final Exception exc){
                        JOptionPane.showMessageDialog(this.tree.treeman, e.getMessage(), "Error in device setup: " + e, JOptionPane.WARNING_MESSAGE);
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
        JOptionPane.showMessageDialog(this.tree, "Missing model in descriptor", "Error in device setup 3", JOptionPane.WARNING_MESSAGE);
    }

    public final void toggle() throws MdsException {
        this.database.setOn(this.nid, !this.database.isOn(this.nid));
        this.setOnUnchecked();
    }

    public final void toggleFlags(final int flags) throws MdsException {
        final int isflags = this.database.getFlags(this.nid);
        final int clear = isflags & flags, set = (~isflags) & flags;
        if(set != 0) this.database.setFlags(this.nid, set);
        if(clear != 0) this.database.clearFlags(this.nid, clear);
        this.info.setFlags(this.database.getFlags(this.nid));
    }

    @Override
    public final String toString() {
        return this.getName();
    }

    public final void turnOff() {
        try{
            this.database.setOn(this.nid, false);
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error turning off", exc);
        }
        this.setOnUnchecked();
    }

    public final void turnOn() {
        try{
            this.database.setOn(this.nid, true);
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error turning on", exc);
        }
        this.setOnUnchecked();
    }

    public final void updateData() throws MdsException {
        this.data = this.database.getData(this.nid);
    }

    public final void updateInfo() throws MdsException {
        this.info = this.database.getInfo(this.nid);
    }
}

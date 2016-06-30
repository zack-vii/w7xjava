package jtraverser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import mds.Database;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Conglom;
import mds.data.descriptor_s.Nid;

@SuppressWarnings("serial")
public class Tree extends JTree implements TreeSelectionListener, DataChangeListener{
    // Inner class FromTranferHandler managed drag operation
    class FromTransferHandler extends TransferHandler{
        @Override
        public final Transferable createTransferable(final JComponent comp) {
            try{
                return new StringSelection(Tree.this.getName() + ":" + Tree.this.getCurrentNode().getFullPath());
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public final int getSourceActions(final JComponent comp) {
            return TransferHandler.COPY_OR_MOVE;
        }
    }
    private final class MDSCellRenderer extends DefaultTreeCellRenderer{
        @Override
        public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded, final boolean hasFocus, final int row, final boolean leaf) {
            final Object usrObj = ((DefaultMutableTreeNode)value).getUserObject();
            Node node;
            if(usrObj instanceof String){
                node = Tree.this.getCurrentNode();
                if(node.getTreeNode() == value){
                    final String newName = (((String)usrObj).trim()).toUpperCase();
                    if(Tree.this.lastName == null || !Tree.this.lastName.equals(newName)){
                        Tree.this.lastName = newName;
                        node.rename(newName);
                    }
                    node.getTreeNode().setUserObject(node);
                }
            }else node = (Node)usrObj;
            if(isSelected) Tree.this.treeman.dialogs.update();
            return node.getIcon(isSelected);
        }
    };

    public static final Descriptor compile(final String expr) throws MdsException {
        return Database.tdiCompile(expr);
    }

    public static final String decompile(final Descriptor data) {
        return data.toString();
    }
    /*
    public static final void main(final String[] args) throws Exception {//TODO:main
        final String def_server = System.getProperty("server");
        final String def_tree = System.getProperty("tree");
        if(def_tree != null){
            final String def_shot = System.getProperty("shot");
            int shot;
            if(def_shot != null) shot = Integer.parseInt(def_shot);
            else shot = -1;
            new Tree(null, def_server, def_tree, shot, 0);
        }
    }
    */
    private JDialog                add_device_dialog;
    private JTextField             add_device_type, add_device_name;
    private Node                   curr_node;
    private final Database         database;
    private int                    default_nid = 0;
    private String                 lastName;
    private DefaultMutableTreeNode top;
    public final TreeManager       treeman;

    public Tree(final TreeManager treeman, final String provider, final String expt, final long shot, final int mode) throws MdsException{
        super();
        this.treeman = treeman;
        Database database;
        try{
            database = new Database(provider, expt, shot, mode);
        }catch(final MdsException me){
            if(mode != Database.EDITABLE || me.getStatus() != MdsException.TREE_E_FOPENW) throw me;
            final int n = JOptionPane.showConfirmDialog(this, "Tree " + expt + " cannot be opened in edit mode. Create new instead?", "Editing Tree ", JOptionPane.YES_NO_OPTION);
            if(n != JOptionPane.YES_OPTION) throw me;
            database = new Database(provider, expt, shot, Database.NEW);
        }
        this.database = database;
        final DefaultTreeModel model = (DefaultTreeModel)this.getModel();
        final Node top_node = new Node(this);
        top_node.setTreeNode(this.top = new DefaultMutableTreeNode(top_node));
        model.setRoot(this.top);
        top_node.expand();
        final Node members[] = top_node.getMembers();
        for(final Node member : members){
            DefaultMutableTreeNode currNode;
            this.top.add(currNode = new DefaultMutableTreeNode(member));
            member.setTreeNode(currNode);
        }
        final Node sons[] = top_node.getSons();
        for(final Node son : sons){
            DefaultMutableTreeNode currNode;
            this.top.add(currNode = new DefaultMutableTreeNode(son));
            son.setTreeNode(currNode);
        }
        this.setCurrentNode(0);
        // GAB 2014 Add DragAndDrop capability
        this.setTransferHandler(new FromTransferHandler());
        this.setDragEnabled(true);
        /////////////////////////////
        ToolTipManager.sharedInstance().registerComponent(this);
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(final KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_CANCEL) // i.e. Ctrl+C
                {
                    Tree.this.getCurrentNode().copyToClipboard();
                    Tree.this.getCurrentNode().copy();
                }else if(e.getKeyChar() == 24) // i.e. Ctrl+X
                if(Tree.this.isEditable()) Tree.this.getCurrentNode().cut();
                else Tree.this.getCurrentNode().copy();
                else if(e.getKeyChar() == 22) // i.e. Ctrl+V
                Tree.this.getCurrentNode().paste();
                else if(e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) Tree.this.getCurrentNode().delete();
                Tree.this.treeman.reportChange();
            }
        });
        this.setEditable(this.database.isEditable());
        this.setCellRenderer(new MDSCellRenderer());
        this.addTreeSelectionListener(this);
        this.addMouseListener(treeman.getContextMenu());
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public void addDevice() {
        final Node currnode = this.getCurrentNode();
        if(currnode == null) return;
        if(this.add_device_dialog == null){
            this.add_device_dialog = new JDialog(this.treeman.getFrame());
            final JPanel jp = new JPanel();
            jp.setLayout(new BorderLayout());
            JPanel jp1 = new JPanel();
            jp1.add(new JLabel("Device: "));
            jp1.add(this.add_device_type = new JTextField(12));
            jp.add(jp1, "North");
            jp1 = new JPanel();
            jp1.add(new JLabel("Name:   "));
            jp1.add(this.add_device_name = new JTextField(12));
            jp.add(jp1, "South");
            jp1 = new JPanel();
            JButton ok_button;
            jp1.add(ok_button = new JButton("Ok"));
            ok_button.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if(Tree.this.addDevice(Tree.this.add_device_name.getText().toUpperCase(), Tree.this.add_device_type.getText()) == null) ;
                    Tree.this.add_device_dialog.setVisible(false);
                }
            });
            final JButton cancel_b = new JButton("Cancel");
            cancel_b.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Tree.this.add_device_dialog.setVisible(false);
                }
            });
            jp1.add(cancel_b);
            JPanel jp2;
            jp2 = new JPanel();
            jp2.setLayout(new BorderLayout());
            jp2.add(jp, "North");
            jp2.add(jp1, "South");
            this.add_device_dialog.getContentPane().add(jp2);
            this.add_device_dialog.addKeyListener(new KeyAdapter(){
                @Override
                public void keyTyped(final KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) if(Tree.this.addDevice(Tree.this.add_device_name.getText().toUpperCase(), Tree.this.add_device_type.getText()) == null) ;
                    Tree.this.add_device_dialog.setVisible(false);
                }
            });
            this.add_device_dialog.pack();
        }
        this.add_device_name.setText("");
        this.add_device_type.setText("");
        this.add_device_dialog.setTitle("Add device to: " + currnode.getFullPath());
        this.add_device_dialog.setLocation(this.treeman.dialogLocation());
        this.add_device_dialog.setVisible(true);
    }

    public Node addDevice(final String name, final String type) {
        return this.addDevice(name, type, this.getCurrentNode());
    }

    public Node addDevice(final String name, final String type, final Node toNode) {
        DefaultMutableTreeNode new_tree_node = null;
        if(name == null || name.length() == 0 || name.length() > 12){
            JOptionPane.showMessageDialog(this.treeman.getFrame(), "Name length must range between 1 and 12 characters", "Error adding Node", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if(type == null || type.length() == 0){
            JOptionPane.showMessageDialog(this.treeman.getFrame(), "Missing device type", "Error adding Node", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Node new_node = null;
        try{
            new_node = toNode.addDevice(name, type);
            final int num_children = toNode.getTreeNode().getChildCount();
            int i;
            if(num_children > 0){
                String curr_name;
                for(i = 0; i < num_children; i++){
                    curr_name = Node.getNode(toNode.getTreeNode().getChildAt(i)).getName();
                    if(name.compareTo(curr_name) < 0) break;
                }
                new_node.setTreeNode(new_tree_node = new DefaultMutableTreeNode(new_node));
                final DefaultTreeModel tree_model = (DefaultTreeModel)this.getModel();
                tree_model.insertNodeInto(new_tree_node, this.getCurrTreeNode(), i);
                this.makeVisible(new TreePath(new_tree_node.getPath()));
                return new_node;
            }
        }catch(final Throwable e){
            JOptionPane.showMessageDialog(this.treeman.getFrame(), "Add routine for the selected device cannot be activated:\n" + e.getMessage(), "Error adding Device", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new_node;
    }

    public Node addNode(final int usage, final String name) {
        return this.addNode(usage, name, this.getCurrentNode());
    }

    public Node addNode(final int usage, final String name, final Node toNode) {
        Node new_node;
        DefaultMutableTreeNode new_tree_node;
        // final DefaultMutableTreeNode toTreeNode = toNode.getTreeNode();
        if(name == null || name.length() == 0 || name.length() > 12){
            JOptionPane.showMessageDialog(this.treeman.getFrame(), "Name length must range between 1 and 12 characters", "Error adding Node", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try{
            new_node = toNode.addNode(name, usage);
            new_tree_node = new DefaultMutableTreeNode(new_node);
            new_node.setTreeNode(new_tree_node);
            // this.addNodeToParent(new_tree_node, toTreeNode);
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this.treeman.getFrame(), e.getMessage(), "Error adding Node", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new_node;
    }

    public void addNodeToParent(final DefaultMutableTreeNode treenode, final DefaultMutableTreeNode toTreeNode) {
        final int num_children = toTreeNode.getChildCount();
        int i = 0;
        if(num_children > 0){
            final String name = Node.getNode(treenode).getName();
            String curr_name;
            for(i = 0; i < num_children; i++){
                curr_name = ((Node)((DefaultMutableTreeNode)toTreeNode.getChildAt(i)).getUserObject()).getName();
                if(name.compareTo(curr_name) < 0) break;
            }
        }
        final DefaultTreeModel tree_model = (DefaultTreeModel)Tree.this.getModel();
        tree_model.insertNodeInto(treenode, toTreeNode, i);
        Tree.this.expandPath(new TreePath(treenode.getPath()));
        Tree.this.treeDidChange();
    }

    public final void close() {
        try{
            this.database.close();
        }catch(final Exception e){
            boolean editable = false;
            String name = null;
            try{
                editable = this.database.isEditable();
                name = this.database.getName().trim();
            }catch(final Exception exc){}
            if(editable){
                final int n = JOptionPane.showConfirmDialog(this.treeman.getFrame(), "Tree " + name + " open in edit mode has been changed: Write it before closing?", "Closing Tree ", JOptionPane.YES_NO_OPTION);
                if(n == JOptionPane.YES_OPTION){
                    try{
                        this.database.write();
                        this.database.close();
                    }catch(final Exception exc){
                        JOptionPane.showMessageDialog(this.treeman.getFrame(), "Error closing tree", exc.getMessage(), JOptionPane.WARNING_MESSAGE);
                    }
                }else{
                    try{
                        this.database.quit();
                    }catch(final Exception exce){
                        JOptionPane.showMessageDialog(this.treeman.getFrame(), "Error quitting tree", exce.getMessage(), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }else JOptionPane.showMessageDialog(this.treeman.getFrame(), "Error closing tree", e.getMessage(), JOptionPane.WARNING_MESSAGE);
        }
    }

    @SuppressWarnings("static-method")
    public final void compile() {
        return;
    }

    @Override
    public void dataChanged(final DataChangeEvent e) {
        this.treeman.reportChange();
    }

    @SuppressWarnings("static-method")
    public final void decompile() {
        return;
    }

    public final void deleteNode() {
        this.deleteNode(this.getCurrentNode());
        this.setCurrentNode(0);
    }

    public void deleteNode(final Node delNode) {
        if(delNode == null) return;
        final Node del_node = delNode;
        final int n_children = del_node.startDelete();
        if(n_children < 0) return;
        String msg = "You are about to delete node " + del_node.getName().trim();
        if(n_children > 0) msg += " which has " + n_children + " descendents.\n Please confirm";
        else msg += "\n Please confirm";
        final int n = JOptionPane.showConfirmDialog(this.treeman.getFrame(), msg, "Delete node(s)", JOptionPane.YES_NO_OPTION);
        if(n == JOptionPane.YES_OPTION){
            if(!del_node.executeDelete()) return;
            final DefaultTreeModel tree_model = (DefaultTreeModel)this.getModel();
            tree_model.removeNodeFromParent(delNode.getTreeNode());
        }
    }

    public final DefaultMutableTreeNode findNid(final Nid nid) {
        return this.findPath(nid.getFullPath());
    }

    public final DefaultMutableTreeNode findPath(final String path) {
        final String[] treepath = path.split("::", 2);
        return this.findPath(treepath[treepath.length - 1].split("[\\.:]"));
    }

    public final DefaultMutableTreeNode findPath(String[] path) {
        if(path[0].equalsIgnoreCase("TOP")) path = Arrays.copyOfRange(path, 1, path.length);
        return this.findSubPath(path, this.top);
    }

    private final DefaultMutableTreeNode findSubPath(final String[] path, final DefaultMutableTreeNode root) {
        if(path == null || path.length == 0) return root;
        final Enumeration children = root.children();
        while(children.hasMoreElements()){
            final DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
            final Node node = (Node)child.getUserObject();
            if(node.getName().equalsIgnoreCase(path[0])) return this.findSubPath(Arrays.copyOfRange(path, 1, path.length), child);
        }
        return null;
    }

    public final Node getCurrentNode() {
        return this.curr_node;
    }

    public final DefaultMutableTreeNode getCurrTreeNode() {
        final Node node = this.getCurrentNode();
        if(node == null) return null;
        return node.getTreeNode();
    }

    public final Database getDatabase() {
        return this.database;
    }

    public final int getDefault() {
        return this.default_nid;
    }

    public final String getExpt() {
        return this.database.getName();
    }

    public final String getProvider() {
        return this.database.getProvider();
    }

    public final long getShot() {
        return this.database.getShot();
    }

    public final Nid[] getSubTrees() {
        try{
            return this.database.getWild(NodeInfo.USAGE_SUBTREE);
        }catch(final MdsException e){
            e.printStackTrace();
            return null;
        }
    }

    public final boolean isModel() {
        return this.database.getShot() < 0;
    }

    public final boolean isReadOnly() {
        return this.database.isReadonly();
    }

    public void pasteSubtree(final Node fromNode, final Node toNode, final boolean isMember) {
        final DefaultMutableTreeNode savedTreeNode = this.getCurrTreeNode();
        try{
            fromNode.expand();
            final String[] usedNames = new String[toNode.getSons().length + toNode.getMembers().length];
            // collect names used so far
            int idx = 0;
            for(final Node son : toNode.getSons())
                usedNames[idx++] = son.getName();
            for(final Node member : toNode.getMembers())
                usedNames[idx++] = member.getName();
            if(fromNode.getUsage() == NodeInfo.USAGE_DEVICE){
                final Conglom conglom = (Conglom)fromNode.getData();
                final Node newNode = this.addDevice((isMember ? ":" : ".") + Node.getUniqueName(fromNode.getName(), usedNames), conglom.getModel().toString(), toNode);
                newNode.expand();
                Node.copySubtreeContent(fromNode, newNode);
            }else{
                final Node newNode = this.addNode(fromNode.getUsage(), (isMember ? ":" : ".") + Node.getUniqueName(fromNode.getName(), usedNames), toNode);
                if(newNode == null) return;
                newNode.expand();
                try{
                    final Descriptor data = fromNode.getData();
                    if(data != null && fromNode.getUsage() != NodeInfo.USAGE_ACTION) newNode.setData(data);
                }catch(final Exception exc){}
                for(final Node son : fromNode.getSons()){
                    this.pasteSubtree(son, newNode, false);
                }
                for(final Node member : fromNode.getMembers()){
                    this.pasteSubtree(member, newNode, true);
                }
            }
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "" + exc, "Error copying subtree", JOptionPane.WARNING_MESSAGE);
        }
        this.setCurrentNode(savedTreeNode);
    }

    public final void setCurrentNode(final DefaultMutableTreeNode treenode) {
        final Node curr_node = Node.getNode(treenode);
        if(curr_node == null) return;
        this.setCurrentNode(curr_node);
    }

    private final void setCurrentNode(final int row) {
        this.setSelectionRow(row);
        final DefaultMutableTreeNode DMN = (DefaultMutableTreeNode)this.getLastSelectedPathComponent();
        if(DMN == null) return;
        this.setCurrentNode(DMN);
    }

    public final void setCurrentNode(final Node curr_node) {
        this.setSelectionPath(new TreePath(curr_node.getTreeNode().getPath()));
        this.curr_node = curr_node;
    }

    @Override
    public final String toString() {
        return this.getDatabase().toString();
    }

    public final void updateDefault() throws MdsException {
        if(this.database == null) return;
        this.default_nid = this.database.getDefault().getValue();
    }

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        final DefaultMutableTreeNode tree_node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
        final Node currnode = Node.getNode(tree_node);
        this.setCurrentNode(currnode);
        if(tree_node.isLeaf()){
            Node sons[], members[];
            DefaultMutableTreeNode last_node = null;
            try{
                this.getCurrentNode().expand();
            }catch(final Exception exc){
                jTraverserFacade.stderr("Error expanding tree", exc);
            }
            sons = currnode.getSons();
            members = currnode.getMembers();
            for(final Node member : members){
                tree_node.add(last_node = new DefaultMutableTreeNode(member));
                member.setTreeNode(last_node);
            }
            for(final Node son : sons){
                tree_node.add(last_node = new DefaultMutableTreeNode(son));
                son.setTreeNode(last_node);
            }
            if(last_node != null) this.expandPath(new TreePath(last_node.getPath()));
        }
    }

    public final void write() {
        try{
            this.database.write();
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this.treeman.getFrame(), "Error writing tree", exc.getMessage(), JOptionPane.WARNING_MESSAGE);
        }
    }
}

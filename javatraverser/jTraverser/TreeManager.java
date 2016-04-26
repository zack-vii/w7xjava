package jTraverser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import devicebeans.DeviceSetup;
import jTraverser.dialogs.Dialogs;
import jTraverser.dialogs.DisplayData;
import jTraverser.dialogs.DisplayNci;
import jTraverser.dialogs.DisplayTags;
import jTraverser.dialogs.ModifyData;
import jTraverser.dialogs.ModifyTags;
import jTraverser.dialogs.TreeDialog;
import jTraverser.dialogs.TreeOpenDialog;
import jTraverser.editor.NodeEditor;
import mds.MdsException;

public class TreeManager extends JScrollPane{
    public static final class AddNodeMenu extends Menu{
        private final class addDevice implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                AddNodeMenu.this.treeman.getCurrentTree().addDevice();
            }
        }
        public class AddNodeAL implements ActionListener{
            private final int usage;

            public AddNodeAL(final int usage){
                this.usage = usage;
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                AddNodeMenu.this.treeman.dialogs.addNode.open(AddNodeMenu.this.treeman.getCurrentNode(), this.usage);
            }
        }
        private final String[] name  = new String[]{"Structure", "Subtree", "Action", "Any", "Axis", "Dispatch", "Numeric", "Signal", "Task", "Text", "Window"};
        private final int[]    usage = new int[]{NodeInfo.USAGE_STRUCTURE, NodeInfo.USAGE_SUBTREE, NodeInfo.USAGE_ACTION, NodeInfo.USAGE_ANY, NodeInfo.USAGE_AXIS, NodeInfo.USAGE_DISPATCH, NodeInfo.USAGE_NUMERIC, NodeInfo.USAGE_SIGNAL, NodeInfo.USAGE_TASK, NodeInfo.USAGE_TEXT, NodeInfo.USAGE_WINDOW};

        public AddNodeMenu(final TreeManager treeman, final JComponent menu){
            super(treeman);
            menu.add(this.addMenuItem("Device", new addDevice()));
            for(int i = 0; i < this.name.length; i++)
                menu.add(this.addMenuItem(this.name[i], new AddNodeAL(this.usage[i])));
        }
    }
    public static class DisplayMenu extends Menu{
        public final class modifyFlags implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                DisplayMenu.this.treeman.dialogs.flags.open();
            }
        }

        public DisplayMenu(final TreeManager treeman, final JComponent menu){
            super(treeman);
            menu.add(this.addMenuItem("Display Data", new Menu.NodeEditorAL(DisplayData.class)));
            menu.add(this.addMenuItem("Display Nci", new Menu.NodeEditorAL(DisplayNci.class)));
            menu.add(this.addMenuItem("Display Flags", new modifyFlags()));
            menu.add(this.addMenuItem("Display Tags", new Menu.NodeEditorAL(DisplayTags.class)));
        }

        @Override
        public final void checkSupport() {
            final Node node = this.treeman.getCurrentNode();
            boolean[] mask = new boolean[this.items.size()];
            if(node != null){
                final int usage = node.getUsage();
                final boolean enable = !(usage == NodeInfo.USAGE_STRUCTURE || usage == NodeInfo.USAGE_SUBTREE);
                mask = new boolean[]{enable, true, true, enable};
            }
            for(int i = 0; i < mask.length; i++)
                this.items.get(i).setEnabled(mask[i]);
        }
    }
    public static final class EditMenu extends Menu{
        private final class copyNode implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                EditMenu.this.treeman.getCurrentNode().copy();
            }
        }
        private final class deleteNode implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                EditMenu.this.treeman.getCurrentTree().deleteNode();
            }
        }
        public final class editTags implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Node currnode = EditMenu.this.treeman.getCurrentNode();
                if(currnode == null) return;
                new ModifyTags(EditMenu.this.treeman).open(currnode);
            }
        };
        public final class pasteNode implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                EditMenu.this.treeman.getCurrentNode().paste();
            }
        }
        public final class renameNode implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                EditMenu.this.treeman.dialogs.rename.open(EditMenu.this.treeman.getCurrentNode());
            }
        }

        public EditMenu(final TreeManager treeman, final JComponent menu){
            super(treeman);
            final JMenu submenu = new JMenu("Add Node");
            this.items.add(submenu);
            menu.add(submenu);
            new TreeManager.AddNodeMenu(treeman, submenu);
            menu.add(this.addMenuItem("Delete Node", new deleteNode()));
            menu.add(this.addMenuItem("Rename node", new renameNode()));
            menu.add(this.addMenuItem("Edit Tags", new editTags()));
            if(menu instanceof JPopupMenu) ((JPopupMenu)menu).addSeparator();
            menu.add(this.addMenuItem("Copy", new copyNode()));
            menu.add(this.addMenuItem("Paste", new pasteNode()));
        };

        @Override
        public final void checkSupport() {
            final Node node = this.treeman.getCurrentNode();
            boolean[] mask = new boolean[this.items.size()];
            if(node != null){
                final int usage = node.getUsage();
                final boolean isst = usage == NodeInfo.USAGE_SUBTREE;
                mask = new boolean[]{!isst || node.nid.getValue() == 0, node.nid.getValue() != 0, !isst, !isst, node.nid.getValue() > 0, true};
            }
            for(int i = 0; i < mask.length; i++)
                this.items.get(i).setEnabled(mask[i]);
        }
    }
    public static class FileMenu extends Menu{
        private final class close implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileMenu.this.treeman.close();
            }
        }
        /*private final class compile implements ActionListener{
        @Override
        public void actionPerformed(final ActionEvent e) {
            TreeManager.this.compile();
        }
        }
        private final class decompile implements ActionListener{
        @Override
        public void actionPerformed(final ActionEvent e) {
            Tree.this.decompile();
        }
        };*/
        private final class open implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileMenu.this.treeman.open_dialog.open();
            }
        }
        private final class quit implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileMenu.this.treeman.quit();
            }
        };
        private final class write implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileMenu.this.treeman.write();
            }
        }

        public FileMenu(final TreeManager treeman, final JComponent menu){
            super(treeman);
            menu.add(this.addMenuItem("Open", new open()));
            menu.add(this.addMenuItem("Write", new write()));
            menu.add(this.addMenuItem("Close", new close()));
            /*menu.add(this.addMenuItem("Export",new decompile()));
            menu.add(this.addMenuItem("Import",new compile()));*/
            menu.add(this.addMenuItem("Quit", new quit()));
        }

        @Override
        public final void checkSupport() {
            final boolean noopen = this.treeman.getCurrentTree() == null;
            this.items.get(2).setEnabled(!noopen);
            this.items.get(1).setEnabled(noopen ? false : this.treeman.getCurrentTree().isEditable());
        }
    }
    public static class Menu{
        protected final class NodeEditorAL implements ActionListener{
            private final Class<? extends NodeEditor> nodeeditor;

            public NodeEditorAL(final Class<? extends NodeEditor> nodeeditor){
                this.nodeeditor = nodeeditor;
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                final Node currnode = Menu.this.treeman.getCurrentNode();
                if(currnode == null) return;
                try{
                    final NodeEditor dd = this.nodeeditor.newInstance();
                    TreeDialog dialog;
                    dialog = new TreeDialog(dd);
                    dd.setFrame(dialog);
                    dd.setNode(currnode);
                    dialog.pack();
                    dialog.setLocation(Menu.this.treeman.dialogLocation());
                    dialog.setVisible(true);
                }catch(final Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        protected final List<JMenuItem> items = new ArrayList<JMenuItem>();
        final TreeManager               treeman;

        public Menu(final TreeManager treeman){
            this.treeman = treeman;
        }

        protected final JMenuItem addMenuItem(final String name, final ActionListener l) {
            final JMenuItem item = new JMenuItem(name);
            this.items.add(item);
            item.addActionListener(l);
            return item;
        }

        public void checkSupport() {}
    }
    public final class mlContextMenu extends MouseAdapter{
        public mlContextMenu(){}

        @Override
        public final void mouseClicked(final MouseEvent e) {
            final Tree tree = (Tree)e.getSource();
            final DefaultMutableTreeNode tree_node = (DefaultMutableTreeNode)tree.getClosestPathForLocation(e.getX(), e.getY()).getLastPathComponent();
            final Node currnode = Node.getNode(tree_node);
            tree.setCurrentNode(currnode);
            if((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
                final JPopupMenu pop = new JPopupMenu();
                new TreeManager.DisplayMenu(TreeManager.this, pop).checkSupport();
                if(!tree.isReadOnly()){
                    pop.addSeparator();
                    new TreeManager.ModifyMenu(TreeManager.this, pop).checkSupport();
                }
                if(tree.isEditable()){
                    pop.addSeparator();
                    new TreeManager.EditMenu(TreeManager.this, pop).checkSupport();
                }
                pop.show((Component)e.getSource(), e.getX(), e.getY());
            }
            tree.treeDidChange();
            TreeManager.this.dialogs.update();
        }
    }
    public static final class ModifyMenu extends Menu{
        public final class doAction implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                jTraverserFacade.stdout("doAction not yet implemented.");
            }
        };
        public final class modifyFlags implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e1) {
                ModifyMenu.this.treeman.dialogs.flags.open();
            }
        }
        public final class setDefault implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Node currnode = ModifyMenu.this.treeman.getCurrentNode();
                if(currnode == null) return;
                try{
                    currnode.setDefault();
                }catch(final Exception exc){
                    jTraverserFacade.stderr("Error setting default", exc);
                }
                ModifyMenu.this.treeman.reportChange();
            }
        };
        public final class setupDevice implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Node currnode = ModifyMenu.this.treeman.getCurrentNode();
                if(currnode == null) return;
                currnode.setupDevice();
            }
        }
        private final class turnOff implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Node currnode = ModifyMenu.this.treeman.getCurrentNode();
                if(currnode == null) return;
                currnode.turnOff();
                ModifyMenu.this.treeman.reportChange();
            }
        }
        private final class turnOn implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Node currnode = ModifyMenu.this.treeman.getCurrentNode();
                if(currnode == null) return;
                currnode.turnOn();
                ModifyMenu.this.treeman.reportChange();
            }
        }

        public ModifyMenu(final TreeManager treeman, final JComponent menu){
            super(treeman);
            menu.add(this.addMenuItem("Modify Data", new Menu.NodeEditorAL(ModifyData.class)));
            menu.add(this.addMenuItem("Modify Flags", new modifyFlags()));
            menu.add(this.addMenuItem("Turn On", new turnOn()));
            menu.add(this.addMenuItem("Turn Off", new turnOff()));
            menu.add(this.addMenuItem("Set Default", new setDefault()));
            menu.add(this.addMenuItem("Setup Device", new setupDevice()));
            menu.add(this.addMenuItem("Do Action", new doAction()));
        }

        @Override
        public final void checkSupport() {
            final Node node = this.treeman.getCurrentNode();
            boolean[] mask = new boolean[this.items.size()];
            if(node != null){
                final int usage = node.getUsage();
                final boolean isst = usage == NodeInfo.USAGE_STRUCTURE || usage == NodeInfo.USAGE_SUBTREE;
                final boolean isact = usage == NodeInfo.USAGE_ACTION || usage == NodeInfo.USAGE_TASK;
                final boolean isdev = usage == NodeInfo.USAGE_DEVICE;
                mask = new boolean[]{!isst, true, true, true, true, isdev, isact};
            }
            for(int i = 0; i < mask.length; i++)
                this.items.get(i).setEnabled(mask[i]);
        }
    }
    private static final long serialVersionUID = -6393859107589833312L;

    static{
        ToolTipManager.sharedInstance().setDismissDelay(60000);
    }
    public final Dialogs           dialogs;
    private final jTraverserFacade frame;
    private final TreeOpenDialog   open_dialog;;
    private final Stack<Tree>      trees = new Stack<Tree>();

    public TreeManager(final jTraverserFacade frame){
        this(frame, null);
    }

    public TreeManager(final jTraverserFacade frame, final JScrollPane pane){
        this.frame = frame;
        this.setPreferredSize(new Dimension(300, 400));
        this.setBackground(Color.white);
        this.open_dialog = new TreeOpenDialog(this);
        this.dialogs = new Dialogs(this);
    }

    public final void close() {
        if(this.getCurrentTree() == null) return;
        this.trees.pop().close();
        final Tree tree = this.getCurrentTree();
        this.setViewportView(tree == null ? new JPanel() : tree);
        this.frame.reportChange(tree);
        DeviceSetup.closeOpenDevices();
        this.dialogs.update();
        this.frame.pack();
        this.frame.repaint();
    }

    public final Point dialogLocation() {
        return new Point(this.frame.getLocation().x + 32, this.frame.getLocation().y + 32);
    }

    public final MouseListener getContextMenu() {
        return new TreeManager.mlContextMenu();
    }

    public final Database getCurrentDatabase() {
        final Tree tree = this.getCurrentTree();
        if(tree == null) return null;
        return tree.getDatabase();
    }

    public final Node getCurrentNode() {
        final Tree tree = this.getCurrentTree();
        if(tree == null) return null;
        return tree.getCurrentNode();
    }

    public final Tree getCurrentTree() {
        if(this.trees.empty()) return null;
        return this.trees.peek();
    }

    public final Frame getFrame() {
        return this.frame;
    }

    public final void openTree(final String provider, final String expt, long shot, final int mode) {
        this.open_dialog.setFields(provider, expt, shot);
        // first we need to check if the tree is already open
        for(final Tree tree : this.trees){
            if(!tree.getExpt().equalsIgnoreCase(expt)) continue;
            try{
                if(shot == 0) shot = tree.getDatabase().getCurrentShot();
                if(tree.getShot() == shot){
                    tree.close();
                    this.trees.remove(tree);
                    break;
                }
            }catch(final Exception exc){}
        }
        Tree tree;
        try{
            tree = new Tree(this, provider, expt, shot, mode);
        }catch(final MdsException e){
            JOptionPane.showMessageDialog(this.frame, e.getMessage(), "Error opening tree " + expt, JOptionPane.ERROR_MESSAGE);
            return;
        }
        tree.expandRow(0);
        this.setViewportView(tree);
        this.trees.push(tree);
        this.frame.reportChange(tree);
        this.dialogs.update();
        this.repaint();
    }

    public final void quit() {
        while(!this.trees.empty())
            this.trees.pop().close();
        System.exit(0);
    }

    public final void reportChange() {
        if(this.getCurrentTree() != null) this.getCurrentTree().treeDidChange();
        this.dialogs.update();
    }

    public final void write() {
        this.getCurrentTree().write();
    }
}

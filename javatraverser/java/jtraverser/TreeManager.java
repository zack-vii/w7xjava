package jtraverser;

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
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import jtraverser.dialogs.Dialogs;
import jtraverser.dialogs.DisplayData;
import jtraverser.dialogs.DisplayNci;
import jtraverser.dialogs.DisplayTags;
import jtraverser.dialogs.GraphPanel;
import jtraverser.dialogs.ModifyData;
import jtraverser.dialogs.ModifyTags;
import jtraverser.dialogs.OpenConnectionDialog;
import jtraverser.dialogs.OpenTreeDialog;
import jtraverser.dialogs.SubTrees;
import jtraverser.dialogs.TreeDialog;
import jtraverser.editor.NodeEditor;
import jtraverser.tools.DecompileTree;
import mds.Mds;
import mds.MdsException;
import mds.TCL;
import mds.TREE;
import mds.TreeShr.TagRefStatus;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.NODE;
import mds.data.descriptor_s.Nid;
import mds.mdsip.Connection;
import mds.mdsip.Connection.Provider;

@SuppressWarnings("serial")
public class TreeManager extends JTabbedPane{
    public static final class AddNodeMenu extends Menu{
        private final class addDevice implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                AddNodeMenu.this.treeman.getCurrentTreeView().addDevice();
            }
        }
        public class AddNodeAL implements ActionListener{
            private final byte usage;

            public AddNodeAL(final byte usage){
                this.usage = usage;
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                AddNodeMenu.this.treeman.dialogs.addNode.open(AddNodeMenu.this.treeman.getCurrentNode(), this.usage);
            }
        }
        private final String[] name  = new String[]{   //
                "Structure",                           //
                "Subtree",                             //
                "Action",                              //
                "Any",                                 //
                "Axis",                                //
                "Dispatch",                            //
                "Numeric",                             //
                "Signal",                              //
                "Task",                                //
                "Text",                                //
                "Window"                               //
        };
        private final byte[]   usage = new byte[]{     //
                NODE.USAGE_STRUCTURE,                  //
                NODE.USAGE_SUBTREE,                    //
                NODE.USAGE_ACTION,                     //
                NODE.USAGE_ANY,                        //
                NODE.USAGE_AXIS,                       //
                NODE.USAGE_DISPATCH,                   //
                NODE.USAGE_NUMERIC,                    //
                NODE.USAGE_SIGNAL,                     //
                NODE.USAGE_TASK,                       //
                NODE.USAGE_TEXT,                       //
                NODE.USAGE_WINDOW                      //
        };

        public AddNodeMenu(final TreeManager treeman, final JComponent menu){
            super(treeman);
            menu.add(this.addMenuItem("Device", new addDevice()));
            for(int i = 0; i < this.name.length; i++)
                menu.add(this.addMenuItem(this.name[i], new AddNodeAL(this.usage[i])));
        }
    }
    public static class DisplayMenu extends Menu{
        public final class DisplaySignal implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent ae) {
                try{
                    final Descriptor sig = DisplayMenu.this.treeman.getCurrentNode().getData();
                    GraphPanel.newPlot(sig, DisplayMenu.this.treeman, DisplayMenu.this.treeman.getCurrentTree().expt, DisplayMenu.this.treeman.getCurrentTree().shot, DisplayMenu.this.treeman.getCurrentNode().getFullPath());
                }catch(final MdsException e){}
            }
        }
        public final class modifyFlags implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                DisplayMenu.this.treeman.dialogs.modifyFlags.open();
            }
        }
        public final class setDefault implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                final TreeView treeview = DisplayMenu.this.treeman.getCurrentTreeView();
                if(treeview == null) return;
                final Node currnode = treeview.getCurrentNode();
                if(currnode == null) return;
                try{
                    currnode.setDefault();
                }catch(final Exception exc){
                    MdsException.stderr("Error setting default", exc);
                }
                treeview.reportChange();
            }
        }

        public DisplayMenu(final TreeManager treeman, final JComponent menu){
            super(treeman);
            menu.add(this.addMenuItem("Display Data", new Menu.NodeEditorAL(DisplayData.class)));
            menu.add(this.addMenuItem("Display Signal", new DisplaySignal()));
            menu.add(this.addMenuItem("Display Nci", new Menu.NodeEditorAL(DisplayNci.class)));
            menu.add(this.addMenuItem("Display Flags", new modifyFlags()));
            menu.add(this.addMenuItem("Display Tags", new Menu.NodeEditorAL(DisplayTags.class)));
            if(menu instanceof JPopupMenu) ((JPopupMenu)menu).addSeparator();
            menu.add(this.addMenuItem("Set Default", new DisplayMenu.setDefault()));
        };

        @Override
        public final void checkSupport() {
            final Node node = this.treeman.getCurrentNode();
            boolean[] mask = new boolean[this.items.size()];
            if(node != null){
                final int usage = node.getUsage();
                final boolean enable = !(usage == NODE.USAGE_STRUCTURE || usage == NODE.USAGE_SUBTREE);
                final boolean enable2 = (usage == NODE.USAGE_SIGNAL);
                mask = new boolean[]{enable, enable2, true, true, true, true};
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
                EditMenu.this.treeman.getCurrentTreeView().deleteNode();
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
                final boolean isst = usage == NODE.USAGE_SUBTREE;
                mask = new boolean[]{!isst || node.nid.getValue() == 0, node.nid.getValue() != 0, !isst, !isst, node.nid.getValue() > 0, Node.isCopied()};
            }
            for(int i = 0; i < mask.length; i++)
                this.items.get(i).setEnabled(mask[i]);
        }
    }
    public static final class ExtrasMenu extends Menu{
        public final class CopyFormat extends JMenu implements ActionListener{
            public static final int    FULLPATH = 0;
            public static final int    PATH     = 1;
            public static final int    MINPATH  = 2;
            final JRadioButtonMenuItem fullpath, path, minpath;

            public CopyFormat(){
                super("Copy Format");
                final ButtonGroup group = new ButtonGroup();
                group.add(this.add(this.fullpath = new JRadioButtonMenuItem("FullPath")));
                group.add(this.add(this.path = new JRadioButtonMenuItem("Path")));
                group.add(this.add(this.minpath = new JRadioButtonMenuItem("MinPath")));
                this.fullpath.setSelected(true);
                this.fullpath.addActionListener(this);
                this.path.addActionListener(this);
                this.minpath.addActionListener(this);
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                if(e.getSource().equals(CopyFormat.this.fullpath)) ExtrasMenu.this.treeman.copy_format = CopyFormat.FULLPATH;
                else if(e.getSource().equals(CopyFormat.this.path)) ExtrasMenu.this.treeman.copy_format = CopyFormat.PATH;
                else if(e.getSource().equals(CopyFormat.this.minpath)) ExtrasMenu.this.treeman.copy_format = CopyFormat.MINPATH;
            }
        }
        public final class ShowDatabase implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                try{
                    JOptionPane.showMessageDialog(ExtrasMenu.this.treeman.getFrame(), //
                            new TCL(Mds.getActiveMds()).showDatabase(), //
                            Mds.getActiveMds().toString(), //
                            JOptionPane.PLAIN_MESSAGE);
                }catch(final Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        public final class ShowSubTrees implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                new SubTrees(ExtrasMenu.this.treeman, ExtrasMenu.this.treeman.getFrame());
            }
        }
        public final class ShowTags implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                final DefaultTableModel model = new DefaultTableModel(0, 2);
                final JTable table = new JTable(model);
                table.setPreferredScrollableViewportSize(new Dimension(600, 500));
                table.getColumnModel().getColumn(0).setHeaderValue("Tag");
                table.getColumnModel().getColumn(1).setHeaderValue("Full Path");
                table.getColumnModel().getColumn(0).setPreferredWidth(100);;
                table.getColumnModel().getColumn(1).setPreferredWidth(300);;
                final JScrollPane scollpane = new JScrollPane(table);
                scollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                final Thread thread = new Thread("ShowTags"){
                    {/** worker to list the tags and the full path of the target node **/
                        this.setDaemon(true);
                        this.setPriority(Thread.MIN_PRIORITY);
                    }

                    @Override
                    public final void run() {
                        final TreeView treeview = ExtrasMenu.this.treeman.getCurrentTreeView();
                        final TREE tree = treeview.getTree();
                        TagRefStatus tag = TagRefStatus.init;
                        try{
                            final String root = new StringBuilder(tree.expt.length() + 3).append("\\").append(tree.expt).append("::").toString();
                            while((tag = tree.treeFindTagWild("***", tag)).ok()){
                                model.addRow(new String[]{tag.data.replace(root, "\\"), new Nid(tag.nid).toString()});
                                synchronized(this){
                                    if(this.isInterrupted()) return;
                                }
                            }
                        }catch(final MdsException e){}
                    }
                };
                thread.start();
                JOptionPane.showMessageDialog(ExtrasMenu.this.treeman.getFrame(), //
                        scollpane, //
                        ExtrasMenu.this.treeman.getCurrentTree().toString(), //
                        JOptionPane.PLAIN_MESSAGE);
                if(thread.isAlive()) thread.interrupt();
            }
        }

        public ExtrasMenu(final TreeManager treeman, final JComponent menu){
            super(treeman);
            menu.add(new CopyFormat());
            menu.add(this.addMenuItem("Show DataBase", new ShowDatabase()));
            menu.add(this.addMenuItem("Show SubTrees", new ShowSubTrees()));
            menu.add(this.addMenuItem("Show Tags", new ShowTags()));
        }

        @Override
        public final void checkSupport() {
            final boolean open = this.treeman.getCurrentTree() != null;
            for(final JMenuItem item : this.items)
                item.setEnabled(open);
        }
    }
    public static class FileMenu extends Menu{
        private final class closeMds implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileMenu.this.treeman.closeMds();
            }
        }
        private final class closeTree implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileMenu.this.treeman.closeTree();
            }
        } /*private final class compile implements ActionListener{
          @Override
          public void actionPerformed(final ActionEvent e) {
          FileMenu.this.treeman.compile();
          }
          }*/
        private final class decompile implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                new DecompileTree().decompile(FileMenu.this.treeman.getCurrentTree(), FileMenu.this.treeman.frame, true);
            }
        };
        private final class openMds implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileMenu.this.treeman.openmds_dialog.open();
            }
        }
        private final class openTree implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileMenu.this.treeman.opentree_dialog.open();
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
            menu.add(this.addMenuItem("Connect", new openMds()));
            menu.add(this.addMenuItem("Open", new openTree()));
            menu.add(this.addMenuItem("Write", new write()));
            menu.add(this.addMenuItem("Close", new closeTree()));
            menu.add(this.addMenuItem("Disconnect", new closeMds()));
            menu.add(this.addMenuItem("Export", new decompile()));
            /* menu.add(this.addMenuItem("Import",new compile()));*/
            menu.add(this.addMenuItem("Quit", new quit()));
        }

        @Override
        public final void checkSupport() {
            final boolean noconnected = this.treeman.getCurrentMdsView() == null;
            final boolean noopen = noconnected || this.treeman.getCurrentTree() == null;
            this.items.get(1).setEnabled(!noconnected);
            this.items.get(2).setEnabled(noopen ? false : this.treeman.getCurrentTree().isEditable());
            this.items.get(3).setEnabled(!noopen);
            this.items.get(4).setEnabled(!noconnected);
            this.items.get(5).setEnabled(!noopen);
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
        public final void mouseClicked(final MouseEvent ev) {
            final TreeView tree = (TreeView)ev.getSource();
            final DefaultMutableTreeNode tree_node = (DefaultMutableTreeNode)tree.getClosestPathForLocation(ev.getX(), ev.getY()).getLastPathComponent();
            final Node currnode = Node.getNode(tree_node);
            tree.setCurrentNode(currnode);
            if((ev.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
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
                pop.show((Component)ev.getSource(), ev.getX(), ev.getY());
            }else if((ev.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && (ev.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK)) != 0){
                if(currnode.isSubTree()) try{
                    currnode.toggleFlags(NODE.Flags.INCLUDE_IN_PULSE);
                }catch(final MdsException e){
                    MdsException.stderr("INCLUDE_IN_PULSE", e);
                }
            }
            tree.treeDidChange();
            TreeManager.this.dialogs.update();
        }
    }
    public static final class ModifyMenu extends Menu{
        public final class doAction implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                MdsException.stdout("doAction not yet implemented.");
            }
        };
        public final class modifyFlags implements ActionListener{
            @Override
            public void actionPerformed(final ActionEvent e) {
                ModifyMenu.this.treeman.dialogs.modifyFlags.open();
            }
        }
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
            menu.add(this.addMenuItem("Setup Device", new setupDevice()));
            menu.add(this.addMenuItem("Do Action", new doAction()));
        }

        @Override
        public final void checkSupport() {
            final Node node = this.treeman.getCurrentNode();
            boolean[] mask = new boolean[this.items.size()];
            if(node != null){
                final int usage = node.getUsage();
                final boolean isst = usage == NODE.USAGE_STRUCTURE || usage == NODE.USAGE_SUBTREE;
                final boolean isact = usage == NODE.USAGE_ACTION || usage == NODE.USAGE_TASK;
                final boolean isdev = usage == NODE.USAGE_DEVICE;
                mask = new boolean[]{!isst, true, true, true, isdev, isact};
            }
            for(int i = 0; i < mask.length; i++)
                this.items.get(i).setEnabled(mask[i]);
        }
    }
    static{
        ToolTipManager.sharedInstance().setDismissDelay(60000);
    }
    public int                         copy_format;
    public final Dialogs               dialogs;
    private final jTraverserFacade     frame;
    private final OpenTreeDialog       opentree_dialog;
    private final OpenConnectionDialog openmds_dialog;
    private final Stack<MdsView>       mdsviews = new Stack<MdsView>();

    public TreeManager(final jTraverserFacade frame){
        this(frame, null);
    }

    public TreeManager(final jTraverserFacade frame, final JScrollPane pane){
        this.frame = frame;
        this.setPreferredSize(new Dimension(300, 400));
        this.setBackground(Color.white);
        this.openmds_dialog = new OpenConnectionDialog(this);
        this.opentree_dialog = new OpenTreeDialog(this);
        this.dialogs = new Dialogs(this);
        this.setForeground(new Color(0, 0, 127));
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(final ChangeEvent ce) {
                final TREE tree = TreeManager.this.getCurrentTree();
                if(tree == null) return;
                try{
                    tree.setActive();
                }catch(final MdsException e){}
                TreeManager.this.reportChange();
            }
        });
    }

    public final void closeMds() {
        final MdsView mdsview = this.getCurrentMdsView();
        if(mdsview == null) return;
        this.closeMds(this.getSelectedIndex());
    }

    public final void closeMds(final int idx) {
        if(idx >= this.getTabCount() || idx < 0) return;
        this.mdsviews.remove(this.getMdsViewAt(idx).close(false));
        this.removeTabAt(idx);
        if(this.getTabCount() == 0) this.frame.reportChange(null);
    }

    public final void closeTree() {
        final MdsView mdsview = this.getCurrentMdsView();
        if(mdsview == null) return;
        mdsview.closeTree(false);
    }

    public final Point dialogLocation() {
        return new Point(this.frame.getLocation().x + 32, this.frame.getLocation().y + 32);
    }

    public final MouseListener getContextMenu() {
        return new TreeManager.mlContextMenu();
    }

    public final MdsView getCurrentMdsView() {
        if(this.getTabCount() == 0) return null;
        return (MdsView)this.getSelectedComponent();
    }

    public final Node getCurrentNode() {
        final MdsView mdsview = this.getCurrentMdsView();
        if(mdsview == null) return null;
        return mdsview.getCurrentNode();
    }

    public final TREE getCurrentTree() {
        final MdsView mdsview = this.getCurrentMdsView();
        if(mdsview == null) return null;
        return mdsview.getCurrentTree();
    }

    public TreeView getCurrentTreeView() {
        final MdsView mdsview = this.getCurrentMdsView();
        if(mdsview == null) return null;
        return mdsview.getCurrentTreeView();
    }

    public final Frame getFrame() {
        return this.frame;
    }

    private final MdsView getMdsViewAt(final int index) {
        return (MdsView)this.getComponentAt(index);
    }

    public MdsView openMds(final Provider provider) {
        this.openmds_dialog.setFields(provider.toString());
        // first we need to check if the tree is already open
        final Mds mds = Connection.sharedConnection(provider);
        if(mds == null){
            JOptionPane.showMessageDialog(this.frame, "Could not connect to server " + provider.toString(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        for(int i = 0; i < this.getTabCount(); i++){
            final MdsView mdsview = (MdsView)this.getComponentAt(i);
            if(mdsview.getMds().equals(mds)){
                this.setSelectedComponent(mdsview);
                return mdsview;
            }
        }
        final MdsView mdsview = new MdsView(this, mds);
        this.addTab(mdsview.toString(), mdsview);
        this.setSelectedIndex(this.getTabCount() - 1);
        return mdsview;
    }

    public final void openTree(final String expt, final int shot, final int mode) {
        this.opentree_dialog.setFields(expt, shot);
        final MdsView mdsview = this.getCurrentMdsView();
        if(mdsview == null) return;
        mdsview.openTree(expt, shot, mode);
    }

    public final void quit() {
        while(!this.mdsviews.empty())
            this.mdsviews.pop().close(true);
        System.exit(0);
    }

    synchronized public final void reportChange() {
        this.dialogs.update();
        this.frame.reportChange(this.getCurrentMdsView());
        this.frame.repaint();
    }

    public final void write() {
        this.getCurrentTreeView().write();
    }
}

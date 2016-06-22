package jtraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import jtraverser.Node;
import jtraverser.NodeInfo;
import jtraverser.Tree;
import jtraverser.TreeManager;
import mds.MdsException;
import mds.data.descriptor_s.Nid;

@SuppressWarnings("serial")
public class SubTrees extends JDialog{
    public class CheckBoxList extends JList<JCheckBox>{
        protected class CellRenderer implements ListCellRenderer{
            @Override
            public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
                final JCheckBox checkbox = (JCheckBox)value;
                checkbox.setBackground(isSelected ? CheckBoxList.this.getSelectionBackground() : CheckBoxList.this.getBackground());
                checkbox.setForeground(isSelected ? CheckBoxList.this.getSelectionForeground() : CheckBoxList.this.getForeground());
                checkbox.setEnabled(CheckBoxList.this.isEnabled());
                checkbox.setFont(CheckBoxList.this.getFont());
                checkbox.setFocusPainted(false);
                checkbox.setBorderPainted(true);
                checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : new EmptyBorder(1, 1, 1, 1));
                return checkbox;
            }
        }

        @SuppressWarnings("unchecked")
        public CheckBoxList(){
            this.setCellRenderer(new CellRenderer());
            this.setVisibleRowCount(16);
            this.addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(final MouseEvent e) {
                    final int index = CheckBoxList.this.locationToIndex(e.getPoint());
                    if(index != -1){
                        if((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0){
                            final JCheckBox checkbox = CheckBoxList.this.getModel().getElementAt(index);
                            checkbox.setSelected(!checkbox.isSelected());
                        }else if((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
                            CheckBoxList.this.setRange(index);
                        }else return;
                        CheckBoxList.this.repaint();
                    }
                }
            });
            this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        private void setRange(final int index) {
            final int selected = CheckBoxList.this.getSelectedIndex();
            if(selected < 0) return;
            final boolean state = CheckBoxList.this.getModel().getElementAt(selected).isSelected();
            final int start, stop;
            if(index < selected){
                start = index;
                stop = selected;
            }else{
                start = selected + 1;
                stop = index + 1;
            }
            for(int i = start; i < stop; i++)
                CheckBoxList.this.getModel().getElementAt(i).setSelected(state);
        }
    }
    private final class CheckBoxListener implements ActionListener{
        @Override
        public void actionPerformed(final ActionEvent ce) {}
    }
    private final JButton      close_b  = new JButton("Close");
    private final CheckBoxList subtrees = new CheckBoxList();
    private final TreeManager  treeman;
    private final JButton      apply_b  = new JButton("Apply");
    private final JButton      update_b = new JButton("Refresh");
    private JCheckBox[]        checkBoxes;

    public SubTrees(final TreeManager treeman){
        this(treeman, treeman.getFrame());
    }

    public SubTrees(final TreeManager treeman, final Frame frame){
        super(frame, "SubTrees include in Pulse");
        this.close_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent ae) {
                SubTrees.this.dispose();
            }
        });
        this.update_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent ae) {
                SubTrees.this.update();
            }
        });
        this.apply_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent ae) {
                SubTrees.this.apply();
            }
        });
        this.treeman = treeman;
        this.setLocationRelativeTo(treeman);
        final JScrollPane sp = new JScrollPane();
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setViewportView(this.subtrees);
        this.add(sp);
        final JPanel buttons = new JPanel(new GridLayout(1, 3));
        buttons.add(this.apply_b);
        buttons.add(this.update_b);
        buttons.add(this.close_b);
        this.add(buttons, BorderLayout.SOUTH);
        this.updateTree();
        this.pack();
        this.setVisible(true);
    }

    private void apply() {
        for(final JCheckBox checkbox : this.checkBoxes){
            final Nid nid = (Nid)checkbox.getClientProperty("nid");
            final Tree tree = SubTrees.this.treeman.getCurrentTree();
            try{
                if(checkbox.isSelected()) SubTrees.this.treeman.getCurrentDatabase().setFlags(nid, NodeInfo.INCLUDE_IN_PULSE);
                else SubTrees.this.treeman.getCurrentDatabase().clearFlags(nid, NodeInfo.INCLUDE_IN_PULSE);
                ((Node)tree.findPath((String)checkbox.getClientProperty("fullpath")).getUserObject()).getFlags();
            }catch(final MdsException me){}
        }
        SubTrees.this.treeman.reportChange();
        this.update();
    }

    public final void update() {
        final Tree tree = this.treeman.getCurrentTree();
        for(final JCheckBox cb : this.checkBoxes){
            final Nid nid = (Nid)cb.getClientProperty("nid");
            try{
                cb.setSelected((tree.getDatabase().getFlags(nid) & NodeInfo.INCLUDE_IN_PULSE) != 0);
                cb.setEnabled(true);
            }catch(final MdsException e){
                cb.setEnabled(false);
            }
        }
        this.repaint();
    }

    public final void updateTree() {
        final Tree tree = this.treeman.getCurrentTree();
        final Nid[] nids = tree.getSubTrees();
        this.checkBoxes = new JCheckBox[nids.length];
        JCheckBox cb;
        for(int i = 0; i < nids.length; i++){
            final Nid nid = nids[i];
            final String fullpath = nid.getFullPath();
            this.checkBoxes[i] = cb = new JCheckBox(nid.toString());
            cb.putClientProperty("nid", nid);
            cb.putClientProperty("fullpath", fullpath);
            cb.addActionListener(new CheckBoxListener());
        }
        this.subtrees.setListData(this.checkBoxes);
        this.update();
    }
}

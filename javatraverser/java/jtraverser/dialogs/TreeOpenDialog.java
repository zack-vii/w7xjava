package jtraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import jtraverser.TreeManager;
import jtraverser.jTraverserFacade;
import mds.MdsException;
import mds.TREE;
import mds.data.descriptor_s.CString;
import mds.mdsip.Connection;

@SuppressWarnings("serial")
public class TreeOpenDialog extends JDialog{
    private final JRadioButton readonly, edit, normal;
    private final JTextField   provider, expt;
    JComboBox<String>          shot;
    private final TreeManager  treeman;

    /**
     * Create the dialog.
     */
    public TreeOpenDialog(final TreeManager treeman){
        this(treeman, treeman.getFrame());
    }

    public TreeOpenDialog(final TreeManager treeman, final Frame frame){
        super(frame);
        this.treeman = treeman;
        this.setTitle("Open new tree");
        final JPanel mjp = new JPanel();
        mjp.setLayout(new BorderLayout());
        final JPanel grid = new JPanel();
        grid.setBorder(new EmptyBorder(5, 5, 5, 5));
        grid.setLayout(new GridLayout(3, 2){
            @Override
            public void layoutContainer(final Container parent) {
                synchronized(parent.getTreeLock()){
                    final Insets insets = parent.getInsets();
                    final int ncomponents = parent.getComponentCount();
                    int nrows = this.getRows();
                    int ncols = this.getColumns();
                    if(ncomponents == 0){ return; }
                    if(nrows > 0) ncols = (ncomponents + nrows - 1) / nrows;
                    else nrows = (ncomponents + ncols - 1) / ncols;
                    final int hgap = this.getHgap();
                    final int vgap = this.getVgap();
                    // scaling factors
                    final Dimension pd = this.preferredLayoutSize(parent);
                    final double sw = (1.0 * parent.getWidth()) / pd.width;
                    final double sh = (1.0 * parent.getHeight()) / pd.height;
                    // scale
                    final int[] w = new int[ncols];
                    final int[] h = new int[nrows];
                    for(int i = 0; i < ncomponents; i++){
                        final int r = i / ncols;
                        final int c = i % ncols;
                        final Component comp = parent.getComponent(i);
                        final Dimension d = comp.getPreferredSize();
                        d.width = (int)(sw * d.width);
                        d.height = (int)(sh * d.height);
                        if(w[c] < d.width){
                            w[c] = d.width;
                        }
                        if(h[r] < d.height){
                            h[r] = d.height;
                        }
                    }
                    for(int c = 0, x = insets.left; c < ncols; c++){
                        for(int r = 0, y = insets.top; r < nrows; r++){
                            final int i = r * ncols + c;
                            if(i < ncomponents){
                                parent.getComponent(i).setBounds(x, y, w[c], h[r]);
                            }
                            y += h[r] + vgap;
                        }
                        x += w[c] + hgap;
                    }
                }
            }

            @Override
            public Dimension preferredLayoutSize(final Container parent) {
                synchronized(parent.getTreeLock()){
                    final Insets insets = parent.getInsets();
                    final int ncomponents = parent.getComponentCount();
                    int nrows = this.getRows();
                    int ncols = this.getColumns();
                    if(nrows > 0){
                        ncols = (ncomponents + nrows - 1) / nrows;
                    }else{
                        nrows = (ncomponents + ncols - 1) / ncols;
                    }
                    final int[] w = new int[ncols];
                    final int[] h = new int[nrows];
                    for(int i = 0; i < ncomponents; i++){
                        final int r = i / ncols;
                        final int c = i % ncols;
                        final Component comp = parent.getComponent(i);
                        final Dimension d = comp.getPreferredSize();
                        if(w[c] < d.width){
                            w[c] = d.width;
                        }
                        if(h[r] < d.height){
                            h[r] = d.height;
                        }
                    }
                    int nw = 0;
                    for(int j = 0; j < ncols; j++){
                        nw += w[j];
                    }
                    int nh = 0;
                    for(int i = 0; i < nrows; i++){
                        nh += h[i];
                    }
                    return new Dimension(insets.left + insets.right + nw + (ncols - 1) * this.getHgap(), insets.top + insets.bottom + nh + (nrows - 1) * this.getVgap());
                }
            }
        });
        grid.add(new JLabel("server: "));
        grid.add(this.provider = new JTextField(16));
        grid.add(new JLabel("expt: "));
        grid.add(this.expt = new JTextField(16));
        grid.add(new JLabel("shot: "));
        grid.add(this.shot = new JComboBox<String>());
        this.shot.setEditable(true);
        this.shot.addPopupMenuListener(new PopupMenuListener(){
            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                TreeOpenDialog.this.shot.removeAllItems();
                TreeOpenDialog.this.shot.addItem("model");
                final Connection mds = Connection.sharedConnection(TreeOpenDialog.this.provider.getText());
                final boolean wasconnected = mds.isConnected();
                if(!wasconnected) mds.connect();
                try{
                    int[] shots;
                    try{
                        shots = mds.getIntegerArray("getShotDB($)", new CString(TreeOpenDialog.this.expt.getText()));
                    }catch(final MdsException exc){
                        jTraverserFacade.stderr("getShotDB", exc);
                        shots = new int[0];
                    }
                    for(final int shot : shots)
                        TreeOpenDialog.this.shot.addItem(Integer.toString(shot));
                }finally{
                    if(!wasconnected) mds.disconnect();
                }
            }
        });
        mjp.add(grid, BorderLayout.NORTH);
        final JPanel access = new JPanel(new GridLayout(1, 3));
        access.add(this.readonly = new JRadioButton("readonly"));
        access.add(this.normal = new JRadioButton("normal"));
        access.add(this.edit = new JRadioButton("edit/new"));
        final ButtonGroup bgMode = new ButtonGroup();
        bgMode.add(this.readonly);
        bgMode.add(this.normal);
        bgMode.add(this.edit);
        this.readonly.setSelected(true);
        mjp.add(access, "Center");
        final JPanel buttons = new JPanel();
        JButton but;
        buttons.add(but = new JButton("Ok"));
        but.setSelected(true);
        but.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                TreeOpenDialog.this.ok();
            }
        });
        buttons.add(but = new JButton("Cancel"));
        but.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                TreeOpenDialog.this.setVisible(false);
            }
        });
        mjp.add(buttons, "South");
        this.getContentPane().add(mjp);
        this.shot.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(final KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) TreeOpenDialog.this.ok();
            }

            @Override
            public void keyReleased(final KeyEvent e) {}

            @Override
            public void keyTyped(final KeyEvent e) {}
        });
        this.pack();
        this.setResizable(false);
    }

    void ok() {
        final String provider = this.provider.getText().trim(), exp = this.expt.getText().trim(), shot_str = ((String)this.shot.getSelectedItem()).trim();
        if(exp == null || exp.length() == 0){
            JOptionPane.showMessageDialog(this, "Missing experiment name", "Error opening tree", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int shot;
        if(shot_str == null || shot_str.length() == 0 || shot_str.equalsIgnoreCase("model")) shot = -1;
        else try{
            shot = Integer.parseInt(shot_str);
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this, "Wrong shot number", "Error opening tree", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(this.edit.isSelected() && this.readonly.isSelected()){
            JOptionPane.showMessageDialog(this, "Tree cannot be open in both edit and readonly mode", "Error opening tree", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.setVisible(false);
        final int mode;
        if(this.edit.isSelected()) mode = TREE.EDITABLE;
        else if(this.readonly.isSelected()) mode = TREE.READONLY;
        else mode = TREE.NORMAL;
        if(this.treeman != null) this.treeman.openTree(provider, exp, shot, mode);
    }

    public final void open() {
        if(this.treeman != null) this.setLocation(this.treeman.dialogLocation());
        this.readonly.setSelected(true);
        this.setVisible(true);
    }

    public final void setFields(final String provider, final String expt, final long shot) {
        this.provider.setText(provider);
        this.expt.setText(expt);
        this.shot.setSelectedItem(Long.toString(shot));
    }
}

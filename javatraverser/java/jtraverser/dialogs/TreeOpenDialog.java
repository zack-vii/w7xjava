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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import jtraverser.TreeManager;
import mds.Database;

@SuppressWarnings("serial")
public class TreeOpenDialog extends JDialog{
    /*
    public static void main(final String[] args) {// TODO:main
        try{
            final TreeOpenDialog dialog = new TreeOpenDialog(null, null);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        }catch(final Exception e){
            e.printStackTrace();
        }
    }
    */
    private final JRadioButton open_readonly, open_edit, open_normal;
    private final JTextField   provider, open_shot, open_expt;
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
        grid.add(this.open_expt = new JTextField(16));
        grid.add(new JLabel("shot: "));
        grid.add(this.open_shot = new JTextField(16));
        mjp.add(grid, BorderLayout.NORTH);
        final JPanel access = new JPanel(new GridLayout(1, 3));
        access.add(this.open_readonly = new JRadioButton("readonly"));
        access.add(this.open_normal = new JRadioButton("normal"));
        access.add(this.open_edit = new JRadioButton("edit/new"));
        final ButtonGroup bgMode = new ButtonGroup();
        bgMode.add(this.open_readonly);
        bgMode.add(this.open_normal);
        bgMode.add(this.open_edit);
        this.open_readonly.setSelected(true);
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
        this.open_shot.addKeyListener(new KeyListener(){
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
        final String provider = this.provider.getText().trim(), exp = this.open_expt.getText().trim(), shot_str = this.open_shot.getText().trim();
        if(exp == null || exp.length() == 0){
            JOptionPane.showMessageDialog(this, "Missing experiment name", "Error opening tree", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int shot;
        if(shot_str == null || shot_str.length() == 0){
            JOptionPane.showMessageDialog(this, "Wrong shot number", "Error opening tree", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try{
            shot = Integer.parseInt(shot_str);
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this, "Wrong shot number", "Error opening tree", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(this.open_edit.isSelected() && this.open_readonly.isSelected()){
            JOptionPane.showMessageDialog(this, "Tree cannot be open in both edit and readonly mode", "Error opening tree", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.setVisible(false);
        final int mode;
        if(this.open_edit.isSelected()) mode = Database.EDITABLE;
        else if(this.open_readonly.isSelected()) mode = Database.READONLY;
        else mode = Database.NORMAL;
        if(this.treeman != null) this.treeman.openTree(provider, exp, shot, mode);
    }

    public final void open() {
        if(this.treeman != null) this.setLocation(this.treeman.dialogLocation());
        this.open_readonly.setSelected(true);
        this.setVisible(true);
    }

    public final void setFields(final String provider, final String expt, final long shot) {
        this.provider.setText(provider);
        this.open_expt.setText(expt);
        this.open_shot.setText(Long.toString(shot));
    }
}

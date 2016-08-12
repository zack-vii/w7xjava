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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import jtraverser.TreeManager;
import mds.mdsip.Connection.Provider;

@SuppressWarnings("serial")
public class OpenConnectionDialog extends JDialog{
    private final JPasswordField pass;
    private final JTextField     provider;
    private final TreeManager    treeman;

    /**
     * Create the dialog.
     */
    public OpenConnectionDialog(final TreeManager treeman){
        this(treeman, treeman.getFrame());
    }

    public OpenConnectionDialog(final TreeManager treeman, final Frame frame){
        super(frame);
        this.treeman = treeman;
        this.setTitle("Open new connection");
        final JPanel mjp = new JPanel();
        mjp.setLayout(new BorderLayout());
        final JPanel grid = new JPanel();
        grid.setBorder(new EmptyBorder(5, 5, 5, 5));
        grid.setLayout(new GridLayout(2, 2){
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
        grid.add(new JLabel("password: "));
        grid.add(this.pass = new JPasswordField(16));
        mjp.add(grid, BorderLayout.NORTH);
        final JPanel buttons = new JPanel();
        JButton but;
        buttons.add(but = new JButton("Ok"));
        but.setSelected(true);
        but.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                OpenConnectionDialog.this.ok();
            }
        });
        buttons.add(but = new JButton("Cancel"));
        but.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                OpenConnectionDialog.this.setVisible(false);
            }
        });
        mjp.add(buttons, "South");
        this.getContentPane().add(mjp);
        this.pack();
        this.setResizable(false);
    }

    private Provider getProvider() {
        return new Provider(this.provider.getText().trim(), this.pass.getPassword().length == 0 ? null : new String(this.pass.getPassword()));
    }

    void ok() {
        this.setVisible(false);
        if(this.treeman != null) this.treeman.openMds(this.getProvider());
    }

    public final void open() {
        if(this.treeman != null){
            this.setLocation(this.treeman.dialogLocation());
            /*final MdsView mdsview = this.treeman.getCurrentMdsView();
            if(mdsview != null){
                final Mds mds = mdsview.getMds();
                if(mds instanceof Connection) this.provider.setText(((Connection)mds).getProvider().toString());
                else this.provider.setText("");
            }*/
        }
        this.setVisible(true);
    }

    public final void setFields(final String provider) {
        this.provider.setText(provider);
        this.pass.setText("");
    }
}

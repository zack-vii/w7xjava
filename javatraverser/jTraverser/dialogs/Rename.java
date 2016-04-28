package jTraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import jTraverser.Node;
import jTraverser.TreeManager;

@SuppressWarnings("serial")
public final class Rename extends JDialog{
    private Node              currnode;
    private final JTextField  new_name;
    private final TreeManager treeman;

    public Rename(final TreeManager treeman){
        super(treeman.getFrame());
        this.treeman = treeman;
        final JPanel mjp = new JPanel();
        mjp.setLayout(new BorderLayout());
        JPanel jp = new JPanel();
        jp.add(new JLabel("New Name: "));
        jp.add(this.new_name = new JTextField(12));
        mjp.add(jp, "North");
        jp = new JPanel();
        final JButton ok_b = new JButton("Ok");
        ok_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                Rename.this.commit();
            }
        });
        jp.add(ok_b);
        final JButton cancel_b = new JButton("Cancel");
        cancel_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                Rename.this.setVisible(false);
            }
        });
        jp.add(cancel_b);
        mjp.add(jp, "South");
        this.getContentPane().add(mjp);
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(final KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) Rename.this.commit();
            }
        });
        this.pack();
        this.setResizable(false);
    }

    public final void close() {
        this.setVisible(false);
    }

    private final void commit() {
        if(this.currnode == null) return;
        final String name = this.new_name.getText();
        if(name == null || name.length() == 0) return;
        try{
            this.currnode.rename(name);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this.treeman.getFrame(), exc.getMessage(), "Error renaming Node", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.treeman.reportChange();
        this.close();
    }

    public final void open(final Node currnode) {
        this.currnode = currnode;
        if(currnode == null) return;
        this.setTitle("Rename node " + currnode.getFullPath());
        this.setLocation(this.treeman.dialogLocation());
        this.new_name.setText("");
        this.setVisible(true);
    }

    public final void update() {// don't update; close instead
        this.close();
    }
}

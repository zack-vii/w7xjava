package jTraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import jTraverser.Node;
import jTraverser.NodeInfo;
import jTraverser.TreeManager;
import jTraverser.jTraverserFacade;

public final class AddNode extends JDialog{
    private static final long serialVersionUID = -6368448328454620096L;
    private Node              currnode;
    private final JTextField  e_name, e_tag;
    private final TreeManager treeman;
    private int               usage;

    public AddNode(final TreeManager treeman){
        super(treeman.getFrame());
        this.treeman = treeman;
        final JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        this.getContentPane().add(jp);
        JPanel jp1 = new JPanel();
        jp1.add(new JLabel("Node name: "));
        jp1.add(this.e_name = new JTextField(12));
        jp.add(jp1, BorderLayout.NORTH);
        jp1 = new JPanel();
        jp1.add(new JLabel("Node tag: "));
        jp1.add(this.e_tag = new JTextField(12));
        jp.add(jp1, BorderLayout.CENTER);
        jp1 = new JPanel();
        JButton b;
        jp1.add(b = new JButton("Ok"));
        b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                AddNode.this.addNode();
            }
        });
        jp1.add(b = new JButton("Cancel"));
        b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                AddNode.this.setVisible(false);
            }
        });
        jp.add(jp1, BorderLayout.SOUTH);
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(final KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) AddNode.this.addNode();
            }
        });
        this.pack();
    }

    public final void addNode() {
        try{
            final Node newNode = this.currnode.addNode(this.e_name.getText().toUpperCase(), this.usage);
            if(!this.e_tag.getText().trim().equals("")){
                try{
                    newNode.setTags(new String[]{this.e_tag.getText().trim().toUpperCase()});
                }catch(final Exception exc){
                    jTraverserFacade.stderr("Error adding tag", exc);
                }
            }
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error adding node", exc);
        }
        this.close();
    }

    public final void close() {
        this.currnode = null;
        this.setVisible(false);
    }

    public final void open(final Node currnode, final int usage) {
        this.currnode = currnode;
        if(this.currnode == null) return;
        this.usage = usage;
        this.e_name.setText("");
        this.e_tag.setText("");
        this.e_tag.setVisible(usage != NodeInfo.USAGE_SUBTREE);
        this.setTitle("Add to: " + this.currnode.getFullPath());
        this.setLocation(this.treeman.dialogLocation());
        this.setVisible(true);
    }
}
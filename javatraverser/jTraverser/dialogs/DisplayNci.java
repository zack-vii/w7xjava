package jTraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jTraverser.Node;
import jTraverser.jTraverserFacade;
import jTraverser.editor.NodeEditor;

public class DisplayNci extends NodeEditor implements ActionListener{
    private static final long serialVersionUID = -623170674315001785L;
    JLabel                    label;

    public DisplayNci(){
        this.setLayout(new BorderLayout());
        JPanel jp;
        this.add(jp = new JPanel(), BorderLayout.NORTH);
        jp.setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 0;
        jp.add(this.label = new JLabel(""), c);
        this.add(jp = new JPanel(), BorderLayout.SOUTH);
        c.gridy = 1;
        JButton close;
        jp.add(close = new JButton("Close"), c);
        close.addActionListener(this);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        this.frame.dispose();
    }

    @Override
    public void setNode(final Node node) {
        this.node = node;
        this.frame.setTitle("Display Nci information");
        try{
            this.label.setText(node.getInfo().toString());
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error retieving Nci", exc);
        }
    }
}
package jtraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jtraverser.Node;
import jtraverser.editor.NodeEditor;
import mds.MdsException;

@SuppressWarnings("serial")
public class DisplayNci extends NodeEditor implements ActionListener{
    JLabel label;

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
            this.label.setText(node.getInfoTextBox());
        }catch(final Exception exc){
            MdsException.stderr("Error retieving Nci", exc);
        }
    }
}
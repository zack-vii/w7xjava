package jTraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jTraverser.Node;
import jTraverser.editor.NodeEditor;

@SuppressWarnings("serial")
public class DisplayTags extends NodeEditor implements ActionListener{
    JLabel tagsLabel;
    JPanel tagsPanel;

    public DisplayTags(){
        this.setLayout(new BorderLayout());
        this.tagsPanel = new JPanel();
        this.tagsPanel.add(this.tagsLabel = new JLabel());
        this.add(this.tagsPanel, BorderLayout.CENTER);
        final JPanel jp1 = new JPanel();
        final JButton cancel = new JButton("Close");
        jp1.add(cancel);
        this.add(jp1, BorderLayout.SOUTH);
        cancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        this.frame.dispose();
    }

    @Override
    public void setNode(final Node _node) {
        String tags[] = new String[0];
        this.node = _node;
        this.frame.setTitle("Display Node Tags");
        try{
            tags = this.node.getTags();
        }catch(final Exception e){
            System.out.println("Error retieving Tags");
            return;
        }
        String tagNames = "";
        if(tags == null || tags.length == 0) tagNames = "No Tags";
        else{
            final String text;
            if(tags.length > 32) text = String.join("<br>", Arrays.copyOfRange(tags, 0, 32)) + "<br>...";
            else text = String.join("<br>", tags);
            tagNames = new StringBuilder("<html>").append(text).append("</html>").toString();
        }
        this.tagsLabel.setText(tagNames);
    }
}
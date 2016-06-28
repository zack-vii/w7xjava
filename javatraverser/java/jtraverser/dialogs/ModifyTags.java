package jtraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jtraverser.Node;
import jtraverser.TreeManager;
import jtraverser.jTraverserFacade;

@SuppressWarnings("serial")
public final class ModifyTags extends JDialog{
    private final JTextField               curr_tag_selection;;
    private final DefaultListModel<String> curr_taglist_model;
    private Node                           currnode;
    private final JList<String>            modify_tags_list;
    private String[]                       tags;
    private final TreeManager              treeman;

    public ModifyTags(final TreeManager treeman){
        super(treeman.getFrame());
        this.treeman = treeman;
        final JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        final JPanel jp1 = new JPanel();
        jp1.setLayout(new BorderLayout());
        this.modify_tags_list = new JList<String>();
        this.curr_taglist_model = new DefaultListModel<String>();
        this.modify_tags_list.addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                final int idx = ModifyTags.this.modify_tags_list.getSelectedIndex();
                if(idx != -1) ModifyTags.this.curr_tag_selection.setText(ModifyTags.this.curr_taglist_model.getElementAt(idx));
            }
        });
        final JScrollPane scroll_list = new JScrollPane(this);
        jp1.add(new JLabel("Tag List:"), BorderLayout.NORTH);
        jp1.add(scroll_list, BorderLayout.CENTER);
        final JPanel jp2 = new JPanel();
        jp2.setLayout(new GridLayout(2, 1));
        final JButton add_tag = new JButton("Add Tag");
        add_tag.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String curr_tag = ModifyTags.this.curr_tag_selection.getText().toUpperCase();
                if(curr_tag == null || curr_tag.length() == 0) return;
                for(int i = 0; i < ModifyTags.this.curr_taglist_model.getSize(); i++)
                    if(curr_tag.equals(ModifyTags.this.curr_taglist_model.getElementAt(i))) return;
                ModifyTags.this.curr_taglist_model.addElement(curr_tag);
            }
        });
        jp2.add(add_tag);
        final JButton remove_tag = new JButton("Remove Tag");
        remove_tag.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                int idx;
                if((idx = ModifyTags.this.modify_tags_list.getSelectedIndex()) != -1){
                    jtraverser.dialogs.ModifyTags.this.curr_taglist_model.removeElementAt(idx);
                }
            }
        });
        jp2.add(remove_tag);
        final JPanel jp4 = new JPanel();
        jp4.add(jp2);
        jp1.add(jp4, BorderLayout.EAST);
        this.curr_tag_selection = new JTextField(30);
        final JPanel jp5 = new JPanel();
        jp5.add(new JLabel("Current Selection: "));
        jp5.add(this.curr_tag_selection);
        jp1.add(jp5, BorderLayout.SOUTH);
        jp.add(jp1, BorderLayout.NORTH);
        final JPanel jp3 = new JPanel();
        final JButton ok_b = new JButton("Ok");
        ok_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ModifyTags.this.addTag();
            }
        });
        jp3.add(ok_b);
        final JButton reset_b = new JButton("Reset");
        reset_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ModifyTags.this.curr_taglist_model.clear();
                for(final String tag : ModifyTags.this.tags)
                    ModifyTags.this.curr_taglist_model.addElement(tag);
                ModifyTags.this.modify_tags_list.setModel(ModifyTags.this.curr_taglist_model);
            }
        });
        jp3.add(reset_b);
        final JButton cancel_b = new JButton("Cancel");
        cancel_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ModifyTags.this.setVisible(false);
            }
        });
        jp3.add(cancel_b);
        jp.add(jp3, BorderLayout.SOUTH);
        this.getContentPane().add(jp);
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(final KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) ModifyTags.this.addTag();
            }
        });
        this.pack();
    }

    public final void addTag() {
        final String[] out_tags = new String[this.curr_taglist_model.getSize()];
        for(int i = 0; i < this.curr_taglist_model.getSize(); i++){
            out_tags[i] = this.curr_taglist_model.getElementAt(i);
        }
        try{
            this.currnode.setTags(out_tags);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this.treeman.getFrame(), exc.getMessage(), "Error adding tags", JOptionPane.WARNING_MESSAGE);
        }
        this.currnode = null;
        this.setVisible(false);
    }

    public final void open(final Node currnode) {
        this.currnode = currnode;
        if(currnode == null) return;
        final String[] tags;
        {
            String[] tmptags;
            try{
                tmptags = currnode.getTags();
            }catch(final Exception exc){
                jTraverserFacade.stderr("Error getting tags", exc);
                tmptags = new String[0];
            }
            tags = tmptags;
        }
        this.curr_taglist_model.clear();
        for(final String tag : tags){
            this.curr_taglist_model.addElement(tag);
        }
        this.setTitle("Modify tags of " + currnode.getFullPath());
        this.modify_tags_list.setModel(this.curr_taglist_model);
        this.curr_tag_selection.setText("");
        this.setLocation(this.treeman.dialogLocation());
        this.setVisible(true);
    }
}

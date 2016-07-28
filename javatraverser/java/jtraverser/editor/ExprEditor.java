package jtraverser.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.CString;
import mds.mdsip.Connection;

@SuppressWarnings("serial")
public class ExprEditor extends JPanel implements Editor{
    protected final class PopupAdapter extends MouseAdapter{
        JPopupMenu pop;

        @Override
        public final void mouseClicked(final MouseEvent e) {
            if(ExprEditor.this.quotes_added) return;
            if((e.getModifiers() & InputEvent.BUTTON3_MASK) == 0) return;
            if(!(e.getSource() instanceof JTextComponent)) return;
            final String expr = ((JTextComponent)e.getSource()).getText();
            if(expr == null || expr.length() == 0) return;
            if(this.pop == null){
                this.pop = new JPopupMenu();
                final JMenuItem eval = new JMenuItem("Evaluate");
                this.pop.add(eval);
                eval.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        PopupAdapter.this.pop.setVisible(false);
                        String eval;
                        try{
                            final Descriptor data = Connection.getActiveConnection().getDescriptor(expr);
                            if(data == null) eval = "no data";
                            else eval = data.toString();
                        }catch(final MdsException de){
                            eval = de.getMessage();
                        }
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(eval), null);
                        JOptionPane.showMessageDialog(ExprEditor.this, "<html><body><p style='width: 360px;'>" + eval + "</p></body></html>", "Evaluated Data", JOptionPane.PLAIN_MESSAGE);
                    }
                });
            }
            this.pop.show((JTextComponent)e.getSource(), e.getX(), e.getY());
        }
    }
    Descriptor               data;
    boolean                  default_scroll;
    boolean                  default_to_string;
    boolean                  editable = false;
    String                   expr;
    private final JButton    left, right;
    private final JPanel     pl, pr;
    boolean                  quotes_added;
    int                      rows, columns;
    private final JTextArea  text_area;
    private final JTextField text_field;

    public ExprEditor(final boolean default_to_string){
        this(null, default_to_string, 1, 32);
    }

    public ExprEditor(final Descriptor descriptor, final boolean default_to_string){
        this(descriptor, default_to_string, 1, 32);
    }

    public ExprEditor(final Descriptor descriptor, final boolean default_to_string, final int rows, final int columns){
        final boolean quotes_needed;
        final JScrollPane scroll_pane;
        this.rows = rows;
        this.columns = columns;
        this.default_to_string = default_to_string;
        if(rows > 1) this.default_scroll = true;
        if(descriptor == null) this.expr = null;
        else this.expr = descriptor.decompileX();
        quotes_needed = (default_to_string && (this.expr == null || this.expr.charAt(0) == '\"'));
        if(quotes_needed){
            this.quotes_added = true;
            this.left = new JButton("\"");
            this.right = new JButton("\"");
            this.left.setMargin(new Insets(0, 0, 0, 0));
            this.right.setMargin(new Insets(0, 0, 0, 0));
            final ActionListener leftright = new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if(ExprEditor.this.editable){
                        ExprEditor.this.quotes_added = false;
                        if(ExprEditor.this.default_scroll){
                            ExprEditor.this.remove(ExprEditor.this.pl);
                            ExprEditor.this.remove(ExprEditor.this.pr);
                        }else{
                            ExprEditor.this.remove(ExprEditor.this.left);
                            ExprEditor.this.remove(ExprEditor.this.right);
                        }
                        if(ExprEditor.this.default_scroll) ExprEditor.this.expr = ExprEditor.this.text_area.getText();
                        else ExprEditor.this.expr = ExprEditor.this.text_field.getText();
                        ExprEditor.this.expr = "\"" + ExprEditor.this.expr + "\"";
                        if(ExprEditor.this.default_scroll) ExprEditor.this.text_area.setText(ExprEditor.this.expr);
                        else ExprEditor.this.text_field.setText(ExprEditor.this.expr);
                        ExprEditor.this.validate();
                        ExprEditor.this.repaint();
                    }
                }
            };
            this.left.addActionListener(leftright);
            this.right.addActionListener(leftright);
            if(this.expr != null) this.expr = this.expr.substring(1, this.expr.length() - 1);
        }else{
            this.right = null;
            this.left = null;
            this.quotes_added = false;
        }
        this.setLayout(new BorderLayout());
        if(this.default_scroll){
            this.text_area = new JTextArea(this.rows, this.columns);
            this.text_area.setTabSize(2);
            this.text_area.addMouseListener(new PopupAdapter());
            final Dimension d = this.text_area.getPreferredSize();
            this.text_area.setText(this.expr);
            d.height += 20;
            d.width += 20;
            scroll_pane = new JScrollPane(this.text_area);
            scroll_pane.setPreferredSize(d);
            this.text_area.setLineWrap(true);
            if(quotes_needed){
                this.pl = new JPanel();
                this.pl.setLayout(new BorderLayout());
                this.pl.add(this.left, BorderLayout.NORTH);
                this.add(this.pl, BorderLayout.LINE_START);
            }else this.pl = null;
            this.add(scroll_pane, BorderLayout.CENTER);
            if(quotes_needed){
                this.pr = new JPanel();
                this.pr.setLayout(new BorderLayout());
                this.pr.add(this.right, BorderLayout.NORTH);
                this.add(this.pr, BorderLayout.LINE_END);
            }else this.pr = null;
            this.text_field = null;
        }else{
            this.pl = this.pr = null;
            this.text_area = null;
            if(quotes_needed) this.add(this.left, BorderLayout.LINE_START);
            this.text_field = new JTextField(this.columns);
            this.text_field.addMouseListener(new PopupAdapter());
            this.text_field.setText(this.expr);
            this.add(this.text_field, BorderLayout.CENTER);
            if(quotes_needed) this.add(this.right, BorderLayout.LINE_END);
        }
    }

    @Override
    public final Descriptor getData() throws MdsException {
        if(this.default_scroll) this.expr = this.text_area.getText();
        else this.expr = this.text_field.getText().trim();
        if(this.expr.isEmpty()) return null;
        if(this.quotes_added) return new CString(this.expr);
        return Connection.getActiveConnection().getDescriptor("COMPILE($)", new CString(this.expr));
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        if(this.data == null) this.expr = "";
        else this.expr = this.data.toString();
        if(this.default_to_string){
            final int len = this.expr.length();
            if(len >= 2) this.expr = this.expr.substring(1, len - 1);
        }
        if(this.default_scroll) this.text_area.setText(this.expr);
        else this.text_field.setText(this.expr);
    }

    public final void setData(final Descriptor data) {
        this.data = data;
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        if(this.text_area != null) this.text_area.setEditable(editable);
        if(this.text_field != null) this.text_field.setEditable(editable);
    }
}

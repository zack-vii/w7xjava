package jScope;

/* $Id$ */
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

final public class SignalsBoxDialog extends JDialog{
    static final long serialVersionUID = 4362345435764563L;
    TableModel        dataModel;
    jScopeFacade      scope;
    JTable            table;

    SignalsBoxDialog(final JFrame f, final String title, final boolean modal){
        super(f, title, false);
        this.scope = (jScopeFacade)f;
        this.dataModel = new AbstractTableModel(){
            static final long serialVersionUID = 43624535345564L;

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public String getColumnName(final int col) {
                switch(col){
                    case 0:
                        return "Y expression";
                    case 1:
                        return "X expression";
                }
                return null;
            }

            @Override
            public int getRowCount() {
                return WaveInterface.sig_box.signals_name.size();
            }

            @Override
            public Object getValueAt(final int row, final int col) {
                switch(col){
                    case 0:
                        return WaveInterface.sig_box.getYexpr(row);
                    case 1:
                        return WaveInterface.sig_box.getXexpr(row);
                }
                return null;
            }
        };
        this.table = new JTable(this.dataModel);
        final JScrollPane scrollpane = new JScrollPane(this.table);
        this.getContentPane().add("Center", scrollpane);
        final JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        final JButton add = new JButton("Add");
        add.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                final int idx[] = SignalsBoxDialog.this.table.getSelectedRows();
                final String x_expr[] = new String[idx.length];
                final String y_expr[] = new String[idx.length];
                for(int i = 0; i < idx.length; i++){
                    y_expr[i] = (String)SignalsBoxDialog.this.table.getValueAt(idx[i], 0);
                    x_expr[i] = (String)SignalsBoxDialog.this.table.getValueAt(idx[i], 1);
                }
                SignalsBoxDialog.this.scope.wave_panel.AddSignals(null, null, x_expr, y_expr, true, false);
            }
        });
        p.add(add);
        /*
         * JButton remove = new JButton("Remove"); remove.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { int idx[] = table.getSelectedRows(); table.clearSelection(); for(int i = 0; i < idx.length; i++) {
         * WaveInterface.sig_box.removeExpr(idx[i]-i); } table.updateUI(); } }); p.add(remove);
         */
        final JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                SignalsBoxDialog.this.dispose();
            }
        });
        p.add(cancel);
        this.getContentPane().add("South", p);
        this.pack();
        this.setLocationRelativeTo(f);
    }
}

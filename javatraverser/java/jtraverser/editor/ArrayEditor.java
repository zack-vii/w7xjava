package jtraverser.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import jtraverser.dialogs.TreeDialog;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.Float32Array;
import mds.data.descriptor_a.NUMBERArray;
import mds.data.descriptor_s.Missing;

@SuppressWarnings("serial")
public class ArrayEditor extends JPanel implements ActionListener, Editor{
    public class RowNumberTable extends JTable implements ChangeListener, PropertyChangeListener{
        private class RowNumberRenderer extends DefaultTableCellRenderer{
            public RowNumberRenderer(){
                this.setHorizontalAlignment(JLabel.CENTER);
            }

            @Override
            public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
                if(table != null){
                    final JTableHeader header = table.getTableHeader();
                    if(header != null){
                        RowNumberTable.this.setForeground(header.getForeground());
                        RowNumberTable.this.setBackground(header.getBackground());
                        RowNumberTable.this.setFont(header.getFont());
                    }
                }
                if(isSelected) RowNumberTable.this.setFont(RowNumberTable.this.getFont().deriveFont(Font.BOLD));
                this.setText((value == null) ? "" : value.toString());
                RowNumberTable.this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return this;
            }
        }
        private final JTable main;

        public RowNumberTable(final JTable table){
            this.main = table;
            this.main.addPropertyChangeListener(this);
            this.main.getModel().addTableModelListener(this);
            this.setFocusable(false);
            this.setAutoCreateColumnsFromModel(false);
            this.setSelectionModel(this.main.getSelectionModel());
            final TableColumn column = new TableColumn();
            column.setIdentifier(this);
            column.setHeaderValue("");
            this.addColumn(column);
            column.setCellRenderer(new RowNumberRenderer());
            this.getColumnModel().getColumn(0).setPreferredWidth(50);
            this.setPreferredScrollableViewportSize(this.getPreferredSize());
        }

        @Override
        public void addNotify() {
            super.addNotify();
            final Component c = this.getParent();
            if(c instanceof JViewport){
                final JViewport viewport = (JViewport)c;
                viewport.addChangeListener(this);
            }
        }

        @Override
        public int getRowCount() {
            return this.main.getRowCount();
        }

        @Override
        public int getRowHeight(final int row) {
            final int rowHeight = this.main.getRowHeight(row);
            if(rowHeight != super.getRowHeight(row)) super.setRowHeight(row, rowHeight);
            return rowHeight;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            return Integer.toString(row + 1);
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return false;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent e) {
            if("selectionModel".equals(e.getPropertyName())) this.setSelectionModel(this.main.getSelectionModel());
            if("rowHeight".equals(e.getPropertyName())) this.repaint();
            if(!"model".equals(e.getPropertyName())) return;
            this.main.getModel().addTableModelListener(this);
            this.revalidate();
        }

        @Override
        public void setValueAt(final Object value, final int row, final int column) {}

        @Override
        public void stateChanged(final ChangeEvent e) {
            final JViewport viewport = (JViewport)e.getSource();
            final JScrollPane scrollPane = (JScrollPane)viewport.getParent();
            scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
        }

        @Override
        public void tableChanged(final TableModelEvent e) {
            this.revalidate();
        }
    }
    private final JComboBox          combo;
    private int                      mode_idx, curr_mode_idx;
    private JTable                   table;
    private JTable                   rows;
    private Descriptor               array;
    private Thread                   updater;
    private final String             name;
    private HashMap<Integer, Object> changes;
    private JScrollPane              array_panel;
    private LabeledExprEditor        expr_edit;
    private final TreeDialog         dialog;

    public ArrayEditor(final Descriptor array, final TreeDialog dialog, final String name){
        this.name = name;
        this.dialog = dialog;
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel(new GridLayout(2, 1));
        jp.add(new JLabel(name + ':'));
        this.combo = new JComboBox<String>(new String[]{"Undefined", "Array", "Expression"});
        this.combo.setEditable(false);
        this.combo.setSelectedIndex(this.mode_idx);
        this.combo.addActionListener(this);
        jp.add(this.combo);
        this.add(jp, BorderLayout.NORTH);
        this.setData(array);
    }

    public ArrayEditor(final TreeDialog dialog, final String name){
        this(null, dialog, name);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final int idx = this.combo.getSelectedIndex();
        if(idx != this.curr_mode_idx) this.setMode(idx);
    }

    private final void addEditor() {
        switch(this.curr_mode_idx){
            case 0:
                return;
            case 1:
                this.table = new JTable();
                this.array_panel = new JScrollPane(this.table);
                this.rows = new RowNumberTable(this.table);
                this.array_panel.setRowHeaderView(this.rows);
                this.array_panel.setCorner(JScrollPane.UPPER_LEFT_CORNER, this.rows.getTableHeader());
                this.array_panel.setPreferredSize(new Dimension(240, 640));
                this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                this.add(this.array_panel, BorderLayout.CENTER);
                this.setArray();
                break;
            case 2:
                this.expr_edit = new LabeledExprEditor(this.array);
                this.add(this.expr_edit, BorderLayout.CENTER);
                break;
        }
    }

    @Override
    public void finalize() throws Throwable {
        this.interrupt();
        super.finalize();
    }

    @Override
    public final Descriptor getData() throws MdsException {
        switch(this.curr_mode_idx){
            case 0:
                return null;
            case 1:
                return this.array;
            case 2:
                return this.expr_edit.getData();
        }
        return null;
    }

    public final void interrupt() {
        if(this.updater != null && this.updater.isAlive()) this.updater.interrupt();
    }

    @Override
    public final boolean isNull() {
        return(this.array == null || this.array == Missing.NEW);
    }

    @Override
    public final void reset() {
        this.combo.setSelectedIndex(this.mode_idx);
        this.setMode(this.mode_idx);
    }

    public final void setArray() {
        if(this.updater != null && this.updater.isAlive()) this.updater.interrupt();
        if(!(this.array instanceof NUMBERArray)) return;
        this.changes = new HashMap<Integer, Object>();
        final TableColumn column = ArrayEditor.this.rows.getColumn(ArrayEditor.this.rows);
        column.setHeaderValue("*");
        final JTableHeader header = ArrayEditor.this.rows.getTableHeader();
        final NUMBERArray narray = (NUMBERArray)this.array;
        final DefaultTableModel model = new DefaultTableModel(){
            @Override
            public final void setValueAt(final Object value, final int row, final int col) {
                if(col > 0) return;
                try{
                    final Number number = narray.parse(value.toString());
                    ArrayEditor.this.changes.put(Integer.valueOf(row), number);
                    super.setValueAt(number, row, col);
                }catch(final Exception e){
                    ArrayEditor.this.setToolTipText(e.getMessage());
                }
            }
        };
        model.addColumn(this.array.getDTypeName());
        this.table.setModel(model);
        this.table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        (this.updater = new Thread(this.name){
            {
                this.setDaemon(true);
                this.setPriority(Thread.MIN_PRIORITY);
            }

            @Override
            public final void run() {
                final int n = narray.length == 0 ? narray.arsize : narray.arsize / narray.length;
                for(int i = 0; i < n; i++){
                    if(i % 10000 == 0){
                        column.setHeaderValue(String.format("%d%%", 100 * i / n));
                        header.repaint();
                        synchronized(this){
                            if(this.isInterrupted()) return;
                        }
                    }
                    model.addRow(new Object[]{narray.getValue(i)});
                }
                column.setHeaderValue("");
                header.repaint();
            }
        }).start();
    }

    public final void setData(final Descriptor array) {
        if(array == null) this.mode_idx = 0;
        else if(array instanceof NUMBERArray) this.mode_idx = 1;
        else this.mode_idx = 2;
        this.array = array == null ? new Float32Array() : array;
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.combo.setEnabled(editable);
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
        if(this.table != null) this.table.setEnabled(editable);
    }

    private final void setMode(final int idx) {
        switch(this.curr_mode_idx){
            case 1:
                this.remove(this.array_panel);
                this.interrupt();
                this.array_panel = null;
                this.rows = null;
                this.table = null;
                this.changes = null;
                this.updater = null;
                break;
            case 2:
                this.remove(this.expr_edit);
                this.expr_edit = null;
                break;
        }
        this.curr_mode_idx = idx;
        this.addEditor();
        this.validate();
        this.dialog.repack();
        this.repaint();
    }
}
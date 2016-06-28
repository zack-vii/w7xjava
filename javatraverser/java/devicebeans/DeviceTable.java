package devicebeans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;
import javax.swing.CellEditor;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import mds.Database;
import mds.MdsException;
import mds.data.descriptor.Descriptor;

@SuppressWarnings("serial")
public class DeviceTable extends DeviceComponent{
    static String           copiedColItems[], copiedRowItems[], copiedItems[];
    // int preferredWidth = 200;
    // int preferredHeight = 100;
    static public final int NORMAL = 0, REFLEX = 1, REFLEX_INVERT = 2;

    private static boolean balancedParenthesis(final String inStr) {
        int roundCount = 0;
        int squareCount = 0;
        int braceCount = 0;
        for(int i = 0; i < inStr.length(); i++){
            switch(inStr.charAt(i)){
                case '(':
                    roundCount++;
                    break;
                case ')':
                    roundCount--;
                    break;
                case '[':
                    squareCount++;
                    break;
                case ']':
                    squareCount--;
                    break;
                case '{':
                    braceCount++;
                    break;
                case '}':
                    braceCount--;
                    break;
            }
        }
        return(roundCount == 0 && squareCount == 0 && braceCount == 0);
    }

    private static boolean balancedSquareParenthesis(final String inStr) {
        int squareCount = 0;
        for(int i = 0; i < inStr.length(); i++){
            switch(inStr.charAt(i)){
                case '[':
                    squareCount++;
                    break;
                case ']':
                    squareCount--;
                    break;
            }
        }
        return(squareCount == 0);
    }

    private static String expandBackslash(final String str) {
        String outStr = "";
        for(int i = 0; i < str.length(); i++){
            final char currChar = str.charAt(i);
            outStr += currChar;
            if(currChar == '\\') outStr += currChar;
        }
        return outStr;
    }

    private static String shrinkBackslash(final String str) {
        String outStr = "";
        char prevChar = str.charAt(0);
        for(int i = 1; i < str.length(); i++){
            final char currChar = str.charAt(i);
            if(!(currChar == '\\' && prevChar == '\\')) outStr += currChar;
            prevChar = currChar;
        }
        return outStr;
    }
    boolean               binary               = false;
    String                columnNames[]        = new String[0];
    JMenuItem             copyRowI, copyColI, copyI, pasteRowI, pasteColI, pasteI, propagateToRowI, propagateToColI;
    int                   currCol              = -1;
    int                   currRow              = -1;
    boolean               displayRowNumber     = false;
    boolean               editable             = true;
    boolean               initializing         = false;
    protected String      items[]              = new String[9];
    protected JLabel      label;
    String                labelString          = "";
    int                   numCols              = 3;
    int                   numRows              = 3;
    JPopupMenu            popM                 = null;
    protected int         preferredColumnWidth = 30;
    protected int         preferredHeight      = 70;
    int                   refMode              = DeviceTable.NORMAL;
    String                rowNames[]           = new String[0];
    protected JScrollPane scroll;
    boolean               state                = true;
    protected JTable      table;
    boolean               useExpressions       = false;

    public DeviceTable(){
        this.initializing = true;
        if(this.rowNames.length > 0) this.displayRowNumber = true;
        this.table = new JTable();
        this.scroll = new JScrollPane(this.table);
        this.table.setPreferredScrollableViewportSize(new Dimension(200, 70));
        this.label = new JLabel();
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        jp.add(this.label);
        this.add(jp, "North");
        this.add(this.scroll, "Center");
        this.table.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(final MouseEvent e) {
                if((e.getModifiers() & Event.META_MASK) != 0){ // If MB3
                    DeviceTable.this.showPopup(e.getX(), e.getY());
                }
            }
        });
        this.initializing = false;
    }

    @Override
    public Component add(final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Field. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final Component c, final int intex) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Field. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final String name, final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Field. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public void apply() throws Exception {
        final CellEditor ce = this.table.getCellEditor();
        if(ce != null) ce.stopCellEditing();
        super.apply();
    }

    void completeTable() {
        if(this.refMode != DeviceTable.REFLEX && this.refMode != DeviceTable.REFLEX_INVERT) return;
        // First row
        String sign = "";
        if(this.refMode == DeviceTable.REFLEX_INVERT) sign = "- ";
        // First row
        for(int i = 1; i < this.numCols / 2; i++)
            this.items[this.numCols - i] = sign + this.items[i];
        // First column
        for(int i = 1; i < this.numRows / 2; i++){
            this.items[(this.numRows - i) * this.numCols] = sign + this.items[i * this.numCols];
        }
        // Remaining
        // for(int i = 1; i < numRows/2; i++)
        for(int i = 1; i < this.numRows; i++)
            for(int j = 1; j < this.numCols / 2; j++){
                this.items[(this.numRows - i) * this.numCols + this.numCols - j] = sign + this.items[i * this.numCols + j];
            }
        System.out.println("HERMITIAN:");
        for(int i = 0; i < this.numRows; i++){
            for(int j = 0; j < this.numCols; j++)
                System.out.print(this.items[i * this.numCols + j] + " ");
            System.out.println("");
        }
    }

    void copy() {
        DeviceTable.copiedItems = new String[this.items.length];
        for(int i = 0; i < this.items.length; i++)
            DeviceTable.copiedItems[i] = this.items[i];
    }

    void copyCol(int col) {
        if(this.displayRowNumber) col--;
        DeviceTable.copiedColItems = new String[this.numRows];
        for(int i = 0; i < this.numRows; i++)
            DeviceTable.copiedColItems[i] = this.items[col + i * this.numCols];
    }

    void copyRow(final int row) {
        if(row == -1) return;
        DeviceTable.copiedRowItems = new String[this.numCols];
        for(int i = 0; i < this.numCols; i++)
            DeviceTable.copiedRowItems[i] = this.items[row * this.numCols + i];
    }

    @Override
    public void displayData(final Descriptor data, final boolean is_on) {
        this.state = is_on;
        final String decompiled = data.toString();
        final StringTokenizer st = new StringTokenizer(decompiled, ",[]");
        this.items = new String[this.numCols * this.numRows];
        int idx = 0;
        while(idx < this.numCols * this.numRows && st.hasMoreTokens())
            this.items[idx++] = st.nextToken();
        int actCols = this.numCols;
        if(this.refMode == DeviceTable.REFLEX || this.refMode == DeviceTable.REFLEX_INVERT) actCols = this.numCols / 2 + 1;
        // for(int i = 0; i < numCols; i++)
        for(int i = 0; i < actCols; i++){
            this.table.getColumnModel().getColumn(i).setMinWidth(6);
            this.table.getColumnModel().getColumn(i).setPreferredWidth(6);
            this.table.getColumnModel().getColumn(i).setWidth(6);
        }
        this.completeTable();
        this.table.repaint();
        this.redisplay();
    }

    public boolean getBinary() {
        return this.binary;
    }

    public String[] getColumnNames() {
        return this.columnNames;
    }

    @Override
    public Descriptor getData() {
        if(this.refMode == DeviceTable.REFLEX || this.refMode == DeviceTable.REFLEX_INVERT) this.completeTable();
        final int n_data = this.items.length;
        String dataString = "[";
        for(int i = 0; i < n_data; i++){
            if(i % this.numCols == 0) dataString += "[";
            if(this.items[i].trim().equals("")) dataString += "0";
            else dataString += this.items[i];
            if(i % this.numCols == this.numCols - 1){
                dataString += "]";
                if(i < n_data - 1 && this.items[i + 1] != null) dataString += ",";
                else if(i == n_data - 1) dataString += "]";
            }else dataString += ",";
        }
        try{
            if(this.useExpressions) return Database.tdiCompile("COMPILE(\'" + DeviceTable.expandBackslash(dataString) + "\')");
            return Database.tdiCompile(dataString);
        }catch(final MdsException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public boolean getDisplayRowNumber() {
        return this.displayRowNumber;
    }

    public boolean getEditable() {
        return this.editable;
    }

    public String getLabelString() {
        return this.labelString;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getPreferredColumnWidth() {
        return this.preferredColumnWidth;
    }

    public int getPreferredHeight() {
        return this.preferredHeight;
    }

    public int getRefMode() {
        return this.refMode;
    }

    public String[] getRowNames() {
        return this.rowNames;
    }

    @Override
    public boolean getState() {
        return this.state;
    }

    public boolean getUseExpressions() {
        return this.useExpressions;
    }

    @Override
    public void initializeData(final Descriptor data, final boolean is_on) {
        this.initializing = true;
        String decompiled = "";
        try{
            if(this.useExpressions){
                decompiled = data.toString();
                if(decompiled.startsWith("COMPILE(\'") || decompiled.startsWith("COMPILE(\"")) decompiled = DeviceTable.shrinkBackslash(decompiled.substring(9, decompiled.length() - 2));
            }else decompiled = data.toString();
        }catch(final Exception exc){
            System.err.println(exc);
            decompiled = null;
        }
        this.items = new String[this.numCols * this.numRows];
        int idx = 0;
        for(int i = 0; i < this.items.length; i++)
            this.items[i] = "";
        /*        if(decompiled != null)
        {
            StringTokenizer st = new StringTokenizer(decompiled, ",[]");
            if (!decompiled.startsWith("["))
                st.nextToken();
            while (idx < numCols * numRows && st.hasMoreTokens())
            {
                items[idx] += st.nextToken();
                if(balancedParenthesis(items[idx]))
                    idx++;
                else
                    items[idx]+=",";
            }
        }
        */ if(decompiled != null){
            // Remove surrounding [
            decompiled = decompiled.trim();
            while(decompiled.startsWith("[") && decompiled.endsWith("]") && DeviceTable.balancedSquareParenthesis(decompiled)){
                decompiled = decompiled.substring(1, decompiled.length() - 1);
                decompiled = decompiled.trim();
            }
            final StringTokenizer st = new StringTokenizer(decompiled, ",");
            while(idx < this.numCols * this.numRows && st.hasMoreTokens()){
                String currToken = st.nextToken();
                currToken = currToken.trim();
                while(currToken.startsWith("[") && !DeviceTable.balancedSquareParenthesis(currToken))
                    currToken = currToken.substring(1);
                while(currToken.endsWith("]") && !DeviceTable.balancedSquareParenthesis(currToken))
                    currToken = currToken.substring(0, currToken.length() - 1);
                while(currToken.startsWith("[") && currToken.endsWith("]") && DeviceTable.balancedSquareParenthesis(currToken)){
                    currToken = currToken.substring(1, currToken.length() - 1);
                    currToken = currToken.trim();
                }
                this.items[idx] += currToken;
                if(DeviceTable.balancedParenthesis(this.items[idx])) idx++;
                else this.items[idx] += ",";
            }
        }
        this.label.setText(this.labelString);
        this.table.setModel(new AbstractTableModel(){
            /**
             *
             */
            /*
            private String convertValue(final String value) {
                if(DeviceTable.this.refMode != DeviceTable.REFLEX_INVERT) return value;
                else if(value.trim().startsWith("-")) return value.trim().substring(1);
                else return "-" + value.trim();
            }
            */
            @SuppressWarnings("unchecked")
            @Override
            public Class getColumnClass(final int c) {
                if(!DeviceTable.this.binary) return String.class;
                if((DeviceTable.this.rowNames != null && (DeviceTable.this.rowNames.length > 0 || DeviceTable.this.displayRowNumber)) && c == 0) return String.class;
                return Boolean.class;
            }

            @Override
            public int getColumnCount() {
                if(DeviceTable.this.refMode == DeviceTable.REFLEX || DeviceTable.this.refMode == DeviceTable.REFLEX_INVERT){
                    if(DeviceTable.this.displayRowNumber || (DeviceTable.this.rowNames != null && DeviceTable.this.rowNames.length > 0)) return DeviceTable.this.numCols / 2 + 1 + 1;
                    return DeviceTable.this.numCols / 2 + 1;
                }
                if(DeviceTable.this.displayRowNumber || (DeviceTable.this.rowNames != null && DeviceTable.this.rowNames.length > 0)) return DeviceTable.this.numCols + 1;
                return DeviceTable.this.numCols;
            }

            @Override
            public String getColumnName(final int idx) {
                try{
                    if(DeviceTable.this.displayRowNumber || (DeviceTable.this.rowNames != null && DeviceTable.this.rowNames.length > 0)){
                        if(idx == 0) return "";
                        return DeviceTable.this.columnNames[idx - 1];
                    }
                    return DeviceTable.this.columnNames[idx];
                }catch(final Exception exc){
                    return "";
                }
            }

            @Override
            public int getRowCount() {
                return DeviceTable.this.numRows;
            }

            @Override
            public Object getValueAt(final int row, final int col) {
                if(DeviceTable.this.rowNames != null && DeviceTable.this.rowNames.length > 0){
                    if(col == 0) try{
                        return DeviceTable.this.rowNames[row];
                    }catch(final Exception exc){
                        return "";
                    }
                    try{
                        final String retItem = DeviceTable.this.items[row * DeviceTable.this.numCols + col - 1];
                        if(!DeviceTable.this.binary) return retItem;
                        if(retItem.trim().equals("0") || retItem.trim().equals("0.")) return new Boolean(false);
                        return new Boolean(true);
                    }catch(final Exception exc){
                        return null;
                    }
                }else if(DeviceTable.this.displayRowNumber){
                    if(col == 0) return "" + (row + 1);
                    try{
                        final String retItem = DeviceTable.this.items[row * DeviceTable.this.numCols + col - 1];
                        if(!DeviceTable.this.binary) return retItem;
                        if(retItem.trim().equals("0") || retItem.trim().equals("0.")) return new Boolean(false);
                        return new Boolean(true);
                    }catch(final Exception exc){
                        return null;
                    }
                }else{
                    try{
                        final String retItem = DeviceTable.this.items[row * DeviceTable.this.numCols + col];
                        if(!DeviceTable.this.binary) return retItem;
                        else if(retItem.trim().equals("0") || retItem.trim().equals("0.")) return new Boolean(false);
                        else return new Boolean(true);
                    }catch(final Exception exc){
                        return null;
                    }
                }
            }

            @Override
            public boolean isCellEditable(final int row, final int col) {
                return DeviceTable.this.isEditable(row, col);
            }

            @Override
            public void setValueAt(final Object value, final int row, final int col) {
                int itemIdx;
                int actCol;
                if((DeviceTable.this.rowNames != null && DeviceTable.this.rowNames.length > 0) || DeviceTable.this.displayRowNumber) actCol = col - 1;
                else actCol = col;
                /*               if(refMode == REFLEX || refMode == REFLEX_INVERT)
                {
                  //binary assumed to be false
                  if(row == 0 || row == numRows/2)
                  {
                      if(actCol <= numCols/2)
                      {
                          itemIdx = row * numCols + actCol;
                          items[itemIdx] = (String)value;
                          if(actCol > 0)
                          {
                            itemIdx = row * numCols + numCols - actCol;
                            items[itemIdx] = convertValue((String)value);
                            table.repaint();
                          }
                      }
                  }
                  else if(row <= numRows/2)
                  {
                      itemIdx = row * numCols + actCol;
                      items[itemIdx] =(String)value;
                      if(actCol == 0 && row > 0)
                      {
                          itemIdx = (numRows - row) * numCols + actCol;
                          items[itemIdx] = convertValue((String)value);
                          table.repaint();
                      }
                      if(actCol > 0 && row > 0 && (actCol != numCols/2 || row != numRows/2 ))
                      {
                          itemIdx = (numRows - row) * numCols + numCols - actCol;
                          items[itemIdx] = convertValue((String)value);
                          table.repaint();
                      }
                  }
                
                              }
                              else //refMode == NORMAL
                */ {
                    if((DeviceTable.this.rowNames != null && DeviceTable.this.rowNames.length > 0) || DeviceTable.this.displayRowNumber) itemIdx = row * DeviceTable.this.numCols + actCol;
                    else itemIdx = row * DeviceTable.this.numCols + actCol;
                    if(DeviceTable.this.binary){
                        final boolean isOn = ((Boolean)value).booleanValue();
                        DeviceTable.this.items[itemIdx] = (isOn) ? "1" : "0";
                        DeviceTable.this.currRow = row;
                        DeviceTable.this.currCol = col;
                    }else DeviceTable.this.items[itemIdx] = (String)value;
                    this.fireTableCellUpdated(row, col);
                }
                if(DeviceTable.this.refMode == DeviceTable.REFLEX || DeviceTable.this.refMode == DeviceTable.REFLEX_INVERT){
                    DeviceTable.this.completeTable();
                    DeviceTable.this.table.repaint();
                }
            }
        });
        if(this.binary) this.table.setRowSelectionAllowed(false);
        if(this.refMode == DeviceTable.REFLEX || this.refMode == DeviceTable.REFLEX_INVERT) this.table.setPreferredScrollableViewportSize(new Dimension(this.preferredColumnWidth * (this.numCols / 2 + 1), this.preferredHeight));
        else this.table.setPreferredScrollableViewportSize(new Dimension(this.preferredColumnWidth * this.numCols, this.preferredHeight));
        this.table.revalidate();
        this.initializing = false;
    }

    private boolean isEditable(final int row, final int col) {
        if(this.displayRowNumber && col == 0) return false;
        else if(this.refMode == DeviceTable.NORMAL) return this.editable;
        else if(!this.editable) return false;
        else // REFLEX || REFLEX_INVERT
        {
            /*            int actCol = (displayRowNumber)?col-1:col;
            if((row == 0 || row == numRows/2) && actCol > numCols/2)
                return false;
            else if (row > numRows/2)
                return false;
            return true;
            */ final int firstCol = (this.displayRowNumber) ? 1 : 0;
            if(col == firstCol && row > this.numRows - this.numRows / 2) return false;
            return true;
        }
    }

    void paste() {
        try{
            for(int i = 0; i < this.items.length; i++)
                this.items[i] = DeviceTable.copiedItems[i];
        }catch(final Exception exc){}
        this.completeTable();
        this.table.repaint();
    }

    void pasteCol(int col) {
        if(this.displayRowNumber) col--;
        try{
            for(int i = 0; i < this.numRows; i++)
                this.items[col + i * this.numCols] = DeviceTable.copiedColItems[i];
        }catch(final Exception exc){}
        this.completeTable();
        this.table.repaint();
    }

    void pasteRow(final int row) {
        if(row == -1) return;
        try{
            for(int i = 0; i < this.numCols; i++)
                this.items[row * this.numCols + i] = DeviceTable.copiedRowItems[i];
        }catch(final Exception exc){}
        this.completeTable();
        this.table.repaint();
    }

    void propagateToCol(int row, int col) {
        if(row == -1 || col == -1){
            row = this.currRow;
            col = this.currCol;
        }
        if(row == -1 || col == -1) return;
        if(this.displayRowNumber) col--;
        try{
            for(int i = 0; i < this.numRows; i++)
                this.items[col + i * this.numCols] = this.items[row * this.numCols + col];
        }catch(final Exception exc){}
        this.completeTable();
        this.table.repaint();
    }

    void propagateToRow(int row, int col) {
        if(row == -1 || col == -1){
            row = this.currRow;
            col = this.currCol;
        }
        if(row == -1 || col == -1) return;
        if(this.displayRowNumber) col--;
        try{
            for(int i = 0; i < this.numCols; i++)
                this.items[row * this.numCols + i] = this.items[row * this.numCols + col];
        }catch(final Exception exc){}
        this.completeTable();
        this.table.repaint();
    }

    void readFromClipboard() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Transferable transferable = clipboard.getContents(null);
        if(transferable == null) return;
        if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)){
            try{
                final String tableText = (String)transferable.getTransferData(DataFlavor.stringFlavor);
                final StringTokenizer st = new StringTokenizer(tableText, ",\n");
                int idx = 0;
                while(st.hasMoreTokens() && this.items.length > idx)
                    this.items[idx++] = st.nextToken();
            }catch(final Exception exc){
                System.err.println("Error reading from clipboard: " + exc);
            }
            this.completeTable();
            this.table.repaint();
        }
    }

    public void setBinary(final boolean binary) {
        this.binary = binary;
    }

    public void setColumnNames(final String[] columnNames) {
        this.columnNames = columnNames;
        this.redisplay();
    }

    public void setDisplayRowNumber(final boolean displayRowNumber) {
        this.displayRowNumber = displayRowNumber;
    }

    public void setEditable(final boolean state) {
        this.editable = state;
    }

    @Override
    public void setHighlight(final boolean highlighted) {
        if(highlighted){
            if(this.label != null) this.label.setForeground(Color.red);
        }else{
            if(this.label != null) this.label.setForeground(Color.black);
        }
        super.setHighlight(highlighted);
    }

    public void setLabelString(final String labelString) {
        this.labelString = labelString;
        this.redisplay();
    }

    public void setNumCols(final int numCols) {
        this.numCols = numCols;
        this.table.setPreferredScrollableViewportSize(new Dimension(this.preferredColumnWidth * numCols, this.preferredHeight));
        this.redisplay();
    }

    public void setNumRows(final int numRows) {
        this.numRows = numRows;
    }

    public void setPreferredColumnWidth(final int preferredColumnWidth) {
        this.preferredColumnWidth = preferredColumnWidth;
    }

    public void setPreferredHeight(final int preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    public void setRefMode(final int refMode) {
        this.refMode = refMode;
        if(refMode == DeviceTable.REFLEX || refMode == DeviceTable.REFLEX_INVERT){
            this.table.setDefaultRenderer(Object.class, new TableCellRenderer(){
                @Override
                public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
                    final JTextField tf = new JTextField();
                    tf.setText("" + value);
                    tf.setEnabled(DeviceTable.this.isEditable(row, column));
                    return tf;
                }
            });
        }
    }

    public void setRowNames(final String[] rowNames) {
        this.rowNames = rowNames;
        if(rowNames != null && rowNames.length > 0) this.displayRowNumber = true;
        this.redisplay();
    }

    public void setUseExpressions(final boolean useExpressions) {
        this.useExpressions = useExpressions;
    }

    void showPopup(final int x, final int y) {
        if(this.popM == null){
            this.popM = new JPopupMenu();
            this.copyRowI = new JMenuItem("Copy row");
            this.copyRowI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.copyRow(DeviceTable.this.table.getSelectedRow());
                }
            });
            this.popM.add(this.copyRowI);
            this.copyColI = new JMenuItem("Copy column");
            this.copyColI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.copyCol(DeviceTable.this.table.getSelectedColumn());
                }
            });
            this.popM.add(this.copyColI);
            this.copyI = new JMenuItem("Copy table");
            this.copyI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.copy();
                }
            });
            this.popM.add(this.copyI);
            this.pasteRowI = new JMenuItem("Paste row");
            this.pasteRowI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.pasteRow(DeviceTable.this.table.getSelectedRow());
                }
            });
            this.popM.add(this.pasteRowI);
            this.pasteColI = new JMenuItem("Paste column");
            this.pasteColI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.pasteCol(DeviceTable.this.table.getSelectedColumn());
                }
            });
            this.popM.add(this.pasteColI);
            this.pasteI = new JMenuItem("Paste table");
            this.pasteI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.paste();
                }
            });
            this.popM.add(this.pasteI);
            final JMenuItem copyClipboardI = new JMenuItem("Clipboard Copy");
            copyClipboardI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.writeToClipboard();
                }
            });
            this.popM.add(copyClipboardI);
            final JMenuItem pasteClipboardI = new JMenuItem("Clipboard Paste");
            pasteClipboardI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.readFromClipboard();
                }
            });
            this.popM.add(pasteClipboardI);
            final JMenuItem propagateToRowI = new JMenuItem("Propagate to Row");
            propagateToRowI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.propagateToRow(DeviceTable.this.table.getSelectedRow(), DeviceTable.this.table.getSelectedColumn());
                }
            });
            this.popM.add(propagateToRowI);
            final JMenuItem propagateToColI = new JMenuItem("Propagate to Column");
            propagateToColI.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DeviceTable.this.propagateToCol(DeviceTable.this.table.getSelectedRow(), DeviceTable.this.table.getSelectedColumn());
                }
            });
            this.popM.add(propagateToColI);
        }
        if(DeviceTable.copiedRowItems == null) this.pasteRowI.setEnabled(false);
        else this.pasteRowI.setEnabled(true);
        if(DeviceTable.copiedColItems == null) this.pasteColI.setEnabled(false);
        else this.pasteColI.setEnabled(true);
        if(DeviceTable.copiedItems == null) this.pasteI.setEnabled(false);
        else this.pasteI.setEnabled(true);
        this.popM.show(this.table, x, y);
    }

    void writeToClipboard() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String tableText = "";
        int idx = 0;
        if(this.refMode == DeviceTable.REFLEX || this.refMode == DeviceTable.REFLEX_INVERT) for(int i = 0; i < this.numRows; i++){
            for(int j = 0; j < this.numCols; j++)
                tableText += " " + this.items[idx++];
            tableText += "\n";
        }
        clipboard.setContents(new StringSelection(tableText), null);
    }
}

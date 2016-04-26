package devicebeans;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.StringTokenizer;

public class DeviceTableCustomizer extends DeviceCustomizer implements Customizer{
    private static final long serialVersionUID = 7125835834502042070L;
    DeviceTable               bean             = null;
    Checkbox                  displayRowNumC, editableC, binaryC, useExpressionsC;
    Button                    doneButton;
    TextField                 labelString, identifier, numCols, numRows, columnNames, rowNames, preferredColumnWidthT, preferredHeightT;
    PropertyChangeSupport     listeners        = new PropertyChangeSupport(this);
    Choice                    nids, modeChoice;
    Object                    obj;

    public DeviceTableCustomizer(){}

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        this.listeners.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        this.listeners.removePropertyChangeListener(l);
    }

    @Override
    public void setObject(final Object o) {
        this.bean = (DeviceTable)o;
        this.setLayout(new BorderLayout());
        Panel jp = new Panel();
        jp.setLayout(new GridLayout(5, 1));
        Panel jp1 = new Panel();
        jp1.add(new Label("Label: "));
        jp1.add(this.labelString = new TextField(20));
        this.labelString.setText(this.bean.getLabelString());
        jp1.add(new Label("Num. Rows: "));
        jp1.add(this.numRows = new TextField(4));
        final int rows = this.bean.getNumRows();
        this.numRows.setText((new Integer(rows)).toString());
        jp1.add(new Label("Num. Columns: "));
        jp1.add(this.numCols = new TextField(4));
        final int cols = this.bean.getNumCols();
        this.numCols.setText((new Integer(cols)).toString());
        jp.add(jp1);
        jp1 = new Panel();
        jp1.add(new Label("Offset nid: "));
        jp1.add(this.nids = new Choice());
        final String names[] = DeviceCustomizer.getDeviceFields();
        if(names != null) for(final String name2 : names)
            this.nids.addItem(name2);
        int offsetNid = this.bean.getOffsetNid();
        if(offsetNid > 0) offsetNid--;
        try{
            this.nids.select(offsetNid);
        }catch(final Exception exc){}
        jp1.add(new Label("Opt. identifier: "));
        jp1.add(this.identifier = new TextField(this.bean.getIdentifier(), 15));
        jp1.add(this.displayRowNumC = new Checkbox("Display row num.", this.bean.getDisplayRowNumber()));
        jp.add(jp1);
        jp1 = new Panel();
        jp1.add(new Label("Pref. Column Width: "));
        jp1.add(this.preferredColumnWidthT = new TextField("" + this.bean.getPreferredColumnWidth(), 4));
        jp1.add(new Label("Pref. Height: "));
        jp1.add(this.preferredHeightT = new TextField("" + this.bean.getPreferredHeight(), 4));
        jp1.add(this.editableC = new Checkbox("Editable", this.bean.getEditable()));
        jp1.add(this.binaryC = new Checkbox("Binary", this.bean.getBinary()));
        jp1.add(this.useExpressionsC = new Checkbox("Use Expressions", this.bean.getUseExpressions()));
        jp1.add(new Label("Mode: "));
        jp1.add(this.modeChoice = new Choice());
        this.modeChoice.add("Normal");
        this.modeChoice.add("Reflex");
        this.modeChoice.add("Reflex Inverted");
        this.modeChoice.select(this.bean.getRefMode());
        jp.add(jp1);
        jp1 = new Panel();
        jp1.add(new Label("Column Names: "));
        this.columnNames = new TextField(30);
        final String[] colNamesArray = this.bean.getColumnNames();
        String cnames = "";
        if(colNamesArray != null){
            for(final String element : colNamesArray)
                cnames += element + " ";
        }
        this.columnNames.setText(cnames);
        jp1.add(this.columnNames);
        jp.add(jp1);
        jp1 = new Panel();
        jp1.add(new Label("Row Names: "));
        this.rowNames = new TextField(30);
        final String[] rowNamesArray = this.bean.getRowNames();
        cnames = "";
        if(rowNamesArray != null){
            for(final String element : rowNamesArray)
                cnames += element + " ";
        }
        this.rowNames.setText(cnames);
        jp1.add(this.rowNames);
        jp.add(jp1);
        this.add(jp, "Center");
        jp = new Panel();
        jp.add(this.doneButton = new Button("Apply"));
        this.doneButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String oldLabelString = DeviceTableCustomizer.this.bean.getLabelString();
                DeviceTableCustomizer.this.bean.setLabelString(DeviceTableCustomizer.this.labelString.getText());
                DeviceTableCustomizer.this.listeners.firePropertyChange("labelString", oldLabelString, DeviceTableCustomizer.this.bean.getLabelString());
                final int oldNumCols = DeviceTableCustomizer.this.bean.getNumCols();
                DeviceTableCustomizer.this.bean.setNumCols(Integer.parseInt(DeviceTableCustomizer.this.numCols.getText()));
                DeviceTableCustomizer.this.listeners.firePropertyChange("numCols", oldNumCols, DeviceTableCustomizer.this.bean.getNumCols());
                final int oldNumRows = DeviceTableCustomizer.this.bean.getNumRows();
                DeviceTableCustomizer.this.bean.setNumRows(Integer.parseInt(DeviceTableCustomizer.this.numRows.getText()));
                DeviceTableCustomizer.this.listeners.firePropertyChange("numRows", oldNumRows, DeviceTableCustomizer.this.bean.getNumRows());
                final int oldOffsetNid = DeviceTableCustomizer.this.bean.getOffsetNid();
                DeviceTableCustomizer.this.bean.setOffsetNid(DeviceTableCustomizer.this.nids.getSelectedIndex() + 1);
                DeviceTableCustomizer.this.listeners.firePropertyChange("offsetNid", oldOffsetNid, DeviceTableCustomizer.this.bean.getOffsetNid());
                final String oldIdentifier = DeviceTableCustomizer.this.bean.getIdentifier();
                DeviceTableCustomizer.this.bean.setIdentifier(DeviceTableCustomizer.this.identifier.getText());
                DeviceTableCustomizer.this.listeners.firePropertyChange("identifier", oldIdentifier, DeviceTableCustomizer.this.bean.getIdentifier());
                final boolean oldDisplayRowNumber = DeviceTableCustomizer.this.bean.getDisplayRowNumber();
                DeviceTableCustomizer.this.bean.setDisplayRowNumber(DeviceTableCustomizer.this.displayRowNumC.getState());
                DeviceTableCustomizer.this.listeners.firePropertyChange("displayRowNumber", oldDisplayRowNumber, DeviceTableCustomizer.this.bean.getDisplayRowNumber());
                final int oldRefMode = DeviceTableCustomizer.this.bean.getRefMode();
                DeviceTableCustomizer.this.bean.setRefMode(DeviceTableCustomizer.this.modeChoice.getSelectedIndex());
                DeviceTableCustomizer.this.listeners.firePropertyChange("refMode", oldRefMode, DeviceTableCustomizer.this.bean.getRefMode());
                final boolean oldEditable = DeviceTableCustomizer.this.bean.getEditable();
                DeviceTableCustomizer.this.bean.setEditable(DeviceTableCustomizer.this.editableC.getState());
                DeviceTableCustomizer.this.listeners.firePropertyChange("editable", oldEditable, DeviceTableCustomizer.this.bean.getEditable());
                final boolean oldBinary = DeviceTableCustomizer.this.bean.getBinary();
                DeviceTableCustomizer.this.bean.setBinary(DeviceTableCustomizer.this.binaryC.getState());
                DeviceTableCustomizer.this.listeners.firePropertyChange("binary", oldBinary, DeviceTableCustomizer.this.bean.getBinary());
                final boolean oldUseExpressions = DeviceTableCustomizer.this.bean.getUseExpressions();
                DeviceTableCustomizer.this.bean.setUseExpressions(DeviceTableCustomizer.this.useExpressionsC.getState());
                DeviceTableCustomizer.this.listeners.firePropertyChange("useExpression", oldUseExpressions, DeviceTableCustomizer.this.bean.getUseExpressions());
                final int oldPreferredColumnWidth = DeviceTableCustomizer.this.bean.getPreferredColumnWidth();
                DeviceTableCustomizer.this.bean.setPreferredColumnWidth(Integer.parseInt(DeviceTableCustomizer.this.preferredColumnWidthT.getText()));
                DeviceTableCustomizer.this.listeners.firePropertyChange("preferredColumnWidth", oldPreferredColumnWidth, DeviceTableCustomizer.this.bean.getPreferredColumnWidth());
                final int oldPreferredHeight = DeviceTableCustomizer.this.bean.getPreferredHeight();
                DeviceTableCustomizer.this.bean.setPreferredHeight(Integer.parseInt(DeviceTableCustomizer.this.preferredHeightT.getText()));
                DeviceTableCustomizer.this.listeners.firePropertyChange("preferredHeight", oldPreferredHeight, DeviceTableCustomizer.this.bean.getPreferredHeight());
                StringTokenizer st = new StringTokenizer(DeviceTableCustomizer.this.columnNames.getText(), " ");
                final String colNames[] = new String[st.countTokens()];
                int idx = 0;
                while(st.hasMoreTokens())
                    colNames[idx++] = st.nextToken();
                final String[] oldColumnNames = DeviceTableCustomizer.this.bean.getColumnNames();
                DeviceTableCustomizer.this.bean.setColumnNames(colNames);
                DeviceTableCustomizer.this.listeners.firePropertyChange("columnNames", oldColumnNames, DeviceTableCustomizer.this.bean.getColumnNames());
                st = new StringTokenizer(DeviceTableCustomizer.this.rowNames.getText(), " ");
                final String rowNames[] = new String[st.countTokens()];
                idx = 0;
                while(st.hasMoreTokens())
                    rowNames[idx++] = st.nextToken();
                final String[] oldRowNames = DeviceTableCustomizer.this.bean.getRowNames();
                DeviceTableCustomizer.this.bean.setRowNames(rowNames);
                DeviceTableCustomizer.this.listeners.firePropertyChange("rowNames", oldRowNames, DeviceTableCustomizer.this.bean.getRowNames());
            }
        });
        this.add(jp, "South");
    }
}

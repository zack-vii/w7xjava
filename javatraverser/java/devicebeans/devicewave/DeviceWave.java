package devicebeans.devicewave;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import devicebeans.DeviceComponent;
import jscope.Waveform;
import jscope.WaveformEditor;
import jscope.WaveformEditorListener;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.Float32Array;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.Float32;
import mds.data.descriptor_s.Nid;

@SuppressWarnings("serial")
public class DeviceWave extends DeviceComponent{
    private final class TableModel extends DefaultTableModel{
        @Override
        public void addTableModelListener(final TableModelListener l) {}

        @Override
        public Class<? extends String> getColumnClass(final int col) {
            return new String().getClass();
        }

        @Override
        public int getColumnCount() {
            return DeviceWave.columnNames.length;
        }

        @Override
        public String getColumnName(final int col) {
            return DeviceWave.columnNames[col];
        }

        @Override
        public int getRowCount() {
            return DeviceWave.MAX_POINTS;
        }

        @Override
        public Object getValueAt(final int row, final int col) {
            if(DeviceWave.this.waveX == null || row >= DeviceWave.this.waveX.length) return "";
            final float currVal = (col == 0) ? DeviceWave.this.waveX[row] : DeviceWave.this.waveY[row];
            // return (new Float(currVal)).toString();
            return(DeviceWave.this.nf.format(currVal));
        }

        @Override
        public boolean isCellEditable(final int row, final int col) {
            if(!DeviceWave.this.waveEditable) return false;
            if(row == 0 && col == 0) return false;
            if(row == DeviceWave.this.waveX.length - 1 && col == 0) return false;
            return true;
        }

        @Override
        public void removeTableModelListener(final TableModelListener l) {}

        @Override
        public void setValueAt(final Object val, final int row, final int col) {
            if(row >= DeviceWave.this.waveX.length){
                JOptionPane.showMessageDialog(DeviceWave.this, "There are misssing points in the waveform definition", "Incorrect waveform definition", JOptionPane.WARNING_MESSAGE);
                return;
            }
            float valFloat;
            try{
                valFloat = (new Float((String)val)).floatValue();
            }catch(final Exception exc){
                JOptionPane.showMessageDialog(DeviceWave.this, "The value is not a correct floating point representation", "Incorrect waveform definition", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(col == 0){
                if(valFloat > DeviceWave.this.maxX) DeviceWave.this.waveX[row] = DeviceWave.this.maxX;
                else if(valFloat < DeviceWave.this.minX) DeviceWave.this.waveX[row] = DeviceWave.this.minX;
                else DeviceWave.this.waveX[row] = valFloat;
                if(row == 0 || row == DeviceWave.this.waveX.length - 1) return;
                if(DeviceWave.this.waveX[row] < DeviceWave.this.waveX[row - 1] + DeviceWave.MIN_STEP) DeviceWave.this.waveX[row] = DeviceWave.this.waveX[row - 1] + (float)DeviceWave.MIN_STEP;
                if(DeviceWave.this.waveX[row] > DeviceWave.this.waveX[row + 1] - DeviceWave.MIN_STEP) DeviceWave.this.waveX[row] = DeviceWave.this.waveX[row + 1] - (float)DeviceWave.MIN_STEP;
            }else{
                // if(valFloat > maxY)
                // waveY[row] = maxY;
                // else if(valFloat < minY)
                // waveY[row] = minY;
                // else
                DeviceWave.this.waveY[row] = valFloat;
            }
            DeviceWave.this.waveEditor.setWaveform(DeviceWave.this.waveX, DeviceWave.this.waveY, DeviceWave.this.minY, DeviceWave.this.maxY);
        }
    }
    private static final String[] columnNames  = new String[]{"Time", "Value"};
    static final int              MAX_POINTS   = 50;
    static final double           MIN_STEP     = 1E-5;
    protected static float        savedMinX, savedMinY, savedMaxX, savedMaxY;
    protected static float        savedWaveX[] = null, savedWaveY[] = null;

    public static void main(final String args[]) {
        new DeviceWave();
        System.out.println("Success");
    }
    JMenuItem                  copyI, pasteI;
    JPopupMenu                 copyPastePopup;
    protected JCheckBox        editCB;
    protected boolean          initializing     = false;
    protected float            maxX, minX, maxY, minY;
    protected JTextField       maxXField        = null, minXField = null, maxYField = null, minYField = null;
    protected float            maxXOld, minXOld, maxYOld, minYOld;
    public boolean             maxXVisible      = false;
    public boolean             maxYVisible      = true;
    public boolean             minXVisible      = false;
    public boolean             minYVisible      = false;
    private final NumberFormat nf               = NumberFormat.getInstance(Locale.ENGLISH);
    protected int              numPoints;
    protected int              prefHeight       = 200;
    protected JScrollPane      scroll;
    protected JTable           table;
    public String              updateExpression = null;
    public boolean             waveEditable     = false;
    protected WaveformEditor   waveEditor;
    protected float[]          waveX            = null, waveY = null;
    protected float[]          waveXOld         = null, waveYOld = null;

    public DeviceWave(){}

    @Override
    public Component add(final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(this, "You cannot add a component to a Device Wave. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final Component c, final int intex) {
        return this.add(c);
    }

    @Override
    public Component add(final String name, final Component c) {
        return this.add(c);
    }

    @Override
    public void apply() throws Exception {
        final CellEditor ce = this.table.getCellEditor();
        if(ce != null) ce.stopCellEditing();
        super.apply();
        this.updateLimits();
        if(this.minXVisible){
            try{
                this.subtree.putData(new Nid(this.nidData.getValue() + 1), new Float32(this.minX));
            }catch(final Exception exc){
                System.out.println("Error storing min X value: " + exc);
            }
        }
        if(this.maxXVisible){
            try{
                this.subtree.putData(new Nid(this.nidData.getValue() + 2), new Float32(this.maxX));
            }catch(final Exception exc){
                System.out.println("Error storing max X value: " + exc);
            }
        }
        if(this.minYVisible){
            try{
                this.subtree.putData(new Nid(this.nidData.getValue() + 3), new Float32(this.minY));
            }catch(final Exception exc){
                System.out.println("Error storing min Y value: " + exc);
            }
        }
        if(this.maxYVisible){
            try{
                this.subtree.putData(new Nid(this.nidData.getValue() + 4), new Float32(this.maxY));
            }catch(final Exception exc){
                System.out.println("Error storing max Y value: " + exc);
            }
        }
    }

    private void create() {
        DeviceWave.savedWaveX = null;
        this.waveEditor = new WaveformEditor();
        this.nf.setMaximumFractionDigits(4);
        this.nf.setGroupingUsed(false);
        this.waveEditor.setPreferredSize(new Dimension(300, this.prefHeight));
        this.waveEditor.addWaveformEditorListener(new WaveformEditorListener(){
            @Override
            public void waveformUpdated(final float[] waveX, final float[] waveY, final int newIdx) {
                DeviceWave.this.numPoints = waveX.length;
                DeviceWave.this.waveX = waveX;
                DeviceWave.this.waveY = waveY;
                if(newIdx >= 0){
                    DeviceWave.this.table.setRowSelectionInterval(newIdx, newIdx);
                    DeviceWave.this.table.setEditingRow(newIdx);
                    int centerIdx;
                    if(newIdx > 8) centerIdx = newIdx - 4;
                    else centerIdx = 0;
                    final int rowY = centerIdx * DeviceWave.this.table.getRowHeight();
                    DeviceWave.this.scroll.getViewport().setViewPosition(new Point(0, rowY));
                }
                if(DeviceWave.this.maxYVisible){
                    try{
                        DeviceWave.this.maxY = (new Float(DeviceWave.this.maxYField.getText()).floatValue());
                        for(int i = 0; i < waveY.length; i++)
                            if(waveY[i] > DeviceWave.this.maxY) waveY[i] = DeviceWave.this.maxY;
                    }catch(final Exception exc){}
                }
                DeviceWave.this.table.repaint();
            }
        });
        this.waveEditor.setEditable(false);
        this.table = new JTable();
        /*table.setColumnModel(new DefaultTableColumnModel()
                 {
            public int getTotalColumnWidth() {return 150;}
                 });
         */
        this.table.setModel(new TableModel());
        this.setLayout(new BorderLayout());
        this.add(this.waveEditor, "Center");
        this.scroll = new JScrollPane(this.table);
        this.scroll.setPreferredSize(new Dimension(150, 200));
        this.add(this.scroll, "East");
        final JPanel jp = new JPanel();
        if(this.minXVisible){
            jp.add(new JLabel("Min X: "));
            jp.add(this.minXField = new JTextField("" + this.minX, 6));
        }
        if(this.maxXVisible){
            jp.add(new JLabel("Max X: "));
            jp.add(this.maxXField = new JTextField("" + this.maxX, 6));
        }
        if(this.minYVisible){
            jp.add(new JLabel("Min Y: "));
            jp.add(this.minYField = new JTextField("" + this.minY, 6));
        }
        if(this.maxYVisible){
            jp.add(new JLabel("Max Y: "));
            jp.add(this.maxYField = new JTextField("" + this.maxY, 6));
        }
        this.editCB = new JCheckBox("Edit", false);
        this.editCB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                DeviceWave.this.waveEditable = DeviceWave.this.editCB.isSelected();
                DeviceWave.this.waveEditor.setEditable(DeviceWave.this.editCB.isSelected());
            }
        });
        jp.add(this.editCB);
        final JButton updateB = new JButton("Update");
        updateB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                DeviceWave.this.updateLimits();
                DeviceWave.this.waveEditor.setWaveform(DeviceWave.this.waveX, DeviceWave.this.waveY, DeviceWave.this.minY, DeviceWave.this.maxY);
            }
        });
        jp.add(updateB);
        this.add(jp, "North");
        // Add popup for copy/paste
        this.copyPastePopup = new JPopupMenu();
        this.copyI = new JMenuItem("Copy");
        this.copyI.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                DeviceWave.savedMinX = DeviceWave.this.minX;
                DeviceWave.savedMinY = DeviceWave.this.minY;
                DeviceWave.savedMaxX = DeviceWave.this.maxX;
                DeviceWave.savedMaxY = DeviceWave.this.maxY;
                DeviceWave.savedWaveX = DeviceWave.this.waveX;
                DeviceWave.savedWaveY = DeviceWave.this.waveY;
            }
        });
        this.copyPastePopup.add(this.copyI);
        this.pasteI = new JMenuItem("Paste");
        this.pasteI.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(DeviceWave.savedWaveX == null) return;
                if(DeviceWave.this.minXVisible) DeviceWave.this.minX = DeviceWave.savedMinX;
                if(DeviceWave.this.minYVisible) DeviceWave.this.minY = DeviceWave.savedMinY;
                if(DeviceWave.this.maxXVisible) DeviceWave.this.maxX = DeviceWave.savedMaxX;
                if(DeviceWave.this.maxYVisible) DeviceWave.this.maxY = DeviceWave.savedMaxY;
                try{
                    DeviceWave.this.waveX = new float[DeviceWave.savedWaveX.length];
                    DeviceWave.this.waveY = new float[DeviceWave.savedWaveY.length];
                    for(int i = 0; i < DeviceWave.savedWaveX.length; i++){
                        DeviceWave.this.waveX[i] = DeviceWave.savedWaveX[i];
                        DeviceWave.this.waveY[i] = DeviceWave.savedWaveY[i];
                    }
                }catch(final Exception exc){}
                DeviceWave.this.displayData(null, true);
            }
        });
        this.copyPastePopup.add(this.pasteI);
        this.copyPastePopup.pack();
        this.copyPastePopup.setInvoker(this);
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(final MouseEvent e) {
                if((e.getModifiers() & Event.META_MASK) != 0){
                    if(DeviceWave.savedWaveX == null) DeviceWave.this.pasteI.setEnabled(false);
                    else DeviceWave.this.pasteI.setEnabled(true);
                    DeviceWave.this.copyPastePopup.setInvoker(DeviceWave.this);
                    DeviceWave.this.copyPastePopup.show(DeviceWave.this, e.getX(), e.getY());
                }
            }
        });
    }

    @Override
    protected void dataChanged(final int offsetNid, final Object data) {
        if(offsetNid != this.getOffsetNid()) return;
        Vector<?> inVect;
        try{
            inVect = (Vector<?>)data;
        }catch(final Exception exc){
            System.err.println("Internal error: wrong data passed to DeviceWave.dataChanged");
            return;
        }
        this.minX = ((Float)inVect.elementAt(0)).floatValue();
        this.maxX = ((Float)inVect.elementAt(1)).floatValue();
        this.minY = ((Float)inVect.elementAt(2)).floatValue();
        this.maxY = ((Float)inVect.elementAt(3)).floatValue();
        final float[] currX = (float[])inVect.elementAt(4);
        final float[] currY = (float[])inVect.elementAt(5);
        try{
            this.waveX = new float[currX.length];
            this.waveY = new float[currY.length];
            for(int i = 0; i < currX.length; i++){
                this.waveX[i] = currX[i];
                this.waveY[i] = currY[i];
            }
        }catch(final Exception exc){}
        this.displayData(null, true);
    }

    @Override
    protected void displayData(final Descriptor data, final boolean is_on) {
        this.waveEditor.setWaveform(this.waveX, this.waveY, this.minY, this.maxY);
        if(this.maxXVisible){
            this.maxXField.setText("" + this.maxX);
        }
        if(this.minXVisible){
            this.minXField.setText("" + this.minX);
        }
        if(this.maxYVisible){
            this.maxYField.setText("" + this.maxY);
        }
        if(this.minYVisible){
            this.minYField.setText("" + this.minY);
        }
        this.table.repaint();
    }

    @Override
    public void fireUpdate(final String updateId, final Descriptor newExpr) {
        if(this.updateIdentifier != null && this.updateExpression != null && this.updateIdentifier.equals(updateId)){
            // Substitute $ in expression with the new value
            final StringTokenizer st = new StringTokenizer(this.updateExpression, "$");
            String newExprStr = "";
            try{
                final String newVal = newExpr.toString();
                while(st.hasMoreTokens()){
                    newExprStr += st.nextToken();
                    if(st.hasMoreTokens()) newExprStr += newVal;
                }
                // System.out.println(newExprStr);
                // Update first current id TDI variables
                this.master.updateIdentifiers();
                // Compute new Max
                final Descriptor newData = this.subtree.evaluate(newExprStr);
                this.maxY = newData.toFloat();
                // System.out.println(""+maxY);
                if(this.maxYVisible) this.maxYField.setText("" + this.maxY);
                this.waveEditor.setWaveform(this.waveX, this.waveY, this.minY, this.maxY);
            }catch(final Exception exc){
                System.err.println("Error updating Max Y: " + exc);
            }
        }
    }

    @Override
    protected Descriptor getData() {
        final Descriptor[] dims = new Descriptor[1];
        dims[0] = new Float32Array(this.waveX);
        final Descriptor values = new Float32Array(this.waveY);
        return new Signal(null, values, dims);
    }

    @Override
    protected Object getFullData() {
        final Vector<Object> res = new Vector<Object>();
        res.add(new Float(this.minX));
        res.add(new Float(this.maxX));
        res.add(new Float(this.minY));
        res.add(new Float(this.maxY));
        res.add(this.waveX);
        res.add(this.waveY);
        return res;
    }

    public boolean getMaxXVisible() {
        return this.maxXVisible;
    }

    public boolean getMaxYVisible() {
        return this.maxYVisible;
    }

    public boolean getMinXVisible() {
        return this.minXVisible;
    }

    public boolean getMinYVisible() {
        return this.minYVisible;
    }

    public int getPrefHeight() {
        return this.prefHeight;
    }

    @Override
    protected boolean getState() {
        return true;
    }

    public String getUpdateExpression() {
        return this.updateExpression;
    }

    public boolean getWaveEditable() {
        return this.waveEditable;
    }

    @Override
    protected void initializeData(final Descriptor data, final boolean is_on) {
        this.create();
        this.initializing = true;
        // Read X and Y extremes
        Nid currNid;
        Descriptor currData;
        float[] currX, currY;
        // Min X
        try{
            currNid = new Nid(this.nidData.getValue() + 1);
            currData = this.subtree.evaluate(currNid);
            this.minX = this.minXOld = currData.toFloat();
        }catch(final Exception exc){
            this.minX = this.minXOld = 0;
        }
        // Max X
        try{
            currNid = new Nid(this.nidData.getValue() + 2);
            currData = this.subtree.evaluate(currNid);
            this.maxX = this.maxXOld = currData.toFloat();
        }catch(final Exception exc){
            this.maxX = this.maxXOld = 1;
        }
        // Min Y
        try{
            currNid = new Nid(this.nidData.getValue() + 3);
            currData = this.subtree.evaluate(currNid);
            this.minY = this.minYOld = currData.toFloat();
        }catch(final Exception exc){
            this.minY = this.minYOld = 0;
        }
        // Max Y
        try{
            currNid = new Nid(this.nidData.getValue() + 4);
            currData = this.subtree.evaluate(currNid);
            this.maxY = this.maxYOld = currData.toFloat();
        }catch(final Exception exc){
            this.maxY = this.maxYOld = 1;
        }
        // Prepare waveX and waveY
        Descriptor xData, yData;
        try{
            yData = this.subtree.compile("FLOAT(" + this.subtree.evaluate(data) + ")");
            currY = yData.toFloats();
            xData = this.subtree.evaluate("FLOAT(DIM_OF(" + this.subtree.decompile(data) + "))");
            currX = xData.toFloats();
        }catch(final Exception exc){
            currX = new float[]{this.minX, this.maxX};
            currY = new float[]{0, 0};
        }
        // Check that the stored signal lies into valid X range
        if(currX[0] <= this.minX - (float)DeviceWave.MIN_STEP || currX[currX.length - 1] >= this.maxX + (float)DeviceWave.MIN_STEP){
            currX = new float[]{this.minX, this.maxX};
            currY = new float[]{0, 0};
            JOptionPane.showMessageDialog(DeviceWave.this, "The stored signal lies outside the valid X range. Hit apply to override the incorrect values.", "Incorret waveform limits", JOptionPane.WARNING_MESSAGE);
        }
        // set extreme points, if not present
        int nPoints = currX.length;
        if(Math.abs(currX[0] - this.minX) > DeviceWave.MIN_STEP) nPoints++;
        else currX[0] = this.minX;
        if(Math.abs(currX[currX.length - 1] - this.maxX) > DeviceWave.MIN_STEP) nPoints++;
        else currX[currX.length - 1] = this.maxX;
        this.waveX = new float[nPoints];
        this.waveY = new float[nPoints];
        int currIdx = 0;
        if(Math.abs(currX[0] - this.minX) > DeviceWave.MIN_STEP){
            this.waveX[0] = this.minX;
            this.waveY[0] = 0;
            currIdx++;
        }
        for(int i = 0; i < currX.length; i++){
            this.waveX[currIdx] = currX[i];
            this.waveY[currIdx++] = currY[i];
        }
        if(Math.abs(currX[currX.length - 1] - this.maxX) > DeviceWave.MIN_STEP){
            this.waveX[currIdx] = this.maxX;
            this.waveY[currIdx] = 0;
        }
        this.waveXOld = new float[this.waveX.length];
        this.waveYOld = new float[this.waveX.length];
        for(int i = 0; i < this.waveX.length; i++){
            this.waveXOld[i] = this.waveX[i];
            this.waveYOld[i] = this.waveY[i];
        }
        // updateLimits();
        this.displayData(data, is_on);
        this.initializing = false;
    }

    @Override
    public void reset() {
        this.minX = this.minXOld;
        this.maxX = this.maxXOld;
        this.minY = this.minYOld;
        this.maxY = this.maxYOld;
        this.waveX = new float[this.waveXOld.length];
        this.waveY = new float[this.waveXOld.length];
        for(int i = 0; i < this.waveXOld.length; i++){
            this.waveX[i] = this.waveXOld[i];
            this.waveY[i] = this.waveYOld[i];
        }
        super.reset();
    }

    @Override
    public void setEnabled(final boolean state) {}

    @Override
    public void setHighlight(final boolean highlighted) {
        if(highlighted){
            Waveform.setColors(new Color[]{Color.red}, new String[]{"Red"});
        }else{
            Waveform.setColors(new Color[]{Color.black}, new String[]{"Black"});
        }
        super.setHighlight(highlighted);
    }

    public void setMaxXVisible(final boolean visible) {
        this.maxXVisible = visible;
    }

    public void setMaxYVisible(final boolean visible) {
        this.maxYVisible = visible;
    }

    public void setMinXVisible(final boolean visible) {
        this.minXVisible = visible;
    }

    public void setMinYVisible(final boolean visible) {
        this.minYVisible = visible;
    }

    public void setPrefHeight(final int prefHeight) {
        this.prefHeight = prefHeight;
    }

    public void setUpdateExpression(final String updateExpression) {
        this.updateExpression = updateExpression;
    }

    public void setWaveEditable(final boolean editable) {
        this.waveEditable = editable;
    }

    protected void updateLimits() {
        if(this.minXVisible){
            while(true){
                try{
                    this.minX = (new Float(this.minXField.getText()).floatValue());
                    break;
                }catch(final Exception exc){
                    JOptionPane.showMessageDialog(DeviceWave.this, "Invalid value for min X", "Incorret limit", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        if(this.maxXVisible){
            while(true){
                try{
                    this.maxX = (new Float(this.maxXField.getText()).floatValue());
                    break;
                }catch(final Exception exc){
                    JOptionPane.showMessageDialog(DeviceWave.this, "Invalid value for max X", "Incorret limit", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        if(this.minYVisible){
            while(true){
                try{
                    this.minY = (new Float(this.minYField.getText()).floatValue());
                    break;
                }catch(final Exception exc){
                    JOptionPane.showMessageDialog(DeviceWave.this, "Invalid value for min Y", "Incorret limit", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        if(this.maxYVisible){
            while(true){
                try{
                    this.maxY = (new Float(this.maxYField.getText()).floatValue());
                    break;
                }catch(final Exception exc){
                    JOptionPane.showMessageDialog(DeviceWave.this, "Invalid value for max Y", "Incorret limit", JOptionPane.WARNING_MESSAGE);
                }
            }
            for(int i = 0; i < this.waveX.length; i++){
                if(this.waveY[i] > this.maxY) this.waveY[i] = this.maxY;
            }
            this.repaint();
        }
    }
}

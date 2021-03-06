package devicebeans;

import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

@SuppressWarnings("serial")
public class DeviceParameters extends DeviceMultiComponent{
    protected Vector<DeviceComponent> parameters = new Vector<DeviceComponent>();

    @Override
    protected void addParameter(final JPanel jp, final Nid nidData) {
        try{
            nidData.setDefault();
            Nid currNode;
            currNode = new Path(":DESCRIPTION").toNid();
            final String description = currNode.getRecord().toString();
            currNode = new Path(":TYPE").toNid();
            final String typeStr = currNode.getRecord().toString();
            currNode = new Path(":DIMS").toNid();
            final int[] dims = currNode.getRecord().toIntArray();
            currNode = new Path(":DATA").toNid();
            if(dims[0] == 0) // Scalar
            {
                final DeviceField currField = new DeviceField();
                currField.setSubtree(this.subtree);
                currField.setBaseNid(currNode.getNidNumber());
                currField.setOffsetNid(0);
                currField.setLabelString(description);
                final JPanel jp1 = new JPanel();
                jp1.add(currField);
                jp.add(jp1);
                currField.configure(currNode.getNidNumber());
                this.parameters.addElement(currField);
            }else // Array or Matrix, use DeviceTable
            {
                final DeviceTable currField = new DeviceTable();
                currField.setSubtree(this.subtree);
                currField.setBaseNid(currNode.getNidNumber());
                currField.setOffsetNid(0);
                if(typeStr.toUpperCase().trim().equals("BINARY")) currField.setBinary(true);
                else currField.setBinary(false);
                if(typeStr.toUpperCase().equals("REFLEX")) currField.setRefMode(DeviceTable.REFLEX);
                if(typeStr.toUpperCase().equals("REFLEX_INVERT")) currField.setRefMode(DeviceTable.REFLEX_INVERT);
                currField.setUseExpressions(true);
                currField.setDisplayRowNumber(true);
                currField.setLabelString(description);
                int numCols;
                if(dims.length == 1){
                    currField.setNumRows(1);
                    currField.setNumCols(numCols = dims[0]);
                }else{
                    currField.setNumRows(dims[0]);
                    currField.setNumCols(numCols = dims[1]);
                }
                final String colNames[] = new String[numCols];
                if(typeStr.toUpperCase().equals("REFLEX_INVERT") || typeStr.toUpperCase().equals("REFLEX")){
                    for(int i = 0; i <= numCols / 2; i++)
                        colNames[i] = "" + (-i);
                    for(int i = 1; i < numCols / 2; i++)
                        colNames[numCols / 2 + i] = "" + (numCols / 2 - i);
                }else{
                    for(int i = 0; i < numCols; i++)
                        colNames[i] = "" + i;
                }
                currField.setColumnNames(colNames);
                jp.add(currField);
                currField.configure(currNode.getNidNumber());
                this.parameters.addElement(currField);
            }
        }catch(final Exception exc){
            System.err.println("Error in DeviceParameters.addParam: " + exc);
        }
    }

    @Override
    protected void applyComponent(final Nid nidData) {
        try{
            for(int i = 0; i < this.parameters.size(); i++){
                this.parameters.elementAt(i).apply();
            }
        }catch(final Exception exc){
            System.err.println("Error in DeviceParameters.apply: " + exc);
        }
    }

    @Override
    protected String getComponentNameAt(final Nid nidData, final int idx) {
        String parName;
        Nid prevDefNid;
        final String paramName = this.getParameterName();
        if(idx < 10) parName = paramName + "_00" + (idx + 1);
        else if(idx < 100) parName = paramName + "_0" + (idx + 1);
        else parName = paramName + "_" + (idx + 1);
        try{
            prevDefNid = this.subtree.getDefault();
            nidData.setDefault();
            Nid currNode;
            currNode = new Path(parName + ":NAME").toNid();
            parName = this.subtree.tdiEvaluate(currNode).toString();
            prevDefNid.setDefault();
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Error getting Component Name in DeviceParameters: " + exc);
            parName = "";
        }
        return parName;
    }

    @Override
    protected Nid getComponentNidAt(final Nid nidData, final int idx) {
        String parName;
        Nid prevDefNid;
        final String paramName = this.getParameterName();
        if(idx < 10) parName = paramName + "_00" + (idx + 1);
        else if(idx < 100) parName = paramName + "_0" + (idx + 1);
        else parName = paramName + "_" + (idx + 1);
        try{
            prevDefNid = this.subtree.getDefault();
            nidData.setDefault();
            Nid currNode;
            currNode = new Path(parName, this.subtree.tree).toNid();
            prevDefNid.setDefault();
            return currNode;
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Error getting Component Nid in DeviceParameters: " + exc);
            return null;
        }
    }

    @Override
    protected int getNumComponents(final Nid nidData) {
        try{
            final Nid prevDefNid = this.subtree.getDefault();
            nidData.setDefault();
            Nid currNode;
            currNode = new Path(":NUM_ACTIVE", this.subtree.tree).toNid();
            final int numComponents = this.subtree.tdiEvaluate(currNode).toInt();
            prevDefNid.setDefault();
            return numComponents;
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Error getting Num Components in DeviceParameters: " + exc);
            return 0;
        }
    }

    @SuppressWarnings("static-method")
    protected String getParameterName() {
        return "PAR";
    }

    @Override
    protected void resetComponent(final Nid nidData) {
        for(int i = 0; i < this.parameters.size(); i++){
            this.parameters.elementAt(i).reset();
        }
    }
}

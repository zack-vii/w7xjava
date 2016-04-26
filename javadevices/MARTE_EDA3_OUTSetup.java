import devicebeans.DeviceButtons;
import devicebeans.DeviceChoice;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;
import devicebeans.DeviceTable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * MARTE_EDA3_OUTSetup.java
 * Created on Mar 2, 2011, 1:07:35 PM
 */
/**
 * @author manduchi
 */
public class MARTE_EDA3_OUTSetup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = -5499290731805665989L;
    /** Creates new form MARTE_EDA3_OUTSetup */
    public MARTE_EDA3_OUTSetup(){
        initComponents();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        deviceButtons1 = new DeviceButtons();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        deviceField1 = new DeviceField();
        jPanel6 = new javax.swing.JPanel();
        deviceField2 = new DeviceField();
        deviceField3 = new DeviceField();
        jPanel8 = new javax.swing.JPanel();
        deviceChoice1 = new DeviceChoice();
        deviceField6 = new DeviceField();
        jPanel9 = new javax.swing.JPanel();
        deviceField7 = new DeviceField();
        deviceField8 = new DeviceField();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        deviceTable3 = new DeviceTable();
        jPanel10 = new javax.swing.JPanel();
        deviceTable1 = new DeviceTable();
        setDeviceProvider("localhost");
        setDeviceTitle("MARTe MhdAcBc Setup");
        setDeviceType("MARTE_EDA3_OUT");
        setHeight(400);
        setWidth(600);
        getContentPane().add(deviceButtons1, java.awt.BorderLayout.PAGE_END);
        jPanel2.setLayout(new java.awt.GridLayout(4, 0));
        deviceField1.setIdentifier("");
        deviceField1.setLabelString("Comment: ");
        deviceField1.setNumCols(30);
        deviceField1.setOffsetNid(1);
        deviceField1.setTextOnly(true);
        jPanel5.add(deviceField1);
        jPanel2.add(jPanel5);
        deviceField2.setIdentifier("");
        deviceField2.setLabelString("Start Sampling(s):");
        deviceField2.setOffsetNid(8);
        jPanel6.add(deviceField2);
        deviceField3.setIdentifier("");
        deviceField3.setLabelString("End Sampling(s):");
        deviceField3.setOffsetNid(9);
        jPanel6.add(deviceField3);
        jPanel2.add(jPanel6);
        deviceChoice1.setChoiceItems(new String[]{"Eda2DacControl"});
        deviceChoice1.setIdentifier("");
        deviceChoice1.setLabelString("Control: ");
        deviceChoice1.setOffsetNid(12);
        deviceChoice1.setUpdateIdentifier("");
        jPanel8.add(deviceChoice1);
        deviceField6.setIdentifier("");
        deviceField6.setLabelString("Contr. Duration(s): ");
        deviceField6.setOffsetNid(5);
        jPanel8.add(deviceField6);
        jPanel2.add(jPanel8);
        deviceField7.setIdentifier("");
        deviceField7.setLabelString("Trig. Time: ");
        deviceField7.setNumCols(25);
        deviceField7.setOffsetNid(4);
        jPanel9.add(deviceField7);
        deviceField8.setIdentifier("");
        deviceField8.setLabelString("Freq. (Hz):");
        deviceField8.setOffsetNid(3);
        jPanel9.add(deviceField8);
        jPanel2.add(jPanel9);
        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel3.setLayout(new java.awt.GridLayout(1, 1));
        deviceTable3.setDisplayRowNumber(true);
        deviceTable3.setIdentifier("");
        deviceTable3.setLabelString("Mapping:");
        deviceTable3.setNumCols(1);
        deviceTable3.setNumRows(36);
        deviceTable3.setOffsetNid(1339);
        deviceTable3.setPreferredColumnWidth(60);
        deviceTable3.setPreferredHeight(200);
        deviceTable3.setUseExpressions(true);
        jPanel3.add(deviceTable3);
        jTabbedPane1.addTab("Mapping", jPanel3);
        jPanel10.setLayout(new java.awt.BorderLayout());
        deviceTable1.setColumnNames(new String[]{"Gain", "Offset"});
        deviceTable1.setDisplayRowNumber(true);
        deviceTable1.setIdentifier("");
        deviceTable1.setNumCols(2);
        deviceTable1.setNumRows(36);
        deviceTable1.setOffsetNid(11);
        deviceTable1.setUseExpressions(true);
        jPanel10.add(deviceTable1, java.awt.BorderLayout.CENTER);
        jTabbedPane1.addTab("Out Calibration", jPanel10);
        jPanel1.add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DeviceButtons           deviceButtons1;
    private DeviceChoice            deviceChoice1;
    private DeviceField             deviceField1;
    private DeviceField             deviceField2;
    private DeviceField             deviceField3;
    private DeviceField             deviceField6;
    private DeviceField             deviceField7;
    private DeviceField             deviceField8;
    private DeviceTable             deviceTable1;
    private DeviceTable             deviceTable3;
    private javax.swing.JPanel      jPanel1;
    private javax.swing.JPanel      jPanel10;
    private javax.swing.JPanel      jPanel2;
    private javax.swing.JPanel      jPanel3;
    private javax.swing.JPanel      jPanel5;
    private javax.swing.JPanel      jPanel6;
    private javax.swing.JPanel      jPanel8;
    private javax.swing.JPanel      jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}

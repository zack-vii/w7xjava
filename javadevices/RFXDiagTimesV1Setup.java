import devicebeans.DeviceButtons;
import devicebeans.DeviceChoice;
import devicebeans.DeviceDispatch;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;
import devicebeans.DeviceTable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * RFXDiagTimesV1Setup.java
 * Created on 16-Jan-2014, 13:27:16
 */
/**
 * @author taliercio
 */
public class RFXDiagTimesV1Setup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = 6254014606468142284L;
    /** Creates new form RFXDiagTimesV1Setup */
    public RFXDiagTimesV1Setup(){
        initComponents();
        setSize(240, 550);
        this.pack();
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
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        deviceField2 = new DeviceField();
        deviceDispatch1 = new DeviceDispatch();
        deviceButtons1 = new DeviceButtons();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        deviceChoice1 = new DeviceChoice();
        deviceField1 = new DeviceField();
        jPanel5 = new javax.swing.JPanel();
        deviceTable1 = new DeviceTable();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        deviceChoice2 = new DeviceChoice();
        setDeviceProvider("localhost");
        setDeviceTitle("RFX Diagnostic Timing Configuration");
        setDeviceType("RFXDiagTimesV1");
        jPanel1.setLayout(new java.awt.BorderLayout());
        deviceField2.setIdentifier("");
        deviceField2.setLabelString("Comment:");
        deviceField2.setNumCols(20);
        deviceField2.setOffsetNid(14);
        deviceField2.setTextOnly(true);
        jPanel7.add(deviceField2);
        jPanel7.add(deviceDispatch1);
        jPanel1.add(jPanel7, java.awt.BorderLayout.NORTH);
        jPanel1.add(deviceButtons1, java.awt.BorderLayout.SOUTH);
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel3.setLayout(new java.awt.GridLayout(2, 0));
        deviceChoice1.setChoiceItems(new String[]{"EXT_DT", "EXT_10_DT", "EXT_RT"});
        deviceChoice1.setIdentifier("");
        deviceChoice1.setLabelString("Trigger Mode:");
        deviceChoice1.setOffsetNid(3);
        deviceChoice1.setUpdateIdentifier("");
        jPanel4.add(deviceChoice1);
        deviceField1.setIdentifier("");
        deviceField1.setLabelString("Trigger Time:");
        deviceField1.setOffsetNid(2);
        jPanel4.add(deviceField1);
        jPanel3.add(jPanel4);
        jPanel5.setLayout(new java.awt.BorderLayout());
        deviceTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        deviceTable1.setDisplayRowNumber(true);
        deviceTable1.setIdentifier("");
        deviceTable1.setLabelString("Delay Pulse");
        deviceTable1.setNumCols(10);
        deviceTable1.setNumRows(1);
        deviceTable1.setOffsetNid(5);
        deviceTable1.setRowNames(new String[]{"Delay_Pulse"});
        jPanel5.add(deviceTable1, java.awt.BorderLayout.CENTER);
        jPanel3.add(jPanel5);
        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);
        jTabbedPane1.addTab("Thomson Scattering", jPanel2);
        jPanel6.setLayout(new java.awt.BorderLayout());
        deviceChoice2.setChoiceItems(new String[]{"ENABLED", "DISABLED"});
        deviceChoice2.setIdentifier("");
        deviceChoice2.setLabelString("Gas Puffing Imaging Trigger: ");
        deviceChoice2.setOffsetNid(12);
        deviceChoice2.setUpdateIdentifier("");
        jPanel8.add(deviceChoice2);
        jPanel6.add(jPanel8, java.awt.BorderLayout.CENTER);
        jTabbedPane1.addTab("Gas Puffing Imaging", jPanel6);
        jPanel1.add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DeviceButtons           deviceButtons1;
    private DeviceChoice            deviceChoice1;
    private DeviceChoice            deviceChoice2;
    private DeviceDispatch          deviceDispatch1;
    private DeviceField             deviceField1;
    private DeviceField             deviceField2;
    private DeviceTable             deviceTable1;
    private javax.swing.JPanel      jPanel1;
    private javax.swing.JPanel      jPanel2;
    private javax.swing.JPanel      jPanel3;
    private javax.swing.JPanel      jPanel4;
    private javax.swing.JPanel      jPanel5;
    private javax.swing.JPanel      jPanel6;
    private javax.swing.JPanel      jPanel7;
    private javax.swing.JPanel      jPanel8;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}

import devicebeans.DeviceButtons;
import devicebeans.DeviceChoice;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;
import devicebeans.DeviceTable;
import devicebeans.DeviceTableBeanInfo;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * MARTE_MHD_BRSetup.java
 * Created on Mar 2, 2011, 1:07:35 PM
 */
/**
 * @author manduchi
 */
public class MARTE_MHD_BRSetup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = 4257790186338386370L;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DeviceButtons           deviceButtons1;
    private DeviceChoice            deviceChoice1;
    private DeviceField             deviceField1;
    private DeviceField             deviceField10;
    private DeviceField             deviceField11;
    private DeviceField             deviceField2;
    private DeviceField             deviceField3;
    private DeviceField             deviceField4;
    private DeviceField             deviceField5;
    private DeviceField             deviceField6;
    private DeviceField             deviceField7;
    private DeviceField             deviceField8;
    private DeviceField             deviceField9;
    private DeviceTable             deviceTable1;
    private DeviceTable             deviceTable2;
    private DeviceTable             deviceTable3;
    private DeviceTable             deviceTable4;
    private DeviceTableBeanInfo     deviceTableBeanInfo1;
    private javax.swing.JPanel      jPanel1;
    private javax.swing.JPanel      jPanel10;
    private javax.swing.JPanel      jPanel11;
    private javax.swing.JPanel      jPanel2;
    private javax.swing.JPanel      jPanel3;
    private javax.swing.JPanel      jPanel4;
    private javax.swing.JPanel      jPanel5;
    private javax.swing.JPanel      jPanel6;
    private javax.swing.JPanel      jPanel7;
    private javax.swing.JPanel      jPanel8;
    private javax.swing.JPanel      jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;

    // End of variables declaration//GEN-END:variables
    /** Creates new form MARTE_MHD_BRSetup */
    public MARTE_MHD_BRSetup(){
        this.initComponents();
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
        this.deviceTableBeanInfo1 = new DeviceTableBeanInfo();
        this.deviceButtons1 = new DeviceButtons();
        this.jPanel2 = new javax.swing.JPanel();
        this.jPanel5 = new javax.swing.JPanel();
        this.deviceField1 = new DeviceField();
        this.jPanel6 = new javax.swing.JPanel();
        this.deviceField2 = new DeviceField();
        this.deviceField3 = new DeviceField();
        this.jPanel7 = new javax.swing.JPanel();
        this.deviceField4 = new DeviceField();
        this.deviceField5 = new DeviceField();
        this.jPanel8 = new javax.swing.JPanel();
        this.deviceChoice1 = new DeviceChoice();
        this.deviceField6 = new DeviceField();
        this.jPanel9 = new javax.swing.JPanel();
        this.deviceField7 = new DeviceField();
        this.deviceField8 = new DeviceField();
        this.jPanel1 = new javax.swing.JPanel();
        this.jTabbedPane1 = new javax.swing.JTabbedPane();
        this.jPanel3 = new javax.swing.JPanel();
        this.deviceTable3 = new DeviceTable();
        this.deviceTable4 = new DeviceTable();
        this.jPanel4 = new javax.swing.JPanel();
        this.deviceField9 = new DeviceField();
        this.deviceField10 = new DeviceField();
        this.deviceField11 = new DeviceField();
        this.jPanel10 = new javax.swing.JPanel();
        this.deviceTable1 = new DeviceTable();
        this.jScrollPane1 = new javax.swing.JScrollPane();
        this.jPanel11 = new javax.swing.JPanel();
        this.deviceTable2 = new DeviceTable();
        this.setDeviceProvider("localhost");
        this.setDeviceTitle("MARTe MhdBr Setup");
        this.setDeviceType("MARTE_MHD_BR");
        this.setHeight(400);
        this.setWidth(600);
        this.getContentPane().add(this.deviceButtons1, java.awt.BorderLayout.PAGE_END);
        this.jPanel2.setLayout(new java.awt.GridLayout(5, 0));
        this.deviceField1.setIdentifier("");
        this.deviceField1.setLabelString("Comment: ");
        this.deviceField1.setNumCols(30);
        this.deviceField1.setOffsetNid(1);
        this.deviceField1.setTextOnly(true);
        this.jPanel5.add(this.deviceField1);
        this.jPanel2.add(this.jPanel5);
        this.deviceField2.setIdentifier("");
        this.deviceField2.setLabelString("Start Sampling(s):");
        this.deviceField2.setOffsetNid(8);
        this.jPanel6.add(this.deviceField2);
        this.deviceField3.setIdentifier("");
        this.deviceField3.setLabelString("End Sampling(s):");
        this.deviceField3.setOffsetNid(9);
        this.jPanel6.add(this.deviceField3);
        this.jPanel2.add(this.jPanel6);
        this.deviceField4.setIdentifier("");
        this.deviceField4.setLabelString("Start Offset Comp(s).:");
        this.deviceField4.setOffsetNid(6);
        this.jPanel7.add(this.deviceField4);
        this.deviceField5.setIdentifier("");
        this.deviceField5.setLabelString("End Offset Comp.(s):");
        this.deviceField5.setOffsetNid(7);
        this.jPanel7.add(this.deviceField5);
        this.jPanel2.add(this.jPanel7);
        this.deviceChoice1.setChoiceItems(new String[]{"MhdBrControl"});
        this.deviceChoice1.setIdentifier("");
        this.deviceChoice1.setLabelString("Control: ");
        this.deviceChoice1.setOffsetNid(12);
        this.deviceChoice1.setUpdateIdentifier("");
        this.jPanel8.add(this.deviceChoice1);
        this.deviceField6.setIdentifier("");
        this.deviceField6.setLabelString("Contr. Duration(s): ");
        this.deviceField6.setOffsetNid(5);
        this.jPanel8.add(this.deviceField6);
        this.jPanel2.add(this.jPanel8);
        this.deviceField7.setIdentifier("");
        this.deviceField7.setLabelString("Trig. Time: ");
        this.deviceField7.setNumCols(25);
        this.deviceField7.setOffsetNid(4);
        this.jPanel9.add(this.deviceField7);
        this.deviceField8.setIdentifier("");
        this.deviceField8.setLabelString("Freq. (Hz):");
        this.deviceField8.setOffsetNid(3);
        this.jPanel9.add(this.deviceField8);
        this.jPanel2.add(this.jPanel9);
        this.getContentPane().add(this.jPanel2, java.awt.BorderLayout.PAGE_START);
        this.jPanel1.setLayout(new java.awt.BorderLayout());
        this.jPanel3.setLayout(new java.awt.GridLayout(1, 2));
        this.deviceTable3.setDisplayRowNumber(true);
        this.deviceTable3.setIdentifier("");
        this.deviceTable3.setLabelString("Mapping:");
        this.deviceTable3.setNumCols(1);
        this.deviceTable3.setNumRows(192);
        this.deviceTable3.setOffsetNid(1339);
        this.deviceTable3.setPreferredColumnWidth(60);
        this.deviceTable3.setPreferredHeight(200);
        this.deviceTable3.setUseExpressions(true);
        this.jPanel3.add(this.deviceTable3);
        this.deviceTable4.setBinary(true);
        this.deviceTable4.setDisplayRowNumber(true);
        this.deviceTable4.setIdentifier("");
        this.deviceTable4.setLabelString("Autozero Mask");
        this.deviceTable4.setNumCols(1);
        this.deviceTable4.setNumRows(192);
        this.deviceTable4.setOffsetNid(1345);
        this.deviceTable4.setPreferredColumnWidth(4);
        this.deviceTable4.setPreferredHeight(200);
        this.jPanel3.add(this.deviceTable4);
        this.jTabbedPane1.addTab("Mapping&Offset", this.jPanel3);
        this.deviceField9.setIdentifier("");
        this.deviceField9.setLabelString("Max. Br Horizontal Probes (T): ");
        this.deviceField9.setNumCols(20);
        this.deviceField9.setOffsetNid(1351);
        this.jPanel4.add(this.deviceField9);
        this.deviceField10.setIdentifier("");
        this.deviceField10.setLabelString("Max. Br Vertical Probes (T): ");
        this.deviceField10.setNumCols(20);
        this.deviceField10.setOffsetNid(1357);
        this.jPanel4.add(this.deviceField10);
        this.deviceField11.setIdentifier("");
        this.deviceField11.setLabelString("Max time period above threshold (s):");
        this.deviceField11.setNumCols(20);
        this.deviceField11.setOffsetNid(1363);
        this.jPanel4.add(this.deviceField11);
        this.jTabbedPane1.addTab("Alarms", this.jPanel4);
        this.jPanel10.setLayout(new java.awt.BorderLayout());
        this.deviceTable1.setColumnNames(new String[]{"Gain", "Offset"});
        this.deviceTable1.setDisplayRowNumber(true);
        this.deviceTable1.setIdentifier("");
        this.deviceTable1.setNumCols(2);
        this.deviceTable1.setNumRows(192);
        this.deviceTable1.setOffsetNid(10);
        this.deviceTable1.setUseExpressions(true);
        this.jPanel10.add(this.deviceTable1, java.awt.BorderLayout.CENTER);
        this.jTabbedPane1.addTab("In Calibration", this.jPanel10);
        this.jPanel11.setLayout(new java.awt.BorderLayout());
        this.deviceTable2.setColumnNames(new String[]{"1", "2", "3", "4"});
        this.deviceTable2.setDisplayRowNumber(true);
        this.deviceTable2.setIdentifier("");
        this.deviceTable2.setNumCols(4);
        this.deviceTable2.setNumRows(48);
        this.deviceTable2.setOffsetNid(1369);
        this.deviceTable2.setPreferredColumnWidth(100);
        this.jPanel11.add(this.deviceTable2, java.awt.BorderLayout.CENTER);
        this.jScrollPane1.setViewportView(this.jPanel11);
        this.jTabbedPane1.addTab("Br Corrections", this.jScrollPane1);
        this.jPanel1.add(this.jTabbedPane1, java.awt.BorderLayout.CENTER);
        this.getContentPane().add(this.jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
}

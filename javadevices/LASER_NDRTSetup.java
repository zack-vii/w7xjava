import devicebeans.DeviceButtons;
import devicebeans.DeviceChoice;
import devicebeans.DeviceDispatch;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;
import devicebeans.DeviceTable;

/*
 * LASER_NDRTSetup.java
 * Created on May 12, 2009, 6:58 PM
 */
/**
 * @author taliercio
 */
public class LASER_NDRTSetup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = -9098379968963068187L;
    /** Creates new form LASER_NDRTSetup */
    public LASER_NDRTSetup(){
        initComponents();
        this.pack();
        setSize(800, 450);
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
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        deviceField1 = new DeviceField();
        deviceDispatch1 = new DeviceDispatch();
        jPanel3 = new javax.swing.JPanel();
        deviceField2 = new DeviceField();
        deviceField3 = new DeviceField();
        jPanel4 = new javax.swing.JPanel();
        deviceChoice1 = new DeviceChoice();
        deviceField4 = new DeviceField();
        deviceField6 = new DeviceField();
        jPanel5 = new javax.swing.JPanel();
        deviceTable1 = new DeviceTable();
        jPanel6 = new javax.swing.JPanel();
        deviceChoice2 = new DeviceChoice();
        deviceField5 = new DeviceField();
        jPanel7 = new javax.swing.JPanel();
        deviceField8 = new DeviceField();
        deviceField9 = new DeviceField();
        jPanel8 = new javax.swing.JPanel();
        deviceField10 = new DeviceField();
        jLabel2 = new javax.swing.JLabel();
        deviceField11 = new DeviceField();
        jLabel4 = new javax.swing.JLabel();
        setDeviceProvider("localhost");
        setDeviceTitle("Laser Neodymium Yag  Real Time Trigger");
        setDeviceType("LASER_NDRT");
        deviceButtons1.setCheckExpressions(new String[]{});
        deviceButtons1.setCheckMessages(new String[]{});
        deviceButtons1.setMethods(new String[]{"init", "dump", "store"});
        getContentPane().add(deviceButtons1, java.awt.BorderLayout.PAGE_END);
        jPanel1.setLayout(new java.awt.GridLayout(7, 0));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        deviceField1.setIdentifier("");
        deviceField1.setLabelString("Comment:");
        deviceField1.setNumCols(40);
        deviceField1.setOffsetNid(1);
        deviceField1.setTextOnly(true);
        jPanel2.add(deviceField1);
        jPanel2.add(deviceDispatch1);
        jPanel1.add(jPanel2);
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        deviceField2.setIdentifier("");
        deviceField2.setLabelString("Controller addr");
        deviceField2.setNumCols(25);
        deviceField2.setOffsetNid(2);
        deviceField2.setTextOnly(true);
        jPanel3.add(deviceField2);
        deviceField3.setIdentifier("");
        deviceField3.setLabelString("Port:");
        deviceField3.setOffsetNid(3);
        jPanel3.add(deviceField3);
        jPanel1.add(jPanel3);
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        deviceChoice1.setChoiceItems(new String[]{"EXT_DT", "EXT_10_DT", "EXT_RT"});
        deviceChoice1.setIdentifier("");
        deviceChoice1.setLabelString("Trigger Mode:");
        deviceChoice1.setOffsetNid(5);
        deviceChoice1.setUpdateIdentifier("");
        jPanel4.add(deviceChoice1);
        deviceField4.setIdentifier("");
        deviceField4.setLabelString("Trigger Source:");
        deviceField4.setNumCols(15);
        deviceField4.setOffsetNid(6);
        jPanel4.add(deviceField4);
        deviceField6.setIdentifier("");
        deviceField6.setLabelString("Num. Pulses:");
        deviceField6.setOffsetNid(8);
        jPanel4.add(deviceField6);
        jPanel1.add(jPanel4);
        jPanel5.setLayout(new java.awt.BorderLayout());
        deviceTable1.setColumnNames(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        deviceTable1.setIdentifier("");
        deviceTable1.setNumCols(10);
        deviceTable1.setNumRows(1);
        deviceTable1.setOffsetNid(9);
        deviceTable1.setPreferredColumnWidth(20);
        deviceTable1.setRowNames(new String[]{"Delay_Pulse"});
        deviceTable1.setUseExpressions(true);
        jPanel5.add(deviceTable1, java.awt.BorderLayout.CENTER);
        jPanel1.add(jPanel5);
        deviceChoice2.setChoiceItems(new String[]{"M1_N7_AMP", "M1_N7_AMP_PH", "RATIO_DOM_SEC", "RATIO_DOM_SEC_PH", "M0_N1_AMP", "M0_N1_AMP_PH", "M0_N7_AMP", "M0_N7_AMP_PH"});
        deviceChoice2.setIdentifier("");
        deviceChoice2.setLabelString("RT Trig. Mode:");
        deviceChoice2.setOffsetNid(7);
        deviceChoice2.setUpdateIdentifier("");
        jPanel6.add(deviceChoice2);
        deviceField5.setIdentifier("");
        deviceField5.setLabelString("RT IP addr:");
        deviceField5.setNumCols(20);
        deviceField5.setOffsetNid(4);
        deviceField5.setTextOnly(true);
        jPanel6.add(deviceField5);
        jPanel1.add(jPanel6);
        deviceField8.setIdentifier("");
        deviceField8.setLabelString("Amplitude [T]  OR Dom/Sec Min :");
        deviceField8.setNumCols(12);
        deviceField8.setOffsetNid(11);
        jPanel7.add(deviceField8);
        deviceField9.setIdentifier("");
        deviceField9.setLabelString("Amplitude [T]  OR Dom/Sec Max :");
        deviceField9.setNumCols(12);
        deviceField9.setOffsetNid(10);
        jPanel7.add(deviceField9);
        jPanel1.add(jPanel7);
        deviceField10.setIdentifier("");
        deviceField10.setLabelString("Phase Min :");
        deviceField10.setNumCols(15);
        deviceField10.setOffsetNid(13);
        jPanel8.add(deviceField10);
        jLabel2.setText("[º]");
        jPanel8.add(jLabel2);
        deviceField11.setIdentifier("");
        deviceField11.setLabelString("Phase Max :");
        deviceField11.setNumCols(15);
        deviceField11.setOffsetNid(12);
        jPanel8.add(deviceField11);
        jLabel4.setText("[º]");
        jPanel8.add(jLabel4);
        jPanel1.add(jPanel8);
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DeviceButtons      deviceButtons1;
    private DeviceChoice       deviceChoice1;
    private DeviceChoice       deviceChoice2;
    private DeviceDispatch     deviceDispatch1;
    private DeviceField        deviceField1;
    private DeviceField        deviceField10;
    private DeviceField        deviceField11;
    private DeviceField        deviceField2;
    private DeviceField        deviceField3;
    private DeviceField        deviceField4;
    private DeviceField        deviceField5;
    private DeviceField        deviceField6;
    private DeviceField        deviceField8;
    private DeviceField        deviceField9;
    private DeviceTable        deviceTable1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    // End of variables declaration//GEN-END:variables
}

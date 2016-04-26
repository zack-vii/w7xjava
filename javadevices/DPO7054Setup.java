import devicebeans.DeviceButtons;
import devicebeans.DeviceChannel;
import devicebeans.DeviceChoice;
import devicebeans.DeviceDispatch;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;

/*
 * DPO7054Setup.java
 * Created on July 14, 2008, 9:47 AM
 */
/**
 * @author Administrator
 */
public class DPO7054Setup extends DeviceSetup{
    /**
     *
     */
    private static final long       serialVersionUID = -3203052710855492064L;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DeviceButtons           deviceButtons2;
    private DeviceChannel           deviceChannel1;
    private DeviceChannel           deviceChannel3;
    private DeviceChannel           deviceChannel4;
    private DeviceChannel           deviceChannel5;
    private DeviceChannel           deviceChannel9;
    private DeviceChoice            deviceChoice1;
    private DeviceChoice            deviceChoice12;
    private DeviceChoice            deviceChoice13;
    private DeviceChoice            deviceChoice15;
    private DeviceChoice            deviceChoice16;
    private DeviceChoice            deviceChoice18;
    private DeviceChoice            deviceChoice19;
    private DeviceChoice            deviceChoice2;
    private DeviceChoice            deviceChoice21;
    private DeviceChoice            deviceChoice24;
    private DeviceChoice            deviceChoice27;
    private DeviceChoice            deviceChoice3;
    private DeviceChoice            deviceChoice30;
    private DeviceChoice            deviceChoice34;
    private DeviceChoice            deviceChoice35;
    private DeviceChoice            deviceChoice36;
    private DeviceChoice            deviceChoice37;
    private DeviceChoice            deviceChoice4;
    private DeviceChoice            deviceChoice48;
    private DeviceChoice            deviceChoice52;
    private DeviceChoice            deviceChoice56;
    private DeviceChoice            deviceChoice6;
    private DeviceChoice            deviceChoice60;
    private DeviceChoice            deviceChoice7;
    private DeviceDispatch          deviceDispatch1;
    private DeviceField             deviceField1;
    private DeviceField             deviceField10;
    private DeviceField             deviceField11;
    private DeviceField             deviceField12;
    private DeviceField             deviceField13;
    private DeviceField             deviceField14;
    private DeviceField             deviceField15;
    private DeviceField             deviceField16;
    private DeviceField             deviceField17;
    private DeviceField             deviceField18;
    private DeviceField             deviceField2;
    private DeviceField             deviceField29;
    private DeviceField             deviceField31;
    private DeviceField             deviceField32;
    private DeviceField             deviceField33;
    private DeviceField             deviceField6;
    private DeviceField             deviceField7;
    private DeviceField             deviceField9;
    private javax.swing.JPanel      jPanel1;
    private javax.swing.JPanel      jPanel10;
    private javax.swing.JPanel      jPanel11;
    private javax.swing.JPanel      jPanel12;
    private javax.swing.JPanel      jPanel14;
    private javax.swing.JPanel      jPanel15;
    private javax.swing.JPanel      jPanel16;
    private javax.swing.JPanel      jPanel17;
    private javax.swing.JPanel      jPanel18;
    private javax.swing.JPanel      jPanel19;
    private javax.swing.JPanel      jPanel21;
    private javax.swing.JPanel      jPanel22;
    private javax.swing.JPanel      jPanel23;
    private javax.swing.JPanel      jPanel24;
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
    /** Creates new form DPO7054Setup */
    public DPO7054Setup(){
        this.initComponents();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        this.jScrollPane1 = new javax.swing.JScrollPane();
        this.jPanel1 = new javax.swing.JPanel();
        this.jPanel6 = new javax.swing.JPanel();
        this.deviceField9 = new DeviceField();
        this.deviceDispatch1 = new DeviceDispatch();
        this.jTabbedPane1 = new javax.swing.JTabbedPane();
        this.jPanel3 = new javax.swing.JPanel();
        this.jPanel4 = new javax.swing.JPanel();
        this.deviceChoice4 = new DeviceChoice();
        this.jPanel5 = new javax.swing.JPanel();
        this.deviceChoice1 = new DeviceChoice();
        this.deviceChoice2 = new DeviceChoice();
        this.deviceChoice3 = new DeviceChoice();
        this.deviceField1 = new DeviceField();
        this.deviceField2 = new DeviceField();
        this.deviceField29 = new DeviceField();
        this.jPanel7 = new javax.swing.JPanel();
        this.deviceChannel1 = new DeviceChannel();
        this.deviceChoice6 = new DeviceChoice();
        this.deviceChoice7 = new DeviceChoice();
        this.deviceField6 = new DeviceField();
        this.deviceField7 = new DeviceField();
        this.deviceChoice34 = new DeviceChoice();
        this.jPanel8 = new javax.swing.JPanel();
        this.deviceChannel3 = new DeviceChannel();
        this.deviceChoice12 = new DeviceChoice();
        this.deviceChoice13 = new DeviceChoice();
        this.deviceField10 = new DeviceField();
        this.deviceField11 = new DeviceField();
        this.deviceChoice35 = new DeviceChoice();
        this.jPanel9 = new javax.swing.JPanel();
        this.deviceChannel4 = new DeviceChannel();
        this.deviceChoice15 = new DeviceChoice();
        this.deviceChoice16 = new DeviceChoice();
        this.deviceField12 = new DeviceField();
        this.deviceField13 = new DeviceField();
        this.deviceChoice36 = new DeviceChoice();
        this.jPanel10 = new javax.swing.JPanel();
        this.deviceChannel5 = new DeviceChannel();
        this.deviceChoice18 = new DeviceChoice();
        this.deviceChoice19 = new DeviceChoice();
        this.deviceField14 = new DeviceField();
        this.deviceField15 = new DeviceField();
        this.deviceChoice37 = new DeviceChoice();
        this.jPanel18 = new javax.swing.JPanel();
        this.jPanel19 = new javax.swing.JPanel();
        this.deviceField31 = new DeviceField();
        this.deviceField32 = new DeviceField();
        this.deviceField33 = new DeviceField();
        this.jPanel21 = new javax.swing.JPanel();
        this.deviceChoice48 = new DeviceChoice();
        this.jPanel22 = new javax.swing.JPanel();
        this.deviceChoice52 = new DeviceChoice();
        this.jPanel23 = new javax.swing.JPanel();
        this.deviceChoice56 = new DeviceChoice();
        this.jPanel24 = new javax.swing.JPanel();
        this.deviceChoice60 = new DeviceChoice();
        this.jPanel11 = new javax.swing.JPanel();
        this.deviceChannel9 = new DeviceChannel();
        this.jPanel12 = new javax.swing.JPanel();
        this.deviceField16 = new DeviceField();
        this.deviceField17 = new DeviceField();
        this.deviceField18 = new DeviceField();
        this.jPanel14 = new javax.swing.JPanel();
        this.deviceChoice21 = new DeviceChoice();
        this.jPanel15 = new javax.swing.JPanel();
        this.deviceChoice24 = new DeviceChoice();
        this.jPanel16 = new javax.swing.JPanel();
        this.deviceChoice27 = new DeviceChoice();
        this.jPanel17 = new javax.swing.JPanel();
        this.deviceChoice30 = new DeviceChoice();
        this.deviceButtons2 = new DeviceButtons();
        this.setDeviceProvider("localhost");
        this.setDeviceTitle("DPO7054");
        this.setDeviceType("DPO7054");
        this.setHeight(600);
        this.setWidth(970);
        this.jPanel1.setLayout(new java.awt.BorderLayout());
        this.jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("GENERAL"));
        this.jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceField9.setIdentifier("");
        this.deviceField9.setLabelString("Comment:");
        this.deviceField9.setNumCols(40);
        this.deviceField9.setOffsetNid(1);
        this.jPanel6.add(this.deviceField9);
        this.jPanel6.add(this.deviceDispatch1);
        this.jPanel1.add(this.jPanel6, java.awt.BorderLayout.NORTH);
        this.jPanel3.setLayout(new java.awt.GridLayout(6, 0));
        this.jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("HORIZONTAL - ACQ"));
        this.jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice4.setChoiceFloatValues(new float[]{(float)5.0E8, (float)2.5E8, (float)2.0E8, (float)1.25E8, (float)1.0E8, (float)6.25E7, (float)5.0E7, (float)2.5E7, (float)2.0E7, (float)1.25E7, (float)1.0E7, (float)6250000.0, (float)5000000.0, (float)2500000.0, (float)2000000.0, (float)1000000.0});
        this.deviceChoice4.setChoiceItems(new String[]{"500E6", "250E6", "200E6", "125E6", "100E6", "62.5E6", "50E6", "25E6", "20E6", "12.5E6", "10E6", "6.25E6", "5E6", "2.5E6", "2E6", "1E6"});
        this.deviceChoice4.setIdentifier("");
        this.deviceChoice4.setLabelString("Sample Rate (S/s):");
        this.deviceChoice4.setOffsetNid(4);
        this.deviceChoice4.setUpdateIdentifier("");
        this.jPanel4.add(this.deviceChoice4);
        this.jPanel3.add(this.jPanel4);
        this.jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("TRIGGER"));
        this.jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice1.setChoiceItems(new String[]{"AUX", "CH1", "CH2", "CH3", "CH4"});
        this.deviceChoice1.setIdentifier("");
        this.deviceChoice1.setLabelString("Source:");
        this.deviceChoice1.setOffsetNid(6);
        this.deviceChoice1.setUpdateIdentifier("");
        this.jPanel5.add(this.deviceChoice1);
        this.deviceChoice2.setChoiceItems(new String[]{"AC", "DC", "HF RJ", "LF RJ", "NOISE RJ"});
        this.deviceChoice2.setIdentifier("");
        this.deviceChoice2.setLabelString("Coupling:");
        this.deviceChoice2.setOffsetNid(7);
        this.deviceChoice2.setUpdateIdentifier("");
        this.jPanel5.add(this.deviceChoice2);
        this.deviceChoice3.setChoiceItems(new String[]{"RISE", "FALL", "EITHER"});
        this.deviceChoice3.setIdentifier("");
        this.deviceChoice3.setLabelString("Slope:");
        this.deviceChoice3.setOffsetNid(8);
        this.deviceChoice3.setUpdateIdentifier("");
        this.jPanel5.add(this.deviceChoice3);
        this.deviceField1.setIdentifier("");
        this.deviceField1.setLabelString("Level (V):");
        this.deviceField1.setNumCols(5);
        this.deviceField1.setOffsetNid(9);
        this.jPanel5.add(this.deviceField1);
        this.deviceField2.setIdentifier("");
        this.deviceField2.setLabelString("Holdoff (s):");
        this.deviceField2.setNumCols(5);
        this.deviceField2.setOffsetNid(10);
        this.jPanel5.add(this.deviceField2);
        this.deviceField29.setIdentifier("");
        this.deviceField29.setLabelString("Trig. Source:");
        this.deviceField29.setNumCols(20);
        this.deviceField29.setOffsetNid(11);
        this.jPanel5.add(this.deviceField29);
        this.jPanel3.add(this.jPanel5);
        this.jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 01"));
        this.jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChannel1.setInSameLine(true);
        this.deviceChannel1.setLabelString("ON - OFF");
        this.deviceChannel1.setOffsetNid(13);
        this.deviceChannel1.setShowVal("");
        this.deviceChannel1.setUpdateIdentifier("");
        this.jPanel7.add(this.deviceChannel1);
        this.deviceChoice6.setChoiceItems(new String[]{"AC", "DC", "GND"});
        this.deviceChoice6.setIdentifier("");
        this.deviceChoice6.setLabelString("Coupling:");
        this.deviceChoice6.setOffsetNid(14);
        this.deviceChoice6.setUpdateIdentifier("");
        this.jPanel7.add(this.deviceChoice6);
        this.deviceChoice7.setChoiceFloatValues(new float[]{(float)1000000.0, (float)50.0});
        this.deviceChoice7.setChoiceItems(new String[]{"1E6", "50"});
        this.deviceChoice7.setIdentifier("");
        this.deviceChoice7.setLabelString("Termination (ohm):");
        this.deviceChoice7.setOffsetNid(15);
        this.deviceChoice7.setUpdateIdentifier("");
        this.jPanel7.add(this.deviceChoice7);
        this.deviceField6.setIdentifier("");
        this.deviceField6.setLabelString("Position (div):");
        this.deviceField6.setNumCols(5);
        this.deviceField6.setOffsetNid(16);
        this.jPanel7.add(this.deviceField6);
        this.deviceField7.setIdentifier("");
        this.deviceField7.setLabelString("Offset (V):");
        this.deviceField7.setNumCols(5);
        this.deviceField7.setOffsetNid(17);
        this.jPanel7.add(this.deviceField7);
        this.deviceChoice34.setChoiceItems(new String[]{"FULL", "20MHz", "250MHz", "500MHz"});
        this.deviceChoice34.setIdentifier("");
        this.deviceChoice34.setLabelString("BW Limit:");
        this.deviceChoice34.setOffsetNid(18);
        this.deviceChoice34.setUpdateIdentifier("");
        this.jPanel7.add(this.deviceChoice34);
        this.jPanel3.add(this.jPanel7);
        this.jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 02"));
        this.jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChannel3.setInSameLine(true);
        this.deviceChannel3.setLabelString("ON - OFF");
        this.deviceChannel3.setOffsetNid(19);
        this.deviceChannel3.setShowVal("");
        this.deviceChannel3.setUpdateIdentifier("");
        this.jPanel8.add(this.deviceChannel3);
        this.deviceChoice12.setChoiceItems(new String[]{"AC", "DC", "GND"});
        this.deviceChoice12.setIdentifier("");
        this.deviceChoice12.setLabelString("Coupling:");
        this.deviceChoice12.setOffsetNid(20);
        this.deviceChoice12.setUpdateIdentifier("");
        this.jPanel8.add(this.deviceChoice12);
        this.deviceChoice13.setChoiceFloatValues(new float[]{(float)1000000.0, (float)50.0});
        this.deviceChoice13.setChoiceItems(new String[]{"1E6", "50"});
        this.deviceChoice13.setIdentifier("");
        this.deviceChoice13.setLabelString("Termination (ohm):");
        this.deviceChoice13.setOffsetNid(21);
        this.deviceChoice13.setUpdateIdentifier("");
        this.jPanel8.add(this.deviceChoice13);
        this.deviceField10.setIdentifier("");
        this.deviceField10.setLabelString("Position (div):");
        this.deviceField10.setNumCols(5);
        this.deviceField10.setOffsetNid(22);
        this.jPanel8.add(this.deviceField10);
        this.deviceField11.setIdentifier("");
        this.deviceField11.setLabelString("Offset (V):");
        this.deviceField11.setNumCols(5);
        this.deviceField11.setOffsetNid(23);
        this.jPanel8.add(this.deviceField11);
        this.deviceChoice35.setChoiceItems(new String[]{"FULL", "20MHz", "250MHz", "500MHz"});
        this.deviceChoice35.setIdentifier("");
        this.deviceChoice35.setLabelString("BW Limit:");
        this.deviceChoice35.setOffsetNid(24);
        this.deviceChoice35.setUpdateIdentifier("");
        this.jPanel8.add(this.deviceChoice35);
        this.jPanel3.add(this.jPanel8);
        this.jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 03"));
        this.jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChannel4.setInSameLine(true);
        this.deviceChannel4.setLabelString("ON - OFF");
        this.deviceChannel4.setOffsetNid(25);
        this.deviceChannel4.setShowVal("");
        this.deviceChannel4.setUpdateIdentifier("");
        this.jPanel9.add(this.deviceChannel4);
        this.deviceChoice15.setChoiceItems(new String[]{"AC", "DC", "GND"});
        this.deviceChoice15.setIdentifier("");
        this.deviceChoice15.setLabelString("Coupling:");
        this.deviceChoice15.setOffsetNid(26);
        this.deviceChoice15.setUpdateIdentifier("");
        this.jPanel9.add(this.deviceChoice15);
        this.deviceChoice16.setChoiceFloatValues(new float[]{(float)1000000.0, (float)50.0});
        this.deviceChoice16.setChoiceItems(new String[]{"1E6", "50"});
        this.deviceChoice16.setIdentifier("");
        this.deviceChoice16.setLabelString("Termination (ohm):");
        this.deviceChoice16.setOffsetNid(27);
        this.deviceChoice16.setUpdateIdentifier("");
        this.jPanel9.add(this.deviceChoice16);
        this.deviceField12.setIdentifier("");
        this.deviceField12.setLabelString("Position (div):");
        this.deviceField12.setNumCols(5);
        this.deviceField12.setOffsetNid(28);
        this.jPanel9.add(this.deviceField12);
        this.deviceField13.setIdentifier("");
        this.deviceField13.setLabelString("Offset (V):");
        this.deviceField13.setNumCols(5);
        this.deviceField13.setOffsetNid(29);
        this.jPanel9.add(this.deviceField13);
        this.deviceChoice36.setChoiceItems(new String[]{"FULL", "20MHz", "250MHz", "500MHz"});
        this.deviceChoice36.setIdentifier("");
        this.deviceChoice36.setLabelString("BW Limit:");
        this.deviceChoice36.setOffsetNid(30);
        this.deviceChoice36.setUpdateIdentifier("");
        this.jPanel9.add(this.deviceChoice36);
        this.jPanel3.add(this.jPanel9);
        this.jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 04"));
        this.jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChannel5.setInSameLine(true);
        this.deviceChannel5.setLabelString("ON - OFF");
        this.deviceChannel5.setOffsetNid(31);
        this.deviceChannel5.setShowVal("");
        this.deviceChannel5.setUpdateIdentifier("");
        this.jPanel10.add(this.deviceChannel5);
        this.deviceChoice18.setChoiceItems(new String[]{"AC", "DC", "GND"});
        this.deviceChoice18.setIdentifier("");
        this.deviceChoice18.setLabelString("Coupling:");
        this.deviceChoice18.setOffsetNid(32);
        this.deviceChoice18.setUpdateIdentifier("");
        this.jPanel10.add(this.deviceChoice18);
        this.deviceChoice19.setChoiceFloatValues(new float[]{(float)1000000.0, (float)50.0});
        this.deviceChoice19.setChoiceItems(new String[]{"1E6", "50"});
        this.deviceChoice19.setIdentifier("");
        this.deviceChoice19.setLabelString("Termination (ohm):");
        this.deviceChoice19.setOffsetNid(33);
        this.deviceChoice19.setUpdateIdentifier("");
        this.jPanel10.add(this.deviceChoice19);
        this.deviceField14.setIdentifier("");
        this.deviceField14.setLabelString("Position (div):");
        this.deviceField14.setNumCols(5);
        this.deviceField14.setOffsetNid(34);
        this.jPanel10.add(this.deviceField14);
        this.deviceField15.setIdentifier("");
        this.deviceField15.setLabelString("Offset (V):");
        this.deviceField15.setNumCols(5);
        this.deviceField15.setOffsetNid(35);
        this.jPanel10.add(this.deviceField15);
        this.deviceChoice37.setChoiceItems(new String[]{"FULL", "20MHz", "250MHz", "500MHz"});
        this.deviceChoice37.setIdentifier("");
        this.deviceChoice37.setLabelString("BW Limit:");
        this.deviceChoice37.setOffsetNid(36);
        this.deviceChoice37.setUpdateIdentifier("");
        this.jPanel10.add(this.deviceChoice37);
        this.jPanel3.add(this.jPanel10);
        this.jTabbedPane1.addTab("COMMON", this.jPanel3);
        this.jPanel18.setLayout(new java.awt.GridLayout(6, 0));
        this.jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("HORIZONTAL - ACQ"));
        this.jPanel19.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceField31.setIdentifier("");
        this.deviceField31.setLabelString("Scale (s/div):");
        this.deviceField31.setNumCols(5);
        this.deviceField31.setOffsetNid(39);
        this.jPanel19.add(this.deviceField31);
        this.deviceField32.setIdentifier("");
        this.deviceField32.setLabelString("Delay (s):");
        this.deviceField32.setNumCols(5);
        this.deviceField32.setOffsetNid(40);
        this.jPanel19.add(this.deviceField32);
        this.deviceField33.setIdentifier("");
        this.deviceField33.setLabelString("Position (%):");
        this.deviceField33.setNumCols(5);
        this.deviceField33.setOffsetNid(41);
        this.jPanel19.add(this.deviceField33);
        this.jPanel18.add(this.jPanel19);
        this.jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 01"));
        this.jPanel21.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice48.setChoiceFloatValues(new float[]{(float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2, (float)0.1, (float)0.05, (float)0.01, (float)0.0050, (float)0.0040, (float)0.0030, (float)0.0020, (float)0.0010});
        this.deviceChoice48.setChoiceItems(new String[]{"10", "5", "2", "1", ".5", ".2", ".1", ".05", ".01", ".005", ".004", ".003", ".002", ".001"});
        this.deviceChoice48.setIdentifier("");
        this.deviceChoice48.setLabelString("Scale (V/div):");
        this.deviceChoice48.setOffsetNid(44);
        this.deviceChoice48.setUpdateIdentifier("");
        this.jPanel21.add(this.deviceChoice48);
        this.jPanel18.add(this.jPanel21);
        this.jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 02"));
        this.jPanel22.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice52.setChoiceFloatValues(new float[]{(float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2, (float)0.1, (float)0.05, (float)0.01, (float)0.0050, (float)0.0040, (float)0.0030, (float)0.0020, (float)0.0010});
        this.deviceChoice52.setChoiceItems(new String[]{"10", "5", "2", "1", ".5", ".2", ".1", ".05", ".01", ".005", ".004", ".003", ".002", ".001"});
        this.deviceChoice52.setIdentifier("");
        this.deviceChoice52.setLabelString("Scale (V/div):");
        this.deviceChoice52.setOffsetNid(47);
        this.deviceChoice52.setUpdateIdentifier("");
        this.jPanel22.add(this.deviceChoice52);
        this.jPanel18.add(this.jPanel22);
        this.jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 03"));
        this.jPanel23.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice56.setChoiceFloatValues(new float[]{(float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2, (float)0.1, (float)0.05, (float)0.01, (float)0.0050, (float)0.0040, (float)0.0030, (float)0.0020, (float)0.0010});
        this.deviceChoice56.setChoiceItems(new String[]{"10", "5", "2", "1", ".5", ".2", ".1", ".05", ".01", ".005", ".004", ".003", ".002", ".001"});
        this.deviceChoice56.setIdentifier("");
        this.deviceChoice56.setLabelString("Scale (V/div):");
        this.deviceChoice56.setOffsetNid(50);
        this.deviceChoice56.setUpdateIdentifier("");
        this.jPanel23.add(this.deviceChoice56);
        this.jPanel18.add(this.jPanel23);
        this.jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 04"));
        this.jPanel24.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice60.setChoiceFloatValues(new float[]{(float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2, (float)0.1, (float)0.05, (float)0.01, (float)0.0050, (float)0.0040, (float)0.0030, (float)0.0020, (float)0.0010});
        this.deviceChoice60.setChoiceItems(new String[]{"10", "5", "2", "1", ".5", ".2", ".1", ".05", ".01", ".005", ".004", ".003", ".002", ".001"});
        this.deviceChoice60.setIdentifier("");
        this.deviceChoice60.setLabelString("Scale (V/div):");
        this.deviceChoice60.setOffsetNid(53);
        this.deviceChoice60.setUpdateIdentifier("");
        this.jPanel24.add(this.deviceChoice60);
        this.jPanel18.add(this.jPanel24);
        this.jTabbedPane1.addTab("WINDOW 01", this.jPanel18);
        this.jPanel11.setLayout(new java.awt.GridLayout(7, 0));
        this.deviceChannel9.setInSameLine(true);
        this.deviceChannel9.setLabelString("ON - OFF");
        this.deviceChannel9.setOffsetNid(55);
        this.deviceChannel9.setShowVal("");
        this.deviceChannel9.setUpdateIdentifier("");
        this.jPanel11.add(this.deviceChannel9);
        this.jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("HORIZONTAL - ACQ"));
        this.jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceField16.setIdentifier("");
        this.deviceField16.setLabelString("Scale (s/div):");
        this.deviceField16.setNumCols(5);
        this.deviceField16.setOffsetNid(57);
        this.jPanel12.add(this.deviceField16);
        this.deviceField17.setIdentifier("");
        this.deviceField17.setLabelString("Delay (s):");
        this.deviceField17.setNumCols(5);
        this.deviceField17.setOffsetNid(58);
        this.jPanel12.add(this.deviceField17);
        this.deviceField18.setIdentifier("");
        this.deviceField18.setLabelString("Position (%):");
        this.deviceField18.setNumCols(5);
        this.deviceField18.setOffsetNid(59);
        this.jPanel12.add(this.deviceField18);
        this.jPanel11.add(this.jPanel12);
        this.jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 01"));
        this.jPanel14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice21.setChoiceFloatValues(new float[]{(float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2, (float)0.1, (float)0.05, (float)0.01, (float)0.0050, (float)0.0040, (float)0.0030, (float)0.0020, (float)0.0010});
        this.deviceChoice21.setChoiceItems(new String[]{"10", "5", "2", "1", ".5", ".2", ".1", ".05", ".01", ".005", ".004", ".003", ".002", ".001"});
        this.deviceChoice21.setIdentifier("");
        this.deviceChoice21.setLabelString("Scale (V/div):");
        this.deviceChoice21.setOffsetNid(62);
        this.deviceChoice21.setUpdateIdentifier("");
        this.jPanel14.add(this.deviceChoice21);
        this.jPanel11.add(this.jPanel14);
        this.jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 02"));
        this.jPanel15.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice24.setChoiceFloatValues(new float[]{(float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2, (float)0.1, (float)0.05, (float)0.01, (float)0.0050, (float)0.0040, (float)0.0030, (float)0.0020, (float)0.0010});
        this.deviceChoice24.setChoiceItems(new String[]{"10", "5", "2", "1", ".5", ".2", ".1", ".05", ".01", ".005", ".004", ".003", ".002", ".001"});
        this.deviceChoice24.setIdentifier("");
        this.deviceChoice24.setLabelString("Scale (V/div):");
        this.deviceChoice24.setOffsetNid(65);
        this.deviceChoice24.setUpdateIdentifier("");
        this.jPanel15.add(this.deviceChoice24);
        this.jPanel11.add(this.jPanel15);
        this.jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 03"));
        this.jPanel16.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice27.setChoiceFloatValues(new float[]{(float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2, (float)0.1, (float)0.05, (float)0.01, (float)0.0050, (float)0.0040, (float)0.0030, (float)0.0020, (float)0.0010});
        this.deviceChoice27.setChoiceItems(new String[]{"10", "5", "2", "1", ".5", ".2", ".1", ".05", ".01", ".005", ".004", ".003", ".002", ".001"});
        this.deviceChoice27.setIdentifier("");
        this.deviceChoice27.setLabelString("Scale (V/div):");
        this.deviceChoice27.setOffsetNid(68);
        this.deviceChoice27.setUpdateIdentifier("");
        this.jPanel16.add(this.deviceChoice27);
        this.jPanel11.add(this.jPanel16);
        this.jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("CH 04"));
        this.jPanel17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        this.deviceChoice30.setChoiceFloatValues(new float[]{(float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2, (float)0.1, (float)0.05, (float)0.01, (float)0.0050, (float)0.0040, (float)0.0030, (float)0.0020, (float)0.0010});
        this.deviceChoice30.setChoiceItems(new String[]{"10", "5", "2", "1", ".5", ".2", ".1", ".05", ".01", ".005", ".004", ".003", ".002", ".001"});
        this.deviceChoice30.setIdentifier("");
        this.deviceChoice30.setLabelString("Scale (V/div):");
        this.deviceChoice30.setOffsetNid(71);
        this.deviceChoice30.setUpdateIdentifier("");
        this.jPanel17.add(this.deviceChoice30);
        this.jPanel11.add(this.jPanel17);
        this.jTabbedPane1.addTab("WINDOW 02", this.jPanel11);
        this.jPanel1.add(this.jTabbedPane1, java.awt.BorderLayout.CENTER);
        this.jScrollPane1.setViewportView(this.jPanel1);
        this.getContentPane().add(this.jScrollPane1, java.awt.BorderLayout.CENTER);
        this.deviceButtons2.setCheckExpressions(new String[]{});
        this.deviceButtons2.setCheckMessages(new String[]{});
        this.deviceButtons2.setMethods(new String[]{"INIT", "ARM", "STORE", "FORCE_TRIGGER", "RESET"});
        this.getContentPane().add(this.deviceButtons2, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
}

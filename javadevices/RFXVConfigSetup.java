import java.awt.Component;
import java.awt.Container;
import javax.swing.JCheckBox;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import devicebeans.DeviceButtons;
import devicebeans.DeviceChoice;
import devicebeans.DeviceDispatch;
import devicebeans.DeviceField;
import devicebeans.DeviceLabel;
import devicebeans.DeviceSetup;
import jTraverser.Database;
import jTraverser.Node;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Nid;

/*
 * RFXVConfigSetup.java
 * Created on Dec 29, 2010, 3:19:31 PM
 */
/**
 * @author taliercio
 */
public class RFXVConfigSetup extends DeviceSetup{
    /**
     *
     */
    private static final long       serialVersionUID = -8716171259925719396L;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JCheckBox   controlled1;
    private javax.swing.JCheckBox   controlled2;
    private javax.swing.JCheckBox   controlled3;
    private DeviceButtons           deviceButtons1;
    private DeviceChoice            deviceChoice1;
    private DeviceChoice            deviceChoice2;
    private DeviceChoice            deviceChoice3;
    private DeviceDispatch          deviceDispatch1;
    private DeviceField             deviceField1;
    private DeviceField             deviceField2;
    private DeviceField             deviceField3;
    private DeviceField             deviceField4;
    private DeviceField             deviceField5;
    private DeviceLabel             deviceLabel1;
    private DeviceLabel             deviceLabel10;
    private DeviceLabel             deviceLabel11;
    private DeviceLabel             deviceLabel2;
    private DeviceLabel             deviceLabel3;
    private DeviceLabel             deviceLabel4;
    private DeviceLabel             deviceLabel6;
    private DeviceLabel             deviceLabel7;
    private DeviceLabel             deviceLabel9;
    private javax.swing.JLabel      jLabel1;
    private javax.swing.JLabel      jLabel2;
    private javax.swing.JLabel      jLabel3;
    private javax.swing.JLabel      jLabel4;
    private javax.swing.JLabel      jLabel5;
    private javax.swing.JPanel      jPanel1;
    private javax.swing.JPanel      jPanel10;
    private javax.swing.JPanel      jPanel11;
    private javax.swing.JPanel      jPanel12;
    private javax.swing.JPanel      jPanel13;
    private javax.swing.JPanel      jPanel14;
    private javax.swing.JPanel      jPanel15;
    private javax.swing.JPanel      jPanel16;
    private javax.swing.JPanel      jPanel17;
    private javax.swing.JPanel      jPanel18;
    private javax.swing.JPanel      jPanel19;
    private javax.swing.JPanel      jPanel2;
    private javax.swing.JPanel      jPanel20;
    private javax.swing.JPanel      jPanel21;
    private javax.swing.JPanel      jPanel22;
    private javax.swing.JPanel      jPanel23;
    private javax.swing.JPanel      jPanel24;
    private javax.swing.JPanel      jPanel25;
    private javax.swing.JPanel      jPanel26;
    private javax.swing.JPanel      jPanel27;
    private javax.swing.JPanel      jPanel28;
    private javax.swing.JPanel      jPanel29;
    private javax.swing.JPanel      jPanel3;
    private javax.swing.JPanel      jPanel30;
    private javax.swing.JPanel      jPanel31;
    private javax.swing.JPanel      jPanel32;
    private javax.swing.JPanel      jPanel33;
    private javax.swing.JPanel      jPanel4;
    private javax.swing.JPanel      jPanel5;
    private javax.swing.JPanel      jPanel6;
    private javax.swing.JPanel      jPanel7;
    private javax.swing.JPanel      jPanel8;
    private javax.swing.JPanel      jPanel9;
    private javax.swing.JCheckBox   notControlled1;
    private javax.swing.JCheckBox   notControlled2;
    private javax.swing.JCheckBox   notControlled3;

    // End of variables declaration//GEN-END:variables
    /** Creates new form RFXVConfigSetup */
    public RFXVConfigSetup(){
        this.initComponents();
    }

    @Override
    public void apply() {
        super.apply();
        for(int i = 1; i <= 3; i++){
            final Nid ctrlNid = new Nid(this.baseNid + 14 + i * 5);
            final JCheckBox cb1 = (JCheckBox)this.getComponentByBame(this, "controlled" + i);
            final String val = cb1.isSelected() ? "CONTROLLED" : "UNCONTROLLED";
            try{
                this.subtree.putData(ctrlNid, new CString(val));
            }catch(final Exception e){
                System.out.println("Error writing device data: " + e);
            }
        }
        return;
    }

    @Override
    public void configure(final Database subtree, final int baseNid, final Node node) {
        super.configure(subtree, baseNid, node);
        this.resetCtrlState();
        return;
    }

    private Component getComponentByBame(final Container c, final String name) {
        final Component comps[] = c.getComponents();
        Component comp;
        for(final Component comp2 : comps){
            if(comp2 instanceof java.awt.Container) if((comp = this.getComponentByBame((Container)comp2, name)) != null) return comp;
            else if(comp2.getName() != null && comp2.getName().equals(name)) return comp2;
        }
        return null;
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
        this.buttonGroup1 = new javax.swing.ButtonGroup();
        this.buttonGroup2 = new javax.swing.ButtonGroup();
        this.buttonGroup3 = new javax.swing.ButtonGroup();
        this.jPanel1 = new javax.swing.JPanel();
        this.deviceButtons1 = new DeviceButtons();
        this.jPanel2 = new javax.swing.JPanel();
        this.jPanel3 = new javax.swing.JPanel();
        this.deviceField1 = new DeviceField();
        this.deviceDispatch1 = new DeviceDispatch();
        this.jPanel4 = new javax.swing.JPanel();
        this.jPanel33 = new javax.swing.JPanel();
        this.deviceField3 = new DeviceField();
        this.deviceField5 = new DeviceField();
        this.jPanel5 = new javax.swing.JPanel();
        this.jPanel31 = new javax.swing.JPanel();
        this.deviceField2 = new DeviceField();
        this.jPanel32 = new javax.swing.JPanel();
        this.deviceField4 = new DeviceField();
        this.deviceChoice1 = new DeviceChoice();
        this.deviceChoice2 = new DeviceChoice();
        this.deviceChoice3 = new DeviceChoice();
        this.jPanel6 = new javax.swing.JPanel();
        this.jPanel7 = new javax.swing.JPanel();
        this.jPanel9 = new javax.swing.JPanel();
        this.jLabel4 = new javax.swing.JLabel();
        this.jPanel10 = new javax.swing.JPanel();
        this.jLabel2 = new javax.swing.JLabel();
        this.jPanel11 = new javax.swing.JPanel();
        this.jLabel3 = new javax.swing.JLabel();
        this.jPanel12 = new javax.swing.JPanel();
        this.jLabel1 = new javax.swing.JLabel();
        this.jPanel13 = new javax.swing.JPanel();
        this.jLabel5 = new javax.swing.JLabel();
        this.jPanel8 = new javax.swing.JPanel();
        this.jPanel14 = new javax.swing.JPanel();
        this.deviceLabel1 = new DeviceLabel();
        this.jPanel15 = new javax.swing.JPanel();
        this.deviceLabel2 = new DeviceLabel();
        this.jPanel16 = new javax.swing.JPanel();
        this.deviceLabel3 = new DeviceLabel();
        this.jPanel17 = new javax.swing.JPanel();
        this.controlled1 = new javax.swing.JCheckBox();
        this.jPanel18 = new javax.swing.JPanel();
        this.notControlled1 = new javax.swing.JCheckBox();
        this.jPanel19 = new javax.swing.JPanel();
        this.jPanel20 = new javax.swing.JPanel();
        this.deviceLabel4 = new DeviceLabel();
        this.jPanel21 = new javax.swing.JPanel();
        this.deviceLabel6 = new DeviceLabel();
        this.jPanel22 = new javax.swing.JPanel();
        this.deviceLabel7 = new DeviceLabel();
        this.jPanel23 = new javax.swing.JPanel();
        this.controlled2 = new javax.swing.JCheckBox();
        this.jPanel24 = new javax.swing.JPanel();
        this.notControlled2 = new javax.swing.JCheckBox();
        this.jPanel25 = new javax.swing.JPanel();
        this.jPanel26 = new javax.swing.JPanel();
        this.deviceLabel9 = new DeviceLabel();
        this.jPanel27 = new javax.swing.JPanel();
        this.deviceLabel10 = new DeviceLabel();
        this.jPanel28 = new javax.swing.JPanel();
        this.deviceLabel11 = new DeviceLabel();
        this.jPanel29 = new javax.swing.JPanel();
        this.controlled3 = new javax.swing.JCheckBox();
        this.jPanel30 = new javax.swing.JPanel();
        this.notControlled3 = new javax.swing.JCheckBox();
        this.setDeviceProvider("localhost");
        this.setDeviceTitle("RFX Vessel Configuration");
        this.setDeviceType("RFXVConfig");
        this.setHeight(500);
        this.setWidth(1080);
        this.jPanel1.setLayout(new java.awt.BorderLayout());
        this.jPanel1.add(this.deviceButtons1, java.awt.BorderLayout.PAGE_END);
        this.jPanel2.setLayout(new java.awt.GridLayout(1, 0));
        this.jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        this.deviceField1.setIdentifier("");
        this.deviceField1.setLabelString("Comment:");
        this.deviceField1.setNumCols(40);
        this.deviceField1.setOffsetNid(1);
        this.deviceField1.setTextOnly(true);
        this.jPanel3.add(this.deviceField1);
        this.jPanel3.add(this.deviceDispatch1);
        this.jPanel2.add(this.jPanel3);
        this.jPanel1.add(this.jPanel2, java.awt.BorderLayout.NORTH);
        this.jPanel4.setLayout(new java.awt.BorderLayout());
        this.jPanel33.setBorder(javax.swing.BorderFactory.createTitledBorder("DPEL PELLET INJECTORS"));
        this.jPanel33.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        this.deviceField3.setIdentifier("");
        this.deviceField3.setLabelString("Angle [deg] :");
        this.deviceField3.setOffsetNid(61);
        this.jPanel33.add(this.deviceField3);
        this.deviceField5.setIdentifier("");
        this.deviceField5.setLabelString("DRIGAS max [bar] :");
        this.deviceField5.setOffsetNid(62);
        this.jPanel33.add(this.deviceField5);
        this.jPanel4.add(this.jPanel33, java.awt.BorderLayout.SOUTH);
        this.jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("  VI  "));
        this.jPanel5.setLayout(new java.awt.GridLayout(3, 0, 2, 0));
        this.deviceField2.setIdentifier("");
        this.deviceField2.setLabelString("N. Group Filling Valves :");
        this.deviceField2.setOffsetNid(4);
        this.jPanel31.add(this.deviceField2);
        this.jPanel5.add(this.jPanel31);
        this.deviceField4.setIdentifier("");
        this.deviceField4.setLabelString("N. Group Puffing Valves:");
        this.deviceField4.setOffsetNid(5);
        this.jPanel32.add(this.deviceField4);
        this.jPanel5.add(this.jPanel32);
        this.deviceChoice1.setChoiceItems(new String[]{"H2", "He", "D2", "Not used"});
        this.deviceChoice1.setIdentifier("");
        this.deviceChoice1.setLabelString("VIK1 Gas  :");
        this.deviceChoice1.setOffsetNid(7);
        this.deviceChoice1.setUpdateIdentifier("");
        this.jPanel5.add(this.deviceChoice1);
        this.deviceChoice2.setChoiceItems(new String[]{"H2", "He", "Ne", "Ar", "Not used"});
        this.deviceChoice2.setIdentifier("");
        this.deviceChoice2.setLabelString("Bottle Gas");
        this.deviceChoice2.setOffsetNid(9);
        this.deviceChoice2.setUpdateIdentifier("");
        this.jPanel5.add(this.deviceChoice2);
        this.deviceChoice3.setChoiceIntValues(new int[]{0, 1});
        this.deviceChoice3.setChoiceItems(new String[]{"Disabled", "Enabled"});
        this.deviceChoice3.setConvert(true);
        this.deviceChoice3.setIdentifier("");
        this.deviceChoice3.setLabelString("NE Control : ");
        this.deviceChoice3.setOffsetNid(64);
        this.deviceChoice3.setUpdateIdentifier("");
        this.jPanel5.add(this.deviceChoice3);
        this.jPanel4.add(this.jPanel5, java.awt.BorderLayout.NORTH);
        this.jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("  VD  "));
        this.jPanel6.setLayout(new java.awt.GridLayout(4, 0));
        this.jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        this.jPanel7.setLayout(new java.awt.GridLayout(1, 0));
        this.jLabel4.setText("POSITION");
        this.jPanel9.add(this.jLabel4);
        this.jPanel7.add(this.jPanel9);
        this.jLabel2.setText("NAME");
        this.jPanel10.add(this.jLabel2);
        this.jPanel7.add(this.jPanel10);
        this.jLabel3.setText("VD MODULE");
        this.jPanel11.add(this.jLabel3);
        this.jPanel7.add(this.jPanel11);
        this.jLabel1.setText("CONTROLLED");
        this.jPanel12.add(this.jLabel1);
        this.jPanel7.add(this.jPanel12);
        this.jLabel5.setText("UNCONTROLLED");
        this.jPanel13.add(this.jLabel5);
        this.jPanel7.add(this.jPanel13);
        this.jPanel6.add(this.jPanel7);
        this.jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        this.jPanel8.setLayout(new java.awt.GridLayout(1, 5));
        this.deviceLabel1.setIdentifier("");
        this.deviceLabel1.setOffsetNid(16);
        this.deviceLabel1.setTextOnly(true);
        this.jPanel14.add(this.deviceLabel1);
        this.jPanel8.add(this.jPanel14);
        this.deviceLabel2.setIdentifier("");
        this.deviceLabel2.setOffsetNid(17);
        this.deviceLabel2.setTextOnly(true);
        this.jPanel15.add(this.deviceLabel2);
        this.jPanel8.add(this.jPanel15);
        this.deviceLabel3.setIdentifier("");
        this.deviceLabel3.setOffsetNid(18);
        this.deviceLabel3.setTextOnly(true);
        this.jPanel16.add(this.deviceLabel3);
        this.jPanel8.add(this.jPanel16);
        this.jPanel17.setName(""); // NOI18N
        this.buttonGroup1.add(this.controlled1);
        this.controlled1.setActionCommand("jCheckBox1");
        this.controlled1.setName("controlled1"); // NOI18N
        this.jPanel17.add(this.controlled1);
        this.jPanel8.add(this.jPanel17);
        this.buttonGroup1.add(this.notControlled1);
        this.notControlled1.setName("notControlled1"); // NOI18N
        this.jPanel18.add(this.notControlled1);
        this.jPanel8.add(this.jPanel18);
        this.jPanel6.add(this.jPanel8);
        this.jPanel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        this.jPanel19.setLayout(new java.awt.GridLayout(1, 5));
        this.deviceLabel4.setIdentifier("");
        this.deviceLabel4.setOffsetNid(21);
        this.deviceLabel4.setTextOnly(true);
        this.jPanel20.add(this.deviceLabel4);
        this.jPanel19.add(this.jPanel20);
        this.deviceLabel6.setIdentifier("");
        this.deviceLabel6.setOffsetNid(22);
        this.deviceLabel6.setTextOnly(true);
        this.jPanel21.add(this.deviceLabel6);
        this.jPanel19.add(this.jPanel21);
        this.deviceLabel7.setIdentifier("");
        this.deviceLabel7.setOffsetNid(23);
        this.deviceLabel7.setTextOnly(true);
        this.jPanel22.add(this.deviceLabel7);
        this.jPanel19.add(this.jPanel22);
        this.buttonGroup2.add(this.controlled2);
        this.controlled2.setName("controlled2"); // NOI18N
        this.jPanel23.add(this.controlled2);
        this.jPanel19.add(this.jPanel23);
        this.buttonGroup2.add(this.notControlled2);
        this.notControlled2.setName("notControlled2"); // NOI18N
        this.jPanel24.add(this.notControlled2);
        this.jPanel19.add(this.jPanel24);
        this.jPanel6.add(this.jPanel19);
        this.jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        this.jPanel25.setLayout(new java.awt.GridLayout(1, 5));
        this.deviceLabel9.setIdentifier("");
        this.deviceLabel9.setOffsetNid(26);
        this.deviceLabel9.setTextOnly(true);
        this.jPanel26.add(this.deviceLabel9);
        this.jPanel25.add(this.jPanel26);
        this.deviceLabel10.setIdentifier("");
        this.deviceLabel10.setOffsetNid(27);
        this.deviceLabel10.setTextOnly(true);
        this.jPanel27.add(this.deviceLabel10);
        this.jPanel25.add(this.jPanel27);
        this.deviceLabel11.setIdentifier("");
        this.deviceLabel11.setOffsetNid(28);
        this.deviceLabel11.setTextOnly(true);
        this.jPanel28.add(this.deviceLabel11);
        this.jPanel25.add(this.jPanel28);
        this.buttonGroup3.add(this.controlled3);
        this.controlled3.setName("controlled3"); // NOI18N
        this.jPanel29.add(this.controlled3);
        this.jPanel25.add(this.jPanel29);
        this.buttonGroup3.add(this.notControlled3);
        this.notControlled3.setName("notControlled3"); // NOI18N
        this.jPanel30.add(this.notControlled3);
        this.jPanel25.add(this.jPanel30);
        this.jPanel6.add(this.jPanel25);
        this.jPanel4.add(this.jPanel6, java.awt.BorderLayout.CENTER);
        this.jPanel1.add(this.jPanel4, java.awt.BorderLayout.CENTER);
        this.getContentPane().add(this.jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void reset() {
        super.reset();
        this.resetCtrlState();
        return;
    }

    private void resetCtrlState() {
        for(int i = 1; i <= 3; i++){
            final Nid ctrlNid = new Nid(this.baseNid + 14 + i * 5);
            String val;
            JCheckBox cb;
            try{
                val = this.subtree.getData(ctrlNid).toString();
                if(val.equals("\"CONTROLLED\"")){
                    cb = (JCheckBox)this.getComponentByBame(this, "controlled" + i);
                }else{
                    cb = (JCheckBox)this.getComponentByBame(this, "notControlled" + i);
                }
                cb.setSelected(true);
            }catch(final Exception e){
                System.out.println("Error set button state: " + e);
            }
        }
    }
}

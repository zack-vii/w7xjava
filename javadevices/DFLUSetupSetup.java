import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import devicebeans.DeviceButtons;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class DFLUSetupSetup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = 8951375042371695917L;
    BorderLayout  borderLayout1  = new BorderLayout();
    BorderLayout  borderLayout2  = new BorderLayout();
    BorderLayout  borderLayout3  = new BorderLayout();
    DeviceButtons deviceButtons1 = new DeviceButtons();
    DeviceField   deviceField1   = new DeviceField();
    DeviceField   deviceField10  = new DeviceField();
    DeviceField   deviceField11  = new DeviceField();
    DeviceField   deviceField110 = new DeviceField();
    DeviceField   deviceField111 = new DeviceField();
    DeviceField   deviceField112 = new DeviceField();
    DeviceField   deviceField12  = new DeviceField();
    DeviceField   deviceField13  = new DeviceField();
    DeviceField   deviceField14  = new DeviceField();
    DeviceField   deviceField15  = new DeviceField();
    DeviceField   deviceField16  = new DeviceField();
    DeviceField   deviceField17  = new DeviceField();
    DeviceField   deviceField18  = new DeviceField();
    DeviceField   deviceField19  = new DeviceField();
    DeviceField   deviceField2   = new DeviceField();
    DeviceField   deviceField20  = new DeviceField();
    DeviceField   deviceField21  = new DeviceField();
    DeviceField   deviceField22  = new DeviceField();
    DeviceField   deviceField23  = new DeviceField();
    DeviceField   deviceField24  = new DeviceField();
    DeviceField   deviceField3   = new DeviceField();
    DeviceField   deviceField4   = new DeviceField();
    DeviceField   deviceField5   = new DeviceField();
    DeviceField   deviceField6   = new DeviceField();
    DeviceField   deviceField7   = new DeviceField();
    DeviceField   deviceField8   = new DeviceField();
    DeviceField   deviceField9   = new DeviceField();
    GridLayout    gridLayout1    = new GridLayout();
    GridLayout    gridLayout2    = new GridLayout();
    GridLayout    gridLayout3    = new GridLayout();
    GridLayout    gridLayout4    = new GridLayout();
    GridLayout    gridLayout5    = new GridLayout();
    GridLayout    gridLayout6    = new GridLayout();
    JPanel        jPanel1        = new JPanel();
    JPanel        jPanel10       = new JPanel();
    JPanel        jPanel11       = new JPanel();
    JPanel        jPanel110      = new JPanel();
    JPanel        jPanel111      = new JPanel();
    JPanel        jPanel112      = new JPanel();
    JPanel        jPanel12       = new JPanel();
    JPanel        jPanel13       = new JPanel();
    JPanel        jPanel14       = new JPanel();
    JPanel        jPanel15       = new JPanel();
    JPanel        jPanel16       = new JPanel();
    JPanel        jPanel17       = new JPanel();
    JPanel        jPanel18       = new JPanel();
    JPanel        jPanel19       = new JPanel();
    JPanel        jPanel2        = new JPanel();
    JPanel        jPanel20       = new JPanel();
    JPanel        jPanel21       = new JPanel();
    JPanel        jPanel22       = new JPanel();
    JPanel        jPanel23       = new JPanel();
    JPanel        jPanel3        = new JPanel();
    JPanel        jPanel4        = new JPanel();
    JPanel        jPanel5        = new JPanel();
    JPanel        jPanel6        = new JPanel();
    JPanel        jPanel7        = new JPanel();
    JPanel        jPanel8        = new JPanel();
    JPanel        jPanel9        = new JPanel();
    JTabbedPane   jTabbedPane1   = new JTabbedPane();
    JTabbedPane   jTabbedPane2   = new JTabbedPane();
    JTabbedPane   jTabbedPane3   = new JTabbedPane();

    public DFLUSetupSetup(){
        try{
            this.jbInit();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setWidth(900);
        this.setHeight(250);
        this.setDeviceType("DFLUSetup");
        this.setDeviceProvider("150.178.3.33");
        this.setDeviceTitle("DFLU Acquisition Configuration");
        this.getContentPane().setLayout(this.borderLayout1);
        this.jPanel1.setLayout(this.borderLayout2);
        this.jPanel3.setLayout(this.gridLayout1);
        this.gridLayout1.setRows(3);
        this.deviceField1.setOffsetNid(3);
        this.deviceField1.setTextOnly(true);
        this.deviceField1.setLabelString("Event: ");
        this.deviceField1.setNumCols(20);
        this.deviceField1.setIdentifier("");
        this.deviceField1.setEditable(false);
        this.deviceField2.setOffsetNid(4);
        this.deviceField2.setLabelString("Delay: ");
        this.deviceField2.setNumCols(20);
        this.deviceField2.setIdentifier("");
        this.deviceField3.setOffsetNid(5);
        this.deviceField3.setLabelString("Duration: ");
        this.deviceField3.setNumCols(20);
        this.deviceField3.setIdentifier("");
        this.deviceField4.setOffsetNid(6);
        this.deviceField4.setLabelString("Frequency 1: ");
        this.deviceField4.setNumCols(20);
        this.deviceField4.setIdentifier("");
        this.deviceField5.setOffsetNid(7);
        this.deviceField5.setLabelString("Frequency 2:");
        this.deviceField5.setNumCols(20);
        this.deviceField5.setIdentifier("");
        this.deviceField6.setOffsetNid(8);
        this.deviceField6.setLabelString("Start Acq. :");
        this.deviceField6.setNumCols(20);
        this.deviceField6.setIdentifier("");
        this.deviceField7.setOffsetNid(9);
        this.deviceField7.setLabelString("Stop Acq. :");
        this.deviceField7.setNumCols(20);
        this.deviceField7.setIdentifier("");
        this.jPanel4.setLayout(this.gridLayout2);
        this.gridLayout2.setRows(3);
        this.gridLayout2.setVgap(0);
        this.deviceField9.setOffsetNid(11);
        this.deviceField9.setTextOnly(true);
        this.deviceField9.setLabelString("Event: ");
        this.deviceField9.setNumCols(20);
        this.deviceField9.setIdentifier("");
        this.deviceField9.setEditable(false);
        this.deviceField10.setOffsetNid(12);
        this.deviceField10.setLabelString("Delay: ");
        this.deviceField10.setNumCols(20);
        this.deviceField10.setIdentifier("");
        this.deviceField11.setOffsetNid(13);
        this.deviceField11.setLabelString("Duration: ");
        this.deviceField11.setNumCols(20);
        this.deviceField11.setIdentifier("");
        this.jPanel5.setLayout(this.gridLayout3);
        this.gridLayout3.setRows(3);
        this.deviceField14.setOffsetNid(17);
        this.deviceField14.setLabelString("Duration:");
        this.deviceField14.setNumCols(20);
        this.deviceField14.setIdentifier("");
        this.deviceField12.setOffsetNid(15);
        this.deviceField12.setTextOnly(true);
        this.deviceField12.setLabelString("Event: ");
        this.deviceField12.setNumCols(20);
        this.deviceField12.setIdentifier("");
        this.deviceField12.setEditable(false);
        this.deviceField13.setOffsetNid(16);
        this.deviceField13.setLabelString("Delay: ");
        this.deviceField13.setNumCols(20);
        this.deviceField13.setIdentifier("");
        this.jPanel2.setLayout(this.borderLayout3);
        this.deviceField15.setOffsetNid(34);
        this.deviceField15.setLabelString("Duration:");
        this.deviceField15.setNumCols(20);
        this.deviceField16.setOffsetNid(24);
        this.deviceField16.setLabelString("Frequency 2:");
        this.deviceField16.setNumCols(20);
        this.deviceField16.setIdentifier("");
        this.jPanel15.setLayout(this.gridLayout5);
        this.deviceField17.setOffsetNid(23);
        this.deviceField17.setLabelString("Frequency 1: ");
        this.deviceField17.setNumCols(20);
        this.deviceField17.setIdentifier("");
        this.jPanel17.setLayout(this.gridLayout4);
        this.gridLayout4.setRows(3);
        this.deviceField18.setOffsetNid(33);
        this.deviceField18.setLabelString("Delay: ");
        this.deviceField18.setNumCols(20);
        this.deviceField18.setIdentifier("");
        this.deviceField19.setOffsetNid(26);
        this.deviceField19.setLabelString("Stop Acq. :");
        this.deviceField19.setNumCols(20);
        this.deviceField19.setIdentifier("");
        this.gridLayout5.setRows(3);
        this.gridLayout5.setVgap(0);
        this.deviceField20.setOffsetNid(25);
        this.deviceField20.setLabelString("Start Acq. :");
        this.deviceField20.setNumCols(20);
        this.deviceField20.setIdentifier("");
        this.jPanel19.setLayout(this.gridLayout6);
        this.deviceField110.setOffsetNid(29);
        this.deviceField110.setLabelString("Delay: ");
        this.deviceField110.setNumCols(20);
        this.deviceField110.setIdentifier("");
        this.gridLayout6.setRows(3);
        this.deviceField21.setOffsetNid(22);
        this.deviceField21.setLabelString("Duration: ");
        this.deviceField21.setNumCols(20);
        this.deviceField21.setIdentifier("");
        this.deviceField111.setOffsetNid(30);
        this.deviceField111.setLabelString("Duration: ");
        this.deviceField111.setNumCols(20);
        this.deviceField111.setIdentifier("");
        this.deviceField112.setOffsetNid(32);
        this.deviceField112.setTextOnly(true);
        this.deviceField112.setLabelString("Event: ");
        this.deviceField112.setNumCols(20);
        this.deviceField112.setIdentifier("");
        this.deviceField112.setEditable(false);
        this.deviceField22.setOffsetNid(28);
        this.deviceField22.setTextOnly(true);
        this.deviceField22.setLabelString("Event: ");
        this.deviceField22.setNumCols(20);
        this.deviceField22.setIdentifier("");
        this.deviceField22.setEditable(false);
        this.deviceField23.setOffsetNid(20);
        this.deviceField23.setTextOnly(true);
        this.deviceField23.setLabelString("Event: ");
        this.deviceField23.setNumCols(20);
        this.deviceField23.setIdentifier("");
        this.deviceField23.setEditable(false);
        this.deviceField24.setOffsetNid(21);
        this.deviceField24.setLabelString("Delay: ");
        this.deviceField24.setNumCols(20);
        this.deviceField24.setIdentifier("");
        this.getContentPane().add(this.jTabbedPane1, BorderLayout.CENTER);
        this.jTabbedPane1.add(this.jPanel1, "POLOIDAL");
        this.jPanel1.add(this.jTabbedPane2, BorderLayout.CENTER);
        this.jTabbedPane2.add(this.jPanel3, "CLOCK");
        this.jPanel3.add(this.jPanel6, null);
        this.jPanel6.add(this.deviceField1, null);
        this.jPanel6.add(this.deviceField2, null);
        this.jPanel6.add(this.deviceField3, null);
        this.jPanel3.add(this.jPanel7, null);
        this.jPanel7.add(this.deviceField4, null);
        this.jPanel7.add(this.deviceField5, null);
        this.jPanel3.add(this.jPanel8, null);
        this.jPanel8.add(this.deviceField6, null);
        this.jPanel8.add(this.deviceField7, null);
        this.jTabbedPane2.add(this.jPanel4, "TRIGGER");
        this.jPanel4.add(this.jPanel9, null);
        this.jPanel4.add(this.jPanel10, null);
        this.jPanel10.add(this.deviceField9, null);
        this.jPanel10.add(this.deviceField10, null);
        this.jPanel10.add(this.deviceField11, null);
        this.jPanel4.add(this.jPanel11, null);
        this.jTabbedPane2.add(this.jPanel5, "AUTO ZERO");
        this.jPanel5.add(this.jPanel12, null);
        this.jPanel5.add(this.jPanel13, null);
        this.jPanel13.add(this.deviceField12, null);
        this.jPanel13.add(this.deviceField13, null);
        this.jPanel13.add(this.deviceField14, null);
        this.jPanel5.add(this.jPanel14, null);
        this.jTabbedPane1.add(this.jPanel2, "TOROIDAL");
        this.jPanel2.add(this.jTabbedPane3, BorderLayout.CENTER);
        this.jPanel22.add(this.deviceField23, null);
        this.jPanel22.add(this.deviceField24, null);
        this.jPanel22.add(this.deviceField21, null);
        this.jPanel17.add(this.jPanel22, null);
        this.jPanel17.add(this.jPanel21, null);
        this.jPanel21.add(this.deviceField17, null);
        this.jPanel21.add(this.deviceField16, null);
        this.jPanel17.add(this.jPanel20, null);
        this.jPanel20.add(this.deviceField20, null);
        this.jPanel20.add(this.deviceField19, null);
        this.jTabbedPane3.add(this.jPanel17, "CLOCK");
        this.jTabbedPane3.add(this.jPanel15, "TRIGGER");
        this.jPanel15.add(this.jPanel23, null);
        this.jPanel15.add(this.jPanel18, null);
        this.jPanel18.add(this.deviceField22, null);
        this.jPanel18.add(this.deviceField110, null);
        this.jPanel18.add(this.deviceField111, null);
        this.jPanel15.add(this.jPanel110, null);
        this.jTabbedPane3.add(this.jPanel19, "AUTO ZERO");
        this.jPanel19.add(this.jPanel111, null);
        this.jPanel19.add(this.jPanel16, null);
        this.jPanel16.add(this.deviceField112, null);
        this.jPanel16.add(this.deviceField18, null);
        this.jPanel16.add(this.deviceField15, null);
        this.jPanel19.add(this.jPanel112, null);
        this.getContentPane().add(this.deviceButtons1, BorderLayout.SOUTH);
    }
}
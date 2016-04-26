import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
public class DEQUSetupSetup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = -4259320047146483473L;
    BorderLayout  borderLayout1  = new BorderLayout();
    DeviceButtons deviceButtons1 = new DeviceButtons();
    DeviceField   deviceField1   = new DeviceField();
    DeviceField   deviceField10  = new DeviceField();
    DeviceField   deviceField11  = new DeviceField();
    DeviceField   deviceField110 = new DeviceField();
    DeviceField   deviceField12  = new DeviceField();
    DeviceField   deviceField13  = new DeviceField();
    DeviceField   deviceField14  = new DeviceField();
    DeviceField   deviceField15  = new DeviceField();
    DeviceField   deviceField16  = new DeviceField();
    DeviceField   deviceField17  = new DeviceField();
    DeviceField   deviceField18  = new DeviceField();
    DeviceField   deviceField19  = new DeviceField();
    DeviceField   deviceField2   = new DeviceField();
    DeviceField   deviceField3   = new DeviceField();
    DeviceField   deviceField4   = new DeviceField();
    DeviceField   deviceField5   = new DeviceField();
    DeviceField   deviceField6   = new DeviceField();
    DeviceField   deviceField7   = new DeviceField();
    DeviceField   deviceField8   = new DeviceField();
    DeviceField   deviceField9   = new DeviceField();
    FlowLayout    flowLayout1    = new FlowLayout();
    FlowLayout    flowLayout2    = new FlowLayout();
    FlowLayout    flowLayout3    = new FlowLayout();
    FlowLayout    flowLayout4    = new FlowLayout();
    FlowLayout    flowLayout5    = new FlowLayout();
    FlowLayout    flowLayout6    = new FlowLayout();
    GridLayout    gridLayout1    = new GridLayout();
    GridLayout    gridLayout2    = new GridLayout();
    GridLayout    gridLayout3    = new GridLayout();
    GridLayout    gridLayout5    = new GridLayout();
    JPanel        jPanel1        = new JPanel();
    JPanel        jPanel10       = new JPanel();
    JPanel        jPanel11       = new JPanel();
    JPanel        jPanel12       = new JPanel();
    JPanel        jPanel13       = new JPanel();
    JPanel        jPanel14       = new JPanel();
    JPanel        jPanel15       = new JPanel();
    JPanel        jPanel16       = new JPanel();
    JPanel        jPanel2        = new JPanel();
    JPanel        jPanel3        = new JPanel();
    JPanel        jPanel4        = new JPanel();
    JPanel        jPanel5        = new JPanel();
    JPanel        jPanel6        = new JPanel();
    JPanel        jPanel7        = new JPanel();
    JPanel        jPanel8        = new JPanel();
    JPanel        jPanel9        = new JPanel();
    JTabbedPane   jTabbedPane1   = new JTabbedPane();

    public DEQUSetupSetup(){
        try{
            this.jbInit();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setWidth(800);
        this.setHeight(250);
        this.setDeviceType("DEQUSetup");
        this.setDeviceProvider("150.178.3.33");
        this.setDeviceTitle("DEQU Acquisition Configuration");
        this.getContentPane().setLayout(this.borderLayout1);
        this.jPanel1.setLayout(this.gridLayout1);
        this.gridLayout1.setHgap(0);
        this.gridLayout1.setRows(3);
        this.gridLayout1.setVgap(0);
        this.jPanel5.setLayout(this.flowLayout1);
        this.jPanel7.setLayout(this.flowLayout2);
        this.flowLayout1.setAlignment(FlowLayout.CENTER);
        this.flowLayout1.setHgap(5);
        this.flowLayout2.setAlignment(FlowLayout.CENTER);
        this.flowLayout2.setHgap(0);
        this.flowLayout2.setVgap(0);
        this.deviceField1.setOffsetNid(2);
        this.deviceField1.setTextOnly(true);
        this.deviceField1.setLabelString("Event: ");
        this.deviceField1.setNumCols(15);
        this.deviceField1.setIdentifier("");
        this.deviceField1.setShowState(false);
        this.deviceField1.setEditable(false);
        this.deviceField2.setOffsetNid(3);
        this.deviceField2.setLabelString("Delay: ");
        this.deviceField2.setNumCols(15);
        this.deviceField2.setIdentifier("");
        this.deviceField3.setOffsetNid(4);
        this.deviceField3.setLabelString("Duration: ");
        this.deviceField3.setNumCols(15);
        this.deviceField3.setIdentifier("");
        this.deviceField4.setOffsetNid(5);
        this.deviceField4.setLabelString("Frequency 1: ");
        this.deviceField4.setNumCols(15);
        this.deviceField4.setIdentifier("");
        this.deviceField5.setOffsetNid(6);
        this.deviceField5.setLabelString("Frequency 2: ");
        this.deviceField5.setNumCols(15);
        this.deviceField5.setIdentifier("");
        this.deviceField6.setOffsetNid(7);
        this.deviceField6.setLabelString("Start Acq. :");
        this.deviceField6.setNumCols(15);
        this.deviceField6.setIdentifier("");
        this.deviceField7.setOffsetNid(8);
        this.deviceField7.setLabelString("Stop Acq: ");
        this.deviceField7.setNumCols(15);
        this.deviceField7.setIdentifier("");
        this.deviceField8.setNumCols(15);
        this.deviceField8.setIdentifier("");
        this.deviceField8.setOffsetNid(14);
        this.deviceField8.setLabelString("Frequency 2: ");
        this.deviceField9.setNumCols(15);
        this.deviceField9.setIdentifier("");
        this.deviceField9.setOffsetNid(16);
        this.deviceField9.setLabelString("Stop Acq: ");
        this.flowLayout3.setAlignment(FlowLayout.CENTER);
        this.flowLayout3.setHgap(0);
        this.flowLayout3.setVgap(5);
        this.deviceField10.setOffsetNid(12);
        this.deviceField10.setLabelString("Duration: ");
        this.deviceField10.setNumCols(15);
        this.deviceField10.setIdentifier("");
        this.deviceField11.setOffsetNid(10);
        this.deviceField11.setTextOnly(true);
        this.deviceField11.setLabelString("Event: ");
        this.deviceField11.setNumCols(15);
        this.deviceField11.setIdentifier("");
        this.deviceField11.setShowState(false);
        this.deviceField11.setEditable(false);
        this.deviceField12.setOffsetNid(15);
        this.deviceField12.setLabelString("Start Acq. :");
        this.deviceField12.setNumCols(15);
        this.deviceField12.setIdentifier("");
        this.flowLayout4.setAlignment(FlowLayout.CENTER);
        this.flowLayout4.setHgap(0);
        this.flowLayout4.setVgap(0);
        this.jPanel9.setLayout(this.flowLayout3);
        this.jPanel10.setLayout(this.flowLayout4);
        this.deviceField13.setOffsetNid(13);
        this.deviceField13.setLabelString("Frequency 1: ");
        this.deviceField13.setNumCols(15);
        this.deviceField13.setIdentifier("");
        this.deviceField14.setOffsetNid(11);
        this.deviceField14.setLabelString("Delay: ");
        this.deviceField14.setNumCols(15);
        this.deviceField14.setIdentifier("");
        this.jPanel2.setLayout(this.gridLayout2);
        this.gridLayout2.setRows(3);
        this.flowLayout5.setVgap(0);
        this.flowLayout5.setHgap(0);
        this.flowLayout5.setAlignment(FlowLayout.CENTER);
        this.deviceField15.setNumCols(15);
        this.deviceField15.setIdentifier("");
        this.deviceField15.setOffsetNid(23);
        this.deviceField15.setLabelString("Delay: ");
        this.deviceField16.setNumCols(15);
        this.deviceField16.setIdentifier("");
        this.deviceField16.setOffsetNid(24);
        this.deviceField16.setLabelString("Duration: ");
        this.deviceField17.setEditable(false);
        this.deviceField17.setShowState(false);
        this.deviceField17.setIdentifier("");
        this.deviceField17.setNumCols(15);
        this.deviceField17.setLabelString("Event: ");
        this.deviceField17.setOffsetNid(22);
        this.deviceField17.setTextOnly(true);
        this.jPanel11.setLayout(this.flowLayout5);
        this.jPanel4.setLayout(this.gridLayout3);
        this.deviceField18.setNumCols(15);
        this.deviceField18.setIdentifier("");
        this.deviceField18.setOffsetNid(19);
        this.deviceField18.setLabelString("Delay: ");
        this.deviceField19.setNumCols(15);
        this.deviceField19.setIdentifier("");
        this.deviceField19.setOffsetNid(20);
        this.deviceField19.setLabelString("Duration: ");
        this.deviceField110.setEditable(false);
        this.deviceField110.setShowState(false);
        this.deviceField110.setIdentifier("");
        this.deviceField110.setNumCols(15);
        this.deviceField110.setLabelString("Event: ");
        this.deviceField110.setOffsetNid(18);
        this.deviceField110.setTextOnly(true);
        this.jPanel12.setLayout(this.flowLayout6);
        this.gridLayout3.setRows(3);
        this.jPanel3.setLayout(this.gridLayout5);
        this.gridLayout5.setRows(3);
        this.flowLayout6.setAlignment(FlowLayout.LEFT);
        this.getContentPane().add(this.jTabbedPane1, BorderLayout.CENTER);
        this.jTabbedPane1.add(this.jPanel1, "TR10 ACQ");
        this.getContentPane().add(this.deviceButtons1, BorderLayout.SOUTH);
        this.jTabbedPane1.add(this.jPanel2, "TRCH ACQ");
        this.jPanel8.add(this.deviceField13, null);
        this.jPanel8.add(this.deviceField8, null);
        this.jPanel2.add(this.jPanel9, null);
        this.jPanel9.add(this.deviceField11, null);
        this.jPanel9.add(this.deviceField14, null);
        this.jPanel9.add(this.deviceField10, null);
        this.jPanel10.add(this.deviceField12, null);
        this.jPanel10.add(this.deviceField9, null);
        this.jPanel2.add(this.jPanel8, null);
        this.jPanel2.add(this.jPanel10, null);
        this.jTabbedPane1.add(this.jPanel4, "TRIGGER");
        this.jPanel4.add(this.jPanel13, null);
        this.jPanel4.add(this.jPanel11, null);
        this.jPanel11.add(this.deviceField17, null);
        this.jPanel11.add(this.deviceField15, null);
        this.jPanel11.add(this.deviceField16, null);
        this.jPanel4.add(this.jPanel14, null);
        this.jTabbedPane1.add(this.jPanel3, "AUTO ZERO");
        this.jPanel3.add(this.jPanel15, null);
        this.jPanel3.add(this.jPanel12, null);
        this.jPanel12.add(this.deviceField110, null);
        this.jPanel12.add(this.deviceField18, null);
        this.jPanel12.add(this.deviceField19, null);
        this.jPanel3.add(this.jPanel16, null);
        this.jPanel1.add(this.jPanel5, null);
        this.jPanel5.add(this.deviceField1, null);
        this.jPanel5.add(this.deviceField2, null);
        this.jPanel5.add(this.deviceField3, null);
        this.jPanel1.add(this.jPanel6, null);
        this.jPanel6.add(this.deviceField4, null);
        this.jPanel6.add(this.deviceField5, null);
        this.jPanel1.add(this.jPanel7, null);
        this.jPanel7.add(this.deviceField6, null);
        this.jPanel7.add(this.deviceField7, null);
    }
}
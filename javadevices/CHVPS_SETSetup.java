import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import devicebeans.DeviceButtons;
import devicebeans.DeviceChoice;
import devicebeans.DeviceDispatch;
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
public class CHVPS_SETSetup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = 7012054313987031379L;
    BorderLayout   borderLayout1   = new BorderLayout();
    DeviceButtons  deviceButtons1  = new DeviceButtons();
    DeviceChoice   deviceChoice1   = new DeviceChoice();
    DeviceChoice   deviceChoice2   = new DeviceChoice();
    DeviceChoice   deviceChoice3   = new DeviceChoice();
    DeviceDispatch deviceDispatch1 = new DeviceDispatch();
    DeviceField    deviceField1    = new DeviceField();
    DeviceField    deviceField2    = new DeviceField();
    DeviceField    deviceField3    = new DeviceField();
    DeviceField    deviceField4    = new DeviceField();
    DeviceField    deviceField5    = new DeviceField();
    DeviceField    deviceField6    = new DeviceField();
    DeviceField    deviceField7    = new DeviceField();
    DeviceField    deviceField8    = new DeviceField();
    DeviceField    deviceField9    = new DeviceField();
    FlowLayout     flowLayout1     = new FlowLayout();
    FlowLayout     flowLayout2     = new FlowLayout();
    FlowLayout     flowLayout3     = new FlowLayout();
    FlowLayout     flowLayout4     = new FlowLayout();
    GridLayout     gridLayout1     = new GridLayout();
    JPanel         jPanel1         = new JPanel();
    JPanel         jPanel2         = new JPanel();
    JPanel         jPanel3         = new JPanel();
    JPanel         jPanel4         = new JPanel();
    JPanel         jPanel5         = new JPanel();

    public CHVPS_SETSetup(){
        try{
            this.jbInit();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setWidth(800);
        this.setHeight(240);
        this.setDeviceType("CHVPS_SET");
        this.setDeviceProvider("150.178.3.33");
        this.setDeviceTitle("CHVPS Channel set configuration");
        this.getContentPane().setLayout(this.borderLayout1);
        this.jPanel1.setLayout(this.gridLayout1);
        this.gridLayout1.setRows(4);
        this.deviceField1.setOffsetNid(1);
        this.deviceField1.setLabelString("Comment :");
        this.deviceField1.setNumCols(30);
        this.deviceField1.setIdentifier("");
        this.jPanel2.setLayout(this.flowLayout1);
        this.flowLayout1.setAlignment(FlowLayout.LEFT);
        this.deviceField2.setOffsetNid(2);
        this.deviceField2.setLabelString("CHVPS Target : ");
        this.deviceField2.setNumCols(20);
        this.deviceField2.setIdentifier("");
        this.jPanel3.setLayout(this.flowLayout2);
        this.flowLayout2.setAlignment(FlowLayout.LEFT);
        this.deviceField3.setOffsetNid(3);
        this.deviceField3.setLabelString("Set num.: ");
        this.deviceField3.setIdentifier("");
        this.deviceChoice1.setChoiceIntValues(null);
        this.deviceChoice1.setChoiceFloatValues(null);
        this.deviceChoice1.setOffsetNid(4);
        this.deviceChoice1.setLabelString("Reset Mode: ");
        this.deviceChoice1.setChoiceItems(new String[]{"Disable", "Enable"});
        this.deviceChoice1.setUpdateIdentifier("");
        this.deviceChoice1.setIdentifier("");
        this.deviceChoice2.setChoiceIntValues(null);
        this.deviceChoice2.setChoiceFloatValues(null);
        this.deviceChoice2.setOffsetNid(5);
        this.deviceChoice2.setLabelString("Gate Mode: ");
        this.deviceChoice2.setChoiceItems(new String[]{"None", "External"});
        this.deviceChoice2.setUpdateIdentifier("");
        this.deviceChoice2.setIdentifier("");
        this.deviceChoice3.setChoiceIntValues(null);
        this.deviceChoice3.setChoiceFloatValues(null);
        this.deviceChoice3.setOffsetNid(6);
        this.deviceChoice3.setLabelString("Overload Mode:");
        this.deviceChoice3.setChoiceItems(new String[]{"Disable", "Enable"});
        this.deviceChoice3.setUpdateIdentifier("");
        this.deviceChoice3.setIdentifier("");
        this.deviceField4.setOffsetNid(7);
        this.deviceField4.setLabelString("Trip Time: ");
        this.deviceField4.setIdentifier("");
        this.jPanel4.setLayout(this.flowLayout3);
        this.flowLayout3.setAlignment(FlowLayout.LEFT);
        this.deviceField5.setOffsetNid(10);
        this.deviceField5.setLabelString("Vmax: ");
        this.deviceField5.setIdentifier("");
        this.deviceField6.setOffsetNid(8);
        this.deviceField6.setLabelString("Voltage: ");
        this.deviceField6.setIdentifier("");
        this.deviceField7.setOffsetNid(9);
        this.deviceField7.setLabelString("Current");
        this.deviceField7.setIdentifier("");
        this.deviceField8.setOffsetNid(11);
        this.deviceField8.setLabelString("Raise Rate: ");
        this.deviceField8.setIdentifier("");
        this.deviceField9.setOffsetNid(12);
        this.deviceField9.setLabelString("Fall Rate: ");
        this.deviceField9.setIdentifier("");
        this.jPanel5.setLayout(this.flowLayout4);
        this.flowLayout4.setAlignment(FlowLayout.LEFT);
        this.deviceButtons1.setMethods(new String[]{"init"});
        this.getContentPane().add(this.deviceButtons1, BorderLayout.SOUTH);
        this.getContentPane().add(this.jPanel1, BorderLayout.CENTER);
        this.jPanel1.add(this.jPanel2, null);
        this.jPanel2.add(this.deviceField1, null);
        this.jPanel2.add(this.deviceDispatch1, null);
        this.jPanel1.add(this.jPanel3, null);
        this.jPanel3.add(this.deviceField2, null);
        this.jPanel3.add(this.deviceField3, null);
        this.jPanel3.add(this.deviceChoice1, null);
        this.jPanel1.add(this.jPanel4, null);
        this.jPanel4.add(this.deviceChoice2, null);
        this.jPanel4.add(this.deviceChoice3, null);
        this.jPanel4.add(this.deviceField4, null);
        this.jPanel4.add(this.deviceField5, null);
        this.jPanel1.add(this.jPanel5, null);
        this.jPanel5.add(this.deviceField6, null);
        this.jPanel5.add(this.deviceField7, null);
        this.jPanel5.add(this.deviceField8, null);
        this.jPanel5.add(this.deviceField9, null);
    }
}
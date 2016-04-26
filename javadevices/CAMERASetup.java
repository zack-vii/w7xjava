import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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
public class CAMERASetup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = -1576972716933547679L;
    Border         border1;
    BorderLayout   borderLayout1   = new BorderLayout();
    DeviceButtons  deviceButtons1  = new DeviceButtons();
    DeviceChoice   deviceChoice1   = new DeviceChoice();
    DeviceDispatch deviceDispatch1 = new DeviceDispatch();
    DeviceField    deviceField1    = new DeviceField();
    DeviceField    deviceField10   = new DeviceField();
    DeviceField    deviceField11   = new DeviceField();
    DeviceField    deviceField12   = new DeviceField();
    DeviceField    deviceField13   = new DeviceField();
    DeviceField    deviceField14   = new DeviceField();
    DeviceField    deviceField15   = new DeviceField();
    DeviceField    deviceField16   = new DeviceField();
    DeviceField    deviceField17   = new DeviceField();
    DeviceField    deviceField2    = new DeviceField();
    DeviceField    deviceField3    = new DeviceField();
    DeviceField    deviceField4    = new DeviceField();
    DeviceField    deviceField5    = new DeviceField();
    DeviceField    deviceField6    = new DeviceField();
    DeviceField    deviceField7    = new DeviceField();
    DeviceField    deviceField8    = new DeviceField();
    DeviceField    deviceField9    = new DeviceField();
    FlowLayout     flowLayout1     = new FlowLayout();
    FlowLayout     flowLayout10    = new FlowLayout();
    FlowLayout     flowLayout11    = new FlowLayout();
    FlowLayout     flowLayout12    = new FlowLayout();
    FlowLayout     flowLayout13    = new FlowLayout();
    FlowLayout     flowLayout14    = new FlowLayout();
    FlowLayout     flowLayout15    = new FlowLayout();
    FlowLayout     flowLayout2     = new FlowLayout();
    FlowLayout     flowLayout3     = new FlowLayout();
    FlowLayout     flowLayout4     = new FlowLayout();
    FlowLayout     flowLayout6     = new FlowLayout();
    FlowLayout     flowLayout7     = new FlowLayout();
    FlowLayout     flowLayout8     = new FlowLayout();
    FlowLayout     flowLayout9     = new FlowLayout();
    GridLayout     gridLayout1     = new GridLayout();
    GridLayout     gridLayout2     = new GridLayout();
    GridLayout     gridLayout3     = new GridLayout();
    JPanel         jPanel1         = new JPanel();
    JPanel         jPanel10        = new JPanel();
    JPanel         jPanel11        = new JPanel();
    JPanel         jPanel12        = new JPanel();
    JPanel         jPanel13        = new JPanel();
    JPanel         jPanel14        = new JPanel();
    JPanel         jPanel15        = new JPanel();
    JPanel         jPanel16        = new JPanel();
    JPanel         jPanel17        = new JPanel();
    JPanel         jPanel18        = new JPanel();
    JPanel         jPanel19        = new JPanel();
    JPanel         jPanel2         = new JPanel();
    JPanel         jPanel3         = new JPanel();
    JPanel         jPanel4         = new JPanel();
    JPanel         jPanel5         = new JPanel();
    JPanel         jPanel6         = new JPanel();
    JPanel         jPanel7         = new JPanel();
    TitledBorder   titledBorder1;

    public CAMERASetup(){
        try{
            this.jbInit();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.border1 = BorderFactory.createEtchedBorder(Color.white, new Color(148, 145, 140));
        this.titledBorder1 = new TitledBorder(this.border1, "Camera Info");
        this.setWidth(700);
        this.setHeight(380);
        this.setDeviceType("CAMERA");
        this.setDeviceProvider("150.178.3.33");
        this.setDeviceTitle("CAMERA");
        this.getContentPane().setLayout(this.borderLayout1);
        this.jPanel1.setLayout(this.gridLayout1);
        this.gridLayout1.setRows(2);
        this.jPanel2.setLayout(this.gridLayout2);
        this.gridLayout2.setRows(4);
        this.jPanel4.setLayout(this.flowLayout1);
        this.flowLayout1.setAlignment(FlowLayout.LEFT);
        this.flowLayout1.setHgap(0);
        this.flowLayout1.setVgap(0);
        this.jPanel5.setLayout(this.flowLayout2);
        this.flowLayout2.setAlignment(FlowLayout.LEFT);
        this.flowLayout2.setHgap(0);
        this.flowLayout2.setVgap(0);
        this.jPanel6.setLayout(this.flowLayout3);
        this.flowLayout3.setAlignment(FlowLayout.LEFT);
        this.flowLayout3.setHgap(0);
        this.flowLayout3.setVgap(0);
        this.deviceField1.setOffsetNid(1);
        this.deviceField1.setTextOnly(true);
        this.deviceField1.setLabelString("Comment: ");
        this.deviceField1.setNumCols(35);
        this.deviceField1.setIdentifier("");
        this.deviceField2.setOffsetNid(2);
        this.deviceField2.setLabelString("Name: ");
        this.deviceField2.setIdentifier("");
        this.deviceField3.setOffsetNid(3);
        this.deviceField3.setLabelString("Ip Address: ");
        this.deviceField3.setNumCols(15);
        this.deviceField3.setIdentifier("");
        this.deviceField4.setOffsetNid(4);
        this.deviceField4.setLabelString("Port: ");
        this.deviceField4.setIdentifier("");
        this.deviceChoice1.setChoiceIntValues(null);
        this.deviceChoice1.setChoiceFloatValues(null);
        this.deviceChoice1.setOffsetNid(5);
        this.deviceChoice1.setLabelString("Trig Mode:");
        this.deviceChoice1.setChoiceItems(new String[]{"INTERNAL", "EXTERNAL"});
        this.deviceChoice1.setUpdateIdentifier("");
        this.deviceChoice1.setIdentifier("");
        this.deviceField5.setOffsetNid(6);
        this.deviceField5.setLabelString("Trig. Source: ");
        this.deviceField5.setNumCols(30);
        this.deviceField5.setIdentifier("");
        this.jPanel7.setLayout(this.flowLayout4);
        this.flowLayout4.setAlignment(FlowLayout.LEFT);
        this.flowLayout4.setHgap(0);
        this.flowLayout4.setVgap(0);
        this.deviceField7.setOffsetNid(7);
        this.deviceField7.setLabelString("Num. Frame");
        this.deviceField7.setNumCols(15);
        this.deviceField7.setIdentifier("");
        this.deviceField6.setOffsetNid(18);
        this.deviceField6.setLabelString("Frame rate (f.p.s.)");
        this.deviceField6.setNumCols(15);
        this.deviceField6.setIdentifier("");
        this.jPanel3.setBorder(this.titledBorder1);
        this.jPanel3.setLayout(this.gridLayout3);
        this.deviceButtons1.setMethods(new String[]{"Init", "Arm", "Store"});
        this.gridLayout3.setColumns(2);
        this.gridLayout3.setRows(5);
        this.flowLayout6.setVgap(0);
        this.flowLayout6.setHgap(0);
        this.flowLayout6.setAlignment(FlowLayout.LEFT);
        this.jPanel10.setLayout(this.flowLayout6);
        this.flowLayout7.setVgap(0);
        this.flowLayout7.setHgap(0);
        this.flowLayout7.setAlignment(FlowLayout.LEFT);
        this.jPanel11.setLayout(this.flowLayout7);
        this.flowLayout8.setVgap(0);
        this.flowLayout8.setHgap(0);
        this.flowLayout8.setAlignment(FlowLayout.LEFT);
        this.jPanel12.setLayout(this.flowLayout8);
        this.flowLayout9.setVgap(0);
        this.flowLayout9.setHgap(0);
        this.flowLayout9.setAlignment(FlowLayout.LEFT);
        this.jPanel13.setLayout(this.flowLayout9);
        this.flowLayout10.setVgap(0);
        this.flowLayout10.setHgap(0);
        this.flowLayout10.setAlignment(FlowLayout.LEFT);
        this.jPanel14.setLayout(this.flowLayout10);
        this.flowLayout11.setVgap(0);
        this.flowLayout11.setHgap(0);
        this.flowLayout11.setAlignment(FlowLayout.LEFT);
        this.jPanel15.setLayout(this.flowLayout11);
        this.flowLayout12.setVgap(0);
        this.flowLayout12.setHgap(0);
        this.flowLayout12.setAlignment(FlowLayout.LEFT);
        this.jPanel16.setLayout(this.flowLayout12);
        this.flowLayout13.setVgap(0);
        this.flowLayout13.setHgap(0);
        this.flowLayout13.setAlignment(FlowLayout.LEFT);
        this.jPanel17.setLayout(this.flowLayout13);
        this.flowLayout14.setVgap(0);
        this.flowLayout14.setHgap(0);
        this.flowLayout14.setAlignment(FlowLayout.LEFT);
        this.jPanel18.setLayout(this.flowLayout14);
        this.flowLayout15.setVgap(0);
        this.flowLayout15.setHgap(0);
        this.flowLayout15.setAlignment(FlowLayout.LEFT);
        this.jPanel19.setLayout(this.flowLayout15);
        this.deviceField8.setOffsetNid(8);
        this.deviceField8.setTextOnly(true);
        this.deviceField8.setLabelString("Model: ");
        this.deviceField8.setNumCols(20);
        this.deviceField8.setIdentifier("");
        this.deviceField9.setOffsetNid(9);
        this.deviceField9.setTextOnly(true);
        this.deviceField9.setLabelString("Lens Type: ");
        this.deviceField9.setNumCols(20);
        this.deviceField9.setIdentifier("");
        this.deviceField10.setOffsetNid(10);
        this.deviceField10.setTextOnly(false);
        this.deviceField10.setLabelString("Aperture: ");
        this.deviceField10.setNumCols(20);
        this.deviceField10.setIdentifier("");
        this.deviceField11.setOffsetNid(11);
        this.deviceField11.setLabelString("F Distance: ");
        this.deviceField11.setNumCols(20);
        this.deviceField11.setIdentifier("");
        this.deviceField12.setOffsetNid(12);
        this.deviceField12.setTextOnly(true);
        this.deviceField12.setLabelString("Filter: ");
        this.deviceField12.setNumCols(20);
        this.deviceField12.setIdentifier("");
        this.deviceField13.setOffsetNid(13);
        this.deviceField13.setTextOnly(true);
        this.deviceField13.setLabelString("Shutter: ");
        this.deviceField13.setNumCols(20);
        this.deviceField13.setIdentifier("");
        this.deviceField14.setOffsetNid(14);
        this.deviceField14.setTextOnly(true);
        this.deviceField14.setLabelString("Tor. Pos. :");
        this.deviceField14.setNumCols(20);
        this.deviceField14.setIdentifier("");
        this.deviceField15.setOffsetNid(15);
        this.deviceField15.setTextOnly(true);
        this.deviceField15.setLabelString("Pol. Pos. :");
        this.deviceField15.setNumCols(20);
        this.deviceField15.setIdentifier("");
        this.deviceField16.setOffsetNid(16);
        this.deviceField16.setTextOnly(true);
        this.deviceField16.setLabelString("Target Zone :");
        this.deviceField16.setNumCols(20);
        this.deviceField16.setIdentifier("");
        this.deviceField17.setOffsetNid(17);
        this.deviceField17.setTextOnly(true);
        this.deviceField17.setLabelString("Pixel Frame: ");
        this.deviceField17.setNumCols(20);
        this.deviceField17.setIdentifier("");
        this.getContentPane().add(this.deviceButtons1, BorderLayout.SOUTH);
        this.getContentPane().add(this.jPanel1, BorderLayout.CENTER);
        this.jPanel1.add(this.jPanel2, null);
        this.jPanel2.add(this.jPanel4, null);
        this.jPanel4.add(this.deviceField1, null);
        this.jPanel4.add(this.deviceDispatch1, null);
        this.jPanel2.add(this.jPanel5, null);
        this.jPanel5.add(this.deviceField2, null);
        this.jPanel5.add(this.deviceField3, null);
        this.jPanel5.add(this.deviceField4, null);
        this.jPanel2.add(this.jPanel6, null);
        this.jPanel6.add(this.deviceChoice1, null);
        this.jPanel6.add(this.deviceField5, null);
        this.jPanel2.add(this.jPanel7, null);
        this.jPanel7.add(this.deviceField7, null);
        this.jPanel7.add(this.deviceField6, null);
        this.jPanel1.add(this.jPanel3, null);
        this.jPanel3.add(this.jPanel19, null);
        this.jPanel19.add(this.deviceField8, null);
        this.jPanel3.add(this.jPanel18, null);
        this.jPanel18.add(this.deviceField9, null);
        this.jPanel3.add(this.jPanel17, null);
        this.jPanel17.add(this.deviceField10, null);
        this.jPanel3.add(this.jPanel16, null);
        this.jPanel16.add(this.deviceField11, null);
        this.jPanel3.add(this.jPanel15, null);
        this.jPanel15.add(this.deviceField12, null);
        this.jPanel3.add(this.jPanel14, null);
        this.jPanel14.add(this.deviceField13, null);
        this.jPanel3.add(this.jPanel13, null);
        this.jPanel13.add(this.deviceField14, null);
        this.jPanel3.add(this.jPanel12, null);
        this.jPanel12.add(this.deviceField15, null);
        this.jPanel3.add(this.jPanel11, null);
        this.jPanel11.add(this.deviceField16, null);
        this.jPanel3.add(this.jPanel10, null);
        this.jPanel10.add(this.deviceField17, null);
    }
}
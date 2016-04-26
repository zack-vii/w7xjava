/*
 * A basic implementation of the DeviceSetup class.
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import devicebeans.DeviceButtons;
import devicebeans.DeviceChannel;
import devicebeans.DeviceChoice;
import devicebeans.DeviceDispatch;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;
import devicebeans.DeviceTable;

public class ACQD240Setup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = 4052076152974417940L;

    static public void main(final String args[]) {
        (new ACQD240Setup()).setVisible(true);
    }
    DeviceField             comment           = new DeviceField();
    javax.swing.JLabel      comment_label     = new javax.swing.JLabel();
    DeviceButtons           deviceButtons1    = new DeviceButtons();
    DeviceChannel           deviceChannel1    = new DeviceChannel();
    DeviceChannel           deviceChannel10   = new DeviceChannel();
    DeviceChannel           deviceChannel11   = new DeviceChannel();
    DeviceChannel           deviceChannel12   = new DeviceChannel();
    DeviceChannel           deviceChannel13   = new DeviceChannel();
    DeviceChannel           deviceChannel14   = new DeviceChannel();
    DeviceChannel           deviceChannel15   = new DeviceChannel();
    DeviceChannel           deviceChannel16   = new DeviceChannel();
    DeviceChannel           deviceChannel17   = new DeviceChannel();
    DeviceChannel           deviceChannel18   = new DeviceChannel();
    DeviceChannel           deviceChannel19   = new DeviceChannel();
    DeviceChannel           deviceChannel2    = new DeviceChannel();
    DeviceChannel           deviceChannel20   = new DeviceChannel();
    DeviceChannel           deviceChannel21   = new DeviceChannel();
    DeviceChannel           deviceChannel22   = new DeviceChannel();
    DeviceChannel           deviceChannel23   = new DeviceChannel();
    DeviceChannel           deviceChannel24   = new DeviceChannel();
    DeviceChannel           deviceChannel25   = new DeviceChannel();
    DeviceChannel           deviceChannel26   = new DeviceChannel();
    DeviceChannel           deviceChannel27   = new DeviceChannel();
    DeviceChannel           deviceChannel28   = new DeviceChannel();
    DeviceChannel           deviceChannel3    = new DeviceChannel();
    DeviceChannel           deviceChannel4    = new DeviceChannel();
    DeviceChannel           deviceChannel5    = new DeviceChannel();
    DeviceChannel           deviceChannel6    = new DeviceChannel();
    DeviceChannel           deviceChannel7    = new DeviceChannel();
    DeviceChannel           deviceChannel8    = new DeviceChannel();
    DeviceChannel           deviceChannel9    = new DeviceChannel();
    DeviceChoice            deviceChoice1     = new DeviceChoice();
    DeviceChoice            deviceChoice10    = new DeviceChoice();
    DeviceChoice            deviceChoice11    = new DeviceChoice();
    DeviceChoice            deviceChoice12    = new DeviceChoice();
    DeviceChoice            deviceChoice13    = new DeviceChoice();
    DeviceChoice            deviceChoice14    = new DeviceChoice();
    DeviceChoice            deviceChoice15    = new DeviceChoice();
    DeviceChoice            deviceChoice16    = new DeviceChoice();
    DeviceChoice            deviceChoice17    = new DeviceChoice();
    DeviceChoice            deviceChoice18    = new DeviceChoice();
    DeviceChoice            deviceChoice19    = new DeviceChoice();
    DeviceChoice            deviceChoice2     = new DeviceChoice();
    DeviceChoice            deviceChoice20    = new DeviceChoice();
    DeviceChoice            deviceChoice21    = new DeviceChoice();
    DeviceChoice            deviceChoice22    = new DeviceChoice();
    DeviceChoice            deviceChoice23    = new DeviceChoice();
    DeviceChoice            deviceChoice24    = new DeviceChoice();
    DeviceChoice            deviceChoice25    = new DeviceChoice();
    DeviceChoice            deviceChoice26    = new DeviceChoice();
    DeviceChoice            deviceChoice27    = new DeviceChoice();
    DeviceChoice            deviceChoice28    = new DeviceChoice();
    DeviceChoice            deviceChoice29    = new DeviceChoice();
    DeviceChoice            deviceChoice3     = new DeviceChoice();
    DeviceChoice            deviceChoice30    = new DeviceChoice();
    DeviceChoice            deviceChoice31    = new DeviceChoice();
    DeviceChoice            deviceChoice32    = new DeviceChoice();
    DeviceChoice            deviceChoice33    = new DeviceChoice();
    DeviceChoice            deviceChoice34    = new DeviceChoice();
    DeviceChoice            deviceChoice35    = new DeviceChoice();
    DeviceChoice            deviceChoice36    = new DeviceChoice();
    DeviceChoice            deviceChoice37    = new DeviceChoice();
    DeviceChoice            deviceChoice38    = new DeviceChoice();
    DeviceChoice            deviceChoice39    = new DeviceChoice();
    DeviceChoice            deviceChoice4     = new DeviceChoice();
    DeviceChoice            deviceChoice40    = new DeviceChoice();
    DeviceChoice            deviceChoice41    = new DeviceChoice();
    DeviceChoice            deviceChoice42    = new DeviceChoice();
    DeviceChoice            deviceChoice43    = new DeviceChoice();
    DeviceChoice            deviceChoice44    = new DeviceChoice();
    DeviceChoice            deviceChoice45    = new DeviceChoice();
    DeviceChoice            deviceChoice46    = new DeviceChoice();
    DeviceChoice            deviceChoice47    = new DeviceChoice();
    DeviceChoice            deviceChoice48    = new DeviceChoice();
    DeviceChoice            deviceChoice49    = new DeviceChoice();
    DeviceChoice            deviceChoice5     = new DeviceChoice();
    DeviceChoice            deviceChoice50    = new DeviceChoice();
    DeviceChoice            deviceChoice51    = new DeviceChoice();
    DeviceChoice            deviceChoice52    = new DeviceChoice();
    DeviceChoice            deviceChoice53    = new DeviceChoice();
    DeviceChoice            deviceChoice54    = new DeviceChoice();
    DeviceChoice            deviceChoice55    = new DeviceChoice();
    DeviceChoice            deviceChoice56    = new DeviceChoice();
    DeviceChoice            deviceChoice57    = new DeviceChoice();
    DeviceChoice            deviceChoice58    = new DeviceChoice();
    DeviceChoice            deviceChoice59    = new DeviceChoice();
    DeviceChoice            deviceChoice6     = new DeviceChoice();
    DeviceChoice            deviceChoice60    = new DeviceChoice();
    DeviceChoice            deviceChoice61    = new DeviceChoice();
    DeviceChoice            deviceChoice62    = new DeviceChoice();
    DeviceChoice            deviceChoice63    = new DeviceChoice();
    DeviceChoice            deviceChoice64    = new DeviceChoice();
    DeviceChoice            deviceChoice65    = new DeviceChoice();
    DeviceChoice            deviceChoice66    = new DeviceChoice();
    DeviceChoice            deviceChoice67    = new DeviceChoice();
    DeviceChoice            deviceChoice68    = new DeviceChoice();
    DeviceChoice            deviceChoice69    = new DeviceChoice();
    DeviceChoice            deviceChoice7     = new DeviceChoice();
    DeviceChoice            deviceChoice70    = new DeviceChoice();
    DeviceChoice            deviceChoice71    = new DeviceChoice();
    DeviceChoice            deviceChoice72    = new DeviceChoice();
    DeviceChoice            deviceChoice73    = new DeviceChoice();
    DeviceChoice            deviceChoice74    = new DeviceChoice();
    DeviceChoice            deviceChoice75    = new DeviceChoice();
    DeviceChoice            deviceChoice76    = new DeviceChoice();
    DeviceChoice            deviceChoice77    = new DeviceChoice();
    DeviceChoice            deviceChoice78    = new DeviceChoice();
    DeviceChoice            deviceChoice79    = new DeviceChoice();
    DeviceChoice            deviceChoice8     = new DeviceChoice();
    DeviceChoice            deviceChoice80    = new DeviceChoice();
    DeviceChoice            deviceChoice81    = new DeviceChoice();
    DeviceChoice            deviceChoice82    = new DeviceChoice();
    DeviceChoice            deviceChoice83    = new DeviceChoice();
    DeviceChoice            deviceChoice84    = new DeviceChoice();
    DeviceChoice            deviceChoice85    = new DeviceChoice();
    DeviceChoice            deviceChoice86    = new DeviceChoice();
    DeviceChoice            deviceChoice87    = new DeviceChoice();
    DeviceChoice            deviceChoice88    = new DeviceChoice();
    DeviceChoice            deviceChoice89    = new DeviceChoice();
    DeviceChoice            deviceChoice9     = new DeviceChoice();
    DeviceDispatch          deviceDispatch1   = new DeviceDispatch();
    DeviceField             deviceField1      = new DeviceField();
    DeviceField             deviceField11     = new DeviceField();
    DeviceField             deviceField13     = new DeviceField();
    DeviceField             deviceField15     = new DeviceField();
    DeviceField             deviceField17     = new DeviceField();
    DeviceField             deviceField19     = new DeviceField();
    DeviceField             deviceField2      = new DeviceField();
    DeviceField             deviceField21     = new DeviceField();
    DeviceField             deviceField23     = new DeviceField();
    DeviceField             deviceField25     = new DeviceField();
    DeviceField             deviceField27     = new DeviceField();
    DeviceField             deviceField29     = new DeviceField();
    DeviceField             deviceField3      = new DeviceField();
    DeviceField             deviceField31     = new DeviceField();
    DeviceField             deviceField33     = new DeviceField();
    DeviceField             deviceField35     = new DeviceField();
    DeviceField             deviceField37     = new DeviceField();
    DeviceField             deviceField39     = new DeviceField();
    DeviceField             deviceField4      = new DeviceField();
    DeviceField             deviceField41     = new DeviceField();
    DeviceField             deviceField43     = new DeviceField();
    DeviceField             deviceField45     = new DeviceField();
    DeviceField             deviceField47     = new DeviceField();
    DeviceField             deviceField49     = new DeviceField();
    DeviceField             deviceField5      = new DeviceField();
    DeviceField             deviceField51     = new DeviceField();
    DeviceField             deviceField53     = new DeviceField();
    DeviceField             deviceField55     = new DeviceField();
    DeviceField             deviceField57     = new DeviceField();
    DeviceField             deviceField59     = new DeviceField();
    DeviceField             deviceField6      = new DeviceField();
    DeviceField             deviceField61     = new DeviceField();
    DeviceField             deviceField63     = new DeviceField();
    DeviceField             deviceField7      = new DeviceField();
    DeviceField             deviceField9      = new DeviceField();
    DeviceTable             deviceTable1      = new DeviceTable();
    DeviceTable             deviceTable2      = new DeviceTable();
    // }}
    DeviceTable             deviceTable3      = new DeviceTable();
    // Used by addNotify
    boolean                 frameSizeAdjusted = false;
    javax.swing.JLabel      JLabel1           = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel10          = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel11          = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel12          = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel13          = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel14          = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel16          = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel2           = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel3           = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel4           = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel5           = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel6           = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel7           = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel8           = new javax.swing.JLabel();
    javax.swing.JLabel      JLabel9           = new javax.swing.JLabel();
    javax.swing.JPanel      JPanel10          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel11          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel12          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel13          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel14          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel15          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel16          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel2           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel3           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel4           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel5           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel6           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel7           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel8           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel9           = new javax.swing.JPanel();
    javax.swing.JTabbedPane JTabbedPane1      = new javax.swing.JTabbedPane();
    DeviceField             name              = new DeviceField();
    // {{DECLARE_CONTROLS
    javax.swing.JLabel      name_label        = new javax.swing.JLabel();

    public ACQD240Setup(){
        this((JFrame)null);
    }

    public ACQD240Setup(final JFrame parent){
        super(parent);
        this.initComponents();
    }

    public ACQD240Setup(final String sTitle){
        this();
        setTitle(sTitle);
    }

    public void addNotify() {
        // Record the size of the window prior to calling parents addNotify.
        final Dimension size = getSize();
        super.addNotify();
        if(this.frameSizeAdjusted) return;
        this.frameSizeAdjusted = true;
        // Adjust size of frame according to the insets
        final Insets insets = getInsets();
        setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height);
    }

    private void initComponents() {
        // This code is automatically generated by Visual Cafe when you add
        // components to the visual environment. It instantiates and initializes
        // the components. To modify the code, only use code syntax that matches
        // what Visual Cafe can generate, or Visual Cafe may be unable to back
        // parse your Java file into its visual environment.
        // {{INIT_CONTROLS
        setDeviceTitle("Acqiris Digitizer Model DC240");
        setDeviceType("ACQD240");
        getContentPane().setLayout(null);
        setSize(600, 592);
        this.name_label.setText("Name:");
        getContentPane().add(this.name_label);
        this.name_label.setBounds(10, 42, 42, 24);
        this.name.setNumCols(15);
        this.name.setIdentifier("dev_name");
        this.name.setTextOnly(true);
        this.name.setOffsetNid(1);
        getContentPane().add(this.name);
        this.name.setBounds(40, 35, 361, 29);
        this.comment_label.setText("Comment:");
        getContentPane().add(this.comment_label);
        this.comment_label.setBounds(10, 7, 65, 27);
        this.comment.setNumCols(35);
        this.comment.setTextOnly(true);
        this.comment.setOffsetNid(2);
        getContentPane().add(this.comment);
        this.comment.setBounds(40, 7, 480, 28);
        this.JPanel2.setLayout(null);
        getContentPane().add(this.JPanel2);
        this.JPanel2.setBounds(11, 105, 527, 96);
        this.deviceChoice1.setOffsetNid(13);
        {
            final String[] tempString = new String[2];
            tempString[0] = "INTERNAL";
            tempString[1] = "EXTERNAL";
            this.deviceChoice1.setChoiceItems(tempString);
        }
        this.deviceChoice1.setLabelString("Trig. Mode: ");
        this.JPanel2.add(this.deviceChoice1);
        this.deviceChoice1.setBounds(2, 1, 177, 34);
        this.deviceField3.setNumCols(5);
        this.deviceField3.setOffsetNid(14);
        this.JPanel2.add(this.deviceField3);
        this.deviceField3.setBounds(79, 35, 87, 28);
        this.JLabel3.setText("Trig. Source:");
        this.JLabel3.setToolTipText("Time value of the first trigger pule.");
        this.JPanel2.add(this.JLabel3);
        this.JLabel3.setBounds(9, 35, 110, 28);
        this.JLabel4.setText("Trig. Delay:");
        this.JPanel2.add(this.JLabel4);
        this.JLabel4.setBounds(9, 64, 89, 28);
        this.deviceField4.setNumCols(5);
        this.deviceField4.setOffsetNid(15);
        this.JPanel2.add(this.deviceField4);
        this.deviceField4.setBounds(79, 64, 87, 28);
        this.JLabel5.setText("Trig. Level:");
        this.JLabel5.setToolTipText("In % of the vertical Full Scale of the channel, or in mV if using external trig source");
        this.JPanel2.add(this.JLabel5);
        this.JLabel5.setBounds(172, 35, 69, 28);
        this.deviceField5.setNumCols(5);
        this.deviceField5.setOffsetNid(19);
        this.JPanel2.add(this.deviceField5);
        this.deviceField5.setBounds(245, 35, 87, 28);
        this.JLabel6.setText("Trig. Channel:");
        this.JLabel6.setToolTipText("Positive number: channel trigger number (Internal trigger), board number (External trigger)");
        this.JPanel2.add(this.JLabel6);
        this.JLabel6.setBounds(172, 64, 96, 28);
        this.deviceField6.setNumCols(5);
        this.deviceField6.setOffsetNid(16);
        this.JPanel2.add(this.deviceField6);
        this.deviceField6.setBounds(245, 64, 92, 28);
        this.deviceChoice2.setOffsetNid(17);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC";
            tempString[1] = "AC";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice2.setChoiceItems(tempString);
        }
        this.deviceChoice2.setLabelString("Trig. Coupling:");
        this.JPanel2.add(this.deviceChoice2);
        this.deviceChoice2.setBounds(177, 1, 205, 34);
        this.deviceChoice3.setOffsetNid(18);
        {
            final String[] tempString = new String[2];
            tempString[0] = "POS";
            tempString[1] = "NEG";
            this.deviceChoice3.setChoiceItems(tempString);
        }
        this.deviceChoice3.setLabelString("Trig. Slope:");
        this.JPanel2.add(this.deviceChoice3);
        this.deviceChoice3.setBounds(375, 1, 142, 34);
        this.JLabel2.setText("Segment Number:");
        this.JPanel2.add(this.JLabel2);
        this.JLabel2.setBounds(336, 64, 110, 28);
        this.JLabel1.setText("Segment Points:");
        this.JPanel2.add(this.JLabel1);
        this.JLabel1.setBounds(337, 35, 99, 28);
        this.deviceField1.setNumCols(5);
        this.deviceField1.setIdentifier("seg_points");
        this.deviceField1.setOffsetNid(10);
        this.JPanel2.add(this.deviceField1);
        this.deviceField1.setBounds(433, 35, 92, 28);
        this.deviceField2.setNumCols(5);
        this.deviceField2.setIdentifier("seg_num");
        this.deviceField2.setOffsetNid(11);
        this.JPanel2.add(this.deviceField2);
        this.deviceField2.setBounds(432, 64, 87, 28);
        this.deviceTable1.setNumCols(1);
        {
            final String[] tempString = new String[1];
            tempString[0] = "Serial_Number";
            this.deviceTable1.setColumnNames(tempString);
        }
        this.deviceTable1.setNumRows(7);
        this.deviceTable1.setOffsetNid(3);
        getContentPane().add(this.deviceTable1);
        this.deviceTable1.setBounds(19, 200, 121, 142);
        {
            final String[] tempString = new String[4];
            tempString[0] = "Device name must be defined";
            tempString[1] = "Segment number must be  > 0";
            tempString[2] = "Segment points must be > 0";
            tempString[3] = "Acquired samples must be <= 128k";
            this.deviceButtons1.setCheckMessages(tempString);
        }
        {
            final String[] tempString = new String[4];
            tempString[0] = "len(_dev_name)>0";
            tempString[1] = "_seg_num > 0";
            tempString[2] = "_seg_points > 0";
            tempString[3] = "_seg_num * _seg_points < 128000";
            this.deviceButtons1.setCheckExpressions(tempString);
        }
        {
            final String[] tempString = new String[8];
            tempString[0] = "calibrate";
            tempString[1] = "pre_init";
            tempString[2] = "init";
            tempString[3] = "config";
            tempString[4] = "start";
            tempString[5] = "trigger";
            tempString[6] = "stop";
            tempString[7] = "store";
            this.deviceButtons1.setMethods(tempString);
        }
        getContentPane().add(this.deviceButtons1);
        this.deviceButtons1.setBounds(132, 553, 312, 40);
        getContentPane().add(this.deviceDispatch1);
        this.deviceDispatch1.setBounds(404, 33, 136, 33);
        this.deviceChoice4.setOffsetNid(5);
        {
            final String[] tempString = new String[3];
            tempString[0] = "INTERNAL";
            tempString[1] = "EXTERNAL";
            tempString[2] = "EXT Ref 10MHz";
            this.deviceChoice4.setChoiceItems(tempString);
        }
        this.deviceChoice4.setLabelString("Ck. Mode:");
        getContentPane().add(this.deviceChoice4);
        this.deviceChoice4.setBounds(7, 70, 205, 30);
        this.deviceChoice5.setChoiceFloatValues(new float[]{(float)1.0E2, (float)2.0E2, (float)2.5E2, (float)4.0E2, (float)5.0E2, (float)1.0E3, (float)2.0E3, (float)2.5E3, (float)4.0E3, (float)5.0E3, (float)1.0E4, (float)2.0E4, (float)2.5E4, (float)4.0E4, (float)5.0E4, (float)1.0E5, (float)2.0E5, (float)2.5E5, (float)4.0E5, (float)5.0E5, (float)1.0E6, (float)2.0E6, (float)2.5E6, (float)4.0E6, (float)5.0E6, (float)1.0E7, (float)2.0E7, (float)2.5E7, (float)4.0E7, (float)5.0E7, (float)1.0E8, (float)2.0E8, (float)2.5E8, (float)4.0E8, (float)5.0E8, (float)1.0E9, (float)2.0E9});
        this.deviceChoice5.setOffsetNid(8);
        {
            final String[] tempString = new String[37];
            tempString[0] = "100";
            tempString[1] = "200";
            tempString[2] = "250";
            tempString[3] = "400";
            tempString[4] = "500";
            tempString[5] = "1000";
            tempString[6] = "2000";
            tempString[7] = "2500";
            tempString[8] = "4000";
            tempString[9] = "5000";
            tempString[10] = "10E3";
            tempString[11] = "20E3";
            tempString[12] = "25E3";
            tempString[13] = "40E3";
            tempString[14] = "50E3";
            tempString[15] = "100E3";
            tempString[16] = "200E3";
            tempString[17] = "250E3";
            tempString[18] = "400E3";
            tempString[19] = "500E3";
            tempString[20] = "1E6";
            tempString[21] = "2E6";
            tempString[22] = "2.5E6";
            tempString[23] = "4E6";
            tempString[24] = "5E6";
            tempString[25] = "10E6";
            tempString[26] = "20E6";
            tempString[27] = "25E5";
            tempString[28] = "40E6";
            tempString[29] = "50E6";
            tempString[30] = "100E6";
            tempString[31] = "200E6";
            tempString[32] = "250E6";
            tempString[33] = "400E6";
            tempString[34] = "500E6";
            tempString[35] = "1E9";
            tempString[36] = "2E9";
            this.deviceChoice5.setChoiceItems(tempString);
        }
        this.deviceChoice5.setLabelString("Freq. :");
        getContentPane().add(this.deviceChoice5);
        this.deviceChoice5.setBounds(208, 70, 116, 30);
        this.JLabel7.setText("Ck. Source:");
        this.JLabel7.setToolTipText("External clock source reference (External clock source not implemented)");
        getContentPane().add(this.JLabel7);
        this.JLabel7.setBounds(351, 72, 72, 30);
        this.deviceField7.setOffsetNid(6);
        this.deviceField7.setNumCols(6);
        getContentPane().add(this.deviceField7);
        this.deviceField7.setBounds(413, 72, 120, 30);
        this.JLabel8.setText("Hz");
        getContentPane().add(this.JLabel8);
        this.JLabel8.setBounds(324, 72, 23, 30);
        getContentPane().add(this.JTabbedPane1);
        this.JTabbedPane1.setBounds(8, 357, 549, 188);
        this.JPanel9.setAlignmentX(0.498925F);
        this.JPanel9.setLayout(null);
        this.JTabbedPane1.add(this.JPanel9);
        this.JPanel9.setBackground(new java.awt.Color(204, 204, 204));
        this.JPanel9.setBounds(2, 24, 524, 161);
        this.JPanel9.setVisible(false);
        this.JPanel10.setAlignmentY(0.0F);
        this.JPanel10.setLayout(new BoxLayout(this.JPanel10, BoxLayout.X_AXIS));
        this.JPanel9.add(this.JPanel10);
        this.JPanel10.setBounds(22, 0, 490, 15);
        this.JLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.JLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        this.JLabel16.setText("                      Full Scale  (V)              Offset  (V)                     Coupling                     Bandwidth");
        this.JPanel10.add(this.JLabel16);
        this.deviceChannel13.setInSameLine(true);
        this.deviceChannel13.setOffsetNid(20);
        this.deviceChannel13.setLabelString("Ch01");
        this.deviceChannel13.setLayout(new BorderLayout(0, 0));
        this.JPanel9.add(this.deviceChannel13);
        this.deviceChannel13.setBounds(6, 15, 524, 44);
        this.deviceChoice62.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice62.setOffsetNid(21);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice62.setChoiceItems(tempString);
        }
        this.deviceChoice62.setLabelString(" ");
        this.deviceChannel13.add(this.deviceChoice62);
        this.deviceField33.setNumCols(8);
        this.deviceField33.setOffsetNid(22);
        this.deviceChannel13.add(this.deviceField33);
        this.deviceChoice30.setOffsetNid(23);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice30.setChoiceItems(tempString);
        }
        this.deviceChoice30.setLabelString(" ");
        this.deviceChannel13.add(this.deviceChoice30);
        this.deviceChoice31.setOffsetNid(24);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice31.setChoiceItems(tempString);
        }
        this.deviceChoice31.setLabelString(" ");
        this.deviceChannel13.add(this.deviceChoice31);
        this.deviceChannel14.setInSameLine(true);
        this.deviceChannel14.setOffsetNid(26);
        this.deviceChannel14.setLabelString("Ch02");
        this.deviceChannel14.setLayout(new BorderLayout(0, 0));
        this.JPanel9.add(this.deviceChannel14);
        this.deviceChannel14.setBounds(6, 49, 524, 44);
        this.deviceChoice63.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice63.setOffsetNid(27);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice63.setChoiceItems(tempString);
        }
        this.deviceChoice63.setLabelString(" ");
        this.deviceChannel14.add(this.deviceChoice63);
        this.deviceField35.setNumCols(8);
        this.deviceField35.setOffsetNid(28);
        this.deviceChannel14.add(this.deviceField35);
        this.deviceChoice32.setOffsetNid(29);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice32.setChoiceItems(tempString);
        }
        this.deviceChoice32.setLabelString(" ");
        this.deviceChannel14.add(this.deviceChoice32);
        this.deviceChoice33.setOffsetNid(24);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice33.setChoiceItems(tempString);
        }
        this.deviceChoice33.setLabelString(" ");
        this.deviceChannel14.add(this.deviceChoice33);
        this.JPanel3.setAlignmentX(0.498925F);
        this.JPanel3.setLayout(null);
        this.JTabbedPane1.add(this.JPanel3);
        this.JPanel3.setBackground(new java.awt.Color(204, 204, 204));
        this.JPanel3.setBounds(2, 24, 524, 161);
        this.JPanel3.setVisible(false);
        this.JPanel4.setAlignmentY(0.0F);
        this.JPanel4.setLayout(new BoxLayout(this.JPanel4, BoxLayout.X_AXIS));
        this.JPanel3.add(this.JPanel4);
        this.JPanel4.setBounds(22, 0, 465, 15);
        this.JLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.JLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        this.JLabel9.setText("                      Full Scale  (V)              Offset  (V)                     Coupling                     Bandwidth");
        this.JPanel4.add(this.JLabel9);
        this.deviceChannel15.setInSameLine(true);
        this.deviceChannel15.setOffsetNid(32);
        this.deviceChannel15.setLabelString("Ch03");
        this.deviceChannel15.setLayout(new BorderLayout(0, 0));
        this.JPanel3.add(this.deviceChannel15);
        this.deviceChannel15.setBounds(6, 15, 524, 44);
        this.deviceChoice64.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice64.setOffsetNid(33);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice64.setChoiceItems(tempString);
        }
        this.deviceChoice64.setLabelString(" ");
        this.deviceChannel15.add(this.deviceChoice64);
        this.deviceField37.setNumCols(8);
        this.deviceField37.setOffsetNid(34);
        this.deviceChannel15.add(this.deviceField37);
        this.deviceChoice34.setOffsetNid(35);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice34.setChoiceItems(tempString);
        }
        this.deviceChoice34.setLabelString(" ");
        this.deviceChannel15.add(this.deviceChoice34);
        this.deviceChoice35.setOffsetNid(36);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice35.setChoiceItems(tempString);
        }
        this.deviceChoice35.setLabelString(" ");
        this.deviceChannel15.add(this.deviceChoice35);
        this.deviceChannel16.setInSameLine(true);
        this.deviceChannel16.setOffsetNid(38);
        this.deviceChannel16.setLabelString("Ch04");
        this.deviceChannel16.setLayout(new BorderLayout(0, 0));
        this.JPanel3.add(this.deviceChannel16);
        this.deviceChannel16.setBounds(6, 49, 524, 44);
        this.deviceChoice65.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice65.setOffsetNid(39);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice65.setChoiceItems(tempString);
        }
        this.deviceChoice65.setLabelString(" ");
        this.deviceChannel16.add(this.deviceChoice65);
        this.deviceField39.setNumCols(8);
        this.deviceField39.setOffsetNid(40);
        this.deviceChannel16.add(this.deviceField39);
        this.deviceChoice36.setOffsetNid(41);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice36.setChoiceItems(tempString);
        }
        this.deviceChoice36.setLabelString(" ");
        this.deviceChannel16.add(this.deviceChoice36);
        this.deviceChoice37.setOffsetNid(42);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice37.setChoiceItems(tempString);
        }
        this.deviceChoice37.setLabelString(" ");
        this.deviceChannel16.add(this.deviceChoice37);
        this.JPanel7.setAlignmentX(0.498925F);
        this.JPanel7.setLayout(null);
        this.JTabbedPane1.add(this.JPanel7);
        this.JPanel7.setBackground(new java.awt.Color(204, 204, 204));
        this.JPanel7.setBounds(2, 24, 524, 161);
        this.JPanel7.setVisible(false);
        this.JPanel8.setAlignmentY(0.0F);
        this.JPanel8.setLayout(new BoxLayout(this.JPanel8, BoxLayout.X_AXIS));
        this.JPanel7.add(this.JPanel8);
        this.JPanel8.setBounds(22, 0, 465, 15);
        this.JLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.JLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        this.JLabel11.setText("                      Full Scale  (V)              Offset  (V)                     Coupling                     Bandwidth");
        this.JPanel8.add(this.JLabel11);
        this.deviceChannel1.setInSameLine(true);
        this.deviceChannel1.setOffsetNid(44);
        this.deviceChannel1.setLabelString("Ch05");
        this.deviceChannel1.setLayout(new BorderLayout(0, 0));
        this.JPanel7.add(this.deviceChannel1);
        this.deviceChannel1.setBounds(6, 15, 524, 44);
        this.deviceChoice66.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice66.setOffsetNid(45);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice66.setChoiceItems(tempString);
        }
        this.deviceChoice66.setLabelString(" ");
        this.deviceChannel1.add(this.deviceChoice66);
        this.deviceField9.setNumCols(8);
        this.deviceField9.setOffsetNid(46);
        this.deviceChannel1.add(this.deviceField9);
        this.deviceChoice6.setOffsetNid(47);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice6.setChoiceItems(tempString);
        }
        this.deviceChoice6.setLabelString(" ");
        this.deviceChannel1.add(this.deviceChoice6);
        this.deviceChoice7.setOffsetNid(48);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice7.setChoiceItems(tempString);
        }
        this.deviceChoice7.setLabelString(" ");
        this.deviceChannel1.add(this.deviceChoice7);
        this.deviceChannel2.setInSameLine(true);
        this.deviceChannel2.setOffsetNid(50);
        this.deviceChannel2.setLabelString("Ch06");
        this.deviceChannel2.setLayout(new BorderLayout(0, 0));
        this.JPanel7.add(this.deviceChannel2);
        this.deviceChannel2.setBounds(6, 49, 524, 44);
        this.deviceChoice67.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice67.setOffsetNid(51);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice67.setChoiceItems(tempString);
        }
        this.deviceChoice67.setLabelString(" ");
        this.deviceChannel2.add(this.deviceChoice67);
        this.deviceField11.setNumCols(8);
        this.deviceField11.setOffsetNid(52);
        this.deviceChannel2.add(this.deviceField11);
        this.deviceChoice8.setOffsetNid(53);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice8.setChoiceItems(tempString);
        }
        this.deviceChoice8.setLabelString(" ");
        this.deviceChannel2.add(this.deviceChoice8);
        this.deviceChoice9.setOffsetNid(54);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice9.setChoiceItems(tempString);
        }
        this.deviceChoice9.setLabelString(" ");
        this.deviceChannel2.add(this.deviceChoice9);
        this.JPanel5.setAlignmentX(0.498925F);
        this.JPanel5.setLayout(null);
        this.JTabbedPane1.add(this.JPanel5);
        this.JPanel5.setBackground(new java.awt.Color(204, 204, 204));
        this.JPanel5.setBounds(2, 24, 524, 161);
        this.JPanel5.setVisible(false);
        this.JPanel6.setAlignmentY(0.0F);
        this.JPanel6.setLayout(new BoxLayout(this.JPanel6, BoxLayout.X_AXIS));
        this.JPanel5.add(this.JPanel6);
        this.JPanel6.setBounds(22, 0, 465, 15);
        this.JLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.JLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        this.JLabel10.setText("                      Full Scale  (V)              Offset  (V)                     Coupling                     Bandwidth");
        this.JPanel6.add(this.JLabel10);
        this.deviceChannel3.setInSameLine(true);
        this.deviceChannel3.setOffsetNid(56);
        this.deviceChannel3.setLabelString("Ch07");
        this.deviceChannel3.setLayout(new BorderLayout(0, 0));
        this.JPanel5.add(this.deviceChannel3);
        this.deviceChannel3.setBounds(6, 15, 524, 44);
        this.deviceChoice68.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice68.setOffsetNid(57);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice68.setChoiceItems(tempString);
        }
        this.deviceChoice68.setLabelString(" ");
        this.deviceChannel3.add(this.deviceChoice68);
        this.deviceField13.setNumCols(8);
        this.deviceField13.setOffsetNid(58);
        this.deviceChannel3.add(this.deviceField13);
        this.deviceChoice10.setOffsetNid(59);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice10.setChoiceItems(tempString);
        }
        this.deviceChoice10.setLabelString(" ");
        this.deviceChannel3.add(this.deviceChoice10);
        this.deviceChoice11.setOffsetNid(60);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice11.setChoiceItems(tempString);
        }
        this.deviceChoice11.setLabelString(" ");
        this.deviceChannel3.add(this.deviceChoice11);
        this.deviceChannel4.setInSameLine(true);
        this.deviceChannel4.setOffsetNid(62);
        this.deviceChannel4.setLabelString("Ch08");
        this.deviceChannel4.setLayout(new BorderLayout(0, 0));
        this.JPanel5.add(this.deviceChannel4);
        this.deviceChannel4.setBounds(6, 49, 524, 44);
        this.deviceChoice69.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice69.setOffsetNid(63);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice69.setChoiceItems(tempString);
        }
        this.deviceChoice69.setLabelString(" ");
        this.deviceChannel4.add(this.deviceChoice69);
        this.deviceField15.setNumCols(8);
        this.deviceField15.setOffsetNid(64);
        this.deviceChannel4.add(this.deviceField15);
        this.deviceChoice12.setOffsetNid(65);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice12.setChoiceItems(tempString);
        }
        this.deviceChoice12.setLabelString(" ");
        this.deviceChannel4.add(this.deviceChoice12);
        this.deviceChoice13.setOffsetNid(66);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice13.setChoiceItems(tempString);
        }
        this.deviceChoice13.setLabelString(" ");
        this.deviceChannel4.add(this.deviceChoice13);
        this.JPanel11.setAlignmentX(0.498925F);
        this.JPanel11.setLayout(null);
        this.JTabbedPane1.add(this.JPanel11);
        this.JPanel11.setBackground(new java.awt.Color(204, 204, 204));
        this.JPanel11.setBounds(2, 24, 524, 161);
        this.JPanel11.setVisible(false);
        this.JPanel12.setAlignmentY(0.0F);
        this.JPanel12.setLayout(new BoxLayout(this.JPanel12, BoxLayout.X_AXIS));
        this.JPanel11.add(this.JPanel12);
        this.JPanel12.setBounds(22, 0, 465, 15);
        this.JLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.JLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        this.JLabel12.setText("                      Full Scale  (V)              Offset  (V)                     Coupling                     Bandwidth");
        this.JPanel12.add(this.JLabel12);
        this.deviceChannel5.setInSameLine(true);
        this.deviceChannel5.setOffsetNid(68);
        this.deviceChannel5.setLabelString("Ch09");
        this.deviceChannel5.setLayout(new BorderLayout(0, 0));
        this.JPanel11.add(this.deviceChannel5);
        this.deviceChannel5.setBounds(6, 15, 524, 44);
        this.deviceChoice70.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice70.setOffsetNid(69);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice70.setChoiceItems(tempString);
        }
        this.deviceChoice70.setLabelString(" ");
        this.deviceChannel5.add(this.deviceChoice70);
        this.deviceField17.setNumCols(8);
        this.deviceField17.setOffsetNid(70);
        this.deviceChannel5.add(this.deviceField17);
        this.deviceChoice14.setOffsetNid(71);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice14.setChoiceItems(tempString);
        }
        this.deviceChoice14.setLabelString(" ");
        this.deviceChannel5.add(this.deviceChoice14);
        this.deviceChoice15.setOffsetNid(72);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice15.setChoiceItems(tempString);
        }
        this.deviceChoice15.setLabelString(" ");
        this.deviceChannel5.add(this.deviceChoice15);
        this.deviceChannel6.setInSameLine(true);
        this.deviceChannel6.setOffsetNid(74);
        this.deviceChannel6.setLabelString("Ch10");
        this.deviceChannel6.setLayout(new BorderLayout(0, 0));
        this.JPanel11.add(this.deviceChannel6);
        this.deviceChannel6.setBounds(6, 49, 524, 44);
        this.deviceChoice71.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice71.setOffsetNid(75);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice71.setChoiceItems(tempString);
        }
        this.deviceChoice71.setLabelString(" ");
        this.deviceChannel6.add(this.deviceChoice71);
        this.deviceField19.setNumCols(8);
        this.deviceField19.setOffsetNid(76);
        this.deviceChannel6.add(this.deviceField19);
        this.deviceChoice16.setOffsetNid(77);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice16.setChoiceItems(tempString);
        }
        this.deviceChoice16.setLabelString(" ");
        this.deviceChannel6.add(this.deviceChoice16);
        this.deviceChoice17.setOffsetNid(78);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice17.setChoiceItems(tempString);
        }
        this.deviceChoice17.setLabelString(" ");
        this.deviceChannel6.add(this.deviceChoice17);
        this.JPanel13.setAlignmentX(0.498925F);
        this.JPanel13.setLayout(null);
        this.JTabbedPane1.add(this.JPanel13);
        this.JPanel13.setBackground(new java.awt.Color(204, 204, 204));
        this.JPanel13.setBounds(2, 24, 524, 161);
        this.JPanel13.setVisible(false);
        this.JPanel14.setAlignmentY(0.0F);
        this.JPanel14.setLayout(new BoxLayout(this.JPanel14, BoxLayout.X_AXIS));
        this.JPanel13.add(this.JPanel14);
        this.JPanel14.setBounds(22, 0, 465, 15);
        this.JLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.JLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        this.JLabel13.setText("                      Full Scale  (V)              Offset  (V)                     Coupling                     Bandwidth");
        this.JPanel14.add(this.JLabel13);
        this.deviceChannel7.setInSameLine(true);
        this.deviceChannel7.setOffsetNid(80);
        this.deviceChannel7.setLabelString("Ch11");
        this.deviceChannel7.setLayout(new BorderLayout(0, 0));
        this.JPanel13.add(this.deviceChannel7);
        this.deviceChannel7.setBounds(6, 15, 524, 44);
        this.deviceChoice72.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice72.setOffsetNid(81);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice72.setChoiceItems(tempString);
        }
        this.deviceChoice72.setLabelString(" ");
        this.deviceChannel7.add(this.deviceChoice72);
        this.deviceField21.setNumCols(8);
        this.deviceField21.setOffsetNid(82);
        this.deviceChannel7.add(this.deviceField21);
        this.deviceChoice18.setOffsetNid(83);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice18.setChoiceItems(tempString);
        }
        this.deviceChoice18.setLabelString(" ");
        this.deviceChannel7.add(this.deviceChoice18);
        this.deviceChoice19.setOffsetNid(84);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice19.setChoiceItems(tempString);
        }
        this.deviceChoice19.setLabelString(" ");
        this.deviceChannel7.add(this.deviceChoice19);
        this.deviceChannel8.setInSameLine(true);
        this.deviceChannel8.setOffsetNid(86);
        this.deviceChannel8.setLabelString("Ch12");
        this.deviceChannel8.setLayout(new BorderLayout(0, 0));
        this.JPanel13.add(this.deviceChannel8);
        this.deviceChannel8.setBounds(6, 49, 524, 44);
        this.deviceChoice73.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice73.setOffsetNid(87);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice73.setChoiceItems(tempString);
        }
        this.deviceChoice73.setLabelString(" ");
        this.deviceChannel8.add(this.deviceChoice73);
        this.deviceField23.setNumCols(8);
        this.deviceField23.setOffsetNid(88);
        this.deviceChannel8.add(this.deviceField23);
        this.deviceChoice20.setOffsetNid(89);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice20.setChoiceItems(tempString);
        }
        this.deviceChoice20.setLabelString(" ");
        this.deviceChannel8.add(this.deviceChoice20);
        this.deviceChoice21.setOffsetNid(90);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice21.setChoiceItems(tempString);
        }
        this.deviceChoice21.setLabelString(" ");
        this.deviceChannel8.add(this.deviceChoice21);
        this.JPanel15.setAlignmentX(0.498925F);
        this.JPanel15.setLayout(null);
        this.JTabbedPane1.add(this.JPanel15);
        this.JPanel15.setBackground(new java.awt.Color(204, 204, 204));
        this.JPanel15.setBounds(2, 24, 524, 161);
        this.JPanel15.setVisible(false);
        this.JPanel16.setAlignmentY(0.0F);
        this.JPanel16.setLayout(new BoxLayout(this.JPanel16, BoxLayout.X_AXIS));
        this.JPanel15.add(this.JPanel16);
        this.JPanel16.setBounds(22, 0, 465, 15);
        this.JLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        this.JLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        this.JLabel14.setText("                      Full Scale  (V)              Offset  (V)                     Coupling                     Bandwidth");
        this.JPanel16.add(this.JLabel14);
        this.JLabel14.setForeground(new java.awt.Color(102, 102, 153));
        this.deviceChannel9.setInSameLine(true);
        this.deviceChannel9.setOffsetNid(92);
        this.deviceChannel9.setLabelString("Ch13");
        this.deviceChannel9.setLayout(new BorderLayout(0, 0));
        this.JPanel15.add(this.deviceChannel9);
        this.deviceChannel9.setBounds(6, 15, 524, 44);
        this.deviceChoice74.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice74.setOffsetNid(93);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice74.setChoiceItems(tempString);
        }
        this.deviceChoice74.setLabelString(" ");
        this.deviceChannel9.add(this.deviceChoice74);
        this.deviceField25.setNumCols(8);
        this.deviceField25.setOffsetNid(94);
        this.deviceChannel9.add(this.deviceField25);
        this.deviceChoice22.setOffsetNid(95);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice22.setChoiceItems(tempString);
        }
        this.deviceChoice22.setLabelString(" ");
        this.deviceChannel9.add(this.deviceChoice22);
        this.deviceChoice23.setOffsetNid(96);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice23.setChoiceItems(tempString);
        }
        this.deviceChoice23.setLabelString(" ");
        this.deviceChannel9.add(this.deviceChoice23);
        this.deviceChannel10.setInSameLine(true);
        this.deviceChannel10.setOffsetNid(98);
        this.deviceChannel10.setLabelString("Ch14");
        this.deviceChannel10.setLayout(new BorderLayout(0, 0));
        this.JPanel15.add(this.deviceChannel10);
        this.deviceChannel10.setBounds(6, 49, 524, 44);
        this.deviceChoice75.setChoiceFloatValues(new float[]{(float)0.05, (float)0.1, (float)0.2, (float)0.5, (float)1.0, (float)2.0, (float)5.0});
        this.deviceChoice75.setOffsetNid(99);
        {
            final String[] tempString = new String[7];
            tempString[0] = "50e-3";
            tempString[1] = "100e-3";
            tempString[2] = "200e-3";
            tempString[3] = "500e-3";
            tempString[4] = "1";
            tempString[5] = "2";
            tempString[6] = "5 ";
            this.deviceChoice75.setChoiceItems(tempString);
        }
        this.deviceChoice75.setLabelString(" ");
        this.deviceChannel10.add(this.deviceChoice75);
        this.deviceField27.setNumCols(8);
        this.deviceField27.setOffsetNid(100);
        this.deviceChannel10.add(this.deviceField27);
        this.deviceChoice24.setOffsetNid(101);
        {
            final String[] tempString = new String[4];
            tempString[0] = "DC 1M";
            tempString[1] = "AC 1M";
            tempString[2] = "DC 50ohm";
            tempString[3] = "AC 50ohm";
            this.deviceChoice24.setChoiceItems(tempString);
        }
        this.deviceChoice24.setLabelString(" ");
        this.deviceChannel10.add(this.deviceChoice24);
        this.deviceChoice25.setOffsetNid(102);
        {
            final String[] tempString = new String[2];
            tempString[0] = "LIMIT ON";
            tempString[1] = "LIMIT OFF";
            this.deviceChoice25.setChoiceItems(tempString);
        }
        this.deviceChoice25.setLabelString(" ");
        this.deviceChannel10.add(this.deviceChoice25);
        this.JTabbedPane1.setSelectedIndex(0);
        this.JTabbedPane1.setSelectedComponent(this.JPanel9);
        this.JTabbedPane1.setTitleAt(0, "Board 1");
        this.JTabbedPane1.setTitleAt(1, "Board 2");
        this.JTabbedPane1.setTitleAt(2, "Board 3");
        this.JTabbedPane1.setTitleAt(3, "Board 4");
        this.JTabbedPane1.setTitleAt(4, "Board 5");
        this.JTabbedPane1.setTitleAt(5, "Board 6");
        this.JTabbedPane1.setTitleAt(6, "Board 7");
        this.deviceTable3.setNumCols(1);
        {
            final String[] tempString = new String[1];
            tempString[0] = "Temperature";
            this.deviceTable3.setColumnNames(tempString);
        }
        this.deviceTable3.setNumRows(7);
        this.deviceTable3.setOffsetNid(7);
        this.deviceTable3.setEditable(false);
        getContentPane().add(this.deviceTable3);
        this.deviceTable3.setBounds(250, 200, 100, 142);
        this.deviceTable2.setNumCols(1);
        {
            final String[] tempString = new String[1];
            tempString[0] = "Slot number";
            this.deviceTable2.setColumnNames(tempString);
        }
        this.deviceTable2.setNumRows(7);
        this.deviceTable2.setOffsetNid(4);
        this.deviceTable2.setEditable(false);
        getContentPane().add(this.deviceTable2);
        this.deviceTable2.setBounds(142, 200, 106, 142);
        // }}
    }

    public void setVisible(final boolean b) {
        if(b) setLocation(50, 50);
        super.setVisible(b);
    }
}

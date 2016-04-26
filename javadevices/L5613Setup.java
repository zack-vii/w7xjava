/*
 * A basic implementation of the DeviceSetup class.
 */
import java.awt.*;
import javax.swing.*;
import devicebeans.DeviceButtons;
import devicebeans.DeviceChoice;
import devicebeans.DeviceDispatch;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;

public class L5613Setup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = -3696881007225815771L;
    public L5613Setup(JFrame parent){
        super(parent);
        // This code is automatically generated by Visual Cafe when you add
        // components to the visual environment. It instantiates and initializes
        // the components. To modify the code, only use code syntax that matches
        // what Visual Cafe can generate, or Visual Cafe may be unable to back
        // parse your Java file into its visual environment.
        // {{INIT_CONTROLS
        setDeviceProvider("150.178.3.101");
        setDeviceTitle("LeCroy - L5613");
        setDeviceType("L5613");
        getContentPane().setLayout(null);
        setSize(457, 260);
        deviceField1.setNumCols(15);
        deviceField1.setTextOnly(true);
        deviceField1.setOffsetNid(1);
        deviceField1.setLabelString("Camac Name: ");
        getContentPane().add(deviceField1);
        deviceField1.setBounds(12, 12, 300, 40);
        getContentPane().add(deviceDispatch1);
        deviceDispatch1.setBounds(324, 12, 131, 40);
        deviceField2.setNumCols(30);
        deviceField2.setTextOnly(true);
        deviceField2.setOffsetNid(2);
        deviceField2.setLabelString("Comment: ");
        getContentPane().add(deviceField2);
        deviceField2.setBounds(12, 60, 456, 40);
        deviceChoice1.setOffsetNid(3);
        {
            String[] tempString = new String[4];
            tempString[0] = "STD";
            tempString[1] = "100";
            tempString[2] = "200";
            tempString[3] = "300";
            deviceChoice1.setChoiceItems(tempString);
        }
        deviceChoice1.setLabelString("Model: ");
        getContentPane().add(deviceChoice1);
        deviceChoice1.setBounds(0, 96, 132, 40);
        deviceChoice2.setChoiceFloatValues(new float[]{(float)100.0, (float)50.0, (float)20.0, (float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2});
        deviceChoice2.setOffsetNid(7);
        {
            String[] tempString = new String[9];
            tempString[0] = "100";
            tempString[1] = "50";
            tempString[2] = "20";
            tempString[3] = "10";
            tempString[4] = "5";
            tempString[5] = "2";
            tempString[6] = "1";
            tempString[7] = "0.5";
            tempString[8] = "0.2";
            deviceChoice2.setChoiceItems(tempString);
        }
        deviceChoice2.setLabelString("Chan A range: ");
        getContentPane().add(deviceChoice2);
        deviceChoice2.setBounds(132, 96, 168, 40);
        deviceChoice3.setChoiceFloatValues(new float[]{(float)100.0, (float)50.0, (float)20.0, (float)10.0, (float)5.0, (float)2.0, (float)1.0, (float)0.5, (float)0.2});
        deviceChoice3.setOffsetNid(11);
        {
            String[] tempString = new String[9];
            tempString[0] = "100";
            tempString[1] = "50";
            tempString[2] = "20";
            tempString[3] = "10";
            tempString[4] = "5";
            tempString[5] = "2";
            tempString[6] = "1";
            tempString[7] = "0.5";
            tempString[8] = "0.2";
            deviceChoice3.setChoiceItems(tempString);
        }
        deviceChoice3.setLabelString("Chan B range");
        getContentPane().add(deviceChoice3);
        deviceChoice3.setBounds(300, 96, 158, 40);
        deviceField3.setNumCols(30);
        deviceField3.setOffsetNid(5);
        deviceField3.setLabelString("Chan A Output:  ");
        getContentPane().add(deviceField3);
        deviceField3.setBounds(0, 144, 456, 40);
        deviceField4.setNumCols(30);
        deviceField4.setOffsetNid(9);
        deviceField4.setLabelString("Chan B Output: ");
        getContentPane().add(deviceField4);
        deviceField4.setBounds(0, 180, 456, 40);
        getContentPane().add(deviceButtons1);
        deviceButtons1.setBounds(108, 216, 300, 40);
        // }}
    }

    public L5613Setup(){
        this((JFrame)null);
    }

    public L5613Setup(String sTitle){
        this();
        setTitle(sTitle);
    }

    static public void main(String args[]) {
        (new L5613Setup()).setVisible(true);
    }

    public void addNotify() {
        // Record the size of the window prior to calling parents addNotify.
        Dimension size = getSize();
        super.addNotify();
        if(frameSizeAdjusted) return;
        frameSizeAdjusted = true;
        // Adjust size of frame according to the insets
        Insets insets = getInsets();
        setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height);
    }
    // Used by addNotify
    boolean        frameSizeAdjusted = false;
    // {{DECLARE_CONTROLS
    DeviceField    deviceField1      = new DeviceField();
    DeviceDispatch deviceDispatch1   = new DeviceDispatch();
    DeviceField    deviceField2      = new DeviceField();
    DeviceChoice   deviceChoice1     = new DeviceChoice();
    DeviceChoice   deviceChoice2     = new DeviceChoice();
    DeviceChoice   deviceChoice3     = new DeviceChoice();
    DeviceField    deviceField3      = new DeviceField();
    DeviceField    deviceField4      = new DeviceField();
    DeviceButtons  deviceButtons1    = new DeviceButtons();
    // }}
}

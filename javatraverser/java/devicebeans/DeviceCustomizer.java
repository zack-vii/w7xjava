package devicebeans;

import java.awt.Panel;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class DeviceCustomizer extends Panel{
    static mds.mdsip.Connection deviceProvider;
    static String               lastDeviceProvider = null;
    static String               lastDeviceType     = null;
    static String[]             lastFields;

    public static String[] getDeviceFields() {
        if(DeviceSetupBeanInfo.beanDeviceType == null){
            DeviceSetupBeanInfo.beanDeviceType = JOptionPane.showInputDialog("Please define the device type");
        }
        System.out.println("Device type: " + DeviceSetupBeanInfo.beanDeviceType);
        System.out.println("Inquiring Device Provider...");
        if(DeviceSetupBeanInfo.beanDeviceProvider == null){
            DeviceSetupBeanInfo.beanDeviceProvider = JOptionPane.showInputDialog("Please define the IP address of the device repository");
        }
        System.out.println("Device Provider: " + DeviceSetupBeanInfo.beanDeviceProvider);
        System.out.println("lastDeviceType = " + DeviceCustomizer.lastDeviceType);
        if(DeviceCustomizer.lastDeviceType != null && DeviceCustomizer.lastDeviceType.equals(DeviceSetupBeanInfo.beanDeviceType)) return DeviceCustomizer.lastFields;
        DeviceCustomizer.lastDeviceType = DeviceSetupBeanInfo.beanDeviceType;
        String linFields = "";
        if(DeviceCustomizer.deviceProvider == null || !DeviceCustomizer.deviceProvider.equals(DeviceSetupBeanInfo.beanDeviceProvider)){
            DeviceCustomizer.deviceProvider = new mds.mdsip.Connection(DeviceSetupBeanInfo.beanDeviceProvider);
        }
        byte[] linBytes = null;
        try{
            linBytes = DeviceCustomizer.deviceProvider.getByteBuffer("JavaGetDeviceFields(\"" + DeviceSetupBeanInfo.beanDeviceType + "\")").array();
            linFields = new String(linBytes);
            // linFields = deviceProvider.GetString("JavaGetDeviceFields(\""+
            // DeviceSetupBeanInfo.beanDeviceType + "\")");
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(null, "Cannot retrieve device field names: " + exc);
        }
        final StringTokenizer st = new StringTokenizer(linFields);
        DeviceCustomizer.lastFields = new String[st.countTokens()];
        for(int i = 0; i < DeviceCustomizer.lastFields.length; i++)
            DeviceCustomizer.lastFields[i] = st.nextToken();
        if(DeviceCustomizer.lastFields.length == 0) // If name retrieval failed
            JOptionPane.showMessageDialog(null, "Unable to retrieve device field names: check deviceType and deviceProvider main form properties");
        return DeviceCustomizer.lastFields;
    }
    String dummies[] = {":NAME", ":COMMENT", ":ACT_CHANNELS", ":CLOCK_MODE", ":CLOCK_SOURCE", ":FREQUENCY", ":TRIGGER_MODE", ":TRIG_SOURCE", ":INIT_ACTION", ":STORE_ACTION", ".CHANNEL_1", ".CHANNEL_1:START", ".CHANNEL_1:END", ".CHANNEL_1:DATA", ".CHANNEL_2", ".CHANNEL_2:START", ".CHANNEL_2:END", ".CHANNEL_2:DATA", ".CHANNEL_3", ".CHANNEL_3:START", ".CHANNEL_3:END", ".CHANNEL_3:DATA", ".CHANNEL_4", ".CHANNEL_4:START", ".CHANNEL_4:END", ".CHANNEL_4:DATA"};
}

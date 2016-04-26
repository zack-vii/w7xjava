package devicebeans;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceSetupBeanInfo extends SimpleBeanInfo{
    static String beanDeviceProvider = null;
    static String beanDeviceType     = null;

    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceSetup.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceSetup.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceSetupBeanInfo.property("width", "width"), DeviceSetupBeanInfo.property("height", "height"), DeviceSetupBeanInfo.property("deviceType", "The MDS type of the device"), DeviceSetupBeanInfo.property("deviceProvider", "The IP address of the device repository"), DeviceSetupBeanInfo.property("deviceTitle", "The title of the device setup form"), DeviceSetupBeanInfo.property("layout", "The Layout manager"),};
            return props;
        }catch(final IntrospectionException e){
            return super.getPropertyDescriptors();
        }
    }
}

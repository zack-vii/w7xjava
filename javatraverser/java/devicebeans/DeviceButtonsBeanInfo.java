package devicebeans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceButtonsBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceButtons.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(DeviceButtons.class, DeviceButtonsCustomizer.class);
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceButtons.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceButtonsBeanInfo.property("checkExpressions", "Check expressions"), DeviceButtonsBeanInfo.property("checkMessages", "Check messages"), DeviceButtonsBeanInfo.property("methods", "Method list for the device")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceButtons: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

package devicebeans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceWaveDisplayBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceChannel.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(DeviceWaveDisplay.class, DeviceWaveDisplayCustomizer.class);
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceWave.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceWaveDisplayBeanInfo.property("offsetNid", "Offset nid"), DeviceWaveDisplayBeanInfo.property("prefHeight", "Preferred height")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceChannel: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

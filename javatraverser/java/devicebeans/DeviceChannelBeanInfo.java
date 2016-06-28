package devicebeans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceChannelBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceChannel.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        final BeanDescriptor desc = new BeanDescriptor(DeviceChannel.class, DeviceChannelCustomizer.class);
        desc.setValue("allowedChildTypes", new String[]{"DeviceComponent"});
        desc.setValue("disallowedChildTypes", new String[]{});
        desc.setValue("isContainer", Boolean.TRUE);
        desc.setValue("containerDelegate", "getContainer");
        return desc;
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceChannel.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceChannelBeanInfo.property("labelString", "Label string"), DeviceChannelBeanInfo.property("offsetNid", "Offset nid"), DeviceChannelBeanInfo.property("borderVisible", "Display border"), DeviceChannelBeanInfo.property("lines", "Number of lines"), DeviceChannelBeanInfo.property("columns", "Number of Columns"), DeviceChannelBeanInfo.property("inSameLine", "All Items in same line"), DeviceChannelBeanInfo.property("showState", "Display channel state"), DeviceChannelBeanInfo.property("updateIdentifier", "Show identifier"), DeviceChannelBeanInfo.property("showVal", "Show value"), DeviceChannelBeanInfo.property("layout", "Layout Manager")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceChannel: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

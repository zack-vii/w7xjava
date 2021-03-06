package devicebeans.devicewave;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceWaveParametersBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceWaveParameters.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("structure.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceWaveParametersBeanInfo.property("offsetNid", "Offset nid"), DeviceWaveParametersBeanInfo.property("baseName", "Base name")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceParameters: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

package devicebeans;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceParametersBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceParameters.class);
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
            final PropertyDescriptor[] props = {DeviceParametersBeanInfo.property("offsetNid", "Offset nid"), DeviceParametersBeanInfo.property("baseName", "Base name")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceParameters: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

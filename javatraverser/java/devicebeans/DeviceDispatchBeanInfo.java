package devicebeans;

import java.awt.Image;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceDispatchBeanInfo extends SimpleBeanInfo{
    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceDispatch.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return new PropertyDescriptor[]{};
    }
}

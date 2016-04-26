package devicebeans;

import java.awt.Image;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceResetBeanInfo extends SimpleBeanInfo{
    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceReset.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return new PropertyDescriptor[]{};
    }
}
/* Do nothing and prevent editing by means of bean builders */

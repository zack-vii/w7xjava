package devicebeans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceFieldBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceField.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(DeviceField.class, DeviceFieldCustomizer.class);
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceField.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceFieldBeanInfo.property("offsetNid", "Offset nid"), DeviceFieldBeanInfo.property("textOnly", "Deals only with text"), DeviceFieldBeanInfo.property("labelString", "Field label"), DeviceFieldBeanInfo.property("numCols", "Number of columns"), DeviceFieldBeanInfo.property("identifier", "Optional field identifier"), DeviceFieldBeanInfo.property("showState", "Display on/off state"), DeviceFieldBeanInfo.property("editable", "Text field editable"), DeviceFieldBeanInfo.property("displayEvaluated", "Display evaluated data"), DeviceFieldBeanInfo.property("preferredWidth", "Preferred width")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceField: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

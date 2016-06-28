package devicebeans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceLabelBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceLabel.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(DeviceLabel.class, DeviceLabelCustomizer.class);
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceLabel.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceLabelBeanInfo.property("offsetNid", "Offset nid"), DeviceLabelBeanInfo.property("textOnly", "Deals only with text"), DeviceLabelBeanInfo.property("labelString", "Field label"), DeviceLabelBeanInfo.property("numCols", "Number of columns"), DeviceLabelBeanInfo.property("identifier", "Optional field identifier"),
            // property("showState", "Display on/off state"),
            // property("editable", "Text field editable"),
                    DeviceLabelBeanInfo.property("displayEvaluated", "Display evaluated data"), DeviceLabelBeanInfo.property("preferredWidth", "Preferred width")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceField: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

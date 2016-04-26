package devicebeans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceTableBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceTable.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(DeviceTable.class, DeviceTableCustomizer.class);
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceTable.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceTableBeanInfo.property("offsetNid", "Offset nid"), DeviceTableBeanInfo.property("labelString", "Field label"), DeviceTableBeanInfo.property("numCols", "Number of columns"), DeviceTableBeanInfo.property("numRows", "Number of rows"), DeviceTableBeanInfo.property("identifier", "Optional field identifier"), DeviceTableBeanInfo.property("columnNames", "Column names"), DeviceTableBeanInfo.property("rowNames", "Row names"), DeviceTableBeanInfo.property("editable", "Editable"), DeviceTableBeanInfo.property("binary", "Binary"), DeviceTableBeanInfo.property("displayRowNumber", "displayRowNumber"), DeviceTableBeanInfo.property("preferredColumnWidth", "preferredColumnWidth"), DeviceTableBeanInfo.property("preferredHeight", "preferredHeight"), DeviceTableBeanInfo.property("useExpressions", "Use Expressions"), DeviceTableBeanInfo.property("refMode", "Reflexion Mode")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceTable: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

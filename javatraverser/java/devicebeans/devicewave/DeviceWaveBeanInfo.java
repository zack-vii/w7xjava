package devicebeans.devicewave;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceWaveBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceWave.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(DeviceWave.class, DeviceWaveCustomizer.class);
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceWave.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceWaveBeanInfo.property("offsetNid", "Offset nid"), DeviceWaveBeanInfo.property("minXVisible", "Display min X"), DeviceWaveBeanInfo.property("maxXVisible", "Display max X"), DeviceWaveBeanInfo.property("minYVisible", "Display min Y"), DeviceWaveBeanInfo.property("maxYVisible", "Display max Y"), DeviceWaveBeanInfo.property("identifier", "Optional field identifier"), DeviceWaveBeanInfo.property("updateIdentifier", "Update identifier"), DeviceWaveBeanInfo.property("updateExpression", "Update expression"), DeviceWaveBeanInfo.property("prefHeight", "Preferred height")};
            return props;
        }catch(final IntrospectionException e){
            System.out.println("DeviceWave: property exception " + e);
            return super.getPropertyDescriptors();
        }
    }
}

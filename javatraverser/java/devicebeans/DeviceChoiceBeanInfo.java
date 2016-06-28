package devicebeans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DeviceChoiceBeanInfo extends SimpleBeanInfo{
    public static PropertyDescriptor property(final String name, final String description) throws IntrospectionException {
        final PropertyDescriptor p = new PropertyDescriptor(name, DeviceChoice.class);
        p.setShortDescription(description);
        return p;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(DeviceChoice.class, DeviceChoiceCustomizer.class);
    }

    @Override
    public Image getIcon(final int kind) {
        return this.loadImage("DeviceChoice.gif");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try{
            final PropertyDescriptor[] props = {DeviceChoiceBeanInfo.property("choiceIntValues", "Integer items"), DeviceChoiceBeanInfo.property("choiceFloatValues", "Float items"), DeviceChoiceBeanInfo.property("convert", "Enable code conversion"), DeviceChoiceBeanInfo.property("offsetNid", "Offset of the associated node in the conglomerate - MANDATORY!!"), DeviceChoiceBeanInfo.property("labelString", "Associated label text"), DeviceChoiceBeanInfo.property("showState", "Enable state check box"), DeviceChoiceBeanInfo.property("convert", "Enable code conversion"), DeviceChoiceBeanInfo.property("choiceItems", "String items"), DeviceChoiceBeanInfo.property("updateIdentifier", "Update identifier"), DeviceChoiceBeanInfo.property("identifier", "Optional identifier")};
            props[0].setPropertyEditorClass(IntArrayEditor.class);
            props[1].setPropertyEditorClass(FloatArrayEditor.class);
            return props;
        }catch(final IntrospectionException e){
            System.out.println("Exception in DeviceChoice: " + e);
            return super.getPropertyDescriptors();
        }
    }
}

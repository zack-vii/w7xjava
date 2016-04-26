package devicebeans;

import java.beans.PropertyEditorSupport;

public class IntArrayEditor extends PropertyEditorSupport{
    @Override
    public final String getJavaInitializationString() {
        final int[] array = (int[])this.getValue();
        if(array == null) return "null";
        final StringBuffer sb = new StringBuffer("new int[] {");
        for(int i = 0; i < array.length; i++){
            sb.append("(int)" + (new Integer(array[i])).toString());
            if(i < (array.length - 1)) sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }
}
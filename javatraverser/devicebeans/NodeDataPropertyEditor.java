package devicebeans;

// package jtraverser;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import mds.Database;
import mds.data.descriptor.Descriptor;

public abstract class NodeDataPropertyEditor implements PropertyEditor{
    protected Descriptor data;

    // event notification not used here
    @Override
    public final void addPropertyChangeListener(final PropertyChangeListener l) {}

    @Override
    public final String getAsText() {
        try{
            return this.data.toString();
        }catch(final Exception e){
            return null;
        }
    }

    @Override
    public abstract Component getCustomEditor(); // to be subclassed

    @Override
    public final String getJavaInitializationString() {
        return null;
    }

    @Override
    public final String[] getTags() {
        return null;
    }

    @Override
    public final Object getValue() {
        return this.data;
    }

    @Override
    public final boolean isPaintable() {
        return false;
    }

    @Override
    public final void paintValue(final Graphics g, final Rectangle r) {}

    @Override
    public final void removePropertyChangeListener(final PropertyChangeListener l) {}

    @Override
    public final void setAsText(final String s) {
        try{
            this.data = Database.tdiCompile(s);
        }catch(final Exception e){
            this.data = null;
        }
    }

    @Override
    public final void setValue(final Object o) {
        this.data = (Descriptor)o;
    }

    @Override
    public final boolean supportsCustomEditor() {
        return true;
    }
}
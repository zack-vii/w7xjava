package devicebeans;

// package jTraverser;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import jTraverser.editor.NodeEditor;
import mds.data.descriptor.Descriptor;

public class NodeInfoPropertyEditor implements PropertyEditor{
    protected Descriptor data;

    // event notification not used here
    @Override
    public final void addPropertyChangeListener(final PropertyChangeListener l) {}

    @Override
    public final String getAsText() {
        return null;
    }

    @Override
    public final Component getCustomEditor() {
        return new NodeEditor();
    }

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
    public final void setAsText(final String s) {}

    @Override
    public final void setValue(final Object o) {
        this.data = (Descriptor)o;
    }

    @Override
    public final boolean supportsCustomEditor() {
        return true;
    }
}
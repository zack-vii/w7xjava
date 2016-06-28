package jtraverser.editor;

import mds.MdsException;
import mds.data.descriptor.Descriptor;

public interface Editor{
    Descriptor getData() throws MdsException;

    boolean isNull();

    void reset();

    void setEditable(boolean editable);
}
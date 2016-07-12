package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Missing;

public final class Dim extends BUILD{
    public Dim(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Dim(final Descriptor window, final Descriptor axis){
        super(DTYPE.DIMENSION, null, window, axis);
    }

    public final Descriptor getAxis() {
        return this.getDescriptor(1);
    }

    @Override
    public Descriptor getData() {
        if(this.getDescriptor(0) == Missing.NEW) return this.getAxis();
        return this;// TODO getData
    }

    public final Descriptor getWindow() {
        return this.getDescriptor(0);
    }
}

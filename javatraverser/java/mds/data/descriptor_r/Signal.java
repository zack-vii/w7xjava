package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Signal extends BUILD{
    public Signal(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Signal(final Descriptor data, final Descriptor raw, final Descriptor dim, final Descriptor... dims){
        super(DTYPE.ROUTINE, null, dims, data, raw, dim);
    }

    @Override
    public final Descriptor getData() {
        return this.getDescriptor(0);
    }

    public final Descriptor getDimension() {
        return this.getDimension(0);
    }

    public final Descriptor getDimension(final int idx) {
        return this.getDimension(2 + idx);
    }

    public final Descriptor getRaw() {
        return this.getDescriptor(1);
    }

    @Override
    public final int[] getShape() {
        return this.getData().getShape();
    }
}

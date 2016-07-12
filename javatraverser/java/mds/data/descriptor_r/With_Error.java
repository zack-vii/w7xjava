package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class With_Error extends BUILD{
    public With_Error(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public With_Error(final Descriptor data, final Descriptor error){
        super(DTYPE.WITH_ERROR, null, data, error);
    }

    @Override
    public final Descriptor getData() {
        return this.getDescriptor(0);
    }

    public final Descriptor getError() {
        return this.getDescriptor(1);
    }

    @Override
    public final int[] getShape() {
        return this.getData().getShape();
    }
}

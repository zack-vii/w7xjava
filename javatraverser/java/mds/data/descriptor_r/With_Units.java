package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class With_Units extends BUILD{
    public With_Units(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public With_Units(final Descriptor data, final Descriptor units){
        super(DTYPE.WITH_UNITS, null, new Descriptor[]{data, units});
    }

    @Override
    public final Descriptor getData() {
        return this.getDescriptor(0);
    }

    @Override
    public final int[] getShape() {
        return this.getData().getShape();
    }

    public final Descriptor getUnits() {
        return this.getDescriptor(1);
    }
}

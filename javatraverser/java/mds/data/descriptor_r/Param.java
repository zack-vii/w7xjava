package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Param extends BUILD{
    public Param(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Param(final Descriptor data, final Descriptor help, final Descriptor valid){
        super(DTYPE.PARAM, null, data, help, valid);
    }

    @Override
    public final Descriptor getData() {
        return this.getDescriptor(0);
    }

    public final Descriptor getHelp() {
        return this.getDescriptor(1);
    }

    @Override
    public final int[] getShape() {
        return this.getData().getShape();
    }

    public final Descriptor getValidation() {
        return this.getDescriptor(2);
    }
}

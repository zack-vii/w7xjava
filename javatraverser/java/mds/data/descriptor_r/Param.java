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
        super(DTYPE.PARAM, null, new Descriptor[]{data, help, valid});
    }

    public final Descriptor getData() {
        return this.getDescriptor(0);
    }

    public final Descriptor getHelp() {
        return this.getDescriptor(1);
    }
    }

    public final Descriptor getValidation() {
        return this.getDescriptor(2);
    }

    @Override
    public final double[] toDouble() {
        return this.getData().toDouble();
    }

    @Override
    public final float[] toFloat() {
        return this.getData().toFloat();
    }

    @Override
    public final int[] toInt() {
        return this.getData().toInt();
    }

    @Override
    public final long[] toLong() {
        return this.getData().toLong();
    }
}

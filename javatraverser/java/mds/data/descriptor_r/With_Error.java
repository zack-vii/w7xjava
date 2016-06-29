package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class With_Error extends BUILD{
    public With_Error(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public With_Error(final Descriptor data, final Descriptor opaque_type){
        super(DTYPE.WITH_ERROR, null, new Descriptor[]{data, opaque_type});
    }

    @Override
    public final Descriptor getData() {
        return this.getDescriptor(0);
    }

    public final Descriptor getOpaqueType() {
        return this.getDescriptor(1);
    }

    @Override
    public final int[] getShape() {
        return this.getData().getShape();
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
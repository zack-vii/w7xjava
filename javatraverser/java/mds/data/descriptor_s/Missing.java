package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_S;

public final class Missing extends Descriptor_S{
    public static final Missing NEW = new Missing();

    private Missing(){
        super(DTYPE.MISSING, ByteBuffer.allocate(0).order(Descriptor.BYTEORDER));
    }

    private Missing(final ByteBuffer b){
        super(b);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return pout.append('*');
    }

    @Override
    protected final Object getValue(final ByteBuffer b) {
        return null;
    }

    @Override
    public final byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public final double[] toDoubleArray() {
        return new double[0];
    }

    @Override
    public final float[] toFloatArray() {
        return new float[0];
    }

    @Override
    public final int[] toIntArray() {
        return new int[0];
    }

    @Override
    public final long[] toLongArray() {
        return new long[0];
    }
}

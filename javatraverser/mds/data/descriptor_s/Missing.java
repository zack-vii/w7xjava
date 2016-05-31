package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor_S;

public final class Missing extends Descriptor_S{
    public Missing(){
        super(DTYPE.MISSING, new byte[0]);
    }

    public Missing(final ByteBuffer b){
        super(b);
    }

    @Override
    public final String decompile() {
        return "*";
    }

    @Override
    protected Object getValue(final ByteBuffer b) {
        return null;
    }

    @Override
    public double[] toDouble() {
        return null;
    }

    @Override
    public float[] toFloat() {
        return null;
    }

    @Override
    public int[] toInt() {
        return null;
    }

    @Override
    public long[] toLong() {
        return null;
    }
}

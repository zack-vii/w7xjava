package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Int32;

public final class Int32Array extends NUMBERArray<Integer>{
    public Int32Array(final ByteBuffer b){
        super(b);
    }

    public Int32Array(final int... values){
        super(DTYPE.L, values);
    }

    public Int32Array(final int shape[], final int... values){
        super(DTYPE.L, values, shape);
    }

    @Override
    protected final StringBuilder decompileT(final StringBuilder pout, final Integer value) {
        return pout.append(value);
    }

    @Override
    protected final Integer getElement(final ByteBuffer b) {
        return b.getInt();
    }

    @Override
    public Int32 getScalar(final int idx) {
        return new Int32(this.getValue(idx));
    }

    @Override
    protected final Integer[] initArray(final int size) {
        return new Integer[size];
    }

    @Override
    public final Integer parse(final String in) {
        return Integer.decode(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Integer value) {
        b.putInt(value);
    }
}

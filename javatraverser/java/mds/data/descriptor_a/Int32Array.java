package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Int32Array extends NUMBERArray<Integer>{
    public Int32Array(final ByteBuffer b){
        super(b);
    }

    public Int32Array(final int[] values){
        super(DTYPE.L, values);
    }

    @Override
    protected final Integer getElement(final ByteBuffer b) {
        return b.getInt();
    }

    @Override
    protected final Integer[] initArray(final int size) {
        return new Integer[size];
    }
}

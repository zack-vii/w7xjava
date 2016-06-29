package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Int16Array extends NUMBERArray<Short>{
    public Int16Array(final ByteBuffer b){
        super(b);
    }

    public Int16Array(final short[] values){
        super(DTYPE.W, values);
    }

    @Override
    protected final Short getElement(final ByteBuffer b) {
        return b.getShort();
    }

    @Override
    protected final Short[] initArray(final int size) {
        return new Short[size];
    }
}
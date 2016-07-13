package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Int64Array extends NUMBERArray<Long>{
    public Int64Array(final ByteBuffer b){
        super(b);
    }

    public Int64Array(final long... values){
        super(DTYPE.Q, values);
    }

    @Override
    public final Long getElement(final ByteBuffer b) {
        return b.getLong();
    }

    @Override
    protected final Long[] initArray(final int size) {
        return new Long[size];
    }

    @Override
    public final Long parse(final String in) {
        return Long.decode(in);
    }
}
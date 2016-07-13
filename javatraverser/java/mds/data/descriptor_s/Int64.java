package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Int64 extends NUMBER<Long>{
    public Int64(final ByteBuffer b){
        super(b);
    }

    public Int64(final long value){
        super(DTYPE.Q, value);
    }

    @Override
    public final Long getValue(final ByteBuffer b) {
        return b.getLong(0);
    }

    @Override
    public final Long parse(final String in) {
        return Long.decode(in);
    }
}
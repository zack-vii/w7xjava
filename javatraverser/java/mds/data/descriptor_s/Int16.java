package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Int16 extends NUMBER<Short>{
    public Int16(final ByteBuffer b){
        super(b);
    }

    public Int16(final short value){
        super(DTYPE.W, value);
    }

    @Override
    public final Short getValue(final ByteBuffer b) {
        return b.getShort(0);
    }

    @Override
    public final Short parse(final String in) {
        return Short.decode(in);
    }
}

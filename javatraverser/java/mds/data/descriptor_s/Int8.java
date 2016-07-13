package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Int8 extends NUMBER<Byte>{
    public Int8(final byte value){
        super(DTYPE.B, value);
    }

    public Int8(final ByteBuffer b){
        super(b);
    }

    @Override
    public final Byte getValue(final ByteBuffer b) {
        return b.get();
    }

    @Override
    public final Byte parse(final String in) {
        return Byte.decode(in);
    }
}

package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public class Int32 extends NUMBER<Integer>{
    protected Int32(final byte dtype, final Integer value){
        super(dtype, value);
    }

    public Int32(final ByteBuffer b){
        super(b);
    }

    public Int32(final int value){
        super(DTYPE.L, value);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout) {
        return pout.append(this);
    }

    @Override
    public final Integer getValue(final ByteBuffer b) {
        return b.getInt(0);
    }
}

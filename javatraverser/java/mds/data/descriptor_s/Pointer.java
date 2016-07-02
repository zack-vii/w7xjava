package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Pointer extends NUMBER<Long>{
    public Pointer(final ByteBuffer b){
        super(b);
    }

    public Pointer(final long value){
        super(DTYPE.POINTER, value);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        final String hex = Long.toHexString(this.getValue());
        pout.append(this.getDName()).append('(');
        for(int i = hex.length(); i < 16; i++)
            pout.append('0');
        return pout.append(hex.toUpperCase()).append(')');
    }

    @Override
    public final Long getValue(final ByteBuffer b) {
        return b.getLong(0);
    }
}
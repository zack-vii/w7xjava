package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint64.ULong;

public final class Pointer extends NUMBER<Number>{
    public static final Pointer NULL = new Pointer(0);

    public Pointer(final ByteBuffer b){
        super(b);
    }

    public Pointer(final int value){
        super(DTYPE.POINTER, value);
    }

    public Pointer(final long value){
        super(DTYPE.POINTER, value);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        pout.append(this.getDTypeName());
        if(this.isNull()) return pout.append("(0)");
        return pout.append("(0x").append(this.length == 4 ? Integer.toHexString(this.toInt()) : Long.toHexString(this.toLong())).append(')');
    }

    @Override
    public final Number getValue(final ByteBuffer b) {
        return this.length == 4 ? b.getInt(0) : b.getLong(0);
    }

    public final boolean isNull() {
        return this.toLong() == 0l;
    }

    @Override
    public Number parse(final String in) {
        return ULong.decode(in).longValue();
    }
}
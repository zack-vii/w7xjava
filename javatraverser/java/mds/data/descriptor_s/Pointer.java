package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint64.ULong;

public final class Pointer extends NUMBER<Number>{
    public static final Pointer NULL = Pointer.NULL();

    public static final Pointer NULL() {
        return new Pointer(0l);
    }

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

    public final void setValue(final ByteBuffer val) {
        if(this.length == 8){
            if(val.capacity() == 8) this.b.putLong(Pointer.BYTES, val.getLong());
            else this.b.putLong(Pointer.BYTES, val.getInt());
        }else if(this.length == 4 && val.capacity() == 4) this.b.putInt(Pointer.BYTES, val.getInt());
        else((ByteBuffer)this.b.duplicate().position(Pointer.BYTES)).put(new byte[this.length]);
    }
}
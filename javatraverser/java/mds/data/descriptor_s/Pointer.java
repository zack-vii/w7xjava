package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
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
        if(val.capacity() == 8) this.setValue(val.getLong());
        else this.setValue(val.getInt());
    }

    public final void setValue(final int val) {
        if(this.length == 8) this.b.putLong(Descriptor.BYTES, val);
        else this.b.putInt(Descriptor.BYTES, val);
    }

    public final void setValue(final long val) {
        if(this.length == 8) this.b.putLong(Descriptor.BYTES, val);
        else if((val & 0xFFFFFFFFl) == val) this.b.putInt(Descriptor.BYTES, (int)val);
        else((ByteBuffer)this.b.duplicate().position(Descriptor.BYTES)).put(new byte[this.length]);
    }
}
package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Int8Array extends NUMBERArray<Byte>{
    public Int8Array(final byte... values){
        super(DTYPE.B, values);
    }

    public Int8Array(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final boolean format() {
        return true;
    }

    @Override
    public final Byte getElement(final ByteBuffer b) {
        return b.get();
    }

    @Override
    protected final Byte[] initArray(final int size) {
        return new Byte[size];
    }

    @Override
    public final Byte parse(final String in) {
        return Byte.decode(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Byte value) {
        b.put(value);
    }
}

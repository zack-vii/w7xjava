package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Int16Array extends NUMBERArray<Short>{
    public Int16Array(final ByteBuffer b){
        super(b);
    }

    public Int16Array(final short... values){
        super(DTYPE.W, values);
    }

    @Override
    protected final boolean format() {
        return true;
    }

    @Override
    protected final Short getElement(final ByteBuffer b) {
        return b.getShort();
    }

    @Override
    protected final Short[] initArray(final int size) {
        return new Short[size];
    }

    @Override
    public final Short parse(final String in) {
        return Short.decode(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Short value) {
        b.putShort(value);
    }
}

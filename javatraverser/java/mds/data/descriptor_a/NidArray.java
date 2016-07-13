package mds.data.descriptor_a;

import java.nio.ByteBuffer;

public final class NidArray extends NUMBERArray<Integer>{
    public NidArray(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final Integer getElement(final ByteBuffer b) {
        return b.getInt();
    }

    @Override
    protected final Integer[] initArray(final int size) {
        return new Integer[size];
    }

    @Override
    public final Integer parse(final String in) {
        return Integer.decode(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Integer value) {
        b.putInt(value);
    }
}

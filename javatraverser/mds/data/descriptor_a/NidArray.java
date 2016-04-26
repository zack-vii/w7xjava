package mds.data.descriptor_a;

import java.nio.ByteBuffer;

public final class NidArray extends NUMBERArray<Integer>{
    public NidArray(final ByteBuffer b){
        super(b);
    }

    @Override
    public final String format(final String in) {
        return in;
    }

    @Override
    protected final Integer getElement(final ByteBuffer b) {
        return b.getInt();
    }

    @Override
    protected final Integer[] initArray(final int size) {
        return new Integer[size];
    }
}

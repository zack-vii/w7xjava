package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint32.UInteger;

public final class Uint32Array extends NUMBERArray<UInteger>{
    public Uint32Array(final ByteBuffer b){
        super(b);
    }

    public Uint32Array(final int... values){
        super(DTYPE.LU, values);
    }

    @Override
    protected final boolean format() {
        return true;
    }

    @Override
    protected final UInteger getElement(final ByteBuffer b) {
        return UInteger.fromBuffer(b);
    }

    @Override
    protected final UInteger[] initArray(final int size) {
        return new UInteger[size];
    }
}

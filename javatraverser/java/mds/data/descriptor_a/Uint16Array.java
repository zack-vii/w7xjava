package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint16.UShort;

public final class Uint16Array extends NUMBERArray<UShort>{
    public Uint16Array(final ByteBuffer b){
        super(b);
    }

    public Uint16Array(final short... values){
        super(DTYPE.WU, values);
    }

    @Override
    protected final boolean format() {
        return true;
    }

    @Override
    protected final UShort getElement(final ByteBuffer b) {
        return UShort.fromBuffer(b);
    }

    @Override
    protected final UShort[] initArray(final int size) {
        return new UShort[size];
    }

    @Override
    public final UShort parse(final String in) {
        return UShort.decode(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final UShort value) {
        b.putShort(value.shortValue());
    }
}

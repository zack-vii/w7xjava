package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint32;
import mds.data.descriptor_s.Uint32.UInteger;

public final class Uint32Array extends NUMBERArray<UInteger>{
    public Uint32Array(final ByteBuffer b){
        super(b);
    }

    public Uint32Array(final int... values){
        super(DTYPE.LU, values);
    }

    public Uint32Array(final int shape[], final int... values){
        super(DTYPE.LU, values, shape);
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
    public Uint32 getScalar(final int idx) {
        return new Uint32(this.getValue(idx));
    }

    @Override
    protected final UInteger[] initArray(final int size) {
        return new UInteger[size];
    }

    @Override
    public final UInteger parse(final String in) {
        return UInteger.decode(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final UInteger value) {
        b.putInt(value.intValue());
    }
}

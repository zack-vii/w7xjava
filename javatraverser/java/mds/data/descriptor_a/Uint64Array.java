package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint64.ULong;

public final class Uint64Array extends NUMBERArray<ULong>{
    public Uint64Array(final ByteBuffer b){
        super(b);
    }

    public Uint64Array(final int shape[], final long... values){
        super(DTYPE.QU, values, shape);
    }

    public Uint64Array(final long... values){
        super(DTYPE.QU, values);
    }

    @Override
    protected final ULong getElement(final ByteBuffer b) {
        return ULong.fromBuffer(b);
    }

    @Override
    protected final ULong[] initArray(final int size) {
        return new ULong[size];
    }

    @Override
    public final ULong parse(final String in) {
        return ULong.decode(in);
    }

    @Override
    protected void setElement(final ByteBuffer b, final ULong value) {
        b.putLong(value.longValue());
    }
}

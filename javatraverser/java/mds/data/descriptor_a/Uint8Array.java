package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint8;
import mds.data.descriptor_s.Uint8.UByte;

public final class Uint8Array extends NUMBERArray<UByte>{
    public Uint8Array(final byte... values){
        super(DTYPE.BU, values);
    }

    public Uint8Array(final ByteBuffer b){
        super(b);
    }

    public Uint8Array(final int shape[], final byte... values){
        super(DTYPE.BU, values, shape);
    }

    @Override
    protected final boolean format() {
        return true;
    }

    @Override
    protected final UByte getElement(final ByteBuffer b) {
        return UByte.fromBuffer(b);
    }

    @Override
    public Uint8 getScalar(final int idx) {
        return new Uint8(this.getValue(idx));
    }

    @Override
    protected final UByte[] initArray(final int size) {
        return new UByte[size];
    }

    @Override
    public final UByte parse(final String in) {
        return UByte.decode(in);
    }

    @Override
    protected void setElement(final ByteBuffer b, final UByte value) {
        b.put(value.byteValue());
    }
}

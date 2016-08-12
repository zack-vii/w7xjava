package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Int8;

public final class Int8Array extends NUMBERArray<Byte>{
    public Int8Array(final byte... values){
        super(DTYPE.B, values);
    }

    public Int8Array(final ByteBuffer b){
        super(b);
    }

    public Int8Array(final int shape[], final byte... values){
        super(DTYPE.B, values, shape);
    }

    public final Descriptor deserialize() throws MdsException {
        return Descriptor.deserialize(this.getBuffer());
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
    public Int8 getScalar(final int idx) {
        return new Int8(this.getValue(idx));
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

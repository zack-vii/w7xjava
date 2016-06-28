package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint8.UByte;

public final class Uint8Array extends NUMBERArray<UByte>{
    public Uint8Array(final byte[] values){
        super(DTYPE.BU, values);
    }

    public Uint8Array(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final UByte getElement(final ByteBuffer b) {
        return UByte.fromBuffer(b);
    }

    @Override
    protected final UByte[] initArray(final int size) {
        return new UByte[size];
    }
}

package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Float32Array extends FLOATArray<Float>{
    public Float32Array(final ByteBuffer b){
        super(b);
    }

    public Float32Array(final float... values){
        super(DTYPE.FLOAT, values);
    }

    @Override
    protected final Float getElement(final ByteBuffer b) {
        return b.getFloat();
    }

    @Override
    protected final Float[] initArray(final int size) {
        return new Float[size];
    }
}

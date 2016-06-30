package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Float32 extends FLOAT<Float>{
    Float32(final byte dtype, final float value){
        super(dtype, value);
    }

    public Float32(final ByteBuffer b){
        super(b);
    }

    public Float32(final float value){
        this(DTYPE.FLOAT, value);
    }

    @Override
    public final Float getValue(final ByteBuffer b) {
        return b.getFloat(0);
    }
}

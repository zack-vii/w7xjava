package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Float32 extends FLOAT<Float>{
    public static final Float32 F(final float value) {
        return new Float32(DTYPE.F, value);
    }

    public static final Float32 FS(final float value) {
        return new Float32(DTYPE.FS, value);
    }

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

    @Override
    public final Float parse(final String in) {
        return Float.valueOf(in);
    }
}

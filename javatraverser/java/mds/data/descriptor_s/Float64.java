package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Float64 extends FLOAT<Double>{
    protected Float64(final byte dtype, final double value){
        super(dtype, value);
    }

    public Float64(final ByteBuffer b){
        super(b);
    }

    public Float64(final double value){
        this(DTYPE.FT, value);
    }

    @Override
    public final Double getValue(final ByteBuffer b) {
        return b.getDouble(0);
    }
}

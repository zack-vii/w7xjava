package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Float64 extends FLOAT<Double>{
    public static final Float64 D(final double value) {
        return new Float64(DTYPE.D, value);
    }

    public static final Float64 FT(final double value) {
        return new Float64(DTYPE.FT, value);
    }

    public static final Float64 G(final double value) {
        return new Float64(DTYPE.G, value);
    }

    protected Float64(final byte dtype, final double value){
        super(dtype, value);
    }

    public Float64(final ByteBuffer b){
        super(b);
    }

    public Float64(final double value){
        this(DTYPE.DOUBLE, value);
    }

    @Override
    public final Double getValue(final ByteBuffer b) {
        return b.getDouble(0);
    }

    @Override
    public final Double parse(final String in) {
        return Double.valueOf(in);
    }
}

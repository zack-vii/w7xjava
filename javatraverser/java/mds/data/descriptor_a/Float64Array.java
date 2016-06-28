package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Float64Array extends FLOATArray<Double>{
    public Float64Array(final ByteBuffer b){
        super(b);
    }

    public Float64Array(final double[] values){
        super(DTYPE.FT, values);
    }

    @Override
    protected final Double getElement(final ByteBuffer b) {
        return b.getDouble();
    }

    @Override
    protected final Double[] initArray(final int size) {
        return new Double[size];
    }
}

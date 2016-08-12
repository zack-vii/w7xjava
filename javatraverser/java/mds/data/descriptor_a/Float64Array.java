package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Float64;

public final class Float64Array extends FLOATArray<Double>{
    public Float64Array(final ByteBuffer b){
        super(b);
    }

    public Float64Array(final double... values){
        super(DTYPE.DOUBLE, values);
    }

    public Float64Array(final int shape[], final double... values){
        super(DTYPE.DOUBLE, values, shape);
    }

    @Override
    protected final Double getElement(final ByteBuffer b) {
        return b.getDouble();
    }

    @Override
    public Float64 getScalar(final int idx) {
        return new Float64(this.getValue(idx));
    }

    @Override
    protected final Double[] initArray(final int size) {
        return new Double[size];
    }

    @Override
    public final Double parse(final String in) {
        return Double.valueOf(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Double value) {
        b.putDouble(value);
    }
}

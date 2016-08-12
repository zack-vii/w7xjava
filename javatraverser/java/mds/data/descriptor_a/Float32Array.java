package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Float32;

public final class Float32Array extends FLOATArray<Float>{
    public Float32Array(final ByteBuffer b){
        super(b);
    }

    public Float32Array(final float... values){
        super(DTYPE.FLOAT, values);
    }

    public Float32Array(final int shape[], final float... values){
        super(DTYPE.FLOAT, values, shape);
    }

    @Override
    protected final Float getElement(final ByteBuffer b) {
        return b.getFloat();
    }

    @Override
    public Float32 getScalar(final int idx) {
        return new Float32(this.getValue(idx));
    }

    @Override
    protected final Float[] initArray(final int size) {
        return new Float[size];
    }

    @Override
    public final Float parse(final String in) {
        return Float.valueOf(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Float value) {
        b.putFloat(value);
    }
}

package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.COMPLEX.Complex;
import mds.data.descriptor_s.Complex32;
import mds.data.descriptor_s.Complex32.ComplexFloat;

public final class Complex32Array extends COMPLEXArray<Float>{
    public Complex32Array(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final ComplexFloat getElement(final ByteBuffer b) {
        return new ComplexFloat(b.getFloat(), b.getFloat());
    }

    @Override
    public Complex32 getScalar(final int idx) {
        return new Complex32(this.getValue(idx));
    }

    @Override
    protected final ComplexFloat[] initArray(final int size) {
        return new ComplexFloat[size];
    }

    @Override
    public final ComplexFloat parse(final String in) {
        return ComplexFloat.decode(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Complex<Float> value) {
        b.putFloat(value.real);
        b.putFloat(value.real);
    }
}

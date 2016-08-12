package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.COMPLEX.Complex;
import mds.data.descriptor_s.Complex64;
import mds.data.descriptor_s.Complex64.ComplexDouble;

public final class Complex64Array extends COMPLEXArray<Double>{
    public Complex64Array(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final ComplexDouble getElement(final ByteBuffer b) {
        return new ComplexDouble(b.getDouble(), b.getDouble());
    }

    @Override
    public Complex64 getScalar(final int idx) {
        return new Complex64(this.getValue(idx));
    }

    @Override
    protected final ComplexDouble[] initArray(final int size) {
        return new ComplexDouble[size];
    }

    @Override
    public final ComplexDouble parse(final String in) {
        return ComplexDouble.decode(in);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Complex<Double> value) {
        b.putDouble(value.real);
        b.putDouble(value.real);
    }
}

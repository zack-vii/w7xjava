package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.COMPLEX.Complex;

@SuppressWarnings("serial")
public final class Complex64Array extends COMPLEXArray<Double>{
    private final class ComplexDouble extends Complex<Double>{
        public ComplexDouble(final Double real, final Double imag){
            super(real, imag);
        }
    }

    public Complex64Array(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final Complex<Double> getElement(final ByteBuffer b) {
        return new ComplexDouble(b.getDouble(), b.getDouble());
    }

    @Override
    protected final Complex<Double>[] initArray(final int size) {
        return new ComplexDouble[size];
    }
}

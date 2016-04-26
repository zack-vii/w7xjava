package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.COMPLEX.Complex;

public final class Complex64Array extends COMPLEXArray<Double>{
    private final class ComplexDouble extends Complex<Double>{
        private static final long serialVersionUID = 1L;

        public ComplexDouble(final Double real, final Double imag){
            super(real, imag);
        }
    }

    public Complex64Array(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final Complex<Double> getElement(final ByteBuffer b) {
        return new Complex<Double>(b.getDouble(), b.getDouble());
    }

    @Override
    protected final Complex<Double>[] initArray(final int size) {
        return new ComplexDouble[size];
    }
}

package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.COMPLEX.Complex;

public final class Complex32Array extends COMPLEXArray<Float>{
    private final class ComplexFloat extends Complex<Float>{
        private static final long serialVersionUID = 1L;

        public ComplexFloat(final Float real, final Float imag){
            super(real, imag);
        }
    }

    public Complex32Array(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final Complex<Float> getElement(final ByteBuffer b) {
        return new Complex<Float>(b.getFloat(), b.getFloat());
    }

    @Override
    protected final Complex<Float>[] initArray(final int size) {
        return new ComplexFloat[size];
    }
}

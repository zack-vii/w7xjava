package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Complex64 extends COMPLEX<Double>{
    @SuppressWarnings("serial")
    public static final class ComplexDouble extends Complex<Double>{
        public static final ComplexDouble decode(final String in) {
            final String[] parts = in.split(",", 2);
            return new ComplexDouble(Double.valueOf(parts[0]), Double.valueOf(parts[1]));
        }

        public ComplexDouble(final Double real, final Double imag){
            super(real, imag);
        }
    }

    public Complex64(final ByteBuffer b){
        super(b);
    }

    public Complex64(final double real, final double imag){
        super(DTYPE.COMPLEX_DOUBLE, real, imag);
    }

    public Complex64(final double[] value){
        this(value[0], value[1]);
    }

    @Override
    protected final ComplexDouble getValue(final ByteBuffer b) {
        return new ComplexDouble(b.getDouble(), b.getDouble());
    }

    @Override
    public ComplexDouble parse(final String in) {
        return ComplexDouble.decode(in);
    }
}

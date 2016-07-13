package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Complex32 extends COMPLEX<Float>{
    @SuppressWarnings("serial")
    public static final class ComplexFloat extends Complex<Float>{
        public static final ComplexFloat decode(final String in) {
            final String[] parts = in.split(",", 2);
            return new ComplexFloat(Float.valueOf(parts[0]), Float.valueOf(parts[1]));
        }

        public ComplexFloat(final Float real, final Float imag){
            super(real, imag);
        }
    }

    public Complex32(final ByteBuffer b){
        super(b);
    }

    public Complex32(final float real, final float imag){
        super(DTYPE.COMPLEX_FLOAT, real, imag);
    }

    public Complex32(final float[] value){
        this(value[0], value[1]);
    }

    @Override
    protected final ComplexFloat getValue(final ByteBuffer b) {
        return new ComplexFloat(b.getFloat(), b.getFloat());
    }

    @Override
    public ComplexFloat parse(final String in) {
        return ComplexFloat.decode(in);
    }
}

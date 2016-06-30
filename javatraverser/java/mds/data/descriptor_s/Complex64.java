package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Complex64 extends COMPLEX<Double>{
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
    protected final COMPLEX.Complex<Double> getValue(final ByteBuffer b) {
        return new COMPLEX.Complex<Double>(b.getDouble(), b.getDouble());
    }
}

package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;

public final class Complex32 extends COMPLEX<Float>{
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
    protected final COMPLEX.Complex<Float> getValue(final ByteBuffer b) {
        return new COMPLEX.Complex<Float>(b.getFloat(), b.getFloat());
    }
}

package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.COMPLEX;
import mds.data.descriptor_s.COMPLEX.Complex;

public abstract class COMPLEXArray<T extends Number>extends NUMBERArray<Complex<T>>{
    public COMPLEXArray(final ByteBuffer b){
        super(b);
    }

    @Override
    public final String decompileT(final Complex<T> val) {
        return COMPLEX.decompile(val, this.dtype, false);
    }

    @Override
    public final String format(final String in) {
        return in;
    }

    public final T getImag(final int idx) {
        return this.getValue(idx).imag;
    }

    public final T getReal(final int idx) {
        return this.getValue(idx).real;
    }
}

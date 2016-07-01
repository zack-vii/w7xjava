package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.COMPLEX;
import mds.data.descriptor_s.COMPLEX.Complex;

public abstract class COMPLEXArray<T extends Number>extends NUMBERArray<Complex<T>>{
    public COMPLEXArray(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final StringBuilder decompileT(final StringBuilder pout, final Complex<T> t) {
        return COMPLEX.decompile(pout, t, this.dtype, Descriptor.DECO_NRM);
    }

    public final T getImag(final int idx) {
        return this.getValue(idx).imag;
    }

    public final T getReal(final int idx) {
        return this.getValue(idx).real;
    }
}

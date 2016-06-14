package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.COMPLEX.Complex;

@SuppressWarnings("serial")
public abstract class COMPLEX<T extends Number>extends NUMBER<Complex<T>>{
    public static class Complex<T extends Number>extends Number{
        public final T imag;
        public final T real;

        public Complex(final T real, final T imag){
            this.real = real;
            this.imag = imag;
        }

        @Override
        public double doubleValue() {
            return this.real.doubleValue();
        }

        @Override
        public float floatValue() {
            return this.real.floatValue();
        }

        @Override
        public int intValue() {
            return this.real.intValue();
        }

        @Override
        public long longValue() {
            return this.real.longValue();
        }
    }

    public static final <T extends Number> StringBuilder decompile(final StringBuilder pout, final Complex<T> value, final byte dtype, final boolean preview) {
        final byte realdtype = (byte)(dtype - 2);
        pout.append("Cmplx(");
        pout.append(FLOAT.decompile(value.real, realdtype, preview)).append(',');
        pout.append(FLOAT.decompile(value.imag, realdtype, preview)).append(')');
        return pout;
    }

    protected COMPLEX(final byte dtype, final Complex<T> value){
        super(dtype, value);
    }

    protected COMPLEX(final byte dtype, final T real, final T imag){
        super(dtype, new Complex<T>(real, imag));
    }

    protected COMPLEX(final ByteBuffer b){
        super(b);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout) {
        return COMPLEX.decompile(pout, this.getValue(), this.dtype, false);
    }

    public final T getImag() {
        return this.getValue().imag;
    }

    public final T getReal() {
        return this.getValue().real;
    }

    @Override
    public final String toString() {
        return COMPLEX.decompile(new StringBuilder(32), this.getValue(), this.dtype, true).toString();
    }
}

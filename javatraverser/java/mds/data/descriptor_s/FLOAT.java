package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import java.util.Locale;
import mds.data.descriptor.DTYPE;

public abstract class FLOAT<T extends Number>extends NUMBER<T>{
    public static final String decompile(final Number value, final byte dtype, final int mode) {
        final boolean isF = dtype == DTYPE.F || dtype == DTYPE.FS;
        if(value.doubleValue() == 0) return isF ? "0." : new StringBuilder(3).append('0').append(DTYPE.getSuffix(dtype)).append('0').toString();
        final double val, absval = Math.abs(value.doubleValue());
        final int E;
        if(absval >= 1e6 || absval < .1){
            E = (int)Math.log10(absval);
            val = value.doubleValue() / Math.pow(10, E);
        }else{
            val = value.doubleValue();
            E = 0;
        }
        final String tmp = String.format(Locale.US, "%f", val);
        if(isF && E == 0) return tmp.replaceAll("(^0*|0*$)", "");
        return new StringBuilder(16).append(tmp.replaceAll("(^0*|[0\\.]*$)", "")).append(DTYPE.getSuffix(dtype)).append(E).toString();
    }

    protected FLOAT(final byte dtype, final double value){
        super(dtype, value);
    }

    protected FLOAT(final byte dtype, final float value){
        super(dtype, value);
    }

    protected FLOAT(final ByteBuffer b){
        super(b);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return pout.append(FLOAT.decompile(this.getValue(), this.dtype, mode));
    }
}

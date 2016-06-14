package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import java.util.Locale;
import mds.data.descriptor.DTYPE;

public abstract class FLOAT<T extends Number>extends NUMBER<T>{
    public static final Float64 D(final double value) {
        return new Float64(DTYPE.D, value);
    }

    public static final String decompile(final Number value, final byte dtype, final boolean preview) {
        String tmp = String.format(Locale.US, "%s", value);
        if(preview && tmp.length() > 5) tmp = String.format(Locale.US, "%1.3G", value);
        tmp.replace("+", "");
        if(tmp.contains("E")) return tmp.replace("E", DTYPE.getSuffix(dtype));
        return preview ? tmp : tmp.concat(DTYPE.getSuffix(dtype)).concat("0");
    }

    public static final Float32 F(final float value) {
        return new Float32(DTYPE.F, value);
    }

    public static final Float32 FS(final float value) {
        return new Float32(DTYPE.FS, value);
    }

    public static final Float64 FT(final double value) {
        return new Float64(DTYPE.FT, value);
    }

    public static final Float64 G(final double value) {
        return new Float64(DTYPE.G, value);
    }

    protected FLOAT(final byte dtype, final double value){
        super(dtype, value);
    }

    protected FLOAT(final ByteBuffer b){
        super(b);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout) {
        return pout.append(FLOAT.decompile(this.getValue(), this.dtype, false));
    }

    @Override
    public final String toString() {
        return FLOAT.decompile(this.getValue(), this.dtype, true);
    }
}

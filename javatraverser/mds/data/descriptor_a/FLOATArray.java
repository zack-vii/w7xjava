package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.FLOAT;

public abstract class FLOATArray<T extends Number>extends NUMBERArray<T>{
    public FLOATArray(final byte dtype, final double[] values){
        super(dtype, values);
    }

    public FLOATArray(final byte dtype, final float[] values){
        super(dtype, values);
    }

    public FLOATArray(final ByteBuffer b){
        super(b);
    }

    @Override
    public final String decompileT(final T value) {
        return FLOAT.decompile(value, this.dtype, false);
    }

    @Override
    public final String format(final String in) {
        return in;
    }

    @Override
    public final String toString(final int idx) {
        return this.TtoString(this.getValue(idx));
    }

    @Override
    public final String TtoString(final T value) {
        return FLOAT.decompile(value, this.dtype, true);
    }
}

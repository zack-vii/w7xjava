package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.FLOAT;

public abstract class FLOATArray<T extends Number>extends NUMBERArray<T>{
    protected FLOATArray(final byte dtype, final double[] values, final int... shape){
        super(dtype, values, shape);
    }

    protected FLOATArray(final byte dtype, final float[] values, final int... shape){
        super(dtype, values, shape);
    }

    protected FLOATArray(final ByteBuffer b){
        super(b);
    }

    @Override
    public final StringBuilder decompileT(final StringBuilder pout, final T value) {
        return pout.append(FLOAT.decompile(value, this.dtype, Descriptor.DECO_NRM));
    }

    @Override
    public final String toString(final int idx) {
        return this.TtoString(this.getValue(idx));
    }

    @Override
    public final String TtoString(final T value) {
        return FLOAT.decompile(value, this.dtype, Descriptor.DECO_STR);
    }
}

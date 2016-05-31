package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Signal extends Descriptor_R{
    public Signal(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Signal(final Descriptor data, final Descriptor raw, final byte ndims){
        super(DTYPE.SIGNAL, (byte)(2 + ndims), null);
        this.dscptrs[0] = data == null ? Function.$VALUE() : data;
        this.dscptrs[1] = raw;
    }

    public Signal(final Descriptor data, final Descriptor raw, final Descriptor dim){
        this(data, raw, (byte)1);
        this.dscptrs[2] = dim;
    }

    public Signal(final Descriptor data, final Descriptor raw, final Descriptor dim1, final Descriptor dim2){
        this(data, raw, (byte)2);
        this.dscptrs[2] = dim1;
        this.dscptrs[3] = dim2;
    }

    public Signal(final Descriptor data, final Descriptor raw, final Descriptor[] dims){
        this(data, raw, (byte)(dims == null ? 0 : dims.length));
        if(dims == null) return;
        System.arraycopy(dims, 0, this.dscptrs, 2, dims.length);
    }

    public final Descriptor getArgument(final int idx) {
        return this.dscptrs[3 + idx];
    }

    public final Descriptor getData() {
        return this.dscptrs[0];
    }

    public final Descriptor getDimension() {
        return this.getDimension(0);
    }

    public final Descriptor getDimension(final int idx) {
        return this.dscptrs[2 + idx];
    }

    public final Descriptor getRaw() {
        return this.dscptrs[1];
    }

    @Override
    public final double[] toDouble() {
        return this.getData().toDouble();
    }

    @Override
    public final float[] toFloat() {
        return this.getData().toFloat();
    }

    @Override
    public final int[] toInt() {
        return this.getData().toInt();
    }

    @Override
    public final long[] toLong() {
        return this.getData().toLong();
    }
}

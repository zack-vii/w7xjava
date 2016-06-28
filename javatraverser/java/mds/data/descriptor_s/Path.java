package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor_S;

public final class Path extends Descriptor_S<String>{
    public Path(final ByteBuffer b){
        super(b);
    }

    public Path(final String path){
        super(DTYPE.PATH, path.getBytes());
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return pout.append(this.getValue());
    }

    @Override
    protected String getValue(final ByteBuffer b) {
        final byte[] buf = new byte[this.length];
        b.get(buf);
        return new String(buf);
    }

    @Override
    public double[] toDouble() {
        return this.evaluate().toDouble();
    }

    @Override
    public float[] toFloat() {
        return this.evaluate().toFloat();
    }

    @Override
    public int[] toInt() {
        return this.evaluate().toInt();
    }

    @Override
    public long[] toLong() {
        return this.evaluate().toLong();
    }
}

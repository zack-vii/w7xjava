package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_S;

public final class Path extends Descriptor_S<String>{
    public Path(final ByteBuffer b){
        super(b);
    }

    public Path(final String path){
        super(DTYPE.PATH, ByteBuffer.wrap(path.getBytes()).order(Descriptor.BYTEORDER));
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
    public double[] toDoubles() {
        return this.evaluate().toDoubles();
    }

    @Override
    public float[] toFloats() {
        return this.evaluate().toFloats();
    }

    @Override
    public int[] toInts() {
        return this.evaluate().toInts();
    }

    @Override
    public long[] toLongs() {
        return this.evaluate().toLongs();
    }
}

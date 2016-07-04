package mds.data.descriptor;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.mdsip.Message;

/** XS (-63 : 193) **/
public class Descriptor_XS extends Descriptor<Descriptor>{
    public static final int  _l_len = 8;
    public static final int  BYTES  = 12;
    public static final byte CLASS  = -63;

    public static final Descriptor_XS deserialize(final ByteBuffer bb) throws MdsException {
        return new Descriptor_XS(bb);
    }

    protected static final ByteBuffer getPointer(final ByteBuffer bi) {
        ByteBuffer bo = bi.duplicate().order(bi.order());;
        bo.position(bi.getInt(Descriptor._ptrI));
        bo = bo.slice().order(bi.order());;
        bo.limit(bi.getShort(Descriptor_XS._l_len));
        return bo;
    }
    public final int        l_length;
    public final Descriptor payload;

    public Descriptor_XS(final ByteBuffer b) throws MdsException{
        super(b);
        this.l_length = b.getInt();
        this.payload = Descriptor.deserialize(b);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return this.payload.decompile(prec, pout, mode);
    }

    @Override
    public int[] getShape() {
        return new int[0];
    }

    @Override
    public Descriptor getValue(final ByteBuffer b) {
        return this.payload;
    }

    @Override
    public byte[] toByteArray() {
        return this.payload.toByteArray();
    }

    @Override
    public double[] toDoubleArray() {
        return this.payload.toDoubleArray();
    }

    @Override
    public float[] toFloatArray() {
        return this.payload.toFloatArray();
    }

    @Override
    public int[] toIntArray() {
        return this.payload.toIntArray();
    }

    @Override
    public long[] toLongArray() {
        return this.payload.toLongArray();
    }

    @Override
    public Message toMessage(final byte descr_idx, final byte n_args) {
        return this.payload.toMessage(descr_idx, n_args);
    }
}

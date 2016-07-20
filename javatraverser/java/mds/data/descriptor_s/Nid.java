package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;

public final class Nid extends TREENODE<Integer>{
    public static final Nid[] getArrayOfNids(final int[] nid_nums) {
        final Nid[] nids = new Nid[nid_nums.length];
        for(int i = 0; i < nids.length; i++)
            nids[i] = new Nid(nid_nums[i]);
        return nids;
    }

    public Nid(final ByteBuffer b){
        super(b);
    }

    public Nid(final int integer){
        super(DTYPE.NID, NUMBER.toByteBuffer(integer));
    }

    public Nid(final Nid nid, final int relative){
        this(nid.getValue() + relative);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        try{
            return pout.append(this.getNciMinPath());
        }catch(final MdsException e){
            return pout.append("<nid ").append(this.getValue()).append('>');
        }catch(final Exception e){
            e.printStackTrace();
            return pout.append(e.toString());
        }
    }

    @Override
    public final int getNidNumber() throws MdsException {
        return this.getValue();
    }

    @Override
    public final Integer getValue(final ByteBuffer b) {
        return b.getInt(0);
    }

    @Override
    public double[] toDoubleArray() {
        return this.getRecord().toDoubleArray();
    }

    @Override
    public float[] toFloatArray() {
        return this.getRecord().toFloatArray();
    }

    @Override
    public int[] toIntArray() {
        return this.getRecord().toIntArray();
    }

    @Override
    public long[] toLongArray() {
        return this.getRecord().toLongArray();
    }
}
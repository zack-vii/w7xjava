package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.Database;
import mds.MdsException;
import mds.data.descriptor.DTYPE;

public final class Nid extends Int32{
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
        super(DTYPE.NID, integer);
    }

    public Nid(final Nid nid, final int relative){
        this(nid.getValue() + relative);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        try{
            return pout.append(Database.getMinPath(this.getValue()));
        }catch(final MdsException e){
            return pout.append("<nid ").append(this.getValue()).append('>');
        }catch(final Exception e){
            e.printStackTrace();
            return pout.append(e.toString());
        }
    }

    public final String getFullPath() {
        try{
            return Database.getFullPath(this.getValue());
        }catch(final MdsException e){
            return null;
        }
    }

    @Override
    public double[] toDoubleArray() {
        return this.evaluate().toDoubleArray();
    }

    @Override
    public float[] toFloatArray() {
        return this.evaluate().toFloatArray();
    }

    @Override
    public int[] toIntArray() {
        return this.evaluate().toIntArray();
    }

    @Override
    public long[] toLongArray() {
        return this.evaluate().toLongArray();
    }
}
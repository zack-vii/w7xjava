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
            return pout.append('(').append(this.getValue()).append(')');
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
package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.mdsip.Connection;

public final class Nid extends TREENODE<Integer>{
    public static final Nid[] getArrayOfNids(final int[] nid_nums) {
        return Nid.getArrayOfNids(nid_nums, Connection.getActiveConnection());
    }

    public static final Nid[] getArrayOfNids(final int[] nid_nums, final Connection connection) {
        final Nid[] nids = new Nid[nid_nums.length];
        for(int i = 0; i < nids.length; i++)
            nids[i] = new Nid(nid_nums[i], connection);
        return nids;
    }

    public Nid(final ByteBuffer b){
        super(b);
    }

    public Nid(final int integer){
        super(DTYPE.NID, NUMBER.toByteBuffer(integer));
    }

    public Nid(final int integer, final Connection connection){
        super(DTYPE.NID, NUMBER.toByteBuffer(integer), connection);
    }

    public Nid(final Nid nid, final int relative){
        this(nid.getValue() + relative, nid.connection);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        try{
            return pout.append(this.getNciFullPath());
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

    public final Path toFullPath() throws MdsException {
        return new Path(this.getNciFullPath(), this.connection);
    }

    public final Path toMinPath() throws MdsException {
        return new Path(this.getNciMinPath(), this.connection);
    }

    public final Path toPath() throws MdsException {
        return new Path(this.getNciPath(), this.connection);
    }
}
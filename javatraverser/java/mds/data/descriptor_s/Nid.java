package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.TREE;
import mds.data.descriptor.DTYPE;

public final class Nid extends NODE<Integer>{
    public static final Nid[] getArrayOfNids(final int[] nid_nums) {
        return Nid.getArrayOfNids(nid_nums, TREE.getActiveTree());
    }

    public static final Nid[] getArrayOfNids(final int[] nid_nums, final TREE tree) {
        final Nid[] nids = new Nid[nid_nums.length];
        for(int i = 0; i < nids.length; i++)
            nids[i] = new Nid(nid_nums[i], tree);
        return nids;
    }

    public Nid(final ByteBuffer b){
        super(b);
    }

    public Nid(final int integer){
        super(DTYPE.NID, NUMBER.toByteBuffer(integer));
    }

    public Nid(final int integer, final TREE tree){
        super(DTYPE.NID, NUMBER.toByteBuffer(integer), tree);
    }

    public Nid(final Nid nid, final int relative){
        this(nid.getValue() + relative, nid.tree);
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

    @Override
    public final Path toFullPath() throws MdsException {
        return new Path(this.getNciFullPath(), this.tree);
    }

    @Override
    public final Path toMinPath() throws MdsException {
        return new Path(this.getNciMinPath(), this.tree);
    }

    @Override
    public final Nid toNid() {
        return this;
    }

    @Override
    public final Path toPath() throws MdsException {
        return new Path(this.getNciPath(), this.tree);
    }
}
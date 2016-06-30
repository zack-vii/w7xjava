package mds;

import mds.data.descriptor.Descriptor;
import mds.mdsip.Connection;

public final class TreeShr{
    private final Connection connection;

    public TreeShr(final Connection connection){
        this.connection = connection;
    }

    public final int[] treeAddConglom(final String path, final String model) throws MdsException {
        return this.connection.getIntegerArray(String.format("_a=-1;_s=TreeShr->_TreeAddConglom(ref('%s'),ref('%s'),ref(_a));[_s,_a]", path, model));
    }

    public final int[] treeAddNode(final String name, final int usage) throws MdsException {
        return this.connection.getIntegerArray(String.format("_a=-1;_s=TreeShr->TreeAddNode(ref('\\%s'),ref(_a),val(%d));[_s,_a]", name, usage));
    }

    public final int treeAddTag(final int nidnum, final String tag) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeAddTag(val(%d),ref('%s'))", nidnum, tag));
    }

    public final int treeClose(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeClose(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeCreateTreeFiles(final String expt, final int newshot, final int fromshot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeCreateTreeFiles(ref('%s'),val(%d),val(%d))", expt, newshot, fromshot));
    }

    public final int treeCtx() throws MdsException {
        return this.connection.getInteger("TreeShr->TreeCtx()");
    }

    public final int treeDeleteNodeExecute() throws MdsException {
        return this.connection.getInteger("TreeShr->TreeDeleteNodeExecute()");
    }

    public final int[] treeDeleteNodeGetNid(final String path, final String model) throws MdsException {
        return this.connection.getIntegerArray("_a=0,_s=TreeShr->TreeDeleteNodeGetNid(ref(_a)));[_s,_a]");
    }

    public final long[] treeDeleteNodeInitialize(final int nidnum, final long ref, final boolean init) throws MdsException {
        return this.connection.getLongArray(String.format("_a=%dQU;_s=TreeShr->TreeDeleteNodeInitialize(val(%d),ref(_a),val(%d));[_s,_a]", ref, nidnum, init ? 1 : 0));
    }

    public final int treeGetCurrentShotId(final String expt) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeGetCurrentShotId('%s')", expt));
    }

    public final int[] treeGetDefaultNid() throws MdsException {
        return this.connection.getIntegerArray("_a=-1;_s=TreeShr->TreeGetDefaultNid(ref(_a));[_s,_a]");
    }

    public final Descriptor treeGetRecord(final int nidnum) throws MdsException {
        return this.connection.mdsValue(String.format("_a=*;TreeShr->TreeGetRecord(val(%d),xd(_a));_a", nidnum), Descriptor.class);
    }

    public final int treeOpen(final String expt, final long shot, final boolean readonly) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpen(ref('%s'),val(%d),val(%d))", expt, shot, readonly ? 1 : 0));
    }

    public final int treeOpenEdit(final String expt, final long shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpenEdit(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeOpenNew(final String expt, final long shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpenNew(ref('%s'),val(%d))", expt, shot));
    }

    public final int treePutRecord(final int nidnum, final Descriptor dsc) throws MdsException {
        final String xd = (dsc == null) ? "*" : "xd(as_is(" + dsc.decompile() + "))";
        return this.connection.getInteger(String.format("TreeShr->TreePutRecord(val(%d),%s,val(0))", nidnum, xd));
    }

    public final int treePutRow(final int nidnum, final int arg1, final Descriptor data, final long time) throws MdsException {
        if(data == null) return this.treePutRecord(nidnum, null);;
        return this.connection.getInteger(String.format("TreeShr->TreePutRow(val(%d),val(%d),ref(%dQU),xd(%s))", nidnum, arg1, time, data.decompile()));
    }

    public final int treeQuitTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeQuitTree(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeRemoveNodesTags(final int nidnum) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeRemoveNodesTags(val(%d))", nidnum));
    }

    public final int treeRenameNode(final int nidnum, final String name) throws MdsException {
        return this.connection.getInteger(new StringBuilder(256).append("TreeShr->TreeRenameNode(val(").append(nidnum).append("),ref('").append(name).append("'))").toString());
    }

    public final int treeSetCurrentShotId(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetCurrentShotId(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeSetDefault(final int nidnum) throws MdsException {
        return this.connection.getInteger(String.format("TreeSetDefault(GETNCI(%d,'FULLPATH'))", nidnum));
    }

    public final int treeSetNciItm(final int nidnum, final boolean state, final int flags) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetNciItm(val(%d),val(%d),val(%d))", nidnum, state ? 1 : 2, flags & 0x7FFFFFFC));
    }

    public final int treeSetSubtree(final int nidnum) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetSubtree(val(%d))", nidnum));
    }

    public final int treeTurnOff(final int nidnum) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeTurnOff(val(%d))", nidnum));
        /* 265392050 : TreeLock-Failure but does the change of state */
    }

    public final int treeTurnOn(final int nidnum) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeTurnOn(val(%d))", nidnum));
        /* 265392050 : TreeLock-Failure but does the change of state */
    }

    public final int treeWriteTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeWriteTree(ref('%s'),val(%d))", expt, shot));
    }
}

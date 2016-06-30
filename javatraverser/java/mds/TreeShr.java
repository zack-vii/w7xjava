package mds;

import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_a.Uint64Array;
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

    public final int treeAddTag(final int nid, final String tag) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeAddTag(val(%d),ref('%s'))", nid, tag));
    }

    public final int treeBeginTimestampedSegment(final int nid, final Descriptor_A initialValue, final int idx) throws MdsException {
        return this.connection.mdsValue(String.format("TreeShr->TreeBeginTimestampedSegment(val(%d),xd($),val(%d))", nid, idx), new Descriptor[]{initialValue}).toInt();
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

    public final long[] treeDeleteNodeInitialize(final int nid, final long ref, final boolean init) throws MdsException {
        return this.connection.getLongArray(String.format("_a=%dQU;_s=TreeShr->TreeDeleteNodeInitialize(val(%d),ref(_a),val(%d));[_s,_a]", ref, nid, init ? 1 : 0));
    }

    public final int treeGetCurrentShotId(final String expt) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeGetCurrentShotId('%s')", expt));
    }

    public final int[] treeGetDefaultNid() throws MdsException {
        return this.connection.getIntegerArray("_a=-1;_s=TreeShr->TreeGetDefaultNid(ref(_a));[_s,_a]");
    }

    public final Descriptor treeGetRecord(final int nid) throws MdsException {
        return this.connection.mdsValue(String.format("COMMA(_a=*,TreeShr->TreeGetRecord(val(%d),xd(_a)),_a)", nid), Descriptor.class);
    }

    public final Descriptor treeGetSegmentedRecord(final int nid, final Descriptor dsc) throws MdsException {
        return this.connection.mdsValue(String.format("_a=*;_s=TreeShr->TreePutRecord(val(%d),xd(_a));_a", nid));
    }

    public final int treeMakeTimestampedSegment(final int nid, final Uint64Array timestamps, final Descriptor_A initialValue, final int idx, final int rows_filled) throws MdsException {
        return this.connection.mdsValue(String.format("TreeShr->TreeMakeTimestampedSegment(val(%d),ref($),xd($),val(%d),val(%d))", nid, idx, rows_filled), new Descriptor[]{timestamps, initialValue}).toInt();
    }

    public final int treeOpen(final String expt, final int shot, final boolean readonly) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpen(ref('%s'),val(%d),val(%d))", expt, shot, readonly ? 1 : 0));
    }

    public final int treeOpenEdit(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpenEdit(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeOpenNew(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpenNew(ref('%s'),val(%d))", expt, shot));
    }

    /** utility_update=2: compress **/
    public final int treePutRecord(final int nid, final Descriptor_A dsc, final int utility_update) throws MdsException {
        final String xd = (dsc == null) ? "*" : "xd(as_is(" + dsc.decompile() + "))";
        return this.connection.getInteger(String.format("TreeShr->TreePutRecord(val(%d),%s,val(%d))", nid, xd, utility_update));
    }

    /** adds row to segmented node **/
    public final int treePutRow(final int nid, final int bufsize, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.connection.mdsValue(String.format("TreeShr->TreePutRow(val(%d),val(%d),ref(%dQU),xd($))", nid, bufsize, timestamp), new Descriptor[]{data}).toInt();
    }

    /** adds row to segmented node **/
    public final int treePutTimestampedSegment(final int nid, final long timestamp, final Descriptor data) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreePutTimestampedSegment(val(%d),ref(%dQU),xd(%s))", nid, timestamp, data.decompile()));
    }

    public final int treeQuitTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeQuitTree(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeRemoveNodesTags(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeRemoveNodesTags(val(%d))", nid));
    }

    public final int treeRenameNode(final int nid, final String name) throws MdsException {
        return this.connection.getInteger(new StringBuilder(256).append("TreeShr->TreeRenameNode(val(").append(nid).append("),ref('").append(name).append("'))").toString());
    }

    public final int treeSetCurrentShotId(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetCurrentShotId(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeSetDefault(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeSetDefault(GETNCI(%d,'FULLPATH'))", nid));
    }

    public final int treeSetNciItm(final int nid, final boolean state, final int flags) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetNciItm(val(%d),val(%d),val(%d))", nid, state ? 1 : 2, flags & 0x7FFFFFFC));
    }

    public final int treeSetSubtree(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetSubtree(val(%d))", nid));
    }

    /** 265392050 : TreeLock-Failure but does the change of state **/
    public final int treeTurnOff(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeTurnOff(val(%d))", nid));
    }

    /** 265392050 : TreeLock-Failure but does the change of state **/
    public final int treeTurnOn(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeTurnOn(val(%d))", nid));
    }

    public final int treeWriteTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeWriteTree(ref('%s'),val(%d))", expt, shot));
    }
}

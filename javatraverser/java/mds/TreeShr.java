package mds;

import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_a.Uint64Array;
import mds.mdsip.Connection;

public final class TreeShr{
    private final Connection connection;
    private long             treeFindTagWildRef = 0;
    private int              treeFindTagWildNid = -1;

    public TreeShr(final Connection connection){
        this.connection = connection;
    }

    public final int[] treeAddConglom(final String path, final String model) throws MdsException {
        return this.connection.getIntegerArray(String.format("_i=-1;_s=TreeShr->TreeAddConglom(ref('%s'),ref('%s'),ref(_i));[_s,_i]", path.replace("\\", "\\\\"), model));
    }

    public final int[] treeAddNode(final String path, final byte usage) throws MdsException {
        return this.connection.getIntegerArray(String.format("_i=-1;_s=TreeShr->TreeAddNode(ref('%s'),ref(_i),val(%d));[_s,_i]", path.replace("\\", "\\\\"), usage));
    }

    public final int treeAddTag(final int nid, final String tag) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeAddTag(val(%d),ref('%s'))", nid, tag));
    }

    public final int treeBeginTimestampedSegment(final int nid, final Descriptor_A initialValue, final int idx) throws MdsException {
        return this.connection.mdsValue(String.format("_s=TreeShr->TreeBeginTimestampedSegment(val(%d),xd($),val(%d))", nid, idx), new Descriptor[]{initialValue}).toInt();
    }

    public final int treeCleanDatafile(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeCleanDatafile(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeClose(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeClose(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeCompressDatafile(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeCompressDatafile(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeCreateTreeFiles(final String expt, final int newshot, final int fromshot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeCreateTreeFiles(ref('%s'),val(%d),val(%d))", expt, newshot, fromshot));
    }

    public final int treeCtx() throws MdsException {
        return this.connection.getInteger("TreeShr->TreeCtx()");
    }

    public final int treeDeleteNodeExecute() throws MdsException {
        return this.connection.getInteger("TreeShr->TreeDeleteNodeExecute()");
    }

    public final int[] treeDeleteNodeGetNid(final int idx) throws MdsException {
        return this.connection.getIntegerArray(String.format("_i=%d;_s=TreeShr->TreeDeleteNodeGetNid(ref(_i));[_s,_i]", idx));
    }

    public final int[] treeDeleteNodeInitialize(final int nid) throws MdsException {
        return this.treeDeleteNodeInitialize(nid, 0, true);
    }

    public final int[] treeDeleteNodeInitialize(final int nid, final int count, final boolean init) throws MdsException {
        return this.connection.getIntegerArray(String.format("_i=%d;_s=TreeShr->TreeDeleteNodeInitialize(val(%d),ref(_i),val(1));[_s,_i]", count, nid, init ? 1 : 0));
    }

    public final int treeEndConglomerate() throws MdsException {
        return this.connection.getInteger("TreeShr->TreeEndConglomerate()");
    }

    public final String treeFindTagWild(final String searchstr) throws MdsException {
        if(this.connection.getInteger(String.format("_t='';_q=%dQ;_i=-1;_s=TreeShr->TreeFindTagWildDsc(ref('%s'),ref(_i),ref(_q),xd(_t))", this.treeFindTagWildRef, searchstr)) == 0){
            this.treeFindTagWildReset();
            return null;
        }
        this.treeFindTagWildNid = this.connection.getInteger("_i");
        this.treeFindTagWildRef = this.connection.getLong("_q");
        return this.connection.getString("_t");
    }

    public final int treeFindTagWildNid() {
        return this.treeFindTagWildNid;
    }

    public final void treeFindTagWildReset() {
        this.treeFindTagWildRef = 0;
        this.treeFindTagWildNid = -1;
    }

    public final int treeGetCurrentShotId(final String expt) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeGetCurrentShotId('%s')", expt));
    }

    public final long treeGetDatafileSize() throws MdsException {
        return this.connection.getLong("TreeShr->TreeGetDatafileSize()");
    }

    public final int[] treeGetDefaultNid() throws MdsException {
        return this.connection.getIntegerArray("_i=-1;_s=TreeShr->TreeGetDefaultNid(ref(_i));[_s,_i]");
    }

    public final Descriptor treeGetRecord(final int nid) throws MdsException {
        return this.connection.evaluate(String.format("_d=*;_s=TreeShr->TreeGetRecord(val(%d),xd(_d));_d", nid));
    }

    public final Descriptor treeGetSegmentedRecord(final int nid) throws MdsException {
        return this.connection.mdsValue(String.format("_d=*;_s=TreeShr->TreePutRecord(val(%d),xd(_d));_d", nid));
    }

    public final int treeMakeTimestampedSegment(final int nid, final Uint64Array timestamps, final Descriptor_A initialValue, final int idx, final int rows_filled) throws MdsException {
        return this.connection.mdsValue(String.format("_s=TreeShr->TreeMakeTimestampedSegment(val(%d),ref($),xd($),val(%d),val(%d))", nid, idx, rows_filled), new Descriptor[]{timestamps, initialValue}).toInt();
    }

    public final int treeOpen(final String expt, final int shot, final boolean readonly) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeOpen(ref('%s'),val(%d),val(%d))", expt, shot, readonly ? 1 : 0));
    }

    public final int treeOpenEdit(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeOpenEdit(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeOpenNew(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeOpenNew(ref('%s'),val(%d))", expt, shot));
    }

    /** utility_update=2: compress **/
    public final int treePutRecord(final int nid, final Descriptor dsc, final int utility_update) throws MdsException {
        if(dsc == null) return this.connection.getInteger(String.format("_s=TreeShr->TreePutRecord(val(%d),*,val(%d))", nid, utility_update));
        return this.connection.getInteger(String.format("_s=TreeShr->TreePutRecord(val(%d),xd(as_is(%s)),val(%d))", nid, dsc.decompile(), utility_update));
    }

    /** adds row to segmented node **/
    public final int treePutRow(final int nid, final int bufsize, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.connection.mdsValue(String.format("_s=TreeShr->TreePutRow(val(%d),val(%d),ref(%dQU),xd($))", nid, bufsize, timestamp), new Descriptor[]{data}).toInt();
    }

    /** adds row to segmented node **/
    public final int treePutTimestampedSegment(final int nid, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreePutTimestampedSegment(val(%d),ref(%dQU),xd(%s))", nid, timestamp, data.decompile()));
    }

    public final int treeQuitTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeQuitTree(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeRemoveNodesTags(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeRemoveNodesTags(val(%d))", nid));
    }

    public final int treeRenameNode(final int nid, final String name) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeRenameNode(val(%d),ref('%s'))", nid, name));
    }

    public final int treeSetCurrentShotId(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeSetCurrentShotId(ref('%s'),val(%d))", expt, shot));
    }

    public final int treeSetDefault(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeSetDefault(GETNCI(%d,'FULLPATH'))", nid));
    }

    public final int treeSetNciItm(final int nid, final boolean state, final int flags) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeSetNciItm(val(%d),val(%d),val(%d))", nid, state ? 1 : 2, flags));
    }

    public final int treeSetNoSubtree(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeSetNoSubtree(val(%d))", nid));
    }

    public final int treeSetSubtree(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeSetSubtree(val(%d))", nid));
    }

    public final int treeStartConglomerate(final int size) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeStartConglomerate(%d)", size));
    }

    /** 265392050 : TreeLock-Failure but does the change of state **/
    public final int treeTurnOff(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeTurnOff(val(%d))", nid));
    }

    /** 265392050 : TreeLock-Failure but does the change of state **/
    public final int treeTurnOn(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeTurnOn(val(%d))", nid));
    }

    public final int treeWriteTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeWriteTree(ref('%s'),val(%d))", expt, shot));
    }
}

package mds;

import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_a.Uint64Array;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.NUMBER;
import mds.data.descriptor_s.Pointer;
import mds.mdsip.Connection;

public final class TreeShr{
    public static final class SegmentInfo{
        public final byte  dtype;
        public final byte  dimct;
        public final int[] dims;
        public final int   next_row;

        public SegmentInfo(final byte dtype, final byte dimct, final int[] dims, final int next_row){
            this.dtype = dtype;
            this.dimct = dimct;
            this.dims = dims;
            this.next_row = next_row;
        }

        public SegmentInfo(final int[] array){
            this.dtype = (byte)array[0];
            this.dimct = (byte)array[1];
            this.dims = new int[8];
            System.arraycopy(array, 2, this.dims, 0, 8);
            this.next_row = array[10];
        }
    }
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
        return this.connection.getInteger(String.format("_s=TreeShr->TreeBeginTimestampedSegment(val(%d),xd($),val(%d))", nid, idx), initialValue);
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

    public final Pointer treeCtx() throws MdsException {
        return (Pointer)this.connection.getDescriptor("TreeShr->TreeCtx:P()", Pointer.class);
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

    public final int treeGetNumSegments(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_a=0;_s=TreeShr->TreeGetNumSegments(val(%d),ref(_a));_a", nid));
    }

    public final Descriptor treeGetRecord(final int nid) throws MdsException {
        return this.connection.getDescriptor(String.format("_d=*;_s=TreeShr->TreeGetRecord(val(%d),xd(_d));_d", nid));
    }

    public final Signal treeGetSegment(final int nid, final int idx) throws MdsException {
        return (Signal)this.connection.getDescriptor(String.format("_a=_t=*;_s=TreeShr->TreeGetSegment(val(%d),val(%d),xd(_a),xd(_t));make_signal(_a,*,_t)", nid, idx), Signal.class);
    }

    public final SegmentInfo treeGetSegmentInfo(final int nid, final int idx) throws MdsException {
        return new SegmentInfo(this.connection.getIntegerArray(String.format("_a=0B;_b=0B;_d=zero(8,0);_i=0;_s=TreeShr->TreeGetSegmentInfo(val(%d),val(%d),ref(_a),ref(_b),ref(_d),ref(_i));[_a,_b,_d[0],_d[1],_d[2],_d[3],_d[4],_d[5],_d[6],_d[7],_i]", nid, idx)));
    }

    public final Descriptor treeGetSegmentLimits(final int nid, final int idx) throws MdsException {
        return this.connection.getDescriptor(String.format("_a=_b=*;_s=TreeShr->TreeGetSegmentLimits(val(%d),val(%d),xd(_a),xd(_b));[_a,_b]", nid, idx));
    }

    public final Descriptor treeGetXNci(final int nid) throws MdsException {
        return this.treeGetXNci(nid, "attributenames");
    }

    public final Descriptor treeGetXNci(final int nid, final String name) throws MdsException {
        return this.connection.getDescriptor(String.format("_a=*;_s=TreeShr->TreeGetXNci(val(%d),ref('%s'),xd(_a));_a", nid, name));
    }

    public final int treeMakeTimestampedSegment(final int nid, final Uint64Array timestamps, final Descriptor_A values) throws MdsException {
        return this.treeMakeTimestampedSegment(nid, timestamps, values, -1, values.getLength());
    }

    public final int treeMakeTimestampedSegment(final int nid, final Uint64Array timestamps, final Descriptor_A values, final int idx) throws MdsException {
        return this.treeMakeTimestampedSegment(nid, timestamps, values, idx, values.getLength());
    }

    public final int treeMakeTimestampedSegment(final int nid, final Uint64Array timestamps, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeMakeTimestampedSegment(val(%d),ref($),xd($),val(%d),val(%d))", nid, idx, rows_filled), timestamps, values);
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

    /** utility_update=2: compress, if utility_update!=0 no_write-flags are ignored and flags are reset to 0x5400 **/
    public final int treePutRecord(final int nid, final Descriptor dsc) throws MdsException {
        return this.treePutRecord(nid, dsc, 0);
    }

    public final int treePutRecord(final int nid, final Descriptor dsc, final int utility_update) throws MdsException {
        if(dsc == null) return this.connection.getInteger(String.format("_s=TreeShr->TreePutRecord(val(%d),*,val(%d))", nid, utility_update));
        return this.connection.getInteger(String.format("_s=TreeShr->TreePutRecord(val(%d),xd($),val(%d))", nid, utility_update), dsc);
    }

    /** adds row to segmented node **/
    public final int treePutRow(final int nid, final int bufsize, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreePutRow(val(%d),val(%d),ref(%dQU),xd($))", nid, bufsize, timestamp), data);
    }

    public final int treePutSegment(final int nid, final int idx, final Descriptor_A data) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreePutSegment(val(%d),val(%d),xd($))", nid, idx), data);
    }

    /** adds row to segmented node **/
    public final int treePutTimestampedSegment(final int nid, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreePutTimestampedSegment(val(%d),ref(%dQU),xd($))", nid, timestamp), data);
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

    public final int treeRestoreContext(final Pointer treectx) throws MdsException {
        return this.connection.getInteger("TreeShr->TreeRestoreContext(val($))", treectx);
    }

    public final Pointer treeSaveContext() throws MdsException {
        return (Pointer)this.connection.getDescriptor("TreeShr->TreeSaveContext:P()", Pointer.class);
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

    public final int treeSetTimeContext() throws MdsException {
        return this.treeSetTimeContext((NUMBER)null, (NUMBER)null, (NUMBER)null);
    }

    public final int treeSetTimeContext(final Number start, final Number end, final Number delta) throws MdsException {
        return this.treeSetTimeContext(NUMBER.make(start), NUMBER.make(end), NUMBER.make(delta));
    }

    public final int treeSetTimeContext(final NUMBER start, final NUMBER end, final NUMBER delta) throws MdsException {
        final StringBuilder cmd = new StringBuilder(128).append("_s=TreeShr->TreeSetTimeContext(");
        if(start == null || start.dtype == DTYPE.MISSING) cmd.append("val(0)");
        else cmd.append("descr(").append(start.decompile()).append(')');
        cmd.append(',');
        if(end == null || end.dtype == DTYPE.MISSING) cmd.append("val(0)");
        else cmd.append("descr(").append(end.decompile()).append(')');
        cmd.append(',');
        if(delta == null || delta.dtype == DTYPE.MISSING) cmd.append("val(0)");
        else cmd.append("descr(").append(delta.decompile()).append(')');
        return this.connection.getInteger(cmd.append(')').toString());
    }

    public final int treeSetXNci(final int nid, final String name, final Descriptor value) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeSetXNci(val(%d),ref('%s'),xd($))", nid, name), value.getData());
    }

    public final int treeStartConglomerate(final int size) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeStartConglomerate(val(%d))", size));
    }

    /** 265392050 : TreeLock-Failure but does the change of state **/
    public final int treeTurnOff(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeTurnOff(val(%d))", nid));
    }

    /** 265392050 : TreeLock-Failure but does the change of state **/
    public final int treeTurnOn(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeTurnOn(val(%d))", nid));
    }

    public final boolean treeUsePrivateCtx(final boolean state) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeUsePrivateCtx(%d)", state ? 1 : 0)) == 1;
    }

    public final boolean treeUsingPrivateCtx(final boolean state) throws MdsException {
        return this.connection.getInteger("TreeShr->TreeUseingPrivateCtx()") == 1;
    }

    public final int treeWriteTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("_s=TreeShr->TreeWriteTree(ref('%s'),val(%d))", expt, shot));
    }
}

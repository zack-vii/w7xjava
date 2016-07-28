package mds;

import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_a.Int64Array;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.NUMBER;
import mds.data.descriptor_s.Pointer;
import mds.mdsip.Connection;

public final class TreeShr implements ITreeShr{
    private final Connection connection;

    public TreeShr(final Connection connection){
        this.connection = connection;
    }

    @Override
    public final IntegerStatus treeAddConglom(final String path, final String model) throws MdsException {
        return new IntegerStatus(this.connection.getIntegerArray("_a=-1;_s=TreeShr->TreeAddConglom(ref($),ref($),ref(_a));[_s,_a]", new CString(path), new CString(model)));
    }

    @Override
    public final IntegerStatus treeAddNode(final String path, final byte usage) throws MdsException {
        return new IntegerStatus(this.connection.getIntegerArray(String.format("_a=-1;_s=TreeShr->TreeAddNode(ref($),ref(_a),val(%d));[_s,_a]", usage), new CString(path)));
    }

    @Override
    public final int treeAddTag(final int nid, final String tag) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeAddTag(val(%d),ref($))", nid), new CString(tag));
    }

    @Override
    public final int treeBeginTimestampedSegment(final int nid, final Descriptor_A initialValue, final int idx) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeBeginTimestampedSegment(val(%d),xd($),val(%d))", nid, idx), initialValue);
    }

    @Override
    public final int treeCleanDatafile(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeCleanDatafile(ref($),val(%d))", shot), new CString(expt));
    }

    @Override
    public final int treeClose(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeClose(ref($),val(%d))", shot), new CString(expt));
    }

    @Override
    public final int treeCompressDatafile(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeCompressDatafile(ref($),val(%d))", shot), new CString(expt));
    }

    @Override
    public final int treeCreateTreeFiles(final String expt, final int newshot, final int fromshot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeCreateTreeFiles(ref($),val(%d),val(%d))", newshot, fromshot), new CString(expt));
    }

    @Override
    public final Pointer treeCtx() throws MdsException {
        return (Pointer)this.connection.getDescriptor("TreeShr->TreeCtx:P()", Pointer.class);
    }

    @Override
    public final int treeDeleteNodeExecute() throws MdsException {
        return this.connection.getInteger("TreeShr->TreeDeleteNodeExecute()");
    }

    @Override
    public final IntegerStatus treeDeleteNodeGetNid(final int idx) throws MdsException {
        return new IntegerStatus(this.connection.getIntegerArray(String.format("_a=%d;_s=TreeShr->TreeDeleteNodeGetNid(ref(_a));[_s,_a]", idx)));
    }

    @Override
    public final IntegerStatus treeDeleteNodeInitialize(final int nid) throws MdsException {
        return this.treeDeleteNodeInitialize(nid, 0, true);
    }

    @Override
    public final IntegerStatus treeDeleteNodeInitialize(final int nid, final int count, final boolean init) throws MdsException {
        return new IntegerStatus(this.connection.getIntegerArray(String.format("_a=%d;_s=TreeShr->TreeDeleteNodeInitialize(val(%d),ref(_a),val(%d));[_s,_a]", count, nid, init ? 1 : 0)));
    }

    @Override
    public final int treeEndConglomerate() throws MdsException {
        return this.connection.getInteger("_s=TreeShr->TreeEndConglomerate()");
    }

    @Override
    public final NodeRefStatus treeFindNodeWild(final String searchstr, final int usage_mask, final NodeRefStatus ref) throws MdsException {
        synchronized(this.connection){
            final int status = this.connection.getInteger(String.format("_a=-1;_q=%dQ;TreeShr->TreeFindTagWildDsc(ref($),ref(_a),ref(_q),val(%d))", ref.ref, usage_mask), new CString(searchstr));
            if(status == 0) return NodeRefStatus.init;
            return new NodeRefStatus(this.connection.getInteger("_a"), this.connection.getLong("_q"), status);
        }
    }

    @Override
    public final TagRefStatus treeFindTagWild(final String searchstr, final TagRefStatus ref) throws MdsException {
        synchronized(this.connection){
            final int status = this.connection.getInteger(String.format("_a=*;_i=-1;_q=%dQ;TreeShr->TreeFindTagWildDsc(ref($),ref(_i),ref(_q),xd(_a))", ref.ref), new CString(searchstr));
            if(status == 0) return TagRefStatus.init;
            return new TagRefStatus(this.connection.getString("_a"), this.connection.getInteger("_i"), this.connection.getLong("_q"), status);
        }
    }

    @Override
    public final int treeGetCurrentShotId(final String expt) throws MdsException {
        return this.connection.getInteger("TreeShr->TreeGetCurrentShotId($)", new CString(expt));
    }

    @Override
    public final long treeGetDatafileSize() throws MdsException {
        return this.connection.getLong("TreeShr->TreeGetDatafileSize()");
    }

    @Override
    public final IntegerStatus treeGetDefaultNid() throws MdsException {
        return new IntegerStatus(this.connection.getIntegerArray("_a=-1;_s=TreeShr->TreeGetDefaultNid(ref(_a));[_s,_a]"));
    }

    @Override
    public final String treeGetMinimumPath(final int nid) throws MdsException {
        return this.connection.getString(String.format("TreeShr->TreeGetMinimumPath:T(val(0),val(%d))", nid));
    }

    @Override
    public final IntegerStatus treeGetNumSegments(final int nid) throws MdsException {
        return new IntegerStatus(this.connection.getIntegerArray(String.format("_a=0;_s=TreeShr->TreeGetNumSegments(val(%d),ref(_a));[_s,_a]", nid)));
    }

    @Override
    public final String treeGetPath(final int nid) throws MdsException {
        return this.connection.getString(String.format("TreeShr->TreeGetPath:T(val(%d))", nid));
    }

    @Override
    public final DescriptorStatus treeGetRecord(final int nid) throws MdsException {
        synchronized(this.connection){
            final int status = this.connection.getInteger(String.format("_a=*;TreeShr->TreeGetRecord(val(%d),xd(_a))", nid));
            if((status & 1) == 0) return new DescriptorStatus(null, status);
            return new DescriptorStatus(this.connection.getDescriptor("_a"), status);
        }
    }

    @Override
    public final SignalStatus treeGetSegment(final int nid, final int idx) throws MdsException {
        synchronized(this.connection){
            final int status = this.connection.getInteger(String.format("_a=_t=*;TreeShr->TreeGetSegment(val(%d),val(%d),xd(_a),xd(_t))", nid, idx));
            if((status & 1) == 0) return new SignalStatus(null, status);
            return new SignalStatus((Signal)this.connection.getDescriptor("make_signal(_a,*,_t)", Signal.class), status);
        }
    }

    @Override
    public final SegmentInfo treeGetSegmentInfo(final int nid, final int idx) throws MdsException {
        return new SegmentInfo(this.connection.getIntegerArray(String.format("_a=0B;_b=0B;_d=zero(8,0);_i=0;_s=TreeShr->TreeGetSegmentInfo(val(%d),val(%d),ref(_a),ref(_b),ref(_d),ref(_i));[_a,_b,_d[0],_d[1],_d[2],_d[3],_d[4],_d[5],_d[6],_d[7],_i,_s]", nid, idx)));
    }

    @Override
    public final DescriptorStatus treeGetSegmentLimits(final int nid, final int idx) throws MdsException {
        synchronized(this.connection){
            final int status = this.connection.getInteger(String.format("_a=_b=*;TreeShr->TreeGetSegmentLimits(val(%d),val(%d),xd(_a),xd(_b))", nid, idx));
            if((status & 1) == 0) return new DescriptorStatus(null, status);
            return new DescriptorStatus(this.connection.getDescriptor("[_a,_b]"), status);
        }
    }

    @Override
    public final DescriptorStatus treeGetXNci(final int nid) throws MdsException {
        return this.treeGetXNci(nid, "attributenames");
    }

    @Override
    public final DescriptorStatus treeGetXNci(final int nid, final String name) throws MdsException {
        synchronized(this.connection){
            final int status = this.connection.getInteger(String.format("_a=*;TreeShr->TreeGetXNci(val(%d),ref($),xd(_a))", nid), new CString(name));
            if((status & 1) == 0) return new DescriptorStatus(null, status);
            return new DescriptorStatus(this.connection.getDescriptor("_a"), status);
        }
    }

    @Override
    public final int treeIsOn(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeIsOn(val(%d))", nid));
    }

    @Override
    public final int treeMakeTimestampedSegment(final int nid, final Int64Array timestamps, final Descriptor_A values) throws MdsException {
        return this.treeMakeTimestampedSegment(nid, timestamps, values, -1);
    }

    @Override
    public final int treeMakeTimestampedSegment(final int nid, final Int64Array timestamps, final Descriptor_A values, final int idx) throws MdsException {
        return this.treeMakeTimestampedSegment(nid, timestamps, values, idx, timestamps.getLength());
    }

    @Override
    public final int treeMakeTimestampedSegment(final int nid, final Int64Array timestamps, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeMakeTimestampedSegment(val(%d),ref($),xd($),val(%d),val(%d))", nid, idx, rows_filled), timestamps, values);
    }

    @Override
    public final int treeMakeTimestampedSegment(final int nid, final long[] timestamps, final Descriptor_A values) throws MdsException {
        return this.treeMakeTimestampedSegment(nid, new Int64Array(timestamps), values);
    }

    @Override
    public final int treeMakeTimestampedSegment(final int nid, final long[] timestamps, final Descriptor_A values, final int idx) throws MdsException {
        return this.treeMakeTimestampedSegment(nid, new Int64Array(timestamps), values, idx);
    }

    @Override
    public final int treeMakeTimestampedSegment(final int nid, final long[] timestamps, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException {
        return this.treeMakeTimestampedSegment(nid, new Int64Array(timestamps), values, idx, rows_filled);
    }

    @Override
    public final int treeOpen(final String expt, final int shot, final boolean readonly) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpen(ref($),val(%d),val(%d))", shot, readonly ? 1 : 0), new CString(expt));
    }

    @Override
    public final int treeOpenEdit(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpenEdit(ref($),val(%d))", shot), new CString(expt));
    }

    @Override
    public final int treeOpenNew(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeOpenNew(ref($),val(%d))", shot), new CString(expt));
    }

    @Override
    public final int treePutRecord(final int nid, final Descriptor dsc) throws MdsException {
        return this.treePutRecord(nid, dsc, 0);
    }

    @Override
    public final int treePutRecord(final int nid, final Descriptor dsc, final int utility_update) throws MdsException {
        if(dsc == null) return this.connection.getInteger(String.format("_s=TreeShr->TreePutRecord(val(%d),*,val(%d))", nid, utility_update));
        return this.connection.getInteger(String.format("TreeShr->TreePutRecord(val(%d),xd($),val(%d))", nid, utility_update), dsc);
    }

    @Override
    public final int treePutRow(final int nid, final int bufsize, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreePutRow(val(%d),val(%d),ref(%dQU),xd($))", nid, bufsize, timestamp), data);
    }

    @Override
    public final int treePutSegment(final int nid, final int idx, final Descriptor_A data) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreePutSegment(val(%d),val(%d),xd($))", nid, idx), data);
    }

    @Override
    public final int treePutTimestampedSegment(final int nid, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreePutTimestampedSegment(val(%d),ref(%dQU),xd($))", nid, timestamp), data);
    }

    @Override
    public final int treeQuitTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeQuitTree(ref($),val(%d))", shot), new CString(expt));
    }

    @Override
    public final int treeRemoveNodesTags(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeRemoveNodesTags(val(%d))", nid));
    }

    @Override
    public final int treeRenameNode(final int nid, final String name) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeRenameNode(val(%d),ref($))", nid), new CString(name));
    }

    @Override
    public final int treeRestoreContext(final Pointer treectx) throws MdsException {
        return this.connection.getInteger("TreeShr->TreeRestoreContext(val($))", treectx);
    }

    @Override
    public final Pointer treeSaveContext() throws MdsException {
        return (Pointer)this.connection.getDescriptor("TreeShr->TreeSaveContext:P()", Pointer.class);
    }

    @Override
    public final int treeSetCurrentShotId(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetCurrentShotId(ref($),val(%d))", shot), new CString(expt));
    }

    @Override
    public final int treeSetDefault(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeSetDefault(GETNCI(%d,'FULLPATH'))", nid));
    }

    @Override
    public final int treeSetNciItm(final int nid, final boolean state, final int flags) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetNciItm(val(%d),val(%d),val(%d))", nid, state ? 1 : 2, flags));
    }

    @Override
    public final int treeSetNoSubtree(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetNoSubtree(val(%d))", nid));
    }

    @Override
    public final int treeSetSubtree(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetSubtree(val(%d))", nid));
    }

    @Override
    public final int treeSetTimeContext() throws MdsException {
        return this.treeSetTimeContext((NUMBER)null, (NUMBER)null, (NUMBER)null);
    }

    @Override
    public final int treeSetTimeContext(final Number start, final Number end, final Number delta) throws MdsException {
        return this.treeSetTimeContext(NUMBER.make(start), NUMBER.make(end), NUMBER.make(delta));
    }

    @Override
    public final int treeSetTimeContext(final NUMBER start, final NUMBER end, final NUMBER delta) throws MdsException {
        final StringBuilder cmd = new StringBuilder(128).append("TreeShr->TreeSetTimeContext(");
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

    @Override
    public final int treeSetXNci(final int nid, final String name, final Descriptor value) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeSetXNci(val(%d),ref($),xd($))", nid), new CString(name), value.getData());
    }

    @Override
    public final int treeStartConglomerate(final int size) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeStartConglomerate(val(%d))", size));
    }

    @Override
    public final int treeTurnOff(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeTurnOff(val(%d))", nid));
    }

    @Override
    public final int treeTurnOn(final int nid) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeTurnOn(val(%d))", nid));
    }

    @Override
    public final boolean treeUsePrivateCtx(final boolean state) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeUsePrivateCtx(%d)", state ? 1 : 0)) == 1;
    }

    @Override
    public final boolean treeUsingPrivateCtx() throws MdsException {
        return this.connection.getInteger("TreeShr->TreeUsingPrivateCtx()") == 1;
    }

    @Override
    public final int treeWriteTree(final String expt, final int shot) throws MdsException {
        return this.connection.getInteger(String.format("TreeShr->TreeWriteTree(ref($),val(%d))", shot), new CString(expt));
    }
}

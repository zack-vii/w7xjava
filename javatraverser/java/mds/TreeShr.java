package mds;

import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_a.Int64Array;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.NUMBER;
import mds.data.descriptor_s.Pointer;

public final class TreeShr{
    public static class DescriptorStatus{
        public final Descriptor data;
        public final int        status;

        public DescriptorStatus(final Descriptor data, final int status){
            this.data = data;
            this.status = status;
        }

        @Override
        public final String toString() {
            return new StringBuilder().append(this.data).append(";").append(this.status).toString();
        }
    }
    public static class IntegerStatus{
        public final int data;
        public final int status;

        public IntegerStatus(final int data, final int status){
            this.data = data;
            this.status = status;
        }

        public IntegerStatus(final int[] datastatus){
            this(datastatus[1], datastatus[0]);
        }

        @Override
        public final String toString() {
            final StringBuilder str = new StringBuilder(32).append(this.data).append(';');
            return str.append(this.status).toString();
        }
    }
    public static class NodeRefStatus{
        public static final NodeRefStatus init = new NodeRefStatus(-1, 0, 0);
        public final int                  data;
        public final long                 ref;
        public final int                  status;

        public NodeRefStatus(final int data, final long ref, final int status){
            this.data = data;
            this.ref = ref;
            this.status = status;
        }

        public final boolean hasMore() {
            return this.ref != -1;
        }

        public final boolean ok() {
            return this.status == 1;
        }

        @Override
        public final String toString() {
            final StringBuilder str = new StringBuilder(64).append(this.data).append(';');
            str.append(this.ref).append("l;");
            return str.append(this.status).toString();
        }
    }
    public static class SegmentInfo{
        public byte      dtype;
        public byte      dimct;
        public int[]     dims;
        public int       next_row;
        public final int status;

        public SegmentInfo(final byte dtype, final byte dimct, final int[] dims, final int next_row){
            this(dtype, dimct, dims, next_row, -1);
        }

        public SegmentInfo(final byte dtype, final byte dimct, final int[] dims, final int next_row, final int status){
            this.dtype = dtype;
            this.dimct = dimct;
            this.dims = dims;
            this.next_row = next_row;
            this.status = status;
        }

        public SegmentInfo(final int[] array){
            this.dtype = (byte)array[0];
            this.dimct = (byte)array[1];
            this.dims = new int[8];
            System.arraycopy(array, 2, this.dims, 0, 8);
            this.next_row = array[10];
            this.status = array.length > 11 ? array[11] : -1;
        }

        @Override
        public final String toString() {
            final StringBuilder str = new StringBuilder(128).append(this.dtype).append(';');
            str.append(this.dimct).append(';');
            for(final int dim : this.dims)
                str.append(dim).append(',');
            str.append(this.next_row).append(';');
            return str.append(this.status).toString();
        }
    }
    public static class SignalStatus{
        public final Signal data;
        public final int    status;

        public SignalStatus(final Signal data, final int status){
            this.data = data;
            this.status = status;
        }

        @Override
        public final String toString() {
            return new StringBuilder().append(this.data).append(";").append(this.status).toString();
        }
    }
    public static class StringStatus{
        public final String data;
        public final int    status;

        public StringStatus(final String data, final int status){
            this.data = data;
            this.status = status;
        }

        @Override
        public final String toString() {
            return new StringBuilder(32).append(this.data).append(";").append(this.status).toString();
        }
    }
    public static class TagRef{
        public static final TagRef init = new TagRef(null, 0);
        public final String        data;
        public final long          ref;

        public TagRef(final String data, final long ref){
            this.data = data;
            this.ref = ref;
        }

        public final boolean hasMore() {
            return this.ref != -1;
        }

        public final boolean ok() {
            return this.ref != 0;
        }

        @Override
        public final String toString() {
            return new StringBuilder(32).append(this.data).append(";").append(this.ref).append('l').toString();
        }
    }
    public static class TagRefStatus{
        public static final TagRefStatus init = new TagRefStatus(null, -1, 0, 0);
        public final String              data;
        public final int                 nid;
        public final long                ref;
        public final int                 status;

        public TagRefStatus(final String data, final int nid, final long ref, final int status){
            this.data = data;
            this.nid = nid;
            this.ref = ref;
            this.status = status;
        }

        public final boolean hasMore() {
            return this.ref != -1;
        }

        public final boolean ok() {
            return this.status == 1;
        }

        @Override
        public final String toString() {
            final StringBuilder str = new StringBuilder(128).append(this.data).append(';');
            str.append(this.nid).append(';');
            str.append(this.ref).append("l;");
            return str.append(this.status).toString();
        }
    }
    public static final int NO_STATUS = -1;
    private final Mds       mds;

    public TreeShr(final Mds mds){
        this.mds = mds;
    }

    public final int doMethod(final Pointer ctx, final int nid, final String method, final String... args) throws MdsException {
        final CString[] parms = new CString[args.length + 1];
        parms[0] = new CString(method);
        final StringBuilder expr = new StringBuilder(256).append("TreeShr->TreeDoMethod(val(").append(nid).append("),ref($)");
        for(int i = 0; i < args.length; i++){
            parms[i + 1] = new CString(args[i]);
            expr.append(",ref($)");
        }
        return this.mds.getInteger(ctx, expr.append(')').toString(), parms);
    }

    /**
     * adds a device node of specific model to a tree (EDIT)
     *
     * @return IntegerStatus
     **/
    public final IntegerStatus treeAddConglom(final Pointer ctx, final String path, final String model) throws MdsException {
        return new IntegerStatus(this.mds.getIntegerArray(ctx, "_a=-1;_s=TreeShr->TreeAddConglom(ref($),ref($),ref(_a));[_s,_a]", new CString(path), new CString(model)));
    }

    /**
     * adds a node of specific usage to a tree (EDIT)
     *
     * @return IntegerStatus
     **/
    public final IntegerStatus treeAddNode(final Pointer ctx, final String path, final byte usage) throws MdsException {
        return new IntegerStatus(this.mds.getIntegerArray(ctx, String.format("_a=-1;_s=TreeShr->TreeAddNode(ref($),ref(_a),val(%d));[_s,_a]", usage), new CString(path)));
    }

    /**
     * adds a tag to a node
     *
     * @return int: status
     **/
    public final int treeAddTag(final Pointer ctx, final int nid, final String tag) throws MdsException {
        return this.mds.getInteger(String.format("TreeShr->TreeAddTag(val(%d),ref($))", nid), new CString(tag));
    }

    /**
     * initiates a new time-stamped segment
     *
     * @return int: status
     **/
    public final int treeBeginTimestampedSegment(final Pointer ctx, final int nid, final Descriptor_A initialValue, final int idx) throws MdsException {
        return this.mds.getInteger(String.format("TreeShr->TreeBeginTimestampedSegment(val(%d),xd($),val(%d))", nid, idx), initialValue);
    }

    /**
     * cleans the data file by removing unreferenced data
     *
     * @return int: status
     **/
    public final int treeCleanDatafile(final String expt, final int shot) throws MdsException {
        return this.mds.getInteger(String.format("TreeShr->TreeCleanDatafile(ref($),val(%d))", shot), new CString(expt));
    }

    /**
     * closes a tree
     *
     * @return int: status
     **/
    public final int treeClose(final Pointer ctx, final String expt, final int shot) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeClose(ref($),val(%d))", shot), new CString(expt));
    }

    /**
     * cleans and compresses the data file of a tree
     *
     * @return int: status
     **/
    public final int treeCompressDatafile(final String expt, final int shot) throws MdsException {
        return this.mds.getInteger(String.format("TreeShr->TreeCompressDatafile(ref($),val(%d))", shot), new CString(expt));
    }

    /**
     * creates a copy of a tree with a new shot number
     *
     * @return int: status
     **/
    public final int treeCreateTreeFiles(final String expt, final int newshot, final int fromshot) throws MdsException {
        return this.mds.getInteger(String.format("TreeShr->TreeCreateTreeFiles(ref($),val(%d),val(%d))", newshot, fromshot), new CString(expt));
    }

    /**
     * returns the current tree context
     *
     * @return Pointer: context
     **/
    public final Pointer treeCtx(final Pointer ctx) throws MdsException {
        return (Pointer)this.mds.getDescriptor("TreeShr->TreeCtx:P()", Pointer.class);
    }

    /**
     * executes the initiated delete operation
     *
     * @return int: status
     **/
    public final int treeDeleteNodeExecute(final Pointer ctx) throws MdsException {
        return this.mds.getInteger("TreeShr->TreeDeleteNodeExecute()");
    }

    /** @return IntegerStatus: nid-number of the next node in the list of nodes to be deleted **/
    public final IntegerStatus treeDeleteNodeGetNid(final Pointer ctx, final int idx) throws MdsException {
        return new IntegerStatus(this.mds.getIntegerArray(String.format("_a=%d;_s=TreeShr->TreeDeleteNodeGetNid(ref(_a));[_s,_a]", idx)));
    }

    /**
     * initiates a delete operation of a node and its descendants (primitive)
     *
     * @return IntegerStatus
     **/
    public final IntegerStatus treeDeleteNodeInitialize(final Pointer ctx, final int nid) throws MdsException {
        return this.treeDeleteNodeInitialize(ctx, nid, 0, true);
    }

    /**
     * initiates a delete operation of a node and its descendants
     *
     * @return IntegerStatus
     **/
    public final IntegerStatus treeDeleteNodeInitialize(final Pointer ctx, final int nid, final int count, final boolean init) throws MdsException {
        return new IntegerStatus(this.mds.getIntegerArray(ctx, String.format("_a=%d;_s=TreeShr->TreeDeleteNodeInitialize(val(%d),ref(_a),val(%d));[_s,_a]", count, nid, init ? 1 : 0)));
    }

    /**
     * finishes the added conglomerate
     *
     * @return IntegerStatus
     **/
    public final int treeEndConglomerate(final Pointer ctx) throws MdsException {
        return this.mds.getInteger(ctx, "_s=TreeShr->TreeEndConglomerate()");
    }

    /**
     * searches for the next tag assigned to a node
     *
     * @return TagRef: next tag found
     **/
    public final TagRef treeFindNodeTags(final Pointer ctx, final int nid, final TagRef ref) throws MdsException {
        synchronized(this.mds){
            final long r = this.mds.getLong(ctx, String.format("_a=%dQ;TreeShr->TreeFindNodeTags(val(%d),ref(_a));_a", ref.ref, nid));
            if(r == 0) return TagRef.init;
            return new TagRef(this.mds.getString(String.format("TreeShr->TreeFindNodeTags:T(val(%d),ref(%dQ))", nid, ref.ref)), r);
        }
    }

    /**
     * searches for the next node meeting the criteria of a matching search string and usage mask
     *
     * @return NodeRefStatus: next nid-number found
     **/
    public final NodeRefStatus treeFindNodeWild(final Pointer ctx, final String searchstr, final int usage_mask, final NodeRefStatus ref) throws MdsException {
        synchronized(this.mds){
            final int status = this.mds.getInteger(ctx, String.format("_a=-1;_q=%dQ;TreeShr->treeFindNodeWild(ref($),ref(_a),ref(_q),val(%d))", ref.ref, usage_mask), new CString(searchstr));
            if(status == 0) return NodeRefStatus.init;
            return new NodeRefStatus(this.mds.getInteger("_a"), this.mds.getLong("_q"), status);
        }
    }

    /**
     * searches for the next tag in the tag list matching a search string
     *
     * @return TagRefStatus: next tag found
     **/
    public final TagRefStatus treeFindTagWild(final Pointer ctx, final String searchstr, final TagRefStatus ref) throws MdsException {
        synchronized(this.mds){
            final int status = this.mds.getInteger(ctx, String.format("_a=*;_i=-1;_q=%dQ;TreeShr->TreeFindTagWildDsc(ref($),ref(_i),ref(_q),xd(_a))", ref.ref), new CString(searchstr));
            if(status == 0) return TagRefStatus.init;
            return new TagRefStatus(this.mds.getString(null, "_a"), this.mds.getInteger(null, "_i"), this.mds.getLong(null, "_q"), status);
        }
    }

    /**
     * resolves the current shot number (0)
     *
     * @return int: shot number
     **/
    public final int treeGetCurrentShotId(final String expt) throws MdsException {
        return this.mds.getInteger(null, "TreeShr->TreeGetCurrentShotId($)", new CString(expt));
    }

    /**
     * checks the size of the data file
     *
     * @return long: file size in byte
     **/
    public final long treeGetDatafileSize(final Pointer ctx) throws MdsException {
        return this.mds.getLong(ctx, "TreeShr->TreeGetDatafileSize()");
    }

    /**
     * checks the default node of active tree
     *
     * @return IntegerStatus: nid-number of default node
     **/
    public final IntegerStatus treeGetDefaultNid(final Pointer ctx) throws MdsException {
        return new IntegerStatus(this.mds.getIntegerArray(ctx, "_a=-1;_s=TreeShr->TreeGetDefaultNid(ref(_a));[_s,_a]"));
    }

    /**
     * returns the shortest path to node (from default node)
     *
     * @return String: minimal path to node
     **/
    public final String treeGetMinimumPath(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getString(ctx, String.format("IF(TreeShr->TreeGetMinimumPath(val(0),val(%d))==0){*;}ELSE{TreeShr->TreeGetMinimumPath:T(val(0),val(%d));}", nid, nid));
    }

    /**
     * @return IntegerStatus: number of segments
     **/
    public final IntegerStatus treeGetNumSegments(final Pointer ctx, final int nid) throws MdsException {
        return new IntegerStatus(this.mds.getIntegerArray(ctx, String.format("_a=0;_s=TreeShr->TreeGetNumSegments(val(%d),ref(_a));[_s,_a]", nid)));
    }

    /**
     * returns the natural path of a node (form its parent tree root)
     *
     * @return String: path to node
     **/
    public final String treeGetPath(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getString(ctx, String.format("IF(TreeShr->TreeGetPath(val(%d))==0){*;}ELSE{TreeShr->TreeGetPath:T(val(%d));}", nid, nid));
    }

    /**
     * reads the full record of a node
     *
     * @return DescriptorStatus: record of node
     **/
    public final DescriptorStatus treeGetRecord(final Pointer ctx, final int nid) throws MdsException {
        synchronized(this.mds){
            final int status = this.mds.getInteger(ctx, String.format("_a=*;TreeShr->TreeGetRecord(val(%d),xd(_a))", nid));
            if((status & 1) == 0) return new DescriptorStatus(null, status);
            return new DescriptorStatus(this.mds.getDescriptor(ctx, "_a"), status);
        }
    }

    /**
     * reads idx-th segment of a node
     *
     * @return SignalStatus: segment
     **/
    public final SignalStatus treeGetSegment(final Pointer ctx, final int nid, final int idx) throws MdsException {
        synchronized(this.mds){
            final int status = this.mds.getInteger(ctx, String.format("_a=_t=*;TreeShr->TreeGetSegment(val(%d),val(%d),xd(_a),xd(_t))", nid, idx));
            if((status & 1) == 0) return new SignalStatus(null, status);
            return new SignalStatus((Signal)this.mds.getDescriptor(ctx, "make_signal(_a,*,_t)", Signal.class), status);
        }
    }

    /**
     * @return SegmentInfo: info about the idx-th segment of a node
     **/
    public final SegmentInfo treeGetSegmentInfo(final Pointer ctx, final int nid, final int idx) throws MdsException {
        return new SegmentInfo(this.mds.getIntegerArray(ctx, String.format("_a=0B;_b=0B;_d=zero(8,0);_i=0;_s=TreeShr->TreeGetSegmentInfo(val(%d),val(%d),ref(_a),ref(_b),ref(_d),ref(_i));[_a,_b,_d[0],_d[1],_d[2],_d[3],_d[4],_d[5],_d[6],_d[7],_i,_s]", nid, idx)));
    }

    /**
     * @return DescriptorStatus: time limits of the idx-th segment of a node
     **/
    public final DescriptorStatus treeGetSegmentLimits(final Pointer ctx, final int nid, final int idx) throws MdsException {
        synchronized(this.mds){
            final int status = this.mds.getInteger(ctx, String.format("_a=_b=*;TreeShr->TreeGetSegmentLimits(val(%d),val(%d),xd(_a),xd(_b))", nid, idx));
            if((status & 1) == 0) return new DescriptorStatus(null, status);
            return new DescriptorStatus(this.mds.getDescriptor(ctx, "[_a,_b]"), status);
        }
    }

    /**
     * reads the list of attributes of a node
     *
     * @return int: status
     **/
    public final DescriptorStatus treeGetXNci(final Pointer ctx, final int nid) throws MdsException {
        return this.treeGetXNci(ctx, nid, "attributenames");
    }

    /**
     * reads an extended attribute of a node
     *
     * @return int: status
     **/
    public final DescriptorStatus treeGetXNci(final Pointer ctx, final int nid, final String name) throws MdsException {
        synchronized(this.mds){
            final int status = this.mds.getInteger(ctx, String.format("_a=*;TreeShr->TreeGetXNci(val(%d),ref($),xd(_a))", nid), new CString(name));
            if((status & 1) == 0) return new DescriptorStatus(null, status);
            return new DescriptorStatus(this.mds.getDescriptor(ctx, "_a"), status);
        }
    }

    /**
     * checks the state of a node
     *
     * @return int: status
     **/
    public final int treeIsOn(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeIsOn(val(%d))", nid));
    }

    /**
     * adds a new segment to segmented node
     *
     * @return int: status
     **/
    public final int treeMakeSegment(final Pointer ctx, final int nid, final Descriptor start, final Descriptor end, final Descriptor dimension, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeMakeTimestampedSegment(val(%d),xd($),xd($),xd($),xd($),val(%d),val(%d))", nid, idx, rows_filled), start, end, dimension, values);
    }

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public final int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final Int64Array timestamps, final Descriptor_A values) throws MdsException {
        return this.treeMakeTimestampedSegment(ctx, nid, timestamps, values, -1);
    }

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public final int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final Int64Array timestamps, final Descriptor_A values, final int idx) throws MdsException {
        return this.treeMakeTimestampedSegment(ctx, nid, timestamps, values, idx, timestamps.getLength());
    }

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public final int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final Int64Array timestamps, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeMakeTimestampedSegment(val(%d),ref($),xd($),val(%d),val(%d))", nid, idx, rows_filled), timestamps, values);
    }

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public final int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final long[] timestamps, final Descriptor_A values) throws MdsException {
        return this.treeMakeTimestampedSegment(ctx, nid, new Int64Array(timestamps), values);
    }

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public final int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final long[] timestamps, final Descriptor_A values, final int idx) throws MdsException {
        return this.treeMakeTimestampedSegment(ctx, nid, new Int64Array(timestamps), values, idx);
    }

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public final int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final long[] timestamps, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException {
        return this.treeMakeTimestampedSegment(ctx, nid, new Int64Array(timestamps), values, idx, rows_filled);
    }

    /**
     * open tree and in normal or read-only mode
     *
     * @return int: status
     **/
    public final int treeOpen(final Pointer ctx, final String expt, final int shot, final boolean readonly) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeOpen(ref($),val(%d),val(%d))", shot, readonly ? 1 : 0), new CString(expt));
    }

    /**
     * open tree in edit mode
     *
     * @return int: status
     **/
    public final int treeOpenEdit(final Pointer ctx, final String expt, final int shot) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeOpenEdit(ref($),val(%d))", shot), new CString(expt));
    }

    /**
     * create new tree and open it in edit mode
     *
     * @return int: status
     **/
    public final int treeOpenNew(final Pointer ctx, final String expt, final int shot) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeOpenNew(ref($),val(%d))", shot), new CString(expt));
    }

    /**
     * sets the record of a node (primitive)
     *
     * @return int: status
     **/
    public final int treePutRecord(final Pointer ctx, final int nid, final Descriptor dsc) throws MdsException {
        return this.treePutRecord(ctx, nid, dsc, 0);
    }

    /**
     * sets the record of a node
     *
     * @param utility_update
     *            2:compress
     *            !=0: no_write-flags are ignored and flags are reset to 0x5400
     * @return int: status
     **/
    public final int treePutRecord(final Pointer ctx, final int nid, final Descriptor dsc, final int utility_update) throws MdsException {
        if(dsc == null) return this.mds.getInteger(ctx, String.format("_s=TreeShr->TreePutRecord(val(%d),*,val(%d))", nid, utility_update));
        return this.mds.getInteger(ctx, String.format("TreeShr->TreePutRecord(val(%d),xd($),val(%d))", nid, utility_update), dsc);
    }

    /**
     * adds row to segmented node
     *
     * @return int: status
     **/
    public final int treePutRow(final Pointer ctx, final int nid, final int bufsize, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreePutRow(val(%d),val(%d),ref(%dQU),xd($))", nid, bufsize, timestamp), data);
    }

    /**
     * adds a segment to a segmented node
     *
     * @return int: status
     **/
    public final int treePutSegment(final Pointer ctx, final int nid, final int idx, final Descriptor_A data) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreePutSegment(val(%d),val(%d),xd($))", nid, idx), data);
    }

    /**
     * adds a time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public final int treePutTimestampedSegment(final Pointer ctx, final int nid, final long timestamp, final Descriptor_A data) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreePutTimestampedSegment(val(%d),ref(%dQU),xd($))", nid, timestamp), data);
    }

    /**
     * exits a tree (EDIT)
     *
     * @return int: status
     **/
    public final int treeQuitTree(final Pointer ctx, final String expt, final int shot) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeQuitTree(ref($),val(%d))", shot), new CString(expt));
    }

    /**
     * clears all tags assigned to a node (EDIT)
     *
     * @return int: status
     **/
    public final int treeRemoveNodesTags(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeRemoveNodesTags(val(%d))", nid));
    }

    /**
     * renames/moves a node (EDIT)
     *
     * @return int: status
     **/
    public final int treeRenameNode(final Pointer ctx, final int nid, final String name) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeRenameNode(val(%d),ref($))", nid), new CString(name));
    }

    /**
     * restores a saved context
     *
     * @return int: status; 1 on success, context if input is null
     **/
    public final int treeRestoreContext(final Pointer treectx) throws MdsException {
        return this.mds.getInteger("TreeShr->TreeRestoreContext(val($))", treectx);
    }

    /**
     * save the current context
     *
     * @return Pointer: pointer to saved context
     **/
    public final Pointer treeSaveContext() throws MdsException {
        return (Pointer)this.mds.getDescriptor("TreeShr->TreeSaveContext:P()", Pointer.class);
    }

    /**
     * sets the current shot of an experiment
     *
     * @return int: status
     **/
    public final int treeSetCurrentShotId(final String expt, final int shot) throws MdsException {
        return this.mds.getInteger(String.format("TreeShr->TreeSetCurrentShotId(ref($),val(%d))", shot), new CString(expt));
    }

    /**
     * makes a node the default node
     *
     * @return int: status
     **/
    public final int treeSetDefault(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeSetDefault(GETNCI(%d,'FULLPATH'))", nid));
    }

    /**
     * sets or resets the flags selected by a mask
     *
     * @return int: status
     **/
    public final int treeSetNciItm(final Pointer ctx, final int nid, final boolean state, final int flags) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeSetNciItm(val(%d),val(%d),val(%d))", nid, state ? 1 : 2, flags));
    }

    /**
     * removes subtree flag from a node (EDIT)
     *
     * @return int: status
     **/
    public final int treeSetNoSubtree(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeSetNoSubtree(val(%d))", nid));
    }

    /**
     * adds subtree flag to a node (EDIT)
     *
     * @return int: status
     **/
    public final int treeSetSubtree(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeSetSubtree(val(%d))", nid));
    }

    /**
     * clears current time context that will affect signal reads from a tree
     *
     * @return int: status
     **/
    public final int treeSetTimeContext(final Pointer ctx) throws MdsException {
        return this.treeSetTimeContext(ctx, (NUMBER)null, (NUMBER)null, (NUMBER)null);
    }

    /**
     * sets the current time context that will affect signal reads a a tree
     *
     * @return int: status
     **/
    public final int treeSetTimeContext(final Pointer ctx, final Number start, final Number end, final Number delta) throws MdsException {
        return this.treeSetTimeContext(ctx, NUMBER.make(start), NUMBER.make(end), NUMBER.make(delta));
    }

    /**
     * sets the current time context that will affect signal reads from a tree
     *
     * @return int: status
     **/
    public final int treeSetTimeContext(final Pointer ctx, final NUMBER start, final NUMBER end, final NUMBER delta) throws MdsException {
        final StringBuilder cmd = new StringBuilder(128).append("TreeShr->TreeSetTimeContext(");
        if(start == null || start.dtype == DTYPE.MISSING) cmd.append("val(0)");
        else cmd.append("descr(").append(start.decompile()).append(')');
        cmd.append(',');
        if(end == null || end.dtype == DTYPE.MISSING) cmd.append("val(0)");
        else cmd.append("descr(").append(end.decompile()).append(')');
        cmd.append(',');
        if(delta == null || delta.dtype == DTYPE.MISSING) cmd.append("val(0)");
        else cmd.append("descr(").append(delta.decompile()).append(')');
        return this.mds.getInteger(ctx, cmd.append(')').toString());
    }

    /**
     * sets/adds an extended attribute to a node
     *
     * @return int: status
     **/
    public final int treeSetXNci(final Pointer ctx, final int nid, final String name, final Descriptor value) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeSetXNci(val(%d),ref($),xd($))", nid), new CString(name), value.getData());
    }

    /**
     * initiates a conglomerate of nodes similar to devices (EDIT)
     *
     * @return int: status
     **/
    public final int treeStartConglomerate(final Pointer ctx, final int size) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeStartConglomerate(val(%d))", size));
    }

    /**
     * turns a node off (265392050 : TreeLock-Failure but does change the state)
     *
     * @return int: status
     **/
    public final int treeTurnOff(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeTurnOff(val(%d))", nid));
    }

    /**
     * turns a node on (265392050 : TreeLock-Failure but does change the state)
     *
     * @return int: status
     **/
    public final int treeTurnOn(final Pointer ctx, final int nid) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeTurnOn(val(%d))", nid));
    }

    /**
     * sets the current private context state
     *
     * @return boolean: previous state
     **/
    public final boolean treeUsePrivateCtx(final Pointer ctx, final boolean state) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeUsePrivateCtx(val(%d))", state ? 1 : 0)) == 1;
    }

    /**
     * checks for the current private context state
     *
     * @return boolean: true if private
     **/
    public final boolean treeUsingPrivateCtx(final Pointer ctx) throws MdsException {
        return this.mds.getInteger(ctx, "TreeShr->TreeUsingPrivateCtx()") == 1;
    }

    /**
     * writes changes to a tree (EDIT)
     *
     * @return int: status
     **/
    public final int treeWriteTree(final Pointer ctx, final String expt, final int shot) throws MdsException {
        return this.mds.getInteger(ctx, String.format("TreeShr->TreeWriteTree(ref($),val(%d))", shot), new CString(expt));
    }
}

package mds;

import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_a.Int64Array;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.NUMBER;
import mds.data.descriptor_s.Pointer;

public interface ITreeShr{
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

    /**
     * adds a device node of specific model to a tree (EDIT)
     *
     * @return IntegerStatus
     **/
    public IntegerStatus treeAddConglom(final Pointer ctx, final String path, String model) throws MdsException;

    /**
     * adds a node of specific usage to a tree (EDIT)
     *
     * @return IntegerStatus
     **/
    public IntegerStatus treeAddNode(final Pointer ctx, final String path, byte usage) throws MdsException;

    /**
     * adds a tag to a node
     *
     * @return int: status
     **/
    public int treeAddTag(final Pointer ctx, final int nid, String tag) throws MdsException;

    /**
     * initiates a new time-stamped segment
     *
     * @return int: status
     **/
    public int treeBeginTimestampedSegment(final Pointer ctx, final int nid, Descriptor_A initialValue, int idx) throws MdsException;

    /**
     * cleans the data file by removing unreferenced data
     *
     * @return int: status
     **/
    public int treeCleanDatafile(final String expt, int shot) throws MdsException;

    /**
     * closes a tree
     *
     * @return int: status
     **/
    public int treeClose(final Pointer ctx, final String expt, int shot) throws MdsException;

    /**
     * cleans and compresses the data file of a tree
     *
     * @return int: status
     **/
    public int treeCompressDatafile(final String expt, int shot) throws MdsException;

    /**
     * creates a copy of a tree with a new shot number
     *
     * @return int: status
     **/
    public int treeCreateTreeFiles(final String expt, int newshot, int fromshot) throws MdsException;

    /**
     * returns the current tree context
     *
     * @return Pointer: context
     **/
    public Pointer treeCtx(final Pointer ctx) throws MdsException;

    /**
     * executes the initiated delete operation
     *
     * @return int: status
     **/
    public int treeDeleteNodeExecute(final Pointer ctx) throws MdsException;

    /** @return IntegerStatus: nid-number of the next node in the list of nodes to be deleted **/
    public IntegerStatus treeDeleteNodeGetNid(final Pointer ctx, final int idx) throws MdsException;

    /**
     * initiates a delete operation of a node and its descendants (primitive)
     *
     * @return IntegerStatus
     **/
    public IntegerStatus treeDeleteNodeInitialize(final Pointer ctx, final int nid) throws MdsException;

    /**
     * initiates a delete operation of a node and its descendants
     *
     * @return IntegerStatus
     **/
    public IntegerStatus treeDeleteNodeInitialize(final Pointer ctx, final int nid, int count, boolean init) throws MdsException;

    /**
     * finishes the added conglomerate
     *
     * @return IntegerStatus
     **/
    public int treeEndConglomerate(final Pointer ctx) throws MdsException;

    /**
     * searches for the next tag assigned to a node
     *
     * @return TagRef: next tag found
     **/
    public TagRef treeFindNodeTags(final Pointer ctx, int nid, TagRef ref) throws MdsException;

    /**
     * searches for the next node meeting the criteria of a matching search string and usage mask
     *
     * @return NodeRefStatus: next nid-number found
     **/
    public NodeRefStatus treeFindNodeWild(final Pointer ctx, final String searchstr, final int usage_mask, final NodeRefStatus ref) throws MdsException;

    /**
     * searches for the next tag in the tag list matching a search string
     *
     * @return TagRefStatus: next tag found
     **/
    public TagRefStatus treeFindTagWild(final Pointer ctx, final String searchstr, final TagRefStatus ref) throws MdsException;

    /**
     * resolves the current shot number (0)
     *
     * @return int: shot number
     **/
    public int treeGetCurrentShotId(final String expt) throws MdsException;

    /**
     * checks the size of the data file
     *
     * @return long: file size in byte
     **/
    public long treeGetDatafileSize(final Pointer ctx) throws MdsException;

    /**
     * checks the default node of active tree
     *
     * @return IntegerStatus: nid-number of default node
     **/
    public IntegerStatus treeGetDefaultNid(final Pointer ctx) throws MdsException;

    /**
     * returns the shortest path to node (from default node)
     *
     * @return String: minimal path to node
     **/
    public String treeGetMinimumPath(final Pointer ctx, final int nid) throws MdsException;

    /**
     * @return IntegerStatus: number of segments
     **/
    public IntegerStatus treeGetNumSegments(final Pointer ctx, final int nid) throws MdsException;

    /**
     * returns the natural path of a node (form its parent tree root)
     *
     * @return String: path to node
     **/
    public String treeGetPath(final Pointer ctx, final int nid) throws MdsException;

    /**
     * reads the full record of a node
     *
     * @return DescriptorStatus: record of node
     **/
    public DescriptorStatus treeGetRecord(final Pointer ctx, final int nid) throws MdsException;

    /**
     * reads idx-th segment of a node
     *
     * @return SignalStatus: segment
     **/
    public SignalStatus treeGetSegment(final Pointer ctx, final int nid, int idx) throws MdsException;

    /**
     * @return SegmentInfo: info about the idx-th segment of a node
     **/
    public SegmentInfo treeGetSegmentInfo(final Pointer ctx, final int nid, int idx) throws MdsException;

    /**
     * @return DescriptorStatus: time limits of the idx-th segment of a node
     **/
    public DescriptorStatus treeGetSegmentLimits(final Pointer ctx, final int nid, int idx) throws MdsException;

    /**
     * reads the list of attributes of a node
     *
     * @return int: status
     **/
    public DescriptorStatus treeGetXNci(final Pointer ctx, final int nid) throws MdsException;

    /**
     * reads an extended attribute of a node
     *
     * @return int: status
     **/
    public DescriptorStatus treeGetXNci(final Pointer ctx, final int nid, String name) throws MdsException;

    /**
     * checks the state of a node
     *
     * @return int: status
     **/
    public int treeIsOn(final Pointer ctx, final int nid) throws MdsException;

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final Int64Array timestamps, final Descriptor_A values) throws MdsException;

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final Int64Array timestamps, final Descriptor_A values, final int idx) throws MdsException;

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final Int64Array timestamps, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException;

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final long[] timestamps, final Descriptor_A values) throws MdsException;

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final long[] timestamps, final Descriptor_A values, final int idx) throws MdsException;

    /**
     * adds a new time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public int treeMakeTimestampedSegment(final Pointer ctx, final int nid, final long[] timestamps, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException;

    /**
     * open tree and in normal or read-only mode
     *
     * @return int: status
     **/
    public int treeOpen(final Pointer ctx, final String expt, final int shot, final boolean readonly) throws MdsException;

    /**
     * open tree in edit mode
     *
     * @return int: status
     **/
    public int treeOpenEdit(final Pointer ctx, final String expt, final int shot) throws MdsException;

    /**
     * create new tree and open it in edit mode
     *
     * @return int: status
     **/
    public int treeOpenNew(final Pointer ctx, final String expt, final int shot) throws MdsException;

    /**
     * sets the record of a node (primitive)
     *
     * @return int: status
     **/
    public int treePutRecord(final Pointer ctx, final int nid, final Descriptor dsc) throws MdsException;

    /**
     * sets the record of a node
     *
     * @param utility_update
     *            2:compress
     *            !=0: no_write-flags are ignored and flags are reset to 0x5400
     * @return int: status
     **/
    public int treePutRecord(final Pointer ctx, final int nid, final Descriptor dsc, final int utility_update) throws MdsException;

    /**
     * adds row to segmented node
     *
     * @return int: status
     **/
    public int treePutRow(final Pointer ctx, final int nid, final int bufsize, final long timestamp, final Descriptor_A data) throws MdsException;

    /**
     * adds a segment to a segmented node
     *
     * @return int: status
     **/
    public int treePutSegment(final Pointer ctx, final int nid, final int idx, final Descriptor_A data) throws MdsException;

    /**
     * adds a time-stamped segment to segmented node
     *
     * @return int: status
     **/
    public int treePutTimestampedSegment(final Pointer ctx, final int nid, final long timestamp, final Descriptor_A data) throws MdsException;

    /**
     * exits the ctx tree (EDIT)
     *
     * @return int: status
     **/
    public int treeQuitTree(final Pointer ctx) throws MdsException;

    /**
     * exits a tree (EDIT)
     *
     * @return int: status
     **/
    public int treeQuitTree(final String expt, final int shot) throws MdsException;

    /**
     * clears all tags assigned to a node (EDIT)
     *
     * @return int: status
     **/
    public int treeRemoveNodesTags(final Pointer ctx, final int nid) throws MdsException;

    /**
     * renames/moves a node (EDIT)
     *
     * @return int: status
     **/
    public int treeRenameNode(final Pointer ctx, final int nid, final String path) throws MdsException;

    /**
     * restores a saved context
     *
     * @return int: status; 1 on success, context if input is null
     **/
    public int treeRestoreContext(final Pointer treectx) throws MdsException;

    /**
     * save the current context
     *
     * @return Pointer: pointer to saved context
     **/
    public Pointer treeSaveContext() throws MdsException;

    /**
     * sets the current shot of an experiment
     *
     * @return int: status
     **/
    public int treeSetCurrentShotId(final String expt, final int shot) throws MdsException;

    /**
     * makes a node the default node
     *
     * @return int: status
     **/
    public int treeSetDefault(final Pointer ctx, final int nid) throws MdsException;

    /**
     * sets or resets the flags selected by a mask
     *
     * @return int: status
     **/
    public int treeSetNciItm(final Pointer ctx, final int nid, final boolean state, final int flags) throws MdsException;

    /**
     * removes subtree flag from a node (EDIT)
     *
     * @return int: status
     **/
    public int treeSetNoSubtree(final Pointer ctx, final int nid) throws MdsException;

    /**
     * adds subtree flag to a node (EDIT)
     *
     * @return int: status
     **/
    public int treeSetSubtree(final Pointer ctx, final int nid) throws MdsException;

    /**
     * clears current time context that will affect signal reads from a tree
     *
     * @return int: status
     **/
    public int treeSetTimeContext(final Pointer ctx) throws MdsException;

    /**
     * sets the current time context that will affect signal reads a a tree
     *
     * @return int: status
     **/
    public int treeSetTimeContext(final Pointer ctx, final Number start, Number end, Number delta) throws MdsException;

    /**
     * sets the current time context that will affect signal reads from a tree
     *
     * @return int: status
     **/
    public int treeSetTimeContext(final Pointer ctx, final NUMBER start, NUMBER end, NUMBER delta) throws MdsException;

    /**
     * sets/adds an extended attribute to a node
     *
     * @return int: status
     **/
    public int treeSetXNci(final Pointer ctx, final int nid, String name, Descriptor value) throws MdsException;

    /**
     * initiates a conglomerate of nodes similar to devices (EDIT)
     *
     * @return int: status
     **/
    public int treeStartConglomerate(final Pointer ctx, final int size) throws MdsException;

    /**
     * turns a node off (265392050 : TreeLock-Failure but does change the state)
     *
     * @return int: status
     **/
    public int treeTurnOff(final Pointer ctx, final int nid) throws MdsException;

    /**
     * turns a node on (265392050 : TreeLock-Failure but does change the state)
     *
     * @return int: status
     **/
    public int treeTurnOn(final Pointer ctx, final int nid) throws MdsException;

    /**
     * sets the current private context state
     *
     * @return boolean: previous state
     **/
    public boolean treeUsePrivateCtx(final Pointer ctx, final boolean state) throws MdsException;

    /**
     * checks for the current private context state
     *
     * @return boolean: true if private
     **/
    public boolean treeUsingPrivateCtx(final Pointer ctx) throws MdsException;

    /**
     * writes changes to the ctx tree (EDIT)
     *
     * @return int: status
     **/
    public int treeWriteTree(final Pointer ctx) throws MdsException;

    /**
     * writes changes to a tree (EDIT)
     *
     * @return int: status
     **/
    public int treeWriteTree(final Pointer ctx, final String expt, int shot) throws MdsException;
}

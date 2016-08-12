package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.TCL;
import mds.TREE;
import mds.TreeShr.SegmentInfo;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_a.NidArray;
import mds.data.descriptor_r.Signal;

public abstract class NODE<T>extends Descriptor_S<T>{
    public static final class Flags{
        public static final int STATE             = 1 << 0;
        public static final int PARENT_STATE      = 1 << 1;
        public static final int ESSENTIAL         = 1 << 2;
        public static final int CACHED            = 1 << 3;
        public static final int VERSION           = 1 << 4;
        public static final int SEGMENTED         = 1 << 5;
        public static final int SETUP             = 1 << 6;
        public static final int WRITE_ONCE        = 1 << 7;
        public static final int COMPRESSIBLE      = 1 << 8;
        public static final int DO_NOT_COMPRESS   = 1 << 9;
        public static final int COMPRESS_ON_PUT   = 1 << 10;
        public static final int NO_WRITE_MODEL    = 1 << 11;
        public static final int NO_WRITE_SHOT     = 1 << 12;
        public static final int PATH_REFERENCE    = 1 << 13;
        public static final int NID_REFERENCE     = 1 << 14;
        public static final int INCLUDE_IN_PULSE  = 1 << 15;
        public static final int COMPRESS_SEGMENTS = 1 << 16;
        public static final int ERROR             = 1 << 31;
        public final int        flags;

        public Flags(){
            this(Integer.MIN_VALUE);
        }

        public Flags(final int flags){
            this.flags = flags;
        }

        public final boolean isCached() {
            return (this.flags & Flags.CACHED) != 0;
        }

        public final boolean isCompressible() {
            return (this.flags & Flags.COMPRESSIBLE) != 0;
        }

        public final boolean isCompressOnPut() {
            return (this.flags & Flags.COMPRESS_ON_PUT) != 0;
        }

        public final boolean isCompressSegments() {
            return (this.flags & Flags.COMPRESS_SEGMENTS) != 0;
        }

        public final boolean isDoNotCompress() {
            return (this.flags & Flags.DO_NOT_COMPRESS) != 0;
        }

        public final boolean isError() {
            return this.flags < 0;
        }

        public final boolean isEssential() {
            return (this.flags & Flags.ESSENTIAL) != 0;
        }

        public final boolean isIncludeInPulse() {
            return (this.flags & Flags.INCLUDE_IN_PULSE) != 0;
        }

        public final boolean isNidReference() {
            return (this.flags & Flags.NID_REFERENCE) != 0;
        }

        public final boolean isNoWriteModel() {
            return (this.flags & Flags.NO_WRITE_MODEL) != 0;
        }

        public final boolean isNoWriteShot() {
            return (this.flags & Flags.NO_WRITE_SHOT) != 0;
        }

        public final boolean isOn() {
            return !this.isState();
        }

        public final boolean isParentOn() {
            return !this.isParentState();
        }

        public final boolean isParentState() {
            return (this.flags & Flags.PARENT_STATE) != 0;
        }

        public final boolean isPathReference() {
            return (this.flags & Flags.PATH_REFERENCE) != 0;
        }

        public final boolean isSegmented() {
            return (this.flags & Flags.SEGMENTED) != 0;
        }

        public final boolean isSetup() {
            return (this.flags & Flags.SETUP) != 0;
        }

        public final boolean isState() {
            return (this.flags & Flags.STATE) != 0;
        }

        public final boolean isVersion() {
            return (this.flags & Flags.VERSION) != 0;
        }

        public final boolean isWriteOnce() {
            return (this.flags & Flags.WRITE_ONCE) != 0;
        }
    }
    public static final byte     USAGE_MAXIMUM       = 12;
    public static final byte     USAGE_ANY           = 0;
    public static final byte     USAGE_STRUCTURE     = 1;
    public static final byte     USAGE_ACTION        = 2;
    public static final byte     USAGE_DEVICE        = 3;
    public static final byte     USAGE_DISPATCH      = 4;
    public static final byte     USAGE_NUMERIC       = 5;
    public static final byte     USAGE_SIGNAL        = 6;
    public static final byte     USAGE_TASK          = 7;
    public static final byte     USAGE_TEXT          = 8;
    public static final byte     USAGE_WINDOW        = 9;
    public static final byte     USAGE_AXIS          = 10;
    public static final byte     USAGE_SUBTREE       = 11;
    public static final byte     USAGE_COMPOUND_DATA = 12;
    public static final int      CHILD               = 1;
    public static final int      MEMBER              = 2;
    private static final boolean atomic              = false;
    protected final TREE         tree;

    public NODE(final byte dtype, final ByteBuffer data){
        this(dtype, data, TREE.getActiveTree());
    }

    public NODE(final byte dtype, final ByteBuffer data, final TREE tree){
        super(dtype, data);
        this.tree = tree;
    }

    public NODE(final ByteBuffer b){
        this(b, TREE.getActiveTree());
    }

    public NODE(final ByteBuffer b, final TREE tree){
        super(b);
        this.tree = tree;
    }

    public final Nid addConglom(final String name, final String model) throws MdsException {
        return this.tree.addConglom(this, name, model);
    }

    public final Nid addNode(final String name, final byte usage) throws MdsException {
        return this.tree.addNode(this, name, usage);
    }

    public final NODE addTag(final String tag) throws MdsException {
        this.tree.addTag(this.getNidNumber(), tag);
        return this;
    }

    public final NODE clearFlags(final int flags) throws MdsException {
        this.tree.clearFlags(this.getNidNumber(), flags);
        return this;
    }

    public final NODE clearTags() throws MdsException {
        this.tree.clearTags(this.getNidNumber());
        return this;
    }

    public int deleteInitialize() throws MdsException {
        return this.tree.deleteNodeInitialize(this.getNidNumber());
    }

    public final NODE doAction() throws MdsException {
        this.tree.doAction(this.getNidNumber());
        return this;
    }

    public final NODE doDeviceMethod(final String method) throws MdsException {
        return this.doDeviceMethod(method, null);
    }

    public final NODE doDeviceMethod(final String method, final String arg) throws MdsException {
        new TCL(this.tree.mds).doMethod(this.getNciPath(), method, arg, true);
        return this;
    }

    public final NODE followReference() throws MdsException {
        final byte dtype = this.getNciDType();
        if(dtype == DTYPE.NID || dtype == DTYPE.PATH) return ((NODE)this.getNciRecord()).followReference();
        return this;
    }

    public final Descriptor getNci(final String name) throws MdsException {
        return this.tree.getNci(this, name);
    }

    public final Nid getNciBrother() throws MdsException {
        return (Nid)this.getNci(TREE.NCI_BROTHER);
    }

    public final Nid getNciChild() throws MdsException {
        return (Nid)this.getNci(TREE.NCI_CHILD);
    }

    public final NidArray getNciChildrenNids() throws MdsException {
        return this.tree.getNciChildrenNids(this);
    }

    public final byte getNciClass() throws MdsException {
        return this.getNci(TREE.NCI_CLASS).toByte();
    }

    public final String getNciClassStr() throws MdsException {
        return this.getNci(TREE.NCI_CLASS_STR).toString();
    }

    public final short getNciConglomerateElt() throws MdsException {
        return this.getNci(TREE.NCI_CONGLOMERATE_ELT).toShort();
    }

    public final NidArray getNciConglomerateNids() throws MdsException {
        return (NidArray)this.getNci(TREE.NCI_CONGLOMERATE_NIDS);
    }

    public final int getNciDataInNci() throws MdsException {
        return this.getNci(TREE.NCI_DATA_IN_NCI).toInt();
    }

    public final int getNciDepth() throws MdsException {
        return this.getNci(TREE.NCI_DEPTH).toInt();
    }

    public final byte getNciDType() throws MdsException {
        return this.getNci(TREE.NCI_DTYPE).toByte();
    }

    public final String getNciDTypeStr() throws MdsException {
        return this.getNci(TREE.NCI_DTYPE_STR).toString();
    }

    public final int getNciErrorOnPut() throws MdsException {
        return this.getNci(TREE.NCI_ERROR_ON_PUT).toInt();
    }

    public final int getNciFlags() throws MdsException {
        return this.getNci(TREE.NCI_GET_FLAGS).toInt();
    }

    public final String getNciFullPath() throws MdsException {
        return this.getNci(TREE.NCI_FULLPATH).toString();
    }

    public final int getNciIOStatus() throws MdsException {
        return this.getNci(TREE.NCI_IO_STATUS).toInt();
    }

    public final int getNciIOStv() throws MdsException {
        return this.getNci(TREE.NCI_IO_STV).toInt();
    }

    public final boolean getNciIsChild() throws MdsException {
        return this.getNci(TREE.NCI_IS_CHILD).toByte() != 0;
    }

    public final boolean getNciIsMember() throws MdsException {
        return this.getNci(TREE.NCI_IS_MEMBER).toByte() != 0;
    }

    public final int getNciLength() throws MdsException {
        return this.getNci(TREE.NCI_LENGTH).toInt();
    }

    public final Nid getNciMember() throws MdsException {
        return (Nid)this.getNci(TREE.NCI_MEMBER);
    }

    public final NidArray getNciMemberNids() throws MdsException {
        return this.tree.getNciMemberNids(this);
    }

    public final String getNciMinPath() throws MdsException {
        return this.getNci(TREE.NCI_MINPATH).toString();
    }

    public final int getNciNidNumber() throws MdsException {
        return this.getNci(TREE.NCI_NID_NUMBER).toInt();
    }

    public final String getNciNodeName() throws MdsException {
        return this.getNci(TREE.NCI_NODE_NAME).toString().trim();
    }

    public final int getNciNumberOfChildren() throws MdsException {
        return this.getNci(TREE.NCI_NUMBER_OF_CHILDREN).toInt();
    }

    public final int getNciNumberOfElts() throws MdsException {
        return this.getNci(TREE.NCI_NUMBER_OF_ELTS).toInt();
    }

    public final int getNciNumberOfMembers() throws MdsException {
        return this.getNci(TREE.NCI_NUMBER_OF_MEMBERS).toInt();
    }

    public final String getNciOriginalPartName() throws MdsException {
        return this.getNci(TREE.NCI_ORIGINAL_PART_NAME).toString().trim();
    }

    public final int getNciOwnerId() throws MdsException {
        return this.getNci(TREE.NCI_OWNER_ID).toInt();
    }

    public final Nid getNciParent() throws MdsException {
        return (Nid)this.getNci(TREE.NCI_PARENT);
    }

    public final int getNciParentRelationship() throws MdsException {
        return this.getNci(TREE.NCI_PARENT_RELATIONSHIP).toInt();
    }

    public final String getNciParentTree() throws MdsException {
        return this.getNci(TREE.NCI_PARENT_TREE).toString();
    }

    public final String getNciPath() throws MdsException {
        return this.getNci(TREE.NCI_PATH).toString();
    }

    public final Descriptor getNciRecord() throws MdsException {
        return this.getNci(TREE.NCI_RECORD);
    }

    public final long getNciRfa() throws MdsException {
        return this.getNci("RFA").toLong();
    }

    public final int getNciRLength() throws MdsException {
        return this.getNci(TREE.NCI_RLENGTH).toInt();
    }

    public final boolean getNciStatus() throws MdsException {
        return this.getNci(TREE.NCI_STATUS).toByte() != 0;
    }

    public final long getNciTimeInserted() throws MdsException {
        return this.getNci(TREE.NCI_TIME_INSERTED).toLong();
    }

    public final String getNciTimeInsertedStr() throws MdsException {
        return this.tree.getNciTimeInsertedStr(this);
    }

    public final byte getNciUsage() throws MdsException {
        return this.getNci(TREE.NCI_USAGE).toByte();
    }

    public final String getNciUsageStr() throws MdsException {
        return this.getNci(TREE.NCI_USAGE_STR).toString();
    }

    public final int getNciVersion() throws MdsException {
        return this.getNci(TREE.NCI_VERSION).toInt();
    }

    public abstract int getNidNumber() throws MdsException;

    public final int getNumSegments() throws MdsException {
        return this.tree.getNumSegments(this.getNidNumber());
    }

    public final Descriptor getRecord() throws MdsException {
        return this.tree.getRecord(this.getNidNumber());
    }

    public final Signal getSegment(final int idx) throws MdsException {
        return this.tree.getSegment(this.getNidNumber(), idx);
    }

    public final SegmentInfo getSegmentInfo(final int idx) throws MdsException {
        return this.tree.getSegmentInfo(this.getNidNumber(), idx);
    }

    public final Descriptor getSegmentLimits(final int idx) throws MdsException {
        return this.tree.getSegmentLimits(this.getNidNumber(), idx);
    }

    public final String[] getTags() throws MdsException {
        return this.tree.getTags(this.getNidNumber());
    }

    public final TREE getTree() {
        return this.tree;
    }

    public final Descriptor getXNci(final String name) throws MdsException {
        return this.tree.getXNci(this.getNidNumber(), name);
    }

    @Override
    public final boolean isAtomic() {
        return NODE.atomic;
    }

    public final boolean isNidReference() throws MdsException {
        return new Flags(this.getNciFlags()).isNidReference();
    }

    public final boolean isOn() throws MdsException {
        return new Flags(this.getNciFlags()).isOn();
    }

    public final boolean isPathReference() throws MdsException {
        return new Flags(this.getNciFlags()).isPathReference();
    }

    public final boolean isSegmented() throws MdsException {
        synchronized(this.tree.mds){
            if(new Flags(this.getNciFlags()).isSegmented()) return true; // cannot be sure due to issue in winter 2015/2016
            return this.getNumSegments() > 0;
        }
    }

    public final NODE makeSegment(final Descriptor start, final Descriptor end, final Descriptor dimension, final Descriptor_A values, final int idx, final int rows_filled) throws MdsException {
        this.tree.makeSegment(this.getNidNumber(), start, end, dimension, values, idx, rows_filled);
        return this;
    }

    public final NODE makeSegment(final Descriptor_A dimension, final Descriptor_A values) throws MdsException {
        this.tree.makeSegment(this.getNidNumber(), dimension.getScalar(0), dimension.getScalar(dimension.getLength() - 1), dimension, values, -1, dimension.getLength());
        return this;
    }

    public final NODE putRecord(final Descriptor data) throws MdsException {
        this.tree.putRecord(this.getNidNumber(), data);
        return this;
    }

    public final NODE putRow(final long time, final Descriptor_A data) throws MdsException {
        this.tree.putRow(this.getNidNumber(), time, data);
        return this;
    }

    public final NODE putSegment(final int idx, final Descriptor_A data) throws MdsException {
        this.tree.putSegment(this.getNidNumber(), idx, data);
        return this;
    }

    public final NODE putTimestampedSegment(final int nid, final long timestamp, final Descriptor_A data) throws MdsException {
        this.tree.putTimestampedSegment(this.getNidNumber(), timestamp, data);
        return this;
    }

    public final NODE setDefault() throws MdsException {
        this.tree.setDefault(this.getNidNumber());
        return this;
    }

    public final NODE setFlags(final int flags) throws MdsException {
        this.tree.setFlags(this.getNidNumber(), flags);
        return this;
    }

    public final NODE setOn(final boolean state) throws MdsException {
        if(state) this.tree.turnOn(this.getNidNumber());
        else this.tree.turnOff(this.getNidNumber());
        return this;
    }

    public final NODE setPath(final String path) throws MdsException {
        this.tree.setPath(this.getNidNumber(), path);
        return this;
    }

    public final NODE setSubtree() throws MdsException {
        this.tree.setSubtree(this.getNidNumber());
        return this;
    }

    public final NODE setTags(final String... tags) throws MdsException {
        this.tree.setTags(this.getNidNumber(), tags);
        return this;
    }

    @Override
    public final byte[] toByteArray() {
        try{
            return this.getRecord().toByteArray();
        }catch(final MdsException e){
            return null;
        }
    }

    @Override
    public final double[] toDoubleArray() {
        try{
            return this.getRecord().toDoubleArray();
        }catch(final MdsException e){
            return null;
        }
    }

    @Override
    public final float[] toFloatArray() {
        try{
            return this.getRecord().toFloatArray();
        }catch(final MdsException e){
            return null;
        }
    }

    public abstract Path toFullPath() throws MdsException;

    @Override
    public final int[] toIntArray() {
        try{
            return this.getRecord().toIntArray();
        }catch(final MdsException e){
            return null;
        }
    }

    @Override
    public final long[] toLongArray() {
        try{
            return this.getRecord().toLongArray();
        }catch(final MdsException e){
            return null;
        }
    }

    public abstract Path toMinPath() throws MdsException;

    public abstract Nid toNid() throws MdsException;

    public abstract Path toPath() throws MdsException;

    @Override
    public final short[] toShortArray() {
        try{
            return this.getRecord().toShortArray();
        }catch(final MdsException e){
            return null;
        }
    }
}

package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.TREE;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_a.NidArray;
import mds.data.descriptor_r.Signal;

public abstract class TREENODE<T>extends Descriptor_S<T>{
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
    public final TREE            tree;

    public TREENODE(final byte dtype, final ByteBuffer data){
        this(dtype, data, TREE.getActiveTree());
    }

    public TREENODE(final byte dtype, final ByteBuffer data, final TREE tree){
        super(dtype, data);
        this.tree = tree;
    }

    public TREENODE(final ByteBuffer b){
        this(b, TREE.getActiveTree());
    }

    public TREENODE(final ByteBuffer b, final TREE tree){
        super(b);
        this.tree = tree;
    }

    public final void addTag(final String tag) throws MdsException {
        this.tree.addTag(this.getNidNumber(), tag);
    }

    public final void clearFlags(final int flags) throws MdsException {
        this.tree.clearFlags(this.getNidNumber(), flags);
    }

    public final void clearTags() throws MdsException {
        MdsException.handleStatus(this.tree.treeshr.treeRemoveNodesTags(this.tree.getContext(), this.getNidNumber()));
    }

    public final void doAction() throws MdsException {
        this.tree.doAction(this.getNidNumber());
    }

    public final void doDeviceMethod(final String name) throws MdsException {
        this.tree.doDeviceMethod(this.getNidNumber(), name);
    }

    public final TREENODE followReference() throws MdsException {
        final byte dtype = this.getNciDType();
        if(dtype == DTYPE.NID || dtype == DTYPE.PATH) return ((TREENODE)this.getNciRecord()).followReference();
        return this;
    }

    public final Nid getNciBrother() throws MdsException {
        return this.tree.getNciBrother(this.getNidNumber());
    }

    public final Nid getNciChild() throws MdsException {
        return this.tree.getNciChild(this.getNidNumber());
    }

    public final Descriptor getNciChild(final String name) throws MdsException {
        return this.tree.connection.getDescriptor("GETNCI($,$)", this, new CString(name));
    }

    public final NidArray getNciChildrenNids() throws MdsException {
        return this.tree.getNciChildrenNids(this.getNidNumber());
    }

    public final byte getNciClass() throws MdsException {
        return this.tree.getNciClass(this.getNidNumber());
    }

    public final String getNciClassStr() throws MdsException {
        return this.tree.getNciClassStr(this.getNidNumber());
    }

    public final short getNciConglomerateElt() throws MdsException {
        return this.tree.getNciConglomerateElt(this.getNidNumber());
    }

    public final NidArray getNciConglomerateNids() throws MdsException {
        return this.tree.getNciConglomerateNids(this.getNidNumber());
    }

    public final int getNciDataInNci() throws MdsException {
        return this.tree.getNciDataInNci(this.getNidNumber());
    }

    public final int getNciDepth() throws MdsException {
        return this.tree.getNciDepth(this.getNidNumber());
    }

    public final byte getNciDType() throws MdsException {
        return this.tree.getNciDType(this.getNidNumber());
    }

    public final String getNciDTypeStr() throws MdsException {
        return this.tree.getNciDTypeStr(this.getNidNumber());
    }

    public final int getNciErrorOnPut() throws MdsException {
        return this.tree.getNciErrorOnPut(this.getNidNumber());
    }

    public final int getNciFlags() throws MdsException {
        return this.tree.getNciFlags(this.getNidNumber());
    }

    public final String getNciFullPath() throws MdsException {
        return this.tree.getNciFullPath(this.getNidNumber());
    }

    public final int getNciIOStatus() throws MdsException {
        return this.tree.getNciIOStatus(this.getNidNumber());
    }

    public final int getNciIOStv() throws MdsException {
        return this.tree.getNciIOStv(this.getNidNumber());
    }

    public final boolean getNciIsChild() throws MdsException {
        return this.tree.getNciIsChild(this.getNidNumber());
    }

    public final boolean getNciIsMember() throws MdsException {
        return this.tree.getNciIsMember(this.getNidNumber());
    }

    public final int getNciLength() throws MdsException {
        return this.tree.getNciLength(this.getNidNumber());
    }

    public final Nid getNciMember() throws MdsException {
        return this.tree.getNciMember(this.getNidNumber());
    }

    public final NidArray getNciMemberNids() throws MdsException {
        return this.tree.getNciMemberNids(this.getNidNumber());
    }

    public final String getNciMinPath() throws MdsException {
        return this.tree.getNciMinPath(this.getNidNumber());
    }

    public final int getNciNidNumber() throws MdsException {
        return this.tree.getNciNidNumber(this.getNidNumber());
    }

    public final String getNciNodeName() throws MdsException {
        return this.tree.getNciNodeName(this.getNidNumber());
    }

    public final int getNciNumberOfChildren() throws MdsException {
        return this.tree.getNciNumberOfChildren(this.getNidNumber());
    }

    public final int getNciNumberOfElts() throws MdsException {
        return this.tree.getNciNumberOfElts(this.getNidNumber());
    }

    public final int getNciNumberOfMembers() throws MdsException {
        return this.tree.getNciNumberOfMembers(this.getNidNumber());
    }

    public final String getNciOriginalPartName() throws MdsException {
        return this.tree.getNciOriginalPartName(this.getNidNumber());
    }

    public final int getNciOwnerId() throws MdsException {
        return this.tree.getNciOwnerId(this.getNidNumber());
    }

    public final Nid getNciParent() throws MdsException {
        return this.tree.getNciParent(this.getNidNumber());
    }

    public final int getNciParentRelationship() throws MdsException {
        return this.tree.getNciParentRelationship(this.getNidNumber());
    }

    public final String getNciParentTree() throws MdsException {
        return this.tree.getNciParentTree(this.getNidNumber());
    }

    public final String getNciPath() throws MdsException {
        return this.tree.getNciPath(this.getNidNumber());
    }

    public final Descriptor getNciRecord() throws MdsException {
        return this.tree.getNciRecord(this.getNidNumber());
    }

    public final long getNciRfa() throws MdsException {
        return this.tree.getNciRfa(this.getNidNumber());
    }

    public final int getNciRLength() throws MdsException {
        return this.tree.getNciRLength(this.getNidNumber());
    }

    public final boolean getNciStatus() throws MdsException {
        return this.tree.getNciStatus(this.getNidNumber());
    }

    public final long getNciTimeInserted() throws MdsException {
        return this.tree.getNciTimeInserted(this.getNidNumber());
    }

    public final String getNciTimeInsertedStr() throws MdsException {
        return this.tree.getNciTimeInsertedStr(this.getNidNumber());
    }

    public final byte getNciUsage() throws MdsException {
        return this.tree.getNciUsage(this.getNidNumber());
    }

    public final String getNciUsageStr() throws MdsException {
        return this.tree.getNciUsageStr(this.getNidNumber());
    }

    public final int getNciVersion() throws MdsException {
        return this.tree.getNciVersion(this.getNidNumber());
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
        return TREENODE.atomic;
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
        if(new Flags(this.getNciFlags()).isSegmented()) return true; // cannot be sure due to issue in winter 2015/2016
        return this.getNumSegments() > 0;
    }

    public final void putRecord(final Descriptor data) throws MdsException {
        this.tree.putRecord(this.getNidNumber(), data);
    }

    public final void putRow(final long time, final Descriptor_A data) throws MdsException {
        this.tree.putRow(this.getNidNumber(), time, data);
    }

    public final void setDefault() throws MdsException {
        this.tree.setDefault(this.getNidNumber());
    }

    public final void setFlags(final int flags) throws MdsException {
        this.tree.setFlags(this.getNidNumber(), flags);
    }

    public final void setOn(final boolean state) throws MdsException {
        if(state) this.tree.turnOn(this.getNidNumber());
        else this.tree.turnOff(this.getNidNumber());
    }

    public final void setPath(final String path) throws MdsException {
        this.tree.setPath(this.getNidNumber(), path);
    }

    public final void setSubtree() throws MdsException {
        this.tree.setSubtree(this.getNidNumber());
    }

    public final void setTags(final String[] tags) throws MdsException {
        this.tree.setTags(this.getNidNumber(), tags);
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

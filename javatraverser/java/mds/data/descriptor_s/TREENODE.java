package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.TreeShr;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_a.NidArray;
import mds.data.descriptor_r.Signal;
import mds.mdsip.Connection;

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
    public static final byte   USAGE_MAXIMUM       = 12;
    public static final byte   USAGE_ANY           = 0;
    public static final byte   USAGE_STRUCTURE     = 1;
    public static final byte   USAGE_ACTION        = 2;
    public static final byte   USAGE_DEVICE        = 3;
    public static final byte   USAGE_DISPATCH      = 4;
    public static final byte   USAGE_NUMERIC       = 5;
    public static final byte   USAGE_SIGNAL        = 6;
    public static final byte   USAGE_TASK          = 7;
    public static final byte   USAGE_TEXT          = 8;
    public static final byte   USAGE_WINDOW        = 9;
    public static final byte   USAGE_AXIS          = 10;
    public static final byte   USAGE_SUBTREE       = 11;
    public static final byte   USAGE_COMPOUND_DATA = 12;
    public static final int    CHILD               = 1;
    public static final int    MEMBER              = 2;
    protected final Connection connection;
    protected final TreeShr    treeshr;

    public TREENODE(final byte dtype, final ByteBuffer data){
        this(dtype, data, Connection.getActiveConnection());
    }

    public TREENODE(final byte dtype, final ByteBuffer data, final Connection connection){
        super(dtype, data);
        this.connection = connection;
        this.treeshr = new TreeShr(connection);
    }

    public TREENODE(final ByteBuffer b){
        this(b, Connection.getActiveConnection());
    }

    public TREENODE(final ByteBuffer b, final Connection connection){
        super(b);
        this.connection = connection;
        this.treeshr = new TreeShr(connection);
    }

    public final int addTag(final String tag) throws MdsException {
        return this.treeshr.treeAddTag(this.getNidNumber(), tag);
    }

    public final int clearFlags(final int flags) throws MdsException {
        return this.treeshr.treeSetNciItm(this.getNidNumber(), false, flags & 0x7FFFFFFC);
    }

    public final int clearTags() throws MdsException {
        return this.treeshr.treeRemoveNodesTags(this.getNidNumber());
    }

    public final Connection getConnection() {
        return this.connection;
    }

    public final Descriptor getNci(final String name) throws MdsException {
        return this.connection.getDescriptor("GETNCI($,$)", this, new CString(name));
    }

    public final Nid getNciBrother() throws MdsException {
        return (Nid)this.getNci("BROTHER");
    }

    public final Nid getNciChild() throws MdsException {
        return (Nid)this.getNci("CHILD");
    }

    public final NidArray getNciChildrenNids() throws MdsException {
        if(this.getNciNumberOfChildren() == 0) return new NidArray();
        return (NidArray)this.getNci("CHILDREN_NIDS");
    }

    public final byte getNciClass() throws MdsException {
        return this.getNci("CLASS").toByte();
    }

    public final String getNciClassStr() throws MdsException {
        return this.getNci("CLASS_STR").toString();
    }

    public final short getNciConglomerateElt() throws MdsException {
        return this.getNci("CONGLOMERATE_ELT").toShort();
    }

    public final NidArray getNciConglomerateNids() throws MdsException {
        return (NidArray)this.getNci("CONGLOMERATE_NIDS");
    }

    public final int getNciDataInNci() throws MdsException {
        return this.getNci("CLASS").toInt();
    }

    public final int getNciDepth() throws MdsException {
        return this.getNci("DEPTH").toInt();
    }

    public final byte getNciDType() throws MdsException {
        return this.getNci("DTYPE").toByte();
    }

    public final String getNciDTypeStr() throws MdsException {
        return this.getNci("DTYPE_STR").toString();
    }

    public final int getNciErrorOnPut() throws MdsException {
        return this.getNci("ERROR_ON_PUT").toInt();
    }

    public final int getNciFlags() throws MdsException {
        return this.getNci("GET_FLAGS").toInt();
    }

    public final String getNciFullPath() throws MdsException {
        return this.getNci("FULLPATH").toString();
    }

    public final int getNciIOStatus() throws MdsException {
        return this.getNci("IO_STATUS").toInt();
    }

    public final int getNciIOStv() throws MdsException {
        return this.getNci("IO_STV").toInt();
    }

    public final boolean getNciIsChild() throws MdsException {
        return this.getNci("IS_CHILD").toByte() != 0;
    }

    public final boolean getNciIsMember() throws MdsException {
        return this.getNci("IS_MEMBER").toByte() != 0;
    }

    public final int getNciLength() throws MdsException {
        return this.getNci("LENGTH").toInt();
    }

    public final Nid getNciMember() throws MdsException {
        return (Nid)this.getNci("MEMBER");
    }

    public final NidArray getNciMemberNids() throws MdsException {
        if(this.getNciNumberOfMembers() == 0) return new NidArray();
        return (NidArray)this.getNci("MEMBER_NIDS");
    }

    public final String getNciMinPath() throws MdsException {
        return this.getNci("MINPATH").toString();
    }

    public final int getNciNidNumber() throws MdsException {
        return this.getNci("NID_NUMBER").toInt();
    }

    public final String getNciNodeName() throws MdsException {
        return this.getNci("NODE_NAME").toString().trim();
    }

    public final int getNciNumberOfChildren() throws MdsException {
        return this.getNci("NUMBER_OF_CHILDREN").toInt();
    }

    public final int getNciNumberOfElts() throws MdsException {
        return this.getNci("NUMBER_OF_ELTS").toInt();
    }

    public final int getNciNumberOfMembers() throws MdsException {
        return this.getNci("NUMBER_OF_MEMBERS").toInt();
    }

    public final String getNciOriginalPartName() throws MdsException {
        return this.getNci("ORIGINAL_PART_NAME").toString().trim();
    }

    public final int getNciOwnerId() throws MdsException {
        return this.getNci("OWNER_ID").toInt();
    }

    public final Nid getNciParent() throws MdsException {
        return (Nid)this.getNci("PARENT");
    }

    public final int getNciParentRelationship() throws MdsException {
        return this.getNci("PARENT_RELATIONSHIP").toInt();
    }

    public final String getNciParentTree() throws MdsException {
        return this.getNci("PARENT_TREE").toString();
    }

    public final String getNciPath() throws MdsException {
        return this.getNci("PATH").toString();
    }

    public final Descriptor getNciRecord() throws MdsException {
        return this.getNci("RECORD");
    }

    public final long getNciRfa() throws MdsException {
        return this.getNci("RFA").toLong();
    }

    public final int getNciRLength() throws MdsException {
        return this.getNci("RLENGTH").toInt();
    }

    public final boolean getNciStatus() throws MdsException {
        return this.getNci("STATUS").toByte() != 0;
    }

    public final long getNciTimeInserted() throws MdsException {
        return this.getNci("TIME_INSERTED").toLong();
    }

    public final String getNciTimeInsertedStr() throws MdsException {
        return this.connection.getString("DATE_TIME(GETNCI($,'TIME_INSERTED'))", this);
    }

    public final byte getNciUsage() throws MdsException {
        return this.getNci("USAGE").toByte();
    }

    public final String getNciUsageStr() throws MdsException {
        return this.getNci("USAGE_STR").toString();
    }

    public final int getNciVersion() throws MdsException {
        return this.getNci("VERSION").toInt();
    }

    public abstract int getNidNumber() throws MdsException;

    public final int getNumSegments() throws MdsException {
        return this.treeshr.treeGetNumSegments(this.getNidNumber()).data;
    }

    public final Descriptor getRecord() throws MdsException {
        return this.treeshr.treeGetRecord(this.getNidNumber()).data;
    }

    public final Signal getSegment(final int idx) throws MdsException {
        return this.treeshr.treeGetSegment(this.getNidNumber(), idx).data;
    }

    public final Descriptor getXNci(final String name) throws MdsException {
        return this.treeshr.treeGetXNci(this.getNidNumber(), name).data;
    }

    public final int putRecord(final Descriptor data) throws MdsException {
        return this.treeshr.treePutRecord(this.getNidNumber(), data);
    }

    public final int putRow(final long time, final Descriptor_A data) throws MdsException {
        return this.treeshr.treePutRow(this.getNidNumber(), 1 << 20, time, data);
    }

    public final int setDefault() throws MdsException {
        return this.treeshr.treeSetDefault(this.getNidNumber());
    }

    public final int setFlags(final int flags) throws MdsException {
        return this.treeshr.treeSetNciItm(this.getNidNumber(), true, flags & 0x7FFFFFFC);
    }

    public final int setPath(final String path) throws MdsException {
        return this.treeshr.treeRenameNode(this.getNidNumber(), path);
    }

    public final int setSubtree() throws MdsException {
        return this.treeshr.treeSetSubtree(this.getNidNumber());
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

    @Override
    public final short[] toShortArray() {
        try{
            return this.getRecord().toShortArray();
        }catch(final MdsException e){
            return null;
        }
    }

    public final int turnOff() throws MdsException {
        return this.treeshr.treeTurnOff(this.getNidNumber());
    }

    public final int turnOn() throws MdsException {
        return this.treeshr.treeTurnOn(this.getNidNumber());
    }
}

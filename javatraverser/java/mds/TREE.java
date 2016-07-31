package mds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mds.ITreeShr.DescriptorStatus;
import mds.ITreeShr.IntegerStatus;
import mds.ITreeShr.SignalStatus;
import mds.ITreeShr.TagRef;
import mds.ITreeShr.TagRefStatus;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_a.NidArray;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.NODE;
import mds.data.descriptor_s.NODE.Flags;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;
import mds.data.descriptor_s.Pointer;
import mds.mdsip.Connection;
import mds.mdsip.Connection.Provider;

public final class TREE{
    @SuppressWarnings("serial")
    public static final class TagList extends HashMap<String, Nid>{
        private final String root;

        public TagList(final int capacity, final String expt){
            super(capacity);
            this.root = new StringBuilder(expt.length() + 3).append("\\").append(expt).append("::").toString();
        }

        @Override
        public final String toString() {
            final StringBuilder str = new StringBuilder(this.size() * 64);
            for(final Entry<String, Nid> entry : this.entrySet())
                str.append(entry.getKey().replace(this.root, "\\")).append("  =>  ").append(entry.getValue()).append("\n");
            return str.toString();
        }
    }
    public static final int EDITABLE = 2;
    public static final int NEW      = 3;
    public static final int NORMAL   = 1;
    public static final int READONLY = 0;
    public static final int REALTIME = 4;
    private static TREE     active   = null;

    public static TREE getActiveTree() {
        return TREE.active;
    }

    public static final int getCurrentShot(final Connection connection, final String expt) throws MdsException {
        return new TreeShr(connection).treeGetCurrentShotId(expt);
    }

    public static final int getCurrentShot(final Provider provider, final String expt) throws MdsException {
        return TREE.getCurrentShot(Connection.sharedConnection(provider), expt);
    }
    private final Pointer   ctx = Pointer.NULL();
    public final int        shot;
    public final Connection connection;
    public final String     expt;
    private int             mode;
    public final TreeShr    treeshr;

    public TREE(final Connection connection, final String expt, final int shot){
        this(connection, expt, shot, TREE.READONLY);
    }

    public TREE(final Connection connection, final String expt, final int shot, final int mode){
        this.connection = connection;
        this.treeshr = new TreeShr(connection);
        this.expt = expt.toUpperCase();
        this.shot = shot;
        this.mode = mode;
    }

    public TREE(final Provider provider, final String expt, final int shot, final int mode){
        this(Connection.sharedConnection(provider), expt, shot, mode);
    }

    public final Nid addDevice(final String path, final String model) throws MdsException {
        synchronized(this.connection){
            final IntegerStatus res = this.setActive().treeshr.treeAddConglom(this.ctx, path, model);
            MdsException.handleStatus(res.status);
            return new Nid(res.data);
        }
    }

    public final Nid addNode(final NODE node, final String name, final byte usage) throws MdsException {
        synchronized(this.connection){
            final Nid def = this.getDefault();
            node.setDefault();
            final Nid nid = this.addNode(name, usage);
            def.setDefault();
            return nid;
        }
    }

    public final Nid addNode(final String path, final byte usage) throws MdsException {
        synchronized(this.connection){
            final IntegerStatus res = this.setActive().treeshr.treeAddNode(this.ctx, path, usage);
            MdsException.handleStatus(res.status);
            final Nid nid = new Nid(res.data, this);
            if(usage == NODE.USAGE_SUBTREE) nid.setSubtree();
            return nid;
        }
    }

    public final TREE addTag(final int nid, final String tag) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeAddTag(this.ctx, nid, tag));
        }
        return this;
    }

    public final TREE clearFlags(final int nid, final int flags) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeSetNciItm(this.ctx, nid, false, flags & 0x7FFFFFFC));
        }
        return this;
    }

    public final TREE clearTags(final int nid) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeRemoveNodesTags(this.ctx, nid));
        }
        return this;
    }

    public final TREE close() throws MdsException {
        synchronized(this.connection){
            final int status = this.treeshr.treeClose(this.ctx, this.expt, this.shot);
            MdsException.handleStatus(status);
        }
        return this;
    }

    public final TREE deleteExecute() throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeDeleteNodeExecute(this.ctx));
        }
        return this;
    }

    public final Nid[] deleteGetNids() throws MdsException {
        synchronized(this.connection){
            final List<Nid> nids = new ArrayList<Nid>(256);
            int last = 0;
            for(;;){
                final IntegerStatus res = this.setActive().treeshr.treeDeleteNodeGetNid(this.ctx, last);
                if(res.status == MdsException.TreeNMN) break;
                MdsException.handleStatus(res.status);
                nids.add(new Nid(last = res.data));
            }
            return nids.toArray(new Nid[0]);
        }
    }

    public final int deleteStart(final Nid nid) throws MdsException {
        synchronized(this.connection){
            final IntegerStatus res = this.setActive().treeshr.treeDeleteNodeInitialize(this.ctx, nid.getValue());
            MdsException.handleStatus(res.status);
            return res.data;
        }
    }

    public final TREE doAction(final int nid) {
        synchronized(this.connection){
            // TODO: MdsException.handleStatus(this.setActive());
        }
        return this;
    }

    public final TREE doDeviceMethod(final int nid, final String name) {
        // TODO Auto-generated method stub
        return this;
    }

    public final Nid[] findNodeWild(final int usage) throws MdsException {
        return this.findNodeWild("***", 1 << usage);
    }

    public final Nid[] findNodeWild(final String searchstr, final int usage_mask) throws MdsException {
        synchronized(this.connection){
            final int[] nidnums = this.setActive().connection.getIntegerArray(String.format("_i=-1;_a='[';_q=0Q;WHILE(IAND(_s=TreeShr->TreeFindNodeWild(ref($),ref(_i),ref(_q),val(%d)),1)==1) _a=_a//TEXT(_i)//',';_a=COMPILE(_a//'*]')", usage_mask), new CString(searchstr));
            if(nidnums == null) MdsException.handleStatus(this.connection.getInteger("_s"));
            final Nid[] nids = Nid.getArrayOfNids(nidnums, this);
            return nids;
        }
    }

    public final TagList findTagsWild() throws MdsException {
        return this.findTagsWild("***", 255);
    }

    public final TagList findTagsWild(final String search, final int max) throws MdsException {
        final TagList taglist = new TagList(max, this.expt);
        TagRefStatus tag = TagRefStatus.init;
        synchronized(this.connection){
            while(taglist.size() < max && (tag = this.treeshr.treeFindTagWild(this.ctx, search, tag)).status != 0){
                MdsException.handleStatus(tag.status);
                taglist.put(tag.data, new Nid(tag.nid));
            }
        }
        return taglist;
    }

    public final Pointer getContext() throws MdsException {
        synchronized(this.connection){
            return this.setActive().treeshr.treeCtx(this.ctx);
        }
    }

    public final int getCurrentShot() throws MdsException {
        return TREE.getCurrentShot(this.connection, this.expt);
    }

    public final Nid getDefault() throws MdsException {
        synchronized(this.connection){
            final IntegerStatus res = this.setActive().treeshr.treeGetDefaultNid(this.ctx);
            MdsException.handleStatus(res.status);
            return new Nid(res.data);
        }
    }

    public final Descriptor getNci(final int nid, final String name) throws MdsException {
        synchronized(this.connection){
            return this.setActive().connection.getDescriptor(this.ctx, String.format("GETNCI(%d,$)", nid), new CString(name));
        }
    }

    public final Nid getNciBrother(final int nid) throws MdsException {
        return (Nid)this.getNci(nid, "BROTHER");
    }

    public final Nid getNciChild(final int nid) throws MdsException {
        return (Nid)this.getNci(nid, "CHILD");
    }

    public final NidArray getNciChildrenNids(final int nid) throws MdsException {
        synchronized(this.connection){
            if(this.setActive().connection.getInteger(this.ctx, String.format("GETNCI(%d,'NUMBER_OF_CHILDREN')", nid)) == 0) return new NidArray();
            return (NidArray)this.connection.getDescriptor(String.format("GETNCI(%d,'CHILDREN_NIDS')", nid));
        }
    }

    public final byte getNciClass(final int nid) throws MdsException {
        return this.getNci(nid, "CLASS").toByte();
    }

    public final String getNciClassStr(final int nid) throws MdsException {
        return this.getNci(nid, "CLASS_STR").toString();
    }

    public final short getNciConglomerateElt(final int nid) throws MdsException {
        return this.getNci(nid, "CONGLOMERATE_ELT").toShort();
    }

    public final NidArray getNciConglomerateNids(final int nid) throws MdsException {
        return (NidArray)this.getNci(nid, "CONGLOMERATE_NIDS");
    }

    public final int getNciDataInNci(final int nid) throws MdsException {
        return this.getNci(nid, "CLASS").toInt();
    }

    public final int getNciDepth(final int nid) throws MdsException {
        return this.getNci(nid, "DEPTH").toInt();
    }

    public final byte getNciDType(final int nid) throws MdsException {
        return this.getNci(nid, "DTYPE").toByte();
    }

    public final String getNciDTypeStr(final int nid) throws MdsException {
        return this.getNci(nid, "DTYPE_STR").toString();
    }

    public final int getNciErrorOnPut(final int nid) throws MdsException {
        return this.getNci(nid, "ERROR_ON_PUT").toInt();
    }

    public final int getNciFlags(final int nid) throws MdsException {
        return this.getNci(nid, "GET_FLAGS").toInt();
    }

    public final String getNciFullPath(final int nid) throws MdsException {
        return this.getNci(nid, "FULLPATH").toString();
    }

    public final int getNciIOStatus(final int nid) throws MdsException {
        return this.getNci(nid, "IO_STATUS").toInt();
    }

    public final int getNciIOStv(final int nid) throws MdsException {
        return this.getNci(nid, "IO_STV").toInt();
    }

    public final boolean getNciIsChild(final int nid) throws MdsException {
        return this.getNci(nid, "IS_CHILD").toByte() != 0;
    }

    public final boolean getNciIsMember(final int nid) throws MdsException {
        return this.getNci(nid, "IS_MEMBER").toByte() != 0;
    }

    public final int getNciLength(final int nid) throws MdsException {
        return this.getNci(nid, "LENGTH").toInt();
    }

    public final Nid getNciMember(final int nid) throws MdsException {
        return (Nid)this.getNci(nid, "MEMBER");
    }

    public final NidArray getNciMemberNids(final int nid) throws MdsException {
        synchronized(this.connection){
            if(this.setActive().connection.getInteger(this.ctx, String.format("GETNCI(%d,'NUMBER_OF_MEMBERS')", nid)) == 0) return new NidArray();
            return (NidArray)this.connection.getDescriptor(String.format("GETNCI(%d,'MEMBER_NIDS')", nid));
        }
    }

    public final String getNciMinPath(final int nid) throws MdsException {
        return this.getNci(nid, "MINPATH").toString();
    }

    public final int getNciNidNumber(final int nid) throws MdsException {
        return this.getNci(nid, "NID_NUMBER").toInt();
    }

    public final String getNciNodeName(final int nid) throws MdsException {
        return this.getNci(nid, "NODE_NAME").toString().trim();
    }

    public final int getNciNumberOfChildren(final int nid) throws MdsException {
        return this.getNci(nid, "NUMBER_OF_CHILDREN").toInt();
    }

    public final int getNciNumberOfElts(final int nid) throws MdsException {
        return this.getNci(nid, "NUMBER_OF_ELTS").toInt();
    }

    public final int getNciNumberOfMembers(final int nid) throws MdsException {
        return this.getNci(nid, "NUMBER_OF_MEMBERS").toInt();
    }

    public final String getNciOriginalPartName(final int nid) throws MdsException {
        return this.getNci(nid, "ORIGINAL_PART_NAME").toString().trim();
    }

    public final int getNciOwnerId(final int nid) throws MdsException {
        return this.getNci(nid, "OWNER_ID").toInt();
    }

    public final Nid getNciParent(final int nid) throws MdsException {
        return (Nid)this.getNci(nid, "PARENT");
    }

    public final int getNciParentRelationship(final int nid) throws MdsException {
        return this.getNci(nid, "PARENT_RELATIONSHIP").toInt();
    }

    public final String getNciParentTree(final int nid) throws MdsException {
        return this.getNci(nid, "PARENT_TREE").toString();
    }

    public final String getNciPath(final int nid) throws MdsException {
        return this.getNci(nid, "PATH").toString();
    }

    public final Descriptor getNciRecord(final int nid) throws MdsException {
        return this.getNci(nid, "RECORD");
    }

    public final long getNciRfa(final int nid) throws MdsException {
        return this.getNci(nid, "RFA").toLong();
    }

    public final int getNciRLength(final int nid) throws MdsException {
        return this.getNci(nid, "RLENGTH").toInt();
    }

    public final boolean getNciStatus(final int nid) throws MdsException {
        return this.getNci(nid, "STATUS").toByte() != 0;
    }

    public final long getNciTimeInserted(final int nid) throws MdsException {
        return this.getNci(nid, "TIME_INSERTED").toLong();
    }

    public final String getNciTimeInsertedStr(final int nid) throws MdsException {
        synchronized(this.connection){
            return this.setActive().connection.getString(this.ctx, String.format("DATE_TIME(GETNCI(%d,'TIME_INSERTED'))", nid));
        }
    }

    public final byte getNciUsage(final int nid) throws MdsException {
        return this.getNci(nid, "USAGE").toByte();
    }

    public final String getNciUsageStr(final int nid) throws MdsException {
        return this.getNci(nid, "USAGE_STR").toString();
    }

    public final int getNciVersion(final int nid) throws MdsException {
        return this.getNci(nid, "VERSION").toInt();
    }

    public final Nid getNode(final String path) throws MdsException {
        return new Path(path, this).toNid();
    }

    public final int getNumSegments(final int nid) throws MdsException {
        synchronized(this.connection){
            final IntegerStatus res = this.setActive().treeshr.treeGetNumSegments(this.ctx, nid);
            MdsException.handleStatus(res.status);
            return res.data;
        }
    }

    public final Provider getProvider() {
        return this.connection.getProvider();
    }

    public final Descriptor getRecord(final int nid) throws MdsException {
        synchronized(this.connection){
            final DescriptorStatus res = this.setActive().treeshr.treeGetRecord(this.ctx, nid);
            if(res.status == MdsException.TreeNODATA) return null;
            MdsException.handleStatus(res.status);
            return res.data;
        }
    }

    public final Signal getSegment(final int nid, final int idx) throws MdsException {
        synchronized(this.connection){
            final SignalStatus res = this.setActive().treeshr.treeGetSegment(this.ctx, nid, idx);
            MdsException.handleStatus(res.status);
            return res.data;
        }
    }

    public final String[] getTags(final int nid) throws MdsException {
        final List<String> tags = new ArrayList<String>(255);
        synchronized(this.connection){
            TagRef tag = TagRef.init;
            while(tags.size() < 255 && ((tag = this.treeshr.treeFindNodeTags(this.ctx, nid, tag))).ok()){
                tags.add(tag.data);
            }
        }
        return tags.toArray(new String[0]);
    }

    public final Nid getTop() {
        return new Nid(0, this);
    }

    public final Descriptor getXNci(final int nid, final String name) throws MdsException {
        synchronized(this.connection){
            final DescriptorStatus res = this.setActive().treeshr.treeGetXNci(this.ctx, nid, name);
            MdsException.handleStatus(res.status);
            return res.data;
        }
    }

    public final boolean isEditable() {
        return this.mode == TREE.EDITABLE;
    }

    public final boolean isNidReference(final int nid) throws MdsException {
        return new Flags(this.getNciFlags(nid)).isNidReference();
    }

    public final boolean isOpen() {
        return(this.ctx != null && !this.ctx.isNull());
    }

    public final boolean isPathReference(final int nid) throws MdsException {
        return new Flags(this.getNciFlags(nid)).isPathReference();
    }

    public final boolean isReadonly() {
        return this.mode == TREE.READONLY;
    }

    public final boolean isRealtime() {
        return this.mode == TREE.REALTIME;
    }

    public final boolean isSegmented(final int nid) throws MdsException {
        if(new Flags(this.getNciFlags(nid)).isSegmented()) return true; // cannot be sure due to issue in winter 2015/2016
        return this.getNumSegments(nid) > 0;
    }

    public final TREE open() throws MdsException {
        synchronized(this.connection){
            final int status;
            switch(this.mode){
                case TREE.NEW:
                    this.mode = TREE.EDITABLE;
                    status = this.treeshr.treeOpenNew(this.ctx, this.expt, this.shot);
                    break;
                case TREE.EDITABLE:
                    status = this.treeshr.treeOpenEdit(this.ctx, this.expt, this.shot);
                    break;
                case TREE.REALTIME:
                    System.err.println("REALTIME not implemented");// TODO
                default:
                    this.mode = TREE.READONLY;
                case TREE.READONLY:
                case TREE.NORMAL:
                    status = this.treeshr.treeOpen(this.ctx, this.expt, this.shot, this.isReadonly());
            }
            MdsException.handleStatus(status);
            return this;
        }
    }

    public final TREE open(final int mode) throws MdsException {
        this.mode = mode;
        return this.open();
    }

    public final TREE putRecord(final int nid, final Descriptor data) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treePutRecord(this.ctx, nid, data));
        }
        return this;
    }

    public final TREE putRow(final int nid, final long time, final Descriptor_A data) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treePutRow(this.ctx, nid, 1 << 20, time, data));
        }
        return this;
    }

    public final TREE quit() throws MdsException {
        synchronized(this.connection){
            final int status = this.treeshr.treeQuitTree(this.ctx);
            MdsException.handleStatus(status);
        }
        return this;
    }

    public final TREE setActive() throws MdsException {
        return TREE.active = this;
    }

    public final TREE setDefault(final int nid) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeSetDefault(this.ctx, nid));
        }
        return this;
    }

    public final TREE setFlags(final int nid, final int flags) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeSetNciItm(this.ctx, nid, true, flags & 0x7FFFFFFC));
        }
        return this;
    }

    public final TREE setPath(final int nid, final String path) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeRenameNode(this.ctx, nid, path));
        }
        return this;
    }

    public final TREE setSubtree(final int nid) throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeSetSubtree(this.ctx, nid));
        }
        return this;
    }

    public final TREE setTags(final int nid, final String... tags) throws MdsException {
        synchronized(this.connection){
            this.clearTags(nid);
            if(tags == null) return this;
            for(int i = tags.length; i-- > 0;)
                this.addTag(nid, tags[i]);
        }
        return this;
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder("Tree(\"").append(this.expt);
        sb.append("\", ").append(this.shot == -1 ? "model" : this.shot);
        if(this.mode == TREE.EDITABLE) sb.append(", edit");
        else if(this.mode == TREE.READONLY) sb.append(", readonly");
        sb.append(") on ").append(this.getProvider());
        return sb.toString();
    }

    public final TagRefStatus treeFindTagWild(final String searchstr, final TagRefStatus tag) throws MdsException {
        return this.treeshr.treeFindTagWild(this.ctx, searchstr, tag);
    }

    public final TREE turnOff(final int nid) throws MdsException {
        synchronized(this.connection){
            final int status = this.setActive().treeshr.treeTurnOff(this.ctx, nid);
            if(status == MdsException.TreeLOCK_FAILURE) return this;// ignore: it changes the state
            MdsException.handleStatus(status);
        }
        return this;
    }

    public final TREE turnOn(final int nid) throws MdsException {
        synchronized(this.connection){
            final int status = this.setActive().treeshr.treeTurnOn(this.ctx, nid);
            if(status == MdsException.TreeLOCK_FAILURE) return this;// ignore: it changes the state
            MdsException.handleStatus(status);
        }
        return this;
    }

    public final TREE withPrivateConnection() {
        return new TREE(new Connection(this.getProvider()), this.expt, this.shot);
    }

    public final TREE write() throws MdsException {
        synchronized(this.connection){
            MdsException.handleStatus(this.setActive().treeshr.treeWriteTree(this.ctx));
        }
        return this;
    }
}

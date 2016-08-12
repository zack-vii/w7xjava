package mds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mds.TreeShr.DescriptorStatus;
import mds.TreeShr.IntegerStatus;
import mds.TreeShr.SignalStatus;
import mds.TreeShr.TagRef;
import mds.TreeShr.TagRefStatus;
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
import mds.mdsip.MdsIp;

public final class TREE implements MdsListener{
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
    private static TREE     active   = null;

    public static TREE getActiveTree() {
        return TREE.active;
    }

    public static final int getCurrentShot(final Mds mds, final String expt) throws MdsException {
        return new TreeShr(mds).treeGetCurrentShotId(expt);
    }
    private Nid           def_nid;
    private final Pointer ctx    = Pointer.NULL();
    public final int      shot;
    public final Mds      mds;
    public final String   expt;
    private int           mode;
    public final TreeShr  treeshr;
    public boolean        opened = false;
    private boolean       ready;

    public TREE(final Mds mds, final String expt, final int shot){
        this(mds, expt, shot, TREE.READONLY);
    }

    public TREE(final Mds mds, final String expt, final int shot, final int mode){
        this.mds = mds;
        this.ready = mds.isReady() == null;
        this.treeshr = new TreeShr(mds);
        this.expt = expt.toUpperCase();
        this.shot = shot;
        this.mode = mode;
        this.def_nid = this.getTop();
    }

    private final TREE _open() throws MdsException {
        final int status;
        switch(this.mode){
            case TREE.NEW:
                this.mode = TREE.EDITABLE;
                status = this.treeshr.treeOpenNew(this.ctx, this.expt, this.shot);
                break;
            case TREE.EDITABLE:
                status = this.treeshr.treeOpenEdit(this.ctx, this.expt, this.shot);
                break;
            default:
                this.mode = TREE.READONLY;
            case TREE.READONLY:
            case TREE.NORMAL:
                status = this.treeshr.treeOpen(this.ctx, this.expt, this.shot, this.isReadonly());
        }
        MdsException.handleStatus(status);
        this.updateListener(true);
        return this;
    }

    public final Nid addConglom(final NODE node, final String name, final String model) throws MdsException {
        synchronized(this.mds){
            final Nid def = this.getDefault();
            node.setDefault();
            final Nid nid = this.addConglom(name, model);
            def.setDefault();
            return nid;
        }
    }

    public final Nid addConglom(final String path, final String model) throws MdsException {
        final IntegerStatus res = this.setActive().treeshr.treeAddConglom(this.ctx, path, model);
        MdsException.handleStatus(res.status);
        return new Nid(res.data, this);
    }

    public final Nid addNode(final NODE node, final String name, final byte usage) throws MdsException {
        synchronized(this.mds){
            final Nid def = this.getDefault();
            node.setDefault();
            final Nid nid = this.addNode(name, usage);
            def.setDefault();
            return nid;
        }
    }

    public final Nid addNode(final String path, final byte usage) throws MdsException {
        synchronized(this.mds){
            final IntegerStatus res = this.setActive().treeshr.treeAddNode(this.ctx, path, usage);
            MdsException.handleStatus(res.status);
            final Nid nid = new Nid(res.data, this);
            if(usage == NODE.USAGE_SUBTREE) nid.setSubtree();
            return nid;
        }
    }

    public final TREE addTag(final int nid, final String tag) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeAddTag(this.ctx, nid, tag));
        return this;
    }

    public final TREE clearFlags(final int nid, final int flags) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeSetNciItm(this.ctx, nid, false, flags & 0x7FFFFFFC));
        return this;
    }

    public final TREE clearTags(final int nid) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeRemoveNodesTags(this.ctx, nid));
        return this;
    }

    public final TREE close() throws MdsException {
        MdsException.handleStatus(this.treeshr.treeClose(this.ctx, this.expt, this.shot));
        this.updateListener(false);
        return this;
    }

    public final TREE deleteExecute() throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeDeleteNodeExecute(this.ctx));
        return this;
    }

    public final Nid[] deleteGetNids() throws MdsException {
        synchronized(this.mds){
            final List<Nid> nids = new ArrayList<Nid>(256);
            int last = 0;
            for(;;){
                final IntegerStatus res = this.setActive().treeshr.treeDeleteNodeGetNid(this.ctx, last);
                if(res.status == MdsException.TreeNMN) break;
                MdsException.handleStatus(res.status);
                nids.add(new Nid(last = res.data, this));
            }
            return nids.toArray(new Nid[0]);
        }
    }

    public final int deleteStart(final Nid nid) throws MdsException {
        final IntegerStatus res = this.setActive().treeshr.treeDeleteNodeInitialize(this.ctx, nid.getValue());
        MdsException.handleStatus(res.status);
        return res.data;
    }

    public final TREE doAction(final int nid) {
        // TODO: MdsException.handleStatus(this.setActive());
        return this;
    }

    public final TREE doDeviceMethod(final int nid, final String method) throws MdsException {
        MdsException.handleStatus(this.treeshr.doMethod(this.ctx, nid, method));
        return this;
    }

    @Override
    public final void finalize() {
        if(this.opened) try{
            this.quit();
        }catch(final MdsException e){
            this.mds.removeMdsListener(this);
        }
    }

    public final Nid[] findNodeWild(final int usage) throws MdsException {
        return this.findNodeWild("***", 1 << usage);
    }

    public final Nid[] findNodeWild(final String searchstr, final int usage_mask) throws MdsException {
        final int[] nidnums;
        synchronized(this.mds){
            nidnums = this.setActive().mds.getIntegerArray(String.format("_i=-1;_a='[';_q=0Q;WHILE(IAND(_s=TreeShr->TreeFindNodeWild(ref($),ref(_i),ref(_q),val(%d)),1)==1) _a=_a//TEXT(_i)//',';_a=COMPILE(_a//'*]')", usage_mask), new CString(searchstr));
            if(nidnums == null) MdsException.handleStatus(this.mds.getInteger("_s"));
        }
        final Nid[] nids = Nid.getArrayOfNids(nidnums, this);
        return nids;
    }

    public final TagList findTagsWild() throws MdsException {
        return this.findTagsWild("***", 255);
    }

    public final TagList findTagsWild(final String search, final int max) throws MdsException {
        final TagList taglist = new TagList(max, this.expt);
        synchronized(this.mds){
            TagRefStatus tag = this.setActive().treeshr.treeFindTagWild(this.ctx, search, TagRefStatus.init);
            while(taglist.size() < max && tag.status != 0){
                MdsException.handleStatus(tag.status);
                taglist.put(tag.data, new Nid(tag.nid, this));
                tag = this.treeshr.treeFindTagWild(null, search, tag);
            }
        }
        return taglist;
    }

    public final Pointer getContext() throws MdsException {
        return this.setActive().treeshr.treeCtx(this.ctx);
    }

    public final int getCurrentShot() throws MdsException {
        return TREE.getCurrentShot(this.mds, this.expt);
    }

    public final Nid getDefault() throws MdsException {
        final IntegerStatus res = this.setActive().treeshr.treeGetDefaultNid(this.ctx);
        MdsException.handleStatus(res.status);
        return this.def_nid = new Nid(res.data, this);
    }

    public final Nid getDefaultCached() {
        return this.def_nid;
    }

    public final Descriptor getNci(final int nid, final String name) throws MdsException {
        return this.setActive().mds.getDescriptor(this.ctx, String.format("GETNCI(%d,$)", nid), new CString(name));
    }

    public final Descriptor getNci(final NODE node, final String name) throws MdsException {
        return this.setActive().mds.getDescriptor(this.ctx, "GETNCI($,$)", node, new CString(name));
    }

    public final Nid getNciBrother(final int nid) throws MdsException {
        return (Nid)this.getNci(nid, "BROTHER");
    }

    public final Nid getNciChild(final int nid) throws MdsException {
        return (Nid)this.getNci(nid, "CHILD");
    }

    public final NidArray getNciChildrenNids(final int nid) throws MdsException {
        synchronized(this.mds){
            if(this.setActive().mds.getInteger(this.ctx, String.format("GETNCI(%d,'NUMBER_OF_CHILDREN')", nid)) == 0) return new NidArray();
            return (NidArray)this.mds.getDescriptor(String.format("GETNCI(%d,'CHILDREN_NIDS')", nid));
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
        synchronized(this.mds){
            if(this.setActive().mds.getInteger(this.ctx, String.format("GETNCI(%d,'NUMBER_OF_MEMBERS')", nid)) == 0) return new NidArray();
            return (NidArray)this.mds.getDescriptor(String.format("GETNCI(%d,'MEMBER_NIDS')", nid));
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
        return this.setActive().mds.getString(this.ctx, String.format("DATE_TIME(GETNCI(%d,'TIME_INSERTED'))", nid));
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
        final IntegerStatus res = this.setActive().treeshr.treeGetNumSegments(this.ctx, nid);
        MdsException.handleStatus(res.status);
        return res.data;
    }

    public final Descriptor getRecord(final int nid) throws MdsException {
        final DescriptorStatus res = this.setActive().treeshr.treeGetRecord(this.ctx, nid);
        if(res.status == MdsException.TreeNODATA) return null;
        MdsException.handleStatus(res.status);
        return res.data;
    }

    public final Signal getSegment(final int nid, final int idx) throws MdsException {
        final SignalStatus res = this.setActive().treeshr.treeGetSegment(this.ctx, nid, idx);
        MdsException.handleStatus(res.status);
        return res.data;
    }

    public final String[] getTags(final int nid) throws MdsException {
        final List<String> tags = new ArrayList<String>(255);
        synchronized(this.mds){
            TagRef tag = this.treeshr.treeFindNodeTags(this.ctx, nid, TagRef.init);
            while(tags.size() < 255 && tag.ok()){
                tags.add(tag.data);
                tag = this.treeshr.treeFindNodeTags(null, nid, tag);
            }
        }
        return tags.toArray(new String[0]);
    }

    public final Nid getTop() {
        return new Nid(0, this);
    }

    public final Descriptor getXNci(final int nid, final String name) throws MdsException {
        final DescriptorStatus res = this.setActive().treeshr.treeGetXNci(this.ctx, nid, name);
        MdsException.handleStatus(res.status);
        return res.data;
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

    public final boolean isSegmented(final int nid) throws MdsException {
        if(new Flags(this.getNciFlags(nid)).isSegmented()) return true; // cannot be sure due to issue in winter 2015/2016
        return this.getNumSegments(nid) > 0;
    }

    public final TREE open() throws MdsException {
        return this.setActive()._open();
    }

    public final TREE open(final int mode) throws MdsException {
        this.mode = mode;
        return this.open();
    }

    @Override
    public void processMdsEvent(final MdsEvent e) {
        switch(e.getID()){
            case MdsEvent.TRANSFER:
                break;
            case MdsEvent.HAVE_CONTEXT:
                this.ready = true;
                break;
            case MdsEvent.LOST_CONTEXT:
                this.ctx.setValue(0);
                this.ready = false;
                break;
            default:
                System.out.println(e.getID() + e.getInfo());
        }
    }

    public final TREE putRecord(final int nid, final Descriptor data) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treePutRecord(this.ctx, nid, data));
        return this;
    }

    public final TREE putRow(final int nid, final long time, final Descriptor_A data) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treePutRow(this.ctx, nid, 1 << 20, time, data));
        return this;
    }

    public final TREE quit() throws MdsException {
        MdsException.handleStatus(this.treeshr.treeQuitTree(this.ctx, this.expt, this.shot));
        this.updateListener(false);
        return this;
    }

    public final TREE setActive() throws MdsException {
        if(this.opened && !this.isOpen() && this.ready){
            this._open();
            this.def_nid.setDefault();
        }
        return TREE.active = this;
    }

    public final TREE setDefault(final int nid) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeSetDefault(this.ctx, nid));
        this.def_nid = new Nid(nid, this);
        return this;
    }

    public final TREE setFlags(final int nid, final int flags) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeSetNciItm(this.ctx, nid, true, flags & 0x7FFFFFFC));
        return this;
    }

    public final TREE setPath(final int nid, final String path) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeRenameNode(this.ctx, nid, path));
        return this;
    }

    public final TREE setSubtree(final int nid) throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeSetSubtree(this.ctx, nid));
        return this;
    }

    public final TREE setTags(final int nid, final String... tags) throws MdsException {
        synchronized(this.mds){
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
        return sb.append(')').toString();
    }

    public final TagRefStatus treeFindTagWild(final String searchstr, final TagRefStatus tag) throws MdsException {
        return this.treeshr.treeFindTagWild(this.ctx, searchstr, tag);
    }

    public final TREE turnOff(final int nid) throws MdsException {
        final int status = this.setActive().treeshr.treeTurnOff(this.ctx, nid);
        if(status == MdsException.TreeLOCK_FAILURE) return this;// ignore: it changes the state
        MdsException.handleStatus(status);
        return this;
    }

    public final TREE turnOn(final int nid) throws MdsException {
        final int status = this.setActive().treeshr.treeTurnOn(this.ctx, nid);
        if(status == MdsException.TreeLOCK_FAILURE) return this;// ignore: it changes the state
        MdsException.handleStatus(status);
        return this;
    }

    private final void updateListener(final boolean opened) {
        if(this.opened = opened) this.mds.addMdsListener(this);
        else this.mds.removeMdsListener(this);
    }

    public final TREE withPrivateConnection() {
        if(!(this.mds instanceof MdsIp)) return null;
        return new TREE(new MdsIp(((MdsIp)this.mds).getProvider()), this.expt, this.shot);
    }

    public final TREE write() throws MdsException {
        MdsException.handleStatus(this.setActive().treeshr.treeWriteTree(this.ctx, this.expt, this.shot));
        return this;
    }
}

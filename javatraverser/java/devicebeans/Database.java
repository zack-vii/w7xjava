package devicebeans;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import mds.ITreeShr.DescriptorStatus;
import mds.ITreeShr.IntegerStatus;
import mds.ITreeShr.SignalStatus;
import mds.ITreeShr.TagRefStatus;
import mds.MdsException;
import mds.MdsShr;
import mds.TREE;
import mds.TREE.TagList;
import mds.TdiShr;
import mds.TreeShr;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Pointer;
import mds.data.descriptor_s.TREENODE;
import mds.mdsip.Connection;
import mds.mdsip.Connection.Provider;

public final class Database{
    private static final String extractProvider(final String expt) {
        final String[] parts = System.getenv(String.format("%s_path", expt.toLowerCase())).split("::", 2);
        return (parts.length > 1) ? parts[0] : Connection.Provider.DEFAULT_HOST;
    }

    public static final ByteBuffer getByteBuffer(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return null;
        return Connection.getActiveConnection().getByteBuffer(expr);
    }

    public static final Provider getCurrentProvider() {
        return Connection.getActiveConnection().getProvider();
    }

    public static final String getDatabase() throws MdsException {
        if(!Connection.getActiveConnection().isConnected()) return "Not connected.";
        final String result = Connection.getActiveConnection().getString("_t=*;TCL('show db',_t);_t");
        return result.trim();
    }

    private static final void stderr(final String line, final Exception exc) {
        MdsException.stderr(line, exc);
    }

    private static final void stdout(final String line) {
        MdsException.stdout(line);
    }
    private final Pointer    ctx  = Pointer.NULL();
    private final Connection con;
    private final String     expt;
    private boolean          open = false;
    private final int        mode;
    private final int        shot;
    private final TreeShr    treeshr;
    private final MdsShr     mdsshr;
    private final TdiShr     tdishr;

    public Database(final Connection con, final String expt, final int shot, final int mode) throws MdsException{
        this.con = con.setActive();
        this.treeshr = new TreeShr(this.con);
        this.mdsshr = new MdsShr(this.con);
        this.tdishr = new TdiShr(this.con);
        this.expt = expt.toUpperCase();
        this.shot = (shot == 0) ? this.getCurrentShot(expt) : shot;
        if(mode == TREE.NEW){
            this._connect();
            this.mode = TREE.EDITABLE;
            this._open_new();
        }else{
            this.mode = mode;
            this.open();
        }
    }

    public Database(final String expt, final int shot) throws MdsException{
        this(Database.extractProvider(expt), expt, shot, 0);
    }

    public Database(final String expt, final int shot, final int mode) throws MdsException{
        this(Database.extractProvider(expt), expt, shot, mode);
    }

    public Database(final String provider, final String expt, final int shot, final int mode) throws MdsException{
        this(Connection.sharedConnection(provider), expt, shot, mode);
    }

    private final void _checkContext() throws MdsException {
        this._connect();
        if(!this.isOpen() || this.ctx == null) this._open(); // !this.ctx.equals(this.getTreeCtx())
    }

    private final void _connect() throws MdsException {
        if(!this.con.isConnected()) throw new MdsException("Not connected");
    }

    private final void _open() throws MdsException {
        final int status;
        if(this.isEditable()) status = this.treeshr.treeOpenEdit(this.ctx, this.expt, this.shot);
        else status = this.treeshr.treeOpen(this.ctx, this.expt, this.shot, this.isReadonly());
        MdsException.handleStatus(status);
        this.open = true;
    }

    private final void _open_new() throws MdsException {
        final int status = this.treeshr.treeOpenNew(this.ctx, this.expt, this.shot);
        MdsException.handleStatus(status);
        this.open = true;
    }

    public final Nid addDevice(final String path, final String model) throws MdsException {
        this._checkContext();
        final IntegerStatus res = this.treeshr.treeAddConglom(this.ctx, path, model);
        MdsException.handleStatus(res.status);
        return new Nid(res.data);
    }

    public final Nid addNode(final String path, final byte usage) throws MdsException {
        this._checkContext();
        final IntegerStatus res = this.treeshr.treeAddNode(this.ctx, path, usage);
        MdsException.handleStatus(res.status);
        final Nid nid = new Nid(res.data);
        if(usage == TREENODE.USAGE_SUBTREE) this.setSubtree(nid);
        return nid;
    }

    public final String[] allTags(final Nid nid) throws MdsException {
        this._checkContext();
        final String str = this.con.getString(String.format("_i=-1;_q=0Q,_a='',_t='',_j=0,WHILE(AND(TreeShr->TreeFindTagWildDsc(ref('***'),ref(_i),ref(_q),xd(_t)),_i<%d)) IF(OR(%d==0,_i==%d)) _a=(_j++;_a//','//_t;);_a", nid.getValue(), nid.getValue()));
        if(str == null) return new String[0];
        return str.substring(1).split(",");
    }

    public final void clearFlags(final Nid nid, final int flags) throws MdsException {
        this._checkContext();
        nid.clearFlags(flags);
    }

    public final void close() throws MdsException {
        final int status = this.treeshr.treeClose(this.ctx, this.expt, this.shot);
        MdsException.handleStatus(status);
        this.open = false;
    }

    public final void create(final int shot) throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeCreateTreeFiles(this.expt, shot, this.shot);
        MdsException.handleStatus(status);
    }

    public final void deleteExecute() throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeDeleteNodeExecute(this.ctx);
        MdsException.handleStatus(status);
    }

    public final Nid[] deleteGetNids() throws MdsException {
        final List<Nid> nids = new ArrayList<Nid>(256);
        int last = 0;
        synchronized(this.con){
            IntegerStatus res = this.treeshr.treeDeleteNodeGetNid(this.ctx, last);
            for(;;){
                if(res.status == 265388128) break;
                MdsException.handleStatus(res.status);
                nids.add(new Nid(last = res.data));
                res = this.treeshr.treeDeleteNodeGetNid(null, last);
            }
        }
        return nids.toArray(new Nid[0]);
    }

    public final int deleteStart(final Nid nid) throws MdsException {
        this._checkContext();
        final int nidnum = nid.getValue();
        final IntegerStatus res = this.treeshr.treeDeleteNodeInitialize(this.ctx, nidnum);
        MdsException.handleStatus(res.status);
        return res.data;
    }

    public final int doAction(final Nid nid) throws MdsException {
        this._checkContext();
        return this.con.getInteger(String.format("TCL('dispatch/nowait '//GETNCI(%d,'FULL_PATH'))", nid.getValue()));
    }

    public final void doDeviceMethod(final Nid nid, final String method) throws MdsException {
        this._checkContext();
        // TODO:TreeShr->TreeDoMethod
        throw new MdsException("No implementation");
    }

    @Override
    protected final void finalize() throws Throwable {
        try{
            if(this.open) this.close();
        }finally{
            super.finalize();
        }
    }

    public final int getCurrentShot() throws MdsException {
        return this.getCurrentShot(this.expt);
    }

    public final int getCurrentShot(final String expt) throws MdsException {
        return this.treeshr.treeGetCurrentShotId(expt);
    }

    public final Descriptor getData(final Nid nid) throws MdsException {
        this._checkContext();
        return nid.getRecord();
    }

    public final Nid getDefault() throws MdsException {
        this._checkContext();
        final IntegerStatus res = this.treeshr.treeGetDefaultNid(null);
        MdsException.handleStatus(res.status);
        return new Nid(res.data);
    }

    public final int getFlags(final Nid nid) throws MdsException {
        this._checkContext();
        return nid.getNciFlags();
    }

    public final String getMdsMessage(final int status) {
        try{
            return this.mdsshr.mdsGetMsgDsc(status);
        }catch(final MdsException e){
            return e.getMessage();
        }
    }

    public final Nid[] getMembers(final Nid nid) throws MdsException {
        this._checkContext();
        return nid.getNciMemberNids().toArray();
    }

    public final String getName() {
        return this.expt;
    }

    public final String getOriginalPartName(final Nid nid) throws MdsException {
        this._checkContext();
        return nid.getNciOriginalPartName();
    }

    public final Provider getProvider() {
        return this.con.getProvider();
    }

    public final Descriptor getRecord(final Nid nid) throws MdsException {
        this._checkContext();
        final DescriptorStatus res = this.treeshr.treeGetRecord(this.ctx, nid.getValue());
        MdsException.handleStatus(res.status);
        return res.data;
    }

    public Signal getSegment(final Nid nid, final int segment) throws MdsException {
        final SignalStatus res = this.treeshr.treeGetSegment(this.ctx, nid.getValue(), segment);
        MdsException.handleStatus(res.status);
        return res.data;
    }

    public final int getShot() {
        return this.shot;
    }

    public final Nid[] getSons(final Nid nid) throws MdsException {
        this._checkContext();
        return nid.getNciChildrenNids().toArray();
    }

    public final String[] getTags(final Nid nid) throws MdsException {
        return this.getTags(nid, "***", 255);
    }

    public final String[] getTags(final Nid nid, final String search, final int max) throws MdsException {
        this._checkContext();
        final List<String> tags = new ArrayList<String>(max);
        synchronized(this.con){
            TagRefStatus tag = this.treeshr.treeFindTagWild(this.ctx, search, TagRefStatus.init);
            while(tags.size() < max && (tag.status != 0)){
                MdsException.handleStatus(tag.status);
                if(nid.getValue() == tag.nid){
                    final String[] parts = tag.data.split("\\.|:");
                    tags.add(parts[parts.length - 1]);
                }
                tag = this.treeshr.treeFindTagWild(null, search, tag);
            }
        }
        return tags.toArray(new String[0]);
    }

    public final TagList getTagsWild(final String search, final int max) throws MdsException {
        this._checkContext();
        final TagList taglist = new TagList(max, this.expt);
        TagRefStatus tag = TagRefStatus.init;
        while(taglist.size() < max && (tag = this.treeshr.treeFindTagWild(this.ctx, search, tag)).status != 0){
            MdsException.handleStatus(tag.status);
            taglist.put(tag.data, new Nid(tag.nid));
        }
        return taglist;
    }

    public final Pointer getTreeCtx() throws MdsException {
        return this.treeshr.treeCtx(null);
    }

    public final byte[] getType(final String expr) throws MdsException {
        return this.con.getByteArray("_a=As_Is(EXECUTE($));_a=[Class(_a),Kind(_a)]", new CString(expr));
    }

    public final Nid[] getWild(final int usage_mask) throws MdsException {
        final Nid[] nids = Nid.getArrayOfNids(this.con.getIntegerArray(String.format("_i=-1;_a='[';_q=0Q;WHILE(IAND(_s=TreeShr->TreeFindNodeWild(ref('***'),ref(_i),ref(_q),val(%d)),1)==1) _a=_a//TEXT(_i)//',';_a=COMPILE(_a//'*]')", 1 << usage_mask)));
        if(nids == null) MdsException.handleStatus(this.con.getInteger("_s"));
        return nids;
    }

    public final boolean isEditable() {
        return this.mode == TREE.EDITABLE;
    }

    public final boolean isOn(final Nid nid) throws MdsException {
        this._checkContext();
        return !nid.getNciStatus();
    }

    public final boolean isOpen() {
        return this.open;
    }

    public final boolean isReadonly() {
        return this.mode == TREE.READONLY;
    }

    public final boolean isRealtime() {
        return this.mode == TREE.REALTIME;
    }

    public final void open() throws MdsException {
        this._checkContext();
    }

    public final void putData(final Nid nid, final Descriptor data) throws MdsException {
        this._checkContext();
        nid.putRecord(data);
    }

    public final void putRow(final Nid nid, final long time, final Descriptor_A data) throws MdsException {
        this._checkContext();
        nid.putRow(time, data);
    }

    public final void quit() throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeQuitTree(this.expt, this.shot);
        MdsException.handleStatus(status);
    }

    public final void renameNode(final Nid nid, final String path) throws MdsException {
        this._checkContext();
        nid.setPath(path);
    }

    public final Nid resolveRefSimple(final Nid nid) throws MdsException {
        this._checkContext();
        return new Nid(this.con.getInteger(String.format("_a=%d;WHILE(IAND(GETNCI(_a,'DTYPE'),-2)==192) _a=GETNCI(_a,'RECORD');GETNCI(_a,'NID_NUMBER')", nid.getValue())));
    }

    public final void setCurrentShot(final int shot) throws MdsException {
        this.setCurrentShot(this.expt, shot);
    }

    public final void setCurrentShot(final String expt, final int shot) throws MdsException {
        final int status = this.treeshr.treeSetCurrentShotId(expt, shot);
        if((status & 1) == 0) Database.stderr("", new Exception("Could not set current shot id."));
        else Database.stdout(String.format("Current shot of %s set to %d", expt, shot));
    }

    public final void setDefault(final Nid nid) throws MdsException {
        this._checkContext();
        nid.setDefault();
    }

    public final void setEvent(final String event) throws MdsException {
        MdsException.handleStatus(this.mdsshr.mdsEvent(event));
    }

    public final void setFlags(final Nid nid, final int flags) throws MdsException {
        this._checkContext();
        nid.setFlags(flags);
    }

    public final void setOn(final Nid nid, final boolean on) throws MdsException {
        this._checkContext();
        nid.setOn(on);
    }

    public final void setSubtree(final Nid nid) throws MdsException {
        this._checkContext();
        nid.setSubtree();
    }

    public final void setTags(final Nid nid, final String tags[]) throws MdsException {
        this._checkContext();
        nid.clearTags();
        if(tags == null) return;
        for(final String tag : tags)
            nid.addTag(tag);
    }

    public final Descriptor tdiCompile(final String expr) throws MdsException {
        this._checkContext();
        return this.tdishr.tdiCompile(expr);
    }

    public final String tdiDecompile(final Descriptor data) throws MdsException {
        this._checkContext();
        return this.tdishr.tdiDecompile(data);
    }

    public final Descriptor tdiEvaluate(final Descriptor data) throws MdsException {
        this._checkContext();
        return this.tdishr.tdiEvaluate(data);
    }

    public final Descriptor tdiExecute(final String expr, final Descriptor... args) throws MdsException {
        this._checkContext();
        return this.tdishr.tdiExecute(expr, args);
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder("Tree(\"").append(this.expt);
        sb.append("\", ").append(this.shot == -1 ? "model" : this.shot);
        if(this.mode == TREE.EDITABLE) sb.append(", edit");
        else if(this.mode == TREE.READONLY) sb.append(", readonly");
        sb.append(')');
        if(this.con != null) sb.append(" on ").append(this.con.getProvider());
        return sb.toString();
    }

    public final void write() throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeWriteTree(this.ctx, this.expt, this.shot);
        MdsException.handleStatus(status);
    }
}

package mds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jtraverser.jTraverserFacade;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;
import mds.data.descriptor_s.Pointer;
import mds.data.descriptor_s.TREENODE;
import mds.mdsip.Connection;

public final class Database{
    @SuppressWarnings("serial")
    public final class TagList extends HashMap<String, Nid>{
        public TagList(final int cap){
            super(cap);
        }

        @Override
        public final String toString() {
            final StringBuilder str = new StringBuilder(this.size() * 64);
            final String root = new StringBuilder(Database.this.expt.length() + 3).append("\\").append(Database.this.expt).append("::").toString();
            for(final Entry<String, Nid> entry : this.entrySet())
                str.append(entry.getKey().replace(root, "\\")).append("  =>  ").append(entry.getValue()).append("\n");
            return str.toString();
        }
    }
    private static String                                     cexpt       = null;
    private static final Map<Connection.Provider, Connection> connections = new HashMap<Connection.Provider, Connection>(16);
    private static int                                        cshot       = 0;
    public static final int                                   EDITABLE    = 2;
    public static final int                                   NEW         = 3;
    public static final int                                   NORMAL      = 1;
    public static final int                                   READONLY    = 0;
    public static final int                                   REALTIME    = 4;

    private static final String extractProvider(final String expt) {
        final String[] parts = System.getenv(String.format("%s_path", expt.toLowerCase())).split("::", 2);
        return (parts.length > 1) ? parts[0] : Connection.Provider.DEFAULT_HOST;
    }

    public static final ByteBuffer getByteBuffer(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return null;
        return Connection.getActiveConnection().getByteBuffer(expr);
    }

    public static final String getCurrentProvider() {
        return Connection.getActiveConnection().getProvider();
    }

    public static final String getDatabase() throws MdsException {
        if(!Connection.getActiveConnection().isConnected()) return "Not connected.";
        final String result = Connection.getActiveConnection().getString("_t=*;TCL('show db',_t);_t");
        return result.trim();
    }

    private static final Connection setupConnection(final String provider_in) {
        final Connection.Provider provider = new Connection.Provider(provider_in);
        if(Database.connections.containsKey(provider)) return Database.connections.get(provider).setActive();
        Database.connections.put(provider, new Connection(provider).setActive());
        return Connection.getActiveConnection();
    }

    private static final void stderr(final String line, final Exception exc) {
        jTraverserFacade.stderr(line, exc);
    }

    private static final void stdout(final String line) {
        jTraverserFacade.stdout(line);
    }

    private static final void updateCurrent() throws MdsException {
        try{
            Database.cshot = Connection.getActiveConnection().getInteger("$SHOT");
            Database.cexpt = Connection.getActiveConnection().getString("$EXPT").trim();
        }catch(final MdsException de){
            Database.cshot = 0;
            Database.cexpt = null;
        }
    }
    private final Connection con;
    private final String     expt;
    private boolean          is_open = false;
    private final int        mode;
    private final int        shot;
    private final TreeShr    treeshr;
    private final MdsShr     mdsshr;
    private final TdiShr     tdishr;

    public Database(final String provider) throws MdsException{
        this.con = Database.setupConnection(provider);
        this.treeshr = new TreeShr(this.con);
        this.mdsshr = new MdsShr(this.con);
        this.tdishr = new TdiShr(this.con);
        this.expt = null;
        this.shot = 0;
        this.mode = 0;
    }

    public Database(final String expt, final int shot) throws MdsException{
        this(Database.extractProvider(expt), expt, shot, 0);
    }

    public Database(final String expt, final int shot, final int mode) throws MdsException{
        this(Database.extractProvider(expt), expt, shot, mode);
    }

    public Database(final String provider, final String expt, final int shot, final int mode) throws MdsException{
        this.con = Database.setupConnection(provider);
        this.treeshr = new TreeShr(this.con);
        this.mdsshr = new MdsShr(this.con);
        this.tdishr = new TdiShr(this.con);
        this.expt = expt.toUpperCase();
        this.shot = (shot == 0) ? this.getCurrentShot(expt) : shot;
        if(mode == Database.NEW){
            this._connect();
            this._open_new();
            this.mode = Database.EDITABLE;
        }else this.mode = mode;
        this._checkContext();
    }

    private final void _checkContext() throws MdsException {
        this._connect();
        Database.updateCurrent();
        if(this.shot == Database.cshot && this.expt.equals(Database.cexpt)) return;// && Database.cctx == Database.getTreeCtx()
        this._open();
    }

    private final void _connect() throws MdsException {
        if(!this.con.isConnected()) throw new MdsException("Not connected");
    }

    private final void _open() throws MdsException {
        final int status;
        if(this.isEditable()) status = this.treeshr.treeOpenEdit(this.expt, this.shot);
        else status = this.treeshr.treeOpen(this.expt, this.shot, this.isReadonly());
        this.handleStatus(status);
        this.is_open = true;
        Database.updateCurrent();
    }

    private final void _open_new() throws MdsException {
        final int status = this.treeshr.treeOpenNew(this.expt, this.shot);
        this.handleStatus(status);
        this.is_open = true;
        Database.updateCurrent();
    }

    public final Nid addDevice(final String path, final String model) throws MdsException {
        this._checkContext();
        final int[] res = this.treeshr.treeAddConglom(path, model);
        this.handleStatus(res[0]);
        return new Nid(res[1]);
    }

    public final Nid addNode(final String path, final byte usage) throws MdsException {
        this._checkContext();
        final int[] res = this.treeshr.treeAddNode(path, usage);
        this.handleStatus(res[0]);
        final Nid nid = new Nid(res[1]);
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
        final int status = this.treeshr.treeSetNciItm(nid.getValue(), false, flags & 0x7FFFFFFC);
        this.handleStatus(status);
    }

    public final void close() throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeClose(this.expt, this.shot);
        this.handleStatus(status);
        this.is_open = false;
        Database.updateCurrent();
    }

    public final void create(final int shot) throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeCreateTreeFiles(this.expt, shot, this.shot);
        this.handleStatus(status);
    }

    public final void deleteExecute() throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeDeleteNodeExecute();
        this.handleStatus(status);
    }

    public final Nid[] deleteGetNids() throws MdsException {
        final List<Nid> nids = new ArrayList<Nid>(256);
        int last = 0;
        for(;;){
            final int[] res = this.treeshr.treeDeleteNodeGetNid(last);
            if(res[0] == 265388128) break;
            this.handleStatus(res[0]);
            nids.add(new Nid(last = res[1]));
        }
        return nids.toArray(new Nid[0]);
    }

    public final int deleteStart(final Nid nid) throws MdsException {
        this._checkContext();
        final int nidnum = nid.getValue();
        final int[] res = this.treeshr.treeDeleteNodeInitialize(nidnum);
        this.handleStatus(res[0]);
        return res[1];
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
            this.close();
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
        return this.treeshr.treeGetRecord(nid.getValue());
    }

    public final Nid getDefault() throws MdsException {
        this._checkContext();
        final int[] res = this.treeshr.treeGetDefaultNid();
        this.handleStatus(res[0]);
        return new Nid(res[1]);
    }

    public final int getFlags(final Nid nid) throws MdsException {
        this._checkContext();
        return this.con.getInteger(String.format("GETNCI(%d,'GET_FLAGS')", nid.getValue()));
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
        return Nid.getArrayOfNids(this.con.getIntegerArray(String.format("IF_ERROR(GETNCI(GETNCI(%d,'MEMBER_NIDS'),'NID_NUMBER'),[])", nid.getValue())));
    }

    public final String getName() {
        return this.expt;
    }

    public final String getOriginalPartName(final Nid nid) throws MdsException {
        this._checkContext();
        return this.con.getString(String.format("GETNCI(%d,'ORIGINAL_PART_NAME')", nid.getValue()));
    }

    public final String getProvider() {
        return this.con.getProvider();
    }

    public final Descriptor getRecord(final Nid nid) throws MdsException {
        this._checkContext();
        return this.treeshr.treeGetRecord(nid.getValue());
    }

    public Signal getSegment(final Nid nid, final int segment) throws MdsException {
        return this.treeshr.treeGetSegment(nid.getValue(), segment);
    }

    public final long getShot() {
        return this.shot;
    }

    public final Nid[] getSons(final Nid nid) throws MdsException {
        this._checkContext();
        return Nid.getArrayOfNids(this.con.getIntegerArray(String.format("IF_ERROR(GETNCI(GETNCI(%d,'CHILDREN_NIDS'),'NID_NUMBER'),[])", nid.getValue())));
    }

    public final String[] getTags(final Nid nid) throws MdsException {
        return this.getTags(nid, "***", 255);
    }

    public final String[] getTags(final Nid nid, final String search, final int max) throws MdsException {
        this._checkContext();
        this.treeshr.treeFindTagWildReset();
        final List<String> tags = new ArrayList<String>(max);
        String tag;
        while(tags.size() < max && (tag = this.treeshr.treeFindTagWild(search)) != null)
            if(nid.getValue() == this.treeshr.treeFindTagWildNid()){
                final String[] parts = tag.split("\\.|:");
                tags.add(parts[parts.length - 1]);
            }
        return tags.toArray(new String[0]);
    }

    public final TagList getTagsWild(final String search, final int max) throws MdsException {
        this._checkContext();
        this.treeshr.treeFindTagWildReset();
        final TagList taglist = new TagList(max);
        String tag;
        while(taglist.size() < max && (tag = this.treeshr.treeFindTagWild(search)) != null)
            taglist.put(tag, new Nid(this.treeshr.treeFindTagWildNid()));
        return taglist;
    }

    public final Pointer getTreeCtx() throws MdsException {
        return this.treeshr.treeCtx();
    }

    public final byte[] getType(final String expr) throws MdsException {
        return this.con.getByteArray("_a=As_Is(EXECUTE($));_a=[Class(_a),Kind(_a)]", new CString(expr));
    }

    public final Nid[] getWild(final int usage_mask) throws MdsException {
        final Nid[] nids = Nid.getArrayOfNids(this.con.getIntegerArray(String.format("_i=-1;_a='[';_q=0Q;WHILE(IAND(_s=TreeShr->TreeFindNodeWild(ref('***'),ref(_i),ref(_q),val(%d)),1)==1) _a=_a//TEXT(_i)//',';_a=COMPILE(_a//'*]')", 1 << usage_mask)));
        if(nids == null) this.handleStatus(this.con.getInteger("_s"));
        return nids;
    }

    private final void handleStatus(final int status) throws MdsException {
        final String msg = this.getMdsMessage(status);
        final boolean success = (status & 1) == 1;
        if(!success){
            Database.stderr(msg, null);
            throw new MdsException(msg, status);
        }
        Database.stdout(msg);
    }

    public final boolean isEditable() {
        return this.mode == Database.EDITABLE;
    }

    public final boolean isOn(final Nid nid) throws MdsException {
        return 0 == (this.getFlags(nid) & TREENODE.Flags.STATE);
    }

    public final boolean isOpen() {
        return this.is_open;
    }

    public final boolean isReadonly() {
        return this.mode == Database.READONLY;
    }

    public final boolean isRealtime() {
        return this.mode == Database.REALTIME;
    }

    public final void open() throws MdsException {
        this._checkContext();
    }

    public final void putData(final Nid nid, final Descriptor data) throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treePutRecord(nid.getValue(), data);
        this.handleStatus(status);
    }

    public final void putRow(final Nid nid, final Descriptor_A data, final long time) throws MdsException {
        if(data == null){
            this.putData(nid, null);
            return;
        }
        this._checkContext();
        final int status = this.treeshr.treePutRow(nid.getValue(), 1 << 20, time, data);;
        this.handleStatus(status);
    }

    public final void quit() throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeQuitTree(this.expt, this.shot);
        this.handleStatus(status);
    }

    public final void renameNode(final Nid nid, final String path) throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeRenameNode(nid.getValue(), path);
        this.handleStatus(status);
    }

    public final Nid resolve(final Path pad) throws MdsException {
        this._checkContext();
        return (Nid)this.con.getDescriptor(String.format("GETNCI('%s','NID_NUMBER')", pad.getValue()), Nid.class);
    }

    public final Nid resolveRefSimple(final Nid nid) throws MdsException {
        this._checkContext();
        return new Nid(this.con.getInteger(String.format("_a=%d;WHILE(IAND(GETNCI(_a,'DTYPE'),-2)==192) _a=GETNCI(_a,'RECORD');GETNCI(_a,'NID_NUMBER')", nid.getValue())));
    }

    /*
    private final boolean restoreContext() throws MdsException {
        System.out.println(String.format("restore: %s(%03d) from %12d", this.expt, this.shot, this.saveslot));
        final boolean success = (this.saveslot == 0) ? false : this.mds.getInteger(String.format("TreeShr->TreeRestoreContext(val(%d))", this.saveslot)) != 0;
        this.saveContext();
        return success;
    }
    private final boolean saveContext() throws MdsException {
        this.saveslot = 0;
        System.out.print(String.format("saving:  %s(%03d) to   ", this.expt, this.shot));
        this.saveslot = this.mds.getInteger("TreeShr->TreeSaveContext()");
        System.out.println(String.format("%12d", this.saveslot));
        return(this.saveslot != 0);
    }
    */
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
        final int status = this.treeshr.treeSetDefault(nid.getValue());
        this.handleStatus(status);
    }

    public final void setEvent(final String event) throws MdsException {
        this.handleStatus(this.mdsshr.mdsEvent(event));
    }

    public final void setFlags(final Nid nid, final int flags) throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeSetNciItm(nid.getValue(), true, flags & 0x7FFFFFFC);
        this.handleStatus(status);
    }

    public final void setOn(final Nid nid, final boolean on) throws MdsException {
        this._checkContext();
        final int status = on ? this.treeshr.treeTurnOn(nid.getValue()) : this.treeshr.treeTurnOff(nid.getValue());
        if(status == 265392050) return;// TreeLock-Failure but does the change of state
        this.handleStatus(status);
    }

    public final void setSubtree(final Nid nid) throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeSetSubtree(nid.getValue());
        this.handleStatus(status);
    }

    public final void setTags(final Nid nid, final String tags[]) throws MdsException {
        this._checkContext();
        this.treeshr.treeRemoveNodesTags(nid.getValue());
        if(tags == null) return;
        for(final String tag : tags)
            this.handleStatus(this.treeshr.treeAddTag(nid.getValue(), tag));
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
        if(this.mode == Database.EDITABLE) sb.append(", edit");
        else if(this.mode == Database.READONLY) sb.append(", readonly");
        sb.append(')');
        if(this.con != null) sb.append(" on ").append(this.con.getProvider());
        return sb.toString();
    }

    public final void write() throws MdsException {
        this._checkContext();
        final int status = this.treeshr.treeWriteTree(this.expt, this.shot);
        this.handleStatus(status);
    }
}

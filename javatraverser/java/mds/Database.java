package mds;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jtraverser.NodeInfo;
import jtraverser.jTraverserFacade;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;
import mds.mdsip.Connection;

@SuppressWarnings("static-method")
public final class Database{
    @SuppressWarnings("serial")
    public static class TagList extends HashMap<String, Nid>{
        public TagList(final int cap){
            super(cap);
        }

        @Override
        public final String toString() {
            final StringBuilder str = new StringBuilder(this.size() * 64);
            for(final Entry<String, Nid> entry : this.entrySet())
                str.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
            return str.toString();
        }
    }
    private static String                                     cexpt       = null;
    private static final Map<Connection.Provider, Connection> connections = new HashMap<Connection.Provider, Connection>(16);
    private static long                                       cshot       = 0;
    public static final int                                   EDITABLE    = 2;
    private static Connection                                 mds;
    public static final int                                   NEW         = 3;
    public static final int                                   NORMAL      = 1;
    public static final int                                   READONLY    = 0;
    public static final int                                   REALTIME    = 4;

    private static final String extractProvider(final String expt) {
        final String[] parts = System.getenv(String.format("%s_path", expt.toLowerCase())).split("::", 2);
        return (parts.length > 1) ? parts[0] : Connection.Provider.DEFAULT_HOST;
    }

    public static final String getCurrentProvider() {
        return Database.mds.getProvider();
    }

    public static final String getDatabase() throws MdsException {
        if(!Database.mds.isConnected()) return "Not connected.";
        final String result = Database.mds.getString("COMMA(_ans=*,TCL('show db',_ans),_ans)");
        return result.trim();
    }

    public static final String getFullPath(final int nid) throws MdsException {
        try{
            return Database.mds.getString(String.format("GETNCI(%d,'FULLPATH')", nid));
        }catch(final MdsException e){
            return String.format("<nid %d>", nid);
        }
    }

    public static final String getMinPath(final int nid) throws MdsException {
        if(Database.mds == null) throw new MdsException("Offline");
        return Database.mds.getString(String.format("GETNCI(%d,'MINPATH')", nid));
    }

    public static final String getPath(final int nid) throws MdsException {
        try{
            return Database.mds.getString(String.format("GETNCI(%d,'PATH')", nid));
        }catch(final MdsException e){
            return String.format("<nid %d>", nid);
        }
    }

    public static final int getTreeCtx() throws MdsException {
        return Database.mds.getInteger("TreeShr->TreeCtx()");
    }

    /*
    public static final void main(final String[] args) {// TODO:main
    }
    */
    private static final Connection setupConnection(final String provider_in) {
        final Connection.Provider provider = new Connection.Provider(provider_in);
        if(Database.connections.containsKey(provider)) return Database.mds = Database.connections.get(provider);
        Database.connections.put(provider, Database.mds = new Connection(provider));
        return Database.mds;
    }

    private static final void stderr(final String line, final Exception exc) {
        jTraverserFacade.stderr(line, exc);
    }

    private static final void stdout(final String line) {
        jTraverserFacade.stdout(line);
    }

    public static final Descriptor tdiCompile(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return null;
        return Database.mds.compile(expr);
    }

    public static final String tdiDecompile(final Descriptor data) throws MdsException {
        if(data == null) return "*";
        return Descriptor.decompile(data);
    }

    public static final String tdiEvalDeco(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return "*";
        try{
            return Database.mds.getString(String.format("COMMA(_ans=*,TdiShr->TdiDecompile(xd(EVALUATE((%s))),xd(_ans),val(-1)),_ans)", expr));
        }catch(final MdsException e){
            return "<" + e.getMessage() + ">";
        }
    }

    public static final Descriptor tdiEvaluate(final Descriptor data) throws MdsException {
        if(data == null) return null;
        return Database.mds.evaluate(data.decompile());
    }

    public static final Descriptor tdiEvaluate(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return null;
        return Database.mds.evaluate(expr);
    }

    public static final ByteBuffer tdiSerialize(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return null;
        return Database.mds.mdsIO(expr, true).body;
    }

    private static final void updateCurrent() throws MdsException {
        try{
            Database.cshot = Database.mds.getLong("$SHOT");
            // if(Database.cshot < -1) Database.cshot &= 0xFFFFFFFFl;
            Database.cexpt = Database.mds.getString("$EXPT").trim();
        }catch(final MdsException de){
            Database.cshot = 0;
            Database.cexpt = null;
        }
    }
    private final Connection con;
    private final String     expt;
    private boolean          is_open = false;
    private final int        mode;
    private final long       shot;

    public Database(final String provider) throws MdsException{
        this.con = Database.setupConnection(provider);
        this.expt = null;
        this.shot = 0;
        this.mode = 0;
    }

    public Database(final String expt, final long shot) throws MdsException{
        this(Database.extractProvider(expt), expt, shot, 0);
    }

    public Database(final String expt, final long shot, final int mode) throws MdsException{
        this(Database.extractProvider(expt), expt, shot, mode);
    }

    public Database(final String provider, final String expt, final long shot, final int mode) throws MdsException{
        this.con = Database.setupConnection(provider);
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
        if(Database.mds != this.con) Database.mds = this.con;
        if(!Database.mds.isConnected()) throw new MdsException("Not connected");
        Database.updateCurrent();
    }

    /* Low level MDS database management routines, will be  masked by the Node class*/
    private final void _open() throws MdsException {
        final int status;
        if(this.isEditable()) status = Database.mds.getInteger(String.format("TreeShr->TreeOpenEdit(ref('%s'),val(%d))", this.expt, this.shot));
        else status = Database.mds.getInteger(String.format("TreeShr->TreeOpen(ref('%s'),val(%d),val(%d))", this.expt, this.shot, this.isReadonly() ? 1 : 0));
        this.handleStatus(status);
        this.is_open = true;
        Database.updateCurrent();
    }

    private final void _open_new() throws MdsException {
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeOpenNew(ref('%s'),val(%d))", this.expt, this.shot));
        this.handleStatus(status);
        this.is_open = true;
        Database.updateCurrent();
    }

    public final Nid addDevice(final String path, final String model) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("COMMA(_ans=-1,_s=TreeShr->_TreeAddConglom(ref('%s'),ref('%s'),ref(_ans)),_s)", path, model));
        final Nid nid = new Nid(Database.mds.getInteger("_ans"));
        this.handleStatus(status);
        return nid;
    }

    public final Nid addNode(final String name, final int usage) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("COMMA(_ans=-1,_s=TreeShr->TreeAddNode(ref('%s'),ref(_ans),val(%d)),_s)", name, usage));
        final Nid nid = new Nid(Database.mds.getInteger("_ans"));
        this.handleStatus(status);
        if(usage == NodeInfo.USAGE_SUBTREE) this.setSubtree(nid);
        return nid;
    }

    public final String[] allTags(final Nid nid) throws MdsException {
        this._checkContext();
        final String str = Database.mds.getString(String.format("COMMA(_n=0,_c=0Q,_ans='',_tag='',_i=0,WHILE(AND(TreeShr->TreeFindTagWildDsc(ref('***'),ref(_n),ref(_c),xd(_tag)),_i<%d)) IF(OR(%d==0,_n==%d)) _ans=COMMA(_i++,_ans//','//_tag),_ans)", nid.getValue(), nid.getValue()));
        if(str == null) return new String[0];
        return str.substring(1).split(",");
    }

    public final void clearFlags(final Nid nid, final int flags) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeSetNciItm(val(%d),val(2),val(%d))", nid.getValue(), flags & 0x7FFFFFFC));;
        this.handleStatus(status);
    }

    public final void close() throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeClose(ref('%s'),val(%d))", this.expt, this.shot));
        this.handleStatus(status);
        this.is_open = false;
        Database.updateCurrent();
    }

    public final Descriptor compile(final String expr) throws MdsException {
        this._checkContext();
        return Database.tdiCompile(expr);
    }

    public final void create(final long shot) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeCreateTreeFiles(ref('%s'),val(%d),val(%d))", this.expt, this.shot, shot));
        this.handleStatus(status);
    }

    public final String decompile(final Descriptor data) throws MdsException {
        this._checkContext();
        return Database.tdiDecompile(data);
    }

    public String decompile_data(final Nid nid) throws MdsException {//
        return Database.mds.getString(String.format("COMMA(_ans=*,TreeShr->TreeGetRecord(val(%d),xd(_ans)),TdiShr->TdiDecompile(xd(_ans),xd(_ans),val(-1)),_ans)", nid.getValue()));
    }

    public final int doAction(final Nid nid) throws MdsException {
        this._checkContext();
        return Database.mds.getInteger(String.format("TCL('dispatch/nowait '//GETNCI(%d,'FULL_PATH'))", nid.getValue()));
    }

    public final void doDeviceMethod(final Nid nid, final String method) throws MdsException {
        this._checkContext();
        // TODO:TreeShr->TreeDoMethod
        throw new MdsException("No implementation");
    }

    public final Descriptor evaluate(final Descriptor data) throws MdsException {
        this._checkContext();
        return Database.tdiEvaluate(data);
    }

    public final Descriptor evaluate(final String expr) throws MdsException {
        this._checkContext();
        return Database.tdiEvaluate(expr);
    }

    public final void executeDelete() throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger("TreeShr->TreeDeleteNodeExecute()");
        this.handleStatus(status);
    }

    @Override
    protected final void finalize() throws Throwable {
        try{
            this.close();
        }finally{
            super.finalize();
        }
    }

    public final long getCurrentShot() throws MdsException {
        return this.getCurrentShot(this.expt);
    }

    public final long getCurrentShot(final String expt) throws MdsException {
        return Database.mds.getInteger(String.format("TreeShr->TreeGetCurrentShotId('%s')", expt));
    }

    public final Descriptor getData(final Nid nid) throws MdsException {
        return Database.mds.mdsValue(String.format("COMMA(_ans=*,TreeShr->TreeGetRecord(val(%d),xd(_ans)),_ans)", nid.getValue()), Descriptor.class);
    }

    public final Nid getDefault() throws MdsException {
        this._checkContext();
        return new Nid(Database.mds.getInteger("COMMA(_ans=-1L,TreeShr->TreeGetDefaultNid(ref(_ans)),_ans)"));
    }

    public final int getFlags(final Nid nid) throws MdsException {
        this._checkContext();
        return Database.mds.getInteger(String.format("LONG(GETNCI(%d,'GET_FLAGS'))", nid.getValue()));
    }

    public final NodeInfo getInfo(final Nid nid) throws MdsException {
        this._checkContext();
        final int[] I = Database.mds.getIntegerArray(String.format("_ans=%d;LONG([GETNCI(_ans,'CLASS'),GETNCI(_ans,'DTYPE'),GETNCI(_ans,'USAGE'),GETNCI(_ans,'GET_FLAGS'),GETNCI(_ans,'OWNER_ID'),GETNCI(_ans,'LENGTH'),IF_ERROR(SHAPE(GETNCI(GETNCI(_ans,'CONGLOMERATE_NID'),'NID_NUMBER'))[0],0),GETNCI(_ans,'CONGLOMERATE_ELT')])", nid.getValue()));
        final String date = Database.mds.getString(String.format("DATE_TIME(GETNCI(%d,'TIME_INSERTED'))", nid.getValue())).substring(0, 23).trim(); // TODO: only to compensate issue in DATA_TIME build-in
        final String node_name = Database.mds.getString(String.format("GETNCI(%d,'NODE_NAME')", nid.getValue()));
        final String fullpath = Database.mds.getString(String.format("GETNCI(%d,'FULLPATH')", nid.getValue()));
        final String minpath = Database.mds.getString(String.format("GETNCI(%d,'MINPATH')", nid.getValue()));
        final String path = Database.mds.getString(String.format("GETNCI(%d,'PATH')", nid.getValue()));
        return new NodeInfo(nid.getValue(), (byte)I[0], (byte)I[1], (byte)I[2], I[3], I[4], I[5], I[6], I[7], date, node_name, fullpath, minpath, path);
    }

    public final String getMdsMessage(final int status) {
        try{
            return Database.mds.getString(String.format("GetMsg(%d)", status));
        }catch(final Exception e){
            return e.getMessage();
        }
    }

    public final Nid[] getMembers(final Nid nid) throws MdsException {
        this._checkContext();
        return Nid.getArrayOfNids(Database.mds.getIntegerArray(String.format("IF_ERROR(GETNCI(GETNCI(%d,'MEMBER_NIDS'),'NID_NUMBER'),[])", nid.getValue())));
    }

    public final String getName() {
        return this.expt;
    }

    public final int getOpcode(final String name) throws MdsException {
        return Database.mds.getInteger(String.format("mdsGetOpcode(%s)", name));
    }

    public final String getOriginalPartName(final Nid nid) throws MdsException {
        this._checkContext();
        return Database.mds.getString(String.format("GETNCI(%d,'ORIGINAL_PART_NAME')", nid.getValue()));
    }

    public final String getProvider() {
        return this.con.getProvider();
    }

    public final Descriptor getRecord(final Nid nid) throws MdsException {// _ans=*,TreeShr->TreeGetRecord(val(%d),xd(_ans)),_ans
        this._checkContext();
        return Database.tdiEvaluate(String.format("GETNCI(%d,'RECORD')", nid.getValue()));
    }

    public Descriptor getSegment(final Nid nid, final int segment) throws MdsException {
        return Database.tdiEvaluate(String.format("GetSegment(%d,%d)", nid.getValue(), segment));
    }

    public final long getShot() {
        return this.shot;
    }

    public final Nid[] getSons(final Nid nid) throws MdsException {
        this._checkContext();
        return Nid.getArrayOfNids(Database.mds.getIntegerArray(String.format("IF_ERROR(GETNCI(GETNCI(%d,'CHILDREN_NIDS'),'NID_NUMBER'),[])", nid.getValue())));
    }

    public final String[] getTags(final Nid nid) throws MdsException {
        if(nid != null) return this.getTags(nid, "***", 255);
        final Map<String, Nid> taglist = this.getTagsWild("***", 255);
        final String[] str = new String[taglist.size()];
        final String[] key = taglist.keySet().toArray(str);
        for(int i = 0; i < str.length; i++)
            str[i] = String.format("%s -> %s", key[i], taglist.get(key[i]));
        return str;
    }

    public final String[] getTags(final Nid nid, final String search, final int max) throws MdsException {
        this._checkContext();
        final String str = Database.mds.getString(String.format("_c=0Q;_ans='';_tag='';_n=0;_i=0;WHILE(AND(TreeShr->TreeFindTagWildDsc(ref('%s'),ref(_n),ref(_c),xd(_tag)),_i<%d)) IF(_n==%d) _ans=COMMA(_i++,_ans//','//_tag);_ans", search, max, nid.getValue()));
        if(str == null || str.length() == 0) return new String[0];
        if(str.charAt(0) == '%') throw new MdsException(str, 0);
        final String[] strs = str.substring(1).split(",");
        for(int i = 0; i < strs.length; i++)
            strs[i] = strs[i].split("::", 2)[1];
        return strs;
    }

    public final TagList getTagsWild(final String search, final int max) throws MdsException {
        this._checkContext();
        final String str = Database.mds.getString(String.format("_c=0Q;_ans='';_tag='';_n=0;_nids='[';_i=0;WHILE(AND(TreeShr->TreeFindTagWildDsc(ref('%s'),ref(_n),ref(_c),xd(_tag)),_i<%d)) STATEMENT(_i++,_ans=_ans//','//_tag,_nids=_nids//TEXT(_n)//',');_ans", search, max));
        if(str == null) return null;
        if(str.charAt(0) == '%') throw new MdsException(str, 0);
        final String[] tags = str.substring(1).split(",");
        final Nid[] nids = Nid.getArrayOfNids(Database.mds.getIntegerArray("_ans=COMPILE(_nids//'*]');_nids=*;_ans"));
        final TagList taglist = new TagList(nids.length);
        for(int i = 0; i < nids.length & i < tags.length; i++)
            taglist.put(tags[i], nids[i]);
        return taglist;
    }

    public final byte[] getType(final String expr) throws MdsException {
        return (byte[])Database.mds.mdsValue(String.format("_ans=As_Is(%s);_ans=[Class(_ans),Kind(_ans)]", expr)).getValue();
    }

    public final Nid[] getWild(final int usage_mask) throws MdsException {
        final Nid[] nids = Nid.getArrayOfNids(Database.mds.getIntegerArray(String.format("_n=0;_ans='[';_c=0Q;WHILE(IAND(TreeShr->TreeFindNodeWild(ref('***'),ref(_n),ref(_c),val(%d)),1)==1) _ans=_ans//TEXT(_n)//',';_ans=COMPILE(_ans//'*]')", 1 << usage_mask)));
        if(nids == null) this.handleStatus(Database.mds.getInteger("_s"));
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
        return 0 == (this.getFlags(nid) & NodeInfo.STATE);
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
        final String xd = (data == null) ? "*" : "xd(as_is(" + data.decompile() + "))";
        final int status = Database.mds.getInteger(String.format("TreeShr->TreePutRecord(val(%d),%s,val(0))", nid.getValue(), xd));
        this.handleStatus(status);
    }

    public final void putRow(final Nid nid, final Descriptor data, final long time) throws MdsException {
        if(data == null){
            this.putData(nid, null);
            return;
        }
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeShr->TreePutRow(val(%d),val(1000),ref(%dQU),xd(%s))", nid.getValue(), time, data.decompile()));
        this.handleStatus(status);
    }

    public final void quit() throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeQuitTree(ref('%s'),val(%d))", this.expt, this.shot));
        this.handleStatus(status);
    }

    public final void renameNode(final Nid nid, final String name) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(new StringBuilder(256).append("TreeShr->TreeRenameNode(val(").append(nid.getValue()).append("),ref('").append(name.replace("\\", "\\\\")).append("'))").toString());
        this.handleStatus(status);
    }

    public final Nid resolve(final Path pad) throws MdsException {
        this._checkContext();
        return (Nid)Database.mds.mdsValue(String.format("GETNCI('%s','NID_NUMBER')", pad.getValue()), Nid.class);
    }

    public final Nid resolveRefSimple(final Nid nid) throws MdsException {
        this._checkContext();
        return new Nid(Database.mds.getInteger(String.format("COMMA(_ans=%d;WHILE(IAND(GETNCI(_ans,'DTYPE'),-2)==192) _ans=GETNCI(_ans,'RECORD'),GETNCI(_ans,'NID_NUMBER'))", nid.getValue())));
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
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeSetCurrentShotId(ref('%s'),val(%d))", expt, shot));
        if((status & 1) == 0) Database.stderr("", new Exception("Could not set current shot id."));
        else Database.stdout(String.format("Current shot of %s set to %d", expt, shot));
    }

    public final void setDefault(final Nid nid) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeSetDefault(GETNCI(%d,'FULLPATH'))", nid.getValue()));
        this.handleStatus(status);
    }

    public final void setEvent(final String event) throws MdsException {
        final int status = Database.mds.getInteger(String.format("MdsShr->MDSEvent(ref('%s'),val(0),val(0))", event));
        this.handleStatus(status);
    }

    public final void setFlags(final Nid nid, final int flags) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeSetNciItm(val(%d),val(1),val(%d))", nid.getValue(), flags & 0x7FFFFFFC));
        this.handleStatus(status);
    }

    public final void setOn(final Nid nid, final boolean on) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeTurn%s(val(%d))", on ? "On" : "Off", nid.getValue()));
        if(status == 265392050) return;// TreeLock-Failure but does the change of state
        this.handleStatus(status);
    }

    public final void setSubtree(final Nid nid) throws MdsException {
        this._checkContext();
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeSetSubtree(val(%d))", nid.getValue()));
        this.handleStatus(status);
    }

    public final void setTags(final Nid nid, final String tags[]) throws MdsException {
        if(tags == null) return;
        this._checkContext();
        final String tagsexpr = (tags.length == 0) ? "[]" : new StringBuilder("[\"").append(String.join("\",\"", tags)).append("\"]").toString();
        final String cmd = String.format("COMMA(_n=%d,_ans=%s,_s=TreeShr->TreeRemoveNodesTags(val(_n)),_i=0;WHILE(IAND(_s,1)==1,_i<%d)_s=TreeShr->TreeAddTag(val(_n),ref(_ans[_i++])),_s)", nid.getValue(), tagsexpr, tags.length);
        final int status = Database.mds.getInteger(cmd);
        this.handleStatus(status);
    }

    public final Nid[] startDelete(final Nid[] nids) throws MdsException {
        this._checkContext();
        final int[] nidnum = new int[nids.length];
        for(int i = 0; i < nids.length; i++)
            nidnum[i] = nids[i].getValue();
        final String array = Arrays.toString(nidnum);
        final int[] nid_nums = Database.mds.getIntegerArray(String.format("COMMA(_ans=%s,_ntd=0,FOR(_i=0,_i<1,_i++,TreeShr->TreeDeleteNodeInitialize(val(_ans[_i]),ref(_ntd),val(_i==0))),_ans=ZERO(_ntd-=1,0),FOR(_i=0,_i<_ntd,_i++,TreeShr->TreeDeleteNodeGetNid(ref(_ans[_i]))),_ans)", array, nids.length, nids.length));
        return Nid.getArrayOfNids(nid_nums);
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
        final int status = Database.mds.getInteger(String.format("TreeShr->TreeWriteTree(ref('%s'),val(%d))", this.expt, this.shot));
        this.handleStatus(status);
    }
}

package devicebeans;

import java.nio.ByteBuffer;
import mds.Mds;
import mds.MdsException;
import mds.MdsShr;
import mds.TREE;
import mds.TdiShr;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Nid;
import mds.mdsip.MdsIp;

public final class Database{
    private static final String extractProvider(final String expt) {
        final String[] parts = System.getenv(String.format("%s_path", expt.toLowerCase())).split("::", 2);
        return (parts.length > 1) ? parts[0] : MdsIp.Provider.DEFAULT_HOST;
    }

    public static final ByteBuffer getByteBuffer(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return null;
        return Mds.getActiveMds().getByteBuffer(expr);
    }

    public static final String getDatabase() throws MdsException {
        final Mds mds = Mds.getActiveMds();
        final String ready = mds.isReady();
        if(ready != null) return ready;
        return mds.tcl("show db").trim();
    }

    private static final void stderr(final String line, final Exception exc) {
        MdsException.stderr(line, exc);
    }

    private static final void stdout(final String line) {
        MdsException.stdout(line);
    }
    private final TdiShr tdishr;
    private final MdsShr mdsshr;
    public final TREE    tree;

    public Database(final Mds mds, final String expt, final int shot, final int mode) throws MdsException{
        this(new TREE(mds.setActive(), expt, shot, mode));
    }

    public Database(final String expt, final int shot) throws MdsException{
        this(Database.extractProvider(expt), expt, shot, 0);
    }

    public Database(final String expt, final int shot, final int mode) throws MdsException{
        this(Database.extractProvider(expt), expt, shot, mode);
    }

    public Database(final String provider, final String expt, final int shot, final int mode) throws MdsException{
        this(MdsIp.sharedConnection(provider, null), expt, shot, mode);
    }

    public Database(final TREE tree){
        this.tree = tree;
        this.mdsshr = new MdsShr(tree.mds);
        this.tdishr = new TdiShr(tree.mds);
        if(!tree.isOpen()) try{
            this.tree.open(TREE.NORMAL);
        }catch(final MdsException e){}
    }

    public final String[] allTags(final Nid nid) throws MdsException {
        final String str = this.tree.mds.getString(String.format("_i=-1;_q=0Q,_a='',_t='',_j=0,WHILE(AND(TreeShr->TreeFindTagWildDsc(ref('***'),ref(_i),ref(_q),xd(_t)),_i<%d)) IF(OR(%d==0,_i==%d)) _a=(_j++;_a//','//_t;);_a", nid.getValue(), nid.getValue()));
        if(str == null) return new String[0];
        return str.substring(1).split(",");
    }

    public final int doAction(final Nid nid) throws MdsException {
        return this.tree.mds.getInteger(String.format("TCL('dispatch/nowait '//GETNCI(%d,'FULL_PATH'))", nid.getValue()));
    }

    public final void doDeviceMethod(final Nid nid, final String method) throws MdsException {
        this.tree.doDeviceMethod(nid.getNidNumber(), method);
    }

    public final int getCurrentShot(final String expt) throws MdsException {
        return this.tree.treeshr.treeGetCurrentShotId(expt);
    }

    public final Nid getDefault() throws MdsException {
        return this.tree.getDefaultNid();
    }

    public final String getMdsMessage(final int status) {
        try{
            return this.mdsshr.mdsGetMsgDsc(status);
        }catch(final MdsException e){
            return e.getMessage();
        }
    }

    public final String getName() {
        return this.tree.expt;
    }

    @SuppressWarnings("static-method")
    public final String getOriginalPartName(final Nid nid) throws MdsException {
        return nid.getNciOriginalPartName();
    }

    public final int getShot() {
        return this.tree.shot;
    }

    public final byte[] getType(final String expr) throws MdsException {
        return this.tree.mds.getByteArray("_a=As_Is(EXECUTE($));_a=[Class(_a),Kind(_a)]", new CString(expr));
    }

    public final Nid[] getWild(final int usage_mask) throws MdsException {
        final Nid[] nids = Nid.getArrayOfNids(this.tree.mds.getIntegerArray(String.format("_i=-1;_a='[';_q=0Q;WHILE(IAND(_s=TreeShr->TreeFindNodeWild(ref('***'),ref(_i),ref(_q),val(%d)),1)==1) _a=_a//TEXT(_i)//',';_a=COMPILE(_a//'*]')", 1 << usage_mask)));
        if(nids == null) MdsException.handleStatus(this.tree.mds.getInteger("_s"));
        return nids;
    }

    public final boolean isEditable() {
        return this.tree.isEditable();
    }

    public final boolean isOpen() {
        return this.tree.isOpen();
    }

    public final void setCurrentShot(final String expt, final int shot) throws MdsException {
        final int status = this.tree.treeshr.treeSetCurrentShotId(expt, shot);
        if((status & 1) == 0) Database.stderr("", new Exception("Could not set current shot id."));
        else Database.stdout(String.format("Current shot of %s set to %d", expt, shot));
    }

    public final void setEvent(final String event) throws MdsException {
        MdsException.handleStatus(this.mdsshr.mdsEvent(event));
    }

    public final Descriptor tdiCompile(final String expr) throws MdsException {
        return this.tdishr.tdiCompile(expr);
    }

    public final String tdiDecompile(final Descriptor data) throws MdsException {
        return this.tdishr.tdiDecompile(data);
    }

    public final Descriptor tdiEvaluate(final Descriptor data) throws MdsException {
        return this.tdishr.tdiEvaluate(data);
    }

    public final Descriptor tdiExecute(final String expr, final Descriptor... args) throws MdsException {
        return this.tdishr.tdiExecute(expr, args);
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder(64).append(this.tree);
        if(this.tree.mds instanceof MdsIp) sb.append(" on ").append(((MdsIp)this.tree.mds).getProvider());
        return sb.toString();
    }
}

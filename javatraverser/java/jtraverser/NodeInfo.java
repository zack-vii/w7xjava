package jtraverser;

/** class NodeInfo carries all the NCI information */
import java.io.Serializable;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.TREENODE;
import mds.data.descriptor_s.TREENODE.Flags;

@SuppressWarnings("serial")
public class NodeInfo implements Serializable{
    private final String date_inserted, name, fullpath, minpath, path;
    private final byte   dtype, dclass, usage;
    private Flags        flags;
    private final int    nid, owner, length, conglomerate_elt, conglomerate_nids;

    public NodeInfo(final int nid, final byte dclass, final byte dtype, final byte usage, final int flags, final int owner, final int length, final int conglomerate_nids, final int conglomerate_elt, final String date_inserted, final String name, final String fullpath, final String minpath, final String path){
        this.nid = nid;
        this.dclass = dclass;
        this.dtype = dtype;
        this.usage = usage;
        this.flags = new Flags(flags);
        this.owner = owner;
        this.length = length;
        this.conglomerate_nids = conglomerate_nids;
        this.conglomerate_elt = conglomerate_elt;
        this.date_inserted = date_inserted;
        this.name = name.trim();
        this.fullpath = fullpath;
        this.minpath = minpath;
        this.path = path;
    }

    public final int getConglomerateElt() {
        return this.conglomerate_elt;
    }

    public final int getConglomerateNids() {
        return this.conglomerate_nids;
    }

    public final String getDate() {
        return this.date_inserted;
    }

    public final byte getDClass() {
        return this.dclass;
    }

    public final byte getDType() {
        return this.dtype;
    }

    public final Flags getFlags() {
        return this.flags;
    }

    public final String getFullPath() {
        return this.fullpath;
    }

    public final int getLength() {
        return this.length;
    }

    public final String getMinPath() {
        return this.minpath;
    }

    public final String getName() {
        return this.name;
    }

    public final int getOwner() {
        return this.owner;
    }

    public final String getPath() {
        return this.path;
    }

    public final byte getUsage() {
        return this.usage;
    }

    public final boolean isSubTree() {
        return this.usage == TREENODE.USAGE_SUBTREE;
    }

    public final void setFlags(final int flags) {
        this.flags = new Flags(flags);
    }

    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer("<html><table width=\"240\"> <tr><td width=\"60\" align=\"left\"/><nobr>full path:</nobr></td><td align=\"left\">");
        sb.append(this.fullpath);
        sb.append(" (").append(this.nid).append(")");
        sb.append("</td></tr><tr><td align=\"left\" valign=\"top\">Status:</td><td align=\"left\"><nobr>");
        final String sep = "</nobr>, <nobr>";
        if(this.flags.isOn()) sb.append("on");
        else sb.append("off");
        sb.append(sep).append("parent is ");
        if(this.flags.isParentState()) sb.append("off");
        else sb.append("on");
        if(this.flags.isSetup()) sb.append(sep).append("setup");
        if(this.flags.isEssential()) sb.append(sep).append("essential");
        if(this.flags.isCached()) sb.append(sep).append("cached");
        if(this.flags.isVersion()) sb.append(sep).append("version");
        if(this.flags.isSegmented()) sb.append(sep).append("segmented");
        if(this.flags.isWriteOnce()) sb.append(sep).append("write once");
        if(this.flags.isCompressible()) sb.append(sep).append("compressible");
        if(this.flags.isDoNotCompress()) sb.append(sep).append("do not compress");
        if(this.flags.isCompressOnPut()) sb.append(sep).append("compress on put");
        if(this.flags.isNoWriteModel()) sb.append(sep).append("no write model");
        if(this.flags.isNoWriteShot()) sb.append(sep).append("no write shot");
        if(this.flags.isPathReference()) sb.append(sep).append("path reference");
        if(this.flags.isNidReference()) sb.append(sep).append("nid reference");
        if(this.flags.isCompressSegments()) sb.append(sep).append("compress segments");
        if(this.flags.isIncludeInPulse()) sb.append(sep).append("include in pulse");
        sb.append("</nobr></td></tr><tr><td align=\"left\">Data:</td><td align=\"left\">");
        if(this.getLength() == 0) sb.append("<nobr>There is no data stored for this this</nobr>");
        else{
            final String dtype = DTYPE.getName(this.getDType());
            final String dclass = Descriptor.getDClassName(this.getDClass());
            sb.append("<nobr>").append(dtype + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(dclass).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(this.getLength()).append(" Bytes</nobr>");
            sb.append("</td></tr><tr><td align=\"left\">Inserted:</td><td align=\"left\">");
            sb.append(this.getDate());
        }
        return sb.append("</td></tr></table></html>").toString();
    }
}

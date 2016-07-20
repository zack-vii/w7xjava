package jtraverser;

/** class NodeInfo carries all the NCI information */
import java.io.Serializable;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.TREENODE;

@SuppressWarnings("serial")
public class NodeInfo implements Serializable{
    private final String date_inserted, name, fullpath, minpath, path;
    private final byte   dtype, dclass, usage;
    private int          flags;
    private final int    nid, owner, length, conglomerate_elt, conglomerate_nids;

    public NodeInfo(final int nid, final byte dclass, final byte dtype, final byte usage, final int flags, final int owner, final int length, final int conglomerate_nids, final int conglomerate_elt, final String date_inserted, final String name, final String fullpath, final String minpath, final String path){
        this.nid = nid;
        this.dclass = dclass;
        this.dtype = dtype;
        this.usage = usage;
        this.flags = flags;
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

    public final int getFlags() {
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

    public final boolean isCached() {
        return (this.flags & TREENODE.CACHED) != 0;
    }

    public final boolean isCompressible() {
        return (this.flags & TREENODE.COMPRESSIBLE) != 0;
    }

    public final boolean isCompressOnPut() {
        return (this.flags & TREENODE.COMPRESS_ON_PUT) != 0;
    }

    public final boolean isCompressSegments() {
        return (this.flags & TREENODE.COMPRESS_SEGMENTS) != 0;
    }

    public final boolean isDoNotCompress() {
        return (this.flags & TREENODE.DO_NOT_COMPRESS) != 0;
    }

    public final boolean isEssential() {
        return (this.flags & TREENODE.ESSENTIAL) != 0;
    }

    public final boolean isIncludeInPulse() {
        return (this.flags & TREENODE.INCLUDE_IN_PULSE) != 0;
    }

    public final boolean isNidReference() {
        return (this.flags & TREENODE.NID_REFERENCE) != 0;
    }

    public final boolean isNoWriteModel() {
        return (this.flags & TREENODE.NO_WRITE_MODEL) != 0;
    }

    public final boolean isNoWriteShot() {
        return (this.flags & TREENODE.NO_WRITE_SHOT) != 0;
    }

    public final boolean isOn() {
        return !this.isState();
    }

    public final boolean isParentOn() {
        return !this.isParentState();
    }

    public final boolean isParentState() {
        return (this.flags & TREENODE.PARENT_STATE) != 0;
    }

    public final boolean isPathReference() {
        return (this.flags & TREENODE.PATH_REFERENCE) != 0;
    }

    public final boolean isSegmented() {
        return (this.flags & TREENODE.SEGMENTED) != 0;
    }

    public final boolean isSetup() {
        return (this.flags & TREENODE.SETUP) != 0;
    }

    public final boolean isState() {
        return (this.flags & TREENODE.STATE) != 0;
    }

    public final boolean isSubTree() {
        return (this.usage & TREENODE.USAGE_SUBTREE) != 0;
    }

    public final boolean isVersion() {
        return (this.flags & TREENODE.VERSION) != 0;
    }

    public final boolean isWriteOnce() {
        return (this.flags & TREENODE.WRITE_ONCE) != 0;
    }

    public final void setFlags(final int flags) {
        this.flags = flags;
    }

    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer("<html><table width=\"240\"> <tr><td width=\"60\" align=\"left\"/><nobr>full path:</nobr></td><td align=\"left\">");
        sb.append(this.fullpath);
        sb.append(" (").append(this.nid).append(")");
        sb.append("</td></tr><tr><td align=\"left\" valign=\"top\">Status:</td><td align=\"left\"><nobr>");
        final String sep = "</nobr>, <nobr>";
        if(this.isOn()) sb.append("on");
        else sb.append("off");
        sb.append(sep).append("parent is ");
        if(this.isParentState()) sb.append("off");
        else sb.append("on");
        if(this.isSetup()) sb.append(sep).append("setup");
        if(this.isEssential()) sb.append(sep).append("essential");
        if(this.isCached()) sb.append(sep).append("cached");
        if(this.isVersion()) sb.append(sep).append("version");
        if(this.isSegmented()) sb.append(sep).append("segmented");
        if(this.isWriteOnce()) sb.append(sep).append("write once");
        if(this.isCompressible()) sb.append(sep).append("compressible");
        if(this.isDoNotCompress()) sb.append(sep).append("do not compress");
        if(this.isCompressOnPut()) sb.append(sep).append("compress on put");
        if(this.isNoWriteModel()) sb.append(sep).append("no write model");
        if(this.isNoWriteShot()) sb.append(sep).append("no write shot");
        if(this.isPathReference()) sb.append(sep).append("path reference");
        if(this.isNidReference()) sb.append(sep).append("nid reference");
        if(this.isCompressSegments()) sb.append(sep).append("compress segments");
        if(this.isIncludeInPulse()) sb.append(sep).append("include in pulse");
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

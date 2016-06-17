package jTraverser;

/** class NodeInfo carries all the NCI information */
import java.io.Serializable;
import mds.data.descriptor.Descriptor;

@SuppressWarnings("serial")
public class NodeInfo implements Serializable{
    public static final int  CACHED              = 1 << 3;
    public static final int  COMPRESS_ON_PUT     = 1 << 10;
    public static final int  COMPRESS_SEGMENTS   = 1 << 16;
    public static final int  COMPRESSIBLE        = 1 << 8;
    public static final int  DO_NOT_COMPRESS     = 1 << 9;
    public static final int  ESSENTIAL           = 1 << 2;
    public static final int  INCLUDE_IN_PULSE    = 1 << 15;
    public static final int  NID_REFERENCE       = 1 << 14;
    public static final int  NO_WRITE_MODEL      = 1 << 11;
    public static final int  NO_WRITE_SHOT       = 1 << 12;
    public static final int  PARENT_STATE        = 1 << 1;
    public static final int  PATH_REFERENCE      = 1 << 13;
    public static final int  SEGMENTED           = 1 << 5;
    public static final int  SETUP               = 1 << 6;
    public static final int  STATE               = 1 << 0;
    public static final byte USAGE_ACTION        = 2;
    public static final byte USAGE_ANY           = 0;
    public static final byte USAGE_AXIS          = 10;
    public static final byte USAGE_COMPOUND_DATA = 12;
    public static final byte USAGE_DEVICE        = 3;
    public static final byte USAGE_DISPATCH      = 4;
    public static final byte USAGE_MAXIMUM       = 12;
    public static final byte USAGE_NONE          = 1;
    public static final byte USAGE_NUMERIC       = 5;
    public static final byte USAGE_SIGNAL        = 6;
    public static final byte USAGE_STRUCTURE     = 1;
    public static final byte USAGE_SUBTREE       = 11;
    public static final byte USAGE_TASK          = 7;
    public static final byte USAGE_TEXT          = 8;
    public static final byte USAGE_WINDOW        = 9;
    public static final int  VERSION             = 1 << 4;
    public static final int  WRITE_ONCE          = 1 << 7;
    private final String     date_inserted, name, fullpath, minpath, path;
    private final byte       dtype, dclass, usage;
    private int              flags;
    private final int        nid, owner, length, conglomerate_elt, conglomerate_nids;

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
        return (this.flags & NodeInfo.CACHED) != 0;
    }

    public final boolean isCompressible() {
        return (this.flags & NodeInfo.COMPRESSIBLE) != 0;
    }

    public final boolean isCompressOnPut() {
        return (this.flags & NodeInfo.COMPRESS_ON_PUT) != 0;
    }

    public final boolean isCompressSegments() {
        return (this.flags & NodeInfo.COMPRESS_SEGMENTS) != 0;
    }

    public final boolean isDoNotCompress() {
        return (this.flags & NodeInfo.DO_NOT_COMPRESS) != 0;
    }

    public final boolean isEssential() {
        return (this.flags & NodeInfo.ESSENTIAL) != 0;
    }

    public final boolean isIncludeInPulse() {
        return (this.flags & NodeInfo.INCLUDE_IN_PULSE) != 0;
    }

    public final boolean isNidReference() {
        return (this.flags & NodeInfo.NID_REFERENCE) != 0;
    }

    public final boolean isNoWriteModel() {
        return (this.flags & NodeInfo.NO_WRITE_MODEL) != 0;
    }

    public final boolean isNoWriteShot() {
        return (this.flags & NodeInfo.NO_WRITE_SHOT) != 0;
    }

    public final boolean isOn() {
        return !this.isState();
    }

    public final boolean isParentOn() {
        return !this.isParentState();
    }

    public final boolean isParentState() {
        return (this.flags & NodeInfo.PARENT_STATE) != 0;
    }

    public final boolean isPathReference() {
        return (this.flags & NodeInfo.PATH_REFERENCE) != 0;
    }

    public final boolean isSegmented() {
        return (this.flags & NodeInfo.SEGMENTED) != 0;
    }

    public final boolean isSetup() {
        return (this.flags & NodeInfo.SETUP) != 0;
    }

    public final boolean isState() {
        return (this.flags & NodeInfo.STATE) != 0;
    }

    public final boolean isVersion() {
        return (this.flags & NodeInfo.VERSION) != 0;
    }

    public final boolean isWriteOnce() {
        return (this.flags & NodeInfo.WRITE_ONCE) != 0;
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
            final String dtype = Descriptor.getDTypeName(this.getDType());
            final String dclass = Descriptor.getDClassName(this.getDClass());
            sb.append("<nobr>").append(dtype + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(dclass).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(this.getLength()).append(" Bytes</nobr>");
            sb.append("</td></tr><tr><td align=\"left\">Inserted:</td><td align=\"left\">");
            sb.append(this.getDate());
        }
        return sb.append("</td></tr></table></html>").toString();
    }
}

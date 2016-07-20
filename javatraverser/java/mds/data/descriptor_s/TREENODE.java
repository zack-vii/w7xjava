package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.TreeShr;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_r.Signal;
import mds.mdsip.Connection;

public abstract class TREENODE<T>extends Descriptor_S<T>{
    public static final int  STATE               = 1 << 0;
    public static final int  PARENT_STATE        = 1 << 1;
    public static final int  ESSENTIAL           = 1 << 2;
    public static final int  CACHED              = 1 << 3;
    public static final int  VERSION             = 1 << 4;
    public static final int  SEGMENTED           = 1 << 5;
    public static final int  SETUP               = 1 << 6;
    public static final int  WRITE_ONCE          = 1 << 7;
    public static final int  COMPRESSIBLE        = 1 << 8;
    public static final int  DO_NOT_COMPRESS     = 1 << 9;
    public static final int  COMPRESS_ON_PUT     = 1 << 10;
    public static final int  NO_WRITE_MODEL      = 1 << 11;
    public static final int  NO_WRITE_SHOT       = 1 << 12;
    public static final int  PATH_REFERENCE      = 1 << 13;
    public static final int  NID_REFERENCE       = 1 << 14;
    public static final int  INCLUDE_IN_PULSE    = 1 << 15;
    public static final int  COMPRESS_SEGMENTS   = 1 << 16;
    public static final byte USAGE_NONE          = 1;
    public static final byte USAGE_MAXIMUM       = 12;
    public static final byte USAGE_ANY           = 0;
    public static final byte USAGE_STRUCTURE     = 1;
    public static final byte USAGE_ACTION        = 2;
    public static final byte USAGE_DEVICE        = 3;
    public static final byte USAGE_DISPATCH      = 4;
    public static final byte USAGE_NUMERIC       = 5;
    public static final byte USAGE_SIGNAL        = 6;
    public static final byte USAGE_TASK          = 7;
    public static final byte USAGE_TEXT          = 8;
    public static final byte USAGE_WINDOW        = 9;
    public static final byte USAGE_AXIS          = 10;
    public static final byte USAGE_SUBTREE       = 11;
    public static final byte USAGE_COMPOUND_DATA = 12;
    private final Connection connection;

    public TREENODE(final byte dtype, final ByteBuffer data){
        super(dtype, data);
        this.connection = Connection.getActiveConnection();
    }

    public TREENODE(final ByteBuffer b){
        super(b);
        this.connection = Connection.getActiveConnection();
    }

    public final Connection getConnection() {
        return this.connection;
    }

    public final int getFlags() throws MdsException {
        return this.getNci("FLAGS").toInt();
    }

    public final String getFullPath() throws MdsException {
        return this.getNci("FULLPATH").toString();
    }

    public final String getMinPath() throws MdsException {
        return this.getNci("MINPATH").toString();
    }

    public final Descriptor getNci(final String name) throws MdsException {
        return this.connection.getDescriptor("GETNCI($,$)", this, new CString(name));
    }

    public final int getNidNumber() throws MdsException {
        final Object value = this.getValue();
        if(value instanceof Number) return ((Number)value).intValue();
        return this.getNci("NID_NUMBER").toInt();
    }

    public final int getNumSegments() throws MdsException {
        return new TreeShr(this.connection).treeGetNumSegments(this.getNidNumber());
    }

    public final String getPath() throws MdsException {
        return this.getNci("PATH").toString();
    }

    public final Descriptor getRecord() {
        try{
            return this.getTreeShr().treeGetRecord(this.getNidNumber());
        }catch(final MdsException e){
            e.printStackTrace();
            return null;
        }
    }

    public final Signal getSegment(final int idx) {
        try{
            return this.getTreeShr().treeGetSegment(this.getNidNumber(), idx);
        }catch(final MdsException e){
            e.printStackTrace();
            return null;
        }
    }

    public final TreeShr getTreeShr() {
        return new TreeShr(this.getConnection());
    }

    public final Descriptor getXNci(final String name) {
        try{
            return this.getTreeShr().treeGetXNci(this.getNidNumber(), name);
        }catch(final MdsException e){
            e.printStackTrace();
            return Missing.NEW;
        }
    }
}

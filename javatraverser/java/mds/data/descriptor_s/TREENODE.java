package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.TreeShr;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_r.Signal;
import mds.mdsip.Connection;

public abstract class TREENODE<T>extends Descriptor_S<T>{
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

    public final String getFullPath() throws MdsException {
        return this.connection.getString("GETNCI($,'FULLPATH')", this);
    }

    public final String getMinPath() throws MdsException {
        return this.connection.getString("GETNCI($,'MINPATH')", this);
    }

    public final int getNidNumber() {
        final Object value = this.getValue();
        if(value instanceof Number) return ((Number)value).intValue();
        try{
            return this.connection.getInteger("GETNCI($,'NID_NUMBER')", this);
        }catch(final MdsException e){
            e.printStackTrace();
            return -1;
        }
    }

    public final int getNumSegments() {
        try{
            return new TreeShr(this.connection).treeGetNumSegments(this.getNidNumber());
        }catch(final MdsException e){
            e.printStackTrace();
            return 0;
        }
    }

    public final String getPath() throws MdsException {
        return this.connection.getString("GETNCI($,'PATH')", this);
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

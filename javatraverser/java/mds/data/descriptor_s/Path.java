package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.mdsip.Connection;

public final class Path extends TREENODE<String>{
    public Path(final ByteBuffer b){
        super(b);
    }

    public Path(final String path){
        super(DTYPE.PATH, ByteBuffer.wrap(path.getBytes()).order(Descriptor.BYTEORDER));
    }

    public Path(final String path, final Connection connection){
        super(DTYPE.PATH, ByteBuffer.wrap(path.getBytes()).order(Descriptor.BYTEORDER), connection);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return pout.append(this.getValue());
    }

    @Override
    public final int getNidNumber() throws MdsException {
        return this.getNciNidNumber();
    }

    @Override
    protected String getValue(final ByteBuffer b) {
        final byte[] buf = new byte[this.length];
        b.get(buf);
        return new String(buf);
    }

    public final Nid toNid() throws MdsException {
        return new Nid(this.getNidNumber(), this.connection);
    }
}

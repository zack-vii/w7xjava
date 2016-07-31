package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.TREE;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Path extends NODE<String>{
    public Path(final ByteBuffer b){
        super(b);
    }

    public Path(final String path){
        super(DTYPE.PATH, ByteBuffer.wrap(path.getBytes()).order(Descriptor.BYTEORDER));
    }

    public Path(final String path, final TREE tree){
        super(DTYPE.PATH, ByteBuffer.wrap(path.getBytes()).order(Descriptor.BYTEORDER), tree);
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

    @Override
    public final Path toFullPath() throws MdsException {
        return new Path(this.getNciFullPath(), this.tree);
    }

    @Override
    public final Path toMinPath() throws MdsException {
        return new Path(this.getNciMinPath(), this.tree);
    }

    @Override
    public final Nid toNid() throws MdsException {
        return new Nid(this.getNciNidNumber(), this.tree);
    }

    @Override
    public final Path toPath() throws MdsException {
        return new Path(this.getNciPath(), this.tree);
    }
}

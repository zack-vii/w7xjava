package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.Descriptor_S;

public final class Ident extends Descriptor_S<String>{
    public Ident(final ByteBuffer b){
        super(b);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return pout.append(this.getValue());
    }

    @Override
    public final String getValue(final ByteBuffer b) {
        final byte[] buf = new byte[this.length];
        b.get(buf);
        return new String(buf);
    }
}

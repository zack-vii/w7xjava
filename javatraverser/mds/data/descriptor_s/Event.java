package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.Descriptor_S;

/** depreciated **/
@Deprecated
public final class Event extends Descriptor_S<String>{
    public Event(final ByteBuffer b){
        super(b);
    }

    @Override
    public final String decompile() {
        return this.getValue();
    }

    @Override
    public final String getValue(final ByteBuffer b) {
        final byte[] buf = new byte[this.length];
        b.get(buf);
        return new String(buf);
    }
}

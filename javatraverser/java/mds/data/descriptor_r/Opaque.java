package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Opaque extends BUILD{
    public Opaque(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Opaque(final Descriptor data, final Descriptor opaque_type){
        super(DTYPE.OPAQUE, null, new Descriptor[]{data,});
    }

    @Override
    public final Descriptor getData() {
        return this.getDescriptor(0);
    }

    public final Descriptor getOpaqueType() {
        return this.getDescriptor(1);
    }
}

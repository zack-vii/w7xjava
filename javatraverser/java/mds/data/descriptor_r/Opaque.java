package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.Descriptor;

public final class Opaque extends BUILD{
    public Opaque(final ByteBuffer b) throws MdsException{
        super(b);
    }

    /*
    public Opaque(final Descriptor data, final Descriptor error){
        super(Opaque.DTYPE, (byte)2);
        this.dscptrs[0] = data;
        this.dscptrs[1] = error;
    }
    */
    public final Descriptor getData() {
        return this.getDescriptor(0);
    }

    public final Descriptor getError() {
        return this.getDescriptor(1);
    }
}

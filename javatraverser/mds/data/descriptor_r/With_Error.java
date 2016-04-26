package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class With_Error extends Descriptor_R{
    public With_Error(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public With_Error(final Descriptor data, final Descriptor opaque_type){
        super(DTYPE.WITH_ERROR, (byte)2, null);
        this.dscptrs[0] = data;
        this.dscptrs[1] = opaque_type;
    }

    public final Descriptor getData() {
        return this.dscptrs[0];
    }

    public final Descriptor getOpaqueType() {
        return this.dscptrs[1];
    }
}

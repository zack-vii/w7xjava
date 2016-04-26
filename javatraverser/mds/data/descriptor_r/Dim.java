package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Dim extends Descriptor_R{
    public Dim(final ByteBuffer b) throws MdsException{
        super(b);
    }

    /*
    public Dimension(final Descriptor window, final Descriptor axis){
        super(Dimension.DTYPE, (byte)2);
        this.dscptrs[0] = window;
        this.dscptrs[1] = axis;
    }
    */
    public final Descriptor getAxis() {
        return this.dscptrs[1];
    }

    public final Descriptor getWindow() {
        return this.dscptrs[0];
    }
}

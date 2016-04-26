package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

/** depreciated **/
@Deprecated
public final class Slope extends Descriptor_R{
    public Slope(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public final Descriptor getBegin() {
        return this.dscptrs[1];
    }

    public final Descriptor getEnding() {
        return this.dscptrs[2];
    }

    public final Descriptor getSlope() {
        return this.dscptrs[0];
    }
}

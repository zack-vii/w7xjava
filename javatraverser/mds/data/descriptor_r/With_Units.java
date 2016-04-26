package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class With_Units extends Descriptor_R{
    public With_Units(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public With_Units(final Descriptor data, final Descriptor units){
        super(DTYPE.WITH_UNITS, (byte)2, null);
        this.dscptrs[0] = data;
        this.dscptrs[1] = units;
    }

    public final Descriptor getData() {
        return this.dscptrs[0];
    }

    public final Descriptor getUnits() {
        return this.dscptrs[1];
    }
}

package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Window extends Descriptor_R{
    public Window(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Window(final Descriptor startingidx, final Descriptor endingidx, final Descriptor valueat0){
        super(DTYPE.WINDOW, (byte)3, null);
        this.dscptrs[0] = startingidx;
        this.dscptrs[1] = endingidx;
        this.dscptrs[2] = valueat0;
    }

    public final Descriptor getEndingIdx() {
        return this.dscptrs[1];
    }

    public final Descriptor getStartingIdx() {
        return this.dscptrs[0];
    }

    public final Descriptor getValueAtIdx0() {
        return this.dscptrs[2];
    }
}

package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.Descriptor;

/** depreciated **/
@Deprecated
public final class Slope extends BUILD{
    public Slope(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public final Descriptor getBegin() {
        return this.getDescriptor(1);
    }

    public final Descriptor getEnding() {
        return this.getDescriptor(2);
    }

    @Override
    public final int[] getShape() {
        return new int[]{(int)((this.getEnding().toFloat() - this.getBegin().toFloat()) / this.getSlope().toFloat())};
    }

    public final Descriptor getSlope() {
        return this.getDescriptor(0);
    }
}

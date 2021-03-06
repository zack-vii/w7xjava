package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Window extends BUILD{
    public Window(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Window(final Descriptor startingidx, final Descriptor endingidx, final Descriptor valueat0){
        super(DTYPE.WINDOW, null, startingidx, endingidx, valueat0);
    }

    public final Descriptor getEndingIdx() {
        return this.getDescriptor(1);
    }

    @Override
    public final int[] getShape() {
        return new int[]{this.getEndingIdx().toInt() - this.getStartingIdx().toInt()};
    }

    public final Descriptor getStartingIdx() {
        return this.getDescriptor(0);
    }

    public final Descriptor getValueAtIdx0() {
        return this.getDescriptor(2);
    }
}

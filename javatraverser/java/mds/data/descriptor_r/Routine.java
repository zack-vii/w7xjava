package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Routine extends BUILD{
    public Routine(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Routine(final Descriptor time_out, final Descriptor image, final Descriptor routine){
        super(DTYPE.ROUTINE, null, new Descriptor[]{time_out, image, routine});
    }

    public Routine(final Descriptor time_out, final Descriptor image, final Descriptor routine, final Descriptor[] args){
        super(DTYPE.ROUTINE, null, new Descriptor[]{time_out, image, routine}, args);
    }

    public final Descriptor getArgument(final int idx) {
        return this.getDescriptor(3 + idx);
    }

    public final Descriptor[] getArguments() {
        final Descriptor[] desc = new Descriptor[this.ndesc - 3];
        for(int i = 0; i < desc.length; i++)
            desc[i] = this.getDescriptor(i + 3);
        return desc;
    }

    public final Descriptor getImage() {
        return this.getDescriptor(1);
    }

    public final Descriptor getRoutine() {
        return this.getDescriptor(2);
    }

    public final Descriptor getTimeOut() {
        return this.getDescriptor(0);
    }
}

package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Method extends BUILD{
    public Method(final ByteBuffer bb) throws MdsException{
        super(bb);
    }

    public Method(final Descriptor time_out, final Descriptor method, final Descriptor object, final byte nargs){
        super(DTYPE.METHOD, null, new Descriptor[]{time_out, method, object});
    }

    public Method(final Descriptor time_out, final Descriptor method, final Descriptor object, final Descriptor[] args){
        super(DTYPE.METHOD, null, new Descriptor[]{time_out, method, object}, args);
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

    public final Descriptor getMethod() {
        return this.getDescriptor(1);
    }

    public final Descriptor getObject() {
        return this.getDescriptor(2);
    }

    public final Descriptor getTimeOut() {
        return this.getDescriptor(0);
    }
}

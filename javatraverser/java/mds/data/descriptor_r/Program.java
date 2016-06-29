package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

/** depreciated **/
@Deprecated
public final class Program extends BUILD{
    public Program(final ByteBuffer bb) throws MdsException{
        super(bb);
    }

    public Program(final Descriptor time_out, final Descriptor program){
        super(DTYPE.PROGRAM, null, new Descriptor[]{time_out, program});
    }

    public final Descriptor getProgram() {
        return this.getDescriptor(1);
    }

    public final Descriptor getTimeOut() {
        return this.getDescriptor(0);
    }
}

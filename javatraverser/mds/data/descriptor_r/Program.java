package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

/** depreciated **/
@Deprecated
public final class Program extends Descriptor_R{
    public Program(final ByteBuffer bb) throws MdsException{
        super(bb);
    }

    public Program(final Descriptor time_out, final Descriptor program){
        super(DTYPE.PROGRAM, (byte)2, null);
        this.dscptrs[0] = time_out;
        this.dscptrs[1] = program;
    }

    public final Descriptor getProgram() {
        return this.dscptrs[1];
    }

    public final Descriptor getTimeOut() {
        return this.dscptrs[0];
    }
}

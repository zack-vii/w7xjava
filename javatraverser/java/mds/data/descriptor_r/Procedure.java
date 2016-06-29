package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

/** depreciated **/
@Deprecated
public final class Procedure extends BUILD{
    public Procedure(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Procedure(final Descriptor time_out, final Descriptor procedure, final Descriptor language, final byte nargs){
        super(DTYPE.PROCEDURE, null, new Descriptor[]{time_out, procedure, language});
    }

    public Procedure(final Descriptor time_out, final Descriptor procedure, final Descriptor language, final Descriptor[] args){
        super(DTYPE.PROCEDURE, null, new Descriptor[]{time_out, procedure, language}, args);
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

    public final Descriptor getLanguage() {
        return this.getDescriptor(1);
    }

    public final Descriptor getProcedure() {
        return this.getDescriptor(2);
    }

    public final Descriptor getTimeOut() {
        return this.getDescriptor(0);
    }
}

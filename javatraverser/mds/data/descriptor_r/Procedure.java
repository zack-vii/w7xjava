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
        super(DTYPE.PROCEDURE, (byte)(3 + nargs), null);
        this.dscptrs[0] = time_out;
        this.dscptrs[1] = procedure;
        this.dscptrs[2] = language;
    }

    public Procedure(final Descriptor time_out, final Descriptor procedure, final Descriptor language, final Descriptor[] args){
        this(time_out, procedure, language, (byte)(args == null ? 0 : args.length));
        if(args == null) return;
        System.arraycopy(args, 0, this.dscptrs, 3, args.length);
    }

    public final Descriptor getArgument(final int idx) {
        return this.dscptrs[3 + idx];
    }

    public final Descriptor[] getArguments() {
        final Descriptor[] args = new Descriptor[this.dscptrs.length - 3];
        System.arraycopy(this.dscptrs, 3, args, 0, args.length);
        return args;
    }

    public final Descriptor getLanguage() {
        return this.dscptrs[1];
    }

    public final Descriptor getProcedure() {
        return this.dscptrs[2];
    }

    public final Descriptor getTimeOut() {
        return this.dscptrs[0];
    }
}

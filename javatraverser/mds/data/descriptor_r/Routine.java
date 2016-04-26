package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Routine extends Descriptor_R{
    public Routine(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Routine(final Descriptor time_out, final Descriptor image, final Descriptor routine, final byte nargs){
        super(DTYPE.ROUTINE, (byte)(3 + nargs), null);
        this.dscptrs[0] = time_out;
        this.dscptrs[1] = image;
        this.dscptrs[2] = routine;
    }

    public Routine(final Descriptor time_out, final Descriptor image, final Descriptor routine, final Descriptor[] args){
        this(time_out, image, routine, (byte)(args == null ? 0 : args.length));
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

    public final Descriptor getImage() {
        return this.dscptrs[1];
    }

    public final Descriptor getRoutine() {
        return this.dscptrs[2];
    }

    public final Descriptor getTimeOut() {
        return this.dscptrs[0];
    }
}

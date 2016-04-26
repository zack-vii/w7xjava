package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Dispatch extends Descriptor_R<Byte>{
    public static final byte SCHED_ASYNC = 1;
    public static final byte SCHED_COND  = 3;
    public static final byte SCHED_NONE  = 0;
    public static final byte SCHED_SEQ   = 2;

    public Dispatch(final byte type, final Descriptor ident, final Descriptor phase, final Descriptor when, final Descriptor completion){
        super(DTYPE.DISPATCH, (byte)4, new byte[]{type});
        this.dscptrs[0] = ident;
        this.dscptrs[1] = phase;
        this.dscptrs[2] = when;
        this.dscptrs[3] = completion;
    }

    public Dispatch(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public final Descriptor getCompletion() {
        return this.getDscptrs(3);
    }

    public final Descriptor getIdent() {
        return this.getDscptrs(0);
    }

    public final Descriptor getPhase() {
        return this.getDscptrs(1);
    }

    public final byte getType() {
        return this.getValue();
    }

    @Override
    public final Byte getValue(final ByteBuffer b) {
        switch(this.length){
            case 1:
                return b.get();
            case 2:
                return (byte)b.getShort();
            case 4:
                return (byte)b.getInt();
            default:
                return 0;
        }
    }

    public final Descriptor getWhen() {
        return this.getDscptrs(2);
    }
}

package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Dispatch extends BUILD<Byte>{
    public static final byte SCHED_ASYNC = 1;
    public static final byte SCHED_COND  = 3;
    public static final byte SCHED_NONE  = 0;
    public static final byte SCHED_SEQ   = 2;

    public Dispatch(final byte type, final Descriptor ident, final Descriptor phase, final Descriptor when, final Descriptor completion){
        super(DTYPE.DISPATCH, ByteBuffer.allocate(Byte.BYTES).order(ByteOrder.LITTLE_ENDIAN).put(type), new Descriptor[]{ident, phase, when, completion});
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

package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

/** depreciated **/
@Deprecated
public final class Condition extends BUILD<Byte>{
    public static final byte DEPENDENCY_AND   = 10;
    public static final byte DEPENDENCY_OR    = 11;
    public static final byte IGNORE_STATUS    = 9;
    public static final byte IGNORE_UNDEFINED = 8;
    public static final byte NEGATE_CONDITION = 7;

    public Condition(final byte mode, final Descriptor cond){
        super(DTYPE.CONDITION, ByteBuffer.allocate(Byte.BYTES).order(Descriptor.BYTEORDER).put(mode), cond);
    }

    public Condition(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public final Descriptor getCondition() {
        return this.getDescriptor(0);
    }

    public final byte getModifier() {
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
}

package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.Descriptor;

/** depreciated **/
@Deprecated
public final class Dependency extends BUILD<Byte>{
    public Dependency(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public final Descriptor getArg1() {
        return this.getDescriptor(0);
    }

    public final Descriptor getArg2() {
        return this.getDescriptor(1);
    }

    public final byte getOpCode() {
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

package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

/** depreciated **/
@Deprecated
public final class Dependenc extends Descriptor_R<Byte>{
    public Dependenc(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public final Descriptor getArg1() {
        return this.dscptrs[0];
    }

    public final Descriptor getArg2() {
        return this.dscptrs[1];
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

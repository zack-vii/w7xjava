package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Call extends Descriptor_R<Short>{
    public Call(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Call(final short type, final Descriptor image, final Descriptor routine, final byte nargs){
        super(DTYPE.CALL, (byte)(2 + nargs), ByteBuffer.allocate(Short.BYTES).putShort(type).array());
        this.dscptrs[0] = image;
        this.dscptrs[1] = routine;
    }

    public Call(final short dtype, final Descriptor image, final Descriptor routine, final Descriptor[] args){
        this(dtype, image, routine, (byte)(args == null ? 0 : args.length));
        if(args == null) return;
        System.arraycopy(args, 0, this.dscptrs, 2, args.length);
    }

    public final Descriptor getArguments(final int idx) {
        return this.dscptrs[2 + idx];
    }

    public final Descriptor getImage() {
        return this.dscptrs[0];
    }

    public final Descriptor getRoutine() {
        return this.dscptrs[1];
    }

    @Override
    public final Short getValue(final ByteBuffer b) {
        switch(this.length){
            case 2:
                return b.getShort();
            case 1:
                return (short)Byte.toUnsignedInt(b.get());
            case 4:
                return (short)b.getInt();
            default:
                return 0;
        }
    }
}

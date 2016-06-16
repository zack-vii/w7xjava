package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Range extends Descriptor_R{
    public Range(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Range(final Descriptor begin, final Descriptor ending, final Descriptor delta){
        super(DTYPE.RANGE, (byte)3, null);
        this.dscptrs[0] = begin;
        this.dscptrs[1] = ending;
        this.dscptrs[2] = delta;
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        pout.append(DTYPE.getName(DTYPE.RANGE));
        this.addArguments(0, "(", ")", pout, mode);
        return pout;
    }

    public final Descriptor getBegin() {
        return this.dscptrs[0];
    }

    public final Descriptor getDelta() {
        return this.dscptrs[2];
    }

    public final Descriptor getEnding() {
        return this.dscptrs[1];
    }
}

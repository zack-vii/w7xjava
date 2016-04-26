package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Param extends Descriptor_R{
    public Param(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Param(final Descriptor data, final Descriptor help, final Descriptor valid){
        super(DTYPE.PARAM, (byte)3, null);
        this.dscptrs[0] = data;
        this.dscptrs[1] = help;
        this.dscptrs[2] = valid;
    }

    public final Descriptor getData() {
        return this.dscptrs[0];
    }

    /*
    public Param(final Descriptor value, final Descriptor help, final Descriptor validation){
        super(Param.DTYPE, (byte)3);
        this.dscptrs[0] = value;
        this.dscptrs[1] = help;
        this.dscptrs[2] = validation;
    }
    */
    public final Descriptor getHelp() {
        return this.dscptrs[1];
    }

    public final Descriptor getValidation() {
        return this.dscptrs[2];
    }
}

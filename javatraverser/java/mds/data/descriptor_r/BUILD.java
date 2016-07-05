package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public abstract class BUILD<T extends Number>extends Descriptor_R<T>{
    public BUILD(final byte dtype, final ByteBuffer data, final Descriptor[] args){
        super(dtype, data, args);
    }

    public BUILD(final byte dtype, final ByteBuffer data, final Descriptor[] args0, final Descriptor[] args1){
        super(dtype, data, args0, args1);
    }

    public BUILD(final ByteBuffer b){
        super(b);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        final T val = this.getValue();
        pout.append("Build_").append(this.getClass().getSimpleName()).append("(");
        if(val != null) pout.append(val.toString()).append((mode & Descriptor.DECO_X) > 0 ? "," : ", ");
        this.addArguments(0, "", ")", pout, mode);
        return pout;
    }
}

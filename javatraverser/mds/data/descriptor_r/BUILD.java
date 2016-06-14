package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.Descriptor_R;

public abstract class BUILD<T extends Number>extends Descriptor_R<T>{
    public BUILD(final byte dtype, final byte ndesc, final byte[] data){
        super(dtype, ndesc, data);
    }

    public BUILD(final ByteBuffer b) throws MdsException{
        super(b);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout) throws MdsException {
        return this.decompile(prec, pout, ", ", "", ", ", ")");
    }

    public final StringBuilder decompile(final int prec, final StringBuilder pout, final String s1, final String s2, final String s3, final String s4) throws MdsException {
        final T val = this.getValue();
        pout.append("Build_").append(this.getClass().getSimpleName()).append("(");
        if(val != null) pout.append(val.toString()).append(s1);
        this.addArguments(0, s2, s3, s4, pout);
        return pout;
    }

    @Override
    public StringBuilder decompileX(final int prec, final StringBuilder pout) throws MdsException {
        return this.decompile(prec, pout, ",", "\n\t", ",\n\t", "\n)");
    }

    @Override
    public final String toString() {
        return this.decompile();
    }

    @Override
    public final String toStringX() {
        return this.decompileX();
    }
}

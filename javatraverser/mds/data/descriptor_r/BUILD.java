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
    public final String decompile() {
        return this.decompile(", ", "", ", ", ")");
    }

    public final String decompile(final String s1, final String s2, final String s3, final String s4) {
        final String[] args = new String[this.ndesc];
        for(int i = 0; i < this.ndesc; i++)
            args[i] = this.dscptrs[i] == null ? "*" : this.dscptrs[i].decompile();
        final T val = this.getValue();
        final StringBuilder sb = new StringBuilder(256).append("Build_").append(this.getClass().getSimpleName()).append('(');
        if(val != null) sb.append(val.toString()).append(s1);
        return sb.append(s2).append(String.join(s3, args)).append(s4).toString();
    }

    @Override
    public final String decompileX() {
        return this.decompile(",", "\n\t", ",\n\t", "\n)");
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

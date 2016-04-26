package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor_A;

public final class EmptyArray extends Descriptor_A{
    public EmptyArray(){
        super(DTYPE.MISSING, new byte[0], 0);
    }

    public EmptyArray(final ByteBuffer b){
        super(b);
    }

    @Override
    public final String decompile() {
        return "[]";
    }

    @Override
    protected final Object getElement(final ByteBuffer b) {
        return null;
    }

    @Override
    protected final Object[] initArray(final int size) {
        return new Object[0];
    }

    @Override
    public final double toDouble(final Object t) {
        return 0.;
    }

    @Override
    public final float toFloat(final Object t) {
        return 0.f;
    }

    @Override
    public final int toInt(final Object t) {
        return 0;
    }

    @Override
    public final long toLong(final Object t) {
        return 0l;
    }

    @Override
    protected final String TtoString(final Object t) {
        return "*";
    }
}

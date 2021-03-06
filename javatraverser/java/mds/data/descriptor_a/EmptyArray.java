package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;

public final class EmptyArray extends Descriptor_A{
    public static final EmptyArray NEW = new EmptyArray();

    public EmptyArray(){
        super(DTYPE.MISSING, ByteBuffer.allocate(0).order(Descriptor.BYTEORDER));
    }

    public EmptyArray(final ByteBuffer b){
        super(b);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return pout.append("[]");
    }

    @Override
    protected final Object getElement(final ByteBuffer b) {
        return null;
    }

    @Override
    public Descriptor getScalar(final int idx) {
        return null;
    }

    @Override
    protected final String getSuffix() {
        return "";
    }

    @Override
    protected final Object[] initArray(final int size) {
        return new Object[0];
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Object value) {}

    @Override
    public final byte toByte(final Object t) {
        return 0;
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
    public final short toShort(final Object t) {
        return 0;
    }

    @Override
    protected final String TtoString(final Object t) {
        return "*";
    }
}

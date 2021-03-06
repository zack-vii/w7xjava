package mds.data.descriptor_a;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_s.COMPLEX.Complex;
import mds.data.descriptor_s.NUMBER;

public abstract class NUMBERArray<T extends Number>extends Descriptor_A<T>{
    private static final ByteBuffer toByteBuffer(final BigInteger[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * 16).order(Descriptor.BYTEORDER);
        for(final BigInteger value : values){
            final byte[] by = value.or(NUMBER.max128).toByteArray();
            b.put(by, 1, 16);
        }
        return b;
    }

    private static final ByteBuffer toByteBuffer(final Complex[] values) {
        if(values.length == 0) return ByteBuffer.allocate(0);
        final ByteBuffer b;
        if(values[0].real instanceof Double){
            b = ByteBuffer.allocate(values.length * Double.BYTES * 2).order(Descriptor.BYTEORDER);
            for(final Complex value : values)
                b.putDouble((Double)value.real).putDouble((Double)value.imag);
        }else{
            b = ByteBuffer.allocate(values.length * Float.BYTES * 2).order(Descriptor.BYTEORDER);
            for(final Complex value : values)
                b.putFloat((Float)value.real).putDouble((Float)value.imag);
        }
        return b;
    }

    private static final ByteBuffer toByteBuffer(final double[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Double.BYTES).order(Descriptor.BYTEORDER);
        b.asDoubleBuffer().put(values);
        return b;
    }

    private static final ByteBuffer toByteBuffer(final float[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Float.BYTES).order(Descriptor.BYTEORDER);
        b.asFloatBuffer().put(values);
        return b;
    }

    private static final ByteBuffer toByteBuffer(final int[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Integer.BYTES).order(Descriptor.BYTEORDER);
        b.asIntBuffer().put(values);
        return b;
    }

    private static final ByteBuffer toByteBuffer(final long[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Long.BYTES).order(Descriptor.BYTEORDER);
        b.asLongBuffer().put(values);
        return b;
    }

    private static final ByteBuffer toByteBuffer(final short[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Short.BYTES).order(Descriptor.BYTEORDER);
        b.asShortBuffer().put(values);
        return b;
    }

    protected NUMBERArray(final byte dtype, final BigInteger[] values, final int... shape){
        super(dtype, NUMBERArray.toByteBuffer(values), shape.length == 0 ? new int[]{values.length} : shape);
    }

    protected NUMBERArray(final byte dtype, final byte[] values, final int... shape){
        super(dtype, ByteBuffer.wrap(values).order(Descriptor.BYTEORDER), shape.length == 0 ? new int[]{values.length} : shape);
    }

    protected NUMBERArray(final byte dtype, final Complex[] values, final int... shape){
        super(dtype, NUMBERArray.toByteBuffer(values), shape.length == 0 ? new int[]{values.length} : shape);
    }

    protected NUMBERArray(final byte dtype, final double[] values, final int... shape){
        super(dtype, NUMBERArray.toByteBuffer(values), shape.length == 0 ? new int[]{values.length} : shape);
    }

    protected NUMBERArray(final byte dtype, final float[] values, final int... shape){
        super(dtype, NUMBERArray.toByteBuffer(values), shape.length == 0 ? new int[]{values.length} : shape);
    }

    protected NUMBERArray(final byte dtype, final int[] values, final int... shape){
        super(dtype, NUMBERArray.toByteBuffer(values), shape.length == 0 ? new int[]{values.length} : shape);
    }

    protected NUMBERArray(final byte dtype, final long[] values, final int... shape){
        super(dtype, NUMBERArray.toByteBuffer(values), shape.length == 0 ? new int[]{values.length} : shape);
    }

    protected NUMBERArray(final byte dtype, final short[] values, final int... shape){
        super(dtype, NUMBERArray.toByteBuffer(values), shape.length == 0 ? new int[]{values.length} : shape);
    }

    protected NUMBERArray(final ByteBuffer b){
        super(b);
    }

    @Override
    protected StringBuilder decompileT(final StringBuilder pout, final T value) {
        pout.append(value);
        if(!this.format()) pout.append(this.getSuffix());
        return pout;
    }

    protected final String getSuffix() {
        return DTYPE.getSuffix(this.dtype);
    }

    public abstract T parse(String in);

    public final void setValue(final int idx, final T value) {
        final ByteBuffer buf = this.b.duplicate().order(this.b.order());
        final int maxidx = buf.limit() / this.length - 1;
        if(idx > maxidx || idx < 0) return;
        if(idx > 0) buf.position(idx * this.length);
        this.setElement(buf, value);
    }

    @Override
    public byte toByte(final T t) {
        return t.byteValue();
    }

    @Override
    public double toDouble(final T t) {
        return t.doubleValue();
    }

    @Override
    public float toFloat(final T t) {
        return t.floatValue();
    }

    @Override
    public int toInt(final T t) {
        return t.intValue();
    }

    @Override
    public long toLong(final T t) {
        return t.longValue();
    }

    @Override
    public short toShort(final T t) {
        return t.shortValue();
    }

    @Override
    public String toString(final int idx) {
        return this.TtoString(this.getValue(idx));
    }

    @Override
    public String TtoString(final T t) {
        return t.toString();
    }
}

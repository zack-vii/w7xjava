package mds.data.descriptor_a;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_s.COMPLEX.Complex;
import mds.data.descriptor_s.NUMBER;

public abstract class NUMBERArray<T extends Number>extends Descriptor_A<T>{
    private static byte[] toBytes(final BigInteger[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * 16);
        for(final BigInteger value : values){
            final byte[] by = value.or(NUMBER.max128).toByteArray();
            b.put(by, 1, 16);
        }
        return b.array();
    }

    private static byte[] toBytes(final Complex[] values) {
        if(values.length == 0) return new byte[0];
        final ByteBuffer b;
        if(values[0].real instanceof Double){
            b = ByteBuffer.allocate(values.length * Double.BYTES * 2);
            for(final Complex value : values)
                b.putDouble((Double)value.real).putDouble((Double)value.imag);
        }else{
            b = ByteBuffer.allocate(values.length * Float.BYTES * 2);
            for(final Complex value : values)
                b.putFloat((Float)value.real).putDouble((Float)value.imag);
        }
        return b.array();
    }

    private static final byte[] toBytes(final double[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Double.BYTES);
        b.asDoubleBuffer().put(values);
        return b.array();
    }

    private static final byte[] toBytes(final float[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Float.BYTES);
        b.asFloatBuffer().put(values);
        return b.array();
    }

    private static final byte[] toBytes(final int[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Integer.BYTES);
        b.asIntBuffer().put(values);
        return b.array();
    }

    private static final byte[] toBytes(final long[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Long.BYTES);
        b.asLongBuffer().put(values);
        return b.array();
    }

    private static final byte[] toBytes(final short[] values) {
        final ByteBuffer b = ByteBuffer.allocate(values.length * Short.BYTES);
        b.asShortBuffer().put(values);
        return b.array();
    }

    public NUMBERArray(final byte dtype, final BigInteger[] values){
        super(dtype, NUMBERArray.toBytes(values), values.length);
    }

    public NUMBERArray(final byte dtype, final byte[] values){
        super(dtype, values, values.length);
    }

    public NUMBERArray(final byte dtype, final Complex[] values){
        super(dtype, NUMBERArray.toBytes(values), values.length);
    }

    public NUMBERArray(final byte dtype, final double[] values){
        super(dtype, NUMBERArray.toBytes(values), values.length);
    }

    public NUMBERArray(final byte dtype, final float[] values){
        super(dtype, NUMBERArray.toBytes(values), values.length);
    }

    public NUMBERArray(final byte dtype, final int[] values){
        super(dtype, NUMBERArray.toBytes(values), values.length);
    }

    public NUMBERArray(final byte dtype, final long[] values){
        super(dtype, NUMBERArray.toBytes(values), values.length);
    }

    public NUMBERArray(final byte dtype, final short[] values){
        super(dtype, NUMBERArray.toBytes(values), values.length);
    }

    public NUMBERArray(final ByteBuffer b){
        super(b);
    }

    @Override
    public String decompileT(final T t) {
        return t.toString();// new StringBuilder(32).append(this.TtoString(t)).append(this.getSuffix()).toString();
    }

    protected final String getSuffix() {
        return DTYPE.getSuffix(this.dtype);
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
    public String toString(final int idx) {
        return this.TtoString(this.getValue(idx));
    }

    @Override
    public String TtoString(final T t) {
        return t.toString();
    }
}

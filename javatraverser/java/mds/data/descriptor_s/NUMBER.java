package mds.data.descriptor_s;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_s.COMPLEX.Complex;

public abstract class NUMBER<T extends Number>extends Descriptor_S<T>{
    public static final BigInteger max128 = BigInteger.ONE.shiftLeft(128);

    private static ByteBuffer toByteBuffer(final BigInteger value) {
        return ByteBuffer.allocate(16).order(Descriptor.BYTEORDER).put(value.or(NUMBER.max128).toByteArray(), 1, 16);
    }

    private static final ByteBuffer toByteBuffer(final byte value) {
        return ByteBuffer.allocate(Double.BYTES).order(Descriptor.BYTEORDER).put(value);
    }

    private static ByteBuffer toByteBuffer(final Complex value) {
        if(value.real instanceof Double) return NUMBER.toByteBuffer(value.real.doubleValue(), value.imag.doubleValue());
        return NUMBER.toByteBuffer(value.real.floatValue(), value.imag.floatValue());
    }

    private static final ByteBuffer toByteBuffer(final double value) {
        return ByteBuffer.allocate(Double.BYTES).order(Descriptor.BYTEORDER).putDouble(value);
    }

    private static final ByteBuffer toByteBuffer(final double real, final double imag) {
        return ByteBuffer.allocate(Double.BYTES * 2).order(Descriptor.BYTEORDER).putDouble(real).putDouble(imag);
    }

    private static final ByteBuffer toByteBuffer(final float value) {
        return ByteBuffer.allocate(Float.BYTES).order(Descriptor.BYTEORDER).putFloat(value);
    }

    private static final ByteBuffer toByteBuffer(final float real, final float imag) {
        return ByteBuffer.allocate(Float.BYTES * 2).order(Descriptor.BYTEORDER).putFloat(real).putFloat(imag);
    }

    private static final ByteBuffer toByteBuffer(final int value) {
        return ByteBuffer.allocate(Integer.BYTES).order(Descriptor.BYTEORDER).putInt(value);
    }

    private static final ByteBuffer toByteBuffer(final long value) {
        return ByteBuffer.allocate(Long.BYTES).order(Descriptor.BYTEORDER).putLong(value);
    }

    protected NUMBER(final byte dtype, final BigInteger value){
        super(dtype, NUMBER.toByteBuffer(value));
    }

    protected NUMBER(final byte dtype, final byte value){
        super(dtype, NUMBER.toByteBuffer(value));
    }

    protected NUMBER(final byte dtype, final Complex value){
        super(dtype, NUMBER.toByteBuffer(value));
    }

    protected NUMBER(final byte dtype, final double value){
        super(dtype, NUMBER.toByteBuffer(value));
    }

    protected NUMBER(final byte dtype, final float value){
        super(dtype, NUMBER.toByteBuffer(value));
    }

    protected NUMBER(final byte dtype, final int value){
        super(dtype, NUMBER.toByteBuffer(value));
    }

    protected NUMBER(final byte dtype, final long value){
        super(dtype, NUMBER.toByteBuffer(value));
    }

    protected NUMBER(final byte dtype, final short value){
        super(dtype, NUMBER.toByteBuffer(value));
    }

    protected NUMBER(final ByteBuffer b){
        super(b);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return pout.append(this.toString()).append(this.getSuffix());
    }

    protected String getSuffix() {
        return DTYPE.getSuffix(this.dtype);
    }

    @Override
    public double[] toDouble() {
        return new double[]{this.getValue().doubleValue()};
    }

    @Override
    public float[] toFloat() {
        return new float[]{this.getValue().floatValue()};
    }

    @Override
    public int[] toInt() {
        return new int[]{this.getValue().intValue()};
    }

    @Override
    public long[] toLong() {
        return new long[]{this.getValue().longValue()};
    }
}

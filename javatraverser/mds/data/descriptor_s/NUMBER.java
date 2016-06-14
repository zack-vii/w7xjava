package mds.data.descriptor_s;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_s.COMPLEX.Complex;

public abstract class NUMBER<T extends Number>extends Descriptor_S<T>{
    public static final BigInteger max128 = BigInteger.ONE.shiftLeft(128);

    private static byte[] toBytes(final BigInteger value) {
        return ByteBuffer.allocate(16).put(value.or(NUMBER.max128).toByteArray(), 1, 16).array();
    }

    private static final byte[] toBytes(final byte value) {
        return new byte[]{value};
    }

    private static byte[] toBytes(final Complex value) {
        if(value.real instanceof Double) return NUMBER.toBytes(value.real.doubleValue(), value.imag.doubleValue());
        return NUMBER.toBytes(value.real.floatValue(), value.imag.floatValue());
    }

    private static final byte[] toBytes(final double value) {
        return ByteBuffer.allocate(Double.BYTES).putDouble(value).array();
    }

    private static final byte[] toBytes(final double real, final double imag) {
        return ByteBuffer.allocate(Double.BYTES * 2).putDouble(real).putDouble(imag).array();
    }

    private static final byte[] toBytes(final float value) {
        return ByteBuffer.allocate(Float.BYTES).putFloat(value).array();
    }

    private static final byte[] toBytes(final float real, final float imag) {
        return ByteBuffer.allocate(Float.BYTES * 2).putFloat(real).putFloat(imag).array();
    }

    private static final byte[] toBytes(final int value) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
    }

    private static final byte[] toBytes(final long value) {
        return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
    }

    protected NUMBER(final byte dtype, final BigInteger value){
        super(dtype, NUMBER.toBytes(value));
    }

    protected NUMBER(final byte dtype, final byte value){
        super(dtype, NUMBER.toBytes(value));
    }

    protected NUMBER(final byte dtype, final Complex value){
        super(dtype, NUMBER.toBytes(value));
    }

    protected NUMBER(final byte dtype, final double value){
        super(dtype, NUMBER.toBytes(value));
    }

    protected NUMBER(final byte dtype, final float value){
        super(dtype, NUMBER.toBytes(value));
    }

    protected NUMBER(final byte dtype, final int value){
        super(dtype, NUMBER.toBytes(value));
    }

    protected NUMBER(final byte dtype, final long value){
        super(dtype, NUMBER.toBytes(value));
    }

    protected NUMBER(final byte dtype, final short value){
        super(dtype, NUMBER.toBytes(value));
    }

    protected NUMBER(final ByteBuffer b){
        super(b);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout) {
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

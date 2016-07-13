package mds.data.descriptor_s;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_s.COMPLEX.Complex;

public abstract class NUMBER<T extends Number>extends Descriptor_S<T>{
    public static final BigInteger max128 = BigInteger.ONE.shiftLeft(128);

    public static NUMBER make(final Number value) {
        if(value == null) return null;
        if(value instanceof Integer) return new Int32(value.intValue());
        if(value instanceof Long) return new Int64(value.longValue());
        if(value instanceof Float) return new Float32(value.floatValue());
        if(value instanceof Double) return new Float64(value.doubleValue());
        if(value instanceof Short) return new Int16(value.shortValue());
        if(value instanceof Byte) return new Int8(value.byteValue());
        if(value instanceof BigInteger) return new Int128((BigInteger)value);
        return new Float32(value.floatValue());// default
    }

    private static ByteBuffer toByteBuffer(final BigInteger value) {
        return ByteBuffer.allocate(16).order(Descriptor.BYTEORDER).put(value.or(NUMBER.max128).toByteArray(), 0, 16);
    }

    private static final ByteBuffer toByteBuffer(final byte value) {
        return ByteBuffer.allocate(Double.BYTES).order(Descriptor.BYTEORDER).put(0, value);
    }

    private static ByteBuffer toByteBuffer(final Complex value) {
        if(value.real instanceof Double) return NUMBER.toByteBuffer(value.real.doubleValue(), value.imag.doubleValue());
        return NUMBER.toByteBuffer(value.real.floatValue(), value.imag.floatValue());
    }

    private static final ByteBuffer toByteBuffer(final double value) {
        return ByteBuffer.allocate(Double.BYTES).order(Descriptor.BYTEORDER).putDouble(0, value);
    }

    private static final ByteBuffer toByteBuffer(final double real, final double imag) {
        return ByteBuffer.allocate(Double.BYTES * 2).order(Descriptor.BYTEORDER).putDouble(0, real).putDouble(Double.BYTES, imag);
    }

    private static final ByteBuffer toByteBuffer(final float value) {
        return ByteBuffer.allocate(Float.BYTES).order(Descriptor.BYTEORDER).putFloat(0, value);
    }

    private static final ByteBuffer toByteBuffer(final float real, final float imag) {
        return ByteBuffer.allocate(Float.BYTES * 2).order(Descriptor.BYTEORDER).putFloat(0, real).putFloat(Float.BYTES, imag);
    }

    private static final ByteBuffer toByteBuffer(final int value) {
        return ByteBuffer.allocate(Integer.BYTES).order(Descriptor.BYTEORDER).putInt(0, value);
    }

    private static final ByteBuffer toByteBuffer(final long value) {
        return ByteBuffer.allocate(Long.BYTES).order(Descriptor.BYTEORDER).putLong(0, value);
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
        return pout.append(this.getValue()).append(this.getSuffix());
    }

    protected String getSuffix() {
        return DTYPE.getSuffix(this.dtype);
    }

    public abstract T parse(final String in);

    @Override
    public byte toByte() {
        return this.getValue().byteValue();
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{this.toByte()};
    }

    @Override
    public double toDouble() {
        return this.getValue().doubleValue();
    }

    @Override
    public double[] toDoubleArray() {
        return new double[]{this.toDouble()};
    }

    @Override
    public float toFloat() {
        return this.getValue().floatValue();
    }

    @Override
    public float[] toFloatArray() {
        return new float[]{this.toFloat()};
    }

    @Override
    public int toInt() {
        return this.getValue().intValue();
    }

    @Override
    public int[] toIntArray() {
        return new int[]{this.toInt()};
    }

    @Override
    public long toLong() {
        return this.getValue().longValue();
    }

    @Override
    public long[] toLongArray() {
        return new long[]{this.toLong()};
    }
}

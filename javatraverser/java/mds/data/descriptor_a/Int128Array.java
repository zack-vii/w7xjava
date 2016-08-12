package mds.data.descriptor_a;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Int128;

public final class Int128Array extends NUMBERArray<BigInteger>{
    public Int128Array(final BigInteger... values){
        super(DTYPE.O, values);
    }

    public Int128Array(final ByteBuffer b){
        super(b);
    }

    public Int128Array(final int shape[], final BigInteger... values){
        super(DTYPE.O, values, shape);
    }

    @Override
    protected final BigInteger getElement(final ByteBuffer b) {
        return Int128.getBigInteger(b);
    }

    @Override
    public Int128 getScalar(final int idx) {
        return new Int128(this.getValue(idx));
    }

    @Override
    protected final BigInteger[] initArray(final int size) {
        return new BigInteger[size];
    }

    @Override
    public final BigInteger parse(final String in) {
        return new BigInteger(in).setBit(128);
    }

    @Override
    protected final void setElement(final ByteBuffer b, final BigInteger value) {
        Int128.putBigInteger(b, value);
    }
}

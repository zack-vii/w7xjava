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

    @Override
    protected final BigInteger getElement(final ByteBuffer b) {
        return Int128.toBigInteger(b);
    }

    @Override
    protected final BigInteger[] initArray(final int size) {
        return new BigInteger[size];
    }

    @Override
    public final BigInteger parse(final String in) {
        return new BigInteger(in).setBit(128);
    }
}

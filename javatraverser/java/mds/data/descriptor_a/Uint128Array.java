package mds.data.descriptor_a;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint128;

public final class Uint128Array extends NUMBERArray<BigInteger>{
    public Uint128Array(final BigInteger... values){
        super(DTYPE.OU, values);
    }

    public Uint128Array(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final BigInteger getElement(final ByteBuffer b) {
        return Uint128.toBigInteger(b);
    }

    @Override
    protected final BigInteger[] initArray(final int size) {
        return new BigInteger[size];
    }

    @Override
    public final BigInteger parse(final String in) {
        return new BigInteger(in).abs().setBit(128);
    }
}

package mds.data.descriptor_s;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import mds.data.descriptor.DTYPE;

public final class Uint128 extends NUMBER<BigInteger>{
    public static BigInteger getBigInteger(final ByteBuffer b) {
        final byte[] buf = new byte[17]; // takes car of unsigned
        if(b.order() == ByteOrder.BIG_ENDIAN) b.get(buf);
        else for(int i = 16; i-- > 0;)
            buf[i] = b.get();
        return new BigInteger(buf);
    }

    public static void putBigInteger(final ByteBuffer b, final BigInteger value) {
        final byte[] buf = new byte[16], bbuf = value.toByteArray();
        System.arraycopy(bbuf, 0, buf, 0, bbuf.length > 16 ? 16 : bbuf.length);
        if(b.order() == ByteOrder.BIG_ENDIAN) b.put(buf);
        else for(int i = 16; i-- > 0;)
            b.put(buf[i]);
    }

    public Uint128(final BigInteger value){
        super(DTYPE.OU, value);
    }

    public Uint128(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final BigInteger getValue(final ByteBuffer b) {
        return Uint128.getBigInteger(b);
    }

    @Override
    public final BigInteger parse(final String in) {
        return null;// TODO Parse BigInteger
    }
}

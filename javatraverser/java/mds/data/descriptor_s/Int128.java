package mds.data.descriptor_s;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import mds.data.descriptor.DTYPE;

public final class Int128 extends NUMBER<BigInteger>{
    public static BigInteger toBigInteger(final ByteBuffer b) {
        final byte[] buf = new byte[16];
        if(b.order() == ByteOrder.BIG_ENDIAN) b.get(buf);
        else for(int i = 16; i-- > 0;)
            buf[i] = b.get();
        return new BigInteger(buf);
    }

    public Int128(final BigInteger value){
        super(DTYPE.O, value);
    }

    public Int128(final ByteBuffer b){
        super(b);
    }

    @Override
    public final BigInteger getValue(final ByteBuffer b) {
        return Int128.toBigInteger(b);
    }
}

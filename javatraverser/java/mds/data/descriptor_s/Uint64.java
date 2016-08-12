package mds.data.descriptor_s;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint64.ULong;

@SuppressWarnings("serial")
public final class Uint64 extends NUMBER<ULong>{
    public static final class ULong extends Number{
        private static final BigInteger max = BigInteger.ONE.shiftLeft(64);

        public static ULong decode(final String in) {
            return new ULong(new BigInteger(in).longValue());
        }

        public static final ULong fromBuffer(final ByteBuffer b) {
            return new ULong(b.getLong());
        }
        private final long value;

        public ULong(final Long value){
            this.value = value;
        }

        @Override
        public final double doubleValue() {
            if(this.value >= 0) return this.value;
            return BigInteger.valueOf(this.value).add(ULong.max).doubleValue();
        }

        @Override
        public final float floatValue() {
            if(this.value >= 0) return this.value;
            return BigInteger.valueOf(this.value).add(ULong.max).floatValue();
        }

        @Override
        public final int intValue() {
            return (int)this.value;
        }

        @Override
        public final long longValue() {
            return this.value;
        }

        @Override
        public final String toString() {
            return Long.toUnsignedString(this.value);
        }
    }

    public Uint64(final ByteBuffer b){
        super(b);
    }

    public Uint64(final long value){
        super(DTYPE.QU, value);
    }

    public Uint64(final ULong value){
        this(value.value);
    }

    @Override
    protected final ULong getValue(final ByteBuffer b) {
        return new ULong(b.getLong(0));
    }

    @Override
    public final ULong parse(final String in) {
        return ULong.decode(in);
    }
}

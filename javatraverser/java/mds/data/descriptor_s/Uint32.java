package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint32.UInteger;

@SuppressWarnings("serial")
public final class Uint32 extends NUMBER<UInteger>{
    public static final class UInteger extends Number{
        public static final UInteger fromBuffer(final ByteBuffer b) {
            return new UInteger(b.getInt());
        }
        private final int value;

        public UInteger(final Integer value){
            this.value = value;
        }

        @Override
        public final double doubleValue() {
            return Integer.toUnsignedLong(this.value);
        }

        @Override
        public final float floatValue() {
            return Integer.toUnsignedLong(this.value);
        }

        @Override
        public final int intValue() {
            return this.value;
        }

        @Override
        public final long longValue() {
            return Integer.toUnsignedLong(this.value);
        }

        @Override
        public final String toString() {
            return Long.toString(Integer.toUnsignedLong(this.value));
        }
    }

    public Uint32(final ByteBuffer b){
        super(b);
    }

    public Uint32(final int value){
        super(DTYPE.LU, value);
    }

    @Override
    protected final UInteger getValue(final ByteBuffer b) {
        return new UInteger(b.getInt(0));
    }
}

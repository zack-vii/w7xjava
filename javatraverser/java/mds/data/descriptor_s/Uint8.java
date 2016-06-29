package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor_s.Uint8.UByte;

@SuppressWarnings("serial")
public final class Uint8 extends NUMBER<UByte>{
    public static final class UByte extends Number{
        public static final UByte fromBuffer(final ByteBuffer b) {
            return new UByte(b.get());
        }
        private final byte value;

        public UByte(final byte value){
            this.value = value;
        }

        @Override
        public final double doubleValue() {
            return Byte.toUnsignedInt(this.value);
        }

        @Override
        public final float floatValue() {
            return Byte.toUnsignedInt(this.value);
        }

        @Override
        public final int intValue() {
            return Byte.toUnsignedInt(this.value);
        }

        @Override
        public final long longValue() {
            return Byte.toUnsignedLong(this.value);
        }

        @Override
        public final String toString() {
            return Integer.toString(Byte.toUnsignedInt(this.value));
        }
    }

    public Uint8(final byte value){
        super(DTYPE.BU, value);
    }

    public Uint8(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final UByte getValue(final ByteBuffer b) {
        return new UByte(b.get(0));
    }
}
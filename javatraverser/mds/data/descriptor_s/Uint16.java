package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor_s.Uint16.UShort;

public final class Uint16 extends NUMBER<UShort>{
    public static final class UShort extends Number{
        private static final long serialVersionUID = 1L;

        public static final UShort fromBuffer(final ByteBuffer b) {
            return new UShort(b.getShort());
        }
        private final short value;

        public UShort(final short value){
            this.value = value;
        }

        @Override
        public final double doubleValue() {
            return Short.toUnsignedInt(this.value);
        }

        @Override
        public final float floatValue() {
            return Short.toUnsignedInt(this.value);
        }

        @Override
        public final int intValue() {
            return Short.toUnsignedInt(this.value);
        }

        @Override
        public final long longValue() {
            return Short.toUnsignedLong(this.value);
        }

        @Override
        public final String toString() {
            return Integer.toString(Short.toUnsignedInt(this.value));
        }
    }

    public Uint16(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final UShort getValue(final ByteBuffer b) {
        return new UShort(b.getShort(0));
    }
}

package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_s.CString;

public final class CStringArray extends Descriptor_A<String>{
    private static final ByteBuffer toBytes(final String[] lines) {
        int max = 0;
        for(final String line : lines)
            if(max < line.length()) max = line.length();
        final ByteBuffer b = ByteBuffer.allocate(max * lines.length).order(Descriptor.BYTEORDER);
        for(final String line : lines){
            b.put(line.getBytes());
            for(int i = line.length(); i < max; i++)
                b.put((byte)32);
        }
        return b;
    }

    public CStringArray(final ByteBuffer b){
        super(b);
    }

    public CStringArray(final int shape[], final String... lines){
        super(DTYPE.T, CStringArray.toBytes(lines), shape);
    }

    public CStringArray(final String... lines){
        super(DTYPE.T, CStringArray.toBytes(lines), lines.length);
    }

    @Override
    protected StringBuilder decompileT(final StringBuilder pout, final String t) {
        return pout.append('\"').append(t).append('\"');
    }

    @Override
    protected final String getElement(final ByteBuffer b) {
        final byte[] buf = new byte[this.length];
        b.get(buf);
        return new String(buf).replaceAll(" +$", "");
    }

    @Override
    public CString getScalar(final int idx) {
        return new CString(this.getValue(idx));
    }

    @Override
    protected final String getSuffix() {
        return "";
    }

    @Override
    protected final String[] initArray(final int size) {
        return new String[size];
    }

    @Override
    protected final void setElement(final ByteBuffer b, final String value) {
        final int maxlength = this.length < b.remaining() ? this.length : b.remaining();
        b.put(//
                (maxlength < value.length() ? value.substring(0, maxlength) : value)//
                        .getBytes());
    }

    @Override
    public final byte toByte(final String t) {
        return Byte.parseByte(t);
    }

    @Override
    public final double toDouble(final String t) {
        return Double.parseDouble(t);
    }

    @Override
    public final float toFloat(final String t) {
        return Float.parseFloat(t);
    }

    @Override
    public final int toInt(final String t) {
        return Integer.parseInt(t);
    }

    @Override
    public final long toLong(final String t) {
        return Long.parseLong(t);
    }

    @Override
    public final short toShort(final String t) {
        return Short.parseShort(t);
    }

    @Override
    protected final String TtoString(final String t) {
        return t.toString();
    }
}

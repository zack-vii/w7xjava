package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor_A;

public final class CStringArray extends Descriptor_A<String>{
    private static final byte[] toBytes(final String[] lines) {
        int max = 0;
        for(final String line : lines)
            if(max < line.length()) max = line.length();
        final ByteBuffer b = ByteBuffer.allocate(max * lines.length);
        for(final String line : lines){
            b.put(line.getBytes());
            for(int i = line.length(); i < max; i++)
                b.put((byte)32);
        }
        return b.array();
    }

    public CStringArray(final ByteBuffer b){
        super(b);
    }

    public CStringArray(final String[] lines){
        super(DTYPE.T, CStringArray.toBytes(lines), lines.length);
    }

    @Override
    protected final String decompileT(final String t) {
        return new StringBuilder(t.length() + 2).append('\"').append(t).append('\"').toString();
    }

    @Override
    protected final String getElement(final ByteBuffer b) {
        final byte[] buf = new byte[this.length];
        b.get(buf);
        return new String(buf).replaceAll(" +$", "");
    }

    @Override
    protected final String[] initArray(final int size) {
        return new String[size];
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
    protected final String TtoString(final String t) {
        return t.toString();
    }
}
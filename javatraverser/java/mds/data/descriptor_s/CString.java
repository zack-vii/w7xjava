package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_S;

public final class CString extends Descriptor_S<String>{
    private static final String addQuotes(final String str) {
        if(str.contains("\"")){
            if(str.contains("'")) return "\"" + str.replaceAll("\"", "\\\\\"") + "\"";
            return "'" + str + "'";
        }
        return "\"" + str + "\"";
    }

    public static final String decompile(final String str) {
        return CString.addQuotes(str).replaceAll("\\\\", "\\\\\\\\");
    }

    public static void putString(final ByteBuffer b, final String value) {
        b.put(//
                (b.remaining() < value.length() ? value.substring(0, b.remaining()) : value)//
                        .getBytes());
    }

    public CString(final byte[] array){
        super(DTYPE.T, ByteBuffer.wrap(array).order(Descriptor.BYTEORDER));
    }

    public CString(final ByteBuffer b){
        super(b);
    }

    public CString(final String value){
        this(value.getBytes());
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        return pout.append(((mode & Descriptor.DECO_STR) == 0) ? CString.decompile(this.getValue()) : this.getValue());
    }

    @Override
    public final String getValue(final ByteBuffer b) {
        final byte[] buf = new byte[this.length];
        b.get(buf);
        return new String(buf);
    }
}

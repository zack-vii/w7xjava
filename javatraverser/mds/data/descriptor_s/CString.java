package mds.data.descriptor_s;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
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

    public CString(final byte[] array){
        super(DTYPE.T, array);
    }

    public CString(final ByteBuffer b){
        super(b);
    }

    public CString(final String value){
        this(value.getBytes());
    }

    @Override
    public String decompile() {
        return CString.decompile(this.getValue());
    }

    @Override
    public final String getValue(final ByteBuffer b) {
        final byte[] buf = new byte[this.length];
        b.get(buf);
        return new String(buf);
    }
}

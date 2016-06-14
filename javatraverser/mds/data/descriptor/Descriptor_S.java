package mds.data.descriptor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import mds.MdsException;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Complex32;
import mds.data.descriptor_s.Complex64;
import mds.data.descriptor_s.Event;
import mds.data.descriptor_s.Float32;
import mds.data.descriptor_s.Float64;
import mds.data.descriptor_s.Ident;
import mds.data.descriptor_s.Int128;
import mds.data.descriptor_s.Int16;
import mds.data.descriptor_s.Int32;
import mds.data.descriptor_s.Int64;
import mds.data.descriptor_s.Int8;
import mds.data.descriptor_s.Missing;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;
import mds.data.descriptor_s.Uint128;
import mds.data.descriptor_s.Uint16;
import mds.data.descriptor_s.Uint32;
import mds.data.descriptor_s.Uint64;
import mds.data.descriptor_s.Uint8;
import mds.mdsip.Message;

/** Fixed-Length (static) Descriptor (1) **/
@SuppressWarnings("deprecation")
public abstract class Descriptor_S<T>extends Descriptor<T>{
    public static final byte     CLASS    = 1;
    private static final boolean isatomic = true;

    public static final Descriptor_S deserialize(final ByteBuffer b) throws MdsException {
        switch(b.get(Descriptor._typB)){
            case DTYPE.NID:
                return new Nid(b);
            case DTYPE.BU:
                return new Uint8(b);
            case DTYPE.WU:
                return new Uint16(b);
            case DTYPE.LU:
                return new Uint32(b);
            case DTYPE.QU:
                return new Uint64(b);
            case DTYPE.OU:
                return new Uint128(b);
            case DTYPE.B:
                return new Int8(b);
            case DTYPE.W:
                return new Int16(b);
            case DTYPE.L:
                return new Int32(b);
            case DTYPE.Q:
                return new Int64(b);
            case DTYPE.O:
                return new Int128(b);
            case DTYPE.F:
            case DTYPE.FS:
                return new Float32(b);
            case DTYPE.FC:
            case DTYPE.FSC:
                return new Complex32(b);
            case DTYPE.D:
            case DTYPE.G:
            case DTYPE.FT:
                return new Float64(b);
            case DTYPE.DC:
            case DTYPE.GC:
            case DTYPE.FTC:
                return new Complex64(b);
            case DTYPE.T:
                return new CString(b);
            case DTYPE.IDENT:
                return new Ident(b);
            case DTYPE.PATH:
                return new Path(b);
            case DTYPE.MISSING:
                return new Missing(b);
            case DTYPE.EVENT:
                return new Event(b);
        }
        throw new MdsException(String.format("Unsupported dtype %s for class %s", Descriptor.getDTypeName(b.get(Descriptor._typB)), Descriptor.getDClassName(b.get(Descriptor._clsB))), 0);
    }

    public static final void main(final String[] args) {}

    public Descriptor_S(final byte dtype, final byte[] data){
        this((short)data.length, dtype, data);
    }

    public Descriptor_S(final ByteBuffer b){
        super(b);
    }

    public Descriptor_S(final short length, final byte dtype, final byte[] value){
        super(length, dtype, Descriptor_S.CLASS, value);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout) {
        return pout.append(this.getValue());
    }

    public boolean equals(final Descriptor_S dsca) {
        return this.getValue().equals(dsca.getValue());
    }

    @Override
    public final boolean isAtomic() {
        return Descriptor_S.isatomic;
    }

    @Override
    public double[] toDouble() {
        final T val = this.getValue();
        if(val instanceof Number) return new double[]{((Number)val).doubleValue()};
        else if(val instanceof String) return new double[]{Double.parseDouble((String)val)};
        else if(val instanceof float[]) return new double[]{((float[])val)[0]};
        else if(val instanceof double[]) return new double[]{((double[])val)[0]};
        else return new double[]{Double.NaN};
    }

    @Override
    public float[] toFloat() {
        final T val = this.getValue();
        if(val instanceof Number) return new float[]{((Number)val).floatValue()};
        else if(val instanceof String) return new float[]{Float.parseFloat((String)val)};
        else if(val instanceof float[]) return new float[]{((float[])val)[0]};
        else if(val instanceof double[]) return new float[]{(float)((double[])val)[0]};
        else if(val instanceof double[]) return new float[]{(float)((double[])val)[0]};
        else return new float[]{Float.NaN};
    }

    @Override
    public int[] toInt() {
        final T val = this.getValue();
        if(val instanceof Number) return new int[]{((Number)val).intValue()};
        else if(val instanceof String) return new int[]{Integer.parseInt((String)val)};
        else if(val instanceof float[]) return new int[]{(int)((float[])val)[0]};
        else if(val instanceof double[]) return new int[]{(int)((double[])val)[0]};
        else return new int[]{0};
    }

    @Override
    public long[] toLong() {
        final T val = this.getValue();
        if(val instanceof Number) return new long[]{((Number)val).longValue()};
        else if(val instanceof String) return new long[]{Long.parseLong((String)val)};
        else if(val instanceof float[]) return new long[]{(long)((float[])val)[0]};
        else if(val instanceof double[]) return new long[]{(long)((double[])val)[0]};
        else return new long[]{0l};
    }

    @Override
    public Message toMessage(final byte descr_idx, final byte n_args) {
        final byte[] body = new byte[this.length];
        this.getBuffer().get(body);
        final boolean little = this.b.order() != ByteOrder.BIG_ENDIAN;
        return new Message(descr_idx, this.dtype, n_args, null, body, little);
    }

    @Override
    public String toString() {
        return this.getValue().toString();
    }
}

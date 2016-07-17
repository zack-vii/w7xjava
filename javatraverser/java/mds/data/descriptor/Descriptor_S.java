package mds.data.descriptor;

import java.nio.ByteBuffer;
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
import mds.data.descriptor_s.Pointer;
import mds.data.descriptor_s.Uint128;
import mds.data.descriptor_s.Uint16;
import mds.data.descriptor_s.Uint32;
import mds.data.descriptor_s.Uint64;
import mds.data.descriptor_s.Uint8;

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
            case DTYPE.POINTER:
                return new Pointer(b);
            case DTYPE.IDENT:
                return new Ident(b);
            case DTYPE.PATH:
                return new Path(b);
            case DTYPE.MISSING:
                return Missing.NEW;
            case DTYPE.EVENT:
                return new Event(b);
        }
        throw new MdsException(String.format("Unsupported dtype %s for class %s", DTYPE.getName(b.get(Descriptor._typB)), Descriptor.getDClassName(b.get(Descriptor._clsB))), 0);
    }

    public Descriptor_S(final byte dtype, final ByteBuffer data){
        this((short)data.limit(), dtype, data);
    }

    public Descriptor_S(final ByteBuffer b){
        super(b);
    }

    public Descriptor_S(final short length, final byte dtype, final ByteBuffer value){
        super(length, dtype, Descriptor_S.CLASS, value, Descriptor.BYTES, 0);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        final T val = this.getValue();
        return pout.append(val == null ? "*" : val.toString());
    }

    public boolean equals(final Descriptor_S dsca) {
        return this.getValue().equals(dsca.getValue());
    }

    @Override
    public int[] getShape() {
        return new int[0];
    }

    @Override
    public final boolean isAtomic() {
        return Descriptor_S.isatomic;
    }

    @Override
    public byte[] toByteArray() {
        final T val = this.getValue();
        if(val instanceof Number) return new byte[]{((Number)val).byteValue()};
        else if(val instanceof String) return new byte[]{Byte.parseByte((String)val)};
        else if(val instanceof float[]) return new byte[]{(byte)((float[])val)[0]};
        else if(val instanceof double[]) return new byte[]{(byte)((double[])val)[0]};
        else return new byte[]{0};
    }

    @Override
    public double[] toDoubleArray() {
        final T val = this.getValue();
        if(val instanceof Number) return new double[]{((Number)val).doubleValue()};
        else if(val instanceof String) return new double[]{Double.parseDouble((String)val)};
        else if(val instanceof float[]) return new double[]{((float[])val)[0]};
        else if(val instanceof double[]) return new double[]{((double[])val)[0]};
        else return new double[]{Double.NaN};
    }

    @Override
    public float[] toFloatArray() {
        final T val = this.getValue();
        if(val instanceof Number) return new float[]{((Number)val).floatValue()};
        else if(val instanceof String) return new float[]{Float.parseFloat((String)val)};
        else if(val instanceof float[]) return new float[]{((float[])val)[0]};
        else if(val instanceof double[]) return new float[]{(float)((double[])val)[0]};
        else if(val instanceof double[]) return new float[]{(float)((double[])val)[0]};
        else return new float[]{Float.NaN};
    }

    @Override
    public int[] toIntArray() {
        final T val = this.getValue();
        if(val instanceof Number) return new int[]{((Number)val).intValue()};
        else if(val instanceof String) return new int[]{Integer.parseInt((String)val)};
        else if(val instanceof float[]) return new int[]{(int)((float[])val)[0]};
        else if(val instanceof double[]) return new int[]{(int)((double[])val)[0]};
        else return new int[]{0};
    }

    @Override
    public long[] toLongArray() {
        final T val = this.getValue();
        if(val instanceof Number) return new long[]{((Number)val).longValue()};
        else if(val instanceof String) return new long[]{Long.parseLong((String)val)};
        else if(val instanceof float[]) return new long[]{(long)((float[])val)[0]};
        else if(val instanceof double[]) return new long[]{(long)((double[])val)[0]};
        else return new long[]{0};
    }

    @Override
    public short[] toShortArray() {
        final T val = this.getValue();
        if(val instanceof Number) return new short[]{((Number)val).shortValue()};
        else if(val instanceof String) return new short[]{Short.parseShort((String)val)};
        else if(val instanceof float[]) return new short[]{(short)((float[])val)[0]};
        else if(val instanceof double[]) return new short[]{(short)((double[])val)[0]};
        else return new short[]{0};
    }
}

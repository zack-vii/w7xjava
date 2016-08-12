package mds.data.descriptor;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor_a.CStringArray;
import mds.data.descriptor_a.Complex32Array;
import mds.data.descriptor_a.Complex64Array;
import mds.data.descriptor_a.EmptyArray;
import mds.data.descriptor_a.Float32Array;
import mds.data.descriptor_a.Float64Array;
import mds.data.descriptor_a.Int128Array;
import mds.data.descriptor_a.Int16Array;
import mds.data.descriptor_a.Int32Array;
import mds.data.descriptor_a.Int64Array;
import mds.data.descriptor_a.Int8Array;
import mds.data.descriptor_a.NidArray;
import mds.data.descriptor_a.Uint128Array;
import mds.data.descriptor_a.Uint16Array;
import mds.data.descriptor_a.Uint32Array;
import mds.data.descriptor_a.Uint64Array;
import mds.data.descriptor_a.Uint8Array;
import mds.mdsip.Message;

/** Array Descriptor (4) **/
public abstract class Descriptor_A<T>extends ARRAY<T[]>{
    private final class AStringBuilder{
        private int                 i = 0;
        private final StringBuilder pout;
        private final int[]         shape;
        private final T[]           t;

        public AStringBuilder(final StringBuilder pout){
            this.pout = pout;
            this.shape = Descriptor_A.this.dims;
            this.t = Descriptor_A.this.getValue();
            if(this.t.length == 0) pout.append("[]");
            else this.level(this.shape.length);
        }

        private final void level(final int l) {
            if(l == 0){
                Descriptor_A.this.decompileT(this.pout, this.t[this.i++]);
                return;
            }
            this.pout.append("[");
            int j = 0;
            final int len = this.shape[l - 1];
            for(; j < len && this.i < 1000; j++){
                if(j > 0) this.pout.append(',');
                this.level(l - 1);
            }
            j = len - j;
            if(j > 0 || this.i >= 1000) this.pout.append(",...(").append(len).append(')');
            this.pout.append(']');
        }

        @Override
        public final String toString() {
            return this.pout.toString();
        }
    }
    private static final boolean atomic = true;
    public static final byte     CLASS  = 4;

    public static Descriptor_A deserialize(final ByteBuffer b) throws MdsException {
        switch(b.get(Descriptor._typB)){
            case DTYPE.NID:
                return new NidArray(b);
            case DTYPE.BU:
                return new Uint8Array(b);
            case DTYPE.WU:
                return new Uint16Array(b);
            case DTYPE.LU:
                return new Uint32Array(b);
            case DTYPE.QU:
                return new Uint64Array(b);
            case DTYPE.OU:
                return new Uint128Array(b);
            case DTYPE.B:
                return new Int8Array(b);
            case DTYPE.W:
                return new Int16Array(b);
            case DTYPE.L:
                return new Int32Array(b);
            case DTYPE.Q:
                return new Int64Array(b);
            case DTYPE.O:
                return new Int128Array(b);
            case DTYPE.F:
            case DTYPE.FS:
                return new Float32Array(b);
            case DTYPE.FC:
            case DTYPE.FSC:
                return new Complex32Array(b);
            case DTYPE.D:
            case DTYPE.G:
            case DTYPE.FT:
                return new Float64Array(b);
            case DTYPE.DC:
            case DTYPE.GC:
            case DTYPE.FTC:
                return new Complex64Array(b);
            case DTYPE.T:
                return new CStringArray(b);
            case DTYPE.MISSING:
                return new EmptyArray(b);
        }
        throw new MdsException(String.format("Unsupported dtype %s for class %s", DTYPE.getName(b.get(Descriptor._typB)), Descriptor.getDClassName(b.get(Descriptor._clsB))), 0);
    }

    public static final Descriptor_A readMessage(final Message msg) throws MdsException {
        final ByteBuffer msgh = msg.header.duplicate().order(msg.header.order());
        final byte dmct = msgh.get(Message._dmctB);
        final int shape = (dmct > 1) ? (1 + dmct) * Integer.BYTES : 0;
        final short header_size = (short)(Descriptor.BYTES + Descriptor.BYTES + shape);
        final int arsize = msgh.getInt(Message._mlenI) - Message.HEADER_SIZE;
        final ByteBuffer b = ByteBuffer.allocate(header_size + arsize).order(msgh.order());
        b.putShort(msgh.getShort(Message._lenS));
        b.put(msgh.get(Message._typB));
        b.put(Descriptor_A.CLASS);
        b.putInt(header_size);
        b.put((byte)0);
        b.put((byte)0);
        if(shape > 0) b.put(ARRAY.f_coeff.toByte());
        else b.put(ARRAY.f_array.toByte());
        b.put(msgh.get(Message._dmctB));
        b.putInt(arsize);
        if(shape > 0){
            b.putInt(header_size);
            msgh.position(Message._dmsI);
            for(int i = 0; i < dmct; i++)
                b.putInt(msgh.getInt());
        }
        b.put(msg.body).rewind();
        return Descriptor_A.deserialize(b);
    }

    public Descriptor_A(final byte dtype, final ByteBuffer byteBuffer, final int... shape){
        super(dtype, Descriptor_A.CLASS, byteBuffer, shape);
    }

    protected Descriptor_A(final ByteBuffer b){
        super(b);
    }

    @Override
    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        if(pout.capacity() < 1024) pout.ensureCapacity(1024);
        if(this.format()) pout.append(this.getDTypeName()).append('(');
        if((mode & Descriptor.DECO_STR) != 0 && this.arsize > 255){
            String size;
            if(this.dimct == 0 || ((this.dims != null) && (this.dims.length == 0))) size = "0";
            else if(this.dims == null) size = Integer.toString(this.arsize / this.length);
            else{
                final String[] dimstr = new String[this.dims.length];
                for(int i = 0; i < dimstr.length; i++)
                    dimstr[i] = Integer.toString(this.dims[i]);
                size = String.join(",", dimstr);
            }
            pout.append("Set_Range(").append(size).append(',');
            this.decompileT(pout, this.getValue(0));
            pout.append(" /*** etc. ***/)");
        }else new AStringBuilder(pout);
        if(this.format()) pout.append(')');
        return pout;
    }

    protected StringBuilder decompileT(final StringBuilder pout, final T t) {
        return pout.append(this.TtoString(t));
    }

    protected String decompileT(final T t) {
        return this.decompileT(new StringBuilder(32), t).toString();
    }

    @SuppressWarnings("static-method")
    protected boolean format() {
        return false;
    }

    @Override
    public final ByteBuffer getBuffer() {
        final ByteBuffer bo = super.getBuffer();
        bo.limit(bo.capacity() < this.arsize ? bo.capacity() : this.arsize);
        return bo;
    }

    protected abstract T getElement(ByteBuffer b);

    protected abstract String getSuffix();

    @Override
    protected final T[] getValue(final ByteBuffer b) {
        return this.getValue(0, this.arsize);
    }

    public final T getValue(final int idx) {
        final T[] vals = this.getValue(idx, 1);
        return vals[0];
    }

    protected final T[] getValue(int begin, int count) {
        if(begin < 0 || count <= 0) return this.initArray(0);
        final ByteBuffer buf = this.getBuffer();
        final int arrlength = buf.limit() / this.length;
        if(begin >= arrlength) return this.initArray(0);
        if(begin > 0) buf.position(begin * this.length);
        else{// clip negative index
            count += begin;
            begin = 0;
        }
        if(begin + count > arrlength) count = arrlength - begin;
        final T[] bi = this.initArray(count);
        for(int i = 0; i < count; i++){
            bi[i] = this.getElement(buf);
        }
        return bi;
    }

    protected abstract T[] initArray(int size);

    @Override
    public boolean isAtomic() {
        return Descriptor_A.atomic;
    }

    protected abstract void setElement(ByteBuffer b, T value);

    public abstract byte toByte(T t);

    @Override
    public final byte[] toByteArray() {
        final T[] val = this.getValue();
        final byte[] out = new byte[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toByte(val[i]);
        return out;
    }

    public abstract double toDouble(T t);

    @Override
    public final double[] toDoubleArray() {
        final T[] val = this.getValue();
        final double[] out = new double[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toDouble(val[i]);
        return out;
    }

    public abstract float toFloat(T t);

    @Override
    public final float[] toFloatArray() {
        final T[] val = this.getValue();
        final float[] out = new float[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toFloat(val[i]);
        return out;
    }

    public abstract int toInt(T t);

    @Override
    public final int[] toIntArray() {
        final T[] val = this.getValue();
        final int[] out = new int[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toInt(val[i]);
        return out;
    }

    public abstract long toLong(T t);

    @Override
    public final long[] toLongArray() {
        final T[] val = this.getValue();
        final long[] out = new long[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toLong(val[i]);
        return out;
    }

    public abstract short toShort(T t);

    @Override
    public final short[] toShortArray() {
        final T[] val = this.getValue();
        final short[] out = new short[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toShort(val[i]);
        return out;
    }

    public String toString(final int idx) {
        return this.getValue(idx).toString();
    }

    protected abstract String TtoString(T t);
}

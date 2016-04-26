package mds.data.descriptor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
        private int                 i  = 0;
        private final StringBuilder sb = new StringBuilder(0xFFFF);
        private final int[]         shape;
        private final T[]           t;

        public AStringBuilder(final int[] shape, final T[] t){
            this.shape = shape;
            this.t = t;
            this.level(this.shape.length);
        }

        private final void level(final int l) {
            if(l == 0){
                this.sb.append(Descriptor_A.this.decompileT(this.t[this.i++]));
                return;
            }
            this.sb.append("[");
            int j = 0;
            final int len = this.shape[l - 1];
            for(; j < len && j < 128 && this.i < 10000; j++){
                if(j > 0) this.sb.append(", ");
                this.level(l - 1);
            }
            j = len - j;
            if(j > 0 || this.i >= 10000) this.sb.append(",...(").append(len).append(')');
            this.sb.append(']');
        }

        @Override
        public final String toString() {
            return this.sb.toString();
        }
    }
    public static final byte CLASS = 4;

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
        throw new MdsException(String.format("Unsupported dtype %s for class %s", Descriptor.getDTypeName(b.get(Descriptor._typB)), Descriptor.getDClassName(b.get(Descriptor._clsB))), 0);
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
        if(shape > 0) b.put(Descriptor_A.f_coeff.toByte());
        else b.put(Descriptor_A.f_array.toByte());
        b.put(msgh.get(Message._dmctB));
        b.putInt(arsize);
        if(shape > 0){
            b.putInt(header_size);
            msgh.position(Message._dmsI);
            for(int i = 0; i < dmct; i++)
                b.putInt(msgh.getInt());
        }
        b.put(msg.body).position(0);
        return Descriptor_A.deserialize(b);
    }

    public Descriptor_A(final byte dtype, final byte[] buf, final int nelements){
        super(dtype, Descriptor_A.CLASS, buf, nelements);
    }

    protected Descriptor_A(final ByteBuffer b){
        super(b);
    }

    @Override
    public String decompile() {
        return this.format(new AStringBuilder(this.dims, this.getValue()).toString());
    }

    protected String decompileT(final T t) {
        return this.TtoString(t);
    }

    public String format(final String in) {
        return new StringBuilder(in.length() + 32).append(this.getDName()).append('(').append(in).append(')').toString();
    }

    @Override
    protected final ByteBuffer getBuffer() {
        final ByteBuffer bo = super.getBuffer();
        bo.limit(bo.capacity() < this.arsize ? bo.capacity() : this.arsize);
        return bo;
    }

    protected abstract T getElement(ByteBuffer b);

    @Override
    protected final T[] getValue(final ByteBuffer b) {
        return this.getValue(0, this.arsize);
    }

    public final T getValue(final int idx) {
        final T[] vals = this.getValue(idx, 1);
        return vals[0];
    }

    protected final T[] getValue(final int begin, int count) {
        if(begin < 0 || count < 0) return this.initArray(0);
        final ByteBuffer buf = this.getBuffer();
        final int maxidx = buf.limit() / this.length;
        if(begin > 0) buf.position(begin * this.length);
        if(begin + count > maxidx) count = maxidx - begin;
        final T[] bi = this.initArray(count);
        for(int i = 0; i < count; i++){
            bi[i] = this.getElement(buf);
        }
        return bi;
    }

    protected abstract T[] initArray(int size);

    @Override
    public final double[] toDouble() {
        final T[] val = this.getValue();
        final double[] out = new double[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toDouble(val[i]);
        return out;
    }

    public abstract double toDouble(T t);

    @Override
    public final float[] toFloat() {
        final T[] val = this.getValue();
        final float[] out = new float[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toFloat(val[i]);
        return out;
    }

    public abstract float toFloat(T t);

    @Override
    public final int[] toInt() {
        final T[] val = this.getValue();
        final int[] out = new int[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toInt(val[i]);
        return out;
    }

    public abstract int toInt(T t);

    @Override
    public final long[] toLong() {
        final T[] val = this.getValue();
        final long[] out = new long[val.length];
        for(int i = 0; i < out.length; i++)
            out[i] = this.toLong(val[i]);
        return out;
    }

    public abstract long toLong(T t);

    @Override
    public final Message toMessage(final byte descr_idx, final byte n_args) {
        final byte[] body = new byte[this.arsize];
        this.getBuffer().get(body);
        final boolean little = this.b.order() != ByteOrder.BIG_ENDIAN;
        return new Message(descr_idx, this.dtype, n_args, this.dims, body, little);
    }

    @Override
    public String toString() {
        String size;
        if(this.dimct == 0 || ((this.dims != null) && (this.dims.length == 0))) size = "0";
        else if(this.dims == null) size = Integer.toString(this.arsize / this.length);
        else{
            final String[] dimstr = new String[this.dims.length];
            for(int i = 0; i < dimstr.length; i++)
                dimstr[i] = Integer.toString(this.dims[i]);
            size = String.join("x", dimstr);
        }
        return new StringBuilder(256).append(this.getDName()).append('(').append(size).append('@').append(this.length).append("B)").toString();
    }

    public String toString(final int idx) {
        return this.getValue(idx).toString();
    }

    protected abstract String TtoString(T t);
}

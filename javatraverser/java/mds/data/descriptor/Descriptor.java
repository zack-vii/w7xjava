package mds.data.descriptor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import mds.Database;
import mds.MdsException;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Missing;
import mds.mdsip.Message;

/** DSC (24) **/
public abstract class Descriptor<T>{
    protected static final int       _clsB     = 3;
    protected static final int       _lenS     = 0;
    protected static final int       _ptrI     = 4;
    protected static final int       _typB     = 2;
    public static final short        BYTES     = 8;
    protected static final int       DECO_NRM  = 0;
    protected static final int       DECO_STR  = 1;
    protected static final int       DECO_STRX = Descriptor.DECO_X | Descriptor.DECO_STR;
    protected static final int       DECO_X    = 2;
    public static final boolean      isatomic  = false;
    protected static final byte      P_ARG     = 88;
    protected static final byte      P_STMT    = 96;
    protected static final byte      P_SUBS    = 0;
    protected static final ByteOrder BYTEORDER = Descriptor.BYTEORDER;

    private static final ByteBuffer Buffer(final byte[] buf, final boolean swap_little) {
        return Descriptor.Buffer(buf, swap_little ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
    }

    private static final ByteBuffer Buffer(final byte[] buf, final ByteOrder bo) {
        return ByteBuffer.wrap(buf).asReadOnlyBuffer().order(bo);
    }

    public static final String decompile(final Descriptor t) {
        return t == null ? "*" : t.decompile();
    }

    public static final Descriptor deserialize(final byte[] buf, final boolean swap) throws MdsException {
        if(buf == null) return null;
        return Descriptor.deserialize(Descriptor.Buffer(buf, swap));
    }

    public static Descriptor deserialize(final ByteBuffer bi) throws MdsException {
        if(bi.capacity() == 0) return null;
        if(!bi.hasRemaining()) return Missing.NEW;
        final ByteBuffer b = bi.slice().order(bi.order());
        switch(b.get(Descriptor._clsB)){
            case Descriptor_A.CLASS:
                return Descriptor_A.deserialize(b);
            case Descriptor_APD.CLASS:
                return Descriptor_APD.deserialize(b);
            case Descriptor_CA.CLASS:
                return Descriptor_CA.deserialize(b);
            case Descriptor_D.CLASS:
                return Descriptor_S.deserialize(b);
            case Descriptor_R.CLASS:
                return Descriptor_R.deserialize(b);
            case Descriptor_S.CLASS:
                return Descriptor_S.deserialize(b);
            case Descriptor_XD.CLASS:
                return Descriptor_XS.deserialize(b);
            case Descriptor_XS.CLASS:
                return Descriptor_XS.deserialize(b);
        }
        throw new MdsException(String.format("Unsupported class %s", Descriptor.getDClassName(b.get(Descriptor._clsB))), 0);
    }

    public static final short getDataSize(final byte type, final int length) {
        switch(type){
            case DTYPE.T:
                return (short)length;
            case DTYPE.BU:
            case DTYPE.B:
                return 1;
            case DTYPE.WU:
            case DTYPE.W:
                return 2;
            case DTYPE.LU:
            case DTYPE.L:
            case DTYPE.F:
            case DTYPE.FS:
                return 4;
            case DTYPE.Q:
            case DTYPE.QU:
            case DTYPE.FC:
            case DTYPE.FSC:
            case DTYPE.D:
            case DTYPE.FT:
            case DTYPE.G:
                return 8;
            case DTYPE.O:
            case DTYPE.OU:
            case DTYPE.DC:
            case DTYPE.FTC:
            case DTYPE.GC:
                return 16;
        }
        return 0;
    }

    public static final String getDClassName(final byte dclass) {
        switch(dclass){
            case Descriptor_S.CLASS:
                return "CLASS_S";
            case Descriptor_D.CLASS:
                return "CLASS_D";
            case Descriptor_R.CLASS:
                return "CLASS_R";
            case Descriptor_A.CLASS:
                return "CLASS_A";
            case Descriptor_XS.CLASS:
                return "CLASS_XS";
            case Descriptor_XD.CLASS:
                return "CLASS_XD";
            case Descriptor_CA.CLASS:
                return "CLASS_CA";
            case Descriptor_APD.CLASS:
                return "CLASS_APD";
            default:
                return "CLASS" + (dclass & 0xFF);
        }
    }

    public static final String getDTypeName(final byte dtype) {
        final String name = DTYPE.getName(dtype);
        if(name != null) return name;
        return "DTYPE_" + (dtype & 0xFF);
    }

    public static Descriptor readMessage(final Message msg) throws MdsException {
        if(msg.header.get(Message._typB) == DTYPE.T) return new CString(msg.body.array());
        return Descriptor_A.readMessage(msg);
    }

    public static final String toString(final Descriptor t) {
        return t == null ? "*" : t.toString();
    }
    protected final boolean isserial;
    public final ByteBuffer b;
    /** (0,s) specific length typically a 16-bit (unsigned) length **/
    public final short      length;
    /** (2,b) data type code **/
    public final byte       dtype;
    /** (3,b) descriptor class code **/
    public final byte       dclass;
    /** (4,i) address of first byte of data element **/
    public final int        pointer;

    public Descriptor(final ByteBuffer b){
        this.isserial = true;
        this.b = b.slice().order(b.order());
        this.length = b.getShort();
        this.dtype = b.get();
        this.dclass = b.get();
        this.pointer = b.getInt();
    }

    public Descriptor(final short length, final byte dtype, final byte dclass, final ByteBuffer byteBuffer, final int offset){
        this.isserial = true;
        if(byteBuffer == null) this.b = ByteBuffer.allocate(Descriptor.BYTES + offset + (byteBuffer == null ? 0 : byteBuffer.limit())).order(Descriptor.BYTEORDER);
        else this.b = ByteBuffer.allocate(Descriptor.BYTES + offset + byteBuffer.limit()).order(byteBuffer.order());
        this.b.putShort(this.length = length);
        this.b.put(this.dtype = dtype);
        this.b.put(this.dclass = dclass);
        this.b.putInt(this.pointer = Descriptor.BYTES + offset);
        if(byteBuffer == null) return;
        ((ByteBuffer)this.b.duplicate().position(this.pointer)).put((ByteBuffer)byteBuffer.rewind());
    }

    public final String decompile() {
        return this.decompile(Descriptor.P_STMT, new StringBuilder(1024), Descriptor.DECO_NRM).toString();
    }

    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        pout.append("<Descriptor(");
        pout.append(this.length & 0xFFFF).append(',');
        pout.append(this.dtype & 0xFF).append(',');
        pout.append(this.dclass & 0xFF).append(',');
        pout.append(this.pointer & 0xFFFFFFFFl);
        return pout.append(")>");
    }

    public final String decompileX() {
        return this.decompile(Descriptor.P_STMT, new StringBuilder(1024), Descriptor.DECO_X).toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof Descriptor) return this.getValue().equals(((Descriptor)obj).getValue());
        return false;
    }

    public final Descriptor evaluate() {
        try{
            return Database.tdiEvaluate(this);
        }catch(final MdsException e){
            return new Missing();
        }
    }

    protected ByteBuffer getBuffer() {
        return ((ByteBuffer)this.b.duplicate().position(this.pointer)).slice().order(this.b.order());
    }

    public Descriptor getData() {
        return this;
    }

    public final Descriptor getDescriptor() throws MdsException {
        return Descriptor.deserialize(this.getBuffer());
    }

    protected String getDName() {
        return DTYPE.getName(this.dtype);
    }

    public abstract int[] getShape();

    public final T getValue() {
        return this.getValue(this.getBuffer());
    }

    protected abstract T getValue(ByteBuffer b);

    @SuppressWarnings("static-method")
    public boolean isAtomic() {
        return Descriptor.isatomic;
    }

    public ByteBuffer serialize() {
        if(this.isserial) return this.b.duplicate().order(this.b.order());
        return ByteBuffer.allocate(8).order(this.getBuffer().order()).putShort(this.length).put(this.dtype).put(this.dclass).putInt(this.pointer);
    }

    public abstract double[] toDouble();

    public abstract float[] toFloat();

    public abstract int[] toInt();

    public abstract long[] toLong();

    public Message toMessage(final byte descr_idx, final byte n_args) {
        final Descriptor data = this.getData();
        return new Message(descr_idx, data.dtype, n_args, data.getShape(), data.getBuffer());
    }

    @Override
    public final String toString() {
        return this.decompile(Descriptor.P_STMT, new StringBuilder(1024), Descriptor.DECO_STR).toString();
    }

    public final String toStringX() {
        return this.decompile(Descriptor.P_STMT, new StringBuilder(1024), Descriptor.DECO_STRX).toString();
    }
}

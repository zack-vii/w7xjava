package mds.data.descriptor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import mds.MdsException;
import mds.TdiShr;
import mds.data.descriptor_a.Int8Array;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Missing;
import mds.mdsip.Connection;
import mds.mdsip.Message;

/** DSC (24) **/
public abstract class Descriptor<T>{
    protected static final int    _lenS     = 0;
    protected static final int    _typB     = 2;
    protected static final int    _clsB     = 3;
    protected static final int    _ptrI     = 4;
    public static final ByteOrder BYTEORDER = Descriptor.BYTEORDER;
    public static final short     BYTES     = 8;
    protected static final int    DECO_NRM  = 0;
    protected static final int    DECO_STR  = 1;
    protected static final int    DECO_STRX = Descriptor.DECO_X | Descriptor.DECO_STR;
    protected static final int    DECO_X    = 2;
    public static final boolean   atomic    = false;
    protected static final byte   P_ARG     = 88;
    protected static final byte   P_STMT    = 96;
    protected static final byte   P_SUBS    = 0;

    private static final ByteBuffer Buffer(final byte[] buf, final boolean swap_little) {
        return Descriptor.Buffer(buf, swap_little ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
    }

    private static final ByteBuffer Buffer(final byte[] buf, final ByteOrder bo) {
        return ByteBuffer.wrap(buf).asReadOnlyBuffer().order(bo);
    }

    /** null safe decompile of the given Descriptor **/
    public static final String decompile(final Descriptor t) {
        return t == null ? "*" : t.decompile();
    }

    /** Returns the Descriptor deserialized from the given byte[] with byte order **/
    public static final Descriptor deserialize(final byte[] buf, final boolean swap) throws MdsException {
        if(buf == null) return null;
        return Descriptor.deserialize(Descriptor.Buffer(buf, swap));
    }

    /** Returns the Descriptor deserialized from the given ByteBuffer **/
    public static Descriptor deserialize(final ByteBuffer bi) throws MdsException {
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

    /** Returns the element length of the given dtype **/
    public static final short getDataSize(final byte dtype, final int length) {
        switch(dtype){
            default:
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
    }

    /** Returns the name of the given dclass **/
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
                return "CLASS_" + (dclass & 0xFF);
        }
    }

    /** Returns Descriptor contained in Message **/
    public static Descriptor readMessage(final Message msg) throws MdsException {
        if(msg.header.get(Message._typB) == DTYPE.T) return new CString(msg.body.array());
        return Descriptor_A.readMessage(msg);
    }

    /** null safe sloppy decompile of the given Descriptor **/
    public static final String toString(final Descriptor t) {
        return t == null ? "*" : t.toString();
    }
    protected final ByteBuffer b;
    /** (0,s) specific length typically a 16-bit (unsigned) length **/
    public final short         length;
    /** (2,b) data type code **/
    public final byte          dtype;
    /** (3,b) descriptor class code **/
    public final byte          dclass;
    /** (4,i) address of first byte of data element **/
    public final int           pointer;
    protected Descriptor       VALUE;

    public Descriptor(final ByteBuffer b){
        this.b = b.slice().order(b.order());
        this.length = b.getShort();
        this.dtype = b.get();
        this.dclass = b.get();
        this.pointer = b.getInt();
    }

    public Descriptor(final short length, final byte dtype, final byte dclass, final ByteBuffer byteBuffer, final int pointer, int size){
        size += pointer;
        if(byteBuffer != null) size += byteBuffer.limit();
        this.b = ByteBuffer.allocate(size).order(Descriptor.BYTEORDER);
        this.b.putShort(this.length = length);
        this.b.put(this.dtype = dtype);
        this.b.put(this.dclass = dclass);
        if(byteBuffer == null) this.b.putInt(this.pointer = 0);
        else{
            this.b.putInt(this.pointer = pointer);
            ((ByteBuffer)this.b.position(pointer)).put(byteBuffer);
        }
        this.b.position(0);
    }

    /** Returns the decompiled string **/
    public final String decompile() {
        return this.decompile(Descriptor.P_STMT, new StringBuilder(1024), Descriptor.DECO_NRM).toString();
    }

    /** Core method for decompiling (dummy) **/
    public StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        pout.append("<Descriptor(");
        pout.append(this.length & 0xFFFF).append(',');
        pout.append(this.dtype & 0xFF).append(',');
        pout.append(this.dclass & 0xFF).append(',');
        pout.append(this.pointer & 0xFFFFFFFFl);
        return pout.append(")>");
    }

    /** Returns the decompiled string with first level indentation **/
    public final String decompileX() {
        return this.decompile(Descriptor.P_STMT, new StringBuilder(1024), Descriptor.DECO_X).toString();
    }

    @Override
    public final boolean equals(final Object obj) {
        if(obj instanceof Descriptor) return this.getValue().equals(((Descriptor)obj).getValue());
        return false;
    }

    /** Evaluates Descriptor remotely and returns result Descriptor **/
    public Descriptor evaluate() {
        try{
            return new TdiShr(Connection.getActiveConnection()).tdiEvaluate(this);
        }catch(final MdsException e){
            return Missing.NEW;
        }
    }

    /** Returns the value as raw ByteBuffer **/
    public ByteBuffer getBuffer() {
        return ((ByteBuffer)this.b.asReadOnlyBuffer().position(this.pointer == 0 ? this.b.limit() : this.pointer)).slice().order(this.b.order());
    }

    /** Returns the data of the Descriptor, i.e. DATA($THIS) **/
    public Descriptor getData() {
        return this;
    }

    /** Returns the dclass name of the Descriptor **/
    public final String getDClassName() {
        return Descriptor.getDClassName(this.dclass);
    }

    /** Returns the value cast to Descriptor **/
    public final Descriptor getDescriptor() throws MdsException {
        return Descriptor.deserialize(this.getBuffer());
    }

    /** Returns the dtype name of the Descriptor **/
    public String getDTypeName() {
        return DTYPE.getName(this.dtype);
    }

    /** Returns the shape of the Descriptor, i.e. SHAPE($THIS) **/
    public abstract int[] getShape();

    /** Returns the total size of the backing buffer in bytes **/
    public int getSize() {
        return this.serialize().limit();
    }

    /** Returns the value<T> of the body directed to by pointer **/
    public final T getValue() {
        return this.getValue(this.getBuffer());
    }

    /** Returns value<T> from given ByteBuffer **/
    protected abstract T getValue(ByteBuffer b);

    public Descriptor getVALUE() {
        return this.VALUE.getVALUE();
    }

    @SuppressWarnings("static-method")
    public boolean isAtomic() {
        return Descriptor.atomic;
    }

    /** Returns serialized byte stream as ByteBuffer **/
    public final ByteBuffer serialize() {
        return this.b.asReadOnlyBuffer().order(this.b.order());
    }

    /** Returns serialized byte stream as byte[] **/
    public final byte[] serializeArray() {
        final ByteBuffer b = this.serialize();
        return ByteBuffer.allocate(b.limit()).put(b).array();
    }

    /** Returns serialized byte stream as Descriptor **/
    public final Int8Array serializeDsc() {
        return new Int8Array(this.serializeArray());
    }

    /** Returns value as byte **/
    public byte toByte() {
        return this.toByteArray()[0];
    }

    /** Returns value as byte[] **/
    public abstract byte[] toByteArray();

    /** Returns value as double **/
    public double toDouble() {
        return this.toDoubleArray()[0];
    }

    /** Returns value as double[] **/
    public abstract double[] toDoubleArray();

    /** Returns value as float **/
    public float toFloat() {
        return this.toFloatArray()[0];
    }

    /** Returns value as float[] **/
    public abstract float[] toFloatArray();

    /** Returns value as int **/
    public int toInt() {
        return this.toIntArray()[0];
    }

    /** Returns value as int[] **/
    public abstract int[] toIntArray();

    /** Returns value as long **/
    public long toLong() {
        return this.toLongArray()[0];
    }

    /** return value as long[] **/
    public abstract long[] toLongArray();

    /** Returns the MdsIp Message representing this Descriptor **/
    public Message toMessage(final byte descr_idx, final byte n_args) {
        final Descriptor data = this.getData();
        return new Message(descr_idx, data.dtype, n_args, data.getShape(), data.getBuffer());
    }

    /** Returns value as short **/
    public short toShort() {
        return this.toShortArray()[0];
    }

    /** Returns value as short[] **/
    public abstract short[] toShortArray();

    @Override
    /** Returns a sloppy decompiled string **/
    public final String toString() {
        return this.decompile(Descriptor.P_STMT, new StringBuilder(1024), Descriptor.DECO_STR).toString();
    }

    /** Returns a sloppy decompiled string with first level indentation **/
    public final String toStringX() {
        return this.decompile(Descriptor.P_STMT, new StringBuilder(1024), Descriptor.DECO_STRX).toString();
    }
}

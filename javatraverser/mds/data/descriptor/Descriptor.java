package mds.data.descriptor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import mds.MdsException;
import mds.data.descriptor_s.CString;
import mds.mdsip.Connection;
import mds.mdsip.Message;

/** DSC (24) **/
public abstract class Descriptor<T>{
    public static final int     _clsB    = 3;
    public static final int     _lenS    = 0;
    public static final int     _ptrI    = 4;
    public static final int     _typB    = 2;
    public static final short   BYTES    = 8;
    public static final boolean isatomic = false;

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
        if(!bi.hasRemaining()) return null;
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

    public static final int getDataSize(final byte type, final byte[] body) {
        switch(type){
            case DTYPE.T:
                return body.length;
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

    public static void main(final String[] a) throws IOException {// TODO: main
        final Connection m = new Connection();
        m.setProvider("localhost");
        m.connectToMds(false);
        if(m.error != null) throw new MdsException(m.error);
        final String tree = "test";
        final int shot = -1;
        final int nid = 7;
        m.mdsValue(String.format("treeopen('%s',%d)", tree, shot));
        final Descriptor D = m.mdsValue(String.format("COMMA(TreeShr->TreeGetRecord(val(%d),xd(_ans)),MdsShr->MdsSerializeDscOut(xd(_ans),xd(_ans)),_ans)", nid), Descriptor.class);
        System.out.println(D);
        System.exit(0);
    }

    public static Descriptor readMessage(final Message msg) throws MdsException {
        if(msg.header.get(Message._typB) == DTYPE.T) return new CString(msg.body.array());
        return Descriptor_A.readMessage(msg);
    }

    public static final String toString(final Descriptor t) {
        return t == null ? "*" : t.toString();
    }
    public final ByteBuffer b;
    /** (3,b) descriptor class code **/
    public final byte       dclass;
    /** (2,b) data type code **/
    public final byte       dtype;
    /** (0,s) specific length typically a 16-bit (unsigned) length **/
    public final short      length;
    /** (4,i) address of first byte of data element **/
    public final int        pointer;

    public Descriptor(final ByteBuffer b){
        this.b = b.slice().order(b.order());
        this.length = b.getShort();
        this.dtype = b.get();
        this.dclass = b.get();
        this.pointer = b.getInt();
    }

    public Descriptor(final short length, final byte dtype, final byte dclass, final byte[] value){
        this.b = ByteBuffer.allocate(Descriptor.BYTES + (value == null ? 0 : value.length));
        this.b.putShort(this.length = length);
        this.b.put(this.dtype = dtype);
        this.b.put(this.dclass = dclass);
        this.b.putInt(this.pointer = Descriptor.BYTES);
        if(value == null) return;
        this.b.put(value);
    }

    public String decompile() {
        return String.format("<Descriptor(%d,%d,%d,%d)>", this.length & 0xFFFF, this.dtype & 0xFF, this.dclass & 0xFF, this.pointer & 0xFFFFFFFFl);
    }

    public String decompileX() {
        return this.decompile();
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof Descriptor) return this.getValue().equals(((Descriptor)obj).getValue());
        return false;
    }

    protected ByteBuffer getBuffer() {
        final ByteBuffer b = ((ByteBuffer)this.b.duplicate().position(this.pointer)).slice().order(this.b.order());
        return b;
    }

    protected String getDName() {
        return DTYPE.getName(this.dtype);
    }

    public final T getValue() {
        return this.getValue(this.getBuffer());
    }

    protected abstract T getValue(ByteBuffer b);

    @SuppressWarnings("static-method")
    public boolean isAtomic() {
        return Descriptor.isatomic;
    }

    public abstract double[] toDouble();

    public abstract float[] toFloat();

    public abstract int[] toInt();

    public abstract long[] toLong();

    public abstract Message toMessage(byte descr_idx, byte n_args);

    @Override
    public String toString() {
        return new StringBuilder(32).append("DSC(").append(Descriptor.getDClassName(this.dclass)).append(',').append(Descriptor.getDTypeName(this.dtype)).append(')').toString();
    }

    public String toStringX() {
        return this.toString();
    }
}

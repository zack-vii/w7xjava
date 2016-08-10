package mds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;
import java.util.zip.InflaterInputStream;
import jscope.ConnectionEvent;
import jscope.ConnectionListener;

public final class MdsMessage extends Object{
    private static final byte     BIG_ENDIAN_MASK            = (byte)0x80;
    private static final byte     COMPRESSED                 = (byte)0x20;
    protected static final String EVENTASTREQUEST            = "---EVENTAST---REQUEST---";
    protected static final String EVENTCANREQUEST            = "---EVENTCAN---REQUEST---";
    private static final int      HEADER_SIZE                = 48;
    private static final byte     JAVA_CLIENT                = (byte)((byte)3 | MdsMessage.BIG_ENDIAN_MASK | MdsMessage.SWAP_ENDIAN_ON_SERVER_MASK);
    private static int            msgid                      = 1;
    // private static final byte SENDCAPABILITIES = (byte)0xF;
    private static final int      SUPPORTS_COMPRESSION       = 0x8000;
    private static final byte     SWAP_ENDIAN_ON_SERVER_MASK = (byte)0x40;

    private static final synchronized void dispatchConnectionEvent(final Vector<ConnectionListener> listeners, final ConnectionEvent e) {
        if(listeners != null) for(final ConnectionListener listener : listeners)
            listener.processConnectionEvent(e);
    }

    protected static boolean IsRoprand(final byte arr[], final int idx) {
        return(arr[idx] == 0 && arr[idx + 1] == 0 && arr[idx + 2] == -128 && arr[idx + 3] == 0);
    }

    private static final synchronized byte[] readBuf(int bytes_to_read, final InputStream dis, final Vector<ConnectionListener> listeners) throws IOException {
        final byte[] buf = new byte[bytes_to_read];
        int read_bytes = 0, curr_offset = 0;
        final boolean send = (bytes_to_read > 2000);
        if(send) MdsMessage.dispatchConnectionEvent(listeners, new ConnectionEvent(dis, buf.length, curr_offset));
        while(bytes_to_read > 0){
            read_bytes = dis.read(buf, curr_offset, bytes_to_read);
            curr_offset += read_bytes;
            bytes_to_read -= read_bytes;
            if(send) MdsMessage.dispatchConnectionEvent(listeners, new ConnectionEvent(dis, buf.length, curr_offset));
        }
        return buf;
    }

    protected static final synchronized byte[] ReadCompressedBuf(final InputStream dis, final boolean swap, final Vector<ConnectionListener> listeners) throws IOException {
        final byte[] btr = new byte[Integer.BYTES];
        final DataInputStream reader = new DataInputStream(dis);
        final int bytes_to_read;
        if(swap){
            reader.read(btr);
            bytes_to_read = ByteBuffer.wrap(btr).order(ByteOrder.LITTLE_ENDIAN).getInt() - MdsMessage.HEADER_SIZE;
        }else bytes_to_read = reader.readInt() - MdsMessage.HEADER_SIZE;
        final InflaterInputStream zis = new InflaterInputStream(dis);
        final byte[] buf = MdsMessage.readBuf(bytes_to_read, zis, listeners);
        while(zis.available() == 1)
            zis.skip(1); // EOF
        return buf;
    }

    public final synchronized static MdsMessage Receive(final InputStream dis, final Vector<ConnectionListener> listeners) throws IOException {
        final byte[] header = MdsMessage.readBuf(MdsMessage.HEADER_SIZE, dis, null);
        final boolean swap = (header[14] & MdsMessage.BIG_ENDIAN_MASK) == 0;
        final ByteBuffer buf = ByteBuffer.wrap(header).order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        final int msglen = buf.getInt();
        final int status = buf.getInt();
        buf.getShort();
        final byte nargs = buf.get();
        final byte descr_idx = buf.get();
        final byte message_id = buf.get();
        final byte dtype = buf.get();
        final byte c_type = buf.get();
        final byte ndims = buf.get();
        final int[] dims = new int[Descriptor.MAX_DIM];
        for(int i = 0; i < Descriptor.MAX_DIM; i++)
            dims[i] = buf.getInt();
        final byte[] body;
        final boolean compressed = ((c_type & MdsMessage.COMPRESSED) == MdsMessage.COMPRESSED);
        if(msglen > MdsMessage.HEADER_SIZE){
            if(compressed) body = MdsMessage.ReadCompressedBuf(dis, swap, listeners);
            else body = MdsMessage.readBuf(msglen - MdsMessage.HEADER_SIZE, dis, listeners);
        }else body = new byte[0];
        return new MdsMessage(descr_idx, dtype, nargs, dims, ndims, body, c_type, status, message_id);
    }
    protected final byte body[];
    private final byte   client_type;
    private final byte   descr_idx;
    private final int    dims[];
    protected final byte dtype;
    protected final int  length;
    protected final int  message_id;
    protected final int  msglen;
    private final byte   nargs;
    private final byte   ndims;
    protected int        status;

    public MdsMessage(){
        this((byte)0, (byte)0, (byte)0, null, new byte[1]);
        this.verify();
    }

    protected MdsMessage(final byte c){
        this((byte)0, Descriptor.DTYPE_CSTRING, (byte)1, null, new byte[]{c});
    }

    public MdsMessage(final byte descr_idx, final byte dtype, final byte nargs, final int dims[], final byte body[]){
        this(descr_idx, dtype, nargs, dims, (dims == null) ? (byte)0 : (byte)dims.length, body, MdsMessage.JAVA_CLIENT, 0, MdsMessage.msgid);
    }

    public MdsMessage(final byte descr_idx, final byte dtype, final byte nargs, final int dims[], final byte ndims, final byte body[], final byte client_type, final int status, final int msgid){
        final int body_size = (body != null ? body.length : 0);
        this.msglen = MdsMessage.HEADER_SIZE + body_size;
        this.status = status;
        this.message_id = msgid;
        this.length = Descriptor.getDataSize(dtype, body);
        this.nargs = nargs;
        this.descr_idx = descr_idx;
        this.ndims = (ndims > Descriptor.MAX_DIM) ? Descriptor.MAX_DIM : ndims;
        if(dims == null || dims.length != Descriptor.MAX_DIM){
            this.dims = new int[Descriptor.MAX_DIM];
            if(dims != null) System.arraycopy(dims, 0, this.dims, 0, this.ndims < dims.length ? this.ndims : dims.length);
        }else this.dims = dims;
        this.dtype = dtype;
        this.client_type = client_type;
        this.body = body;
    }

    protected MdsMessage(final String s){
        this((byte)0, Descriptor.DTYPE_CSTRING, (byte)1, null, s.getBytes());
    }

    public final byte[] asByteArray() {
        return this.body;
    }

    protected final double asDouble() throws IOException {
        return ByteBuffer.wrap(this.body).order(this.getByteOrder()).getDouble();
    }

    protected final double[] asDoubleArray() throws IOException {
        final DoubleBuffer buf = ByteBuffer.wrap(this.body).order(this.getByteOrder()).asDoubleBuffer();
        final double[] out = new double[buf.remaining()];
        buf.get(out);
        return out;
    }

    protected final float asFloat(final byte bytes[]) throws IOException {
        return ByteBuffer.wrap(this.body).order(this.getByteOrder()).getFloat();
    }

    protected final float[] asFloatArray() throws IOException {
        final FloatBuffer buf = ByteBuffer.wrap(this.body).order(this.getByteOrder()).asFloatBuffer();
        final float[] out = new float[buf.remaining()];
        buf.get(out);
        return out;
    }

    public final int asInt() throws IOException {
        return ByteBuffer.wrap(this.body).order(this.getByteOrder()).getInt();
    }

    public final int[] asIntArray() throws IOException {
        final IntBuffer buf = ByteBuffer.wrap(this.body).order(this.getByteOrder()).asIntBuffer();
        final int[] out = new int[buf.remaining()];
        buf.get(out);
        return out;
    }

    public final long[] asLongArray() throws IOException {
        final LongBuffer buf = ByteBuffer.wrap(this.body).order(this.getByteOrder()).asLongBuffer();
        final long[] out = new long[buf.remaining()];
        buf.get(out);
        return out;
    }

    public final short asShort() throws IOException {
        return ByteBuffer.wrap(this.body).order(this.getByteOrder()).getShort();
    }

    public final short[] asShortArray() throws IOException {
        final ShortBuffer buf = ByteBuffer.wrap(this.body).order(this.getByteOrder()).asShortBuffer();
        final short[] out = new short[buf.remaining()];
        buf.get(out);
        return out;
    }

    public final String asString() {
        return new String(this.body);
    }

    public final long[] asUIntArray() throws IOException {
        final IntBuffer buf = ByteBuffer.wrap(this.body).order(this.getByteOrder()).asIntBuffer();
        final long[] out = new long[buf.remaining()];
        for(int i = 0; i < out.length; i++)
            out[i] = buf.get() & 0xFFFFFFFFl;
        return out;
    }

    public final int[] asUShortArray() throws IOException {
        final ShortBuffer buf = ByteBuffer.wrap(this.body).order(this.getByteOrder()).asShortBuffer();
        final int[] out = new int[buf.remaining()];
        for(int i = 0; i < out.length; i++)
            out[i] = buf.get() & 0xFFFF;
        return out;
    }

    private final ByteOrder getByteOrder() {
        return (this.client_type & MdsMessage.BIG_ENDIAN_MASK) == 0 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }

    public final byte getNargs() {
        return this.nargs;
    }

    public final synchronized void Send(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.msglen);
        dos.writeInt(this.status);
        dos.writeShort(this.length);
        dos.writeByte(this.getNargs());
        dos.writeByte(this.descr_idx);
        dos.writeByte(this.message_id);
        dos.writeByte(this.dtype);
        dos.writeByte(this.client_type);
        dos.writeByte(this.ndims);
        for(int i = 0; i < Descriptor.MAX_DIM; i++)
            dos.writeInt(this.dims[i]);
        dos.write(this.body, 0, this.body.length);
        dos.flush();
        if(this.descr_idx == (this.getNargs() - 1)) MdsMessage.msgid++;
        if(MdsMessage.msgid == 0) MdsMessage.msgid = 1;
    }

    protected final void useCompression(final boolean use_cmp) {
        this.status = (use_cmp ? MdsMessage.SUPPORTS_COMPRESSION | 5 : 0);
    }

    public final void verify() {
        this.status |= 1;
    }
}

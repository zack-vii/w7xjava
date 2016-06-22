package mds;

/* $Id$ */
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    public static final DataInput littleEndian(final DataInput decorated) {
        class LittleInput implements DataInput{
            private final ByteBuffer buffer = ByteBuffer.allocate(8);

            @Override
            public boolean readBoolean() throws IOException {
                return decorated.readBoolean();
            }

            @Override
            public byte readByte() throws IOException {
                return decorated.readByte();
            }

            @Override
            public char readChar() throws IOException {
                return decorated.readChar();
            }

            @Override
            public double readDouble() throws IOException {
                this.buffer.clear();
                this.buffer.order(ByteOrder.BIG_ENDIAN).putDouble(decorated.readDouble()).flip();
                return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getDouble();
            }

            @Override
            public float readFloat() throws IOException {
                this.buffer.clear();
                this.buffer.order(ByteOrder.BIG_ENDIAN).putFloat(decorated.readFloat()).flip();
                return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getFloat();
            }

            @Override
            public void readFully(final byte[] b) throws IOException {
                decorated.readFully(b);
            }

            @Override
            public void readFully(final byte[] b, final int off, final int len) throws IOException {
                decorated.readFully(b, off, len);
            }

            @Override
            public int readInt() throws IOException {
                this.buffer.clear();
                this.buffer.order(ByteOrder.BIG_ENDIAN).putInt(decorated.readInt()).flip();
                return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
            }

            @Override
            public String readLine() throws IOException {
                return decorated.readLine();
            }

            @Override
            public long readLong() throws IOException {
                this.buffer.clear();
                this.buffer.order(ByteOrder.BIG_ENDIAN).putLong(decorated.readLong()).flip();
                return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getLong();
            }

            @Override
            public short readShort() throws IOException {
                this.buffer.clear();
                this.buffer.order(ByteOrder.BIG_ENDIAN).putShort(decorated.readShort()).flip();
                return this.buffer.order(ByteOrder.LITTLE_ENDIAN).getShort();
            }

            @Override
            public int readUnsignedByte() throws IOException {
                return decorated.readUnsignedByte();
            }

            @Override
            public int readUnsignedShort() throws IOException {
                return this.readShort() & 0xFFFF;
            }

            @Override
            public String readUTF() throws IOException {
                return decorated.readUTF();
            }

            @Override
            public int skipBytes(final int n) throws IOException {
                return decorated.skipBytes(n);
            }
        }
        return new LittleInput();
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
        DataInput reader = new DataInputStream(dis);
        if(swap) reader = MdsMessage.littleEndian(reader);
        final int bytes_to_read = reader.readInt() - MdsMessage.HEADER_SIZE;
        final InflaterInputStream zis = new InflaterInputStream(dis);
        final byte[] buf = MdsMessage.readBuf(bytes_to_read, zis, listeners);
        while(zis.available() == 1)
            zis.skip(1); // EOF
        return buf;
    }

    public final synchronized static MdsMessage Receive(final InputStream dis, final Vector<ConnectionListener> listeners) throws IOException {
        final byte c_type;
        /*reader = new DataInputStream(dis);
        if(dis.markSupported()){// attempt on getting c_type using mark and reset caused hang
            dis.mark(16);
            dis.skip(14);
            c_type = reader.readByte();
            dis.reset();
        }else{*/
        final byte[] buf = MdsMessage.readBuf(MdsMessage.HEADER_SIZE, dis, null);
        DataInput reader = new DataInputStream(new ByteArrayInputStream(buf));
        c_type = buf[14];
        // }
        final boolean swap = ((c_type & MdsMessage.BIG_ENDIAN_MASK) == 0);
        if(swap) reader = MdsMessage.littleEndian(reader);
        final boolean compressed = ((c_type & MdsMessage.COMPRESSED) == MdsMessage.COMPRESSED);
        final int msglen = reader.readInt();
        final int status = reader.readInt();
        reader.skipBytes(2);// short length
        final byte nargs = reader.readByte();
        final byte descr_idx = reader.readByte();
        final byte message_id = reader.readByte();
        final byte dtype = reader.readByte();
        reader.skipBytes(1);// byte c_type
        final byte ndims = reader.readByte();
        final int[] dims = new int[Descriptor.MAX_DIM];
        for(int i = 0; i < Descriptor.MAX_DIM; i++)
            dims[i] = reader.readInt();
        final byte[] body;
        if(msglen > MdsMessage.HEADER_SIZE){
            if(compressed) body = MdsMessage.ReadCompressedBuf(dis, swap, listeners);
            else{
                body = MdsMessage.readBuf(msglen - MdsMessage.HEADER_SIZE, dis, listeners);
            }
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

    protected final double[] asDoubleArray() throws IOException {
        final double out[] = new double[this.body.length / Double.BYTES];
        DataInput reader = new DataInputStream(new ByteArrayInputStream(this.body));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        for(int i = 0; i < out.length; i++)
            out[i] = reader.readDouble();
        return out;
    }

    protected final float asFloat(final byte bytes[]) throws IOException {
        DataInput reader = new DataInputStream(new ByteArrayInputStream(bytes));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        return reader.readFloat();
    }

    protected final float[] asFloatArray() throws IOException {
        final float out[] = new float[this.body.length / Float.BYTES];
        DataInput reader = new DataInputStream(new ByteArrayInputStream(this.body));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        for(int i = 0; i < out.length; i++)
            out[i] = reader.readFloat();
        return out;
    }

    protected final int asInt(final byte bytes[]) throws IOException {
        DataInput reader = new DataInputStream(new ByteArrayInputStream(bytes));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        return reader.readInt();
    }

    public final int[] asIntArray() throws IOException {
        final int out[] = new int[this.body.length / Integer.BYTES];
        DataInput reader = new DataInputStream(new ByteArrayInputStream(this.body));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        for(int i = 0; i < out.length; i++)
            out[i] = reader.readInt();
        return out;
    }

    public final long[] asLongArray() throws IOException {
        final long out[] = new long[this.body.length / Long.BYTES];
        DataInput reader = new DataInputStream(new ByteArrayInputStream(this.body));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        for(int i = 0; i < out.length; i++)
            out[i] = reader.readLong();
        return out;
    }

    public final short asShort(final byte bytes[]) throws IOException {
        DataInput reader = new DataInputStream(new ByteArrayInputStream(bytes));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        return reader.readShort();
    }

    public final short[] asShortArray() throws IOException {
        final short out[] = new short[this.body.length / Short.BYTES];
        DataInput reader = new DataInputStream(new ByteArrayInputStream(this.body));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        for(int i = 0; i < out.length; i++)
            out[i] = reader.readShort();
        return out;
    }

    public final String asString() {
        return new String(this.body);
    }

    public final long[] asUIntArray() throws IOException {
        final long out[] = new long[this.body.length / Integer.BYTES];
        DataInput reader = new DataInputStream(new ByteArrayInputStream(this.body));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        for(int i = 0; i < out.length; i++)
            out[i] = reader.readInt() & 0xFFFFFFFFl;
        return out;
    }

    public final int[] asUShortArray() throws IOException {
        final int out[] = new int[this.body.length / Short.BYTES];
        DataInput reader = new DataInputStream(new ByteArrayInputStream(this.body));
        if(this.isBigEndian()) reader = MdsMessage.littleEndian(reader);
        for(int i = 0; i < out.length; i++)
            out[i] = reader.readShort() & 0xFFFF;
        return out;
    }

    public final byte getNargs() {
        return this.nargs;
    }

    private final boolean isBigEndian() {
        return (this.client_type & MdsMessage.BIG_ENDIAN_MASK) == 0;
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

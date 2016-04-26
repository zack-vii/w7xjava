package jScope;

/* $Id$ */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (12/10/99 18:49:35)
 */
public final class RandomAccessData{
    byte buffer[];
    int  position = 0;

    /**
     * RandomAccessData constructor comment.
     */
    public RandomAccessData(){
        super();
    }

    public RandomAccessData(final byte[] buffer) throws IOException{
        super();
        this.buffer = buffer;
    }

    public RandomAccessData(final RandomAccessFile file) throws IOException{
        super();
        final int len = (int)file.length();
        this.buffer = new byte[len];
        file.read(this.buffer);
    }

    /**
     * Insert the method's description here.
     * Creation date: (15/10/99 17:15:27)
     *
     * @return long
     */
    public final long getFilePointer() {
        return this.position;
    }

    public final byte readByte() throws java.io.IOException {
        return this.buffer[this.position++];
    }

    /**
     * Insert the method's description here.
     * Creation date: (15/10/99 17:13:31)
     *
     * @param size
     *            int
     */
    public final void readFully(final byte data[]) {
        for(int i = 0; i < data.length; i++)
            data[i] = this.buffer[this.position + i];
        this.position += data.length;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/10/99 19:05:42)
     */
    public final int readInt() throws java.io.IOException {
        int x = (this.buffer[this.position++] & 0xFF) << 24;
        x |= (this.buffer[this.position++] & 0xFF) << 16;
        x |= (this.buffer[this.position++] & 0xFF) << 8;
        x |= (this.buffer[this.position++] & 0xFF);
        return x;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/10/99 19:02:37)
     *
     * @exception java.io.IOException
     *                The exception description.
     */
    public final short readShort() throws java.io.IOException {
        short x = (short)((this.buffer[this.position++] & 0xFF) << 8);
        x |= (this.buffer[this.position++] & 0xFF);
        return x;
    }
    // public final String readString() throws IOException {}

    // public final String readString(final int length) throws IOException {}
    /**
     * Insert the method's description here.
     * Creation date: (15/10/99 17:17:05)
     *
     * @return java.lang.String
     */
    public final String readString0() throws IOException {
        final Vector<Byte> data = new Vector<Byte>();
        while(true){
            final byte b = this.readByte();
            if(b == 0) break;
            data.add(b);
        }
        return data.toArray().toString();
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/10/99 19:00:27)
     *
     * @exception java.io.IOException
     *                The exception description.
     */
    public final void seek(final long pos) throws IOException {
        this.position = (int)pos;
    }

    /**
     * Insert the method's description here.
     * Creation date: (15/10/99 17:16:08)
     *
     * @param amount
     *            int
     */
    public final void skipBytes(final int amount) {
        this.position += amount;
    }
}
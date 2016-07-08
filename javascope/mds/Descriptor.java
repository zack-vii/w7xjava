package mds;

/* $Id$ */
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

final public class Descriptor{
    public static final byte DTYPE_BYTE           = 6;
    public static final byte DTYPE_COMPLEX        = 12;
    public static final byte DTYPE_COMPLEX_DOUBLE = 13;
    public static final byte DTYPE_CSTRING        = 14;
    public static final byte DTYPE_DOUBLE         = 11;
    public static final byte DTYPE_EVENT          = 99;
    public static final byte DTYPE_EVENT_NOTIFY   = 99;
    public static final byte DTYPE_FLOAT          = 10;
    public static final byte DTYPE_LONG           = 8;
    public static final byte DTYPE_LONGLONG       = 9;
    public static final byte DTYPE_SHORT          = 7;
    public static final byte DTYPE_UBYTE          = 2;
    public static final byte DTYPE_ULONG          = 4;
    public static final byte DTYPE_ULONGLONG      = 5;
    public static final byte DTYPE_USHORT         = 3;
    public static final byte MAX_DIM              = 8;

    public static byte[] dataToByteArray(final Object o) {
        try{
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(baos);
            if(o instanceof Short) dos.writeShort(((Short)o).shortValue());
            else if(o instanceof Integer) dos.writeInt(((Integer)o).intValue());
            else if(o instanceof Float) dos.writeFloat(((Float)o).floatValue());
            else if(o instanceof Double) dos.writeDouble(((Double)o).doubleValue());
            else if(o instanceof Long) dos.writeLong(((Long)o).longValue());
            dos.close();
            return baos.toByteArray();
        }catch(final Exception e){
            System.err.println("# Descriptor.dataToByteArray(" + o + "): " + e);
        }
        return null;
    }

    static public int getDataSize(final byte type, final byte[] body) {
        switch(type){
            case DTYPE_CSTRING:
                return body.length;
            case DTYPE_UBYTE:
            case DTYPE_BYTE:
                return 1;
            case DTYPE_USHORT:
            case DTYPE_SHORT:
                return 2;
            case DTYPE_ULONG:
            case DTYPE_LONG:
            case DTYPE_FLOAT:
                return 4;
            case DTYPE_ULONGLONG:
            case DTYPE_LONGLONG:
            case DTYPE_DOUBLE:
                return 8;
        }
        return 0;
    }
    public byte   byte_data[];
    public int    dims[];
    public double double_data[];
    public byte   dtype;
    public String error = null;
    public float  float_data[];
    public int    int_data[];
    public long   long_data[];
    public short  short_data[];
    public int    status;
    public String strdata;

    public Descriptor(){}

    public Descriptor(final byte dtype, final int dims[], final byte byte_data[]){
        this.dtype = dtype;
        this.dims = dims;
        this.byte_data = byte_data;
    }

    public Descriptor(final int dims[], final byte byte_data[]){
        this.dtype = Descriptor.DTYPE_BYTE;
        this.dims = dims;
        this.byte_data = byte_data;
    }

    public Descriptor(final int dims[], final float float_data[]){
        this.dtype = Descriptor.DTYPE_FLOAT;
        this.dims = dims;
        this.float_data = float_data;
    }

    public Descriptor(final int dims[], final int int_data[]){
        this.dtype = Descriptor.DTYPE_LONG;
        this.dims = dims;
        this.int_data = int_data;
    }

    public Descriptor(final int dims[], final long long_data[]){
        this.dtype = Descriptor.DTYPE_LONGLONG;
        this.dims = dims;
        this.long_data = long_data;
    }

    public Descriptor(final int dims[], final short short_data[]){
        this.dtype = Descriptor.DTYPE_SHORT;
        this.dims = dims;
        this.short_data = short_data;
    }

    public Descriptor(final int dims[], final String strdata){
        this.dtype = Descriptor.DTYPE_CSTRING;
        this.dims = dims;
        this.strdata = strdata;
    }

    public Descriptor(final String error){
        this.error = error;
    }

    public byte[] dataToByteArray() {
        try{
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(baos);
            switch(this.dtype){
                case DTYPE_CSTRING:
                    dos.close();
                    if(this.strdata != null) return this.strdata.getBytes();
                    return this.byte_data;
                case DTYPE_UBYTE:
                case DTYPE_BYTE:
                    dos.close();
                    return this.byte_data;
                case DTYPE_USHORT:
                case DTYPE_SHORT:
                    for(final short element : this.short_data)
                        dos.writeShort(element);
                    break;
                case DTYPE_ULONG:
                case DTYPE_LONG:
                    for(final int element : this.int_data)
                        dos.writeInt(element);
                    break;
                case DTYPE_FLOAT:
                    for(final float element : this.float_data)
                        dos.writeFloat(element);
                    break;
                case DTYPE_ULONGLONG:
                case DTYPE_LONGLONG:
                    for(final long element : this.long_data)
                        dos.writeLong(element);
                    break;
                case DTYPE_DOUBLE:
                    for(final double element : this.double_data)
                        dos.writeDouble(element);
                    break;
            }
            dos.close();
            return baos.toByteArray();
        }catch(final Exception e){
            System.err.println("# Descriptor.dataToByteArray: " + e);
        }
        return null;
    }

    public int getInt() {
        return this.int_data[0];
    }

    public int getStatus() {
        return this.status;
    }
}
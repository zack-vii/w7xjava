package mds.data.descriptor;

import java.nio.ByteBuffer;

/** Array Descriptor **/
public abstract class ARRAY<T>extends Descriptor<T>{
    public static final class aflags{
        /** if set, scale is a power-of-two, otherwise, -ten **/
        boolean binscale;
        /** if set, indicates the bounds block is present **/
        boolean bounds;
        /** if set, indicates the multipliers block is present **/
        boolean coeff;
        /** if set, indicates column-major order (FORTRAN) **/
        boolean column;
        /** if set, indicates the array can be re-dimensioned **/
        boolean redim;

        public aflags(final boolean binscale, final boolean redim, final boolean column, final boolean coeff, final boolean bounds){
            this.binscale = binscale;
            this.redim = redim;
            this.column = column;
            this.coeff = coeff;
            this.bounds = bounds;
        }

        public aflags(final byte af){
            this.binscale = (af & 0x08) > 0;
            this.redim = (af & 0x10) > 0;
            this.column = (af & 0x20) > 0;
            this.coeff = (af & 0x40) > 0;
            this.bounds = (af & 0x80) > 0;
        }

        public final byte toByte() {
            byte af = 0;
            if(this.binscale) af |= 0x08;
            if(this.redim) af |= 0x10;
            if(this.column) af |= 0x20;
            if(this.coeff) af |= 0x40;
            if(this.bounds) af |= 0x80;
            return af;
        }
    }
    public static final class bounds{
        public final int l;
        public final int u;

        public bounds(final int l, final int u){
            this.l = l;
            this.u = u;
        }
    }
    public static final int       _sclB    = 8;
    public static final int       _dgtsB   = 9;
    public static final int       _afsB    = 10;
    public static final int       _dmctB   = 11;
    public static final int       _aszI    = 12;
    public static final int       _a0I     = 16;
    public static final int       _dmsIa   = 20;
    public static final byte      CLASS    = 4;
    protected static final aflags f_array  = new aflags(false, true, true, false, false);
    protected static final aflags f_bounds = new aflags(false, true, true, true, true);
    protected static final aflags f_coeff  = new aflags(false, true, true, true, false);
    public static final byte      MAX_DIM  = 8;

    private static final short getLength(final int[] shape, final byte dtype, final int size) {
        if(shape == null || shape.length == 0 || shape[0] == 0) return Descriptor_A.getDataSize(dtype, 0);
        int arrlen = shape[0];
        for(int i = 1; i < shape.length; i++)
            arrlen *= shape[i];
        return (short)(size / arrlen);
    }
    /** (8,b) scale **/
    public final byte     scale;
    /** (9,b) digits **/
    public final byte     digits;
    /** (10,b) array flags **/
    public final aflags   aflags;
    /** (11,b) dim count **/
    public final byte     dimct;
    /** (12,i) array size **/
    public final int      arsize;
    /** (16,i) a0 **/
    public final int      a0;
    /** (20,i) dimensions **/
    public final int[]    dims;
    /** (20+dimct*4,2i) bounds **/
    public final bounds[] bounds;

    protected ARRAY(final byte dtype, final byte dclass, final ByteBuffer byteBuffer, final int... shape){
        super(ARRAY.getLength(shape, dtype, byteBuffer.limit()), dtype, dclass, byteBuffer, shape.length > 1 ? ARRAY._dmsIa + shape.length * Integer.BYTES : ARRAY._a0I, 0);
        final ByteBuffer b = this.b.duplicate().order(this.b.order());
        b.position(ARRAY._sclB);
        b.put(this.scale = (byte)0);
        b.put(this.digits = (byte)0);
        b.put((this.aflags = shape.length > 1 ? ARRAY.f_coeff : ARRAY.f_array).toByte());
        b.put(this.dimct = (byte)shape.length);
        b.putInt(this.arsize = byteBuffer.limit());
        this.a0 = this.pointer;
        this.dims = shape;
        this.bounds = null;
        if(shape.length > 1){
            b.putInt(this.pointer);
            b.asIntBuffer().put(shape);
        }
    }

    protected ARRAY(final ByteBuffer b){
        super(b);
        this.scale = b.get();
        this.digits = b.get();
        this.aflags = new aflags(b.get());
        this.dimct = b.get();
        this.arsize = b.getInt();
        if(this.aflags.coeff || this.aflags.bounds){
            this.a0 = b.getInt();
            this.dims = new int[this.dimct];
            for(int i = 0; i < this.dimct; i++)
                this.dims[i] = b.getInt();
        }else{
            this.a0 = 0;
            this.dims = new int[]{this.length == 0 ? 0 : this.arsize / this.length};
        }
        if(this.aflags.bounds){
            this.bounds = new bounds[this.dimct];
            for(int i = 0; i < this.dimct; i++)
                this.bounds[i] = new bounds(b.getInt(), b.getInt());
        }else this.bounds = null;
    }

    /** Returns the number of elements contained in the Array **/
    public final int getLength() {
        if(this.dimct == 0) return 0;
        int length = 1;
        for(final int dim : this.dims)
            length *= dim;
        return length;
    }

    @Override
    public final int[] getShape() {
        return this.dims;
    }
}

package mds.data.descriptor;

import java.nio.ByteBuffer;

/** Array Descriptor **/
public abstract class ARRAY<T>extends Descriptor<T>{
    public static final class aflags{
        boolean            binscale = true; // if set, scale is a power-of-two, otherwise, -ten
        boolean            bounds   = true; // if set, indicates the bounds block is present
        public final short BYTES    = 8;
        boolean            coeff    = true; // if set, indicates the multipliers block is present
        boolean            column   = true; // if set, indicates column-major order (FORTRAN)
        boolean            redim    = true; // if set, indicates the array can be re-dimensioned

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
    public static final int       _a0I     = 16;
    public static final int       _afsB    = 10;
    public static final int       _aszI    = 12;
    public static final int       _dgtsB   = 9;
    public static final int       _dmctB   = 11;
    public static final int       _dmsI    = 20;
    public static final int       _sclB    = 8;
    public static final byte      CLASS    = 4;
    protected static final aflags f_array  = new aflags(false, true, true, false, false);
    protected static final aflags f_bounds = new aflags(false, true, true, true, true);
    protected static final aflags f_coeff  = new aflags(false, true, true, true, false);
    public static final byte      MAX_DIM  = 8;
    public final int              a0;
    public final aflags           aflags;
    public final int              arsize;
    public final bounds[]         bounds;
    public final byte             digits;
    public final byte             dimct;
    public final int[]            dims;
    public final byte             scale;

    public ARRAY(final byte dtype, final byte dclass, final byte[] buf, final int nelements){
        super((short)(buf.length / nelements), dtype, dclass, buf);
        this.scale = (byte)0;
        this.digits = (byte)0;
        this.aflags = nelements > 1 ? ARRAY.f_coeff : ARRAY.f_array;
        this.dimct = nelements > 1 ? (byte)1 : (byte)0;
        this.arsize = buf.length;
        this.a0 = this.pointer;
        this.dims = (this.dimct > 0) ? new int[]{nelements} : null;
        this.bounds = null;
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
            this.dims = new int[]{this.arsize / this.length};
        }
        if(this.aflags.bounds){
            this.bounds = new bounds[this.dimct];
            for(int i = 0; i < this.dimct; i++)
                this.bounds[i] = new bounds(b.getInt(), b.getInt());
        }else this.bounds = null;
    }
}

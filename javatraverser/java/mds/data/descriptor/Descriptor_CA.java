package mds.data.descriptor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import debug.DEBUG;
import mds.MdsException;
import mds.mdsip.Message;

/** Compressed Array Descriptor (-61 : 195) **/
public class Descriptor_CA extends ARRAY<ByteBuffer>{
    private static final class DECOMPRESS{
        private static final int       BITSX   = 10;
        private static final int       BITSY   = 6;
        private static final IntBuffer diff    = IntBuffer.allocate(DECOMPRESS.MAXX);
        private static final IntBuffer exce    = IntBuffer.allocate(DECOMPRESS.MAXX);
        private static final int       FIELDSX = 2;
        private static final int       FIELDSY = DECOMPRESS.BITSY + DECOMPRESS.BITSX;
        private static final IntBuffer header  = IntBuffer.allocate(2);
        private static final int[]     MASKS   = new int[]{0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff, 0x7fff, 0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff,};
        private static final int       MASKX   = DECOMPRESS.MASKS[DECOMPRESS.BITSX];
        private static final int       MASKY   = DECOMPRESS.MASKS[DECOMPRESS.BITSY];
        private static final int       MAXX    = 1024;
        // private static final int MAXY = 32;

        private static final int X_OF_INT(final int x) {
            return (x >> DECOMPRESS.BITSY) & DECOMPRESS.MASKX;
        }

        private static final int Y_OF_INT(final int y) {
            return y & DECOMPRESS.MASKY;
        }
        private int                bit;
        private final Descriptor_A out_dsc;

        @SuppressWarnings("unused")
        private DECOMPRESS(){
            this.out_dsc = null;
        }

        public DECOMPRESS(final Descriptor_CA ca) throws MdsException{
            if(DEBUG.D) System.out.println("DECOMPRESS: " + ca.getDName() + ca.arsize);
            this.out_dsc = this.mdsXpand(ca.arsize / ca.length, (ARRAY)ca.payload.getDescriptor(3), ca);
        }

        /** MdsUnpk expects LittleEndian buffers **/
        private final void MdsUnpk(final int nbits, int nitems, final ByteBuffer pack_in, final IntBuffer items_in) {
            final IntBuffer items = ((IntBuffer)items_in.position(0)).duplicate();
            final ByteBuffer pack = (ByteBuffer)pack_in.duplicate().order(ByteOrder.LITTLE_ENDIAN).position((this.bit >> 5) * Integer.BYTES);
            if(DEBUG.D) System.err.print(String.format("MdsUnpk(%d, %d, [..%d..], %d) -> ", nbits, nitems, pack.getInt(pack.position()), this.bit));
            if(nbits == 0){// zero fill
                if(DEBUG.D) System.err.println("zero fill");
                for(int i = 0; i < nitems; i++)
                    items.put(0);
                return;
            }
            final int size = nbits >= 0 ? nbits : -nbits;
            int off = this.bit & 31;
            final int test = 32 - size;
            this.bit += size * nitems;
            if(test == 0){// 32-bit data
                if(DEBUG.D) System.err.println("32-bit data");
                if((off & 7) == 0){// is multiple of byte
                    pack.position(pack.position() + (off >> 3));
                    for(; nitems-- > 0;)
                        items.put(pack.getInt());
                    return;
                }
                int hold;
                final int ioff, masko = DECOMPRESS.MASKS[ioff = 32 - off];
                for(; nitems-- > 0;){
                    hold = (pack.getInt() >> off) & masko;
                    hold |= pack.getInt(pack.position()) << ioff;
                    items.put(hold);
                }
                return;
            }
            final int mask = DECOMPRESS.MASKS[size], full = mask + 1, max = mask >> 1;
            int ioff, masko, hold;
            if(nbits < 0){ // sign extended
                if(DEBUG.D) System.err.println("sign extended");
                for(; nitems-- > 0;){
                    if(off >= test){
                        masko = DECOMPRESS.MASKS[ioff = 32 - off];
                        hold = (pack.getInt() >> off) & masko;
                        hold |= (pack.getInt(pack.position()) << ioff) & mask;
                        if(hold > max) items.put(hold - full);
                        else items.put(hold);
                        off -= test;
                    }else{
                        hold = (pack.getInt(pack.position()) >> off) & mask;
                        if(hold > max) items.put(hold - full);
                        else items.put(hold);
                        off += size;
                    }
                }
                return;
            }
            if(nbits > 0){// zero extended
                if(DEBUG.D) System.err.println("zero extended");
                for(; nitems-- > 0;){
                    if(off >= test){
                        masko = DECOMPRESS.MASKS[ioff = 32 - off];
                        hold = (pack.getInt() >> off) & masko;
                        hold |= (pack.getInt(pack.position()) << ioff) & mask;
                        items.put(hold);
                        off -= test;
                    }else{
                        hold = (pack.getInt(pack.position()) >> off) & mask;
                        items.put(hold);
                        off += size;
                    }
                }
                return;
            }
        }

        private final Descriptor_A mdsXpand(int nitems, ARRAY pack_dsc, final Descriptor_CA items_dsc) throws MdsException {
            if(pack_dsc instanceof Descriptor_CA) pack_dsc = new DECOMPRESS((Descriptor_CA)pack_dsc).out_dsc;
            if(DEBUG.D) System.out.println("mdsXpand: " + pack_dsc.getDName() + pack_dsc.arsize);
            this.bit = 0;
            final ByteBuffer bpack = ByteBuffer.allocate(pack_dsc.arsize + 4).order(ByteOrder.LITTLE_ENDIAN);
            // bpack.asIntBuffer().put(pack_dsc.getBuffer().asIntBuffer()).position(0); // would swap ints
            bpack.put(pack_dsc.getBuffer()).position(0); // take buffer as is
            final ByteBuffer bout = ByteBuffer.allocate(items_dsc.arsize + items_dsc.pointer).order(pack_dsc.b.order());
            bout.put((ByteBuffer)items_dsc.b.duplicate().limit(items_dsc.pointer));
            bout.put(Descriptor._clsB, Descriptor_A.CLASS).position(0);
            final Descriptor_A out_dsc = Descriptor_A.deserialize(bout);
            final int limit = pack_dsc.arsize * 8;
            final int dtype = items_dsc.dtype;
            int step;
            int j = 0;
            final ByteBuffer bx = out_dsc.getBuffer();
            int ye, xe, yn, xn, xhead;
            final IntBuffer n = DECOMPRESS.diff, e = DECOMPRESS.exce;
            int old, buf = 0, mark = 0;
            if(dtype == DTYPE.T) step = Byte.BYTES;
            else if((items_dsc.length & (Integer.BYTES - 1)) == 0) step = Integer.BYTES;
            else if((items_dsc.length & (Short.BYTES - 1)) == 0) step = Short.BYTES;
            else step = Byte.BYTES;
            nitems *= items_dsc.length / step;
            /********************************
             * Note the sign-extended unpacking.
             ********************************/
            final ByteBuffer fb = ByteBuffer.allocate(Integer.BYTES);
            final boolean isfloat = dtype == DTYPE.F || dtype == DTYPE.FC || dtype == DTYPE.D || dtype == DTYPE.DC || dtype == DTYPE.G || dtype == DTYPE.GC || dtype == DTYPE.H || dtype == DTYPE.HC;
            while(nitems > 0){
                if((this.bit + 2 * DECOMPRESS.FIELDSY) > limit) break;
                this.MdsUnpk(DECOMPRESS.FIELDSY, DECOMPRESS.FIELDSX, bpack, DECOMPRESS.header);
                xhead = j = DECOMPRESS.X_OF_INT(DECOMPRESS.header.get(0)) + 1;
                if(j > nitems) j = nitems;
                xn = j;
                yn = -DECOMPRESS.Y_OF_INT(DECOMPRESS.header.get(0));
                xe = DECOMPRESS.X_OF_INT(DECOMPRESS.header.get(1));
                ye = -DECOMPRESS.Y_OF_INT(DECOMPRESS.header.get(1)) - 1;
                if(this.bit - ye * xe - yn * j > limit) break;
                nitems -= j;
                this.MdsUnpk((byte)yn, xn, bpack, DECOMPRESS.diff);
                if(xe != 0){
                    this.bit -= yn * (xhead - j);
                    this.MdsUnpk((byte)ye, xe, bpack, DECOMPRESS.exce);
                    mark = -1 << (-yn - 1);
                    e.position(0);
                }
                /***********************************
                 * Summation. Old=0 here, is new start.
                 * Sign and field extend.
                 * Note, signed and unsigned are same.
                 ***********************************/
                n.position(0);
                old = 0;
                for(; j-- > 0;){
                    buf = n.get();
                    if(xe != 0 && e != null && (buf == mark)) old += e.get();
                    else old += buf;
                    if(step == Integer.BYTES) if(isfloat){
                        fb.putInt(0, old);
                        bx.putShort(fb.getShort(1));
                        bx.putShort(fb.getShort(0));
                    }else bx.putInt(old);
                    else if(step == Short.BYTES) bx.putShort((short)old);
                    else bx.put((byte)old);
                }
            }
            return out_dsc;
        }
    }
    public static final byte CLASS = -61;// 195

    public static final Descriptor_CA deserialize(final ByteBuffer b) {
        try{
            return new Descriptor_CA(b);
        }catch(final Exception exc){}
        return null;
    }
    /*
    public static final void main(final String[] args) throws MdsException {//TODO:main
        final Database db = new Database(null, "TEST", 1l, Database.NORMAL);
        final Descriptor rec = db.getData(new Nid(18));// 8,9
        System.out.println(rec.decompile());
        System.exit(0);
    }
    */
    public Descriptor_A decmprs;
    public Descriptor_R payload;

    public Descriptor_CA(final ByteBuffer b) throws Exception{
        super(b);
        if(this.pointer == 0) this.payload = null;
        else this.payload = Descriptor_R.deserialize(this.getBuffer());
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        if(this.payload == null) return this.substitute(pout);
        return this.resolve().decompile(prec, pout, mode);
    }

    @Override
    public ByteBuffer getValue(final ByteBuffer b) {
        return b.slice().order(b.order());
    }

    private Descriptor resolve() {
        if(this.decmprs != null) return this.decmprs;
        try{
            return(this.decmprs = new DECOMPRESS(this).out_dsc);
        }catch(final MdsException e){
            e.printStackTrace();
            return this.payload;
        }
    }

    private final StringBuilder substitute(final StringBuilder pout) {
        return pout.append("ZERO(").append(this.arsize / this.length).append(", 0").append(DTYPE.getSuffix(this.dtype)).append(')');
    }

    @Override
    public double[] toDoubles() {
        if(this.payload == null) return null;
        return this.resolve().toDoubles();
    }

    @Override
    public float[] toFloats() {
        if(this.payload == null) return null;
        return this.resolve().toFloats();
    }

    @Override
    public int[] toInts() {
        if(this.payload == null) return null;
        return this.resolve().toInts();
    }

    @Override
    public long[] toLongs() {
        if(this.payload == null) return null;
        return this.resolve().toLongs();
    }

    @Override
    public Message toMessage(final byte descr_idx, final byte n_args) {
        return this.payload == null ? null : this.payload.toMessage(descr_idx, n_args);// TODO: null
    }
}
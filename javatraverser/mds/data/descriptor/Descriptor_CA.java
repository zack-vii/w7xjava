package mds.data.descriptor;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import jTraverser.Database;
import mds.MdsException;
import mds.data.descriptor_s.Nid;
import mds.mdsip.Message;

/** Compressed Array Descriptor (-61 : 195) **/
public class Descriptor_CA extends ARRAY<ByteBuffer>{
    private static final class DECOMPRESS{
        private static final int BITSX   = 10;
        private static final int BITSY   = 6;
        private static final int FIELDSX = 2;
        private static final int FIELDSY = DECOMPRESS.BITSY + DECOMPRESS.BITSX;
        private static final int MASKX   = (1 << DECOMPRESS.BITSX) - 1;
        private static final int MASKY   = (1 << DECOMPRESS.BITSY) - 1;
        private static final int MAXX    = 1024;
        // private static final int MAXY = 32;

        private static final int X_OF_INT(final int x) {
            return (x >> DECOMPRESS.BITSY) & DECOMPRESS.MASKX;
        }

        private static final int Y_OF_INT(final int y) {
            return y & DECOMPRESS.MASKY;
        }
        private int                bit;
        private final Descriptor_A out_dsc;

        public DECOMPRESS(final Descriptor_CA ca) throws MdsException{
            this.out_dsc = this.mdsXpand(ca.arsize / ca.length, (ARRAY)ca.payload.dscptrs[3], ca);
        }

        private final void MdsUnpk(final int nbits, final int nitems, final ByteBuffer pack, final IntBuffer items) {
            pack.position((this.bit >> 5) * Integer.BYTES);
            items.position(0);
            final int size = nbits >= 0 ? nbits : -nbits;
            int off = this.bit & 31;
            final int test = 32 - size;
            int hold, last;
            this.bit += size * nitems;
            // zero fill
            if(size == 0) for(int i = 0; i < nitems; i++)
                items.put(0);
            // 32-bit data
            else if(test == 0){
                if((off & 7) == 0){
                    int i;
                    pack.position(pack.position() + off >> 3);
                    for(i = 0; i < nitems; i++)
                        items.put(pack.getInt());
                }else{
                    last = pack.getInt();
                    for(int i = 0; i < nitems; i++){
                        hold = last >> off;
                        last = pack.getInt();
                        hold |= last << (32 - off);
                        items.put(hold);
                    }
                }
            }else{
                final int full = 1 << size, max = 1 << (size - 1);
                final int mask = full - 1;
                if(nbits < 0){ // sign extended
                    last = pack.getInt();
                    for(int i = 0; i < nitems; i++)
                        if(off >= test){
                            hold = last >> off;
                            last = pack.getInt();
                            hold |= (last << (32 - off)) & mask;
                            if(hold > max) hold -= full;
                            items.put(hold);
                            off -= test;
                        }else{
                            hold = (last >> off) & mask;
                            if(hold > max) hold -= full;
                            items.put(hold);
                            off += size;
                        }
                }
                // zero extended
                else if(nbits > 0){
                    last = pack.getInt();
                    for(int i = 0; i < nitems; i++)
                        if(off >= test){
                            hold = last >> off;
                            last = pack.getInt();
                            hold |= (last << (32 - off)) & mask;
                            items.put(hold);
                            off -= test;
                        }else{
                            hold = (last >> off) & mask;
                            items.put(hold);
                            off += size;
                        }
                }
            }
            items.position(0);
        }

        private final Descriptor_A mdsXpand(int nitems, final ARRAY pack_dsc, final Descriptor_CA items_dsc) throws MdsException {
            this.bit = 0;
            final ByteBuffer bpack = ByteBuffer.allocate(pack_dsc.arsize + 4).order(pack_dsc.b.order());
            bpack.put(pack_dsc.getBuffer()).position(0);
            final ByteBuffer bout = ByteBuffer.allocate(items_dsc.arsize + items_dsc.pointer).order(pack_dsc.b.order());
            bout.put((ByteBuffer)items_dsc.b.duplicate().limit(items_dsc.pointer));
            bout.position(0);
            final Descriptor_A out_dsc = Descriptor_A.deserialize(bout);
            final int limit = pack_dsc.arsize * 8;
            final int dtype = items_dsc.dtype;
            int step;
            int j = 0;
            final ByteBuffer bx = out_dsc.getBuffer();
            int ye, xe, yn, xn, xhead;
            int[] e = null, n = null;
            int pe = 0, pn = 0;
            int old, mark = 0;
            final IntBuffer header = IntBuffer.allocate(2);
            final IntBuffer diff = IntBuffer.allocate(DECOMPRESS.MAXX);
            final IntBuffer exce = IntBuffer.allocate(DECOMPRESS.MAXX);
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
                if((this.bit + 2 * (DECOMPRESS.BITSY + DECOMPRESS.BITSX)) > limit) break;
                this.MdsUnpk(DECOMPRESS.FIELDSY, DECOMPRESS.FIELDSX, bpack, header);
                xhead = j = DECOMPRESS.X_OF_INT(header.get(0)) + 1;
                if(j > nitems) j = nitems;
                xn = j;
                yn = -DECOMPRESS.Y_OF_INT(header.get(0));
                xe = DECOMPRESS.X_OF_INT(header.get(1));
                ye = -((header.get(1) & DECOMPRESS.MASKY) + 1);
                if(this.bit - ye * xe - yn * j > limit) break;
                nitems -= j;
                this.MdsUnpk((byte)yn, xn, bpack, diff);
                if(xe != 0){
                    this.bit -= yn * (xhead - j);
                    this.MdsUnpk((byte)ye, xe, bpack, exce);
                    e = exce.array();
                    pe = 0;
                    mark = -1 << (-yn - 1);
                }
                /***********************************
                 * Summation. Old=0 here, is new start.
                 * Sign and field extend.
                 * Note, signed and unsigned are same.
                 ***********************************/
                n = diff.array();
                pn = 0;
                old = 0;
                for(; j-- > 0;){
                    if(xe != 0 && e != null && (n[pn] == mark)){
                        old += e[pe];
                        pe += 1;
                    }else old += n[pn];
                    if(step == Integer.BYTES) if(isfloat){
                        fb.putInt(0, old);
                        bx.putShort(fb.getShort(1));
                        bx.putShort(fb.getShort(0));
                    }else bx.putInt(old);
                    else if(step == Short.BYTES) bx.putShort((short)old);
                    else bx.put((byte)old);
                    pn += 1;
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

    public static final void main(final String[] args) throws MdsException {// TODO: main
        final Database db = new Database(null, "TEST", 1l, Database.NORMAL);
        final Descriptor rec = db.getData(new Nid(9));
        System.out.println(rec.decompile());
        final int size = 31;
        int full, max, mask;
        full = 1 << size;
        max = 1 << (size - 1);
        mask = full - 1;
        System.out.println(String.format("%x , %x , %x", full, mask, max));
        // System.out.println(String.format("%x , %x", DECOMPRESS.X_OF_INT(0xffffffff), DECOMPRESS.Y_OF_INT(0xffffffff)));
        System.exit(0);
    }
    public Descriptor_R payload;

    public Descriptor_CA(final ByteBuffer b) throws Exception{
        super(b);
        if(this.pointer == 0) this.payload = null;
        else this.payload = Descriptor_R.deserialize(this.getBuffer());
    }

    @Override
    public final String decompile() {
        if(this.payload == null) return new StringBuffer(64).append("ZERO(").append(this.arsize / this.length).append(", 0").append(DTYPE.getSuffix(this.dtype)).append(')').toString();
        try{
            return new DECOMPRESS(this).out_dsc.decompile();
        }catch(final Exception e){
            e.printStackTrace();
            return this.payload.decompile();
        }
    }

    @Override
    public ByteBuffer getValue(final ByteBuffer b) {
        return b.slice().order(b.order());
    }

    @Override
    public double[] toDouble() {
        return this.payload == null ? null : this.payload.toDouble();
    }

    @Override
    public float[] toFloat() {
        return this.payload == null ? null : this.payload.toFloat();
    }

    @Override
    public int[] toInt() {
        return this.payload == null ? null : this.payload.toInt();
    }

    @Override
    public long[] toLong() {
        return this.payload == null ? null : this.payload.toLong();
    }

    @Override
    public Message toMessage(final byte descr_idx, final byte n_args) {
        return this.payload == null ? null : this.payload.toMessage(descr_idx, n_args);// TODO: null
    }

    @Override
    public final String toString() {
        if(this.payload == null) return new StringBuffer(64).append("ZERO(").append(this.arsize / this.length).append(", 0").append(DTYPE.getSuffix(this.dtype)).append(')').toString();
        try{
            return new DECOMPRESS(this).out_dsc.toString();
        }catch(final Exception e){
            return this.payload.toString();
        }
    }
}
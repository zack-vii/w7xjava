package mds.data.descriptor;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import mds.mdsip.Message;

/** Compressed Array Descriptor **/
public class Descriptor_CA extends ARRAY<ByteBuffer>{
    public static final byte CLASS = -61;// 195

    public static final Descriptor_CA deserialize(final ByteBuffer b) {
        try{
            return new Descriptor_CA(b);
        }catch(final Exception exc){}
        return null;
    }

    private static int MdsUnpk(final byte nbits, final int nitems, final ByteBuffer pack, final IntBuffer items, final int bit) {
        pack.position((bit >> 5) * Integer.BYTES);
        items.position(0);
        int off = bit & 31;
        final int size = nbits >= 0 ? nbits : -nbits;
        final int test = 32 - size;
        long hold, last;
        final long mask = (1l << size) - 1l;
        // 32-bit data
        if(test == 0){
            if((off & 31) == 0){
                int i;
                pack.position(pack.position() + off >> 3);
                for(i = 0; i < nitems; i++)
                    items.put(pack.getInt());
            }else{
                last = pack.getInt() & 0xFFFFFFFFl;
                for(int i = 0; i < nitems; i++){
                    hold = last >> off;
                    last = pack.getInt() & 0xFFFFFFFFl;
                    hold |= last << (32 - off);
                    items.put((int)hold);
                }
            }
        }else if(nbits < 0){ // sign extended
            final long full = mask + 1, max = mask >> 1;
            last = pack.getInt() & 0xFFFFFFFFl;
            for(int i = 0; i < nitems; i++)
                if(off >= test){
                    hold = (int)(last >> off);
                    last = pack.getInt() & 0xFFFFFFFFl;
                    hold |= (last << (32 - off)) & mask;
                    if(hold > max) hold -= full;
                    items.put((int)hold);
                    off -= test;
                }else{
                    hold = (last >> off) & mask;
                    if(hold > max) hold -= full;
                    items.put((int)hold);
                    off += size;
                }
        }
        // zero extended
        else if(nbits > 0){
            last = pack.getInt() & 0xFFFFFFFFl;
            for(int i = 0; i < nitems; i++)
                if(off >= test){
                    hold = last >> off;
                    last = pack.getInt() & 0xFFFFFFFFl;
                    hold |= (last << (32 - off)) & mask;
                    items.put((int)hold);
                    off -= test;
                }else{
                    hold = (last >> off) & mask;
                    items.put((int)hold);
                    off += size;
                }
        }
        // zero fill
        else for(int i = 0; i < nitems; i++)
            items.put(0);
        items.position(0);
        return bit + size * nitems;
    }

    private static Descriptor_A mdsXpand(int nitems, final ARRAY pack_dsc, final Descriptor_CA items_dsc, int bit) throws Exception {
        final int MAXX = 1024;
        // final int MAXY = 32;
        final int BITSX = 10;
        final int BITSY = 6;
        final int MASKX = (1 << BITSX) - 1;
        final int MASKY = (1 << BITSY) - 1;
        final int FIELDSY = BITSY + BITSX;
        final int FIELDSX = 2;
        final ByteBuffer bpack = ByteBuffer.allocate(pack_dsc.arsize + 4).order(pack_dsc.b.order());
        bpack.put(pack_dsc.getBuffer()).position(0);
        final ByteBuffer bout = ByteBuffer.allocate(items_dsc.arsize + items_dsc.pointer).order(items_dsc.b.order());
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
        final IntBuffer diff = IntBuffer.allocate(MAXX);
        final IntBuffer exce = IntBuffer.allocate(MAXX);
        if(dtype == DTYPE.T) step = Byte.BYTES;
        else if((items_dsc.length & (Integer.BYTES - 1)) == 0) step = Integer.BYTES;
        else if((items_dsc.length & (Short.BYTES - 1)) == 0) step = Short.BYTES;
        else step = Byte.BYTES;
        nitems *= items_dsc.length / step;
        /********************************
         * Note the sign-extended unpacking.
         ********************************/
        final ByteBuffer fb = ByteBuffer.allocate(Integer.BYTES).order(items_dsc.b.order());
        final boolean isfloat = dtype == DTYPE.F || dtype == DTYPE.FC || dtype == DTYPE.D || dtype == DTYPE.DC || dtype == DTYPE.G || dtype == DTYPE.GC || dtype == DTYPE.H || dtype == DTYPE.HC;
        while(nitems > 0){
            if((bit + 2 * (BITSY + BITSX)) > limit) break;
            bit = Descriptor_CA.MdsUnpk((byte)FIELDSY, FIELDSX, bpack, header, bit);
            xhead = j = ((header.get(0) >> BITSY) & MASKX) + 1;
            if(j > nitems) j = nitems;
            xn = j;
            yn = -(header.get(0) & MASKY);
            xe = (header.get(1) >> BITSY) & MASKX;
            ye = -((header.get(1) & MASKY) + 1);
            if(bit - ye * xe - yn * j > limit) break;
            nitems -= j;
            bit = Descriptor_CA.MdsUnpk((byte)yn, xn, bpack, diff, bit);
            if(xe != 0){
                bit = Descriptor_CA.MdsUnpk((byte)ye, xe, bpack, exce, bit - yn * (xhead - j));
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
                    bx.putShort(fb.getShort(0));
                    bx.putShort(fb.getShort(1));
                }else bx.putInt(old);
                else if(step == Short.BYTES) bx.putShort((short)old);
                else bx.put((byte)old);
                pn += 1;
            }
        }
        return out_dsc;
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
            return Descriptor_CA.mdsXpand(this.arsize / this.length, (ARRAY)this.payload.dscptrs[3], this, 0).decompile();
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
            return Descriptor_CA.mdsXpand(this.arsize / this.length, (ARRAY)this.payload.dscptrs[3], this, 0).toString();
        }catch(final Exception e){
            return this.payload.toString();
        }
    }
}
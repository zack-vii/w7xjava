package mds.data.descriptor;

import java.nio.ByteBuffer;
import mds.MdsException;

/** Array of Descriptor (-60 : 196) **/
public class Descriptor_APD extends Descriptor_A<Descriptor>{
    public static final byte     CLASS  = -60;
    public static final String   name   = "APD";
    private static final boolean atomic = false;

    public static Descriptor_A deserialize(final ByteBuffer b) throws MdsException {
        return new Descriptor_APD(b);
    }

    protected Descriptor_APD(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final StringBuilder decompileT(final StringBuilder pout, final Descriptor t) {
        return t == null ? pout.append("*") : t.decompile(Descriptor.P_STMT, pout, Descriptor.DECO_NRM);
    }

    @Override
    public final String getDTypeName() {
        return Descriptor_APD.name;
    }

    @Override
    protected final Descriptor getElement(final ByteBuffer b) {
        final ByteBuffer bd = this.b.duplicate().order(this.b.order());
        try{
            return Descriptor.deserialize((ByteBuffer)bd.position(b.getInt()));
        }catch(final Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Descriptor getScalar(final int idx) {
        return this.getValue(idx);
    }

    @Override
    protected final String getSuffix() {
        return "";
    }

    @Override
    protected final Descriptor[] initArray(final int size) {
        return new Descriptor[size];
    }

    @Override
    public boolean isAtomic() {
        return Descriptor_APD.atomic;
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Descriptor value) {}

    @Override
    public final byte toByte(final Descriptor t) {
        return t.toByte();
    }

    @Override
    public final double toDouble(final Descriptor t) {
        return t.toDouble();
    }

    @Override
    public final float toFloat(final Descriptor t) {
        return t.toFloat();
    }

    @Override
    public final int toInt(final Descriptor t) {
        return t.toInt();
    }

    @Override
    public final long toLong(final Descriptor t) {
        return t.toLong();
    }

    @Override
    public final short toShort(final Descriptor t) {
        return t.toShort();
    }

    @Override
    protected final String TtoString(final Descriptor t) {
        return Descriptor.toString(t);
    }
}

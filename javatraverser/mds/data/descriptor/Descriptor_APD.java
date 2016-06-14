package mds.data.descriptor;

import java.nio.ByteBuffer;
import mds.MdsException;

/** Array of Descriptor (-60 : 196) **/
public class Descriptor_APD extends Descriptor_A<Descriptor>{
    public static final byte   CLASS = -60;
    public static final String name  = "APD";

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
    protected final boolean format() {
        return false;
    }

    @Override
    protected final String getDName() {
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
    protected final Descriptor[] initArray(final int size) {
        return new Descriptor[size];
    }

    @Override
    public final double toDouble(final Descriptor t) {
        return t.toDouble()[0];
    }

    @Override
    public final float toFloat(final Descriptor t) {
        return t.toFloat()[0];
    }

    @Override
    public final int toInt(final Descriptor t) {
        return t.toInt()[0];
    }

    @Override
    public final long toLong(final Descriptor t) {
        return t.toLong()[0];
    }

    @Override
    protected final String TtoString(final Descriptor t) {
        return Descriptor.toString(t);
    }
}

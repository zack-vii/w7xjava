package mds.data.descriptor;

import java.nio.ByteBuffer;
import mds.MdsException;

/*
 * Array Descriptor
 */
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
    protected final String decompileT(final Descriptor t) {
        return Descriptor.decompile(t);
    }

    @Override
    public final String format(final String in) {
        return in;
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
    public final String toString() {
        final String[] elements = new String[this.arsize / this.length];
        for(int i = 0; i < elements.length; i++){
            final Descriptor desc = this.getValue(i);
            elements[i] = desc == null ? "*" : desc.toString();
        }
        return new StringBuilder(256).append('[').append(String.join(", ", elements)).append(']').toString();
    }

    @Override
    protected final String TtoString(final Descriptor t) {
        return Descriptor.toString(t);
    }
}

package mds.data.descriptor_a;

import java.nio.ByteBuffer;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_s.Nid;

public final class NidArray extends Descriptor_A<Nid>{
    public NidArray(){
        super(DTYPE.NID, ByteBuffer.allocate(0));
    }

    public NidArray(final ByteBuffer b){
        super(b);
    }

    @Override
    protected final Nid getElement(final ByteBuffer b) {
        return new Nid(b.getInt());
    }

    @Override
    public Nid getScalar(final int idx) {
        return this.getValue(idx);
    }

    @Override
    protected String getSuffix() {
        return "";
    }

    @Override
    protected final Nid[] initArray(final int size) {
        return new Nid[size];
    }

    @Override
    protected final void setElement(final ByteBuffer b, final Nid value) {
        b.putInt(value.getValue());
    }

    public final Nid[] toArray() {
        return this.getValue(0, this.getLength());
    }

    @Override
    public byte toByte(final Nid t) {
        return t.toByte();
    }

    @Override
    public double toDouble(final Nid t) {
        return t.toDouble();
    }

    @Override
    public float toFloat(final Nid t) {
        return t.toFloat();
    }

    @Override
    public int toInt(final Nid t) {
        return t.toInt();
    }

    @Override
    public long toLong(final Nid t) {
        return t.toLong();
    }

    @Override
    public short toShort(final Nid t) {
        return t.toShort();
    }

    @Override
    protected String TtoString(final Nid t) {
        return t.toString();
    }
}

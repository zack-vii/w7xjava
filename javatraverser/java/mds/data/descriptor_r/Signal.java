package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.Mds;
import mds.MdsException;
import mds.data.descriptor.ARRAY;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Missing;

public final class Signal extends BUILD{
    public Signal(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Signal(final Descriptor data, final Descriptor raw, final Descriptor dim, final Descriptor... dims){
        super(DTYPE.SIGNAL, null, dims, data, raw, dim);
    }

    @Override
    public final Descriptor getData() {
        Descriptor data;
        if((data = this.getDescriptor(0)) instanceof ARRAY) return data;
        if((data = this.getDescriptor(0).getData()) == Missing.NEW) try{
            return Mds.getActiveMds().getDescriptor("DATA($)", this);
        }catch(final MdsException e){}
        return data;
    }

    public final Descriptor getDimension() {
        return this.getDimension(0);
    }

    public final Descriptor getDimension(final int idx) {
        return this.getDescriptor(2 + idx);
    }

    public final Descriptor getRaw() {
        return this.getDescriptor(1);
    }

    @Override
    public final int[] getShape() {
        return this.getData().getShape();
    }

    @Override
    public final Descriptor getVALUE() {
        return this.getRaw();
    }
}

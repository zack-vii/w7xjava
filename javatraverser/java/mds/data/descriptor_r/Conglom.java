package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Conglom extends BUILD{
    public Conglom(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Conglom(final Descriptor image, final Descriptor model, final Descriptor name, final Descriptor qualifiers){
        super(DTYPE.CONGLOM, null, image, model, name, qualifiers);
    }

    public final Descriptor getImage() {
        return this.getDescriptor(0);
    }

    public final Descriptor getModel() {
        return this.getDescriptor(1);
    }

    public final Descriptor getName() {
        return this.getDescriptor(2);
    }

    public final Descriptor getQualifiers() {
        return this.getDescriptor(3);
    }
}

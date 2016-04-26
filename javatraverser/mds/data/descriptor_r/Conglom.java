package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;

public final class Conglom extends Descriptor_R{
    public Conglom(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Conglom(final Descriptor image, final Descriptor model, final Descriptor name, final Descriptor qualifiers){
        super(DTYPE.CONGLOM, (byte)4, null);
        this.dscptrs[0] = image;
        this.dscptrs[1] = model;
        this.dscptrs[2] = name;
        this.dscptrs[3] = qualifiers;
    }

    public final Descriptor getImage() {
        return this.dscptrs[0];
    }

    public final Descriptor getModel() {
        return this.dscptrs[1];
    }

    public final Descriptor getName() {
        return this.dscptrs[2];
    }

    public final Descriptor getQualifiers() {
        return this.dscptrs[3];
    }
}

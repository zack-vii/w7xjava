package mds.data.descriptor;

import java.nio.ByteBuffer;
import mds.MdsException;

/** XD (-64 : 192) **/
public class Descriptor_XD extends Descriptor_XS{
    public static final byte CLASS = -64;

    public Descriptor_XD(final ByteBuffer b) throws MdsException{
        super(b);
    }
}

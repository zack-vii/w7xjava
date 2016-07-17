package mds;

import java.util.ArrayList;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor.Descriptor_CA;
import mds.data.descriptor.Descriptor_R;
import mds.data.descriptor_a.Int8Array;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Missing;
import mds.mdsip.Connection;

public final class MdsShr{
    private final Connection connection;

    public MdsShr(final Connection connection){
        this.connection = connection;
    }

    public final Descriptor mdsCompress(final Descriptor input) throws MdsException {
        return this.mdsCompress(null, null, input);
    }

    public final Descriptor mdsCompress(final String entry, final Descriptor input) throws MdsException {
        return this.mdsCompress(null, entry, input);
    }

    public final Descriptor mdsCompress(final String image, final String entry, final Descriptor input) throws MdsException {
        final StringBuilder cmd = new StringBuilder(128).append("_a=*;");
        final String in;
        Descriptor_A argin;
        if(input instanceof Descriptor_A){
            in = "$";
            argin = (Descriptor_A)input;
        }else{
            cmd.append("_s=MdsShr->MdsSerializeDscIn(ref($),xd(_a));");
            in = "_a";
            argin = input.serializeDsc();
        }
        cmd.append("_s=MdsShr->MdsCompress(");
        final ArrayList<Descriptor> args = new ArrayList<Descriptor>(3);
        if(image == null) cmd.append("0,");
        else{
            cmd.append("ref($),");
            args.add(new CString(image));
        }
        if(entry == null) cmd.append("0,");
        else{
            cmd.append("ref($),");
            args.add(new CString(entry));
        }
        cmd.append("xd(").append(in).append("),xd(_a));_a");
        args.add(argin);
        final Descriptor result = this.connection.getDescriptor(cmd.toString(), args.toArray(new Descriptor[0]));
        return (result == null || result == Missing.NEW) ? input : result;
    }

    public final Descriptor mdsDecompress(final Descriptor_CA input) throws MdsException {
        return this.mdsDecompress(input.payload);
    }

    public final Descriptor mdsDecompress(final Descriptor_R input) throws MdsException {
        return this.connection.getDescriptor("_a=*;_s=MdsShr->MdsDecompress(xd($), xd(_a));_a", input);
    }

    public final int mdsEvent(final String event) throws MdsException {
        return this.connection.getInteger("_s=MdsShr->MDSEvent(ref($),val(0),val(0))", new CString(event));
    }

    public final String mdsGetMsgDsc(final int status) throws MdsException {
        return this.connection.getString(String.format("_a=repeat(' ',256);MdsShr->MdsGetMsgDsc(val(%d),descr(_a));trim(_a)", status));
    }

    public final Descriptor mdsSerializeDscIn(final Int8Array serial) throws MdsException {
        return this.connection.getDescriptor("_a=*;_s=MdsShr->MdsSerializeDscIn(ref($),xd(_a));_a", Descriptor.class, serial);
    }

    public final Int8Array mdsSerializeDscOut(final String expr, final Descriptor... args) throws MdsException {
        return (Int8Array)this.connection.getDescriptor(new StringBuffer(expr.length() + 64)//
                .append("_a=*;_s=MdsShr->MdsSerializeDscOut(xd((").append(expr).append(";)),xd(_a));_a").toString(), Int8Array.class, args);
    }
}

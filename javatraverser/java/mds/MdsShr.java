package mds;

import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.Int8Array;
import mds.mdsip.Connection;

public final class MdsShr{
    private final Connection connection;

    public MdsShr(final Connection connection){
        this.connection = connection;
    }

    public final int mdsEvent(final String event) throws MdsException {
        return this.connection.getInteger(String.format("_s=MdsShr->MDSEvent(ref('%s'),val(0),val(0))", event));
    }

    public final String mdsGetMsgDsc(final int status) throws MdsException {
        return this.connection.getString(String.format("_a=repeat(' ',256);MdsShr->MdsGetMsgDsc(val(%d),descr(_a));trim(_a)", status));
    }

    public final Descriptor mdsSerializeDscIn(final Int8Array serial) throws MdsException {
        return this.connection.mdsValue("_a=*;_s=MdsShr->MdsSerializeDscIn(ref($),xd(_a));_a", new Descriptor[]{serial}, Descriptor.class);
    }

    public final Int8Array mdsSerializeDscOut(final String expr) throws MdsException {
        return (Int8Array)this.connection.mdsValue(new StringBuffer(expr.length() + 64)//
        .append("_a=*;_s=MdsShr->MdsSerializeDscOut(xd((").append(expr).append(";)),xd(_a));_a").toString(), Int8Array.class);
    }
}

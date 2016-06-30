package mds;

import mds.mdsip.Connection;

public final class MdsShr{
    private final Connection connection;

    public MdsShr(final Connection connection){
        this.connection = connection;
    }

    public final int mdsEvent(final String event) throws MdsException {
        return this.connection.getInteger(String.format("MdsShr->MDSEvent(ref('%s'),val(0),val(0))", event));
    }

    public final String mdsGetMsgDsc(final int status) throws MdsException {
        return this.connection.getString(String.format("_a=repeat(' ',256);MdsShr->MdsGetMsgDsc(val(%d),descr(_a));trim(_a)", status));
    }
}

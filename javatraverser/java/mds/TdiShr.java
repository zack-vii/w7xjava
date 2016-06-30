package mds;

import mds.mdsip.Connection;

public final class TdiShr{
    private final Connection connection;

    public TdiShr(final Connection connection){
        this.connection = connection;
    }

    public final String tdiDecompile(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return "*";
        return this.connection.getString(String.format("_a=*;TdiShr->TdiDecompile(xd(EVALUATE((%s))),xd(_ans),val(-1));_a", expr));
    }
}

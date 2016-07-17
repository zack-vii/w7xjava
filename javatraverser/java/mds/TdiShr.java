package mds;

import java.util.ArrayList;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_S;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Int32;
import mds.mdsip.Connection;

public final class TdiShr{
    private final Connection connection;

    public TdiShr(final Connection connection){
        this.connection = connection;
    }

    public final int[] getShotDB(final CString expt, final Descriptor_S path, final Int32 lower, final Int32 upper) throws MdsException {
        final ArrayList<Descriptor> args = new ArrayList<Descriptor>(4);
        final StringBuilder expr = new StringBuilder(32).append("getShotDB($");
        args.add(expt);
        if(path != null){
            args.add(path);
            expr.append(",$");
        }else expr.append(",*");
        if(lower != null){
            args.add(lower);
            expr.append(",$");
        }else expr.append(",*");
        if(upper != null){
            args.add(upper);
            expr.append(",$");
        }else expr.append(",*");
        return this.connection.getIntegerArray(expr.append(')').toString(), args.toArray(new Descriptor[args.size()]));
    }

    public final String tdiDecompile(final Descriptor dsc) throws MdsException {
        return this.connection.getString("TdiShr->TdiDecompile(xd($),xd(_a),val(-1)", dsc);
    }

    public final String tdiDecompile(final String expr) throws MdsException {
        if(expr == null || expr.isEmpty()) return "*";
        return this.connection.getString(String.format("_a=*;TdiShr->TdiDecompile(xd(EVALUATE($)),xd(_a),val(-1));_a", new CString(expr)));
    }
}

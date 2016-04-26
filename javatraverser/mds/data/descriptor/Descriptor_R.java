package mds.data.descriptor;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor_r.Action;
import mds.data.descriptor_r.Call;
import mds.data.descriptor_r.Condition;
import mds.data.descriptor_r.Conglom;
import mds.data.descriptor_r.Dependenc;
import mds.data.descriptor_r.Dim;
import mds.data.descriptor_r.Dispatch;
import mds.data.descriptor_r.Function;
import mds.data.descriptor_r.Method;
import mds.data.descriptor_r.Opaque;
import mds.data.descriptor_r.Param;
import mds.data.descriptor_r.Procedure;
import mds.data.descriptor_r.Program;
import mds.data.descriptor_r.Range;
import mds.data.descriptor_r.Routine;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_r.Slope;
import mds.data.descriptor_r.Window;
import mds.data.descriptor_r.With_Error;
import mds.data.descriptor_r.With_Units;
import mds.mdsip.Message;

/*
 * Fixed-Length (static) Descriptor
 */
@SuppressWarnings("deprecation")
public class Descriptor_R<T extends Number>extends Descriptor<T>{
    public static final byte _dscoff = 12;
    public static final byte _ndesc  = 8;
    public static final int  BYTES   = Descriptor.BYTES + 24;
    public static final byte CLASS   = -62;                  // 194

    public static Descriptor_R deserialize(final ByteBuffer b) throws MdsException {
        switch(b.get(Descriptor._typB)){
            case DTYPE.ACTION:
                return new Action(b);
            case DTYPE.CALL:
                return new Call(b);
            case DTYPE.CONGLOM:
                return new Conglom(b);
            case DTYPE.DIMENSION:
                return new Dim(b);
            case DTYPE.DISPATCH:
                return new Dispatch(b);
            case DTYPE.FUNCTION:
                return new Function(b);
            case DTYPE.METHOD:
                return new Method(b);
            case DTYPE.OPAQUE:
                return new Opaque(b);
            case DTYPE.PARAM:
                return new Param(b);
            case DTYPE.RANGE:
                return new Range(b);
            case DTYPE.ROUTINE:
                return new Routine(b);
            case DTYPE.SIGNAL:
                return new Signal(b);
            case DTYPE.WINDOW:
                return new Window(b);
            case DTYPE.WITH_ERROR:
                return new With_Error(b);
            case DTYPE.WITH_UNITS:
                return new With_Units(b);
            case DTYPE.CONDITION:
                return new Condition(b);
            case DTYPE.DEPENDENC:
                return new Dependenc(b);
            case DTYPE.PROCEDURE:
                return new Procedure(b);
            case DTYPE.PROGRAM:
                return new Program(b);
            case DTYPE.SLOPE:
                return new Slope(b);
        }
        throw new MdsException(String.format("Unsupported dtype %s for class %s", Descriptor.getDTypeName(b.get(Descriptor._typB)), Descriptor.getDClassName(b.get(Descriptor._clsB))), 0);
    }
    public final Descriptor[] dscptrs;
    public final byte         ndesc;

    public Descriptor_R(final byte dtype, final byte ndesc, final byte[] data){
        super((short)(data == null ? 0 : data.length), dtype, Descriptor_R.CLASS, data);
        this.ndesc = ndesc;
        this.dscptrs = new Descriptor[this.ndesc];
    }

    public Descriptor_R(final ByteBuffer b) throws MdsException{
        super(b);
        this.ndesc = b.get();
        b.position(Descriptor_R._dscoff);
        this.dscptrs = new Descriptor[this.ndesc];
        final int[] pos = new int[this.ndesc];
        for(int i = 0; i < this.ndesc; i++)
            pos[i] = b.getInt();
        int end = b.limit();
        for(int i = this.ndesc; i-- > 0;){
            if(pos[i] == 0) continue;
            b.position(pos[i]).limit(end);
            this.dscptrs[i] = Descriptor.deserialize(b);
            end = pos[i];
        }
    }

    @Override
    public String decompile() {
        return this.decompile(", ", "", ", ", ")", true);
    }

    public final String decompile(final String s1, final String s2, final String s3, final String s4, final boolean deco) {
        final String[] args = new String[this.ndesc];
        for(int i = 0; i < this.ndesc; i++)
            args[i] = this.dscptrs[i] == null ? "*" : (deco ? this.dscptrs[i].decompile() : this.dscptrs[i].toString());
        final T val = this.getValue();
        final StringBuilder sb = new StringBuilder(256).append("Build_").append(this.getClass().getSimpleName()).append('(');
        if(val != null) sb.append(val.toString()).append(s1);
        return sb.append(s2).append(String.join(s3, args)).append(s4).toString();
    }

    @Override
    public String decompileX() {
        return this.decompile(",", "\n\t", ",\n\t", "\n)", true);
    }

    public final Descriptor getDscptrs(final int idx) {
        return this.dscptrs.length <= idx ? null : this.dscptrs[idx];
    }

    @Override
    public T getValue(final ByteBuffer b) {
        return null;
    }

    @Override
    public double[] toDouble() {
        return null;
    }

    @Override
    public float[] toFloat() {
        return null;
    }

    @Override
    public int[] toInt() {
        return null;
    }

    @Override
    public long[] toLong() {
        return null;
    }

    @Override
    public Message toMessage(final byte descr_idx, final byte n_args) {
        final String[] args = new String[this.ndesc];
        for(int i = 0; i < this.ndesc; i++)
            args[i] = "$";
        final T val = this.getValue();
        final StringBuilder sb = new StringBuilder(256).append("Build_").append(this.getClass().getSimpleName()).append('(');
        if(val != null) sb.append(val.toString()).append(',');
        final byte[] body = sb.append(String.join(",", args)).append(')').toString().getBytes();
        return new Message(descr_idx, this.dtype, n_args, null, body);
    }

    @Override
    public String toString() {
        return this.decompile(", ", "", ", ", ")", false);
    }

    @Override
    public String toStringX() {
        return this.decompile(",", "\n\t", ",\n\t", "\n)", false);
    }
}

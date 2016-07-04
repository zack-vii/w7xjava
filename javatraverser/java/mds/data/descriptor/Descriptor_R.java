package mds.data.descriptor;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor_r.Action;
import mds.data.descriptor_r.Call;
import mds.data.descriptor_r.Condition;
import mds.data.descriptor_r.Conglom;
import mds.data.descriptor_r.Dependency;
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
import mds.data.descriptor_s.Missing;

/** Fixed-Length (static) Descriptor (-62 : 194) **/
@SuppressWarnings("deprecation")
public abstract class Descriptor_R<T extends Number>extends Descriptor<T>{
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
            case DTYPE.DEPENDENCY:
                return new Dependency(b);
            case DTYPE.PROCEDURE:
                return new Procedure(b);
            case DTYPE.PROGRAM:
                return new Program(b);
            case DTYPE.SLOPE:
                return new Slope(b);
        }
        throw new MdsException(String.format("Unsupported dtype %s for class %s", Descriptor.getDTypeName(b.get(Descriptor._typB)), Descriptor.getDClassName(b.get(Descriptor._clsB))), 0);
    }
    private final Descriptor[] dscptrs;
    public final byte          ndesc;

    public Descriptor_R(final byte dtype, final ByteBuffer data, final Descriptor[] args){
        super((short)(data == null ? 0 : data.limit()), dtype, Descriptor_R.CLASS, data, Byte.BYTES + (args == null ? 0 : args.length) * Integer.BYTES);
        this.ndesc = (byte)(args == null ? 0 : args.length);
        this.dscptrs = new Descriptor[this.ndesc];
        if(args != null) System.arraycopy(args, 0, this.dscptrs, 0, args.length);
    }

    protected Descriptor_R(final byte dtype, final ByteBuffer data, final Descriptor[] args0, final Descriptor[] args1){
        super((short)(data == null ? 0 : data.limit()), dtype, Descriptor_R.CLASS, data, Byte.BYTES + (args0.length + (args1 == null ? 0 : args1.length)) * Integer.BYTES);
        this.ndesc = (byte)(args0.length + (args1 == null ? 0 : args1.length));
        this.dscptrs = new Descriptor[this.ndesc];
        System.arraycopy(args0, 0, this.dscptrs, 0, args0.length);
        if(args1 != null) System.arraycopy(args1, args0.length, this.dscptrs, 0, args1.length);
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
            if(pos[i] == 0) this.dscptrs[i] = Missing.NEW;
            else{
                b.position(pos[i]).limit(end);
                this.dscptrs[i] = Descriptor.deserialize(b);
                end = pos[i];
            }
        }
    }

    protected void addArguments(final int first, final String left, final String right, final StringBuilder pout, final int mode) {
        int j;
        final boolean indent = (mode & Descriptor.DECO_X) > 0;
        final String sep = indent ? ",\n\t" : ", ";
        final int last = this.ndesc - 1;
        if(left != null){
            pout.append(left);
            if(indent) pout.append("\n\t");
        }
        for(j = first; j <= last; j++){
            this.dscptrs[j].decompile(Descriptor_R.P_ARG, pout, (mode & ~Descriptor.DECO_X));
            if(j < last) pout.append(sep);
        }
        if(right != null){
            if(indent) pout.append("\n");
            pout.append(right);
        }
    }

    public final Descriptor getDescriptor(final int idx) {
        return this.dscptrs[idx] == null ? Missing.NEW : this.dscptrs[idx];
    }

    public final Descriptor getDscptrs(final int idx) {
        return this.dscptrs.length <= idx ? Missing.NEW : this.dscptrs[idx];
    }

    @Override
    public int[] getShape() {
        return new int[0];
    }

    @Override
    public T getValue(final ByteBuffer b) {
        return null;
    }

    @Override
    public byte[] toByteArray() {
        return this.getData().toByteArray();
    }

    @Override
    public double toDouble() {
        return (Double)this.getValue();
    }

    @Override
    public double[] toDoubleArray() {
        return this.getData().toDoubleArray();
    }

    @Override
    public float[] toFloatArray() {
        return this.getData().toFloatArray();
    }

    @Override
    public int[] toIntArray() {
        return this.getData().toIntArray();
    }

    @Override
    public long[] toLongArray() {
        return this.getData().toLongArray();
    }
}

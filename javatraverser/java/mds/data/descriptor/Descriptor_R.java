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
    public static final byte _ndescB   = 8;
    public static final byte _dscoffIa = 12;
    public static final int  BYTES     = Descriptor.BYTES + 24;
    public static final byte CLASS     = -62;                  // 194

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
        throw new MdsException(String.format("Unsupported dtype %s for class %s", DTYPE.getName(b.get(Descriptor._typB)), Descriptor.getDClassName(b.get(Descriptor._clsB))), 0);
    }

    private static final Descriptor[] joinArrays(final Descriptor[] args0, final Descriptor[] args1) {
        if(args0 == null || args0.length == 0) return args1;
        if(args1 == null || args1.length == 0) return args0;
        final Descriptor[] args = new Descriptor[args0.length + args1.length];
        System.arraycopy(args0, 0, args, 0, args0.length);
        System.arraycopy(args1, 0, args, args0.length, args1.length);
        return args;
    }

    private static final int joinSize(final Descriptor... args) {
        if(args == null) return 0;
        int size = 0;
        for(final Descriptor arg : args)
            if(arg != null) size += arg.getSize();
        return size;
    }
    private final int[]        desc_ptr;
    private final Descriptor[] dscptrs;
    protected final int        ndesc;

    @SuppressWarnings("unchecked")
    public Descriptor_R(final byte dtype, final ByteBuffer data, final Descriptor... args){
        super((short)(data == null ? 0 : data.limit()), dtype, Descriptor_R.CLASS, data, Descriptor_R._dscoffIa + (args == null ? 0 : args.length * Integer.BYTES), Descriptor_R.joinSize(args));
        this.b.put(Descriptor_R._ndescB, (byte)(this.ndesc = args == null ? 0 : args.length));
        this.b.position(Descriptor_R._dscoffIa);
        this.dscptrs = new Descriptor[this.ndesc];
        this.desc_ptr = new int[this.ndesc];
        if(args != null && args.length > 0){
            int offset = Descriptor_R._dscoffIa + (args == null ? 0 : args.length * Integer.BYTES) + (data == null ? 0 : data.limit());
            for(int i = 0; i < this.ndesc; i++){
                if(args[i] == null | args[i] == Missing.NEW) this.b.putInt(this.desc_ptr[i] = 0);
                else{
                    this.b.putInt(this.desc_ptr[i] = offset);
                    offset += args[i].getSize();
                }
            }
            for(int i = 0; i < this.ndesc; i++){
                if(this.desc_ptr[i] == 0) this.dscptrs[i] = Missing.NEW;
                else{
                    this.b.position(this.desc_ptr[i]);
                    this.b.put(args[i].serialize());
                    try{
                        this.dscptrs[i] = Descriptor.deserialize((ByteBuffer)this.serialize().position(this.desc_ptr[i]));
                    }catch(final MdsException e){
                        e.printStackTrace();
                        this.dscptrs[i] = Missing.NEW;
                    }
                    this.dscptrs[i].VALUE = this;
                }
            }
        }
        this.b.position(0);
    }

    protected Descriptor_R(final byte dtype, final ByteBuffer data, final Descriptor[] args1, final Descriptor... args0){
        this(dtype, data, Descriptor_R.joinArrays(args0, args1));
    }

    @SuppressWarnings("unchecked")
    public Descriptor_R(final ByteBuffer b){
        super(b);
        this.ndesc = b.get();
        b.position(Descriptor_R._dscoffIa);
        this.dscptrs = new Descriptor[this.ndesc];
        this.desc_ptr = new int[this.ndesc];
        for(int i = 0; i < this.ndesc; i++)
            this.desc_ptr[i] = b.getInt();
        int end = b.limit();
        for(int i = this.ndesc; i-- > 0;){
            if(this.desc_ptr[i] == 0) this.dscptrs[i] = Missing.NEW;
            else{
                b.position(this.desc_ptr[i]).limit(end);
                try{
                    this.dscptrs[i] = Descriptor.deserialize(b);
                    this.dscptrs[i].VALUE = this;
                }catch(final MdsException e){
                    e.printStackTrace();
                }
                end = this.desc_ptr[i];
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
            this.dscptrs[j].decompile(Descriptor.P_ARG, pout, (mode & ~Descriptor.DECO_X));
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

    @Override
    public short[] toShortArray() {
        return this.getData().toShortArray();
    }
}

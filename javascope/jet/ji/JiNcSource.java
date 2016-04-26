package jet.ji;

/* $Id$ */
import java.io.IOException;
import java.io.PrintStream;
import jScope.RandomAccessData;

public class JiNcSource implements JiDataSource{
    public static final boolean DEBUG       = false;
    public static final int     NcAttribute = 12;
    public static final int     NcDimension = 10;
    public static final int     NcVariable  = 11;

    public static void doPad(final RandomAccessData in) throws IOException {
        final long current = in.getFilePointer();
        // Everything on 4 byte boundary
        final int residue = (4 - (int)(current % 4)) % 4;
        if(JiNcSource.DEBUG){
            System.out.print("doPad: (current,residue) ");
            System.out.println(current + " " + residue);
        }
        in.skipBytes(residue);
    }

    private static JiVar findVariable(final String name, final JiNcVar[] varray) {
        for(final JiNcVar element : varray)
            if(name.equals(element.getName())) return element;
        return null;
    }

    private static String readString(final RandomAccessData in) throws IOException {
        final int nelems = in.readInt();
        if(nelems < 0) throw new IOException("Bad netCDF string");
        if(JiNcSource.DEBUG){
            System.out.println("readString: nelems " + nelems);
        }
        final byte b[] = new byte[nelems];
        in.readFully(b);
        JiNcSource.doPad(in);
        return new String(b);
    }

    private static void toStringAtt(final PrintStream out, final JiVar parent, final JiVar v) {
        if(parent == null) out.print("\t\t:");
        else out.print("\t\t" + parent.getName() + ":");
        out.print(v.getName() + " = ");
        JiNcSource.toStringData(out, v);
    }

    private static void toStringData(final PrintStream out, final JiVar v) {
        try{
            byte bytes[] = null;
            char chars[] = null;
            short shorts[] = null;
            int ints[] = null;
            float floats[] = null;
            double doubles[] = null;
            final Object result = v.read(v.getDims());
            final int maxPerLine = 8;
            if(result instanceof byte[]){
                bytes = (byte[])result;
                out.print(bytes[0]);
                for(int i = 1; i < bytes.length; ++i){
                    out.print(", " + bytes[i]);
                    if(i % maxPerLine == 0) out.println();
                }
                out.println(";");
            }else if(result instanceof char[]){
                chars = (char[])result;
                out.println("\"" + new String(chars) + "\";");
            }else if(result instanceof short[]){
                shorts = (short[])result;
                out.print(shorts[0] + "s");
                for(short i = 1; i < shorts.length; ++i){
                    out.print(", " + shorts[i] + "s");
                    if(i % maxPerLine == 0) out.println();
                }
                out.println(";");
            }else if(result instanceof int[]){
                ints = (int[])result;
                out.print(ints[0]);
                for(int i = 1; i < ints.length; ++i){
                    out.print(", " + ints[i]);
                    if(i % maxPerLine == 0) out.println();
                }
                out.println(";");
            }else if(result instanceof float[]){
                floats = (float[])result;
                out.print(floats[0]);
                for(int i = 1; i < floats.length; ++i){
                    out.print(", " + floats[i] + "f");
                    if(i % maxPerLine == 0) out.println();
                }
                out.println(";");
            }else if(result instanceof double[]){
                doubles = (double[])result;
                out.print(doubles[0]);
                for(int i = 1; i < doubles.length; ++i){
                    out.print(", " + doubles[i] + "d");
                    if(i % maxPerLine == 0) out.println();
                }
                out.println(";");
            }
        }catch(final IOException e){
            System.out.println(e);
        }
    }

    private static void toStringVar(final PrintStream out, final JiVar v) {
        final JiDim[] dims = v.getDims();
        out.print("\t" + v.getTypeString() + " " + v.getName() + "(");
        out.print(dims[0].getName());
        for(int j = 1; j < dims.length; ++j){
            out.print(", " + dims[j].getName());
        }
        out.println(") ;");
        final JiVar[] atts = v.getAtts();
        if(atts != null){
            for(final JiVar att : atts){
                JiNcSource.toStringAtt(out, v, att);
            }
        }
    }

    private static void toStringVarData(final PrintStream out, final JiVar v) {
        out.print(v.getName() + " = ");
        JiNcSource.toStringData(out, v);
    }
    private JiDim[]                mDimArray;
    private JiNcVar[]              mGattArray;
    // private int mNextVarID = 0;
    private final RandomAccessData mInput;
    private final String           mName;
    // netCDF record size is sum of record variable lengths
    private int                    mRecordSize = 0;
    private int                    mRecs       = 0;
    private JiNcVar[]              mVarArray;

    public JiNcSource(final String name, final RandomAccessData in) throws IOException{
        this.mName = name;
        this.mInput = in;
        this.readHeader(in);
    }

    public void dump(final PrintStream out) {
        out.println("netcdf " + this.mName + "{");
        out.println("variables:");
        final JiVar[] vars = this.getVars();
        if(vars != null){
            for(final JiVar var : vars)
                JiNcSource.toStringVar(out, var);
        }
        final JiVar[] gatts = this.getGlobalAtts();
        if(gatts != null){
            out.println("\n// global attributes:");
            for(final JiVar gatt : gatts)
                JiNcSource.toStringAtt(out, null, gatt);
        }
        if(vars != null){
            out.println("\ndata:");
            for(final JiVar var : vars)
                JiNcSource.toStringVarData(out, var);
        }
    }

    /**
     * Get a global attribute named 'name'
     *
     * @exception IOException
     * @return the global attribute named 'name'
     */
    @Override
    public JiVar getGlobalAtt(final String name) throws IOException {
        return JiNcSource.findVariable(name, this.mGattArray);
    }

    /**
     * Get all the global attributes for this source
     *
     * @return Vector containing global attributes
     */
    @Override
    public JiVar[] getGlobalAtts() {
        final JiVar[] rvar = new JiVar[this.mGattArray.length];
        for(int i = 0; i < this.mGattArray.length; ++i){
            rvar[i] = this.mGattArray[i];
        }
        return rvar;
    }

    public RandomAccessData getInput() {
        return this.mInput;
    }

    public int getNumRecords() {
        return this.mRecs;
    }

    public int getRecordSize() {
        return this.mRecordSize;
    }

    /**
     * Get a variable named 'name'
     *
     * @exception IOException
     * @return the variable named 'name'
     */
    @Override
    public JiVar getVar(final String name) throws IOException {
        return JiNcSource.findVariable(name, this.mVarArray);
    }

    /**
     * Get all the variables for this source
     *
     * @return Vector containing variables
     */
    @Override
    public JiVar[] getVars() {
        final JiVar[] rvar = new JiVar[this.mVarArray.length];
        for(int i = 0; i < this.mVarArray.length; ++i){
            rvar[i] = this.mVarArray[i];
        }
        return rvar;
    }

    private JiNcVar readAttribute(final RandomAccessData in) throws IOException {
        final String name = JiNcSource.readString(in);
        final int type = in.readInt();
        final int nelems = in.readInt();
        if(JiNcSource.DEBUG){
            System.out.print("readAttribute: (name,type,nelems) ");
            System.out.println(name + " " + type + " " + nelems);
        }
        final JiDim dims[] = new JiDim[1];
        dims[0] = new JiDim(null, 0, nelems);
        final JiNcVar nvar = new JiNcVar(this, name, dims, in.getFilePointer(), type, false);
        in.skipBytes(nvar.size());
        JiNcSource.doPad(in);
        return nvar;
    }

    private JiNcVar[] readAttributes(final RandomAccessData in) throws IOException {
        final int type = in.readInt();
        final int nelems = in.readInt();
        if(JiNcSource.DEBUG){
            System.out.println("readAttributes: (type,nelems) " + type + " " + nelems);
        }
        if(type == 0 && nelems == 0) return null;
        if(type != JiNcSource.NcAttribute) throw new IOException("Bad file -- expected netCDF global atts");
        final JiNcVar[] rval = new JiNcVar[nelems];
        for(int i = 0; i < nelems; i++){
            rval[i] = this.readAttribute(in);
        }
        return rval;
    }

    private void readDims(final RandomAccessData in) throws IOException {
        final int type = in.readInt();
        final int nelems = in.readInt();
        if(JiNcSource.DEBUG){
            System.out.println("readDims: type " + type + " nelems " + nelems);
        }
        if(type == 0 && nelems == 0) return;
        if(type != JiNcSource.NcDimension) throw new IOException("Bad file -- expected netCDF dimensions");
        this.mDimArray = new JiDim[nelems];
        for(int i = 0; i < nelems; i++){
            final String name = JiNcSource.readString(in);
            final int size = in.readInt();
            if(JiNcSource.DEBUG){
                System.out.println("readDims: (name, size) " + name + " " + size);
            }
            this.mDimArray[i] = new JiDim(name, 0, size);
        }
    }

    private void readGatts(final RandomAccessData in) throws IOException {
        final int type = in.readInt();
        final int nelems = in.readInt();
        if(JiNcSource.DEBUG){
            System.out.println("readGatts: (type,nelems) " + type + " " + nelems);
        }
        if(type == 0 && nelems == 0) return;
        if(type != JiNcSource.NcAttribute) throw new IOException("Bad file -- expected netCDF global atts");
        this.mGattArray = new JiNcVar[nelems];
        for(int i = 0; i < nelems; i++){
            this.mGattArray[i] = this.readAttribute(in);
        }
    }

    private void readHeader(final RandomAccessData in) throws IOException {
        final byte b[] = new byte[4];
        in.readFully(b);
        if(!(b[0] == 'C' && b[1] == 'D' && b[2] == 'F' && b[3] == 1)){ throw new IOException("Not a netCDF file"); }
        this.mRecs = in.readInt();
        this.readDims(in);
        this.readGatts(in);
        this.readVars(in);
        if(JiNcSource.DEBUG){
            System.out.println("readHeader: (recs, recsize): " + this.mRecs + " " + this.mRecordSize);
        }
    }

    private JiNcVar readVar(final RandomAccessData in) throws IOException {
        final String name = JiNcSource.readString(in);
        final int nelems = in.readInt();
        final JiDim[] dims = new JiDim[nelems];
        for(int i = 0; i < nelems; i++){
            final int dimid = in.readInt();
            dims[i] = (JiDim)this.mDimArray[dimid].clone();
        }
        boolean isRecord = false;
        if(dims[0].mCount == 0){
            isRecord = true;
            dims[0].mCount = this.getNumRecords();
        }
        final JiNcVar[] atts = this.readAttributes(in);
        final int type = in.readInt();
        final int size = in.readInt();
        final int offset = in.readInt();
        if(JiNcSource.DEBUG){
            System.out.print("readVar: (name,rank,type,size,offset,isRecord) ");
            System.out.println(name + " " + nelems + " " + type + " " + size + " " + offset + " " + isRecord);
        }
        final JiNcVar nvar = new JiNcVar(this, name, dims, offset, type, isRecord);
        nvar.addAtts(atts);
        if(isRecord){
            this.mRecordSize += size;
        }
        return nvar;
    }

    private void readVars(final RandomAccessData in) throws IOException {
        final int type = in.readInt();
        final int nelems = in.readInt();
        if(JiNcSource.DEBUG){
            System.out.println("readVars: (type,nelems) " + type + " " + nelems);
        }
        if(type == 0 && nelems == 0) return;
        if(type != JiNcSource.NcVariable) throw new IOException("Bad file -- expected netCDF global atts");
        this.mVarArray = new JiNcVar[nelems];
        for(int i = 0; i < nelems; i++){
            this.mVarArray[i] = this.readVar(in);
        }
    }
}

package jet.ji;

/* $Id$ */
import java.io.IOException;

public class JiNcVar extends JiVarImpl{
    public static final int NcByte    = 1;
    public static final int NcChar    = 2;
    public static final int NcDouble  = 6;
    public static final int NcFloat   = 5;
    public static final int NcLong    = 4;
    public static final int NcShort   = 3;
    private JiVar[]         mAtts     = null; // Attributes for this variable
    private final JiDim[]   mDims;           // Array of dimensions for variable
    private JiNcVarImp      mFactory  = null; // Factory for variable implementation
    private final boolean   mIsRecord;
    private JiSlabIterator  mIterator = null;
    protected int           mNcType;         // Two types -- our own type and netCDF type

    public JiNcVar(final JiNcSource s, final String name, final JiDim[] dims, final long offset, final int netCDFtype, final boolean isRecord) throws IOException{
        super(s, name);
        this.mIsRecord = isRecord;
        this.mDims = dims;
        this.mNcType = netCDFtype;
        switch(netCDFtype){
            case NcByte:
                this.mFactory = new JiNcVarByte(s.getInput(), this, offset);
                this.mType = JiVar.Byte;
                break;
            case NcChar:
                this.mFactory = new JiNcVarChar(s.getInput(), this, offset);
                this.mType = JiVar.Char;
                break;
            case NcShort:
                this.mFactory = new JiNcVarShort(s.getInput(), this, offset);
                this.mType = JiVar.Short;
                break;
            case NcLong: // netCDF long is same as Java int
                this.mFactory = new JiNcVarInt(s.getInput(), this, offset);
                this.mType = JiVar.Int;
                break;
            case NcFloat:
                this.mFactory = new JiNcVarFloat(s.getInput(), this, offset);
                this.mType = JiVar.Float;
                break;
            case NcDouble:
                this.mFactory = new JiNcVarDouble(s.getInput(), this, offset);
                this.mType = JiVar.Double;
                break;
            default:
                throw new IOException("Bad variable type");
        }
    }

    public void addAtts(final JiVar[] atts) {
        this.mAtts = atts;
    }

    /**
     * Get a attribute named 'name'
     *
     * @param name
     *            attribute name
     * @return the attribute named 'name'
     * @exception IOException
     * @exception java.io.IOException
     */
    @Override
    public JiVar getAtt(final String name) throws IOException {
        for(final JiVar mAtt : this.mAtts)
            if(name.equals(mAtt.getName())) return mAtt;
        return null;
    }

    /**
     * Get all the attributes for this variable
     *
     * @return Vector containing attributes
     */
    @Override
    public JiVar[] getAtts() {
        final JiVar[] rvar = new JiVar[this.mAtts.length];
        for(int i = 0; i < this.mAtts.length; ++i){
            rvar[i] = this.mAtts[i];
        }
        return rvar;
    }

    /**
     * Get a dimension named 'name'
     *
     * @param name
     *            string name
     * @return the dimension named 'name'
     * @exception IOException
     * @exception java.io.IOException
     */
    @Override
    public JiDim getDim(final String name) throws IOException {
        JiDim rval = null;
        for(final JiDim mDim : this.mDims){
            if(name.equals(mDim.mName)){
                rval = (JiDim)mDim.clone();
                break;
            }
        }
        return rval;
    }

    /**
     * Get all the dimensions for this source
     *
     * @return Vector containing dimensions
     */
    @Override
    public JiDim[] getDims() {
        final JiDim[] rdim = new JiDim[this.mDims.length];
        for(int i = 0; i < this.mDims.length; ++i){
            rdim[i] = (JiDim)this.mDims[i].clone();
        }
        return rdim;
    }

    public boolean isRecord() {
        return this.mIsRecord;
    }

    @Override
    public Object read(final JiDim[] dims) throws IOException {
        return this.mFactory.read(dims);
    }

    @Override
    public byte[] readByte(final JiDim[] dims) throws IOException {
        return this.mFactory.readByte(dims);
    }

    @Override
    public char[] readChar(final JiDim[] dims) throws IOException {
        return this.mFactory.readChar(dims);
    }

    @Override
    public double[] readDouble(final JiDim[] dims) throws IOException {
        return this.mFactory.readDouble(dims);
    }

    @Override
    public float[] readFloat(final JiDim[] dims) throws IOException {
        return this.mFactory.readFloat(dims);
    }

    @Override
    public int[] readInt(final JiDim[] dims) throws IOException {
        return this.mFactory.readInt(dims);
    }

    @Override
    public short[] readShort(final JiDim[] dims) throws IOException {
        return this.mFactory.readShort(dims);
    }

    public int size() {
        // mIterator init Must be deferred until all of header is read
        if(this.mIterator == null) this.mIterator = new JiSlabIterator((JiNcSource)this.mSource, this, this.mDims);
        return this.mIterator.size() * this.sizeof();
    }

    public int sizeof() {
        return this.mFactory.sizeof();
    }

    public void validateDims(final JiDim[] dims) throws IOException {
        if(dims.length != this.mDims.length) throw new IOException("Dimension lengths don't match");
        for(int i = 0; i < dims.length; ++i){
            final JiDim cdim = dims[i];
            final JiDim mdim = this.mDims[i];
            final int cend = cdim.mStart + cdim.mCount;
            final int mend = mdim.mStart + mdim.mCount;
            if(cdim.mStart < mdim.mStart || cdim.mStart >= mend || cend < mdim.mStart || cend > mend){
                final String message = "Dimensions " + cdim + " " + mdim + " don't match";
                throw new IOException(message);
            }
        }
    }
}

package jet.ji;

/* $Id$ */
import java.io.IOException;
import jScope.RandomAccessData;

class JiNcVarDouble extends JiNcVarImp{
    public JiNcVarDouble(final RandomAccessData in, final JiNcVar parent, final long offset){
        super(in, parent, offset);
    }

    @Override
    public Object read(final JiDim[] dims) throws IOException {
        return this.readDouble(dims);
    }

    @Override
    public double[] readDouble(final JiDim[] dims) throws IOException {
        double[] rval = null;
        this.mParent.validateDims(dims);
        final JiSlabIterator itr = new JiSlabIterator((JiNcSource)this.mParent.getSource(), this.mParent, dims);
        final int size = itr.size();
        rval = new double[size];
        JiSlab slab;
        int counter = 0;
        while((slab = itr.next()) != null){
            final byte[] bytes = new byte[slab.mSize * this.sizeof()];
            final double[] doubles = new double[slab.mSize];
            this.mRFile.seek(this.mOffset + slab.mOffset);
            this.mRFile.readFully(bytes);
            JiNcVarImp.convertDoubles(bytes, doubles);
            for(int i = 0; i < slab.mSize; ++i){
                rval[counter++] = doubles[i];
            }
        }
        return rval;
    }

    @Override
    public int sizeof() {
        return 8;
    }
}

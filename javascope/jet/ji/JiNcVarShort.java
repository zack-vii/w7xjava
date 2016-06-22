package jet.ji;

/* $Id$ */
import java.io.IOException;
import jscope.RandomAccessData;

class JiNcVarShort extends JiNcVarImp{
    public JiNcVarShort(final RandomAccessData in, final JiNcVar parent, final long offset){
        super(in, parent, offset);
    }

    @Override
    public Object read(final JiDim[] dims) throws IOException {
        return this.readShort(dims);
    }

    @Override
    public short[] readShort(final JiDim[] dims) throws IOException {
        short[] rval = null;
        this.mParent.validateDims(dims);
        final JiSlabIterator itr = new JiSlabIterator((JiNcSource)this.mParent.getSource(), this.mParent, dims);
        final int size = itr.size();
        rval = new short[size];
        JiSlab slab;
        int counter = 0;
        while((slab = itr.next()) != null){
            this.mRFile.seek(this.mOffset + slab.mOffset);
            for(int i = 0; i < slab.mSize; ++i){
                rval[counter++] = this.mRFile.readShort();
            }
        }
        return rval;
    }

    @Override
    public int sizeof() {
        return 2;
    }
}

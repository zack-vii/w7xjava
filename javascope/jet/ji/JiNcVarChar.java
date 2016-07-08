package jet.ji;

/* $Id$ */
import java.io.IOException;
import jscope.RandomAccessData;

class JiNcVarChar extends JiNcVarImp{
    public JiNcVarChar(final RandomAccessData in, final JiNcVar parent, final long offset){
        super(in, parent, offset);
    }

    @Override
    public Object read(final JiDim[] dims) throws IOException {
        return this.readChar(dims);
    }

    @Override
    public char[] readChar(final JiDim[] dims) throws IOException {
        char[] rval = null;
        this.mParent.validateDims(dims);
        final JiSlabIterator itr = new JiSlabIterator((JiNcSource)this.mParent.getSource(), this.mParent, dims);
        final int size = itr.size();
        rval = new char[size];
        JiSlab slab;
        int counter = 0;
        while((slab = itr.next()) != null){
            final byte[] bytes = new byte[slab.mSize * this.sizeof()];
            this.mRFile.seek(this.mOffset + slab.mOffset);
            this.mRFile.readFully(bytes);
            for(int i = 0; i < slab.mSize; ++i){
                rval[counter++] = (char)bytes[i];
            }
        }
        return rval;
    }

    @Override
    public int sizeof() {
        return 1;
    }
}

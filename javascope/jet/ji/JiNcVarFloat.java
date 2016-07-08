package jet.ji;

/* $Id$ */
import java.io.IOException;
import jscope.RandomAccessData;

class JiNcVarFloat extends JiNcVarImp{
    public JiNcVarFloat(final RandomAccessData in, final JiNcVar parent, final long offset){
        super(in, parent, offset);
    }

    @Override
    public Object read(final JiDim[] dims) throws IOException {
        return this.readFloat(dims);
    }

    @Override
    public float[] readFloat(final JiDim[] dims) throws IOException {
        float[] rval = null;
        this.mParent.validateDims(dims);
        final JiSlabIterator itr = new JiSlabIterator((JiNcSource)this.mParent.getSource(), this.mParent, dims);
        final int size = itr.size();
        rval = new float[size];
        JiSlab slab;
        int counter = 0;
        while((slab = itr.next()) != null){
            final byte[] bytes = new byte[slab.mSize * this.sizeof()];
            final float[] floats = new float[slab.mSize];
            this.mRFile.seek(this.mOffset + slab.mOffset);
            this.mRFile.readFully(bytes);
            JiNcVarImp.convertFloats(bytes, floats);
            for(int i = 0; i < slab.mSize; ++i){
                rval[counter++] = floats[i];
            }
        }
        return rval;
    }

    @Override
    public int sizeof() {
        return 4;
    }
}

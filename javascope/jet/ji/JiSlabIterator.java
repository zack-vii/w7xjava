package jet.ji;

/* $Id$ */
import java.io.IOException;

// Counter for hyper slab access
class JiSlabIterator{
    private static boolean DEBUG = false;

    private static int innerProduct(final int[] v1, final int[] v2) {
        int rval = 0;
        for(int i = 0; i < v1.length; i++)
            rval += v1[i] * v2[i];
        return rval;
    }
    private final int[] mCounter;         // Current slice
    private boolean     mFinished = false;
    private boolean     mIsRecord = false;
    private final int   mOffset;
    private final int[] mProducts, mSizes; // Product matrix, sizes of dimensions
    private int         mSize     = 1;

    public JiSlabIterator(final JiNcSource source, final JiNcVar var, final JiDim[] sliceDims){
        final JiDim[] varDims = var.getDims();
        this.mIsRecord = var.isRecord();
        this.mCounter = new int[sliceDims.length];
        this.mProducts = new int[sliceDims.length];
        this.mSizes = new int[sliceDims.length];
        final int[] dimOffsets = new int[sliceDims.length];
        int value = 1;
        int j = sliceDims.length - 1;
        for(int i = 0; i < sliceDims.length; i++, j--){
            this.mProducts[i] = value * var.sizeof();
            this.mCounter[i] = 0;
            this.mSizes[i] = sliceDims[j].mCount;
            dimOffsets[i] = sliceDims[j].mStart;
            value *= varDims[j].mCount;
        }
        if(this.mIsRecord){
            this.mProducts[sliceDims.length - 1] = source.getRecordSize();
        }
        for(int i = 0; i < sliceDims.length; i++)
            this.mSize *= this.mSizes[i];
        this.mOffset = JiSlabIterator.innerProduct(this.mProducts, dimOffsets);
    }

    private int getOffset() throws IOException {
        return JiSlabIterator.innerProduct(this.mProducts, this.mCounter) + this.mOffset;
    }

    public JiSlab next() throws IOException {
        JiSlab rval = null;
        if(!this.mFinished){
            if(this.mIsRecord && this.mSizes.length == 1) rval = new JiSlab(this.getOffset(), 1);
            else rval = new JiSlab(this.getOffset(), this.mSizes[0]);
            if(!this.mIsRecord && this.mCounter.length == 1){
                this.mFinished = true;
            }else{ // Advance counter and ripple carry if needed
                final int length = this.mSizes.length;
                int i;
                if(this.mIsRecord && length == 1) i = 0;
                else i = 1;
                for(; i < length; ++i){
                    if(JiSlabIterator.DEBUG){
                        System.out.print("mCounter = ");
                        for(int j = length - 1; j >= 0; --j){
                            System.out.print(this.mCounter[j]);
                        }
                        System.out.println();
                    }
                    ++this.mCounter[i];
                    this.mCounter[i] %= this.mSizes[i];
                    if(this.mCounter[i] != 0) break;
                }
                if(i == length) this.mFinished = true;
            }
        }
        return rval;
    }

    public int size() {
        return this.mSize;
    }
}

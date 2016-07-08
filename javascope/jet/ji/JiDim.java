package jet.ji;

/* $Id$ */
import java.io.IOException;

public class JiDim{
    public static JiVar getCoordVar() throws IOException {
        throw new IOException("JiDim::getCoordVar() : not supported");
    }
    public String mName;
    public int    mStart, mCount, mStride;

    public JiDim(final String name, final int start, final int count){
        this.mName = name;
        this.mStart = start;
        this.mCount = count;
        this.mStride = 1;
    }

    public JiDim(final String name, final int start, final int count, final int stride){
        this.mName = name;
        this.mStart = start;
        this.mCount = count;
        this.mStride = stride;
    }

    @Override
    protected Object clone() {
        return new JiDim(this.mName, this.mStart, this.mCount, this.mStride);
    }

    public JiDim copy() {
        return (JiDim)this.clone();
    }

    public String getName() {
        return this.mName;
    }

    @Override
    public String toString() {
        return "(" + this.mName + "," + this.mStart + "," + this.mCount + "," + this.mStride + ")";
    }
}

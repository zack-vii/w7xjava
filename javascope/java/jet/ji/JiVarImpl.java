package jet.ji;

/* $Id$ */
import java.io.IOException;

public abstract class JiVarImpl implements JiVar{
    protected String       mName;
    protected JiDataSource mSource;  // Source object for this variable
    protected int          mType = 0;

    public JiVarImpl(final JiDataSource s, final String name){
        this.mName = name;
        this.mSource = s;
    }

    /**
     * Get a attribute named 'name'
     *
     * @exception IOException
     * @return the attribute named 'name'
     */
    @Override
    public abstract JiVar getAtt(String name) throws IOException;

    /**
     * Get all the attributes for this source
     *
     * @return Vector containing attributes
     */
    @Override
    public abstract JiVar[] getAtts();

    /**
     * Get a dimension named 'name'
     *
     * @exception IOException
     * @return the dimension named 'name'
     */
    @Override
    public abstract JiDim getDim(String name) throws IOException;

    /**
     * Get all the dimensions for this source
     *
     * @return Vector containing dimensions
     */
    @Override
    public abstract JiDim[] getDims();

    @Override
    public String getName() {
        return this.mName;
    }

    @Override
    public JiDataSource getSource() {
        return this.mSource;
    }

    @Override
    public int getType() {
        return this.mType;
    }

    @Override
    public String getTypeString() {
        switch(this.mType){
            case Byte:
                return "byte";
            case Char:
                return "char";
            case Short:
                return "short";
            case Int:
                return "int";
            case Float:
                return "float";
            case Double:
                return "double";
            default:
                return "unknown";
        }
    }

    @Override
    public abstract Object read(JiDim[] dim) throws IOException;

    @Override
    public abstract byte[] readByte(JiDim[] dim) throws IOException;

    @Override
    public abstract char[] readChar(JiDim[] dim) throws IOException;

    @Override
    public abstract double[] readDouble(JiDim[] dim) throws IOException;

    @Override
    public abstract float[] readFloat(JiDim[] dim) throws IOException;

    @Override
    public abstract int[] readInt(JiDim[] dim) throws IOException;

    @Override
    public abstract short[] readShort(JiDim[] dim) throws IOException;
}

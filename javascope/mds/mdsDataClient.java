package mds;

/* $Id$ */
import java.util.Vector;

final public class mdsDataClient extends mdsConnection{
    /*
    public static void main(final String arg[]) {//TODO:main
        mdsDataClient mdc = null;
        try{
            mdc = new mdsDataClient("10.44.4.11");
            System.out.println("OK connection");
            mdc.open("W7X", 0);
            System.out.println(mdc.getString("PY([''])"));
            mdc.close();
            final double data[][] = mdc.getDoubleMatrix("[[1.,2.,3.],[4.,5.,6.],[7.,8.,9.],[10.,11.,12.]]");
            for(final double[] row : data){
                for(final double element : row)
                    System.out.print(element + "  ");
                System.out.println();
            }
            System.out.println(mdc.getString("WHOAMI()"));
            System.exit(1);
        }catch(final Exception exc){
            System.out.println("" + exc);
            if(mdc != null) mdc.close();
        }
    }
    */
    @SuppressWarnings("unused")
    private String experiment;
    @SuppressWarnings("unused")
    private int    shot;

    /**
     * This class allows to read the data from an mdsip data server.
     *
     * @param provider
     *            String to define the mdsip data server to use: host_address[:port]
     * @exception mdsIOException
     *                if an I/0 error occurs
     */
    public mdsDataClient(final String provider) throws mdsIOException{
        super(provider);
        if(!this.ConnectToMds(false)) throw new mdsIOException(this.error);
    }

    /**
     * This class allows to read the data from an mdsip data server.
     *
     * @param provider
     *            String to define the mdsip data server to use: host_address[:port]
     * @param user
     *            String to define the mdsip user name
     * @exception mdsIOException
     *                if an I/0 error occurs
     */
    public mdsDataClient(final String provider, final String user) throws mdsIOException{
        super(provider);
        this.setUser(user);
        if(!this.ConnectToMds(false)) throw new mdsIOException(this.error);
    }

    /**
     * Close currently open experiment
     */
    public void close() {
        if(this.connected) this.DisconnectFromMds();
    }

    public Object evaluate(final String expr, final Vector<Descriptor> args) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr, args);
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                return new Float(desc.float_data[0]);
            case Descriptor.DTYPE_LONG:
                return new Integer(desc.int_data[0]);
            case Descriptor.DTYPE_DOUBLE:
                return new Double(desc.double_data[0]);
            case Descriptor.DTYPE_ULONG:
                return new Long(desc.long_data[0]);
            case Descriptor.DTYPE_BYTE:
                return new Character((char)desc.byte_data[0]);
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
                return new String(desc.strdata);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return a byte array
     *
     * @param expr
     *            expression to evaluate
     * @return byte array value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public byte[] getByteArray(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        byte out[] = null;
        byte out_data[] = null;
        switch(desc.dtype){
            case Descriptor.DTYPE_BYTE:
            case Descriptor.DTYPE_UBYTE:
                out_data = new byte[desc.byte_data.length];
                for(int i = 0; i < desc.byte_data.length; i++)
                    out_data[i] = desc.byte_data[i];
                out = out_data;
                break;
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
                out = desc.strdata.getBytes();
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
        return out;
    }

    /**
     * Evaluate an mds expression which return a double value
     *
     * @param expr
     *            expression to evaluate
     * @return double value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public double getDouble(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                return desc.float_data[0];
            case Descriptor.DTYPE_LONG:
                return desc.int_data[0];
            case Descriptor.DTYPE_DOUBLE:
                return desc.double_data[0];
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Cannot convert a string to float");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return a double array
     *
     * @param expr
     *            expression to evaluate
     * @return double array value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public double[] getDoubleArray(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        double out[] = null;
        double out_data[] = null;
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                out_data = new double[desc.float_data.length];
                for(int i = 0; i < desc.float_data.length; i++)
                    out_data[i] = desc.float_data[i];
                out = out_data;
                break;
            case Descriptor.DTYPE_LONG:
                out_data = new double[desc.int_data.length];
                for(int i = 0; i < desc.int_data.length; i++)
                    out_data[i] = desc.int_data[i];
                out = out_data;
                break;
            case Descriptor.DTYPE_DOUBLE:
                out = desc.double_data;
                break;
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Cannot convert a string to float array");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
        return out;
    }

    /**
     * Evaluate an mds expression which return a bidimensional double array
     *
     * @param expr
     *            expression to evaluate
     * @return bidimensional double array returned by expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public double[][] getDoubleMatrix(final String expr) throws mdsIOException {
        Descriptor desc = this.mdsValue("shape(" + expr + ")");
        double out[][] = null;
        int row = 0, col = 0;
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                throw new mdsIOException("Evaluated expression not a matrix");
            case Descriptor.DTYPE_LONG:
                if(desc.int_data.length != 2) throw new mdsIOException("Can be read only bi-dimensional matrix");
                col = desc.int_data[0];
                row = desc.int_data[1];
                break;
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Evaluated expression not a matrix");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Evaluated expression not a matrix");
        }
        desc = this.mdsValue(expr);
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                out = new double[row][col];
                for(int i = 0, k = 0; i < row; i++)
                    for(int j = 0; j < col; j++)
                        out[i][j] = desc.float_data[k++];
                return out;
            case Descriptor.DTYPE_LONG:
                out = new double[row][col];
                for(int i = 0, k = 0; i < row; i++)
                    for(int j = 0; j < col; j++)
                        out[i][j] = desc.int_data[k++];
                return out;
            case Descriptor.DTYPE_DOUBLE:
                out = new double[row][col];
                for(int i = 0, k = 0; i < row; i++)
                    for(int j = 0; j < col; j++)
                        out[i][j] = desc.double_data[k++];
                return out;
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Cannot convert a string to float array");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return a float value
     *
     * @param expr
     *            expression to evaluate
     * @return float value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public float getFloat(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                return desc.float_data[0];
            case Descriptor.DTYPE_LONG:
                return desc.int_data[0];
            case Descriptor.DTYPE_DOUBLE:
                return (float)desc.double_data[0];
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Cannot convert a string to float");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return a float array
     *
     * @param expr
     *            expression to evaluate
     * @return float array value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public float[] getFloatArray(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        float out[] = null;
        float out_data[] = null;
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                out = desc.float_data;
                break;
            case Descriptor.DTYPE_LONG:
                out_data = new float[desc.int_data.length];
                for(int i = 0; i < desc.int_data.length; i++)
                    out_data[i] = desc.int_data[i];
                out = out_data;
                break;
            case Descriptor.DTYPE_DOUBLE:
                out_data = new float[desc.double_data.length];
                for(int i = 0; i < desc.double_data.length; i++)
                    out_data[i] = (float)desc.double_data[i];
                out = out_data;
                break;
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Cannot convert a string to float array");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
        return out;
    }

    /**
     * Evaluate an mds expression which return a bidimensional float array
     *
     * @param expr
     *            expression to evaluate
     * @return bidimensional float array returned by expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public float[][] getFloatMatrix(final String expr) throws mdsIOException {
        Descriptor desc = this.mdsValue("shape(" + expr + ")");
        float out[][] = null;
        int row = 0, col = 0;
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                throw new mdsIOException("Evaluated expression not a matrix");
            case Descriptor.DTYPE_LONG:
                if(desc.int_data.length != 2) throw new mdsIOException("Can be read only bi-dimensional matrix");
                col = desc.int_data[0];
                row = desc.int_data[1];
                break;
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Evaluated expression not a matrix");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Evaluated expression not a matrix");
        }
        desc = this.mdsValue(expr);
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                out = new float[row][col];
                for(int i = 0, k = 0; i < row; i++)
                    for(int j = 0; j < col; j++)
                        out[i][j] = desc.float_data[k++];
                return out;
            case Descriptor.DTYPE_LONG:
                out = new float[row][col];
                for(int i = 0, k = 0; i < row; i++)
                    for(int j = 0; j < col; j++)
                        out[i][j] = desc.int_data[k++];
                return out;
            case Descriptor.DTYPE_DOUBLE:
                out = new float[row][col];
                for(int i = 0, k = 0; i < row; i++)
                    for(int j = 0; j < col; j++)
                        out[i][j] = (float)desc.double_data[k++];
                return out;
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Cannot convert a string to float array");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return an integer value
     *
     * @param expr
     *            expression to evaluate
     * @return integer value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public int getInt(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        switch(desc.dtype){
            case Descriptor.DTYPE_UBYTE:
            case Descriptor.DTYPE_BYTE:
                return desc.byte_data[0];
            case Descriptor.DTYPE_FLOAT:
                return (int)desc.float_data[0];
            case Descriptor.DTYPE_SHORT:
            case Descriptor.DTYPE_USHORT:
                return desc.short_data[0];
            case Descriptor.DTYPE_LONG:
                return desc.int_data[0];
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return a int array
     *
     * @param expr
     *            expression to evaluate
     * @return integer array value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public int[] getIntArray(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        int out_data[] = null;
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                throw new mdsIOException("Cannot convert a float to int array");
            case Descriptor.DTYPE_LONG:
            case Descriptor.DTYPE_ULONG:
                return desc.int_data;
            case Descriptor.DTYPE_DOUBLE:
                throw new mdsIOException("Cannot convert a double to int array");
            case Descriptor.DTYPE_UBYTE:
            case Descriptor.DTYPE_BYTE:
                out_data = new int[desc.byte_data.length];
                for(int i = 0; i < desc.byte_data.length; i++)
                    out_data[i] = desc.byte_data[i];
                return out_data;
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return an long value
     *
     * @param expr
     *            expression to evaluate
     * @return long value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public long getLong(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                return (long)desc.float_data[0];
            case Descriptor.DTYPE_LONG:
                return desc.int_data[0];
            case Descriptor.DTYPE_ULONG:
                return desc.long_data[0];
            case Descriptor.DTYPE_BYTE:
                throw new mdsIOException("Cannot convert a string to float");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return an short value
     *
     * @param expr
     *            expression to evaluate
     * @return short value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public short getShort(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        switch(desc.dtype){
            case Descriptor.DTYPE_UBYTE:
            case Descriptor.DTYPE_BYTE:
                return desc.byte_data[0];
            case Descriptor.DTYPE_SHORT:
                return desc.short_data[0];
            case Descriptor.DTYPE_FLOAT:
                throw new mdsIOException("Cannot convert a float to short");
            case Descriptor.DTYPE_LONG:
                throw new mdsIOException("Cannot convert a int to short");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return a short array
     *
     * @param expr
     *            expression to evaluate
     * @return float array value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    @SuppressWarnings("fallthrough")
    public short[] getShortArray(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        short out_data[] = null;
        switch(desc.dtype){
            case Descriptor.DTYPE_FLOAT:
                throw new mdsIOException("Cannot convert a float to short array");
            case Descriptor.DTYPE_LONG:
            case Descriptor.DTYPE_ULONG:
                throw new mdsIOException("Cannot convert a long to short array");
            case Descriptor.DTYPE_DOUBLE:
                throw new mdsIOException("Cannot convert a double to short array");
            case Descriptor.DTYPE_UBYTE:
            case Descriptor.DTYPE_BYTE:
                out_data = new short[desc.byte_data.length];
                for(int i = 0; i < desc.byte_data.length; i++)
                    out_data[i] = desc.byte_data[i];
                return out_data;
            case Descriptor.DTYPE_SHORT:
            case Descriptor.DTYPE_USHORT:
                return desc.short_data;
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 0) throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Evaluate an mds expression which return a string value
     *
     * @param expr
     *            expression to evaluate
     * @return string value returned by the expression evaluation
     * @exception mdsIOException
     *                if an I/0 or an expression evaluation error occurs
     */
    public String getString(final String expr) throws mdsIOException {
        final Descriptor desc = this.mdsValue(expr);
        switch(desc.dtype){
            case Descriptor.DTYPE_UBYTE:
            case Descriptor.DTYPE_BYTE:
                return new String(desc.byte_data);
            case Descriptor.DTYPE_LONG:
                throw new mdsIOException("Cannot convert a integer to string");
            case Descriptor.DTYPE_FLOAT:
                throw new mdsIOException("Cannot convert a float to string");
            case Descriptor.DTYPE_CSTRING:
                if((desc.status & 1) == 1) return desc.strdata;
                throw new mdsIOException(desc.error);
            default:
                throw new mdsIOException("Data type code " + desc.dtype + " unsupported");
        }
    }

    /**
     * Open an mds experiment in read only access mode
     *
     * @param experiment
     *            Experiment name
     * @param shot
     *            Shot number
     * @exception mdsIOException
     *                if an I/0 error occurs
     */
    public void open(final String experiment, final int shot) throws mdsIOException {
        this.open(experiment, shot, 1);
    }

    /**
     * Open an mds experiment
     *
     * @param experiment
     *            Experiment name
     * @param shot
     *            Shot number
     * @param readOnly
     *            access mode 1 for read only
     * @exception mdsIOException
     *                if an I/0 error occurs
     */
    public void open(final String experiment, final int shot, final int readOnly) throws mdsIOException {
        if(!this.connected) throw new mdsIOException("mds data client not connected to " + this.provider);
        this.experiment = experiment;
        this.shot = shot;
        final Descriptor descr = this.mdsValue("JavaOpen(\"" + experiment + "\"," + shot + "," + readOnly + ")");
        if(!(descr.dtype != Descriptor.DTYPE_CSTRING && descr.dtype == Descriptor.DTYPE_LONG && descr.int_data != null && descr.int_data.length > 0 && (descr.int_data[0] % 2 == 1))){
            if(this.error != null) throw new mdsIOException("Cannot open experiment " + experiment + " shot " + shot + " : " + this.error);
            throw new mdsIOException("Cannot open experiment " + experiment + " shot " + shot);
        }
    }
}

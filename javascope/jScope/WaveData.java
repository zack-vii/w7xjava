package jScope;

/* $Id$ */
import java.io.IOException;

/**
 * When a signal is requested to a DataProvider, it is returned by means of an object implementing WaveData interface. WaveData defines all the data access method for the corresponding signal.
 *
 * @see DataProvider
 */
public interface WaveData{
    void addWaveDataListener(WaveDataListener listener);

    /**
     * Get Y data (for unidimensional signals) or Z data (for bidimensional signals) as a float array. If bidimensional signals are returned, values are ordered by rows.
     *
     * @return The signal Y or Z data coded as a float array.
     * @exception java.io.IOException
     */
    XYData getData(double xmin, double xmax, int numPoints) throws Exception;

    XYData getData(int numPoints) throws Exception;

    /**
     * Start data readout. Expected to return soon, being the data segment communicated later
     *
     * @param lowerBound
     *            : Lower bound of X region of interest
     * @param upperBound
     *            : Upper bound of X region of interest
     * @param numPoints
     *            : Estimated number of requested point
     */
    void getDataAsync(double lowerBound, double upperBound, int numPoints);

    /**
     * Returns the number of dimensions of the corresponding signal. Currently only unidimensional signals are supported by jScope.
     *
     * @return The number of dimension for the represented signal.
     * @exception java.io.IOException
     */
    public int getNumDimension() throws IOException;

    public String GetTitle() throws IOException;

    double[] getX2D();

    long[] getX2DLong();

    /**
     * Get the associated label for X axis. It is displayed if no X axis label is defined in the setup data definition.
     *
     * @return The X label string.
     * @exception java.io.IOException
     */
    public String GetXLabel() throws IOException;

    float[] getY2D();

    /**
     * Get the associated label for Y axis. It is displayed if no Y axis label is defined in the setup data definition.
     *
     * @return The Y label string.
     * @exception java.io.IOException
     */
    public String GetYLabel() throws IOException;

    float[] getZ();

    /**
     * Get the associated label for Z axis (for bi-dimensional signals only). It is displayed if no X axis label is defined in the setup data definition.
     *
     * @return The Z label string.
     * @exception java.io.IOException
     */
    public String GetZLabel() throws IOException;

    // double[] getXLimits();
    // long []getXLong();
    boolean isXLong();

    /**
     * Enables/Disables the option of periodic readout of new data
     *
     * @param continuousUpdate
     */
    void setContinuousUpdate(boolean continuousUpdate);
}

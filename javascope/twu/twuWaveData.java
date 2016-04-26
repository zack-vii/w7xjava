package twu;

// ---------------------------------------------------------------------------------------------
// twuWaveData
// An implementation of "WaveData" for signals from a TEC Web-Umbrella (TWU) server.
//
// $Id$
//
// ---------------------------------------------------------------------------------------------
import java.io.IOException;
import jScope.WaveData;
import jScope.WaveDataListener;
import jScope.XYData;

// ---------------------------------------------------------------------------------------------
final class twuWaveData implements WaveData{
    public static double[] GetXDoubleData() {
        return null;
    }

    public static double[] getXLimits() {
        System.out.println("BADABUM!!");
        return null;
    }

    public static long[] getXLong() {
        System.out.println("BADABUM!!");
        return null;
    }

    public static long[] GetXLongData() {
        return null;
    }
    private twuSingleSignal abscissa_X      = null;
    private String          abscissa_X_name = null;
    private twuSingleSignal mainSignal      = null;
    private String          mainSignal_name = null;
    private long            shotOfThisData  = 0;
    private String          title           = null;

    public twuWaveData(){}

    public twuWaveData(final twuDataProvider dp, final String in_y){
        this.init(dp, in_y, null);
    }

    public twuWaveData(final twuDataProvider dp, final String in_y, final String in_x){
        this.init(dp, in_y, in_x);
    }

    @Override
    public void addWaveDataListener(final WaveDataListener listener) {}

    // GAB JULY 2014 NEW WAVEDATA INTERFACE RAFFAZZONATA
    @Override
    public XYData getData(final double xmin, final double xmax, final int numPoints) throws Exception {
        final double x[] = twuWaveData.GetXDoubleData();
        final float y[] = this.GetFloatData();
        return new XYData(x, y, Double.POSITIVE_INFINITY);
    }

    @Override
    public XYData getData(final int numPoints) throws Exception {
        final double x[] = twuWaveData.GetXDoubleData();
        final float y[] = this.GetFloatData();
        return new XYData(x, y, Double.POSITIVE_INFINITY);
    }

    @Override
    public void getDataAsync(final double lowerBound, final double upperBound, final int numPoints) {}

    // JScope has an inconsistent way of dealing with data: GetFloatData() is used to
    // get the Y data, and *if* there's an abscissa (aka time data, aka X data) this
    // is retrieved using GetXData(). however, GetYData() is not used ?! MvdG
    // It is used! it represents the second abscissa, for a 2D signal! JGK
    public float[] GetFloatData() throws IOException {
        if(this.mainSignal == null || this.mainSignal.error()) return null;
        return this.mainSignal.getData();
    }

    @Override
    public int getNumDimension() throws IOException {
        return this.mainSignal.getTWUProperties(this.shotOfThisData).Dimensions();
    }

    @Override
    public String GetTitle() throws IOException {
        // now has a special treatment for scalars ...
        if(this.title != null) return this.title;
        final int dim = this.getNumDimension();
        if(dim != 0) this.title = this.mainSignal.getTWUProperties(this.shotOfThisData).Title();
        else{
            try{
                this.title = this.mainSignal.ScalarToTitle(this.shotOfThisData);
            }catch(final IOException e){
                throw e;
            }catch(final Exception e){
                twuSingleSignal.handleException(e);
                throw new IOException(e.toString());
            }
        }
        return this.title;
    }

    // another utility method. needed by TwuAccess (via via).
    // this is an efficient way to do it because it allows storage
    // of the properties within the twuSingleSignal, so it won't
    // need to be retrieved time after time ...
    //
    public TWUProperties getTWUProperties() throws IOException {
        return this.mainSignal.getTWUProperties(this.shotOfThisData);
    }

    @Override
    public double[] getX2D() {
        System.out.println("BADABUM!!");
        return null;
    }

    @Override
    public long[] getX2DLong() {
        System.out.println("BADABUM!!");
        return null;
    }

    public float[] GetXData() throws IOException {
        return this.abscissa_X.getData();
    }

    @Override
    public String GetXLabel() throws IOException {
        return this.abscissa_X.getTWUProperties(this.shotOfThisData).Units();
    }

    @Override
    public float[] getY2D() {
        System.out.println("BADABUM!!");
        return null;
    }

    public float[] GetYData() throws IOException {
        return this.mainSignal.getData(); // used to be : return null; ... :o
        // Wrong !! should return Abscissa.1 data!
        // TODO: To be fixed later! JGK.
    }

    @Override
    public String GetYLabel() throws IOException {
        return this.mainSignal.getTWUProperties(this.shotOfThisData).Units();
    }

    @Override
    public float[] getZ() {
        System.out.println("BADABUM!!");
        return null;
    }

    @Override
    public String GetZLabel() throws IOException {
        return null;
    }

    protected void init(final twuDataProvider dp, String in_y, String in_x) {
        in_y = (in_y != null && in_y.trim().length() != 0) ? in_y.trim() : null;
        in_x = (in_x != null && in_x.trim().length() != 0) ? in_x.trim() : null;
        this.shotOfThisData = dp.shot;
        this.mainSignal_name = in_y;
        this.abscissa_X_name = in_x;
        this.mainSignal = new twuSingleSignal(dp, this.mainSignal_name);
        if(this.abscissa_X_name != null) this.abscissa_X = new twuSingleSignal(dp, this.abscissa_X_name);
        else this.abscissa_X = new twuSingleSignal(dp, this.mainSignal);
    }

    @Override
    public boolean isXLong() {
        return false;
    }

    public boolean notEqualsInputSignal(String in_y, String in_x, final long requestedShot) {
        // this uses a simple (i.e. imperfect) comparison approach to see
        // if the WaveData for in_x, in_y has already been created ...
        if(this.shotOfThisData != requestedShot) return true;
        in_y = (in_y != null && in_y.trim().length() != 0) ? in_y.trim() : null;
        in_x = (in_x != null && in_x.trim().length() != 0) ? in_x.trim() : null;
        final boolean y_equals = (in_y == null) ? (this.mainSignal_name == null) : (this.mainSignal_name != null && in_y.equalsIgnoreCase(this.mainSignal_name));
        final boolean x_equals = (in_x == null) ? (this.abscissa_X_name == null) : (this.abscissa_X_name != null && in_x.equalsIgnoreCase(this.abscissa_X_name));
        return !(x_equals && y_equals);
    }

    @Override
    public void setContinuousUpdate(final boolean continuopusUpdate) {}

    // A little utility method for the subclasses ...
    // (most fetch options, particularly settings involved with zoom range,
    // should be the same for both x and y data.)
    //
    protected void setFetchOptions(final TWUFetchOptions opt) throws IOException {
        this.mainSignal.setFetchOptions(opt);
        this.abscissa_X.setFetchOptions(opt);
    }

    public void setFullFetch() {
        try{
            this.setFetchOptions(new TWUFetchOptions());
        }catch(final IOException e){}
        // same story as above, in setZoom.
    }

    public void setZoom(final float xmin, final float xmax, final int n_points) {
        // this method allows reusing of this object
        // to fetch data from the same signal at a
        // different zoomrange.
        try{
            this.setFetchOptions(this.abscissa_X.FindIndicesForXRange(this.shotOfThisData, xmin, xmax, n_points));
        }catch(final Exception e){}
        // the twuSingleSignal already has the error flag set (?),
        // and it will throw an exception when jscope tries to
        // call GetFloatData().
    }
}
// ---------------------------------------------------------------------------------------------
// End of: $Id$
// ---------------------------------------------------------------------------------------------

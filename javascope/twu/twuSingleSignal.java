package twu;

// -------------------------------------------------------------------------------------------------
// twuSingleSignal
// A support class of "TwuDataProvider".
//
// $Id$
//
// -------------------------------------------------------------------------------------------------
import java.io.IOException;
import jScope.ConnectionEvent;
import jScope.Waveform;

// -------------------------------------------------------------------------------------------------
final public class twuSingleSignal{
    static private void checkForError(final twuSingleSignal sig) throws Exception {
        if(sig != null && sig.error()) throw((Exception)sig.errorSource.fillInStackTrace());
    }

    // ---------------------------------------------------------------------------------------------
    static private int FindIndex(final float target, final twuSingleSignal xsig, final int start, final int step, final int maxpts, final int upperlimit) {
        // This is an iterative routine : it 'zooms in' on (a subsampled part of the)
        // abscissa data, until it finds the closest index. It looks between indices
        // start and start+(step*maxpts), subsamples at most maxpts at a time =>
        // next stepsize will be ceil (step/maxpts) ....
        //
        float[] data = xsig.doFetch(new TWUFetchOptions(start, step, maxpts + 1));
        final int newnum = data.length;
        final int ix = twuSingleSignal.findIndexInSubset(data, target);
        final int bestGuess = start + ix * step;
        if(step > 1){
            // Continue search with smaller step.
            int newstep = (int)Math.ceil(step / ((float)maxpts));
            if(newstep < 1) newstep = 1;
            data = null;
            return twuSingleSignal.FindIndex(target, xsig, bestGuess, newstep, maxpts, upperlimit);
        }
        if(ix >= newnum - 1) return bestGuess > upperlimit ? upperlimit : bestGuess;
        final boolean closer = (Math.abs(data[ix] - target) <= Math.abs(data[ix + 1] - target));
        return closer ? bestGuess : bestGuess + 1;
    }

    // ---------------------------------------------------------------------------------------------
    static private int findIndexInSubset(final float[] subsetData, final float target) {
        if(subsetData == null) return 0;
        final int len = subsetData.length;
        if(len < 2) return 0;
        final boolean up = subsetData[1] > subsetData[0];
        int ix = 0;
        if(up){
            while(ix < len && subsetData[ix] <= target)
                ix++;
        }else{
            while(ix < len && subsetData[ix] >= target)
                ix++;
        }
        if(ix != 0) ix--; // correct the overshoot from the 'break'.
        return ix;
    }

    // ---------------------------------------------------------------------------------------------
    protected static void handleException(final Exception e) {
        e.printStackTrace(System.err);
    }
    private float[]         data                = null;
    private Exception       errorSource         = null;
    private TWUFetchOptions fetchOptions        = null;
    private boolean         isAbscissa          = false;
    private twuSingleSignal mainSignal          = null;
    private TWUProperties   properties          = null;
    private twuDataProvider provider            = null;
    private long            shotOfTheProperties = 0;
    private String          source              = null;

    // A constructor that is useful for main signals.
    public twuSingleSignal(final twuDataProvider dp, final String src){
        this.provider = dp;
        this.source = src;
    }

    // A constructor that derives an abscissa signal from a main signal.
    public twuSingleSignal(final twuDataProvider dp, final twuSingleSignal prnt){
        this.provider = dp;
        this.mainSignal = prnt;
        this.isAbscissa = true;
    }

    // And a constructor that is mainly useful for testing purposes.
    public twuSingleSignal(final TWUProperties fakedSignal){
        this.provider = null;
        this.properties = fakedSignal;
        this.mainSignal = null;
        this.isAbscissa = true;
    }

    private void checkForError() throws Exception {
        twuSingleSignal.checkForError(this);
    }

    // -----------------------------------------------------------------------------
    private void createScalarData() {
        // an extra check to see if it really is a scalar
        if(this.properties != null && this.properties.LengthTotal() == 1){
            // return an (almost) empty array so there won't be
            // an error ; also, TwuWaveData.GetTitle() will
            // return an adapted title string containing the scalar value.
            if(this.properties.getProperty("Signal.Minimum") != null) this.data = new float[]{(float)this.properties.Minimum()};
            else this.data = new float[]{0.0f};
            // an empty array would cause an exception in Signal. But this works.
            return;
        }
        this.setErrorString("No numerical data available!");
        this.data = null; // 'triggers' display of the error_string.
    }

    // -----------------------------------------------------------------------------
    // Access to our DataProvider should only be necessary to access its
    // event connection methods.
    private void DispatchConnectionEvent(final ConnectionEvent e) {
        if(this.provider != null) this.provider.DispatchConnectionEvent(e);
    }

    private void doClip(final TWUFetchOptions opt) throws IOException {
        final int length = this.getTWUProperties(this.shotOfTheProperties).LengthTotal();
        opt.clip(length);
    }

    protected float[] doFetch(final TWUFetchOptions opt) {
        ConnectionEvent ce;
        ce = this.makeConnectionEvent("Start Loading " + (this.isAbscissa ? "X" : "Y"));
        this.DispatchConnectionEvent(ce);
        final TWUSignal bulk = new TWUSignal(this.properties, opt.getStart(), opt.getStep(), opt.getTotal());
        return this.SimplifiedGetFloats(bulk, opt.getTotal());
    }

    // -----------------------------------------------------------------------------
    public boolean error() {
        return this.errorSource != null;
    }

    private void fake_my_Properties() {
        final int len = this.mainSignal.getTWUProperties().LengthTotal();
        this.properties = new FakeTWUProperties(len);
        // creates an empty (but non-null!) Properties object,
        // which can used _almost_ just like the real thing.
    }

    private void fetch_my_Properties(final String propsurl, final String XorY) throws Exception {
        this.DispatchConnectionEvent(this.makeConnectionEvent("Load Properties", 0, 0));
        this.properties = new TWUProperties(propsurl);
        this.DispatchConnectionEvent(this.makeConnectionEvent(null, 0, 0));
        if(!this.properties.valid()){
            this.setErrorString("No Such " + XorY + " Signal : " + propsurl);
            this.throwError("Error loading properties of " + XorY + " data !" + propsurl);
        }
    }

    @SuppressWarnings("null") // is handled indirectly by throwError()
    private void fetch_X_Properties() throws Exception {
        twuSingleSignal.checkForError(this.mainSignal);
        final TWUProperties yprops = this.mainSignal != null ? this.mainSignal.getTWUProperties() : null;
        if(yprops == null) this.throwError("No yprops or mainSignal!");
        final int dim = yprops.Dimensions();
        if(dim > 1 || dim < 0) this.throwError("Not a 1-dimensional signal !");
        if(!yprops.hasAbscissa0()){
            this.fake_my_Properties();
            return;
        }
        final String mypropsurl = yprops.FQAbscissa0Name();
        this.fetch_my_Properties(mypropsurl, "X");
    }

    private void fetch_Y_Properties() throws Exception {
        if(this.source == null) this.throwError("No input signal set !");
        final String propsurl = twuNameServices.GetSignalPath(this.source, this.shotOfTheProperties);
        this.fetch_my_Properties(propsurl, "Y");
    }

    private void fetchBulkData() throws Exception {
        if(this.fetchOptions == null) this.throwError("unspecified fetch options (internal error)");
        if(this.fetchOptions.getTotal() == -1) this.doClip(this.fetchOptions); // just in case ...
        if(this.fetchOptions.getTotal() <= 1){
            this.createScalarData();
            return;
        }
        this.data = this.doFetch(this.fetchOptions);
    }

    // note that this setup only fetches the properties (and, later on, data)
    // if (and when) it is needed. it's less likely to do redundant work than
    // if I'd get the properties in the constructor.
    private void fetchProperties() throws Exception {
        try{
            // Don't remember errors and data from previous attempts
            this.errorSource = null;
            // error = false ;
            this.data = null;
            if(this.isAbscissa) this.fetch_X_Properties();
            else this.fetch_Y_Properties();
        }catch(final Exception e){
            this.errorSource = e;
            // error = true ;
        }
        this.checkForError();
    }

    // ---------------------------------------------------------------------------------------------
    public TWUFetchOptions FindIndicesForXRange(final long requestedShot, final float x_start, final float x_end, final int n_points) throws Exception {
        final TWUProperties prop = this.getTWUProperties(requestedShot);
        final int len = prop.LengthTotal();
        if(prop.Dimensions() == 0 || len <= 1) return new TWUFetchOptions(0, 1, 1); // mainly used to pick scalars out.
        // do an iterated search to find the indices,
        // by reading parts of the abscissa values.
        final int POINTS_PER_REQUEST = 100;
        final int step = (int)Math.ceil(len / (float)POINTS_PER_REQUEST);
        final int ix_start = twuSingleSignal.FindIndex(x_start, this, 0, step, POINTS_PER_REQUEST, len);
        final int ix_end = twuSingleSignal.FindIndex(x_end, this, 0, step, POINTS_PER_REQUEST, len);
        final int range = ix_end - ix_start;
        final int aproxStep = range / (n_points - 1);
        final int finalStep = aproxStep < 1 ? 1 : (range / (n_points - 1));
        final int finalPoints = 1 + (int)Math.floor((float)range / (float)finalStep);
        // I want the last point (ix_end) included.
        // you should end up getting *at least* n_point points.
        return new TWUFetchOptions(ix_start, finalStep, finalPoints);
    }

    public float[] getData() throws IOException {
        if(this.data != null) return this.data;
        try{
            this.fetchBulkData();
        }catch(final IOException e){
            throw e;
        }catch(final Exception e){
            twuSingleSignal.handleException(e);
            throw new IOException(e.toString());
        }
        return this.data;
    }

    // -----------------------------------------------------------------------------
    public float[] getData(final TWUFetchOptions opt) throws IOException {
        this.setFetchOptions(opt);
        return this.getData();
    }

    public Exception getError() {
        return this.errorSource;
    }

    // -----------------------------------------------------------------------------
    public TWUProperties getTWUProperties() {
        return this.properties;
    }

    public TWUProperties getTWUProperties(final long requestedShot) throws IOException {
        if(this.properties == null || this.shotOfTheProperties != requestedShot){
            try{
                this.shotOfTheProperties = requestedShot;
                this.fetchProperties();
                // NB, this throws an exception if an error occurs
                // OR HAS occurred before. And what good did that do?
            }catch(final IOException e){
                throw e;
            }catch(final Exception e){
                twuSingleSignal.handleException(e);
                throw new IOException(e.toString());
                // e.getMessage() might be nicer... but then you won't
                // know the original exception type at all, and
                // there's a possibility there won't be a message
                // in the exception. Then you'd have _nothing_ to go on.
            }
        }
        return this.properties;
    }

    private ConnectionEvent makeConnectionEvent(final String info) {
        return new ConnectionEvent((this.provider != null) ? this.provider : (Object)this, info);
    }

    private ConnectionEvent makeConnectionEvent(final String info, final int total_size, final int current_size) {
        return new ConnectionEvent((this.provider != null) ? this.provider : (Object)this, info, total_size, current_size);
    }

    public boolean propertiesReady() {
        return this.properties != null; // ditto.
    }

    public String ScalarToTitle(final long requestedShot) throws Exception {
        final TWUProperties props = this.getTWUProperties(requestedShot);
        // makes sure that the properties are really fetched.
        // although they should already have been if this method is called.
        final String name = props.Title();
        if(props.LengthTotal() > 1) return name + " (is not a scalar)";
        final String units = props.Units();
        float min = 0.0f;
        if(props.getProperty("Signal.Minimum") != null) min = (float)props.Minimum();
        else{
            final float[] scalar = this.doFetch(new TWUFetchOptions());
            min = scalar[0];
        }
        return name + " = " + min + " " + units;
    }

    private void setErrorString(final String errmsg) {
        if(this.provider != null) this.provider.setErrorstring(errmsg);
    }

    public void setFetchOptions(final TWUFetchOptions opt) throws IOException {
        this.doClip(opt);
        if(this.fetchOptions != null && this.fetchOptions.equalsForBulkData(opt)){ return; }
        this.fetchOptions = opt;
        this.data = null;
    }

    private float[] SimplifiedGetFloats(final TWUSignal bulk, final int n_point) {
        ConnectionEvent ce;
        ce = this.makeConnectionEvent((this.isAbscissa ? "Load X" : "Load Y"), 0, 0);
        this.DispatchConnectionEvent(ce);
        int inc = n_point / Waveform.MAX_POINTS;
        if(inc < 10) inc = 10;
        while(!bulk.complete() && !bulk.error()){
            bulk.tryToRead(inc);
            ce = this.makeConnectionEvent((this.isAbscissa ? "X:" : "Y:"), n_point, bulk.getActualSampleCount());
            this.DispatchConnectionEvent(ce);
            Thread.yield();
            // give the graphics thread a chance to update the user interface (the status bar) ...
        }
        if(bulk.error()) this.setErrorString("Error reading Bulk Data");
        this.DispatchConnectionEvent(this.makeConnectionEvent(null, 0, 0));
        return bulk.error() ? null : bulk.getBulkData();
    }

    // -----------------------------------------------------------------------------
    private final void throwError(final String msg) throws Exception {
        this.errorSource = new Exception(msg);
        throw this.errorSource;
    }
}
// -------------------------------------------------------------------------------------------------
// End of: $Id$
// -------------------------------------------------------------------------------------------------

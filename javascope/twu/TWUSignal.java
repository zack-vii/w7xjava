package twu;

// Header removed: contained some dirty character which made IBM JVM fail
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class TWUSignal{
    /* -------------------------------------------------------------------- */
    public static String revision() {
        return "$Id$";
    }
    private URL            bulkURL      = null;
    private boolean        error        = false;
    private boolean        finished     = false;
    private BufferedReader instream     = null;
    private int            sampleCount  = 0;
    private int            samples2Read = 0;
    private TWUProperties  twup         = null;
    private float          ydata[]      = null;

    /* -------------------------------------------------------------------- */
    // Constructors (and related functions)
    // This (constructor) function creates an array of values representing a
    // signal found on the Web-Ubrella. This array contains at most
    // "maxSamples" samples, from the whole signal, subsampled by a simple
    // skipping algorithm.
    public TWUSignal(final TWUProperties SigURL){
        this(SigURL, 0, 0, 0);
    }

    public TWUSignal(final TWUProperties SigURL, final int firstSample, final int step, int maxSamples){
        boolean success = false;
        this.twup = SigURL;
        if(maxSamples <= 0) maxSamples = this.twup.LengthTotal();
        this.samples2Read = maxSamples;
        this.ydata = new float[this.samples2Read];
        this.finished = false;
        this.sampleCount = 0;
        if(!SigURL.valid()){
            this.finished = true;
            return;
        }
        if(this.twup.Equidistant()) success = this.tryToConstruct(firstSample, step, maxSamples);
        if(!success) this.prepareToRead(firstSample, step, maxSamples);
    }

    /* -------------------------------------------------------------------- */
    // Methods for stepwise completion of the read-data action.
    public boolean complete() {
        return this.finished;
    }

    public boolean error() {
        return this.error;
    }

    /* -------------------------------------------------------------------- */
    // Accessors
    public int getActualSampleCount() {
        return this.sampleCount;
    }

    public float[] getBulkData() {
        /*
         * Several users of this class do not use the getActualSampleCount() method, but rely on getBulkData().length to see how many samples are available. Although this seems reasonable, from the caller's point of view, it is wrong in a number of
         * borderline situations. This could be resolved by using a vector, but that would be slower and the clientcodes depent on arrays. Since the error conditions do not occur, very often, it seems better to resolve it by creating a new array in those
         * few cases when the getActualSampleCount() is less then the array size.
         */
        if(this.sampleCount < this.ydata.length){
            final float[] newydata = new float[this.sampleCount];
            if(this.sampleCount > 0) System.arraycopy(this.ydata, 0, newydata, 0, this.sampleCount);
            this.ydata = null; // Attempt to trigger garbage collection.
            this.ydata = newydata;
        }
        return this.ydata;
    }

    private void prepareToRead(final int firstSample, final int step, final int maxSamples) {
        try{
            this.error = false;
            final StringBuffer bulk = new StringBuffer(this.twup.FQBulkName() + "?start=" + firstSample);
            if(step > 0) bulk.append("&step=" + step);
            if(maxSamples > 0) bulk.append("&total=" + maxSamples);
            this.bulkURL = new URL(bulk.toString());
            final URLConnection con = this.bulkURL.openConnection();
            con.setRequestProperty("User-Agent", "TWUSignal.java for jScope ($Revision$)");
            // It seems to be more efficient, for the type of data we have in the
            // bulk files, to close the connection after the server has send all
            // the data. In that way HTTP/1.1 servers will not "chunk" the data.
            // This chunking doubled the amounts to transfer and the de-chunking
            // on the client side took significant effort.
            con.setRequestProperty("Connection", "close");
            con.connect();
            this.instream = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }catch(final Exception e){
            System.out.println("TWUSignal.prepareToRead :" + e);
            this.error = true;
        }
    }

    private boolean tryToConstruct(int firstSample, int step, int maxSamples) {
        final double min = this.twup.Minimum();
        final double max = this.twup.Maximum();
        final double last;
        final double first;
        /* See sanitizeAQ() in the WUServer family */
        if(firstSample < 0) firstSample = 0;
        if(step < 1) step = 1;
        if(maxSamples < 0) maxSamples = 0;
        if(this.twup.Incrementing()){
            first = min;
            last = max;
        }else if(this.twup.Decrementing()){
            first = max;
            last = min;
        }else return false;
        final long lentotal = this.twup.LengthTotal();
        final long stillToGo = lentotal - firstSample;
        final long stepsToGo = stillToGo < 1 ? 0 : 1 + (stillToGo - 1) / step;
        final long toReturn = stepsToGo < maxSamples ? stepsToGo : maxSamples;
        final double span = last - first;
        final long segments = lentotal - 1;
        final double delta = segments == 0 ? 0 : span / segments;
        final double stepXdelta = step * delta;
        final double firstValue = firstSample * delta + first;
        int ix = 0;
        while(ix < toReturn) // or: (ix < maxSamples ) ???
        {
            this.ydata[ix] = (float)(ix * stepXdelta + firstValue);
            /*
             * The following limiting tests, and looping until (ix<maxSamples) were required, in some early versions of jScope; probably as an artefact of the problem discussed below, at getBulkData().
             */
            if(this.ydata[ix] > max) this.ydata[ix] = (float)max;
            else if(this.ydata[ix] < min) this.ydata[ix] = (float)min;
            ++ix;
        }
        this.sampleCount = ix;
        this.finished = true;
        return true;
    }

    public void tryToRead(final int samples2Try) {
        int thisAttempt = 0;
        try{
            String s = null;
            while((samples2Try > thisAttempt++) && (this.samples2Read > this.sampleCount) && ((s = this.instream.readLine()) != null)){
                final Float F = Float.valueOf(s);
                this.ydata[this.sampleCount++] = F.floatValue();
            }
            this.finished = (this.sampleCount >= this.samples2Read || s == null);
            if(this.finished){
                // boolean premature_eof = (s==null);
                // We should handle this, if it is a real problem.
                try{
                    this.instream.close();
                }catch(final Exception e){}
                if(this.sampleCount < this.samples2Read){
                    // Fill-up required
                    if(this.sampleCount == 0) this.ydata[this.sampleCount++] = 0.0F;
                    while(this.sampleCount < this.samples2Read){
                        this.ydata[this.sampleCount] = this.ydata[this.sampleCount - 1];
                        this.sampleCount++;
                    }
                }
            }
        }catch(final Exception e){
            System.out.println("TWUSignal.tryToRead :" + e);
            this.error = true;
        }
    }

    public String urlstring() {
        return this.bulkURL.toString();
    }
}
/* ------------------------------------------------------------------------ */
// $Id$
/* ------------------------------------------------------------------------ */

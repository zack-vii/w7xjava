package twu;

// -------------------------------------------------------------------------------------------------
// twuDataProvider
// An implementation of "DataProvider" for signals from a TEC Web-Umbrella (TWU) server.
//
// The first versions of this class (cvs revisions 1.x) were designed and written
// by Gabriele Manduchi and with some minor hacks by Jon Krom.
// Marco van de Giessen <A.P.M.vandeGiessen@phys.uu.nl> did some major surgery on
// this class (starting revision line 2.x) mainly in order to ensure that zooming
// works in more situations. (See also the cvs log.)
//
// Most of this revision 2.x work has now, from 2.21 onwards, been moved into separate classes.
// No new major revision number was started; has little use in CVS.
//
// $Id$
//
// -------------------------------------------------------------------------------------------------
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFrame;
import jScope.ConnectionEvent;
import jScope.ConnectionListener;
import jScope.DataAccessURL;
import jScope.DataProvider;
import jScope.DataServerItem;
import jScope.FrameData;
import jScope.UpdateEventListener;
import jScope.WaveData;

public final class twuDataProvider implements DataProvider{
    public static boolean DataPending() {
        return false;
    }

    public static String revision() {
        return "$Id$";
    }

    // DataProvider implementation
    public static boolean SupportsCompression() {
        return false;
    }

    public static boolean SupportsContinuous() {
        return false;
    }

    public static boolean SupportsFastNetwork() {
        return true;
    }
    private transient Vector<ConnectionListener> connection_listener = new Vector<ConnectionListener>();
    private String                               error_string;
    private String                               experiment;
    private twuWaveData                          lastWaveData        = null;
    protected long                               shot;

    public twuDataProvider(){
        super();
    }

    public twuDataProvider(final String user_agent){
        DataAccessURL.addProtocol(new twuAccess());
        // Could be used in the constructor for TWUProperties and in similar get URL actions.
        // A site could used this as a possible (internal) software distribution management
        // tool. In the log of a web-server you can, by checking the user_agent, see which
        // machines are still running old software.
    }

    @Override
    public void abort() {
        // TODO Auto-generated method stub
    }

    // -------------------------------------------
    // connection handling methods ....
    // -------------------------------------------
    @Override
    public synchronized void AddConnectionListener(final ConnectionListener l) {
        if(l == null) return;
        this.connection_listener.addElement(l);
    }

    @Override
    public void AddUpdateEventListener(final UpdateEventListener l, final String event) {}

    @Override
    public final boolean checkProvider() {
        return true;
    }

    protected void DispatchConnectionEvent(final ConnectionEvent e) {
        if(this.connection_listener != null){
            for(int i = 0; i < this.connection_listener.size(); i++)
                this.connection_listener.elementAt(i).processConnectionEvent(e);
        }
    }

    @Override
    public void Dispose() {}

    // --------------------------------------------------------------------------------------------
    // interface methods for getting *Data objects
    // ---------------------------------------------------
    public void enableAsyncUpdate(final boolean enable) {}

    @Override
    public String ErrorString() {
        return this.error_string;
    }

    public synchronized twuWaveData FindWaveData(final String in_y, final String in_x) {
        if(this.lastWaveData == null || this.lastWaveData.notEqualsInputSignal(in_y, in_x, this.shot)){
            this.lastWaveData = new twuWaveData(this, in_y, in_x);
            try{
                // Ensure that the properties are fetched right away.
                this.lastWaveData.getTWUProperties();
            }catch(final IOException e){
                this.setErrorstring("No Such Signal : " + twuNameServices.GetSignalPath(in_y, this.shot));
                // throw new IOException ("No Such Signal");
            }
        }
        return this.lastWaveData;
    }

    @Override
    public Class getDefaultBrowser() {
        return textorBrowseSignals.class;
    }

    protected synchronized String getExperiment() {
        return this.experiment;
    }

    @Override
    public float GetFloat(final String in) {
        return Float.parseFloat(in);
    }

    // ---------------------------------------------------------------------------------------------
    public synchronized float[] GetFloatArray(final String in) {
        this.resetErrorstring(null);
        final twuWaveData wd = (twuWaveData)this.GetWaveData(in);
        float[] data = null;
        try{
            // data = wd.GetFloatData() ;
            data = wd.getData(4000).getY();
        }catch(final Exception e){
            this.resetErrorstring(e.toString());
            data = null;
        }
        return data;
    }

    // ----------------------------------------------------
    // Methods for TwuAccess.
    // ----------------------------------------------------
    public synchronized float[] GetFloatArray(final String in, final boolean is_time) throws IOException {
        final twuWaveData wd = (twuWaveData)this.GetWaveData(in); // TwuAccess wants to get the full signal data .
        return is_time ? wd.GetXData() : wd.GetYData();
    }

    @Override
    public FrameData GetFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        return(new twuSimpleFrameData(this, in_y, in_x, time_min, time_max));
    }

    @Override
    public final String GetLegendString(final String s) {
        return s;
    }

    public synchronized WaveData GetResampledWaveData(final String in, final double start, final double end, final int n_points) {
        return this.GetResampledWaveData(in, null, start, end, n_points);
    }

    public synchronized WaveData GetResampledWaveData(final String in_y, final String in_x, final double start, final double end, final int n_points) {
        final twuWaveData find = this.FindWaveData(in_y, in_x);
        find.setZoom((float)start, (float)end, n_points);
        return find;
    }

    // -------------------------------------------------------
    // parsing of / extraction from input signal string
    // -------------------------------------------------------
    @Override
    public long[] GetShots(final String in) {
        this.resetErrorstring(null);
        long[] result;
        String curr_in = in.trim();
        if(curr_in.startsWith("[", 0)){
            if(curr_in.endsWith("]")){
                curr_in = curr_in.substring(1, curr_in.length() - 1);
                final StringTokenizer st = new StringTokenizer(curr_in, ",", false);
                result = new long[st.countTokens()];
                int i = 0;
                try{
                    while(st.hasMoreTokens())
                        result[i++] = Integer.parseInt(st.nextToken());
                    return result;
                }catch(final Exception e){}
            }
        }else{
            if(curr_in.indexOf(":") != -1){
                final StringTokenizer st = new StringTokenizer(curr_in, ":");
                int start, end;
                if(st.countTokens() == 2){
                    try{
                        start = Integer.parseInt(st.nextToken());
                        end = Integer.parseInt(st.nextToken());
                        if(end < start) end = start;
                        result = new long[end - start + 1];
                        for(int i = 0; i < end - start + 1; i++)
                            result[i] = start + i;
                        return result;
                    }catch(final Exception e){}
                }
            }else{
                result = new long[1];
                try{
                    result[0] = Long.parseLong(curr_in);
                    return result;
                }catch(final Exception e){}
            }
        }
        this.resetErrorstring("Error parsing shot number(s)");
        return null;
    }

    public synchronized String GetSignalProperty(final String prop, final String in) throws IOException {
        final twuWaveData wd = (twuWaveData)this.GetWaveData(in);
        return wd.getTWUProperties().getProperty(prop);
    }

    @Override
    public String GetString(final String in) {
        return in;
    }

    // ---------------------------------------------------
    @Override
    public synchronized WaveData GetWaveData(final String in) {
        return this.GetWaveData(in, null);
    }

    @Override
    public synchronized WaveData GetWaveData(final String in_y, final String in_x) {
        final twuWaveData find = this.FindWaveData(in_y, in_x);
        find.setFullFetch();
        return find;
    }

    @Override
    public int InquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    @Override
    public void join() {
        // TODO Auto-generated method stub
    }

    @Override
    public synchronized void RemoveConnectionListener(final ConnectionListener l) {
        if(l == null) return;
        this.connection_listener.removeElement(l);
    }

    @Override
    public void RemoveUpdateEventListener(final UpdateEventListener l, final String event) {}

    protected synchronized void resetErrorstring(final String newErrStr) {
        this.error_string = newErrStr;
    }

    @Override
    public void SetArgument(final String arg) {}

    public void SetCompression(final boolean state) {}

    public void setContinuousUpdate() {}

    @Override
    public void SetEnvironment(final String s) {}

    protected synchronized void setErrorstring(final String newErrStr) {
        if(this.error_string == null) this.error_string = newErrStr;
    }

    @Override
    public boolean SupportsTunneling() {
        return false;
    }

    // -------------------------------------------
    // Constructor, other small stuff ...
    // -------------------------------------------
    @Override
    public synchronized void Update(final String experiment, final long shot) {
        this.experiment = experiment;
        this.shot = shot;
        this.resetErrorstring(null);
        this.lastWaveData = null;
    }
}
// -------------------------------------------------------------------------------------------------
// End of: $Id$
// -------------------------------------------------------------------------------------------------

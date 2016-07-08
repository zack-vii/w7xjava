package misc;

/* $Id$ */
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import jscope.DataProvider;
import jscope.DataServerItem;
import jscope.WaveData;
import jscope.WaveDataListener;
import jscope.XYData;
import jscope.Array.RealArray;
import mds.MdsDataProvider;

public final class AsdexDataProvider extends MdsDataProvider{
    class SimpleWaveData implements WaveData{
        boolean   _jscope_set = false;
        RealArray currXData   = null;
        String    in_x, in_y;
        int       n_points;
        boolean   resample    = false;
        int       v_idx       = 0;
        float     xmax, xmin;

        public SimpleWaveData(final String in_y){
            this.in_y = in_y;
        }

        public SimpleWaveData(final String in_y, final float xmin, final float xmax, final int n_points){
            this.resample = true;
            this.in_y = in_y;
            this.xmin = xmin;
            this.xmax = xmax;
            this.n_points = n_points;
        }

        public SimpleWaveData(final String in_y, final String in_x){
            this.in_y = in_y;
            this.in_x = in_x;
        }

        public SimpleWaveData(final String in_y, final String in_x, final float xmin, final float xmax, final int n_points){
            this.resample = true;
            this.in_y = in_y;
            this.in_x = in_x;
            this.xmin = xmin;
            this.xmax = xmax;
            this.n_points = n_points;
        }

        @Override
        public void addWaveDataListener(final WaveDataListener listener) {}

        private double[] encodeTimeBase(final String expr) {
            try{
                // double t0 = getFloat("dscptr(window_of(dim_of(" + expr + ")),2)");
                final int startIdx[] = AsdexDataProvider.this.getIntArray("begin_of(window_of(dim_of(" + expr + ")))");
                final int endIdx[] = AsdexDataProvider.this.getIntArray("end_of(window_of(dim_of(" + expr + ")))");
                if(startIdx.length != 1 || endIdx.length != 1) return null;
                final int numPoint = endIdx[0] - startIdx[0] + 1;
                final double delta[] = AsdexDataProvider.this.getDoubleArray("slope_of(axis_of(dim_of(" + expr + ")))");
                double curr;
                final double firstTime[] = AsdexDataProvider.this.getDoubleArray("i_to_x(dim_of(" + expr + ")," + startIdx[0] + ")");
                double begin[];
                double end[];
                try{
                    begin = AsdexDataProvider.this.getDoubleArray("begin_of(axis_of(dim_of(" + expr + ")))");
                    end = AsdexDataProvider.this.getDoubleArray("end_of(axis_of(dim_of(" + expr + ")))");
                }catch(final IOException e){
                    return null;
                }
                if(delta.length == 1 && numPoint > 1){
                    int i, j;
                    final double out[] = new double[numPoint];
                    for(i = j = 0, curr = firstTime[0]; i < numPoint; i++, j++)
                        out[i] = curr + j * delta[0];
                    return out;
                }
                if(delta.length > 1 && numPoint > 1){
                    int i, j, idx;
                    final double out[] = new double[numPoint];
                    for(i = j = 0, idx = 0, curr = firstTime[0]; i < numPoint; i++, j++){
                        out[i] = curr + j * delta[idx];
                        if(out[i] > end[idx]){
                            out[i] = end[idx];
                            idx++;
                            curr = begin[idx];
                            j = 0;
                        }
                    }
                    return out;
                }
            }catch(final Exception exc){
                System.err.println(exc.getMessage());
            }
            return null;
        }

        @Override
        public XYData getData(final double xmin, final double xmax, final int numPoints) throws Exception {
            final double x[] = this.getXDoubleData();
            final float y[] = this.getFloatData();
            return new XYData(x, y, Double.POSITIVE_INFINITY);
        }

        @Override
        public XYData getData(final int numPoints) throws Exception {
            final double x[] = this.getXDoubleData();
            final float y[] = this.getFloatData();
            return new XYData(x, y, Double.POSITIVE_INFINITY);
        }

        @Override
        public void getDataAsync(final double lowerBound, final double upperBound, final int numPoints) {}

        public float[] getFloatData() throws IOException {
            String in_y;
            in_y = AsdexDataProvider.this.ParseExpression(this.in_y);
            // _jscope_set = true;
            final String in_y_expr = "_jscope_" + this.v_idx;
            String set_tdivar = "";
            if(!this._jscope_set){
                this._jscope_set = true;
                set_tdivar = "_jscope_" + this.v_idx + " = (" + in_y + "), ";
            }
            if(this.resample && this.in_x == null){
                final String limits = "FLOAT(" + this.xmin + "), " + "FLOAT(" + this.xmax + ")";
                // String expr = "JavaResample("+ "FLOAT("+in_y+ "), "+
                // "FLOAT(DIM_OF("+in_y+")), "+ limits + ")";
                final String resampledExpr = "JavaResample(" + "FLOAT(" + in_y_expr + "), " + "FLOAT(DIM_OF(" + in_y_expr + ")), " + limits + ")";
                set_tdivar = "_jscope_" + this.v_idx + " = (" + resampledExpr + "), ";
                // String expr = set_tdivar + "fs_float("+resampledExpr+ ")";
                final String expr = set_tdivar + "fs_float(_jscope_" + this.v_idx + ")";
                return AsdexDataProvider.this.getFloatArray(expr);
            }
            return AsdexDataProvider.this.getFloatArray(set_tdivar + "fs_float(" + in_y_expr + ")");
        }

        @Override
        public int getNumDimension() throws IOException {
            String expr;
            if(this._jscope_set) expr = "shape(_jscope_" + this.v_idx + ")";
            else{
                this._jscope_set = true;
                expr = "( _jscope_" + this.v_idx + " = (" + this.in_y + "), shape(_jscope_" + this.v_idx + "))";
            }
            final int shape[] = AsdexDataProvider.this.getNumDimensions(expr);
            if(AsdexDataProvider.this.error != null){
                this._jscope_set = false;
                AsdexDataProvider.this.error = null;
                return 1;
            }
            return shape.length;
        }

        @Override
        public String getTitle() throws IOException {
            String expr;
            if(this._jscope_set) expr = "help_of(_jscope_" + this.v_idx + ")";
            else{
                this._jscope_set = true;
                expr = "( _jscope_" + this.v_idx + " = (" + this.in_y + "), help_of(_jscope_" + this.v_idx + "))";
            }
            final String out = AsdexDataProvider.this.getStringValue(expr);
            if(out == null) this._jscope_set = false;
            return out;
            // return getDefaultTitle(in_y);
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

        public float[] getXData() {
            try{
                if(this.currXData == null) this.currXData = this.getXRealData();
                return this.currXData.getFloatArray();
            }catch(final Exception exc){
                return null;
            }
        }

        public double[] getXDoubleData() {
            try{
                if(this.currXData == null) this.currXData = this.getXRealData();
                return this.currXData.getDoubleArray();
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public String getXLabel() throws IOException {
            String out = null;
            if(this.in_x == null || this.in_x.length() == 0){
                String expr;
                if(this._jscope_set) expr = "Units(dim_of(_jscope_" + this.v_idx + ", 1))";
                else{
                    this._jscope_set = true;
                    expr = "( _jscope_" + this.v_idx + " = (" + this.in_y + "), Units(dim_of(_jscope_" + this.v_idx + ", 1)))";
                }
                out = AsdexDataProvider.this.getStringValue(expr);
                // return getDefaultXLabel(in_y);
            }else{
                /*
                 * String expr; if(_jscope_set) expr = "Units(_jscope_"+v_idx+")"; else { _jscope_set = true; expr = "( _jscope_"+v_idx+" = ("+in_x+"), Units(_jscope_"+v_idx+")"; var_idx++; } return getDefaultYLabel(expr);
                 */
                out = AsdexDataProvider.this.getStringValue("Units(" + this.in_x + ")");
            }
            if(out == null) this._jscope_set = false;
            return out;
        }

        public double[] getXLimits() {
            System.out.println("BADABUM!!");
            return null;
        }

        public long[] getXLong() {
            System.out.println("BADABUM!!");
            return null;
        }

        public long[] getXLongData() {
            try{
                if(this.currXData == null) this.currXData = this.getXRealData();
                return this.currXData.getLongArray();
            }catch(final Exception exc){
                return null;
            }
        }

        RealArray getXRealData() throws IOException {
            String expr = null;
            double tBaseOut[] = null;
            if(this.in_x == null){
                if(this._jscope_set){
                    expr = "dim_of(_jscope_" + this.v_idx + ")";
                    tBaseOut = this.encodeTimeBase("_jscope_" + this.v_idx);
                    // expr = "JavaDim(dim_of(_jscope_"+v_idx + "), FLOAT("+(-Float.MAX_VALUE)+"), " + "FLOAT("+Float.MAX_VALUE+"))";
                    // isCoded = true;
                }else{
                    this._jscope_set = true;
                    final String in_y_expr = "_jscope_" + this.v_idx;
                    final String set_tdivar = "( _jscope_" + this.v_idx + " = (" + this.in_y + "), ";
                    if(this.resample){
                        final String limits = "FLOAT(" + this.xmin + "), " + "FLOAT(" + this.xmax + ")";
                        // expr = "DIM_OF(JavaResample("+ "FLOAT("+in_y+ "), "+
                        // "FLOAT(DIM_OF("+in_y+")), "+ limits + "))";
                        expr = set_tdivar + "JavaResample(" + "FLOAT(" + in_y_expr + "), " + "FLOAT(DIM_OF(" + in_y_expr + ")), " + limits + ")";
                    }else{
                        // expr = "dim_of("+in_y+")";
                        expr = set_tdivar + "dim_of(" + in_y_expr + ")";
                        tBaseOut = this.encodeTimeBase(this.in_y);
                        // expr = "JavaDim(dim_of("+in_y+"), FLOAT("+(-Float.MAX_VALUE)+"), " + "FLOAT("+Float.MAX_VALUE+"))";
                        // isCoded = true;
                    }
                }
                if(tBaseOut != null) return new RealArray(tBaseOut);
                return AsdexDataProvider.this.getRealArray(expr);
            }
            return AsdexDataProvider.this.getRealArray(this.in_x);
        }

        @Override
        public float[] getY2D() {
            System.out.println("BADABUM!!");
            return null;
        }

        public float[] getYData() throws IOException {
            String expr;
            if(this._jscope_set) expr = "dim_of(_jscope_" + this.v_idx + ", 1)";
            else{
                this._jscope_set = true;
                expr = "( _jscope_" + this.v_idx + " = (" + this.in_y + "), dim_of(_jscope_" + this.v_idx + ", 1))";
            }
            return AsdexDataProvider.this.getFloatArray(expr);
            // return getFloatArray("DIM_OF("+in_y+", 1)");
        }

        @Override
        public String getYLabel() throws IOException {
            String expr;
            if(this._jscope_set) expr = "Units(_jscope_" + this.v_idx + ")";
            else{
                this._jscope_set = true;
                expr = "( _jscope_" + this.v_idx + " = (" + this.in_y + "), Units(_jscope_" + this.v_idx + "))";
            }
            final String out = AsdexDataProvider.this.getStringValue(expr);
            if(out == null) this._jscope_set = false;
            return out;
            // return getDefaultYLabel(in_y);
        }

        @Override
        public float[] getZ() {
            System.out.println("BADABUM!!");
            return null;
        }

        @Override
        public String getZLabel() throws IOException {
            String expr;
            if(this._jscope_set) expr = "Units(dim_of(_jscope_" + this.v_idx + ", 1))";
            else{
                this._jscope_set = true;
                expr = "( _jscope_" + this.v_idx + " = (" + this.in_y + "), Units(dim_of(_jscope_" + this.v_idx + ", 1)))";
            }
            final String out = AsdexDataProvider.this.getStringValue(expr);
            if(out == null) this._jscope_set = false;
            return out;
            // return getDefaultZLabel(in_y);
        }

        @Override
        public boolean isXLong() {
            return false;
        }

        @Override
        public void setContinuousUpdate(final boolean continuopusUpdate) {}
    }

    public static boolean DataPending() {
        return false;
    }

    public static boolean SupportsCompression() {
        return false;
    }

    public static boolean SupportsContinuous() {
        return false;
    }

    public static boolean SupportsFastNetwork() {
        return false;
    }

    public AsdexDataProvider(){
        super();
    }

    public AsdexDataProvider(final String provider) throws IOException{
        super(provider);
    }

    @Override
    public synchronized float[] getFloatArray(final String in) throws IOException {
        // String parsed = ParseExpression(in);
        final String parsed = in;
        if(parsed == null) return null;
        this.error = null;
        final float[] out_array = super.getFloatArray(parsed);
        if(out_array == null && this.error == null) this.error = "Cannot evaluate " + in + " for shot " + this.shot;
        if(out_array != null && out_array.length <= 1){
            this.error = "Cannot evaluate " + in + " for shot " + this.shot;
            return null;
        }
        return out_array;
    }

    @Override
    public synchronized int[] getIntArray(final String in) throws IOException {
        final String parsed = this.ParseExpression(in);
        if(parsed == null) return null;
        return super.getIntArray(parsed);
    }

    @Override
    public int[] getNumDimensions(final String spec) {
        return new int[]{1};
    }

    @Override
    public WaveData getWaveData(final String in) {
        return new SimpleWaveData(in);
    }

    @Override
    public int inquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    protected String ParseExpression(final String in) {
        if(in.startsWith("DIM_OF(")) return in;
        final StringTokenizer st = new StringTokenizer(in, ":");
        String res;
        try{
            final String diag = st.nextToken();
            final String name = st.nextToken();
            res = "augsignal(" + this.shot + ",\"" + diag + "\",\"" + name + "\")";
        }catch(final Exception e){
            this.error = "Wrong signal format: must be <diagnostic>:<signal>";
            return null;
        }
        System.out.println(res);
        return res;
    }

    @Override
    public void setArgument(final String arg) throws IOException {
        this.mds.setProvider(arg);
        this.mds.setUser("mdsplus");
    }

    @Override
    public synchronized void update(final String exp, final long s) {
        this.error = null;
        this.shot = s;
    }
}

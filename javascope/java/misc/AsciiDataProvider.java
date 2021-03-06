package misc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import jscope.ConnectionListener;
import jscope.DataProvider;
import jscope.DataServerItem;
import jscope.FrameData;
import jscope.UpdateEventListener;
import jscope.WaveData;
import jscope.WaveDataListener;
import jscope.XYData;

public final class AsciiDataProvider implements DataProvider{
    /*
     * File structure extension prop Title= Signal= XLabel= YLabel= ZLabel= Dimension= Time=t_start:t_end:dt;....;t_start:t_end:dt or t1,t2,...,tn Data=y1,y2,y3....,yn X=x1,x2,x3....,xn
     */
    class SimpleWaveData implements WaveData{
        int        dimension;
        String     file_x, file_y;
        Properties x_prop = new Properties();
        Properties y_prop = new Properties();

        public SimpleWaveData(final String in_y){
            this.file_y = this.getPathValue(in_y);
            AsciiDataProvider.this.xPropertiesFile = AsciiDataProvider.this.yPropertiesFile = this.setPropValues(this.file_y, this.y_prop);
            this.x_prop = this.y_prop;
            this.file_x = null;
        }

        public SimpleWaveData(final String in_y, final String in_x){
            this.file_y = this.getPathValue(in_y);
            AsciiDataProvider.this.yPropertiesFile = this.setPropValues(this.file_y, this.y_prop);
            this.file_x = this.getPathValue(in_x);
            AsciiDataProvider.this.xPropertiesFile = this.setPropValues(this.file_x, this.x_prop);
        }

        @Override
        public void addWaveDataListener(final WaveDataListener listener) {}

        private float[] byteArrayToFloat(final byte a[]) {
            final int size = a.length / 4;
            float out[] = new float[size];
            final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(a));
            try{
                for(int i = 0; i < size; i++)
                    out[i] = dis.readFloat();
                dis.close();
            }catch(final Exception exc){
                AsciiDataProvider.this.error = "File sintax error : " + exc.getMessage();
                out = null;
            }
            return out;
        }

        private float[] decodeTimes(final String val) {
            try{
                if(val == null){
                    AsciiDataProvider.this.error = "File syntax error";
                    return null;
                }
                StringTokenizer st = new StringTokenizer(val, ":");
                if(st.countTokens() > 1){
                    st = new StringTokenizer(val, ";");
                    final int num_base = st.countTokens();
                    final ByteArrayOutputStream aos = new ByteArrayOutputStream();
                    final DataOutputStream dos = new DataOutputStream(aos);
                    String time_st;
                    for(int i = 0; i < num_base; i++){
                        time_st = st.nextToken();
                        final StringTokenizer st1 = new StringTokenizer(time_st, ":");
                        final float start = Float.parseFloat(st1.nextToken().trim());
                        final float end = Float.parseFloat(st1.nextToken().trim());
                        final float dt = Float.parseFloat(st1.nextToken().trim());
                        for(float t = start; t <= end; t += dt)
                            dos.writeFloat(t);
                    }
                    dos.close();
                    return this.byteArrayToFloat(aos.toByteArray());
                }
                return this.decodeValues(val);
            }catch(final Exception exc){
                AsciiDataProvider.this.error = "File syntax error: " + exc.getMessage();
                return null;
            }
        }

        private float[] decodeValues(final String val) {
            if(val == null){
                AsciiDataProvider.this.error = "File syntax error";
                return null;
            }
            final StringTokenizer st = new StringTokenizer(val, ",");
            final int num = st.countTokens();
            float out[] = new float[num];
            String d_st;
            int i = 0;
            try{
                while(st.hasMoreElements()){
                    d_st = st.nextToken().trim();
                    out[i++] = Float.parseFloat(d_st);
                }
            }catch(final NumberFormatException exc){
                AsciiDataProvider.this.error = "File syntax error : " + exc.getMessage();
                out = null;
            }
            return out;
        }

        // GAB JULY 2014 NEW WAVEDATA INTERFACE RAFFAZZONATA
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
            if(AsciiDataProvider.this.xPropertiesFile) return this.decodeValues(this.x_prop.getProperty("Data"));
            if(AsciiDataProvider.this.y == null) throw(new IOException(AsciiDataProvider.this.error));
            return AsciiDataProvider.this.y;
        }

        @Override
        public int getNumDimension() throws IOException {
            try{
                this.dimension = Integer.parseInt(this.y_prop.getProperty("Dimension"));
                return this.dimension;
            }catch(final NumberFormatException exc){
                return(this.dimension = 1);
            }
        }

        private String getPathValue(final String in) {
            String out = "";
            if(AsciiDataProvider.this.path_exp != null) out = AsciiDataProvider.this.path_exp;
            if(AsciiDataProvider.this.curr_shot > 0) out = out + File.separatorChar + AsciiDataProvider.this.curr_shot;
            if(out != null && out.length() > 0) out = out + File.separatorChar + in;
            else out = in;
            return out;
        }

        @Override
        public String getTitle() throws IOException {
            return this.y_prop.getProperty("Title");
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

        public float[] getXData() throws IOException {
            if(this.file_x == null){
                if(AsciiDataProvider.this.yPropertiesFile) return this.decodeTimes(this.y_prop.getProperty("Time"));
                if(AsciiDataProvider.this.x == null) throw(new IOException(AsciiDataProvider.this.error));
                return AsciiDataProvider.this.x;
            }else if(AsciiDataProvider.this.xPropertiesFile) return this.decodeValues(this.x_prop.getProperty("Data"));
            if(AsciiDataProvider.this.y == null) throw(new IOException(AsciiDataProvider.this.error));
            return AsciiDataProvider.this.y;
        }

        public double[] getXDoubleData() {
            return null;
        }

        @Override
        public String getXLabel() throws IOException {
            if(this.file_x == null) return this.y_prop.getProperty("XLabel");
            return this.x_prop.getProperty("YLabel");
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
            return null;
        }

        @Override
        public float[] getY2D() {
            System.out.println("BADABUM!!");
            return null;
        }

        public float[] getYData() throws IOException {
            if(AsciiDataProvider.this.xPropertiesFile) return this.decodeValues(this.x_prop.getProperty("X"));
            AsciiDataProvider.this.error = "2D signal in column ASCII file format not yet supported";
            return null; // throw new IOException(error);
        }

        @Override
        public String getYLabel() throws IOException {
            return this.y_prop.getProperty("YLabel");
        }

        @Override
        public float[] getZ() {
            System.out.println("BADABUM!!");
            return null;
        }

        @Override
        public String getZLabel() throws IOException {
            return this.y_prop.getProperty("ZLabel");
        }

        private boolean isPropertiesFile(final Properties prop) {
            final String val = prop.getProperty("Time");
            return !(val == null || this.numElement(val, ",") < 2);
        }

        @Override
        public boolean isXLong() {
            return false;
        }

        private void loadSignalValues(final String in) throws Exception {
            final BufferedReader bufR = new BufferedReader(new FileReader(in));
            String ln;
            StringTokenizer st;
            while((ln = bufR.readLine()) != null){
                st = new StringTokenizer(ln);
                final int numColumn = st.countTokens();
                if(numColumn == 2 && st.nextToken().equals("Time") && st.nextToken().equals("Data")){
                    AsciiDataProvider.this.x = new float[1000];
                    AsciiDataProvider.this.y = new float[1000];
                    int count = 0;
                    int maxCount = 1000;
                    while((ln = bufR.readLine()) != null){
                        st = new StringTokenizer(ln);
                        if(count == maxCount){
                            AsciiDataProvider.this.x = this.resizeBuffer(AsciiDataProvider.this.x, AsciiDataProvider.this.x.length + 1000);
                            AsciiDataProvider.this.y = this.resizeBuffer(AsciiDataProvider.this.y, AsciiDataProvider.this.y.length + 1000);
                            maxCount = AsciiDataProvider.this.y.length;
                        }
                        AsciiDataProvider.this.x[count] = Float.parseFloat(st.nextToken());
                        AsciiDataProvider.this.y[count] = Float.parseFloat(st.nextToken());
                        count++;
                    }
                    AsciiDataProvider.this.x = this.resizeBuffer(AsciiDataProvider.this.x, count);
                    AsciiDataProvider.this.y = this.resizeBuffer(AsciiDataProvider.this.y, count);
                }
            }
            bufR.close();
            if(AsciiDataProvider.this.x == null || AsciiDataProvider.this.y == null) throw(new Exception("No data in file or file syntax error"));
        }

        private int numElement(final String val, final String separator) {
            final StringTokenizer st = new StringTokenizer(val, separator);
            return(st.countTokens());
        }

        private float[] resizeBuffer(final float[] b, final int size) {
            final float[] newB = new float[size];
            System.arraycopy(b, 0, newB, 0, size);
            return newB;
        }

        @Override
        public void setContinuousUpdate(final boolean continuopusUpdate) {}

        private boolean setPropValues(final String in, final Properties prop) {
            boolean propertiesFile = false;
            try{
                prop.load(new FileInputStream(in));
                propertiesFile = this.isPropertiesFile(prop);
                if(!propertiesFile){
                    this.loadSignalValues(in);
                }
            }catch(final Exception exc){
                AsciiDataProvider.this.error = "File " + in + " error : " + exc.getMessage();
            }
            return false;
        }
    }

    public static boolean DataPending() {
        return false;
    }

    public static byte[] GetAllFrames(final String in_frame) {
        return null;
    }

    public static byte[] GetFrameAt(final String in_expr, final int frame_idx) {
        String n;
        byte buf[] = null;
        long size = 0;
        final int i = frame_idx;
        String in, ext;
        in = in_expr.substring(0, in_expr.indexOf("."));
        ext = in_expr.substring(in_expr.indexOf("."), in_expr.length());
        if(i < 10) n = in + "_00" + (i) + ext;
        else n = in + "_0" + (i) + ext;
        final File f = new File(n);
        if(f.exists()){
            System.out.println("Esiste " + n);
            try{
                final FileInputStream bin = new FileInputStream(n);
                size = f.length();
                buf = new byte[(int)size];
                if(buf != null) bin.read(buf);
                bin.close();
            }catch(final IOException e){}
        }else{
            System.out.println("Non Esiste " + n);
        }
        return buf;
    }

    public static float[] GetFrameTimes(final String in_expr) {
        int cnt = 0;
        String n;
        File f;
        float[] out = null;
        String in, ext;
        in = in_expr.substring(0, in_expr.indexOf("."));
        ext = in_expr.substring(in_expr.indexOf("."), in_expr.length());
        for(int i = 0; i < 100; i++){
            if(i < 10) n = in + "_00" + (i) + ext;
            else n = in + "_0" + (i) + ext;
            f = new File(n);
            if(f.exists()) cnt++;
        }
        if(cnt != 0){
            out = new float[cnt];
            for(int i = 1; i < out.length; i++)
                out[i] += out[i - 1] + 1;
        }
        return out;
    }

    public static WaveData GetResampledWaveData(final String in, final double start, final double end, final int n_points) {
        return null;
    }

    public static WaveData GetResampledWaveData(final String in_y, final String in_x, final double start, final double end, final int n_points) {
        return null;
    }

    /*
    public static void main(final String args[]) {//TODO:main
        final AsciiDataProvider p = new AsciiDataProvider();
        p.getWaveData("c:\\test.txt");
    }
    */
    public static boolean SupportsCompression() {
        return false;
    }

    public static boolean SupportsContinuous() {
        return false;
    }

    public static boolean SupportsFastNetwork() {
        return false;
    }
    long            curr_shot       = -1;
    String          error           = null;
    String          path_exp        = null;
    float           time[];
    float           x[];
    private boolean xPropertiesFile = false;
    float           y[];
    private boolean yPropertiesFile = false;

    @Override
    public void abort() {
        // TODO Auto-generated method stub
    }

    @Override
    public void addConnectionListener(final ConnectionListener l) {}

    @Override
    public void addUpdateEventListener(final UpdateEventListener l, final String event) {}

    @Override
    public final boolean checkProvider() {
        try{
            this.getShots("0");
            return true;
        }catch(final IOException exc){}
        return false;
    }

    @Override
    public void dispose() {}

    public void enableAsyncUpdate(final boolean enable) {}

    @Override
    public String errorString() {
        return this.error;
    }

    public void getDataAsync(final double lowerBound, final double upperBound, final double resolution) {}

    @Override
    public Class getDefaultBrowser() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getFloat(final String in) {
        this.error = null;
        return Float.parseFloat(in);
    }

    @Override
    public FrameData getFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        throw(new IOException("Frames visualization on DemoDataProvider not implemented"));
    }

    @Override
    public final String getLegendString(final String s) {
        return s;
    }

    @Override
    public long[] getShots(final String in) throws IOException {
        this.error = null;
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
                        result[i++] = Long.parseLong(st.nextToken());
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
        this.error = "Error parsing shot number(s)";
        throw(new IOException(this.error));
    }

    @Override
    public String getString(final String in) {
        this.error = null;
        return new String(in);
    }

    @Override
    public WaveData getWaveData(final String in) {
        return new SimpleWaveData(in);
    }

    @Override
    public WaveData getWaveData(final String in_y, final String in_x) {
        return new SimpleWaveData(in_y, in_x);
    }

    @Override
    public int inquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    @Override
    public void join() {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeConnectionListener(final ConnectionListener l) {}

    @Override
    public void removeUpdateEventListener(final UpdateEventListener l, final String event) {}

    @Override
    public void setArgument(final String arg) {}

    public void setCompression(final boolean state) {}

    public void setContinuousUpdate(final boolean continuousUpdate) {}

    @Override
    public void setEnvironment(final String exp) {
        this.error = null;
    }

    @Override
    public boolean supportsTunneling() {
        return false;
    }

    @Override
    public void update(final String exp, final long s) {
        this.error = null;
        this.path_exp = exp;
        this.curr_shot = s;
    }
}

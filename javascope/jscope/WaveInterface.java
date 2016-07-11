package jscope;

/* $Id$ */
import java.io.IOException;
import java.io.PrintWriter;
import debug.DEBUG;
import jscope.ColorMap.ColorProfile;

public class WaveInterface{
    static public boolean    auto_color_on_expr = false;
    protected static boolean brief_error        = true;
    static final int         MAX_NUM_SHOT       = 30;
    static SignalBox         sig_box            = new SignalBox();

    protected static String addNewLineCode(final String s) {
        if(DEBUG.M) System.out.println("WaveInterface.AddNewLineCode(\"" + s + "\")");
        String s_new = new String();
        int new_pos = 0, old_pos = 0;
        if(s == null) return null;
        while((new_pos = s.indexOf("\n", old_pos)) != -1){
            s_new = s_new.concat(s.substring(old_pos, new_pos));
            old_pos = new_pos + "\n".length();
            s_new += "|||";
        }
        s_new = s_new.concat(s.substring(old_pos, s.length()));
        return s_new;
    }

    static public void freeCache() {}

    private static String getFirstLine(final String str) {
        if(DEBUG.M) System.out.println("WaveInterface.getFirstLine(\"" + str + "\")");
        final int idx = str.indexOf("\n");
        if(idx != -1) return str.substring(0, idx);
        return str;
    }

    static public long[] getShotArray(final String in_shots, final String exp, final DataProvider dp) throws IOException {
        if(DEBUG.M) System.out.println("WaveInterface.getShotArray(\"" + in_shots + "\", " + exp + "\", " + dp + ")");
        long shot_list[] = null;
        String error;
        if(in_shots == null || in_shots.trim().length() == 0 || dp == null) return null;
        String shotExpr = in_shots;
        if(exp != null) shotExpr = WaveInterface.processShotExpression(in_shots, exp);
        shot_list = dp.getShots(shotExpr);
        if(shot_list == null || shot_list.length == 0 || shot_list.length > WaveInterface.MAX_NUM_SHOT){
            if(shot_list != null && shot_list.length > WaveInterface.MAX_NUM_SHOT) error = "Too many shots. Max shot list elements " + WaveInterface.MAX_NUM_SHOT + "\n";
            else{
                if(dp.errorString() != null){
                    error = dp.errorString();
                    if(error.indexOf("_jScopeMainShots") != -1) error = "Undefined main shot value";
                }else error = "Shot syntax error\n";
            }
            shot_list = null;
            throw(new IOException(error));
        }
        return shot_list;
    }

    static String processShotExpression(String shotExpr, final String exp) {
        if(DEBUG.M) System.out.println("WaveInterface.processShotExpression(\"" + shotExpr + "\", " + exp + "\")");
        String outStr = "";
        int idx = 0;
        int prevIdx = 0;
        shotExpr = shotExpr.trim();
        if(exp == null || exp.length() == 0) return shotExpr;
        while((idx = shotExpr.indexOf('0', prevIdx)) != -1){
            if((idx > 0 && Character.isLetterOrDigit(shotExpr.charAt(idx - 1))) || (idx < (shotExpr.length() - 1) && Character.isLetterOrDigit(shotExpr.charAt(idx + 1)))){
                outStr += shotExpr.substring(prevIdx, idx + 1);
            }else{
                outStr += shotExpr.substring(prevIdx, idx) + "current_shot(\"" + exp + "\")";
            }
            prevIdx = idx + 1;
        }
        if(outStr.length() == 0) return shotExpr;
        outStr += (prevIdx < shotExpr.length() ? shotExpr.substring(prevIdx, shotExpr.length()) : "");
        return outStr;
    }

    protected static String removeNewLineCode(final String s) {
        if(DEBUG.M) System.out.println("WaveInterface.RemoveNewLineCode(\"" + s + "\")");
        String y_new = new String();
        int new_pos = 0, old_pos = 0;
        while((new_pos = s.indexOf("|||", old_pos)) != -1){
            y_new = y_new.concat(s.substring(old_pos, new_pos));
            old_pos = new_pos + "|||".length();
            y_new += '\n';
        }
        y_new = y_new.concat(s.substring(old_pos, s.length()));
        return y_new;
    }

    static String trimString(final String s) {
        if(DEBUG.M) System.out.println("WaveInterface.TrimString(\"" + s + "\")");
        String s_new = new String();
        int new_pos = 0, old_pos = 0;
        while((new_pos = s.indexOf(" ", old_pos)) != -1){
            s_new = s_new.concat(s.substring(old_pos, new_pos));
            old_pos = new_pos + " ".length();
        }
        s_new = s_new.concat(s.substring(old_pos, s.length()));
        return s_new;
    }

    protected static void writeLine(final PrintWriter out, final String prompt, final String value) {
        if(DEBUG.M) System.out.println("WaveInterface.WriteLine(" + out + ", \"" + prompt + "\", \"" + value + "\")");
        if(value != null && value.length() != 0){
            out.println(prompt + value);
        }
    }
    // True when a signal is added
    protected boolean      add_signal         = false;
    public boolean         cache_enabled      = false;
    protected ColorProfile colorProfile       = new ColorProfile();
    public int             colors_idx[];
    private String         curr_error;
    public DataProvider    dp;
    public String          error;
    /*
     * // Used for asynchronous Update public boolean asynch_update = true; Signal wave_signals[]; double wave_xmin, wave_xmax; int wave_timestamp; boolean request_pending; double orig_xmin, orig_xmax; protected boolean is_async_update = false;
     */
    protected boolean      evaluated[];
    public String          experiment;
    private Frames         frames;
    public int             height;
    protected boolean      horizontal_flip    = false;
    public String          in_def_node;
    public int             in_grid_mode;
    public String          in_label[], in_x[], in_y[], in_up_err[], in_low_err[];
    public String          in_shot;
    public String          in_title, in_xlabel, in_ylabel;
    public boolean         in_upd_limits      = true;
    public String          in_xmin, in_xmax, in_ymax, in_ymin, in_timemax, in_timemin;
    public boolean         interpolates[];
    protected boolean      is_image           = false;
    protected boolean      is_signal_added    = false;
    public boolean         isContinuousUpdate = false;
    protected boolean      keep_ratio         = true;
    protected double       legend_x;
    public double          legend_y;
    public int             markers[];
    public int             markers_step[];
    public int             mode1D[];
    public int             mode2D[];
    private boolean        modified           = true;
    public int             num_shot           = 1;
    public int             num_waves;
    public String          provider;
    protected boolean      reversed           = false;
    public long            shots[];
    protected boolean      show_legend        = false;
    int                    signal_select      = -1;
    public Signal          signals[];
    public String          title, xlabel, ylabel, zlabel;
    protected boolean      vertical_flip      = false;
    public String          w_error[];
    public Waveform        wave;
    public boolean         x_log, y_log;
    public double          xmax, xmin, ymax, ymin, timemax, timemin;

    public WaveInterface(){
        this.createWaveInterface(null, null);
    }

    public WaveInterface(final DataProvider dp){
        this.createWaveInterface(null, dp);
    }

    public WaveInterface(final Waveform wave){
        this.createWaveInterface(wave, null);
    }

    public WaveInterface(final Waveform wave, final DataProvider dp){
        if(DEBUG.M) System.out.println("WaveInterface(" + wave + ", " + dp + ")");
        this.createWaveInterface(wave, dp);
    }

    public void addFrames(final String frames, final String frames_time) {
        if(DEBUG.M) System.out.println("WaveInterface.AddFrames(\"" + frames + "\", \"" + frames_time + "\")");
        this.setAsImage(true);
        this.in_x = new String[1];
        this.in_y = new String[1];
        this.in_x[0] = frames_time;
        this.in_y[0] = frames;
    }

    public void AddFrames(final String frames) {
        this.addFrames(frames, null);
    }

    public boolean addSignal(final String y_expr) {
        return this.addSignal("", y_expr);
    }

    public boolean addSignal(final String x_expr, final String y_expr) {
        if(DEBUG.M) System.out.println("WaveInterface.AddSignal(\"" + x_expr + "\", \"" + y_expr + "\")");
        return this.addSignals(new String[]{x_expr}, new String[]{y_expr});
    }

    public boolean addSignals(final String x_expr[], final String y_expr[]) {
        if(DEBUG.M) System.out.println("WaveInterface.AddSignals(x_expr[], y_expr[])");
        if(x_expr.length != y_expr.length || x_expr.length == 0) return false;
        int new_num_waves;
        int num_sig = x_expr.length;
        boolean is_new[] = null;
        if(this.num_waves != 0){
            is_new = new boolean[x_expr.length];
            for(int j = 0; j < x_expr.length; j++){
                is_new[j] = true;
                for(int i = 0; i < this.num_waves; i++){
                    if(y_expr[j].equals(this.in_y[i]) && (this.in_x[i] != null && x_expr[j].equals(this.in_x[i]))){
                        is_new[j] = this.evaluated != null && this.evaluated[i];
                        num_sig--;
                    }
                }
            }
            if(num_sig == 0) return true;
            new_num_waves = this.num_waves + (this.num_shot != 0 ? this.num_shot : 1) * num_sig;
        }else new_num_waves = (this.num_shot != 0 ? this.num_shot : 1) * x_expr.length;
        final String[] new_in_label = new String[new_num_waves];
        final String[] new_in_x = new String[new_num_waves];
        final String[] new_in_y = new String[new_num_waves];
        final String[] new_in_up_err = new String[new_num_waves];
        final String[] new_in_low_err = new String[new_num_waves];
        final int[] new_markers = new int[new_num_waves];
        final int[] new_markers_step = new int[new_num_waves];
        final int[] new_colors_idx = new int[new_num_waves];
        final boolean[] new_interpolates = new boolean[new_num_waves];
        final int[] new_mode2D = new int[new_num_waves];
        final int[] new_mode1D = new int[new_num_waves];
        final long[] new_shots = (this.shots == null) ? null : new long[new_num_waves];
        final boolean[] new_evaluated = new boolean[new_num_waves];
        final Signal[] new_signals = new Signal[new_num_waves];
        final String[] new_w_error = new String[new_num_waves];
        if(this.num_waves > 0){
            System.arraycopy(this.in_label, 0, new_in_label, 0, this.num_waves);
            System.arraycopy(this.in_x, 0, new_in_x, 0, this.num_waves);
            System.arraycopy(this.in_y, 0, new_in_y, 0, this.num_waves);
            System.arraycopy(this.in_up_err, 0, new_in_up_err, 0, this.num_waves);
            System.arraycopy(this.in_low_err, 0, new_in_low_err, 0, this.num_waves);
            System.arraycopy(this.markers, 0, new_markers, 0, this.num_waves);
            System.arraycopy(this.markers_step, 0, new_markers_step, 0, this.num_waves);
            System.arraycopy(this.colors_idx, 0, new_colors_idx, 0, this.num_waves);
            System.arraycopy(this.interpolates, 0, new_interpolates, 0, this.num_waves);
            System.arraycopy(this.mode2D, 0, new_mode2D, 0, this.num_waves);
            System.arraycopy(this.mode1D, 0, new_mode1D, 0, this.num_waves);
        }
        if(new_shots != null) System.arraycopy(this.shots, 0, new_shots, 0, this.num_waves);
        if(this.signals != null) System.arraycopy(this.signals, 0, new_signals, 0, this.num_waves);
        if(this.w_error != null) System.arraycopy(this.w_error, 0, new_w_error, 0, this.num_waves);
        for(int i = 0; i < this.num_waves; i++){
            if(this.evaluated != null) new_evaluated[i] = this.evaluated[i];
            else new_evaluated[i] = false;
        }
        final int numshot = this.num_shot > 0 ? this.num_shot : 1;
        for(int i = 0, k = this.num_waves; i < x_expr.length; i++){
            if(is_new != null && !is_new[i]) continue;
            for(int j = 0; j < numshot; j++, k++){
                new_in_label[k] = "";
                new_in_x[k] = x_expr[i];
                new_in_y[k] = y_expr[i];
                new_in_up_err[k] = "";
                new_in_low_err[k] = "";
                new_markers[k] = 0;
                new_markers_step[k] = 1;
                if(WaveInterface.auto_color_on_expr) new_colors_idx[k] = (k - j) % Waveform.colors.length;
                else new_colors_idx[k] = j % Waveform.colors.length;
                new_interpolates[k] = true;
                new_evaluated[k] = false;
                new_mode2D[k] = Signal.MODE_XZ;
                new_mode1D[k] = Signal.MODE_LINE;
                if(new_shots != null && this.shots.length > 0 && this.num_shot > 0) new_shots[k] = this.shots[j];
            }
        }
        this.in_label = new_in_label;
        this.in_x = new_in_x;
        this.in_y = new_in_y;
        this.in_up_err = new_in_up_err;
        this.in_low_err = new_in_low_err;
        this.markers = new_markers;
        this.markers_step = new_markers_step;
        this.colors_idx = new_colors_idx;
        this.interpolates = new_interpolates;
        this.mode2D = new_mode2D;
        this.mode1D = new_mode1D;
        this.shots = new_shots;
        this.num_waves = new_num_waves;
        this.evaluated = new_evaluated;
        this.signals = new_signals;
        this.w_error = new_w_error;
        this.add_signal = true;
        return true;
    }

    public boolean allEvaluated() {
        if(DEBUG.M) System.out.println("WaveInterface.allEvaluated()");
        if(this.evaluated == null) return false;
        for(int curr_wave = 0; curr_wave < this.num_waves; curr_wave++)
            if(!this.evaluated[curr_wave]){
                this.modified = true;
                return false;
            }
        this.modified = false;
        return true;
    }

    @SuppressWarnings("rawtypes")
    private void createNewFramesClass(final int image_type) throws IOException {
        if(DEBUG.M) System.out.println("WaveInterface.CreateNewFramesClass()");
        if(image_type == FrameData.JAI_IMAGE){
            try{
                final Class cl = Class.forName("jScope.FrameJAI");
                this.frames = (Frames)cl.newInstance();
            }catch(final Exception e){
                throw(new IOException("Java Advanced Imaging must be installed to show this type of image"));
            }
        }else{
            this.frames = new Frames();
            this.frames.setColorProfile(this.colorProfile);
        }
    }

    private void createWaveInterface(final Waveform wave, final DataProvider dp) {
        if(DEBUG.M) System.out.println("WaveInterface.CreateWaveInterface(" + wave + ", " + dp + ")");
        this.wave = wave;
        this.dp = dp;
        if(dp == null) this.experiment = null;
        this.shots = null;
        this.in_xmin = this.in_xmax = this.in_ymin = this.in_ymax = this.in_title = null;
        this.in_xlabel = this.in_ylabel = this.in_timemax = this.in_timemin = null;
        this.markers = null;
        this.interpolates = null;
        this.mode2D = null;
        this.mode1D = null;
        this.x_log = this.y_log = false;
        this.in_upd_limits = true;
        this.show_legend = false;
        this.reversed = false;
    }

    public void enableCache(final boolean state) {}

    public void erase() {
        if(DEBUG.M) System.out.println("WaveInterface.Erase()");
        this.num_waves = 0;
        this.in_label = null;
        this.in_x = this.in_y = null;
        this.in_up_err = this.in_low_err = null;
        this.in_xmin = this.in_xmax = this.in_ymax = this.in_ymin = null;
        this.in_timemax = null;
        this.in_timemin = null;
        this.in_title = this.in_xlabel = this.in_ylabel = null;
        this.experiment = this.in_shot = null;
        this.num_shot = 0;
        this.modified = true;
        this.markers = this.markers_step = null;
        this.colors_idx = null;
        this.interpolates = null;
        this.mode2D = this.mode1D = null;
        this.shots = null;
        this.error = this.curr_error = null;
        this.w_error = null;
        this.signals = null;
        this.title = null;
        this.xlabel = this.ylabel = this.zlabel = null;
        this.is_image = false;
        this.keep_ratio = true;
        this.horizontal_flip = this.vertical_flip = false;
        this.frames = null;
        this.show_legend = false;
        this.evaluated = null;
    }

    public synchronized void evaluateOthers() throws Exception {
        if(DEBUG.M) System.out.println("WaveInterface.EvaluateOthers()");
        int curr_wave;
        if(this.is_image){
            if(!this.evaluated[0]){
                this.initializeFrames();
                if(this.frames != null) this.frames.setViewRect((int)this.xmin, (int)this.ymin, (int)this.xmax, (int)this.ymax);
                this.error = this.curr_error;
            }
            return;
        }
        if(this.evaluated == null){
            this.signals = null;
            return;
        }
        for(curr_wave = 0; curr_wave < this.num_waves; curr_wave++){
            if(!this.evaluated[curr_wave] && !(!this.interpolates[curr_wave] && this.markers[curr_wave] == Signal.NONE)){
                this.w_error[curr_wave] = null;
                this.signals[curr_wave] = this.getSignal(curr_wave, this.xmin, this.xmax);
                this.evaluated[curr_wave] = true;
                if(this.signals[curr_wave] == null){
                    this.w_error[curr_wave] = this.curr_error;
                    this.evaluated[curr_wave] = false;
                }else{
                    WaveInterface.sig_box.AddSignal(this.in_x[curr_wave], this.in_y[curr_wave]);
                    this.setLimits(this.signals[curr_wave]);
                }
            }
        }
        this.modified = false;
    }

    public synchronized void evaluateShot(final long shot) throws Exception {
        if(DEBUG.M) System.out.println("WaveInterface.EvaluateShot(" + shot + ")");
        int curr_wave;
        if(this.is_image) return;
        // dp.enableAsyncUpdate(false);
        for(curr_wave = 0; curr_wave < this.num_waves; curr_wave++){
            if((shot == 0) || (this.shots[curr_wave] == shot && !this.evaluated[curr_wave] && (this.interpolates[curr_wave] || this.markers[curr_wave] != Signal.NONE))){
                this.w_error[curr_wave] = null;
                this.signals[curr_wave] = this.getSignal(curr_wave, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                this.evaluated[curr_wave] = true;
                if(this.signals[curr_wave] == null){
                    this.w_error[curr_wave] = this.curr_error;
                    this.evaluated[curr_wave] = false;
                }else{
                    WaveInterface.sig_box.AddSignal(this.in_x[curr_wave], this.in_y[curr_wave]);
                    this.setLimits(this.signals[curr_wave]);
                }
            }
        }
        // dp.enableAsyncUpdate(true);
    }

    public ColorProfile getColorProfile() {
        return this.colorProfile;
    }

    public DataProvider getDataProvider() {
        return this.dp;
    }

    public String getErrorTitle() {
        return this.getErrorTitle(WaveInterface.brief_error);
    }

    public String getErrorTitle(final boolean brief) {
        if(DEBUG.M) System.out.println("WaveInterface.getErrorTitle(" + brief + ")");
        int n_error = 0;
        String er = this.error;
        if(DEBUG.D) System.out.println(">> " + this.error);
        if(this.num_waves == 0 || (this.is_image && this.frames == null)){
            if(this.error != null){
                if(brief) er = WaveInterface.getFirstLine(new String(this.error));
            }
            return er;
        }
        for(int ii = 0; ii < this.num_waves; ii++){
            if(this.w_error != null && this.w_error[ii] != null) n_error++;
        }
        if(this.error == null && n_error > 1 && n_error == this.num_waves){
            er = "Evaluation error on all signals";
        }else{
            if(this.error != null) er = this.error;
            else{
                if(n_error == 1 && this.num_waves == 1){
                    er = this.w_error[0];
                }else if(n_error > 0) er = "< Evaluation error on " + n_error + " signal" + (n_error > 1 ? "s" : "") + " >";
            }
        }
        if(er != null && brief){
            final int idx = (er.indexOf('\n') == -1 ? er.length() : er.indexOf('\n'));
            er = er.substring(0, idx);
        }
        return er;
    }

    public Frames getFrames() {
        if(DEBUG.M) System.out.println("WaveInterface.getFrames()");
        return this.frames;
    }

    public boolean getModified() {
        return this.modified;
    }

    public int getNumEvaluatedSignal() {
        if(DEBUG.M) System.out.println("WaveInterface.getNumEvaluatedSignal()");
        if(this.signals == null) return 0;
        int i, n = 0;
        for(i = 0; i < this.signals.length; i++)
            if(this.signals[i] != null) n++;
        return n;
    }

    public long[] getShotArray(final String in_shots) throws IOException {
        final long curr_shots[] = WaveInterface.getShotArray(in_shots, this.experiment, this.dp);
        return curr_shots;
    }

    private Signal getSignal(final int curr_wave, double xmin, double xmax) throws Exception {
        if(DEBUG.M) System.out.println("WaveInterface.getSignal(" + curr_wave + ", " + xmin + ", " + xmax + ")");
        Signal out_signal = null;
        final int mode = this.wave.getMode();
        try{
            this.wave.setMode(Waveform.MODE_WAIT);
            if(this.in_y[curr_wave] == null){
                this.curr_error = "Missing Y value";
                this.wave.setMode(mode);
                return null;
            }
            synchronized(this.dp){
                out_signal = this.getSignalFromProvider(curr_wave, xmin, xmax);
            }
            if(out_signal != null){
                if(this.xmin > xmin) xmin = this.xmin;
                if(this.xmax < xmax) xmax = this.xmax;
                out_signal.setXLimits(xmin, xmax, Signal.AT_CREATION | Signal.FIXED_LIMIT);
                if(this.in_ymax != null && (this.in_ymax.trim()).length() != 0 && this.in_upd_limits) out_signal.setYmax(this.ymax, Signal.AT_CREATION | Signal.FIXED_LIMIT);
                if(this.in_ymin != null && (this.in_ymin.trim()).length() != 0 && this.in_upd_limits) out_signal.setYmin(this.ymin, Signal.AT_CREATION | Signal.FIXED_LIMIT);
            }
            this.wave.setMode(mode);
        }catch(final IOException exc){
            this.wave.setMode(mode);
        }
        return out_signal;
    }

    private Signal getSignalFromProvider(final int curr_wave, final double xmin, final double xmax) throws IOException {
        if(DEBUG.M) System.out.println("WaveInterface.getSignalFromProvider(" + curr_wave + ", " + xmin + ", " + xmax + ")");
        WaveData up_err = null, low_err = null;
        WaveData wd = null;
        WaveData xwd = null;
        int xDimension = 1;
        int yDimension = 1;
        Signal out_signal;
        String xlabel = null, ylabel = null, zlabel = null, title = null;
        if(this.shots != null && this.shots.length != 0) this.dp.update(this.experiment, this.shots[curr_wave]);
        else this.dp.update(null, 0);
        if(this.dp.errorString() != null){
            this.error = this.dp.errorString();
            return null;
        }
        if(this.in_def_node != null && this.in_def_node.length() > 0){
            this.dp.setEnvironment("__default_node = " + this.in_def_node);
        }else{
            this.dp.setEnvironment("__default_node = " + this.experiment + "::TOP");
        }
        if(this.dp.errorString() != null){
            this.error = this.dp.errorString();
            return null;
        }
        try{
            wd = this.dp.getWaveData(this.in_y[curr_wave]);
            yDimension = wd.getNumDimension();
            if(yDimension == 2) zlabel = wd.getZLabel();
        }catch(final Exception exc){
            yDimension = 1;
        }
        if(this.in_x[curr_wave] != null && (this.in_x[curr_wave].trim()).length() > 0){
            // dimension = 1;
            try{
                xwd = this.dp.getWaveData(this.in_x[curr_wave]);
                xDimension = xwd.getNumDimension();
                if(DEBUG.M) System.out.println("xDimension = " + xDimension);
            }catch(final Exception exc){
                xDimension = 1;
            }
            /*
             * if (dp.errorString() != null) { curr_error = dp.errorString(); return null; }
             */
        }
        if(this.dp.errorString() != null){
            this.curr_error = this.dp.errorString();
            return null;
        }
        if(xDimension > 2 || yDimension > 2){
            this.curr_error = "Can't display signal with more than two X or Y Dimensions ";
            return null;
        }
        if(this.in_x[curr_wave] != null && (this.in_x[curr_wave].trim()).length() != 0){
            wd = this.dp.getWaveData(this.in_y[curr_wave], this.in_x[curr_wave]);
            if(wd != null){
                xlabel = wd.getXLabel();
                ylabel = wd.getYLabel();
            }
            if(wd != null && this.in_up_err != null && this.in_up_err[curr_wave] != null && (this.in_up_err[curr_wave].trim()).length() != 0){
                up_err = this.dp.getWaveData(this.in_up_err[curr_wave]);
            }
            if(this.in_low_err != null && this.in_low_err[curr_wave] != null && (this.in_low_err[curr_wave].trim()).length() != 0){
                low_err = this.dp.getWaveData(this.in_low_err[curr_wave]);
            }
        }else // X field not defined
        {
            if(wd == null) wd = this.dp.getWaveData(this.in_y[curr_wave]);
            /*
             * if (yDimension > 1) { if (wd == null) wd = dp.getWaveData(in_y[curr_wave]); } else { if(wd == null) { wd = dp.getWaveData(in_y[curr_wave]); } }
             */
            if(yDimension == 1){
                if(this.in_up_err != null && this.in_up_err[curr_wave] != null && (this.in_up_err[curr_wave].trim()).length() != 0){
                    up_err = this.dp.getWaveData(this.in_up_err[curr_wave]);
                }
                if(this.in_low_err != null && this.in_low_err[curr_wave] != null && (this.in_low_err[curr_wave].trim()).length() != 0){
                    low_err = this.dp.getWaveData(this.in_low_err[curr_wave]);
                }
            }
        }
        if(wd == null){
            this.curr_error = this.dp.errorString();
            return null;
        }
        wd.setContinuousUpdate(this.isContinuousUpdate);
        if(xDimension == 1) out_signal = new Signal(wd, xwd, xmin, xmax, low_err, up_err);
        else out_signal = new Signal(wd, xwd, xmin, xmax);
        if(yDimension > 1) out_signal.setMode2D(this.mode2D[curr_wave]);
        else out_signal.setMode1D(this.mode1D[curr_wave]);
        if(wd != null) title = wd.getTitle();
        if(up_err != null && low_err != null) out_signal.AddAsymError(up_err, low_err);
        else if(up_err != null) out_signal.AddError(up_err);
        if(wd != null){
            xlabel = wd.getXLabel();
            ylabel = wd.getYLabel();
        }
        out_signal.setLabels(title, xlabel, ylabel, zlabel);
        return out_signal;
    }

    public String[] getSignalsName() {
        if(DEBUG.M) System.out.println("WaveInterface.getSignalsName()");
        String name[] = null, s;
        if(this.num_waves != 0){
            final int ns = (this.num_shot > 0 ? this.num_shot : 1);
            name = new String[this.num_waves / ns];
            for(int i = 0, j = 0; i < this.num_waves; i += ns){
                s = (this.in_label[i] != null && this.in_label[i].length() != 0) ? this.in_label[i] : this.in_y[i];
                name[j++] = s;
            }
        }
        return name;
    }

    public boolean[] getSignalsState() {
        if(DEBUG.M) System.out.println("WaveInterface.getSignalsState()");
        boolean state[] = null;
        if(this.num_waves != 0){
            final int ns = (this.num_shot > 0 ? this.num_shot : 1);
            state = new boolean[this.num_waves / ns];
            for(int i = 0, j = 0; i < this.num_waves; i += ns)
                state[j++] = (this.interpolates[i] || (this.markers[i] != Signal.NONE));
        }
        return state;
    }

    public boolean getSignalState(final int i) {
        if(DEBUG.M) System.out.println("WaveInterface.getSignalsState(" + i + ")");
        boolean state = false;
        // int idx = i * (num_shot > 0 ? num_shot : 1);
        if(i < this.num_waves) state = (this.interpolates[i] || (this.markers[i] != Signal.NONE));
        return state;
    }

    private void initializeFrames() {
        if(DEBUG.M) System.out.println("WaveInterface.InitializeFrames()");
        this.curr_error = null;
        WaveformEvent we;
        final int mode = this.wave.getMode();
        this.wave.setMode(Waveform.MODE_WAIT);
        if(this.in_y[0] == null){
            this.curr_error = "Missing Y value";
            return;
        }
        if(this.shots != null && this.shots.length != 0) this.dp.update(this.experiment, this.shots[0]);
        else this.dp.update(null, 0);
        try{
            we = new WaveformEvent(this.wave, "Loading single or multi frame image");
            this.wave.dispatchWaveformEvent(we);
            final FrameData fd = this.dp.getFrameData(this.in_y[0], this.in_x[0], (float)this.timemin, (float)this.timemax);
            if(fd != null){
                this.createNewFramesClass(fd.getFrameType());
                /*
                 * frames.setHorizontalFlip(horizontal_flip); frames.setVerticalFlip(vertical_flip);
                 */
                this.frames.setFrameData(fd);
                if(this.in_label != null && this.in_label[0] != null && this.in_label[0].length() != 0) this.frames.setName(this.in_label[0]);
                else this.frames.setName(this.in_y[0]);
                if(this.evaluated != null && this.evaluated.length > 0) this.evaluated[0] = true;
            }else{
                this.frames = null;
                this.curr_error = this.dp.errorString();
                if(this.evaluated != null && this.evaluated.length > 0) this.evaluated[0] = false;
            }
            // frames.WaitLoadFrame();
            this.wave.setMode(mode);
        }catch(final Throwable e){
            e.printStackTrace();
            this.wave.setMode(mode);
            this.frames = null;
            this.curr_error = " Load Frames error " + e.getMessage();
        }
    }

    public boolean isAddSignal() {
        return this.add_signal;
    }

    public boolean isSignalAdded() {
        return this.is_signal_added;
    }

    public void refresh() throws Exception {}

    public void setAddSignal(final boolean add_signal) {
        this.add_signal = add_signal;
    }

    public void setAsImage(final boolean is_image) {
        if(DEBUG.M) System.out.println("WaveInterface.setAsImage(" + is_image + ")");
        this.is_image = is_image;
    }

    public void setColorProfile(final ColorProfile colorProfile) {
        this.colorProfile = colorProfile;
    }

    public void setDataProvider(final DataProvider dp) {
        if(DEBUG.M) System.out.println("WaveInterface.setDataProvider(" + dp + ")");
        this.dp = dp;
        this.error = null;
        this.curr_error = null;
        this.w_error = null;
        this.signals = null;
        this.modified = true;
    }

    public void setExperiment(final String experiment) {
        if(DEBUG.M) System.out.println("WaveInterface.setExperiment(\"" + experiment + "\")");
        this.experiment = experiment;
    }

    public void setFrames(final Frames f) {
        if(DEBUG.M) System.out.println("WaveInterface.setFrames(" + this.frames + ")");
        this.frames = f;
    }

    public void setIsSignalAdded(final boolean is_signal_added) {
        this.is_signal_added = is_signal_added;
    }

    public void setLegendPosition(final double x, final double y) {
        if(DEBUG.M) System.out.println("WaveInterface.setLegendPosition(" + x + ", " + y + ")");
        this.legend_x = x;
        this.legend_y = y;
        this.show_legend = true;
    }

    public void setLimits() {
        if(DEBUG.M) System.out.println("WaveInterface.setLimits()");
        for(final Signal signal : this.signals)
            if(signal != null) try{
                this.setLimits(signal);
            }catch(final Exception e){
                System.err.println("WaveInterface.setLimits() @ signal " + signal);
            }
    }

    public void setLimits(final Signal s) throws Exception {
        if(DEBUG.M) System.out.println("WaveInterface.setLimits(" + s + ")");
        s.setXLimits(this.xmin, this.xmax, Signal.AT_CREATION);
        s.setYmin(this.ymin, Signal.AT_CREATION);
        s.setYmax(this.ymax, Signal.AT_CREATION);
    }

    public void setModified(final boolean state) {
        this.modified = state;
    }

    public void setShotArray(final String in_shot) throws IOException {
        this.in_shot = in_shot;
        final long curr_shots[] = this.getShotArray(in_shot);
        this.updateShot(curr_shots);
    }

    public void setSignalState(final String name, final boolean state) {
        if(DEBUG.M) System.out.println("WaveInterface.setSignalState(\"" + name + "\", " + state + ")");
        if(this.num_waves != 0){
            final int ns = (this.num_shot == 0) ? 1 : this.num_shot;
            for(int i = 0; i < this.num_waves; i++)
                if(name.equals(this.in_y[i]) || name.equals(this.in_label[i])){
                    for(int j = i; j < i + ns; j++){
                        this.interpolates[j] = state;
                        this.markers[i] = Signal.NONE;
                    }
                    return;
                }
        }
    }

    public void showLegend(final boolean state) {
        this.show_legend = state;
    }

    public synchronized boolean startEvaluate() throws IOException {
        if(DEBUG.M) System.out.println("WaveInterface.StartEvaluate()");
        this.error = null;
        if(this.modified) this.evaluated = null;
        if(this.in_y == null || this.in_x == null){
            this.error = "Missing Y or X values";
            this.signals = null;
            return false;
        }
        if(this.shots == null && !(this.experiment == null || this.experiment.trim().length() == 0)){
            this.error = "Missing shot value";
            this.signals = null;
        }
        if(this.shots != null && (this.experiment == null || this.experiment.trim().length() == 0)){
            this.error = "Missing experiment name";
            this.signals = null;
        }
        this.num_waves = this.in_y.length;
        if(this.modified){
            if(!this.is_image) this.signals = new Signal[this.num_waves];
            this.evaluated = new boolean[this.num_waves];
            this.w_error = new String[this.num_waves];
        }
        if(this.in_x != null && this.num_waves != this.in_x.length){
            this.error = "X values are different from Y values";
            return false;
        }
        if(this.shots != null && this.shots.length > 0){
            int i = 0;
            do{
                this.dp.update(this.experiment, this.shots[i]);
                i++;
            }while(i < this.shots.length && this.dp.errorString() != null);
        }else this.dp.update(null, 0);
        if(this.dp.errorString() != null){
            this.error = this.dp.errorString();
            return false;
        }
        if(DEBUG.D) System.out.println(">> Compute title");
        if(this.in_title != null && (this.in_title.trim()).length() != 0){
            this.title = this.dp.getString(this.in_title);
            if(this.title == null){
                this.error = this.dp.errorString();
                return false;
            }
        }
        if(DEBUG.D) System.out.println(">> Compute limits");
        if(this.in_xmin != null && (this.in_xmin.trim()).length() != 0 && this.in_upd_limits){
            this.xmin = this.dp.getFloat(this.in_xmin);
            if(this.dp.errorString() != null){
                this.error = this.dp.errorString();
                return false;
            }
        }else this.xmin = (!this.is_image) ? Double.NEGATIVE_INFINITY : -1;
        if(this.in_xmax != null && (this.in_xmax.trim()).length() != 0 && this.in_upd_limits){
            this.xmax = this.dp.getFloat(this.in_xmax);
            if(this.dp.errorString() != null){
                this.error = this.dp.errorString();
                return false;
            }
        }else this.xmax = (!this.is_image) ? Double.POSITIVE_INFINITY : -1;
        if(this.in_ymax != null && (this.in_ymax.trim()).length() != 0 && this.in_upd_limits){
            this.ymax = this.dp.getFloat(this.in_ymax);
            if(this.dp.errorString() != null){
                this.error = this.dp.errorString();
                return false;
            }
        }else this.ymax = (!this.is_image) ? Double.POSITIVE_INFINITY : -1;
        if(this.in_ymin != null && (this.in_ymin.trim()).length() != 0 && this.in_upd_limits){
            this.ymin = this.dp.getFloat(this.in_ymin);
            if(this.dp.errorString() != null){
                this.error = this.dp.errorString();
                return false;
            }
        }else this.ymin = (!this.is_image) ? Double.NEGATIVE_INFINITY : -1;
        if(this.is_image){
            if(this.in_timemax != null && (this.in_timemax.trim()).length() != 0){
                this.timemax = this.dp.getFloat(this.in_timemax);
                if(this.dp.errorString() != null){
                    this.error = this.dp.errorString();
                    return false;
                }
            }else this.timemax = Double.POSITIVE_INFINITY;
            if(this.in_timemin != null && (this.in_timemin.trim()).length() != 0){
                this.timemin = this.dp.getFloat(this.in_timemin);
                if(this.dp.errorString() != null){
                    this.error = this.dp.errorString();
                    return false;
                }
            }else this.timemin = Double.NEGATIVE_INFINITY;
        }
        if(DEBUG.D) System.out.println(">> Compute x label");
        if(this.in_xlabel != null && (this.in_xlabel.trim()).length() != 0){
            this.xlabel = this.dp.getString(this.in_xlabel);
            if(this.xlabel == null){
                this.error = this.dp.errorString();
                return false;
            }
        }
        if(DEBUG.D) System.out.println(">> Compute y label");
        if(this.in_ylabel != null && (this.in_ylabel.trim()).length() != 0){
            this.ylabel = this.dp.getString(this.in_ylabel);
            if(this.ylabel == null){
                this.error = this.dp.errorString();
                return false;
            }
        }
        if(this.xmin > this.xmax) this.xmin = this.xmax;
        if(this.ymin > this.ymax) this.ymin = this.ymax;
        return true;
    }

    public boolean updateShot(final long curr_shots[]) throws IOException {
        if(DEBUG.M) System.out.println("WaveInterface.UpdateShot(curr_shots[])");
        int curr_num_shot;
        if(curr_shots == null){
            curr_num_shot = 1;
            this.shots = null;
            if(this.num_shot == 0) return false;
        }else curr_num_shot = curr_shots.length;
        int num_signal;
        int num_expr;
        if(this.num_shot == 0){
            num_signal = this.num_waves * curr_num_shot;
            num_expr = this.num_waves;
        }else{
            num_signal = this.num_waves / this.num_shot * curr_num_shot;
            num_expr = this.num_waves / this.num_shot;
        }
        if(this.is_image){
            this.modified = true;
            this.shots = curr_shots;
            return true;
        }
        if(num_signal == 0) return false;
        this.modified = true;
        final String[] in_label = new String[num_signal];
        final String[] in_x = new String[num_signal];
        final String[] in_y = new String[num_signal];
        final String[] in_up_err = new String[num_signal];
        final String[] in_low_err = new String[num_signal];
        final int[] markers = new int[num_signal];
        final int[] markers_step = new int[num_signal];
        final int[] colors_idx = new int[num_signal];
        final boolean[] interpolates = new boolean[num_signal];
        final int[] mode2D = new int[num_signal];
        final int[] mode1D = new int[num_signal];
        final long[] shots;
        if(curr_shots == null) shots = null;
        else shots = new long[num_signal];
        final int sig_idx = (this.num_shot == 0) ? 1 : this.num_shot;
        for(int i = 0, k = 0; i < num_expr; i++){
            for(int j = 0; j < curr_num_shot; j++, k++){
                in_label[k] = this.in_label[i * sig_idx];
                in_x[k] = this.in_x[i * sig_idx];
                in_y[k] = this.in_y[i * sig_idx];
                if(j < this.num_shot){
                    markers[k] = this.markers[i * this.num_shot + j];
                    markers_step[k] = this.markers_step[i * this.num_shot + j];
                    interpolates[k] = this.interpolates[i * this.num_shot + j];
                    mode2D[k] = this.mode2D[i * this.num_shot + j];
                    mode1D[k] = this.mode1D[i * this.num_shot + j];
                    in_up_err[k] = this.in_up_err[i * this.num_shot + j];
                    in_low_err[k] = this.in_low_err[i * this.num_shot + j];
                    colors_idx[k] = this.colors_idx[i * this.num_shot + j];
                }else{
                    markers[k] = this.markers[i * this.num_shot];
                    markers_step[k] = this.markers_step[i * this.num_shot];
                    interpolates[k] = this.interpolates[i * this.num_shot];
                    mode2D[k] = this.mode2D[i * this.num_shot];
                    mode1D[k] = this.mode1D[i * this.num_shot];
                    in_up_err[k] = this.in_up_err[i * this.num_shot];
                    in_low_err[k] = this.in_low_err[i * this.num_shot];
                    if(WaveInterface.auto_color_on_expr) colors_idx[k] = i % Waveform.colors.length; // this.colors_idx[i * this.num_shot];
                    else colors_idx[k] = j % Waveform.colors.length;
                }
                if(shots != null) shots[k] = curr_shots[j];
            }
        }
        this.in_label = in_label;
        this.in_x = in_x;
        this.in_y = in_y;
        this.in_up_err = in_up_err;
        this.in_low_err = in_low_err;
        this.markers = markers;
        this.markers_step = markers_step;
        this.colors_idx = colors_idx;
        this.interpolates = interpolates;
        this.mode2D = mode2D;
        this.mode1D = mode1D;
        this.shots = shots;
        if(shots != null) this.num_shot = curr_num_shot;
        else this.num_shot = 1;
        this.num_waves = num_signal;
        return true;
    }
}
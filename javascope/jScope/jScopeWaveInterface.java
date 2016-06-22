package jscope;

/* $Id$ */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;
import debug.DEBUG;
import jscope.ColorMap.ColorProfile;

public final class jScopeWaveInterface extends WaveInterface{
    public static final int B_default_node = 9;
    public static final int B_event        = 17;
    public static final int B_exp          = 7;
    public static final int B_shot         = 8;
    public static final int B_title        = 16;
    public static final int B_update       = 0;
    public static final int B_x_label      = 10;
    public static final int B_x_max        = 13;
    public static final int B_x_min        = 12;
    public static final int B_y_label      = 11;
    public static final int B_y_max        = 15;
    public static final int B_y_min        = 14;

    static public String containMainShot(final String in_shot, final String m_shot) {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.containMainShot(\"" + in_shot + "\", \"" + m_shot + "\")");
        int idx;
        String out = in_shot;
        if((in_shot != null) && (idx = in_shot.indexOf("#")) != -1){
            if(m_shot != null) out = "[" + in_shot.substring(0, idx) + m_shot + in_shot.substring(idx + 1) + "]";
            else out = "[" + in_shot.substring(0, idx) + "[]" + in_shot.substring(idx + 1) + "]";
        }
        return out;
    }

    private static final boolean getBoolean(final String prop, final boolean def) {
        try{
            return (prop == null) ? def : Boolean.parseBoolean(prop);
        }catch(final Exception e){
            return def;
        }
    }

    private static final int getInteger(final String prop, final int def) {
        try{
            return (prop == null) ? def : Integer.parseInt(prop);
        }catch(final Exception e){
            return def;
        }
    }

    public static String mode1DCodeToString(final int code) {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.mode1DCodeToString(" + code + ")");
        switch(code){
            case Signal.MODE_LINE:
                return "Line";
            case Signal.MODE_NOLINE:
                return "Noline";
            case Signal.MODE_STEP:
                return "Step";
        }
        return "";
    }

    public static int mode1DStringToCode(final String mode) {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.mode1DStringToCode(\"" + mode + "\")");
        if(mode.equals("Line")) return Signal.MODE_LINE;
        if(mode.equals("Noline")) return Signal.MODE_NOLINE;
        if(mode.equals("Step")) return Signal.MODE_STEP;
        return 0;
    }

    public static String mode2DCodeToString(final int code) {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.mode2DCodeToString(" + code + ")");
        switch(code){
            case Signal.MODE_XZ:
                return "xz(y)";
            case Signal.MODE_YZ:
                return "yz(x)";
            case Signal.MODE_CONTOUR:
                return "Contour";
            case Signal.MODE_IMAGE:
                return "Image";
        }
        return "";
    }

    public static int mode2DStringToCode(final String mode) {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.mode2DStringToCode(\"" + mode + "\")");
        if(mode.equals("xz(y)")) return Signal.MODE_XZ;
        if(mode.equals("yz(x)")) return Signal.MODE_YZ;
        if(mode.equals("Contour")) return Signal.MODE_CONTOUR;
        if(mode.equals("Image")) return Signal.MODE_IMAGE;
        return 0;
    }
    public String              cin_def_node, cin_upd_event, cexperiment;
    public String              cin_shot;
    public String              cin_title, cin_xlabel, cin_ylabel;
    public boolean             cin_upd_limits    = true;
    // Configuration parameter
    public String              cin_xmin, cin_xmax, cin_ymax, cin_ymin, cin_timemax, cin_timemin;
    public jScopeDefaultValues def_vals;
    public boolean             default_is_update = true;
    public int                 defaults          = 0xffffffff;
    public String              in_upd_event, last_upd_event;
    public jScopeWaveInterface prev_wi           = null;
    public String              previous_shot     = "";

    public jScopeWaveInterface(final jScopeWaveInterface wi){
        if(DEBUG.M) System.out.println("jScopeWaveInterface(" + wi + ")");
        this.previous_shot = wi.previous_shot;
        this.cache_enabled = wi.cache_enabled;
        this.provider = wi.provider;
        this.num_waves = wi.num_waves;
        this.num_shot = wi.num_shot;
        this.defaults = wi.defaults;
        this.setModified(wi.getModified());
        this.in_grid_mode = wi.in_grid_mode;
        this.x_log = wi.x_log;
        this.y_log = wi.y_log;
        this.is_image = wi.is_image;
        this.keep_ratio = wi.keep_ratio;
        this.vertical_flip = wi.vertical_flip;
        this.horizontal_flip = wi.horizontal_flip;
        this.show_legend = wi.show_legend;
        this.reversed = wi.reversed;
        this.legend_x = wi.legend_x;
        this.legend_y = wi.legend_y;
        this.setColorProfile(wi.getColorProfile());
        this.in_label = new String[this.num_waves];
        this.in_x = new String[this.num_waves];
        this.in_y = new String[this.num_waves];
        this.in_up_err = new String[this.num_waves];
        this.in_low_err = new String[this.num_waves];
        this.markers = new int[this.num_waves];
        this.markers_step = new int[this.num_waves];
        this.colors_idx = new int[this.num_waves];
        this.interpolates = new boolean[this.num_waves];
        this.mode2D = new int[this.num_waves];
        this.mode1D = new int[this.num_waves];
        this.w_error = new String[this.num_waves];
        this.evaluated = new boolean[this.num_waves];
        this.signals = new Signal[this.num_waves];
        if(wi.in_shot == null || wi.in_shot.trim().length() == 0) this.shots = wi.shots = null;
        else this.shots = new long[this.num_waves];
        for(int i = 0; i < this.num_waves; i++){
            if(wi.in_label[i] != null) this.in_label[i] = new String(wi.in_label[i]);
            else this.in_label[i] = null;
            if(wi.in_x[i] != null) this.in_x[i] = new String(wi.in_x[i]);
            else this.in_x[i] = null;
            if(wi.in_y[i] != null) this.in_y[i] = new String(wi.in_y[i]);
            else this.in_y[i] = null;
            if(wi.in_up_err[i] != null) this.in_up_err[i] = new String(wi.in_up_err[i]);
            else this.in_up_err[i] = null;
            if(wi.in_low_err[i] != null) this.in_low_err[i] = new String(wi.in_low_err[i]);
            else this.in_low_err[i] = null;
        }
        for(int i = 0; i < this.num_waves; i++){
            this.markers[i] = wi.markers[i];
            this.markers_step[i] = wi.markers_step[i];
            this.colors_idx[i] = wi.colors_idx[i];
            this.interpolates[i] = wi.interpolates[i];
            this.mode2D[i] = wi.mode2D[i];
            this.mode1D[i] = wi.mode1D[i];
            if(wi.shots != null) this.shots[i] = wi.shots[i];
            /*****/
            if(wi.evaluated != null) this.evaluated[i] = wi.evaluated[i];
            else this.evaluated[i] = false;
            if(wi.signals != null) this.signals[i] = wi.signals[i];
            if(wi.w_error != null) this.w_error[i] = wi.w_error[i];
            /*****/
        }
        this.in_upd_limits = wi.in_upd_limits;
        if(wi.in_xmin != null) this.in_xmin = new String(wi.in_xmin);
        else this.in_xmin = null;
        if(wi.in_ymin != null) this.in_ymin = new String(wi.in_ymin);
        else this.in_ymin = null;
        if(wi.in_xmax != null) this.in_xmax = new String(wi.in_xmax);
        else this.in_xmax = null;
        if(wi.in_ymax != null) this.in_ymax = new String(wi.in_ymax);
        else this.in_ymax = null;
        if(wi.in_timemax != null) this.in_timemax = new String(wi.in_timemax);
        else this.in_timemax = null;
        if(wi.in_timemin != null) this.in_timemin = new String(wi.in_timemin);
        else this.in_timemin = null;
        if(wi.in_shot != null) this.in_shot = new String(wi.in_shot);
        else this.in_shot = null;
        if(wi.experiment != null) this.experiment = new String(wi.experiment);
        else this.experiment = null;
        if(wi.in_title != null) this.in_title = new String(wi.in_title);
        else this.in_title = null;
        if(wi.in_xlabel != null) this.in_xlabel = new String(wi.in_xlabel);
        else this.in_xlabel = null;
        if(wi.in_ylabel != null) this.in_ylabel = new String(wi.in_ylabel);
        else this.in_ylabel = null;
        if(wi.in_def_node != null) this.in_def_node = new String(wi.in_def_node);
        else this.in_def_node = null;
        this.cin_upd_limits = wi.cin_upd_limits;
        if(wi.cin_xmin != null) this.cin_xmin = new String(wi.cin_xmin);
        else this.cin_xmin = null;
        if(wi.cin_ymin != null) this.cin_ymin = new String(wi.cin_ymin);
        else this.cin_ymin = null;
        if(wi.cin_xmax != null) this.cin_xmax = new String(wi.cin_xmax);
        else this.cin_xmax = null;
        if(wi.cin_ymax != null) this.cin_ymax = new String(wi.cin_ymax);
        else this.cin_ymax = null;
        if(wi.cin_timemax != null) this.cin_timemax = new String(wi.cin_timemax);
        else this.cin_timemax = null;
        if(wi.cin_timemin != null) this.cin_timemin = new String(wi.cin_timemin);
        else this.cin_timemin = null;
        if(wi.cin_shot != null) this.cin_shot = new String(wi.cin_shot);
        else this.cin_shot = null;
        if(wi.cexperiment != null) this.cexperiment = new String(wi.cexperiment);
        else this.cexperiment = null;
        if(wi.cin_title != null) this.cin_title = new String(wi.cin_title);
        else this.cin_title = null;
        if(wi.cin_xlabel != null) this.cin_xlabel = new String(wi.cin_xlabel);
        else this.cin_xlabel = null;
        if(wi.cin_ylabel != null) this.cin_ylabel = new String(wi.cin_ylabel);
        else this.cin_ylabel = null;
        if(wi.error != null) this.error = new String(wi.error);
        else this.error = null;
        if(wi.cin_upd_event != null) this.cin_upd_event = new String(wi.cin_upd_event);
        else this.cin_upd_event = null;
        if(wi.cin_def_node != null) this.cin_def_node = new String(wi.cin_def_node);
        else this.cin_def_node = null;
        this.def_vals = wi.def_vals;
        this.setDataProvider(wi.dp);
    }

    public jScopeWaveInterface(final Waveform wave, final DataProvider dp, final jScopeDefaultValues def_vals, final boolean enable_cache){
        super(dp);
        if(DEBUG.M) System.out.println("jScopeWaveInterface(" + wave + ", " + dp + ", " + def_vals + ", " + enable_cache + ")");
        this.setDefaultsValues(def_vals);
        this.enableCache(enable_cache);
        this.wave = wave;
    }

    public void addEvent(final UpdateEventListener w) throws IOException {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.AddEvent(" + w + ")");
        final int bit = jScopeWaveInterface.B_event;
        final boolean def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        final String new_event = this.getDefaultValue(bit, def_flag);
        if(this.in_upd_event == null || !this.in_upd_event.equals(new_event)){
            this.addEvent(w, new_event);
        }
    }

    public void addEvent(final UpdateEventListener w, final String event) throws IOException {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.AddEvent(" + w + ", \"" + event + "\")");
        if(this.in_upd_event != null && this.in_upd_event.length() != 0){
            if(event == null || event.length() == 0){
                this.dp.removeUpdateEventListener(w, this.in_upd_event);
                this.in_upd_event = null;
            }else{
                if(!this.in_upd_event.equals(event)){
                    this.dp.removeUpdateEventListener(w, this.in_upd_event);
                    this.dp.addUpdateEventListener(w, event);
                    this.in_upd_event = event;
                }
            }
        }else if(event != null && event.length() != 0){
            this.dp.addUpdateEventListener(w, event);
            this.in_upd_event = event;
        }
    }

    public void createVector() {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.CreateVector()");
        this.in_label = new String[this.num_waves];
        this.shots = new long[this.num_waves];
        this.in_y = new String[this.num_waves];
        this.in_x = new String[this.num_waves];
        this.in_up_err = new String[this.num_waves];
        this.in_low_err = new String[this.num_waves];
        this.markers = new int[this.num_waves];
        this.markers_step = new int[this.num_waves];
        this.colors_idx = new int[this.num_waves];
        this.interpolates = new boolean[this.num_waves];
        this.mode2D = new int[this.num_waves];
        this.mode1D = new int[this.num_waves];
        for(int i = 0; i < this.num_waves; i++){
            this.markers[i] = 0;
            this.markers_step[i] = 1;
            this.colors_idx[i] = i % Waveform.colors.length;
            this.interpolates[i] = true;
            this.mode2D[i] = Signal.MODE_XZ;
            this.mode1D[i] = Signal.MODE_LINE;
        }
    }

    @Override
    public void erase() {
        super.erase();
        if(DEBUG.M) System.out.println("jScopeWaveInterface.Erase()");
        this.in_def_node = null;
        this.in_upd_event = null;
        this.last_upd_event = null;
        this.cin_xmin = null;
        this.cin_xmax = null;
        this.cin_ymax = null;
        this.cin_ymin = null;
        this.cin_timemax = null;
        this.cin_timemin = null;
        this.cin_title = null;
        this.cin_xlabel = null;
        this.cin_ylabel = null;
        this.cin_def_node = null;
        this.cin_upd_event = null;
        this.cexperiment = null;
        this.in_shot = null;
        this.cin_shot = null;
        this.defaults = 0xffffffff;
        this.default_is_update = false;
        this.previous_shot = "";
        this.colorProfile = null;
        this.cin_upd_limits = true;
    }

    public void fromFile(final Properties pr, final String prompt, final ColorMapDialog cmd) throws IOException {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.mode2DStringToCode(" + pr + ", \"" + prompt + "\", " + cmd + ")");
        String prop = null;
        int num_expr = 0;
        this.erase();
        try{
            prop = pr.getProperty(prompt + ".height");
            if(prop != null) this.height = Integer.parseInt(prop);
            prop = pr.getProperty(prompt + ".grid_mode");
            this.in_grid_mode = jScopeWaveInterface.getInteger(prop, 0);
            this.cin_xlabel = pr.getProperty(prompt + ".x_label");
            this.cin_ylabel = pr.getProperty(prompt + ".y_label");
            this.cin_title = pr.getProperty(prompt + ".title");
            this.cin_ymin = pr.getProperty(prompt + ".ymin");
            this.cin_ymax = pr.getProperty(prompt + ".ymax");
            this.cin_xmin = pr.getProperty(prompt + ".xmin");
            this.cin_xmax = pr.getProperty(prompt + ".xmax");
            this.cin_timemin = pr.getProperty(prompt + ".time_min");
            this.cin_timemax = pr.getProperty(prompt + ".time_max");
            this.cin_def_node = pr.getProperty(prompt + ".default_node");
            this.cin_upd_event = pr.getProperty(prompt + ".event");
            final String continuousUpdateStr = pr.getProperty(prompt + ".continuous_update");
            if(continuousUpdateStr != null && continuousUpdateStr.trim().equals("1")) this.isContinuousUpdate = true;
            else this.isContinuousUpdate = false;
            prop = pr.getProperty(prompt + ".x_log");
            this.x_log = jScopeWaveInterface.getBoolean(prop, false);
            prop = pr.getProperty(prompt + ".y_log");
            this.y_log = jScopeWaveInterface.getBoolean(prop, false);
            prop = pr.getProperty(prompt + ".update_limits");
            this.cin_upd_limits = jScopeWaveInterface.getBoolean(prop, false);
            prop = pr.getProperty(prompt + ".legend");
            if(prop != null){
                this.show_legend = true;
                this.legend_x = Double.valueOf(prop.substring(prop.indexOf("(") + 1, prop.indexOf(","))).doubleValue();
                this.legend_y = Double.valueOf(prop.substring(prop.indexOf(",") + 1, prop.indexOf(")"))).doubleValue();
            }
            prop = pr.getProperty(prompt + ".is_image");
            this.is_image = jScopeWaveInterface.getBoolean(prop, false);
            prop = pr.getProperty(prompt + ".keep_ratio");
            this.keep_ratio = jScopeWaveInterface.getBoolean(prop, false);
            prop = pr.getProperty(prompt + ".horizontal_flip");
            this.horizontal_flip = jScopeWaveInterface.getBoolean(prop, false);
            prop = pr.getProperty(prompt + ".vertical_flip");
            this.vertical_flip = jScopeWaveInterface.getBoolean(prop, false);
            prop = pr.getProperty(prompt + ".palette");
            this.colorProfile = (prop == null) ? new ColorProfile() : new ColorProfile(cmd.getColorMap(prop));
            prop = pr.getProperty(prompt + ".bitShift");
            this.colorProfile.bitShift = jScopeWaveInterface.getInteger(prop, 0);
            prop = pr.getProperty(prompt + ".bitClip");
            this.colorProfile.bitClip = jScopeWaveInterface.getBoolean(prop, false);
            prop = pr.getProperty(prompt + ".useRGB");
            this.colorProfile.useRGB = jScopeWaveInterface.getBoolean(prop, false);
            this.cexperiment = pr.getProperty(prompt + ".experiment");
            this.cin_shot = pr.getProperty(prompt + ".shot");
            prop = pr.getProperty(prompt + ".x");
            if(prop != null){
                String x_str = prop;
                x_str = WaveInterface.removeNewLineCode(x_str);
                if(this.in_x == null || this.in_x.length == 0) this.in_x = new String[1];
                this.in_x[0] = x_str;
            }
            prop = pr.getProperty(prompt + ".y");
            if(prop != null){
                StringTokenizer st_y = null;
                StringTokenizer st_x = null;
                this.num_shot = 1;
                String token = null;
                String y_str = prop.toLowerCase();
                String x_str = null;
                y_str = WaveInterface.removeNewLineCode(y_str);
                if(this.in_x != null && this.in_x.length != 0) x_str = this.in_x[0];
                if(y_str.indexOf("[") == 0 && y_str.indexOf("$roprand") != -1){
                    try{
                        // Parse [expression1,$roprand,expression2,....,$roprand,expressionN]
                        st_y = new StringTokenizer(y_str, "[]");
                        token = st_y.nextToken();
                        st_y = new StringTokenizer(token, ",");
                        while(st_y.hasMoreTokens()){
                            token = st_y.nextToken().trim();
                            if(token.indexOf("$roprand") != -1) continue;
                            num_expr++;
                        }
                    }catch(final Exception e){
                        num_expr = 1;
                    }
                }else num_expr = 1;
                this.num_waves = num_expr * this.num_shot;
                this.createVector();
                if(num_expr > 1){
                    try{
                        st_y = new StringTokenizer(y_str, "[]");
                        String token_y = st_y.nextToken();
                        st_y = new StringTokenizer(token_y, ",");
                        if(x_str != null){
                            st_x = new StringTokenizer(x_str, "[]");
                            token = st_x.nextToken();
                            st_x = new StringTokenizer(token, ",");
                        }
                        int i = 0;
                        while(st_y.hasMoreTokens()){
                            token_y = st_y.nextToken().trim();
                            if(st_x != null) token = st_x.nextToken().trim();
                            if(token_y.indexOf("$roprand") != -1) continue;
                            this.in_y[i] = token_y;
                            if(st_x != null) this.in_x[i] = token;
                            if(this.in_y[i].indexOf("multitrace") != -1){
                                this.in_y[i] = "compile" + this.in_y[i].substring(this.in_y[i].indexOf("("));
                            }
                            i++;
                        }
                    }catch(final Exception e){
                        num_expr = 1;
                        this.num_waves = num_expr * this.num_shot;
                        this.createVector();
                    }
                }
                if(num_expr == 1){
                    this.in_y[0] = y_str;
                    if(this.in_y[0].indexOf("multitrace") != -1){
                        this.in_y[0] = "compile" + y_str.substring(y_str.indexOf("("));
                        // in_y[0] = in_y[0].substring(in_y[0].indexOf("\"") + 1, in_y[0].indexOf("\")"));
                    }
                    if(x_str != null) this.in_x[0] = new String(x_str);
                    else this.in_x[0] = null;
                }
            }
            prop = pr.getProperty(prompt + ".num_expr");
            num_expr = jScopeWaveInterface.getInteger(prop, 0);
            prop = pr.getProperty(prompt + ".num_shot");
            if(prop != null){
                this.num_shot = new Integer(prop).intValue();
                this.num_shot = (this.num_shot > 0) ? this.num_shot : 1;
                if(num_expr != 0){
                    this.num_waves = num_expr * this.num_shot;
                    this.createVector();
                }
            }
            prop = pr.getProperty(prompt + ".global_defaults");
            this.defaults = jScopeWaveInterface.getInteger(prop, 0);
            int expr_idx;
            for(int idx = 1; idx <= num_expr; idx++){
                prop = pr.getProperty(prompt + ".label_" + idx);
                if(prop != null){
                    expr_idx = (idx - 1) * this.num_shot;
                    this.in_label[expr_idx] = prop;
                    for(int j = 1; j < this.num_shot; j++)
                        this.in_label[expr_idx + j] = prop;
                }
                prop = pr.getProperty(prompt + ".x_expr_" + idx);
                if(prop != null){
                    expr_idx = (idx - 1) * this.num_shot;
                    this.in_x[expr_idx] = WaveInterface.removeNewLineCode(prop);
                    for(int j = 1; j < this.num_shot; j++)
                        this.in_x[expr_idx + j] = this.in_x[expr_idx];
                }
                prop = pr.getProperty(prompt + ".y_expr_" + idx);
                if(prop != null){
                    expr_idx = (idx - 1) * this.num_shot;
                    this.in_y[expr_idx] = WaveInterface.removeNewLineCode(prop);
                    for(int j = 1; j < this.num_shot; j++)
                        this.in_y[expr_idx + j] = this.in_y[expr_idx];
                }
                prop = pr.getProperty(prompt + ".up_error_" + idx);
                if(prop != null){
                    expr_idx = (idx - 1) * this.num_shot;
                    this.in_up_err[expr_idx] = prop;
                    for(int j = 1; j < this.num_shot; j++)
                        this.in_up_err[expr_idx + j] = prop;
                }
                prop = pr.getProperty(prompt + ".in_low_err_" + idx);
                if(prop != null){
                    expr_idx = (idx - 1) * this.num_shot;
                    this.in_low_err[expr_idx] = prop;
                    for(int j = 1; j < this.num_shot; j++)
                        this.in_low_err[expr_idx + j] = prop;
                }
                for(int s = 1; s <= this.num_shot; s++){
                    expr_idx = (idx - 1) * this.num_shot - 1;
                    prop = pr.getProperty(prompt + ".mode_1D_" + idx + "_" + s);
                    this.mode1D[expr_idx + s] = (prop == null) ? 0 : jScopeWaveInterface.mode1DStringToCode(prop);
                    prop = pr.getProperty(prompt + ".mode_2D_" + idx + "_" + s);
                    this.mode2D[expr_idx + s] = (prop == null) ? 0 : jScopeWaveInterface.mode2DStringToCode(prop);
                    prop = pr.getProperty(prompt + ".color_" + idx + "_" + s);
                    this.colors_idx[expr_idx + s] = jScopeWaveInterface.getInteger(prop, 0);
                    prop = pr.getProperty(prompt + ".marker_" + idx + "_" + s);
                    this.markers[expr_idx + s] = jScopeWaveInterface.getInteger(prop, 0);
                    prop = pr.getProperty(prompt + ".step_marker_" + idx + "_" + s);
                    this.markers_step[expr_idx + s] = jScopeWaveInterface.getInteger(prop, 0);
                }
            }
        }catch(final Exception e){
            throw(new IOException(e + " \n when parsing " + prop));
        }
    }

    public String getDefaultValue(final int i, final boolean def_flag) {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.GetDefaultValue(" + i + ", " + def_flag + ")");
        String out = null;
        switch(i){
            case jScopeWaveInterface.B_title:
                out = def_flag ? this.def_vals.title_str : this.cin_title;
                break;
            case jScopeWaveInterface.B_shot:
                out = def_flag ? this.def_vals.shot_str : this.cin_shot;
                break;
            case jScopeWaveInterface.B_exp:
                out = def_flag ? this.def_vals.experiment_str : this.cexperiment;
                break;
            case jScopeWaveInterface.B_x_max:
                if(this.is_image) out = def_flag ? this.def_vals.xmax : this.cin_timemax;
                else out = def_flag ? this.def_vals.xmax : this.cin_xmax;
                break;
            case jScopeWaveInterface.B_x_min:
                if(this.is_image) out = def_flag ? this.def_vals.xmin : this.cin_timemin;
                else out = def_flag ? this.def_vals.xmin : this.cin_xmin;
                break;
            case jScopeWaveInterface.B_x_label:
                out = def_flag ? this.def_vals.xlabel : this.cin_xlabel;
                break;
            case jScopeWaveInterface.B_y_max:
                out = def_flag ? this.def_vals.ymax : this.cin_ymax;
                break;
            case jScopeWaveInterface.B_y_min:
                out = def_flag ? this.def_vals.ymin : this.cin_ymin;
                break;
            case jScopeWaveInterface.B_y_label:
                out = def_flag ? this.def_vals.ylabel : this.cin_ylabel;
                break;
            case jScopeWaveInterface.B_event:
                out = def_flag ? this.def_vals.upd_event_str : this.cin_upd_event;
                break;
            case jScopeWaveInterface.B_default_node:
                out = def_flag ? this.def_vals.def_node_str : this.cin_def_node;
                break;
            case jScopeWaveInterface.B_update:
                out = def_flag ? "" + this.def_vals.upd_limits : "" + this.cin_upd_limits;
                break;
        }
        return out;
    }

    public String getErrorString() // boolean brief_error)
    {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.getErrorString()");
        String full_error = null;
        if(this.w_error == null || this.w_error.length == 0) return null;
        if(!this.is_image){
            int i;
            int idx = 0;
            String e;
            if(this.isAddSignal()){
                // Return error only for added signals
                i = this.num_waves - this.num_shot;
                if(i < 0) i = 0;
            }else i = 0;
            for(; i < this.w_error.length; i++){
                e = this.w_error[i];
                if(e != null){
                    if(!this.isAddSignal()) e = "<Wave " + (i + 1) + "> " + e;
                    if(WaveInterface.brief_error){
                        idx = e.indexOf('\n');
                        if(idx < 0) idx = e.length();
                    }
                    if(full_error == null){
                        if(WaveInterface.brief_error) full_error = e.substring(0, idx) + "\n";
                        else full_error = e + "\n";
                    }else{
                        if(WaveInterface.brief_error) full_error = full_error + e.substring(0, idx) + "\n";
                        else full_error = full_error + e + "\n";
                    }
                }
            }
        }
        if(full_error == null && this.error != null) if(WaveInterface.brief_error && this.error.indexOf("\n") != -1) full_error = this.error.substring(0, this.error.indexOf("\n"));
        else full_error = this.error;
        return full_error;
    }

    /**
     * Check which shot string the wave interface
     * used:
     * 0 wave setup defined shot;
     * 1 global setting defined shot
     * 2 main scope defined shot
     */
    public int getShotIdx() {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.GetShotIdx()");
        final String main_shot_str = ((jScopeWaveContainer)(this.wave.getParent())).getMainShotStr();
        if(this.useDefaultShot()){
            if(main_shot_str != null && main_shot_str.length() != 0) return 2;
            return 1;
        }
        return 0;
    }

    public String getUsedShot() {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.GetUsedShot()");
        String out = null;
        switch(this.getShotIdx()){
            case 0:
                out = this.cin_shot;
                break;
            case 1:
                out = this.def_vals.shot_str;
                break;
            case 2:
                out = ((jScopeWaveContainer)(this.wave.getParent())).getMainShotStr();
                break;
        }
        return out;
    }

    public void mapColorIndex(final int colorMap[]) {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.mapColorIndex(" + colorMap + ")");
        if(colorMap == null) return;
        try{
            for(int i = 0; i < this.colors_idx.length; i++){
                this.colors_idx[i] = colorMap[this.colors_idx[i]];
            }
        }catch(final Exception e){}
    }

    @Override
    public synchronized void refresh() throws Exception {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.refresh()");
        try{
            this.error = this.update();
            if(this.error == null){
                if(this.getModified()){
                    this.startEvaluate();
                    if(this.error == null) this.evaluateOthers();
                }
                this.setModified(this.error != null);
            }
        }catch(final IOException e){
            this.setModified(true);
            this.error = e.getMessage();
        }
    }

    public void removeEvent(final UpdateEventListener w) throws IOException {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.RemoveEvent(" + w + ")");
        if(this.in_upd_event != null){
            this.dp.removeUpdateEventListener(w, this.in_upd_event);
            this.in_upd_event = null;
        }
    }

    public void removeEvent(final UpdateEventListener w, final String event) throws IOException {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.RemoveEvent(" + w + ", \"" + event + "\")");
        this.dp.removeUpdateEventListener(w, event);
    }

    @Override
    public void setDataProvider(final DataProvider _dp) {
        super.setDataProvider(_dp);
        if(DEBUG.M) System.out.println("jScopeWaveInterface.SetDataProvider(" + _dp + ")");
        this.default_is_update = false;
        this.previous_shot = "";
    }

    public void setDefaultsValues(final jScopeDefaultValues def_vals) {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.setDefaultsValues(" + def_vals + ")");
        this.def_vals = def_vals;
        this.default_is_update = false;
        this.def_vals.setIsEvaluated(false);
    }

    @Override
    public void setExperiment(final String experiment) {
        super.setExperiment(experiment);
        if(DEBUG.M){
            System.out.println("jScopeWaveInterface.setExperiment(\"" + experiment + "\")");
        }
        this.cexperiment = experiment;
        // Remove default
        this.defaults &= ~(1 << jScopeWaveInterface.B_exp);
    }

    public void toFile(final PrintWriter out, final String prompt) throws IOException {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.ToFile(" + out + ", \"" + prompt + "\")");
        int exp, exp_n, sht, sht_n, cnum_shot, eval_shot = 1;
        cnum_shot = this.num_shot;
        /*
        if (UseDefaultShot())
        {
            if (cin_shot != null && cin_shot.length() > 0)
            {
                cnum_shot = (GetShotArray(cin_shot)).length;
            }
            else
            {
                cnum_shot = 1;
            }
        }
         */
        WaveInterface.writeLine(out, prompt + "x_label: ", this.cin_xlabel);
        WaveInterface.writeLine(out, prompt + "y_label: ", this.cin_ylabel);
        if(!this.is_image){
            WaveInterface.writeLine(out, prompt + "x_log: ", "" + this.x_log);
            WaveInterface.writeLine(out, prompt + "y_log: ", "" + this.y_log);
            WaveInterface.writeLine(out, prompt + "update_limits: ", "" + this.cin_upd_limits);
            if(this.show_legend){
                WaveInterface.writeLine(out, prompt + "legend: ", "(" + this.legend_x + "," + this.legend_y + ")");
            }
        }else{
            WaveInterface.writeLine(out, prompt + "is_image: ", "" + this.is_image);
            WaveInterface.writeLine(out, prompt + "keep_ratio: ", "" + this.keep_ratio);
            WaveInterface.writeLine(out, prompt + "horizontal_flip: ", "" + this.horizontal_flip);
            WaveInterface.writeLine(out, prompt + "vertical_flip: ", "" + this.vertical_flip);
        }
        if(this.colorProfile != null){
            if(this.colorProfile.useRGB) WaveInterface.writeLine(out, prompt + "useRGB: ", "" + this.colorProfile.useRGB);
            else{
                WaveInterface.writeLine(out, prompt + "palette: ", "" + this.colorProfile.colorMap.name);
                WaveInterface.writeLine(out, prompt + "bitShift: ", "" + this.colorProfile.bitShift);
                WaveInterface.writeLine(out, prompt + "bitClip: ", "" + this.colorProfile.bitClip);
            }
        }
        WaveInterface.writeLine(out, prompt + "experiment: ", this.cexperiment);
        WaveInterface.writeLine(out, prompt + "event: ", this.cin_upd_event);
        WaveInterface.writeLine(out, prompt + "default_node: ", this.cin_def_node);
        WaveInterface.writeLine(out, prompt + "num_shot: ", "" + cnum_shot);
        if(cnum_shot != 0){
            if(this.useDefaultShot()) eval_shot = this.num_shot > 0 ? this.num_shot : 1;
            else eval_shot = cnum_shot;
            WaveInterface.writeLine(out, prompt + "num_expr: ", "" + this.num_waves / eval_shot);
        }else{
            WaveInterface.writeLine(out, prompt + "num_expr: ", "" + this.num_waves);
            // Shot is not defined in the pannel.
            // cnum_shot must be set to 1 to save, in the configuration file,
            // signal view parameters
            cnum_shot = 1;
        }
        WaveInterface.writeLine(out, prompt + "shot: ", this.cin_shot);
        WaveInterface.writeLine(out, prompt + "ymin: ", this.cin_ymin);
        WaveInterface.writeLine(out, prompt + "ymax: ", this.cin_ymax);
        WaveInterface.writeLine(out, prompt + "xmin: ", this.cin_xmin);
        WaveInterface.writeLine(out, prompt + "xmax: ", this.cin_xmax);
        if(this.is_image){
            WaveInterface.writeLine(out, prompt + "time_min: ", this.cin_timemin);
            WaveInterface.writeLine(out, prompt + "time_max: ", this.cin_timemax);
        }
        // GAB 2014
        WaveInterface.writeLine(out, prompt + "continuous_update: ", this.isContinuousUpdate ? "1" : "0");
        // ///////
        WaveInterface.writeLine(out, prompt + "title: ", this.cin_title);
        WaveInterface.writeLine(out, prompt + "global_defaults: ", "" + this.defaults);
        for(exp = 0, exp_n = 1; exp < this.num_waves; exp += eval_shot, exp_n++){
            WaveInterface.writeLine(out, prompt + "label" + "_" + exp_n + ": ", this.in_label[exp]);
            // add blank at the end of expression to fix bug when last expression character is \
            WaveInterface.writeLine(out, prompt + "x_expr" + "_" + exp_n + ": ", WaveInterface.addNewLineCode(this.in_x[exp]));
            WaveInterface.writeLine(out, prompt + "y_expr" + "_" + exp_n + ": ", WaveInterface.addNewLineCode(this.in_y[exp]));
            if(!this.is_image){
                WaveInterface.writeLine(out, prompt + "up_error" + "_" + exp_n + ": ", this.in_up_err[exp]);
                WaveInterface.writeLine(out, prompt + "low_error" + "_" + exp_n + ": ", this.in_low_err[exp]);
                for(sht = 0, sht_n = 1; sht < cnum_shot; sht++, sht_n++){
                    WaveInterface.writeLine(out, prompt + "mode_1D" + "_" + exp_n + "_" + sht_n + ": ", "" + jScopeWaveInterface.mode1DCodeToString(this.mode1D[exp + sht]));
                    WaveInterface.writeLine(out, prompt + "mode_2D" + "_" + exp_n + "_" + sht_n + ": ", "" + jScopeWaveInterface.mode2DCodeToString(this.mode2D[exp + sht]));
                    WaveInterface.writeLine(out, prompt + "color" + "_" + exp_n + "_" + sht_n + ": ", "" + this.colors_idx[exp + sht]);
                    WaveInterface.writeLine(out, prompt + "marker" + "_" + exp_n + "_" + sht_n + ": ", "" + this.markers[exp + sht]);
                    WaveInterface.writeLine(out, prompt + "step_marker" + "_" + exp_n + "_" + sht_n + ": ", "" + this.markers_step[exp + sht]);
                }
            }
        }
    }

    public String update() throws IOException {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.Update()");
        final int mode = this.wave.getMode();
        try{
            this.wave.setMode(Waveform.MODE_WAIT);
            this.updateShot();
            if(this.error == null){
                this.updateDefault();
                /* ces 2015
                if (in_def_node != null)
                {
                    String def = in_def_node;
                    if (in_def_node.indexOf("\\") == 0)
                        def = "\\\\\\" + in_def_node;
                    dp.SetEnvironment("__default_node = " + def);
                }
                 */
            }else{
                this.signals = null;
            }
            this.wave.setMode(mode);
        }catch(final IOException exc){
            this.wave.setMode(mode);
            throw(exc);
        }
        return this.error;
    }

    public void updateDefault() {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.UpdateDefault()");
        boolean def_flag;
        int bit;
        if(this.default_is_update) return;
        this.default_is_update = true;
        bit = jScopeWaveInterface.B_title;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        this.in_title = this.getDefaultValue(bit, def_flag);
        /*
        bit = jScopeWaveInterface.B_shot;
        def_flag =    ((defaults & (1<<bit)) == 1<<bit);
        in_shot       = GetDefaultValue(bit ,  def_flag);
         */
        bit = jScopeWaveInterface.B_exp;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        this.experiment = this.getDefaultValue(bit, def_flag);
        bit = jScopeWaveInterface.B_x_max;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        if(this.is_image){
            this.in_timemax = this.getDefaultValue(bit, def_flag);
            this.in_xmax = this.cin_xmax;
        }else this.in_xmax = this.getDefaultValue(bit, def_flag);
        bit = jScopeWaveInterface.B_x_min;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        if(this.is_image){
            this.in_timemin = this.getDefaultValue(bit, def_flag);
            this.in_xmin = this.cin_xmin;
        }else this.in_xmin = this.getDefaultValue(bit, def_flag);
        bit = jScopeWaveInterface.B_x_label;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        this.in_xlabel = this.getDefaultValue(bit, def_flag);
        bit = jScopeWaveInterface.B_y_max;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        this.in_ymax = this.getDefaultValue(bit, def_flag);
        bit = jScopeWaveInterface.B_y_min;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        this.in_ymin = this.getDefaultValue(bit, def_flag);
        bit = jScopeWaveInterface.B_y_label;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        this.in_ylabel = this.getDefaultValue(bit, def_flag);
        bit = jScopeWaveInterface.B_default_node;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        this.in_def_node = this.getDefaultValue(bit, def_flag);
        bit = jScopeWaveInterface.B_update;
        def_flag = ((this.defaults & (1 << bit)) == 1 << bit);
        this.in_upd_limits = (new Boolean(this.getDefaultValue(bit, def_flag))).booleanValue();
        /*
        bit = jScopeWaveInterface.B_event;
        def_flag =    ((defaults & (1<<bit)) == 1<<bit);
        in_upd_event = getDefaultValue(bit , def_flag );
         */
    }

    public void updateShot() throws IOException {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.UpdateShot()");
        long curr_shots[] = null;
        final String main_shot_str = ((jScopeWaveContainer)(this.wave.getParent())).getMainShotStr();
        final String c_shot_str = jScopeWaveInterface.containMainShot(this.getUsedShot(), main_shot_str);
        /* 12-2-2009
                if( !getModified() && in_shot != null && c_shot_str != null)
                {
                    setModified( !in_shot.equals( c_shot_str ) );
                    if(! getModified() )
                        return;
                }
         */
        this.error = null;
        /*
        Fix bug : shot expression must be always evaluated.
                if (c_shot_str != null)
                {
                    if (previous_shot.equals(c_shot_str) && !previous_shot.equals("0"))
                        return;
                    previous_shot = new String(c_shot_str);
                }
                else
                {
                    if (previous_shot.equals("not defined"))
                        return;
                    previous_shot = "not defined";
                }
         */
        if(this.useDefaultShot()){
            if(main_shot_str != null && main_shot_str.length() != 0){
                curr_shots = ((jScopeWaveContainer)(this.wave.getParent())).getMainShots();
                if(curr_shots == null) this.error = "Main Shot evaluation error: " + ((jScopeWaveContainer)(this.wave.getParent())).getMainShotError(true);
            }else{
                if(this.def_vals.getIsEvaluated() && this.def_vals.shots != null) curr_shots = this.def_vals.shots;
                else{
                    curr_shots = this.getShotArray(jScopeWaveInterface.containMainShot(this.def_vals.shot_str, main_shot_str));
                    if(this.error == null){
                        this.def_vals.shots = curr_shots;
                        this.def_vals.setIsEvaluated(false);
                    }
                }
            }
        }else{
            curr_shots = this.getShotArray(jScopeWaveInterface.containMainShot(this.cin_shot, main_shot_str));
        }
        this.in_shot = c_shot_str;
        if(!this.updateShot(curr_shots)) this.previous_shot = "not defined";
    }

    public boolean useDefaultShot() {
        if(DEBUG.M) System.out.println("jScopeWaveInterface.UseDefaultShot()");
        return((this.defaults & (1 << jScopeWaveInterface.B_shot)) != 0);
    }
}
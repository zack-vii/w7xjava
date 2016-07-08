package jscope;

/* $Id$ */
import java.awt.AWTEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("serial")
final public class WaveformEvent extends AWTEvent{
    public static final int     BROADCAST_SCALE    = AWTEvent.RESERVED_ID_MAX + 4;
    public static final int     CACHE_DATA         = AWTEvent.RESERVED_ID_MAX + 12;
    public static final int     COPY_CUT           = AWTEvent.RESERVED_ID_MAX + 6;
    public static final int     COPY_PASTE         = AWTEvent.RESERVED_ID_MAX + 5;
    private static final String datetime           = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final int     END_UPDATE         = AWTEvent.RESERVED_ID_MAX + 11;
    public static final int     EVENT_UPDATE       = AWTEvent.RESERVED_ID_MAX + 7;
    public static final int     MEASURE_UPDATE     = AWTEvent.RESERVED_ID_MAX + 2;
    public static final int     POINT_IMAGE_UPDATE = AWTEvent.RESERVED_ID_MAX + 9;
    public static final int     POINT_UPDATE       = AWTEvent.RESERVED_ID_MAX + 1;
    public static final int     PROFILE_UPDATE     = AWTEvent.RESERVED_ID_MAX + 8;
    public static final int     START_UPDATE       = AWTEvent.RESERVED_ID_MAX + 10;
    public static final int     STATUS_INFO        = AWTEvent.RESERVED_ID_MAX + 3;
    private static final String time               = "HH:mm:ss.SSS";

    private static String getFormattedDate(final long d, final String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        final Date date = new Date();
        date.setTime(Math.abs(d));
        if(d <= Grid.dayMilliSeconds){
            dateFormat = new SimpleDateFormat("H:mm:ss.SSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat.format(date).toString();
        }
        return dateFormat.format(date).toString();
    }

    private static String setStrSize(final String s, final int size) {
        final StringBuffer sb = new StringBuffer(size);
        sb.append(s.substring(0, ((s.length() < size) ? s.length() : size)));
        if(sb.length() < size) for(int i = sb.length(); i < size; i++)
            sb.append(" ");
        return(new String(sb));
    }
    double       data_value    = Float.NaN;
    private long dateValue;
    double       delta_x;
    double       delta_y;
    int          frame_type;
    float        frames_time[];
    boolean      is_mb2        = false;
    String       name;
    int          pixel_value;
    int          pixels_line[] = null;
    int          pixels_signal[];
    int          pixels_x[];
    int          pixels_y[];
    float        point_value;
    double       point_x;
    double       point_y;
    boolean      showXasDate   = false;
    int          signal_idx;
    int          start_pixel_x;
    int          start_pixel_y;
    String       status_info;
    double       time_value    = Float.NaN;
    float        values_line[] = null;
    float        values_signal[];
    float        values_x[];
    float        values_y[];
    int          x_pixel;
    float        x_value       = Float.NaN;
    int          y_pixel;

    public WaveformEvent(final Object source, final int event_id){
        super(source, event_id);
    }

    public WaveformEvent(final Object source, final int event_id, final double point_x, final double point_y, final double delta_x, final double delta_y, final int pixel_value, final int signal_idx){
        super(source, event_id);
        this.point_x = point_x;
        this.point_y = point_y;
        this.delta_x = delta_x;
        this.delta_y = delta_y;
        this.pixel_value = pixel_value;
        this.signal_idx = signal_idx;
    }

    public WaveformEvent(final Object source, final int x_pixel, final int y_pixel, final float frame_time, final String name, final float values_x[], final int start_pixel_x, final float values_y[], final int start_pixel_y){
        super(source, WaveformEvent.PROFILE_UPDATE);
        this.x_pixel = x_pixel;
        this.y_pixel = y_pixel;
        this.time_value = frame_time;
        this.name = name;
        this.values_x = values_x;
        this.values_y = values_y;
        this.start_pixel_x = start_pixel_x;
        this.start_pixel_y = start_pixel_y;
    }

    public WaveformEvent(final Object source, final int x_pixel, final int y_pixel, final float frame_time, final String name, final int pixels_x[], final int start_pixel_x, final int pixels_y[], final int start_pixel_y){
        super(source, WaveformEvent.PROFILE_UPDATE);
        this.x_pixel = x_pixel;
        this.y_pixel = y_pixel;
        this.time_value = frame_time;
        this.name = name;
        this.pixels_x = pixels_x;
        this.pixels_y = pixels_y;
        this.start_pixel_x = start_pixel_x;
        this.start_pixel_y = start_pixel_y;
    }

    public WaveformEvent(final Object source, final int event_id, final String status_info){
        super(source, event_id);
        this.status_info = status_info;
    }

    public WaveformEvent(final Object source, final String status_info){
        this(source, WaveformEvent.STATUS_INFO, status_info);
    }

    public float getPointValue() {
        return this.point_value;
    }

    public final int getSignalIdx() {
        return this.signal_idx;
    }

    public final String getStatusInfo() {
        return this.status_info;
    }

    public void setDataValue(final double data_value) {
        this.data_value = data_value;
    }

    public void setDateValue(final long date) {
        final long dayMilliSeconds = 24 * 60 * 60 * 1000;
        this.dateValue = date - (date % dayMilliSeconds);
        this.showXasDate = true;
    }

    public void setFrameType(final int frame_type) {
        this.frame_type = frame_type;
    }

    public void setIsMB2(final boolean is_mb2) {
        this.is_mb2 = is_mb2;
    }

    public void setPixelsLine(final int p_line[]) {
        this.pixels_line = p_line;
    }

    public void setPointValue(final float val) {
        this.point_value = val;
    }

    public void setTimeValue(final double time_value) {
        this.time_value = time_value;
    }

    public void setValuesLine(final float v_line[]) {
        this.values_line = v_line;
    }

    public void setXValue(final float x_value) {
        this.x_value = x_value;
    }

    @Override
    @SuppressWarnings("fallthrough")
    public String toString() {
        String s = null;
        final int event_id = this.getID();
        final Waveform w = (Waveform)this.getSource();
        switch(event_id){
            case WaveformEvent.MEASURE_UPDATE:
                double dx_f;
                if(Math.abs(this.delta_x) < 1.e-20) dx_f = 1.e-20;
                else dx_f = Math.abs(this.delta_x);
                if(this.showXasDate){
                    s = WaveformEvent.setStrSize(String.format("[%s, %s; dx %s; dy %s]", //
                    WaveformEvent.getFormattedDate(Grid.toMillis(this.dateValue + this.point_x), WaveformEvent.datetime), //
                    Waveform.convertToString(this.point_y, false), //
                    WaveformEvent.getFormattedDate((long)this.delta_x, WaveformEvent.time), //
                    Waveform.convertToString(this.delta_y, false)), 90);
                }else{
                    s = WaveformEvent.setStrSize(String.format("[%s, %s; dx %s; dy %s; 1/dx %s]", //
                    Waveform.convertToString(this.point_x, false), //
                    Waveform.convertToString(this.point_y, false), //
                    Waveform.convertToString(this.delta_x, false), //
                    Waveform.convertToString(this.delta_y, false), //
                    Waveform.convertToString(1. / dx_f, false)), 90);
                }
            case WaveformEvent.POINT_UPDATE:
            case WaveformEvent.POINT_IMAGE_UPDATE:
                if(s == null){
                    if(!w.isImage()){
                        final Float xf = new Float(this.x_value);
                        final Float tf = new Float(this.time_value);
                        final Float df = new Float(this.data_value);
                        final Float nan_f = new Float(Float.NaN);
                        String xt_string = null;
                        if(!xf.equals(nan_f)) xt_string = ", Y = " + Waveform.convertToString(this.x_value, false);
                        else if(!tf.equals(nan_f)) if(this.showXasDate){
                            xt_string = ", T = " + WaveformEvent.getFormattedDate(Grid.toMillis(this.time_value), WaveformEvent.datetime);
                            this.showXasDate = false;
                        }else xt_string = ", X = " + Waveform.convertToString(this.time_value, false);
                        else if(!df.equals(nan_f)) xt_string = ", Z = " + Waveform.convertToString(this.data_value, false);
                        String x_string = null;
                        int string_size = 40;
                        if(this.showXasDate){
                            x_string = WaveformEvent.getFormattedDate(Grid.toMillis(this.point_x), WaveformEvent.datetime);
                            string_size = 45;
                        }else x_string = "" + new Float(this.point_x);
                        if(xt_string == null) s = WaveformEvent.setStrSize(String.format("[%s, %f]", x_string, this.point_y), string_size);
                        else s = WaveformEvent.setStrSize(String.format("[%s, %f %s]", x_string, this.point_y, xt_string), string_size + 40);
                    }else if(this.frame_type == FrameData.BITMAP_IMAGE_32 || this.frame_type == FrameData.BITMAP_IMAGE_16){
                        s = WaveformEvent.setStrSize(String.format("[%d, %d : (%f) : %f]", (int)this.point_x, (int)this.point_y, this.point_value, this.delta_x), 50);
                    }else{
                        s = WaveformEvent.setStrSize(String.format("[%d, %d : (%06x) : %f]", (int)this.point_x, (int)this.point_y, this.pixel_value & 0xffffff, this.delta_x), 50);
                    }
                }
                break;
        }
        return(s == null ? "" : s);
    }
}

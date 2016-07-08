package jscope;

/* $Id$ */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

@SuppressWarnings("serial")
public class Grid implements Serializable{
    public static final long      dayMilliSeconds = 86400000;                   // 24 * 60 * 60 * 1000;
    final static String           GRID_MODE[]     = {"Dotted", "Gray", "None"};
    public final static int       IS_DOTTED       = 0;
    static final int              IS_GRAY         = 1;
    static final int              IS_NONE         = 2;
    final static int              IS_X            = 0, IS_Y = 1;
    static final int              MAX_GRID        = 10;
    private static final TimeZone UTC             = TimeZone.getTimeZone("UTC");

    public static long calculateDifference(final Date a, final Date b) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTimeZone(Grid.UTC);
        cal1.setTime(a);
        cal1.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTimeZone(Grid.UTC);
        cal2.setTime(b);
        cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        final long diffMillis = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        if(diffMillis < 0) return 0;
        return 1 + diffMillis / Grid.dayMilliSeconds;
    }

    public static final double evalStep(final double min, final double max, final int numStep) {
        final double delta = Math.abs(max - min);
        final int pow = (int)Math.log10(delta) - 1;
        final double k = Math.pow(10, pow);
        return(2. * k);
    }

    public static final long toMillis(double xin)
    /*converts ns into ms if necessary*/
    {
        if(xin > 2E13) xin = xin / 1E6;
        return (long)xin;
    }
    Font            font;
    int             grid_step_x, grid_step_y;
    boolean         int_ylabels, int_xlabels;
    int             label_width, label_height, label_descent, num_x_steps, num_y_steps;
    int             mode;
    boolean         reversed = false;
    WaveformMetrics wm;
    int             x_dim, y_dim;
    String          x_label, y_label, title, error;
    double          x_values[], y_values[], x_step, y_step;
    boolean         xAxisHMS = false;
    double          xmax;

    public Grid(final double xmax, final double ymax, final double xmin, final double ymin, final boolean xlog, final boolean ylog, final int mode, final String x_label, final String y_label, final String title, final String error, final int grid_step_x, final int grid_step_y, final boolean int_xlabels, final boolean int_ylabels, final boolean reversed){
        this.reversed = reversed;
        this.mode = mode;
        this.x_label = x_label;
        this.y_label = y_label;
        this.title = title;
        this.error = error;
        this.grid_step_x = grid_step_x;
        this.grid_step_y = grid_step_y;
        this.int_xlabels = int_xlabels;
        this.int_ylabels = int_ylabels;
        this.font = null;
        this.x_values = new double[50];
        this.y_values = new double[50];
        this.xmax = xmax;
        this.x_dim = this.buildGrid(this.x_values, Grid.IS_X, xmax, ymax, xmin, ymin, xlog, ylog);
        this.y_dim = this.buildGrid(this.y_values, Grid.IS_Y, xmax, ymax, xmin, ymin, xlog, ylog);
    }

    private int buildGrid(final double val[], final int mode, double xmax, double ymax, final double xmin, final double ymin, final boolean xlog, final boolean ylog) {
        if(ymax <= ymin) ymax = ymin + 1E-10;
        if(xmax <= xmin) xmax = xmin + 1E-10;
        final double curr_max, curr_min;
        double step, xrange = xmax - xmin, yrange = ymax - ymin;
        if(xrange <= 0) xrange = 1E-3;
        if(yrange <= 0) yrange = 1E-3;
        if(mode == Grid.IS_X){
            curr_max = xmax + 0.1 * xrange;
            curr_min = xmin - 0.1 * xrange;
            step = (xmax - xmin) / this.grid_step_x;
        }else{
            curr_max = ymax + 0.1 * yrange;
            curr_min = ymin - 0.1 * yrange;
            step = (ymax - ymin) / this.grid_step_y;
        }
        if(Double.isInfinite(step) || Double.isNaN(step)) return 0;
        final int count = (int)Math.floor(Math.log10(step));
        final int num_steps = (int)(step / Math.pow(10., count));
        step = num_steps * Math.pow(10., count);
        final int fac_min = (int)Math.floor(curr_min / step);
        final int fac_max = (int)Math.floor(curr_max / step);
        final int fac_cnt = fac_max - fac_min > 50 ? 50 : fac_max - fac_min;
        for(int i = 0; i < fac_cnt; i++)
            val[i] = (fac_min + i) * step;
        if(mode == Grid.IS_X){
            this.x_step = step / num_steps;
            this.num_x_steps = num_steps;
        }else{
            this.y_step = step / num_steps;
            this.num_y_steps = num_steps;
        }
        return fac_cnt;
    }

    public Rectangle getBoundingBox(final Dimension d) {
        return new Rectangle(this.label_width, 0, d.width - this.label_width + 1, d.height - this.label_height + 1);
    }

    public void getLimits(final Graphics g, final Rectangle lim_rect, final boolean ylog) {
        int label_width, label_height, curr_dim;
        FontMetrics fm;
        fm = g.getFontMetrics();
        if(this.int_xlabels) label_height = 1;
        else{
            if(this.x_label != null) label_height = 2 * fm.getHeight();
            else label_height = fm.getHeight();
        }
        label_width = 0;
        if(!this.int_ylabels){
            for(int i = 0; i < this.y_dim; i++){
                curr_dim = fm.stringWidth(Waveform.convertToString(this.y_values[i], ylog));
                if(label_width < curr_dim) label_width = curr_dim;
            }
            if(this.y_label != null) label_width += fm.getHeight();
        }
        lim_rect.width = label_width;
        lim_rect.height = label_height;
    }

    public void paint(final Graphics g, final Dimension d, final Waveform w, final WaveformMetrics wm) {
        int i, j, dim, curr_dim;
        Color prev_col;
        FontMetrics fm;
        double curr_step;
        String curr_string;
        String curr_date_string = null;
        String prev_date_string = "";
        if(this.reversed) g.setColor(Color.white);
        else g.setColor(Color.black);
        this.wm = wm;
        fm = g.getFontMetrics();
        if(this.int_xlabels) this.label_height = 0;
        else this.label_height = /*2 * */fm.getHeight();
        this.label_descent = fm.getDescent();
        this.label_width = 0;
        if(!this.int_ylabels && wm != null){
            for(i = 0; i < this.y_dim; i++){
                curr_dim = fm.stringWidth(Waveform.convertToString(this.y_values[i], wm.getYLog()));
                if(this.label_width < curr_dim) this.label_width = curr_dim;
            }
            if(this.y_label != null) this.label_width += fm.getHeight();
            // label_width -= fm.charWidth(' ');
        }
        prev_col = g.getColor();
        if(wm != null){
            for(i = 0; i < this.y_dim; i++){
                dim = wm.toYPixel(this.y_values[i], d);
                switch(this.mode){
                    case IS_DOTTED:
                        if(dim <= d.height - this.label_height) for(j = this.label_width; j < d.width; j += 4)
                            g.fillRect(j, dim, 1, 1);
                        break;
                    case IS_GRAY:
                        g.setColor(Color.lightGray);
                        if(dim <= d.height - this.label_height) g.drawLine(this.label_width, dim, d.width, dim);
                        break;
                    case IS_NONE:
                        if(dim <= d.height - this.label_height){
                            g.drawLine(this.label_width + 3, dim, d.width / 80 + this.label_width + 3, dim);
                            g.drawLine(d.width - d.width / 80, dim, d.width, dim);
                        }
                        if(i == this.y_dim - 1) break;
                        if(wm.getYLog()) curr_step = (this.y_values[i + 1] - this.y_values[i]) / this.num_y_steps;
                        else curr_step = this.y_step;
                        for(j = 1; j <= this.num_y_steps; j++){
                            curr_dim = wm.toYPixel(this.y_values[i] + j * curr_step, d);
                            if(curr_dim <= d.height - this.label_height){
                                g.drawLine(this.label_width + 3, curr_dim, this.label_width + d.width / 100 + 3, curr_dim);
                                g.drawLine(d.width - d.width / 100, curr_dim, d.width, curr_dim);
                            }
                        }
                }
                g.setColor(prev_col);
                if(dim <= d.height - this.label_height){
                    curr_dim = dim + fm.getHeight() / 2;
                    if((curr_dim - fm.getAscent() >= 0) && (curr_dim + fm.getDescent() <= d.height)){
                        int ylabel_offset = 1;
                        if(this.y_label != null) ylabel_offset = fm.getHeight();
                        if(this.int_ylabels){
                            if(this.mode == Grid.IS_NONE) ylabel_offset += d.width / 40;
                            else ylabel_offset = 2;
                        }
                        g.drawString(Waveform.convertToString(this.y_values[i], wm.getYLog()), ylabel_offset + 1, curr_dim);
                    }
                }
            }
            int prevIdx;
            String currStringSubSec = "";
            for(i = prevIdx = 0; i < this.x_dim; i++){
                dim = wm.toXPixel(this.x_values[i], d);
                switch(this.mode){
                    case IS_DOTTED:
                        if(dim >= this.label_width) for(j = 0; j < d.height - this.label_height; j += 4)
                            g.fillRect(dim, j, 1, 1);
                        break;
                    case IS_GRAY:
                        g.setColor(Color.lightGray);
                        if(dim >= this.label_width) g.drawLine(dim, 0, dim, d.height - this.label_height);
                        break;
                    case IS_NONE:
                        if(dim >= this.label_width){
                            g.drawLine(dim, 2, dim, d.height / 40);
                            g.drawLine(dim, d.height - this.label_height - d.height / 40, dim, d.height - this.label_height);
                        }
                        if(i == this.x_dim - 1) break;
                        if(wm.getXLog()) curr_step = (this.x_values[i + 1] - this.x_values[i]) / this.num_x_steps;
                        else curr_step = this.x_step;
                        for(j = 1; j <= this.num_x_steps; j++){
                            final double val = this.x_values[i] + j * curr_step;
                            curr_dim = wm.toXPixel(val, d);
                            if(curr_dim >= this.label_width){
                                g.drawLine(curr_dim, 2, curr_dim, d.height / 80);
                                g.drawLine(curr_dim, d.height - this.label_height - d.height / 80, curr_dim, d.height - this.label_height);
                            }
                        }
                        g.drawRect(this.label_width + 3, 2, d.width - this.label_width - 3, d.height - this.label_height - 2);
                }
                g.setColor(prev_col);
                if(this.xAxisHMS){
                    try{
                        final long datel = Grid.toMillis(this.x_values[i]);
                        final DateFormat df = new SimpleDateFormat("HH:mm:ss");
                        df.setTimeZone(Grid.UTC);
                        final DateFormat dfSubSec = new SimpleDateFormat("HH:mm:ss.SSS");
                        dfSubSec.setTimeZone(Grid.UTC);
                        if(datel <= Grid.dayMilliSeconds){
                            // if the date to convert is in the date 1 Jan 1970
                            // is whown only the huor and the time xone must be set
                            // to GTM to avoid to add the hours of the time zone
                            df.setTimeZone(new SimpleTimeZone(0, "GMT"));
                            dfSubSec.setTimeZone(new SimpleTimeZone(0, "GMT"));
                        }
                        final Date date = new Date();
                        date.setTime(datel);
                        currStringSubSec = dfSubSec.format(date).toString();
                        curr_string = df.format(date).toString();
                        final DateFormat df1 = new SimpleDateFormat("d-MMM-yyyy");
                        df1.setTimeZone(Grid.UTC);
                        final String new_date_string = df1.format(date).toString();
                        if(i == 0 || !new_date_string.equals(prev_date_string)){
                            curr_date_string = prev_date_string = new_date_string;
                        }else curr_date_string = null;
                        if(i < this.x_dim - 1){
                            final long num_day = Grid.calculateDifference(new Date(Grid.toMillis(this.x_values[i])), new Date(Grid.toMillis(this.x_values[i + 1])));
                            if(num_day != 0){
                                final Calendar ca = Calendar.getInstance();
                                // ca.setTimeZone(TimeZone.getTimeZone("GMT+00"));
                                ca.setTimeInMillis(Grid.toMillis(this.x_values[i]));
                                ca.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH) + 1, 0, 0);
                                for(int dd = 0; dd < num_day; dd++){
                                    final long timeMillis = ca.getTimeInMillis();
                                    if(timeMillis < this.xmax){
                                        final Color c = g.getColor();
                                        g.setColor(Color.BLUE);
                                        curr_dim = wm.toXPixel(timeMillis, d);
                                        if(curr_dim >= this.label_width){
                                            // g.drawLine(curr_dim, 0, curr_dim,d.height - label_height); case IS_DOTTED:
                                            for(j = 0; j < d.height - this.label_height; j += 7)
                                                g.fillRect(curr_dim, j, 1, 5);
                                        }
                                        g.setColor(c);
                                    }
                                    ca.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH) + 1, 0, 0);
                                }
                            }
                        }
                    }catch(final Exception exc){
                        curr_string = Waveform.convertToString(this.x_values[i], wm.getXLog());
                    }
                }else curr_string = Waveform.convertToString(this.x_values[i], wm.getXLog());
                curr_dim = dim - fm.stringWidth(curr_string) / 2;
                if(curr_dim >= this.label_width && dim + fm.stringWidth(curr_string) / 2 < d.width){
                    if(this.xAxisHMS && i > 0 && Grid.toMillis(this.x_values[i] - this.x_values[prevIdx]) < 1000) g.drawString(currStringSubSec, curr_dim, d.height - fm.getHeight() / 10 - this.label_descent);
                    else g.drawString(curr_string, curr_dim, d.height - fm.getHeight() / 10 - this.label_descent);
                    prevIdx = i;
                }
                if(curr_date_string != null){
                    curr_dim = dim - fm.stringWidth(curr_date_string) / 2;
                    if(curr_dim >= this.label_width && dim + fm.stringWidth(curr_string) / 2 < d.width){
                        g.drawString(curr_date_string, curr_dim, d.height - fm.getHeight() - 2 * fm.getHeight() / 10 - this.label_descent);
                    }
                }
            }
        } // End if check is_image
        if(this.x_label != null && this.x_label.length() != 0) g.drawString(this.x_label, (d.width - fm.stringWidth(this.x_label)) / 2, d.height - this.label_descent - fm.getHeight());
        if(this.y_label != null && this.y_label.length() != 0){
            final Graphics2D g2d = (Graphics2D)g;
            final double x_tra = 4 + fm.getHeight();
            final double y_tra = (d.height + fm.stringWidth(this.y_label)) / 2;
            final double angle = Math.PI / 2.0;
            g2d.translate(x_tra, y_tra);
            g2d.rotate(-angle);
            g2d.drawString(this.y_label, 0, 0);
            g2d.rotate(angle);
            g2d.translate(-x_tra, -y_tra);
        }
        if(this.title != null && this.title.length() != 0) g.drawString(this.title, (d.width - fm.stringWidth(this.title)) / 2, fm.getAscent() + d.height / 40);
        if(this.error != null && this.error.length() != 0){
            int y_pos = 0;
            if(this.title != null && this.title.trim().length() != 0) y_pos = fm.getHeight();
            g.drawString(this.error, (d.width - fm.stringWidth(this.error)) / 2, y_pos + fm.getAscent() + d.height / 40);
        }
    }

    public void setLabels(final String title, final String x_label, final String y_label) {
        this.title = title;
        this.x_label = x_label;
        this.y_label = y_label;
    }

    void setReversed(final boolean reversed) {
        this.reversed = reversed;
    }

    public void setXaxisHMS(final boolean xAxisHMS) {
        this.xAxisHMS = xAxisHMS;
    }

    public void updateValues(final String x_label, final String y_label, final String title, final String error, final int grid_step_x, final int grid_step_y, final boolean int_xlabels, final boolean int_ylabels, final boolean reversed) {
        this.reversed = reversed;
        this.x_label = x_label;
        this.y_label = y_label;
        this.title = title;
        this.error = error;
        this.grid_step_x = grid_step_x;
        this.grid_step_y = grid_step_y;
        this.int_xlabels = int_xlabels;
        this.int_ylabels = int_ylabels;
    }
}

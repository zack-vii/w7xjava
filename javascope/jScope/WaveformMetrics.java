package jScope;

/* $Id$ */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.IndexColorModel;
import java.io.Serializable;
import java.util.Vector;

@SuppressWarnings("serial")
public final class WaveformMetrics implements Serializable{
    private static final int    INT_MAX_VALUE = (int)WaveformMetrics.MAX_VALUE;
    private static final int    INT_MIN_VALUE = (int)WaveformMetrics.MIN_VALUE;
    // static IndexColorModel cm = null;
    private static final double LOG10         = 2.302585092994, MIN_LOG = 10E-100;
    private static final double MAX_VALUE     = 10000.;
    private static final double MIN_VALUE     = -10000.;

    /*
        private IndexColorModel getColorModel() {
            byte rgb[] = new byte[256], b = 0;
            for(int i = 0; i < 256; i++, b++)
                rgb[i] = b;
            return new IndexColorModel(8, 256, rgb, rgb, rgb);
        }
     */
    private static final void drawRectagle(final Graphics g, final IndexColorModel cm, final int x, final int y, final int w, final int h, final int cIdx) {
        g.setColor(new Color(cm.getRed(cIdx), cm.getGreen(cIdx), cm.getBlue(cIdx), cm.getAlpha(cIdx)));
        g.fillRect(x, y, w, h);
    }
    private double        FACT_X, FACT_Y, OFS_X, OFS_Y;
    private final int     horizontal_offset, vertical_offset;
    private final int     start_x;
    private final boolean x_log, y_log;
    private double        x_offset;
    private final double  x_range;
    private double        xmax, xmin, ymax, ymin;
    private double        xrange, yrange;                    // xmax - xmin , ymax - ymin
    private final double  y_range;

    public WaveformMetrics(double _xmax, double _xmin, double _ymax, double _ymin, final Rectangle limits, final Dimension d, final boolean _x_log, final boolean _y_log, final int horizontal_offset, final int vertical_offset){
        final int ylabel_width = limits.width, xlabel_height = limits.height;
        double delta_x, delta_y;
        int border_y;
        this.horizontal_offset = horizontal_offset;
        this.vertical_offset = vertical_offset;
        if(_ymin > _ymax) _ymin = _ymax;
        if(_xmin > _xmax) _xmin = _xmax;
        this.start_x = ylabel_width;
        this.x_log = _x_log;
        this.y_log = _y_log;
        border_y = xlabel_height;
        // y_range = (d.height - border_y)/(double)d.height;
        this.y_range = (d.height - border_y - 2 * vertical_offset) / (double)d.height;
        // x_range = (d.width - start_x)/(double)d.width;
        this.x_range = (d.width - this.start_x - 2 * horizontal_offset) / (double)d.width;
        this.x_offset = this.start_x / (double)d.width;
        if(this.x_log){
            if(_xmax < WaveformMetrics.MIN_LOG) _xmax = WaveformMetrics.MIN_LOG;
            if(_xmin < WaveformMetrics.MIN_LOG) _xmin = WaveformMetrics.MIN_LOG;
            this.xmax = Math.log(_xmax) / WaveformMetrics.LOG10;
            this.xmin = Math.log(_xmin) / WaveformMetrics.LOG10;
        }else{
            this.xmax = _xmax;
            this.xmin = _xmin;
        }
        delta_x = this.xmax - this.xmin;
        this.xmax += delta_x / 100.;
        this.xmin -= delta_x / 100.;
        if(this.y_log){
            if(_ymax < WaveformMetrics.MIN_LOG) _ymax = WaveformMetrics.MIN_LOG;
            if(_ymin < WaveformMetrics.MIN_LOG) _ymin = WaveformMetrics.MIN_LOG;
            this.ymax = Math.log(_ymax) / WaveformMetrics.LOG10;
            this.ymin = Math.log(_ymin) / WaveformMetrics.LOG10;
        }else{
            this.ymax = _ymax;
            this.ymin = _ymin;
        }
        delta_y = this.ymax - this.ymin;
        this.ymax += delta_y / 50;
        this.ymin -= delta_y / 50.;
        this.xrange = this.xmax - this.xmin;
        this.yrange = this.ymax - this.ymin;
        if(this.xrange <= 0){
            this.xrange = 1E-10;
            this.x_offset = 0.5;
        }
        if(this.yrange <= 0){
            this.yrange = 1E-10;
        }
    }

    public final void ComputeFactors(final Dimension d) {
        // OFS_X = x_offset * d.width - xmin*x_range*d.width/xrange + 0.5;
        this.OFS_X = this.x_offset * d.width - this.xmin * this.x_range * d.width / this.xrange + this.horizontal_offset + 0.5;
        this.FACT_X = this.x_range * d.width / this.xrange;
        // OFS_Y = y_range * ymax*d.height/yrange + 0.5;
        this.OFS_Y = this.y_range * this.ymax * d.height / this.yrange + this.vertical_offset + 0.5;
        this.FACT_Y = -this.y_range * d.height / this.yrange;
    }

    public final void ToImage(final Signal s, final Image img, final Dimension d, final ColorMap colorMap) {
        int xSt, xEt, ySt, yEt;
        final Graphics2D g2 = (Graphics2D)img.getGraphics();
        final IndexColorModel cm = colorMap.getIndexColorModel(8);
        this.ComputeFactors(d);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, d.width - 1, d.height - 1);
        final double[] x2D = s.getX2D();
        final float[] y2D = s.getY2D();
        final float[] z2D = s.getZ();
        float z2D_min, z2D_max;
        z2D_min = z2D_max = z2D[0];
        for(final float element : z2D){
            if(element < z2D_min) z2D_min = element;
            if(element > z2D_max) z2D_max = element;
        }
        for(xSt = 0; xSt < x2D.length && x2D[xSt] < this.xmin; xSt++);
        for(xEt = 0; xEt < x2D.length && x2D[xEt] < this.xmax; xEt++);
        for(ySt = 0; ySt < y2D.length && y2D[ySt] < this.ymin; ySt++);
        for(yEt = 0; yEt < y2D.length && y2D[yEt] < this.ymax; yEt++);
        if(yEt == 0) return;
        int p = 0;
        int h = 0;
        int w = 0;
        int yPix0;
        int yPix1;
        int xPix0;
        int xPix1;
        int pix;
        try{
            yPix1 = (this.YPixel(y2D[ySt + 1]) + this.YPixel(y2D[ySt])) / 2;
            yPix1 = 2 * this.YPixel(y2D[ySt]) - yPix1;
            float currMax = z2D_min, currMin = z2D_max;
            for(int y = ySt; y < yEt; y++){
                p = y * x2D.length + xSt;
                for(int x = xSt; x < xEt && p < z2D.length; x++){
                    if(z2D[p] > currMax) currMax = z2D[p];
                    if(z2D[p] < currMin) currMin = z2D[p];
                    p++;
                }
            }
            for(int y = ySt; y < yEt; y++){
                yPix0 = yPix1;
                try{
                    yPix1 = (this.YPixel(y2D[y + 1]) + this.YPixel(y2D[y])) / 2;
                    h = Math.abs(yPix0 - yPix1) + 2;
                }catch(final Exception e){
                    yPix1 = 2 * this.YPixel(y2D[yEt - 1]) - yPix1;
                    h = Math.abs(yPix0 - yPix1) + 2;
                }
                p = y * x2D.length + xSt;
                xPix1 = (this.XPixel(x2D[xSt]) + this.XPixel(x2D[xSt + 1])) / 2;
                xPix1 = 2 * this.XPixel(x2D[xSt]) - xPix1;
                for(int x = xSt; x < xEt && p < z2D.length; x++){
                    xPix0 = xPix1;
                    try{
                        xPix1 = (this.XPixel(x2D[x + 1]) + this.XPixel(x2D[x])) / 2;
                        w = Math.abs(xPix1 - xPix0);
                    }catch(final Exception e){
                        w = 2 * (this.XPixel(x2D[xEt - 1]) - xPix1);
                    }
                    /*
                     * pix = (int) (255 * (z2D[p++] - z2D_min) / (z2D_max - z2D_min));
                     */
                    pix = (int)(255 * (z2D[p++] - currMin) / (currMax - currMin));
                    pix = (pix > 255) ? 255 : pix;
                    pix = (pix < 0) ? 0 : pix;
                    WaveformMetrics.drawRectagle(g2, cm, xPix0, yPix1, w, h, pix);
                }
            }
        }catch(final Exception exc){};
    }

    public final Vector<Polygon> ToPolygons(final Signal sig, final Dimension d) {
        return this.ToPolygons(sig, d, false);
    }

    public final Vector<Polygon> ToPolygons(final Signal sig, final Dimension d, final boolean appendMode) {
        try{
            // System.out.println("ToPolygons "+sig.name+" "+appendMode);
            return this.ToPolygonsDoubleX(sig, d);
        }catch(final Exception exc){
            exc.printStackTrace();
        }
        return null;
    }

    public final Vector<Polygon> ToPolygonsDoubleX(final Signal sig, final Dimension d) {
        int i, j, curr_num_points, start_x;
        double max_y, min_y, curr_y;
        Vector<Polygon> curr_vect = new Vector<Polygon>(5);
        int xpoints[], ypoints[];
        Polygon curr_polygon = null;
        int pol_idx = 0;
        min_y = max_y = sig.getY(0);
        xpoints = new int[sig.getNumPoints()];
        ypoints = new int[sig.getNumPoints()];
        // xpoints = new int[2*sig.getNumPoints()];
        // ypoints = new int[2*sig.getNumPoints()];
        curr_num_points = 0;
        i = j = 0;
        int end_point = sig.getNumPoints();
        if(this.x_log || this.y_log){
            final double xmin_nolog = Math.pow(10, this.xmin);
            double first_y, last_y;
            for(i = 0; i < sig.getNumPoints() && sig.getX(i) < xmin_nolog; i++);
            if(i > 0) i--;
            min_y = max_y = sig.getY(i);
            j = i + 1;
            start_x = this.XPixel(sig.getX(i), d);
            first_y = last_y = sig.getY(i);
            while(j < end_point) // sig.getNumPoints() && sig.x_double[j] < xmax_nolog)
            {
                for(j = i + 1; j < sig.getNumPoints() && (pol_idx >= sig.getNumNaNs() || j != sig.getNaNs()[pol_idx]) && (this.XPixel(sig.getX(j), d)) == start_x; j++){
                    last_y = curr_y = sig.getY(j);
                    if(curr_y < min_y) min_y = curr_y;
                    if(curr_y > max_y) max_y = curr_y;
                }
                if(max_y > min_y){
                    if(first_y != min_y){
                        xpoints[curr_num_points] = start_x;
                        ypoints[curr_num_points] = this.YPixel(first_y, d);
                        curr_num_points++;
                    }
                    xpoints[curr_num_points] = xpoints[curr_num_points + 1] = start_x;
                    ypoints[curr_num_points] = this.YPixel(min_y, d);
                    ypoints[curr_num_points + 1] = this.YPixel(max_y, d);
                    curr_num_points += 2;
                    if(last_y != max_y){
                        xpoints[curr_num_points] = start_x;
                        ypoints[curr_num_points] = this.YPixel(last_y, d);
                        curr_num_points++;
                    }
                }else{
                    xpoints[curr_num_points] = start_x;
                    ypoints[curr_num_points] = this.YPixel(max_y, d);
                    curr_num_points++;
                }
                if(j == sig.getNumPoints() || j == end_point || Double.isNaN(sig.getY(j))) // || sig.x_double[j] >= xmax_nolog)
                {
                    curr_polygon = new Polygon(xpoints, ypoints, curr_num_points);
                    curr_vect.addElement(curr_polygon);
                    pol_idx++;
                    curr_num_points = 0;
                    if(j < sig.getNumPoints()) // need to raise pen
                    {
                        while(j < sig.getNumPoints() && Double.isNaN(sig.getY(j)))
                            j++;
                    }
                }
                if(j < end_point) // sig.getNumPoints())
                {
                    start_x = this.XPixel(sig.getX(j), d);
                    max_y = min_y = sig.getY(j);
                    i = j;
                    if(sig.getX(j) > this.xmax) end_point = j + 1;
                }
            }
        }else // Not using logaritmic scales
        {
            this.ComputeFactors(d);
            try{
                final double x[] = sig.getX();
                final float y[] = sig.getY();
                if(x == null || y == null) return curr_vect;
                for(i = 0; i < x.length && x[i] < this.xmin; i++);
                if(i > 0) i--;
                min_y = max_y = y[i];
                j = i + 1;
                // GAB testare da qua il problema
                start_x = this.XPixel(x[i]);
                double first_y, last_y;
                while(j < end_point) // sig.getNumPoints() && sig.x_double[j] < xmax + dt)
                {
                    first_y = last_y = y[i];
                    for(j = i + 1; j < x.length && // !Float.isNaN(sig.y[j]) &&
                    (pol_idx >= sig.getNumNaNs() || j != sig.getNaNs()[pol_idx]) && (this.XPixel(x[j])) == start_x; j++){
                        last_y = curr_y = y[j];
                        if(curr_y < min_y) min_y = curr_y;
                        if(curr_y > max_y) max_y = curr_y;
                    }
                    if(max_y > min_y){
                        if(first_y == min_y){
                            xpoints[curr_num_points] = start_x;
                            ypoints[curr_num_points] = this.YPixel(first_y);
                            curr_num_points++;
                            if(last_y == max_y){
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(last_y);
                                curr_num_points++;
                            }else{
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(max_y);
                                curr_num_points++;
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(last_y);
                                curr_num_points++;
                            }
                        }else if(first_y == max_y){
                            xpoints[curr_num_points] = start_x;
                            ypoints[curr_num_points] = this.YPixel(first_y);
                            curr_num_points++;
                            if(last_y == min_y){
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(last_y);
                                curr_num_points++;
                            }else{
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(min_y);
                                curr_num_points++;
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(last_y);
                                curr_num_points++;
                            }
                        }else // first_y != min_y && first_y != max_y
                        {
                            xpoints[curr_num_points] = start_x;
                            ypoints[curr_num_points] = this.YPixel(first_y);
                            curr_num_points++;
                            if(last_y == min_y){
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(max_y);
                                curr_num_points++;
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(last_y);
                                curr_num_points++;
                            }else if(last_y == max_y){
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(min_y);
                                curr_num_points++;
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(last_y);
                                curr_num_points++;
                            }else{
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(min_y);
                                curr_num_points++;
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(max_y);
                                curr_num_points++;
                                xpoints[curr_num_points] = start_x;
                                ypoints[curr_num_points] = this.YPixel(last_y);
                                curr_num_points++;
                            }
                        }
                    }else{
                        xpoints[curr_num_points] = start_x;
                        ypoints[curr_num_points] = this.YPixel(max_y);
                        curr_num_points++;
                    }
                    if(j == x.length || j >= end_point || Double.isNaN(y[j])) // || sig.x_double[j] >= xmax)
                    {
                        curr_polygon = new Polygon(xpoints, ypoints, curr_num_points);
                        curr_vect.addElement(curr_polygon);
                        pol_idx++;
                        curr_num_points = 0;
                        if(j < x.length) // need to raise pen
                        {
                            while(j < x.length && Double.isNaN(y[j]))
                                j++;
                        }
                    }
                    if(j < end_point) // sig.getNumPoints())
                    {
                        start_x = this.XPixel(x[j]);
                        max_y = min_y = y[j];
                        i = j;
                        if(sig.isIncreasingX() && x[j] > this.xmax) end_point = j + 1;
                    }
                }
            }catch(final Exception exc){
                // Exception is generated when signal is emty
                // System.out.println("Waveform Metrics exception: " + exc);
            }
        }
        if(sig.getMode1D() == Signal.MODE_STEP){
            final Vector<Polygon> v = new Vector<Polygon>();
            int x[];
            int y[];
            for(i = 0; i < curr_vect.size(); i++){
                curr_polygon = curr_vect.elementAt(i);
                final int np = curr_polygon.npoints * 2 - 1;
                x = new int[np];
                y = new int[np];
                for(i = 0, j = 0; i < curr_polygon.npoints; i++, j++){
                    x[j] = curr_polygon.xpoints[i];
                    y[j] = curr_polygon.ypoints[i];
                    j++;
                    if(j == np) break;
                    x[j] = curr_polygon.xpoints[i + 1];
                    y[j] = curr_polygon.ypoints[i];
                }
                curr_polygon = new Polygon(x, y, np);
                v.addElement(curr_polygon);
            }
            curr_vect = v;
        }
        return curr_vect;
    }

    public final boolean XLog() {
        return this.x_log;
    }

    public final double XMax() {
        return this.xmax;
    }

    public final double XMin() {
        return this.xmin;
    }

    public final int XPixel(final double x) {
        final double xpix = x * this.FACT_X + this.OFS_X;
        if(xpix >= WaveformMetrics.MAX_VALUE) return WaveformMetrics.INT_MAX_VALUE;
        if(xpix <= WaveformMetrics.MIN_VALUE) return WaveformMetrics.INT_MIN_VALUE;
        return (int)xpix;
    }

    public final int XPixel(double x, final Dimension d) {
        double ris;
        if(this.x_log){
            if(x < WaveformMetrics.MIN_LOG) x = WaveformMetrics.MIN_LOG;
            x = Math.log(x) / WaveformMetrics.LOG10;
        }
        ris = (this.x_offset + this.x_range * (x - this.xmin) / this.xrange) * d.width + 0.5;
        if(ris > 20000) ris = 20000;
        if(ris < -20000) ris = -20000;
        return (int)ris;
    }

    public final double XRange() {
        return this.xmax - this.xmin;
    }

    public final double XValue(final int x, final Dimension d) {
        final double ris = (((x - 0.5) / d.width - this.x_offset) * this.xrange / this.x_range + this.xmin);
        if(this.x_log) return Math.exp(WaveformMetrics.LOG10 * ris);
        return ris;
    }

    public final boolean YLog() {
        return this.y_log;
    }

    public final double YMax() {
        return this.ymax;
    }

    public final double YMin() {
        return this.ymin;
    }

    public final int YPixel(final double y) {
        final double ypix = y * this.FACT_Y + this.OFS_Y;
        if(ypix >= WaveformMetrics.MAX_VALUE) return WaveformMetrics.INT_MAX_VALUE;
        if(ypix <= WaveformMetrics.MIN_VALUE) return WaveformMetrics.INT_MIN_VALUE;
        return (int)ypix;
    }

    public final int YPixel(double y, final Dimension d) {
        if(this.y_log){
            if(y < WaveformMetrics.MIN_LOG) y = WaveformMetrics.MIN_LOG;
            y = Math.log(y) / WaveformMetrics.LOG10;
        }
        double ris = (this.y_range * (this.ymax - y) / this.yrange) * d.height + 0.5;
        if(ris > 20000) ris = 20000;
        if(ris < -20000) ris = -20000;
        return (int)ris;
    }

    public final double YRange() {
        return this.ymax - this.ymin;
    }

    public final double YValue(final int y, final Dimension d) {
        final double ris = (this.ymax - ((y - 0.5) / d.height) * this.yrange / this.y_range);
        if(this.y_log) return Math.exp(WaveformMetrics.LOG10 * ris);
        return ris;
    }
}

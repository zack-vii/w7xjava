package jScope;

/* $Id$ */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Vector;
import debug.DEBUG;

/**
 * Class MultiWaveform extends the capability of class Waveform to deal with multiple
 * waveforms.
 */
@SuppressWarnings("serial")
public class MultiWaveform extends Waveform{
    public static final int  HORIZONTAL         = 0;
    public static final int  LEGEND_BOTTOM      = 1;
    public static final int  LEGEND_IN_GRAPHICS = 0;
    public static final int  LEGEND_RIGHT       = 2;
    public static final int  MAX_DRAG_POINT     = 200;
    public static final int  PRINT_BW           = 8;
    public static final int  PRINT_LEGEND       = 4;
    public static final int  VERTICAL           = 1;
    private boolean          asinchAutoscale    = false;
    private int              bottom_size        = 0;
    private boolean          continuosAutoscale = false;
    protected int            curr_point_sig_idx = -1;
    protected boolean        fixed_legend       = false;
    private int              legend_mode        = 0;
    private Point            legend_point;
    protected double         legend_x;
    protected double         legend_y;
    protected Vector<Signal> orig_signals       = null;
    protected double         orig_xmax          = Double.NEGATIVE_INFINITY;
    protected double         orig_xmin          = Double.POSITIVE_INFINITY;
    protected int            right_size         = 0;
    protected boolean        show_legend        = false;
    protected Vector<Signal> signals            = new Vector<Signal>();
    protected WaveInterface  wi;

    public MultiWaveform(){
        super();
        if(this.signals.size() != 0) this.signals.removeAllElements();
        this.orig_signals = null;
        // {{REGISTER_LISTENERS
        final SymContainer aSymContainer = new SymContainer();
        this.addContainerListener(aSymContainer);
        // }}
        // GAB 2014 add Drag And Drop capability
    }

    public void addSignal(final Signal s) {
        if(!this.exists(s)){
            this.signals.addElement(s);
            this.setLimits();
            s.registerSignalListener(this);
        }
    }

    public void addSignals(final Signal s[]) {
        if(s == null || s.length == 0) return;
        for(final Signal element : s){
            this.addSignal(element);
            if(element != null) element.registerSignalListener(this);
        }
        this.setLimits();
        if(this.waveform_signal != null){
            this.curr_point_sig_idx = 0;
            super.Update(this.waveform_signal);
        }
    }

    @Override
    public void appendUpdate() {
        Signal s;
        for(int i = 0; i < this.signals.size(); i++){
            s = this.signals.elementAt(i);
            if(s.fullPaint()){
                this.Update();
                return;
            }
        }
        this.appendPaint(this.getGraphics(), this.getSize());
    }

    @Override
    public void Autoscale() {
        if(DEBUG.M) System.out.println("MultiWaveform.Autoscale()");
        if(this.is_image && this.frames != null){
            super.Autoscale();
            return;
        }
        if(this.waveform_signal == null) return;
        this.update_timestamp++;
        this.continuosAutoscale = true;
        if(this.signals == null) return;
        if(this.orig_signals != null){ // Previous zoom
            this.signals = this.orig_signals;
            this.orig_signals = null;
        }
        boolean any = false;
        for(final Signal signal : this.signals){
            if(signal == null) continue;
            signal.Autoscale();
            if(any) continue;
            this.waveform_signal.setMode1D(signal.getMode1D());
            this.waveform_signal.setMode2D(signal.getMode2D());
            any = true;
        }
        if(!any) return;
        this.setLimits(Signal.SIMPLE);
        this.ReportChanges();
    }

    @Override
    public void AutoscaleY() {
        if(this.waveform_signal == null || this.signals == null) return;
        double ymin = Double.POSITIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;
        for(final Signal signal : this.signals){
            if(signal == null) continue;
            signal.AutoscaleY();
            if(signal.getYmin() < ymin) ymin = signal.getYmin();
            if(signal.getYmax() > ymax) ymax = signal.getYmax();
        }
        this.waveform_signal.setYmin(ymin, Signal.SIMPLE);
        this.waveform_signal.setYmax(ymax, Signal.SIMPLE);
        this.ReportChanges();
    }

    @Override
    public void Copy(final Waveform wave) {
        super.Copy(wave);
        if(!wave.is_image){
            int i;
            final MultiWaveform w = (MultiWaveform)wave;
            if(this.signals.size() != 0) this.signals.removeAllElements();
            final Vector<Signal> s = w.GetSignals();
            for(i = 0; i < s.size(); i++){
                this.signals.addElement(new Signal(s.elementAt(i)));
                if(DEBUG.D){
                    System.out.println("Copy: " + this.signals.elementAt(i));
                }
                this.signals.elementAt(i).registerSignalListener(this);
            }
            this.show_legend = w.show_legend;
            this.show_sig_image = w.show_sig_image;
            if(w.show_legend){
                this.legend_x = w.legend_x;
                this.legend_y = w.legend_y;
                this.legend_point = new Point(w.legend_point);
            }
            this.UpdateLimits();
            if(this.waveform_signal != null) super.Update(this.waveform_signal);
        }
    }

    protected void drawLegend(final Graphics g, final Point p, final int print_mode, final int orientation) {
        final Dimension d = this.getSize();
        final int h = g.getFont().getSize() + 2;
        final Color prev_col = g.getColor();
        final Point pts[] = new Point[1];
        final FontMetrics fm = this.getFontMetrics(g.getFont());
        String s;
        pts[0] = new Point();
        int curr_width = 0, sum_width = p.x;
        int curr_marker = 0;
        g.setColor(Color.black);
        if(orientation == MultiWaveform.VERTICAL) g.translate(-this.marker_width, 0);
        for(int i = 0, py = p.y + h, px = p.x; i < this.getSignalCount(); i++){
            if(!this.isSignalShow(i)) continue;
            if((print_mode & MultiWaveform.PRINT_BW) != MultiWaveform.PRINT_BW) g.setColor(this.getSignalColor(i));
            s = this.getSignalInfo(i);
            if(orientation == MultiWaveform.HORIZONTAL){
                final char s_ar[] = s.toCharArray();
                curr_width = fm.charsWidth(s_ar, 0, s_ar.length) + 3 * this.marker_width;
                if(sum_width + curr_width < d.width){
                    px = sum_width;
                    sum_width += curr_width;
                }else{
                    py += h;
                    px = p.x;
                    sum_width = p.x + curr_width;
                }
            }
            pts[0].x = px + 2 * this.marker_width;
            pts[0].y = py - this.marker_width / 2;
            this.drawMarkers(g, pts, 1, this.GetMarker(i), 1, -1);
            if((this.GetMarker(i) == Signal.NONE) && ((print_mode & MultiWaveform.PRINT_BW) == MultiWaveform.PRINT_BW)){
                this.drawMarkers(g, pts, 1, curr_marker + 1, 1, -1);
                curr_marker = (curr_marker + 1) % (Signal.markerList.length - 1);
            }
            g.drawString(s, px + 3 * this.marker_width, py);
            if(orientation == MultiWaveform.VERTICAL) py += h;
        }
        if(orientation == MultiWaveform.VERTICAL) g.translate(this.marker_width, 0);
        g.setColor(prev_col);
    }

    @Override
    protected void drawMarkers(final Graphics g, final Vector<Polygon> segments, final int mark_type, final int step, final int mode) {
        int num_points = 0;
        Point points[];
        Polygon curr_polygon;
        if(segments == null) return;
        final int num_segments = segments.size();
        for(int i = num_points = 0; i < num_segments; i++)
            num_points += segments.elementAt(i).npoints;
        points = new Point[num_points];
        for(int i = num_points = 0; i < num_segments; i++){
            curr_polygon = segments.elementAt(i);
            for(int j = 0; j < curr_polygon.npoints; j += step){
                points[num_points++] = new Point(curr_polygon.xpoints[j], curr_polygon.ypoints[j]);
            }
        }
        super.drawMarkers(g, points, num_points, mark_type, 1, mode);
    }

    protected void drawMarkers(final Graphics g, final Vector<Polygon> segments, final Signal s) {
        this.drawMarkers(g, segments, s.getMarker(), s.getMarkerStep(), s.getMode1D());
    }

    @Override
    protected void drawSignal(final Graphics g) {
        this.drawSignal(g, this.getSize(), Waveform.NO_PRINT);
    }

    @Override
    protected void drawSignal(final Graphics g, final Dimension d, final int print_mode) {
        if(this.wm == null) return;
        final int num_marker = Signal.markerList.length - 1;
        int i, j, x[], y[];
        Point curr_points[];
        Vector<Polygon> segments = null;
        float step;
        int num_steps, marker_step = 1;
        Signal s;
        g.setColor(Color.black);
        for(i = 0; i < this.signals.size(); i++){
            s = this.signals.elementAt(i);
            if(s == null) continue;
            if((print_mode & MultiWaveform.PRINT_BW) != MultiWaveform.PRINT_BW){
                marker_step = (s.getMarkerStep() > 0) ? s.getMarkerStep() : 1;
                if(s.getColor() != null) g.setColor(s.getColor());
                else g.setColor(Waveform.colors[s.getColorIdx() % Waveform.colors.length]);
            }else{
                if(s.getMarker() != Signal.NONE) marker_step = (int)(((s.getNumPoints() > 1000) ? 100 : s.getNumPoints() / 10.) + 0.5);
            }
            if(this.mode == Waveform.MODE_PAN && this.dragging && s.getNumPoints() > MultiWaveform.MAX_DRAG_POINT) // dragging large signals
            {
                int drag_point = MultiWaveform.MAX_DRAG_POINT;
                if(this.signals.size() == 1) drag_point = (s.getNumPoints() > MultiWaveform.MAX_DRAG_POINT * 3) ? MultiWaveform.MAX_DRAG_POINT * 3 : s.getNumPoints();
                x = new int[s.getNumPoints()];
                y = new int[s.getNumPoints()];
                curr_points = new Point[s.getNumPoints()];
                step = (float)s.getNumPoints() / drag_point;
                num_steps = drag_point;
                for(j = 0; j < num_steps; j++){
                    x[j] = this.wm.XPixel(s.getX((int)(step * j)), d);
                    y[j] = this.wm.YPixel(s.getY((int)(step * j)), d);
                    curr_points[j] = new Point(x[j], y[j]);
                }
                // if(s.getInterpolate())
                for(int jj = 0; jj < num_steps - 1; jj++)
                    if(!Double.isNaN(s.getY((int)(step * jj))) && !Double.isNaN(s.getY((int)(step * (jj + 1))))) g.drawLine(x[jj], y[jj], x[jj + 1], y[jj + 1]);
            }else{
                if(s.getType() == Signal.TYPE_2D && (s.getMode2D() == Signal.MODE_IMAGE || s.getMode2D() == Signal.MODE_CONTOUR)){
                    if(!(this.mode == Waveform.MODE_PAN && this.dragging)){
                        switch(s.getMode2D()){
                            case Signal.MODE_IMAGE:
                                final Image img = this.createImage(d.width, d.height);
                                this.wm.ToImage(s, img, d, this.colorProfile.colorMap);
                                g.drawImage(img, 0, 0, d.width, d.height, this);
                                break;
                            case Signal.MODE_CONTOUR:
                                this.drawSignalContour(s, g, d);
                                break;
                        }
                    }
                }else{
                    segments = this.wm.ToPolygons(s, d, this.appendDrawMode);
                    Polygon curr_polygon;
                    if(segments != null && (s.getInterpolate() || this.mode == Waveform.MODE_PAN && this.dragging)) for(int k = 0; k < segments.size(); k++){
                        curr_polygon = segments.elementAt(k);
                        g.drawPolyline(curr_polygon.xpoints, curr_polygon.ypoints, curr_polygon.npoints);
                    }
                }
            }
            if(this.dragging && this.mode == Waveform.MODE_PAN) continue;
            if(s.getMarker() != Signal.NONE && s.getMode2D() != Signal.MODE_IMAGE)
            // DrawMarkers(g, segments, s.getMarker(), marker_step);
            this.drawMarkers(g, segments, s);
            if(s.hasError()) this.drawError(s, g, d);
        }
        if((print_mode & MultiWaveform.PRINT_BW) == MultiWaveform.PRINT_BW){
            int curr_marker = 0;
            for(i = 0; i < this.signals.size(); i++){
                s = this.signals.elementAt(i);
                if(s == null) continue;
                segments = this.wm.ToPolygons(s, d, this.appendDrawMode);
                marker_step = (int)(((s.getNumPoints() > 1000) ? 100 : s.getNumPoints() / 10.) + 0.5);
                this.drawMarkers(g, segments, curr_marker + 1, marker_step, s.getMode1D());
                curr_marker = (curr_marker + 1) % num_marker;
            }
        }
        segments = null;
    }

    @Override
    public void Erase() {
        if(this.signals.size() != 0) this.signals.removeAllElements();
        this.orig_signals = null;
        this.show_legend = false;
        this.legend_point = null;
        super.Erase();
    }

    public boolean exists(final Signal s) {
        if(s == null) return true;
        if(s.getName() == null || s.getName().length() == 0){
            s.setName("Signal_" + this.signals.size());
            return false;
        }
        for(int i = 0; i < this.signals.size(); i++){
            final Signal s1 = this.signals.elementAt(i);
            if(s1.getName() != null && s.getName() != null && s1.getName().equals(s.getName())) return true;
        }
        return false;
    }

    @Override
    protected Point FindPoint(final double curr_x, final double curr_y, final boolean is_first) {
        return this.FindPoint(curr_x, curr_y, this.getWaveSize(), is_first);
    }

    @Override
    protected Point FindPoint(final double curr_x, final double curr_y, final Dimension d, final boolean is_first) {
        Signal curr_signal;
        int curr_idx = -1, i, img_idx = -1;
        double curr_dist = 0, min_dist = Double.POSITIVE_INFINITY;
        if(this.signals == null || this.signals.size() == 0) return null;
        // if(signals[curr_point_sig_idx] == null) return 0;
        if(!is_first) return this.FindPoint(this.signals.elementAt(this.curr_point_sig_idx), curr_x, curr_y, d);
        for(this.curr_point_sig_idx = i = 0; i < this.signals.size(); i++){
            curr_signal = this.signals.elementAt(i);
            if(curr_signal == null || !this.GetSignalState(i)) continue;
            curr_idx = curr_signal.FindClosestIdx(curr_x, curr_y);
            if(curr_signal.getType() == Signal.TYPE_2D && (curr_signal.getMode2D() == Signal.MODE_IMAGE || curr_signal.getMode2D() == Signal.MODE_CONTOUR)){
                final double x2D[] = curr_signal.getX2D();
                int inc = (int)(x2D.length / 10.) + 1;
                inc = (curr_idx + inc > x2D.length) ? x2D.length - curr_idx - 1 : inc;
                // if(curr_idx >= 0 && curr_idx < x2D.length) img_dist = (x2D[curr_idx] - x2D[curr_idx + inc]) * (x2D[curr_idx] - x2D[curr_idx + inc]);
                img_idx = i;
            }else{
                if(curr_signal.hasX()) curr_dist = (curr_signal.getY(curr_idx) - curr_y) * (curr_signal.getY(curr_idx) - curr_y) + (curr_signal.getX(curr_idx) - curr_x) * (curr_signal.getX(curr_idx) - curr_x);
                if(i == 0 || curr_dist < min_dist){
                    min_dist = curr_dist;
                    this.curr_point_sig_idx = i;
                }
            }
        }
        try{
            if(img_idx != -1){
                if(curr_idx != -1){
                    curr_signal = this.signals.elementAt(this.curr_point_sig_idx);
                    if(min_dist > 10 * (curr_signal.getY(0) - curr_signal.getY(1)) * (curr_signal.getY(0) - curr_signal.getY(1))){
                        this.curr_point_sig_idx = img_idx;
                    }
                }else this.curr_point_sig_idx = img_idx;
            }
        }catch(final Exception exc){}
        this.setPointSignalIndex(this.curr_point_sig_idx);
        curr_signal = this.signals.elementAt(this.curr_point_sig_idx);
        this.not_drawn = true;
        final Point p = this.FindPoint(curr_signal, curr_x, curr_y, d);
        return p;
    }

    @Override
    protected int getBottomSize() {
        return this.bottom_size;
    }

    public int GetColorIdx(final int idx) {
        if(this.is_image) return super.GetColorIdx();
        if(idx < this.signals.size()){ return this.signals.elementAt(idx).getColorIdx(); }
        return 0;
    }

    public boolean GetInterpolate(final int idx) {
        if(idx < this.signals.size()){ return this.signals.elementAt(idx).getInterpolate(); }
        return false;
    }

    protected Dimension getLegendDimension(final Graphics g, final Dimension d, final int orientation) {
        final Dimension dim = new Dimension(0, 0);
        int curr_width = 0, sum_width = 0;
        final Font f = g.getFont();
        final int h = f.getSize() + 2;
        final FontMetrics fm = this.getFontMetrics(f);
        if(this.getSignalCount() == 0) return dim;
        for(int i = 0; i < this.getSignalCount(); i++){
            if(!this.isSignalShow(i)) continue;
            final String lab = this.getSignalInfo(i);
            final char[] lab_ar = lab.toCharArray();
            curr_width = fm.charsWidth(lab_ar, 0, lab_ar.length);
            if(orientation == MultiWaveform.VERTICAL){
                curr_width += 2 * this.marker_width;
                dim.height += h;
                if(curr_width > dim.width) dim.width = curr_width;
            }
            if(orientation == MultiWaveform.HORIZONTAL){
                curr_width += 3 * this.marker_width;
                if(sum_width + curr_width < d.width){
                    sum_width += curr_width;
                }else{
                    if(sum_width > dim.width) dim.width = sum_width;
                    sum_width = curr_width;
                    dim.height += h;
                }
            }
        }
        dim.height += (orientation == MultiWaveform.HORIZONTAL) ? (int)(3. / 2 * h + 0.5) : h / 2;
        return dim;
    }

    public int getLegendMode() {
        return this.legend_mode;
    }

    public double GetLegendXPosition() {
        return this.legend_x;
    }

    public double GetLegendYPosition() {
        return this.legend_y;
    }

    public int GetMarker(final int idx) {
        if(idx < this.signals.size()){ return this.signals.elementAt(idx).getMarker(); }
        return 0;
    }

    public int GetMarkerStep(final int idx) {
        if(idx < this.signals.size() && this.signals.elementAt(idx) != null){ return this.signals.elementAt(idx).getMarkerStep(); }
        return 0;
    }

    @Override
    protected int getRightSize() {
        return this.right_size;
    }

    @Override
    protected int GetSelectedSignal() {
        return this.curr_point_sig_idx;
    }

    @Override
    public int GetShowSignalCount() {
        if(this.signals != null) return this.signals.size();
        return 0;
    }

    @Override
    public Signal GetSignal() {
        if(this.signals != null && this.signals.size() > 0) return this.signals.elementAt(this.curr_point_sig_idx);
        return null;
    }

    protected Color getSignalColor(final int i) {
        if(i > this.signals.size()) return Color.black;
        final Signal sign = this.signals.elementAt(i);
        if(sign.getColor() != null) return sign.getColor();
        return Waveform.colors[sign.getColorIdx() % Waveform.colors.length];
    }

    public int getSignalCount() {
        return this.GetShowSignalCount();
    }

    protected String getSignalInfo(final int i) {
        final Signal sign = this.signals.elementAt(i);
        String lab = sign.getName();
        if(sign.getType() == Signal.TYPE_2D){
            switch(sign.getMode2D()){
                case Signal.MODE_XZ:
                    lab = lab + " [X-Z Y = " + Waveform.ConvertToString(sign.getYinXZplot(), false) + " ]";
                    break;
                case Signal.MODE_YZ:
                    lab = lab + " [Y-Z X = " + sign.getStringOfXinYZplot() +
                    // Waveform.ConvertToString(sign.getTime(), false) +
                    " ]";
                    break;
                /*
                        case Signal.MODE_YX:
                            lab = lab + " [Y-X T = " +  sign.getStringTime() +
                               // Waveform.ConvertToString(sign.getTime(), false)
                               " ]";
                            break;
                 */
            }
        }
        return lab;
    }

    @Override
    public int getSignalMode1D() {
        return this.getSignalMode1D(this.curr_point_sig_idx);
    }

    public int getSignalMode1D(final int idx) {
        if(idx >= 0 && idx < this.signals.size()) return this.signals.elementAt(idx).getMode1D();
        return -1;
    }

    @Override
    public int getSignalMode2D() {
        return this.getSignalMode2D(this.curr_point_sig_idx);
    }

    public int getSignalMode2D(final int idx) {
        if(idx >= 0 && idx < this.signals.size()) return this.signals.elementAt(idx).getMode2D();
        return -1;
    }

    public String getSignalName(final int idx) {
        if(idx < this.signals.size() && this.signals.elementAt(idx) != null){
            final Signal s = this.signals.elementAt(idx);
            if(s.getName() == null) s.setName(new String("Signal_" + idx));
            return s.getName();
        }else if(this.is_image && this.frames != null) return this.frames.getName();
        return null;
    }

    public Vector<Signal> GetSignals() {
        return this.signals;
    }

    public String[] GetSignalsName() {
        try{
            final String names[] = new String[this.signals.size()];
            String n;
            Signal s;
            for(int i = 0; i < this.signals.size(); i++){
                s = this.signals.elementAt(i);
                n = s.getName();
                if(n != null) names[i] = n;
                else{
                    names[i] = new String("Signal_" + i);
                    s.setName(names[i]);
                }
            }
            return names;
        }catch(final Exception e){
            return null;
        }
    }

    public boolean[] GetSignalsState() {
        boolean s_state[] = null;
        if(this.signals != null){
            s_state = new boolean[this.signals.size()];
            for(int i = 0; i < this.signals.size(); i++)
                s_state[i] = this.GetSignalState(i);
        }
        return s_state;
    }

    public boolean GetSignalState(final int idx) {
        if(idx > this.signals.size()) return false;
        final Signal s = this.signals.elementAt(idx);
        if(s == null) return false;
        return !(!s.getInterpolate() && s.getMarker() == Signal.NONE);
    }

    @Override
    public int getSignalType() {
        return this.getSignalType(this.curr_point_sig_idx);
    }

    /*
        public int getSignalMode(int idx)
        {
            int mode = -1;
            if (idx >= 0 && idx < signals.size())
            {
                Signal s = (Signal) signals.elementAt(idx);
                if (s.getType() == Signal.TYPE_1D)
                    mode = ( (Signal) signals.elementAt(idx)).getMode1D();
                else
                if (s.getType() == Signal.TYPE_2D)
                    mode = ( (Signal) signals.elementAt(idx)).getMode2D();

            }
            return mode;
        }

        public int getSignalMode()
        {
            return getSignalMode(curr_point_sig_idx);
        }
     */
    public int getSignalType(final int idx) {
        if((idx >= 0 && idx < this.signals.size()) && (this.signals.elementAt(idx) != null)) return this.signals.elementAt(idx).getType();
        return -1;
    }

    public WaveInterface getWaveInterface() {
        return this.wi;
    }

    @Override
    protected void HandleCopy() {
        /*
                 if(IsCopySelected())
         return;
             if(signals != null && signals.length != 0 && controller.GetCopySource() == null
             || is_image && frames != null && controller.GetCopySource() == null )
                 {
         controller.SetCopySource(this);
         SetCopySelected(true);
                 }
         */
    }

    @Override
    protected void HandlePaste() {
        /*
                 if(IsCopySelected())
                 {
         SetCopySelected(false);
         controller.SetCopySource(null);
                 }
                 else
                 {
         if(controller.GetCopySource() != null)
             controller.NotifyChange(this, controller.GetCopySource());
                 }
         */
    }

    public boolean isFixedLegend() {
        return this.fixed_legend;
    }

    public boolean IsShowLegend() {
        return this.show_legend;
    }

    protected boolean isSignalShow(final int i) {
        final Signal sign = this.signals.elementAt(i);
        return(sign != null && (sign.getInterpolate() || sign.getMarker() != Signal.NONE));
    }

    @Override
    protected void NotifyZoom(final double start_xs, final double end_xs, final double start_ys, final double end_ys, final int timestamp) {
        if(this.orig_signals == null){
            this.orig_signals = new Vector<Signal>();
            for(int i = 0; i < this.signals.size(); i++)
                this.orig_signals.addElement(this.signals.elementAt(i));
            this.orig_xmin = this.waveform_signal.getXmin();
            this.orig_xmax = this.waveform_signal.getXmax();
        }
    }

    @Override
    synchronized public void paint(final Graphics g, final Dimension d, final int print_mode) {
        this.bottom_size = this.right_size = 0;
        if(this.fixed_legend && this.show_legend || (print_mode & MultiWaveform.PRINT_LEGEND) == MultiWaveform.PRINT_LEGEND){
            Waveform.setFont(g);
            if(this.legend_mode == MultiWaveform.LEGEND_BOTTOM){
                final Dimension dim = this.getLegendDimension(g, d, MultiWaveform.HORIZONTAL);
                this.bottom_size = dim.height;
                g.drawLine(0, dim.height - 1, d.width, dim.height - 1);
            }
            if(this.legend_mode == MultiWaveform.LEGEND_RIGHT){
                final Dimension dim = this.getLegendDimension(g, d, MultiWaveform.VERTICAL);
                this.right_size = dim.width;
                g.drawLine(dim.width - 1, 0, dim.width - 1, d.height);
            }
        }
        super.paint(g, d, print_mode);
    }

    @Override
    protected void PaintSignal(final Graphics g, final Dimension d, final int print_mode) {
        Dimension dim;
        if(print_mode == Waveform.NO_PRINT) dim = this.getWaveSize();
        else dim = this.getPrintWaveSize(d);
        super.PaintSignal(g, d, print_mode);
        if(this.show_legend && !this.fixed_legend && !this.is_min_size){
            Point p = new Point();
            if(this.legend_point == null || this.prev_width != d.width || this.prev_height != d.height){
                p.x = this.wm.XPixel(this.legend_x, dim);
                p.y = this.wm.YPixel(this.legend_y, dim);
                this.legend_point = p;
            }else{
                p = this.legend_point;
            }
            this.drawLegend(g, p, print_mode, MultiWaveform.VERTICAL);
        }
        if(this.fixed_legend && this.show_legend || (print_mode & MultiWaveform.PRINT_LEGEND) == MultiWaveform.PRINT_LEGEND){
            g.setClip(0, 0, d.width, d.height);
            if(this.legend_mode == MultiWaveform.LEGEND_BOTTOM && this.bottom_size != 0) this.drawLegend(g, new Point(0, dim.height), print_mode, MultiWaveform.HORIZONTAL);
            if(this.legend_mode == MultiWaveform.LEGEND_RIGHT && this.right_size != 0) this.drawLegend(g, new Point(dim.width, 0), print_mode, MultiWaveform.VERTICAL);
        }
    }

    public void RemoveLegend() {
        this.show_legend = false;
        this.not_drawn = true;
        this.repaint();
    }

    public void removeSignal(final int idx) {
        if(idx < this.signals.size()) this.signals.removeElementAt(idx);
    }

    public void replaceSignal(final int idx, final Signal s) {
        if(idx < this.signals.size()){
            this.signals.removeElementAt(idx);
            this.signals.insertElementAt(s, idx);
        }
    }

    @Override
    protected void ReportLimits(final ZoomRegion r, final boolean add_undo) {
        this.continuosAutoscale = false;
        if(!add_undo){
            if(this.waveform_signal == null) return;
            this.update_timestamp++;
            if(this.signals == null) return;
            if(this.orig_signals != null) // Previous zoom
            {
                this.signals = this.orig_signals;
                this.orig_signals = null;
            }
        }
        super.ReportLimits(r, add_undo);
        if(add_undo) this.NotifyZoom(r.start_xs, r.end_xs, r.start_ys, r.end_ys, this.update_timestamp);
    }

    @Override
    public void ResetScales() {
        if(this.signals == null || this.waveform_signal == null) return;
        if(this.orig_signals != null){
            this.signals = this.orig_signals;
            this.orig_signals = null;
        }
        for(final Signal signal : this.signals)
            if(signal != null) signal.ResetScales();
        this.setLimits(Signal.AT_CREATION);
        this.waveform_signal.ResetScales();
        this.ReportChanges();
    }

    public void SetColorIdx(final int idx, final int color_idx) {
        if(this.is_image){
            super.SetColorIdx(color_idx);
            super.SetCrosshairColor(color_idx);
            return;
        }
        if(idx < this.signals.size()){
            this.signals.elementAt(idx).setColorIdx(color_idx);
            if(idx == this.curr_point_sig_idx) this.crosshair_color = Waveform.colors[color_idx % Waveform.colors.length];
        }
    }

    public void SetInterpolate(final int idx, final boolean interpolate) {
        if(idx < this.signals.size()){
            (this.signals.elementAt(idx)).setInterpolate(interpolate);
        }
    }

    public void SetLegend(final Point p) {
        final Dimension d = this.getSize();
        this.legend_x = this.wm.XValue(p.x, d);
        this.legend_y = this.wm.YValue(p.y, d);
        this.legend_point = new Point(p);
        this.show_legend = true;
        this.not_drawn = true;
        this.repaint();
    }

    public void setLegendMode(final int legend_mode) {
        this.legend_mode = legend_mode;
        if(legend_mode != MultiWaveform.LEGEND_IN_GRAPHICS) this.fixed_legend = true;
        else this.fixed_legend = false;
    }

    protected void setLimits() {
        this.setXlimits(this.lx_min, this.lx_max);
        this.setYlimits(this.ly_min, this.ly_max);
        this.UpdateLimits();
        this.change_limits = true;
    }

    private void setLimits(final int mode) {
        boolean anyLongX = false;
        for(int i = 1; i < this.signals.size(); i++){
            if(this.signals.elementAt(i) == null) continue;
            if(this.signals.elementAt(i).isLongX()){
                anyLongX = true;
                this.waveform_signal.setXLimits(this.signals.elementAt(i).getXmin(), this.signals.elementAt(i).getXmax(), mode);
                break;
            }
        }
        double xmin = Double.POSITIVE_INFINITY, ymin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;
        for(final Signal signal : this.signals){
            if(signal == null) continue;
            if(anyLongX && !signal.isLongX()) continue;
            if(signal.getXmin() < xmin) xmin = signal.getXmin();
            if(signal.getXmax() > xmax) xmax = signal.getXmax();
            if(signal.getYmin() < ymin) ymin = signal.getYmin();
            if(signal.getYmax() > ymax) ymax = signal.getYmax();
        }
        this.waveform_signal.setXLimits(xmin, xmax, mode);
        this.waveform_signal.setYmax(ymax, mode);
        this.waveform_signal.setYmin(ymin, mode);
    }

    public void SetMarker(final int idx, final int marker) {
        if(idx < this.signals.size()){
            this.signals.elementAt(idx).setMarker(marker);
        }
    }

    public void SetMarkerStep(final int idx, final int marker_step) {
        if(idx < this.signals.size()){
            this.signals.elementAt(idx).setMarkerStep(marker_step);
        }
    }

    @Override
    public void SetMode(final int mod) {
        super.SetMode(mod);
    }

    public void setPointSignalIndex(final int idx) {
        if(idx >= 0 && idx < this.signals.size()){
            Signal curr_signal;
            this.curr_point_sig_idx = idx;
            curr_signal = this.signals.elementAt(this.curr_point_sig_idx);
            if(curr_signal == null) return;
            if(curr_signal.getColor() != null) this.crosshair_color = curr_signal.getColor();
            else this.crosshair_color = Waveform.colors[curr_signal.getColorIdx() % Waveform.colors.length];
        }
    }

    public void setShowLegend(final boolean show_legend) {
        this.show_legend = show_legend;
    }

    public void setSignalMode(final int mode) {
        this.setSignalMode(this.curr_point_sig_idx, mode);
    }

    public void setSignalMode(final int idx, final int mode) {
        if(idx >= 0 && idx < this.signals.size()){
            final Signal s = this.signals.elementAt(idx);
            if(s != null){
                if(s.getType() == Signal.TYPE_1D){
                    s.setMode1D(mode);
                }else{
                    if(s.getType() == Signal.TYPE_2D){
                        switch(mode){
                            case Signal.MODE_XZ:
                                s.setMode2D(mode, (float)this.wave_point_y);
                                break;
                            case Signal.MODE_YZ:
                                s.setMode2D(mode, (float)this.wave_point_x);
                                break;
                            case Signal.MODE_IMAGE:
                                s.setMode2D(mode, (float)this.wave_point_x);
                                break;
                            case Signal.MODE_CONTOUR:
                                s.setMode2D(mode, (float)this.wave_point_x);
                                break;
                        }
                        this.sendUpdateEvent();
                        this.Autoscale();
                        /*
                        if (mode == Signal.MODE_XZ &&
                            s.getMode2D() == Signal.MODE_YZ)
                            s.setMode2D(mode, (float) wave_point_y);
                        else
                            s.setMode2D(mode, (float) wave_point_x);
                         */
                    }
                }
            }
        }
        this.not_drawn = true;
        this.repaint();
    }

    public void SetSignalState(final String label, final boolean state) {
        Signal sig;
        if(this.signals != null){
            for(int i = 0; i < this.signals.size(); i++){
                sig = this.signals.elementAt(i);
                if(sig == null) continue;
                if(sig.getName().equals(label)){
                    sig.setInterpolate(state);
                    sig.setMarker(Signal.NONE);
                }
            }
            if(this.mode == Waveform.MODE_POINT){
                final Dimension d = this.getSize();
                final double curr_x = this.wm.XValue(this.end_x, d), curr_y = this.wm.YValue(this.end_y, d);
                this.FindPoint(curr_x, curr_y, true);
            }
        }
    }

    public void setWaveInterface(final WaveInterface wi) {
        this.wi = wi;
    }

    @Override
    public void setXlimits(final double xmin, final double xmax) {
        if(this.signals == null) return;
        Signal s;
        for(int i = 0; i < this.signals.size(); i++){
            s = this.signals.elementAt(i);
            if(s != null) s.setXLimits(xmin, xmax, Signal.SIMPLE);
        }
    }

    @Override
    public void SetXScale(final Waveform w) {
        if(this.waveform_signal == null) return;
        this.waveform_signal.setXLimits(w.waveform_signal.getXmin(), w.waveform_signal.getXmax(), Signal.SIMPLE);
        for(int i = 0; i < this.signals.size(); i++){
            if(this.signals.elementAt(i) == null) continue;
            this.signals.elementAt(i).setXLimits(w.waveform_signal.getXmin(), w.waveform_signal.getXmax(), Signal.SIMPLE);
        }
        this.ReportChanges();
    }

    @Override
    public void SetXScaleAutoY(final Waveform w) {
        if(this.waveform_signal == null || this.signals == null) return;
        if(w != this && this.orig_signals != null) // Previous zoom for different windows
        {
            this.signals = this.orig_signals;
            // operation on signals must not affect original signals
            this.orig_signals = new Vector<Signal>();
            for(int i = 0; i < this.signals.size(); i++)
                this.orig_signals.addElement(this.signals.elementAt(i));
        }
        this.waveform_signal.setXLimits(w.waveform_signal.getXmin(), w.waveform_signal.getXmax(), Signal.SIMPLE);
        for(int i = 0; i < this.signals.size(); i++){
            if(this.signals.elementAt(i) == null) continue;
            this.signals.elementAt(i).setXLimits(w.waveform_signal.getXmin(), w.waveform_signal.getXmax(), Signal.SIMPLE);
        }
        this.AutoscaleY();
        this.update_timestamp++;
        this.asinchAutoscale = true;
        this.NotifyZoom(this.waveform_signal.getXmin(), this.waveform_signal.getXmax(), this.waveform_signal.getYmin(), this.waveform_signal.getYmax(), this.update_timestamp);
    }

    @Override
    public void setYlimits(final float ymin, final float ymax) {
        if(this.signals == null) return;
        Signal s;
        for(int i = 0; i < this.signals.size(); i++){
            s = this.signals.elementAt(i);
            s.setYlimits(ymin, ymax);
        }
    }

    // Inherits from parent in order to force UpdateLimits
    @Override
    public void signalUpdated(final boolean changeLimits) {
        this.change_limits = changeLimits;
        this.not_drawn = true;
        // Check if any of the elements of signals vector refers to absolute time.
        // In this case set minimum and maximum X value of reference waveform_signal to its limits
        this.setLimits(Signal.SIMPLE);
        this.repaint();
    }

    @Override
    public void Update() {
        if(!this.is_image){
            this.UpdateLimits();
            if(this.waveform_signal != null){
                this.curr_point_sig_idx = 0;
                super.Update(this.waveform_signal);
            }else{
                this.not_drawn = true;
                this.repaint();
            }
        }else{
            if(this.frames != null) super.Update();
        }
    }

    public void Update(final Frames frames) {
        this.frames = frames;
        this.is_image = true;
        this.Update();
    }

    public void Update(final Signal signals[]) {
        int i;
        if(signals == null) return;
        if(this.signals.size() != 0) this.signals.removeAllElements();
        for(i = 0; i < signals.length; i++){
            /*
            if(signals[i].getType() == Signal.TYPE_2D && signals[i].getMode2D() == Signal.MODE_IMAGE)
                 hasSignalImage = true;
             */
            this.signals.addElement(signals[i]);
            if(signals[i] != null) signals[i].registerSignalListener(this);
        }
        MultiWaveform.this.Update();
    }

    void UpdateLimits() {
        if(DEBUG.D) System.out.println("MultiWaveform.UpdateLimits()");
        if(this.signals == null || this.signals.size() == 0) return;
        int i;
        this.waveform_signal = null;
        if(this.curr_point_sig_idx == -1 || this.curr_point_sig_idx >= this.signals.size() || this.signals.elementAt(this.curr_point_sig_idx) == null){
            for(i = 0; i < this.signals.size(); i++)
                if(this.signals.elementAt(i) != null) break;
            if(i == this.signals.size()) return;
        }else i = this.curr_point_sig_idx;
        this.waveform_signal = new Signal(this.signals.elementAt(i));
        // Check if any of the elements of signals vector refers to absolute time.
        // In this case set minimum and maximum X value of reference waveform_signal to its limits
        this.setLimits(Signal.AT_CREATION);
    }

    @Override
    public void UpdatePoint(final double curr_x) {
        this.UpdatePoint(curr_x, Double.NaN);
    }

    @Override
    public synchronized void UpdatePoint(final double curr_x, final double curr_y) {
        if(!this.is_image){
            // if(wm == null) { System.out.println("wm == null"); return;}
            // if(dragging || mode != MODE_POINT || signals == null || signals.size() == 0)
            if(this.mode != Waveform.MODE_POINT || this.signals == null || this.signals.size() == 0) return;
            Signal s;
            for(int i = 0; i < this.signals.size(); i++){
                s = this.signals.elementAt(i);
                if(s == null) continue;
                if(s.getType() == Signal.TYPE_2D && s.getMode2D() == Signal.MODE_YZ){
                    s.showYZ(curr_x);
                    this.not_drawn = true;
                }
                /*   if ( s.getType() == Signal.TYPE_2D && s.getMode2D() == Signal.MODE_PROFILE )
                   {
                       s.showProfile(s.getMode2D(), (float) curr_x);
                       not_drawn = true;
                   }
                 */
            }
        }
        super.UpdatePoint(curr_x, curr_y);
    }

    public synchronized void UpdateSignals(final Signal signals[], final int timestamp, final boolean force_update) {
        // System.out.println("timestamp "+update_timestamp + " "+ timestamp);
        Signal s;
        if(!force_update && this.update_timestamp != timestamp) return;
        for(int i = 0; i < signals.length; i++){
            s = this.signals.elementAt(i);
            signals[i].setAttributes(s);
        }
        if(this.signals.size() != 0) this.signals.removeAllElements();
        for(final Signal signal : signals){
            this.signals.addElement(signal);
            if(signal != null) signal.registerSignalListener(this);
        }
        if(force_update){
            this.SetEnabledDispatchEvents(false);
            this.UpdateLimits();
        }
        if(this.waveform_signal != null){
            if(this.curr_point_sig_idx > signals.length || this.curr_point_sig_idx == -1) this.curr_point_sig_idx = 0;
            if(force_update && this.continuosAutoscale) super.Update(this.waveform_signal);
            else super.UpdateSignal(this.waveform_signal);
        }
        if(this.asinchAutoscale){
            this.Autoscale();
            this.asinchAutoscale = false;
        }
        if(force_update) this.SetEnabledDispatchEvents(true);
    }
}

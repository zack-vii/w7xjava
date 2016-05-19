package jScope;

/* $Id$ */
import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import debug.DEBUG;

/**
 * The DataSignal class encapsulates a description of a are : name, marker, point step of marker, color index from an external color pallet, measure point error, offset and gain values. DataSignal is defined in a rectangular region.
 *
 * @see Waveform
 * @see MultiWaveform
 */
public final class Signal implements WaveDataListener{
    private static final class RegionDescriptor{
        double lowerBound, upperBound;
        double resolution;

        RegionDescriptor(final double lowerBound, final double upperBound, final double resolution){
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.resolution = resolution; // Number of points for this region / (upperBound - lowerBound)
        }
    }
    public static final class ResolutionManager{
        Vector<RegionDescriptor> lowResRegions = new Vector<RegionDescriptor>();

        ResolutionManager(){}

        ResolutionManager(final ResolutionManager rm){
            for(int i = 0; i < rm.lowResRegions.size(); i++)
                this.lowResRegions.addElement(rm.lowResRegions.elementAt(i));
        }

        protected final void addRegion(final RegionDescriptor newReg) {
            // New regions can only have increased resolution in case they intersect previous regions
            // Skip disjoint regions with lower bounds
            if(newReg.upperBound < newReg.lowerBound) System.err.println("# INTERNAL ERROR: LOWER BOUND > UPPER BOUND!!!!!");
            int idx;
            RegionDescriptor currRegion;
            for(idx = 0; idx < this.lowResRegions.size(); idx++){
                currRegion = this.lowResRegions.elementAt(idx);
                if(currRegion.upperBound > newReg.lowerBound) break;
            }
            if(idx == this.lowResRegions.size()) // All regions with lower bounds
            {
                if(DEBUG.D) System.out.println("Added region (" + newReg.lowerBound + "," + newReg.upperBound + "," + newReg.resolution + ") at bottom");
                this.lowResRegions.addElement(newReg);
                return;
            }
            // Check if the first region which is not all before this region has any intersection
            currRegion = this.lowResRegions.elementAt(idx);
            if(currRegion.lowerBound < newReg.lowerBound){
                if(currRegion.upperBound <= newReg.upperBound){
                    currRegion.upperBound = newReg.lowerBound;
                    if(DEBUG.D) System.out.println("updated region (" + currRegion.lowerBound + "," + currRegion.upperBound + ") ");
                    idx++;
                }else // The new region is completely contained in currRegion
                {
                    final double prevUpper = currRegion.upperBound;
                    currRegion.upperBound = newReg.lowerBound;
                    if(DEBUG.D) System.out.println("Updated region (" + currRegion.lowerBound + "," + currRegion.upperBound + ") ");
                    idx++;
                    this.lowResRegions.insertElementAt(newReg, idx);
                    if(DEBUG.D) System.out.println("Added region (" + newReg.lowerBound + "," + newReg.upperBound + "," + newReg.resolution + ")");
                    idx++;
                    this.lowResRegions.insertElementAt(new RegionDescriptor(newReg.upperBound, prevUpper, currRegion.resolution), idx);
                    if(DEBUG.D) System.out.println("Added region (" + newReg.upperBound + "," + prevUpper + "," + currRegion.resolution + ")");
                    return; // done in this case
                }
            }
            // Remove regions completely contained in the new one
            while(idx < this.lowResRegions.size() && this.lowResRegions.elementAt(idx).upperBound <= newReg.upperBound){
                if(DEBUG.D) System.out.println("Removed region (" + this.lowResRegions.elementAt(idx).lowerBound + "," + this.lowResRegions.elementAt(idx).upperBound + ")");
                this.lowResRegions.removeElementAt(idx);
            }
            // In case there is a overlapped region, adjust its lower bound
            if(idx < this.lowResRegions.size() && this.lowResRegions.elementAt(idx).lowerBound < newReg.upperBound){
                this.lowResRegions.elementAt(idx).lowerBound = newReg.upperBound;
                if(DEBUG.D) System.out.println("Updated region (" + this.lowResRegions.elementAt(idx).lowerBound + "," + this.lowResRegions.elementAt(idx).upperBound + ")");
            }
            this.lowResRegions.insertElementAt(newReg, idx);
            if(DEBUG.D) System.out.println("Added region (" + newReg.lowerBound + "," + newReg.upperBound + "," + newReg.resolution + ")");
            // Merge adjacent regions with same resolution (may happens due to the interval enlargements which occur in zooms)
            idx = 1;
            while(idx < this.lowResRegions.size()){
                final RegionDescriptor currReg = this.lowResRegions.elementAt(idx);
                final RegionDescriptor prevReg = this.lowResRegions.elementAt(idx - 1);
                if(prevReg.upperBound == currReg.lowerBound && prevReg.resolution == currReg.resolution){
                    if(DEBUG.D) System.out.println("Regions at (" + prevReg.lowerBound + "," + prevReg.upperBound + ")  (" + currReg.lowerBound + "," + currReg.upperBound + ") merged");
                    prevReg.upperBound = currReg.upperBound;
                    this.lowResRegions.removeElementAt(idx);
                }else idx++;
            }
        }

        protected final void appendRegion(final RegionDescriptor newReg) {
            if(newReg.upperBound < newReg.lowerBound) System.err.println("# INTERNAL ERROR IN APPEND: LOWER BOUND > UPPER BOUND!!!!!");
            if(this.lowResRegions.size() == 0){
                this.lowResRegions.addElement(newReg);
                return;
            }
            final RegionDescriptor lastReg = this.lowResRegions.elementAt(this.lowResRegions.size() - 1);
            if(lastReg.resolution == newReg.resolution) lastReg.upperBound = newReg.upperBound;
            else{
                if(lastReg.upperBound > newReg.lowerBound){
                    // System.err.println("# Warning: INTERNAL ERROR IN APPEND: NEW.LOWERBOUND < LAST.UPPERBOUND");
                    newReg.lowerBound = lastReg.upperBound;
                }
                this.lowResRegions.addElement(newReg);
            }
        }

        // Check if the passed interval intersects any low resolution region
        protected final Vector<RegionDescriptor> getLowerResRegions(final double lowerInt, final double upperInt, final double resolution) {
            final Vector<RegionDescriptor> retRegions = new Vector<RegionDescriptor>();
            for(int i = 0; i < this.lowResRegions.size(); i++){
                // 3 cases to be handled
                final RegionDescriptor currReg = this.lowResRegions.elementAt(i);
                // 1) Lower bound is within interval
                if(currReg.lowerBound < upperInt && currReg.lowerBound > lowerInt){
                    if(DEBUG.D) System.out.println("CASE 1: Lower bound is within interval for region " + i + "  its resolution: " + currReg.resolution + " in resolution: " + resolution);
                    if(currReg.resolution < resolution){
                        // Adjust upper bound
                        double currUpper = currReg.upperBound;
                        if(currUpper > upperInt) currUpper = upperInt;
                        if(DEBUG.D) System.out.println("Added Region lower: " + currReg.lowerBound + "  upper: " + currUpper + " resoluton: " + resolution);
                        retRegions.addElement(new RegionDescriptor(currReg.lowerBound, currUpper, resolution));
                    }
                }
                // 2) Upper bound is within interval
                else if(currReg.upperBound < upperInt && currReg.upperBound > lowerInt){
                    if(DEBUG.D) System.out.println("CASE 2: Upper bound is within interval for region " + i);
                    if(currReg.resolution < resolution){
                        // Adjust lower bound
                        double currLower = currReg.lowerBound;
                        if(currLower < lowerInt) currLower = lowerInt;
                        retRegions.addElement(new RegionDescriptor(currLower, currReg.upperBound, resolution));
                    }
                }
                // 3) The interval is fully within the current region
                else if(currReg.lowerBound < lowerInt && currReg.upperBound > upperInt){
                    if(DEBUG.D) System.out.println("CASE 3: UThe interval is fully within the current region for region " + i);
                    if(currReg.resolution < resolution){
                        retRegions.addElement(new RegionDescriptor(lowerInt, upperInt, resolution));
                    }
                }
            }
            return retRegions;
        }

        protected final double[] getMinMaxX() {
            final double limits[] = new double[2];
            limits[0] = this.lowResRegions.elementAt(0).lowerBound;
            limits[1] = this.lowResRegions.elementAt(this.lowResRegions.size() - 1).upperBound;
            return limits;
        }

        protected final boolean isEmpty() {
            return this.lowResRegions.size() == 0;
        }

        protected final void resetRegions() {
            this.lowResRegions.clear();
        }
    } // End inner class ResolutionManager
    public static final int      AT_CREATION           = 1;
    public static final int      CIRCLE                = 2;
    public static final int      CROSS                 = 3;
    public static final int      DEFAULT_CONTOUR_LEVEL = 20;
    public static final int      DEFAULT_INC_SIZE      = 10000;
    public static final int      DO_NOT_UPDATE         = 4;
    public static final int      FIXED_LIMIT           = 2;
    public static final int      FUSO                  = 0;
    public static final String[] markerList            = new String[]{"None", "Square", "Circle", "Cross", "Triangle", "Point"};
    public static final int[]    markerStepList        = new int[]{1, 5, 10, 20, 50, 100};
    public static final int      MODE_CONTOUR          = 2;
    public static final int      MODE_IMAGE            = 3;
    public static final int      MODE_LINE             = 0;
    public static final int      MODE_NOLINE           = 2;
    public static final int      MODE_ONDINE           = 4;
    public static final int      MODE_PROFILE          = 5;
    public static final int      MODE_STEP             = 3;
    public static final int      MODE_XZ               = 0;
    public static final int      MODE_YZ               = 1;
    public static final int      NONE                  = 0;
    public static final int      NOT_FREEZED           = 0, FREEZED_BLOCK = 1, FREEZED_SCROLL = 2;
    public static final int      NUM_POINTS            = 2000;
    public static final int      POINT                 = 5;
    public static final int      SIMPLE                = 0;
    public static final int      SQUARE                = 1;
    public static final int      TRIANGLE              = 4;
    public static final int      TYPE_1D               = 0;
    public static final int      TYPE_2D               = 1;

    private static final double[] appendArray(final double arr1[], final int sizeUsed, final double arr2[], final int incSize) {
        /*
         * float arr[] = new float[arr1.length]; for(int i = 0; i < arr1.length; i++) arr[i] = (float)arr1[i]; return appendArray(arr, sizeUsed, arr2, incSize);
         */
        if(arr1 == null) return arr2.clone();
        if(arr2 == null) return arr1.clone();
        double val[];
        if(arr1.length < sizeUsed + arr2.length){
            val = new double[arr1.length + arr2.length + incSize];
            System.arraycopy(arr1, 0, val, 0, sizeUsed);
        }else val = arr1;
        System.arraycopy(arr2, 0, val, sizeUsed, arr2.length);
        return val;
    }

    private static final float[] appendArray(final float arr1[], final int sizeUsed, final float arr2[], final int incSize) {
        if(arr1 == null) return arr2.clone();
        if(arr2 == null) return arr1.clone();
        float val[];
        if(arr1.length < sizeUsed + arr2.length){
            val = new float[arr1.length + arr2.length + incSize];
            System.arraycopy(arr1, 0, val, 0, sizeUsed);
        }else val = arr1;
        System.arraycopy(arr2, 0, val, sizeUsed, arr2.length);
        return val;
    }

    private static final int findIndex(final double d[], final double v, final int pIdx) {
        int i;
        if(v > d[pIdx]){
            for(i = pIdx; i < d.length && d[i] < v; i++);
            if(i-- > 0) return i;
            return 0;
        }
        if(v < d[pIdx]){
            for(i = pIdx; i > 0 && d[i] > v; i--);
            if(++i < d.length) return i;
            return d.length - 1;
        }
        return pIdx;
    }

    private static final int findIndex(final float d[], final double vd, final int pIdx) {
        final float v = (float)vd;
        int i;
        if(v > d[pIdx]){
            for(i = pIdx; i < d.length && d[i] < v; i++);
            if(i-- > 0) return i;
            return 0;
        }
        if(v < d[pIdx]){
            for(i = pIdx; i > 0 && d[i] > v; i--);
            if(++i < d.length) return i;
            return d.length - 1;
        }
        return pIdx;
    }

    private static final int getArrayIndex(final double arr[], final double d) {
        int i = -1;
        if(i == -1){
            for(i = 0; i < arr.length - 1; i++){
                if((d > arr[i] && d < arr[i + 1]) || d == arr[i]) break;
            }
        }
        return i;
    }

    private static final int getArrayIndex(final float arr[], final double d) {
        int i = -1;
        if(i == -1){
            for(i = 0; i < arr.length - 1; i++){
                if((d > arr[i] && d < arr[i + 1]) || d == arr[i]) break;
            }
        }
        return i;
    }

    private static final String toStringTime(final long time) {
        final DateFormat df = new SimpleDateFormat("HH:mm:sss");
        final Date date = new Date();
        date.setTime(time);
        return df.format(date).toString();
    }
    private boolean                                asym_error;
    private Color                                  color              = null;
    private int                                    color_idx          = 0;
    private double                                 contourLevels[];
    private Vector<Float>                          contourLevelValues = new Vector<Float>();
    private Vector<Vector<Vector<Point2D.Double>>> contourSignals     = new Vector<Vector<Vector<Point2D.Double>>>();
    private ContourSignal                          cs;
    private int                                    curr_x_yz_idx      = -1;
    private double                                 curr_x_yz_plot     = Double.NaN;
    private double                                 curr_xmax;
    private double                                 curr_xmin;
    private int                                    curr_y_xz_idx      = -1;
    private float                                  curr_y_xz_plot     = Float.NaN;
    private WaveData                               data;
    private boolean                                error;
    private boolean                                find_NaN           = false;
    private boolean                                fix_xmax           = false;
    private boolean                                fix_xmin           = false;
    private boolean                                fix_ymax           = false;
    private boolean                                fix_ymin           = false;
    private double                                 freezedXMax;
    private double                                 freezedXMin;
    private int                                    freezeMode         = Signal.NOT_FREEZED;
    // private boolean full_load = false; // True if signal is re-sampled on server side to reduce net load
    private float                                  gain               = 1.0F;
    private int                                    img_xprev          = 0;
    private int                                    img_yprev          = 0;
    private boolean                                increasing_x       = true;
    private boolean                                interpolate        = true;
    private String                                 legend             = null;
    private WaveData                               low_errorData;
    private float                                  lowError[];
    private int                                    marker             = Signal.NONE;
    private int                                    marker_step        = 1;
    private int                                    mode1D;
    private int                                    mode2D;
    private int                                    n_nans             = 0;
    private String                                 name;
    private int                                    nans[];
    private float                                  offset             = 0.0F;
    private final Vector<XYData>                   pendingUpdatesV    = new Vector<XYData>();
    private int                                    prev_idx           = 0;
    private ResolutionManager                      resolutionManager  = new ResolutionManager();
    private double                                 saved_xmax         = Double.POSITIVE_INFINITY;
    private double                                 saved_xmin         = Double.NEGATIVE_INFINITY;
    private double                                 saved_ymax         = Double.POSITIVE_INFINITY;
    private double                                 saved_ymin         = Double.NEGATIVE_INFINITY;
    private Vector<SignalListener>                 signalListeners    = new Vector<SignalListener>();
    private double[]                               sliceX;
    private float[]                                sliceY;
    private int                                    startIndexToUpdate = 0;
    private double                                 t_xmax;
    private double                                 t_xmin;
    private double                                 t_ymax;
    private double                                 t_ymin;
    private String                                 title;
    private int                                    type               = Signal.TYPE_1D;
    private WaveData                               up_errorData;
    private int                                    updSignalSizeInc;
    private float                                  upError[];
    private double[]                               x;
    private WaveData                               x_data;
    private double                                 x2D_max;
    private double                                 x2D_min;
    private int                                    x2D_points         = 0;
    private String                                 xlabel;
    private boolean                                xLimitsInitialized = false;
    private long[]                                 xLong;
    private double                                 xmax               = Double.NEGATIVE_INFINITY;
    private double                                 xmin               = Double.POSITIVE_INFINITY;
    private double[]                               xY2D;
    private float[]                                y;
    private double                                 y2D_max;
    private double                                 y2D_min;
    private int                                    y2D_points         = 0;
    private String                                 ylabel;
    private double                                 ymax               = Double.NEGATIVE_INFINITY;
    private double                                 ymin               = Double.POSITIVE_INFINITY;
    private float[]                                yY2D;
    private float[]                                z;
    private double                                 z_value            = Double.NaN;
    private double                                 z2D_max;
    private double                                 z2D_min;
    private int                                    z2D_points         = 0;
    private String                                 zlabel;
    private float                                  zY2D[];

    /**
     * Constructs a zero Signal with 2 points.
     */
    public Signal(){
        this.error = this.asym_error = false;
        this.data = new XYWaveData(new double[]{0., 1.}, new float[]{0, 0});
        this.setAxis();
        this.saved_xmin = this.curr_xmin = this.xmin;
        this.saved_xmax = this.curr_xmax = this.xmax;
        this.increasing_x = true;
    }

    public Signal(final double _x[], final float _y[]){
        this.error = this.asym_error = false;
        this.data = new XYWaveData(_x, _y, (_x.length < _y.length) ? _x.length : _y.length);
        this.setAxis();
        this.saved_xmin = this.curr_xmin = this.xmin;
        this.saved_xmax = this.curr_xmax = this.xmax;
        this.saved_ymin = this.ymin;
        this.saved_ymax = this.ymax;
        this.checkIncreasingX();
    }

    /**
     * Constructs and initialize a Signal with x and y array.
     *
     * @param _x
     *            an array of x coordinates
     * @param _y
     *            an array of y coordinates
     */
    public Signal(final float _x[], final float _y[]){
        this.error = this.asym_error = false;
        this.data = new XYWaveData(_x, _y, (_x.length < _y.length) ? _x.length : _y.length);
        this.setAxis();
        this.saved_xmin = this.curr_xmin = this.xmin;
        this.saved_xmax = this.curr_xmax = this.xmax;
        this.saved_ymin = this.ymin;
        this.saved_ymax = this.ymax;
        this.checkIncreasingX();
    }

    /**
     * Constructs a Signal with x and y array, with n_points in a defined two-dimensional region.
     *
     * @param _x
     *            an array of x coordinates
     * @param _y
     *            an array of y coordinates
     * @param _n_points
     *            number of Signal points
     * @param _xmin
     *            x minimum of region space
     * @param _xmax
     *            x maximum of region space
     * @param _ymin
     *            y minimum of region space
     * @param _ymax
     *            y maximum of region space
     */
    protected Signal(final float _x[], final float _y[], final int _n_points, final double _xmin, final double _xmax, double _ymin, final double _ymax){
        this.error = this.asym_error = false;
        this.data = new XYWaveData(_x, _y, _n_points);
        this.xLimitsInitialized = true;
        this.xmin = _xmin;
        this.xmax = _xmax;
        if(this.xmax - this.xmin < _x[1] - _x[0]) this.xmax = this.xmin + _x[1] - _x[0];
        this.saved_xmin = this.curr_xmin = this.xmin;
        this.saved_xmax = this.curr_xmax = this.xmax;
        if(this.xmax <= this.xmin) this.saved_xmax = this.xmax = this.xmin + (float)1E-6;
        if(_ymin > _ymax) _ymin = _ymax;
        this.saved_ymin = this.ymin = _ymin;
        this.saved_ymax = this.ymax = _ymax;
        this.curr_xmax = this.xmax;
        this.curr_xmin = this.xmin;
        this.setAxis(); // Here xmin and xmax have been passed, so override values computed by setAxis()
        this.ymin = this.saved_ymin;
        this.ymax = this.saved_ymax;
        this.saved_xmin = this.curr_xmin = this.xmin;
        this.saved_xmax = this.curr_xmax = this.xmax;
        this.checkIncreasingX();
    }

    /**
     * Constructs a Signal equal to argument Signal
     *
     * @param s
     *            a Signal
     */
    protected Signal(final Signal s){
        this.error = s.error;
        if(this.error){
            this.upError = s.upError;
        }
        this.asym_error = s.asym_error;
        if(this.asym_error){
            this.lowError = s.lowError;
        }
        this.nans = s.nans;
        this.n_nans = s.n_nans;
        this.gain = s.gain;
        this.offset = s.offset;
        this.cs = s.cs;
        this.contourLevels = s.contourLevels;
        this.contourSignals = s.contourSignals;
        this.contourLevelValues = s.contourLevelValues;
        this.data = s.data; // WaveData is state less!!
        this.data.addWaveDataListener(this);
        this.resolutionManager = new ResolutionManager(s.resolutionManager);
        this.xLimitsInitialized = s.xLimitsInitialized;
        this.saved_ymax = s.saved_ymax;
        this.ymax = s.ymax;
        this.saved_ymin = s.saved_ymin;
        this.ymin = s.ymin;
        this.saved_xmin = s.saved_xmin;
        this.curr_xmin = s.curr_xmin;
        this.xmin = s.xmin;
        this.saved_xmax = s.saved_xmax;
        this.curr_xmax = s.curr_xmax;
        this.xmax = s.xmax;
        this.fix_xmin = s.fix_xmin;
        this.fix_xmax = s.fix_xmax;
        this.fix_ymin = s.fix_ymin;
        this.fix_ymax = s.fix_ymax;
        this.x2D_max = s.x2D_max;
        this.x2D_min = s.x2D_min;
        this.y2D_max = s.y2D_max;
        this.y2D_min = s.y2D_min;
        this.z2D_max = s.z2D_max;
        this.z2D_min = s.z2D_min;
        if(this.xmax <= this.xmin) this.saved_xmax = this.xmax = this.xmin + 1E-6;
        this.increasing_x = s.increasing_x;
        this.marker = s.marker;
        this.marker_step = s.marker_step;
        this.color_idx = s.color_idx;
        this.color = s.color;
        this.interpolate = s.interpolate;
        this.name = s.name;
        this.type = s.type;
        this.mode1D = s.mode1D;
        this.mode2D = s.mode2D;
        this.xlabel = s.xlabel;
        this.ylabel = s.ylabel;
        this.zlabel = s.zlabel;
        this.title = s.title;
        // Deep copy buffered signals
        if(s.x != null){
            this.x = new double[s.x.length];
            System.arraycopy(s.x, 0, this.x, 0, this.x.length);
        }
        if(s.y != null){
            this.y = new float[s.y.length];
            System.arraycopy(s.y, 0, this.y, 0, this.y.length);
        }
        if(s.xLong != null){
            this.xLong = new long[s.xLong.length];
            System.arraycopy(s.xLong, 0, this.xLong, 0, this.xLong.length);
        }
        this.x_data = s.x_data;
        if(s.z != null){
            this.z = new float[s.z.length];
            System.arraycopy(s.z, 0, this.z, 0, this.z.length);
        }
        if(s.xY2D != null){
            this.xY2D = new double[s.xY2D.length];
            System.arraycopy(s.xY2D, 0, this.xY2D, 0, this.xY2D.length);
        }
        if(s.yY2D != null){
            this.yY2D = new float[s.yY2D.length];
            System.arraycopy(s.yY2D, 0, this.yY2D, 0, this.yY2D.length);
        }
        if(s.zY2D != null){
            this.zY2D = new float[s.zY2D.length];
            System.arraycopy(s.zY2D, 0, this.zY2D, 0, this.zY2D.length);
        }
        this.startIndexToUpdate = s.startIndexToUpdate;
        this.signalListeners = s.signalListeners;
        this.freezeMode = s.freezeMode;
    }

    /**
     * Constructs a zero Signal with name.
     */
    public Signal(final String name){
        this();
        this.name = name;
    }

    protected Signal(final WaveData data, final WaveData x_data, final double xminVal, final double xmaxVal){
        this(data, x_data, xminVal, xmaxVal, null, null);
    }

    protected Signal(final WaveData data, final WaveData x_data, final double xminVal, final double xmaxVal, final WaveData lowErrData, final WaveData upErrData){
        if(DEBUG.M) System.out.println("Signal(" + data + ", " + x_data + ", " + xminVal + ", " + xmaxVal + ", " + lowErrData + ", " + upErrData + ")");
        this.error = (lowErrData != null || upErrData != null);
        this.asym_error = (lowErrData != null && upErrData != null);
        this.up_errorData = upErrData;
        this.low_errorData = lowErrData;
        if(xminVal != Double.NEGATIVE_INFINITY && xmaxVal != Double.POSITIVE_INFINITY){
            this.xLimitsInitialized = true;
            this.saved_xmin = this.xmin = this.curr_xmin = xminVal;
            this.saved_xmax = this.xmax = this.curr_xmax = xmaxVal;
        }
        this.data = data;
        this.x_data = x_data;
        try{
            this.checkData(this.saved_xmin, this.saved_xmax);
            if(this.saved_xmin == Double.NEGATIVE_INFINITY) this.saved_xmin = this.xmin;
            if(this.saved_xmax == Double.POSITIVE_INFINITY) this.saved_xmax = this.xmax;
            data.addWaveDataListener(this);
        }catch(final Exception exc){
            System.err.println(">>> Signal exception: " + exc.getMessage());
        }
    }

    protected void AddAsymError(final WaveData up_error, final WaveData low_error) {
        this.error = this.asym_error = true;
        this.up_errorData = up_error;
        this.low_errorData = low_error;
    }

    protected Vector<Vector<Point2D.Double>> addContourLevel(final double level) {
        Vector<Vector<Point2D.Double>> v;
        if(this.cs == null){
            this.cs = new ContourSignal(this);
        }
        v = this.cs.contour(level);
        if(v.size() != 0){
            this.contourSignals.addElement(v);
            this.contourLevelValues.addElement(new Float(level));
        }
        return v;
    }

    /**
     * Add a symmetric error bar.
     *
     * @param _error
     *            an array of y measure error
     */
    protected void AddError(final WaveData in_error) {
        this.error = true;
        this.up_errorData = this.low_errorData = in_error;
    }

    protected void adjustArraySizes() {
        if(this.x.length < this.y.length){
            final float[] newY = new float[this.x.length];
            System.arraycopy(this.y, 0, newY, 0, this.x.length);
            this.y = newY;
        }
        if(this.y.length < this.x.length){
            final double[] newX = new double[this.y.length];
            System.arraycopy(this.x, 0, newX, 0, this.y.length);
            this.x = newX;
        }
    }

    public void appendValues(final double x[], final float y[], final int numPoints[], final float time[]) {
        if(this.type != Signal.TYPE_2D || x.length != y.length || time == null || numPoints == null) return;
        int numProfile = 0;
        int xIdx, zIdx, yIdx;
        double x2D[] = this.data.getX2D();
        float y2D[] = this.data.getY2D();
        float z2D[] = this.data.getZ();
        xIdx = (x2D == null) ? 0 : this.x2D_points;
        yIdx = (y2D == null) ? 0 : this.y2D_points;
        zIdx = (z2D == null) ? 0 : this.z2D_points;
        if(numPoints.length == time.length) numProfile = time.length * 2;
        else if(numPoints.length > time.length) numProfile = numPoints.length * 2;
        else if(numPoints.length < time.length) numProfile = time.length * 2;
        final float t[] = new float[numProfile];
        for(int i = 0, j = 0; i < numProfile; i += 2, j++){
            t[i] = (time.length == 1) ? time[0] : time[j];
            t[i + 1] = (numPoints.length == 1) ? numPoints[0] : numPoints[j];
        }
        x2D = Signal.appendArray(x2D, this.x2D_points, x, this.updSignalSizeInc);
        this.x2D_points += x.length;
        y2D = Signal.appendArray(y2D, this.y2D_points, y, this.updSignalSizeInc);
        this.y2D_points += y.length;
        z2D = Signal.appendArray(z2D, this.z2D_points, t, this.updSignalSizeInc);
        this.z2D_points += t.length;
        this.data = new XYWaveData(x2D, y2D, z2D);
        this.setAxis(x2D, z2D, y2D, xIdx, zIdx, yIdx);
        if(this.xmin > this.x2D_min) this.xmin = this.x2D_min;
        if(this.ymin > this.y2D_min) this.ymin = this.y2D_min;
        if(this.xmax < this.x2D_max) this.xmax = this.x2D_max;
        if(this.ymax < this.y2D_max) this.ymax = this.y2D_max;
        this.curr_x_yz_plot = t[t.length - 2];
    }

    // NOTE this is called only by CompositeWaveDisplay and not by jScope
    public void appendValues(final float inX[], final float inY[]) {
        if(this.x == null || this.y == null) return;
        if(this.type == Signal.TYPE_1D){
            final int len = (inX.length < inY.length) ? inX.length : inY.length;
            final double newX[] = new double[this.x.length + len];
            final float newY[] = new float[this.x.length + len];
            for(int i = 0; i < this.x.length; i++){
                newX[i] = this.x[i];
                newY[i] = this.y[i];
            }
            for(int i = 0; i < len; i++){
                newX[this.x.length + i] = inX[i];
                newY[this.x.length + i] = inY[i];
            }
            this.data = new XYWaveData(newX, newY);
            try{
                final XYData xyData = this.data.getData(Signal.NUM_POINTS);
                this.x = xyData.getX();
                this.y = xyData.getY();
                this.adjustArraySizes();
                this.xmax = this.x[this.x.length - 1];
            }catch(final Exception exc){}
        }
    }

    /**
     * Autoscale Signal.
     */
    protected final void Autoscale() {
        if(DEBUG.M) System.out.println("Signal.Autoscale()");
        this.AutoscaleX();
        this.AutoscaleY();
    }

    /**
     * Autoscale x coordinates.
     */
    protected final void AutoscaleX() {
        if(DEBUG.M) System.out.println("Signal.AutoscaleX()");
        this.xmin = Double.POSITIVE_INFINITY;
        this.xmax = Double.NEGATIVE_INFINITY;
        this.unfreeze();
        if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_IMAGE || this.mode2D == Signal.MODE_CONTOUR)){
            if(DEBUG.D) System.out.println("Signal.AutoscaleX:x2D!");
            this.xmax = this.x2D_max;
            this.xmin = this.x2D_min;
            return;
        }
        if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_XZ || this.mode2D == Signal.MODE_YZ)) this.AutoscaleX1D(this.sliceX);
        else this.AutoscaleX1D(this.x);
    }

    private final void AutoscaleX1D(final double[] X) {
        this.xmin = Double.POSITIVE_INFINITY;
        this.xmax = Double.NEGATIVE_INFINITY;
        if(X == null) return;
        for(final double x : X){
            if(Double.isNaN(x)) continue;
            if(x < this.xmin) this.xmin = x;
            if(x > this.xmax) this.xmax = x;
        }
        // if(this.fix_xmin && this.saved_xmin > this.xmin) this.xmin = this.saved_xmin;
        // if(this.fix_xmax && this.saved_xmax < this.xmax) this.xmax = this.saved_xmax;
        if(this.xmin != this.xmax) return;
        this.xmax = this.xmin + 1E-3;
        this.xmin = this.xmin - 1E-3;
    }

    /**
     * Autoscale y coordinates.
     */
    protected final void AutoscaleY() {
        this.AutoscaleY(this.xmin, this.xmax);
    }

    protected final void AutoscaleY(final double min, final double max) {
        if(DEBUG.M) System.out.println("Signal.AutoscaleY(" + min + ", " + max + ")");
        this.ymin = Double.POSITIVE_INFINITY;
        this.ymax = Double.NEGATIVE_INFINITY;
        if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_IMAGE || this.mode2D == Signal.MODE_CONTOUR)){
            if(DEBUG.D) System.out.println("Signal.AutoscaleY:y2D!");
            this.ymin = this.y2D_min;
            this.ymax = this.y2D_max;
            return;
        }
        if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_XZ || this.mode2D == Signal.MODE_YZ)){
            this.AutoscaleY1D(this.sliceX, this.sliceY, min, max);
        }else{
            this.AutoscaleY1D(this.x, this.y, min, max);
        }
    }

    private final void AutoscaleY1D(final double[] X, final float[] Y, final double min, final double max) {
        if(DEBUG.M) System.out.println("Signal.AutoscaleY1D(" + X + "," + Y + "," + min + "," + max + ")");
        if(X == null || Y == null) return;
        final int len = (X.length < Y.length) ? X.length : Y.length;
        final boolean All = (min == Double.NEGATIVE_INFINITY && max == Double.POSITIVE_INFINITY);
        for(int i = 0; i < len; i++){
            if(Float.isNaN(Y[i])) continue;
            if(!All && (X[i] < min || X[i] > max)) continue;
            if(Y[i] < this.ymin) this.ymin = Y[i];
            if(Y[i] > this.ymax) this.ymax = Y[i];
        }
        // if(this.fix_ymin && this.saved_ymin > this.ymin) this.ymin = this.saved_ymin;
        // if(this.fix_ymax && this.saved_ymax < this.ymax) this.ymax = this.saved_ymax;
        if(this.ymin != this.ymax) return;
        this.ymax = this.ymin + 1E-3f;
        this.ymin = this.ymin - 1E-3f;
    }

    private final void checkData(final double xMin, final double xMax) throws Exception {
        if(DEBUG.M) System.out.println("Signal.checkData(" + xMin + ", " + xMax + ")");
        final int numDimensions = this.data.getNumDimension();
        if(numDimensions == 1){
            this.type = Signal.TYPE_1D;
            if(this.x == null)// Only if data not present
            {
                XYData xyData;
                if(!this.error) xyData = this.data.getData(xMin, xMax, Signal.NUM_POINTS);
                else xyData = this.data.getData(xMin, xMax, Integer.MAX_VALUE);
                if(xyData == null) return; // empty signal
                this.x = xyData.getX();
                this.y = xyData.getY();
                this.adjustArraySizes();
                this.increasing_x = xyData.increasingX;
                if(xMin == Double.NEGATIVE_INFINITY) this.xmin = this.curr_xmin = xyData.getXMin();
                else this.xmin = this.curr_xmin = xMin;
                if(xMax == Double.POSITIVE_INFINITY) this.xmax = this.curr_xmax = xyData.getXMax();
                else this.xmax = this.curr_xmax = xMax;
                this.AutoscaleY1D(this.x, this.y, xMin, xMax);
                if(this.data.isXLong()) this.xLong = xyData.getXLong();
                this.resolutionManager.addRegion(new RegionDescriptor(xMin, xMax, xyData.resolution));
            }
            if(this.up_errorData != null && this.upError == null){
                // XYData xyData = up_errorData.getData(xMin, xMax, NUM_POINTS);
                final XYData xyData = this.up_errorData.getData(xMin, xMax, Integer.MAX_VALUE);
                this.upError = xyData.getY();
            }
            if(this.low_errorData != null && this.lowError == null){
                // XYData xyData = low_errorData.getData(xMin, xMax, NUM_POINTS);
                final XYData xyData = this.low_errorData.getData(xMin, xMax, Integer.MAX_VALUE);
                this.lowError = xyData.getY();
            }
            if(this.saved_ymin == Double.NEGATIVE_INFINITY) this.saved_ymin = this.ymin;
            if(this.saved_ymax == Double.POSITIVE_INFINITY) this.saved_ymax = this.ymax;
        }else if(numDimensions == 2){
            this.type = Signal.TYPE_2D;
            this.x = this.data.getX2D();
            if(this.x == null && this.data.isXLong()){
                this.xLong = this.data.getX2DLong();
                this.x = new double[this.xLong.length];
                for(int i = 0; i < this.xLong.length; i++)
                    this.x[i] = this.xLong[i];
            }
            this.y = this.data.getY2D();
            this.z = this.data.getZ();
            if(this.x_data != null){
                this.xY2D = this.x_data.getX2D();
                this.yY2D = this.x_data.getY2D();
                this.zY2D = this.x_data.getZ();
                if((this.x != null && this.x.length != this.xY2D.length) || (this.xLong != null && this.xLong.length != this.xY2D.length) && this.y.length != this.yY2D.length && this.z.length != this.zY2D.length){
                    this.xY2D = null;
                    this.yY2D = null;
                    this.zY2D = null;
                    this.x_data = null;
                }
            }
            this.x2D_min = Double.POSITIVE_INFINITY;
            this.x2D_max = Double.NEGATIVE_INFINITY;
            this.y2D_min = Double.POSITIVE_INFINITY;
            this.y2D_max = Double.NEGATIVE_INFINITY;
            this.z2D_min = Double.POSITIVE_INFINITY;
            this.z2D_max = Double.NEGATIVE_INFINITY;
            for(final double element : this.x){
                if(Double.isNaN(element)) continue;
                if(element < this.x2D_min) this.x2D_min = element;
                if(element > this.x2D_max) this.x2D_max = element;
            }
            for(final float element : this.y){
                if(Float.isNaN(element)) continue;
                if(element < this.y2D_min) this.y2D_min = element;
                if(element > this.y2D_max) this.y2D_max = element;
            }
            for(final float element : this.z){
                if(Float.isNaN(element)) continue;
                if(element < this.z2D_min) this.z2D_min = element;
                if(element > this.z2D_max) this.z2D_max = element;
            }
            if(xMin == Double.NEGATIVE_INFINITY) this.xmin = this.curr_xmin = this.x2D_min;
            else this.xmin = this.curr_xmin = xMin;
            if(xMax == Double.POSITIVE_INFINITY) this.xmax = this.curr_xmax = this.x2D_max;
            else this.xmax = this.curr_xmax = xMax;
        }
    }

    /**
     * Check if x array coordinates are increasing.
     */
    private final void checkIncreasingX() {
        this.increasing_x = false;
        for(int i = 1; i < this.x.length; i++)
            if(this.x[i] < this.x[i - 1]) return;
        this.increasing_x = true;
    }

    @Override
    public final void dataRegionUpdated(final XYData data) {
        if(data == null || data.getX() == null || data.getX().length == 0) return;
        if(DEBUG.M) System.out.println("dataRegionUpdated " + this.resolutionManager.lowResRegions.size());
        if(this.freezeMode == Signal.FREEZED_BLOCK) // If zooming in ANY part of the signal
        {
            this.pendingUpdatesV.addElement(data);
            return;
        }
        int samplesBefore, samplesAfter;
        if(this.x == null){
            this.x = data.getX();
            this.xLong = data.getXLong();
            this.y = data.getY();
            if(!this.fix_xmin) this.xmin = this.saved_xmin = data.getXMin();
            if(!this.fix_xmax) this.xmax = this.saved_xmax = data.getXMax();
        }else{
            for(samplesBefore = 0; samplesBefore < this.x.length && this.x[samplesBefore] < data.getXMin(); samplesBefore++);
            if(samplesBefore > 0 && samplesBefore < this.x.length && this.x[samplesBefore] > data.getXMin()) samplesBefore--;
            for(samplesAfter = 0; samplesAfter < this.x.length - 1 && this.x[this.x.length - samplesAfter - 1] > data.getX()[data.getX().length - 1]; samplesAfter++);
            final double[] newX = new double[samplesBefore + data.getX().length + samplesAfter];
            System.arraycopy(this.x, 0, newX, 0, samplesBefore);
            System.arraycopy(data.getX(), 0, newX, samplesBefore, data.getX().length);
            System.arraycopy(this.x, 0, newX, newX.length - samplesAfter, samplesAfter);
            final float[] newY = new float[newX.length];
            System.arraycopy(this.y, 0, newY, 0, samplesBefore);
            System.arraycopy(data.getY(), 0, newY, samplesBefore, data.getY().length);
            System.arraycopy(this.y, 0, newY, newY.length - samplesAfter, samplesAfter);
            this.x = newX;
            this.y = newY;
            if(this.xLong != null && data.getXLong() != null){
                final long[] newXLong = new long[newX.length];
                System.arraycopy(this.xLong, 0, newXLong, 0, samplesBefore);
                System.arraycopy(data.getXLong(), 0, newXLong, samplesBefore, data.getXLong().length);
                System.arraycopy(this.xLong, 0, newXLong, newXLong.length - samplesAfter, samplesAfter);
                this.xLong = newXLong;
            }
        }
        this.resolutionManager.appendRegion(new RegionDescriptor(data.getXMin(), data.getXMax(), data.resolution));
        final boolean dataattached = this.x[0] >= data.getXMin() || this.x[this.x.length - 1] <= data.getXMax();
        if(dataattached){
            if(this.freezeMode == Signal.FREEZED_SCROLL){
                final double delta = data.getXMax() - data.getXMin();
                this.xmin += delta;
                this.saved_xmax = this.xmax += delta;
            }else if(this.freezeMode == Signal.NOT_FREEZED){
                if(!this.fix_xmin && this.saved_xmin > data.getXMin()) this.xmin = this.saved_xmin = data.getXMin();
                if(!this.fix_xmax && this.saved_xmax < data.getXMax()) this.xmax = this.saved_xmax = data.getXMax();
            }
        }
        final double ymin = this.ymin, ymax = this.ymax;
        if(!this.fix_ymin || !this.fix_ymax){
            this.AutoscaleY();
            if(this.fix_ymin) this.ymin = ymin;
            else if(this.saved_ymin == Double.NEGATIVE_INFINITY || this.saved_ymin > this.ymin) this.saved_ymin = this.ymin;
            if(this.fix_ymax) this.ymax = ymax;
            else if(this.saved_ymax == Double.POSITIVE_INFINITY || this.saved_ymax < this.ymax) this.saved_ymax = this.ymax;
        }
        this.fireSignalUpdated(dataattached);
    }

    protected final void decShow() {
        if(this.type == Signal.TYPE_2D){
            switch(this.mode2D){
                case Signal.MODE_XZ:
                    this.decShowXZ();
                    break;
                case Signal.MODE_YZ:
                    this.decShowYZ();
                    break;
                case Signal.MODE_PROFILE:
                    // decShowProfile();
                    break;
            }
        }
    }

    private final void decShowXZ() {
        if(this.type == Signal.TYPE_2D && this.mode2D == Signal.MODE_XZ){
            int idx = this.curr_y_xz_idx - 1;
            if(idx < 0) idx = this.y.length - 1;
            this.showXZ(idx);
        }
    }

    private final void decShowYZ() {
        if(this.type == Signal.TYPE_2D && this.mode2D == Signal.MODE_YZ){
            int idx = this.curr_x_yz_idx - 1;
            if(idx < 0) idx = this.x.length - 1;
            this.showYZ(idx);
        }
    }

    /**
     * Return index of nearest signal point to argument (curr_x, curr_y) point.
     *
     * @param curr_x
     *            value
     * @param curr_y
     *            value
     * @return index of signal point
     */
    protected final int FindClosestIdx(final double curr_x, final double curr_y) {
        if(DEBUG.M) System.out.println("FindClosestIdx(" + curr_x + ", " + curr_y + ")");
        if(this.x == null || this.x.length < 1) return -1;
        try{
            double min_dist, curr_dist;
            int min_idx;
            int i = 0;
            if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_IMAGE || this.mode2D == Signal.MODE_CONTOUR)){
                this.img_xprev = Signal.findIndex(this.x, curr_x, this.img_xprev);
                this.img_yprev = Signal.findIndex(this.y, curr_y, this.img_yprev);
                if(this.img_xprev > this.y.length) return this.img_xprev - 6;
                return this.img_xprev;
            }
            double currX[];
            if(this.type == Signal.TYPE_1D) currX = this.x;
            else{
                if(this.mode2D == Signal.MODE_XZ || this.mode2D == Signal.MODE_YZ) currX = this.sliceX;
                else{
                    final double xf[] = this.x;
                    currX = new double[xf.length];
                    for(int idx = 0; idx < xf.length; idx++)
                        currX[idx] = xf[idx];
                }
            }
            if(this.increasing_x || this.type == Signal.TYPE_2D){
                if(currX == null) return -1;
                if(this.prev_idx >= currX.length) this.prev_idx = currX.length - 1;
                if(curr_x > currX[this.prev_idx]){
                    for(i = this.prev_idx; i < currX.length && currX[i] < curr_x; i++);
                    if(i > 0) i--;
                    this.prev_idx = i;
                    return i;
                }
                if(curr_x < currX[this.prev_idx]){
                    for(i = this.prev_idx; i > 0 && currX[i] > curr_x; i--);
                    this.prev_idx = i;
                    return i;
                }
                return this.prev_idx;
            }
            // Handle below x values not in ascending order
            if(curr_x > this.curr_xmax){
                for(min_idx = 0; min_idx < currX.length && currX[min_idx] != this.curr_xmax; min_idx++);
                if(min_idx == currX.length) min_idx--;
                return min_idx;
            }
            if(curr_x < this.curr_xmin){
                for(min_idx = 0; min_idx < currX.length && currX[min_idx] != this.curr_xmin; min_idx++);
                if(min_idx == currX.length) min_idx--;
                return min_idx;
            }
            min_idx = 0;
            min_dist = Double.POSITIVE_INFINITY;
            this.find_NaN = false;
            for(i = 0; i < this.x.length - 1; i++){
                if(Float.isNaN(this.y[i])){
                    this.find_NaN = true;
                    continue;
                }
                if(curr_x > currX[i] && curr_x < currX[i + 1] || curr_x < currX[i] && curr_x > currX[i + 1] || currX[i] == currX[i + 1]){
                    curr_dist = (curr_x - currX[i]) * (curr_x - currX[i]) + (curr_y - this.y[i]) * (curr_y - this.y[i]);
                    // Patch to elaborate strange RFX signal (roprand bar error signal)
                    if(currX[i] != currX[i + 1] && !Float.isNaN(this.y[i + 1])) curr_dist += (curr_x - currX[i + 1]) * (curr_x - currX[i + 1]) + (curr_y - this.y[i + 1]) * (curr_y - this.y[i + 1]);
                    if(curr_dist < min_dist){
                        min_dist = curr_dist;
                        min_idx = i;
                    }
                }
            }
            return min_idx;
        }catch(final Exception e){
            System.err.println("Signal.FindClosestIdx: " + e);
        }
        return -1;
    }

    protected final boolean findNaN() {
        return this.find_NaN;
    }

    void fireSignalUpdated(final boolean changeLimits) {
        if(DEBUG.M) System.out.println("fireSignalUpdated(" + changeLimits + ") for " + this.signalListeners.size());
        for(int i = 0; i < this.signalListeners.size(); i++)
            this.signalListeners.elementAt(i).signalUpdated(changeLimits);
    }

    protected final void freeze() {
        if(this.isLongX() && this.xmax > this.xLong[this.xLong.length - 1]) this.freezeMode = Signal.FREEZED_SCROLL;
        else this.freezeMode = Signal.FREEZED_BLOCK;
        this.freezedXMin = this.xmin;
        this.freezedXMax = this.xmax;
    }

    @SuppressWarnings("static-method")
    protected final boolean fullPaint() {
        return true;
    }

    protected final float getClosestX(final double x) {
        if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_IMAGE || this.mode2D == Signal.MODE_CONTOUR)){
            this.img_xprev = Signal.findIndex(this.x, x, this.img_xprev);
            return (float)this.x[this.img_xprev];
        }
        return Float.NaN;
    }

    protected final float getClosestY(final double y) {
        if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_IMAGE || this.mode2D == Signal.MODE_CONTOUR)){
            this.img_yprev = Signal.findIndex(this.y, y, this.img_yprev);
            return this.y[this.img_yprev];
        }
        return Float.NaN;
    }

    protected final Color getColor() {
        return this.color;
    }

    protected final int getColorIdx() {
        return this.color_idx;
    }

    protected final Vector<Float> getContourLevelValues() {
        return this.contourLevelValues;
    }

    protected final Vector<Vector<Vector<Point2D.Double>>> getContourSignals() {
        return this.contourSignals;
    }

    protected final double getCurrentXmax() {
        return this.curr_xmax;
    }

    protected final double getCurrentXmin() {
        return this.curr_xmin;
    }

    protected final float getGain() {
        return this.gain;
    }

    protected final boolean getInterpolate() {
        return this.interpolate;
    }

    protected final String getLegend() {
        return this.legend;
    }

    protected final float[] getLowError() {
        return this.lowError;
    }

    protected final int getMarker() {
        return this.marker;
    }

    protected final int getMarkerStep() {
        return (this.marker == Signal.POINT) ? 1 : this.marker_step;
    }

    protected final int getMode1D() {
        return this.mode1D;
    }

    protected final int getMode2D() {
        return this.mode2D;
    }

    public final String getName() {
        return this.name;
    }

    protected final int[] getNaNs() {
        return this.nans;
    }

    protected final int getNumNaNs() {
        return this.n_nans;
    }

    protected final int getNumPoints() {
        try{
            if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_YZ || this.mode2D == Signal.MODE_XZ)) return (this.sliceX == null) ? 0 : this.sliceX.length;
            if(this.x != null && this.y != null){ return (this.x.length < this.y.length) ? this.x.length : this.y.length; }
        }catch(final Exception e){
            if(DEBUG.D) System.err.println("getNumPoints(): " + e);
        }
        return 0;
    }

    protected final float getOffset() {
        return this.offset;
    }

    protected final double getOriginalYmax() {
        return this.saved_ymax;
    }

    protected final double getOriginalYmin() {
        return this.saved_ymin;
    }

    protected final String getStringOfXinYZplot() {
        if(this.isLongX()) return Signal.toStringTime((long)this.curr_x_yz_plot);
        return "" + this.curr_x_yz_plot;
    }

    protected final String getTitlelabel() {
        return this.title;
    }

    public final int getType() {
        return this.type;
    }

    protected final int getUpdSignalSizeInc() {
        return this.updSignalSizeInc;
    }

    protected final float[] getUpError() {
        return this.upError;
    }

    protected final double[] getX() throws Exception {
        if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_XZ || this.mode2D == Signal.MODE_YZ)) return this.sliceX;
        return this.x;
    }

    protected final double getX(final int idx) {
        try{
            if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_YZ || this.mode2D == Signal.MODE_XZ)) return this.sliceX[idx];
            return this.x[idx];
        }catch(final Exception e){
            if(DEBUG.D) System.err.println("getX(" + idx + "): " + e);
            return Double.NaN;
        }
    }

    protected final double[] getX2D() {
        if(this.x == null) this.x = this.data.getX2D();
        return this.x;
    }

    protected final double getX2Dmax() {
        return this.x2D_max;
    }

    protected final double getX2Dmin() {
        return this.x2D_min;
    }

    protected final double getXinYZplot() {
        return this.curr_x_yz_plot;
    }

    protected final String getXlabel() {
        return this.xlabel;
    }

    protected final double getXmax() {
        return this.xmax;
    }

    protected final double getXmin() {
        return this.xmin;
    }

    protected final float[] getY() throws Exception {
        if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_XZ || this.mode2D == Signal.MODE_YZ)) return this.sliceY;
        return this.y;
    }

    protected final float getY(final int idx) {
        try{
            if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_YZ || this.mode2D == Signal.MODE_XZ)) return (this.sliceY == null) ? Float.NaN : this.sliceY[idx];
            return (this.y == null) ? Float.NaN : this.y[idx];
        }catch(final Exception e){
            if(DEBUG.D) System.err.println("getY(" + idx + "): " + e);
            return Float.NaN;
        }
    }

    protected final float[] getY2D() {
        if(this.y == null) this.y = this.data.getY2D();
        return this.y;
    }

    protected final double getY2Dmax() {
        return this.y2D_max;
    }

    protected final double getY2Dmin() {
        return this.y2D_min;
    }

    protected final float getYinXZplot() {
        return this.curr_y_xz_plot;
    }

    protected final String getYlabel() {
        return this.ylabel;
    }

    protected final double getYmax() {
        return this.ymax;
    }

    protected final double getYmin() {
        return this.ymin;
    }

    protected final float[] getZ() {
        if(this.z == null) this.z = this.data.getZ();
        return this.z;
    }

    protected final float getZ(final int idx) {
        if(this.z == null) this.z = this.data.getZ();
        return this.z[idx];
    }

    protected final float[][] getZ2D() {
        final float zOut[][] = new float[this.x.length][this.y.length];
        int k;
        for(int i = 0; i < this.x.length; i++){
            for(int j = 0; j < this.y.length; j++){
                k = j * this.x.length + i;
                if(k < this.z.length) zOut[i][j] = this.z[k];
            }
        }
        return zOut;
    }

    protected final double getZ2Dmax() {
        return this.z2D_max;
    }

    protected final double getZ2Dmin() {
        return this.z2D_min;
    }

    protected final String getZlabel() {
        return this.zlabel;
    }

    @SuppressWarnings("fallthrough")
    protected final double getZValue() {
        if(this.type == Signal.TYPE_2D){
            if(this.mode2D == Signal.MODE_IMAGE){
                final int idx = this.img_xprev * this.y.length + this.img_yprev;
                if(this.z != null && idx < this.z.length) return this.z[idx];
            }else if(this.mode2D == Signal.MODE_CONTOUR) return this.z_value;
        }
        return Float.NaN;
    }

    protected final boolean hasAsymError() {
        return this.asym_error;
    }

    protected final boolean hasError() {
        return this.error;
    }

    @SuppressWarnings("static-method")
    protected final boolean hasX() {
        return true;
    }

    protected final void incShow() {
        if(this.type == Signal.TYPE_2D){
            switch(this.mode2D){
                case Signal.MODE_XZ:
                    this.incShowXZ();
                    break;
                case Signal.MODE_YZ:
                    this.incShowYZ();
                    break;
                case Signal.MODE_PROFILE:
                    // incShowProfile();
                    break;
            }
        }
    }

    private final void incShowXZ() {
        if(this.type == Signal.TYPE_2D && this.mode2D == Signal.MODE_XZ){
            int idx = this.curr_y_xz_idx;
            idx = (idx + 1) % this.y.length;
            this.showXZ(idx);
        }
    }

    private final void incShowYZ() {
        if(this.type == Signal.TYPE_2D && this.mode2D == Signal.MODE_YZ){
            int idx = this.curr_x_yz_idx;
            idx = (idx + 1) % this.x.length;
            this.showYZ(idx);
        }
    }

    private final void initContour() {
        this.saved_ymin = this.ymin = this.y2D_min;
        this.saved_ymax = this.ymax = this.y2D_max;
        this.saved_xmin = this.xmin = this.x2D_min;
        this.saved_xmax = this.xmax = this.x2D_max;
        // x = x2D;
        // y = y2D;
        this.cs = new ContourSignal(this);
        if(this.contourLevels == null || this.contourLevels.length == 0){
            this.contourLevels = new double[Signal.DEFAULT_CONTOUR_LEVEL];
            final double dz = (this.z2D_max - this.z2D_min) / (Signal.DEFAULT_CONTOUR_LEVEL + 1);
            for(int i = 0; i < this.contourLevels.length; i++){
                this.contourLevels[i] = this.z2D_min + dz * (i + 1);
            }
        }
        for(final double contourLevel : this.contourLevels){
            this.addContourLevel(contourLevel);
        }
    }

    /*
    private final boolean isFullLoad() {
        return this.full_load;
    }
     */
    protected final boolean isIncreasingX() {
        return this.increasing_x;
    }

    protected final boolean isLongX() {
        return((this.type == Signal.TYPE_1D || this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_XZ || this.mode2D == Signal.MODE_IMAGE)) && this.xLong != null);
    }

    protected final boolean isLongXForLabel() {
        return (this.type == Signal.TYPE_1D || this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_XZ || this.mode2D == Signal.MODE_YZ || this.mode2D == Signal.MODE_IMAGE)) && this.data.isXLong();
    }

    @Override
    public final void legendUpdated(final String name) {
        this.setLegend(name);
    }

    protected final void registerSignalListener(final SignalListener listener) {
        this.signalListeners.addElement(listener);
    }

    /**
     * Reset scale, return to the initial two dimensional region
     */
    protected final void ResetScales() {
        this.unfreeze();
        this.xmax = this.saved_xmax;
        this.xmin = this.saved_xmin;
        this.ymax = this.saved_ymax;
        this.ymin = this.saved_ymin;
    }

    public final void resetSignalData() {
        this.x2D_points = 0;
        this.y2D_points = 0;
        this.z2D_points = 0;
        final double x[] = new double[]{0., 1.};
        final float y[] = new float[]{0, 0};
        this.data = new XYWaveData(x, y);
        this.low_errorData = null;
        this.up_errorData = null;
        this.startIndexToUpdate = 0;
    }

    /**
     * Reset x scale, return to original x range two dimensional region
     */
    protected final void ResetXScale() {
        this.unfreeze();
        this.xmax = this.saved_xmax;
        this.xmin = this.saved_xmin;
    }

    /**
     * Reset x scale, return to the initial y range two dimensional region
     */
    protected final void ResetYScale() {
        this.ymax = this.saved_ymax;
        this.ymin = this.saved_ymin;
    }

    protected final void setAttributes(final Signal s) {
        this.color = s.getColor();
        this.color_idx = s.getColorIdx();
        this.gain = s.getGain();
        this.interpolate = s.getInterpolate();
        this.marker = s.getMarker();
        this.marker_step = s.getMarkerStep();
        this.offset = s.getOffset();
        this.name = s.getName();
    }

    protected final void setAttributes(final String name, final int color_idx, final int marker, final int marker_step, final boolean interpolate) {
        this.setMarker(marker);
        this.setMarkerStep(marker_step);
        this.setInterpolate(interpolate);
        this.setColorIdx(color_idx);
        this.setName(name);
    }

    protected final boolean setAxis() {
        try{
            int i;
            // If the signal dimension is 2 or the x axis are not increasing, the signal is assumed to be completely in memory
            // and no further readout from data is performed
            if(this.type != Signal.TYPE_1D || !this.increasing_x) return true;
            // Check if the signal is fully available (i.e. has already been read without X limits)
            if(!this.resolutionManager.isEmpty()){
                final double minMax[] = this.resolutionManager.getMinMaxX();
                if(minMax[0] == Double.NEGATIVE_INFINITY && minMax[1] == Double.POSITIVE_INFINITY){
                    this.xLimitsInitialized = true;
                    this.xmin = this.x[0];
                    this.xmax = this.x[this.x.length - 1];
                    return true;
                }
            }
            // resolutionManager.resetRegions();
            final XYData xyData = this.data.getData(Signal.NUM_POINTS);
            if(xyData == null) return false;
            this.x = xyData.getX();
            this.y = xyData.getY();
            this.adjustArraySizes();
            this.increasing_x = xyData.increasingX;
            if(this.increasing_x) this.resolutionManager.addRegion(new RegionDescriptor(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, this.x.length / (this.x[this.x.length - 1] - this.x[0])));
            if(this.data.isXLong()) this.xLong = xyData.getXLong();
            this.curr_xmin = this.xmin = xyData.getXMin();
            this.curr_xmax = this.xmax = xyData.getXMax();
            this.ymax = this.ymin = this.y[0];
            for(i = 0; i < this.x.length; i++){
                if(Float.isNaN(this.y[i]) && this.n_nans < 100) this.nans[this.n_nans++] = i;
                if(this.y[i] > this.ymax) this.ymax = this.y[i];
                if(this.ymin > this.y[i]) this.ymin = this.y[i];
            }
            return true;
        }catch(final Exception e){
            if(DEBUG.D) System.err.println("Signal.setAxis Exception: " + e);
            this.xmax = Double.NEGATIVE_INFINITY;
            this.xmin = Double.POSITIVE_INFINITY;
            this.ymax = Double.NEGATIVE_INFINITY;
            this.ymin = Double.POSITIVE_INFINITY;
            return false;
        }
    }

    protected final void setAxis(final double x2D[], final float z2D[], final float y2D[]) {
        this.x2D_max = this.x2D_min = x2D[0];
        this.z2D_max = this.z2D_min = z2D[0];
        this.y2D_max = this.y2D_min = y2D[0];
        this.setAxis(x2D, z2D, y2D, 0, 0, 0);
    }

    protected final void setAxis(final double x2D[], final float z2D[], final float y2D[], final int xIdx, final int zIdx, final int yIdx) {
        int i;
        for(i = xIdx; i < x2D.length; i++){
            if(x2D[i] > this.x2D_max) this.x2D_max = x2D[i];
            if(this.x2D_min > x2D[i]) this.x2D_min = x2D[i];
        }
        for(i = zIdx; i < z2D.length; i++){
            if(z2D[i] > this.z2D_max) this.z2D_max = z2D[i];
            if(this.z2D_min > z2D[i]) this.z2D_min = z2D[i];
        }
        for(i = yIdx; i < y2D.length; i++){
            if(y2D[i] > this.y2D_max) this.y2D_max = y2D[i];
            if(this.y2D_min > y2D[i]) this.y2D_min = y2D[i];
        }
    }

    public final void setCalibrate(final float gain, final float offset) {
        this.gain = gain;
        this.offset = offset;
        this.setAxis();
    }

    public final void setColor(final Color value) {
        this.color = value;
    }

    public final void setColorIdx(final int value) {
        this.color = null;
        this.color_idx = value;
    }

    /*
    public final void setFullLoad(final boolean full_load) {
        this.full_load = full_load;
    }
     */
    public final void setInterpolate(final boolean value) {
        this.interpolate = value;
    }

    protected final void setLabels(final String title, final String xlabel, final String ylabel, final String zlabel) {
        this.title = title;
        this.xlabel = xlabel;
        this.ylabel = ylabel;
        this.zlabel = zlabel;
    }

    protected final void setLegend(final String legend) {
        this.legend = legend;
    }

    public final void setMarker(final int value) {
        this.marker = value;
    }

    public final void setMarker(final String name) {
        if(name == null) return;
        for(int i = 0; i < Signal.markerList.length; i++)
            if(name.toLowerCase().equals(Signal.markerList[i].toLowerCase())){
                this.setMarker(i);
                return;
            }
        this.setMarker(0);
    }

    protected final void setMarkerStep(final int value) {
        this.marker_step = value;
    }

    protected final void setMode1D(final int mode) {
        this.mode1D = mode;
        switch(mode){
            case MODE_LINE:
                this.interpolate = true;
                break;
            case MODE_NOLINE:
                this.interpolate = false;
                break;
            case MODE_STEP:
                this.interpolate = true;
                break;
        }
    }

    public final void setMode2D(final int mode) {
        if(this.type == Signal.TYPE_1D) return;
        switch(mode){
            case MODE_IMAGE:
                this.setMode2D(mode, 0);
                break;
            case MODE_XZ:
                this.setMode2D(mode, this.y[0]);
                break;
            case MODE_YZ:
                double v = this.x[0];
                if(!Double.isNaN(this.curr_x_yz_plot)) v = this.curr_x_yz_plot;
                this.setMode2D(mode, v);
                break;
            case MODE_CONTOUR:
                this.setMode2D(mode, 0);
                break;
            case MODE_PROFILE:
                /*
                 * if(z2D != null && z2D.length > 0) { float v1 = z2D[0]; if (!Float.isNaN(curr_x_yz_plot)) v1 = curr_x_yz_plot; setMode2D(mode, v1); }
                 */
                break;
        }
    }

    protected final void setMode2D(final int mode, final double value) {
        if(this.type == Signal.TYPE_1D) return;
        this.curr_x_yz_plot = Float.NaN;
        this.curr_y_xz_plot = Float.NaN;
        this.curr_x_yz_idx = -1;
        this.curr_y_xz_idx = -1;
        switch(mode){
            case MODE_IMAGE:
                if(this.saved_ymin == Double.NEGATIVE_INFINITY) this.saved_ymin = this.ymin = this.y2D_min;
                else this.ymin = this.saved_ymin;
                if(this.saved_ymax == Double.POSITIVE_INFINITY) this.saved_ymax = this.ymax = this.y2D_max;
                else this.ymax = this.saved_ymax;
                if(this.saved_xmin == Double.NEGATIVE_INFINITY) this.saved_xmin = this.xmin = this.x2D_min;
                else this.xmin = this.saved_xmin;
                if(this.saved_xmax == Double.POSITIVE_INFINITY) this.saved_xmax = this.xmax = this.x2D_max;
                else this.xmax = this.saved_xmax;
                break;
            case MODE_XZ:
                this.showXZ((float)value);
                break;
            case MODE_YZ:
                this.prev_idx = 0;
                this.showYZ((float)value);
                break;
            case MODE_CONTOUR:
                this.initContour();
                break;
            case MODE_PROFILE:
                /*
                 * prev_idx = 0; showProfile(mode, value);
                 */
                break;
        }
        this.mode2D = mode;
    }

    public final void setName(final String value) {
        if(value != null && value.length() != 0) this.name = new String(value);
    }

    protected final void setStartIndexToUpdate() {
        if(this.x != null) this.startIndexToUpdate = this.x.length;
    }

    public final void setType(final int type) {
        this.type = type;
    }

    public final void setUpdSignalSizeInc(int updSignalSizeInc) {
        if(updSignalSizeInc <= 0) updSignalSizeInc = Signal.DEFAULT_INC_SIZE;
        this.updSignalSizeInc = updSignalSizeInc;
    }

    protected final void setXinYZplot(final float curr_x_yz_plot) {
        this.curr_x_yz_plot = curr_x_yz_plot;
    }

    protected final void setXLimits(final double xmin, final double xmax, final int mode) {
        if(DEBUG.M) System.out.println("setXLimits(" + xmin + ", " + xmax + ", " + mode + ")");
        if(this.freezeMode != Signal.NOT_FREEZED){// If adding samples when freeze
            if(DEBUG.D) System.out.println("unfreezed mode");
            this.xmin = xmin;
            this.xmax = xmax;
            return;
        }
        this.xLimitsInitialized = true;
        if(xmin != Double.NEGATIVE_INFINITY){
            this.xmin = xmin;
            if((mode & (Signal.AT_CREATION | Signal.FIXED_LIMIT)) != 0) this.saved_xmin = xmin;
            if((mode & Signal.FIXED_LIMIT) != 0) this.fix_xmin = true;
        }
        if(xmax != Double.POSITIVE_INFINITY){
            this.xmax = xmax;
            if((mode & (Signal.AT_CREATION | Signal.FIXED_LIMIT)) != 0) this.saved_xmax = xmax;
            if((mode & Signal.FIXED_LIMIT) != 0) this.fix_xmax = true;
        }
        double actXMin = xmin;
        if(actXMin == Double.NEGATIVE_INFINITY) actXMin = this.xmin;
        double actXMax = xmax;
        if(actXMax == Double.POSITIVE_INFINITY) actXMax = this.xmax;
        /* Enlarge by 1/20 */
        final double enlargeFactor = 40;
        actXMax += (actXMax - actXMin) / enlargeFactor;
        actXMin -= (actXMax - actXMin) / enlargeFactor;
        final double actResolution = Signal.NUM_POINTS / (actXMax - actXMin);
        if(!this.increasing_x) return; // Dynamic re-sampling only for "classical" signals
        if(this.up_errorData != null || this.low_errorData != null) return; // Dynamic re-sampling only without error bars
        final Vector<RegionDescriptor> lowResRegions = this.resolutionManager.getLowerResRegions(actXMin, actXMax, actResolution);
        for(int i = 0; i < lowResRegions.size(); i++){
            final RegionDescriptor currReg = lowResRegions.elementAt(i);
            final double currLower = currReg.lowerBound;
            final double currUpper = currReg.upperBound;
            // Error bars are assumed to be used only for small signals and should not arrive here. In case make it not asynchronous
            /*
             * if(up_errorData != null) { try { XYData currError = up_errorData.getData(xmin, xmax, Integer.MAX_VALUE); upError = currError.y; }catch(Exception exc) { System.out.println("Cannot evaluate error: "+ exc); } } if(low_errorData != null) { try {
             * XYData currError = low_errorData.getData(xmin, xmax, Integer.MAX_VALUE); lowError = currError.y; }catch(Exception exc) { System.out.println("Cannot evaluate error: "+ exc); } }
             */
            if(((mode & Signal.DO_NOT_UPDATE) == 0) && (currLower != this.saved_xmin || currUpper != this.saved_xmax || (mode & Signal.AT_CREATION) == 0)) this.data.getDataAsync(currLower, currUpper, Signal.NUM_POINTS);
        }
        // fireSignalUpdated();
    }

    protected final void setYinXZplot(final float curr_y_xz_plot) {
        this.curr_y_xz_plot = curr_y_xz_plot;
    }

    protected final void setYlimits(final double ymin, final double ymax) {
        if(ymax != Double.POSITIVE_INFINITY){
            this.ymax = ymax;
            this.fix_ymax = true;
        }else this.fix_ymax = false;
        if(ymin != Double.NEGATIVE_INFINITY){
            this.ymin = ymin;
            this.fix_ymin = true;
        }else this.fix_ymin = false;
    }

    protected final void setYmax(final double ymax, final int mode) {
        if(ymax == Double.POSITIVE_INFINITY) return;
        this.ymax = ymax;
        if((mode & (Signal.AT_CREATION | Signal.FIXED_LIMIT)) != 0) this.saved_ymax = ymax;
        if((mode & Signal.FIXED_LIMIT) != 0) this.fix_ymax = true;
    }

    protected final void setYmin(final double ymin, final int mode) {
        if(ymin == Double.NEGATIVE_INFINITY) return;
        this.ymin = ymin;
        if((mode & (Signal.AT_CREATION | Signal.FIXED_LIMIT)) != 0) this.saved_ymin = ymin;
        if((mode & Signal.FIXED_LIMIT) != 0) this.fix_ymin = true;
    }

    protected final void showXZ(final double xd) {
        if(this.curr_y_xz_plot == xd) return;
        final int i = Signal.getArrayIndex(this.y, xd);
        this.showXZ(i);
    }

    protected final void showXZ(final int idx) {
        final float[] y2d = this.y;
        double[] x2d = this.x;
        // if ( (idx >= x2d.length || idx == curr_y_xz_idx) &&
        if((idx >= y2d.length || idx == this.curr_y_xz_idx) && this.mode2D == Signal.MODE_XZ) return;
        this.prev_idx = 0;
        this.curr_y_xz_plot = y2d[idx];
        this.curr_y_xz_idx = idx;
        this.curr_x_yz_plot = Float.NaN;
        this.curr_x_yz_idx = -1;
        if(this.zY2D != null){
            x2d = new double[this.x.length];
            this.curr_xmin = this.curr_xmax = this.zY2D[this.x.length * idx];
            for(int j = 0; j < this.x.length; j++){
                x2d[j] = this.zY2D[this.x.length * idx + j];
                if(x2d[j] > this.curr_xmax) this.curr_xmax = x2d[j];
                else if(x2d[j] < this.curr_xmin) this.curr_xmin = x2d[j];
            }
        }
        this.sliceX = new double[x2d.length];
        this.sliceY = new float[x2d.length];
        final int zLen = this.z.length;
        float sliceMin, sliceMax;
        // sliceMin = sliceMax = z[ y2d.length * idx];
        sliceMin = sliceMax = this.z[x2d.length * idx];
        for(int j = 0; j < x2d.length; j++){
            this.sliceX[j] = x2d[j];
            // int k = y2d.length * idx + j;
            final int k = x2d.length * idx + j;
            if(k >= zLen) break;
            this.sliceY[j] = this.z[k];
            if(sliceMin > this.z[k]) sliceMin = this.z[k];
            if(sliceMax < this.z[k]) sliceMax = this.z[k];
        }
        this.error = this.asym_error = false;
        this.mode2D = Signal.MODE_XZ;
        if(!this.fix_xmin) this.saved_xmin = this.curr_xmin = this.xmin = this.x2D_min;
        // saved_xmin = curr_xmin;
        if(!this.fix_xmax) this.saved_xmax = this.curr_xmax = this.xmax = this.x2D_max;
        // saved_xmax = curr_xmax;
        if(!this.fix_ymin) this.saved_ymin = this.ymin = sliceMin;
        if(!this.fix_ymax) this.saved_ymax = this.ymax = sliceMax;
        // Assumed that for 2D data, dimensions are increasing
        this.increasing_x = true;
    }

    protected final void showYZ(final double t) {
        if(this.curr_x_yz_plot == t && this.mode2D == Signal.MODE_YZ) return;
        final int i = Signal.getArrayIndex(this.x, t);
        this.showYZ(i);
    }

    protected final void showYZ(final int idx) {
        final float[] y2d = this.y;
        final double[] x2d = this.x;
        if((idx >= x2d.length || idx == this.curr_x_yz_idx) && this.mode2D == Signal.MODE_YZ) return;
        this.prev_idx = 0;
        this.curr_x_yz_plot = x2d[idx];
        this.curr_x_yz_idx = idx;
        this.curr_y_xz_plot = Float.NaN;
        this.curr_y_xz_idx = -1;
        if(this.zY2D != null){
            this.ymin = this.ymax = this.zY2D[idx];
            for(int j = 0; j < y2d.length; j++){
                final int k = x2d.length * j + idx;
                y2d[j] = this.zY2D[k];
                if(this.ymin > y2d[j]) this.ymin = y2d[j];
                if(this.ymax < y2d[j]) this.ymax = y2d[j];
            }
        }
        this.sliceX = new double[y2d.length];
        this.sliceY = new float[y2d.length];
        final int zLen = this.z.length;
        float sliceMin, sliceMax;
        sliceMin = sliceMax = this.z[idx];
        for(int j = 0; j < y2d.length; j++){
            final int k = x2d.length * j + idx;
            this.sliceX[j] = y2d[j];
            if(k >= zLen) break;
            this.sliceY[j] = this.z[k];
            if(sliceMin > this.z[k]) sliceMin = this.z[k];
            if(sliceMax < this.z[k]) sliceMax = this.z[k];
        }
        this.error = this.asym_error = false;
        this.mode2D = Signal.MODE_YZ;
        if(!this.fix_xmin) this.saved_xmin = this.curr_xmin = this.xmin = this.y2D_min;
        // saved_xmin = curr_xmin = ymin;
        if(!this.fix_xmax) this.saved_xmax = this.curr_xmax = this.xmax = this.y2D_max;
        // saved_xmax = curr_xmax = ymax;
        if(!this.fix_ymin) this.saved_ymin = this.ymin = sliceMin;
        if(!this.fix_ymax) this.saved_ymax = this.ymax = sliceMax;
        // Assumed that for 2D data, dimensions are increasing
        this.increasing_x = true;
    }

    @Override
    public final void sourceUpdated(final XYData xydata) {
        long startTime = 0L;
        if(DEBUG.D) startTime = System.nanoTime();
        if(this.title == null) try{
            this.title = this.data.GetTitle();
        }catch(final Exception e){}
        if(this.xlabel == null) try{
            this.xlabel = (this.x_data == null) ? this.data.GetXLabel() : this.x_data.GetYLabel();
        }catch(final Exception e){}
        if(this.ylabel == null) try{
            this.ylabel = this.data.GetYLabel();
        }catch(final Exception e){}
        if(this.zlabel == null && this.type == Signal.TYPE_2D) try{
            this.zlabel = this.data.GetZLabel();
        }catch(final Exception e){}
        try{
            this.x = xydata.getX();
            this.xLong = xydata.getXLong();
            this.y = xydata.getY();
            this.Autoscale();
            this.saved_xmin = this.curr_xmin = this.xmin;
            this.saved_xmax = this.curr_xmax = this.xmax;
            this.saved_ymin = this.ymin;
            this.saved_ymax = this.ymax;
        }catch(final Exception e){}
        this.fireSignalUpdated(true);
        if(DEBUG.D) System.out.println("sourceUpdated took " + (System.nanoTime() - startTime) / 1E9);
    }

    /**
     * Method to call before execute a translate method.
     */
    protected final void StartTraslate() {
        this.t_xmax = this.xmax;
        this.t_xmin = this.xmin;
        this.t_ymax = this.ymax;
        this.t_ymin = this.ymin;
    }

    protected final double surfaceValue(final double x0, final double y0) {
        try{
            if(this.type == Signal.TYPE_2D && (this.mode2D == Signal.MODE_IMAGE || this.mode2D == Signal.MODE_CONTOUR)){
                this.img_yprev = Signal.findIndex(this.y, y0, this.img_yprev);
                this.img_xprev = Signal.findIndex(this.x, x0, this.img_xprev);
                System.out.println(this.y.length + "," + this.img_yprev + "," + this.x.length + "," + this.img_xprev);
                final int ixl = (this.x.length - 1 == this.img_xprev || x0 < this.x[this.img_xprev]) && this.img_xprev != 0 ? this.img_xprev - 1 : this.img_xprev, ixh = ixl + 1;
                final int iyl = (this.y.length - 1 == this.img_yprev || y0 < this.y[this.img_yprev]) && this.img_yprev != 0 ? this.img_yprev - 1 : this.img_yprev, iyh = iyl + 1;
                final double xl = this.x[ixl], xh = this.x[ixh], x = (x0 - xl) / (xh - xl);
                final double yl = this.y[iyl], yh = this.y[iyh], y = (y0 - yl) / (yh - yl);
                final double z00 = this.z[ixl * this.y.length + iyl];
                final double z10 = this.z[ixh * this.y.length + iyl];
                final double z01 = this.z[ixl * this.y.length + iyh];
                final double z11 = this.z[ixh * this.y.length + iyh];
                this.z_value = z00 * (1 - x) * (1 - y) + z10 * x * (1 - y) + z01 * (1 - x) * y + z11 * x * y;
                System.out.println(x0 + "," + y0 + ": " + z00 + "," + z10 + "," + z01 + "," + z11 + " -> " + this.z_value);
            }
        }catch(final Exception e){
            System.err.println("jScope.Signal.surfaceValue: " + e);
            final int idx = this.img_xprev * this.x.length + this.img_yprev;
            this.z_value = this.z[idx < this.z.length ? idx : this.z.length - 1];
        }
        return this.z_value;
    }

    /**
     * Translate signal of delta_x and delta_y
     *
     * @param delta_x
     *            x translation factor
     * @param delta_y
     *            y translation factor
     * @param x_log
     *            logaritm scale flag, if is logarithm scale true
     * @param y_log
     *            logaritm scale flag, if is logarithm scale true
     */
    protected final void Traslate(final double delta_x, final double delta_y, final boolean x_log, final boolean y_log) {
        if(x_log){
            this.xmax = this.t_xmax * delta_x;
            this.xmin = this.t_xmin * delta_x;
        }else{
            this.xmax = this.t_xmax + delta_x;
            this.xmin = this.t_xmin + delta_x;
        }
        if(y_log){
            this.ymax = this.t_ymax * delta_y;
            this.ymin = this.t_ymin * delta_y;
        }else{
            this.ymax = this.t_ymax + delta_y;
            this.ymin = this.t_ymin + delta_y;
        }
    }

    protected final void unfreeze() {
        if(this.freezeMode == Signal.NOT_FREEZED) return;
        this.freezeMode = Signal.NOT_FREEZED;
        this.xmin = this.freezedXMin;
        this.xmax = this.freezedXMax;
        for(int i = 0; i < this.pendingUpdatesV.size(); i++)
            this.dataRegionUpdated(this.pendingUpdatesV.elementAt(i));
        this.pendingUpdatesV.clear();
    }

    protected final boolean xLimitsInitialized() {
        return this.xLimitsInitialized;
    }
}

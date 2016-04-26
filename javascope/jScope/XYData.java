/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package jScope;

/**
 * @author manduchi
 */
final public class XYData{
    private static final double[] long2double(final long[] xLong) {
        final double[] x = new double[xLong.length];
        for(int i = 0; i < xLong.length; i++)
            x[i] = xLong[i];
        return x;
    }
    public final boolean   increasingX;
    private final int      nSamples;
    public final double    resolution; // Number of points/interval
    private final double[] x;
    private final long[]   xLong;
    private final double   xMin, xMax;
    private final float[]  y;

    public XYData(final double[] x, final float[] y, final double resolution){
        this(x, null, y, resolution, false);
    }

    public XYData(final double[] x, final float[] y, final double resolution, final boolean increasingX){
        this(x, null, y, resolution, increasingX);
    }

    public XYData(final double[] x, final float[] y, final double resolution, final boolean increasingX, final double xMin, final double xMax){
        this(x, null, y, resolution, increasingX, xMin, xMax);
    }

    private XYData(final double[] x, final long[] xLong, final float[] y, final double resolution, boolean increasingX){
        if(xLong == null) this.x = x;
        else{
            this.x = new double[xLong.length];
            for(int i = 0; i < xLong.length; i++)
                x[i] = xLong[i];
        }
        this.xLong = xLong;
        this.y = y;
        this.resolution = resolution;
        this.nSamples = (x.length < y.length) ? x.length : y.length;
        if(this.isEmpty()){
            this.xMax = Double.NEGATIVE_INFINITY;
            this.xMin = Double.POSITIVE_INFINITY;
        }else if(increasingX){
            int i;
            for(i = 0; Double.isNaN(this.x[i]) || Double.isInfinite(this.x[i]); i++);
            this.xMin = this.x[i];
            for(i = this.nSamples - 1; Double.isNaN(this.x[i]) || Double.isInfinite(this.x[i]); i--);
            this.xMax = this.x[i];
        }else{
            double xmax = Double.NEGATIVE_INFINITY;
            double xmin = Double.POSITIVE_INFINITY;
            increasingX = false;
            for(final double element : this.x){
                if(Double.isNaN(element)) continue;
                increasingX &= (xmax <= element);
                if(element < xmin) xmin = element;
                if(element > xmax) xmax = element;
            }
            this.xMin = xmin;
            this.xMax = xmax;
        }
        this.increasingX = increasingX;
    }

    private XYData(final double[] x, final long[] xLong, final float[] y, final double resolution, final boolean increasingX, final double xMin, final double xMax){
        this.x = x;
        this.xLong = xLong;
        this.y = y;
        this.resolution = resolution;
        this.nSamples = (x.length < y.length) ? x.length : y.length;
        this.increasingX = increasingX;
        this.xMin = (xMin == Double.NEGATIVE_INFINITY && increasingX) ? this.x[0] : xMin;
        this.xMax = (xMax == Double.POSITIVE_INFINITY && increasingX) ? this.x[this.x.length - 1] : xMax;
    }

    public XYData(final long xLong[], final float y[], final double resolution, final boolean increasingX, final double xMin, final double xMax){
        this(XYData.long2double(xLong), xLong, y, resolution, increasingX, xMin, xMax);
    }

    public XYData(final long[] xLong, final float[] y, final double resolution){
        this(null, xLong, y, resolution, false);
    }

    public XYData(final long[] xLong, final float[] y, final double resolution, final boolean increasingX){
        this(null, xLong, y, resolution, increasingX);
    }

    public XYData(final XYData old, final XYData add){
        if(old == null || !old.increasingX || !add.increasingX){
            this.x = add.getX();
            this.xLong = add.getXLong();
            this.y = add.getY();
            this.xMin = add.getXMin();
            this.xMax = add.getXMax();
            this.increasingX = add.increasingX;
            this.resolution = add.resolution;
            this.nSamples = add.nSamples;
        }else{
            int samplesBefore, samplesAfter;
            for(samplesBefore = 0; samplesBefore < old.x.length && old.x[samplesBefore] < add.getXMin(); samplesBefore++);
            if(samplesBefore > 0 && samplesBefore < old.x.length && old.x[samplesBefore] > add.getXMin()) samplesBefore--;
            for(samplesAfter = 0; samplesAfter < old.x.length - 1 && old.x[this.x.length - samplesAfter - 1] > add.getX()[add.getX().length - 1]; samplesAfter++);
            this.x = new double[samplesBefore + add.getX().length + samplesAfter];
            System.arraycopy(old.getX(), 0, this.x, 0, samplesBefore);
            System.arraycopy(add.getX(), 0, this.x, samplesBefore, add.getX().length);
            System.arraycopy(old.getX(), samplesBefore, this.x, this.x.length - samplesAfter, samplesAfter);
            this.y = new float[this.x.length];
            System.arraycopy(old.getY(), 0, this.y, 0, samplesBefore);
            System.arraycopy(add.getY(), 0, this.y, samplesBefore, add.getY().length);
            System.arraycopy(old.getY(), samplesBefore, this.y, this.y.length - samplesAfter, samplesAfter);
            if(old.getXLong() != null && add.getXLong() != null){
                this.xLong = new long[this.x.length];
                System.arraycopy(old.getXLong(), 0, this.xLong, 0, samplesBefore);
                System.arraycopy(add.getXLong(), 0, this.xLong, samplesBefore, add.getXLong().length);
                System.arraycopy(old.getXLong(), samplesBefore, this.xLong, this.xLong.length - samplesAfter, samplesAfter);
            }else this.xLong = null;
            this.xMin = (old.xMin < add.xMin) ? old.xMin : add.xMin;
            this.xMax = (old.xMax > add.xMax) ? old.xMax : add.xMax;
            this.resolution = (old.resolution < add.resolution) ? old.resolution : add.resolution;
            this.nSamples = this.x.length;
            this.increasingX = true;
        }
    }

    public final double[] getX() {
        return this.x;
    }

    public final long[] getXLong() {
        return this.xLong;
    }

    public final double getXMax() {
        return this.xMax;
    }

    public final double getXMin() {
        return this.xMin;
    }

    public final float[] getY() {
        return this.y;
    }

    public final boolean isEmpty() {
        return this.nSamples == 0;
    }
}

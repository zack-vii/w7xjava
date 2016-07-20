package jscope;

import java.awt.Color;
import java.awt.image.IndexColorModel;

final public class ColorMap{
    @SuppressWarnings("unused")
    private static final class colorMapBuilder{
        private int     length = 255;
        private float   max    = 255;
        private float   min    = 0;
        private float[] pB     = new float[4];
        private float[] pG     = new float[5];
        private float[] pR     = new float[5];
        private float[] vB     = new float[4];
        private float[] vG     = new float[5];
        private float[] vR     = new float[5];

        public colorMapBuilder(final int length, final float delta){
            this.length = length;
            this.pR[0] = this.min;
            this.pR[1] = this.min + delta * 0.2f;
            this.pR[2] = this.min + delta * 0.6f;
            this.pR[3] = this.min + delta * 0.8f;
            this.pR[4] = this.min + delta * 1.f;
            this.vR[0] = 255;
            this.vR[1] = 0;
            this.vR[2] = 0;
            this.vR[3] = 255;
            this.vR[4] = 255;
            this.pG[0] = this.min;
            this.pG[1] = this.min + delta * 0.2f;
            this.pG[2] = this.min + delta * 0.4f;
            this.pG[3] = this.min + delta * 0.8f;
            this.pG[4] = this.min + delta * 1.f;
            this.vG[0] = 0;
            this.vG[1] = 0;
            this.vG[2] = 255;
            this.vG[3] = 255;
            this.vG[4] = 0;
            this.pB[0] = this.min;
            this.pB[1] = this.min + delta * 0.4f;
            this.pB[2] = this.min + delta * 0.6f;
            this.pB[3] = this.min + delta * 1.f;
            this.vB[0] = 255;
            this.vB[1] = 255;
            this.vB[2] = 0;
            this.vB[3] = 0;
        }

        public final ColorMap build(final int length) {
            final Color[] colors = new Color[length];
            int r[], g[], b[];
            r = ColorMap.getValues(length, this.pR, this.vR);
            g = ColorMap.getValues(length, this.pG, this.vG);
            b = ColorMap.getValues(length, this.pB, this.vB);
            for(int i = 0; i < length; i++)
                colors[i] = new Color(r[i], g[i], b[i]);
            return new ColorMap();
        }

        public byte[] getBlueIntValues() {
            int c[];
            final byte b[] = new byte[this.length];
            c = ColorMap.getValues(this.length, this.pB, this.vB);
            for(int i = 0; i < this.length; b[i] = (byte)c[i], i++);
            return b;
        }

        public float[] getBluePoints() {
            return this.pB;
        }

        public float[] getBlueValues() {
            return this.vB;
        }

        public byte[] getGreenIntValues() {
            int c[];
            final byte b[] = new byte[this.length];
            c = ColorMap.getValues(this.length, this.pG, this.vG);
            for(int i = 0; i < this.length; i++)
                b[i] = (byte)c[i];
            return b;
        }

        public float[] getGreenPoints() {
            return this.pG;
        }

        public float[] getGreenValues() {
            return this.vG;
        }

        public byte[] getRedIntValues() {
            int c[];
            final byte b[] = new byte[this.length];
            c = ColorMap.getValues(this.length, this.pR, this.vR);
            for(int i = 0; i < this.length; i++)
                b[i] = (byte)c[i];
            return b;
        }

        public float[] getRedPoints() {
            return this.pR;
        }

        public float[] getRedValues() {
            return this.vR;
        }

        public void setBlueParam(final float p[], final float v[]) {
            this.pB = p;
            this.vB = v;
        }

        public void setGreenParam(final float p[], final float v[]) {
            this.pG = p;
            this.vG = v;
        }

        public void setMax(final float max) {
            this.max = max;
        }

        public void setMin(final float min) {
            this.min = min;
        }

        public void setRedParam(final float p[], final float v[]) {
            this.pR = p;
            this.vR = v;
        }
    }
    public static final class ColorProfile{
        public boolean  bitClip;
        public int      bitShift;
        public boolean  useRGB;
        public ColorMap colorMap;

        public ColorProfile(){
            this(false);
        }

        public ColorProfile(final boolean useRGB){
            this(new ColorMap(), 0, false, useRGB);
        }

        public ColorProfile(final ColorMap colorMap){
            this(colorMap, 0, false, false);
        }

        public ColorProfile(final ColorMap colorMap, final int bitShift, final boolean bitClip){
            this(colorMap, bitShift, bitClip, false);
        }

        public ColorProfile(final ColorMap colorMap, final int bitShift, final boolean bitClip, final boolean useRGB){
            this.useRGB = useRGB;
            this.colorMap = colorMap;
            this.bitShift = bitShift;
            this.bitClip = bitClip;
        }

        public ColorProfile(final ColorProfile cp){
            if(cp == null){
                this.colorMap = new ColorMap();
                this.bitShift = 0;
                this.bitClip = this.useRGB = false;
            }else{
                this.colorMap = cp.colorMap;
                this.bitShift = cp.bitShift;
                this.bitClip = cp.bitClip;
                this.useRGB = cp.useRGB;
            }
        }
    }

    private static final int[] getValues(final int nVal, final float p[], final float v[]) {
        final int out[] = new int[nVal];
        final float dx = (p[p.length - 1] - p[0]) / (nVal - 1);
        float val = 0;
        int idx = 0;
        float c1 = (v[0] - v[1]) / (p[0] - p[1]);
        for(int i = 0; i < nVal; i++, c1 = (v[idx] - v[idx + 1]) / (p[idx] - p[idx + 1])){
            if(p[idx] == p[idx + 1]){
                idx++;
                i--;
                continue;
            }
            val = i * dx;
            if(val > p[idx + 1]){
                idx++;
                i--;
                continue;
            }
            out[i] = (int)(c1 * val - p[idx] * c1 + v[idx]);
            if(out[i] > 255) out[i] = 255;
            else if(out[i] < 0) out[i] = 0;
        }
        return out;
    }
    Color           colors[];
    IndexColorModel indexColorModel = null;
    float           max;
    float           min;
    public String   name            = "unnamed";

    public ColorMap(){
        this.name = "gray";
        this.colors = new Color[256];
        for(int i = 0; i < this.colors.length; i++){
            this.colors[i] = new Color(i, i, i);
        }
    }

    ColorMap(final String name, final int r[], final int g[], final int b[]){
        this.name = name;
        this.colors = new Color[256];
        for(int i = 0; i < this.colors.length; i++){
            this.colors[i] = new Color(r[i], g[i], b[i]);
        }
    }

    public Color getColor(final float val) {
        if(val < this.min) return this.colors[0];
        else if(val > this.max) return this.colors[this.colors.length - 1];
        final int idx = (int)((val - this.max) / (this.max - this.min) + 1) * (this.colors.length - 1);
        return this.colors[idx];
    }

    public Color getColor(final float val, final float min, final float max) {
        final int idx = (int)((val - min) / (max - min) * (this.colors.length - 1));
        return this.colors[idx];
    }

    public Color[] getColors() {
        return this.colors;
    }

    public IndexColorModel getIndexColorModel(final int numBit) {
        if(this.indexColorModel == null){
            final byte[] r = new byte[this.colors.length];
            final byte[] g = new byte[this.colors.length];
            final byte[] b = new byte[this.colors.length];
            for(int i = 0; i < this.colors.length; i++){
                r[i] = (byte)this.colors[i].getRed();
                g[i] = (byte)this.colors[i].getGreen();
                b[i] = (byte)this.colors[i].getBlue();
            }
            this.indexColorModel = new IndexColorModel(numBit, this.colors.length, r, g, b);
        }
        return this.indexColorModel;
    }

    public float getMax() {
        return this.max;
    }

    public float getMin() {
        return this.min;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
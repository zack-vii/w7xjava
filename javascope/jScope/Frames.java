package jScope;

/* $Id$ */
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import debug.DEBUG;
import jScope.ColorMap.ColorProfile;

@SuppressWarnings("serial")
public final class Frames extends Canvas{
    static final class FrameCache{
        public static final class FrameDescriptor{
            public final byte[] buffer;
            public final Image  image;
            public final int    updateCount;
            public final Image  updatedImage;

            FrameDescriptor(final byte[] buffer, final Image image, final Image updatedImage, final int updateCount){
                this.buffer = buffer;
                this.image = image;
                this.updatedImage = updatedImage;
                this.updateCount = updateCount;
            }
        }
        static final int MAX_CACHE_MEM = 0x8000000; // 128MB

        private final static Image createCompatibleImage(final BufferedImage image) {
            final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            final BufferedImage newImage = gc.createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);
            final Graphics2D g = newImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return newImage;
        }
        int                                               bytesPerPixel;
        ColorProfile                                      colorProfile;
        FrameData                                         fd;
        Dimension                                         frameDim;
        private final Frames                              frames;
        private int                                       frameType;
        private int                                       numFrames;
        private int                                       pixelSize;
        private final Hashtable<Integer, FrameDescriptor> recentFrames;
        private final Vector<Object>                      recentIdxV  = new Vector<Object>();
        private MediaTracker                              tracker;
        private int                                       updateCount = 0;

        FrameCache(final FrameCache fc, final Frames frames){
            if(DEBUG.M) System.out.println("Frames.FrameCache(" + fc + ")");
            this.frames = frames;
            this.fd = fc.fd;
            this.colorProfile = new ColorProfile();
            this.recentFrames = new Hashtable<Integer, FrameDescriptor>();
            final Enumeration<Integer> fds = fc.recentFrames.keys();
            while(fds.hasMoreElements()){
                final Integer idx = fds.nextElement();
                final FrameDescriptor fDescr = fc.recentFrames.get(idx);
                this.recentFrames.put(idx, fDescr);
            }
        }

        public FrameCache(final Frames frames){
            if(DEBUG.M) System.out.println("Frames.FrameCache()");
            this.frames = frames;
            this.recentFrames = new Hashtable<Integer, FrameDescriptor>();
            this.colorProfile = new ColorProfile();
            this.tracker = new MediaTracker(frames);
        }

        private final void doBitShift(final BufferedImage bi, final byte[] buffer) throws Exception {
            if(this.frameType != FrameData.BITMAP_IMAGE_16){ return; }
            if(DEBUG.M) System.out.println("Frames.FrameCache.doBitShift(" + bi + "," + buffer + "[" + buffer.length + "])");
            final ByteArrayInputStream b = new ByteArrayInputStream(buffer);
            final DataInputStream din = new DataInputStream(b);
            final WritableRaster wr = bi.getRaster();
            final DataBuffer db = wr.getDataBuffer();
            final int nPixels = db.getSize();
            if(nPixels != this.frameDim.width * this.frameDim.height) throw new Exception("INTERNAL ERRROR: Inconsistend frame dimension when getting frame");
            int val;
            final int rot32 = (-this.colorProfile.bitShift) % 32;
            for(int j = 0; j < nPixels; j++){
                val = Integer.rotateRight(din.readShort(), rot32) & 0xFFFF;
                if(this.colorProfile.bitClip) db.setElem(j, val > 255 ? 255 : val);
                else db.setElem(j, (val & 0xFF));
            }
        }

        private final void doBitShift(final BufferedImage bi, final FrameDescriptor fDesc) throws Exception {
            this.doBitShift(bi, fDesc.buffer);
        }

        public final byte[] getBufferAt(final int idx) {
            if(DEBUG.M) System.out.println("Frames.FrameCache.getBufferAt(" + idx + ")");
            final FrameDescriptor fDescr = this.getFrameDescr(idx);
            if(fDescr == null) return null;
            return fDescr.buffer;
        }

        public final ColorProfile getColorProfile() {
            return this.colorProfile;
        }

        private final FrameDescriptor getFrameDescr(final int idx) {
            final FrameDescriptor fDesc = this.recentFrames.get(new Integer(idx));
            if(fDesc != null) return fDesc;
            try{
                this.loadFrame(idx);
                return this.recentFrames.get(new Integer(idx));
            }catch(final Exception exc){
                System.err.println("# >> Error Loading frame at " + idx + ": " + exc);
                return null;
            }
        }

        public final Dimension getFrameDimension() {
            if(this.frameDim == null) try{
                this.frameDim = this.fd.GetFrameDimension();
            }catch(final Exception e){}
            return this.frameDim;
        }

        private final int getFrameType() {
            return this.frameType;
        }

        private final Image getImageAt(final int idx) throws IOException {
            if(DEBUG.M) System.out.println("Frames.FrameCache.getImageAt(" + idx + ")");
            final FrameDescriptor fDesc = this.getFrameDescr(idx);
            if(fDesc == null) return null;
            if(fDesc.updateCount == this.updateCount) // fDesc.updatedImage is still ok
            return fDesc.updatedImage;
            // Otherwise it is necessary to update it
            Image img;
            if(this.frameType == FrameData.BITMAP_IMAGE_32) img = fDesc.image;
            else{
                final ColorModel colorModel = this.colorProfile.colorMap.getIndexColorModel(this.pixelSize < 32 ? this.pixelSize : 16);
                img = new BufferedImage(colorModel, ((BufferedImage)fDesc.image).getRaster(), false, null);
            }
            try{
                this.doBitShift((BufferedImage)img, fDesc);
            }catch(final Exception exc){
                System.err.println(exc);
                return null;
            }
            img = FrameCache.createCompatibleImage((BufferedImage)img);
            this.tracker = new MediaTracker(this.frames);
            this.tracker.addImage(img, idx);
            try{
                this.tracker.waitForID(idx);
            }catch(final Exception exc){}
            this.recentFrames.put(new Integer(idx), new FrameDescriptor(fDesc.buffer, fDesc.image, img, this.updateCount));
            return img;
        }

        private final int getNumFrames() {
            return this.numFrames;
        }

        private final float getValueAt(final int idx, final int pixel) {
            final FrameDescriptor fDescr = this.getFrameDescr(idx);
            if(fDescr == null) return Float.NaN;
            try{
                final DataInputStream din = new DataInputStream(new ByteArrayInputStream(fDescr.buffer));
                try{
                    switch(this.frameType){
                        case FrameData.BITMAP_IMAGE_8:
                            din.skipBytes(pixel);
                            return 0xFF & din.readByte();
                        case FrameData.BITMAP_IMAGE_16:
                            din.skipBytes(pixel * Short.BYTES);
                            return 0xFFFF & din.readShort();
                        case FrameData.BITMAP_IMAGE_32:
                            din.skipBytes(pixel * Integer.BYTES);
                            final int intbuf = din.readInt();
                            return ((Math.round(((intbuf & 0xFF0000) * 100.) / 0xFF0000) * 100) //
                            + Math.round(((intbuf & 0xFF00) * 100.) / 0xFF00)) * 100 //
                            + Math.round(((intbuf & 0xFF) * 100.) / 0xFF);
                        case FrameData.BITMAP_IMAGE_FLOAT:
                            din.skipBytes(pixel * Float.BYTES);
                            return din.readFloat();
                        default:
                            System.err.println("# INTERNAL ERROR frame values requested for unexpected type: " + this.frameType);
                    }
                }finally{
                    din.close();
                }
            }catch(final Exception e){}
            return Float.NaN;
        }

        private final float[] getValuesAt(final int idx) {
            if(DEBUG.M) System.out.println("Frames.FrameCache.getValuesAt(" + idx + ")");
            final FrameDescriptor fDescr = this.getFrameDescr(idx);
            if(fDescr == null) return null;
            final byte[] buf = fDescr.buffer;
            final int n_pix = this.frameDim.width * this.frameDim.height;
            final float[] values = new float[n_pix];
            final ByteArrayInputStream b = new ByteArrayInputStream(buf);
            final DataInputStream din = new DataInputStream(b);
            try{
                switch(this.frameType){
                    case FrameData.BITMAP_IMAGE_8:
                        for(int j = 0; j < n_pix; j++)
                            values[j] = 0xFF & din.readByte();
                        break;
                    case FrameData.BITMAP_IMAGE_16:
                        for(int j = 0; j < n_pix; j++)
                            values[j] = 0xFFFF & din.readShort();
                        break;
                    case FrameData.BITMAP_IMAGE_32:
                        int intbuf;
                        for(int j = 0; j < n_pix; j++){
                            intbuf = din.readInt();
                            values[j] = Math.round(((intbuf & 0xFF0000) * 100.) / 0xFF0000);
                            values[j] = Math.round(((intbuf & 0xFF00) * 100.) / 0xFF00) + values[j] * 100;
                            values[j] = Math.round(((intbuf & 0xFF) * 100.) / 0xFF) + values[j] * 100;
                        }
                        break;
                    case FrameData.BITMAP_IMAGE_FLOAT:
                        for(int j = 0; j < n_pix; j++)
                            values[j] = din.readFloat();
                        break;
                    default:
                        System.err.println("# INTERNAL ERROR frame values requested for unexpected type: " + this.frameType);
                        return null;
                }
                din.close();
                b.close();
                return values;
            }catch(final IOException exc){
                System.err.println("# INTERNAL ERROR Getting Frame values: " + exc);
            }
            return null;
        }

        private final void loadFrame(final int idx) throws Exception {
            if(DEBUG.M) System.out.println("Frames.FrameCache.loadFrame(" + idx + ")");
            this.frameType = this.fd.GetFrameType();
            this.frameDim = this.fd.GetFrameDimension();
            this.numFrames = this.fd.GetNumFrames();
            final byte[] buf = this.fd.GetFrameAt(idx);
            if(buf == null) return;
            BufferedImage img;
            DataBuffer db;
            WritableRaster raster;
            switch(this.frameType){
                case FrameData.BITMAP_IMAGE_8:{
                    if(DEBUG.M) System.out.println("Frames.FrameData.BITMAP_IMAGE_8");
                    this.pixelSize = 8;
                    this.bytesPerPixel = 1;
                    // FlipFrame(buf, frameDim, 1);
                    final ColorModel colorModel = this.colorProfile.colorMap.getIndexColorModel(8);
                    db = new DataBufferByte(buf, buf.length);
                    raster = Raster.createInterleavedRaster(db, this.frameDim.width, this.frameDim.height, this.frameDim.width, 1, new int[]{0}, null);
                    img = new BufferedImage(colorModel, raster, false, null);
                    break;
                }
                case FrameData.BITMAP_IMAGE_16:{
                    if(DEBUG.M) System.out.println("Frames.FrameData.BITMAP_IMAGE_16");
                    this.pixelSize = 16;
                    this.bytesPerPixel = 2;
                    final ColorModel colorModel = this.colorProfile.colorMap.getIndexColorModel(16);
                    raster = Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT, this.frameDim.width, this.frameDim.height, this.frameDim.width, 1, new int[]{0}, null);
                    img = new BufferedImage(colorModel, raster, false, null);
                    try{
                        this.doBitShift(img, buf);
                    }catch(final Exception exc){
                        System.err.println(exc);
                    }
                    break;
                }
                case FrameData.BITMAP_IMAGE_32:{
                    if(DEBUG.M) System.out.println("Frames.FrameData.BITMAP_IMAGE_32");
                    this.pixelSize = 32;
                    this.bytesPerPixel = 4;
                    final int n_pix = this.frameDim.width * this.frameDim.height;
                    final int buf_out[] = new int[n_pix];
                    final ByteArrayInputStream b = new ByteArrayInputStream(buf);
                    final DataInputStream din = new DataInputStream(b);
                    for(int j = 0; j < n_pix; j++){
                        buf_out[j] = 0xFF000000 | din.readInt();
                    }
                    if(DEBUG.A) DEBUG.printIntArray(buf_out, 1, this.frameDim.width, this.frameDim.height, 1);
                    final ColorModel colorModel = new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);
                    db = new DataBufferInt(buf_out, buf.length);
                    raster = Raster.createPackedRaster(db, this.frameDim.width, this.frameDim.height, this.frameDim.width, new int[]{0xff0000, 0xff00, 0xff, 0xff000000}, null);
                    img = new BufferedImage(colorModel, raster, false, null);
                    break;
                }
                case FrameData.BITMAP_IMAGE_FLOAT:{
                    if(DEBUG.M) System.out.println("Frames.FrameData.BITMAP_IMAGE_FLOAT");
                    this.pixelSize = 32;
                    this.bytesPerPixel = 4;
                    final int n_pix = this.frameDim.width * this.frameDim.height;
                    final float buf_out[] = new float[n_pix];
                    final ByteArrayInputStream b = new ByteArrayInputStream(buf);
                    final DataInputStream din = new DataInputStream(b);
                    float max = Float.NEGATIVE_INFINITY;
                    float min = Float.POSITIVE_INFINITY;
                    for(int j = 0; j < n_pix; j++){
                        buf_out[j] = din.readFloat();
                        if(buf_out[j] > max) max = buf_out[j];
                        if(buf_out[j] < min) min = buf_out[j];
                    }
                    final short buf_out1[] = new short[n_pix];
                    for(int j = 0; j < n_pix; j++){
                        buf_out1[j] = (short)(255 * (buf_out[j] - min) / (max - min));
                    }
                    final ColorModel colorModel = this.colorProfile.colorMap.getIndexColorModel(16);
                    db = new DataBufferUShort(buf_out1, buf.length);
                    raster = Raster.createInterleavedRaster(db, this.frameDim.width, this.frameDim.height, this.frameDim.width, 1, new int[]{0}, null);
                    img = new BufferedImage(colorModel, raster, false, null);
                    break;
                }
                case FrameData.AWT_IMAGE:
                    if(DEBUG.M) System.out.println("Frames.FrameData.AWT_IMAGE");
                    img = (BufferedImage)Toolkit.getDefaultToolkit().createImage(buf);
                    break;
                default:
                    return;
            }
            this.tracker = new MediaTracker(this.frames);
            this.tracker.addImage(img, idx);
            this.recentFrames.put(new Integer(idx), new FrameDescriptor(buf, img, img, this.updateCount));
            try{
                this.tracker.waitForID(idx);
            }catch(final Exception exc){
                System.err.println("# >> Frames.waitForID: " + exc);
            }
            int maxStoreFrames = FrameCache.MAX_CACHE_MEM / buf.length;
            if(maxStoreFrames < 1) maxStoreFrames = 1;
            this.recentIdxV.insertElementAt(new Integer(idx), 0);
            if(this.recentIdxV.size() > maxStoreFrames){
                final Object delIdx = this.recentIdxV.elementAt(maxStoreFrames);
                this.recentFrames.remove(delIdx);
                this.recentIdxV.removeElementAt(maxStoreFrames);
            }
        }

        public final void setBitShift(final int bitShift, final boolean bitClip) {
            if(DEBUG.M) System.out.println("Frames.FrameCache.setBitShift(" + bitShift + ", " + bitClip + ")");
            if(this.colorProfile.bitShift == bitShift && this.colorProfile.bitClip == bitClip) return;
            this.colorProfile.bitShift = bitShift;
            this.colorProfile.bitClip = bitClip;
            this.updateCount++;
        }

        public final void setColorMap(final ColorMap cm) {
            if(DEBUG.M) System.out.println("Frames.FrameCache.setColorMap(" + cm + ")");
            if(this.colorProfile.colorMap.equals(cm)) return;
            this.colorProfile.colorMap = cm;
            this.updateCount++;
        }

        public final void setColorProfile(final ColorProfile cp) {
            if(DEBUG.M) System.out.println("Frames.FrameCache.setColorProfile(" + cp + ")");
            if(this.colorProfile.equals(cp)) return;
            this.colorProfile = cp;
            this.updateCount++;
        }

        public final void setFrameData(final FrameData fd) {
            if(DEBUG.M) System.out.println("Frames.FrameCache.setFrameData(" + fd + ")");
            this.fd = fd;
            try{
                this.numFrames = fd.GetNumFrames();
            }catch(final Exception exc){
                this.numFrames = 0;
            }
        }
    } // End class FrameCache

    public static final int DecodeImageType(final byte buf[]) {
        if(DEBUG.M) System.out.println("Frames.DecodeImageType(" + buf + ")");
        final String s = new String(buf, 0, 20);
        if(s.indexOf("GIF") == 0) return FrameData.AWT_IMAGE;
        if(s.indexOf("PNG") == 1) return FrameData.AWT_IMAGE;
        if(s.indexOf("JFIF") == 6) return FrameData.AWT_IMAGE;
        return FrameData.JAI_IMAGE;
    }
    protected boolean        aspect_ratio    = true;
    private final FrameCache cache;
    protected int            color_idx;
    private int              curr_frame_idx  = -1;
    protected int            curr_grab_frame = -1;
    Vector<Float>            frame_time      = new Vector<Float>();
    protected int[]          frames_pixel_array;
    protected Rectangle      frames_pixel_roi;
    protected float[]        frames_value_array;
    protected float          ft[]            = null;
    protected boolean        horizontal_flip = false;
    protected int            img_height      = -1;
    protected int            img_width       = -1;
    protected int[]          pixel_array;
    protected int            pixel_size;
    protected Point          sel_point       = null;
    protected float[]        values_array;
    protected boolean        vertical_flip   = false;
    Rectangle                view_rect       = null;
    protected int            x_measure_pixel = 0, y_measure_pixel = 0;
    Rectangle                zoom_rect       = null;

    public Frames(){
        super();
        if(DEBUG.M) System.out.println("Frames()");
        this.cache = new FrameCache(this);
    }

    Frames(final Frames frames){
        super();
        if(DEBUG.M) System.out.println("Frames(" + frames + ")");
        this.img_width = frames.img_width;
        this.img_height = frames.img_height;
        this.cache = new FrameCache(frames.cache, this);
        if(this.frame_time.size() != 0) this.frame_time.removeAllElements();
        if(frames.zoom_rect != null) this.zoom_rect = new Rectangle(frames.zoom_rect);
        if(frames.view_rect != null) this.view_rect = new Rectangle(frames.view_rect);
        this.curr_frame_idx = frames.curr_frame_idx;
    }

    public final void applyColorMap(final ColorMap colorMap) {
        if(DEBUG.M) System.out.println("Frames.applyColorModel(" + colorMap + ")");
        if(colorMap == null) return;
        this.cache.setColorMap(colorMap);
    }

    public final boolean contain(final Point p, final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.contain(" + this.sel_point + ", " + d + ")");
        final Dimension fr_dim = this.getFrameSize(this.curr_frame_idx, d);
        if(p.x > fr_dim.width || p.y > fr_dim.height) return false;
        return true;
    }

    public final void FlipFrame(final byte buf[], final Dimension d, final int num_byte_pixel) {
        if(DEBUG.M) System.out.println("Frames.FlipFrame(" + buf + ", " + d + ", " + num_byte_pixel + ")");
        if(!this.vertical_flip && !this.horizontal_flip) return;
        final int img_size = d.height * d.width * num_byte_pixel;
        final byte tmp[] = new byte[img_size];
        int i, k, l;
        final int h = this.vertical_flip ? d.height - 1 : 0;
        final int w = this.horizontal_flip ? d.width - 1 : 0;
        for(i = 0; i < d.width; i++)
            for(k = 0; k < d.height; k++)
                for(l = 0; l < num_byte_pixel; l++)
                    tmp[((Math.abs(h - k) * d.width) + Math.abs(w - i)) * num_byte_pixel + l] = buf[((k * d.width) + i) * num_byte_pixel + l];
        System.arraycopy(tmp, 0, buf, 0, img_size);
    }

    public final boolean getAspectRatio() {
        return this.aspect_ratio;
    }

    public final int GetColorIdx() {
        return this.color_idx;
    }

    public final ColorProfile getColorProfile() {
        return this.cache.getColorProfile();
    }

    public final Object GetFrame(int idx) {
        if(DEBUG.M) System.out.println("Frames.GetFrame(" + idx + ")");
        if(idx < 0) return null;
        final int numFrames = this.cache.getNumFrames();
        if(idx >= numFrames) idx = numFrames - 1;
        if(idx < 0) return null;
        this.curr_frame_idx = idx;
        BufferedImage img;
        try{
            img = (BufferedImage)this.cache.getImageAt(idx);
        }catch(final IOException e){
            System.err.println("# >> Error getting frame " + idx + ": " + e);
            return null;
        }
        if(this.img_width == -1){
            this.img_width = img.getWidth(this);
            this.img_height = img.getHeight(this);
        }
        // curr_grab_frame = curr_frame_idx;
        return img;
    }

    public final Object GetFrame(final int idx, final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.GetFrame(" + idx + ", " + d + ")");
        return this.GetFrame(idx);
    }

    protected final Dimension GetFrameDim(final int idx) {
        if(DEBUG.M) System.out.println("Frames.GetFrameDim(" + idx + ")");
        return this.cache.getFrameDimension();
    }

    public final int GetFrameIdx() {
        if(DEBUG.M) System.out.println("Frames.GetFrameIdx()");
        return this.curr_frame_idx;
    }

    public final int GetFrameIdxAtTime(final float t) {
        if(DEBUG.M) System.out.println("Frames.GetFrameIdxAtTime(" + t + ")");
        int idx = -1;
        float dt;
        final int numFrames = this.cache.getNumFrames();
        if(numFrames <= 0) return -1;
        if(numFrames == 1) dt = 1;
        else dt = this.frame_time.elementAt(1).floatValue() - this.frame_time.elementAt(0).floatValue();
        if(t >= this.frame_time.elementAt(numFrames - 1).floatValue() + dt) return -1;
        if(t >= this.frame_time.elementAt(numFrames - 1).floatValue()) return numFrames - 1;
        for(int i = 0; i < numFrames - 1; i++){
            if(t >= this.frame_time.elementAt(i).floatValue() && t < this.frame_time.elementAt(i + 1).floatValue()){
                idx = i;
                break;
            }
        }
        return idx;
    }

    public final Point getFramePoint(final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.setFramePoint(" + d + ")");
        if(this.sel_point != null) return this.getImagePoint(new Point(this.sel_point.x, this.sel_point.y), d);
        return new Point(0, 0);
    }

    // return pixel position in the frame
    // Argument point is fit to frame dimension
    public final Point getFramePoint(final Point p, final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.setFramePoint(" + p + ", " + d + ")");
        final Point p_out = new Point(0, 0);
        if(p.x < 0) p.x = 0;
        if(p.y < 0) p.y = 0;
        if(this.curr_frame_idx != -1 && this.cache.getNumFrames() != 0){
            Dimension view_dim;
            Dimension dim;
            final Dimension fr_dim = this.getFrameSize(this.curr_frame_idx, d);
            if(this.zoom_rect == null){
                view_dim = this.GetFrameDim(this.curr_frame_idx);
                dim = view_dim;
            }else{
                dim = new Dimension(this.zoom_rect.width, this.zoom_rect.height);
                view_dim = new Dimension(this.zoom_rect.x + this.zoom_rect.width, this.zoom_rect.y + this.zoom_rect.height);
                p_out.x = this.zoom_rect.x;
                p_out.y = this.zoom_rect.y;
            }
            final double ratio_x = (double)dim.width / fr_dim.width;
            final double ratio_y = (double)dim.height / fr_dim.height;
            p_out.x += ratio_x * p.x;
            if(p_out.x > view_dim.width - 1){
                p_out.x = view_dim.width - 1;
                p.x = fr_dim.width;
            }
            p_out.y += ratio_y * p.y;
            if(p_out.y > view_dim.height - 1){
                p_out.y = view_dim.height - 1;
                p.y = fr_dim.height;
            }
        }
        return p_out;
    }

    /*return frame image pixel dimension*/
    public final Dimension getFrameSize(final int idx, final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.getFrameSize(" + idx + ", " + d + ")");
        int width, height;
        // Border image pixel
        final Dimension dim_b = new Dimension(d.width - 1, d.height - 1);
        width = dim_b.width;
        height = dim_b.height;
        if(this.getAspectRatio()){
            final Dimension dim = this.GetFrameDim(idx);
            int w = dim.width;
            int h = dim.height;
            if(this.zoom_rect != null){
                w = this.zoom_rect.width;
                h = this.zoom_rect.height;
            }
            final double ratio = (double)w / h;
            width = (int)(ratio * d.height);
            if(width > d.width){
                width = d.width;
                height = (int)(d.width / ratio);
            }
        }
        /*
        Temporary fix, in order to avoid modification image if it is resized,
        must be investigate
         */
        return new Dimension(width, height);
        // return GetFrameDim(idx);
    }

    public final float[] getFramesTime() {
        if(DEBUG.M) System.out.println("Frames.getFramesTime()");
        if(this.frame_time == null || this.frame_time.size() == 0) return null;
        if(this.ft == null){
            this.ft = new float[this.frame_time.size()];
            for(int i = 0; i < this.frame_time.size(); i++){
                this.ft[i] = this.frame_time.elementAt(i).floatValue();
            }
        }
        return this.ft;
    }

    public final float GetFrameTime() {
        if(DEBUG.M) System.out.println("Frames.GetFrameTime()");
        float t_out = 0;
        if(this.curr_frame_idx != -1 && this.frame_time.size() != 0){
            t_out = this.frame_time.elementAt(this.curr_frame_idx).floatValue();
        }
        return t_out;
    }

    public final int getFrameType() {
        return this.cache.getFrameType();
    }

    public final boolean getHorizontalFlip() {
        return this.horizontal_flip;
    }

    private final Point getImageBufferPoint(final int x, final int y) {
        if(DEBUG.M) System.out.println("Frames.getImageBufferPoint(" + x + ", " + y + ")");
        final Point p = new Point();
        p.x = x;
        p.y = y;
        if(this.horizontal_flip) p.y = this.img_height - y - 1;
        if(this.vertical_flip) p.x = this.img_width - x - 1;
        return p;
    }

    // return point position in the frame shows
    public final Point getImagePoint(final Point p, final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.getImagePoint()");
        final Point p_out = new Point(0, 0);
        if(this.curr_frame_idx != -1 && this.cache.getNumFrames() != 0){
            final Dimension fr_dim = this.getFrameSize(this.curr_frame_idx, d);
            Dimension view_dim;
            Dimension dim;
            if(this.zoom_rect == null){
                view_dim = this.GetFrameDim(this.curr_frame_idx);
                dim = view_dim;
            }else{
                dim = new Dimension(this.zoom_rect.width, this.zoom_rect.height);
                view_dim = new Dimension(this.zoom_rect.x + this.zoom_rect.width, this.zoom_rect.y + this.zoom_rect.height);
                p.x -= this.zoom_rect.x;
                p.y -= this.zoom_rect.y;
            }
            final double ratio_x = (double)fr_dim.width / dim.width;
            final double ratio_y = (double)fr_dim.height / dim.height;
            p_out.x = (int)(ratio_x * p.x + ratio_x / 2);
            if(p_out.x > (dim.width - 1) * ratio_x){
                p_out.x = 0;
                p_out.y = 0;
            }else{
                p_out.y = (int)(ratio_y * p.y + ratio_y / 2);
                if(p_out.y > (dim.height - 1) * ratio_y){
                    p_out.x = 0;
                    p_out.y = 0;
                }
            }
        }
        return p_out;
    }

    public final int getLastFrameIdx() {
        if(DEBUG.M) System.out.println("Frames.getLastFrameIdx()");
        if(this.curr_frame_idx - 1 < 0) return 0;
        return this.curr_frame_idx -= 1;
    }

    public final Point getMeasurePoint(final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.setMeasurePoint(" + d + ")");
        return this.getImagePoint(new Point(this.x_measure_pixel, this.y_measure_pixel), d);
    }

    public final int getNextFrameIdx() {
        if(DEBUG.M) System.out.println("Frames.getNextFrameIdx()");
        if(this.curr_frame_idx + 1 == this.getNumFrame()) return this.curr_frame_idx;
        return this.curr_frame_idx += 1;
    }

    public final int getNumFrame() {
        return this.cache.getNumFrames();
    }

    public final int getPixel(final int idx, final int x, final int y) {
        if(DEBUG.M) System.out.println("Frames.getPixel(" + idx + ", " + x + ", " + y + ")");
        if(!this.isInImage(idx, x, y)) return -1;
        final byte[] imgBuf = this.cache.getBufferAt(idx);
        final Point p = this.getImageBufferPoint(x, y);
        if(imgBuf != null) return imgBuf[(p.y * this.img_width) + p.x];
        return -1;
    }

    private final int[] getPixelArray(final int idx, final int x, final int y, int img_w, int img_h) {
        if(DEBUG.M) System.out.println("Frames.getPixelArray(" + idx + ", " + x + ", " + y + ", " + img_w + ", " + img_h + ")");
        Image img;
        try{
            img = this.cache.getImageAt(idx);
        }catch(final Exception exc){
            System.out.println("INTERNAL ERROR in Frame.getPixelArrat: " + exc);
            return null;
        }
        if(img_w == -1 && img_h == -1){
            this.img_width = img_w = img.getWidth(this);
            this.img_height = img_h = img.getHeight(this);
        }
        final int pixel_array[] = new int[img_w * img_h];
        final PixelGrabber grabber = new PixelGrabber(img, x, y, img_w, img_h, pixel_array, 0, img_w);
        try{
            grabber.grabPixels();
        }catch(final InterruptedException ie){
            System.err.println("# Pixel array not completed");
            return null;
        }
        return pixel_array;
    }

    public final int[] getPixelsLine(final int st_x, final int st_y, final int end_x, final int end_y) {
        if(DEBUG.M) System.out.println("Frames.getValueArray(" + st_x + ", " + st_y + ", " + end_x + ", " + end_y + ")");
        Point p;
        final int n_point = (int)(Math.sqrt(Math.pow(st_x - end_x, 2.0) + Math.pow(st_y - end_y, 2.0)) + 0.5);
        int pixels_line[] = {this.pixel_array[(st_y * this.img_width) + st_x], this.pixel_array[(st_y * this.img_width) + st_x]};
        this.grabFrame();
        if(n_point < 2){
            pixels_line = new int[2];
            p = this.getImageBufferPoint(st_x, st_y);
            pixels_line[0] = pixels_line[1] = this.pixel_array[(p.y * this.img_width) + p.x];
            return pixels_line;
        }
        pixels_line = new int[n_point];
        for(int i = 0; i < n_point; i++){
            final int x = (int)(st_x + (double)i * (end_x - st_x) / n_point);
            final int y = (int)(st_y + (double)i * (end_y - st_y) / n_point);
            p = this.getImageBufferPoint(x, y);
            pixels_line[i] = this.pixel_array[(p.y * this.img_width) + p.x];
        }
        return pixels_line;
    }

    public final int[] getPixelsX(final int y) {
        if(DEBUG.M) System.out.println("Frames.getPixelsX(" + y + ")");
        Point p;
        int pixels_x[] = null;
        int st, end;
        this.grabFrame();
        if(this.pixel_array != null && y < this.img_height){
            if(this.zoom_rect != null){
                st = this.zoom_rect.x;
                end = this.zoom_rect.x + this.zoom_rect.width;
            }else{
                st = 0;
                end = this.img_width;
            }
            pixels_x = new int[end - st];
            for(int i = st, j = 0; i < end; i++, j++){
                p = this.getImageBufferPoint(i, y);
                // pixels_x[j] = pixel_array[(y * img_width) + i];
                pixels_x[j] = this.pixel_array[(p.y * this.img_width) + p.x];
            }
        }
        return pixels_x;
    }

    public final int[] getPixelsY(final int x) {
        if(DEBUG.M) System.out.println("Frames.getPixelsY(" + x + ")");
        Point p;
        int pixels_y[] = null;
        int st, end;
        this.grabFrame();
        if(this.pixel_array != null && x < this.img_width){
            if(this.zoom_rect != null){
                st = this.zoom_rect.y;
                end = this.zoom_rect.y + this.zoom_rect.height;
            }else{
                st = 0;
                end = this.img_height;
            }
            pixels_y = new int[end - st];
            for(int i = st, j = 0; i < end; i++, j++){
                p = this.getImageBufferPoint(x, i);
                // pixels_y[j] = pixel_array[(i * img_width) + x];
                pixels_y[j] = this.pixel_array[(p.y * this.img_width) + p.x];
            }
        }
        return pixels_y;
    }

    public final float getPointValue(final int idx, final int x, final int y) {
        if(DEBUG.M) System.out.println("Frames.getPointValue(" + idx + ", " + x + ", " + y + ")");
        if(!this.isInImage(idx, x, y)) return -1;
        final Point p = this.getImageBufferPoint(x, y);
        // return this.values_array[(p.y * this.img_width) + p.x];
        return this.cache.getValueAt(idx, (p.y * this.img_width) + p.x);
    }

    public final int getStartPixelX() {
        if(DEBUG.M) System.out.println("Frames.getStartPixelX()");
        if(this.zoom_rect != null) return this.zoom_rect.x;
        return 0;
    }

    public final int getStartPixelY() {
        if(DEBUG.M) System.out.println("Frames.getStartPixelY()");
        if(this.zoom_rect != null) return this.zoom_rect.y;
        return 0;
    }

    public final float GetTime(final int frame_idx) {
        if(DEBUG.M) System.out.println("Frames.GetTime(" + frame_idx + ")");
        if(frame_idx > this.cache.getNumFrames() - 1 || frame_idx < 0) return (float)0.0;
        return this.frame_time.elementAt(frame_idx).floatValue();
    }

    public final float[] getValuesLine(final int st_x, final int st_y, final int end_x, final int end_y) {
        if(DEBUG.M) System.out.println("Frames.getValuesLine(" + st_x + ", " + st_y + ", " + end_x + ", " + end_y + ")");
        Point p;
        final int n_point = (int)(Math.sqrt(Math.pow(st_x - end_x, 2.0) + Math.pow(st_y - end_y, 2.0)) + 0.5);
        // float values_line[] = {values_array[(st_y * img_width) + st_x], values_array[(st_y * img_width) + st_x]};
        float values_line[];
        this.grabFrame();
        if(n_point < 2){
            values_line = new float[2];
            p = this.getImageBufferPoint(st_x, st_y);
            values_line[0] = values_line[1] = this.values_array[(p.y * this.img_width) + p.x];
            return values_line;
        }
        values_line = new float[n_point];
        for(int i = 0; i < n_point; i++){
            final int x = (int)(st_x + (double)i * (end_x - st_x) / n_point);
            final int y = (int)(st_y + (double)i * (end_y - st_y) / n_point);
            p = this.getImageBufferPoint(x, y);
            values_line[i] = this.values_array[(p.y * this.img_width) + p.x];
        }
        return values_line;
    }

    public final float[] getValuesX(final int y) {
        if(DEBUG.M) System.out.println("Frames.getValuesX(" + y + ")");
        Point p;
        float values_x[] = null;
        int st, end;
        this.grabFrame();
        if(this.values_array != null && y < this.img_height){
            if(this.zoom_rect != null){
                st = this.zoom_rect.x;
                end = this.zoom_rect.x + this.zoom_rect.width;
            }else{
                st = 0;
                end = this.img_width;
            }
            values_x = new float[end - st];
            for(int i = st, j = 0; i < end; i++, j++){
                p = this.getImageBufferPoint(i, y);
                // values_x[j] = values_array[(y * img_width) + i];
                values_x[j] = this.values_array[(p.y * this.img_width) + p.x];
            }
        }
        return values_x;
    }

    public final float[] getValuesY(final int x) {
        if(DEBUG.M) System.out.println("Frames.getValuesY(" + x + ")");
        Point p;
        float values_y[] = null;
        int st, end;
        this.grabFrame();
        if(this.values_array != null && x < this.img_width){
            if(this.zoom_rect != null){
                st = this.zoom_rect.y;
                end = this.zoom_rect.y + this.zoom_rect.height;
            }else{
                st = 0;
                end = this.img_height;
            }
            values_y = new float[end - st];
            for(int i = st, j = 0; i < end; i++, j++){
                p = this.getImageBufferPoint(x, i);
                // values_y[j] = values_array[(i * img_width) + x];
                values_y[j] = this.values_array[(p.y * this.img_width) + p.x];
            }
        }
        return values_y;
    }

    public final boolean getVerticalFlip() {
        return this.vertical_flip;
    }

    public final Rectangle GetZoomRect() {
        if(DEBUG.M) System.out.println("Frames.GetZoomRect()");
        return this.zoom_rect;
    }

    protected final void grabFrame() {
        if(DEBUG.M) System.out.println("Frames.grabFrame()");
        if(this.curr_frame_idx != this.curr_grab_frame || this.pixel_array == null){
            this.pixel_array = this.getPixelArray(this.curr_frame_idx, 0, 0, -1, -1);
            if(this.pixel_array == null) return;
            this.values_array = this.cache.getValuesAt(this.curr_frame_idx);
            this.curr_grab_frame = this.curr_frame_idx;
        }
    }

    public final boolean isInImage(final int idx, final int x, final int y) {
        if(DEBUG.M) System.out.println("Frames.isInImage(" + idx + ", " + x + ", " + y + ")");
        final Dimension d = this.GetFrameDim(idx);
        final Rectangle r = new Rectangle(0, 0, d.width, d.height);
        return r.contains(x, y);
    }

    public final void Resize() {
        this.zoom_rect = null;
    }

    public final void setAspectRatio(final boolean aspect_ratio) {
        this.aspect_ratio = aspect_ratio;
    }

    public final void setBitShift(final int bitShift, final boolean bitClip) {
        this.cache.setBitShift(bitShift, bitClip);
    }

    public final void SetColorIdx(final int color_idx) {
        this.color_idx = color_idx;
    }

    public final void setColorProfile(final ColorProfile setColorProfile) {
        if(DEBUG.M) System.out.println("Frames.setColorProfile(" + setColorProfile + ")");
        if(setColorProfile == null) return;
        this.cache.setColorProfile(setColorProfile);
    }

    public final void SetFrameData(final FrameData fd) throws Exception {
        if(DEBUG.M) System.out.println("Frames.SetFrameData(" + fd + ")");
        this.cache.setFrameData(fd);
        this.curr_frame_idx = 0;
        final float t[] = fd.GetFrameTimes();
        for(final double element : t)
            this.frame_time.addElement(new Float(element));
    }

    public final void setFramePoint(final Point sel_point, final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.setFramePoint(" + sel_point + ", " + d + ")");
        this.sel_point = this.getFramePoint(new Point(sel_point.x, sel_point.y), d);
    }

    public final void setHorizontalFlip(final boolean horizontal_flip) {
        this.horizontal_flip = horizontal_flip;
    }

    public final void setMeasurePoint(final int x_pixel, final int y_pixel, final Dimension d) {
        if(DEBUG.M) System.out.println("Frames.setMeasurePoint(" + x_pixel + ", " + y_pixel + ", " + d + ")");
        final Point mp = this.getFramePoint(new Point(x_pixel, y_pixel), d);
        this.x_measure_pixel = mp.x;
        this.y_measure_pixel = mp.y;
    }

    public final void setVerticalFlip(final boolean vertical_flip) {
        this.vertical_flip = vertical_flip;
    }

    public final void SetViewRect(int start_x, int start_y, int end_x, int end_y) {
        if(DEBUG.M) System.out.println("Frames.SetViewRect(" + start_x + ", " + start_y + ", " + end_y + ", " + end_y + ")");
        this.view_rect = null;
        if(start_x == -1 && start_y == -1 && end_x == -1 && end_y == -1) return;
        if(this.getNumFrame() == 0) return;
        final Dimension dim = this.GetFrameDim(0);
        if(dim == null){
            this.view_rect = new Rectangle(0, 0, 1, 1);
            this.zoom_rect = this.view_rect;
            return;
        }
        if(start_x < 0) start_x = 0;
        if(start_y < 0) start_y = 0;
        if(end_x == -1 || end_x > dim.width) end_x = dim.width;
        if(end_y == -1 || end_y > dim.height) end_y = dim.height;
        if(start_x < end_x && start_y < end_y){
            this.view_rect = new Rectangle(start_x, start_y, end_x - start_x, end_y - start_y);
            this.zoom_rect = this.view_rect;
        }
    }

    public final void SetZoomRegion(final int idx, final Dimension d, final Rectangle r) {
        if(DEBUG.M) System.out.println("Frames.SetZoomRegion(" + idx + ", " + d + ", " + r + ")");
        final int numFrames = this.cache.getNumFrames();
        if(idx > numFrames - 1) // || frame.elementAt(idx) == null )
        return;
        Dimension dim;
        final Dimension fr_sz = this.getFrameSize(idx, d);
        if(this.zoom_rect == null){
            this.zoom_rect = new Rectangle(0, 0, 0, 0);
            dim = this.GetFrameDim(idx);
        }else{
            dim = new Dimension(this.zoom_rect.width, this.zoom_rect.height);
        }
        final double ratio_x = (double)dim.width / fr_sz.width;
        final double ratio_y = (double)dim.height / fr_sz.height;
        this.zoom_rect.width = (int)(ratio_x * r.width + 0.5);
        this.zoom_rect.height = (int)(ratio_y * r.height + 0.5);
        if(this.horizontal_flip) this.zoom_rect.x += dim.width - ratio_x * (r.x + r.width) + 0.5;
        else this.zoom_rect.x += ratio_x * r.x + 0.5;
        if(this.vertical_flip) this.zoom_rect.y += dim.height - ratio_y * (r.y + r.height) + 0.5;
        else this.zoom_rect.y += ratio_y * r.y + 0.5;
        if(this.zoom_rect.width == 0) this.zoom_rect.width = 1;
        if(this.zoom_rect.height == 0) this.zoom_rect.height = 1;
        this.curr_frame_idx = idx;
    }
    /* unused
    private final int[] getPixelsSignal(int x, int y) {
        if(DEBUG.M) System.out.println("Frames.getPixelsSignal(" + x + ", " + y + ")");
        int pixels_signal[] = null;
        if(this.frames_pixel_array == null || !this.frames_pixel_roi.contains(x, y)){
            this.frames_pixel_roi = new Rectangle();
            if(this.zoom_rect == null){
                this.zoom_rect = new Rectangle(0, 0, this.img_width, this.img_height);
            }
            this.frames_pixel_roi.x = (x - Frames.ROI >= this.zoom_rect.x ? x - Frames.ROI : this.zoom_rect.x);
            this.frames_pixel_roi.y = (y - Frames.ROI >= this.zoom_rect.y ? y - Frames.ROI : this.zoom_rect.y);
            this.frames_pixel_roi.width = (this.frames_pixel_roi.x + 2 * Frames.ROI <= this.zoom_rect.x + this.zoom_rect.width ? 2 * Frames.ROI : this.zoom_rect.width - (this.frames_pixel_roi.x - this.zoom_rect.x));
            this.frames_pixel_roi.height = (this.frames_pixel_roi.y + 2 * Frames.ROI <= this.zoom_rect.y + this.zoom_rect.height ? 2 * Frames.ROI : this.zoom_rect.height - (this.frames_pixel_roi.y - this.zoom_rect.y));
            this.frames_pixel_array = new int[this.frames_pixel_roi.width * this.frames_pixel_roi.height * this.getNumFrame()];
            int f_array[];
            for(int i = 0; i < this.getNumFrame(); i++){
                f_array = this.getPixelArray(i, this.frames_pixel_roi.x, this.frames_pixel_roi.y, this.frames_pixel_roi.width, this.frames_pixel_roi.height);
                System.arraycopy(f_array, 0, this.frames_pixel_array, f_array.length * i, f_array.length);
            }
        }
        if(this.frames_pixel_array != null){
            x -= this.frames_pixel_roi.x;
            y -= this.frames_pixel_roi.y;
            final int size = this.frames_pixel_roi.width * this.frames_pixel_roi.height;
            pixels_signal = new int[this.getNumFrame()];
            for(int i = 0; i < this.getNumFrame(); i++){
                pixels_signal[i] = this.frames_pixel_array[size * i + (y * this.frames_pixel_roi.width) + x];
            }
        }
        return pixels_signal;
    }
    protected final int getValueArray(final float[] buf, final int offset, final int idx, final Rectangle rect) {
        if(DEBUG.M) System.out.println("Frames.getValueArray(" + idx + ", " + rect + ")");
        final float[] values = (this.curr_grab_frame == idx) ? this.values_array : this.cache.getValuesAt(idx);
        if(rect.width == -1 && rect.height == -1){
            this.img_width = rect.width = this.cache.frameDim.width;
            this.img_height = rect.height = this.cache.frameDim.height;
            if(this.cache.frameDim.width * this.cache.frameDim.height != values.length) System.err.println("getValueArray: width*heigth != pixel");
            System.arraycopy(values, 0, buf, offset, values.length);
            return values.length;
        }
        for(int j = 0; j < rect.height; j++)
            System.arraycopy(values, (rect.y + j) * this.img_width + rect.x, buf, j * rect.height + offset, rect.width);
        return rect.width * rect.height;
    }
    public final float[] getValuesSignal(int x, int y) {
        if(DEBUG.M) System.out.println("Frames.getValuesSignal(" + x + ", " + y + ")");
        float values_signal[] = null;
        if(this.frames_value_array == null || !this.frames_pixel_roi.contains(x, y)){
            this.frames_pixel_roi = new Rectangle();
            if(this.zoom_rect == null) this.zoom_rect = new Rectangle(0, 0, this.img_width, this.img_height);
            this.frames_pixel_roi.x = (x - Frames.ROI >= this.zoom_rect.x ? x - Frames.ROI : this.zoom_rect.x);
            this.frames_pixel_roi.y = (y - Frames.ROI >= this.zoom_rect.y ? y - Frames.ROI : this.zoom_rect.y);
            this.frames_pixel_roi.width = (this.frames_pixel_roi.x + 2 * Frames.ROI <= this.zoom_rect.x + this.zoom_rect.width ? 2 * Frames.ROI : this.zoom_rect.width - (this.frames_pixel_roi.x - this.zoom_rect.x));
            this.frames_pixel_roi.height = (this.frames_pixel_roi.y + 2 * Frames.ROI <= this.zoom_rect.y + this.zoom_rect.height ? 2 * Frames.ROI : this.zoom_rect.height - (this.frames_pixel_roi.y - this.zoom_rect.y));
            this.frames_value_array = new float[this.frames_pixel_roi.width * this.frames_pixel_roi.height * this.getNumFrame()];
            for(int idx = 0, offset = 0; idx < this.getNumFrame(); idx++, offset += this.frames_pixel_roi.width * this.frames_pixel_roi.height)
                this.getValueArray(this.frames_value_array, offset, idx, this.frames_pixel_roi);
        }
        if(this.frames_value_array != null){
            x -= this.frames_pixel_roi.x;
            y -= this.frames_pixel_roi.y;
            final int size = this.frames_pixel_roi.width * this.frames_pixel_roi.height;
            values_signal = new float[this.getNumFrame()];
            for(int i = 0; i < this.getNumFrame(); i++){
                values_signal[i] = this.frames_value_array[size * i + (y * this.frames_pixel_roi.width) + x];
            }
        }
        return values_signal;
    }
    */
}

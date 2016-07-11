package jscope;

/* $Id$ */
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import debug.DEBUG;
import jscope.ColorMap.ColorProfile;

/**
 * Class MultiWaveform extends the capability of class Waveform to deal with multiple
 * waveforms.
 */
@SuppressWarnings("serial")
final public class jScopeMultiWave extends MultiWaveform implements UpdateEventListener{
    // Inner class ToTransferHandler to receive jTraverser info
    class ToTransferHandler extends TransferHandler{
        @Override
        public boolean canImport(final TransferHandler.TransferSupport support) {
            if(!support.isDrop()) return false;
            if(!support.isDataFlavorSupported(DataFlavor.stringFlavor)) return false;
            if((support.getSourceDropActions() & TransferHandler.COPY_OR_MOVE) == 0) return false;
            // support.setDropAction(TransferHandler.COPY);
            return true;
        }

        @Override
        public boolean importData(final TransferHandler.TransferSupport support) {
            if(!this.canImport(support)) return false;
            try{
                final String[] data = ((String)support.getTransferable().getTransferData(DataFlavor.stringFlavor)).split(":", 2);
                final String experiment = data[0].equals("null") ? null : data[0];
                final String path = data[1];
                if(support.getDropAction() == TransferHandler.MOVE) jScopeMultiWave.this.wi.erase();
                jScopeMultiWave.this.wi.setExperiment(experiment);
                jScopeMultiWave.this.wi.addSignal(path);
                final WaveformEvent we = new WaveformEvent(jScopeMultiWave.this, WaveformEvent.EVENT_UPDATE, "Update on Drop event ");
                jScopeMultiWave.this.dispatchWaveformEvent(we);
            }catch(final Exception exc){
                return false;
            }
            return true;
        }
    }

    public static String getBriefError(final String er, final boolean brief) {
        if(brief) return er.substring(0, (er.indexOf('\n') == -1 ? er.length() : er.indexOf('\n')));
        return er;
    }
    String eventName;

    public jScopeMultiWave(final DataProvider dp, final jScopeDefaultValues def_values, final boolean cache_enabled){
        super();
        this.wi = new jScopeWaveInterface(this, dp, def_values, cache_enabled);
        this.setTransferHandler(new ToTransferHandler());
    }

    public void addEvent() throws IOException {
        ((jScopeWaveInterface)this.wi).addEvent(this);
    }

    public void addEvent(final String event) throws IOException {
        ((jScopeWaveInterface)this.wi).addEvent(this, event);
    }

    @Override
    protected void drawImage(final Graphics g, final Object img, final Dimension dim, final int type) {
        if(type != FrameData.JAI_IMAGE) super.drawImage(g, img, dim, type);
        else{
            ((Graphics2D)g).clearRect(0, 0, dim.width, dim.height);
            ((Graphics2D)g).drawRenderedImage((RenderedImage)img, new AffineTransform(1f, 0f, 0f, 1f, 0F, 0F));
        }
    }

    @Override
    public ColorProfile getColorProfile() {
        return this.wi.getColorProfile();
    }

    @Override
    public int getMarker(final int idx) {
        if(idx < this.wi.num_waves) return this.wi.markers[idx];
        return 0;
    }

    @Override
    protected Color getSignalColor(final int i) {
        if(i > this.wi.num_waves) return Color.black;
        return Waveform.colors[this.wi.colors_idx[i] % Waveform.colors.length];
    }

    @Override
    public int getSignalCount() {
        return this.wi.num_waves;
    }

    @Override
    protected String getSignalInfo(final int i) {
        String s;
        final String name = (this.wi.in_label != null && this.wi.in_label[i] != null && this.wi.in_label[i].length() > 0) ? this.wi.in_label[i] : this.wi.in_y[i];
        final String er = (this.wi.w_error != null && this.wi.w_error[i] != null) ? " ERROR " : "";
        // If the legend is defined in the signal, override it
        if(this.signals.size() > i && (this.signals.elementAt(i)).getLegend() != null) return (this.signals.elementAt(i)).getLegend();
        if(this.wi.shots != null) s = name + " " + this.wi.shots[i] + er;
        else s = name + er;
        if(this.signals.size() > i){
            final Signal sign = this.signals.elementAt(i);
            s += sign.getName();
            if(sign != null && sign.getType() == Signal.TYPE_2D){
                switch(sign.getMode2D()){
                    case Signal.MODE_XZ:
                        s = s + " [X-Z Y = " + Waveform.convertToString(sign.getYinXZplot(), false) + " ]";
                        break;
                    case Signal.MODE_YZ:
                        s = s + " [Y-Z X = " + sign.getStringOfXinYZplot() +
                        // Waveform.ConvertToString(sign.getTime(), false) +
                                " ]";
                        break;
                    /*
                    case Signal.MODE_YX:
                    s = s + " [Y-X T = " +  sign.getStringTime() +
                    //Waveform.ConvertToString(sign.getTime(), false) +
                    " ]";
                    break;
                     */
                }
            }
        }
        return this.wi.dp.getLegendString(s);
    }

    @Override
    public String[] getSignalsName() {
        return this.wi.getSignalsName();
    }

    @Override
    public boolean[] getSignalsState() {
        return this.wi.getSignalsState();
    }

    @Override
    protected boolean isSignalShow(final int i) {
        return this.wi.getSignalState(i);
    }

    public void jScopeErase() {
        this.erase();
        this.wi.erase();
    }

    public synchronized void jScopeWaveUpdate() {
        if(this.wi.isAddSignal()){
            // reset to previous configuration if signal/s are not added
            if(((jScopeWaveInterface)this.wi).prev_wi != null && ((jScopeWaveInterface)this.wi).prev_wi.getNumEvaluatedSignal() == ((jScopeWaveInterface)this.wi).getNumEvaluatedSignal()){
                ((jScopeWaveInterface)this.wi).prev_wi.error = (this.wi).error;
                ((jScopeWaveInterface)this.wi).prev_wi.w_error = ((jScopeWaveInterface)this.wi).w_error;
                ((jScopeWaveInterface)this.wi).prev_wi.setAddSignal(this.wi.isAddSignal());
                this.wi = ((jScopeWaveInterface)this.wi).prev_wi;
                this.wi.setIsSignalAdded(false);
            }else this.wi.setIsSignalAdded(true);
            ((jScopeWaveInterface)this.wi).prev_wi = null;
        }
        this.update(this.wi);
        final WaveformEvent e = new WaveformEvent(this, WaveformEvent.END_UPDATE);
        this.dispatchWaveformEvent(e);
    }

    @Override
    public void processUpdateEvent(final UpdateEvent e) {
        this.eventName = e.name;
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                if(DEBUG.E) System.out.println("Event at waveform " + e.name);
                final WaveformEvent we = new WaveformEvent(jScopeMultiWave.this, WaveformEvent.EVENT_UPDATE, "Update on event " + jScopeMultiWave.this.eventName);
                jScopeMultiWave.this.dispatchWaveformEvent(we);
            }
        });
    }

    public void refresh() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try{
            this.addEvent();
        }catch(final IOException e){}
        final Thread p = new Thread(){
            @Override
            public void run() {
                final WaveInterface mwi = jScopeMultiWave.this.wi;
                try{
                    mwi.refresh();
                }catch(final Exception e){}
                SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        jScopeMultiWave.this.jScopeWaveUpdate();
                    }
                });
            }
        };
        p.start();
    }

    public void refreshOnEvent() {
        /*
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try
        {
            addEvent();
        }
        catch (IOException e)
        {}
         */
        /*
         Thread p = new Thread()
         {
             public void run()
             {
         */
        final jScopeWaveInterface mwi = (jScopeWaveInterface)this.wi;
        final boolean cache_state = mwi.cache_enabled;
        mwi.cache_enabled = false;
        try{
            mwi.refresh();
        }catch(final Exception e){
            System.err.println(e);
        }
        mwi.cache_enabled = cache_state;
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                jScopeMultiWave.this.jScopeWaveUpdate();
            }
        });
    }

    public void removeEvent() throws IOException {
        ((jScopeWaveInterface)this.wi).removeEvent(this);
    }

    public void removeEvent(final String event) throws IOException {
        ((jScopeWaveInterface)this.wi).addEvent(this, event);
    }

    @Override
    public void removeNotify() {
        try{
            this.removeEvent();
        }catch(final IOException e){}
        this.wi = null;
        this.signals = null;
        this.orig_signals = null;
        final Graphics g = this.getGraphics();
        g.dispose();
        super.removeNotify();
    }

    @Override
    public void setColorProfile(final ColorProfile colorProfile) {
        super.setColorProfile(colorProfile);
        this.wi.setColorProfile(colorProfile);
    }

    @Override
    public void setSignalState(final String label, final boolean state) {
        this.wi.setSignalState(label, state);
        super.setSignalState(label, state);
    }

    public void update(final WaveInterface wi) {
        this.wi = wi;
        this.resetMode();
        this.orig_signals = null;
        super.x_label = this.wi.xlabel;
        super.y_label = this.wi.ylabel;
        super.z_label = this.wi.zlabel;
        super.x_log = this.wi.x_log;
        super.y_log = this.wi.y_log;
        // String error = null;
        // if(!wi.isAddSignal())
        this.wave_error = this.wi.getErrorTitle(true);
        super.title = (this.wi.title != null) ? this.wi.title : "";
        this.setColorProfile(this.wi.getColorProfile());
        super.show_legend = this.wi.show_legend;
        super.legend_x = this.wi.legend_x;
        super.legend_y = this.wi.legend_y;
        super.is_image = this.wi.is_image;
        this.setFrames(this.wi.getFrames());
        if(this.wi.signals != null){
            boolean all_null = true;
            for(int i = 0; i < this.wi.signals.length; i++)
                if(this.wi.signals[i] != null){
                    all_null = false;
                    if(this.wi.in_label[i] != null && this.wi.in_label[i].length() != 0) this.wi.signals[i].setName(this.wi.in_label[i]);
                    else this.wi.signals[i].setName(this.wi.in_y[i]);
                    this.wi.signals[i].setMarker(this.wi.markers[i]);
                    this.wi.signals[i].setMarkerStep(this.wi.markers_step[i]);
                    this.wi.signals[i].setInterpolate(this.wi.interpolates[i]);
                    this.wi.signals[i].setColorIdx(this.wi.colors_idx[i]);
                    this.wi.signals[i].setMode1D(this.wi.mode1D[i]);
                    this.wi.signals[i].setMode2D(this.wi.mode2D[i]);
                }
            if(!all_null){
                this.update(this.wi.signals);
                return;
            }
        }
        if(this.wi.is_image && this.wi.getFrames() != null){
            super.frames.setAspectRatio(this.wi.keep_ratio);
            super.frames.setHorizontalFlip(this.wi.horizontal_flip);
            super.frames.setVerticalFlip(this.wi.vertical_flip);
            super.curr_point_sig_idx = 0;
            if(this.signals.size() != 0) this.signals.removeAllElements();
            if(this.wi.getModified()) this.frame = 0;
            this.not_drawn = true;
            super.update();
            return;
        }
        this.erase();
    }
}
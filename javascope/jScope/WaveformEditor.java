package jScope;

import java.awt.Event;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;
import javax.swing.JFrame;

final public class WaveformEditor extends Waveform{
    static float[]            copyX, copyY;
    public static final float MIN_STEP         = 1E-6F;
    static final long         serialVersionUID = 23457567346312L;

    public static void main(final String args[]) {
        final float x[] = new float[10];
        final float y[] = new float[10];
        for(int i = 0; i < 10; i++){
            x[i] = (float)(i / 10.);
            y[i] = 0;
        }
        final WaveformEditor we = new WaveformEditor();
        // WaveformEditor we = new WaveformEditor(x,y, -10F, 20F);
        we.setWaveform(x, y, -10F, 20F);
        final JFrame frame = new JFrame("Test WaveformEditor");
        frame.setSize(400, 300);
        frame.getContentPane().add(we);
        frame.setVisible(true);
    }
    int                            closestIdx = -1;
    float[]                        currentX, currentY;
    protected boolean              editable   = false;
    Vector<WaveformEditorListener> listeners  = new Vector<WaveformEditorListener>();
    float                          minY, maxY;

    public WaveformEditor(){
        super();
        this.setupCopyPaste();
    }

    public WaveformEditor(final float[] x, final float[] y, final float minY, final float maxY){
        super(new Signal(x, y, x.length, x[0], x[x.length - 1], minY, maxY));
        this.SetMarker(1);
        this.currentX = x;
        this.currentY = y;
        this.minY = minY;
        this.maxY = maxY;
        this.setupCopyPaste();
    }

    public synchronized void addWaveformEditorListener(final WaveformEditorListener listener) {
        this.listeners.add(listener);
    }

    int convertXPix(final float x) {
        return this.wm.XPixel(x, this.getWaveSize());
    }

    int convertYPix(final float y) {
        return this.wm.YPixel(y, this.getWaveSize());
    }

    public synchronized void notifyUpdate(final float[] waveX, final float[] waveY, final int newIdx) {
        for(int i = 0; i < this.listeners.size(); i++)
            this.listeners.elementAt(i).waveformUpdated(waveX, waveY, newIdx);
    }

    @Override
    public void print(final Graphics g) {
        System.out.println("WAVE PRINT");
        this.paint(g, this.getSize(), 1);
    }

    public synchronized void removeWaveformEditorListener(final WaveformEditorListener listener) {
        this.listeners.remove(listener);
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    @Override
    protected void setMouse() {
        this.addMouseListener(new MouseAdapter(){
            @Override
            public final void mousePressed(final MouseEvent e) {
                int newIdx = -1;
                final int currX = e.getX();
                final int currY = e.getY();
                int minDist = Integer.MAX_VALUE;
                int prevIdx = -1;
                for(int i = WaveformEditor.this.closestIdx = 0; i < WaveformEditor.this.currentX.length; i++){
                    if(prevIdx == -1 && i < WaveformEditor.this.currentX.length - 1 && WaveformEditor.this.convertXPix(WaveformEditor.this.currentX[i + 1]) > currX) prevIdx = i;
                    // (float currDist = (float)Math.abs(currX - currentX[i]);
                    final int currentXPix = WaveformEditor.this.convertXPix(WaveformEditor.this.currentX[i]);
                    final int currentYPix = WaveformEditor.this.convertYPix(WaveformEditor.this.currentY[i]);
                    final int currDist = (currX - currentXPix) * (currX - currentXPix) + (currY - currentYPix) * (currY - currentYPix);
                    if(currDist < minDist){
                        minDist = currDist;
                        WaveformEditor.this.closestIdx = i;
                    }
                }
                WaveformEditor.this.notifyUpdate(WaveformEditor.this.currentX, WaveformEditor.this.currentY, WaveformEditor.this.closestIdx);
                if(!WaveformEditor.this.editable) return;
                if((e.getModifiers() & Event.META_MASK) != 0) // If MB3
                {
                    if((e.getModifiers() & Event.SHIFT_MASK) != 0) // Pont deletion
                    {
                        if(WaveformEditor.this.closestIdx != 0 && WaveformEditor.this.closestIdx != WaveformEditor.this.currentX.length - 1){
                            final float[] newCurrentX = new float[WaveformEditor.this.currentX.length - 1];
                            final float[] newCurrentY = new float[WaveformEditor.this.currentY.length - 1];
                            int j;
                            for(int i = j = 0; i < WaveformEditor.this.closestIdx; i++, j++){
                                newCurrentX[i] = WaveformEditor.this.currentX[i];
                                newCurrentY[i] = WaveformEditor.this.currentY[i];
                            }
                            for(int i = WaveformEditor.this.closestIdx + 1; i < WaveformEditor.this.currentX.length; i++, j++){
                                newCurrentX[j] = WaveformEditor.this.currentX[i];
                                newCurrentY[j] = WaveformEditor.this.currentY[i];
                            }
                            WaveformEditor.this.currentX = newCurrentX;
                            WaveformEditor.this.currentY = newCurrentY;
                        }
                    }else{
                        final float newX = WaveformEditor.this.convertX(e.getX());
                        final float[] newCurrentX = new float[WaveformEditor.this.currentX.length + 1];
                        final float[] newCurrentY = new float[WaveformEditor.this.currentY.length + 1];
                        int j;
                        for(int i = j = 0; i <= prevIdx; i++, j++){
                            newCurrentX[i] = WaveformEditor.this.currentX[i];
                            newCurrentY[i] = WaveformEditor.this.currentY[i];
                        }
                        newCurrentX[j] = newX;
                        newCurrentY[j] = WaveformEditor.this.currentY[j - 1] + (newX - WaveformEditor.this.currentX[j - 1]) * (WaveformEditor.this.currentY[j] - WaveformEditor.this.currentY[j - 1]) / (WaveformEditor.this.currentX[j] - WaveformEditor.this.currentX[j - 1]);
                        j++;
                        for(int i = prevIdx + 1; i < WaveformEditor.this.currentX.length; i++, j++){
                            newCurrentX[j] = WaveformEditor.this.currentX[i];
                            newCurrentY[j] = WaveformEditor.this.currentY[i];
                        }
                        WaveformEditor.this.currentX = newCurrentX;
                        WaveformEditor.this.currentY = newCurrentY;
                        newIdx = prevIdx + 1;
                    }
                    final Signal newSig = new Signal(WaveformEditor.this.currentX, WaveformEditor.this.currentY, WaveformEditor.this.currentX.length, WaveformEditor.this.currentX[0], WaveformEditor.this.currentX[WaveformEditor.this.currentX.length - 1], WaveformEditor.this.minY, WaveformEditor.this.maxY);
                    newSig.setMarker(1);
                    WaveformEditor.this.Update(newSig);
                    WaveformEditor.this.notifyUpdate(WaveformEditor.this.currentX, WaveformEditor.this.currentY, newIdx);
                }
            }

            @Override
            public final void mouseReleased(final MouseEvent e) {
                WaveformEditor.this.closestIdx = -1;
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public final void mouseDragged(final MouseEvent e) {
                if(!WaveformEditor.this.editable) return;
                synchronized(WaveformEditor.this){
                    if(WaveformEditor.this.closestIdx == -1) return;
                    float currX;
                    float currY;
                    try{
                        currX = WaveformEditor.this.convertX(e.getX());
                        currY = WaveformEditor.this.convertY(e.getY());
                    }catch(final Exception exc){
                        return;
                    }
                    if(WaveformEditor.this.closestIdx > 0 && WaveformEditor.this.closestIdx < WaveformEditor.this.currentX.length - 1){
                        if(currX < WaveformEditor.this.currentX[WaveformEditor.this.closestIdx - 1] + WaveformEditor.MIN_STEP) currX = WaveformEditor.this.currentX[WaveformEditor.this.closestIdx - 1] + WaveformEditor.MIN_STEP;
                        if(currX > WaveformEditor.this.currentX[WaveformEditor.this.closestIdx + 1] - WaveformEditor.MIN_STEP) currX = WaveformEditor.this.currentX[WaveformEditor.this.closestIdx + 1] - WaveformEditor.MIN_STEP;
                    }else currX = WaveformEditor.this.currentX[WaveformEditor.this.closestIdx];
                    WaveformEditor.this.currentX[WaveformEditor.this.closestIdx] = currX;
                    if(currY < WaveformEditor.this.minY) currY = WaveformEditor.this.minY;
                    if(currY > WaveformEditor.this.maxY) currY = WaveformEditor.this.maxY;
                    WaveformEditor.this.currentY[WaveformEditor.this.closestIdx] = currY;
                    final Signal newSig = new Signal(WaveformEditor.this.currentX, WaveformEditor.this.currentY, WaveformEditor.this.currentX.length, WaveformEditor.this.currentX[0], WaveformEditor.this.currentX[WaveformEditor.this.currentX.length - 1], WaveformEditor.this.minY, WaveformEditor.this.maxY);
                    newSig.setMarker(1);
                    WaveformEditor.this.Update(newSig);
                    WaveformEditor.this.notifyUpdate(WaveformEditor.this.currentX, WaveformEditor.this.currentY, -1);
                }
            }
        });
    }

    protected void setupCopyPaste() {
        // enableEvents(AWTEvent.KEY_EVENT_MASK);
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(final MouseEvent e) {
                WaveformEditor.this.requestFocus();
            }
        });
        this.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(final KeyEvent ke) {
                if((ke.getModifiers() & InputEvent.CTRL_MASK) != 0 && (ke.getKeyCode() == KeyEvent.VK_C)){
                    WaveformEditor.copyX = new float[WaveformEditor.this.currentX.length];
                    WaveformEditor.copyY = new float[WaveformEditor.this.currentY.length];
                    for(int i = 0; i < WaveformEditor.this.currentX.length; i++){
                        if(i >= WaveformEditor.this.currentY.length) break;
                        WaveformEditor.copyX[i] = WaveformEditor.this.currentX[i];
                        WaveformEditor.copyY[i] = WaveformEditor.this.currentY[i];
                    }
                }
                if((ke.getModifiers() & InputEvent.CTRL_MASK) != 0 && (ke.getKeyCode() == KeyEvent.VK_V)){
                    if(WaveformEditor.copyX == null) return;
                    final Signal sig = new Signal(WaveformEditor.copyX, WaveformEditor.copyY, WaveformEditor.copyX.length, WaveformEditor.copyX[0], WaveformEditor.copyX[WaveformEditor.copyX.length - 1], WaveformEditor.this.minY, WaveformEditor.this.maxY);
                    sig.setMarker(1);
                    WaveformEditor.this.currentX = new float[WaveformEditor.copyX.length];
                    WaveformEditor.this.currentY = new float[WaveformEditor.copyY.length];
                    for(int i = 0; i < WaveformEditor.copyX.length; i++){
                        WaveformEditor.this.currentX[i] = WaveformEditor.copyX[i];
                        WaveformEditor.this.currentY[i] = WaveformEditor.copyY[i];
                    }
                    WaveformEditor.this.Update(sig);
                    WaveformEditor.this.notifyUpdate(WaveformEditor.this.currentX, WaveformEditor.this.currentY, WaveformEditor.this.currentX.length - 1);
                }
            }

            @Override
            public void keyReleased(final KeyEvent ke) {}

            @Override
            public void keyTyped(final KeyEvent ke) {}
        });
    }

    public void setWaveform(final float[] x, final float[] y, final float minY, final float maxY) {
        final Signal sig = new Signal(x, y, x.length, x[0], x[x.length - 1], minY, maxY);
        sig.setMarker(1);
        this.currentX = new float[x.length];
        this.currentY = new float[y.length];
        for(int i = 0; i < x.length; i++){
            this.currentX[i] = x[i];
            this.currentY[i] = y[i];
        }
        this.minY = minY;
        this.maxY = maxY;
        this.Update(sig);
    }
}

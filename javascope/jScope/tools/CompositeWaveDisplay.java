package jScope.tools;

/* $Id$ */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jScope.DataAccess;
import jScope.DataAccessURL;
import jScope.MultiWavePopup;
import jScope.MultiWaveform;
import jScope.SetupWaveformParams;
import jScope.Signal;
import jScope.WaveContainerEvent;
import jScope.WaveContainerListener;
import jScope.WaveInterface;
import jScope.WavePopup;
import jScope.Waveform;
import jScope.WaveformContainer;
import jScope.WaveformEvent;

@SuppressWarnings("serial")
final public class CompositeWaveDisplay extends JApplet implements WaveContainerListener{
    public class AppendThread extends Thread{
        long    sleepTime = 100;
        boolean suspend   = false;

        AppendThread(final long sleepTime){
            this.sleepTime = sleepTime;
        }

        @SuppressWarnings("fallthrough")
        private void appendToSignal(final Signal s, final int operation, final float x[], final float y[]) {
            switch(operation){
                case (CompositeWaveDisplay.CMND_CLEAR):
                    s.resetSignalData();
                case (CompositeWaveDisplay.CMND_ADD):
                    s.appendValues(x, y);
                    break;
            }
        }

        @SuppressWarnings("fallthrough")
        private void appendToSignal(final Signal s, final int operation, final float x[], final float y[], final int numPoints[], final float time[]) {
            switch(operation){
                case (CompositeWaveDisplay.CMND_CLEAR):
                    s.resetSignalData();
                case (CompositeWaveDisplay.CMND_ADD):
                    final double d[] = new double[x.length];
                    for(int i = 0; i < x.length; i++)
                        d[i] = x[i];
                    s.appendValues(d, y, numPoints, time);
                    break;
            }
        }

        /*
        private UpdSignalData mergeMessages(UpdSignalData[] usds)
        {
            UpdSignalData out[] = new UpdSignalData[2 + signals1DVector.size() +  signals2DVector.size()];
            for(int i = 0; i < usds.length; i++);
            return null;
        }

        private UpdSignalData mergeMessage(UpdSignalData upd1, UpdSignalData upd2)
        {
            if(upd1.name != upd2.name || upd1.type != upd2.type) return null;
            upd1.numPointsPerSignal = upd1.numPointsPerSignal + upd2.numPointsPerSignal;
            if(upd1.type == Signal.TYPE_1D)
            {
               if(upd1.operation == CompositeWaveDisplay.CMND_CLEAR) return upd1 ;
               if(upd2.operation == CompositeWaveDisplay.CMND_CLEAR) return upd2 ;

                int numSignal1 = upd1.x.length / upd1.numPointsPerSignal;
                int numSignal2 = upd2.x.length / upd2.numPointsPerSignal;

                if(numSignal1 != numSignal2) return null;

                float y[] = float[upd1.length]

            }
        }
         */
        private void processPacket(final UpdSignalData usd) {
            switch(usd.type){
                case Signal.TYPE_1D:
                    if(usd.name != null) this.processsSignal1DPacket(usd);
                    else this.processsSignals1DPacket(usd);
                    break;
                case Signal.TYPE_2D:
                    if(usd.name != null) this.processsSignal2DPacket(usd);
                    else this.processsSignals2DPacket(usd);
                    break;
            }
        }

        private void processsSignal1DPacket(final UpdSignalData usd) {
            Signal s;
            s = CompositeWaveDisplay.this.signals1DHash.get(usd.name);
            if(s != null){
                this.appendToSignal(s, usd.operation, usd.x, usd.y);
            }
        }

        private void processsSignal2DPacket(final UpdSignalData usd) {
            Signal s;
            s = CompositeWaveDisplay.this.signals2DHash.get(usd.name);
            if(s != null){
                final int nPoints[] = new int[usd.times.length];
                for(int i = 0; i < usd.times.length; i++)
                    nPoints[i] = usd.x.length / usd.times.length;
                this.appendToSignal(s, usd.operation, usd.x, usd.y, nPoints, usd.times);
            }
        }

        private void processsSignals1DPacket(final UpdSignalData usd) {
            Signal s;
            float x[];
            float y[];
            y = new float[usd.numPointsPerSignal];
            for(int i = 0; i < CompositeWaveDisplay.this.signals1DVector.size(); i++){
                s = CompositeWaveDisplay.this.signals1DVector.elementAt(i);
                System.arraycopy(usd.y, i * usd.numPointsPerSignal, y, 0, usd.numPointsPerSignal);
                if(usd.x != null && usd.x.length > 1){
                    x = new float[usd.numPointsPerSignal];
                    System.arraycopy(usd.x, i * usd.numPointsPerSignal, x, 0, usd.numPointsPerSignal);
                }else{
                    x = new float[1];
                    x[0] = usd.x[0];
                }
                this.appendToSignal(s, usd.operation, x, y);
            }
        }

        private void processsSignals2DPacket(final UpdSignalData usd) {
            Signal s;
            float x[];
            float y[];
            final int nPoints[] = new int[1];
            final int totPoints = usd.numPointsPerSignal * usd.times.length;
            y = new float[totPoints];
            x = new float[totPoints];
            // System.out.println("N point per sig "+ usd.numPointsPerSignal +" tot point "+ totPoints +" time len "+ usd.times.length);
            for(int i = 0; i < CompositeWaveDisplay.this.signals2DVector.size(); i++){
                s = CompositeWaveDisplay.this.signals2DVector.elementAt(i);
                System.arraycopy(usd.y, i * totPoints, y, 0, totPoints);
                System.arraycopy(usd.x, i * totPoints, x, 0, totPoints);
                nPoints[0] = usd.numPointsPerSignal;
                this.appendToSignal(s, usd.operation, x, y, nPoints, usd.times);
            }
        }

        synchronized public void resumeThread() {
            this.suspend = false;
            this.notify();
        }

        @Override
        public void run() {
            UpdSignalData usd;
            while(true){
                try{
                    final Object obj[] = CompositeWaveDisplay.this.updSignalDataQeue.dequeue();
                    for(final Object element : obj){
                        usd = (UpdSignalData)element;
                        if(usd == null) break;
                        if(usd.operation == CompositeWaveDisplay.CMND_STOP) return;
                        this.processPacket(usd);
                    }
                    for(int i = 0; i < CompositeWaveDisplay.this.signals2DVector.size(); i++){
                        CompositeWaveDisplay.this.signals2DVector.elementAt(i).setMode2D(Signal.MODE_PROFILE);
                    }
                    CompositeWaveDisplay.this.wave_container.appendUpdateWaveforms();
                    synchronized(this){
                        if(this.suspend){
                            CompositeWaveDisplay.this.wave_container.updateWaveforms();
                            this.wait();
                        }
                    }
                    Thread.sleep(500);
                }catch(final Exception exc){
                    System.out.println(exc);
                }
            }
        }

        synchronized public void suspendThread() {
            this.suspend = true;
        }
    }
    public class myQueue{
        Vector<Object> data = new Vector<Object>();

        synchronized public void add(final Object o) {
            this.data.add(o);
            this.notify();
        }

        synchronized public Object[] dequeue() throws InterruptedException {
            Object objects[] = null;
            while(this.data.size() == 0)
                this.wait();
            objects = this.data.toArray();
            Object o;
            for(int i = objects.length - 1; i > 0; i--){
                o = objects[i];
                objects[i] = objects[objects.length - i];
                objects[objects.length - i] = o;
            }
            this.data.clear();
            return objects;
        }
    }
    private class UpdSignalData{
        String name               = null;
        int    numPointsPerSignal = 0;
        int    operation;
        float  times[]            = null;
        int    type;
        float  x[]                = null;
        float  y[]                = null;

        UpdSignalData(final int numPointsPerSignal, final int operation, final int type, final float x[], final float y[]){
            this.numPointsPerSignal = numPointsPerSignal;
            this.operation = operation;
            this.type = type;
            this.x = x;
            this.y = y;
            // System.out.println("numPointsPerSignal " + numPointsPerSignal);
        }

        UpdSignalData(final int numPointsPerSignal, final int operation, final int type, final float x[], final float y[], final float times[]){
            this.numPointsPerSignal = numPointsPerSignal;
            this.operation = operation;
            this.type = type;
            this.x = x;
            this.y = y;
            this.times = times;
            // System.out.println("numPointsPerSignal " + numPointsPerSignal);
        }

        UpdSignalData(final String name, final int operation, final int type, final float x[], final float y[]){
            this.name = name;
            this.operation = operation;
            this.type = type;
            this.x = x;
            this.y = y;
            // System.out.println("numPoints " + x.length);
        }

        UpdSignalData(final String name, final int operation, final int type, final float x[], final float y[], final float times[]){
            this.name = name;
            this.operation = operation;
            this.type = type;
            this.x = x;
            this.y = y;
            this.times = times;
            // System.out.println("numPoints " + x.length);
        }

        @Override
        public String toString() {
            return "Num point per signal " + this.numPointsPerSignal + " Operation " + this.operation + " Type " + this.type;
        }
    }
    static public final int CMND_ADD   = 1;
    static public final int CMND_CLEAR = 0;
    static public final int CMND_STOP  = -1;
    // private int print_scaling = 100;
    // private boolean fixed_legend = false;
    static private JFrame   frame      = null;

    public static void addProtocol(final DataAccess dataAccess) {
        DataAccessURL.addProtocol(dataAccess);
    }

    public static CompositeWaveDisplay createWindow(final String title) {
        return CompositeWaveDisplay.createWindow(title, false);
    }

    public static CompositeWaveDisplay createWindow(final String title, final boolean enableLiveUpdate) {
        if(title != null) CompositeWaveDisplay.frame = new JFrame(title);
        else CompositeWaveDisplay.frame = new JFrame();
        CompositeWaveDisplay.frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(final WindowEvent e) {
                Object o[] = e.getWindow().getComponents();
                final JRootPane root = (JRootPane)o[0];
                o = root.getContentPane().getComponents();
                for(final Object element : o){
                    if(element instanceof CompositeWaveDisplay){
                        ((CompositeWaveDisplay)element).applicationExit(e.getWindow());
                        break;
                    }
                }
            }
        });
        final CompositeWaveDisplay cwd = new CompositeWaveDisplay(false);
        cwd.init();
        if(enableLiveUpdate) cwd.enableLiveUpdate();
        CompositeWaveDisplay.frame.getContentPane().add(cwd);
        return cwd;
    }

    private static String getExpression(final String paramString, final boolean infoFlag) throws IOException {
        final StringTokenizer st = new StringTokenizer(paramString, "/");
        final String str1 = st.nextToken();
        final String ipAddress = st.nextToken();
        final String expAndRegion = st.nextToken();
        final String experiment = expAndRegion.substring(0, expAndRegion.indexOf('~'));
        final String region = expAndRegion.substring(expAndRegion.indexOf('~') + 1);
        final String str6 = st.nextToken();
        final String str7 = st.nextToken();
        String str8 = str1 + "/" + ipAddress + "/" + expAndRegion + "/" + str6 + "/";
        String str9 = "Experiment : " + experiment + " Source : " + region + " Shot = " + str6;
        if(str7.startsWith("vexpr")){
            Object localObject = null;
            String str10 = null;
            localObject = paramString.substring(paramString.lastIndexOf(',') + 1, paramString.lastIndexOf(')'));
            str10 = paramString.substring(paramString.indexOf('(') + 1, paramString.lastIndexOf(','));
            str8 = str8 + "vexpr(decompile(`getnci(" + str10 + ",\"record\"))," + (String)localObject + ")";
            str9 = str9 + " uRun = " + (String)localObject + "\n    Data Path : " + str10;
        }else{
            str8 = str8 + "decompile(`getnci(" + str7 + ",\"record\"))";
            str9 = str9 + "\n    Data path : " + str7;
        }
        if(infoFlag){ return str9; }
        final Object localObject = DataAccessURL.getDataAccess(str8);
        return str9 + "\n Value : " + ((DataAccess)localObject).getExpression(str8);
    }

    private static String getParameterValue(final String context, final String param) {
        String value = null;
        final StringTokenizer st = new StringTokenizer(context);
        while(st.hasMoreTokens()){
            value = st.nextToken();
            if(value.equals(param)){
                if(st.nextToken().equals("=")){
                    value = st.nextToken();
                    break;
                }
                return null;
            }
            value = null;
        }
        return value;
    }

    /**
     * Set Window dialog title.
     *
     * @param title
     *            Title string
     */
    public static void setTitle(final String title) {
        if(CompositeWaveDisplay.frame != null) CompositeWaveDisplay.frame.setTitle(title);
    }

    private static boolean translateToBoolean(final String value) {
        return value.toUpperCase().equals("TRUE");
    }
    AppendThread              appendThread;
    private boolean           automatic_color   = false;
    private int               currentMode       = Waveform.MODE_ZOOM;
    private boolean           isApplet          = true;
    JCheckBox                 liveUpdate;
    PageFormat                pf;
    private JLabel            point_pos;
    ButtonGroup               pointer_mode;
    PrinterJob                prnJob;
    Hashtable<String, Signal> signals1DHash     = new Hashtable<String, Signal>();
    Vector<Signal>            signals1DVector   = new Vector<Signal>();
    Hashtable<String, Signal> signals2DHash     = new Hashtable<String, Signal>();
    Vector<Signal>            signals2DVector   = new Vector<Signal>();
    myQueue                   updSignalDataQeue = new myQueue();
    private WaveformContainer wave_container;

    public CompositeWaveDisplay(){
        super();
    }

    private CompositeWaveDisplay(final boolean isApplet){
        super();
        this.isApplet = isApplet;
    }

    public void addFrames(final String url, final int row, final int column) {
        Component c = null;
        MultiWaveform w = null;
        WaveInterface wi = null;
        DataAccess da = null;
        c = this.wave_container.getGridComponent(row, column);
        try{
            da = DataAccessURL.getDataAccess(url);
            da.setProvider(url);
            if(c != null){
                w = (MultiWaveform)c;
                wi = w.getWaveInterface();
                if(wi == null){
                    if(w.IsImage() || (w.GetSignals() != null && w.GetSignals().size() != 0)){
                        JOptionPane.showMessageDialog(this, "The selected waveform panel contains signals or frame.\n" + "\nDefine a new waveform panel to show frame image from " + da.getDataProvider().getClass().getName() + " data provider.", "alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    w.setWaveInterface((wi = new WaveInterface(w, da.getDataProvider())));
                }else{
                    if(!wi.getDataProvider().getClass().getName().equals(da.getDataProvider().getClass().getName())){
                        JOptionPane.showMessageDialog(this, "The selected waveform panel is already connected to " + wi.getDataProvider().getClass().getName() + " data provider.\nDefine a new waveform panel to show frame image from " + da.getDataProvider().getClass().getName() + " data provider.", "alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }else{
                w = (MultiWaveform)this.wave_container.CreateWaveComponent();
                w.setWaveInterface((wi = new WaveInterface(w, da.getDataProvider())));
                this.wave_container.add(w, row, column);
            }
            if(da != null && wi != null){
                wi.setExperiment(da.getExperiment());
                wi.AddFrames(da.getSignalName());
                wi.setShotArray(da.getShot());
                wi.StartEvaluate();
                wi.EvaluateOthers();
                if(wi.error != null) throw(new IOException(wi.error));
                w.Update(wi.getFrames());
            }
        }catch(final Exception e){
            if(e instanceof AccessControlException){
                JOptionPane.showMessageDialog(this, e.toString() + "\n url " + url + "\nUse policytool.exe in  JDK or JRE installation directory to add socket access permission\n", "alert", JOptionPane.ERROR_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, e.toString(), "alert", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void addSignal(final double[] x, final float[] y, final int row, final int column, final String color, final String label) {
        this.addSignal(x, y, row, column, color, label, true, 0);
    }

    public void addSignal(final double[] x, final float[] y, final int row, final int column, final String color, final String label, final boolean inter, final int marker) {
        this.addSignal(new Signal(x, y), row, column, color, label, inter, marker);
    }

    /**
     * Add new signal, defined as x and y float vectors, to
     * panel in (row, column) position. The panel
     * is created if not already present.
     *
     * @param x
     *            x array values
     * @param y
     *            y array values
     * @param row
     *            Row position
     * @param column
     *            Column position
     * @param color
     *            Signal color
     * @param label
     *            Signal name
     */
    public void addSignal(final float[] x, final float[] y, final int row, final int column, final String color, final String label) {
        this.addSignal(x, y, row, column, color, label, true, 0);
    }

    /**
     * Add new signal, defined by x and y float vectors, to the
     * panel in (row, column) position. The panel
     * is created if not already present.
     *
     * @param x
     *            x array values
     * @param y
     *            y array values
     * @param row
     *            row position, starting from 1
     * @param column
     *            column position, starting from 1
     * @param color
     *            Signal color
     * @param label
     *            Signal name
     * @param inter
     *            Interpolation flag, if true a line is draw between adiacent point
     * @param marker
     *            Marker point
     */
    public void addSignal(final float[] x, final float[] y, final int row, final int column, final String color, final String label, final boolean inter, final int marker) {
        this.addSignal(new Signal(x, y), row, column, color, label, inter, marker);
    }

    public void addSignal(final int row, final int col, final String names, final int color, final int bufferSize, final int type) {
        System.out.println("Name " + names + " bufferSize " + bufferSize);
        final Signal s = new Signal(names);
        s.setType(type);
        if(type == Signal.TYPE_2D) s.setMode2D(Signal.MODE_PROFILE);
        s.setColor(new Color(color));
        s.setUpdSignalSizeInc(bufferSize);
        this.addSignal(s, row, col);
    }

    /**
     * Add new signal, defined as Signal class, to
     * panel in (row, column) position. The panel
     * is created if not already present.
     *
     * @param s
     *            Signal to add
     * @param row
     *            Row MultiWaveform position
     * @param column
     *            Column MultiWaveform position
     * @param col
     */
    public void addSignal(final Signal s, final int row, final int col) {
        Component c = null;
        MultiWaveform w = null;
        c = this.wave_container.getGridComponent(row, col);
        if(c != null){
            w = (MultiWaveform)c;
            if(w.getWaveInterface() != null){
                JOptionPane.showMessageDialog(this, "The selected waveform panel is  connected to " + w.getWaveInterface().getDataProvider().getClass().getName() + " data provider.\nDefine a new waveform panel to show raw signals", "alert", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(this.automatic_color) s.setColorIdx(w.GetShowSignalCount());
            w.addSignal(s);
        }else{
            w = (MultiWaveform)this.wave_container.CreateWaveComponent();
            w.addSignal(s);
            w.setLegendMode(MultiWaveform.LEGEND_BOTTOM);
            this.wave_container.add(w, row, col);
        }
        if(this.isApplet) this.showStatus("Add " + s.getName() + " col " + col + " row " + row);
        switch(s.getType()){
            case Signal.TYPE_1D:
                this.signals1DHash.put(s.getName(), s);
                this.signals1DVector.addElement(s);
                break;
            case Signal.TYPE_2D:
                this.signals2DHash.put(s.getName(), s);
                this.signals2DVector.addElement(s);
                break;
        }
        w.Update();
        if(this.isShowing() && !this.isValid()) this.wave_container.update();
    }

    public void addSignal(final Signal sig, final int row, final int column, final String color, final String label, final boolean inter, final int marker) {
        if(color != null){
            if(color.equals("Automatic")){
                this.automatic_color = true;
            }else{
                this.automatic_color = false;
                sig.setColorIdx(Waveform.ColorNameToIndex(color));
            }
        }
        if(label != null && label.length() != 0) sig.setName(label);
        sig.setInterpolate(inter);
        sig.setMarker(marker);
        this.addSignal(sig, row, column);
    }

    public void addSignal(final String url, final int row, final int column, final String color, final String label, final boolean inter, final int marker) {
        Component c = null;
        MultiWaveform w = null;
        WaveInterface wi = null;
        DataAccess da = null;
        Signal s;
        c = this.wave_container.getGridComponent(row, column);
        try{
            da = DataAccessURL.getDataAccess(url);
            da.setProvider(url);
            if(c != null){
                w = (MultiWaveform)c;
                wi = w.getWaveInterface();
                if(wi == null){
                    if(w.GetSignals() != null && w.GetSignals().size() != 0){
                        JOptionPane.showMessageDialog(this, "In the selected waveform panel there are raw signals.\n" + "\nDefine a new waveform panel to show signals from " + da.getDataProvider().getClass().getName() + " data provider.", "alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    w.setWaveInterface((wi = new WaveInterface()));
                    wi.SetDataProvider(da.getDataProvider());
                }else{
                    if(!wi.getDataProvider().getClass().getName().equals(da.getDataProvider().getClass().getName())){
                        JOptionPane.showMessageDialog(this, "The selected waveform panel is already connected to " + wi.getDataProvider().getClass().getName() + " data provider.\nDefine a new waveform panel to show signals from " + da.getDataProvider().getClass().getName() + " data provider.", "alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }else{
                w = (MultiWaveform)this.wave_container.CreateWaveComponent();
                w.setWaveInterface((wi = new WaveInterface()));
                this.wave_container.add(w, row, column);
                wi.SetDataProvider(da.getDataProvider());
            }
            if(da != null && wi != null){
                wi.setExperiment(da.getExperiment());
                wi.AddSignal(da.getSignalName());
                wi.setShotArray(da.getShot());
                wi.StartEvaluate();
                wi.EvaluateOthers();
                // If added signal has been evaluated without
                // error it is stored in wi.signals vector
                if(wi.signals != null && (s = wi.signals[wi.signals.length - 1]) != null){
                    s.setColorIdx(Waveform.ColorNameToIndex(color));
                    s.setMarker(marker);
                    if(label != null && label.length() != 0) s.setName(label);
                    else s.setName(wi.in_y[0]);
                }
                w.Update(wi.signals);
            }
        }catch(final Exception e){
            if(e instanceof AccessControlException){
                JOptionPane.showMessageDialog(this, e.toString() + "\n url " + url + "\nUse policytool.exe in  JDK or JRE installation directory to add socket access permission\n", "alert", JOptionPane.ERROR_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, e.toString(), "alert", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String addSignal(final String paramString1, final int paramInt1, final int paramInt2, final String paramString2, final String paramString3, final boolean paramBoolean, final int paramInt3, final String paramString4) {
        String str1 = null;
        Signal localSignal = null;
        String str2 = null;
        int i = paramString1.indexOf("/") + 2;
        i = paramString1.indexOf("/", i) + 1;
        str2 = paramString1.substring(i, paramString1.length());
        try{
            localSignal = DataAccessURL.getSignal(paramString1, paramString4);
            if(localSignal != null){
                if(paramString2 != null){
                    localSignal.setColor(new Color(Integer.decode(paramString2).intValue()));
                    this.automatic_color = false;
                }else{
                    this.automatic_color = true;
                }
                localSignal.setMarker(paramInt3);
                if(paramString3 != null) localSignal.setName(paramString3);
                else{
                    localSignal.setName(str2);
                }
                this.addSignal(localSignal, paramInt1, paramInt2);
                str1 = CompositeWaveDisplay.getExpression(paramString1, true);
            }
        }catch(final Exception localException){
            if(localException.toString().indexOf("TdiINVCLADSC") != -1){
                try{
                    return CompositeWaveDisplay.getExpression(paramString1, false);
                }catch(final IOException localIOException){
                    JOptionPane.showMessageDialog(this, localIOException.toString(), "alert", 0);
                }
            }else{
                JOptionPane.showMessageDialog(this, localException.toString(), "alert", 0);
            }
        }
        return str1;
    }

    public void applicationExit(final Window w) {
        try{
            this.enqueueUpdateSignal(null, CompositeWaveDisplay.CMND_STOP, null, null);
            this.appendThread.join(1000);
            w.dispose();
        }catch(final Exception exc){}
    }

    public void enableLiveUpdate() {
        this.liveUpdate.setVisible(true);
    }

    public void enqueueUpdateSignal(final String name, final int operation, final float x[], final float y[]) {
        this.updSignalDataQeue.add(new UpdSignalData(name, operation, Signal.TYPE_1D, x, y));
    }

    public void enqueueUpdateSignal(final String name, final int operation, final float x[], final float y[], final float times[]) {
        this.updSignalDataQeue.add(new UpdSignalData(name, operation, Signal.TYPE_2D, x, y, times));
    }

    /*
        private static boolean checkMessage(UpdSignalData upd) {
            switch(upd.type){
                case Signal.TYPE_1D:
                    break;
                case Signal.TYPE_2D:
                    break;
            }
            return true;
        }
     */
    public void enqueueUpdateSignals(final int numPointsPerSignal, final int operation, final float x[], final float y[]) {
        this.updSignalDataQeue.add(new UpdSignalData(numPointsPerSignal, operation, Signal.TYPE_1D, x, y));
    }

    public void enqueueUpdateSignals(final int numPointsPerSignal, final int operation, final float x[], final float y[], final float times[]) {
        this.updSignalDataQeue.add(new UpdSignalData(numPointsPerSignal, operation, Signal.TYPE_2D, x, y, times));
    }

    private void getSignalsParameter() {
        String url = null, gain, offset, row, col, color, marker, name;
        String sig_param;
        final String sig_attr = "SIGNAL_";
        int i = 1;
        String global_autentication, signal_autentication, autentication, param;
        global_autentication = this.getParameter("AUTENTICATION");
        /*
        param = getParameter("PRINT_SCALING");

        if(param != null){
            try{
                print_scaling = Integer.parseInt(param);
            }catch(NumberFormatException e){}
        }
        param = getParameter("FIXED_LEGEND");
        if(param != null){
            fixed_legend = translateToBoolean(param);
            wave_container.setLegendMode(MultiWaveform.LEGEND_BOTTOM);
        }
         */
        param = this.getParameter("PRINT_WITH_LEGEND");
        if(param != null) this.wave_container.setPrintWithLegend(CompositeWaveDisplay.translateToBoolean(param));
        param = this.getParameter("PRINT_BW");
        if(param != null) this.wave_container.setPrintBW(CompositeWaveDisplay.translateToBoolean(param));
        try{
            while((sig_param = this.getParameter(sig_attr + i)) != null){
                signal_autentication = CompositeWaveDisplay.getParameterValue(sig_param, "autentication");
                if(signal_autentication != null) autentication = signal_autentication;
                else autentication = global_autentication;
                url = CompositeWaveDisplay.getParameterValue(sig_param, "url");
                final Signal s = DataAccessURL.getSignal(url, autentication);
                if(s != null){
                    float k = 1.0F, q = 0.0F;
                    gain = CompositeWaveDisplay.getParameterValue(sig_param, "gain");
                    offset = CompositeWaveDisplay.getParameterValue(sig_param, "offset");
                    if(gain != null || offset != null){
                        if(offset != null) q = Float.valueOf(offset).floatValue();
                        if(gain != null) k = Float.valueOf(gain).floatValue();
                        s.setCalibrate(k, q);
                    }
                    color = CompositeWaveDisplay.getParameterValue(sig_param, "color");
                    if(color != null){
                        s.setColor(new Color(Integer.decode(color).intValue()));
                        this.automatic_color = false;
                    }else this.automatic_color = true;
                    marker = CompositeWaveDisplay.getParameterValue(sig_param, "marker");
                    s.setMarker(marker);
                    name = CompositeWaveDisplay.getParameterValue(sig_param, "name");
                    if(name != null) s.setName(name);
                    else{
                        int idx = url.indexOf("/") + 2;
                        idx = url.indexOf("/", idx) + 1;
                        s.setName(url.substring(idx, url.length()));
                    }
                    row = CompositeWaveDisplay.getParameterValue(sig_param, "row");
                    col = CompositeWaveDisplay.getParameterValue(sig_param, "col");
                    if(row != null && col != null){
                        this.addSignal(s, Integer.parseInt(row), Integer.parseInt(col));
                    }
                }
                i++;
            }
        }catch(final Exception e){
            if(e instanceof AccessControlException){
                JOptionPane.showMessageDialog(this, e.toString() + "\n url " + url + "\nUse policytool.exe in  JDK or JRE installation directory to add socket access permission\n", "alert", JOptionPane.ERROR_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, e.toString() + " url " + url, "alert", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void init() {
        if(this.isApplet) Waveform.zoom_on_mb1 = false;
        this.setBackground(Color.lightGray);
        this.wave_container = new WaveformContainer();
        this.wave_container.addWaveContainerListener(this);
        final WavePopup wave_popup = new MultiWavePopup(new SetupWaveformParams(CompositeWaveDisplay.frame, "Waveform Params"));
        this.wave_container.setPopupMenu(wave_popup);
        this.wave_container.SetMode(Waveform.MODE_ZOOM);
        this.getContentPane().setLayout(new BorderLayout());
        final JRadioButton point = new JRadioButton("Point", false);
        point.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                CompositeWaveDisplay.this.wave_container.SetMode(Waveform.MODE_POINT);
            }
        });
        final JRadioButton zoom = new JRadioButton("Zoom", true);
        zoom.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                CompositeWaveDisplay.this.wave_container.SetMode(Waveform.MODE_ZOOM);
            }
        });
        final JRadioButton pan = new JRadioButton("Pan", false);
        pan.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                CompositeWaveDisplay.this.wave_container.SetMode(Waveform.MODE_PAN);
            }
        });
        this.liveUpdate = new JCheckBox("Live Update", false);
        this.liveUpdate.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(final ChangeEvent e) {
                CompositeWaveDisplay.this.setLiveUpdate(CompositeWaveDisplay.this.liveUpdate.isSelected(), false);
            }
        });
        // liveUpdate.setSelected(false);
        this.liveUpdate.setVisible(false);
        this.pointer_mode = new ButtonGroup();
        this.pointer_mode.add(point);
        this.pointer_mode.add(zoom);
        this.pointer_mode.add(pan);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 4));
        panel1.add(point);
        panel1.add(zoom);
        panel1.add(pan);
        panel1.add(this.liveUpdate);
        final JPanel panel = new JPanel(){
            @Override
            public void print(final Graphics g) {}

            @Override
            public void printAll(final Graphics g) {}
        };
        panel.setLayout(new BorderLayout());
        panel.add("West", panel1);
        if(!this.isApplet){
            this.point_pos = new JLabel("[0.000000000, 0.000000000]");
            this.point_pos.setFont(new Font("Courier", Font.PLAIN, 12));
            panel.add("Center", this.point_pos);
            this.prnJob = PrinterJob.getPrinterJob();
            this.pf = this.prnJob.defaultPage();
            final JButton print = new JButton("Print");
            print.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final Thread t = new Thread(){
                        @Override
                        public void run() {
                            try{
                                CompositeWaveDisplay.this.pf = CompositeWaveDisplay.this.prnJob.pageDialog(CompositeWaveDisplay.this.pf);
                                if(CompositeWaveDisplay.this.prnJob.printDialog()){
                                    CompositeWaveDisplay.this.prnJob.setPrintable(CompositeWaveDisplay.this.wave_container, CompositeWaveDisplay.this.pf);
                                    CompositeWaveDisplay.this.prnJob.print();
                                }
                            }catch(final Exception ex){}
                        }
                    };
                    t.start();
                }
            });
            panel.add("East", print);
        }
        this.getContentPane().add("Center", this.wave_container);
        this.getContentPane().add("South", panel);
        if(this.isApplet) this.getSignalsParameter();
        this.wave_container.update();
        this.appendThread = new AppendThread(100);
        this.appendThread.start();
        this.setEnabledMode(true);
        this.wave_container.SetMode(Waveform.MODE_ZOOM);
    }

    @Override
    public void processWaveContainerEvent(final WaveContainerEvent e) {
        String s = null;
        final int event_id = e.getID();
        switch(event_id){
            case WaveContainerEvent.WAVEFORM_EVENT:
                final WaveformEvent we = (WaveformEvent)e.getEvent();
                final Waveform w = (Waveform)we.getSource();
                s = we.toString();
                if(w instanceof MultiWaveform){
                    final MultiWaveform mw = (MultiWaveform)w;
                    final String n = mw.getSignalName(we.getSignalIdx());
                    if(n != null) s = s + " " + n;
                }
                if(this.isApplet) this.showStatus(s);
                else this.point_pos.setText(s);
                break;
        }
    }

    /**
     * Remove all signals added to the panels.
     */
    public void removeAllSignals() {
        if(this.wave_container != null) this.wave_container.RemoveAllSignals();
        if(this.signals1DHash.size() > 0) this.signals1DHash.clear();
        if(this.signals2DHash.size() > 0) this.signals2DHash.clear();
        if(this.signals1DVector.size() > 0) this.signals1DVector.clear();
        if(this.signals2DVector.size() > 0) this.signals2DVector.clear();
    }

    /**
     * Remove all signals added to the panels.
     */
    public void removeAllSignals(final int row, final int col) {
        if(this.wave_container != null){
            final MultiWaveform wave = (MultiWaveform)this.wave_container.getGridComponent(row, col);
            wave.Erase();
        }
    }

    public void setEnabledMode(final boolean state) {
        final Enumeration<AbstractButton> e = this.pointer_mode.getElements();
        while(e.hasMoreElements())
            ((JRadioButton)e.nextElement()).setEnabled(state);
    }

    public void setLimits(final int row, final int column, final float xmin, final float xmax, final float ymin, final float ymax) {
        Component c = null;
        MultiWaveform w = null;
        WaveInterface wi = null;
        c = this.wave_container.getGridComponent(row, column);
        if(c != null){
            w = (MultiWaveform)c;
            wi = w.getWaveInterface();
            if(wi == null){
                w.setFixedLimits(xmin, xmax, ymin, ymax);
            }else{
                wi.in_xmax = "" + xmax;
                wi.in_xmin = "" + xmin;
                wi.in_ymax = "" + ymax;
                wi.in_ymin = "" + ymin;
                try{
                    wi.StartEvaluate();
                    wi.setLimits();
                }catch(final Exception e){}
            }
            w.Update();
        }
    }

    public void setLiveUpdate(final boolean state) {
        this.setLiveUpdate(state, true);
    }

    public void setLiveUpdate(final boolean state, final boolean setButton) {
        if(setButton){
            this.liveUpdate.setSelected(state);
            return;
        }
        if(this.wave_container.GetMode() != Waveform.MODE_WAIT){
            this.currentMode = this.wave_container.GetMode();
        }
        if(state){
            this.setEnabledMode(false);
            this.wave_container.SetMode(Waveform.MODE_WAIT);
            this.appendThread.resumeThread();
        }else{
            this.appendThread.suspendThread();
            this.setEnabledMode(true);
            this.wave_container.SetMode(this.currentMode);
        }
    }

    @Override
    public void setSize(final int width, final int height) {
        super.setSize(width, height);
        this.validate();
    }

    /**
     * Show window with defined position and size.
     *
     * @param x
     *            position
     * @param y
     *            position
     * @param width
     *            dimension
     * @param height
     *            dimension
     */
    public void showWindow(final int x, final int y, final int width, final int height) {
        if(!this.isShowing()){
            CompositeWaveDisplay.frame.pack();
            CompositeWaveDisplay.frame.setBounds(new Rectangle(x, y, width, height));
            CompositeWaveDisplay.frame.setVisible(true);
        }
        this.wave_container.update();
    }
}

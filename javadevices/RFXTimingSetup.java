import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import devicebeans.DeviceSetup;
import jTraverser.Database;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.CStringArray;
import mds.data.descriptor_r.Conglom;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

class RFXTimingSetup extends DeviceSetup{
    class Decoder{
        Device[]  devices = new Device[6];
        boolean[] isGate  = new boolean[6];
        String    path;

        public Decoder(final String path){
            this.path = path;
        }

        public String getPath() {
            return this.path;
        }

        public boolean setChan(final int chan, final Device device, final boolean gate) {
            if(this.devices[chan] != null) return false;
            this.devices[chan] = device;
            this.isGate[chan] = gate;
            return true;
        }
    }
    class Device{
        String       comment     = "";
        float[]      freqs;
        boolean      initialHigh = false;
        String       path;
        Descriptor[] times;
        int          type;

        public Device(final int type, final String path, final Descriptor[] times, final float[] freqs, final boolean initialHigh){
            this(type, path, times, freqs, initialHigh, "");
        }

        public Device(final int type, final String path, final Descriptor[] times, final float[] freqs, final boolean initialHigh, final String comment){
            this.path = path;
            this.type = type;
            this.times = times;
            this.freqs = freqs;
            this.initialHigh = initialHigh;
            this.comment = comment;
        }

        public void draw(final Graphics g, final int idx, final float start, final float convFact, final int endX, final String decPath, final int decChan) throws MdsException, java.rmi.RemoteException {
            if(decPath == null) g.drawString(this.path + " " + this.comment, 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - RFXTimingSetup.LINE_HEIGHT - 4);
            else g.drawString(this.path + "    Decoder:" + decPath + "   Chan" + (decChan), 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - RFXTimingSetup.LINE_HEIGHT - 4);
            switch(this.type){
                case RFX_CLOCK:
                    g.drawString("f: " + this.freqs[0], 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - 4);
                    for(int i = RFXTimingSetup.WAVE_X; i < endX; i++){
                        if(i % 5 == 0) g.drawLine(i, idx * RFXTimingSetup.SIGNAL_HEIGHT, i, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                    }
                    g.drawLine(RFXTimingSetup.WAVE_X, idx * RFXTimingSetup.SIGNAL_HEIGHT, endX, idx * RFXTimingSetup.SIGNAL_HEIGHT);
                    g.drawLine(RFXTimingSetup.WAVE_X, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT, endX, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                    break;
                case RFX_PULSE:
                    float ftime1 = RFXTimingSetup.this.subtree.evaluate(this.times[0]).toFloat()[0];
                    if(this.times.length == 1){
                        g.drawString("t1: " + ftime1, 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - 4);
                        final int time1 = RFXTimingSetup.WAVE_X + (int)((ftime1 - start) * convFact);
                        final int lev = (this.initialHigh) ? idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT : idx * RFXTimingSetup.SIGNAL_HEIGHT;
                        g.drawLine(RFXTimingSetup.WAVE_X, lev, time1, lev);
                        final int lev1 = (!this.initialHigh) ? idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT : idx * RFXTimingSetup.SIGNAL_HEIGHT;
                        g.drawLine(time1, lev, time1, lev1);
                        g.drawLine(time1, lev1, endX, lev1);
                    }else{
                        final float ftime2 = RFXTimingSetup.this.subtree.evaluate(this.times[1]).toFloat()[0];
                        g.drawString("t1: " + ftime1 + "    t2: " + ftime2, 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - 4);
                        final int time1 = RFXTimingSetup.WAVE_X + (int)((ftime1 - start) * convFact);
                        final int time2 = RFXTimingSetup.WAVE_X + (int)((ftime2 - start) * convFact);
                        final int lev = (this.initialHigh) ? idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT : idx * RFXTimingSetup.SIGNAL_HEIGHT;
                        g.drawLine(RFXTimingSetup.WAVE_X, lev, time1, lev);
                        final int lev1 = (!this.initialHigh) ? idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT : idx * RFXTimingSetup.SIGNAL_HEIGHT;
                        g.drawLine(time1, lev, time1, lev1);
                        g.drawLine(time1, lev1, time2, lev1);
                        g.drawLine(time2, lev1, time2, lev);
                        g.drawLine(time2, lev, endX, lev);
                    }
                    break;
                case RFX_DCLOCK:
                    g.drawLine(RFXTimingSetup.WAVE_X, idx * RFXTimingSetup.SIGNAL_HEIGHT, endX, idx * RFXTimingSetup.SIGNAL_HEIGHT);
                    g.drawLine(RFXTimingSetup.WAVE_X, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT, endX, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                    ftime1 = RFXTimingSetup.this.subtree.evaluate(this.times[0]).toFloat()[0];
                    if(this.times.length == 1){
                        g.drawString(" f1: " + this.freqs[0] + "    f2: " + this.freqs[1] + "    t1: " + ftime1, 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - 4);
                        final int time1 = RFXTimingSetup.WAVE_X + (int)((ftime1 - start) * convFact);
                        int delta = (this.freqs[0] < this.freqs[1]) ? 5 : 3;
                        for(int i = RFXTimingSetup.WAVE_X; i < time1; i++){
                            if(i % delta == 0) g.drawLine(i, idx * RFXTimingSetup.SIGNAL_HEIGHT, i, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        }
                        delta = (this.freqs[0] < this.freqs[1]) ? 3 : 5;
                        for(int i = time1; i < endX; i++){
                            if(i % delta == 0) g.drawLine(i, idx * RFXTimingSetup.SIGNAL_HEIGHT, i, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        }
                    }else{
                        final float ftime2 = RFXTimingSetup.this.subtree.evaluate(this.times[1]).toFloat()[0];
                        g.drawString("f1: " + this.freqs[0] + "   f2: " + this.freqs[1] + "    t1: " + ftime1 + "   t2: " + ftime2, 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - 4);
                        final int time1 = RFXTimingSetup.WAVE_X + (int)((ftime1 - start) * convFact);
                        final int time2 = RFXTimingSetup.WAVE_X + (int)((ftime2 - start) * convFact);
                        int delta = (this.freqs[0] < this.freqs[1]) ? 5 : 3;
                        for(int i = RFXTimingSetup.WAVE_X; i < time1; i++){
                            if(i % delta == 0) g.drawLine(i, idx * RFXTimingSetup.SIGNAL_HEIGHT, i, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        }
                        delta = (this.freqs[0] < this.freqs[1]) ? 3 : 5;
                        for(int i = time1; i < time2; i++){
                            if(i % delta == 0) g.drawLine(i, idx * RFXTimingSetup.SIGNAL_HEIGHT, i, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        }
                        delta = (this.freqs[0] < this.freqs[1]) ? 5 : 3;
                        for(int i = time2; i < endX; i++){
                            if(i % delta == 0) g.drawLine(i, idx * RFXTimingSetup.SIGNAL_HEIGHT, i, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        }
                    }
                    break;
                case RFX_GCLOCK:
                    ftime1 = RFXTimingSetup.this.subtree.evaluate(this.times[0]).toFloat()[0];
                    if(this.times.length == 1){
                        g.drawString("f: " + this.freqs[0] + "    t1: " + ftime1, 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - 4);
                        final int time1 = RFXTimingSetup.WAVE_X + (int)((ftime1 - start) * convFact);
                        g.drawLine(RFXTimingSetup.WAVE_X, idx * RFXTimingSetup.SIGNAL_HEIGHT, time1, idx * RFXTimingSetup.SIGNAL_HEIGHT);
                        g.drawLine(time1, idx * RFXTimingSetup.SIGNAL_HEIGHT, time1, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        g.drawLine(time1, idx * RFXTimingSetup.SIGNAL_HEIGHT, endX, idx * RFXTimingSetup.SIGNAL_HEIGHT);
                        g.drawLine(time1, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT, endX, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        for(int i = time1; i < endX; i++){
                            if(i % 5 == 0) g.drawLine(i, idx * RFXTimingSetup.SIGNAL_HEIGHT, i, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        }
                    }else{
                        final float ftime2 = RFXTimingSetup.this.subtree.evaluate(this.times[1]).toFloat()[0];
                        g.drawString("f: " + this.freqs[0] + "    t1: " + ftime1 + "   t2: " + ftime2, 0, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT - 4);
                        final int time1 = RFXTimingSetup.WAVE_X + (int)((ftime1 - start) * convFact);
                        final int time2 = RFXTimingSetup.WAVE_X + (int)((ftime2 - start) * convFact);
                        g.drawLine(RFXTimingSetup.WAVE_X, idx * RFXTimingSetup.SIGNAL_HEIGHT, time1, idx * RFXTimingSetup.SIGNAL_HEIGHT);
                        g.drawLine(time1, idx * RFXTimingSetup.SIGNAL_HEIGHT, time1, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        g.drawLine(time1, idx * RFXTimingSetup.SIGNAL_HEIGHT, time2, idx * RFXTimingSetup.SIGNAL_HEIGHT);
                        g.drawLine(time1, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT, time2, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        for(int i = time1; i < time2; i++){
                            if(i % 5 == 0) g.drawLine(i, idx * RFXTimingSetup.SIGNAL_HEIGHT, i, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        }
                        g.drawLine(time2, idx * RFXTimingSetup.SIGNAL_HEIGHT, time2, idx * RFXTimingSetup.SIGNAL_HEIGHT - RFXTimingSetup.WAVE_HEIGHT);
                        g.drawLine(time2, idx * RFXTimingSetup.SIGNAL_HEIGHT, endX, idx * RFXTimingSetup.SIGNAL_HEIGHT);
                    }
                    break;
            }
        }
    }
    static final int          K_NODES_PER_CHANNEL  = 3;
    static final int          LINE_HEIGHT          = 12;
    static final int          N_CHAN_EVENT         = 1;
    static final int          N_CHAN_TRIG          = 2;
    static final int          N_CHANNEL_1          = 3;
    static final int          N_CLOCK_CHAN         = 3;
    static final int          N_CLOCK_DECODER      = 2;
    static final int          N_CLOCK_FREQ         = 4;
    static final int          N_DCLOCK_CLOCK_CHAN  = 4;
    static final int          N_DCLOCK_DECODER     = 2;
    static final int          N_DCLOCK_DELAY       = 8;
    static final int          N_DCLOCK_DURATION    = 11;
    static final int          N_DCLOCK_EVENT       = 6;
    static final int          N_DCLOCK_EXT_TRIG    = 7;
    static final int          N_DCLOCK_FREQUENCY1  = 9;
    static final int          N_DCLOCK_FREQUENCY2  = 10;
    static final int          N_DCLOCK_GATE_CHAN   = 3;
    static final int          N_DCLOCK_OUTPUT_MODE = 12;
    static final int          N_DCLOCK_TRIG_MODE   = 5;
    static final int          N_DIO2_REC_EVENTS    = 143;
    static final int          N_DIO2_REC_TIMES     = 144;
    static final int          N_GCLOCK_CLOCK_CHAN  = 4;
    static final int          N_GCLOCK_DECODER     = 2;
    static final int          N_GCLOCK_DELAY       = 8;
    static final int          N_GCLOCK_DURATION    = 10;
    static final int          N_GCLOCK_EVENT       = 6;
    static final int          N_GCLOCK_EXT_TRIG    = 7;
    static final int          N_GCLOCK_FREQUENCY   = 9;
    static final int          N_GCLOCK_GATE_CHAN   = 3;
    static final int          N_GCLOCK_OUTPUT_MODE = 11;
    static final int          N_GCLOCK_TRIG_MODE   = 5;
    static final int          N_PULSE_CHAN         = 3;
    static final int          N_PULSE_DELAY        = 7;
    static final int          N_PULSE_DURATION     = 8;
    static final int          N_PULSE_EVENT        = 5;
    static final int          N_PULSE_EXT_TRIGGER  = 6;
    static final int          N_PULSE_OUTPUT_MODE  = 14;
    static final int          N_PULSE_TRIG_MODE    = 4;
    static final int          N_RECORDER_EVENTS    = 4;
    static final int          N_RECORDER_TIMES     = 5;
    static final int          RFX_CLOCK            = 1;
    static final int          RFX_DCLOCK           = 3;
    static final int          RFX_GCLOCK           = 2;
    static final int          RFX_PULSE            = 14;
    /**
     *
     */
    private static final long serialVersionUID     = 4132868920356163567L;
    static final int          SIGNAL_HEIGHT        = 55;
    static String             topTiming            = "\\TIMING";
    static final int          USAGE_DEVICE         = 3;
    static final int          WAVE_HEIGHT          = 20;
    static final int          WAVE_X               = 10;
    Vector                    dio2Devices          = new Vector();
    Vector                    errorStrings         = new Vector();
    JTable                    eventTable;
    Hashtable                 eventTimes           = new Hashtable();
    String                    evNames[];
    String                    evRecTimes[];
    Descriptor                evTimes[];
    boolean                   firstScan            = true;
    String                    fullRecEvents[];
    float                     fullRecTimes[];
    Vector                    mpbDecoderNids       = new Vector();
    Vector                    mpbDecoders          = new Vector();
    JDialog                   recordedDialog       = null;
    Nid                       recorderNid          = null;
    Vector                    rfxDeviceCongloms    = new Vector();
    Vector                    rfxDeviceNids        = new Vector();
    Vector                    rfxDevices           = new Vector();
    JSplitPane                splitP;

    public RFXTimingSetup(){
        super();
        this.splitP = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.splitP.setPreferredSize(new Dimension(500, 300));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(this.splitP, "Center");
        this.setTitle("Timing supervisor");
        // getContentPane().add(scroll, "North");
        final JPanel jp = new JPanel();
        final JButton cancelB = new JButton("Cancel");
        cancelB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                RFXTimingSetup.this.dispose();
            }
        });
        jp.add(cancelB);
        final JButton recordedB = new JButton("Last recorded Events");
        recordedB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(RFXTimingSetup.this.reportRecEvents()) RFXTimingSetup.this.showRecordedEvents();
            }
        });
        jp.add(recordedB);
        final JButton updateB = new JButton("Rescan Timing");
        updateB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                RFXTimingSetup.this.scan(false);
                RFXTimingSetup.this.reportEvents();
                RFXTimingSetup.this.repaint();
            }
        });
        jp.add(updateB);
        final JButton updateFullB = new JButton("Rescan Fully");
        updateFullB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                RFXTimingSetup.this.scan(true);
                RFXTimingSetup.this.reportEvents();
                RFXTimingSetup.this.repaint();
            }
        });
        jp.add(updateFullB);
        this.getContentPane().add(jp, "South");
        this.setSize(500, 500);
    }

    JComponent buildEventTable() {
        this.eventTable = new JTable(this.createEventTableModel());
        final JScrollPane scroll = new JScrollPane(this.eventTable);
        this.eventTable.setPreferredScrollableViewportSize(new Dimension(450, 100));
        return(scroll);
    }

    @Override
    public void configure(final Database subtree, final int baseNid) {
        this.subtree = subtree;
        this.scan(true);
        final JComponent waves = new JComponent(){
            @Override
            public void paintComponent(final Graphics g) {
                // super.paintComponent(g);
                RFXTimingSetup.this.showSignals(g, this.getSize());
            }
        };
        waves.setPreferredSize(new Dimension(500, (this.rfxDevices.size() + this.dio2Devices.size() + 10) * RFXTimingSetup.SIGNAL_HEIGHT));
        final JScrollPane scroll = new JScrollPane(waves);
        scroll.setPreferredSize(new Dimension(500, 300));
        this.splitP.setTopComponent(scroll);
        // getContentPane().add(buildEventTable(), "Center");
        final JComponent eventT = this.buildEventTable();
        this.splitP.setBottomComponent(eventT);
        this.pack();
        final javax.swing.Timer timer = new javax.swing.Timer(500, new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String errors = RFXTimingSetup.this.getErrors();
                if(errors != null) JOptionPane.showMessageDialog(RFXTimingSetup.this, errors, "Errors in timing configuration", JOptionPane.WARNING_MESSAGE);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private TableModel createEventTableModel() {
        return new AbstractTableModel(){
            /**
             *
             */
            private static final long serialVersionUID = -8070872841291562523L;

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public String getColumnName(final int col) {
                switch(col){
                    case 0:
                        return "Event";
                    case 1:
                        return "Time";
                }
                return "";
            }

            @Override
            public int getRowCount() {
                if(RFXTimingSetup.this.evNames == null) return 0;
                return RFXTimingSetup.this.evNames.length;
            }

            @Override
            public Object getValueAt(final int row, final int col) {
                switch(col){
                    case 0:
                        return RFXTimingSetup.this.evNames[row];
                    case 1:
                        try{
                            return RFXTimingSetup.this.subtree.decompile(RFXTimingSetup.this.subtree.evaluate(RFXTimingSetup.this.evTimes[row]));
                        }catch(final Exception exc){
                            return "";
                        }
                        // return evTimes[row].toString();
                }
                return "";
            }

            @Override
            public boolean isCellEditable(final int row, final int col) {
                return false;
            }
        };
    }

    public String getErrors() {
        if(this.errorStrings.size() == 0) return null;
        String errors = "";
        for(int i = 0; i < this.errorStrings.size(); i++)
            errors += "\n" + (String)this.errorStrings.elementAt(i);
        return errors;
    }

    void reportEvents() {
        final int nEvents = this.eventTimes.size();
        this.evNames = new String[nEvents];
        this.evTimes = new Descriptor[nEvents];
        this.evRecTimes = new String[nEvents];
        final Enumeration names = this.eventTimes.keys();
        int i = 0;
        while(names.hasMoreElements()){
            this.evNames[i] = (String)names.nextElement();
            this.evTimes[i] = (Descriptor)this.eventTimes.get(this.evNames[i]);
            this.evRecTimes[i] = "";
            i++;
        }
    }

    boolean reportRecEvents() {
        if(this.recorderNid == null) return false;
        try{
            final int shot = (int)this.subtree.getShot();
            Database exp;
            boolean currentOpen = false;
            if(shot == -1){
                try{
                    exp = new Database(this.subtree.getName(), 0);
                    exp.open();
                    currentOpen = true;
                }catch(final Exception ecx){
                    exp = new Database(this.subtree.getName(), -1);
                    exp.open();
                }
            }else exp = this.subtree;
            final Descriptor recEventsData = (exp.evaluate(new Nid(this.recorderNid.getValue() + RFXTimingSetup.N_DIO2_REC_EVENTS)));
            System.out.println(recEventsData);
            this.fullRecEvents = ((CStringArray)recEventsData).getValue();
            final Descriptor recTimesData = (exp.evaluate(new Nid(this.recorderNid.getValue() + RFXTimingSetup.N_DIO2_REC_TIMES)));
            System.out.println(recTimesData);
            this.fullRecTimes = recTimesData.toFloat();
            if(currentOpen) exp.close();
        }catch(final Exception exc){
            // System.out.println(exc);
            JOptionPane.showMessageDialog(RFXTimingSetup.this, "No events recorded", "Missing events", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        final Vector recEventsV = new Vector();
        for(int i = 0; i < this.fullRecEvents.length; i++){
            recEventsV.addElement(this.fullRecEvents[i]);
            i++;
        }
        for(int i = 0; i < this.evNames.length; i++){
            final int idx = recEventsV.indexOf(this.evNames[i]);
            if(idx != -1) this.evRecTimes[i] = "" + this.fullRecTimes[idx];
            else this.evRecTimes[i] = "";
        }
        return true;
    }

    void scan(final boolean full) {
        this.rfxDevices = new Vector();
        this.dio2Devices = new Vector();
        this.mpbDecoders = new Vector();
        this.rfxDeviceCongloms = new Vector();
        this.rfxDeviceNids = new Vector();
        this.mpbDecoderNids = new Vector();
        this.errorStrings = new Vector();
        this.eventTimes = new Hashtable();
        Nid[] deviceNids = null;
        Nid prevNid = null;
        try{
            if(full) this.subtree.setDefault(new Nid(0));
            else try{
                prevNid = this.subtree.getDefault();
                final Nid nid = this.subtree.resolve(new Path(RFXTimingSetup.topTiming));
                this.subtree.setDefault(nid);
            }catch(final Exception exc){
                this.subtree.setDefault(new Nid(0));
            }
            deviceNids = this.subtree.getWild(RFXTimingSetup.USAGE_DEVICE);
        }catch(final Exception exc){
            System.err.println("RFXTimingSetup: Error reading device data: " + exc);
        }
        if(deviceNids == null) return;
        for(int idx = 0; idx < deviceNids.length; idx++){
            try{
                if(!this.subtree.isOn(deviceNids[idx])) continue;
                final Conglom conglom = (Conglom)this.subtree.getData(deviceNids[idx]);
                final String model = this.subtree.decompile(conglom.getModel());
                // if (model.equals("\"MPBRecorder\""))
                if(model.equals("\"DIO2\"") && this.recorderNid == null) this.recorderNid = deviceNids[idx];
                if(model.equals("\"RFXClock\"") || model.equals("\"RFXDClock\"") || model.equals("\"RFXGClock\"") || model.equals("\"RFXPulse\"") || model.equals("\"DIO2\"")){
                    this.rfxDeviceCongloms.addElement(conglom);
                    this.rfxDeviceNids.addElement(deviceNids[idx]);
                }else if(model.equals("\"MPBDecoder\"")) this.mpbDecoderNids.addElement(deviceNids[idx]);
                else if(model.equals("\"MPBEncoder\"")){
                    final int baseNid = deviceNids[idx].getValue();
                    for(int chan = 0; chan < 7; chan++){
                        try{
                            if(!this.subtree.isOn(new Nid(baseNid + chan * RFXTimingSetup.K_NODES_PER_CHANNEL + RFXTimingSetup.N_CHANNEL_1))) continue;
                            final String event = ((CString)this.subtree.getData(new Nid(baseNid + chan * RFXTimingSetup.K_NODES_PER_CHANNEL + RFXTimingSetup.N_CHANNEL_1 + RFXTimingSetup.N_CHAN_EVENT))).getValue();
                            final Descriptor time = this.subtree.getData(new Nid(baseNid + chan * RFXTimingSetup.K_NODES_PER_CHANNEL + RFXTimingSetup.N_CHANNEL_1 + RFXTimingSetup.N_CHAN_TRIG));
                            if(!event.trim().equals("")) this.eventTimes.put(event, time);
                        }catch(final Exception exc){}
                    }
                }else if(model.equals("\"DIO2Encoder\"")){
                    final int DIO2_ENC_NODES_PER_CHANNEL = 5;
                    final int DIO2_ENC_CHANNEL_0 = 6;
                    final int DIO2_ENC_CHAN_EVENT_NAME = 1;
                    final int DIO2_ENC_CHAN_EVENT = 2;
                    final int DIO2_ENC_CHAN_EVENT_TIME = 3;
                    final int baseNid = deviceNids[idx].getValue();
                    for(int chan = 0; chan < 17; chan++){
                        if(!this.subtree.isOn(new Nid(baseNid + chan * DIO2_ENC_NODES_PER_CHANNEL + DIO2_ENC_CHANNEL_0))) continue;
                        try{
                            final String event = ((CString)this.subtree.getData(new Nid(baseNid + chan * DIO2_ENC_NODES_PER_CHANNEL + DIO2_ENC_CHANNEL_0 + DIO2_ENC_CHAN_EVENT_NAME))).getValue();
                            final Descriptor time = this.subtree.getData(new Nid(baseNid + chan * DIO2_ENC_NODES_PER_CHANNEL + DIO2_ENC_CHANNEL_0 + DIO2_ENC_CHAN_EVENT_TIME));
                            boolean sourceOn = true;
                            try{
                                if(time instanceof Nid) sourceOn = this.subtree.isOn((Nid)time);
                                if(time instanceof Path){
                                    final Nid sourceNid = this.subtree.resolve((Path)time);
                                    sourceOn = this.subtree.isOn(sourceNid);
                                }
                            }catch(final Exception exc){
                                sourceOn = false;
                            }
                            if(!event.trim().equals("") && sourceOn) this.eventTimes.put(event, time);
                        }catch(final Exception exc){}
                    }
                }
            }catch(final Exception exc){}
        }
        for(int idx = 0; idx < this.mpbDecoderNids.size(); idx++){
            try{
                final Nid nid = (Nid)this.mpbDecoderNids.elementAt(idx);
                final String path = this.subtree.getInfo(nid).getFullPath();
                this.mpbDecoders.add(new Decoder(path));
            }catch(final Exception exc){}
        }
        for(int idx = 0; idx < this.rfxDeviceCongloms.size(); idx++){
            final Conglom conglom = (Conglom)this.rfxDeviceCongloms.elementAt(idx);
            String model;
            try{
                model = this.subtree.decompile(conglom.getModel());
            }catch(final Exception exc){
                continue;
            }
            if(model.equals("\"DIO2\"")){
                final int DIO2_NODES_PER_CHANNEL = 17;
                final int DIO2_CHANNEL_0 = 7;
                final int DIO2_CHAN_FUNCTION = 1;
                final int DIO2_CHAN_TRIG_MODE = 2;
                final int DIO2_CHAN_EVENT = 3;
                final int DIO2_CHAN_CYCLIC = 4;
                final int DIO2_CHAN_DELAY = 5;
                final int DIO2_CHAN_DURATION = 6;
                final int DIO2_CHAN_FREQUENCY_1 = 7;
                final int DIO2_CHAN_FREQUENCY_2 = 8;
                final int DIO2_CHAN_INIT_LEVEL_1 = 9;
                final int DIO2_CHAN_INIT_LEVEL_2 = 10;
                final int DIO2_CHAN_DUTY_CYCLE = 11;
                final int DIO2_CHAN_TRIGGER = 12;
                final int DIO2_CHAN_CLOCK = 13;
                final int DIO2_CHAN_TRIGGER_1 = 14;
                final int DIO2_CHAN_TRIGGER_2 = 15;
                final int DIO2_CHAN_COMMENT = 16;
                final int baseNid = ((Nid)this.rfxDeviceNids.elementAt(idx)).getValue();
                for(int chan = 0; chan < 8; chan++){
                    try{
                        if(!this.subtree.isOn(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0))) continue;
                        final String function = (this.subtree.getData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_FUNCTION))).toString();
                        String comment;
                        try{
                            comment = (this.subtree.getData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_COMMENT))).toString();
                            System.out.println(comment);
                        }catch(final Exception exc){
                            comment = "";
                        }
                        if(function.equals("CLOCK")){
                            final float freq = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_FREQUENCY_1))).toFloat()[0];
                            final Device device = new Device(RFXTimingSetup.RFX_CLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath() + " CHAN " + (chan + 1), new Descriptor[]{}, new float[]{freq}, false, comment);
                            this.dio2Devices.addElement(device);
                        }else if(function.equals("PULSE")){
                            try{
                                final String triggerMode = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIG_MODE))).toString();
                                final Descriptor delay = this.subtree.getData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_DELAY));
                                final Descriptor duration = this.subtree.getData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_DURATION));
                                Descriptor trigTime;
                                if(triggerMode.equals("EVENT")){
                                    final Descriptor eventData = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_EVENT)));
                                    final String event = eventData.toString();
                                    trigTime = (Descriptor)this.eventTimes.get(event);
                                    if(trigTime == null){
                                        this.errorStrings.addElement("Cannot resolve time for event " + event + " in DIO2 Pulse device ");
                                        continue;
                                    }
                                    this.subtree.putData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIGGER), trigTime);
                                }else trigTime = this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIGGER));
                                final String initLevel1Str = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_INIT_LEVEL_1))).toString();
                                final String initLevel2Str = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_INIT_LEVEL_2))).toString();
                                final boolean init1High = initLevel1Str.equals("HIGH");
                                final boolean init2High = initLevel2Str.equals("HIGH");
                                final Descriptor trig1 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay));
                                final Descriptor trig2 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay) + "+" + this.subtree.decompile(duration));
                                Device device;
                                if(init1High != init2High) device = new Device(RFXTimingSetup.RFX_PULSE, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath() + " CHAN " + (chan + 1), new Descriptor[]{trig1, trig2}, new float[]{}, init1High, comment);
                                else device = new Device(RFXTimingSetup.RFX_PULSE, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath() + " CHAN " + (chan + 1), new Descriptor[]{}, new float[]{}, init1High, comment);
                                this.dio2Devices.addElement(device);
                            }catch(final Exception exc){
                                System.out.println(exc);
                            }
                        }else if(function.equals("GCLOCK")){
                            try{
                                final String triggerMode = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIG_MODE))).toString();
                                final Descriptor delay = this.subtree.getData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_DELAY));
                                final Descriptor duration = this.subtree.getData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_DURATION));
                                Descriptor trigTime;
                                if(triggerMode.equals("EVENT")){
                                    final Descriptor eventData = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_EVENT)));
                                    final String event = eventData.toString();
                                    trigTime = (Descriptor)this.eventTimes.get(event);
                                    if(trigTime == null){
                                        this.errorStrings.addElement("Cannot resolve time for event " + event + " in DIO2 GCLOCK device ");
                                        continue;
                                    }
                                    this.subtree.putData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIGGER), trigTime);
                                }else trigTime = this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIGGER));
                                final String initLevel1Str = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_INIT_LEVEL_1))).toString();
                                final String initLevel2Str = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_INIT_LEVEL_2))).toString();
                                final float freq = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_FREQUENCY_1))).toFloat()[0];
                                final boolean init1High = initLevel1Str.equals("HIGH");
                                final boolean init2High = initLevel2Str.equals("HIGH");
                                final Descriptor trig1 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay));
                                final Descriptor trig2 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay) + "+" + this.subtree.decompile(duration));
                                Device device;
                                if(init1High != init2High) device = new Device(RFXTimingSetup.RFX_GCLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath() + " CHAN " + (chan + 1), new Descriptor[]{trig1, trig2}, new float[]{freq}, init1High, comment);
                                else device = new Device(RFXTimingSetup.RFX_GCLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath() + " CHAN " + (chan + 1), new Descriptor[]{}, new float[]{freq}, init1High, comment);
                                this.dio2Devices.addElement(device);
                            }catch(final Exception exc){
                                System.out.println(exc);
                            }
                        }else if(function.equals("DCLOCK")){
                            try{
                                final String triggerMode = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIG_MODE))).toString();
                                final Descriptor delay = this.subtree.getData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_DELAY));
                                final Descriptor duration = this.subtree.getData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_DURATION));
                                Descriptor trigTime;
                                if(triggerMode.equals("EVENT")){
                                    final Descriptor eventData = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_EVENT)));
                                    final String event = eventData.toString();
                                    trigTime = (Descriptor)this.eventTimes.get(event);
                                    if(trigTime == null){
                                        this.errorStrings.addElement("Cannot resolve time for event " + event + " in DIO2 DCLOCK device ");
                                        continue;
                                    }
                                    this.subtree.putData(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIGGER), trigTime);
                                }else trigTime = this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_TRIGGER));
                                final String initLevel1Str = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_INIT_LEVEL_1))).toString();
                                final String initLevel2Str = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_INIT_LEVEL_2))).toString();
                                final float freq1 = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_FREQUENCY_1))).toFloat()[0];
                                final float freq2 = (this.subtree.evaluate(new Nid(baseNid + chan * DIO2_NODES_PER_CHANNEL + DIO2_CHANNEL_0 + DIO2_CHAN_FREQUENCY_2))).toFloat()[0];
                                final boolean init1High = initLevel1Str.equals("HIGH");
                                final boolean init2High = initLevel2Str.equals("HIGH");
                                final Descriptor trig1 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay));
                                final Descriptor trig2 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay) + "+" + this.subtree.decompile(duration));
                                Device device;
                                if(init1High != init2High) device = new Device(RFXTimingSetup.RFX_DCLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath() + " CHAN " + (chan + 1), new Descriptor[]{trig1, trig2}, new float[]{freq1, freq2}, init1High, comment);
                                else device = new Device(RFXTimingSetup.RFX_DCLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath() + " CHAN " + (chan + 1), new Descriptor[]{}, new float[]{freq1, freq2}, init1High, comment);
                                this.dio2Devices.addElement(device);
                            }catch(final Exception exc){
                                System.out.println(exc);
                            }
                        }
                    }catch(final Exception exc){}
                }
            }else if(model.equals("\"RFXClock\"")){
                try{
                    Descriptor decoder = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_CLOCK_DECODER));
                    if(decoder instanceof Path) decoder = this.subtree.resolve((Path)decoder);
                    if(decoder instanceof Nid){
                        final int decIdx = this.mpbDecoderNids.indexOf(decoder);
                        if(decIdx == -1) continue;
                        final int chan = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_CLOCK_CHAN))).toInt()[0];
                        final float freq = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_CLOCK_FREQ))).toFloat()[0];
                        final Device device = new Device(RFXTimingSetup.RFX_CLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{}, new float[]{freq}, false);
                        final Decoder mpb = (Decoder)this.mpbDecoders.elementAt(decIdx);
                        if(!mpb.setChan(chan, device, false)) this.errorStrings.addElement("Duplicated usage of channel " + chan + " in decoder " + mpb.getPath());
                        this.rfxDevices.addElement(device);
                    }
                }catch(final Exception exc){}
            }else if(model.equals("\"RFXPulse\"")){
                try{
                    Descriptor decoder = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_CLOCK_DECODER));
                    if(decoder instanceof Path) decoder = this.subtree.resolve((Path)decoder);
                    if(decoder instanceof Nid){
                        final int decIdx = this.mpbDecoderNids.indexOf(decoder);
                        if(decIdx == -1) continue;
                        final int chan = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_PULSE_CHAN))).toInt()[0];
                        final String triggerMode = ((CString)this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_PULSE_TRIG_MODE))).getValue();
                        final Descriptor delay = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_PULSE_DELAY));
                        final Descriptor duration = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_PULSE_DURATION));
                        Descriptor trigTime;
                        if(triggerMode.equals("EVENT")){
                            final String event = ((CString)this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_PULSE_EVENT))).getValue();
                            trigTime = (Descriptor)this.eventTimes.get(event);
                            if(trigTime == null){
                                this.errorStrings.addElement("Cannot resolve time for event " + event + " in RFXPulse device ");
                                continue;
                            }
                            this.subtree.putData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_PULSE_EXT_TRIGGER), trigTime);
                        }else trigTime = this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_PULSE_EXT_TRIGGER));
                        final String outputMode = ((CString)this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_PULSE_OUTPUT_MODE))).getValue();
                        final Descriptor trig1 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay));
                        Device device;
                        if(outputMode.equals("DOUBLE TOGGLE: INITIAL HIGH")){
                            final Descriptor trig2 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay) + "+" + this.subtree.decompile(duration));
                            device = new Device(RFXTimingSetup.RFX_PULSE, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1, trig2}, new float[]{}, true);
                        }else if(outputMode.equals("DOUBLE TOGGLE: INITIAL LOW")){
                            final Descriptor trig2 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay) + "+" + this.subtree.decompile(duration));
                            device = new Device(RFXTimingSetup.RFX_PULSE, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1, trig2}, new float[]{}, false);
                        }else if(outputMode.equals("HIGH PULSE")) device = new Device(RFXTimingSetup.RFX_PULSE, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1, trig1}, new float[]{}, false);
                        else if(outputMode.equals("LOW PULSE")) device = new Device(RFXTimingSetup.RFX_PULSE, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1, trig1}, new float[]{}, true);
                        else if(outputMode.equals("SINGLE TOGGLE: INITIAL HIGH")) device = new Device(RFXTimingSetup.RFX_PULSE, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1}, new float[]{}, true);
                        else if(outputMode.equals("SINGLE TOGGLE: INITIAL LOW")) device = new Device(RFXTimingSetup.RFX_PULSE, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1}, new float[]{}, false);
                        else continue;
                        final Decoder mpb = (Decoder)this.mpbDecoders.elementAt(decIdx);
                        if(!mpb.setChan(chan, device, false)) this.errorStrings.addElement("Duplicated usage of channel " + chan + " in decoder " + mpb.getPath());
                        this.rfxDevices.addElement(device);
                    }
                }catch(final Exception exc){}
            }else if(model.equals("\"RFXDClock\"")){
                try{
                    Descriptor decoder = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_DECODER));
                    if(decoder instanceof Path) decoder = this.subtree.resolve((Path)decoder);
                    if(decoder instanceof Nid){
                        final int decIdx = this.mpbDecoderNids.indexOf(decoder);
                        if(decIdx == -1) continue;
                        final int gate_chan = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_GATE_CHAN))).toInt()[0];
                        final int clock_chan = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_CLOCK_CHAN))).toInt()[0];
                        final String triggerMode = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_TRIG_MODE))).toString();
                        Descriptor trigTime;
                        if(triggerMode.equals("EVENT")){
                            final String event = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_EVENT))).toString();
                            trigTime = (Descriptor)this.eventTimes.get(event);
                            if(trigTime == null){
                                this.errorStrings.addElement("Cannot resolve time for event " + event + " in RFXDClock device ");
                                continue;
                            }
                            this.subtree.putData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_EXT_TRIG), trigTime);
                        }else trigTime = this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_EXT_TRIG));
                        final String outputMode = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_OUTPUT_MODE))).toString();
                        final Descriptor freq1 = this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_FREQUENCY1));
                        final Descriptor freq2 = this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_FREQUENCY2));
                        final Descriptor delay = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_DELAY));
                        final Descriptor duration = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_DCLOCK_DURATION));
                        Device device;
                        final Descriptor trig1 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay));
                        if(outputMode.equals("DOUBLE SWITCH: TOGGLE") || outputMode.equals("DOUBLE SWITCH: HIGH PULSES") || outputMode.equals("DOUBLE SWITCH: LOW PULSES")){
                            final Descriptor trig2 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay) + " + " + this.subtree.decompile(duration));
                            device = new Device(RFXTimingSetup.RFX_DCLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1, trig2}, new float[]{freq1.toFloat()[0], freq2.toFloat()[0]}, true);
                        }else device = new Device(RFXTimingSetup.RFX_DCLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1}, new float[]{freq1.toFloat()[0], freq2.toFloat()[0]}, true);
                        final Decoder mpb = (Decoder)this.mpbDecoders.elementAt(decIdx);
                        if(!mpb.setChan(clock_chan, device, false)) this.errorStrings.addElement("Duplicated usage of channel " + clock_chan + " in decoder " + mpb.getPath());
                        if(!mpb.setChan(gate_chan, device, true)) this.errorStrings.addElement("Duplicated usage of channel " + gate_chan + " in decoder " + mpb.getPath());
                        this.rfxDevices.addElement(device);
                    }
                }catch(final Exception exc){}
            }else if(model.equals("\"RFXGClock\"")){
                try{
                    Descriptor decoder = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_DECODER));
                    if(decoder instanceof Path) decoder = this.subtree.resolve((Path)decoder);
                    if(decoder instanceof Nid){
                        final int decIdx = this.mpbDecoderNids.indexOf(decoder);
                        if(decIdx == -1) continue;
                        final int gate_chan = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_GATE_CHAN))).toInt()[0];
                        final int clock_chan = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_CLOCK_CHAN))).toInt()[0];
                        final String triggerMode = ((CString)this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_TRIG_MODE))).getValue();
                        Descriptor trigTime;
                        if(triggerMode.equals("EVENT")){
                            final String event = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_EVENT))).toString();
                            trigTime = (Descriptor)this.eventTimes.get(event);
                            if(trigTime == null){
                                this.errorStrings.addElement("Cannot resolve time for event " + event + " in RFXGClock device ");
                                continue;
                            }
                            this.subtree.putData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_EXT_TRIG), trigTime);
                        }else trigTime = this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_EXT_TRIG));
                        final String outputMode = (this.subtree.evaluate(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_OUTPUT_MODE))).toString();
                        final Descriptor freq = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_FREQUENCY));
                        final Descriptor delay = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_DELAY));
                        final Descriptor duration = this.subtree.getData(new Nid(((Nid)this.rfxDeviceNids.elementAt(idx)).getValue() + RFXTimingSetup.N_GCLOCK_DURATION));
                        Device device;
                        final Descriptor trig1 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay));
                        if(outputMode.equals("DOUBLE SWITCH: TOGGLE") || outputMode.equals("DOUBLE SWITCH: HIGH PULSES") || outputMode.equals("DOUBLE SWITCH: LOW PULSES")){
                            final Descriptor trig2 = this.subtree.compile("" + this.subtree.decompile(trigTime) + " + " + this.subtree.decompile(delay) + " + " + this.subtree.decompile(duration));
                            device = new Device(RFXTimingSetup.RFX_GCLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1, trig2}, new float[]{freq.toFloat()[0]}, true);
                        }else device = new Device(RFXTimingSetup.RFX_GCLOCK, this.subtree.getInfo((Nid)this.rfxDeviceNids.elementAt(idx)).getFullPath(), new Descriptor[]{trig1}, new float[]{freq.toFloat()[0]}, true);
                        final Decoder mpb = (Decoder)this.mpbDecoders.elementAt(decIdx);
                        if(!mpb.setChan(clock_chan, device, false)) this.errorStrings.addElement("Duplicated usage of channel " + clock_chan + " in decoder " + mpb.getPath());
                        if(!mpb.setChan(gate_chan, device, true)) this.errorStrings.addElement("Duplicated usage of channel " + gate_chan + " in decoder " + mpb.getPath());
                        this.rfxDevices.addElement(device);
                    }
                }catch(final Exception exc){}
            }
        }
        this.reportEvents();
        if(this.firstScan) this.firstScan = false;
        else{
            final String errors = this.getErrors();
            if(errors != null) JOptionPane.showMessageDialog(RFXTimingSetup.this, errors, "Errors in timing configuration", JOptionPane.WARNING_MESSAGE);
        }
        try{
            if(prevNid != null) this.subtree.setDefault(prevNid);
        }catch(final Exception exc){}
    }

    void showRecordedEvents() {
        if(this.recordedDialog == null){
            this.recordedDialog = new JDialog(this, "Recorded timing events");
            this.recordedDialog.getContentPane().setLayout(new BorderLayout());
            final JTable recEventTable = new JTable(new AbstractTableModel(){
                /**
                 *
                 */
                private static final long serialVersionUID = 7597940550952567538L;

                @Override
                public int getColumnCount() {
                    return 2;
                }

                @Override
                public String getColumnName(final int col) {
                    switch(col){
                        case 0:
                            return "Event";
                        case 1:
                            return "Time";
                    }
                    return "";
                }

                @Override
                public int getRowCount() {
                    return RFXTimingSetup.this.fullRecEvents.length;
                }

                @Override
                public Object getValueAt(final int row, final int col) {
                    switch(col){
                        case 0:
                            return RFXTimingSetup.this.fullRecEvents[row];
                        case 1:
                            return "" + RFXTimingSetup.this.fullRecTimes[row];
                    }
                    return "";
                }

                @Override
                public boolean isCellEditable(final int row, final int col) {
                    return false;
                }
            });
            final JScrollPane scroll = new JScrollPane(recEventTable);
            recEventTable.setPreferredScrollableViewportSize(new Dimension(250, 100));
            this.recordedDialog.getContentPane().add(scroll, "Center");
            final JButton closeB = new JButton("Close");
            closeB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    RFXTimingSetup.this.recordedDialog.setVisible(false);
                }
            });
            final JPanel jp = new JPanel();
            jp.add(closeB);
            this.recordedDialog.getContentPane().add(jp, "South");
            this.recordedDialog.pack();
        }
        this.recordedDialog.setVisible(true);
        this.eventTable.repaint();
    }

    void showSignals(final Graphics g, final Dimension d) {
        Decoder decoder;
        float minStart = (float)1E6;
        float maxEnd = (float)-1E6;
        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.black);
        for(int idx = 0; idx < this.mpbDecoders.size(); idx++){
            decoder = (Decoder)this.mpbDecoders.elementAt(idx);
            for(int chan = 0; chan < 6; chan++){
                Device device = null;
                if(!decoder.isGate[chan]) device = decoder.devices[chan];
                if(device == null) continue;
                for(final Descriptor time2 : device.times){
                    float time = 0;
                    try{
                        time = this.subtree.evaluate(time2).toFloat()[0];
                    }catch(final Exception exc){}
                    if(time < minStart) minStart = time;
                    if(time > maxEnd) maxEnd = time;
                }
            }
        }
        for(int idx = 0; idx < this.dio2Devices.size(); idx++){
            final Device device = (Device)this.dio2Devices.elementAt(idx);
            for(final Descriptor time2 : device.times){
                float time = 0;
                try{
                    time = this.subtree.evaluate(time2).toFloat()[0];
                }catch(final Exception exc){}
                if(time < minStart) minStart = time;
                if(time > maxEnd) maxEnd = time;
            }
        }
        int chanIdx = 1;
        final float start = minStart - (maxEnd - minStart) / 4;
        final float convFact = d.width / (maxEnd - minStart + (maxEnd - minStart) / 2);
        for(int idx = 0; idx < this.mpbDecoders.size(); idx++){
            decoder = (Decoder)this.mpbDecoders.elementAt(idx);
            for(int chan = 0; chan < 6; chan++){
                Device device = null;
                if(!decoder.isGate[chan]){
                    device = decoder.devices[chan];
                    if(device == null) continue;
                    try{
                        device.draw(g, chanIdx, start, convFact, d.width, decoder.path, chan);
                        chanIdx++;
                    }catch(final Exception exc){
                        System.err.println("Error drawing output for device " + device.path + " (channel " + chan + " of decoder " + decoder.path + "): " + exc);
                    }
                }
            }
        }
        for(int idx = 0; idx < this.dio2Devices.size(); idx++){
            final Device device = (Device)this.dio2Devices.elementAt(idx);
            try{
                device.draw(g, chanIdx, start, convFact, d.width, null, 0);
                chanIdx++;
            }catch(final Exception exc){
                System.err.println("Error drawing output for device " + device.path + ": " + exc);
            }
        }
    }
}

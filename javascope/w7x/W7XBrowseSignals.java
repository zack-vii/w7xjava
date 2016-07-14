package w7x;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JComponent;
import java.util.List;
import java.util.TimeZone;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.SpinnerDateModel;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import de.mpg.ipp.codac.signalaccess.SignalAddress;
import de.mpg.ipp.codac.w7xtime.TimeInterval;
import jscope.Grid;
import jscope.jScopeBrowseSignals;
import jscope.jScopeFacade;
import jscope.jScopeWaveContainer;

@SuppressWarnings("serial")
public final class W7XBrowseSignals extends jScopeBrowseSignals{
    public static class CalendarEdit extends JPanel{
        private final class DateTimePicker extends JXDatePicker{
            private JPanel   timePanel;
            private JSpinner timeSpinner;

            public DateTimePicker(){
                super();
                this.getMonthView().setSelectionModel(new SingleDaySelectionModel());
                this.setTimeZone(W7XBrowseSignals.UTC);
                this.setFormats(CalendarEdit.format);
                this.updateTextFieldFormat();
                this.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if(CalendarEdit.this.settime_cb.isSelected()) CalendarEdit.this.setTiming();
                    }
                });
            }

            public DateTimePicker(final Date date, final int hh, final int mm, final int ss, final int SSS){
                this();
                this.setDateTime(date, hh, mm, ss, SSS);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                this.setTimeSpinners();
            }

            @Override
            public void commitEdit() throws ParseException {
                this.commitTime();
                super.commitEdit();
            }

            private void commitTime() {
                final Date date = this.getDate();
                if(date == null) return;
                final Calendar timeCalendar = Calendar.getInstance(W7XBrowseSignals.UTC);
                timeCalendar.setTimeInMillis(((Date)this.timeSpinner.getValue()).getTime() % Grid.dayMilliSeconds);
                final Calendar calendar = Calendar.getInstance(W7XBrowseSignals.UTC);
                calendar.setTime(date);
                for(final int entry : W7XBrowseSignals.time)
                    calendar.set(entry, timeCalendar.get(entry));
                this.setDate(calendar.getTime());
            }

            private JPanel createTimePanel() {
                final JPanel newPanel = new JPanel();
                newPanel.setLayout(new FlowLayout());
                final SpinnerDateModel dateModel = new SpinnerDateModel();
                this.timeSpinner = new JSpinner(dateModel);
                this.updateTextFieldFormat();
                newPanel.add(new JLabel("Time:"));
                newPanel.add(this.timeSpinner);
                this.timeSpinner.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(final ChangeEvent e) {
                        DateTimePicker.this.commitTime();
                    }
                });
                newPanel.setBackground(Color.WHITE);
                return newPanel;
            }

            @Override
            public JPanel getLinkPanel() {
                super.getLinkPanel();
                if(this.timePanel == null) this.timePanel = this.createTimePanel();
                this.setTimeSpinners();
                return this.timePanel;
            }

            public final void setDateTime(final Date date, final int hh, final int mm, final int ss, final int SSS) {
                final Calendar calendar = Calendar.getInstance(W7XBrowseSignals.UTC);
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, hh);
                calendar.set(Calendar.MINUTE, mm);
                calendar.set(Calendar.SECOND, ss);
                calendar.set(Calendar.MILLISECOND, SSS);
                this.setDate(calendar.getTime());
            }

            private void setTimeSpinners() {
                final Date date = this.getDate();
                if(date == null) return;
                this.timeSpinner.setValue(date);
            }

            private void updateTextFieldFormat() {
                if(this.timeSpinner == null) return;
                final JFormattedTextField tf = ((JSpinner.DefaultEditor)this.timeSpinner.getEditor()).getTextField();
                final DefaultFormatterFactory factory = (DefaultFormatterFactory)tf.getFormatterFactory();
                final DateFormatter formatter = (DateFormatter)factory.getDefaultFormatter();
                formatter.setFormat(CalendarEdit.timeFormat);
            }
        }
        public static final DateFormat  format     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        static{
            CalendarEdit.timeFormat.setTimeZone(W7XBrowseSignals.UTC);
            CalendarEdit.format.setTimeZone(W7XBrowseSignals.UTC);
        }
        private final JXMonthView    jxmv;
        private final DateTimePicker from, upto;
        private final JCheckBox      is_image, settime_cb;

        public CalendarEdit(){
            this(W7XBrowseSignals.UTC);
        }

        public CalendarEdit(final TimeZone tz){
            super(new BorderLayout());
            this.jxmv = new JXMonthView();
            this.jxmv.setTimeZone(tz);
            final Date date = this.jxmv.getToday();
            this.jxmv.setSelectionDate(date);
            this.from = new DateTimePicker(date, 0, 0, 0, 0);
            this.upto = new DateTimePicker(date, 23, 59, 59, 999);
            this.jxmv.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    CalendarEdit.this.from.setDateTime(CalendarEdit.this.jxmv.getSelectionDate(), 0, 0, 0, 0);
                    CalendarEdit.this.upto.setDateTime(CalendarEdit.this.jxmv.getSelectionDate(), 23, 59, 59, 999);
                    if(CalendarEdit.this.settime_cb.isSelected()) CalendarEdit.this.setTiming();
                }
            });
            JButton jb;
            JPanel bp, jp;
            this.add(jp = new JPanel(new BorderLayout()), BorderLayout.WEST);
            jp.add(this.jxmv, BorderLayout.CENTER);
            jp.add(jp = new JPanel(new BorderLayout()), BorderLayout.NORTH);
            jp.add(bp = new JPanel(new GridLayout(1, 2)), BorderLayout.WEST);
            bp.add(jb = new JButton("<<"));
            jb.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    CalendarEdit.this.modCalendar(Calendar.YEAR, -1);
                }
            });
            bp.add(jb = new JButton("<"));
            jb.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    CalendarEdit.this.modCalendar(Calendar.MONTH, -1);
                }
            });
            jp.add(jb = new JButton(), BorderLayout.CENTER);
            jb.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    CalendarEdit.this.setToday();
                }
            });
            jp.add(bp = new JPanel(new GridLayout(1, 2)), BorderLayout.EAST);
            bp.add(jb = new JButton(">"));
            jb.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    CalendarEdit.this.modCalendar(Calendar.MONTH, 1);
                }
            });
            bp.add(jb = new JButton(">>"));
            jb.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    CalendarEdit.this.modCalendar(Calendar.YEAR, 1);
                }
            });
            /* the datepicker */
            JPanel ejp;
            this.settime_cb = new JCheckBox("setTime");
            this.add(jp = new JPanel(new BorderLayout()), BorderLayout.CENTER);
            jp.add(ejp = new JPanel());
            ejp.add(this.settime_cb);
            this.settime_cb.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if(CalendarEdit.this.settime_cb.isSelected()) CalendarEdit.this.setTiming();
                }
            });
            final JButton cleartime_b = new JButton("clearTime");
            ejp.add(cleartime_b);
            cleartime_b.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    CalendarEdit.this.settime_cb.setSelected(false);
                    W7XDataProvider.setTiming();
                }
            });
            ejp.add(this.is_image = new JCheckBox("asImage"));
            jp.add(jp = new JPanel(new GridLayout(1, 2)), BorderLayout.SOUTH);
            jp.add(this.from);
            jp.add(this.upto);
        }

        private final Calendar getCalendar() {
            final Date date = this.jxmv.isSelectionEmpty() ? this.jxmv.getFirstDisplayedDay() : this.jxmv.getSelectionDate();
            final Calendar cal = Calendar.getInstance(this.jxmv.getTimeZone());
            cal.setTime(date);
            return cal;
        }

        public final long getDate() {
            final Calendar cal = this.jxmv.getCalendar();
            for(final int entry : W7XBrowseSignals.time)
                cal.set(entry, 0);
            return cal.getTimeInMillis() * 1000000L;
        }

        private final void modCalendar(final int mode, final int increment) {
            final Calendar cal = CalendarEdit.this.getCalendar();
            cal.set(mode, cal.get(mode) + increment);
            CalendarEdit.this.setCalendar(cal, false);
        }

        public final void setCalendar(final Calendar cal, final boolean setselection) {
            final Date date = cal.getTime();
            if(!this.jxmv.isSelectionEmpty() || setselection) this.jxmv.setSelectionDate(date);
            this.jxmv.setFirstDisplayedDay(date);
            this.jxmv.updateUI();
        }

        protected void setTiming() {
            W7XDataProvider.setTiming(CalendarEdit.this.from.getDate().getTime(), CalendarEdit.this.upto.getDate().getTime());
            jScopeFacade.instance.updateAllWaves();
        }

        public void setToday() {
            final Calendar cal = Calendar.getInstance(CalendarEdit.this.jxmv.getTimeZone());
            cal.setTime(CalendarEdit.this.jxmv.getToday());
            CalendarEdit.this.setCalendar(cal, true);
        }
    }
    private static final class FromTransferHandler extends TransferHandler{
        @Override
        public final Transferable createTransferable(final JComponent comp) {
            try{
                return new StringSelection("W7X:" + ((w7xNode)((JTree)comp).getLastSelectedPathComponent()).getSignalPath());
            }catch(final Exception exc){
                return null;
            }
        }

        @Override
        public final int getSourceActions(final JComponent comp) {
            return TransferHandler.COPY_OR_MOVE;
        }
    }
    public final class W7XDataBase extends w7xNode{
        private final String         name;
        public final W7XSignalAccess sa;

        public W7XDataBase(final String name, final W7XSignalAccess sa){
            super(sa.getAddress(""));
            this.name = name;
            this.sa = sa;
        }

        @Override
        public final String toString() {
            return this.name;
        }
    }
    public class w7xNode extends DefaultMutableTreeNode{
        private boolean loaded = false;

        public w7xNode(final SignalAddress userObject){
            super(userObject);
            this.setAllowsChildren(true);
        }

        public void addSignal() {
            if(W7XBrowseSignals.this.wave_panel == null) return;
            final TreeNode[] path = this.getPath();
            if(path == null || path.length < 2) return;
            final String sig_path = "/" + ((W7XDataBase)path[1]).name + this.getSignalPath();
            if(sig_path != null) W7XBrowseSignals.this.wave_panel.addSignal("W7X", null, null, sig_path, false, W7XBrowseSignals.this.isImage());
        }

        public final List<SignalAddress> getChildren() {
            final TreeNode[] path = this.getPath();
            if(path == null || path.length < 2) return null;
            final TimeInterval ti = W7XBrowseSignals.this.getTimeInterval();
            return W7XSignalAccess.getList(this.getSignalPath(), ti);
        }

        private String getSignalPath() {
            return ((SignalAddress)this.getUserObject()).toString();
        }

        @Override
        public boolean isLeaf() {
            return this.loaded && !this.getAllowsChildren();
        }

        private void loadChildren() {
            this.setChildren(this.getChildren());
        }

        private void setChildren(final List<SignalAddress> children) {
            if(children == null) return;
            this.removeAllChildren();
            this.setAllowsChildren(children.size() > 0);
            for(final SignalAddress node : children)
                this.add(new w7xNode(node));
            this.loaded = true;
        }

        @Override
        public String toString() {
            return ((SignalAddress)this.getUserObject()).tail();
        }
    }
    public static final TimeZone        UTC  = TimeZone.getTimeZone("UTC");
    private static final int[]          time = new int[]{Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};
    private String                      server_url;
    private String                      shot;
    public final DefaultMutableTreeNode top;
    private String                      tree;
    public jScopeWaveContainer          wave_panel;
    private final CalendarEdit          calendarEdit;

    /**
     * Create the frame.
     */
    public W7XBrowseSignals(){
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        this.calendarEdit = new CalendarEdit();
        contentPane.add(this.calendarEdit, BorderLayout.PAGE_START);
        this.setContentPane(contentPane);
        this.top = new DefaultMutableTreeNode("DataBase");
        final JTree tree = new JTree(this.top);
        tree.setTransferHandler(new FromTransferHandler());
        tree.setDragEnabled(true);
        contentPane.add(new JScrollPane(tree));
        tree.setRootVisible(true);
        for(final String db : W7XSignalAccess.getDataBaseList()){
            final W7XSignalAccess sa = W7XSignalAccess.getAccess(db);
            if(sa == null) break;
            this.top.add(new W7XDataBase(db, sa));
        }
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.addTreeWillExpandListener(new TreeWillExpandListener(){
            @Override
            public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException {}

            @Override
            public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
                final TreePath path = event.getPath();
                if(path.getLastPathComponent() instanceof w7xNode){
                    final w7xNode node = (w7xNode)path.getLastPathComponent();
                    if(!node.loaded) node.loadChildren();
                }
            }
        });
        tree.setToggleClickCount(0);
        tree.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(final MouseEvent e) {
                if(e.getClickCount() != 1) return;
                final int selRow = tree.getRowForLocation(e.getX(), e.getY());
                if(selRow < 0) return;
                final TreePath path = tree.getPathForRow(selRow);
                if(((w7xNode)path.getLastPathComponent()).loaded) tree.expandRow(selRow);
                else new Thread(){
                    @Override
                    public final void run() {
                        tree.expandRow(selRow);
                    }
                }.start();
                /*else{//do not add just page it to mark a leaf
                    final w7xNode node = (w7xNode)path.getLastPathComponent();
                    if(e.getClickCount() == 2 && node.isLeaf()) node.addSignal();
                }*/
            }
        });
        tree.expandPath(new TreePath(this.top));
        this.pack();
    }

    @Override
    public String getDefaultURL() {
        return "http://archive-webapi.ipp-hgw.mpg.de";
    }

    @Override
    protected String getServerAddr() {
        return this.server_url;
    }

    @Override
    protected String getShot() {
        return this.shot;
    }

    @Override
    protected String getSignal(final String url_name) {
        return null;
    }

    public final TimeInterval getTimeInterval() {
        final long from = this.calendarEdit.getDate();
        final long upto = from + 86399999999999L;
        return TimeInterval.ALL.withStart(from).withEnd(upto);
    }

    @Override
    protected String getTree() {
        return this.tree;
    }

    public final boolean isImage() {
        return this.calendarEdit.is_image.isSelected();
    }

    @Override
    public void setWaveContainer(final jScopeWaveContainer wave_panel) {
        this.wave_panel = wave_panel;
    }
}
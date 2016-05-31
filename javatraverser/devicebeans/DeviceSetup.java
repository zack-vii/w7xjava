package devicebeans;

import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import jTraverser.DataChangeEvent;
import jTraverser.DataChangeListener;
import jTraverser.Node;
import jTraverser.NodeInfo;
import mds.Database;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

@SuppressWarnings("serial")
public class DeviceSetup extends JDialog{
    // public int width = 400, height = 200;
    static Hashtable<Integer, DeviceSetup> activeNidHash = new Hashtable<Integer, DeviceSetup>();
    static Vector<DeviceSetup>             openDevicesV  = new Vector<DeviceSetup>();

    public static void activateDeviceSetup(final String deviceName, final String experiment, final int shot, final String rootName, final int x, final int y) {
        final Database tree;
        try{
            tree = new Database(experiment, shot, Database.NORMAL);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(null, "Error opening tree " + experiment + " shot " + shot + ": " + exc, "Error in Device Setup", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Nid nid;
        try{
            nid = tree.resolve(new Path(rootName));
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(null, "Cannot find node " + rootName + ": " + exc, "Error in Device Setup", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try{
            final String deviceClassName = deviceName + "Setup";
            final Class deviceClass = Class.forName(deviceClassName);
            final DeviceSetup ds = (DeviceSetup)deviceClass.newInstance();
            ds.configure(tree, nid.getValue(), null);
            if(ds.getContentPane().getLayout() != null) ds.pack();
            ds.setLocation(x, y);
            ds.setVisible(true);
        }catch(final Exception exc){
            exc.printStackTrace();
            JOptionPane.showMessageDialog(null, "Cannot activate Setup dir device " + deviceName + ": " + exc, "Error in Device Setup", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void closeOpenDevices() {
        for(int i = 0; i < DeviceSetup.openDevicesV.size(); i++)
            DeviceSetup.openDevicesV.elementAt(i).cancel();
    }

    public static DeviceSetup getDevice(final int baseNid) {
        return DeviceSetup.activeNidHash.get(new Integer(baseNid));
    }

    public static void main(final String args[]) {
        DeviceSetup.activateDeviceSetup("T2Control", "T2", -1, "\\T2", 100, 100);
    }
    public int                                 baseNid, num_components = 0;
    protected DeviceButtons                    buttons               = null;
    protected Vector<DataChangeListener>       dataChangeListeners   = new Vector<DataChangeListener>();
    protected Vector<DeviceComponent>          device_components     = new Vector<DeviceComponent>();
    protected Vector<DeviceControl>            device_controls       = new Vector<DeviceControl>();
    Vector<DeviceCloseListener>                deviceCloseListenerV  = new Vector<DeviceCloseListener>();
    Node                                       deviceNode;
    protected String                           deviceProvider;
    protected String                           deviceTitle;
    protected String                           deviceType;
    Vector<DeviceUpdateListener>               deviceUpdateListenerV = new Vector<DeviceUpdateListener>();
    private Frame                              frame;
    boolean                                    justApplied           = false;
    protected String[]                         methods;
    JMenuItem                                  pop_items[];
    JPopupMenu                                 pop_methods           = null;
    boolean                                    readOnly              = false;
    public Database                            subtree               = null;
    Hashtable<String, Vector<DeviceComponent>> updateHash            = new Hashtable<String, Vector<DeviceComponent>>();

    public DeviceSetup(){
        this(null);
    }

    public DeviceSetup(final Frame frame){
        this(frame, false);
    }

    public DeviceSetup(final Frame frame, final boolean readOnly){
        super(frame);
        this.readOnly = readOnly;
        this.setTitle(this.deviceTitle);
        DeviceSetupBeanInfo.beanDeviceType = this.deviceType;
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(final WindowEvent e) {
                DeviceSetup.this.cancel();
            }
        });
        DeviceSetup.openDevicesV.addElement(this);
    }

    public void addButton(final JButton button) {
        if(this.buttons != null) this.buttons.add(button);
    }

    public void addDataChangeListener(final DataChangeListener listener) {
        this.dataChangeListeners.addElement(listener);
    }

    public void addDeviceCloseListener(final DeviceCloseListener listener) {
        this.deviceCloseListenerV.addElement(listener);
    }

    void addDeviceUpdateListener(final DeviceUpdateListener listener) {
        this.deviceUpdateListenerV.addElement(listener);
    }

    public void apply() {
        Nid oldNid = null;
        try{
            oldNid = this.subtree.getDefault();
            this.subtree.setDefault(new Nid(this.baseNid));
        }catch(final Exception exc){}
        for(int i = 0; i < this.num_components; i++){
            try{
                this.device_components.elementAt(i).apply();
            }catch(final Exception exc){
                JOptionPane.showMessageDialog(this, exc.toString(), "Error writing data at offset nid " + this.device_components.elementAt(i).getOffsetNid(), JOptionPane.WARNING_MESSAGE);
            }
        }
        for(int i = 0; i < this.num_components; i++){
            this.device_components.elementAt(i).postApply();
        }
        this.fireDataChangeEvent();
        try{
            this.subtree.setDefault(oldNid);
        }catch(final Exception exc){}
        for(int i = 0; i < this.deviceUpdateListenerV.size(); i++){
            if(this.isChanged()) this.deviceUpdateListenerV.elementAt(i).deviceUpdated();
        }
        if(this.deviceNode != null){
            this.deviceNode.setAllOnUnchecked();
            this.frame.repaint();
        }
        this.justApplied = true;
    }

    public void apply(final int currBaseNid) {
        Nid oldNid = null;
        try{
            oldNid = this.subtree.getDefault();
            this.subtree.setDefault(new Nid(currBaseNid));
        }catch(final Exception exc){}
        for(int i = 0; i < this.num_components; i++){
            try{
                this.device_components.elementAt(i).apply(currBaseNid);
            }catch(final Exception exc){
                JOptionPane.showMessageDialog(this, exc.toString(), "Error writing data at offset nid " + this.device_components.elementAt(i).getOffsetNid(), JOptionPane.WARNING_MESSAGE);
            }
        }
        try{
            this.subtree.setDefault(oldNid);
        }catch(final Exception exc){}
        if(this.deviceNode != null) this.deviceNode.setAllOnUnchecked();
        this.justApplied = true;
    }

    void cancel() {
        DeviceSetup.activeNidHash.remove(new Integer(this.baseNid));
        DeviceSetup.openDevicesV.removeElement(this);
        if(this.deviceNode != null) this.deviceNode.setAllOnUnchecked();
        this.dispose();
        for(int i = 0; i < this.deviceCloseListenerV.size(); i++)
            this.deviceCloseListenerV.elementAt(i).deviceClosed(this.isChanged(), this.justApplied);
        this.justApplied = false;
    }

    public boolean check() {
        if(this.buttons != null) return this.buttons.check();
        return true;
    }

    public boolean check(final String expressions[], final String[] messages) {
        if(expressions == null || messages == null) return true;
        int num_expr = expressions.length;
        if(num_expr > messages.length) num_expr = messages.length;
        int idx;
        String currId;
        Descriptor currData;
        final StringBuffer varExpr = new StringBuffer();
        for(idx = 0; idx < this.device_components.size(); idx++){
            currId = (this.device_components.elementAt(idx)).getIdentifier();
            if(currId != null && !currId.trim().equals("")){
                currData = (this.device_components.elementAt(idx)).getData();
                if(currData != null) varExpr.append("_" + currId + " = " + currData.toString() + ";");
                if((this.device_components.elementAt(idx)).getState()) varExpr.append("_" + currId + "_state = 1; ");
                else varExpr.append("_" + currId + "_state = 0; ");
            }
        }
        for(idx = 0; idx < num_expr; idx++){
            try{
                if(Database.tdiEvaluate(varExpr + expressions[idx]).toInt()[0] == 0){
                    JOptionPane.showMessageDialog(this, messages[idx], "Error in device configuration", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }catch(final MdsException e){
                JOptionPane.showMessageDialog(this, messages[idx], "Error in device configuration" + e.getMessage(), JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public void configure(final Database subtree, final int baseNid) {
        this.configure(subtree, baseNid, null);
    }

    public void configure(final Database subtree, final int baseNid, final Node node) {
        this.deviceNode = node;
        DeviceSetup.activeNidHash.put(new Integer(baseNid), this);
        Nid oldNid = null;
        try{
            oldNid = subtree.getDefault();
            subtree.setDefault(new Nid(baseNid));
        }catch(final Exception exc){
            System.out.println(exc);
        }
        this.baseNid = baseNid;
        this.subtree = subtree;
        String path = null;
        try{
            final NodeInfo info = subtree.getInfo(new Nid(baseNid));
            path = info.getFullPath();
        }catch(final Exception exc){
            System.out.println(exc);
        }
        if(path == null) this.setTitle(this.deviceTitle);
        else this.setTitle(this.deviceTitle + " -- " + path);
        // collect every DeviceComponent
        final Stack<Container> search_stack = new Stack<Container>();
        search_stack.push(this);
        do{
            final Component[] curr_components = (search_stack.pop()).getComponents();
            if(curr_components == null) continue;
            for(final Component curr_component : curr_components){
                if(curr_component instanceof DeviceButtons) this.methods = ((DeviceButtons)curr_component).getMethods();
                if(curr_component instanceof DeviceComponent) this.device_components.addElement((DeviceComponent)curr_component);
                if(curr_component instanceof DeviceControl) this.device_controls.addElement((DeviceControl)curr_component);
                if(curr_component instanceof Container) search_stack.push((Container)curr_component);
                if(curr_component instanceof DeviceButtons) this.buttons = (DeviceButtons)curr_component;
            }
        }while(!search_stack.empty());
        // done
        this.num_components = this.device_components.size();
        for(int i = 0; i < this.num_components; i++){
            this.device_components.elementAt(i).setSubtree(subtree);
            this.device_components.elementAt(i).configure(baseNid, this.readOnly);
            final String currUpdateId = this.device_components.elementAt(i).getUpdateId(this);
            if(currUpdateId != null && !currUpdateId.equals("")){
                Vector<DeviceComponent> components = this.updateHash.get(currUpdateId);
                if(components == null){
                    components = new Vector<DeviceComponent>();
                    this.updateHash.put(currUpdateId, components);
                }
                components.addElement(this.device_components.elementAt(i));
            }
        }
        try{
            // A secod turn in order to carry out actions which need inter-component info
            for(int i = 0; i < this.num_components; i++){
                this.device_components.elementAt(i).postConfigure();
            }
        }catch(final Throwable exc){
            System.out.println(exc);
        }
        if(this.methods != null && this.methods.length > 0){
            this.pop_methods = new JPopupMenu("Methods");
            // pop_methods = new JPopupMenu();
            this.pop_items = new JMenuItem[this.methods.length];
            for(int i = 0; i < this.methods.length; i++){
                this.pop_methods.add(this.pop_items[i] = new JMenuItem(this.methods[i]));
                this.pop_items[i].addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        boolean success = true;
                        String errmsg = "";
                        int j;
                        for(j = 0; j < DeviceSetup.this.pop_items.length && ((JMenuItem)e.getSource()) != DeviceSetup.this.pop_items[j]; j++);
                        if(j == DeviceSetup.this.pop_items.length) return;
                        if(JOptionPane.showConfirmDialog(DeviceSetup.this, "Execute " + DeviceSetup.this.methods[j] + "?", "Execute a device method", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                            try{
                                DeviceSetup.this.subtree.doDeviceMethod(new Nid(DeviceSetup.this.baseNid), DeviceSetup.this.methods[j]);
                            }catch(final Exception exc){
                                errmsg = exc.toString();
                                success = false;
                            }
                            if(!success) JOptionPane.showMessageDialog(DeviceSetup.this, "Error executing method " + DeviceSetup.this.methods[j] + ": " + errmsg, "Method execution report", JOptionPane.WARNING_MESSAGE);
                            else JOptionPane.showMessageDialog(DeviceSetup.this, "Method " + DeviceSetup.this.methods[j] + " succesfully executed", "Method execution report", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                });
            }
            this.pop_methods.pack();
            this.addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(final MouseEvent e) {
                    if((e.getModifiers() & Event.META_MASK) != 0){
                        DeviceSetup.this.pop_methods.setInvoker(DeviceSetup.this);
                        DeviceSetup.this.pop_methods.show(DeviceSetup.this, e.getX(), e.getY());
                    }
                }
                /*public void mouseReleased(MouseEvent e)
                                 {
                    if(e.isPopupTrigger())
                        pop_methods.show(DeviceSetup.this, e.getX(), e.getY());
                                 }*/
            });
        }
        try{
            subtree.setDefault(oldNid);
        }catch(final Exception exc){
            System.out.println("Error in Configure: " + exc);
        }
    }

    protected void fireDataChangeEvent() {
        final int num_listeners = this.dataChangeListeners.size();
        for(int i = 0; i < num_listeners; i++)
            this.dataChangeListeners.elementAt(i).dataChanged(new DataChangeEvent(this, 0, null));
    }

    public void fireUpdate(final String id, final Descriptor val) {
        final Vector components = this.updateHash.get(id);
        if(components != null){
            for(int i = 0; i < components.size(); i++)
                ((DeviceComponent)components.elementAt(i)).fireUpdate(id, val);
        }
    }

    public String getDeviceProvider() {
        DeviceSetupBeanInfo.beanDeviceProvider = this.deviceProvider;
        return this.deviceProvider;
    }

    public String getDeviceTitle() {
        return this.deviceTitle;
    }

    public String getDeviceType() {
        DeviceSetupBeanInfo.beanDeviceType = this.deviceType;
        return this.deviceType;
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    boolean isChanged() {
        for(int i = 0; i < this.num_components; i++){
            final DeviceComponent currComponent = this.device_components.elementAt(i);
            if(currComponent.isChanged()) return true;
            if(currComponent.isStateChanged()) return true;
        }
        return false;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void propagateData(final int offsetNid, final Object data) {
        for(int idx = 0; idx < this.device_components.size(); idx++)
            this.device_components.elementAt(idx).dataChanged(offsetNid, data);
    }

    public void propagateState(final int offsetNid, final boolean state) {
        for(int idx = 0; idx < this.device_components.size(); idx++)
            this.device_components.elementAt(idx).stateChanged(offsetNid, state);
    }

    public void reset() {
        Nid oldNid = null;
        try{
            oldNid = this.subtree.getDefault();
            this.subtree.setDefault(new Nid(this.baseNid));
        }catch(final Exception exc){
            System.out.println(exc);
        }
        for(int i = 0; i < this.num_components; i++)
            this.device_components.elementAt(i).reset();
        try{
            this.subtree.setDefault(oldNid);
        }catch(final Exception exc){
            System.out.println("Error in Configure: " + exc);
        }
    }

    public void resetNidHash() {
        DeviceSetup.activeNidHash.remove(new Integer(this.baseNid));
    }

    public void setCancelText(final String cancelText) {
        if(this.buttons != null) this.buttons.setCancelText(cancelText);
    }

    public void setDeviceProvider(final String deviceProvider) {
        this.deviceProvider = deviceProvider;
        DeviceSetupBeanInfo.beanDeviceProvider = deviceProvider;
    }

    public void setDeviceTitle(final String deviceTitle) {
        this.deviceTitle = deviceTitle;
        // setTitle(deviceTitle);
    }

    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
        DeviceSetupBeanInfo.beanDeviceType = deviceType;
    }

    public final void setFrame(final Frame frame) {
        this.frame = frame;
    }

    public void setHeight(final int height) {
        final int width = super.getWidth();
        this.setSize(width, height);
    }

    public void setHighlight(final boolean isHighlighted, final int[] nids) {
        DeviceComponent currDevComponent;
        for(int i = 0; i < this.device_components.size(); i++){
            currDevComponent = this.device_components.elementAt(i);
            final int nid = currDevComponent.getBaseNid() + currDevComponent.getOffsetNid();
            int j;
            for(j = 0; j < nids.length; j++)
                if(nids[j] == nid) break;
            if(j < nids.length) currDevComponent.setHighlight(isHighlighted);
        }
    }

    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        for(int i = 0; i < this.device_controls.size(); i++){
            this.device_controls.elementAt(i).setReadOnly(readOnly);
        }
    }

    public void setWidth(final int width) {
        final int height = super.getHeight();
        this.setSize(width, height);
    }

    public void updateIdentifiers() {
        final StringBuffer varExpr = new StringBuffer();
        for(int idx = 0; idx < this.device_components.size(); idx++){
            final String currId = (this.device_components.elementAt(idx)).getIdentifier();
            if(currId != null){
                final Descriptor currData = (this.device_components.elementAt(idx)).getData();
                if(currData != null) varExpr.append("_" + currId + " = " + currData.toString() + ";");
                if((this.device_components.elementAt(idx)).getState()) varExpr.append("_" + currId + "_state = 1; ");
                else varExpr.append("_" + currId + "_state = 0; ");
            }
        }
        if(this.device_components.size() > 0) try{
            Database.tdiEvaluate(varExpr.toString());
        }catch(final MdsException e){
            e.printStackTrace();
        }// TODO
    }
}

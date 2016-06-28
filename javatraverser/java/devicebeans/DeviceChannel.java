package devicebeans;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mds.data.descriptor.Descriptor;

@SuppressWarnings("serial")
public class DeviceChannel extends DeviceComponent{
    // Keep copied data for channel components: indexed by the offset between this nid and the component nid
    static Hashtable<Integer, Object> componentHash = new Hashtable<Integer, Object>();

    public static void main(final String[] args) {}
    public boolean              borderVisible     = false;
    protected JCheckBox         checkB            = null;
    protected JPanel            componentsPanel;
    private final JPopupMenu    copyPastePopup;
    protected Vector<Component> device_components = null;
    private boolean             initial_state;
    protected boolean           initializing      = false;
    public boolean              inSameLine        = false;
    public String               labelString       = null;          // if columns == 0 FlowLayout is assumed
    public int                  lines             = 1, columns = 0;
    private boolean             reportingChange   = false;
    public boolean              showState         = true;
    public String               showVal;

    public DeviceChannel(){
        this.initializing = true;
        this.mode = DeviceComponent.STATE;
        this.setLayout(new BorderLayout());
        this.componentsPanel = new JPanel();
        this.add(this.checkB = new JCheckBox(), "North");
        this.checkB.setText(this.labelString);
        this.add(this.componentsPanel, "Center");
        this.copyPastePopup = new JPopupMenu();
        final JMenuItem copyI = new JMenuItem("Copy");
        copyI.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                DeviceChannel.this.copy();
            }
        });
        this.copyPastePopup.add(copyI);
        final JMenuItem pasteI = new JMenuItem("Paste");
        pasteI.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                DeviceChannel.this.paste();
            }
        });
        this.copyPastePopup.add(pasteI);
        final JMenuItem propagateI = new JMenuItem("Propagate");
        propagateI.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                DeviceChannel.this.propagate();
            }
        });
        this.copyPastePopup.add(propagateI);
        this.copyPastePopup.pack();
        this.copyPastePopup.setInvoker(this);
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(final MouseEvent e) {
                if((e.getModifiers() & Event.META_MASK) != 0){
                    DeviceChannel.this.copyPastePopup.setInvoker(DeviceChannel.this);
                    DeviceChannel.this.copyPastePopup.show(DeviceChannel.this, e.getX(), e.getY());
                }
            }
        });
        this.initializing = false;
    }

    @Override
    public Component add(final Component c) {
        if(!this.initializing) return this.componentsPanel.add(c);
        return super.add(c);
    }

    @Override
    public Component add(final Component c, final int intex) {
        if(!this.initializing) return this.componentsPanel.add(c);
        return super.add(c);
    }

    @Override
    public Component add(final String name, final Component c) {
        if(!this.initializing) return this.componentsPanel.add(c);
        return super.add(c);
    }

    private void buildComponentList() {
        if(this.device_components == null){
            this.device_components = new Vector<Component>();
            final Stack<Container> search_stack = new Stack<Container>();
            search_stack.push(this);
            do{
                final Component[] curr_components = search_stack.pop().getComponents();
                if(curr_components == null) continue;
                for(final Component curr_component : curr_components){
                    if(curr_component instanceof DeviceComponent) this.device_components.addElement(curr_component);
                    else if(curr_component instanceof Container) search_stack.push((Container)curr_component);
                }
            }while(!search_stack.empty());
        }
    }

    public void copy() {
        this.buildComponentList();
        for(int i = 0; i < this.device_components.size(); i++){
            final DeviceComponent currComponent = (DeviceComponent)this.device_components.elementAt(i);
            final int intOffset = currComponent.getOffsetNid() - this.getOffsetNid();
            DeviceChannel.componentHash.put(new Integer(intOffset), currComponent.getFullData());
        }
    }

    @Override
    protected void displayData(final Descriptor data, final boolean is_on) {
        this.initial_state = is_on;
        if(this.checkB != null) this.checkB.setSelected(is_on);
        this.propagateState(is_on);
    }

    @Override
    public void fireUpdate(final String updateId, final Descriptor newExpr) {
        if(updateId == null || !this.updateIdentifier.equals(updateId)) return;
        String newVal = newExpr.toString();
        newVal = newVal.substring(1, newVal.length() - 1);
        if(this.showVal != null && this.showVal.equals(newVal)){
            this.setEnabledAll(true);
            final LayoutManager layout = this.getParent().getLayout();
            if(layout != null && (layout instanceof CardLayout)) ((CardLayout)layout).show(this.getParent(), this.showVal);
            // Display this component using showVal as constraint
        }else this.setEnabledAll(false);
    }

    public boolean getBorderVisible() {
        return this.borderVisible;
    }

    public int getColumns() {
        return this.columns;
    }

    public Container getContainer() {
        // JOptionPane.showMessageDialog(this, "GET CONTAINER", "", JOptionPane.INFORMATION_MESSAGE);
        return this.componentsPanel;
    }

    @Override
    protected Descriptor getData() {
        return null;
    }

    public boolean getInSameLine() {
        return this.inSameLine;
    }

    public String getLabelString() {
        return this.labelString;
    }

    public int getLines() {
        return this.lines;
    }

    public boolean getShowState() {
        return this.showState;
    }

    public String getShowVal() {
        return this.showVal;
    }

    @Override
    protected boolean getState() {
        if(!this.showState || this.checkB == null) return this.initial_state;
        return this.checkB.isSelected();
    }

    @Override
    protected void initializeData(final Descriptor data, final boolean is_on) {
        if(!this.showState){
            this.remove(this.checkB);
            this.checkB = null;
        }else{
            this.checkB.setText(this.labelString);
            this.checkB.setSelected(is_on);
            this.checkB.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(final ChangeEvent e) {
                    DeviceChannel.this.reportingChange = true;
                    DeviceChannel.this.reportStateChanged(DeviceChannel.this.checkB.isSelected());
                    DeviceChannel.this.reportingChange = false;
                    DeviceChannel.this.propagateState(DeviceChannel.this.checkB.isSelected());
                }
            });
        }
        this.propagateState(is_on);
    }

    public void paste() {
        this.buildComponentList();
        for(int i = 0; i < this.device_components.size(); i++){
            final DeviceComponent currComponent = (DeviceComponent)this.device_components.elementAt(i);
            final int intOffset = currComponent.getOffsetNid() - this.getOffsetNid();
            final Object currData = DeviceChannel.componentHash.get(new Integer(intOffset));
            if(currData != null) currComponent.dataChanged(currComponent.getOffsetNid(), currData);
        }
    }

    @Override
    public void postConfigure() {
        this.propagateState(this.curr_on);
    }

    public void propagate() {
        this.copy();
        final Container parent = this.getParent();
        final Component components[] = parent.getComponents();
        for(final Component component2 : components){
            if(component2 instanceof DeviceChannel && component2 != this){
                ((DeviceChannel)component2).paste();
            }
        }
    }

    private void propagateState(final boolean state) {
        this.buildComponentList();
        final int size = this.device_components.size();
        for(int i = 0; i < size; i++)
            ((DeviceComponent)this.device_components.elementAt(i)).setEnabled(state);
    }

    public void setBorderVisible(final boolean borderVisible) {
        this.borderVisible = borderVisible;
        if(borderVisible)
        // componentsPanel.setBorder(new LineBorder(Color.black, 1));
        this.setBorder(new LineBorder(Color.black, 1));
        else
        // componentsPanel.setBorder(null);
        this.setBorder(null);
    }

    public void setColumns(final int columns) {
        this.initializing = true;
        this.columns = columns;
        if(this.lines != 0 && columns != 0) this.componentsPanel.setLayout(new GridLayout(this.lines, columns));
        else this.componentsPanel.setLayout(new FlowLayout());
        this.initializing = false;
    }

    @Override
    public void setEnabled(final boolean state) {
        if(this.checkB != null) this.checkB.setEnabled(state);
        this.buildComponentList();
        if(this.device_components != null){
            final int size = this.device_components.size();
            for(int i = 0; i < size; i++)
                ((DeviceComponent)this.device_components.elementAt(i)).setEnabled(state);
        }
    }

    protected void setEnabledAll(final boolean enabled) {
        this.buildComponentList();
        if(this.device_components != null){
            final int size = this.device_components.size();
            for(int i = 0; i < size; i++){
                if(enabled) ((DeviceComponent)this.device_components.elementAt(i)).setEnable();
                else((DeviceComponent)this.device_components.elementAt(i)).setDisable();
            }
        }
    }

    public void setInSameLine(final boolean inSameLine) {
        this.inSameLine = inSameLine;
        if(this.checkB != null){
            this.remove(this.checkB);
            if(inSameLine) this.add(this.checkB, "West");
            else this.add(this.checkB, "North");
        }
    }

    public void setLabelString(final String labelString) {
        this.labelString = labelString;
        if(this.checkB != null) this.checkB.setText(labelString);
        this.redisplay();
    } // Do not accept interferences

    @Override
    public void setLayout(final LayoutManager layout) {
        if(!this.initializing) return;
        super.setLayout(layout);
    }

    public void setLines(final int lines) {
        this.initializing = true;
        this.lines = lines;
        if(lines != 0 && this.columns != 0) this.componentsPanel.setLayout(new GridLayout(lines, this.columns));
        else if(lines == 0 && this.columns == 0) this.componentsPanel.setLayout(new FlowLayout());
        else this.componentsPanel.setLayout(new BorderLayout());
        this.initializing = false;
    }

    public void setShowState(final boolean showState) {
        this.showState = showState;
    }

    public void setShowVal(final String showVal) {
        this.showVal = showVal;
    }

    @Override
    protected void stateChanged(final int offsetNid, final boolean state) {
        if(this.offsetNid != offsetNid || this.reportingChange) return;
        if(this.checkB != null) this.checkB.setSelected(state);
    }

    @Override
    protected boolean supportsState() {
        return this.showState;
    }
}

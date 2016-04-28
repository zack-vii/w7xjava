package devicebeans;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import jTraverser.Database;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Nid;

@SuppressWarnings("serial")
public abstract class DeviceComponent extends JPanel{
    public static final int DATA = 0, STATE = 1, DISPATCH = 2;

    // Copy-Paste management
    protected static Object copyData() {
        return null;
    }
    protected Nid        baseNid;
    protected int        baseNidNum     = 0;
    protected Descriptor curr_data, init_data;
    protected boolean    curr_on, init_on;
    protected boolean    editable       = true;
    private boolean      enabled        = true;
    protected String     identifier;
    private boolean      is_initialized = false;
    protected boolean    isHighlighted  = false;
    // Event handling in DW setup
    DeviceSetup          master         = null;
    public int           mode           = DeviceComponent.DATA;
    protected Nid        nidData;
    public int           offsetNid      = 0;
    Database             subtree;
    protected String     updateIdentifier;

    public void apply() throws Exception {
        if(!this.enabled) return;
        if(this.mode == DeviceComponent.DATA){
            this.curr_data = this.getData();
            /*            if(curr_data instanceof Path)
            {
                try {
                    curr_data = subtree.resolve((Path)curr_data, Tree.getContext());
                }catch(Exception exc){}
            }
              */
            if(this.editable && this.isDataChanged()){
                try{
                    this.subtree.putData(this.nidData, this.curr_data);
                }catch(final Exception e){
                    System.err.println("Error writing device data: " + e);
                    System.err.println(this.curr_data);
                    throw e;
                }
            }
        }
        if(this.mode != DeviceComponent.DISPATCH && this.supportsState()){
            this.curr_on = this.getState();
            try{
                this.subtree.setOn(this.nidData, this.curr_on);
            }catch(final Exception e){
                System.err.println("Error writing device state: " + e);
            }
        }
    }

    public void apply(final int currBaseNid) throws Exception {
        final Nid currNidData = new Nid(currBaseNid + this.offsetNid);
        if(!this.enabled) return;
        if(this.mode == DeviceComponent.DATA){
            this.curr_data = this.getData();
            if(this.editable)// && isDataChanged())
            {
                try{
                    this.subtree.putData(currNidData, this.curr_data);
                }catch(final Exception e){
                    System.err.println("Error writing device data: " + e);
                    System.err.println("at node: " + this.subtree.getInfo(currNidData).getFullPath());
                    System.err.println(this.curr_data);
                    throw e;
                }
            }
        }
        if(this.mode != DeviceComponent.DISPATCH && this.supportsState()){
            this.curr_on = this.getState();
            try{
                this.subtree.setOn(currNidData, this.curr_on);
            }catch(final Exception e){
                System.err.println("Error writing device state: " + e);
            }
        }
    }

    public void configure(final int baseNidNum) {
        this.baseNidNum = baseNidNum;
        this.nidData = new Nid(this.baseNidNum + this.offsetNid);
        this.baseNid = new Nid(this.baseNidNum);
        if(this.mode == DeviceComponent.DATA){
            try{
                this.init_data = this.curr_data = this.subtree.getData(this.nidData);
            }catch(final Exception e){
                this.init_data = this.curr_data = null;
            }
        }else this.init_data = null;
        // if(mode != DISPATCH)
        {
            try{
                this.init_on = this.curr_on = this.subtree.isOn(this.nidData);
            }catch(final Exception e){
                System.err.println("Error configuring device: " + e);
            }
        }
        if(!this.is_initialized){
            this.initializeData(this.curr_data, this.curr_on);
            this.is_initialized = true;
        }else this.displayData(this.curr_data, this.curr_on);
    }

    public void configure(final int baseNidNum, final boolean readOnly) {
        this.configure(baseNidNum);
    }

    protected void dataChanged(final int offsetNid, final Object data) {}

    protected abstract void displayData(Descriptor data, boolean is_on);

    public void fireUpdate(final String updateId, final Descriptor newExpr) {}

    public int getBaseNid() {
        return this.baseNidNum;
    }

    protected abstract Descriptor getData();

    // Get an object incuding all related info (will be data except for DeviceWaveform
    protected Object getFullData() {
        return this.getData();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getOffsetNid() {
        return this.offsetNid;
    }

    protected abstract boolean getState();

    Database getSubtree() {
        return this.subtree;
    }

    public String getUpdateId(final DeviceSetup master) {
        this.master = master;
        return this.updateIdentifier;
    }

    public String getUpdateIdentifier() {
        return this.updateIdentifier;
    }

    // To be subclassed
    protected abstract void initializeData(Descriptor data, boolean is_on);

    protected boolean isChanged() {
        try{
            final String initDecompiled = this.init_data.toString();
            final String currDecompiled = this.curr_data.toString();
            // System.out.println("Comparing " + initDecompiled + " " + currDecompiled);
            return !(initDecompiled.equals(currDecompiled));
        }catch(final Exception exc){
            return false;
        }
    }

    @SuppressWarnings("static-method")
    protected boolean isDataChanged() {
        return true;
    }

    protected boolean isStateChanged() {
        return !(this.init_on == this.curr_on);
    }

    protected void pasteData(final Object objData) {}

    void postApply() {}

    public void postConfigure() {}

    protected void redisplay() {
        Container curr_container;
        Component curr_component = this;
        do{
            curr_container = curr_component.getParent();
            curr_component = curr_container;
        }while((curr_container != null) && !(curr_container instanceof Window));
        /* if(curr_container != null)
        {
            ((Window)curr_container).pack();
            ((Window)curr_container).setVisible(true);
        }*/
    }

    public void reportDataChanged(final Object data) {
        if(this.master == null) return;
        this.master.propagateData(this.offsetNid, data);
    }

    public void reportStateChanged(final boolean state) {
        if(this.master == null) return;
        this.master.propagateState(this.offsetNid, state);
    }

    public void reset() {
        this.curr_data = this.init_data;
        this.curr_on = this.init_on;
        this.displayData(this.curr_data, this.curr_on);
    }

    public void setBaseNid(final int nid) {
        this.baseNidNum = nid;
    }

    public void setDisable() {
        this.enabled = false;
    }

    public void setEnable() {
        this.enabled = true;
    }

    public void setHighlight(final boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
        Component currParent, currGrandparent = this;
        do{
            currParent = currGrandparent;
            currGrandparent = currParent.getParent();
            if(currGrandparent instanceof JTabbedPane){
                final int idx = ((JTabbedPane)currGrandparent).indexOfComponent(currParent);
                ((JTabbedPane)currGrandparent).setForegroundAt(idx, isHighlighted ? Color.red : Color.black);
            }
        }while(!(currGrandparent instanceof DeviceSetup));
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public void setOffsetNid(final int nid) {
        this.offsetNid = nid;
    }

    void setSubtree(final Database subtree) {
        this.subtree = subtree;
    }

    public void setUpdateIdentifier(final String updateIdentifier) {
        this.updateIdentifier = updateIdentifier;
    }

    protected void stateChanged(final int offsetNid, final boolean state) {}

    @SuppressWarnings("static-method")
    protected boolean supportsState() {
        return false;
    }
}

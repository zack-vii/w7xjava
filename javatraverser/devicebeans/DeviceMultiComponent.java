package devicebeans;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

public abstract class DeviceMultiComponent extends DeviceComponent{
    private static final long serialVersionUID = -7778546697746673348L;

    protected static String getNameSeparator() {
        return "/";
    }
    protected String baseName = ".PARAMETERS";
    Nid              compNids[];

    protected abstract void addParameter(JPanel jp, Nid nidData);

    @Override
    public void apply() throws Exception {
        for(final Nid compNid : this.compNids)
            this.applyComponent(compNid);
    }

    @Override
    public void apply(final int currBaseNid) throws Exception {
        this.apply();
    }

    protected abstract void applyComponent(Nid nidData);

    @Override
    public void configure(final int baseNid) {
        try{
            final Nid prevDefNid = this.subtree.getDefault();
            this.subtree.setDefault(new Nid(baseNid));
            this.nidData = this.subtree.resolve(new Path(this.baseName));
            this.subtree.setDefault(prevDefNid);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(null, "Cannot resolve base nid: " + this.baseName);
            return;
        }
        this.baseNidNum = baseNid;
        this.baseNid = new Nid(baseNid);
        final int numComponents = this.getNumComponents(this.nidData);
        final String compNames[] = new String[numComponents];
        this.compNids = new Nid[numComponents];
        for(int i = 0; i < numComponents; i++){
            compNames[i] = this.getComponentNameAt(this.nidData, i);
            this.compNids[i] = this.getComponentNidAt(this.nidData, i);
        }
        final Hashtable<String, Vector<Nid>> compHash = new Hashtable<String, Vector<Nid>>();
        final String separator = DeviceMultiComponent.getNameSeparator();
        for(int i = 0; i < numComponents; i++){
            if(compNames[i] == null) continue;
            final StringTokenizer st = new StringTokenizer(compNames[i], separator);
            String firstPart = st.nextToken();
            if(!st.hasMoreTokens()) firstPart = "Default";
            Vector<Nid> nidsV = compHash.get(firstPart);
            if(nidsV == null){
                nidsV = new Vector<Nid>();
                compHash.put(firstPart, nidsV);
            }
            if(this.compNids != null) nidsV.addElement(this.compNids[i]);
        }
        this.setLayout(new BorderLayout());
        final JTabbedPane tabP = new JTabbedPane();
        this.add(tabP, "Center");
        final Enumeration groups = compHash.keys();
        while(groups.hasMoreElements()){
            final String currName = (String)groups.nextElement();
            final JPanel jp = new JPanel();
            tabP.add(currName, new JScrollPane(jp));
            final Vector currParams = compHash.get(currName);
            final int nParams = currParams.size();
            jp.setLayout(new GridLayout(nParams, 1));
            for(int i = 0; i < nParams; i++)
                this.addParameter(jp, (Nid)currParams.elementAt(i));
        }
    }

    @Override
    protected void displayData(final Descriptor data, final boolean is_on) {}

    public String getBaseName() {
        return this.baseName;
    }

    protected abstract String getComponentNameAt(Nid nidData, int idx);

    protected abstract Nid getComponentNidAt(Nid nidData, int idx);

    @Override
    protected Descriptor getData() {
        return null;
    }

    protected abstract int getNumComponents(Nid nidData);

    @Override
    protected boolean getState() {
        return false;
    }

    @Override
    protected void initializeData(final Descriptor data, final boolean is_on) {}

    @Override
    public void reset() {
        for(final Nid compNid : this.compNids)
            this.resetComponent(compNid);
    }

    protected abstract void resetComponent(Nid nidData);

    // return null when no more components
    public void setBaseName(final String baseName) {
        this.baseName = baseName;
    }
}

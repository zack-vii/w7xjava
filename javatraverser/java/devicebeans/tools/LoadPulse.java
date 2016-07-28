package devicebeans.tools;

/**
 * Load Configuration from a pulse file. It reads from file LoadPulse.conf. Each line
 * of the file is interpreted as a node reference, and the whole subtree will be read and stored
 * (as decompiled). The read values are then written into the model.
 * +
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import devicebeans.Database;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;
import mds.data.descriptor_s.TREENODE;
import mds.data.descriptor_s.TREENODE.Flags;

public class LoadPulse{
    static class NodeDescriptor{
        String  decompiled;
        boolean on, parentOn, noWriteModel;
        String  path;

        NodeDescriptor(final String path, final String decompiled, final boolean on, final boolean parentOn, final boolean noWriteModel){
            this.path = path;
            this.decompiled = decompiled;
            this.on = on;
            this.parentOn = parentOn;
            this.noWriteModel = noWriteModel;
        }

        String getDecompiled() {
            return this.decompiled;
        }

        String getPath() {
            return this.path;
        }

        boolean isNoWriteModel() {
            return this.noWriteModel;
        }

        boolean isOn() {
            return this.on;
        }

        boolean isParentOn() {
            return this.parentOn;
        }
    }
    static final String confFileName = "LoadPulse.conf";

    public static void main(final String args[]) {
        int outShot = -1;
        if(args.length < 2){
            System.out.println("Usage: java LoadPulse <experiment> <input shot> [<output shot>]");
            System.exit(0);
        }
        if(args.length >= 2){
            try{
                outShot = Integer.parseInt(args[2]);
            }catch(final Exception exc){
                outShot = -1;
            }
            final int shot = Integer.parseInt(args[1]);
            final LoadPulse lp = new LoadPulse();
            try{
                lp.load(args[0], shot, outShot);
            }catch(final Exception exc){
                System.out.println(exc);
            }
        }
    }
    int      numPMUnits   = 0;
    String   PCconnection = "";
    String   PVconnection = "";
    float    rTransfer    = 0;
    Database tree;

    int countPMUnits() {
        try{
            final Nid rootNid = new Path("\\PM_SETUP").toNid();
            final Nid unitsNid = new Nid(rootNid, +5);
            final Descriptor unitsData = this.tree.tdiEvaluate(unitsNid);
            final String units = unitsData.toString();
            final StringTokenizer st = new StringTokenizer(units, " ,\"");
            return st.countTokens();
        }catch(final Exception exc){
            System.out.println("Error getting num enabled PM: " + exc);
            return 0;
        }
    }

    void evaluatePCConnection() {
        try{
            final Nid rootNid = new Path("\\PC_SETUP").toNid();
            final Nid connectionNid = new Nid(rootNid, +2);
            final Descriptor connectionData = this.tree.tdiEvaluate(connectionNid);
            this.PCconnection = connectionData.toString();
        }catch(final Exception exc){
            System.out.println("Error getting PC connection: " + exc);
        }
    }

    void evaluatePVConnection() {
        try{
            final Nid rootNid = new Path("\\PV_SETUP").toNid();
            final Nid connectionNid = new Nid(rootNid, +2);
            final Descriptor connectionData = this.tree.tdiEvaluate(connectionNid);
            this.PVconnection = connectionData.toString();
        }catch(final Exception exc){
            System.out.println("Error getting PV connection: " + exc);
        }
    }

    void evaluateRTransfer() {
        try{
            final Nid rootNid = new Path("\\P_CONFIG").toNid();
            final Nid rTransferNid = new Nid(rootNid, +20);
            final Descriptor rTransferData = this.tree.tdiEvaluate(rTransferNid);
            this.rTransfer = rTransferData.toFloat();
        }catch(final Exception exc){
            System.out.println("Error getting R transfer: " + exc);
        }
    }

    Vector<NodeDescriptor> getNodes(final String experiment, final int shot) throws Exception {
        final Vector<NodeDescriptor> nodesV = new Vector<NodeDescriptor>();
        this.tree = new Database(experiment, shot);
        this.tree.open();
        final BufferedReader br = new BufferedReader(new FileReader(LoadPulse.confFileName));
        String basePathLine;
        String currPath = "";
        String outPath = null;
        final Nid defNid = this.tree.getDefault();
        while((basePathLine = br.readLine()) != null){
            Nid currNid;
            if(basePathLine.trim().equals("")) continue;
            System.out.println(basePathLine);
            String basePath = "";
            try{
                final StringTokenizer st = new StringTokenizer(basePathLine, " ");
                basePath = st.nextToken();
                currNid = new Path(basePath).toNid();
                outPath = null;
                if(st.hasMoreTokens()){
                    final String next = st.nextToken();
                    if(next.toUpperCase().equals("STATE")) // If only state has to be retrieved
                    {
                        final Flags flags = new Flags(currNid.getNciFlags());
                        currPath = currNid.getNciFullPath();
                        try{
                            nodesV.addElement(new NodeDescriptor(currPath, null, flags.isOn(), flags.isParentOn(), flags.isNoWriteModel() || flags.isWriteOnce()));
                        }catch(final Exception exc){
                            System.out.println("Error reading state of " + currPath + ": " + exc);
                        }
                        continue;
                    }
                    outPath = next.toUpperCase();
                }
                this.tree.setDefault(currNid);
                Nid[] nidsNumeric = this.tree.getWild(TREENODE.USAGE_NUMERIC);
                if(nidsNumeric == null) nidsNumeric = new Nid[0];
                Nid[] nidsText = this.tree.getWild(TREENODE.USAGE_TEXT);
                if(nidsText == null) nidsText = new Nid[0];
                Nid[] nidsSignal = this.tree.getWild(TREENODE.USAGE_SIGNAL);
                if(nidsSignal == null) nidsSignal = new Nid[0];
                Nid[] nidsStruct = this.tree.getWild(TREENODE.USAGE_STRUCTURE);
                if(nidsStruct == null) nidsStruct = new Nid[0];
                //// Get also data from subtree root
                int addedLen;
                try{
                    this.tree.getData(currNid);
                    addedLen = 1;
                }catch(final Exception exc){
                    addedLen = 0;
                }
                final Nid nids[] = new Nid[nidsNumeric.length + nidsText.length + nidsSignal.length + nidsStruct.length + addedLen];
                if(addedLen > 0) nids[nidsNumeric.length + nidsText.length + nidsSignal.length + nidsStruct.length] = currNid;
                ///////////////////////
                int j = 0;
                for(final Nid element : nidsNumeric)
                    nids[j++] = element;
                for(final Nid element : nidsText)
                    nids[j++] = element;
                for(final Nid element : nidsSignal)
                    nids[j++] = element;
                for(final Nid element : nidsStruct)
                    nids[j++] = element;
                this.tree.setDefault(defNid);
                for(int i = 0; i < nids.length; i++){
                    final Flags flags = new Flags(nids[i].getNciFlags());
                    currPath = nids[i].getNciFullPath();
                    if(i == (nids.length - 1))// If IT IS the node described in LoadPulse.congf (and not any descendant)
                    {
                        if(outPath != null){
                            System.out.println(currPath + " --> " + outPath);
                            currPath = outPath;
                        }else System.out.println(currPath);
                    }else System.out.println(currPath);
                    try{
                        Descriptor currData;
                        try{
                            currData = this.tree.getData(nids[i]);
                        }catch(final Exception exc){
                            currData = null;
                        }
                        if(currData != null){
                            final String currDecompiled = currData.toString();
                            nodesV.addElement(new NodeDescriptor(currPath, currDecompiled, flags.isOn(), flags.isParentOn(), flags.isNoWriteModel() || flags.isWriteOnce()));
                        }else nodesV.addElement(new NodeDescriptor(currPath, null, flags.isOn(), flags.isParentOn(), flags.isNoWriteModel() || flags.isWriteOnce()));
                    }catch(final Exception exc){
                        nodesV.addElement(new NodeDescriptor(currPath, null, flags.isOn(), flags.isParentOn(), flags.isNoWriteModel() || flags.isWriteOnce()));
                    }
                }
            }catch(final Exception exc){
                System.out.println("Error reading " + basePath + ": " + exc);
            }
        }
        this.numPMUnits = this.countPMUnits();
        this.evaluatePCConnection();
        this.evaluatePVConnection();
        this.evaluateRTransfer();
        this.tree.close();
        br.close();
        return nodesV;
    }

    public String getPCConnection() {
        return this.PCconnection;
    }

    public int getPMUnits() {
        return this.numPMUnits;
    }

    public String getPVConnection() {
        return this.PVconnection;
    }

    public float getRTransfer() {
        return this.rTransfer;
    }

    void getSetup(final String experiment, final int shot, final Hashtable<String, String> setupHash, final Hashtable<String, Boolean> setupOnHash) throws Exception {
        final Vector<NodeDescriptor> nodesV = this.getNodes(experiment, shot);
        int i;
        NodeDescriptor currNode;
        for(i = 0; i < nodesV.size(); i++){
            currNode = nodesV.elementAt(i);
            final String decompiled = currNode.getDecompiled();
            if(decompiled != null){
                try{
                    setupHash.put(currNode.getPath(), currNode.getDecompiled());
                }catch(final Exception exc){
                    System.out.println("Internal error in LoadPulse.getSetup(): " + exc);
                }
            }
            try{
                setupOnHash.put(currNode.getPath(), new Boolean(currNode.isOn()));
            }catch(final Exception exc){
                System.out.println("Internal error in LoadPulse.getSetup(): " + exc);
            }
        }
    }

    // Load setup and expands path names into abs path names with reference to pathRefShot
    public void getSetupWithAbsPath(final String experiment, final int shot, final int pathRefShot, final Hashtable<String, String> setupHash, final Hashtable<String, Boolean> setupOnHash) throws Exception {
        final Hashtable<String, String> currSetupHash = new Hashtable<String, String>();
        this.getSetup(experiment, shot, currSetupHash, setupOnHash);
        try{
            final Database tree = new Database(experiment, pathRefShot);
            tree.open();
            final Enumeration<String> pathNamesEn = currSetupHash.keys();
            while(pathNamesEn.hasMoreElements()){
                final String currPath = pathNamesEn.nextElement();
                try{
                    final Nid currNid = new Path(currPath).toNid();
                    final String currAbsPath = currNid.getNciFullPath();
                    setupHash.put(currAbsPath, currSetupHash.get(currPath));
                }catch(final Exception exc){
                    System.out.println("LoadSetup: Cannot expand path name " + currPath + " : " + exc);
                }
            }
            tree.close();
        }catch(final Exception exc){
            System.out.println("Cannot expand path names in LoadSetup: " + exc);
        }
    }

    void load(final String experiment, final int shot, final int outShot) throws Exception {
        System.out.println("LOAD PULSE");
        final Vector<NodeDescriptor> nodesV = this.getNodes(experiment, shot);
        try{
            this.tree = new Database(experiment, outShot);
            this.tree.open();
            for(int i = 0; i < nodesV.size(); i++){
                final NodeDescriptor currNode = nodesV.elementAt(i);
                try{
                    final Nid currNid = new Path(currNode.getPath()).toNid();
                    // if(currNode.isNoWriteModel()) System.out.println("NO WRITE MODEL!!" + currNode.getPath());
                    if(currNode.getDecompiled() != null && !currNode.isNoWriteModel()){
                        final Descriptor currData = this.tree.tdiCompile(currNode.getDecompiled());
                        this.tree.putData(currNid, currData);
                    }
                    if(currNode.isOn() && currNode.isParentOn()) this.tree.setOn(currNid, true);
                    else if(currNode.isParentOn()){
                        this.tree.setOn(currNid, false);
                    }
                }catch(final Exception exc){
                    System.out.println("Error writing " + currNode.getPath() + " in model: " + exc);
                }
            }
            this.tree.close();
        }catch(final Exception exc){
            System.out.println("FATAL ERROR: " + exc);
        }
    }
}

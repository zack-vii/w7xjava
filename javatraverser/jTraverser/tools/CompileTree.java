package jtraverser.tools;

import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import jtraverser.NodeInfo;
import mds.Database;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

public class CompileTree extends Thread{
    public static void main(final String args[]) {
        String filepath, provider, experiment;
        int shot = -1;
        if(args.length < 1){
            System.err.println("Usage: java CompileTree <file> <provider> <experiment> [<shot>]");
            System.exit(0);
        }
        filepath = args[0];
        provider = args[1];
        experiment = args[2];
        if(args.length > 3){
            try{
                shot = Integer.parseInt(args[3]);
            }catch(final Exception exc){
                System.err.println("Error Parsing shot number");
                System.exit(0);
            }
        }
        (new CompileTree(filepath, provider, experiment, shot)).start();
    }
    String         filepath, provider, experiment;
    Vector<String> newNames         = new Vector<String>();
    // originalNames and renamedNames keep info about nodes to be renamed
    Vector<String> renamedDevices   = new Vector<String>();
    Vector<String> renamedFieldNids = new Vector<String>();
    int            shot;
    Vector<Nid>    subtreeNids      = new Vector<Nid>();
    Database       tree;
    Vector<String> unresolvedExprV  = new Vector<String>();
    Vector<Nid>    unresolvedNidV   = new Vector<Nid>();

    CompileTree(final String filepath, final String provider, final String experiment, final int shot){
        this.filepath = filepath;
        this.provider = provider;
        this.experiment = experiment;
        this.shot = shot;
    }

    void recCompile(final Element node) {
        final String type = node.getNodeName();
        String name = node.getAttribute("NAME");
        // final String state = node.getAttribute("STATE");
        final String usageStr = node.getAttribute("USAGE");
        Nid nid = null;
        boolean success;
        try{
            final Nid parentNid = this.tree.getDefault();
            success = false;
            if(type.equals("data")){
                // final Element parentNode = (Element)node.getParentNode();
                final boolean isDeviceField = node.getNodeName().equals("field");
                final Text dataNode = (Text)node.getFirstChild();
                if(dataNode != null){
                    final String dataStr = dataNode.getData();
                    final Descriptor data = null;
                    {
                        this.unresolvedExprV.addElement(dataStr);
                        this.unresolvedNidV.addElement(this.tree.getDefault());
                    }
                    try{
                        nid = this.tree.getDefault();
                        if(isDeviceField){
                            Descriptor oldData;
                            try{
                                oldData = this.tree.getData(nid);
                            }catch(final Exception exc){
                                oldData = null;
                            }
                            if(oldData == null || !dataStr.equals(oldData.toString())) this.tree.putData(nid, data);
                        }else this.tree.putData(nid, data);
                    }catch(final Exception exc){
                        System.err.println("Error writing data to nid " + nid + ": " + exc);
                    }
                }
                return;
            }
            // First handle renamed nodes: they do not need to be created, but to be renamed
            final String originalDevice = node.getAttribute("DEVICE");
            final String deviceOffsetStr = node.getAttribute("OFFSET_NID");
            if(originalDevice != null && deviceOffsetStr != null && !originalDevice.equals("") && !deviceOffsetStr.equals("")){
                String newName;
                try{
                    newName = (this.tree.getInfo(parentNid)).getFullPath();
                }catch(final Exception exc){
                    System.err.println("Error getting renamed path: " + exc);
                    return;
                }
                if(type.equals("node")) newName += "." + name;
                else newName += ":" + name;
                this.newNames.addElement(newName);
                this.renamedDevices.addElement(originalDevice);
                this.renamedFieldNids.addElement(deviceOffsetStr);
                return; // No descendant for a renamed node
            }
            if(type.equals("node")){
                try{
                    if(name.length() > 12) name = name.substring(0, 12);
                    nid = this.tree.addNode(name, NodeInfo.USAGE_STRUCTURE);
                    if(usageStr != null && usageStr.equals("SUBTREE")) this.subtreeNids.addElement(nid);
                    this.tree.setDefault(nid);
                    success = true;
                }catch(final Exception e){
                    System.err.println("Error adding member " + name + " : " + e);
                }
            }else if(type.equals("member")){
                int usage = NodeInfo.USAGE_NONE;
                if(usageStr.equals("NONE")) usage = NodeInfo.USAGE_NONE;
                if(usageStr.equals("ACTION")) usage = NodeInfo.USAGE_ACTION;
                if(usageStr.equals("NUMERIC")) usage = NodeInfo.USAGE_NUMERIC;
                if(usageStr.equals("SIGNAL")) usage = NodeInfo.USAGE_SIGNAL;
                if(usageStr.equals("TASK")) usage = NodeInfo.USAGE_TASK;
                if(usageStr.equals("TEXT")) usage = NodeInfo.USAGE_TEXT;
                if(usageStr.equals("WINDOW")) usage = NodeInfo.USAGE_WINDOW;
                if(usageStr.equals("AXIS")) usage = NodeInfo.USAGE_AXIS;
                if(usageStr.equals("DISPATCH")) usage = NodeInfo.USAGE_DISPATCH;
                try{
                    if(name.length() > 12) name = name.substring(0, 12);
                    nid = this.tree.addNode(":" + name, usage);
                    this.tree.setDefault(nid);
                    success = true;
                }catch(final Exception e){
                    System.err.println("Error adding member " + name + " : " + e);
                }
            }else if(type.equals("device")){
                final String model = node.getAttribute("MODEL");
                // final NodeInfo info = this.tree.getInfo(parentNid, 0);
                try{
                    Thread.currentThread();
                    Thread.sleep(100);
                    nid = this.tree.addDevice(name.trim(), model);
                    if(nid != null){
                        this.tree.setDefault(nid);
                        success = true;
                    }
                }catch(final Exception exc){}
            }else if(type.equals("field")){
                try{
                    nid = this.tree.resolve(new Path(name));
                    this.tree.setDefault(nid);
                    success = true;
                }catch(final Exception e){
                    System.err.println("WARNING: device field  " + name + " not found in model : " + e);
                }
            }
            if(success){
                // tags
                final String tagsStr = node.getAttribute("TAGS");
                if(tagsStr != null && tagsStr.length() > 0){
                    int i = 0;
                    final StringTokenizer st = new StringTokenizer(tagsStr, ", ");
                    final String[] tags = new String[st.countTokens()];
                    while(st.hasMoreTokens())
                        tags[i++] = st.nextToken();
                    try{
                        this.tree.setTags(nid, tags);
                    }catch(final Exception exc){
                        System.err.println("Error adding tags " + tagsStr + " : " + exc);
                    }
                }
                // flags
                final String flagsStr = node.getAttribute("FLAGS");
                if(flagsStr != null && flagsStr.length() > 0){
                    int flags = 0;
                    final StringTokenizer st = new StringTokenizer(flagsStr, ", ");
                    while(st.hasMoreTokens()){
                        final String flag = st.nextToken();
                        if(flag.equals("WRITE_ONCE")) flags |= NodeInfo.WRITE_ONCE;
                        if(flag.equals("COMPRESSIBLE")) flags |= NodeInfo.COMPRESSIBLE;
                        if(flag.equals("COMPRESS_ON_PUT")) flags |= NodeInfo.COMPRESS_ON_PUT;
                        if(flag.equals("NO_WRITE_MODEL")) flags |= NodeInfo.NO_WRITE_MODEL;
                        if(flag.equals("NO_WRITE_SHOT")) flags |= NodeInfo.NO_WRITE_SHOT;
                    }
                    try{
                        this.tree.setFlags(nid, flags);
                    }catch(final Exception e){
                        System.err.println("Error setting flags to node " + name + " : " + e);
                    }
                }
                // state
                final String stateStr = node.getAttribute("STATE");
                if(stateStr != null && stateStr.length() > 0){
                    try{
                        if(stateStr.equals("ON")) this.tree.setOn(nid, true);
                        if(stateStr.equals("OFF")) this.tree.setOn(nid, false);
                    }catch(final Exception e){
                        System.err.println("Error setting state of node " + name + " : " + e);
                    }
                }
                // Descend
                final NodeList nodes = node.getChildNodes();
                for(int i = 0; i < nodes.getLength(); i++){
                    final Node currNode = nodes.item(i);
                    if(currNode.getNodeType() == Node.ELEMENT_NODE) // Only element nodes at this
                    this.recCompile((Element)currNode);
                }
            }
            this.tree.setDefault(parentNid);
        }catch(final Exception e){
            System.err.println("Internal error in recCompile: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            this.tree = new Database(this.provider, this.experiment, this.shot, Database.NEW);
        }catch(final Exception e){
            System.err.println("Error opening tree " + this.experiment + " : " + e);
            System.exit(0);
        }
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(this.filepath);
            final Element rootNode = document.getDocumentElement();
            final NodeList nodes = rootNode.getChildNodes();
            for(int i = 0; i < nodes.getLength(); i++){
                final Node currNode = nodes.item(i);
                if(currNode.getNodeType() == Node.ELEMENT_NODE) // Only element nodes at this
                this.recCompile((Element)currNode);
            }
        }catch(final SAXParseException e){ // Error generated by the parser
            System.err.println("\n** Parsing error" + ", line " + e.getLineNumber() + ", uri " + e.getSystemId());
            System.err.println("   " + e.getMessage());
            final Exception x = e.getException();
            if(x != null) x.printStackTrace();
            else e.printStackTrace();
        }catch(final SAXException se){ // Error generated during parsing
            final Exception e = se.getException();
            if(e != null) e.printStackTrace();
            else se.printStackTrace();
        }catch(final ParserConfigurationException e){ // Parser with specified options can't be built
            e.printStackTrace();
        }catch(final Exception e){ // I/O error
            e.printStackTrace();
        }
        // handle renamed nodes
        for(int i = 0; i < this.newNames.size(); i++){
            final String newName = this.newNames.elementAt(i);
            final String deviceName = this.renamedDevices.elementAt(i);
            final String offsetStr = this.renamedFieldNids.elementAt(i);
            try{
                final int deviceOffset = Integer.parseInt(offsetStr);
                final Nid deviceNid = this.tree.resolve(new Path(deviceName));
                final Nid renamedNid = new Nid(deviceNid, +deviceOffset);
                this.tree.renameNode(renamedNid, newName);
            }catch(final Exception e){
                System.err.println("Error renaming node of " + deviceName + " to " + newName + " : " + e);
            }
        }
        for(int i = 0; i < this.unresolvedNidV.size(); i++){
            Descriptor data = null;
            try{
                this.tree.setDefault(this.unresolvedNidV.elementAt(i));
                data = Database.tdiCompile(this.unresolvedExprV.elementAt(i));
            }catch(final Exception e){
                System.err.println("Error parsing expression " + this.unresolvedExprV.elementAt(i) + " : " + e);
            }
            try{
                this.tree.putData(this.unresolvedNidV.elementAt(i), data);
            }catch(final Exception e){
                System.err.println("Error writing data to nid " + this.unresolvedNidV.elementAt(i) + ": " + e);
            }
        }
        // Set subtrees (apparently this must be done at the end....
        for(int i = 0; i < this.subtreeNids.size(); i++){
            try{
                this.tree.setSubtree(this.subtreeNids.elementAt(i));
            }catch(final Exception e){
                System.err.println("Error setting subtree: " + e);
            }
        }
        try{
            this.tree.write();
            this.tree.close();
        }catch(final Exception e){
            System.err.println("Error closeing tree: " + e);
        }
    }
}

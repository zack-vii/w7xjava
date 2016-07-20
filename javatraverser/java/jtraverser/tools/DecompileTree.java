package jtraverser.tools;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import jtraverser.NodeInfo;
import mds.Database;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Conglom;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.TREENODE;
import mds.data.descriptor_s.TREENODE.Flags;

public class DecompileTree{
    public static final void main(final String args[]) {
        if(args.length < 1){
            System.out.println("Usage: java DecompileTree <provider> <treeName> [<shot>]");
            System.exit(0);
        }
        final String provider = args[0];
        final String expt = args[1];
        int shot = -1;
        if(args.length > 1){
            try{
                shot = Integer.parseInt(args[2]);
            }catch(final Exception exc){
                System.err.println("Invalid shot number");
                System.exit(-1);
            }
        }
        final Properties properties = System.getProperties();
        final String full = properties.getProperty("full");
        boolean isFull = true;
        if(full != null && full.equals("no")) isFull = false;
        String filename = properties.getProperty("out");
        if(filename == null) filename = args[1] + "@" + args[0];
        try{
            System.exit(new DecompileTree().decompile(provider, expt, shot, filename, isFull));
        }catch(final MdsException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
    private Database database = null;
    private Document document = null;
    public String    error    = null;

    public int decompile(final Database database, final Component parent, final boolean isFull) {
        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new FileNameExtensionFilter("XML file", "xml"));
        fc.setFileFilter(new FileNameExtensionFilter("XML document", "xml"));
        final int returnVal = fc.showSaveDialog(parent);
        if(returnVal != JFileChooser.APPROVE_OPTION) return 0;
        return this.decompile(database, fc.getSelectedFile().getAbsolutePath(), isFull);
    }

    public final int decompile(final Database database, String filename, final boolean isFull) {
        this.database = database;
        if(!filename.contains(".")) filename += ".xml";
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            final DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.newDocument(); // Create from whole cloth
        }catch(final Exception e){
            System.err.println(this.error = "Cannot instantiate a new Document: " + e);
            this.document = null;
            return -1;
        }
        final Element tree = this.document.createElement("tree");
        final Nid topNid = new Nid(0);
        Nid[] sons;
        try{
            sons = this.database.getSons(topNid);
        }catch(final Exception e){
            System.err.println(this.error = "Error setting sons: " + e);
            sons = new Nid[0];
        }
        for(final Nid son : sons){
            final Element docSon = this.document.createElement("node");
            tree.appendChild(docSon);
            this.recDecompile(son, docSon, false, isFull);
        }
        Nid[] members;
        try{
            members = this.database.getMembers(topNid);
        }catch(final Exception e){
            System.err.println(this.error = "Error setting members: " + e);
            members = new Nid[0];
        }
        for(final Nid member : members){
            Element docMember = null;
            try{
                final NodeInfo info = this.database.getInfo(member);
                if(info.getUsage() == TREENODE.USAGE_DEVICE) docMember = this.document.createElement("device");
                if(info.getUsage() == TREENODE.USAGE_COMPOUND_DATA) docMember = this.document.createElement("compound_data");
                else docMember = this.document.createElement("member");
            }catch(final Exception e){
                System.err.println(this.error = e.toString());
            }
            tree.appendChild(docMember);
            this.recDecompile(member, docMember, false, isFull);
        }
        final TransformerFactory transFactory = TransformerFactory.newInstance();
        try{
            final Transformer transformer = transFactory.newTransformer();
            final DOMSource source = new DOMSource(tree);
            final File newXML = new File(filename);
            final FileOutputStream os = new FileOutputStream(newXML);
            try{
                final StreamResult result = new StreamResult(os);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(source, result);
            }finally{
                os.close();
            }
        }catch(final Exception e){
            System.err.println(this.error = e.toString());
        }
        return 0;
    }

    public final int decompile(final String provider, final String expt, final int shot, final String filename, final boolean isFull) throws MdsException {
        final Database database;
        database = new Database(provider, expt, shot, Database.READONLY);
        final int result = this.decompile(database, filename, isFull);
        database.close();
        return result;
    }

    private void recDecompile(final Nid nid, final Element node, final boolean isDeviceField, final boolean isFull) {
        try{
            final Nid prevNid = this.database.getDefault();
            this.database.setDefault(nid);
            NodeInfo info = null;
            final Flags flags;
            try{
                info = this.database.getInfo(nid);
                flags = info.getFlags();
            }catch(final Exception exc){
                System.err.println(this.error = "Error getting info for " + nid.getNciFullPath() + ": " + exc);
                return;
            }
            String[] tags;
            try{
                tags = this.database.getTags(nid);
            }catch(final Exception exc){
                tags = new String[0];
            }
            if(isDeviceField) // Handle device field display
            {
                Descriptor data = null;
                // TACON
                if(info.getName().endsWith("_GAIN")) System.out.println("TACON: " + info.getName());
                if(flags.isSetup() || isFull || info.getName().endsWith("_GAIN"))
                // if(info.isSetup() || isFull)
                {
                    try{
                        data = this.database.getData(nid);
                    }catch(final Exception exc){
                        data = null;
                    }
                }
                // Handle Possible non-device subtree. Never, never do that!!.....
                Nid sons[], members[];
                final Vector<Nid> subtreeNodes = new Vector<Nid>();
                try{
                    sons = this.database.getSons(nid);
                }catch(final Exception exc){
                    sons = new Nid[0];
                }
                for(final Nid son : sons){
                    try{
                        final NodeInfo currInfo = this.database.getInfo(son);
                        if(currInfo.getConglomerateElt() == 1) // Descendant NOT belonging to the device
                            subtreeNodes.addElement(son);
                    }catch(final Exception exc){}
                }
                final Vector<Nid> subtreeMembers = new Vector<Nid>();
                try{
                    members = this.database.getMembers(nid);
                }catch(final Exception exc){
                    members = new Nid[0];
                }
                for(final Nid member : members){
                    try{
                        final NodeInfo currInfo = this.database.getInfo(member);
                        if(currInfo.getConglomerateElt() == 1) // Descendant NOT belonging to the device
                            subtreeMembers.addElement(member);
                    }catch(final Exception exc){}
                }
                if((!flags.isOn() && flags.isParentOn()) || (flags.isOn() && !flags.isParentOn()) || (flags.isSetup() && data != null) || tags.length > 0 || subtreeNodes.size() > 0 || subtreeMembers.size() > 0 || isFull
                // TACON
                        || info.getName().endsWith("_GAIN")) // show it only at these conditions
                {
                    final Element fieldNode = this.document.createElement("field");
                    node.appendChild(fieldNode);
                    final int conglomerateElt = info.getConglomerateElt();
                    final Nid deviceNid = new Nid(nid, -conglomerateElt + 1);
                    NodeInfo deviceInfo = null;
                    try{
                        deviceInfo = this.database.getInfo(deviceNid);
                    }catch(final Exception exc){
                        System.err.println("Error getting device info: " + exc);
                        return;
                    }
                    final String devicePath = deviceInfo.getFullPath();
                    final String fieldPath = info.getFullPath();
                    if(fieldPath.startsWith(devicePath)) // if the field has not been renamed
                    {
                        fieldNode.setAttribute("NAME", fieldPath.substring(devicePath.length(), fieldPath.length()));
                        if(!flags.isOn() && flags.isParentOn()) fieldNode.setAttribute("STATE", "OFF");
                        if(flags.isOn() && !flags.isParentOn()) fieldNode.setAttribute("STATE", "ON");
                        if(tags.length > 0){
                            String tagList = "";
                            for(int i = 0; i < tags.length; i++)
                                tagList += (i == tags.length - 1) ? tags[i] : (tags[i] + ",");
                            fieldNode.setAttribute("TAGS", tagList);
                        }
                        if(data != null){
                            final Element dataNode = this.document.createElement("data");
                            final Text dataText = this.document.createTextNode(data.toString());
                            dataNode.appendChild(dataText);
                            fieldNode.appendChild(dataNode);
                        }
                    }
                }
                // Display possible non device subtrees
                for(int i = 0; i < subtreeNodes.size(); i++){
                    final Element currNode = this.document.createElement("node");
                    this.recDecompile(subtreeNodes.elementAt(i), currNode, false, isFull);
                }
                for(int i = 0; i < subtreeMembers.size(); i++){
                    final Element currNode = this.document.createElement("member");
                    this.recDecompile(subtreeMembers.elementAt(i), currNode, false, isFull);
                }
            } // End management of device fields
            else{
                node.setAttribute("NAME", info.getName());
                if(info.getUsage() == TREENODE.USAGE_DEVICE || info.getUsage() == TREENODE.USAGE_COMPOUND_DATA){
                    Conglom deviceData = null;
                    try{
                        deviceData = (Conglom)this.database.getData(nid);
                        final String model = deviceData.getModel().toString();
                        node.setAttribute("MODEL", model.substring(1, model.length() - 1));
                    }catch(final Exception exc){
                        System.err.println("Error reading device data: " + exc);
                    }
                }
                final int conglomerateElt = info.getConglomerateElt();
                // Handle renamed device fields
                if(conglomerateElt > 1){
                    final Nid deviceNid = new Nid(nid, -conglomerateElt + 1);
                    NodeInfo deviceInfo = null;
                    try{
                        deviceInfo = this.database.getInfo(deviceNid);
                        node.setAttribute("DEVICE", deviceInfo.getFullPath());
                        node.setAttribute("OFFSET_NID", "" + conglomerateElt);
                    }catch(final Exception exc){
                        System.err.println("Error getting device info: " + exc);
                    }
                }
                // tags
                try{
                    tags = this.database.getTags(nid);
                }catch(final Exception exc){
                    System.err.println("Error getting tags: " + exc);
                    tags = new String[0];
                }
                if(tags.length > 0){
                    String tagList = "";
                    for(int i = 0; i < tags.length; i++)
                        tagList += (i == tags.length - 1) ? tags[i] : (tags[i] + ",");
                    node.setAttribute("TAGS", tagList);
                }
                // state
                if(!flags.isOn() && flags.isParentOn()) node.setAttribute("STATE", "OFF");
                if(flags.isOn() && !flags.isParentOn()) node.setAttribute("STATE", "ON");
                // flags
                String flagsStr = "";
                if(flags.isWriteOnce()) flagsStr += (flagsStr.length() > 0) ? ",WRITE_ONCE" : "WRITE_ONCE";
                if(flags.isCompressible()) flagsStr += (flagsStr.length() > 0) ? ",COMPRESSIBLE" : "COMPRESSIBLE";
                if(flags.isCompressOnPut()) flagsStr += (flagsStr.length() > 0) ? ",COMPRESS_ON_PUT" : "COMPRESS_ON_PUT";
                if(flags.isNoWriteModel()) flagsStr += (flagsStr.length() > 0) ? ",NO_WRITE_MODEL" : "NO_WRITE_MODEL";
                if(flags.isNoWriteShot()) flagsStr += (flagsStr.length() > 0) ? ",NO_WRITE_SHOT" : "NO_WRITE_SHOT";
                if(flagsStr.length() > 0) node.setAttribute("FLAGS", flagsStr);
                // usage
                final int usage = info.getUsage();
                if(usage != TREENODE.USAGE_STRUCTURE && usage != TREENODE.USAGE_DEVICE && usage != TREENODE.USAGE_COMPOUND_DATA){
                    String usageStr;
                    switch(usage){
                        default:
                            usageStr = "ANY";
                        case TREENODE.USAGE_ACTION:
                            usageStr = "ACTION";
                            break;
                        case TREENODE.USAGE_DISPATCH:
                            usageStr = "DISPATCH";
                            break;
                        case TREENODE.USAGE_NUMERIC:
                            usageStr = "NUMERIC";
                            break;
                        case TREENODE.USAGE_SIGNAL:
                            usageStr = "SIGNAL";
                            break;
                        case TREENODE.USAGE_TASK:
                            usageStr = "TASK";
                            break;
                        case TREENODE.USAGE_TEXT:
                            usageStr = "TEXT";
                            break;
                        case TREENODE.USAGE_WINDOW:
                            usageStr = "WINDOW";
                            break;
                        case TREENODE.USAGE_AXIS:
                            usageStr = "AXIS";
                            break;
                        case TREENODE.USAGE_SUBTREE:
                            usageStr = "SUBTREE";
                            break;
                    }
                    node.setAttribute("USAGE", usageStr);
                    // if(info.isSetup())
                    {
                        Descriptor data;
                        try{
                            data = this.database.getData(nid);
                        }catch(final Exception exc){
                            data = null;
                        }
                        if(data != null){
                            final Element dataNode = this.document.createElement("data");
                            final Text dataText = this.document.createTextNode(data.decompile());
                            dataNode.appendChild(dataText);
                            node.appendChild(dataNode);
                        }
                    }
                }
                // handle descendants, if not subtree
                if(usage != TREENODE.USAGE_SUBTREE){
                    Nid[] sons;
                    try{
                        sons = this.database.getSons(nid);
                    }catch(final Exception exc){
                        sons = new Nid[0];
                    }
                    if(info.getUsage() == TREENODE.USAGE_DEVICE || info.getUsage() == TREENODE.USAGE_COMPOUND_DATA){
                        // int numFields = info.getConglomerateNids() - 1;
                        final int numFields = info.getConglomerateNids();
                        for(int i = 1; i < numFields; i++)
                            this.recDecompile(new Nid(nid, +i), node, true, isFull);
                    }else{
                        for(final Nid son : sons){
                            final Element docSon = this.document.createElement("node");
                            node.appendChild(docSon);
                            this.recDecompile(son, docSon, false, isFull);
                        }
                        Nid[] members;
                        try{
                            members = this.database.getMembers(nid);
                        }catch(final Exception exc){
                            members = new Nid[0];
                        }
                        for(final Nid member : members){
                            Element docMember;
                            final NodeInfo currInfo = this.database.getInfo(member);
                            if(currInfo.getUsage() == TREENODE.USAGE_DEVICE) docMember = this.document.createElement((currInfo.getUsage() == TREENODE.USAGE_DEVICE) ? "device" : "compound_data");
                            else if(currInfo.getUsage() == TREENODE.USAGE_COMPOUND_DATA) docMember = this.document.createElement("compound_data");
                            else docMember = this.document.createElement("member");
                            node.appendChild(docMember);
                            this.recDecompile(member, docMember, false, isFull);
                        }
                    }
                }
            }
            this.database.setDefault(prevNid);
        }catch(final Exception exc){
            System.err.println(exc);
        }
    }
}

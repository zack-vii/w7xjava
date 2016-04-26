package jTraverser.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;
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
import jTraverser.Database;
import jTraverser.NodeInfo;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Conglom;
import mds.data.descriptor_s.Nid;

public class DecompileTree{
    static Document document;
    static Database mdsTree;

    public static void main(final String args[]) {// TODO
        if(args.length < 1){
            System.out.println("Usage: java DecompileTree <provider> <treeName> [<shot>]");
            System.exit(0);
        }
        final String provider = args[0];
        final String treeName = args[1];
        long shot = -1;
        if(args.length > 1){
            try{
                shot = Long.parseLong(args[2]);
            }catch(final Exception exc){
                System.err.println("Invalid shot number");
                System.exit(-1);
            }
        }
        final Properties properties = System.getProperties();
        final String full = properties.getProperty("full");
        boolean isFull = true;
        if(full != null && full.equals("no")) isFull = false;
        String outName = properties.getProperty("out");
        if(outName == null) outName = args[1] + "@" + args[0] + ".xml";
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            final DocumentBuilder builder = factory.newDocumentBuilder();
            DecompileTree.document = builder.newDocument(); // Create from whole cloth
        }catch(final Exception e){
            System.err.println("Cannot instantiate a new Document: " + e);
            DecompileTree.document = null;
            System.exit(-1);
        }
        final Element tree = DecompileTree.document.createElement("tree");
        try{
            DecompileTree.mdsTree = new Database(provider, treeName, shot, Database.READONLY);
        }catch(final MdsException e){
            System.err.println("Cannot open tree: " + e);
            System.exit(-1);
        }
        final Nid topNid = new Nid(0);
        Nid[] sons;
        try{
            sons = DecompileTree.mdsTree.getSons(topNid);
        }catch(final Exception e){
            System.err.println("Error setting sons: " + e);
            sons = new Nid[0];
        }
        for(final Nid son : sons){
            final Element docSon = DecompileTree.document.createElement("node");
            tree.appendChild(docSon);
            DecompileTree.recDecompile(son, docSon, false, isFull);
        }
        Nid[] members;
        try{
            members = DecompileTree.mdsTree.getMembers(topNid);
        }catch(final Exception e){
            System.err.println("Error setting members: " + e);
            members = new Nid[0];
        }
        for(final Nid member : members){
            Element docMember = null;
            try{
                final NodeInfo info = DecompileTree.mdsTree.getInfo(member);
                if(info.getUsage() == NodeInfo.USAGE_DEVICE) docMember = DecompileTree.document.createElement("device");
                if(info.getUsage() == NodeInfo.USAGE_COMPOUND_DATA) docMember = DecompileTree.document.createElement("compound_data");
                else docMember = DecompileTree.document.createElement("member");
            }catch(final Exception e){
                System.err.println(e);
            }
            tree.appendChild(docMember);
            DecompileTree.recDecompile(member, docMember, false, isFull);
        }
        final TransformerFactory transFactory = TransformerFactory.newInstance();
        try{
            final Transformer transformer = transFactory.newTransformer();
            final DOMSource source = new DOMSource(tree);
            final File newXML = new File(outName);
            final FileOutputStream os = new FileOutputStream(newXML);
            final StreamResult result = new StreamResult(os);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        }catch(final Exception e){
            System.err.println(e);
        }
        System.exit(0);
    }

    static void recDecompile(final Nid nid, final Element node, final boolean isDeviceField, final boolean isFull) {
        try{
            final Nid prevNid = DecompileTree.mdsTree.getDefault();
            DecompileTree.mdsTree.setDefault(nid);
            NodeInfo info = null;
            try{
                info = DecompileTree.mdsTree.getInfo(nid);
            }catch(final Exception exc){
                System.err.println("Error getting info for " + Database.getFullPath(nid.getValue()) + ": " + exc);
                return;
            }
            String[] tags;
            try{
                tags = DecompileTree.mdsTree.getTags(nid);
            }catch(final Exception exc){
                tags = new String[0];
            }
            if(isDeviceField) // Handle device field display
            {
                Descriptor data = null;
                // TACON
                if(info.getName().endsWith("_GAIN")) System.out.println("TACON: " + info.getName());
                if(info.isSetup() || isFull || info.getName().endsWith("_GAIN"))
                // if(info.isSetup() || isFull)
                {
                    try{
                        data = DecompileTree.mdsTree.getData(nid);
                    }catch(final Exception exc){
                        data = null;
                    }
                }
                // Handle Possible non-device subtree. Never, never do that!!.....
                Nid sons[], members[];
                final Vector<Nid> subtreeNodes = new Vector<Nid>();
                try{
                    sons = DecompileTree.mdsTree.getSons(nid);
                }catch(final Exception exc){
                    sons = new Nid[0];
                }
                for(final Nid son : sons){
                    try{
                        final NodeInfo currInfo = DecompileTree.mdsTree.getInfo(son);
                        if(currInfo.getConglomerateElt() == 1) // Descendant NOT belonging to the device
                        subtreeNodes.addElement(son);
                    }catch(final Exception exc){}
                }
                final Vector<Nid> subtreeMembers = new Vector<Nid>();
                try{
                    members = DecompileTree.mdsTree.getMembers(nid);
                }catch(final Exception exc){
                    members = new Nid[0];
                }
                for(final Nid member : members){
                    try{
                        final NodeInfo currInfo = DecompileTree.mdsTree.getInfo(member);
                        if(currInfo.getConglomerateElt() == 1) // Descendant NOT belonging to the device
                        subtreeMembers.addElement(member);
                    }catch(final Exception exc){}
                }
                if((!info.isOn() && info.isParentOn()) || (info.isOn() && !info.isParentOn()) || (info.isSetup() && data != null) || tags.length > 0 || subtreeNodes.size() > 0 || subtreeMembers.size() > 0 || isFull
                // TACON
                || info.getName().endsWith("_GAIN")) // show it only at these conditions
                {
                    final Element fieldNode = DecompileTree.document.createElement("field");
                    node.appendChild(fieldNode);
                    final int conglomerateElt = info.getConglomerateElt();
                    final Nid deviceNid = new Nid(nid, -conglomerateElt + 1);
                    NodeInfo deviceInfo = null;
                    try{
                        deviceInfo = DecompileTree.mdsTree.getInfo(deviceNid);
                    }catch(final Exception exc){
                        System.err.println("Error getting device info: " + exc);
                        return;
                    }
                    final String devicePath = deviceInfo.getFullPath();
                    final String fieldPath = info.getFullPath();
                    if(fieldPath.startsWith(devicePath)) // if the field has not been renamed
                    {
                        fieldNode.setAttribute("NAME", fieldPath.substring(devicePath.length(), fieldPath.length()));
                        if(!info.isOn() && info.isParentOn()) fieldNode.setAttribute("STATE", "OFF");
                        if(info.isOn() && !info.isParentOn()) fieldNode.setAttribute("STATE", "ON");
                        if(tags.length > 0){
                            String tagList = "";
                            for(int i = 0; i < tags.length; i++)
                                tagList += (i == tags.length - 1) ? tags[i] : (tags[i] + ",");
                            fieldNode.setAttribute("TAGS", tagList);
                        }
                        if(data != null){
                            final Element dataNode = DecompileTree.document.createElement("data");
                            final Text dataText = DecompileTree.document.createTextNode(data.toString());
                            dataNode.appendChild(dataText);
                            fieldNode.appendChild(dataNode);
                        }
                    }
                }
                // Display possible non device subtrees
                for(int i = 0; i < subtreeNodes.size(); i++){
                    final Element currNode = DecompileTree.document.createElement("node");
                    DecompileTree.recDecompile(subtreeNodes.elementAt(i), currNode, false, isFull);
                }
                for(int i = 0; i < subtreeMembers.size(); i++){
                    final Element currNode = DecompileTree.document.createElement("member");
                    DecompileTree.recDecompile(subtreeMembers.elementAt(i), currNode, false, isFull);
                }
            } // End management of device fields
            else{
                node.setAttribute("NAME", info.getName());
                if(info.getUsage() == NodeInfo.USAGE_DEVICE || info.getUsage() == NodeInfo.USAGE_COMPOUND_DATA){
                    Conglom deviceData = null;
                    try{
                        deviceData = (Conglom)DecompileTree.mdsTree.getData(nid);
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
                        deviceInfo = DecompileTree.mdsTree.getInfo(deviceNid);
                        node.setAttribute("DEVICE", deviceInfo.getFullPath());
                        node.setAttribute("OFFSET_NID", "" + conglomerateElt);
                    }catch(final Exception exc){
                        System.err.println("Error getting device info: " + exc);
                    }
                }
                // tags
                try{
                    tags = DecompileTree.mdsTree.getTags(nid);
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
                if(!info.isOn() && info.isParentOn()) node.setAttribute("STATE", "OFF");
                if(info.isOn() && !info.isParentOn()) node.setAttribute("STATE", "ON");
                // flags
                String flags = "";
                if(info.isWriteOnce()) flags += (flags.length() > 0) ? ",WRITE_ONCE" : "WRITE_ONCE";
                if(info.isCompressible()) flags += (flags.length() > 0) ? ",COMPRESSIBLE" : "COMPRESSIBLE";
                if(info.isCompressOnPut()) flags += (flags.length() > 0) ? ",COMPRESS_ON_PUT" : "COMPRESS_ON_PUT";
                if(info.isNoWriteModel()) flags += (flags.length() > 0) ? ",NO_WRITE_MODEL" : "NO_WRITE_MODEL";
                if(info.isNoWriteShot()) flags += (flags.length() > 0) ? ",NO_WRITE_SHOT" : "NO_WRITE_SHOT";
                if(flags.length() > 0) node.setAttribute("FLAGS", flags);
                // usage
                final int usage = info.getUsage();
                if(usage != NodeInfo.USAGE_STRUCTURE && usage != NodeInfo.USAGE_DEVICE && usage != NodeInfo.USAGE_COMPOUND_DATA){
                    String usageStr = "";
                    switch(usage){
                        case NodeInfo.USAGE_NONE:
                            usageStr = "NONE";
                            break;
                        case NodeInfo.USAGE_ACTION:
                            usageStr = "ACTION";
                            break;
                        case NodeInfo.USAGE_DISPATCH:
                            usageStr = "DISPATCH";
                            break;
                        case NodeInfo.USAGE_NUMERIC:
                            usageStr = "NUMERIC";
                            break;
                        case NodeInfo.USAGE_SIGNAL:
                            usageStr = "SIGNAL";
                            break;
                        case NodeInfo.USAGE_TASK:
                            usageStr = "TASK";
                            break;
                        case NodeInfo.USAGE_TEXT:
                            usageStr = "TEXT";
                            break;
                        case NodeInfo.USAGE_WINDOW:
                            usageStr = "WINDOW";
                            break;
                        case NodeInfo.USAGE_AXIS:
                            usageStr = "AXIS";
                            break;
                        case NodeInfo.USAGE_SUBTREE:
                            usageStr = "SUBTREE";
                            break;
                    }
                    node.setAttribute("USAGE", usageStr);
                    // if(info.isSetup())
                    {
                        Descriptor data;
                        try{
                            data = DecompileTree.mdsTree.getData(nid);
                        }catch(final Exception exc){
                            data = null;
                        }
                        if(data != null){
                            final Element dataNode = DecompileTree.document.createElement("data");
                            final Text dataText = DecompileTree.document.createTextNode(data.decompile());
                            dataNode.appendChild(dataText);
                            node.appendChild(dataNode);
                        }
                    }
                }
                // handle descendants, if not subtree
                if(usage != NodeInfo.USAGE_SUBTREE){
                    Nid[] sons;
                    try{
                        sons = DecompileTree.mdsTree.getSons(nid);
                    }catch(final Exception exc){
                        sons = new Nid[0];
                    }
                    if(info.getUsage() == NodeInfo.USAGE_DEVICE || info.getUsage() == NodeInfo.USAGE_COMPOUND_DATA){
                        // int numFields = info.getConglomerateNids() - 1;
                        final int numFields = info.getConglomerateNids();
                        for(int i = 1; i < numFields; i++)
                            DecompileTree.recDecompile(new Nid(nid, +i), node, true, isFull);
                    }else{
                        for(final Nid son : sons){
                            final Element docSon = DecompileTree.document.createElement("node");
                            node.appendChild(docSon);
                            DecompileTree.recDecompile(son, docSon, false, isFull);
                        }
                        Nid[] members;
                        try{
                            members = DecompileTree.mdsTree.getMembers(nid);
                        }catch(final Exception exc){
                            members = new Nid[0];
                        }
                        for(final Nid member : members){
                            Element docMember;
                            final NodeInfo currInfo = DecompileTree.mdsTree.getInfo(member);
                            if(currInfo.getUsage() == NodeInfo.USAGE_DEVICE) docMember = DecompileTree.document.createElement((currInfo.getUsage() == NodeInfo.USAGE_DEVICE) ? "device" : "compound_data");
                            else if(currInfo.getUsage() == NodeInfo.USAGE_COMPOUND_DATA) docMember = DecompileTree.document.createElement("compound_data");
                            else docMember = DecompileTree.document.createElement("member");
                            node.appendChild(docMember);
                            DecompileTree.recDecompile(member, docMember, false, isFull);
                        }
                    }
                }
            }
            DecompileTree.mdsTree.setDefault(prevNid);
        }catch(final Exception exc){
            System.err.println(exc);
        }
    }
}

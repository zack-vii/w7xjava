package devicebeans.tools;

// Stores a file as a binary array into a pulse file
// Arguments:
// 1) File name
// 2) Experiment
// 3) shot
// 4) Node name
import java.io.RandomAccessFile;
import jTraverser.Database;
import mds.data.descriptor_a.Int8Array;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

public class StoreFile{
    public static void main(final String args[]) {
        if(args.length < 3 || args.length > 5){
            System.err.println("Usage: java StoreFile <filename> <nodename> <experiment> [< shot> ");
            System.exit(0);
        }
        final String fileName = args[0];
        final String nodeName = args[1];
        final String experiment = args[2];
        int shot = -1;
        if(args.length == 4){
            try{
                shot = Integer.parseInt(args[3]);
            }catch(final Exception exc){
                System.err.println("Invalid shot number");
                System.exit(0);
            }
        }
        final Database tree;
        try{
            tree = new Database(experiment, shot, Database.READONLY);
        }catch(final Exception exc){
            System.err.println("Cannot open experiment " + experiment + " shot " + shot + ": " + exc);
            System.exit(0);
            return;
        }
        final Nid nid;
        try{
            nid = tree.resolve(new Path(nodeName));
        }catch(final Exception exc){
            System.err.println("Cannot find node " + nodeName);
            System.exit(0);
            return;
        }
        final byte[] serialized;
        try{
            final RandomAccessFile raf = new RandomAccessFile(fileName, "r");
            serialized = new byte[(int)raf.length()];
            raf.read(serialized);
            raf.close();
        }catch(final Exception exc){
            System.err.println("Cannot read file " + fileName + ": " + exc);
            System.exit(0);
            return;
        }
        try{
            tree.putData(nid, new Int8Array(serialized));
        }catch(final Exception exc){
            System.err.println("Error writing data in" + nodeName + ": " + exc);
            System.exit(0);
        }
    }
}

package devicebeans.tools;

// Stores a file as a binary array into a pulse file
// Arguments:
// 1) File name
// 2) Experiment
// 3) shot
// 4) Node name
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import mds.Database;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

public class LoadFile{
    public static void main(final String args[]) {
        if(args.length < 3 || args.length > 5){
            System.err.println("Usage: java StoreFile <filename> <nodename> <experiment> [< shot> ");
            System.exit(0);
        }
        final String fileName = args[0];
        final String nodeName = args[1];
        final String expt = args[2];
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
            tree = new Database(expt, shot, Database.READONLY);
        }catch(final Exception exc){
            System.err.println("Cannot open experiment " + expt + " shot " + shot + ": " + exc);
            System.exit(0);
            return;
        }
        final Nid nid;
        try{
            nid = new Path(nodeName).toNid();
        }catch(final Exception exc){
            System.err.println("Cannot find node " + nodeName);
            System.exit(0);
            return;
        }
        ByteBuffer serialized;
        try{
            serialized = tree.getData(nid).serialize();
        }catch(final Exception exc){
            System.err.println("Error reading data in" + nodeName + ": " + exc);
            System.exit(0);
            return;
        }
        try{
            final RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            while(serialized.hasRemaining())
                raf.write(serialized.get());
            raf.close();
        }catch(final Exception exc){
            System.err.println("Cannot read file " + fileName + ": " + exc);
            System.exit(0);
        }
    }
}

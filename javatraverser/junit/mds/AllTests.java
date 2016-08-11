package mds;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import mds.data.descriptor.Descriptor_A_Test;
import mds.data.descriptor.Descriptor_S_Test;
import mds.data.descriptor.Descriptor_Test;
import mds.data.descriptor_r.Function_Test;
import mds.mdsip.Connection;
import mds.mdsip.Connection_Test;

@RunWith(Suite.class)
@SuiteClasses({TREE_Test.class, Connection_Test.class, TreeShr_Test.class, MdsShr_Test.class, Function_Test.class, Descriptor_Test.class, Descriptor_S_Test.class, Descriptor_A_Test.class})
public class AllTests{
    public static final int    port = 8000;
    public static final String tree = "test";
    static{// clean up test files
        try{
            Runtime.getRuntime().exec("taskkill /im mdsip.exe /F").waitFor();
        }catch(final Exception e){
            System.err.println(e + ": " + e.getMessage());
        }
        final String paths = System.getenv(AllTests.tree + "_path").replace("~t", AllTests.tree);
        for(final String path : paths.split(";")){
            final File folder = new File(path);
            final String filesearchstring = String.format("%s_([0-9]+|model)\\..*", AllTests.tree);
            final File[] files = folder.listFiles(new FilenameFilter(){
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.matches(filesearchstring);
                }
            });
            for(final File file : files){
                if(!file.delete()){
                    System.err.println("Can't remove " + file.getAbsolutePath());
                }
            }
        }
    }

    public static Connection setUpBeforeClass() throws Exception {
        final Connection mds = Connection.sharedConnection("localhost:" + AllTests.port);
        mds.connect();
        if(!mds.isConnected()){
            System.out.println("Started new local mdsip server");
            final ProcessBuilder pb = new ProcessBuilder("mdsip", "-h", System.getenv("userprofile") + "/mdsip.hosts", "-m", "-p", String.valueOf(AllTests.port)).inheritIO();// , "-P", "ssh"
            final Map<String, String> env = pb.environment();
            env.put(AllTests.tree + "_path", System.getenv("TMP"));
            pb.start();
            if(!mds.connect()) throw new Exception("Could not connect to mdsip.");
        }
        return mds;
    }
}

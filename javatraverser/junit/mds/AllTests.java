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
import mds.mdsip.MdsIp;
import mds.mdsip.MdsIp.Provider;
import mds.mdsip.MdsIp_Test;

@RunWith(Suite.class)
@SuiteClasses({TREE_Test.class, MdsIp_Test.class, TreeShr_Test.class, MdsShr_Test.class, Function_Test.class, Descriptor_Test.class, Descriptor_S_Test.class, Descriptor_A_Test.class})
public class AllTests{
    private static boolean      local = false;
    public static final int     port  = 8000;
    public static final String  tree  = "test";
    private static final String user  = "user", password = "user", host = "192.168.137.4";// "localhost";
    static{// clean up test files
        if(AllTests.local){
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
    }

    public static MdsIp setUpBeforeClass() throws Exception {
        final Provider provider = new Provider(AllTests.host, AllTests.port, AllTests.user, AllTests.password);
        MdsIp mds = MdsIp.sharedConnection(provider);
        if(mds.isConnected()) return mds;
        if(AllTests.local){
            System.out.println("Started new local mdsip server");
            final ProcessBuilder pb = new ProcessBuilder("mdsip", "-h", System.getenv("userprofile") + "/mdsip.hosts", "-m", "-p", String.valueOf(AllTests.port)).inheritIO();// , "-P", "ssh"
            final Map<String, String> env = pb.environment();
            env.put(AllTests.tree + "_path", System.getenv("TMP"));
            pb.start();
            mds = MdsIp.sharedConnection(provider);
            if(mds.isConnected()) return mds;
        }
        throw new Exception("Could not connect to mdsip.");
    }
}

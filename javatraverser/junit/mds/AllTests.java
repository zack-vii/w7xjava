package mds;

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
@SuiteClasses({Connection_Test.class, TreeShr_Test.class, Function_Test.class, Descriptor_Test.class, Descriptor_S_Test.class, Descriptor_A_Test.class})
public class AllTests{
    public static final int    port = 8000;
    public static final String tree = "test";

    public static Connection setUpBeforeClass() throws Exception {
        final Connection mds = new Connection("localhost:" + AllTests.port);
        if(!mds.isConnected()){
            System.out.println("Started new local mdsip server");
            final ProcessBuilder pb = new ProcessBuilder("mdsip", "-m", "-p", String.valueOf(AllTests.port)).inheritIO();
            final Map<String, String> env = pb.environment();
            env.put(AllTests.tree + "_path", System.getenv("TMP"));
            pb.start();
            if(!mds.connect()) throw new Exception("Could not connect to mdsip.");
        }
        return mds;
    }
}

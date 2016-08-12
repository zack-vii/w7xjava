package mds;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import mds.data.descriptor_a.Float64Array;
import mds.data.descriptor_a.Uint64Array;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.NODE;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;
import mds.mdsip.MdsIp;

@SuppressWarnings("static-method")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TREE_Test{
    private static MdsIp   mds;
    private static final String expt = AllTests.tree;
    private static final int    shot = 7633;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        TREE_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        TREE_Test.mds.close();
    }

    @Test
    public void construct() throws Exception {
        NODE node, node1;
        final TREE tree = new TREE(TREE_Test.mds, TREE_Test.expt, TREE_Test.shot, TREE.NEW);
        Assert.assertTrue(tree.open().isOpen());
        Assert.assertTrue(tree.getContext().decompile().matches("Pointer\\(0x[a-f0-9]+\\)"));
        Assert.assertEquals("\\TEST::TOP.STRUCT", (node = tree.addNode("STRUCT", Nid.USAGE_STRUCTURE)).decompile());
        Assert.assertEquals("\\TEST::TOP.STRUCT:SIGNAL", (node = node.addNode("SIGNAL", Nid.USAGE_SIGNAL)).decompile());
        Assert.assertEquals("\\TEST::TOP:DATA", (node1 = tree.addNode("DATA", Nid.USAGE_SIGNAL)).putRecord(node).decompile());
        Assert.assertEquals(node.getNciMinPath(), node1.followReference().getNciMinPath());
        Assert.assertEquals("\\TEST::TOP:SIGNAL", node.setPath(":SIGNAL").toPath().toString());
        Assert.assertEquals("Build_Conglom(*, \"E1429\", *, *)", new Path(".STRUCT").addConglom("E1429", "E1429").getRecord().decompile());
        Assert.assertEquals("[DAT, SIG]", Arrays.toString(node.addTag("SIG").addTag("DAT").getTags()));
        Assert.assertEquals("[SIG, DAT]", Arrays.toString(node.setTags("SIG", "DAT").getTags()));
        Assert.assertEquals("[1.0, 2.0, 3.0]", Arrays.toString(node.putRecord(new Signal(new Float64Array(1., 2., 3.), null, new Uint64Array(1, 2, 3))).getData().toDoubleArray()));
        Assert.assertEquals(NODE.Flags.COMPRESS_ON_PUT | NODE.Flags.NO_WRITE_MODEL, node.clearFlags(-1).setFlags(NODE.Flags.COMPRESS_ON_PUT | NODE.Flags.NO_WRITE_MODEL).getNciFlags());
        tree.write();
        tree.quit();
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}
}

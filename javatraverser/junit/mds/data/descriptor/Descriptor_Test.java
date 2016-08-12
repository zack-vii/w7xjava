package mds.data.descriptor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import mds.AllTests;
import mds.MdsException;
import mds.data.descriptor_a.Int32Array;
import mds.data.descriptor_s.Nid;
import mds.mdsip.MdsIp;

@SuppressWarnings("static-method")
public final class Descriptor_Test{
    private static MdsIp mds;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        Descriptor_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        Descriptor_Test.mds.close();
    }

    @Before
    public final void setUp() throws Exception {}

    @After
    public final void tearDown() throws Exception {}

    @Test
    public final void tree() throws MdsException, InterruptedException {
        final int shot = 1;
        final String node = "\\TEST::TOP.TEST";
        final Nid nid;
        Assert.assertEquals(MdsException.TreeNORMAL, Descriptor_Test.mds.getInteger(String.format("treeopennew('%s',%d)", AllTests.tree, shot)));
        Assert.assertEquals(MdsException.TreeNORMAL, Descriptor_Test.mds.getInteger(String.format("tcl('add node %s/usage=any')", node.split("\\.", 2)[1])));
        Assert.assertEquals(1, Descriptor_Test.mds.getInteger(String.format("TreeShr->TreeWriteTree(ref('%s'),val(%d))", AllTests.tree, shot)));
        Assert.assertEquals(MdsException.MdsdclEXIT, Descriptor_Test.mds.getInteger(String.format("tcl('quit')")));
        Assert.assertEquals(MdsException.TreeNORMAL, Descriptor_Test.mds.getInteger(String.format("treeopen('%s',%d)", AllTests.tree, shot)));
        Assert.assertNotNull(nid = (Nid)Descriptor_Test.mds.getDescriptor(String.format("GetNci(%s,'NID_NUMBER')", node), Nid.class));
        Assert.assertEquals(MdsException.TreeNORMAL, Descriptor_Test.mds.getInteger("TreePutRecord", nid, new Int32Array(1, 2, 3, 4, 5, 6)));
        Assert.assertEquals("[1,2,3,4,5,6]", Descriptor_Test.mds.getDescriptor("GETNCI(1,'RECORD')").decompile());
        Assert.assertEquals(MdsException.TreeNORMAL, Descriptor_Test.mds.getInteger(String.format("treeclose('%s',%d)", AllTests.tree, shot)));
    }
}

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
import mds.mdsip.Connection;

@SuppressWarnings("static-method")
public final class Descriptor_Test{
    private static Connection mds;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        Descriptor_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        Descriptor_Test.mds.disconnect();
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
        Assert.assertEquals(265388041, Descriptor_Test.mds.mdsValue(String.format("treeopennew('%s',%d)", AllTests.tree, shot)).toInt());
        Assert.assertEquals(265388041, Descriptor_Test.mds.mdsValue(String.format("tcl('add node %s/usage=any')", node.split("\\.", 2)[1])).toInt());
        Assert.assertEquals(1, Descriptor_Test.mds.mdsValue(String.format("TreeShr->TreeWriteTree(ref('%s'),val(%d))", AllTests.tree, shot)).toInt());
        Assert.assertEquals(134348817, Descriptor_Test.mds.mdsValue(String.format("tcl('quit')")).toInt());
        Assert.assertEquals(265388041, Descriptor_Test.mds.mdsValue(String.format("treeopen('%s',%d)", AllTests.tree, shot)).toInt());
        Assert.assertNotNull(nid = (Nid)Descriptor_Test.mds.mdsValue(String.format("GetNci(%s,'NID_NUMBER')", node), Nid.class));
        Assert.assertEquals(265388041, Descriptor_Test.mds.mdsValue("TreePutRecord", new Descriptor[]{nid, new Int32Array(new int[]{1, 2, 3, 4, 5, 6})}).toInt());
        Assert.assertEquals("[1,2,3,4,5,6]", Descriptor_Test.mds.mdsValue("GETNCI(1,'RECORD')").decompile());
    }
}

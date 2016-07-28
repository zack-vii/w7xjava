package mds;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import mds.mdsip.Connection;

@SuppressWarnings("static-method")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TREE_Test{
    private static Connection   mds;
    private static TreeShr      treeshr;
    private static final String expt    = AllTests.tree;
    private static final int    model   = -1, shot = 3;
    private static final int    normal  = 265388041;
    private static final int    success = 265389633;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        TREE_Test.mds = AllTests.setUpBeforeClass();
        TREE_Test.treeshr = new TreeShr(TREE_Test.mds);
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        TREE_Test.mds.disconnect();
    }

    @Test
    public void construct() throws Exception {
        final TREE tree = new TREE(TREE_Test.mds, TREE_Test.expt, TREE_Test.shot, TREE.NEW);
        Assert.assertEquals(TREE_Test.normal, tree.open());
        Assert.assertTrue(tree.getContext().decompile().matches("Pointer\\(0x[a-f0-9]+\\)"));
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}
}

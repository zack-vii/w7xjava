package mds;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import jtraverser.NodeInfo;
import mds.data.descriptor_a.Int32Array;
import mds.mdsip.Connection;

@SuppressWarnings("static-method")
public class TreeShr_Test{
    private static Connection   mds;
    private static TreeShr      treeshr;
    private static final String expt = AllTests.tree;
    private static final int    shot = 1;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        TreeShr_Test.mds = AllTests.setUpBeforeClass();
        TreeShr_Test.treeshr = new TreeShr(TreeShr_Test.mds);
        TreeShr_Test.treeshr.treeOpenNew(AllTests.tree, TreeShr_Test.shot);
        TreeShr_Test.treeshr.treeAddNode("\\TEST::TOP.A", NodeInfo.USAGE_NUMERIC);
        TreeShr_Test.treeshr.treeAddNode("\\TEST::TOP.B", NodeInfo.USAGE_SIGNAL);
        TreeShr_Test.treeshr.treeAddNode("\\TEST::TOP.A.AA", NodeInfo.USAGE_SIGNAL);
        TreeShr_Test.treeshr.treeWriteTree(AllTests.tree, TreeShr_Test.shot);
        TreeShr_Test.treeshr.treeQuitTree(AllTests.tree, TreeShr_Test.shot);
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        TreeShr_Test.mds.disconnect();
    }

    @Before
    public void setUp() throws Exception {
        TreeShr_Test.treeshr.treeOpen(AllTests.tree, TreeShr_Test.shot, false);
    }

    @After
    public void tearDown() throws Exception {
        TreeShr_Test.treeshr.treeClose(AllTests.tree, TreeShr_Test.shot);
    }

    @Test
    public final void testTreeAddConglom() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeAddNode() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeAddTag() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeCreateTreeFiles() throws MdsException {
        Assert.assertEquals(265389633, TreeShr_Test.treeshr.treeCreateTreeFiles(AllTests.tree, TreeShr_Test.shot + 1, TreeShr_Test.shot));
    }

    @Test
    public final void testTreeCtx() throws MdsException {
        Assert.assertTrue(0 < TreeShr_Test.treeshr.treeCtx());
    }

    @Test
    public final void testTreeDeleteNodeExecute() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeDeleteNodeGetNid() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeDeleteNodeInitialize() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeGetCurrentShotId() throws MdsException {
        Assert.assertEquals(TreeShr_Test.shot, TreeShr_Test.treeshr.treeGetCurrentShotId(TreeShr_Test.expt));
    }

    @Test
    public final void testTreeGetDefaultNid() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeGetRecord() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeOpen() throws MdsException {
        Assert.assertEquals(265388041, TreeShr_Test.treeshr.treeOpen(TreeShr_Test.expt, TreeShr_Test.shot + 1, true));
    }

    @Test
    public final void testTreeOpenEdit() throws MdsException {
        Assert.assertEquals(265388041, TreeShr_Test.treeshr.treeOpenEdit(TreeShr_Test.expt, TreeShr_Test.shot + 1));
    }

    @Test
    public final void testTreeOpenNew() throws MdsException {
        Assert.assertEquals(265388041, TreeShr_Test.treeshr.treeOpenNew(TreeShr_Test.expt, TreeShr_Test.shot + 1));
    }

    @Test
    public final void testTreePutRecord() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treePutRecord(2, new Int32Array(new int[]{1, 2, 3, 4})));
    }

    @Test
    public final void testTreePutRow() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeQuitTree() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeRemoveNodesTags() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeRenameNode() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeSetCurrentShotId() throws MdsException {
        Assert.assertEquals(TreeShr_Test.shot, TreeShr_Test.treeshr.treeSetCurrentShotId(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void testTreeSetDefault() throws MdsException {
        Assert.assertEquals(265388041, TreeShr_Test.treeshr.treeSetDefault(1));
    }

    @Test
    public final void testTreeSetNciItm() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeSetSubtree() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeTurnOff() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeTurnOn() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTreeWriteTree() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }
}

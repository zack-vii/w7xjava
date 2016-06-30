package mds;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import jtraverser.NodeInfo;
import mds.data.descriptor_a.Float32Array;
import mds.data.descriptor_a.Uint64Array;
import mds.mdsip.Connection;

@SuppressWarnings("static-method")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TreeShr_Test{
    private static Connection   mds;
    private static TreeShr      treeshr;
    private static final String expt    = AllTests.tree;
    private static final int    shot    = 7357;
    private static final int    normal  = 265388041;
    private static final int    success = 265389633;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        TreeShr_Test.mds = AllTests.setUpBeforeClass();
        TreeShr_Test.treeshr = new TreeShr(TreeShr_Test.mds);
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        TreeShr_Test.mds.disconnect();
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public final void test000TreeOpenNew() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpenNew(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test001TreeSetCurrentShotId() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeSetCurrentShotId(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test002TreeGetCurrentShotId() throws MdsException {
        Assert.assertEquals(TreeShr_Test.shot, TreeShr_Test.treeshr.treeGetCurrentShotId(TreeShr_Test.expt));
    }

    @Test
    public final void test099TreeClose() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeClose(AllTests.tree, TreeShr_Test.shot));
    }

    @Test
    public final void test100TreeOpenEdit() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpenEdit(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test101TreeAddNode() throws MdsException {
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 1}, TreeShr_Test.treeshr.treeAddNode("\\TEST::TOP.A", NodeInfo.USAGE_SIGNAL));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 2}, TreeShr_Test.treeshr.treeAddNode("\\TEST::TOP.B", NodeInfo.USAGE_AXIS));
        Assert.assertEquals(NodeInfo.USAGE_SIGNAL, TreeShr_Test.mds.getInteger("GetNci(\\TEST::TOP.A,'USAGE')"));
        Assert.assertEquals(NodeInfo.USAGE_AXIS, TreeShr_Test.mds.getInteger("GetNci(\\TEST::TOP.B,'USAGE')"));
    }

    @Test
    public final void test102TreeAddConglom() throws MdsException {
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 3}, TreeShr_Test.treeshr.treeAddConglom("C", "E1429"));
    }

    @Test
    public final void test105TreeCtx() throws MdsException {
        Assert.assertTrue(0 < TreeShr_Test.treeshr.treeCtx());
    }

    @Test
    public final void test120TreeDeleteNodeInitialize() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void test121TreeDeleteNodeGetNid() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void test122TreeDeleteNodeExecute() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void test130TreeSetDefault() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetDefault(1));
    }

    @Test
    public final void test131TreeGetDefaultNid() throws MdsException {
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 1}, TreeShr_Test.treeshr.treeGetDefaultNid());
    }

    @Test
    public final void test149TreeAddTag() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeAddTag(2, "DEVICE"));
    }

    @Test
    public final void test150TreeWriteTree() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeWriteTree(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test151TreeFindTagWildDsc() throws MdsException {
        Assert.assertEquals("\\TEST::DEVICE", TreeShr_Test.treeshr.treeFindTagWildDsc("DEVICE"));
        Assert.assertEquals(2, TreeShr_Test.treeshr.treeFindTagWildDscNid());
        Assert.assertNull(TreeShr_Test.treeshr.treeFindTagWildDsc("***"));
        Assert.assertEquals("\\TEST::TOP", TreeShr_Test.treeshr.treeFindTagWildDsc("***"));
    }

    @Test
    public final void test152TreeRemoveNodesTags() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeRemoveNodesTags(2));
    }

    @Test
    public final void test153TreeTurnOff() throws MdsException {
        Assert.assertEquals(265392050, TreeShr_Test.treeshr.treeTurnOff(1));
    }

    @Test
    public final void test154TreeTurnOn() throws MdsException {
        Assert.assertEquals(265392050, TreeShr_Test.treeshr.treeTurnOn(1));
    }

    @Test
    public final void test160TreeBeginTimestampedSegment() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeBeginTimestampedSegment(1, new Float32Array(new float[]{0, 0, 0}), -1));
    }

    @Test
    public final void test161TreePutTimestampedSegment() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutTimestampedSegment(1, System.nanoTime(), new Float32Array(new float[]{1.f, 2.f, 3.f})));
    }

    @Test
    public final void test163TreePutRecord() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutRecord(1, null, 0));
    }

    @Test
    public final void test165TreeMakeTimestampedSegment() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeMakeTimestampedSegment(1, new Uint64Array(new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}), new Float32Array(new float[]{.0f, .0f, .0f, .0f, .0f, .0f, .0f, .0f, .0f, .0f}), -1, 0));
    }

    @Test
    public final void test166TreePutRow() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutRow(1, 1 << 20, System.nanoTime(), new Float32Array(new float[]{7.f})));
    }

    @Test
    public final void test170TreeGetRecord() throws MdsException {
        Assert.assertEquals(7f, TreeShr_Test.treeshr.treeGetRecord(1).toFloat(), 1e-9f);
    }

    @Test
    public final void test180TreeSetSubtree() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetSubtree(1));
    }

    @Test
    public final void test185TreeSetNciItm() throws MdsException {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void test190TreeRenameNode() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeRenameNode(2, "newB"));
    }

    @Test
    public final void test199TreeQuitTree() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeQuitTree(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test200TreeCreateTreeFiles() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeCreateTreeFiles(AllTests.tree, TreeShr_Test.shot + 1, TreeShr_Test.shot));
    }

    @Test
    public final void test201TreeOpen() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpen(TreeShr_Test.expt, TreeShr_Test.shot + 1, true));
    }
}

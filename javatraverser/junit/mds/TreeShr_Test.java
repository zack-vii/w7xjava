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
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.Float32Array;
import mds.data.descriptor_a.Uint64Array;
import mds.data.descriptor_r.Function;
import mds.data.descriptor_s.Pointer;
import mds.mdsip.Connection;

@SuppressWarnings("static-method")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TreeShr_Test{
    private static Connection   mds;
    private static TreeShr      treeshr;
    private static final String expt    = AllTests.tree;
    private static final int    shot    = 1;
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
    public final void test090TreeClose() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeClose(AllTests.tree, TreeShr_Test.shot));
    }

    @Test
    public final void test091TreeCleanDatafile() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeCleanDatafile(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test100TreeOpenEdit() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpenEdit(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test101TreeAddNode() throws MdsException {
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 1}, TreeShr_Test.treeshr.treeAddNode("A", NodeInfo.USAGE_SIGNAL));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 2}, TreeShr_Test.treeshr.treeAddNode("B", NodeInfo.USAGE_SUBTREE));
        Assert.assertEquals(NodeInfo.USAGE_SIGNAL, TreeShr_Test.mds.getInteger("GetNci(\\TEST::TOP:A,'USAGE')"));
        Assert.assertEquals(NodeInfo.USAGE_SUBTREE, TreeShr_Test.mds.getInteger("GetNci(\\TEST::TOP:B,'USAGE')"));
    }

    @Test
    public final void test102TreeAddConglom() throws MdsException {
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 3}, TreeShr_Test.treeshr.treeAddConglom("C", "E1429"));
    }

    @Test
    public final void test110TreeWriteTree() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeWriteTree(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test112TreeCreateTreeFiles() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeCreateTreeFiles(AllTests.tree, TreeShr_Test.shot + 1, TreeShr_Test.shot));
    }

    @Test
    public final void test115TreeContext() throws MdsException {
        final String deco = TreeShr_Test.treeshr.treeCtx().decompile();
        Assert.assertTrue(deco, deco.matches("Pointer\\(0x[a-f0-9]+\\)"));
        final Pointer save = TreeShr_Test.treeshr.treeSaveContext();
        // Assert.assertEquals(TreeShr_Test.mds.decompile(save), save.decompile()); //TODO: inconsistency between unix and windows mdsip server
        Assert.assertTrue(save.decompile().matches("Pointer\\(0x[a-f0-9]+\\)"));
        Assert.assertArrayEquals(save.serializeArray(), TreeShr_Test.mds.mdsValue("_b=*;_s=MdsShr->MdsSerializeDscOut(xd((_a=*;_s=MdsShr->MdsSerializeDscIn(ref($),xd(_a));_a;)),xd(_b));_b", new Descriptor[]{save.serializeDsc()}, Descriptor.class).toByteArray());
        // Assert.assertArrayEquals(TreeShr_Test.mds.mdsValue("$", new Descriptor[]{save}, Descriptor.class).serializeArray(), save.serializeArray());// TODO: works with fix in ProcessMessage.c (#559 zack-vii:zck_mdsip_processmessage_pointer)
        String line0, line1;
        System.out.println(line0 = TreeShr_Test.mds.getString("_t='';_s=TCL('show db',_t);_t"));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpen(AllTests.tree, TreeShr_Test.shot + 1, true));
        System.out.println(line1 = TreeShr_Test.mds.getString("_t='';_s=TCL('show db',_t);_t"));
        Assert.assertTrue(line1, line1.endsWith("001" + line0.substring(3, line0.length())));
        Assert.assertTrue(0 != TreeShr_Test.treeshr.treeRestoreContext(save));
        System.out.println(line1 = TreeShr_Test.mds.getString("_t='';_s=TCL('show db',_t);_t"));
        Assert.assertTrue(line1, line0.startsWith(line0));
    }

    @Test
    public final void test121TreeDeleteNodeInitialize() throws MdsException {
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 37}, TreeShr_Test.treeshr.treeDeleteNodeInitialize(3));
    }

    @Test
    public final void test122TreeDeleteNodeGetNid() throws MdsException {
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 3}, TreeShr_Test.treeshr.treeDeleteNodeGetNid(0));
    }

    @Test
    public final void test123TreeDeleteNodeExecute() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeDeleteNodeExecute());
    }

    @Test
    public final void test130TreeAddTag() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeAddTag(2, "DEVICE"));
        Assert.assertEquals("B", TreeShr_Test.mds.getString("Trim(GetNci(\\DEVICE,'NODE_NAME'))"));
    }

    @Test
    public final void test131TreeFindTagWildDsc() throws MdsException {
        Assert.assertEquals("\\TEST::DEVICE", TreeShr_Test.treeshr.treeFindTagWild("DEVICE"));
        Assert.assertEquals(2, TreeShr_Test.treeshr.treeFindTagWildNid());
        Assert.assertNull(TreeShr_Test.treeshr.treeFindTagWild("***"));
        Assert.assertEquals("\\TEST::TOP", TreeShr_Test.treeshr.treeFindTagWild("***"));
    }

    @Test
    public final void test132TreeRemoveNodesTags() throws MdsException {
        Assert.assertEquals(265388152, TreeShr_Test.treeshr.treeRemoveNodesTags(2));
        Assert.assertNull(TreeShr_Test.treeshr.treeFindTagWild("DEVICE"));
    }

    @Test
    public final void test140TreeSetDefault() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetDefault(1));
    }

    @Test
    public final void test141TreeGetDefaultNid() throws MdsException {
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, 1}, TreeShr_Test.treeshr.treeGetDefaultNid());
    }

    @Test
    public final void test150TreeSetXNci() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeSetXNci(1, "myattr", Function.$HBAR()));
    }

    @Test
    public final void test151TreeGetXNci() throws MdsException {
        Assert.assertEquals("myattr", TreeShr_Test.treeshr.treeGetXNci(1).toString());
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
    public final void test155TreeSetNciItm() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetNciItm(1, true, 0x7FFFFFFF));
        Assert.assertEquals(0x7FFFFFFF, TreeShr_Test.mds.getInteger("GetNci(1,'GET_FLAGS')"));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetNciItm(1, false, -0x10400 - 1));
        Assert.assertEquals(66560, TreeShr_Test.mds.getInteger("GetNci(1,'GET_FLAGS')"));
    }

    @Test
    public final void test161TreeBeginTimestampedSegment() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeBeginTimestampedSegment(1, new Float32Array(new float[3]), -1));
    }

    @Test
    public final void test162TreePutTimestampedSegment() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutTimestampedSegment(1, System.nanoTime(), new Float32Array(new float[]{.1f, .2f, .3f})));
    }

    @Test
    public final void test163TreePutRecord() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutRecord(1, null, 0));
    }

    @Test
    public final void test164TreeMakeTimestampedSegment() throws MdsException {
        final long t0 = 1000000000000l;
        final long[] dim = new long[10];
        for(int i = 0; i < 10; i++)
            dim[i] = t0 + i * 1000000l;
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeMakeTimestampedSegment(1, new Uint64Array(dim), new Float32Array(new float[]{.0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, Float.NaN}), -1, 9));
    }

    @Test
    public final void test165TreePutRow() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutRow(1, 1 << 10, 1000010000000l, new Float32Array(new float[]{.9f})));
    }

    @Test
    public final void test170TreeSetTimeContext_TreeGetRecord() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeSetTimeContext(1000001000000l, 1000007000000l, 2000000l));
        Assert.assertArrayEquals(new float[]{.3f, .5f, .7f}, TreeShr_Test.treeshr.treeGetRecord(1).toFloatArray(), 1e-9f);
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeSetTimeContext());
        Assert.assertArrayEquals(new float[]{.0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f}, TreeShr_Test.treeshr.treeGetRecord(1).toFloatArray(), 1e-9f);
    }

    @Test
    public final void test180TreeSetSubtree() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetSubtree(2));
    }

    @Test
    public final void test181TreeSetNoSubtree() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetNoSubtree(2));
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
    public final void test211TreeCompressDatafile() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeCompressDatafile(TreeShr_Test.expt, TreeShr_Test.shot + 1));
    }

    @Test
    public final void test220TreeOpen() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpen(TreeShr_Test.expt, TreeShr_Test.shot + 1, true));
    }

    @Test
    public final void test221TreeGetDatafileSize() throws MdsException {
        Assert.assertEquals(1303, TreeShr_Test.treeshr.treeGetDatafileSize());
    }
}

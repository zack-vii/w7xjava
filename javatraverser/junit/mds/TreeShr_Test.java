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
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.Float32Array;
import mds.data.descriptor_a.Uint32Array;
import mds.data.descriptor_a.Uint64Array;
import mds.data.descriptor_r.Action;
import mds.data.descriptor_r.Function;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Pointer;
import mds.mdsip.Connection;

@SuppressWarnings("static-method")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TreeShr_Test{
    private static Connection   mds;
    private static TreeShr      treeshr;
    private static final String expt    = AllTests.tree;
    private static final int    model   = -1, shot = 7357;
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
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpenNew(TreeShr_Test.expt, TreeShr_Test.model));
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
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeClose(AllTests.tree, TreeShr_Test.model));
    }

    @Test
    public final void test091TreeCleanDatafile() throws MdsException {
        final int status = TreeShr_Test.treeshr.treeCleanDatafile(TreeShr_Test.expt, TreeShr_Test.model);
        Assert.assertEquals(TreeShr_Test.mds.getString(String.format("GetMsg(%d)", status)), TreeShr_Test.normal, status);
    }

    @Test
    public final void test100TreeOpenEdit() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpenEdit(TreeShr_Test.expt, TreeShr_Test.model));
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
    public final void test103TreeManualConglomerate() throws MdsException {
        final int nid = 40;
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeStartConglomerate(11));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid}, TreeShr_Test.treeshr.treeAddNode("D", NodeInfo.USAGE_DEVICE));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetDefault(nid));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid}, TreeShr_Test.treeshr.treeGetDefaultNid());
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 1}, TreeShr_Test.treeshr.treeAddNode("ACTION", NodeInfo.USAGE_ACTION));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 2}, TreeShr_Test.treeshr.treeAddNode("ANY", NodeInfo.USAGE_ANY));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 3}, TreeShr_Test.treeshr.treeAddNode("AXIS", NodeInfo.USAGE_AXIS));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 4}, TreeShr_Test.treeshr.treeAddNode("COMPOUND", NodeInfo.USAGE_COMPOUND_DATA));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 5}, TreeShr_Test.treeshr.treeAddNode("DISPATCH", NodeInfo.USAGE_DISPATCH));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 6}, TreeShr_Test.treeshr.treeAddNode("NUMERIC", NodeInfo.USAGE_NUMERIC));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 7}, TreeShr_Test.treeshr.treeAddNode("SIGNAL", NodeInfo.USAGE_SIGNAL));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 8}, TreeShr_Test.treeshr.treeAddNode("STRUCTURE", NodeInfo.USAGE_STRUCTURE));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 9}, TreeShr_Test.treeshr.treeAddNode("TASK", NodeInfo.USAGE_TASK));
        Assert.assertArrayEquals(new int[]{TreeShr_Test.normal, nid + 10}, TreeShr_Test.treeshr.treeAddNode("TEXT", NodeInfo.USAGE_TEXT));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeEndConglomerate());
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetDefault(0));
    }

    @Test
    public final void test110TreeWriteTree() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeWriteTree(TreeShr_Test.expt, TreeShr_Test.model));
    }

    @Test
    public final void test112TreeCreateTreeFiles() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeCreateTreeFiles(AllTests.tree, TreeShr_Test.shot, TreeShr_Test.model));
    }

    @Test
    public final void test115TreeContext() throws MdsException {
        final String deco = TreeShr_Test.treeshr.treeCtx().decompile();
        Assert.assertTrue(deco, deco.matches("Pointer\\(0x[a-f0-9]+\\)"));
        final Pointer save = TreeShr_Test.treeshr.treeSaveContext();
        Assert.assertEquals(TreeShr_Test.mds.decompile(save), save.decompile());
        Assert.assertTrue(save.decompile().matches("Pointer\\(0x[a-f0-9]+\\)"));
        Assert.assertArrayEquals(save.serializeArray(), TreeShr_Test.mds.mdsValue("_b=*;_s=MdsShr->MdsSerializeDscOut(xd((_a=*;_s=MdsShr->MdsSerializeDscIn(ref($),xd(_a));_a;)),xd(_b));_b", Descriptor.class, save.serializeDsc()).toByteArray());
        Assert.assertArrayEquals(TreeShr_Test.mds.mdsValue("$", Descriptor.class, save).serializeArray(), save.serializeArray());
        String line0, line1;
        System.out.println(line0 = TreeShr_Test.mds.getString("_t='';_s=TCL('show db',_t);_t"));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpen(AllTests.tree, TreeShr_Test.shot, true));
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
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeRemoveNodesTags(2));
        Assert.assertNull(TreeShr_Test.treeshr.treeFindTagWild("DEVICE"));
    }

    @Test
    public final void test150TreeSetSubtree() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetSubtree(2));
    }

    @Test
    public final void test150TreeSetXNci() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeSetXNci(1, "myattr", Function.$HBAR()));
    }

    @Test
    public final void test151TreeSetNoSubtree() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetNoSubtree(2));
    }

    @Test
    public final void test155TreeRenameNode() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeRenameNode(2, "newB"));
    }

    @Test
    public final void test156TreeGetXNci() throws MdsException {
        Assert.assertEquals("myattr", TreeShr_Test.treeshr.treeGetXNci(1).toString());
    }

    @Test
    public final void test160TreeQuit() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeQuitTree(AllTests.tree, TreeShr_Test.model));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpen(AllTests.tree, TreeShr_Test.shot, false));
    }

    @Test
    public final void test161TreeBeginTimestampedSegment() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeBeginTimestampedSegment(1, new Float32Array(new float[3]), -1));
    }

    @Test
    public final void test162TreePutTimestampedSegment() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutTimestampedSegment(1, System.nanoTime(), new Float32Array(.1f, .2f, .3f)));
    }

    @Test
    public final void test163TreePutRecord() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutRecord(1, null));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treePutRecord(41, new Action(new Nid(45), new Nid(49), null, null, null)));
    }

    @Test
    public final void test164TreeMakeTimestampedSegment() throws MdsException {
        final long t0 = 1000000000000l;
        final long[] dim = new long[10];
        for(int i = 0; i < 10; i++)
            dim[i] = t0 + i * 1000000l;
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treeMakeTimestampedSegment(1, new Uint64Array(dim), new Float32Array(.0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, Float.NaN), -1, 9));
    }

    @Test
    public final void test164TreePutRecordMultiDim() throws MdsException {
        final long t0 = 1000000000000l;
        final int[] dims = {8, 7, 6, 5, 4, 3, 2, 1};
        final long[] dim = {t0};
        int i = 0;
        final int[] data = new int[8 * 7 * 6 * 5 * 4 * 3 * 2 * 1];
        for(int i7 = 0; i7 < dims[7]; i7++)
            for(int i6 = 0; i6 < dims[6]; i6++)
                for(int i5 = 0; i5 < dims[5]; i5++)
                    for(int i4 = 0; i4 < dims[4]; i4++)
                        for(int i3 = 0; i3 < dims[3]; i3++)
                            for(int i2 = 0; i2 < dims[2]; i2++)
                                for(int i1 = 0; i1 < dims[1]; i1++)
                                    for(int i0 = 0; i0 < dims[0]; i0++)
                                        data[i++] = i7 * 10000000 + i6 * 1000000 + i5 * 100000 + i4 * 10000 + i3 * 1000 + i2 * 100 + i1 * 10 + i0;
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treePutRecord(47, new Signal(new Uint32Array(dims, data), null, new Uint64Array(dim))));
        Assert.assertEquals("Build_Signal(Long_Unsigned(Set_Range(8,7,6,5,4,3,2,1,0LU /*** etc. ***/)), *, [0X100000018QU])", TreeShr_Test.treeshr.treeGetRecord(47).decompile());
    }

    @Test
    public final void test169TreePutRow() throws MdsException {
        Assert.assertEquals(TreeShr_Test.success, TreeShr_Test.treeshr.treePutRow(1, 1 << 10, 1000010000000l, new Float32Array(.9f)));
    }

    @Test
    public final void test170TreeSetTimeContext_TreeGetRecord() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeSetTimeContext(1000001000000l, 1000007000000l, 2000000l));
        Assert.assertArrayEquals(new float[]{.3f, .5f, .7f}, TreeShr_Test.treeshr.treeGetRecord(1).toFloatArray(), 1e-9f);
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeSetTimeContext());
        Assert.assertArrayEquals(new float[]{.0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f}, TreeShr_Test.treeshr.treeGetRecord(1).toFloatArray(), 1e-9f);
    }

    @Test
    public final void test171TreeGetSegment() throws MdsException {
        Assert.assertArrayEquals(new int[]{10}, TreeShr_Test.treeshr.treeGetSegment(1, 0).getShape());
    }

    @Test
    public final void test172TreeGetSegmentInfo() throws MdsException {
        Assert.assertEquals(DTYPE.FLOAT, TreeShr_Test.treeshr.treeGetSegmentInfo(1, 0).dtype);
    }

    @Test
    public final void test183TreeTurnOff() throws MdsException {
        Assert.assertEquals(265392050, TreeShr_Test.treeshr.treeTurnOff(1));
    }

    @Test
    public final void test184TreeTurnOn() throws MdsException {
        Assert.assertEquals(265392050, TreeShr_Test.treeshr.treeTurnOn(1));
    }

    @Test
    public final void test185TreeSetNciItm() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetNciItm(1, true, 0x7FFFFFFF));
        Assert.assertEquals(0x7FFFFFFF, TreeShr_Test.mds.getInteger("GetNci(1,'GET_FLAGS')"));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeSetNciItm(1, false, -0x10400 - 1));
        Assert.assertEquals(66560, TreeShr_Test.mds.getInteger("GetNci(1,'GET_FLAGS')"));
    }

    @Test
    public final void test211TreeCompressDatafile() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeClose(AllTests.tree, TreeShr_Test.shot));
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeCompressDatafile(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test220TreeOpen() throws MdsException {
        Assert.assertEquals(TreeShr_Test.normal, TreeShr_Test.treeshr.treeOpen(TreeShr_Test.expt, TreeShr_Test.model, true));
    }

    @Test
    public final void test221TreeGetDatafileSize() throws MdsException {
        Assert.assertEquals(11155, TreeShr_Test.treeshr.treeGetDatafileSize());
    }
}

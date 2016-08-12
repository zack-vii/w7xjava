package mds;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import mds.TreeShr.TagRefStatus;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.Float32Array;
import mds.data.descriptor_a.Uint32Array;
import mds.data.descriptor_a.Uint64Array;
import mds.data.descriptor_r.Action;
import mds.data.descriptor_r.Function;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.NODE;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Pointer;
import mds.mdsip.MdsIp;

@SuppressWarnings("static-method")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TreeShr_Test{
    private static MdsIp        mds;
    private static TreeShr      treeshr;
    private static final String expt  = AllTests.tree;
    private static final int    model = -1, shot = 7357;
    private static Pointer      ctx   = Pointer.NULL();

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        TreeShr_Test.mds = AllTests.setUpBeforeClass();
        TreeShr_Test.treeshr = new TreeShr(TreeShr_Test.mds);
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        TreeShr_Test.mds.close();
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public final void test000TreeOpenNew() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeOpenNew(TreeShr_Test.ctx, TreeShr_Test.expt, TreeShr_Test.model));
        TreeShr_Test.ctx = TreeShr_Test.treeshr.treeSaveContext();
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
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeClose(TreeShr_Test.ctx, AllTests.tree, TreeShr_Test.model));
    }

    @Test
    public final void test091TreeCleanDatafile() throws MdsException {
        final int status = TreeShr_Test.treeshr.treeCleanDatafile(TreeShr_Test.expt, TreeShr_Test.model);
        Assert.assertEquals(TreeShr_Test.mds.getString(String.format("GetMsg(%d)", status)), MdsException.TreeNORMAL, status);
    }

    @Test
    public final void test100TreeOpenEdit() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeOpenEdit(TreeShr_Test.ctx, TreeShr_Test.expt, TreeShr_Test.model));
    }

    @Test
    public final void test101TreeAddNode() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "A", NODE.USAGE_SIGNAL).status);
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "B", NODE.USAGE_SUBTREE).status);
        Assert.assertEquals(NODE.USAGE_SIGNAL, TreeShr_Test.mds.getInteger("GetNci(\\TEST::TOP:A,'USAGE')"));
        Assert.assertEquals(NODE.USAGE_SUBTREE, TreeShr_Test.mds.getInteger("GetNci(\\TEST::TOP:B,'USAGE')"));
    }

    @Test
    public final void test102TreeAddConglom() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeAddConglom(TreeShr_Test.ctx, "C", "E1429").status);
    }

    @Test
    public final void test103TreeManualConglomerate() throws MdsException {
        final int nid = 40;
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeStartConglomerate(TreeShr_Test.ctx, 11));
        Assert.assertEquals(nid, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "D", NODE.USAGE_DEVICE).data);
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeSetDefault(TreeShr_Test.ctx, nid));
        Assert.assertEquals(nid, TreeShr_Test.treeshr.treeGetDefaultNid(TreeShr_Test.ctx).data);
        Assert.assertEquals(nid + 1, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "ACTION", NODE.USAGE_ACTION).data);
        Assert.assertEquals(nid + 2, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "ANY", NODE.USAGE_ANY).data);
        Assert.assertEquals(nid + 3, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "AXIS", NODE.USAGE_AXIS).data);
        Assert.assertEquals(nid + 4, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "COMPOUND", NODE.USAGE_COMPOUND_DATA).data);
        Assert.assertEquals(nid + 5, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "DISPATCH", NODE.USAGE_DISPATCH).data);
        Assert.assertEquals(nid + 6, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "NUMERIC", NODE.USAGE_NUMERIC).data);
        Assert.assertEquals(nid + 7, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "SIGNAL", NODE.USAGE_SIGNAL).data);
        Assert.assertEquals(nid + 8, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "STRUCTURE", NODE.USAGE_STRUCTURE).data);
        Assert.assertEquals(nid + 9, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "TASK", NODE.USAGE_TASK).data);
        Assert.assertEquals(nid + 10, TreeShr_Test.treeshr.treeAddNode(TreeShr_Test.ctx, "TEXT", NODE.USAGE_TEXT).data);
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeEndConglomerate(TreeShr_Test.ctx));
        Assert.assertEquals("ANY", TreeShr_Test.treeshr.treeGetMinimumPath(TreeShr_Test.ctx, nid + 2));
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeSetDefault(TreeShr_Test.ctx, 0));
    }

    @Test
    public final void test110TreeWriteTree() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeWriteTree(TreeShr_Test.ctx, TreeShr_Test.expt, TreeShr_Test.model));
    }

    @Test
    public final void test112TreeCreateTreeFiles() throws MdsException {
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treeCreateTreeFiles(AllTests.tree, TreeShr_Test.shot, TreeShr_Test.model));
    }

    @Test
    public final void test115TreeContext() throws MdsException {
        final String deco = TreeShr_Test.treeshr.treeCtx(TreeShr_Test.ctx).decompile();
        Assert.assertTrue(deco, deco.matches("Pointer\\(0x[a-f0-9]+\\)"));
        final Pointer save = TreeShr_Test.treeshr.treeSaveContext();
        Assert.assertEquals(TreeShr_Test.mds.getString("Decompile($)", save), save.decompile());
        Assert.assertTrue(save.decompile().matches("Pointer\\(0x[a-f0-9]+\\)"));
        Assert.assertArrayEquals(save.serializeArray(), TreeShr_Test.mds.getByteArray(TreeShr_Test.ctx, "_b=*;_s=MdsShr->MdsSerializeDscOut(xd($),xd(_b));_b", save));
        Assert.assertArrayEquals(TreeShr_Test.mds.getDescriptor("$", Descriptor.class, save).serializeArray(), save.serializeArray());
        String line0, line1;
        System.out.println(line0 = TreeShr_Test.mds.getString("_t='';_s=TCL('show db',_t);_t"));
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeOpen(null, AllTests.tree, TreeShr_Test.shot, true));
        System.out.println(line1 = TreeShr_Test.mds.getString("_t='';_s=TCL('show db',_t);_t"));
        final String line2 = line1.split("\n")[1], line3 = "001" + line0.substring(3, line0.length());
        Assert.assertTrue(line1, line3.startsWith(line2));
        Assert.assertTrue(0 != TreeShr_Test.treeshr.treeRestoreContext(save));
        System.out.println(line1 = TreeShr_Test.mds.getString(null, "_t='';_s=TCL('show db',_t);_t"));
        Assert.assertTrue(line1, line0.startsWith(line0));
    }

    @Test
    public final void test116TreeUsePrivateCtx() throws MdsException {
        Assert.assertFalse(TreeShr_Test.treeshr.treeUsePrivateCtx(TreeShr_Test.ctx, true));
        Assert.assertTrue(TreeShr_Test.treeshr.treeUsingPrivateCtx(TreeShr_Test.ctx));
        Assert.assertTrue(TreeShr_Test.treeshr.treeUsePrivateCtx(TreeShr_Test.ctx, false));
        Assert.assertFalse(TreeShr_Test.treeshr.treeUsingPrivateCtx(TreeShr_Test.ctx));
    }

    @Test
    public final void test121TreeDeleteNodeInitialize() throws MdsException {
        Assert.assertEquals(37, TreeShr_Test.treeshr.treeDeleteNodeInitialize(TreeShr_Test.ctx, 3).data);
    }

    @Test
    public final void test122TreeDeleteNodeGetNid() throws MdsException {
        Assert.assertEquals(3, TreeShr_Test.treeshr.treeDeleteNodeGetNid(TreeShr_Test.ctx, 0).data);
    }

    @Test
    public final void test123TreeDeleteNodeExecute() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeDeleteNodeExecute(TreeShr_Test.ctx));
    }

    @Test
    public final void test130TreeAddTag() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeAddTag(TreeShr_Test.ctx, 2, "DEVICE"));
        Assert.assertEquals("B", TreeShr_Test.mds.getString(TreeShr_Test.ctx, "Trim(GetNci(\\DEVICE,'NODE_NAME'))"));
        Assert.assertEquals("\\TEST::DEVICE", TreeShr_Test.treeshr.treeGetPath(TreeShr_Test.ctx, 2));
    }

    @Test
    public final void test131TreeFindTagWildDsc() throws MdsException {
        TagRefStatus tag = TagRefStatus.init;
        Assert.assertEquals("\\TEST::DEVICE", (tag = TreeShr_Test.treeshr.treeFindTagWild(TreeShr_Test.ctx, "DEVICE", tag)).data);
        Assert.assertEquals(2, tag.nid);
        Assert.assertNull((tag = TreeShr_Test.treeshr.treeFindTagWild(TreeShr_Test.ctx, "***", tag)).data);
        Assert.assertEquals("\\TEST::TOP", TreeShr_Test.treeshr.treeFindTagWild(TreeShr_Test.ctx, "***", tag).data);
    }

    @Test
    public final void test132TreeRemoveNodesTags() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeRemoveNodesTags(TreeShr_Test.ctx, 2));
        Assert.assertNull(TreeShr_Test.treeshr.treeFindTagWild(TreeShr_Test.ctx, "DEVICE", TagRefStatus.init).data);
    }

    @Test
    public final void test150TreeSetSubtree() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeSetSubtree(TreeShr_Test.ctx, 2));
    }

    @Test
    public final void test150TreeSetXNci() throws MdsException {
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treeSetXNci(TreeShr_Test.ctx, 1, "myattr", Function.$HBAR()));
    }

    @Test
    public final void test151TreeSetNoSubtree() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeSetNoSubtree(TreeShr_Test.ctx, 2));
    }

    @Test
    public final void test155TreeRenameNode() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeRenameNode(TreeShr_Test.ctx, 2, "newB"));
    }

    @Test
    public final void test156TreeGetXNci() throws MdsException {
        Assert.assertEquals("[\"myattr\"]", TreeShr_Test.treeshr.treeGetXNci(TreeShr_Test.ctx, 1).data.decompile());
    }

    @Test
    public final void test160TreeQuit() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeQuitTree(TreeShr_Test.ctx, TreeShr_Test.expt, TreeShr_Test.model));
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeOpen(TreeShr_Test.ctx, AllTests.tree, TreeShr_Test.shot, false));
    }

    @Test
    public final void test161TreeBeginTimestampedSegment() throws MdsException {
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treeBeginTimestampedSegment(TreeShr_Test.ctx, 1, new Float32Array(new float[3]), -1));
    }

    @Test
    public final void test162TreePutTimestampedSegment() throws MdsException {
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treePutTimestampedSegment(TreeShr_Test.ctx, 1, System.nanoTime(), new Float32Array(.1f, .2f, .3f)));
    }

    @Test
    public final void test163TreePutRecord() throws MdsException {
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treePutRecord(TreeShr_Test.ctx, 1, null));
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treePutRecord(TreeShr_Test.ctx, 41, new Action(new Nid(45), new Nid(49), null, null, null)));
    }

    @Test
    public final void test164TreeMakeTimestampedSegment() throws MdsException {
        final long t0 = 1000000000000l;
        final long[] dim = new long[10];
        for(int i = 0; i < 10; i++)
            dim[i] = t0 + i * 1000000l;
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treeMakeTimestampedSegment(TreeShr_Test.ctx, 1, dim, new Float32Array(.0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, Float.NaN), -1, 9));
    }

    @Test
    public final void test165TreePutRow() throws MdsException {
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treePutRow(TreeShr_Test.ctx, 1, 1 << 10, 1000009000000l, new Float32Array(.9f)));
    }

    @Test
    public final void test166TreeMakeSegment() throws MdsException {
        final long t0 = 1000010000000l;
        final long[] dim = new long[10];
        for(int i = 0; i < 10; i++)
            dim[i] = t0 + i * 1000000l;
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeGetNumSegments(TreeShr_Test.ctx, 1).data);
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treeMakeTimestampedSegment(TreeShr_Test.ctx, 1, dim, new Float32Array(1.0f, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f)));
        Assert.assertEquals(2, TreeShr_Test.treeshr.treeGetNumSegments(TreeShr_Test.ctx, 1).data);
        for(int i = 0; i < 10; i++)
            dim[i] = t0 + i * 1000000l + 10000000l;
        Assert.assertEquals(MdsException.TreeSUCCESS, TreeShr_Test.treeshr.treeMakeTimestampedSegment(TreeShr_Test.ctx, 1, dim, new Float32Array(2.0f, 2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f)));
        Assert.assertEquals(3, TreeShr_Test.treeshr.treeGetNumSegments(TreeShr_Test.ctx, 1).data);
        Assert.assertEquals("[1000010000000Q,1000019000000Q]", TreeShr_Test.treeshr.treeGetSegmentLimits(TreeShr_Test.ctx, 1, 1).data.decompile());
    }

    @Test
    public final void test170TreeSetTimeContext_TreeGetRecord() throws MdsException {
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeSetTimeContext(TreeShr_Test.ctx, 1000001000000l, 1000007000000l, 2000000l));
        Assert.assertArrayEquals(new float[]{.3f, .5f, .7f}, TreeShr_Test.treeshr.treeGetRecord(TreeShr_Test.ctx, 1).data.toFloatArray(), 1e-9f);
        Assert.assertEquals(1, TreeShr_Test.treeshr.treeSetTimeContext(TreeShr_Test.ctx));
        Assert.assertArrayEquals(new float[]{.0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f}, TreeShr_Test.treeshr.treeGetSegment(TreeShr_Test.ctx, 1, 0).data.toFloatArray(), 1e-9f);
    }

    @Test
    public final void test171TreeGetSegment() throws MdsException {
        Assert.assertArrayEquals(new int[]{10}, TreeShr_Test.treeshr.treeGetSegment(TreeShr_Test.ctx, 1, 0).data.getShape());
    }

    @Test
    public final void test172TreeGetSegmentInfo() throws MdsException {
        Assert.assertEquals(DTYPE.FLOAT, TreeShr_Test.treeshr.treeGetSegmentInfo(TreeShr_Test.ctx, 1, 0).dtype);
    }

    @Test
    public final void test175TreePutRecordMultiDim() throws MdsException {
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
        final Signal signal = new Signal(new Uint32Array(dims, data), null, new Uint64Array(dim));
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treePutRecord(TreeShr_Test.ctx, 47, signal));
        Assert.assertEquals(signal.decompile(), signal.serializeDsc().deserialize().decompile());
        final String dec = "Build_Signal(Long_Unsigned(Set_Range(8,7,6,5,4,3,2,1,0LU /*** etc. ***/)), *, [1000000000000QU])";
        Assert.assertEquals(dec, TreeShr_Test.mds.getString(TreeShr_Test.ctx, "_a=*;_s=TdiShr->TdiDecompile(xd($),xd(_a),val(-1));_a", signal));
        Assert.assertEquals(dec, TreeShr_Test.mds.getString(TreeShr_Test.ctx, "_a=GETNCI(47,'RECORD');_s=TdiShr->TdiDecompile(xd(_a),xd(_a),val(-1));_a"));
        Assert.assertEquals(dec, TreeShr_Test.treeshr.treeGetRecord(TreeShr_Test.ctx, 47).data.decompile());
    }

    @Test
    public final void test183TreeTurnOff() throws MdsException {
        final int status = TreeShr_Test.treeshr.treeTurnOff(TreeShr_Test.ctx, 1);
        Assert.assertTrue(status == MdsException.TreeSUCCESS || status == MdsException.TreeLOCK_FAILURE);
        Assert.assertEquals(MdsException.TreeOFF, TreeShr_Test.treeshr.treeIsOn(TreeShr_Test.ctx, 1));
    }

    @Test
    public final void test184TreeTurnOn() throws MdsException {
        final int status = TreeShr_Test.treeshr.treeTurnOn(TreeShr_Test.ctx, 1);
        Assert.assertTrue(status == MdsException.TreeSUCCESS || status == MdsException.TreeLOCK_FAILURE);
    }

    @Test
    public final void test185TreeSetNciItm() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeSetNciItm(TreeShr_Test.ctx, 1, true, 0x7FFFFFFF));
        Assert.assertEquals(0x7FFFFFFF, TreeShr_Test.mds.getInteger(TreeShr_Test.ctx, "GetNci(1,'GET_FLAGS')"));
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeSetNciItm(TreeShr_Test.ctx, 1, false, -0x10400 - 1));
        Assert.assertEquals(66560, TreeShr_Test.mds.getInteger(TreeShr_Test.ctx, "GetNci(1,'GET_FLAGS')"));
    }

    @Test
    public final void test211TreeCompressDatafile() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeClose(TreeShr_Test.ctx, AllTests.tree, TreeShr_Test.shot));
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeCompressDatafile(TreeShr_Test.expt, TreeShr_Test.shot));
    }

    @Test
    public final void test220TreeOpen() throws MdsException {
        Assert.assertEquals(MdsException.TreeNORMAL, TreeShr_Test.treeshr.treeOpen(TreeShr_Test.ctx, TreeShr_Test.expt, TreeShr_Test.model, true));
    }

    @Test
    public final void test221TreeGetDatafileSize() throws MdsException {
        Assert.assertEquals(11187, TreeShr_Test.treeshr.treeGetDatafileSize(TreeShr_Test.ctx));
    }
}

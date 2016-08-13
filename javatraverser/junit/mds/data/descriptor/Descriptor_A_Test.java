package mds.data.descriptor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import mds.AllTests;
import mds.MdsException;
import mds.data.descriptor_a.Float32Array;
import mds.data.descriptor_a.Float64Array;
import mds.data.descriptor_a.Int32Array;
import mds.data.descriptor_a.Int64Array;
import mds.data.descriptor_a.Uint64Array;
import mds.data.descriptor_r.Function;
import mds.data.descriptor_r.Signal;
import mds.mdsip.MdsIp;

@SuppressWarnings("static-method")
public final class Descriptor_A_Test{
    private static MdsIp mds;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        Descriptor_A_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        Descriptor_A_Test.mds.close();
    }

    @Before
    public final void setUp() throws Exception {}

    @After
    public final void tearDown() throws Exception {}

    @Test
    public final void testARRAY() throws MdsException {
        final Descriptor_A test = new Uint64Array(1000000000000l);
        Assert.assertEquals("[1000000000000QU]", Descriptor_A_Test.mds.getString("DECOMPILE($)", test));
        Assert.assertArrayEquals(new byte[]{8, 0, 5, 4, 16, 0, 0, 0, 0, 0, 48, 1, 8, 0, 0, 0, 0, 16, -91, -44, -24, 0, 0, 0}, test.serializeArray());
        Assert.assertEquals("ABS([1000000000000QU])", Descriptor_A_Test.mds.getString("_a=*;_s=MdsShr->MdsSerializeDscIn(ref($),xd(_a));_s=TdiShr->TdiDecompile(xd(_a),xd(_a),val(-1));_a", Function.abs(new Uint64Array(1000000000000l)).serializeDsc()));
        Assert.assertEquals("Build_Signal([1D0,2D0,3D0], *, [1Q,2Q,3Q])", new Signal(new Float64Array(1, 2, 3), null, new Int64Array(1, 2, 3)).decompile());
        Assert.assertEquals("Build_Signal([[[1,2],[3,4]],[[5,6],[7,8]]], *, [1Q,2Q])", new Signal(new Int32Array(new int[]{2, 2, 2}, 1, 2, 3, 4, 5, 6, 7, 8), null, new Int64Array(1, 2)).decompile());
    }

    @Test
    public final void testFloat32Array() throws MdsException {
        Assert.assertEquals("[1.,2.,3.,4.,5.]", new Float32Array(1, 2, 3, 4, 5).decompile());
    }

    @Test
    public final void testInt16Array() throws MdsException {
        final Descriptor D = Descriptor_A_Test.mds.getDescriptor("WORD([1,2,3,4,5])");
        Assert.assertArrayEquals(new byte[]{2, 0, 7, 4, 16, 0, 0, 0, 0, 0, 48, 1, 10, 0, 0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0}, D.serializeArray());
        Assert.assertEquals("Word([1,2,3,4,5])", D.decompile());
    }

    @Test
    public final void testInt64Array() throws MdsException {
        Assert.assertEquals("[1234567890123456QU,1234567890123457QU]", new Uint64Array(1234567890123456l, 1234567890123457l).decompile());
    }
}

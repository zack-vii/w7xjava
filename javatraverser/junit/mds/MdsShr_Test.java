package mds;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.Int8Array;
import mds.data.descriptor_r.Range;
import mds.mdsip.MdsIp;

@SuppressWarnings("static-method")
public class MdsShr_Test{
    private static MdsIp mds;
    private static MdsShr     mdsshr;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        MdsShr_Test.mds = AllTests.setUpBeforeClass();
        MdsShr_Test.mdsshr = new MdsShr(MdsShr_Test.mds);
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        MdsShr_Test.mds.close();
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public final void testMdsCompress() throws MdsException {
        Assert.assertEquals("Set_Range(50000,0 /*** etc. ***/)", MdsShr_Test.mds.getString("_a=DATA(0:99999:2);_s=MdsShr->MdsCompress(0,0,xd(_a),xd(_a));_s=TdiShr->TdiDecompile(xd(_a),xd(_a),val(-1));_a"));
        Assert.assertEquals("Set_Range(50000,0 /*** etc. ***/)", MdsShr_Test.mdsshr.mdsCompress(new Range(0, 99999, 2).getData()).decompile());
    }

    @Test
    public final void testMdsEvent() throws MdsException {
        Assert.assertEquals(1, MdsShr_Test.mdsshr.mdsEvent("myevent"));
    }

    @Test
    public final void testMdsGetMsgDsc() throws MdsException {
        Assert.assertEquals("%SS-S-SUCCESS, Success", MdsShr_Test.mdsshr.mdsGetMsgDsc(1));
    }

    @Test
    public final void testMdsSerializeDsc() throws MdsException {
        final String result = "Build_With_Units(101325., \"Pa\")";
        final Int8Array serial = MdsShr_Test.mdsshr.mdsSerializeDscOut("$P0");
        Assert.assertEquals(result, Descriptor.deserialize(serial.getBuffer()).decompile());
        Assert.assertEquals(result, MdsShr_Test.mdsshr.mdsSerializeDscIn(serial).decompile());
    }
}

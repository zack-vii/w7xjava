package mds.mdsip;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import mds.AllTests;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.Int16Array;
import mds.data.descriptor_s.CString;

@SuppressWarnings("static-method")
public class Connection_Test{
    private static Connection mds;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Connection_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Connection_Test.mds.close();
    }

    @Test
    public void getDescriptor() throws MdsException {
        Assert.assertEquals("Set_Range(1000,0. /*** etc. ***/)", Connection_Test.mds.getDescriptor("Array([1000],0.)").toString());
        Assert.assertEquals("[[[1.1],[2.1]],[[3.1],[4.1]]]", Connection_Test.mds.getDescriptor("[[[1.1],[2.1]],[[3.1],[4.1]]]").toString());
        final Descriptor array = new Int16Array(new short[]{1, 2, 3, 4, 5});
        Assert.assertEquals(Connection_Test.mds.getDescriptor("WORD([1, 2, 3, 4, 5])").decompile(), array.decompile());
        Assert.assertEquals("Word([1,2,3,4,5])", Connection_Test.mds.getDescriptor("$", array).decompile());
        Assert.assertEquals("\"123456789\"", Connection_Test.mds.getDescriptor("concat", new CString("123"), new CString("456"), new CString("789")).decompile());
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}
}

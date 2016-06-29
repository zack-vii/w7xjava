package mds.data.descriptor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import mds.AllTests;
import mds.MdsException;
import mds.mdsip.Connection;

public class Descriptor_A_Test{
    private static Connection mds;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Descriptor_A_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Descriptor_A_Test.mds.disconnect();
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @SuppressWarnings("static-method")
    @Test
    public void test() throws MdsException {
        final Descriptor D = Descriptor_A_Test.mds.compile("WORD([1,2,3,4,5])");
        Assert.assertArrayEquals(new byte[]{2, 0, 7, 4, 16, 0, 0, 0, 0, 0, 48, 1, 10, 0, 0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0}, D.serialize().array());
        Assert.assertEquals("Word([1,2,3,4,5])", D.decompile());
    }
}

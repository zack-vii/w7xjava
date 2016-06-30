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

@SuppressWarnings("static-method")
public final class Descriptor_S_Test{
    private static Connection mds;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        Descriptor_S_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        Descriptor_S_Test.mds.disconnect();
    }

    @Test
    public final void complex() throws MdsException {
        Assert.assertEquals("[Cmplx(100.,30000.)]", Descriptor_S_Test.mds.compile("[cmplx(1.e2,3.e4)]").decompile());
    }

    @Before
    public final void setUp() throws Exception {}

    @Test
    public final void string() throws MdsException {
        Assert.assertEquals("\"test\"", Descriptor_S_Test.mds.compile("'test'").decompile());
        Assert.assertEquals("\"test\" // TEXT(1) // \"test\"", Descriptor_S_Test.mds.compile("'test'//text(1)//\"test\"").decompile());
        Assert.assertEquals("'\"test\"'", Descriptor_S_Test.mds.compile("'\\\"test\\\"'").decompile());
    }

    @After
    public final void tearDown() throws Exception {}
}

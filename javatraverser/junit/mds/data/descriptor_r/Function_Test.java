package mds.data.descriptor_r;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import mds.AllTests;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.mdsip.Connection;

public class Function_Test{
    private static Connection mds;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Function_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Function_Test.mds.disconnect();
    }

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @SuppressWarnings("static-method")
    @Test
    public void test() throws MdsException {
        Assert.assertEquals("[Cmplx(100.,30000.)]", Function_Test.mds.compile("[cmplx(1.e2,3.e4)]").decompile());
        Assert.assertEquals("OUT _R", Function_Test.mds.compile("out _R").decompile());
        Assert.assertEquals("Fun PUBLIC myfun (IN _R, OUT _out) {\r\n\t_out = _R + 1;\r\n\tReturn (_out);\r\n}", Function_Test.mds.compile("public fun myfun(in _R, out _out) STATEMENT(_out = _R+1,return(_out))").decompile());
        Assert.assertEquals("PRIVATE _R", Function_Test.mds.compile("private _R").decompile());
        Assert.assertEquals("PUBLIC _R", Function_Test.mds.compile("public _R").decompile());
        Assert.assertEquals("_r = SQRT((1 + 5) * 6) / (3 - 1)", Function_Test.mds.compile("_r=sqrt((1+5)*6)/(3-1)").decompile());
        Assert.assertEquals("For (_i = 1; _i < 5; _i++) {\r\n\tWRITE(*, TEXT(_i));\r\n}", Function_Test.mds.compile("for(_i=1;_i<5;_i++) write(*,text(_i))").decompile());
        Assert.assertEquals("TreeShr->TreeCtx($SHOT, (5 / 2) ^ 2)", Function_Test.mds.compile("TreeShr->TreeCtx($shot,(5/2)^2)").decompile());
        Assert.assertEquals("TreeShr->TreeCtx:Q()", Function_Test.mds.compile("build_call(9,'TreeShr','TreeCtx')").decompile());
        Assert.assertEquals("\"test\"", Function_Test.mds.compile("'test'").decompile());
        Assert.assertEquals("\"test\" // TEXT(1) // \"test\"", Function_Test.mds.compile("'test'//text(1)//\"test\"").decompile());
        Assert.assertEquals("'\"test\"'", Function_Test.mds.compile("'\\\"test\\\"'").decompile());
        final Descriptor D = Function_Test.mds.compile("$P0");
        Assert.assertEquals("Build_With_Units(101325., \"Pa\")", D.evaluate().decompile());
    }
}

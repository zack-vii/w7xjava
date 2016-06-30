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

@SuppressWarnings("static-method")
public final class Function_Test{
    private static Connection mds;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        Function_Test.mds = AllTests.setUpBeforeClass();
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        Function_Test.mds.disconnect();
    }

    @Test
    public final void $a0() throws MdsException {
        Assert.assertEquals(Function_Test.mds.evaluate("$A0").decompile(), Function.$A0().evaluate().decompile());// , new Descriptor[]{Function.$A0()}));
    }

    @Test
    public final void $p0() throws MdsException {
        final Descriptor D = Function_Test.mds.compile("$P0");
        Assert.assertEquals("Build_With_Units(101325., \"Pa\")", D.evaluate().decompile());
    }

    @Test
    public final void forloop() throws MdsException {
        Assert.assertEquals("For (_i = 1; _i < 5; _i++) {\r\n\tWRITE(*, TEXT(_i));\r\n}", Function_Test.mds.compile("for(_i=1;_i<5;_i++) write(*,text(_i))").decompile());
    }

    @Test
    public final void fun() throws MdsException {
        Assert.assertEquals("Fun PUBLIC myfun (IN _R, OUT _out) {\r\n\t_out = _R + 1;\r\n\tReturn (_out);\r\n}", Function_Test.mds.compile("public fun myfun(in _R, out _out) STATEMENT(_out = _R+1,return(_out))").decompile());
    }

    @Test
    public final void out() throws MdsException {
        Assert.assertEquals("OUT _R", Function_Test.mds.compile("out _R").decompile());
    }

    @Test
    public final void privatepublic() throws MdsException {
        Assert.assertEquals("PRIVATE _R", Function_Test.mds.compile("private _R").decompile());
        Assert.assertEquals("PUBLIC _R", Function_Test.mds.compile("public _R").decompile());
    }

    @Test
    public final void routine() throws MdsException {
        Assert.assertEquals("TreeShr->TreeCtx:Q()", Function_Test.mds.compile("build_call(9,'TreeShr','TreeCtx')").decompile());
    }

    @Before
    public final void setUp() throws Exception {}

    @Test
    public final void shot() throws MdsException {
        Assert.assertEquals("TreeShr->TreeCtx($SHOT, (5 / 2) ^ 2)", Function_Test.mds.compile("TreeShr->TreeCtx($shot,(5/2)^2)").decompile());
    }

    @Test
    public final void sqrtmultadd() throws MdsException {
        Assert.assertEquals("_r = SQRT((1 + 5) * 6) / (3 - 1) ^ 2", Function_Test.mds.compile("_r=sqrt((1+5)*6)/(3-1)^2").decompile());
    }

    @After
    public final void tearDown() throws Exception {}
}

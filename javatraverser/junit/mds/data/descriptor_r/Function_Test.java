package mds.data.descriptor_r;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import mds.AllTests;
import mds.MdsException;
import mds.TdiShr;
import mds.data.descriptor_a.Int32Array;
import mds.data.descriptor_a.Uint64Array;
import mds.mdsip.MdsIp;

@SuppressWarnings("static-method")
public final class Function_Test{
    private static MdsIp mds;
    private static TdiShr     tdi;

    @BeforeClass
    public static final void setUpBeforeClass() throws Exception {
        Function_Test.mds = AllTests.setUpBeforeClass();
        Function_Test.tdi = new TdiShr(Function_Test.mds);
    }

    @AfterClass
    public static final void tearDownAfterClass() throws Exception {
        Function_Test.mds.close();
    }

    @Test
    public final void $a0() throws MdsException {
        Assert.assertEquals(Function_Test.tdi.tdiExecute("$A0").decompile(), Function.$A0().evaluate().decompile());// , Function.$A0()));
    }

    @Test
    public final void $p0() throws MdsException {
        Assert.assertEquals("Build_With_Units(101325., \"Pa\")", Function_Test.tdi.tdiCompile("$P0").evaluate().decompile());
    }

    @Test
    public final void $value() throws MdsException {
        Assert.assertEquals("[1]", new Signal(Function.$VALUE(), new Int32Array(1), new Uint64Array(1l)).getData().decompile());
        Assert.assertEquals("[1.]", new Signal(Function.Float(Function.$VALUE()), new Int32Array(1), new Uint64Array(1l)).getData().decompile());
        Assert.assertEquals("[1D0]", new Signal(Function.FT_Float(Function.$VALUE()), new Int32Array(1), new Uint64Array(1l)).getData().decompile());
    }

    @Test
    public final void concat() throws MdsException {
        Assert.assertEquals("\"test\" // TEXT(1) // \"test\"", Function_Test.tdi.tdiCompile("'test'//text(1)//\"test\"").decompile());
    }

    @Test
    public final void forloop() throws MdsException {
        Assert.assertEquals("For (_i = 1; _i < 5; _i++) {\r\n\tWRITE(*, TEXT(_i));\r\n}", Function_Test.tdi.tdiCompile("for(_i=1;_i<5;_i++) write(*,text(_i))").decompile());
    }

    @Test
    public final void fun() throws MdsException {
        Assert.assertEquals("Fun PUBLIC myfun (IN _R, OUT _out) {\r\n\t_out = _R + 1;\r\n\tReturn (_out);\r\n}", Function_Test.tdi.tdiCompile("public fun myfun(in _R, out _out) STATEMENT(_out = _R+1,return(_out))").decompile());
    }

    @Test
    public final void out() throws MdsException {
        Assert.assertEquals("OUT _R", Function_Test.tdi.tdiCompile("out _R").decompile());
    }

    @Test
    public final void privatepublic() throws MdsException {
        Assert.assertEquals("PRIVATE _R", Function_Test.tdi.tdiCompile("private _R").decompile());
        Assert.assertEquals("PUBLIC _R", Function_Test.tdi.tdiCompile("public _R").decompile());
    }

    @Test
    public final void routine() throws MdsException {
        Assert.assertEquals("TreeShr->TreeCtx:Q()", Function_Test.tdi.tdiCompile("build_call(9,'TreeShr','TreeCtx')").decompile());
    }

    @Before
    public final void setUp() throws Exception {}

    @Test
    public final void shot() throws MdsException {
        Assert.assertEquals("TreeShr->TreeCtx($SHOT, (5 / 2) ^ 2)", Function_Test.tdi.tdiCompile("TreeShr->TreeCtx($shot,(5/2)^2)").decompile());
    }

    @Test
    public final void sqrtmultadd() throws MdsException {
        Assert.assertEquals("_r = SQRT((1 + 5) * 6) / (3 - 1) ^ 2", Function_Test.tdi.tdiCompile("_r=sqrt((1+5)*6)/(3-1)^2").decompile());
    }

    @After
    public final void tearDown() throws Exception {}
}

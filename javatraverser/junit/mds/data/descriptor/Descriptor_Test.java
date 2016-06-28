package mds.data.descriptor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import mds.MdsException;
import mds.mdsip.Connection;

public class Descriptor_Test{
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @SuppressWarnings("static-method")
    @Test
    public void test() throws MdsException {
        final Connection m = new Connection("mds-data-1");
        if(m.error != null) throw new MdsException(m.error);
        final String tree = "w7x";
        final int shot = 0;
        final int nid = 7;
        m.mdsValue(String.format("treeopen('%s',%d)", tree, shot));
        final Descriptor D = m.mdsValue(String.format("COMMA(TreeShr->TreeGetRecord(val(%d),xd(_ans)),MdsShr->MdsSerializeDscOut(xd(_ans),xd(_ans)),_ans)", nid), Descriptor.class);
        Assert.assertNotNull(D);
    }
}

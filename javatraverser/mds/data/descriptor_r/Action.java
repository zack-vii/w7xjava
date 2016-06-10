package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;

public final class Action extends BUILD{
    public Action(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Action(final Descriptor dispatch, final Descriptor task, final Descriptor errorlogs, final Descriptor completion_message, final Descriptor performance){
        super(DTYPE.ACTION, (byte)5, null);
        this.dscptrs[0] = dispatch;
        this.dscptrs[1] = task;
        this.dscptrs[2] = errorlogs;
        this.dscptrs[3] = completion_message;
        this.dscptrs[4] = performance;
    }

    public final Descriptor getCompletionMessage() {
        return this.getDscptrs(3);
    }

    public final Descriptor getDispatch() {
        return this.getDscptrs(0);
    }

    public final Descriptor getErrorLogs() {
        return this.getDscptrs(2);
    }

    public final Descriptor getPerformance() {
        return this.getDscptrs(4);
    }

    public final Descriptor getTask() {
        return this.getDscptrs(1);
    }
}

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
        super(DTYPE.ACTION, null, new Descriptor[]{dispatch, task, errorlogs, completion_message, performance});
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

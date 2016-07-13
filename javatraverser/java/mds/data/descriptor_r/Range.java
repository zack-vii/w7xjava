package mds.data.descriptor_r;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;
import mds.data.descriptor_a.Float64Array;
import mds.data.descriptor_s.Float64;

public final class Range extends Descriptor_R{
    public static double[] range(final double begin, final double ending, final double delta) {
        BigDecimal strt = BigDecimal.valueOf(begin);
        final BigDecimal stop = BigDecimal.valueOf(ending);
        final BigDecimal delt = BigDecimal.valueOf(delta);
        final int n = stop.subtract(strt).divide(delt).intValue();
        final double[] array = new double[n + 1];
        for(int i = 0;; i++){
            array[i] = strt.doubleValue();
            if(i >= n) break;
            strt = strt.add(delt);
        }
        return array;
    }

    public Range(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Range(final Descriptor begin, final Descriptor ending, final Descriptor delta){
        super(DTYPE.RANGE, null, begin, ending, delta);
    }

    public Range(final double begin, final double ending, final double delta){
        this(new Float64(begin), new Float64(ending), new Float64(delta));
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        pout.append(DTYPE.getName(DTYPE.RANGE));
        this.addArguments(0, "(", ")", pout, mode);
        return pout;
    }

    public final Descriptor getBegin() {
        return this.getDescriptor(0);
    }

    @Override
    public final Descriptor getData() {
        return new Float64Array(Range.range(this.getBegin().toDouble(), this.getEnding().toDouble(), this.getDelta().toDouble()));
    }

    public final Descriptor getDelta() {
        return this.getDescriptor(2);
    }

    public final Descriptor getEnding() {
        return this.getDescriptor(1);
    }

    @Override
    public final int[] getShape() {
        return new int[]{(int)((this.getEnding().toFloat() - this.getBegin().toFloat()) / this.getDelta().toFloat())};
    }
}

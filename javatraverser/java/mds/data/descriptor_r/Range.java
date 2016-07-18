package mds.data.descriptor_r;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;
import mds.data.descriptor_a.Float64Array;
import mds.data.descriptor_a.Int32Array;
import mds.data.descriptor_s.FLOAT;
import mds.data.descriptor_s.Float64;
import mds.data.descriptor_s.Int32;
import mds.data.descriptor_s.Missing;

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

    public static int[] range(int begin, final int ending, final int delta) {
        final int n = (ending - begin) / delta;
        final int[] array = new int[n + 1];
        for(int i = 0;; i++){
            array[i] = begin;
            if(i >= n) break;
            begin += delta;
        }
        return array;
    }

    public Range(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Range(final Descriptor begin, final Descriptor ending){
        super(DTYPE.RANGE, null, begin, ending, null);
    }

    public Range(final Descriptor begin, final Descriptor ending, final Descriptor delta){
        super(DTYPE.RANGE, null, begin, ending, delta);
    }

    public Range(final double begin, final double ending){
        this(new Float64(begin), new Float64(ending), null);
    }

    public Range(final double begin, final double ending, final double delta){
        this(new Float64(begin), new Float64(ending), new Float64(delta));
    }

    public Range(final int begin, final int ending){
        this(new Int32(begin), new Int32(ending), null);
    }

    public Range(final int begin, final int ending, final int delta){
        this(new Int32(begin), new Int32(ending), new Int32(delta));
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        this.getBegin().decompile(prec, pout, mode).append(" : ");
        this.getEnding().decompile(prec, pout, mode);
        if(this.getDelta() != Missing.NEW) this.getDelta().decompile(prec, pout.append(" : "), mode);
        return pout;
    }

    public final Descriptor getBegin() {
        return this.getDescriptor(0);
    }

    @Override
    public final Descriptor getData() {
        if(this.getBegin() instanceof FLOAT || this.getEnding() instanceof FLOAT || this.getDelta() instanceof FLOAT) return new Float64Array(Range.range(this.getBegin().toDouble(), this.getEnding().toDouble(), this.getDelta() == Missing.NEW ? 1d : this.getDelta().toDouble()));
        return new Int32Array(Range.range(this.getBegin().toInt(), this.getEnding().toInt(), this.getDelta() == Missing.NEW ? 1 : this.getDelta().toInt()));
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

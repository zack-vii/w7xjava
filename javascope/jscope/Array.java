package jscope;

import java.awt.Color;
import java.awt.Dimension;
import debug.DEBUG;
import mds.Descriptor;

public class Array{
    public static final class AllFrames extends ByteArray{
        public Dimension dim;
        public float[]   times;

        public AllFrames(final ByteArray byteArray, final int width, final int height, final float times[]){
            super(byteArray.buf, byteArray.dtype);
            this.dim = new Dimension(width, height);
            this.times = times;
        }
    }
    public static class ByteArray{
        public final byte buf[], dtype;
        public final int  dataSize, length;

        public ByteArray(final byte[] buf, final byte dtype){
            this.buf = buf;
            this.dtype = dtype;
            this.dataSize = Descriptor.getDataSize(this.dtype, this.buf);
            this.length = buf.length / this.dataSize;
        }

        public final int getFrameType() {
            if(DEBUG.D) System.out.println(">> getFrameType = " + this.dtype);
            switch(this.dtype){
                case Descriptor.DTYPE_UBYTE:
                case Descriptor.DTYPE_BYTE:
                    return FrameData.BITMAP_IMAGE_8;
                case Descriptor.DTYPE_USHORT:
                case Descriptor.DTYPE_SHORT:
                    return FrameData.BITMAP_IMAGE_16;
                case Descriptor.DTYPE_ULONG:
                case Descriptor.DTYPE_LONG:
                    return FrameData.BITMAP_IMAGE_32;
                case Descriptor.DTYPE_ULONGLONG:
                case Descriptor.DTYPE_LONGLONG:
                case Descriptor.DTYPE_FLOAT:
                case Descriptor.DTYPE_DOUBLE:
                    return FrameData.BITMAP_IMAGE_FLOAT;
                default:
                    return FrameData.BITMAP_IMAGE_8;
            }
        }
        /*
        public final float[] toFloatArray() throws IOException {
            final float[] array = new float[this.length];
            final ByteArrayInputStream b = new ByteArrayInputStream(this.buf);
            final DataInputStream din = new DataInputStream(b);
            try{
                switch(this.dtype){
                    case Descriptor.DTYPE_BYTE:
                        for(int i = 0; i < array.length; i++)
                            array[i] = din.readByte();
                        return array;
                    case Descriptor.DTYPE_UBYTE:
                        for(int i = 0; i < array.length; i++)
                            array[i] = (din.readByte() & 0xFF);
                        return array;
                    case Descriptor.DTYPE_SHORT:
                        for(int i = 0; i < array.length; i++)
                            array[i] = (din.readShort() & 0xFFFF);
                        return array;
                    case Descriptor.DTYPE_USHORT:
                        for(int i = 0; i < array.length; i++)
                            array[i] = (din.readShort() & 0xFFFF);
                        return array;
                    case Descriptor.DTYPE_LONG:
                        for(int i = 0; i < array.length; i++)
                            array[i] = din.readInt();
                        return array;
                    case Descriptor.DTYPE_ULONG:
                        for(int i = 0; i < array.length; i++)
                            array[i] = (din.readInt() & 0xFFFFFFFFl);
                        return array;
                    case Descriptor.DTYPE_LONGLONG:
                        for(int i = 0; i < array.length; i++)
                            array[i] = din.readLong();
                        return array;
                    case Descriptor.DTYPE_ULONGLONG:
                        long l;
                        for(int i = 0; i < array.length; i++)
                            array[i] = (float)((l = din.readLong()) & 0x7FFFFFFF) - (float)(l & 0x80000000);
                        return array;
                    case Descriptor.DTYPE_FLOAT:
                        for(int i = 0; i < array.length; i++)
                            array[i] = din.readFloat();
                        return array;
                    case Descriptor.DTYPE_DOUBLE:
                        for(int i = 0; i < array.length; i++)
                            array[i] = (float)din.readDouble();
                        return array;
                }
            }finally{
                din.close();
                b.close();
            }
            return array;
        }
        */
    }
    public static final class RealArray{
        private final double[] doubleArray;
        private final float[]  floatArray;
        private final boolean  isDouble;
        public final boolean   isLong;
        private final long[]   longArray;

        public RealArray(final double[] doubleArray){
            if(DEBUG.M) System.out.println("mdsDataProvider.RealArray(" + doubleArray + ")");
            this.doubleArray = doubleArray;
            this.floatArray = null;
            this.longArray = null;
            this.isDouble = true;
            this.isLong = false;
        }

        public RealArray(final float[] floatArray){
            if(DEBUG.M) System.out.println("mdsDataProvider.RealArray(" + floatArray + ")");
            this.doubleArray = null;
            this.floatArray = floatArray;
            this.longArray = null;
            this.isDouble = false;
            this.isLong = false;
        }

        public RealArray(final long[] longArray){
            if(DEBUG.M) System.out.println("mdsDataProvider.RealArray(" + longArray + ")");
            this.doubleArray = null;
            this.floatArray = null;
            for(int i = 0; i < longArray.length; i++)
                longArray[i] = jScopeFacade.convertFromSpecificTime(longArray[i]);
            this.longArray = longArray;
            this.isDouble = false;
            this.isLong = true;
        }

        public final double[] getDoubleArray() {
            if(DEBUG.M) System.out.println("mdsDataProvider.RealArray.getDoubleArray()");
            if(DEBUG.D) System.out.println(">> " + this.isLong + this.isDouble + (this.floatArray != null) + (this.doubleArray != null));
            if(this.isLong) return null;
            if(this.isDouble) return this.doubleArray;
            final double[] doubleArray = new double[this.floatArray.length];
            for(int i = 0; i < this.floatArray.length; i++)
                doubleArray[i] = this.floatArray[i];
            return doubleArray;
        }

        public final float[] getFloatArray() {
            if(DEBUG.M) System.out.println("mdsDataProvider.RealArray.getFloatArray()");
            if(this.isLong) return null;
            if(!this.isDouble) return this.floatArray;
            final float[] floatArray = new float[this.doubleArray.length];
            for(int i = 0; i < this.doubleArray.length; i++)
                floatArray[i] = (float)this.doubleArray[i];
            return floatArray;
        }

        public final long[] getLongArray() {
            if(DEBUG.M) System.out.println("mdsDataProvider.RealArray.getLongArray()");
            return this.longArray;
        }
    }

    public static final byte[] copy(final byte[] array) {
        final byte[] out = new byte[array.length];
        System.arraycopy(array, 0, out, 0, out.length);
        return out;
    }

    public static final Color[] copy(final Color[] array) {
        final Color[] out = new Color[array.length];
        System.arraycopy(array, 0, out, 0, out.length);
        return out;
    }

    public static final double[] copy(final double[] array) {
        final double[] out = new double[array.length];
        System.arraycopy(array, 0, out, 0, out.length);
        return out;
    }

    public static final float[] copy(final float[] array) {
        final float[] out = new float[array.length];
        System.arraycopy(array, 0, out, 0, out.length);
        return out;
    }

    public static final int[] copy(final int[] array) {
        final int[] out = new int[array.length];
        System.arraycopy(array, 0, out, 0, out.length);
        return out;
    }

    public static final long[] copy(final long[] array) {
        final long[] out = new long[array.length];
        System.arraycopy(array, 0, out, 0, out.length);
        return out;
    }

    public static final short[] copy(final short[] array) {
        final short[] out = new short[array.length];
        System.arraycopy(array, 0, out, 0, out.length);
        return out;
    }
}
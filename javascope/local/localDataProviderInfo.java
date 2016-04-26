package local;

class localDataProviderInfo{
    int[] dims;
    int   dtype;
    int   pixelSize;

    public localDataProviderInfo(final int dtype, final int pixelSize, final int dims[]){
        this.dtype = dtype;
        this.pixelSize = pixelSize;
        this.dims = dims;
    }
}

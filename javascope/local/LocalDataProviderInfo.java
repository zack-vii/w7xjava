package local;

class LocalDataProviderInfo{
    int[] dims;
    int   dtype;
    int   pixelSize;

    public LocalDataProviderInfo(final int dtype, final int pixelSize, final int dims[]){
        this.dtype = dtype;
        this.pixelSize = pixelSize;
        this.dims = dims;
    }
}

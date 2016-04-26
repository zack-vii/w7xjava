package twu;

// -------------------------------------------------------------------------------------------------
// TWUFetchOptions
//
// A support class for users of the TWUSignal class. This class handles the query options
// used access segments of a TWU dataset.
//
// The TWU* classes (with TWU in three capitals) are written so that they can also be used
// in other java based applications and not only by jScope. Please keep the code of these
// separate of jScope specific elements.
//
// Written by Marco van de Giessen <A.P.M.vandeGiessen@phys.uu.nl>, originally as a nested
// class in TwuDataProvider.java, now a proper 'first class' class in its own source file.
//
// $Id$
//
// For the modification history, see the CVS log.
// -------------------------------------------------------------------------------------------------
final public class TWUFetchOptions{
    public static TWUFetchOptions NewCopy(final TWUFetchOptions opt) {
        return opt.NewCopy();
    }
    private int start = 0;
    private int step  = 1;
    private int total = -1;

    public TWUFetchOptions(){}

    // defaults to the options for retrieving
    // the full signal. (i.e. no subsampling.)
    public TWUFetchOptions(final int sta, final int ste, final int tot){
        this.start = sta;
        this.step = ste;
        this.total = tot;
    }

    public void clip(final int twupLengthTotal) {
        final int length = twupLengthTotal;
        if((length <= 0) || (length <= this.start)){
            this.start = 0;
            this.step = 1;
            this.total = 0;
            return;
        }
        if(this.start < 0) this.start = 0;
        if(this.step < 1) this.step = 1;
        if(this.total < 0) this.total = length;
        final int requestedEnd = this.start + (this.total - 1) * this.step;
        int overshoot = requestedEnd - (length - 1);
        if(overshoot > 0){
            overshoot %= this.step;
            if(overshoot > 0) overshoot -= this.step;
        }
        final int realEnd = (length - 1) + overshoot;
        // got a valid range now :
        this.total = (realEnd - this.start) / this.step + 1;
    }

    public boolean equalsForBulkData(final TWUFetchOptions opt) {
        // simple approach, assumes it's already been clipped
        // or at least it's incrementing (eg. step > 0, total >= 0).
        return(this.start == opt.start && this.step == opt.step && this.total == opt.total);
    }

    public int getStart() {
        return this.start;
    }

    public int getStep() {
        return this.step;
    }

    public int getTotal() {
        return this.total;
    }

    public TWUFetchOptions NewCopy() {
        return new TWUFetchOptions(this.start, this.step, this.total);
    }

    @Override
    public String toString() {
        return "TWUFetchOptions(" + this.start + ", " + this.step + ", " + this.total + ")";
    }
}
// -------------------------------------------------------------------------------------------------
// End of: $Id$
// -------------------------------------------------------------------------------------------------

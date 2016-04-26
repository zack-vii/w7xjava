package twu;

// -------------------------------------------------------------------------------------------------
// twuSimpleFrameData
// A "simple" implementation of "FrameData" for signals from a TEC Web-Umbrella (TWU) server.
//
// $Id$
//
// -------------------------------------------------------------------------------------------------
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import jScope.ConnectionEvent;
import jScope.FrameData;
import jScope.Frames;

// -------------------------------------------------------------------------------------------------
class twuSimpleFrameData implements FrameData{
    byte                    buf[];
    private final Dimension dim             = null;
    String                  error;
    int                     first_frame_idx = -1;
    String                  in_x, in_y;
    int                     mode            = -1;
    private int             n_frames        = 0;
    int                     pixel_size;
    private twuDataProvider provider        = null;
    private int             st_idx          = -1, end_idx = -1;
    float                   time_max, time_min;
    private float           times[]         = null;

    public twuSimpleFrameData(final twuDataProvider dp, final String in_y, final String in_x, final float time_min, final float time_max) throws IOException{
        int i;
        float t;
        float all_times[] = null;
        this.provider = dp;
        this.in_y = in_y;
        this.in_x = in_x;
        this.time_min = time_min;
        this.time_max = time_max;
        /* Da modificare per multi frame */
        if(in_x == null || in_x.length() == 0) all_times = new float[352 / 3];
        else all_times = this.provider.GetFloatArray(in_x);
        for(i = 0; i < all_times.length; i++)
            all_times[i] = (float)(-0.1 + 0.06 * i);
        // if(all_times == null){ throw(new IOException("Frame time evaluation error")); }
        for(i = 0; i < all_times.length; i++){
            t = all_times[i];
            if(t > time_max) break;
            if(t >= time_min){
                if(this.st_idx == -1) this.st_idx = i;
            }
        }
        this.end_idx = i;
        if(this.st_idx == -1) throw(new IOException("No frames found between " + time_min + " - " + time_max));
        this.n_frames = this.end_idx - this.st_idx;
        this.times = new float[this.n_frames];
        int j = 0;
        for(i = this.st_idx; i < this.end_idx; i++)
            this.times[j++] = all_times[i];
    }

    @Override
    public byte[] GetFrameAt(int idx) throws IOException {
        if(idx == this.first_frame_idx && this.buf != null) return this.buf;
        // b_img = MdsDataProvider.this.GetFrameAt(in_y, st_idx+idx);
        // Da modificare per leggere i frames
        idx *= 3;
        final ConnectionEvent ce = new ConnectionEvent(this, "Loading Image " + idx, 0, 0);
        this.provider.DispatchConnectionEvent(ce);
        final StringTokenizer st = new StringTokenizer(this.in_y, "/", true);
        String str = new String();
        final int nt = st.countTokens();
        for(int i = 0; i < nt - 1; i++)
            str = str + st.nextToken();
        String img_name = "00000" + idx;
        img_name = img_name.substring(img_name.length() - 6, img_name.length());
        str = str + img_name + ".jpg";
        final URL url = new URL(str);
        final URLConnection url_con = url.openConnection();
        int size = url_con.getContentLength();
        /* Sometimes size < 0 and an exception is thrown */
        /* Taliercio 27/02/2003 */
        byte b_img[] = null;
        if(size > 0){
            int offset = 0, num_read = 0;
            // byte b_img[] = new byte[size];
            b_img = new byte[size];
            final InputStream is = url_con.getInputStream();
            while(size > 0 && num_read != -1){
                num_read = is.read(b_img, offset, size);
                size -= num_read;
                offset += num_read;
            }
        }
        return b_img;
    }

    @Override
    public Dimension GetFrameDimension() {
        return this.dim;
    }

    @Override
    public float[] GetFrameTimes() {
        final float dtimes[] = new float[this.times.length];
        for(int i = 0; i < this.times.length; i++)
            dtimes[i] = this.times[i];
        return dtimes;
    }

    @Override
    public int GetFrameType() throws IOException {
        if(this.mode != -1) return this.mode;
        int i;
        for(i = 0; i < this.n_frames; i++){
            this.buf = this.GetFrameAt(i);
            if(this.buf != null) break;
        }
        this.first_frame_idx = i;
        this.mode = Frames.DecodeImageType(this.buf);
        return this.mode;
    }

    @Override
    public int GetNumFrames() {
        return this.n_frames;
    }
}
// -------------------------------------------------------------------------------------------------
// End of: $Id$
// -------------------------------------------------------------------------------------------------

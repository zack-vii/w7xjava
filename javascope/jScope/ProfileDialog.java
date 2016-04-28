package jScope;

/* $Id$ */
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class ProfileDialog extends JDialog implements WaveformListener{
    private static final String     TITLE[]        = {"X profile", "Y profile", "Pixel time profile"};
    private final WaveformContainer profile_container;
    private final int               row[]          = {2};
    private final Waveform          source_profile;
    private Waveform                w_profile_line = null;
    private final Waveform          wave[]         = new Waveform[2];

    ProfileDialog(final JFrame parent, final Waveform source_profile, final boolean reversed){
        super(parent, "Profile Dialog");
        this.source_profile = source_profile;
        source_profile.addWaveformListener(this);
        this.profile_container = new WaveformContainer(this.row, false);
        final WavePopup wp = new WavePopup();
        this.profile_container.setPopupMenu(wp);
        for(int i = 0; i < 2; i++){
            this.wave[i] = new Waveform();
            this.wave[i].SetTitle(ProfileDialog.TITLE[i]);
        }
        this.profile_container.add(this.wave);
        this.getContentPane().add(this.profile_container);
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(final WindowEvent e) {
                if(ProfileDialog.this.source_profile != null){
                    ProfileDialog.this.source_profile.setSendProfile(false);
                    ProfileDialog.this.source_profile.removeWaveformListener(ProfileDialog.this);
                }
                ProfileDialog.this.dispose();
            }
        });
        this.SetReversed(reversed);
        this.setAlwaysOnTop(true);
    }

    public final void addProfileLine() {
        this.w_profile_line = new Waveform();
        this.profile_container.add(this.w_profile_line, 3, 1);
        this.w_profile_line.SetTitle("Line Profile");
        this.profile_container.update();
    }

    @Override
    public final void processWaveformEvent(final WaveformEvent e) {
        final WaveformEvent we = e;
        final int we_id = we.getID();
        switch(we_id){
            case WaveformEvent.PROFILE_UPDATE:
                if(this.isShowing()){
                    if(e.frame_type == FrameData.BITMAP_IMAGE_32 || e.frame_type == FrameData.BITMAP_IMAGE_16){
                        this.updateProfiles(e.name, e.x_pixel, e.y_pixel, e.time_value, e.values_x, e.start_pixel_x, e.values_y, e.start_pixel_y);
                        if(e.values_line != null) this.updateProfileLine(e.values_line);
                        else this.removeProfileLine();
                    }else{
                        this.updateProfiles(e.name, e.x_pixel, e.y_pixel, e.time_value, e.pixels_x, e.start_pixel_x, e.pixels_y, e.start_pixel_y);
                        if(e.pixels_line != null) this.updateProfileLine(e.pixels_line);
                        else this.removeProfileLine();
                    }
                }
                break;
        }
    }

    public final void removeProfileLine() {
        if(this.w_profile_line == null) return;
        this.profile_container.removeComponent(this.w_profile_line);
        this.w_profile_line = null;
        this.profile_container.update();
    }

    public final void SetReversed(final boolean reversed) {
        for(final Waveform w : this.wave)
            w.SetReversed(reversed);
    }

    public final synchronized void updateProfileLine(final float values_line[]) {
        final float xt[] = new float[values_line.length];
        if(this.w_profile_line == null) this.addProfileLine();
        for(int i = 0; i < values_line.length; i++)
            xt[i] = i;
        this.w_profile_line.Update(xt, values_line);
    }

    public final synchronized void updateProfileLine(final int pixels_line[]) {
        final float x[] = new float[pixels_line.length];
        final float xt[] = new float[pixels_line.length];
        if(this.w_profile_line == null) this.addProfileLine();
        for(int i = 0; i < pixels_line.length; i++){
            x[i] = pixels_line[i] & 0xff;
            xt[i] = i;
        }
        this.w_profile_line.Update(xt, x);
    }

    public final synchronized void updateProfiles(final String name, final int x_pixel, final int y_pixel, final double time, final float values_x[], final int start_pixel_x, final float values_y[], final int start_pixel_y) {
        this.setTitle("Profile Dialog - " + name + " x_pix : " + x_pixel + " y_pix : " + y_pixel + " time : " + time);
        if(values_x != null && values_x.length > 0){
            final float xt[] = new float[values_x.length];
            for(int i = 0; i < values_x.length; i++)
                xt[i] = (float)start_pixel_x + i;
            this.wave[0].setProperties("expr=" + name + "\nx_pix=" + x_pixel + "\ny_pix=" + y_pixel + "\ntime=" + time + "\n");
            this.wave[0].Update(xt, values_x);
        }
        if(values_y != null && values_y.length > 0){
            final float yt[] = new float[values_y.length];
            for(int i = 0; i < values_y.length; i++)
                yt[i] = (float)start_pixel_y + i;
            this.wave[1].setProperties("expr=" + name + "\nx_pix=" + x_pixel + "\ny_pix=" + y_pixel + "\ntime=" + time + "\n");
            this.wave[1].Update(yt, values_y);
        }
    }

    public final synchronized void updateProfiles(final String name, final int x_pixel, final int y_pixel, final double time, final int pixels_x[], final int start_pixel_x, final int pixels_y[], final int start_pixel_y) {
        this.setTitle("Profile Dialog - " + name + " x_pix : " + x_pixel + " y_pix : " + y_pixel + " time : " + time);
        if(pixels_x != null && pixels_x.length > 0){
            final float x[] = new float[pixels_x.length];
            final float xt[] = new float[pixels_x.length];
            for(int i = 0; i < pixels_x.length; i++){
                x[i] = pixels_x[i] & 0xff;
                xt[i] = (float)start_pixel_x + i;
            }
            this.wave[0].setProperties("expr=" + name + "\nx_pix=" + x_pixel + "\ny_pix=" + y_pixel + "\ntime=" + time + "\n");
            this.wave[0].Update(xt, x);
        }
        if(pixels_y != null && pixels_y.length > 0){
            final float y[] = new float[pixels_y.length];
            final float yt[] = new float[pixels_y.length];
            for(int i = 0; i < pixels_y.length; i++){
                y[i] = pixels_y[i] & 0xff;
                yt[i] = (float)start_pixel_y + i;
            }
            this.wave[1].setProperties("expr=" + name + "\nx_pix=" + x_pixel + "\ny_pix=" + y_pixel + "\ntime=" + time + "\n");;
            this.wave[1].Update(yt, y);
        }
    }
}

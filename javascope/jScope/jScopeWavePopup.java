package jscope;

/* $Id$ */
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.Timer;

@SuppressWarnings("serial")
final class jScopeWavePopup extends MultiWavePopup{
    protected JMenuItem           refresh;
    protected JMenuItem           selectWave;
    protected JSeparator          sep1;
    private final SetupDataDialog setup_dialog;

    public jScopeWavePopup(final SetupDataDialog setup_dialog, final ColorMapDialog colorMapDialog){
        super(null);
        this.setColorMapDialog(colorMapDialog);
        this.setup.setText("Setup data source...");
        this.setup_dialog = setup_dialog;
        this.selectWave = new JMenuItem("Select wave panel");
        this.selectWave.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(jScopeWavePopup.this.wave != ((WaveformManager)jScopeWavePopup.this.parent).getSelected()) ((WaveformManager)jScopeWavePopup.this.parent).select(jScopeWavePopup.this.wave);
                else((WaveformManager)jScopeWavePopup.this.parent).deselect();
            }
        });
        this.sep1 = new JSeparator();
        this.refresh = new JMenuItem("Refresh");
        this.refresh.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ((jScopeWaveContainer)jScopeWavePopup.this.parent).refresh(((jScopeMultiWave)jScopeWavePopup.this.wave), "Refresh ");
            }
        });
        /*
                sep3 = new JSeparator();
                saveAsText = new JMenuItem("Save as text ...");
                saveAsText.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        ( (jScopeWaveContainer) jScopeWavePopup.this.parent).saveAsText( ( (
                            jScopeMultiWave) wave), false);
                    }
                }
                );
         */
        /*************
         * profile_dialog = new JMenuItem("Show profile dialog");
         * profile_dialog.addActionListener(new ActionListener()
         * {
         * public void actionPerformed(ActionEvent e)
         * {
         * ((jScopeWaveContainer)jScopeWavePopup.this.parent).ShowProfileDialog(((jScopeMultiWave)wave));
         * }
         * }
         * );
         **************/
    }

    @Override
    protected void positionLegend(final Point p) {
        super.positionLegend(p);
        ((jScopeMultiWave)this.wave).wi.showLegend(true);
        ((jScopeMultiWave)this.wave).wi.setLegendPosition(((jScopeMultiWave)this.wave).getLegendXPosition(), ((jScopeMultiWave)this.wave).getLegendYPosition());
    }

    @Override
    protected void removeLegend() {
        super.removeLegend();
        ((jScopeMultiWave)this.wave).wi.showLegend(false);
    }

    @Override
    public void setColor(final int idx) {
        super.setColor(idx);
        final jScopeMultiWave w = (jScopeMultiWave)this.wave;
        final int sigIdx = w.getSelectedSignal();
        if(sigIdx != -1 && w.wi.colors_idx[sigIdx] != idx){
            w.wi.colors_idx[w.getSelectedSignal()] = idx % Waveform.colors.length;
            w.setCrosshairColor(idx);
        }
    }

    @Override
    public void setDeselectPoint(final Waveform w) {
        final String f_name = System.getProperty("jScope.save_selected_points");
        if(w.showMeasure() && f_name != null && f_name.length() != 0){
            long shot = 0;
            final jScopeMultiWave mw = (jScopeMultiWave)w;
            if(mw.wi.shots != null) shot = mw.wi.shots[mw.getSelectedSignal()];
            try{
                boolean exist = false;
                final File f = new File(f_name);
                if(f.exists()) exist = true;
                final BufferedWriter out = new BufferedWriter(new FileWriter(f_name, true));
                if(!exist){
                    out.write(" Shot X1 Y1 X2 Y2");
                    out.newLine();
                }
                out.write(" " + shot + w.getIntervalPoints());
                out.newLine();
                out.close();
            }catch(final IOException e){}
        }
        super.setDeselectPoint(w);
    }

    @Override
    protected void setInterpolate(final boolean state) {
        super.setInterpolate(state);
        final jScopeMultiWave w = (jScopeMultiWave)this.wave;
        w.wi.interpolates[w.getSelectedSignal()] = state;
    }

    @Override
    public void setMarker(final int idx) {
        super.setMarker(idx);
        final jScopeMultiWave w = (jScopeMultiWave)this.wave;
        if(w.wi.markers[w.getSelectedSignal()] != idx) w.wi.markers[w.getSelectedSignal()] = idx;
    }

    @Override
    public void setMarkerStep(final int step) {
        super.setMarkerStep(step);
        final jScopeMultiWave w = (jScopeMultiWave)this.wave;
        if(w.wi.markers_step[w.getSelectedSignal()] != step) w.wi.markers_step[w.getSelectedSignal()] = step;
    }

    @Override
    protected void setMenu() {
        super.setMenu();
        this.wave = super.wave;
        // remove_panel.setEnabled(((WaveformManager)parent).getWaveformCount() > 1);
        jScopeFacade.jScopeSetUI(this);
    }

    @Override
    protected void setMenuItem(final boolean is_image) {
        super.setMenuItem(is_image);
        this.insert(this.refresh, this.getComponentCount() - 2);
        this.setup.setEnabled((this.setup_dialog != null));
        if(is_image){
            this.insert(this.profile_dialog, 1);
        }else{
            this.insert(this.selectWave, 2);
            this.add(this.sep3);
            this.add(this.saveAsText);
        }
        if(this.wave != null && ((jScopeMultiWave)this.wave).wi.num_waves == 1) ((jScopeMultiWave)this.wave).wi.signal_select = 0;
    }

    @Override
    protected void setMenuLabel() {
        super.setMenuLabel();
        if(!this.wave.isImage()){
            if(this.wave.isSelected()) this.selectWave.setText("Deselect wave panel");
            else this.selectWave.setText("Select wave panel");
        }else{
            this.profile_dialog.setEnabled(!this.wave.isSendProfile());
        }
    }

    @Override
    protected void setMode1D(final int mode) {
        final jScopeMultiWave w = (jScopeMultiWave)this.wave;
        w.wi.mode1D[w.getSelectedSignal()] = mode;
        super.setMode1D(mode);
    }

    @Override
    protected void setMode2D(final int mode) {
        final jScopeMultiWave w = (jScopeMultiWave)this.wave;
        w.wi.mode2D[w.getSelectedSignal()] = mode;
        super.setMode2D(mode);
    }

    @Override
    public void setSignalState(final String label, final boolean state) {
        final jScopeMultiWave w = (jScopeMultiWave)this.wave;
        w.setSignalState(label, state);
    }

    @Override
    protected void showDialog() {
        jScopeWavePopup.this.showSetupDialog();
    }

    protected void showSetupDialog() {
        final jScopeMultiWave w = (jScopeMultiWave)this.wave;
        if(w.mode == Waveform.MODE_POINT) // && w.wi.signal_select != -1)
        {
            this.setup_dialog.selectSignal(w.getSelectedSignal());
        }else if(w.getShowSignalCount() > 0 || w.is_image && w.wi.num_waves != 0) this.setup_dialog.selectSignal(1);
        final Timer t = new Timer(20, new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent ae) {
                final Point p = ((WaveformManager)jScopeWavePopup.this.parent).getWavePosition(jScopeWavePopup.this.wave);
                jScopeWavePopup.this.setup_dialog.show(jScopeWavePopup.this.wave, p.x, p.y);
            }
        });
        t.setRepeats(false);
        t.start();
    }
}

package jScope;

/* $Id$ */
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MultiWavePopup extends WavePopup{
    static final long       serialVersionUID = 864135413574153L;
    protected JMenuItem     legend, remove_legend;
    protected JMenu         signalList;
    protected MultiWaveform wave             = null;

    public MultiWavePopup(){
        this(null);
    }

    public MultiWavePopup(final SetupWaveformParams setup_params){
        super(setup_params);
        this.legend = new JMenuItem("Position legend");
        this.legend.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(!MultiWavePopup.this.wave.isFixedLegend() || !MultiWavePopup.this.wave.IsShowLegend()) MultiWavePopup.this.PositionLegend(MultiWavePopup.this.getLocation());
                else if(MultiWavePopup.this.wave.isFixedLegend()) MultiWavePopup.this.RemoveLegend();
            }
        });
        this.legend.setEnabled(false);
        this.remove_legend = new JMenuItem("Remove legend");
        this.remove_legend.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                MultiWavePopup.this.RemoveLegend();
            }
        });
        this.remove_legend.setEnabled(false);
        this.signalList = new JMenu("Signals");
        this.signalList.setEnabled(false);
    }

    protected Waveform getWave() {
        return super.wave;
    }

    @Override
    protected void InitOptionMenu() {
        int sig_idx;
        final String s_name[] = this.wave.GetSignalsName();
        final boolean s_state[] = this.wave.GetSignalsState();
        if(!(s_name != null && s_state != null && s_name.length > 0 && s_name.length > 0 && s_name.length == s_state.length)) return;
        final boolean state = (this.wave.mode == Waveform.MODE_POINT || this.wave.GetShowSignalCount() == 1);
        this.markerList.setEnabled(state);
        this.colorList.setEnabled(state);
        this.set_point.setEnabled(this.wave.mode == Waveform.MODE_POINT);
        if(state){
            if(this.wave.GetShowSignalCount() == 1) sig_idx = 0;
            else sig_idx = this.wave.GetSelectedSignal();
            final boolean state_m = state && (this.wave.GetMarker(sig_idx) != Signal.NONE && this.wave.GetMarker(sig_idx) != Signal.POINT);
            this.markerStep.setEnabled(state_m);
            WavePopup.SelectListItem(this.markerList_bg, this.wave.GetMarker(sig_idx));
            int st;
            for(st = 0; st < Signal.markerStepList.length; st++)
                if(Signal.markerStepList[st] == this.wave.GetMarkerStep(sig_idx)) break;
            WavePopup.SelectListItem(this.markerStep_bg, st);
            WavePopup.SelectListItem(this.colorList_bg, this.wave.GetColorIdx(sig_idx));
        }else this.markerStep.setEnabled(false);
        JCheckBoxMenuItem ob;
        if(s_name != null){
            if(this.signalList.getItemCount() != 0) this.signalList.removeAll();
            this.signalList.setEnabled(s_name.length != 0);
            this.legend.setEnabled(s_name.length != 0);
            for(int i = 0; i < s_name.length; i++){
                ob = new JCheckBoxMenuItem(s_name[i]);
                this.signalList.add(ob);
                ob.setState(s_state[i]);
                ob.addItemListener(new ItemListener(){
                    @Override
                    public void itemStateChanged(final ItemEvent e) {
                        final Object target = e.getSource();
                        MultiWavePopup.this.SetSignalState(((JCheckBoxMenuItem)target).getText(), ((JCheckBoxMenuItem)target).getState());
                        MultiWavePopup.this.wave.Repaint(true);
                    }
                });
            }
        }
        if(this.wave.isFixedLegend()){
            if(this.wave.IsShowLegend()) this.legend.setText("Hide Legend");
            else this.legend.setText("Show Legend");
        }else{
            this.legend.setText("Position Legend");
            if(this.wave.IsShowLegend()) this.remove_legend.setEnabled(true);
            else this.remove_legend.setEnabled(false);
        }
    }

    protected void PositionLegend(final Point p) {
        this.wave.SetLegend(p);
    }

    protected void RemoveLegend() {
        this.wave.RemoveLegend();
    }

    @Override
    public void SetColor(final int idx) {
        if(this.wave.GetColorIdx(this.wave.GetSelectedSignal()) != idx) this.wave.SetColorIdx(this.wave.GetSelectedSignal(), idx);
    }

    protected void SetInterpolate(final boolean state) {
        this.wave.SetInterpolate(this.wave.GetSelectedSignal(), state);
    }

    @Override
    public void SetMarker(final int idx) {
        if(this.wave.GetMarker(this.wave.GetSelectedSignal()) != idx) this.wave.SetMarker(this.wave.GetSelectedSignal(), idx);
    }

    @Override
    public void SetMarkerStep(final int step) {
        if(this.wave.GetMarkerStep(this.wave.GetSelectedSignal()) != step) this.wave.SetMarkerStep(this.wave.GetSelectedSignal(), step);
    }

    @Override
    protected void SetMenu() {
        this.wave = (MultiWaveform)super.wave;
        super.SetMenu();
    }

    @Override
    protected void SetMenuItem(final boolean is_image) {
        int start = 0;
        super.SetMenuItem(is_image);
        if(!is_image){
            if(this.parent instanceof WaveformManager) start += 2;
            this.insert(this.legend, start + 1);
            if(this.wave.isFixedLegend()){
                this.insert(this.signalList, start + 4);
                this.legend.setText("Show Legend");
            }else{
                this.insert(this.remove_legend, start + 2);
                this.insert(this.signalList, start + 5);
            }
        }
    }

    @Override
    protected void SetMode2D(final int mode) {
        this.wave.setSignalMode(this.wave.GetSelectedSignal(), mode);
    }

    @Override
    protected void SetSignalMenu() {
        super.SetSignalMenu();
        if(this.wave.GetShowSignalCount() == 0){
            this.legend.setEnabled(false);
            this.remove_legend.setEnabled(false);
            this.signalList.setEnabled(false);
        }
    }

    public void SetSignalState(final String label, final boolean state) {
        this.wave.SetSignalState(label, state);
    }
}

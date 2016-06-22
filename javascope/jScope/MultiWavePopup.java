package jscope;

/* $Id$ */
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MultiWavePopup extends WavePopup{
    protected JMenuItem     legend, remove_legend;
    protected JMenu         signalList;
    protected MultiWaveform wave = null;

    public MultiWavePopup(){
        this(null);
    }

    public MultiWavePopup(final SetupWaveformParams setup_params){
        super(setup_params);
        this.legend = new JMenuItem("Position legend");
        this.legend.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                if(!MultiWavePopup.this.wave.isFixedLegend() || !MultiWavePopup.this.wave.isShowLegend()) MultiWavePopup.this.positionLegend(MultiWavePopup.this.getLocation());
                else if(MultiWavePopup.this.wave.isFixedLegend()) MultiWavePopup.this.removeLegend();
            }
        });
        this.legend.setEnabled(false);
        this.remove_legend = new JMenuItem("Remove legend");
        this.remove_legend.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                MultiWavePopup.this.removeLegend();
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
    protected void initOptionMenu() {
        int sig_idx;
        final String s_name[] = this.wave.getSignalsName();
        final boolean s_state[] = this.wave.getSignalsState();
        if(!(s_name != null && s_state != null && s_name.length > 0 && s_name.length > 0 && s_name.length == s_state.length)) return;
        final boolean state = (this.wave.mode == Waveform.MODE_POINT || this.wave.getShowSignalCount() == 1);
        this.markerList.setEnabled(state);
        this.colorList.setEnabled(state);
        this.set_point.setEnabled(this.wave.mode == Waveform.MODE_POINT);
        if(state){
            if(this.wave.getShowSignalCount() == 1) sig_idx = 0;
            else sig_idx = this.wave.getSelectedSignal();
            final boolean state_m = state && (this.wave.getMarker(sig_idx) != Signal.NONE && this.wave.getMarker(sig_idx) != Signal.POINT);
            this.markerStep.setEnabled(state_m);
            WavePopup.selectListItem(this.markerList_bg, this.wave.getMarker(sig_idx));
            int st;
            for(st = 0; st < Signal.markerStepList.length; st++)
                if(Signal.markerStepList[st] == this.wave.getMarkerStep(sig_idx)) break;
            WavePopup.selectListItem(this.markerStep_bg, st);
            WavePopup.selectListItem(this.colorList_bg, this.wave.getColorIdx(sig_idx));
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
                        MultiWavePopup.this.setSignalState(((JCheckBoxMenuItem)target).getText(), ((JCheckBoxMenuItem)target).getState());
                        MultiWavePopup.this.wave.repaint(true);
                    }
                });
            }
        }
        if(this.wave.isFixedLegend()){
            if(this.wave.isShowLegend()) this.legend.setText("Hide Legend");
            else this.legend.setText("Show Legend");
        }else{
            this.legend.setText("Position Legend");
            if(this.wave.isShowLegend()) this.remove_legend.setEnabled(true);
            else this.remove_legend.setEnabled(false);
        }
    }

    protected void positionLegend(final Point p) {
        this.wave.setLegend(p);
    }

    protected void removeLegend() {
        this.wave.removeLegend();
    }

    @Override
    public void setColor(final int idx) {
        if(this.wave.getColorIdx(this.wave.getSelectedSignal()) != idx) this.wave.setColorIdx(this.wave.getSelectedSignal(), idx);
    }

    protected void setInterpolate(final boolean state) {
        this.wave.setInterpolate(this.wave.getSelectedSignal(), state);
    }

    @Override
    public void setMarker(final int idx) {
        if(this.wave.getMarker(this.wave.getSelectedSignal()) != idx) this.wave.setMarker(this.wave.getSelectedSignal(), idx);
    }

    @Override
    public void setMarkerStep(final int step) {
        if(this.wave.getMarkerStep(this.wave.getSelectedSignal()) != step) this.wave.setMarkerStep(this.wave.getSelectedSignal(), step);
    }

    @Override
    protected void setMenu() {
        this.wave = (MultiWaveform)super.wave;
        super.setMenu();
    }

    @Override
    protected void setMenuItem(final boolean is_image) {
        int start = 0;
        super.setMenuItem(is_image);
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
    protected void setMode2D(final int mode) {
        this.wave.setSignalMode(this.wave.getSelectedSignal(), mode);
    }

    @Override
    protected void setSignalMenu() {
        super.setSignalMenu();
        if(this.wave.getShowSignalCount() == 0){
            this.legend.setEnabled(false);
            this.remove_legend.setEnabled(false);
            this.signalList.setEnabled(false);
        }
    }

    public void setSignalState(final String label, final boolean state) {
        this.wave.setSignalState(label, state);
    }
}

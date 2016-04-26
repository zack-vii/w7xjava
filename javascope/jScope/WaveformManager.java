package jScope;

/* $Id$ */
import java.awt.Component;
import java.awt.Point;

/**
 * This interface can be implemented by (Multi)Waveform panel container to perfom interaction between (Multi)Waveform object, autoscale action, copy/paste action etc..
 *
 * @see WaveformContainer
 */
public interface WaveformManager{
    /**
     * Set the same scale factor of the argument waveform to all waveform
     *
     * @param curr_w
     *            a waveform
     * @see Waveform
     * @see MultiWaveform
     */
    public void AllSameScale(Waveform curr_w);

    /**
     * Set x scale factor of all waveform equals to argument waveform
     *
     * @param curr_w
     *            a waveform
     * @see Waveform
     * @see MultiWaveform
     */
    public void AllSameXScale(Waveform curr_w);

    /**
     * Autoscale y axis and set x axis equals to argument waveform
     *
     * @param curr_w
     *            a waveform
     * @see Waveform
     * @see MultiWaveform
     */
    public void AllSameXScaleAutoY(Waveform curr_w);

    /**
     * Set y scale factor of all waveform equals to argument waveform
     *
     * @param curr_w
     *            a waveform
     * @see Waveform
     * @see MultiWaveform
     */
    public void AllSameYScale(Waveform curr_w);

    /**
     * Autoscale operation on all waveforms
     *
     * @see Waveform
     * @see MultiWaveform
     */
    public void AutoscaleAll();

    /**
     * Autoscale operation on all images
     *
     * @see Waveform
     * @see MultiWaveform
     */
    public void AutoscaleAllImages();

    /**
     * Autoscale y axis on all waveform
     *
     * @see Waveform
     * @see MultiWaveform
     */
    public void AutoscaleAllY();

    /**
     * Deselect waveform.
     *
     * @see Waveform
     * @see MultiWaveform
     */
    public void Deselect();

    /**
     * Get current waveform selected as copy source
     *
     * @return copy source waveform
     */
    public Waveform GetCopySource();

    public Component getMaximizeComponent();

    /**
     * Get current selected waveform.
     *
     * @return current selected waveform or null
     * @see Waveform
     * @see MultiWaveform
     */
    public Waveform GetSelected();

    /**
     * Get the number of waveform in the container
     *
     * @return number of waveform in the container
     */
    public int GetWaveformCount();

    /**
     * Return row, column position on the panel
     *
     * @param w
     *            a waveform
     */
    public Point getWavePosition(Waveform w);

    public boolean isMaximize();

    public void maximizeComponent(Waveform w);

    /**
     * Perform copy operation
     *
     * @param dest
     *            destination waveform
     * @param source
     *            source waveform
     */
    public void NotifyChange(Waveform dest, Waveform source);

    /**
     * Remove a waveform.
     *
     * @param w
     *            waveform to remove
     */
    public void removePanel(Waveform w);

    /**
     * Reset all waveform scale factor.
     *
     * @see Waveform
     * @see MultiWaveform
     */
    public void ResetAllScales();

    /**
     * Select a waveform
     *
     * @param w
     *            waveform to select
     * @see Waveform
     * @see MultiWaveform
     */
    public void Select(Waveform w);

    /**
     * Set copy source waveform
     *
     * @param w
     *            copy source waveform
     * @see Waveform
     * @see MultiWaveform
     */
    public void SetCopySource(Waveform w);

    /**
     * Enable or disable measure mode
     *
     * @param state
     *            measure mode flag
     * @see Waveform
     * @see MultiWaveform
     */
    public void SetShowMeasure(boolean state);

    /**
     * Update crosshair position
     *
     * @param curr_x
     *            x axis position
     * @param w
     *            a waveform to update cross
     * @see Waveform
     * @see MultiWaveform
     */
    public void UpdatePoints(double curr_x, Waveform w);
}

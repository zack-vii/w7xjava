/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package jscope;

/**
 * @author manduchi Defines the methods called by WaveData to report regions of increased resolution or new data available
 */
public interface WaveDataListener{
    public void dataRegionUpdated(XYData data);

    public void legendUpdated(String name);

    public void sourceUpdated(XYData xydata);
}

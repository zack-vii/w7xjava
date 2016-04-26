/*
 * Extends mdsDataProvider to display an asynchronous waveform IN ADDITION to the waveform provided by the superclass
 * This is a template class.
 */
package mds;

import java.util.Vector;
import jScope.AsynchDataSource;
import jScope.WaveDataListener;
import jScope.XYData;

/**
 * @author manduchi
 */
public class mdsAsynchDataProvider extends mdsDataProvider{
    // Inner class AsynchWaevdata handles the generation of the waveform
    class AsynchWaveData implements AsynchDataSource{
        Vector<WaveDataListener> listeners  = new Vector<WaveDataListener>();
        double                   sinePeriod = 1;

        @Override
        public void addDataListener(final WaveDataListener listener) {
            this.listeners.addElement(listener);
        }

        @Override
        public void startGeneration(final String expression) {
            // JUST A SAMPLE SINE GENERATION...TO BE REPLACED BY REAL WAVEFORMS
            // JUST ASSUMED THAT THE PASSED EXPRESSION IS THE SINE PERIOD
            try{
                this.sinePeriod = Double.parseDouble(expression);
            }catch(final Exception exc){}
            (new Thread(){
                @Override
                public void run() {
                    for(int i = 0; i < 100; i++){
                        try{
                            Thread.sleep(100);
                        }catch(final InterruptedException exc){}
                        if(!mdsAsynchDataProvider.this.asynchEnabled){
                            System.out.println("NOT ENABLED");
                            continue;
                        }
                        final double x[] = new double[]{i};
                        final float y[] = new float[]{(float)Math.sin(2 * Math.PI * AsynchWaveData.this.sinePeriod * i / 100.)};
                        for(int j = 0; j < AsynchWaveData.this.listeners.size(); j++){
                            AsynchWaveData.this.listeners.elementAt(j).dataRegionUpdated(new XYData(x, y, Double.POSITIVE_INFINITY, true, x[0], x[x.length - 1]));
                            AsynchWaveData.this.listeners.elementAt(j).legendUpdated("CICCIO" + i);
                        }
                    }
                }
            }).start();
        }
    } // End inner class AsynchWaveData
    boolean asynchEnabled = true;

    @Override
    public void enableAsyncUpdate(final boolean asynchEnabled) {
        this.asynchEnabled = asynchEnabled;
    }

    @Override
    public AsynchDataSource getAsynchSource() {
        return new AsynchWaveData();
    }
}

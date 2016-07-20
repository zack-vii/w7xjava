/*
 * AsynchDataSource interface is used by MdsDataProvider to handle asynchronour data generation
 */
package jscope;

/**
 * @author manduchi
 */
public interface AsynchDataSource{
    void addDataListener(WaveDataListener listener);

    void startGeneration(String expression);
}

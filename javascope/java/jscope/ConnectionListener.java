package jscope;

/**
 * $Id$
 * Interface ConnectionListener describes method processConnectionEvent called by the DataProvider
 * implementation to notify jScope of the current status of the data transfer. ConnectionListener objects
 * register themselves by means of DataProvider.addConnectionListener() method.
 *
 * @see ConnectionEvent
 * @see DataProvider
 */
import java.util.EventListener;

public interface ConnectionListener extends EventListener{
    public void processConnectionEvent(ConnectionEvent e);
}
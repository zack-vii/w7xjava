package jScope;

/* $Id$ */
import java.awt.AWTEvent;
import java.sql.Connection;

/**
 * ConnectionEvent instances describe the current status of the data transfer and are passed by the DataProvider
 * implementation to jScope by means of ConnectionListener.processConnectionEvent method.
 * ConnectionEvent instances can also signal a connection lost. In this case field id (inherited by AWTEvent) is set
 * to ConnectionEvent.LOST_CONNECTION, otherwise field id should be set to 0.
 *
 * @see Connection Listener
 * @see DataProvider
 */
@SuppressWarnings("serial")
public class ConnectionEvent extends AWTEvent{
    public static final int LOST_CONNECTION = AWTEvent.RESERVED_ID_MAX + 1;
    /**
     * Number ofbytes transferred so far.
     */
    int                     current_size;
    /**
     * Additional string information, shown in the status bar of jScope.
     */
    String                  info;
    /**
     * Total size of the data to be transferred.
     */
    int                     total_size;

    public ConnectionEvent(final Object source, final int total_size, final int current_size){
        super(source, 0);
        this.total_size = total_size;
        this.current_size = current_size;
        this.info = null;
    }

    public ConnectionEvent(final Object source, final int event_id, final String info){
        super(source, event_id);
        this.info = new String(info);
    }

    public ConnectionEvent(final Object source, final String info){
        super(source, 0);
        this.info = new String(info);
    }

    public ConnectionEvent(final Object source, final String info, final int total_size, final int current_size){
        super(source, 0);
        this.total_size = total_size;
        this.current_size = current_size;
        this.info = info;
    }

    public String getInfo() {
        return this.info;
    }
}

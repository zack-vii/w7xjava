package mds.mdsip;

/* $Id$ */
import java.awt.AWTEvent;
import jscope.DataProvider;

/**
 * An UpdateEvent instance is passed to UpdateEventListener implementations as argument of processUpdateEvent in order to notify the receipt of an asynchronous event triggering the display of a waveform.
 *
 * @see UpdateEventListener
 * @see DataProvider
 */
@SuppressWarnings("serial")
public class UpdateEvent extends AWTEvent{
    /**
     * The name of the event just received.
     */
    String name;

    public UpdateEvent(final Object source, final String event){
        super(source, 0);
        this.name = event;
    }

    public String getName() {
        return this.name;
    }
}

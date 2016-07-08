package jscope;

/* $Id$ */
import java.awt.AWTEvent;

@SuppressWarnings("serial")
public class WaveContainerEvent extends AWTEvent{
    public static final int END_UPDATE     = AWTEvent.RESERVED_ID_MAX + 5;
    public static final int KILL_UPDATE    = AWTEvent.RESERVED_ID_MAX + 6;
    public static final int START_UPDATE   = AWTEvent.RESERVED_ID_MAX + 4;
    public static final int WAVEFORM_EVENT = AWTEvent.RESERVED_ID_MAX + 7;
    private final String    info;
    private final AWTEvent  we;

    public WaveContainerEvent(final Object source, final AWTEvent we){
        super(source, WaveContainerEvent.WAVEFORM_EVENT);
        this.we = we;
        this.info = null;
    }

    public WaveContainerEvent(final Object source, final int event_id, final String info){
        super(source, event_id);
        this.info = info;
        this.we = null;
    }

    public final AWTEvent getEvent() {
        return this.we;
    }

    public final String getInfo() {
        return this.info;
    }
}

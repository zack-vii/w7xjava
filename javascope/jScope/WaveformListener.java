package jscope;

/* $Id$ */
import java.util.EventListener;

public interface WaveformListener extends EventListener{
    public void processWaveformEvent(WaveformEvent e);
}

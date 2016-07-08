package jscope;

/* $Id$ */
import java.io.IOException;
import javax.swing.JFrame;

final class NotConnectedDataProvider implements DataProvider{
    private static final String error = "Not Connected";

    @Override
    public void abort() {}

    @Override
    public void addConnectionListener(final ConnectionListener l) {}

    @Override
    public void addUpdateEventListener(final UpdateEventListener l, final String event) {}

    @Override
    public boolean checkProvider() {
        return true;
    }

    @Override
    public void dispose() {}

    @Override
    public String errorString() {
        return NotConnectedDataProvider.error;
    }

    @Override
    public Class getDefaultBrowser() {
        return null;
    }

    @Override
    public float getFloat(final String in) {
        return Float.parseFloat(in);
    }

    @Override
    public FrameData getFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        return null;
    }

    @Override
    public final String getLegendString(final String s) {
        return s;
    }

    @Override
    public long[] getShots(final String in) {
        return new long[]{0L};
    }

    @Override
    public String getString(final String in) {
        return "";
    }

    @Override
    public WaveData getWaveData(final String in) {
        return null;
    }

    @Override
    public WaveData getWaveData(final String in_y, final String in_x) {
        return null;
    }

    @Override
    public int inquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    @Override
    public void join() {}

    @Override
    public void removeConnectionListener(final ConnectionListener l) {}

    @Override
    public void removeUpdateEventListener(final UpdateEventListener l, final String event) {}

    @Override
    public void setArgument(final String arg) {}

    @Override
    public void setEnvironment(final String exp) {}

    @Override
    public boolean supportsTunneling() {
        return false;
    }

    @Override
    public void update(final String exp, final long s) {}
}
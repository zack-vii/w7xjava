package jScope;

/* $Id$ */
import java.io.IOException;
import javax.swing.JFrame;

final class NotConnectedDataProvider implements DataProvider{
    public static boolean DataPending() {
        return false;
    }

    public static float[] GetFloatArray(final String in) {
        return null;
    }

    public static float[] GetFloatArray(final String in_x, final String in_y, final float start, final float end) {
        return null;
    }

    public static WaveData GetResampledWaveData(final String in, final double start, final double end, final int n_points) {
        return null;
    }

    public static WaveData GetResampledWaveData(final String in_y, final String in_x, final double start, final double end, final int n_points) {
        return null;
    }

    public static void SetCompression(final boolean state) {}

    public static void setContinuousUpdate() {}

    public static boolean SupportsCompression() {
        return false;
    }

    public static boolean SupportsContinuous() {
        return false;
    }

    public static boolean SupportsFastNetwork() {
        return false;
    }
    final String error = "Not Connected";

    @Override
    public void abort() {}

    @Override
    public void AddConnectionListener(final ConnectionListener l) {}

    @Override
    public void AddUpdateEventListener(final UpdateEventListener l, final String event) {}

    @Override
    public boolean checkProvider() {
        return true;
    }

    @Override
    public void Dispose() {}

    public void enableAsyncUpdate(final boolean enable) {}

    @Override
    public String ErrorString() {
        return this.error;
    }

    @Override
    public Class getDefaultBrowser() {
        return null;
    }

    @Override
    public float GetFloat(final String in) {
        return Float.parseFloat(in);
    }

    @Override
    public FrameData GetFrameData(final String in_y, final String in_x, final float time_min, final float time_max) throws IOException {
        return null;
    }

    @Override
    public final String GetLegendString(final String s) {
        return s;
    }

    @Override
    public long[] GetShots(final String in) {
        return new long[]{0L};
    }

    @Override
    public String GetString(final String in) {
        return "";
    }

    @Override
    public WaveData GetWaveData(final String in) {
        return null;
    }

    @Override
    public WaveData GetWaveData(final String in_y, final String in_x) {
        return null;
    }

    @Override
    public int InquireCredentials(final JFrame f, final DataServerItem server_item) {
        return DataProvider.LOGIN_OK;
    }

    @Override
    public void join() {}

    @Override
    public void RemoveConnectionListener(final ConnectionListener l) {}

    @Override
    public void RemoveUpdateEventListener(final UpdateEventListener l, final String event) {}

    @Override
    public void SetArgument(final String arg) {}

    @Override
    public void SetEnvironment(final String exp) {}

    @Override
    public boolean SupportsTunneling() {
        return false;
    }

    @Override
    public void Update(final String exp, final long s) {}
}
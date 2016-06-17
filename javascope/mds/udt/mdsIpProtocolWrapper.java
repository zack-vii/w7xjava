package mds.udt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * mdsProtocolWrapper handles mdstcpip management for protocol plug in
 */
final public class mdsIpProtocolWrapper{
    class mdsIpInputStream extends InputStream{
        @Override
        public int read() throws IOException {
            if(mdsIpProtocolWrapper.this.connectionIdx == -1) throw new IOException("Not Connected");
            final byte[] readBuf = mdsIpProtocolWrapper.this.recv(mdsIpProtocolWrapper.this.connectionIdx, 1);
            if(readBuf == null) throw new IOException("Cannot Read Data");
            return readBuf[0];
        }

        @Override
        public int read(final byte buf[]) throws IOException {
            if(mdsIpProtocolWrapper.this.connectionIdx == -1) throw new IOException("Not Connected");
            final byte[] readBuf = mdsIpProtocolWrapper.this.recv(mdsIpProtocolWrapper.this.connectionIdx, buf.length);
            if(readBuf == null) throw new IOException("Cannot Read Data");
            System.arraycopy(readBuf, 0, buf, 0, buf.length);
            return buf.length;
        }

        @Override
        public int read(final byte buf[], final int offset, final int len) throws IOException {
            if(mdsIpProtocolWrapper.this.connectionIdx == -1) throw new IOException("Not Connected");
            final byte[] readBuf = mdsIpProtocolWrapper.this.recv(mdsIpProtocolWrapper.this.connectionIdx, len);
            if(readBuf == null || readBuf.length == 0) throw new IOException("Cannot Read Data");
            System.arraycopy(readBuf, 0, buf, offset, readBuf.length);
            return readBuf.length;
        }
    }
    class mdsIpOutputStream extends OutputStream{
        /*        public void flush() throws IOException
                {
        System.out.println("FLUSH..");
                    if(connectionIdx == -1)  throw new IOException("Not Connected");
                    mdsIpProtocolWrapper.this.flush(connectionIdx);
        System.out.println("FLUSH FATTO");
                }
         */@Override
        public void close() throws IOException {
            if(mdsIpProtocolWrapper.this.connectionIdx != -1){
                mdsIpProtocolWrapper.this.disconnect(mdsIpProtocolWrapper.this.connectionIdx);
                mdsIpProtocolWrapper.this.connectionIdx = -1;
            }
        }

        @Override
        public void write(final byte[] b) throws IOException {
            if(mdsIpProtocolWrapper.this.connectionIdx == -1) throw new IOException("Not Connected");
            final int numSent = mdsIpProtocolWrapper.this.send(mdsIpProtocolWrapper.this.connectionIdx, b, false);
            if(numSent == b.length) throw new IOException("Incomplete write");
        }

        @Override
        public void write(final int b) throws IOException {
            if(mdsIpProtocolWrapper.this.connectionIdx == -1) throw new IOException("Not Connected");
            final int numSent = mdsIpProtocolWrapper.this.send(mdsIpProtocolWrapper.this.connectionIdx, new byte[]{(byte)b}, false);
            if(numSent == -1) throw new IOException("Cannot Write Data");
        }
    }

    static{
        try{
            System.loadLibrary("Javamds");
        }catch(final UnsatisfiedLinkError e){
            javax.swing.JOptionPane.showMessageDialog(null, "Can't load data provider class LocalDataProvider : " + e, "Alert LocalDataProvider", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    /*
    public static void main(final String args[]) {//TODO:main
        final mdsIpProtocolWrapper mpw = new mdsIpProtocolWrapper("tcp");
        final int idx = mpw.connectTomds("tcp://ra22.igi.cnr.it:8100");
        System.out.println("Connected: " + idx);
    }
    */
    int connectionIdx = -1;

    public mdsIpProtocolWrapper(final String url){
        this.connectionIdx = this.connectTomds(url);
    }

    public native int connectTomds(String url);

    public native void disconnect(int connectionId);

    public native void flush(int connectionId);

    public InputStream getInputStream() {
        return new mdsIpInputStream();
    }

    public OutputStream getOutputStream() {
        return new mdsIpOutputStream();
    }

    public native byte[] recv(int connectionId, int len);

    public native int send(int connectionId, byte[] sendBuf, boolean nowait);
}

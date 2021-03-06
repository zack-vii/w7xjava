package mds.mdsip;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import debug.DEBUG;
import mds.Mds;
import mds.MdsEvent;
import mds.MdsException;
import mds.MdsListener;
import mds.UpdateEventListener;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.Pointer;

public class MdsIp extends Mds{
    private final class MdsConnect extends Thread{
        private boolean close = false;
        private boolean tried = false;

        public MdsConnect(){
            super(MdsIp.this.getName("MdsConnect"));
            this.setDaemon(true);
        }

        synchronized public final void close() {
            this.close = true;
            this.notify();
        }

        synchronized public void retry() {
            this.tried = false;
            this.notify();
        }

        @Override
        public final void run() {
            try{
                while(!this.close)
                    try{
                        MdsIp.this.connectToServer();
                        this.setTried(true);
                        synchronized(this){
                            this.wait();
                        }
                    }catch(final IOException ce){
                        this.setTried(true);
                        try{
                            Thread.sleep(3000);
                        }catch(final InterruptedException ie){
                            System.err.println(this.getName() + ": isInterrupted1");
                        }
                    }
            }catch(final InterruptedException ie){
                System.err.println(this.getName() + ": isInterrupted2");
                this.close = true;
            }
            this.setTried(true);
        }

        synchronized private void setTried(final boolean tried) {
            this.tried = tried;
            if(tried) MdsIp.this.notifyTried();
        }

        synchronized public void update() {
            this.notifyAll();
        }
    }
    private final class MRT extends Thread // mds Receive Thread
    {
        boolean killed = false;
        Message message;

        public MRT(){
            super(MdsIp.this.getName("MRT"));
            this.setDaemon(true);
        }

        public Message getMessage() {
            if(DEBUG.D) System.out.println("getMessage()");
            long time;
            if(DEBUG.D) time = System.nanoTime();
            Message msg = null;
            try{
                for(;;)
                    synchronized(this){
                        this.wait(1000);
                        if(this.killed) return null;
                        if(this.message == null) continue;
                        msg = this.message;
                        this.message = null;
                        break;
                    }
            }catch(final InterruptedException e){
                Thread.currentThread().interrupt();
            }
            if(DEBUG.D) System.out.println(msg.msglen + "B in " + (System.nanoTime() - time) / 1e9 + "sec" + (msg.body.capacity() == 0 ? "" : " (" + msg.asString().substring(0, (msg.body.capacity() < 64) ? msg.body.capacity() : 64) + ")"));
            return msg;
        }

        @Override
        public void run() {
            try{
                while(true){
                    final Message message = Message.receive(MdsIp.this.dis, MdsIp.this.mdslisteners);
                    if(DEBUG.A) System.out.println(String.format("%s received %s", this.getName(), message.toString()));
                    if(message.dtype == DTYPE.EVENT){
                        final PMET PmdsEvent = new PMET();
                        PmdsEvent.setEventid(message.body.get(12));
                        PmdsEvent.start();
                    }else{
                        synchronized(this){
                            this.message = message;
                            this.notify();
                        }
                    }
                }
            }catch(final Exception e){
                synchronized(this){
                    this.killed = true;
                    this.notifyAll();
                }
                if(MdsIp.this.connected){
                    this.message = null;
                    MdsIp.this.connected = false;
                    if(MdsIp.this.connectThread != null && MdsIp.this.connectThread.tried) MdsIp.this.connectThread.update();
                    (new Thread(){
                        @Override
                        public void run() {
                            MdsIp.this.dispatchMdsEvent(new MdsEvent(MdsIp.this, MdsEvent.LOST_CONTEXT, "Lost connection from " + MdsIp.this.provider.host));
                        }
                    }).start();
                    if(!(e instanceof SocketException)) e.printStackTrace();
                }
            }
        }

        public synchronized void waitExited() {
            while(!this.killed)
                try{
                    this.wait();
                }catch(final InterruptedException exc){
                    System.err.println(this.getName() + ": isInterrupted");
                }
        }
    } // End MRT class
    class PMET extends Thread // Process mds Event Thread
    {
        int    eventId = -1;
        String eventName;

        public PMET(){
            super(MdsIp.this.getName("PMET"));
            this.setDaemon(true);
        }

        @Override
        public void run() {
            if(this.eventName != null) MdsIp.this.dispatchUpdateEvent(this.eventName);
            else if(this.eventId != -1) MdsIp.this.dispatchUpdateEvent(this.eventId);
        }

        public void setEventid(final int id) {
            if(DEBUG.M){
                System.out.println("Received Event ID " + id);
            }
            this.eventId = id;
            this.eventName = null;
        }

        public void setEventName(final String name) {
            if(DEBUG.M){
                System.out.println("Received Event Name " + name);
            }
            this.eventId = -1;
            this.eventName = name;
        }
    }// end PMET class
    public final static class Provider{
        public static final String DEFAULT_HOST = "localhost";
        public static final int    DEFAULT_PORT = 8000;
        public static final String DEFAULT_USER = "JAVA_USER";
        public final String        host;
        public final int           port;
        public final String        user;                      // System.getProperty("user.name")
        private String             password;

        public Provider(final String host, final int port){
            this.user = Provider.DEFAULT_USER;
            this.host = host == null ? Provider.DEFAULT_HOST : host;
            this.port = port == 0 ? Provider.DEFAULT_PORT : port;
        }

        public Provider(final String host, final int port, final String user, final String password){
            this.user = user;
            this.host = host == null ? Provider.DEFAULT_HOST : host;
            this.port = port == 0 ? Provider.DEFAULT_PORT : port;
            this.password = password;
        }

        public Provider(final String provider, final String password){
            if(provider == null || provider.length() == 0){
                this.user = Provider.DEFAULT_USER;
                this.host = Provider.DEFAULT_HOST;
                this.port = Provider.DEFAULT_PORT;
            }else{
                final int at = provider.indexOf("@");
                final int cn = provider.indexOf(":");
                this.user = at < 0 ? Provider.DEFAULT_USER : provider.substring(0, at);
                this.host = cn < 0 ? provider.substring(at + 1) : provider.substring(at + 1, cn);
                this.port = cn < 0 ? Provider.DEFAULT_PORT : Short.parseShort(provider.substring(cn + 1));
            }
            this.password = password;
        }

        @Override
        public final boolean equals(final Object obj) {
            if(obj == null || !(obj instanceof Provider)) return false;
            final Provider provider = (Provider)obj;
            return this.host.equalsIgnoreCase(provider.host) && this.port == provider.port && this.user.equals(provider.user);
        }

        public final MdsIp getConnection() {
            return new MdsIp(this);
        }

        @Override
        public final int hashCode() {
            return this.host.toLowerCase().hashCode() + this.port;
        }

        public final boolean queryPassword(final Component parent) {
            final JPanel panel = new JPanel(new GridLayout(2, 1));
            final JLabel label = new JLabel("Enter SSH password:");
            final JPasswordField pass = new JPasswordField(16);
            panel.add(label);
            panel.add(pass);
            final String[] options = new String[]{"OK", "Cancel"};
            final int option = JOptionPane.showOptionDialog(parent, panel, "SSH to " + this.toString(), JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if(option != 0) return false;
            return this.setPassword(new String(pass.getPassword()));
        }

        public final boolean setPassword(final String password) {
            if(this.password == null) return (this.password = password) != null;
            if(this.password.equals(password)) return false;
            this.password = password;
            return true;
        }

        @Override
        public final String toString() {
            return new StringBuilder(this.user.length() + this.host.length() + 7).append(this.user).append('@').append(this.host).append(':').append(this.port).toString();
        }
    }
    @SuppressWarnings("unchecked")
    private static final class SSHSocket extends Socket{
        private static final String     localhost = "localhost";
        private static Class            JSch, Session;
        private static Method           getSession, setConfig, setPassword, connect, setPortForwardingL, disconnect;
        private static Object           jsch;
        private static final Properties config    = new Properties();
        private static boolean          available = false;
        static{
            try{
                SSHSocket.JSch = Class.forName("com.jcraft.jsch.JSch");
                SSHSocket.Session = Class.forName("com.jcraft.jsch.Session");
                SSHSocket.getSession = SSHSocket.JSch.getMethod("getSession", String.class, String.class);
                SSHSocket.setConfig = SSHSocket.Session.getMethod("setConfig", Properties.class);
                SSHSocket.setPassword = SSHSocket.Session.getMethod("setPassword", String.class);
                SSHSocket.connect = SSHSocket.Session.getMethod("connect");
                SSHSocket.disconnect = SSHSocket.Session.getMethod("disconnect");
                SSHSocket.setPortForwardingL = SSHSocket.Session.getMethod("setPortForwardingL", int.class, String.class, int.class);
                SSHSocket.jsch = SSHSocket.JSch.newInstance();
                SSHSocket.config.put("StrictHostKeyChecking", "no");
                SSHSocket.available = true;
            }catch(final Exception e){
                System.err.println(e.getMessage());
            }
        }

        private static final Object[] openSession(final Provider provider) throws IOException {
            if(!SSHSocket.available) throw new IOException("JSch not found! SSH connection not available.");
            try{
                final Object session = SSHSocket.getSession.invoke(SSHSocket.jsch, provider.user, provider.host);
                SSHSocket.setConfig.invoke(session, SSHSocket.config);
                SSHSocket.setPassword.invoke(session, provider.password);
                SSHSocket.connect.invoke(session);
                return new Object[]{SSHSocket.setPortForwardingL.invoke(session, 0, SSHSocket.localhost, provider.port), session};
            }catch(final Exception e){
                if(e instanceof IOException) throw(IOException)e;
                throw new ConnectException(e.toString());
            }
        }
        private final Object session;

        private SSHSocket(final Object[] lport_session) throws IOException{
            super(SSHSocket.localhost, (Integer)lport_session[0]);
            this.session = lport_session[1];
        }

        public SSHSocket(final Provider provider) throws IOException{
            this(SSHSocket.openSession(provider));
        }

        @Override
        public void close() throws IOException {
            super.close();
            try{
                SSHSocket.disconnect.invoke(this.session);
            }catch(final Exception e){
                if(e instanceof IOException) throw(IOException)e;
                throw new IOException(e.toString());
            }
        }
    }
    private static final List<MdsIp> open_connections = Collections.synchronizedList(new ArrayList<MdsIp>());
    public static final int          LOGIN_OK         = 1, LOGIN_ERROR = 2, LOGIN_CANCEL = 3;
    private static final String      NOT_CONNECTED    = "Not Connected.";

    public static final boolean addSharedConnection(final MdsIp con) {
        synchronized(MdsIp.open_connections){
            return MdsIp.open_connections.add(con);
        }
    }

    public static final int closeSharedConnections() {
        for(final MdsIp con : MdsIp.open_connections)
            con.close();
        final int size = MdsIp.open_connections.size();
        MdsIp.open_connections.clear();
        return size;
    }

    public static final boolean removeSharedConnection(final MdsIp con) {
        synchronized(MdsIp.open_connections){
            return MdsIp.open_connections.remove(con);
        }
    }

    public static MdsIp sharedConnection(final Provider provider) {
        synchronized(MdsIp.open_connections){
            for(final MdsIp con : MdsIp.open_connections)
                if(con.provider.equals(provider)){
                    con.setPassword(provider.password);
                    con.connect();
                    return con;
                }
            final MdsIp con = new MdsIp(provider);
            if(con.connect()) MdsIp.open_connections.add(con);
            else con.close();
            return con;
        }
    }

    public static MdsIp sharedConnection(final String provider, final String password) {
        return MdsIp.sharedConnection(new Provider(provider, password));
    }
    private boolean        connected       = false;
    private MdsConnect     connectThread   = null;
    private InputStream    dis             = null;
    private OutputStream   dos             = null;
    private final Provider provider;
    private MRT            receiveThread   = null;
    private Socket         sock            = null;
    private boolean        use_compression = false;
    private final Object   mutex           = new Object();

    public MdsIp(final Provider provider){
        this(provider, null);
    }

    /** main constructor of the Connection class **/
    public MdsIp(final Provider provider, final MdsListener cl){
        this.addMdsListener(cl);
        this.provider = provider;
    }

    public MdsIp(final String provider){
        this(new Provider(provider, null));
    }

    public MdsIp(final String provider, final MdsListener cl){
        this(new Provider(provider, null), cl);
    }

    /** disconnect from server and close **/
    public final boolean close() {
        if(this.connectThread != null) this.connectThread.close();
        this.connectThread = null;
        this.disconnectFromServer();
        if(this.receiveThread != null) this.receiveThread.waitExited();
        this.receiveThread = null;
        this.dos = null;
        this.dis = null;
        return true;
    }

    /** re-/connects to the servers mdsip service **/
    public final boolean connect() {
        if(this.connected) return true;
        if(this.connectThread == null || !this.connectThread.isAlive()){
            this.connectThread = new MdsConnect();
            this.connectThread.start();
        }
        this.connectThread.retry();
        this.waitTried();
        return this.connected;
    }

    /** re-/connects to the servers mdsip service **/
    public final boolean connect(final boolean use_compression) {
        this.use_compression = use_compression;
        return this.connect();
    }

    private final void connectToServer() throws IOException {
        /* connect to server */
        if(this.provider.password != null) this.sock = new SSHSocket(this.provider);
        else this.sock = new Socket(this.provider.host, this.provider.port);
        System.out.println(this.sock.toString());
        this.sock.setTcpNoDelay(true);
        this.dis = this.sock.getInputStream();
        this.dos = this.sock.getOutputStream();
        /* connect to mdsip */
        final Message message = new Message(this.provider.user);
        message.useCompression(this.use_compression);
        message.send(this.dos);
        this.sock.setSoTimeout(3000);
        final Message msg = Message.receive(this.dis, null);
        this.sock.setSoTimeout(0);
        if(msg.header.get(4) == 0){
            this.close();
            return;
        }
        this.receiveThread = new MRT();
        this.receiveThread.start();
        this.connected = true;
        MdsIp.this.dispatchMdsEvent(new MdsEvent(this, MdsEvent.HAVE_CONTEXT, "Connected to " + this.provider.toString()));
    }

    private final void disconnectFromServer() {
        try{
            try{
                if(this.dos != null) this.dos.close();
            }finally{
                if(this.dis != null) this.dis.close();
            }
        }catch(final IOException e){
            System.err.println("The closing of sockets failed:\n" + e.getMessage());
        }
        this.connected = false;
    }

    @Override
    public void finalize() throws Throwable {
        try{
            this.close();
        }finally{
            super.finalize();
        }
    }

    private final Message getAnswer() throws MdsException {
        final Message message = this.receiveThread.getMessage();
        if(message == null) throw new MdsException("Null response from server", 0);
        if((message.status & 1) == 0 && message.status != 0 && message.dtype == DTYPE.T){
            final String msg = message.asString();
            throw new MdsException((msg == null || msg.isEmpty()) ? "<empty>" : msg, message.status);
        }
        return message;
    }

    @Override
    public final ByteBuffer getByteBuffer(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getMessage(ctx, expr, false, args).body;
    }

    @Override
    public final <D extends Descriptor> Descriptor getDescriptor(final Pointer ctx, final String expr, final Class<D> cls, final Descriptor... args) throws MdsException {
        final Message msg = this.getMessage(ctx, expr, true, args);
        if(msg.dtype == DTYPE.T) throw new MdsException(msg.toString());
        return Mds.bufferToClass(msg.body, cls);
    }

    public final String getHost() {
        return this.provider.host;
    }

    public final Message getMessage(Pointer ctx, final String expr, final boolean serialize, final Descriptor... args) throws MdsException {
        if(DEBUG.M) System.out.println("mdsConnection.mdsValue(\"" + expr + "\", " + args + ", " + serialize + ")");
        if(!this.connected) throw new MdsException(MdsIp.NOT_CONNECTED);
        this.setActive();
        final Message msg;
        byte idx = 0;
        final StringBuffer cmd = new StringBuffer(expr.length() + 128);
        if(serialize) cmd.append("_ans=*;MdsShr->MdsSerializeDscOut(xd((");
        if(args != null && args.length > 0){
            final boolean[] atomic = new boolean[args.length];
            for(int i = 0; i < args.length; i++)
                if(!(atomic[i] = args[i].isAtomic())) args[i] = args[i].serializeDsc();
            if(expr.indexOf("$") == -1){ // If no $ args specified, build argument list
                cmd.append(expr);
                for(int i = 0; i < args.length; i++)
                    cmd.append(i == 0 ? '(' : ',').append(atomic[i] ? "$" : MdsIp.serialStr);
                cmd.append(')');
            }else{
                final Matcher m = Pattern.compile("\\$").matcher(expr);
                int pos = 0;
                for(int i = 0; i < args.length && m.find(); i++){
                    cmd.append(expr.substring(pos, m.start())).append(atomic[i] ? "$" : MdsIp.serialStr);
                    pos = m.end();
                }
                cmd.append(expr.substring(pos));
            }
        }else cmd.append(expr);
        if(serialize) cmd.append(";)),xd(_ans));_ans");
        if(ctx == Pointer.NULL) ctx = null;
        synchronized(this.mutex){
            if(ctx != null){
                this.sendArg((byte)0, DTYPE.T, (byte)2, null, "TreeShr->TreeRestoreContext(val($))".getBytes());
                ctx.toMessage((byte)1, (byte)2).send(this.dos);
                if(this.getAnswer().status != 1) throw new MdsException("Could not restore context.");
            }
            try{
                if(args != null && args.length > 0){
                    final byte totalarg = (byte)(args.length + 1);
                    this.sendArg(idx++, DTYPE.T, totalarg, null, cmd.toString().getBytes());
                    for(final Descriptor d : args)
                        d.toMessage(idx++, totalarg).send(this.dos);
                }else new Message(cmd.toString()).send(this.dos);
                msg = this.getAnswer();
            }catch(final IOException e){
                throw new MdsException("Connection.getMessage", e);
            }
            if(ctx != null){
                try{
                    new Message("TreeShr->TreeSaveContext:P()").send(this.dos);
                    final Message ctx_msg = this.getAnswer();
                    ctx.setValue(ctx_msg.body);
                }catch(final IOException e){
                    ctx.setValue(0);
                }
            }
        }
        if(msg == null) throw new MdsException("Could not get IO for " + this.provider.host, 0);
        return msg;
    }

    public final Message getMessage(final String expr, final boolean serialize, final Descriptor... args) throws MdsException {
        return this.getMessage(null, expr, serialize, args);
    }

    private final String getName(final String classname) {
        return new StringBuilder(128).append(classname).append('(').append(this.provider.user).append('@').append(this.provider.host).append(':').append(this.provider.port).append(')').toString();
    }

    public final int getPort() {
        return this.provider.port;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public final String getUser() {
        return this.provider.user;
    }

    public final boolean isConnected() {
        return this.connected;
    }

    @Override
    public final String isReady() {
        this.waitTried();
        if(!this.isConnected()) return MdsIp.NOT_CONNECTED;
        return null;
    }

    synchronized public final void mdsRemoveEvent(final UpdateEventListener l, final String event) {
        int eventid;
        if((eventid = this.removeEvent(l, event)) == -1) return;
        try{
            this.sendArg((byte)0, DTYPE.T, (byte)2, null, Message.EVENTCANREQUEST.getBytes());
            this.sendArg((byte)1, DTYPE.T, (byte)2, null, new byte[]{(byte)eventid});
        }catch(final IOException e){
            System.err.print("Could not get IO for " + this.provider.host + ":\n" + e.getMessage());
        }
    }

    @Override
    synchronized protected final void mdsSetEvent(final String event, final int eventid) {
        try{
            this.sendArg((byte)0, DTYPE.T, (byte)3, null, Message.EVENTASTREQUEST.getBytes());
            this.sendArg((byte)1, DTYPE.T, (byte)3, null, event.getBytes());
            this.sendArg((byte)2, DTYPE.BU, (byte)3, null, new byte[]{(byte)(eventid)});
        }catch(final IOException e){
            System.err.print("Could not get IO for " + this.provider.host + ":\n" + e.getMessage());
        }
    }

    synchronized private void notifyTried() {
        this.notifyAll();
    }

    public final void removeFromShare() {
        if(MdsIp.open_connections.contains(this)) MdsIp.open_connections.remove(this);
    }

    private final void sendArg(final byte descr_idx, final byte dtype, final byte nargs, final int dims[], final byte body[]) throws MdsException {
        final Message msg = new Message(descr_idx, dtype, nargs, dims, body);
        try{
            msg.send(this.dos);
        }catch(final IOException e){
            throw new MdsException("Connection.sendArg", e);
        }
    }

    public final void setPassword(final String password) {
        this.provider.setPassword(password);
    }

    @Override
    public final String toString() {
        final String provider = this.provider.toString();
        return new StringBuilder(provider.length() + 12).append("MdsIp(").append(provider).append(")").toString();
    }

    synchronized private void waitTried() {
        if(this.connectThread == null) return;
        if(!this.connectThread.tried) try{
            this.wait();
        }catch(final InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
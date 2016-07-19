package mds.mdsip;

/* $Id$ */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import debug.DEBUG;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Missing;

public class Connection{
    static class EventItem{
        int                         eventid;
        Vector<UpdateEventListener> listener = new Vector<UpdateEventListener>();
        String                      name;

        public EventItem(final String name, final int eventid, final UpdateEventListener l){
            this.name = name;
            this.eventid = eventid;
            this.listener.addElement(l);
        }

        @Override
        public String toString() {
            return new String("Event name = " + this.name + " Event id = " + this.eventid);
        }
    }
    private final class MdsConnect extends Thread{
        private boolean close = false;
        private boolean tried = false;

        public MdsConnect(){
            super(Connection.this.getName("MdsConnect"));
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
                        Connection.this.connectToServer();
                        Connection.this.connectToMds(Connection.this.use_compression);
                        this.setTried(true);
                        synchronized(this){
                            this.wait();
                        }
                    }catch(final ConnectException ce){
                        this.setTried(true);
                        try{
                            Thread.sleep(3000);
                        }catch(final InterruptedException ie){}
                    }
            }catch(final InterruptedException ie){
                this.close = true;
            }catch(final IOException e){
                System.err.print("MdsConnect - No IO for " + Connection.this.provider.host + ":\n" + e.getMessage());
            }
        }

        synchronized private void setTried(final boolean tried) {
            this.tried = tried;
            if(tried) Connection.this.notifyTried();
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
            super(Connection.this.getName("MRT"));
            this.setDaemon(true);
        }

        public Message getMessage() {
            if(DEBUG.D) System.out.println("GetMessage()");
            long time;
            if(DEBUG.D) time = System.nanoTime();
            synchronized(this){
                while(!this.killed && this.message == null)
                    try{
                        this.wait();
                    }catch(final InterruptedException e){}
            }
            if(this.killed) return null;
            if(DEBUG.D) time = System.nanoTime() - time;
            final Message msg = this.message;
            this.message = null;
            if(DEBUG.D) System.out.println(msg.msglen + "B in " + time / 1e9 + "sec" + (msg.body.capacity() == 0 ? "" : " (" + msg.asString().substring(0, (msg.body.capacity() < 64) ? msg.body.capacity() : 64) + ")"));
            return msg;
        }

        @Override
        public void run() {
            try{
                while(true){
                    final Message message = Message.receive(Connection.this.dis, Connection.this.connection_listener);
                    if(DEBUG.A) System.out.println(String.format("%s received %s", this.getName(), message.toString()));
                    if(message.dtype == DTYPE.EVENT){
                        final PMET PmdsEvent = new PMET();
                        PmdsEvent.setEventid(message.body.get(12));
                        PmdsEvent.start();
                    }else{
                        Connection.this.pending_count--;
                        synchronized(this){
                            this.message = message;
                            if(Connection.this.pending_count == 0) this.notify();
                        }
                    }
                }
            }catch(final Exception e){
                synchronized(this){
                    this.killed = true;
                    this.notifyAll();
                    if(Connection.this.connected){
                        this.message = null;
                        Connection.this.connected = false;
                        if(Connection.this.connectThread != null) Connection.this.connectThread.update();
                        (new Thread(){
                            @Override
                            public void run() {
                                final ConnectionEvent ce = new ConnectionEvent(Connection.this, ConnectionEvent.LOST_CONNECTION, "Lost connection from : " + Connection.this.provider.host);
                                Connection.this.dispatchConnectionEvent(ce);
                            }
                        }).start();
                        if(!(e instanceof SocketException)) e.printStackTrace();
                    }
                }
            }
        }

        public synchronized void waitExited() {
            while(!this.killed)
                try{
                    this.wait();
                }catch(final InterruptedException exc){}
        }
    } // End MRT class
    class PMET extends Thread // Process mds Event Thread
    {
        int    eventId = -1;
        String eventName;

        public PMET(){
            super(Connection.this.getName("PMET"));
            this.setDaemon(true);
        }

        @Override
        public void run() {
            if(this.eventName != null) Connection.this.dispatchUpdateEvent(this.eventName);
            else if(this.eventId != -1) Connection.this.dispatchUpdateEvent(this.eventId);
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
        public final String        user         = System.getProperty("user.name");

        public Provider(final String provider){
            if(provider == null || provider.length() == 0){
                // this.user = Provider.DEFAULT_USER;
                this.host = Provider.DEFAULT_HOST;
                this.port = Provider.DEFAULT_PORT;
            }else{
                final int at = provider.indexOf("@");
                final int cn = provider.indexOf(":");
                // this.user = at < 0 ? Provider.DEFAULT_USER : provider.substring(0, at);
                this.host = cn < 0 ? provider.substring(at + 1) : provider.substring(at + 1, cn);
                this.port = cn < 0 ? Provider.DEFAULT_PORT : Short.parseShort(provider.substring(cn + 1));
            }
        }

        @Override
        public final boolean equals(final Object obj) {
            if(obj == null || !(obj instanceof Provider)) return false;
            final Provider provider = (Provider)obj;
            return this.host.equalsIgnoreCase(provider.host) && this.port == provider.port;
        }

        public final Connection getConnection() {
            return new Connection(this);
        }

        @Override
        public final int hashCode() {
            return this.host.toLowerCase().hashCode() + this.port;
        }

        @Override
        public final String toString() {
            return new StringBuilder(this.user.length() + this.host.length() + 7).append(this.user).append('@').append(this.host).append(':').append(this.port).toString();
        }
    }
    private static Connection  active;
    public static final int    LOGIN_OK       = 1, LOGIN_ERROR = 2, LOGIN_CANCEL = 3;
    static final int           MAX_NUM_EVENTS = 256;
    public static final String serialStr      = "(_d=*;_s=MdsShr->MdsSerializeDscIn(ref($),xd(_d));_d;)";

    private static <D extends Descriptor> Descriptor bufferToClass(final ByteBuffer b, final Class<D> cls) throws MdsException {
        if(b.capacity() == 0) return null;// NoData
        if(cls == null || cls == Descriptor.class) return Descriptor.deserialize(b);
        if(cls == Descriptor_A.class) return Descriptor_A.deserialize(b);
        try{
            return cls.getConstructor(ByteBuffer.class).newInstance(b);
        }catch(final Exception e){
            throw new MdsException(cls.getSimpleName(), e);
        }
    }

    public static final Connection getActiveConnection() {
        return Connection.active;// TODO: always up to date?
    }
    private boolean                                 connected           = false;
    private transient Vector<ConnectionListener>    connection_listener = new Vector<ConnectionListener>();
    private MdsConnect                              connectThread       = null;
    private InputStream                             dis                 = null;
    private DataOutputStream                        dos                 = null;
    private transient boolean[]                     event_flags         = new boolean[Connection.MAX_NUM_EVENTS];
    private transient Hashtable<Integer, EventItem> hashEventId         = new Hashtable<Integer, EventItem>();
    private transient Hashtable<String, EventItem>  hashEventName       = new Hashtable<String, EventItem>();
    private int                                     pending_count       = 0;
    private final Provider                          provider;
    private MRT                                     receiveThread       = null;
    private Socket                                  sock                = null;
    private boolean                                 use_compression     = false;

    public Connection(final Provider provider){
        this(provider, false, null);
    }

    public Connection(final Provider provider, final boolean use_compression){
        this(provider, use_compression, null);
    }

    public Connection(final Provider provider, final boolean use_compression, final ConnectionListener cl){
        this.addConnectionListener(cl);
        this.use_compression = use_compression;
        this.provider = provider;
        this.connect();
    }

    public Connection(final Provider provider, final ConnectionListener cl){
        this(provider, false, cl);
    }

    public Connection(final String provider){
        this(new Provider(provider));
    }

    public Connection(final String provider, final boolean use_compression){
        this(new Provider(provider), use_compression);
    }

    public Connection(final String provider, final boolean use_compression, final ConnectionListener cl){
        this(new Provider(provider), use_compression, cl);
    }

    public Connection(final String provider, final ConnectionListener cl){
        this(new Provider(provider), cl);
    }

    synchronized public final void addConnectionListener(final ConnectionListener l) {
        if(l == null) return;
        this.connection_listener.addElement(l);
    }

    synchronized private final int addEvent(final UpdateEventListener l, final String eventName) {
        int eventid = -1;
        EventItem eventItem;
        if(this.hashEventName.containsKey(eventName)){
            eventItem = this.hashEventName.get(eventName);
            if(!eventItem.listener.contains(l)) eventItem.listener.addElement(l);
        }else{
            eventid = this.getEventId();
            eventItem = new EventItem(eventName, eventid, l);
            this.hashEventName.put(eventName, eventItem);
            this.hashEventId.put(new Integer(eventid), eventItem);
        }
        return eventid;
    }

    public final boolean connect() {
        if(this.connectThread == null || !this.connectThread.isAlive()){
            this.connectThread = new MdsConnect();
            this.connectThread.start();
        }
        this.connectThread.retry();
        this.waitTried();
        return this.connected;
    }

    private final void connectToMds(final boolean use_compression) throws IOException {
        this.use_compression = use_compression;
        final Message message = new Message(this.provider.user);
        message.useCompression(use_compression);
        message.send(this.dos);
        this.sock.setSoTimeout(3000);
        Message.receive(this.dis, null);
        this.sock.setSoTimeout(0);
        this.receiveThread = new MRT();
        this.receiveThread.start();
        this.connected = true;
        Connection.this.dispatchConnectionEvent(new ConnectionEvent(this, "connected"));
    }

    private final void connectToServer() throws IOException {
        this.sock = new Socket(this.provider.host, this.provider.port);
        System.out.println(this.sock.toString());
        this.sock.setTcpNoDelay(true);
        this.dis = new BufferedInputStream(this.sock.getInputStream());
        this.dos = new DataOutputStream(new BufferedOutputStream(this.sock.getOutputStream()));
    }

    public final boolean disconnect() {
        if(this.connectThread != null) this.connectThread.close();
        this.connectThread = null;
        this.disconnectFromServer();
        if(this.receiveThread != null) this.receiveThread.waitExited();
        this.receiveThread = null;
        this.dos = null;
        this.dis = null;
        return true;
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
        if(!this.connection_listener.isEmpty()) this.connection_listener.removeAllElements();
        this.connected = false;
    }

    protected final void dispatchConnectionEvent(final ConnectionEvent e) {
        if(this.connection_listener != null) for(int i = 0; i < this.connection_listener.size(); i++)
            this.connection_listener.elementAt(i).processConnectionEvent(e);
    }

    private final void dispatchUpdateEvent(final EventItem eventItem) {
        final Vector<UpdateEventListener> eventListener = eventItem.listener;
        final UpdateEvent e = new UpdateEvent(this, eventItem.name);
        for(int i = 0; i < eventListener.size(); i++)
            eventListener.elementAt(i).processUpdateEvent(e);
    }

    synchronized private final void dispatchUpdateEvent(final int eventid) {
        if(this.hashEventId.containsKey(eventid)) this.dispatchUpdateEvent(this.hashEventId.get(eventid));
    }

    synchronized private final void dispatchUpdateEvent(final String eventName) {
        if(this.hashEventName.containsKey(eventName)) this.dispatchUpdateEvent(this.hashEventName.get(eventName));
    }

    @Override
    public void finalize() throws Throwable {
        try{
            this.disconnect();
        }finally{
            super.finalize();
        }
    }

    private final Message getAnswer() throws MdsException {
        synchronized(this){
            this.pending_count++;
        }
        final Message message = this.receiveThread.getMessage();
        if(message == null) throw new MdsException("Null response from server", 0);
        if((message.status & 1) == 0 && message.status != 0 && message.dtype == DTYPE.T){
            final String msg = message.asString();
            throw new MdsException((msg == null || msg.isEmpty()) ? "<empty>" : msg, message.status);
        }
        return message;
    }

    public byte getByte(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toByte();
    }

    public byte[] getByteArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toByteArray();
    }

    public ByteBuffer getByteBuffer(final String expr, final Descriptor... args) throws MdsException {
        return this.getMessage(expr, false, args).body;
    }

    public final <D extends Descriptor> Descriptor getDescriptor(final String expr, final Class<D> cls, final Descriptor... args) throws MdsException {
        final ByteBuffer b = this.getMessage(expr, true, args).body;
        return Connection.bufferToClass(b, cls);
    }

    public final <D extends Descriptor> Descriptor getDescriptor(final String expr, final Descriptor... args) throws MdsException {
        return this.getDescriptor(expr, Descriptor.class, args);
    }

    public final double getDouble(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toDouble();
    }

    public final double[] getDoubleArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toDoubleArray();
    }

    private final int getEventId() {
        int i;
        for(i = 0; i < Connection.MAX_NUM_EVENTS && this.event_flags[i]; i++);
        if(i == Connection.MAX_NUM_EVENTS) return -1;
        this.event_flags[i] = true;
        return i;
    }

    public final float getFloat(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toFloat();
    }

    public final float[] getFloatArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toFloatArray();
    }

    public final String getHost() {
        return this.provider.host;
    }

    public final int getInteger(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toInt();
    }

    public final int[] getIntegerArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toIntArray();
    }

    public final long getLong(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toLong();
    }

    public final long[] getLongArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toLongArray();
    }

    public final Message getMessage(final String expr, final boolean serialize, final Descriptor... args) throws MdsException {
        if(DEBUG.M) System.out.println("mdsConnection.mdsValue(\"" + expr + "\", " + args + ", " + serialize + ")");
        if(!this.connected) throw new MdsException("Not connected");
        this.setActive();
        byte idx = 0;
        final Message msg;
        final StringBuffer cmd = new StringBuffer(expr.length() + 128);
        if(serialize) cmd.append("_ans=*;MdsShr->MdsSerializeDscOut(xd((");
        if(args != null && args.length > 0){
            final boolean[] atomic = new boolean[args.length];
            for(int i = 0; i < args.length; i++)
                if(!(atomic[i] = args[i].isAtomic())) args[i] = args[i].serializeDsc();
            if(expr.indexOf("$") == -1){ // If no $ args specified, build argument list
                cmd.append(expr);
                for(int i = 0; i < args.length; i++)
                    cmd.append(i == 0 ? '(' : ',').append(atomic[i] ? "$" : Connection.serialStr);
                cmd.append(')');
            }else{
                final Matcher m = Pattern.compile("\\$").matcher(expr);
                int pos = 0;
                for(int i = 0; i < args.length && m.find(); i++){
                    cmd.append(expr.substring(pos, m.start())).append(atomic[i] ? "$" : Connection.serialStr);
                    pos = m.end();
                }
                cmd.append(expr.substring(pos));
            }
        }else cmd.append(expr);
        if(serialize) cmd.append(";)),xd(_ans));_ans");
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
        if(msg == null) throw new MdsException("Could not get IO for " + this.provider.host, 0);
        return msg;
    }

    private final String getName(final String classname) {
        if(this.sock == null) return new StringBuilder(128).append(classname).append('(').append(this.provider.user).append('@').append(this.provider.host).append(':').append(this.provider.port).append(')').toString();
        return new StringBuilder(128).append(classname).append('(').append(this.sock.getInetAddress()).append(", ").append(this.sock.getPort()).append(", ").append(this.sock.getLocalPort()).append(')').toString();
    }

    private final Descriptor getNumberArray(final String in, final Descriptor... args) throws MdsException {
        final Descriptor desc = this.getDescriptor(in, args);
        if(desc instanceof CString){
            if(desc.length > 0) throw new MdsException(desc.toString(), 0);
            return Missing.NEW;
        }
        return desc;
    }

    public final int getPort() {
        return this.provider.port;
    }

    public final String getProvider() {
        return this.provider.toString();
    }

    public short getShort(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toShort();
    }

    public short[] getShortArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(expr, args).toShortArray();
    }

    public final String getString(final String expr, final Descriptor... args) throws MdsException {
        final Descriptor desc = this.getDescriptor(expr, args);
        if(desc instanceof CString) return ((CString)desc).getValue();
        return desc.toString();
    }

    public final String getUser() {
        return this.provider.user;
    }

    public final boolean isConnected() {
        return this.connected;
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

    synchronized public final void mdsSetEvent(final UpdateEventListener l, final String event) {
        int eventid;
        if((eventid = this.addEvent(l, event)) == -1) return;
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

    synchronized public final void removeConnectionListener(final ConnectionListener l) {
        if(l == null) return;
        this.connection_listener.removeElement(l);
    }

    synchronized private final int removeEvent(final UpdateEventListener l, final String eventName) {
        int eventid = -1;
        if(this.hashEventName.containsKey(eventName)){
            final EventItem eventItem = this.hashEventName.get(eventName);
            eventItem.listener.remove(l);
            if(eventItem.listener.isEmpty()){
                eventid = eventItem.eventid;
                this.event_flags[eventid] = false;
                this.hashEventName.remove(eventName);
                this.hashEventId.remove(new Integer(eventid));
            }
        }
        return eventid;
    }

    private final void sendArg(final byte descr_idx, final byte dtype, final byte nargs, final int dims[], final byte body[]) throws MdsException {
        final Message msg = new Message(descr_idx, dtype, nargs, dims, body);
        try{
            msg.send(this.dos);
        }catch(final IOException e){
            throw new MdsException("Connection.sendArg", e);
        }
    }

    public final Connection setActive() {
        return Connection.active = this;
    }

    @Override
    public final String toString() {
        final String provider = this.provider.toString();
        return new StringBuilder(provider.length() + 12).append("Connection(").append(provider).append(")").toString();
    }

    synchronized private void waitTried() {
        if(!this.connectThread.tried) try{
            this.wait();
        }catch(final InterruptedException e){}
    }
}
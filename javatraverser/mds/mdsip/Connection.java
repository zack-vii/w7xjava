package mds.mdsip;

/* $Id$ */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Vector;
import debug.DEBUG;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_s.CString;

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
    private final class MRT extends Thread // mds Receive Thread
    {
        boolean killed = false;
        Message message;

        public MRT(final String name){
            this.setName(name);
        }

        public synchronized Message GetMessage() {
            // System.out.println("Get Message");
            long time;
            if(DEBUG.D) time = System.nanoTime();
            while(!this.killed && this.message == null)
                try{
                    this.wait();
                }catch(final InterruptedException e){}
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
                    final Message message = Message.Receive(Connection.this.dis, Connection.this.connection_listener);
                    if(DEBUG.A) System.out.println(String.format("%s received %s", this.getName(), message.toString()));
                    if(message.dtype == DTYPE.EVENT){
                        final PMET PmdsEvent = new PMET();
                        PmdsEvent.SetEventid(message.body.get(12));
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
                }
                if(Connection.this.connected){
                    this.message = null;
                    Connection.this.connected = false;
                    (new Thread(){
                        @Override
                        public void run() {
                            final ConnectionEvent ce = new ConnectionEvent(Connection.this, ConnectionEvent.LOST_CONNECTION, "Lost connection from : " + Connection.this.provider);
                            Connection.this.dispatchConnectionEvent(ce);
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
                }catch(final InterruptedException exc){}
        }
    } // End MRT class
    class PMET extends Thread // Process mds Event Thread
    {
        int    eventId = -1;
        String eventName;

        @Override
        public void run() {
            this.setName("Process mds Event Thread");
            if(this.eventName != null) Connection.this.dispatchUpdateEvent(this.eventName);
            else if(this.eventId != -1) Connection.this.dispatchUpdateEvent(this.eventId);
        }

        public void SetEventid(final int id) {
            if(DEBUG.M){
                System.out.println("Received Event ID " + id);
            }
            this.eventId = id;
            this.eventName = null;
        }

        public void SetEventName(final String name) {
            if(DEBUG.M){
                System.out.println("Received Event Name " + name);
            }
            this.eventId = -1;
            this.eventName = name;
        }
    }// end PMET class
    public static final int    DEFAULT_PORT   = 8000;
    public static final String DEFAULT_USER   = "JAVA_USER";
    public static final int    LOGIN_OK       = 1, LOGIN_ERROR = 2, LOGIN_CANCEL = 3;
    static final int           MAX_NUM_EVENTS = 256;

    private static <D extends Descriptor> Descriptor bufferToClass(final ByteBuffer b, final Class<D> cls) throws MdsException {
        if(cls == null || cls == Descriptor.class) return Descriptor.deserialize(b);
        if(cls == Descriptor_A.class) return Descriptor_A.deserialize(b);
        try{
            return cls.getConstructor(ByteBuffer.class).newInstance(b);
        }catch(final Exception e){
            throw new MdsException(cls.getSimpleName(), e);
        }
    }

    public static void main(final String[] args) throws Exception {// TODO
        final Connection mds = new Connection("localhost");
        mds.connectToMds(false);
        final Descriptor d = mds.mdsValue("[[[1.0],[2.0]],[[3.0],[4.0]]]");// BYTE([1,2,3,4,5,6,7,8,9,0])
        System.out.println(d);
    }
    public boolean                          connected;
    transient Vector<ConnectionListener>    connection_listener = new Vector<ConnectionListener>();
    protected InputStream                   dis;
    protected DataOutputStream              dos;
    public String                           error;
    transient boolean                       event_flags[]       = new boolean[Connection.MAX_NUM_EVENTS];
    transient Vector<EventItem>             event_list          = new Vector<EventItem>();
    transient Hashtable<Integer, EventItem> hashEventId         = new Hashtable<Integer, EventItem>();
    transient Hashtable<String, EventItem>  hashEventName       = new Hashtable<String, EventItem>();
    protected String                        host;
    int                                     pending_count       = 0;
    protected int                           port;
    protected String                        provider;
    MRT                                     receiveThread;
    protected Socket                        sock;
    protected String                        user;

    /*
    private synchronized void NotifyMessage() {
        notify();
        System.out.printf("-- Notify");
    }
     */
    public Connection(){
        this(null);
    }

    public Connection(final String provider){
        this.connected = false;
        this.sock = null;
        this.dis = null;
        this.dos = null;
        this.provider = provider;
        this.port = Connection.DEFAULT_PORT;
        this.host = null;
    }

    public final synchronized void addConnectionListener(final ConnectionListener l) {
        if(l == null) return;
        this.connection_listener.addElement(l);
    }

    public final synchronized int AddEvent(final UpdateEventListener l, final String eventName) {
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

    public Descriptor compile(final String expr) throws MdsException {
        return this.mdsValue(String.format("COMPILE('%s')", expr), Descriptor.class);
    }

    public synchronized boolean connectToMds(final boolean use_compression) {
        try{
            if(this.provider != null){
                this.connectToServer();
                final Message message = new Message(this.user);
                message.useCompression(use_compression);
                message.Send(this.dos);
                this.sock.setSoTimeout(3000);
                Message.Receive(this.dis, null);
                this.sock.setSoTimeout(0);
                // NOTE Removed check, unsuccessful in UDT
                // if((message.status & 1) != 0)
                // if(true){
                this.receiveThread = new MRT(String.format("MRT(%s:%d->%d)", this.sock.getInetAddress(), this.sock.getPort(), this.sock.getLocalPort()));
                this.receiveThread.start();
                this.connected = true;
                return true;
            }
            // error = "Could not get IO for : Host " + host +" Port "+ port + " User " + user;
            this.error = "Data provider host:port is <null>";
        }catch(final NumberFormatException e){
            this.error = "Data provider syntax error " + this.provider + " (host:port)";
        }catch(final UnknownHostException e){
            this.error = "Data provider: " + this.host + " port " + this.port + " unknown";
        }catch(final SocketTimeoutException e){
            this.error = "Connection timeout: " + this.provider + " does not respond.";
        }catch(final IOException e){
            this.error = "Could not get IO for " + this.provider + " " + e;
        }
        return false;
    }

    protected void connectToServer() throws IOException {
        this.host = this.getProviderHost();
        this.port = this.getProviderPort();
        this.user = this.getProviderUser();
        this.sock = new Socket(this.host, this.port);
        System.out.println(this.sock.toString());
        this.sock.setTcpNoDelay(true);
        this.dis = new BufferedInputStream(this.sock.getInputStream());
        this.dos = new DataOutputStream(new BufferedOutputStream(this.sock.getOutputStream()));
    }

    public final int disconnectFromMds() {
        try{
            if(this.connection_listener.size() > 0) this.connection_listener.removeAllElements();
            this.connected = false;
            this.dos.close();
            this.dis.close();
            this.receiveThread.waitExited();
            this.dos = null;
            this.dis = null;
        }catch(final IOException e){
            this.error.concat("Could not get IO for " + this.provider + e);
            return 0;
        }
        return 1;
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

    public final synchronized void dispatchUpdateEvent(final int eventid) {
        if(this.hashEventId.containsKey(eventid)) this.dispatchUpdateEvent(this.hashEventId.get(eventid));
    }

    public final synchronized void dispatchUpdateEvent(final String eventName) {
        if(this.hashEventName.containsKey(eventName)) this.dispatchUpdateEvent(this.hashEventName.get(eventName));
    }

    public final Descriptor evaluate(final Descriptor desc) throws MdsException {
        return this.evaluate(desc.toString());// TODO: use Vector for args
    }

    public final Descriptor evaluate(final String expr) throws MdsException {
        return this.mdsValue(String.format("EVALUATE((%s))", expr), Descriptor.class);
    }

    public final synchronized Message getAnswer() throws MdsException {
        final Message message = this.receiveThread.GetMessage();
        if(message == null) throw new MdsException("Null response from server", 0);
        if((message.status & 1) == 0 && message.status != 0 && message.dtype == DTYPE.T) throw new MdsException(message.asString(), message.status);
        return message;
    }

    public final double getDouble(final String in) throws MdsException {
        return this.getDoubleArray(in)[0];
    }

    public final double[] getDoubleArray(final String in) throws MdsException {
        return this.getNumberArray(in).toDouble();
    }

    private final int getEventId() {
        int i;
        for(i = 0; i < Connection.MAX_NUM_EVENTS && this.event_flags[i]; i++);
        if(i == Connection.MAX_NUM_EVENTS) return -1;
        this.event_flags[i] = true;
        return i;
    }

    public final float getFloat(final String in) throws MdsException {
        return this.getFloatArray(in)[0];
    }

    public final float[] getFloatArray(final String in) throws MdsException {
        return this.getNumberArray(in).toFloat();
    }

    public final int getInteger(final String in) throws MdsException {
        return this.getIntegerArray(in)[0];
    }

    public final int[] getIntegerArray(final String in) throws MdsException {
        return this.getNumberArray(in).toInt();
    }

    public final long getLong(final String in) throws MdsException {
        return this.getLongArray(in)[0];
    }

    public final long[] getLongArray(final String in) throws MdsException {
        return this.getNumberArray(in).toLong();
    }

    private final Descriptor getNumberArray(final String in) throws MdsException {
        final Descriptor desc = this.mdsValue(in);
        if(desc instanceof CString) throw new MdsException(desc.toString(), 0);
        return desc;
    }

    public final String getProvider() {
        return this.provider;
    }

    public final synchronized String getProviderHost() {
        if(this.provider == null) return null;
        String address = this.provider;
        final int idx = this.provider.indexOf("|");
        int idx_1 = this.provider.indexOf(":");
        if(idx_1 == -1) idx_1 = this.provider.length();
        if(idx != -1) address = this.provider.substring(idx + 1, idx_1);
        else address = this.provider.substring(0, idx_1);
        return address;
    }

    public final synchronized int getProviderPort() throws NumberFormatException {
        if(this.provider == null) return Connection.DEFAULT_PORT;
        int port = Connection.DEFAULT_PORT;
        final int idx = this.provider.indexOf(":");
        if(idx != -1) port = Integer.parseInt(this.provider.substring(idx + 1, this.provider.length()));
        return port;
    }

    public final String getProviderUser() {
        return(this.user != null ? this.user : Connection.DEFAULT_USER);
    }

    public final String getString(final String in) throws MdsException {
        final Descriptor desc = this.mdsValue(in);
        if(desc instanceof CString) return ((CString)desc).getValue();
        return desc.toString();
    }

    public final synchronized Message mdsIO(final String expr, final boolean serialize) throws MdsException {
        if(DEBUG.M) System.out.println("mdsConnection.mdsValue(\"" + expr + "\"," + serialize + ")");
        try{
            final Message message;
            if(serialize) message = new Message(String.format("COMMA(_ans=*,MdsShr->MdsSerializeDscOut(xd((%s)),xd(_ans)),_ans)", expr));
            else message = new Message(expr);
            this.pending_count++;
            message.Send(this.dos);
            return this.getAnswer();
        }catch(final MdsException exc){
            throw exc;
        }catch(final IOException exc){
            throw new MdsException(String.format("Could not get IO for %s: %s", this.provider, exc.getMessage()), 0);
        }
    }

    public final synchronized Message mdsIO(final String expr, Vector<Descriptor> args, final boolean serialize) throws MdsException {
        if(DEBUG.M) System.out.println("mdsConnection.mdsValue(\"" + expr + "\", " + args + ", " + serialize + ")");
        if(args == null) args = new Vector<Descriptor>();
        final StringBuffer cmd = new StringBuffer(expr);
        final int n_args = args.size();
        byte idx = 0;
        final byte totalarg = (byte)(n_args + 1);
        Message msg;
        if(expr.indexOf("($") == -1) // If no $ args specified, build argument list
        {
            if(n_args > 0){
                cmd.append("(");
                for(int i = 0; i < n_args - 1; i++)
                    cmd.append("$,");
                cmd.append("$)");
            }
        }
        try{
            if(serialize) this.sendArg(idx++, DTYPE.T, totalarg, null, String.format("COMMA(_ans=*,MdsShr->MdsSerializeDscOut(xd((%s)),xd(_ans)),_ans)", cmd.toString()).getBytes());
            else this.sendArg(idx++, DTYPE.T, totalarg, null, cmd.toString().getBytes());
            Descriptor p;
            for(int i = 0; i < n_args; i++){
                p = args.elementAt(i);
                p.toMessage(idx++, totalarg).Send(this.dos);
            }
            this.pending_count++;
            msg = this.getAnswer();
            if(msg == null) throw new MdsException("Could not get IO for " + this.provider, 0);
        }catch(final IOException e){
            throw new MdsException("Connection.mdsValue", e);
        }
        return msg;
    }

    public final synchronized void mdsRemoveEvent(final UpdateEventListener l, final String event) {
        int eventid;
        if((eventid = this.RemoveEvent(l, event)) == -1) return;
        try{
            this.sendArg((byte)0, DTYPE.T, (byte)2, null, Message.EVENTCANREQUEST.getBytes());
            this.sendArg((byte)1, DTYPE.T, (byte)2, null, new byte[]{(byte)eventid});
        }catch(final IOException e){
            this.error = new String("Could not get IO for " + this.provider + e);
        }
    }

    public final synchronized void mdsSetEvent(final UpdateEventListener l, final String event) {
        int eventid;
        if((eventid = this.AddEvent(l, event)) == -1) return;
        try{
            this.sendArg((byte)0, DTYPE.T, (byte)3, null, Message.EVENTASTREQUEST.getBytes());
            this.sendArg((byte)1, DTYPE.T, (byte)3, null, event.getBytes());
            this.sendArg((byte)2, DTYPE.BU, (byte)3, null, new byte[]{(byte)(eventid)});
        }catch(final IOException e){
            this.error = new String("Could not get IO for " + this.provider + e);
        }
    }

    public final Descriptor mdsValue(final String expr) throws MdsException {
        return Descriptor.readMessage(this.mdsIO(expr, false));
    }

    public <D extends Descriptor> Descriptor mdsValue(final String expr, final Class<D> cls) throws MdsException {
        final ByteBuffer b = this.mdsIO(expr, true).body;
        return Connection.bufferToClass(b, cls);
    }

    public final Descriptor mdsValue(final String expr, final Vector<Descriptor> args) throws MdsException {
        return Descriptor.readMessage(this.mdsIO(expr, args, false));
    }

    public final <D extends Descriptor> Descriptor mdsValue(final String expr, final Vector<Descriptor> args, final Class<D> cls) throws MdsException {
        final ByteBuffer b = this.mdsIO(expr, args, true).body;
        return Connection.bufferToClass(b, cls);
    }

    public final void QuitFromMds() {
        try{
            if(this.connection_listener.size() > 0) this.connection_listener.removeAllElements();
            this.dos.close();
            this.dis.close();
            this.connected = false;
        }catch(final IOException e){
            this.error.concat("Could not get IO for " + this.provider + e);
        }
    }

    public final synchronized void removeConnectionListener(final ConnectionListener l) {
        if(l == null) return;
        this.connection_listener.removeElement(l);
    }

    public final synchronized int RemoveEvent(final UpdateEventListener l, final String eventName) {
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

    public final void sendArg(final byte descr_idx, final byte dtype, final byte nargs, final int dims[], final byte body[]) throws MdsException {
        final Message msg = new Message(descr_idx, dtype, nargs, dims, body);
        try{
            msg.Send(this.dos);
        }catch(final IOException e){
            throw new MdsException("Connection.sendArg", e);
        }
    }

    public final void setProvider(final String provider) {
        if(this.connected) this.disconnectFromMds();
        this.provider = provider;
        this.port = Connection.DEFAULT_PORT;
        this.host = null;
    }

    public final void setUser(final String user) {
        if(user == null || user.length() == 0) this.user = Connection.DEFAULT_USER;
        else this.user = user;
    }
}
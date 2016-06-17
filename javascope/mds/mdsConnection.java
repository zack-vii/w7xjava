package mds;

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
import java.util.Hashtable;
import java.util.Vector;
import debug.DEBUG;
import jScope.ConnectionEvent;
import jScope.ConnectionListener;
import jScope.UpdateEvent;
import jScope.UpdateEventListener;
import jScope.jScopeFacade;

public class mdsConnection{
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
        boolean    killed = false;
        mdsMessage message;

        public MRT(final String name){
            this.setName(name);
        }

        public synchronized mdsMessage GetMessage() {
            // System.out.println("Get Message");
            long time;
            if(DEBUG.D) time = System.nanoTime();
            while(!this.killed && this.message == null)
                try{
                    this.wait();
                }catch(final InterruptedException e){}
            if(this.killed) return null;
            if(DEBUG.D) time = System.nanoTime() - time;
            final mdsMessage msg = this.message;
            this.message = null;
            if(DEBUG.D) System.out.println(msg.msglen + "B in " + time / 1e9 + "sec (" + msg.asString().substring(0, (msg.length < 64) ? msg.length : 64) + ")");
            return msg;
        }

        @Override
        public void run() {
            try{
                while(true){
                    final mdsMessage message = mdsMessage.Receive(mdsConnection.this.dis, mdsConnection.this.connection_listener);
                    if(DEBUG.A) System.out.println(String.format("%s received %s", this.getName(), message.toString()));
                    if(message.dtype == Descriptor.DTYPE_EVENT){
                        final PMET PmdsEvent = new PMET();
                        PmdsEvent.SetEventid(message.body[12]);
                        PmdsEvent.start();
                    }else{
                        mdsConnection.this.pending_count--;
                        synchronized(this){
                            this.message = message;
                            if(mdsConnection.this.pending_count == 0) this.notify();
                        }
                    }
                }
            }catch(final Exception e){
                synchronized(this){
                    this.killed = true;
                    this.notifyAll();
                }
                if(mdsConnection.this.connected){
                    this.message = null;
                    mdsConnection.this.connected = false;
                    (new Thread(){
                        @Override
                        public void run() {
                            final ConnectionEvent ce = new ConnectionEvent(mdsConnection.this, ConnectionEvent.LOST_CONNECTION, "Lost connection from : " + mdsConnection.this.provider);
                            mdsConnection.this.dispatchConnectionEvent(ce);
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
            if(jScopeFacade.busy()) return;
            if(this.eventName != null) mdsConnection.this.dispatchUpdateEvent(this.eventName);
            else if(this.eventId != -1) mdsConnection.this.dispatchUpdateEvent(this.eventId);
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
    public static final int                 DEFAULT_PORT        = 8000;
    public static final String              DEFAULT_USER        = "JAVA_USER";
    static final int                        MAX_NUM_EVENTS      = 256;
    /*
    public static void main(final String[] args) throws IOException {// TODO:main
        final mdsConnection mds = new mdsConnection("localhost");
        mds.ConnectToMds(false);
        Descriptor d = mds.mdsValue("_ans=[['" + String.join("____", new String[256]) + "','i','4']];MdsShr->MdsSerializeDscOut(xd(_ans),xd(_ans));_ans");
        DEBUG.printByteArray(d.byte_data, 1, d.byte_data.length, 1, 1);
        // mds.DisconnectFromMds();
        final mdsConnection mds2 = new mdsConnection("localhost");
        mds2.ConnectToMds(false);
        d = mds2.mdsValue("_ans=[['" + String.join("____", new String[256]) + "','i','4']];MdsShr->MdsSerializeDscOut(xd(_ans),xd(_ans));_ans");
        DEBUG.printByteArray(d.byte_data, 1, d.byte_data.length, 1, 1);
    }
    */
    public boolean                          connected;
    transient Vector<ConnectionListener>    connection_listener = new Vector<ConnectionListener>();
    protected InputStream                   dis;
    protected DataOutputStream              dos;
    public String                           error;
    transient boolean                       event_flags[]       = new boolean[mdsConnection.MAX_NUM_EVENTS];
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
    public mdsConnection(){
        this(null);
    }

    public mdsConnection(final String provider){
        this.connected = false;
        this.sock = null;
        this.dis = null;
        this.dos = null;
        this.provider = provider;
        this.port = mdsConnection.DEFAULT_PORT;
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

    public synchronized boolean ConnectToMds(final boolean use_compression) {
        try{
            if(this.provider != null){
                this.connectToServer();
                final mdsMessage message = new mdsMessage(this.user);
                message.useCompression(use_compression);
                message.Send(this.dos);
                this.sock.setSoTimeout(3000);
                mdsMessage.Receive(this.dis, null);
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

    public final int DisconnectFromMds() {
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

    public final synchronized Descriptor getAnswer() throws IOException {
        final Descriptor out = new Descriptor();
        // wait();//!!!!!!!!!!
        final mdsMessage message = this.receiveThread.GetMessage();
        if(message == null || message.length == 0){
            out.error = "Null response from server";
            return out;
        }
        out.status = message.status;
        switch((out.dtype = message.dtype)){
            case Descriptor.DTYPE_UBYTE:
            case Descriptor.DTYPE_BYTE:
                out.byte_data = message.body;
                break;
            case Descriptor.DTYPE_USHORT:
                out.int_data = message.asUShortArray();
                out.dtype = Descriptor.DTYPE_LONG;
                break;
            case Descriptor.DTYPE_SHORT:
                out.short_data = message.asShortArray();
                break;
            case Descriptor.DTYPE_LONG:
            case Descriptor.DTYPE_ULONG:
                out.int_data = message.asIntArray();
                break;
            case Descriptor.DTYPE_ULONGLONG:
            case Descriptor.DTYPE_LONGLONG:
                out.long_data = message.asLongArray();
                break;
            case Descriptor.DTYPE_CSTRING:
                if((message.status & 1) == 1) out.strdata = new String(message.body);
                else out.error = new String(message.body);
                break;
            case Descriptor.DTYPE_FLOAT:
                out.float_data = message.asFloatArray();
                break;
            case Descriptor.DTYPE_DOUBLE:
                out.double_data = message.asDoubleArray();
                break;
        }
        return out;
    }

    private final int getEventId() {
        int i;
        for(i = 0; i < mdsConnection.MAX_NUM_EVENTS && this.event_flags[i]; i++);
        if(i == mdsConnection.MAX_NUM_EVENTS) return -1;
        this.event_flags[i] = true;
        return i;
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
        if(this.provider == null) return mdsConnection.DEFAULT_PORT;
        int port = mdsConnection.DEFAULT_PORT;
        final int idx = this.provider.indexOf(":");
        if(idx != -1) port = Integer.parseInt(this.provider.substring(idx + 1, this.provider.length()));
        return port;
    }

    public final String getProviderUser() {
        return(this.user != null ? this.user : mdsConnection.DEFAULT_USER);
    }

    public final synchronized void mdsRemoveEvent(final UpdateEventListener l, final String event) {
        int eventid;
        if((eventid = this.RemoveEvent(l, event)) == -1) return;
        try{
            this.sendArg((byte)0, Descriptor.DTYPE_CSTRING, (byte)2, null, mdsMessage.EVENTCANREQUEST.getBytes());
            this.sendArg((byte)1, Descriptor.DTYPE_CSTRING, (byte)2, null, new byte[]{(byte)eventid});
        }catch(final IOException e){
            this.error = new String("Could not get IO for " + this.provider + e);
        }
    }

    public final synchronized void mdsSetEvent(final UpdateEventListener l, final String event) {
        int eventid;
        if((eventid = this.AddEvent(l, event)) == -1) return;
        try{
            this.sendArg((byte)0, Descriptor.DTYPE_CSTRING, (byte)3, null, mdsMessage.EVENTASTREQUEST.getBytes());
            this.sendArg((byte)1, Descriptor.DTYPE_CSTRING, (byte)3, null, event.getBytes());
            this.sendArg((byte)2, Descriptor.DTYPE_UBYTE, (byte)3, null, new byte[]{(byte)(eventid)});
        }catch(final IOException e){
            this.error = new String("Could not get IO for " + this.provider + e);
        }
    }

    // Read either a string or a float array
    public final synchronized Descriptor mdsValue(final String expr) {
        if(DEBUG.M) System.out.println("mdsConnection.mdsValue(\"" + expr + "\")");
        try{
            final mdsMessage message = new mdsMessage(expr);
            this.pending_count++;
            message.Send(this.dos);
            return this.getAnswer();
        }catch(final IOException exc){
            return new Descriptor("Could not get IO for " + this.provider + exc);
        }
    }

    public final Descriptor mdsValue(final String expr, final Vector<Descriptor> args) {
        return this.mdsValue(expr, args, true);
    }

    public final synchronized Descriptor mdsValue(final String expr, Vector<Descriptor> args, final boolean wait) {
        if(DEBUG.M){
            System.out.println("mdsConnection.mdsValue(\"" + expr + "\", " + args + ", " + wait + ")");
        }
        if(args == null) args = new Vector<Descriptor>();
        final StringBuffer cmd = new StringBuffer(expr);
        final int n_args = args.size();
        byte idx = 0;
        final byte totalarg = (byte)(n_args + 1);
        Descriptor out;
        try{
            if(expr.indexOf("($") == -1) // If no $ args specified, build argument list
            {
                if(n_args > 0){
                    cmd.append("(");
                    for(int i = 0; i < n_args - 1; i++)
                        cmd.append("$,");
                    cmd.append("$)");
                }
            }
            this.sendArg(idx++, Descriptor.DTYPE_CSTRING, totalarg, null, cmd.toString().getBytes());
            Descriptor p;
            for(int i = 0; i < n_args; i++){
                p = args.elementAt(i);
                this.sendArg(idx++, p.dtype, totalarg, p.dims, p.dataToByteArray());
            }
            this.pending_count++;
            if(wait){
                out = this.getAnswer();
                if(out == null) out = new Descriptor("Could not get IO for " + this.provider);
            }else out = new Descriptor();
        }catch(final IOException e){
            out = new Descriptor("Could not get IO for " + this.provider + e);
        }
        return out;
    }

    public final synchronized mdsMessage mdsValueM(final String expr) {
        if(DEBUG.M) System.out.println("mdsConnection.mdsValue(\"" + expr + "\")");
        try{
            final mdsMessage message = new mdsMessage(expr);
            this.pending_count++;
            message.Send(this.dos);
            return this.receiveThread.GetMessage();
        }catch(final IOException exc){
            return null;
        }
    }

    public final Descriptor mdsValueStraight(final String expr, final Vector<Descriptor> args) {
        return this.mdsValue(expr, args, false);
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

    public final void sendArg(final byte descr_idx, final byte dtype, final byte nargs, final int dims[], final byte body[]) throws IOException {
        final mdsMessage msg = new mdsMessage(descr_idx, dtype, nargs, dims, body);
        msg.Send(this.dos);
    }

    public final void setProvider(final String provider) {
        if(this.connected) this.DisconnectFromMds();
        this.provider = provider;
        this.port = mdsConnection.DEFAULT_PORT;
        this.host = null;
    }

    public final void setUser(final String user) {
        if(user == null || user.length() == 0) this.user = mdsConnection.DEFAULT_USER;
        else this.user = user;
    }
}
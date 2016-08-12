package mds;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_A;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Missing;
import mds.data.descriptor_s.Pointer;

public abstract class Mds{
    public static class EventItem{
        public final int                         eventid;
        public final Vector<UpdateEventListener> listener = new Vector<UpdateEventListener>();
        public final String                      name;

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
    protected static final int MAX_NUM_EVENTS = 256;
    public static final String serialStr      = "(_d=*;_s=MdsShr->MdsSerializeDscIn(ref($),xd(_d));_d;)";
    private static Mds         active;

    protected static <D extends Descriptor> Descriptor bufferToClass(final ByteBuffer b, final Class<D> cls) throws MdsException {
        if(b.capacity() == 0) return null;// NoData
        if(cls == null || cls == Descriptor.class) return Descriptor.deserialize(b);
        if(cls == Descriptor_A.class) return Descriptor_A.deserialize(b);
        try{
            return cls.getConstructor(ByteBuffer.class).newInstance(b);
        }catch(final Exception e){
            throw new MdsException(cls.getSimpleName(), e);
        }
    }

    public static final Mds getActiveMds() {
        return Mds.active;// TODO: always up to date?
    }
    protected transient HashSet<MdsListener>          mdslisteners  = new HashSet<MdsListener>();
    protected transient boolean[]                     event_flags   = new boolean[Mds.MAX_NUM_EVENTS];
    protected transient Hashtable<Integer, EventItem> hashEventId   = new Hashtable<Integer, EventItem>();
    protected transient Hashtable<String, EventItem>  hashEventName = new Hashtable<String, EventItem>();

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

    synchronized public final void addMdsListener(final MdsListener l) {
        if(l != null) this.mdslisteners.add(l);
    }

    public final int deallocateAll() throws MdsException {
        return this.getInteger(null, "DEALLOCATE('*')");
    }

    protected final void dispatchMdsEvent(final MdsEvent e) {
        if(this.mdslisteners != null) for(final MdsListener listener : this.mdslisteners)
            listener.processMdsEvent(e);
    }

    synchronized private final void dispatchUpdateEvent(final EventItem eventItem) {
        final Vector<UpdateEventListener> eventListener = eventItem.listener;
        final UpdateEvent e = new UpdateEvent(this, eventItem.name);
        for(int i = 0; i < eventListener.size(); i++)
            eventListener.elementAt(i).processUpdateEvent(e);
    }

    protected final void dispatchUpdateEvent(final int eventid) {
        if(this.hashEventId.containsKey(eventid)) this.dispatchUpdateEvent(this.hashEventId.get(eventid));
    }

    protected final void dispatchUpdateEvent(final String eventName) {
        if(this.hashEventName.containsKey(eventName)) this.dispatchUpdateEvent(this.hashEventName.get(eventName));
    }

    public final byte getByte(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toByte();
    }

    public final byte getByte(final String expr, final Descriptor... args) throws MdsException {
        return this.getByte(null, expr, args);
    }

    public final byte[] getByteArray(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toByteArray();
    }

    public final byte[] getByteArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getByteArray(null, expr, args);
    }

    public abstract ByteBuffer getByteBuffer(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException;

    public final ByteBuffer getByteBuffer(final String expr, final Descriptor... args) throws MdsException {
        return this.getByteBuffer(null, expr, args);
    }

    public abstract <D extends Descriptor> Descriptor getDescriptor(final Pointer ctx, final String expr, final Class<D> cls, final Descriptor... args) throws MdsException;

    public final <D extends Descriptor> Descriptor getDescriptor(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getDescriptor(ctx, expr, Descriptor.class, args);
    }

    public final <D extends Descriptor> Descriptor getDescriptor(final String expr, final Class<D> cls, final Descriptor... args) throws MdsException {
        return this.getDescriptor(null, expr, cls, args);
    }

    public final <D extends Descriptor> Descriptor getDescriptor(final String expr, final Descriptor... args) throws MdsException {
        return this.getDescriptor(null, expr, args);
    }

    public final double getDouble(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toDouble();
    }

    public final double getDouble(final String expr, final Descriptor... args) throws MdsException {
        return this.getDouble(null, expr, args);
    }

    public final double[] getDoubleArray(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toDoubleArray();
    }

    public final double[] getDoubleArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getDoubleArray(null, expr, args);
    }

    private final int getEventId() {
        int i;
        for(i = 0; i < Mds.MAX_NUM_EVENTS && this.event_flags[i]; i++);
        if(i == Mds.MAX_NUM_EVENTS) return -1;
        this.event_flags[i] = true;
        return i;
    }

    public final float getFloat(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toFloat();
    }

    public final float getFloat(final String expr, final Descriptor... args) throws MdsException {
        return this.getFloat(null, expr, args);
    }

    public final float[] getFloatArray(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toFloatArray();
    }

    public final float[] getFloatArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getFloatArray(null, expr, args);
    }

    public final int getInteger(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toInt();
    }

    public final int getInteger(final String expr, final Descriptor... args) throws MdsException {
        return this.getInteger(null, expr, args);
    }

    public final int[] getIntegerArray(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toIntArray();
    }

    public final int[] getIntegerArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getIntegerArray(null, expr, args);
    }

    public final long getLong(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toLong();
    }

    public final long getLong(final String expr, final Descriptor... args) throws MdsException {
        return this.getLong(null, expr, args);
    }

    public final long[] getLongArray(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toLongArray();
    }

    public final long[] getLongArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getLongArray(null, expr, args);
    }

    private final Descriptor getNumberArray(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        final Descriptor desc = this.getDescriptor(ctx, expr, args);
        if(desc instanceof CString){
            if(desc.length > 0) throw new MdsException(desc.toString(), 0);
            return Missing.NEW;
        }
        return desc;
    }

    public final short getShort(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toShort();
    }

    public final short getShort(final String expr, final Descriptor... args) throws MdsException {
        return this.getShort(null, expr, args);
    }

    public final short[] getShortArray(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        return this.getNumberArray(ctx, expr, args).toShortArray();
    }

    public final short[] getShortArray(final String expr, final Descriptor... args) throws MdsException {
        return this.getShortArray(null, expr, args);
    }

    public final String getString(final Pointer ctx, final String expr, final Descriptor... args) throws MdsException {
        final Descriptor desc = this.getDescriptor(ctx, expr, args);
        if(desc instanceof CString) return ((CString)desc).getValue();
        return desc.toString();
    }

    public final String getString(final String expr, final Descriptor... args) throws MdsException {
        return this.getString(null, expr, args);
    }

    public abstract String isReady() throws MdsException;

    protected abstract void mdsSetEvent(final String event, final int eventid);

    synchronized public final int removeEvent(final UpdateEventListener l, final String event) {
        int eventid = -1;
        if(this.hashEventName.containsKey(event)){
            final EventItem eventItem = this.hashEventName.get(event);
            eventItem.listener.remove(l);
            if(eventItem.listener.isEmpty()){
                eventid = eventItem.eventid;
                this.event_flags[eventid] = false;
                this.hashEventName.remove(event);
                this.hashEventId.remove(new Integer(eventid));
            }
        }
        return eventid;
    }

    synchronized public final void removeMdsListener(final MdsListener l) {
        if(l == null) return;
        this.mdslisteners.remove(l);
    }

    public final Mds setActive() {
        return Mds.active = this;
    }

    public final void setEvent(final UpdateEventListener l, final String event) {
        int eventid;
        if((eventid = this.addEvent(l, event)) == -1) return;
        this.mdsSetEvent(event, eventid);
    }

    public final String tcl(final String tclcmd) throws MdsException {
        return this.getString("_a=*;TCL($,_a);_a", new CString(tclcmd)).trim();
    }
}

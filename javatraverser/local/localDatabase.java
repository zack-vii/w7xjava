package local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import jtraverser.Database;
import jtraverser.DatabaseException;
import jtraverser.NodeInfo;
import jtraverser.Database;
import mds.data.Data;
import mds.data.NidData;
import mds.data.PathData;

public final class localDatabase implements Database{
    static{
        try{
            localDatabase.loadLibraryFromJar("localDatabase");
        }catch(final IOException e){
            System.err.println("Cannot load 'localDatabase' library.");
            e.printStackTrace();
        }
    }

    /* Low level MDS database management routines, will be  masked by the Node class*/
    public static final void loadLibraryFromJar(final String filename) throws IOException {
        try{
            // Check if the filename is okay
            if(filename == null || filename.length() < 3) throw new IllegalArgumentException("The filename has to be at least 3 characters long: " + filename);
            // Prepare os-specific path
            final boolean is64bit = System.getProperty("sun.arch.data.model", "").contains("64");
            final boolean isWin = System.getProperty("os.name", "").startsWith("Win");
            final String suffix = isWin ? ".dll" : ".so";
            final String libpath = is64bit ? "/lib/amd64/" : "/lib/x86/";
            final String libfilename = isWin ? filename : "lib" + filename;
            final String path = libpath + libfilename + suffix;
            // Prepare temporary file
            final File temp = File.createTempFile(libfilename, suffix);
            temp.deleteOnExit();
            if(!temp.exists()){ throw new FileNotFoundException("Could not create file '" + temp.getAbsolutePath() + "'."); }
            // Prepare buffer for data copying
            final byte[] buffer = new byte[1024];
            int readBytes;
            // Open and check input stream
            final InputStream is = localDatabase.class.getResourceAsStream(path);
            if(is == null){ throw new FileNotFoundException("File '" + path + "' was not found inside JAR: " + path); }
            // Open output stream and copy data between source file in JAR and the temporary file
            final OutputStream os = new FileOutputStream(temp);
            try{
                while((readBytes = is.read(buffer)) != -1){
                    os.write(buffer, 0, readBytes);
                }
            }finally{
                // If read/write fails, close streams safely before throwing an exception
                os.close();
                is.close();
            }
            // Finally, load the library
            System.load(temp.getAbsolutePath());
        }catch(final IOException e){
            System.load(filename);
        }
    }
    private final boolean is_open = false;
    private final int     mode;
    private final String  name;
    private String        provider;
    private final int     shot;

    public localDatabase(final String name, final int shot, final int mode){
        this.name = name.toUpperCase();
        this.shot = shot;
        this.mode = mode;
        try{
            this.open();
        }catch(final DatabaseException e){
            e.printStackTrace();
        }
    }

    @Override
    public native NidData addDevice(String path, String model) throws DatabaseException;

    @Override
    public native NidData addNode(String name, int usage) throws DatabaseException;

    @Override
    public native void clearFlags(NidData nid, int flags) throws DatabaseException;

    @Override
    public native void close() throws DatabaseException;

    @Override
    public native void create(long shot) throws DatabaseException;

    @Override
    public Data dataFromExpr(final String expr) {
        return Data.compile(expr);
    }

    @Override
    public String dataToString(final Data data) {
        return data.toString();
    }

    @Override
    public native int doAction(NidData nid) throws DatabaseException;

    @Override
    public native void doDeviceMethod(NidData nid, String method) throws DatabaseException;

    @Override
    public Data evaluateData(final Data data) throws DatabaseException {
        return this.evaluateSimpleData(data);
    }

    @Override
    public native Data evaluateData(NidData nid) throws DatabaseException;

    public native Data evaluateSimpleData(Data data) throws DatabaseException;

    @Override
    public native void executeDelete() throws DatabaseException;

    @Override
    public long getCurrentShot() {
        return this.getCurrentShot(this.name);
    }

    @Override
    public native long getCurrentShot(String experiment);

    @Override
    public native Data getData(NidData nid) throws DatabaseException;

    @Override
    public native NidData getDefault() throws DatabaseException;

    @Override
    public native int getFlags(NidData nid) throws DatabaseException;

    @Override
    public native NodeInfo getInfo(NidData nid) throws DatabaseException;

    public native String getMdsMessage(int status);

    @Override
    public native NidData[] getMembers(NidData nid) throws DatabaseException;

    @Override
    final public String getName() {
        return this.name;
    }

    public native String getOriginalPartName(NidData nid) throws DatabaseException;

    @Override
    public String getProvider() throws RemoteException {
        return this.provider;
    }

    @Override
    final public long getShot() {
        return this.shot;
    }

    @Override
    public native NidData[] getSons(NidData nid) throws DatabaseException;

    @Override
    public native String[] getTags(NidData nid);

    @Override
    public native NidData[] getWild(int usage_mask) throws DatabaseException;

    @Override
    public boolean isEditable() {
        return this.mode == Database.EDITABLE;
    }

    @Override
    public native boolean isOn(NidData nid) throws DatabaseException;

    @Override
    public boolean isOpen() {
        return this.is_open;
    }

    @Override
    public boolean isReadonly() {
        return this.mode == Database.READONLY;
    }

    @Override
    public boolean isRealtime() {
        return this.mode == Database.REALTIME;
    }

    @Override
    public native void open() throws DatabaseException;

    public native void openNew() throws DatabaseException;

    @Override
    public native void putData(NidData nid, Data data) throws DatabaseException;

    @Override
    public native void putRow(NidData nid, Data data, long time) throws DatabaseException;

    @Override
    public native void quit() throws DatabaseException;

    @Override
    public native void renameNode(NidData nid, String name) throws DatabaseException;

    @Override
    public native NidData resolve(PathData pad) throws DatabaseException;

    public native void restoreContext(long context);

    public native long saveContext();

    @Override
    public void setCurrentShot(final int shot) {
        this.setCurrentShot(this.name, shot);
    }

    @Override
    public native void setCurrentShot(String experiment, int shot);

    @Override
    public native void setDefault(NidData nid) throws DatabaseException;

    @Override
    public native void setEvent(String event) throws DatabaseException;

    @Override
    public native void setFlags(NidData nid, int flags) throws DatabaseException;

    @Override
    public native void setOn(NidData nid, boolean on) throws DatabaseException;

    @Override
    public native void setSubtree(NidData nid) throws DatabaseException;

    @Override
    public native void setTags(NidData nid, String tags[]) throws DatabaseException;

    @Override
    public native NidData[] startDelete(NidData nid[]) throws DatabaseException;

    @Override
    public native void write() throws DatabaseException;
}

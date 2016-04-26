package jScope;

/* $Id$ */
import java.io.IOException;
import java.util.Vector;

public final class DataAccessURL{
    static Vector<DataAccess> dataAccessVector = new Vector<DataAccess>();

    static public void addProtocol(final DataAccess dataAccess) {
        DataAccessURL.dataAccessVector.addElement(dataAccess);
    }

    static public void close() {
        DataAccess da = null;
        for(int i = 0; i < DataAccessURL.dataAccessVector.size(); i++){
            da = DataAccessURL.dataAccessVector.elementAt(i);
            if(da != null) da.close();
        }
    }

    static public DataAccess getDataAccess(final String url) throws IOException {
        DataAccess da = null;
        for(int i = 0; i < DataAccessURL.dataAccessVector.size(); da = null, i++){
            da = DataAccessURL.dataAccessVector.elementAt(i);
            if(da.supports(url)) break;
        }
        if(da == null) throw(new IOException("Protocol not recognized"));
        return da;
    }

    static public void getImages(final String url, final Frames f) throws Exception {
        DataAccessURL.getImages(url, null, null, f);
    }

    static public void getImages(final String url, final String passwd, final Frames f) throws Exception {
        DataAccessURL.getImages(url, null, passwd, f);
    }

    static public void getImages(final String url, final String name, final String passwd, final Frames f) throws Exception {
        final DataAccess da = DataAccessURL.getDataAccess(url);
        if(da == null || f == null) throw(new IOException("Protocol not recognized"));
        da.setPassword(passwd);
        final FrameData fd = da.getFrameData(url);
        if(fd == null && da.getError() == null) throw(new IOException("Incorrect password or read images error"));
        f.SetFrameData(fd);
        f.setName(da.getSignalName());
        if(da.getError() != null){ throw(new IOException(da.getError())); }
    }

    static public int getNumProtocols() {
        return DataAccessURL.dataAccessVector.size();
    }

    static public Signal getSignal(final String url) throws IOException {
        return DataAccessURL.getSignal(url, null, null);
    }

    static public Signal getSignal(final String url, final String passwd) throws IOException {
        return DataAccessURL.getSignal(url, null, passwd);
    }

    static public Signal getSignal(final String url, String name, final String passwd) throws IOException {
        final DataAccess da = DataAccessURL.getDataAccess(url);
        if(da == null) return null;
        da.setPassword(passwd);
        if(da.getError() != null) throw new IOException(da.getError());
        final Signal s = da.getSignal(url);
        if(s == null) throw(new IOException("Incorrect password or read signal error"));
        if(name == null) name = s.getName();
        if(name == null) name = da.getSignalName() + " " + da.getShot();
        else name = name + " " + da.getShot();
        s.setName(name);
        return s;
    }
}

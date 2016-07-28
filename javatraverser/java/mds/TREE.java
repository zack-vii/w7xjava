package mds;

import mds.mdsip.Connection;
import mds.mdsip.Connection.Provider;

public final class TREE{
    public static final int EDITABLE = 2;
    public static final int NEW      = 3;
    public static final int NORMAL   = 1;
    public static final int READONLY = 0;
    public static final int REALTIME = 4;
    public final int        shot;
    public final Connection connection;
    public final String     expt;
    private int             mode;
    private boolean         open;

    TREE(final Connection connection, final String expt, final int shot){
        this(connection, expt, shot, TREE.READONLY);
    }

    TREE(final Connection connection, final String expt, final int shot, final int mode){
        this.connection = connection;
        this.expt = expt;
        this.shot = shot;
    }

    TREE(final Provider provider, final String expt, final int shot, final int mode){
        this(Connection.sharedConnection(provider), expt, shot, mode);
    }

    public final Connection getConnection() {
        return this.connection;
    }

    public final Provider getProvider() {
        return this.connection.getProvider();
    }

    public final int getShot() {
        return this.shot;
    }

    public final boolean isEditable() {
        return this.mode == TREE.EDITABLE;
    }

    public final boolean isOpen() {
        return this.open;
    }

    public final boolean isReadonly() {
        return this.mode == TREE.READONLY;
    }

    public final boolean isRealtime() {
        return this.mode == TREE.REALTIME;
    }

    public TREE withPrivateConnection() {
        return new TREE(new Connection(this.getProvider()), this.expt, this.shot);
    }
}

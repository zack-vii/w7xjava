package jScope;

/* $Id$ */
public class DataServerItem{
    public String argument;
    String        browse_class;
    String        browse_url;
    String        class_name;
    boolean       enable_cache       = false;
    boolean       enable_compression = false;
    boolean       fast_network_access;
    String        name;
    public String tunnel_port;
    public String user;

    public DataServerItem(){
        this(null, null, null, null, null, null, null, false);
    }

    public DataServerItem(final String user){
        this(null, null, user, null, null, null, null, false);
    }

    public DataServerItem(final String name, final String argument, final String user, final String class_name, final String browse_class, final String browse_url, final String tunnel_port, final boolean fast_network_access){
        this.name = name;
        this.argument = argument;
        this.user = user;
        this.class_name = class_name;
        this.browse_class = browse_class;
        this.browse_url = browse_url;
        this.fast_network_access = fast_network_access;
        this.tunnel_port = tunnel_port;
    }

    public boolean equals(final DataServerItem dsi) {
        try{
            return this.name.equals(dsi.name);
            // &&
            // argument.equals(dsi.argument) &&
            // class_name.equals(dsi.class_name) &&
            // browse_class.equals(dsi.browse_class);
        }catch(final Exception exc){
            return false;
        }
    }

    public boolean equals(final String name) {
        return this.name.equals(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}

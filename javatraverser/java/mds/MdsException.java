package mds;

import java.io.IOException;

@SuppressWarnings("serial")
public final class MdsException extends IOException{
    public static final int TREE_E_FOPENW = 265392146;

    private static final String parseMessage(final String message) {
        final String[] parts = message.split(":", 2);
        return parts[parts.length - 1];
    }
    private final int status;

    public MdsException(final String message){
        super(MdsException.parseMessage(message));
        int status = 0;
        try{
            final String[] parts = message.split(":", 2);
            if(parts.length > 1) status = Integer.parseInt(parts[0]);
        }catch(final Exception exc){}
        this.status = status;
    }

    public MdsException(final String header, final Exception e){
        super(String.format("%s: %s", header, e.getMessage()));
        this.status = 0;
    }

    public MdsException(final String message, final int status){
        super(message);
        this.status = status;
    }

    public final int getStatus() {
        return this.status;
    }
}
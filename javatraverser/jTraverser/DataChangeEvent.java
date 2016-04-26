package jTraverser;

import java.awt.Event;

public class DataChangeEvent extends Event{
    private static final long serialVersionUID = 6290317103493023763L;

    public DataChangeEvent(final Object target, final int id, final Object arg){
        super(target, id, arg);
    }
}
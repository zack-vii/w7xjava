package jScope;

/* $Id$ */
import java.io.Serializable;

class DataCacheObject implements Serializable{
    static final long serialVersionUID = 35346874385634L;
    float             data[];
    int               dimension;
    float             low_err[];
    String            title;
    float             up_err[];
    float             x[];
    double            x_double[];
    String            x_label;
    float             y[];
    String            y_label;
    String            z_label;
}

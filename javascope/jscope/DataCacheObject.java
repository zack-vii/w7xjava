package jscope;

/* $Id$ */
import java.io.Serializable;

@SuppressWarnings("serial")
class DataCacheObject implements Serializable{
    float  data[];
    int    dimension;
    float  low_err[];
    String title;
    float  up_err[];
    float  x[];
    double x_double[];
    String x_label;
    float  y[];
    String y_label;
    String z_label;
}

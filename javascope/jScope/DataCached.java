package jScope;

/* $Id$ */
import java.io.Serializable;

public class DataCached implements Serializable{
    static final long serialVersionUID = 56436845874835L;
    Object            data;
    String            experiment;
    String            expression;
    long              shot;

    DataCached(final String expression, final String experiment, final long shot, final Object data){
        this.expression = expression;
        this.shot = shot;
        this.experiment = experiment;
        this.data = data;
    }

    public boolean equals(final String expression, final String experiment, final long shot) {
        return(this.expression.equals(expression) && this.shot == shot && this.experiment.equals(experiment));
    }
}

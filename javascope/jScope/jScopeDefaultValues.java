package jScope;

import java.util.Properties;

/* $Id$ */
public final class jScopeDefaultValues{
    public String   experiment_str;
    private boolean is_evaluated     = false;
    private String  public_variables = null;
    public boolean  reversed         = false;
    public String   shot_str;
    public long     shots[];
    public String   title_str, xlabel, ylabel;
    public String   upd_event_str, def_node_str;
    public boolean  upd_limits       = true;
    public String   xmin, xmax, ymax, ymin;

    public final void FromFile(final Properties pr, final String prompt) {
        final String prop = pr.getProperty(prompt + ".reversed");
        if(prop != null) this.reversed = Boolean.parseBoolean(prop);
        else this.reversed = false;
    }

    public boolean getIsEvaluated() {
        return this.is_evaluated || this.public_variables == null || this.public_variables.length() == 0;
    }

    public String getPublicVariables() {
        return this.public_variables;
    }

    public boolean isSet() {
        return(this.public_variables != null && this.public_variables.length() > 0);
    }

    public void Reset() {
        this.shots = null;
        this.xmin = this.xmax = this.ymax = this.ymin = null;
        this.title_str = this.xlabel = this.ylabel = null;
        this.experiment_str = this.shot_str = null;
        this.upd_event_str = this.def_node_str = null;
        this.is_evaluated = false;
        this.upd_limits = true;
        this.reversed = false;
    }

    public void setIsEvaluated(final boolean evaluated) {
        this.is_evaluated = evaluated;
    }

    public void setPublicVariables(final String public_variables) {
        if(this.public_variables == null || public_variables == null) return;
        if(!this.public_variables.equals(public_variables)){
            this.is_evaluated = false;
            this.public_variables = public_variables.trim();
        }
    }
}

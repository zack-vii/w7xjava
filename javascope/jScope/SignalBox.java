package jScope;

/* $Id$ */
import java.util.Hashtable;

final public class SignalBox{
    final class Sign{
        String x_expr;
        String y_expr;

        Sign(final String x_expr, final String y_expr){
            this.x_expr = new String(x_expr == null ? "" : x_expr);
            this.y_expr = new String(y_expr == null ? "" : y_expr);
        }

        @Override
        public String toString() {
            return this.y_expr + " " + this.x_expr;
        }
    }
    Object                  obj[];
    Hashtable<String, Sign> signals_name = new Hashtable<String, Sign>();

    public void AddSignal(final String x_expr, final String y_expr) {
        if(x_expr == null && y_expr == null) return;
        final String s = x_expr + y_expr;
        if(!this.signals_name.containsKey(s)){
            this.signals_name.put(s, new Sign(x_expr, y_expr));
            this.obj = this.signals_name.values().toArray();
        }
    }

    public String getXexpr(final int i) {
        return ((Sign)this.obj[i]).x_expr;
    }

    public String getYexpr(final int i) {
        return ((Sign)this.obj[i]).y_expr;
    }

    public void removeExpr(final int i) {
        this.RemoveSignal(((Sign)this.obj[i]).x_expr, ((Sign)this.obj[i]).y_expr);
    }

    public void RemoveSignal(final String x_expr, final String y_expr) {
        final String s = x_expr + y_expr;
        if(this.signals_name.containsKey(s)){
            this.signals_name.remove(s);
            this.obj = this.signals_name.values().toArray();
        }
    }

    @Override
    public String toString() {
        if(this.obj == null) return "";
        String out = new String();
        for(final Object element : this.obj)
            out = out + "\n" + element;
        return out;
    }
}
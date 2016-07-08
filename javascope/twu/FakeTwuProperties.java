/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package twu;

public class FakeTwuProperties extends TwuProperties{
    public FakeTwuProperties(final int len){
        super(null);
        this.signalProps.put("TWU.properties.version", "0.7");
        this.signalProps.put("Dimensions", "1");
        this.signalProps.put("Length.total", String.valueOf(len));
        this.signalProps.put("Length.dimension.0", String.valueOf(len));
        this.signalProps.put("Equidistant", "Incrementing");
        this.signalProps.put("Signal.Minimum", "0.0");
        this.signalProps.put("Signal.Maximum", String.valueOf((double)(len - 1)));
    }

    @Override
    public boolean valid() {
        return true;
    }
}

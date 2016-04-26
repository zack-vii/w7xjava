package devicebeans;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

public class DeviceWaveParameters extends DeviceParameters{
    private static final long serialVersionUID = -3104891286776580295L;

    public DeviceWaveParameters(){}

    @Override
    protected void addParameter(final JPanel jp, final Nid nidData) {
        try{
            this.subtree.setDefault(nidData);
            Nid currNid;
            currNid = this.subtree.resolve(new Path(":DESCRIPTION"));
            final String description = this.subtree.evaluate(currNid).toString();
            final Nid currXNid = this.subtree.resolve(new Path(":X"));
            final DeviceField currXField = new DeviceField();
            currXField.setSubtree(this.subtree);
            currXField.setBaseNid(currXNid.getValue());
            currXField.setOffsetNid(0);
            currXField.setLabelString("X:");
            currXField.setNumCols(30);
            this.parameters.add(currXField);
            final Nid currYNid = this.subtree.resolve(new Path(":Y"));
            final DeviceField currYField = new DeviceField();
            currYField.setSubtree(this.subtree);
            currYField.setBaseNid(currYNid.getValue());
            currYField.setOffsetNid(0);
            currYField.setLabelString("Y:");
            currYField.setNumCols(30);
            this.parameters.add(currYField);
            final JPanel jp1 = new JPanel();
            jp1.add(new JLabel(description));
            jp1.add(currXField);
            jp1.add(currYField);
            jp.add(jp1);
            currXField.configure(currXNid.getValue());
            currYField.configure(currYNid.getValue());
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(null, "Error in DeviceWaveParameters.addParam: " + exc);
        }
    }

    @Override
    protected String getParameterName() {
        return "WAVE";
    }
}

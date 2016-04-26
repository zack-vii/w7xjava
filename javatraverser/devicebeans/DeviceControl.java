package devicebeans;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public abstract class DeviceControl extends JButton{
    private static final long serialVersionUID = 4129067720008275635L;
    protected String[]        checkExpressions, checkMessages;
    DeviceSetup               deviceSetup      = null;

    DeviceControl(){
        this.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                DeviceControl.this.discoverDevice();
                if(DeviceControl.this.deviceSetup != null) DeviceControl.this.doOperation(DeviceControl.this.deviceSetup);
            }
        });
    }

    protected boolean check() {
        if(this.deviceSetup == null) this.discoverDevice();
        if(this.deviceSetup != null && this.checkExpressions != null && this.checkMessages != null){//
            return this.deviceSetup.check(this.checkExpressions, this.checkMessages);
        }
        return true;
    }

    protected void discoverDevice() {
        Container curr_container;
        Component curr_component = this;
        do{
            curr_container = curr_component.getParent();
            curr_component = curr_container;
        }while((curr_container != null) && !(curr_container instanceof DeviceSetup));
        if(curr_container != null){
            this.deviceSetup = (DeviceSetup)curr_container;
        }
    }

    protected abstract void doOperation(DeviceSetup deviceSetup);

    public String[] getCheckExpressions() {
        return this.checkExpressions;
    }

    public String[] getCheckMessages() {
        return this.checkMessages;
    }

    public void setCheckExpressions(final String[] checkExpressions) {
        this.checkExpressions = checkExpressions;
    }

    public void setCheckMessages(final String[] checkMessages) {
        this.checkMessages = checkMessages;
    }

    void setReadOnly(final boolean readOnly) {}
}

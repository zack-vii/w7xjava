package devicebeans;

@SuppressWarnings("serial")
public class DeviceApply extends DeviceControl{
    public DeviceApply(){
        this.setText("Apply");
    }

    @Override
    protected void doOperation(final DeviceSetup deviceSetup) {
        if(this.check()) deviceSetup.apply();
    }

    @Override
    void setReadOnly(final boolean readOnly) {
        this.setEnabled(!readOnly);
    }
}

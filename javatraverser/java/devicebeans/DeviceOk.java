package devicebeans;

@SuppressWarnings("serial")
public class DeviceOk extends DeviceControl{
    public DeviceOk(){
        this.setText("Ok");
    }

    @Override
    protected void doOperation(final DeviceSetup deviceSetup) {
        this.check();
        deviceSetup.apply();
        deviceSetup.cancel();
    }

    @Override
    void setReadOnly(final boolean readOnly) {
        this.setEnabled(!readOnly);
    }
}

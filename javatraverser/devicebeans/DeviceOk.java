package devicebeans;

public class DeviceOk extends DeviceControl{
    private static final long serialVersionUID = -8547074693435300526L;

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

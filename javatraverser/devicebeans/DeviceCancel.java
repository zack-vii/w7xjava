package devicebeans;

public class DeviceCancel extends DeviceControl{
    private static final long serialVersionUID = -3103218818878298711L;

    public DeviceCancel(){
        this.setText("Cancel");
    }

    @Override
    protected void doOperation(final DeviceSetup deviceSetup) {
        deviceSetup.cancel();
    }
}

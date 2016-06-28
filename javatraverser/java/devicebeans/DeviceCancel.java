package devicebeans;

@SuppressWarnings("serial")
public class DeviceCancel extends DeviceControl{
    public DeviceCancel(){
        this.setText("Cancel");
    }

    @Override
    protected void doOperation(final DeviceSetup deviceSetup) {
        deviceSetup.cancel();
    }
}

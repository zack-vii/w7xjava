package devicebeans;

@SuppressWarnings("serial")
public class DeviceReset extends DeviceControl{
    public DeviceReset(){
        this.setText("Reset");
    }

    @Override
    protected void doOperation(final DeviceSetup deviceSetup) {
        deviceSetup.reset();
    }
}
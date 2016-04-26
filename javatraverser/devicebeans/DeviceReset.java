package devicebeans;

public class DeviceReset extends DeviceControl{
    private static final long serialVersionUID = 5554215138143980257L;

    public DeviceReset(){
        this.setText("Reset");
    }

    @Override
    protected void doOperation(final DeviceSetup deviceSetup) {
        deviceSetup.reset();
    }
}
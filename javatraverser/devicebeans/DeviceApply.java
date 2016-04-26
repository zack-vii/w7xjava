package devicebeans;

public class DeviceApply extends DeviceControl{
    private static final long serialVersionUID = 652113172010076545L;

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

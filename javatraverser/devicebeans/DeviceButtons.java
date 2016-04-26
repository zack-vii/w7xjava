package devicebeans;


import javax.swing.JPanel;

public class DeviceButtons extends JPanel{
    private static final long serialVersionUID = -1364376360190126183L;
    protected DeviceControl   apply, ok, cancel;
    public String             methods[];

    public DeviceButtons(){
        this.add(this.ok = new DeviceOk());
        this.add(this.apply = new DeviceApply());
        this.add(new DeviceReset());
        this.add(this.cancel = new DeviceCancel());
    }

    public boolean check() {
        return this.apply.check();
    }

    public String[] getCheckExpressions() {
        return this.apply.getCheckExpressions();
    }

    public String[] getCheckMessages() {
        return this.apply.getCheckMessages();
    }

    public String[] getMethods() {
        return this.methods;
    }

    public void setCancelText(final String cancelText) {
        this.cancel.setText(cancelText);
    }

    public void setCheckExpressions(final String[] checkExpressions) {
        this.apply.setCheckExpressions(checkExpressions);
        this.ok.setCheckExpressions(checkExpressions);
    }

    public void setCheckMessages(final String[] checkMessages) {
        this.apply.setCheckMessages(checkMessages);
        this.ok.setCheckMessages(checkMessages);
    }

    public void setMethods(final String methods[]) {
        this.methods = methods;
    }
}

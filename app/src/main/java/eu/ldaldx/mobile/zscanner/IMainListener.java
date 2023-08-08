package eu.ldaldx.mobile.zscanner;

public interface IMainListener {
    public void displayAlert(String title, String alert);
    public void executeMenuAction(String action, Boolean local);
}



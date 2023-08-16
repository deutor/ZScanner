package eu.ldaldx.mobile.zscanner;

public interface IMainListener {
    public void displayAlert(String title, String alert);
    public void executeMenuAction(String action, Boolean local);

    public void setControlsOnBrowseVC(String setString);
    public void onBrowserEnterClicked();

    public Boolean moveToPrevTabItem(IView vw);
    public Boolean moveToNextTabItem(IView vw);
}



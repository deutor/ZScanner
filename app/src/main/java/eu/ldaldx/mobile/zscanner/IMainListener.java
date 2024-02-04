package eu.ldaldx.mobile.zscanner;

public interface IMainListener {
    void displayAlert(String title, String alert);
    void executeMenuAction(String action, String actionArgs);

    void setControlsOnBrowseVC(String setString);
    void onBrowserEnterClicked();

    Boolean moveToPrevTabItem(IView vw);
    Boolean moveToNextTabItem(IView vw);
}



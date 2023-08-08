package eu.ldaldx.mobile.zscanner;

public class CustomMenuEntry {
    protected String menuEntry;
    protected String action;
    protected Boolean localAction;

    public CustomMenuEntry() {
    }

    public CustomMenuEntry(String menuEntry, String action, Boolean localAction) {
        this();

        this.menuEntry = menuEntry;
        this.action    = action;
        this.localAction = localAction;

    }

    public String getMenuEntry() {
        return menuEntry;
    }

    public void setMenuEntry(String menuEntry) {
        this.menuEntry = menuEntry;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getLocalAction() {
        return localAction;
    }

    public void setLocalAction(Boolean localAction) {
        this.localAction = localAction;
    }
}

package eu.ldaldx.mobile.zscanner.menu;

public class CustomMenuEntry {
    protected String menuEntry;
    protected String action;
    protected String actionArgs;

    public CustomMenuEntry() {
    }

    public CustomMenuEntry(String menuEntry, String action, String actionArgs) {
        this();

        this.menuEntry = menuEntry;
        this.action    = action;
        this.actionArgs = actionArgs;

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

    public String getActionArgs() {
        return actionArgs;
    }

    public void setActionArgs(String actionArgs) {
        this.actionArgs = actionArgs;
    }
}

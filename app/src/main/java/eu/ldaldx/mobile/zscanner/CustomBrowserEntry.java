package eu.ldaldx.mobile.zscanner;

import java.util.ArrayList;

public class CustomBrowserEntry {
    static class Column {
        private final String column_value;
        private final String column_action;

        public Column(String value, String action) {
            this.column_value = value;
            this.column_action = action;
        }

        public String getColumn_value() {
            return column_value;
        }

        @SuppressWarnings("unused")
        public String getColumn_action() {
            return column_action;
        }
    }

    protected CustomBrowser parentBrowser = null;
    protected ArrayList<Column> columns = new ArrayList<>();

    public Integer getColumnWidth(int nthColumn) {
        return parentBrowser.getColumnWidth(nthColumn);
    }

    public Boolean isColumnVisible(int nthColumn) {
        return parentBrowser.isColumnVisible(nthColumn);
    }

    public String getColumnValue(int nthColumn) {
        int arrPos = nthColumn - 1;
        if(arrPos <= columns.size() && columns.size() > 0) {
            return columns.get(arrPos).getColumn_value();
        }
        return "Incorrect column number";
    }

    private CustomBrowserEntry() {
    }

    public CustomBrowserEntry(String column1, CustomBrowser parent) {
        this();
        columns.add(new Column(column1, ""));
        this.parentBrowser = parent;
    }

    public CustomBrowserEntry(String column1, String column2, CustomBrowser parent) {
        this();
        columns.add(new Column(column1, ""));
        columns.add(new Column(column2, ""));
        this.parentBrowser = parent;
    }

    public CustomBrowserEntry(String column1, String column2, String column3, CustomBrowser parent) {
        this();

        columns.add(new Column(column1, ""));
        columns.add(new Column(column2, ""));
        columns.add(new Column(column3, ""));
        this.parentBrowser = parent;
    }

    public CustomBrowserEntry(String column1, String column2, String column3, String column4, CustomBrowser parent) {
        this();
        columns.add(new Column(column1, ""));
        columns.add(new Column(column2, ""));
        columns.add(new Column(column3, ""));
        columns.add(new Column(column4, ""));

        this.parentBrowser = parent;
    }

    public CustomBrowserEntry(String column1, String column2, String column3, String column4, String action, CustomBrowser parent) {
        this();
        columns.add(new Column(column1, action));
        columns.add(new Column(column2, ""));
        columns.add(new Column(column3, ""));
        columns.add(new Column(column4, ""));

        this.parentBrowser = parent;
    }

}

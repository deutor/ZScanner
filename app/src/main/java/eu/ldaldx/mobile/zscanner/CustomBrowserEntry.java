package eu.ldaldx.mobile.zscanner;

import java.util.ArrayList;

public class CustomBrowserEntry {
    static class Column {
        private final String column_value;
        
        public Column(String value) {
            this.column_value = value;
        }

        public String getColumn_value() {
            return column_value;
        }
    }

    public String getOnGo() {
        return onGo;
    }

    public void setOnGo(String onGo) {
        this.onGo = onGo;
    }

    public String getOnValueChanged() {
        return onValueChanged;
    }

    public void setOnValueChanged(String onValueChanged) {
        this.onValueChanged = onValueChanged;
    }

    private String onGo;
    private String onValueChanged;

    protected CustomBrowser parentBrowser = null;
    protected ArrayList<Column> columns = new ArrayList<>();

    public Integer getColumnWidth(int nthColumn) {
        return parentBrowser.getColumnWidth(nthColumn);
    }

    public Boolean isColumnVisible(int nthColumn) {
        return parentBrowser.isColumnVisible(nthColumn);
    }

    public String getAlign(int nthColumn) {
        return parentBrowser.getAlign(nthColumn);
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
        columns.add(new Column(column1));
        this.parentBrowser = parent;
    }

    public CustomBrowserEntry(String column1, String column2, CustomBrowser parent) {
        this();
        columns.add(new Column(column1));
        columns.add(new Column(column2));
        this.parentBrowser = parent;
    }

    public CustomBrowserEntry(String column1, String column2, String column3, CustomBrowser parent) {
        this();

        columns.add(new Column(column1));
        columns.add(new Column(column2));
        columns.add(new Column(column3));
        this.parentBrowser = parent;
    }

    public CustomBrowserEntry(String column1, String column2, String column3, String column4, CustomBrowser parent) {
        this();
        columns.add(new Column(column1));
        columns.add(new Column(column2));
        columns.add(new Column(column3));
        columns.add(new Column(column4));

        this.parentBrowser = parent;
    }
    
    public CustomBrowserEntry(String column1, String column2, String column3, String column4, String action, CustomBrowser parent) {
        this();
        columns.add(new Column(column1));
        columns.add(new Column(column2));
        columns.add(new Column(column3));
        columns.add(new Column(column4));

        this.parentBrowser = parent;
    }

    public CustomBrowserEntry(String column1, String column2, String column3, String column4, String onGo, String onValueChanged, CustomBrowser parent) {
        this();
        columns.add(new Column(column1));
        columns.add(new Column(column2));
        columns.add(new Column(column3));
        columns.add(new Column(column4));

        this.onGo = onGo;
        this.onValueChanged = onValueChanged;

        this.parentBrowser = parent;
    }
    

}

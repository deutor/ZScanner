package eu.ldaldx.mobile.zscanner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;


public class CustomBrowser extends LinearLayout implements IBrowserListener {
    static class Column {
        protected String column_label;
        protected Boolean column_visible;
        protected Integer column_width;

        protected Boolean column_isAction;



        Column(String label, Boolean visible, Integer width, Boolean isAction) {
            this.column_label = label;
            this.column_visible = visible;
            if(!isAction) {
                if(!column_visible) column_width = 0;
                this.column_width = Math.abs(width);
            }
            column_isAction = isAction;
        }

        public String getColumn_label() {
            return column_label;
        }

        public Boolean getColumn_visible() {
            return column_visible;
        }


        public Integer getColumn_width() {
            return column_width;
        }

        public void setColumn_width(Integer column_width) {
            //if(column_width <= 0 ) column_width = 1;
            if(!column_visible) column_width = 0;
            this.column_width = column_width;
        }
    }


    private FrameLayout parentLayout;
    private LinearLayout layout;

    private final Drawable background;

    private int mainHeight;
    private int parentWidth;
    private View focusedView;

    private IMainListener mainListener;

    RecyclerView rv;
    CustomBrowserAdapter cba;

    ArrayList<CustomBrowserEntry> cbeEntries = new ArrayList<>();
    ArrayList<Column> columns = new ArrayList<>();
    ArrayList<TextView> tvcolumns = new ArrayList<>();

    Integer numVisible;
    Integer totalWidth;

    public CustomBrowser(FrameLayout parentLayout, Context context, IMainListener listener, int width, int height) {
        super(context);
        this.parentLayout = parentLayout;
        this.mainListener = listener;
        this.mainHeight = height;
        this.parentWidth = width;
        this.numVisible = 0;
        this.totalWidth = 0;

        layout = (LinearLayout) parentLayout.findViewById(R.id.browserWithTitleExt);
        //layoutInner = (LinearLayout) parentLayout.findViewById(R.id.browserWithTitleInt);
        background = parentLayout.getResources().getDrawable(R.drawable.browser_selection_bar, null);

        tvcolumns.add( (TextView) parentLayout.findViewById(R.id.txtBrowserCol1_Label ));
        tvcolumns.add( (TextView) parentLayout.findViewById(R.id.txtBrowserCol2_Label ));
        tvcolumns.add( (TextView) parentLayout.findViewById(R.id.txtBrowserCol3_Label ));
        tvcolumns.add( (TextView) parentLayout.findViewById(R.id.txtBrowserCol4_Label ));




        cba = new CustomBrowserAdapter(cbeEntries, this);
        rv = parentLayout.findViewById(R.id.reclerBrowserView);
        if(rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(context));
            rv.setAdapter(cba);
        }

    }

    protected void hide() {
        layout.setVisibility(INVISIBLE);
    }

    protected void show() {
        if(rv!=null) {
            View vPos0 = rv.getLayoutManager().findViewByPosition(0);
            if (vPos0 != null) vPos0.requestFocus();
            cba.notifyDataSetChanged();
        }
        layout.setVisibility(VISIBLE);
    }


    public CustomBrowser(Context context) {
        super(context);

        background = parentLayout.getResources().getDrawable(R.drawable.browser_selection_bar);

    }

    public void clear() {
        cbeEntries.clear();
        columns.clear();
        numVisible = 0;
        totalWidth = 0;
    }

    public void addColumn(String label, Boolean visible, int width, Boolean action) {
        columns.add( new Column(label, visible, width, action));

        if(visible) {
            numVisible++;
            totalWidth = totalWidth + Math.abs(width);
        }
    }

    private void calculateColWidth() {
        int calcWidth;
        for(Column col:columns) {
            calcWidth = col.getColumn_width() * parentWidth / totalWidth;
            col.setColumn_width(calcWidth);
        }
    }

    public void prepareBrowser() {
        TextView tvcolumn;

        calculateColWidth();

        for(int i=0;i<4;i++) {
            tvcolumn = tvcolumns.get(i);
            tvcolumn.setText( columns.get(i).getColumn_label());
            tvcolumn.setWidth( columns.get(i).getColumn_width());
            if(columns.get(i).getColumn_visible()) {
                tvcolumn.setVisibility(VISIBLE);
            } else {
                tvcolumn.setVisibility(INVISIBLE);
            }
        }
    }


    @SuppressWarnings("unused")
    public void addRow(String column1) {
        cbeEntries.add(new CustomBrowserEntry(column1, this));
    }

    @SuppressWarnings("unused")
    public void addRow(String column1, String column2) {
        cbeEntries.add(new CustomBrowserEntry(column1, column2, this));
    }

    @SuppressWarnings("unused")
    public void addRow(String column1, String column2, String column3) {
        cbeEntries.add(new CustomBrowserEntry(column1, column2, column3, this));
    }

    @SuppressWarnings("unused")
    public void addRow(String column1, String column2, String column3, String column4) {
        cbeEntries.add(new CustomBrowserEntry(column1, column2, column3, column4, this));
    }

    @SuppressWarnings("unused")
    public void addRow(String column1, String column2, String column3, String column4, String action) {
        cbeEntries.add(new CustomBrowserEntry(column1, column2, column3, column4, action, this));
    }


    public Integer getColumnWidth(int nthColumn) {  //browser columns are 1-based
        int arrayPos = nthColumn - 1; // array is 0 based
        if(arrayPos <= columns.size() && columns.size() > 0 ) {
            return columns.get(arrayPos).getColumn_width();
        }

        return 0;
    }

    public Boolean isColumnVisible(int nthColumn) {  //browser columns are 1-based
        int arrayPos = nthColumn - 1; // array is 0 based
        if(nthColumn <= columns.size() && columns.size() > 0) {
            return columns.get(arrayPos).getColumn_visible();
        }

        return false;
    }



    @Override
    public void onBrowserItemClick(int position, String data) {
        if(focusedView!=null) {
            focusedView.setBackground(null);
        }

        if(rv!=null) {
            focusedView = Objects.requireNonNull(rv.getLayoutManager()).findViewByPosition(position);
            if(focusedView!=null) focusedView.setBackground(background);
        }

        mainListener.displayAlert("Browse execute action", "Execute action for row: " + data);
    }

    @Override
    public void onBrowserItemClickUp(int position) {
        int numChild = rv.getChildCount();

        position = position - 1;
        if(position < 0) position = numChild - 1;
        // scrollToPosition does not work - use requestFocus to change position, getChildAt requires item to be in viewport or it will return null


        if(focusedView!=null) {
            focusedView.setBackground(null);
        }

        focusedView = rv.getLayoutManager().findViewByPosition(position);
        if (focusedView != null) {
            focusedView.requestFocus();
            focusedView.setBackground(background);
        }


    }

    @Override
    public void onBrowserItemClickDown(int position) {
        int numChild = rv.getChildCount();

        position = position + 1;
        if(position >= numChild ) position = 0;
        // scrollToPosition does not work - use requestFocus to change position, getChildAt requires item to be in viewport or it will return null

        if(focusedView!=null) {
            focusedView.setBackground(null);
        }

        focusedView = Objects.requireNonNull(rv.getLayoutManager()).findViewByPosition(position);
        if (focusedView != null) {
            focusedView.requestFocus();
            focusedView.setBackground(background);
        }

    }

}

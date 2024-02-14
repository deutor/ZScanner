package eu.ldaldx.mobile.zscanner.browser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import eu.ldaldx.mobile.zscanner.IMainListener;
import eu.ldaldx.mobile.zscanner.IView;
import eu.ldaldx.mobile.zscanner.R;


public class CustomBrowser extends LinearLayout implements IBrowserListener, IView {

    static class Column {
        protected String column_label;
        protected Boolean column_visible;
        protected Integer column_width;

        protected String column_align;



        Column(String label, Boolean visible, Integer width, String align) {
            this.column_label = label;
            this.column_visible = visible;

            if(!column_visible) column_width = 0;
            this.column_width = Math.abs(width);

            column_align = align;
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

        public String getColumn_align() {
            return column_align;
        }

    }



    private LinearLayout layoutExt;



    private final Drawable background;


    private int parentWidth;
    private View focusedView;
    private String focusedOnGo;

    private IMainListener mainListener;

    RecyclerView rv;
    CustomBrowserAdapter cba;

    ArrayList<CustomBrowserEntry> cbeEntries = new ArrayList<>();
    ArrayList<Column> columnsDef = new ArrayList<>();
    ArrayList<TextView> columnsTextViews = new ArrayList<>();

    Integer numVisible;
    Integer totalWidth;

    public CustomBrowser(FrameLayout parentLayout, Context context, IMainListener listener, int parentWidth) {
        super(context);

        this.mainListener = listener;

        this.parentWidth = parentWidth;
        this.numVisible = 0;
        this.totalWidth = 0;

        layoutExt = parentLayout.findViewById(R.id.browserWithTitleExt);

        background = ResourcesCompat.getDrawable( getResources(), R.drawable.browser_selection_bar, null);

        columnsTextViews.add( parentLayout.findViewById(R.id.txtBrowserCol1_Label ));
        columnsTextViews.add( parentLayout.findViewById(R.id.txtBrowserCol2_Label ));
        columnsTextViews.add( parentLayout.findViewById(R.id.txtBrowserCol3_Label ));
        columnsTextViews.add( parentLayout.findViewById(R.id.txtBrowserCol4_Label ));

        cba = new CustomBrowserAdapter(cbeEntries, this);
        rv = parentLayout.findViewById(R.id.reclerBrowserView);
        if(rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(context));
            rv.setAdapter(cba);


        }

    }

    public String getFocusedOnGo() {
        return focusedOnGo;
    }

    public void hide() {
        layoutExt.setVisibility(INVISIBLE);
        focusedOnGo = "";
    }

    public void show() {
        if(rv!=null) {
            cba.notifyDataSetChanged();
        }
        layoutExt.setVisibility(VISIBLE);
    }


    public CustomBrowser(Context context) {
        super(context);
        background = ResourcesCompat.getDrawable( getResources(), R.drawable.browser_selection_bar, null);
    }

    public void clear() {
        cbeEntries.clear();
        columnsDef.clear();
        numVisible = 0;
        totalWidth = 0;
    }

    public void clearEntries() {
        cbeEntries.clear();
    }

    public void addColumn(String label, Boolean visible, int width, String align) {
        columnsDef.add( new Column(label, visible, width, align));

        if(visible) {
            numVisible++;
            totalWidth = totalWidth + Math.abs(width);
        }
    }

    private void calculateColWidth() {
        int calcWidth;
        for(Column col:columnsDef) {
            calcWidth = col.getColumn_width() * parentWidth / totalWidth;
            col.setColumn_width(calcWidth);
        }
    }

    public void prepareBrowser() {
        TextView txtColumn;

        calculateColWidth();

        for(int i=0;i<columnsDef.size();i++) {
            txtColumn = columnsTextViews.get(i);
            txtColumn.setText( columnsDef.get(i).getColumn_label());
            txtColumn.setWidth( columnsDef.get(i).getColumn_width());

            if(columnsDef.get(i).getColumn_align().equals("right")) {
                txtColumn.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
                txtColumn.setGravity(View.TEXT_ALIGNMENT_VIEW_END);
            }
            else {
                txtColumn.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
            }

            if(columnsDef.get(i).getColumn_visible()) {
                txtColumn.setVisibility(VISIBLE);
            } else {
                txtColumn.setVisibility(INVISIBLE);
            }
        }
    }

    public void setYPositionAndHeight(int y, int height) {
        FrameLayout.LayoutParams fllp = (FrameLayout.LayoutParams)layoutExt.getLayoutParams();

        fllp.height = height;
        fllp.topMargin = y;
        layoutExt.setLayoutParams(fllp);

    }


    public void addRow(String column1, String column2, String column3, String column4, String onGo, String onValue) {
        cbeEntries.add(new CustomBrowserEntry(column1, column2, column3, column4, onGo, onValue, this));
    }

    public Integer getColumnWidth(int nthColumn) {  //browser columns are 1-based
        int arrayPos = nthColumn - 1; // array is 0 based
        if(arrayPos <= columnsDef.size() - 1 ) {
            return columnsDef.get(arrayPos).getColumn_width();
        }

        return 0;
    }

    public Boolean isColumnVisible(int nthColumn) {  //browser columns are 1-based
        int arrayPos = nthColumn - 1; // array is 0 based
        if(arrayPos <= columnsDef.size() - 1) {
            return columnsDef.get(arrayPos).getColumn_visible();
        }

        return false;
    }

    public String getAlign(int nthColumn) {
        int arrayPos = nthColumn - 1; // array is 0 based
        if(arrayPos <= columnsDef.size() - 1) {
            return columnsDef.get(arrayPos).getColumn_align();
        }

        return "";
    }



    @Override
    public void onBrowserItemClick(int position, String data) {
        if(focusedView!=null) {
            focusedView.setBackground(null);
        }

        if(rv!=null) {
            focusedView = Objects.requireNonNull(rv.getLayoutManager()).findViewByPosition(position);
            if(focusedView!=null) {
                focusedView.setBackground(background);
                focusedView.requestFocus();
                focusedView.requestFocusFromTouch();
            }
        }

        onValueChanged(position);
    }

    public void setFocused() {
        if(focusedView != null) {
            focusedView.requestFocus();
            focusedView.requestFocusFromTouch();
        }
    }

    @Override
    public void onBrowserItemClickUp(int position) {
        int positionToSelect;

        if(position == 0) {
            mainListener.moveToPrevTabItem(this);
            return;
        }

        positionToSelect = position - 1;
        if(positionToSelect < 0) positionToSelect = 0;

        rv.scrollToPosition(positionToSelect - 1);

        if(focusedView!=null) {
            focusedView.setBackground(null);
        }

        if(rv.getLayoutManager() != null) {
            focusedView = rv.getLayoutManager().findViewByPosition(positionToSelect);
        }
        if (focusedView != null) {
            focusedView.setBackground(background);

            if(position >= 0) {
                focusedView.requestFocus();
                focusedView.requestFocusFromTouch();
            }
        }

        onValueChanged(positionToSelect);
        setFocused();
    }

    @Override
    public void onBrowserItemClickDown(int position) {
        int numChild = rv.getChildCount();

        position = position + 1;
        if(position >= numChild ) {
                mainListener.moveToNextTabItem(this);
                return;
        }

        // scrollToPosition does not work - use requestFocus to change position, getChildAt requires item to be in viewport or it will return null

        if(focusedView!=null) {
            focusedView.setBackground(null);
        }

        rv.scrollToPosition(position);

        focusedView = Objects.requireNonNull(rv.getLayoutManager()).findViewByPosition(position);
        if (focusedView != null) {
            focusedView.requestFocusFromTouch();
            focusedView.requestFocus();
            focusedView.setBackground(background);
        }

        onValueChanged(position);
    }

    @Override
    public void onBrowserEnterClicked() {
        mainListener.onBrowserEnterClicked();
    }

    public void onValueChanged(int position) {
        mainListener.setControlsOnBrowseVC( cbeEntries.get(position).getOnValueChanged());
        focusedOnGo = cbeEntries.get(position).getOnGo();
    }
}

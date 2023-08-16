package eu.ldaldx.mobile.zscanner;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;



public class CustomMenu extends LinearLayout implements IMenuListener, IView {

    private FrameLayout parentLayout;
    private FrameLayout userLayout;

    private LinearLayout layoutMenuTopLevel;

    private LinearLayout layout;
    private LinearLayout layoutInner;

    private int mainHeight;

    private IMainListener mainListener;
    private TextView titleTextView;
    private int titleHeight;

    RecyclerView rv;
    CustomMenuAdapter cma;

    ArrayList<CustomMenuEntry> cmeEntries = new ArrayList<>();

    public CustomMenu(FrameLayout parentLayout, Context context, String title, IMainListener listener, int height) {

        super(context);
        this.parentLayout = parentLayout;

        this.mainListener = listener;
        this.mainHeight = height;

        layout = (LinearLayout) parentLayout.findViewById(R.id.mainMenuWithTitleExt);
        layoutInner = (LinearLayout) parentLayout.findViewById(R.id.mainMenuWithTitleInt);


        layoutMenuTopLevel = (LinearLayout) parentLayout.findViewById(R.id.mainMenuTopLayout);

        userLayout = (FrameLayout) parentLayout.findViewById(R.id.mainLayout);



        this.titleTextView = (TextView) parentLayout.findViewById(R.id.mainMenuTitle);
        titleTextView.measure(0,0);
        titleHeight = titleTextView.getMeasuredHeight();
        this.setTitle(title);


        View lineMenuEntry = (View) parentLayout.findViewById(R.id.reclerView);
        //float y = lineMenuEntry.getY();

        cma = new CustomMenuAdapter(cmeEntries, this);
        rv = parentLayout.findViewById(R.id.reclerView);
        if(rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(context));
            rv.setAdapter(cma);
        }


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    hide();
            }
        };
    }
    public void setFocused() {
        super.requestFocus();
    }


    protected void hide() {
        //userLayout.bringToFront();
        layout.setVisibility(GONE);
        layoutMenuTopLevel.setVisibility(GONE);
    }

    protected void setTitle(String title) {
        //title = "";

        if(titleTextView != null) titleTextView.setText(title);
        if(title.equals("")) titleTextView.setVisibility(INVISIBLE);
        else titleTextView.setVisibility(VISIBLE);

    }

    protected void show() {
        View vPos0 = rv.getLayoutManager().findViewByPosition(0);
        if(vPos0 != null) vPos0.requestFocus();

        layoutMenuTopLevel.setVisibility(VISIBLE);
        layoutMenuTopLevel.bringToFront();

        Boolean noTitle = titleTextView.getText().equals("");

        LinearLayout.LayoutParams lllp = (LinearLayout.LayoutParams) layoutInner.getLayoutParams();

        rv.requestFocus();

        if(noTitle) {
            lllp.topMargin = -titleHeight + 6;
        }
        else {
            lllp.topMargin = 0;
        }

        layoutInner.setLayoutParams(lllp);


        int nPos = cmeEntries.size() + 1;
        int minSize = nPos * (titleHeight + 8);

        if (!noTitle) minSize = minSize + titleHeight + 8;

        int bottomMargin = (mainHeight - minSize) / 2;

        if (bottomMargin < 20) bottomMargin = 20;
        int topMargin = bottomMargin;
        if (topMargin < 50) topMargin = 50;

        if(noTitle) {
                topMargin = topMargin + bottomMargin - 4;
                bottomMargin = 0;
        }

        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) layout.getLayoutParams();
        llp.topMargin = topMargin;
        llp.bottomMargin = bottomMargin;
        layout.setLayoutParams(llp);


        layout.setVisibility(VISIBLE);
    }


    public CustomMenu(Context context) {
        super(context);
    }

    public void clear() {
        cmeEntries.clear();
    }
    public void addItem(String label, String action, Boolean localAction) {
        cmeEntries.add(new CustomMenuEntry(label, action, localAction));
    }

    public void onMenuItemClick(int position, String data) {
        mainListener.displayAlert("test", data);
        hide();
    }

    @Override
    public void onMenuItemClickUp(int position) {
        int numChild = rv.getChildCount();

        if(position < 0) rv.getLayoutManager().findViewByPosition(0).requestFocus();
        else {

            position = position - 1;
            if (position < 0) position = numChild - 1;
            // scrollToPosition does not work - use requestFocus to change position, getChildAt requires item to be in viewport or it will return null
            rv.getLayoutManager().findViewByPosition(position).requestFocus();
        }
    }

    @Override
    public void onMenuItemClickDown(int position) {
        int numChild = rv.getChildCount();

        position = position + 1;
        if(position >= numChild ) position = 0;
        // scrollToPosition does not work - use requestFocus to change position, getChildAt requires item to be in viewport or it will return null
        rv.getLayoutManager().findViewByPosition(position).requestFocus();
    }

    @Override
    public void onNumericPressed(int numeric) {
        int position = -1;

        for(int i=0;i< cmeEntries.size();i++) {
            CustomMenuEntry cme = cmeEntries.get(i);
            if(cme.getMenuEntry() == null) continue;
             if( cme.getMenuEntry().charAt(0) == Integer.toString(numeric).charAt(0)) {
                mainListener.executeMenuAction(cme.getAction(), cme.getLocalAction());

                break;
            }
        }
    }
}

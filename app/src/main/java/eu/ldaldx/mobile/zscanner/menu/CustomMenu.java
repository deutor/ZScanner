package eu.ldaldx.mobile.zscanner.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import eu.ldaldx.mobile.zscanner.IMainListener;
import eu.ldaldx.mobile.zscanner.IView;
import eu.ldaldx.mobile.zscanner.R;


public class CustomMenu extends LinearLayout implements IMenuListener, IView {

    private Boolean isVisible = false;

    private LinearLayout layoutMenuInt;
    private LinearLayout layoutMenuTopLevel;

    private LinearLayout layoutMenuWithTitleExt;

    private IMainListener mainListener;
    private TextView titleTextView;

    private String backAction;

    private final Drawable background;

    private View focusedView;

    RecyclerView rv;
    CustomMenuAdapter cma;

    ArrayList<CustomMenuEntry> cmeEntries = new ArrayList<>();

    public CustomMenu(FrameLayout parentLayout, Context context, IMainListener listener) {

        super(context);
        this.mainListener = listener;

        layoutMenuTopLevel = parentLayout.findViewById(R.id.mainMenuTopLayout);
        layoutMenuWithTitleExt = parentLayout.findViewById(R.id.mainMenuWithTitleExt);
        layoutMenuInt = parentLayout.findViewById(R.id.mainMenuWithTitleInt);

        background = ResourcesCompat.getDrawable( getResources(), R.drawable.menu_selection_bar, null);

        this.titleTextView = parentLayout.findViewById(R.id.mainMenuTitle);

        cma = new CustomMenuAdapter(cmeEntries, this);
        rv = parentLayout.findViewById(R.id.menuRecyclerView);
        if(rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(context));
            rv.setAdapter(cma);

            DividerItemDecoration divider = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            rv.addItemDecoration(divider);
        }

    }
    public void setFocused() {
        if(focusedView != null) {
            focusedView.requestFocus();
            focusedView.requestFocusFromTouch();
        }
    }


    public void hide() {
        backAction = "";
        isVisible = false;
        layoutMenuTopLevel.setVisibility(GONE);
    }

    public void setTitle(String title) {
        if(titleTextView != null) {
            titleTextView.setText(title);
            if (title.equals("")) titleTextView.setVisibility(INVISIBLE);
            else titleTextView.setVisibility(VISIBLE);
        }

    }

    public void show() {
        boolean noTitle = titleTextView.getText().equals("");

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutMenuWithTitleExt.getLayoutParams();

        layoutMenuTopLevel.setVisibility(INVISIBLE);
        if(noTitle) {
                titleTextView.setVisibility(GONE);
                params.gravity = Gravity.BOTTOM;
        }
        else {
            titleTextView.setVisibility(VISIBLE);
            params.gravity = Gravity.TOP;
        }

        layoutMenuWithTitleExt.setLayoutParams(params);

        layoutMenuTopLevel.setVisibility(VISIBLE);
        layoutMenuTopLevel.bringToFront();

        layoutMenuTopLevel.clearFocus();
        layoutMenuWithTitleExt.clearFocus();


        if(rv!=null) {
            cma.notifyDataSetChanged();
            rv.requestFocusFromTouch();
        }

        isVisible = true;
    }


    public CustomMenu(Context context) {
        super(context);
        background = ResourcesCompat.getDrawable( getResources(), R.drawable.menu_selection_bar, null);
    }

    public void clear() {
        cmeEntries.clear();
    }
    public void addItem(String label, String action, String actionArgs) {
        cmeEntries.add(new CustomMenuEntry(label, action, actionArgs));
    }

    public void onMenuItemClick(int position, String action, String actionArgs) {
        mainListener.executeMenuAction(action, actionArgs);
    }

    @Override
    public void onMenuItemClickUp(int position) {
        int positionToSelect;

        assert rv != null;

        positionToSelect = position - 1;
        if(positionToSelect < 0) positionToSelect = 0;

        if(focusedView!=null) {
            focusedView.setBackground(null);
        }

        if(rv.getLayoutManager() != null) {
            focusedView = rv.getLayoutManager().findViewByPosition(positionToSelect);
        }
        if (focusedView != null) {
            focusedView.setBackground(background);

            focusedView.requestFocus();
            focusedView.requestFocusFromTouch();
        }

        setFocused();
    }

    @Override
    public void onMenuItemClickDown(int position) {
        assert rv != null;

        int numChild = rv.getChildCount();

        position = position + 1;
        if(position >= numChild ) position = numChild - 1;

        // scrollToPosition does not work - use requestFocus to change position, getChildAt requires item to be in viewport or it will return null

        if(focusedView!=null) {
            focusedView.setBackground(null);
        }

        focusedView = Objects.requireNonNull(rv.getLayoutManager()).findViewByPosition(position);
        if (focusedView != null) {
            focusedView.requestFocusFromTouch();
            focusedView.requestFocus();
            focusedView.setBackground(background);
        }
    }

    @Override
    public void onNumericPressed(int numeric) {
        for(int i=0;i< cmeEntries.size();i++) {
            CustomMenuEntry cme = cmeEntries.get(i);
            if(cme.getMenuEntry() == null) continue;

            if( cme.getMenuEntry().charAt(0) == Integer.toString(numeric).charAt(0)) {
                mainListener.executeMenuAction(cme.getAction(), cme.getActionArgs());
                break;
            }
        }
    }

    public Boolean isVisible() {
        return isVisible;
    }

    public String getBackAction() {
        return backAction;
    }

    public void setBackAction(String backAction) {
        this.backAction = backAction;
    }
}

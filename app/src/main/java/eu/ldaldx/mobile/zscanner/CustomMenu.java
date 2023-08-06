package eu.ldaldx.mobile.zscanner;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;



public class CustomMenu extends LinearLayout implements IMenuListener {

    private FrameLayout parentLayout;
    private LinearLayout layout;
    private LinearLayout layoutInner;

    private String[] mDataset;

    private IMainListener mainListener;

    RecyclerView rv;
    CustomMenuAdapter cma;

    HashMap<Integer,TextView> entries = new HashMap<>();
    public CustomMenu(FrameLayout parentLayout, Context context, String title, IMainListener listener) {

        super(context);
        this.parentLayout = parentLayout;
        this.mainListener = listener;

        layout = (LinearLayout) parentLayout.findViewById(R.id.mainMenuWithTitleExt);
        layoutInner = (LinearLayout) parentLayout.findViewById(R.id.mainMenuWithTitleInt);
        TextView txt = (TextView) parentLayout.findViewById(R.id.mainMenuTitle);

        txt.setText(title);

        mDataset = new String[6];

        for (int i = 0; i < 5; i++) {
            mDataset[i] = (i + 1) + " - This is element";
        }

        cma = new CustomMenuAdapter(mDataset, this);
        rv = parentLayout.findViewById(R.id.reclerView);
        if(rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(context));
            rv.setAdapter(cma);
        }


    }

    protected void hide() {
        layout.setVisibility(INVISIBLE);
    }

    protected void show() {
        layout.setVisibility(VISIBLE);
    }


    public CustomMenu(Context context) {
        super(context);
    }

    public void AddItem(String label) {
    }

    public void onMenuItemClick(int position, String data) {
        mainListener.displayAlert("test", data);
    }

    @Override
    public void onMenuItemClickUp(int position) {
        int numChild = rv.getChildCount();

        position = position - 1;
        if(position < 0) position = numChild - 1;
        // scrollToPosition does not work - use requestFocus to change position, getChildAt requires item to be in viewport or it will return null
        rv.getLayoutManager().findViewByPosition(position).requestFocus();
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

        for(int i=0;i< mDataset.length;i++) {
             if(mDataset[i] != null && mDataset[i].charAt(0) == Integer.toString(numeric).charAt(0)) {
                position = i;
                break;
            }
        }

        if(position >= 0 && position < rv.getChildCount() ) {
            rv.getLayoutManager().findViewByPosition(position).requestFocus();
        }
    }
}

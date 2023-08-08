package eu.ldaldx.mobile.zscanner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CustomMenuAdapter extends RecyclerView.Adapter<CustomMenuAdapter.ViewHolder> {
    private ArrayList<CustomMenuEntry> localCmeEntries;
    private IMenuListener menuListener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView menuEntry;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            menuEntry = (TextView) view.findViewById(R.id.txtMenuEntry);
        }

        public TextView getMenuItem() {
            return menuEntry;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public CustomMenuAdapter(ArrayList<CustomMenuEntry> cmeArray, IMenuListener listener) {
        localCmeEntries = cmeArray;
        this.menuListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.menu_entry, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        TextView tv;

        tv = viewHolder.getMenuItem();

/*
        LinearLayout.LayoutParams lllp = (LinearLayout.LayoutParams)tv.getLayoutParams();

        lllp.width = 200;
        tv.setVisibility(View.INVISIBLE);
        */

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        tv.setText( localCmeEntries.get(position).getMenuEntry());

        tv.setOnClickListener(v -> {
            menuListener.onMenuItemClick( position, Integer.toString(position) );
        });

        tv.setOnKeyListener( new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keycode, KeyEvent event) {
                int numeric = 0;
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch(keycode) {
                        case KeyEvent.KEYCODE_1:
                            numeric = 1;
                            break;
                        case KeyEvent.KEYCODE_2:
                            numeric = 2;
                            break;
                        case KeyEvent.KEYCODE_3:
                            numeric = 3;
                            break;
                        case KeyEvent.KEYCODE_4:
                            numeric = 4;
                            break;
                        case KeyEvent.KEYCODE_5:
                            numeric = 5;
                            break;
                        case KeyEvent.KEYCODE_6:
                            numeric = 6;
                            break;
                        case KeyEvent.KEYCODE_7:
                            numeric = 7;
                            break;
                        case KeyEvent.KEYCODE_8:
                            numeric = 8;
                            break;
                        case KeyEvent.KEYCODE_9:
                            numeric = 9;
                            break;
                        case KeyEvent.KEYCODE_DPAD_UP:
                            menuListener.onMenuItemClickUp(position);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            menuListener.onMenuItemClickDown(position);
                            return true;
                    } // switch

                    if(numeric > 0) {
                        menuListener.onNumericPressed(numeric);
                    }


                } // key down
                return false;
            }

        })
        ;



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localCmeEntries.size();
    }
}



package eu.ldaldx.mobile.zscanner.menu;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import eu.ldaldx.mobile.zscanner.R;


public class CustomMenuAdapter extends RecyclerView.Adapter<CustomMenuAdapter.ViewHolder> {
    private ArrayList<CustomMenuEntry> localCmeEntries;
    private IMenuListener menuListener;


    @Override
    public void onViewAttachedToWindow(@NonNull CustomMenuAdapter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(holder.getAdapterPosition() == 0) {
            menuListener.onMenuItemClickUp(-1);
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView menuEntry;
        private final TableRow row;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            menuEntry = (TextView) view.findViewById(R.id.txtMenuEntry);

            row = view.findViewById(R.id.menuRow);
        }

        public TextView getMenuItem() {
            return menuEntry;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param cmeArray ArrayList<CustomMenuEntry> containing the data to populate views to be used
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
        TableRow row;
        tv = viewHolder.getMenuItem();

        row = viewHolder.row;

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        tv.setText( localCmeEntries.get(position).getMenuEntry());

        row.setOnClickListener(v -> {
            menuListener.onMenuItemClick( position, localCmeEntries.get(position).getAction(), localCmeEntries.get(position).getActionArgs() );
        });

        row.setOnKeyListener( new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keycode, KeyEvent event) {
                int numeric = -1;
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch(keycode) {
                        case KeyEvent.KEYCODE_0:
                            numeric = 0;
                            break;
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
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                        case KeyEvent.KEYCODE_DPAD_UP:
                            menuListener.onMenuItemClickUp(position);
                            return true;
                        case KeyEvent.KEYCODE_TAB:
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            menuListener.onMenuItemClickDown(position);
                            return true;

                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_NUMPAD_ENTER:
                            menuListener.onMenuItemClick( position, localCmeEntries.get(position).getAction(), localCmeEntries.get(position).getActionArgs() );
                            return true;


                        case KeyEvent.KEYCODE_ESCAPE:
                            //
                    } // switch

                    if(numeric >= 0) {
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



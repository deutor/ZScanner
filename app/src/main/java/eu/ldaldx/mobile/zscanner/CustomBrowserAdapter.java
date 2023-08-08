package eu.ldaldx.mobile.zscanner;

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


public class CustomBrowserAdapter extends RecyclerView.Adapter<CustomBrowserAdapter.ViewHolder> {
    private final ArrayList<CustomBrowserEntry> localCbeEntries;
    private final IBrowserListener browserListener;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView[] columns = new TextView[5];
        private final TableRow row;

        public ViewHolder(View view) {
            super(view);

            columns[0] = (TextView) view.findViewById(R.id.txtBrowserCol1);
            columns[1] = (TextView) view.findViewById(R.id.txtBrowserCol2);
            columns[2] = (TextView) view.findViewById(R.id.txtBrowserCol3);
            columns[3] = (TextView) view.findViewById(R.id.txtBrowserCol4);

            row = (TableRow) view.findViewById(R.id.browserRow);
        }


        public TextView getTextViewColumn(int nthColumn) { // 0-based
            int arrPos = nthColumn - 1;
            if( arrPos >= 0 && arrPos < columns.length) {
                return columns[ arrPos ];
            }
            return null;
        }

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param cbeArray ArrayList<CustomBrowserEntry>[] containing the data to populate views to be used
     * by RecyclerView
     */
    public CustomBrowserAdapter(ArrayList<CustomBrowserEntry> cbeArray, IBrowserListener listener) {
        localCbeEntries = cbeArray;
        this.browserListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.browser_entry, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        TableRow row;
        TextView textViewColumn;
        int nthColumn;

        CustomBrowserEntry cbe = localCbeEntries.get(position);

        row = viewHolder.row;

        for(int i = 0; i<4;i++) {
            nthColumn = i + 1; // browser columns are 1-based

            textViewColumn = viewHolder.getTextViewColumn(nthColumn);
            if(textViewColumn != null) {
                textViewColumn.setWidth(cbe.getColumnWidth(nthColumn));
                if (cbe.isColumnVisible(nthColumn)) {
                    textViewColumn.setVisibility(View.VISIBLE);
                } else {
                    textViewColumn.setVisibility(View.INVISIBLE);
                }

                textViewColumn.setText(cbe.getColumnValue(nthColumn));
            }
        }

        row.setOnKeyListener((v, keycode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keycode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        browserListener.onBrowserItemClickUp(position);
                        return true;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        browserListener.onBrowserItemClickDown(position);
                        return true;
                } // switch

            } // key down
            return false;
        });

        row.setOnClickListener(v -> browserListener.onBrowserItemClick( position, Integer.toString(position) ));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localCbeEntries.size();
    }
}



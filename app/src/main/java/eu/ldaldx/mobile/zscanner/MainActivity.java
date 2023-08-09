package eu.ldaldx.mobile.zscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import eu.ldaldx.mobile.zscanner.databinding.ActivityLoginBinding;
import eu.ldaldx.mobile.zscanner.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements IMainListener {
    private ActivityMainBinding binding;
    private CustomMenu csMenu;
    private CustomBrowser csBrowser;

    private BroadcastReceiver zebraBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            if (b != null) {
                for (String key : b.keySet()) {
                    Log.e("ldaldx", key + " : " + (b.get(key) != null ? b.get(key) : "NULL"));
                }
            }

            //
            // The following is useful for debugging to verify
            // the format of received intents from DataWedge:
            //
            // for (String key : b.keySet())
            // {
            //   Log.v(LOG_TAG, key);
            // }
            //

            if (action.equals(getResources().getString(R.string.zebra_activity_intent_filter_action))) {
                //
                //  Received a barcode scan
                //

                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {

                    //
                    // Catch if the UI does not exist when broadcast is received
                    //
                }
            }
        }
    };

    private void displayScanResult(Intent initiatingIntent, String howDataReceived)
    {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_label_type));

        displayAlert("decodedSource", decodedSource + "\n" + decodedData + "\n" + decodedLabelType);
//        displayAlert("decodedData", decodedData);
//        displayAlert("decodedLabelType", decodedLabelType);

    }


    public void displayAlert(String title, String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @Override
    public void executeMenuAction(String action, Boolean local) {
        displayAlert("Execute menu action", action + " " + local);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        FrameLayout layout = (FrameLayout) findViewById(R.id.mainLayout);
        csMenu = new CustomMenu(layout, layout.getContext(), "Menu główne", this, height);
        csBrowser = new CustomBrowser(layout, layout.getContext(), this, width, height);







        csBrowser.clear();
        csBrowser.addColumn("Pierwsza", true, 100, false);
        csBrowser.addColumn("Druga", true, 200, false);
        csBrowser.addColumn("Trzecia", true, 200, false);
        csBrowser.addColumn("Czwarta", false, 400, false);
        csBrowser.prepareBrowser();

        for(int i = 0;i<5;i++) {
            csBrowser.addRow("col1", "col2:" + Integer.toString(i), "col3:" + Integer.toString(i), "col4:" + Integer.toString(i));
        }
        csBrowser.show();


        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.zebra_activity_intent_filter_action));
        registerReceiver(zebraBroadcastReceiver, filter);

        Button btn = findViewById(R.id.button);

        btn.setOnClickListener( (v) -> {
            csBrowser.hide();
            csBrowser.clear();
            csBrowser.setYPositionAndHeight(100, 200);
            csBrowser.addColumn("Pierwsza", true, 100, false);
            csBrowser.addColumn("Druga", true, 200, false);
            csBrowser.addColumn("Trzecia", false, 0, false);
            csBrowser.addColumn("Czwarta", false, 0, false);
            csBrowser.prepareBrowser();

            for(int i = 0;i<5;i++) {
                csBrowser.addRow("1", "uucol2:" + Integer.toString(i), "col3:" + Integer.toString(i), "col4:" + Integer.toString(i));
            }
            csBrowser.show();
            csBrowser.setEnabled(true);
        });

/* 320x460 */
        CustomLabel csLabel = new CustomLabel(this, 300, 30, 200, 10);
        layout.addView(csLabel);
        csLabel.setText("Skanuj lokalizację:");

        CustomEdit csEdit = new CustomEdit(this, 350, 30, 0, 0);
        layout.addView(csEdit);
        csEdit.setText("Skanuj lokalizację z:");


        CustomEdit csEdit2 = new CustomEdit(this, 120, 30, 330, 20);
        layout.addView(csEdit2);
        csEdit2.setText("Skanuj lokalizację do:");

        csEdit2.bringToFront();

        csMenu.clear();
        csMenu.setTitle("Menu");
        csMenu.addItem("1 - Lokalizacja", "LOK/lokalizacja.js", false);
        csMenu.addItem("2 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("3 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("4 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        //csMenu.addItem("4 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("ESC Koniec pracy", "PRZ/pzresunięcia.js", true);
        csMenu.bringToFront();
        csMenu.show();

//        csMenu.hide();




        /*
        FrameLayout layout = (FrameLayout) findViewById(R.id.mainLayout);


        TextView dynamicTextView = new TextView(this);


        FrameLayout.LayoutParams frlp = new FrameLayout.LayoutParams(480,190);
        frlp.leftMargin = 0;
        frlp.topMargin = 10;

        dynamicTextView.setLayoutParams(frlp);
        dynamicTextView.setTextColor(Color.RED);
        dynamicTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        dynamicTextView.setText("NewYork is very large city located in United States Of America");

        layout.addView(dynamicTextView);
*/




    }  // onCreate

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(zebraBroadcastReceiver != null) unregisterReceiver(zebraBroadcastReceiver);
    }

}
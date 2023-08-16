package eu.ldaldx.mobile.zscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import eu.ldaldx.mobile.zscanner.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements IMainListener {

    private float screenPixelDensity;

    private int convP2DP(int pixels) {
        float dpValue = pixels * screenPixelDensity;


        return Math.round(dpValue);
    }

    FrameLayout frameLayout;
    private ActivityMainBinding binding;
    private CustomMenu csMenu;
    private CustomBrowser csBrowser;

    private String userID;
    private String sessionID;

    private HashMap<String,View> listOfViews = new HashMap<>();
    private HashMap<String,View> wantsGS1 = new HashMap<>();

    private ArrayList<IView> tabOrder = new ArrayList<>();

    private BroadcastReceiver zebraBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
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

        displayAlert("main decodedSource", decodedSource + "\n" + decodedData + "\n" + decodedLabelType);
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

    // change fields upon value-changed event of browse
    @Override
    public void setControlsOnBrowseVC(String setString) {
        if((setString == null) || (setString.length() == 0)) return;
        String[] lstElem = setString.split( "^");
        String[] lstValue;
        for(int i = 0; i< lstElem.length;i++) {
            lstValue = lstElem[i].split("=");
            if(lstValue.length < 2) continue;

            if(listOfViews.containsKey(lstValue[0])) {
                View vw = listOfViews.get(lstValue[0]);
                CustomEdit ce;
                CustomLabel cl;

                if( CustomEdit.class.isInstance(vw)) {
                    ce = (CustomEdit)vw;
                    ce.setText(lstValue[1]);
                }

                if( CustomLabel.class.isInstance(vw)) {
                    cl = (CustomLabel)vw;
                    cl.setText(lstValue[1]);
                }

            }
        }
    }

    @Override
    public void onBrowserEnterClicked() {
        onFrameGo();
    }

    private void onFrameGo() {
        HashMap<String,String> lov = new HashMap<>();

        // collect data from browser
        if(csBrowser.getFocusedOnGo() != null) {
            String[] lstElem = csBrowser.getFocusedOnGo().split("^");
            String[] lstValue;
            for (int i = 0; i < lstElem.length; i++) {
                lstValue = lstElem[i].split("=");
                if (lstValue.length < 2) continue;
                lov.put(lstValue[0], lstValue[1]);
            }
        }

        // collect data from edits
        for(String key: listOfViews.keySet()) {
            View vw = listOfViews.get(key);
            String value = "";

            if( CustomEdit.class.isInstance(vw)) {
                CustomEdit ce = (CustomEdit)vw;
                value = ce.getText().toString();
            }

            if( CustomLabel.class.isInstance(vw)) {
                CustomLabel cl = (CustomLabel)vw;
                value = cl.getText().toString();
            }

            // overwrite browser values only if Edit/Label value is not empty
            if(lov.containsKey(key)) {
                if (value.length() > 0) lov.put(key, value);
            } else {
                lov.put(key, value);
            }
        }

        // send
        int i = 0;
    }

    private void doSendDataToFocused(String rawData) {
        for(IView vw : tabOrder) {
            if( CustomEdit.class.isInstance(vw)) {
                CustomEdit ce = (CustomEdit) vw;
                if(ce.isFocused()) ce.setText(rawData);
            }
        }
    }

    @Override
    public Boolean moveToPrevTabItem(IView vw) {
        int idx;
        IView iview;
        idx = tabOrder.indexOf( vw );
        if(idx <= 0) return false;

        iview = tabOrder.get(idx - 1);
        if(iview!= null) {
            iview.setFocused();
            return true;
        }

        return false;
    }

    public Boolean moveToNextTabItem(IView vw) {
        int idx;
        IView iview;
        idx = tabOrder.indexOf( vw );
        if(idx < 0 || idx +1 == tabOrder.size()) return false;

        iview = tabOrder.get(idx + 1);
        if(iview!= null) {
            iview.setFocused();
            return true;
        }

        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent().hasExtra("userID")) {
            userID = getIntent().getStringExtra("userID");
        }

        if(getIntent().hasExtra("sessionID")) {
            sessionID = getIntent().getStringExtra("sessionID");
        }

//        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
//        startActivity(login);

        binding.buttonForward.setOnClickListener(v -> { onFrameGo(); });


        binding.button33.setOnClickListener(v -> {
            //send broadcast
            Intent localIntent = new Intent(getString(R.string.zebra_activity_intent_filter_action));


            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_source), "scanner");
            localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_label_type), "typ_etykiety");
            //localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_data), GS1_Decoder.ctrlFNC1 + "91PROD" + GS1_Decoder.ctrlGS + "92192.168.43.1" + GS1_Decoder.ctrlGS + "9344044"+ GS1_Decoder.ctrlGS);
            localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_data), GS1_Decoder.ctrlFNC1 + "94TOKEN-UZYTKOWNIKA" + GS1_Decoder.ctrlGS);


/*            sendBroadcast(localIntent);*/

            doAction();




            });



        screenPixelDensity = getApplicationContext().getResources().getDisplayMetrics().density;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        frameLayout = (FrameLayout) findViewById(R.id.mainLayout);
        csMenu = new CustomMenu(frameLayout, frameLayout.getContext(), "Menu główne", this, height);
        csMenu.hide();

        csBrowser = new CustomBrowser(frameLayout, frameLayout.getContext(), this, width, height);
        csBrowser.hide();

        frameLayout.setOnKeyListener((v, keycode, event) -> {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keycode) {
                            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                                displayAlert("enter", "enter");
                                return true;

                            case KeyEvent.KEYCODE_ENTER:
                                displayAlert("enter", "enter");
                                return true;
                        } // switch
                    } // key down
                    return false;
                }
        );

        /*
        csBrowser.clear();
        csBrowser.addColumn("Lokalizacja", true, 100, "");
        csBrowser.addColumn("Kod", true, 200, "");
        csBrowser.addColumn("Kod", false, 200, "");
        csBrowser.addColumn("Kod", false, 200, "");
*/


        /*
        csBrowser.addRow("NOWA",     "...005124010008668", "1", "2");
        csBrowser.addRow("SP-NC 2T", "...005124010008651", "1", "2");
        csBrowser.addRow("B-R02",    "...005124010008637", "1", "2");
        csBrowser.addRow("SAMOCHOD", "...005124010008644", "1", "2");
        csBrowser.addRow("H-R02-2",  "...005124010004719", "1", "2");
        csBrowser.addRow("H-R02-2",  "...005124010004696", "1", "2");
        csBrowser.addRow("A",        "...005124010000632", "1", "2");
*/

/*
        csBrowser.setYPositionAndHeight(convP2DP(120), convP2DP( 180));
        //csBrowser.addRow("B-R02",    "...005124010008637", "1", "2");
        csBrowser.prepareBrowser();
        csBrowser.show();

        csBrowser.onBrowserItemClickUp(-1);
        //csBrowser.hide();
*/




        /*  fragmentacja - pobranie palety

        CustomLabel cs1_1 = new CustomLabel(this, convP2DP(100), convP2DP(30), 0, convP2DP(0));
        layout.addView(cs1_1);
        cs1_1.setText("Magazyn:");

        CustomLabel cs1_2 = new CustomLabel(this, convP2DP(100), convP2DP(30), 0, convP2DP(100));
        layout.addView(cs1_2);
        cs1_2.setText("2T");
        cs1_2.setBoldItalic(true, false);

        CustomLabel cs2_1 = new CustomLabel(this, convP2DP(100), convP2DP(30), convP2DP(30), convP2DP(0));
        layout.addView(cs2_1);
        cs2_1.setText("Indeks:");

        CustomLabel cs2_2 = new CustomLabel(this, convP2DP(100), convP2DP(30), convP2DP(30), convP2DP(100));
        layout.addView(cs2_2);
        cs2_2.setText("03089-6");
        cs2_2.setBoldItalic(true, false);

        //new CustomLabel(this, 0,1,2,3);
        CustomLabel cs3_1 = new CustomLabel(this, convP2DP(600), convP2DP(30), convP2DP(60), convP2DP(0));
        layout.addView(cs3_1);
        cs3_1.setText("Maślanka stracciatella 400g");


        CustomLabel cs4_1 = new CustomLabel(this, convP2DP(200), convP2DP(30), convP2DP(100), convP2DP(0));
        layout.addView(cs4_1);
        cs4_1.setText("Skanuj etykietę:");
        cs4_1.setBoldItalic(true, false);

//        CustomEdit ce1 = new CustomEdit(this, convP2DP(180), convP2DP(30), convP2DP(90), convP2DP(140));
        CustomEdit ce1 = new CustomEdit(this, convP2DP(320), convP2DP(30), convP2DP(390), convP2DP(00));
        ce1.setText("123456789_123456789_12345");
        layout.addView(ce1);


        csBrowser = new CustomBrowser(layout, layout.getContext(), this, width, height);
        csBrowser.clear();
        csBrowser.addColumn("Lokalizacja", true, 100, false);
        csBrowser.addColumn("Kod", true, 200, false);
        csBrowser.addColumn("Kod", false, 200, false);
        csBrowser.addColumn("Kod", false, 200, false);

        csBrowser.addRow("NOWA",     "...005124010008668", "1", "2");
        csBrowser.addRow("SP-NC 2T", "...005124010008651", "1", "2");
        csBrowser.addRow("B-R02",    "...005124010008637", "1", "2");
        csBrowser.addRow("SAMOCHOD", "...005124010008644", "1", "2");
        csBrowser.addRow("H-R02-2",  "...005124010004719", "1", "2");
        csBrowser.addRow("H-R02-2",  "...005124010004696", "1", "2");
        csBrowser.addRow("A",        "...005124010000632", "1", "2");

        csBrowser.setYPositionAndHeight(convP2DP(120), convP2DP( 180));
        csBrowser.prepareBrowser();
        csBrowser.show();


        CustomLabel cs5_1 = new CustomLabel(this, convP2DP(100), convP2DP(30), convP2DP(300), convP2DP(0));
        layout.addView(cs5_1);
        cs5_1.setText("Kod:");

        CustomLabel cs5_2 = new CustomLabel(this, convP2DP(300), convP2DP(30), convP2DP(300), convP2DP(100));
        layout.addView(cs5_2);
        cs5_2.setText("00959005124010008668");
        cs5_2.setBoldItalic(true, false);

        CustomLabel cs6_1 = new CustomLabel(this, convP2DP(100), convP2DP(30), convP2DP(330), convP2DP(0));
        layout.addView(cs6_1);
        cs6_1.setText("Nośnik:");

        CustomLabel cs6_2 = new CustomLabel(this, convP2DP(300), convP2DP(30), convP2DP(330), convP2DP(100));
        layout.addView(cs6_2);
        cs6_2.setText("007590051200000001097");
        cs6_2.setBoldItalic(true, false);


        CustomLabel cs7_1 = new CustomLabel(this, convP2DP(100), convP2DP(30), convP2DP(360), convP2DP(0));
        layout.addView(cs7_1);
        cs7_1.setText("Partia:");

        CustomLabel cs7_2 = new CustomLabel(this, convP2DP(300), convP2DP(30), convP2DP(360), convP2DP(100));
        layout.addView(cs7_2);
        cs7_2.setText("00220825L869");
        cs7_2.setBoldItalic(true, false);


        CustomLabel cs8_1 = new CustomLabel(this, convP2DP(100), convP2DP(30), convP2DP(390), convP2DP(0));
        layout.addView(cs8_1);
        cs8_1.setText("Dostępne:");

        CustomLabel cs8_2 = new CustomLabel(this, convP2DP(300), convP2DP(30), convP2DP(390), convP2DP(100));
        layout.addView(cs8_2);
        cs8_2.setText("1 500 szt");
        cs8_2.setBoldItalic(true, false);

*/

        /*



        csBrowser.setYPositionAndHeight(convP2DP(35), convP2DP( 180));
        csBrowser.clear();
        csBrowser.addColumn("Lokalizacja", true, 200, false);
        csBrowser.addColumn("Ilość", true, 200, false);
        csBrowser.addColumn("Trzecia", false, 200, false);
        //csBrowser.addColumn("Czwarta", false, 400, false);
        csBrowser.prepareBrowser();

        csBrowser.addRow("SP-SC", "7108 kg", "1", "2");
        csBrowser.addRow("H-R71-1", "720 kg", "1", "2");
        csBrowser.addRow("ROZB", "998 kg", "1", "2");
        csBrowser.addRow("SP-NC", "200 kg", "1", "2");
        csBrowser.addRow("A-R01", "1200 kg", "1", "2");
        csBrowser.addRow("A-R02", "600 kg", "1", "2");
        csBrowser.addRow("A-R05", "110 kg", "1", "2");

        csBrowser.show();


        csBrowser.clear();
        csBrowser.addColumn("Lokalizacja", true, 200, false);
        csBrowser.addColumn("Ilość", true, 200, false);
        csBrowser.addColumn("Trzecia", false, 200, false);
        //csBrowser.addColumn("Czwarta", false, 400, false);
        csBrowser.prepareBrowser();

        csBrowser.addRow("SP-SC", "7108 kg", "1", "2");
        csBrowser.addRow("H-R71-1", "720 kg", "1", "2");
        csBrowser.addRow("ROZB", "998 kg", "1", "2");
        csBrowser.addRow("SP-NC", "200 kg", "1", "2");
        csBrowser.addRow("A-R01", "1200 kg", "1", "2");
        csBrowser.addRow("A-R02", "600 kg", "1", "2");
        csBrowser.addRow("A-R05", "110 kg", "1", "2");

        csBrowser.show();




        CustomLabel csLabel2 = new CustomLabel(this, convP2DP(300), convP2DP(30), convP2DP(240), convP2DP(10));
        layout.addView(csLabel2);
        csLabel2.setText("02162-50");

        CustomLabel csLabel3 = new CustomLabel(this, convP2DP(300), convP2DP(30), convP2DP(275), convP2DP(10));
        layout.addView(csLabel3);
        csLabel3.setText("Masło Ekstra Polskie 200g");

        CustomLabel csLabel4 = new CustomLabel(this, convP2DP(300), convP2DP(30), convP2DP(310), convP2DP(10));
        layout.addView(csLabel4);
        csLabel4.setText("Magazyn 2");

        CustomLabel csLabel5 = new CustomLabel(this, convP2DP(300), convP2DP(30), convP2DP(345), convP2DP(10));
        layout.addView(csLabel5);
        csLabel5.setText("EAN: 5900512300108");


*/

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.zebra_activity_intent_filter_action));
        registerReceiver(zebraBroadcastReceiver, filter);


/*

        Button btn = findViewById(R.id.button);

        btn.setOnClickListener( (v) -> {
            csBrowser.hide();
            csBrowser.clear();
            csBrowser.setYPositionAndHeight(100, 200);
            csBrowser.addColumn("Pierwsza", true, 100, false);
            csBrowser.addColumn("Druga", true, 200, false);
            csBrowser.prepareBrowser();

            for(int i = 0;i<1;i++) {
                csBrowser.addRow("uucol2:" + Integer.toString(i), "2" , "" , "");
            }
            csBrowser.show();

        });

        CustomEdit csEdit2 = new CustomEdit(this, convP2DP(300), convP2DP(20), convP2DP(0), convP2DP(10));
        layout.addView(csEdit2);
        csEdit2.setText("Skanuj lokalizację do:");

*/
        /* 320x420 */


/*
        csMenu.clear();
        csMenu.setTitle("Wydania");

        csMenu.addItem("1 - Lokalizacja", "LOK/lokalizacja.js", false);
        csMenu.addItem("2 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("3 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("4 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        //csMenu.addItem("4 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("ESC Koniec pracy", "PRZ/pzresunięcia.js", true);

 */
/*
        csMenu.addItem("1 - Odwrotna", "LOK/lokalizacja.js", false);
        csMenu.addItem("2 - Rezerwacyjna", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("3 - Normalna", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("4 - Dokładki", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("5 - Załadunek", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("6 - Drukuj załadunek", "LOK/lokalizacja.js", false);
        csMenu.addItem("7 - Dekompletacja", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("8 - Komasacja palet", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("9 - Rozładunek", "PRZ/pzresunięcia.js", false);

        //csMenu.addItem("4 - Przesunięcia", "PRZ/pzresunięcia.js", false);
        csMenu.addItem("ESC - Koniec", "PRZ/pzresunięcia.js", true);


        csMenu.bringToFront();
        csMenu.show();
        csMenu.hide();
*/





    }  // onCreate


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(zebraBroadcastReceiver != null) unregisterReceiver(zebraBroadcastReceiver);
    }


    protected void doAction() {
        //   if (!validateData()) return;
        RestApi restApi = RestClient.getApi();
        MainRequestData mainRequestData = new MainRequestData();

        Call<MainResponseData> callAction = restApi.doAction(mainRequestData);

        if(callAction == null) return;
        callAction.enqueue(new Callback<MainResponseData>() {
            @Override
            public void onResponse(Call<MainResponseData> call, Response<MainResponseData> response) {
                MainResponseData lr = response.body();

                if(lr == null) {
                    if(response.raw() != null)
                        displayAlert(getString(R.string.loginBladSerwera), response.raw().toString());
                    else
                        displayAlert(getString(R.string.loginBladSerwera), "Problemy z połączeniem.");
                    return;
                }

                processLayoutData(lr);
            }

            @Override
            public void onFailure(Call<MainResponseData> call, Throwable t) {
                callAction.cancel();
                if(t!=null && t.getCause() != null)
                    displayAlert(getString(R.string.loginBladPolaczenia), t.getCause().getMessage());
                else
                    displayAlert(getString(R.string.loginBladPolaczenia), "Serwer jest niedostępny.");
            }

            //
        }); // callLogin.enqueue

    }

    void processLayoutData(MainResponseData mrd) {
        List<MainResponseData.Action> actions;
        int seq;
        if(mrd==null) return;
        actions = mrd.getAction();
        actions.sort(new MainResponseData.ActionComparator());

        mrd.setPixelDensity(screenPixelDensity);

        for(MainResponseData.Action action : actions ) {
            seq = action.getSequence();

            if(action.getType().equals("action")) {
                processAction(action);
                continue;
            }

            if(action.getType().equals("browser")) {
                processBrowser(action, mrd);
                continue;
            }

            if(action.getType().equals("menu")) {
                processMenu(action, mrd);
                continue;
            }


            if(action.getType().equals("text")) {
                processActionText(action);
                continue;
            }

            if(action.getType().equals("edit")) {
                processActionEdit(action);
                continue;
            }


            System.out.print(seq);
        }



    }

    private void processBrowser(MainResponseData.Action action, MainResponseData mrd) {
        List<MainResponseData.Browser> columns;
        List<MainResponseData.Data> dataList;
        int seq;
        if(mrd==null) return;

        csBrowser.setId(action.getSequence());
        tabOrder.add( csBrowser );
        columns = mrd.getBrowser();

        if(columns.size() > 0) {
            csBrowser.clear();
            columns.sort(new MainResponseData.BrowserComparator());
            csBrowser.clear();
            for (MainResponseData.Browser column : columns) {
                csBrowser.addColumn(column.getLabel(), true, column.getWidth(), column.getAlign());
            }

            csBrowser.setYPositionAndHeight(action.getRowDP(), action.getHeightDP());
            csBrowser.prepareBrowser();
        }


        csBrowser.clearEntries();
        dataList = mrd.getData();

        if(dataList.size() > 0) {
            dataList.sort(new MainResponseData.DataComparator());
            for (MainResponseData.Data data : dataList) {
                csBrowser.addRow( data.getColumn1(), data.getColumn2(), data.getColumn3(), data.getColumn4(), data.getOnGo(), data.getOnvalueChanged());
            }
        }

        csBrowser.show();
    }

    private void processMenu(MainResponseData.Action action, MainResponseData mrd) {
        List<MainResponseData.Menu> menuList;
        int seq;
        if(mrd==null) return;

        csMenu.hide();
        csMenu.clear();
        csMenu.setTitle(action.getName());
        menuList = mrd.getMenu();

        if(menuList == null) return;

        if(menuList.size() > 0) {
            menuList.sort(new MainResponseData.MenuComparator());
            for (MainResponseData.Menu menuItem : menuList) {
                csMenu.addItem(menuItem.getLabel(), menuItem.getAction(), false);
            }
        }
        csMenu.show();
    }

    private void processActionText(MainResponseData.Action action) {

        CustomLabel csLabel;
        String name = action.getName();
        if(listOfViews.containsKey(name)) csLabel = (CustomLabel)listOfViews.get(name);
        else {
            csLabel = new CustomLabel(frameLayout.getContext(), action.getWidthDP(),action.getHeightDP(), action.getRowDP(), action.getColDP());
            listOfViews.put(action.getName(), csLabel);
            frameLayout.addView(csLabel);
        }
        csLabel.setText( action.getText() );
        csLabel.setBoldItalic(action.getBold(), false);

    }

    private void processActionEdit(MainResponseData.Action action) {
        CustomEdit csEdit;
        String name = action.getName();

        if(listOfViews.containsKey(name)) csEdit = (CustomEdit)listOfViews.get(name);
        else {
            csEdit = new CustomEdit(frameLayout.getContext(), action.getWidthDP(),action.getHeightDP(), action.getRowDP(), action.getColDP());
            if(!action.getOnGS1().equals("")) {
                wantsGS1.put(action.getOnGS1(), csEdit);
            }

            csEdit.setId(action.getSequence());
            tabOrder.add( csEdit );
            if(action.getSequence() > 0) csEdit.setNextFocusUpId(action.getSequence() - 1);
            csEdit.setNextFocusDownId(action.getSequence() + 1 );


            csEdit.setNextFocusUpId(2);
            csEdit.setNextFocusDownId(2);

            listOfViews.put(name, csEdit);
            frameLayout.addView(csEdit);

            csEdit.setOnKeyListener((v, keycode, event) -> {
                        int idx;
                        IView vw;
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keycode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    if( IView.class.isInstance(v)) return moveToPrevTabItem( (IView)v);
                                    return false;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    if( IView.class.isInstance(v)) return moveToNextTabItem( (IView)v);
                                    return false;
                                case KeyEvent.KEYCODE_ENTER:
                                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                                    onFrameGo();
                                    return true;
/*
*/
                            } // switch
                        } // key down
                        return false;
                    });
/*
            frameLayout.setOnKeyListener((v, keycode, event) -> {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keycode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    displayAlert("enter", "enter");
                                    return true;

                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    csEdit.setNext
                                    return true;
                            } // switch
                        } // key down
                        return false;
                    }
            );
*/


        }
        csEdit.setText( action.getText() );
        csEdit.setBoldItalic(action.getBold(), false);
    }


    private void processAction(MainResponseData.Action action) {
        if(action.getName().equals("clear")) {
            csBrowser.hide();
            csMenu.hide();

            for(String elem : listOfViews.keySet()) {
                frameLayout.removeView(listOfViews.get(elem));
            }

            listOfViews.clear();
            wantsGS1.clear();
            tabOrder.clear();


        }
    }
}
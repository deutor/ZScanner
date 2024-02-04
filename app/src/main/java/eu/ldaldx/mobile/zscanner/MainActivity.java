package eu.ldaldx.mobile.zscanner;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import eu.ldaldx.mobile.zscanner.browser.CustomBrowser;
import eu.ldaldx.mobile.zscanner.databinding.ActivityMainBinding;
import eu.ldaldx.mobile.zscanner.menu.CustomMenu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements IMainListener {

    private float screenPixelDensity;

    private String nextAction = "";
    private String backAction = "";
    FrameLayout frameLayout;

    private Toolbar mainToolbar;
    private ActivityMainBinding binding;
    private CustomMenu csMenu;
    private CustomBrowser csBrowser;

    private GS1_Decoder gs1Decoder = new GS1_Decoder();
    private String userID;
    private String sessionID;

    private final HashMap<String,View> listOfViews = new HashMap<>();
    private final HashMap<String,View> wantsGS1 = new HashMap<>();

    private final ArrayList<IView> tabOrder = new ArrayList<>();


    private final BroadcastReceiver zebraBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();

            if (action.equals(getResources().getString(R.string.zebra_activity_intent_filter_action))) {
                //
                //  Received a barcode scan
                //

                try {
                    //String decodedSource = intent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_source));
                    String decodedData = intent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_data));
                    //String decodedLabelType = intent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_label_type));

                    int numUsedGS1 = 0;
                    if(gs1Decoder.decode(decodedData) > 0) {
                        for (String key : wantsGS1.keySet()) {
                            if (gs1Decoder.containsKey(key)) {
                                View vw = wantsGS1.get(key);
                                if (vw instanceof CustomEdit) {
                                    CustomEdit ce = (CustomEdit) vw;
                                    ce.setText(gs1Decoder.getValueForAI(key));
                                    numUsedGS1++;
                                }
                            }
                        }
                    }

                    if(numUsedGS1 == 0) doSendDataToFocused(gs1Decoder.getRawData());
                    onFrameGo("scanner");

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

        displayAlert("main decodedSource", decodedSource + "\n" + decodedData + "\n" + decodedLabelType + " howDataReceived = " + howDataReceived);
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
    public void executeMenuAction(String action, String actionArgs) {
        if(action != null && action.equals("hide")) {
            actionHideMenu();
            return;
        }

        HashMap<String,String> lov = new HashMap<>();
        String lstAction = action + "&" + actionArgs;

        String[] lstElem = lstAction.split("&");
        String[] lstValue;
        for (int i = 0; i < lstElem.length; i++) {
            lstValue = lstElem[i].split("=");
            if (lstValue.length < 2) continue;
            lov.put(lstValue[0], lstValue[1]);
        }

        doAction(lov, "go", actionArgs + "&action=" + action);
    }

    // change fields upon value-changed event of browse
    @Override
    public void setControlsOnBrowseVC(String setString) {
        if((setString == null) || (setString.length() == 0)) return;
        String[] lstElem = setString.split("&");
        String[] lstValue;
        for(int i = 0; i< lstElem.length;i++) {
            lstValue = lstElem[i].split("=");

            if(lstElem[i].equals(lstValue[0])) continue;
            //if(lstValue.length < 2) continue;

            if(listOfViews.containsKey(lstValue[0])) {
                View vw = listOfViews.get(lstValue[0]);
                CustomEdit ce;
                CustomLabel cl;

                if(vw instanceof CustomEdit) {
                    ce = (CustomEdit)vw;
                    if(lstValue.length == 2) ce.setText(lstValue[1]);
                    else ce.setText("");
                }

                if(vw instanceof CustomLabel) {
                    cl = (CustomLabel)vw;

                    if(lstValue.length == 2) cl.setText(lstValue[1]);
                    else cl.setText("");
                }

            }
        }
    }

    @Override
    public void onBrowserEnterClicked() {
        onFrameGo("browser");
    }

    private void onFrameGo(String goSrc) {
        HashMap<String,String> lov = new HashMap<>();

        // collect data from browser
        if(csBrowser.getFocusedOnGo() != null) {
            String[] lstElem = csBrowser.getFocusedOnGo().split("&");
            String[] lstValue;
            for (int i = 0; i < lstElem.length; i++) {
                lstValue = lstElem[i].split("=");
                if (lstValue.length < 2) continue;
                lov.put(lstValue[0], lstValue[1]);
            }
        }

        lov.put("goSrc", goSrc);

        // collect data from edits
        for(String key: listOfViews.keySet()) {
            View vw = listOfViews.get(key);
            String value = "";

            if(vw instanceof CustomEdit) {
                CustomEdit ce = (CustomEdit)vw;
                value = Objects.requireNonNull(ce.getText()).toString();
            }

            if(vw instanceof CustomLabel) {
                CustomLabel cl = (CustomLabel)vw;
                if(!cl.isReported()) continue;

                value = cl.getText().toString();
            }

            // overwrite browser values only if Edit/Label value is not empty
            if(lov.containsKey(key)) {
                if (value.length() > 0) lov.put(key, value);
            } else {
                lov.put(key, value);
            }
        }

        doAction(lov, "go", null);
    }

    private void doSendDataToFocused(String rawData) {
        for(IView vw : tabOrder) {
            if(vw instanceof CustomEdit) {
                CustomEdit ce = (CustomEdit) vw;
                if(ce.isFocused()) ce.setText(rawData);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            if(csMenu.isVisible()) csMenu.setFocused();
            else{
                if(tabOrder.size() > 0) tabOrder.get(0).setFocused();
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

        binding.buttonForward.setOnClickListener(v -> onFrameGo("goBtn"));
        binding.buttonForward.requestFocus();


        doAction(null, "", null);



        binding.button33.setVisibility(View.GONE);

        binding.button33.setOnClickListener(v -> {
            //send broadcast
            Intent localIntent = new Intent(getString(R.string.zebra_activity_intent_filter_action));


            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_source), "scanner");
            localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_label_type), "typ_etykiety");
            //localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_data), GS1_Decoder.ctrlFNC1 + "91PROD" + GS1_Decoder.ctrlGS + "92192.168.43.1" + GS1_Decoder.ctrlGS + "9344044"+ GS1_Decoder.ctrlGS);
            localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_data), GS1_Decoder.ctrlFNC1 + "94TOKEN-UZYTKOWNIKA" + GS1_Decoder.ctrlGS);


/*            sendBroadcast(localIntent);*/

            doAction(null, "", null);




            });



        screenPixelDensity = getApplicationContext().getResources().getDisplayMetrics().density;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;

        frameLayout = findViewById(R.id.mainLayout);
        mainToolbar = binding.mainToolbar;

        csMenu = new CustomMenu(frameLayout, frameLayout.getContext(), this);
        csMenu.hide();

        csBrowser = new CustomBrowser(frameLayout, frameLayout.getContext(), this, width);
        csBrowser.hide();


        frameLayout.setOnKeyListener((v, keycode, event) -> {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keycode) {
                            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                displayAlert("enter", "enter");
                                return true;
                        } // switch
                    } // key down
                    return false;
                }
        );


        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.zebra_activity_intent_filter_action));
        registerReceiver(zebraBroadcastReceiver, filter);


        /* 320x420 */

    }  // onCreate

    DialogInterface.OnClickListener dialogQuitClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    DialogInterface.OnClickListener dialogConfirmClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    onFrameGo("confirm");
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    //No button clicked
                    break;
            }
        }
    };


    public void actionHideMenu() {
        csMenu.hide();
        binding.buttonForward.show();

        if(tabOrder.size() > 0) tabOrder.get(0).setFocused();
        else binding.buttonForward.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if(csMenu.isVisible()) {
            if(csMenu.getBackAction() != null && csMenu.getBackAction().equals("hide")) {
                actionHideMenu();
                return;
            }
        }

        if(backAction.equals("quit") || backAction.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Czy chcesz zakończyć pracę?").setPositiveButton("Tak", dialogQuitClickListener).setNegativeButton("Nie", dialogQuitClickListener).show();
            return;
        }

        if(backAction.length() > 0 && !backAction.equals("block") && !backAction.equals("ignore")) {
            doAction(null, "back", null);
            return;
        }

        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(zebraBroadcastReceiver != null) unregisterReceiver(zebraBroadcastReceiver);
    }


    protected void doAction(HashMap<String,String> lov, String actionName, String actionArgs) {
        //   if (!validateData()) return;
        RestApi restApi = RestClient.getApi();
        MainRequestData mainRequestData = new MainRequestData();

        mainRequestData.setSessionID(sessionID);
        mainRequestData.setUserID(userID);
        mainRequestData.setRequest(actionName);
        mainRequestData.setActionArgs(actionArgs);
        mainRequestData.setDataFromLov(lov);


        Call<MainResponseData> callAction = restApi.doAction(mainRequestData);

        if(callAction == null) return;
        callAction.enqueue(new Callback<MainResponseData>() {
            @Override
            public void onResponse(@NonNull Call<MainResponseData> call, @NonNull Response<MainResponseData> response) {
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
                processAction(action, mrd);
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
        }

        if(csMenu.isVisible()) {
            csMenu.setFocused();
        } else {
            if(tabOrder.size() > 0) tabOrder.get(0).setFocused();
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
        csBrowser.bringToFront();
    }

    private void processMenu(MainResponseData.Action action, MainResponseData mrd) {
        List<MainResponseData.Menu> menuList;
        int seq;
        if(mrd==null) return;

        binding.buttonForward.hide();
        csMenu.hide();
        csMenu.clear();

        if(action.getText()!= null) {
            csMenu.setTitle(action.getText());
        } else {
            csMenu.setTitle("");
        }

        csMenu.setBackAction( action.getBackAction() );


        menuList = mrd.getMenu();

        if(menuList == null) return;

        if(menuList.size() > 0) {
            menuList.sort(new MainResponseData.MenuComparator());
            for (MainResponseData.Menu menuItem : menuList) {
                csMenu.addItem(menuItem.getLabel(), menuItem.getAction(), menuItem.getActionArgs());
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

        csLabel.setReported( action.isReported() ) ;
        csLabel.setText( action.getText() );
        csLabel.setBoldItalic(action.getBold(), false);

        if(action.getColor().equals("red")) {
            csLabel.setTextColor(Color.RED);
        }

        if(action.getColor().equals("yellow")) {
            csLabel.setTextColor(Color.YELLOW);
        }

        if(action.getColor().equals("yellow/black")) {
            csLabel.setBackgroundColor(Color.YELLOW);
            csLabel.setTextColor(Color.BLACK);
        }



        if(action.getAlign() != null) {
                if(action.getAlign().equals("right")) csLabel.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                if(action.getAlign().equals("left_middle")) {
                        csLabel.setEllipsize(TextUtils.TruncateAt.MIDDLE);

                }
        }


//        textViewColumn.setGravity(View.TEXT_ALIGNMENT_VIEW_END);
//        textViewColumn.setEllipsize(TextUtils.TruncateAt.START);



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

            if(action.getSubtype().equals("date")) {
                csEdit.setInputType(InputType.TYPE_CLASS_DATETIME );

                // limit entry type to 10 characters
                InputFilter[] editFilters = csEdit.getFilters();
                InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
                System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                newFilters[editFilters.length] = new InputFilter.LengthFilter(10);
                csEdit.setFilters(newFilters);
                csEdit.addTextChangedListener(new DateTextWatcher());
            }

            if(action.getSubtype().equals("number")) {
                csEdit.setInputType(InputType.TYPE_CLASS_NUMBER );
            }

            if(action.getSubtype().equals("number3")) {
                csEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }

            csEdit.setId(action.getSequence());
            tabOrder.add( csEdit );
            if(action.getSequence() > 0) csEdit.setNextFocusUpId(action.getSequence() - 1);
            csEdit.setNextFocusDownId(action.getSequence() + 1 );

            listOfViews.put(name, csEdit);
            frameLayout.addView(csEdit);

            csEdit.setOnKeyListener((v, keycode, event) -> {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keycode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    if(v instanceof IView) return moveToPrevTabItem( (IView)v);
                                    return false;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    if(v instanceof IView) return moveToNextTabItem( (IView)v);
                                    return false;
                                case KeyEvent.KEYCODE_ENTER:
                                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                                    onFrameGo(name);
                                    return true;
                            } // switch
                        } // key down
                        return false;
                    });



        }
        csEdit.setText( action.getText() );
        csEdit.setBoldItalic(action.getBold(), false);
    }


    private void processAction(MainResponseData.Action action, MainResponseData mrd) {
        if(action.getName().equals("confirm")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(action.getText()).setPositiveButton("1 - Tak", dialogConfirmClickListener).setNegativeButton("0 - Nie", dialogConfirmClickListener);

            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_0) {
                            dialogInterface.dismiss();
                            return true;
                        }

                        if (keyCode == KeyEvent.KEYCODE_1) {
                            dialogInterface.dismiss();
                            onFrameGo("confirm");
                            return true;
                        }
                    }
                    return false;
                }
            });

            builder.show();
        }



        if(action.getName().equals("nextAction")) {
            nextAction = action.getText();
        }

        if(action.getName().equals("backAction")) {
            backAction = action.getText();
        }

        if(action.getName().equals("path")) {
            mainToolbar.setSubtitle(action.getText());
        }


        if(action.getName().equals("information")) {
            displayAlert("Informacja", action.getText());
        }

        if(action.getName().equals("warning")) {
            displayAlert("Ostrzeżenie", action.getText());
        }

        if(action.getName().equals("error")) {
            displayAlert("Błąd", action.getText());
        }

        if(action.getName().equals("clearBrowse")) {
            csBrowser.clearEntries();
        }

        if(action.getName().equals("refreshBrowse")) {
            List<MainResponseData.Data> dataList;

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


        if(action.getName().equals("clear")) {
            csBrowser.hide();
            csMenu.hide();
            binding.buttonForward.show();
            for(String elem : listOfViews.keySet()) {
                frameLayout.removeView(listOfViews.get(elem));
            }

            mainToolbar.setTitle(action.getText());
            mainToolbar.setSubtitle("");
            listOfViews.clear();
            wantsGS1.clear();
            tabOrder.clear();
            nextAction = "";
            backAction = "back";


        }
    }
}
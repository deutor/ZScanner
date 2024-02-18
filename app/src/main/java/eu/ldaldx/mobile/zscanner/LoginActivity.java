package eu.ldaldx.mobile.zscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;

import eu.ldaldx.mobile.zscanner.databinding.ActivityLoginBinding;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editPassword;
    private EditText editUser;
    private TextView txtServer;
    private TextInputLayout txtServer_title;

    private final GS1_Decoder gs1Decoder = new GS1_Decoder();
    private String server_address;
    private String server_name;
    private String server_port;


    private class ZebraBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals( getString(R.string.zebra_activity_intent_filter_action))) {
                //
                //  Received a barcode scan
                //
                try {
                    //String decodedSource = intent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_source));
                    String decodedData = intent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_data));
                    //String decodedLabelType = intent.getStringExtra(getResources().getString(R.string.zebra_datawedge_intent_key_label_type));

                    if(gs1Decoder.decode(decodedData) > 0) {

                        //displayAlert("gs1", gs1Decoder.getAIs());

                        if(gs1Decoder.containsKey("91")) setServerFromGS1Code();
                        else
                        {
                            if(gs1Decoder.containsKey("94")) doLogin("", "", gs1Decoder.getValueForAI("94"));
                            else doSendDataToFocused(gs1Decoder.getRawData());
                        }
                    } else doSendDataToFocused(gs1Decoder.getRawData());
                } catch (Exception e) {

                    //
                    // Catch if the UI does not exist when broadcast is received
                    //
                }
            }
        }
    }

    ZebraBroadcastReceiver zebraBroadcastReceiver;

    private void doLogin(String login, String password, String userToken) {

        if( server_name == null || server_name.length() == 0) {
            displayAlert(getString(R.string.alert_title_error), getString(R.string.login_define_app_server));
            return;
        }

        if( userToken.length() == 0 ) {
            if (login.length() == 0) {
                displayAlert(getString(R.string.alert_title_error), getString(R.string.login_user_name_cannot_be_empty));
                return;
            }

            if (password.length() == 0) {
                displayAlert(getString(R.string.alert_title_error), getString(R.string.login_password_cannot_be_empty));
                return;
            }
        }

        String password_sha = get_SHA_256_SecurePassword(password, login);

        //   if (!validateData()) return;
        RestClient rs = new RestClient();

        rs.getClient(server_address, server_port);
        RestApi api = rs.getApi();

        LoginRequestData loginRequestData = new LoginRequestData();
        loginRequestData.setUserName(login);
        loginRequestData.setPassword(password_sha);
        loginRequestData.setUserToken(userToken);
        loginRequestData.setAction("doLogin");
        loginRequestData.setVersion(1);

        Call<LoginResponseData> callLogin = api.doLogin(loginRequestData);

        callLogin.enqueue(new Callback<LoginResponseData>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseData> call, @NonNull Response<LoginResponseData> response) {
                LoginResponseData lr = response.body();

                if(lr == null) {
                    if(!response.raw().toString().isEmpty()) {
                        displayAlert(getString(R.string.login_server_error), response.raw().toString());
                    } else {
                        displayAlert(getString(R.string.login_server_error), getString(R.string.login_connection_problem));
                    }
                    return;
                }

                if (lr.getValid()) {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    main.putExtra("userID", lr.getUserID());
                    main.putExtra("sessionID", lr.getSessionID());
                    startActivity(main);

                } else {
                    displayAlert(getString(R.string.login_logon_error), lr.getMessage());
                }
                //noinspection UnnecessaryReturnStatement
                return;

            } // callLogin.enqueue

            @Override
            public void onFailure(@NonNull Call<LoginResponseData> call, @NonNull Throwable t) {
                call.cancel();
                if(t.getCause() != null)
                    displayAlert(getString(R.string.login_connection_error), t.getCause().getMessage());
                else
                    displayAlert(getString(R.string.login_connection_error), getString(R.string.login_server_is_unavailable));
            }
        });
    }

    private void doSendDataToFocused(String rawData) {
        if(editUser.hasFocus()) editUser.setText(rawData);
        else if(editPassword.hasFocus()) editPassword.setText(rawData);
    }

    private void setServerFromGS1Code() {
        String serverName = gs1Decoder.getValueForAI("91");
        String serverAddress = gs1Decoder.getValueForAI("92");
        String serverPort = gs1Decoder.getValueForAI("93");


        if(serverName.length() > 0 && serverAddress.length() > 0 && serverPort.length() > 0) {
            try {
                Integer.parseInt(serverPort);
            } catch (NumberFormatException ex) {
                displayAlert(getString(R.string.login_incorrect_server_label), getString(R.string.login_scanned_incorrect_code) + "\n" +
                        getString(R.string.login_incorrect_port) + serverPort);
                return;
            }

            SharedPreferences preferences = getSharedPreferences( getString(R.string.zscanner_profile_key), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("server_name", serverName);
            editor.putString("server_address", serverAddress);
            editor.putString("server_port", serverPort);

            editor.apply();

            server_name = serverName;
            server_address = serverAddress;
            server_port = serverPort;
            txtServer.setText(server_name);
            txtServer_title.setHint( getResources().getString(R.string.login_txtServer_hint) + " " + server_address + ":" + server_port);
        }
        else
        {
            displayAlert(getString(R.string.login_incorrect_server_label), getString(R.string.login_scanned_incorrect_code) + "\n" +
                                getString(R.string.login_server_name) + ": " + serverName + "\n" +
                                getString(R.string.login_server_address) + ": " + serverAddress + "\n" +
                                getString(R.string.login_server_port) + ": " + serverPort);
        }

    }

    protected void displayAlert(String title, String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    protected void createDataWedgeProfile(Context context) {
        Zebra_DWInterface.createDataWedgeProfile(context);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(zebraBroadcastReceiver != null) unregisterReceiver(zebraBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(zebraBroadcastReceiver == null) zebraBroadcastReceiver = new ZebraBroadcastReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.zebra_activity_intent_filter_action));
        registerReceiver(zebraBroadcastReceiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        eu.ldaldx.mobile.zscanner.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editPassword = findViewById(R.id.editPassword);
        editUser = findViewById(R.id.editUser);
        txtServer = findViewById(R.id.txtServer);
        txtServer_title = findViewById(R.id.txtServer_title);

        SharedPreferences preferences = getSharedPreferences( getString(R.string.zscanner_profile_key), MODE_PRIVATE);
        if(preferences.contains(getString(R.string.zscanner_preference_server_name))) {
            try {
                server_name = preferences.getString(getString(R.string.zscanner_preference_server_name), "");
                server_address = preferences.getString(getString(R.string.zscanner_preference_server_adress), "");
                server_port = preferences.getString(getString(R.string.zscanner_preference_server_port), "");

                txtServer.setText( server_name);
                txtServer_title.setHint( getResources().getString(R.string.login_txtServer_hint) + " " + server_address + ":" + server_port);
            } catch (Exception ex) {
                server_name = "";
                server_address = "";
                server_port = "";
            }


        } else {
            txtServer.setText(R.string.login_click_and_set_server);
        }

        txtServer.setTypeface( null, Typeface.BOLD);

        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 1);

        createDataWedgeProfile(binding.getRoot().getContext());


        // emulate scanning of user's label
        binding.button.setOnClickListener(v -> {
            //send broadcast
            Intent localIntent = new Intent(getString(R.string.zebra_activity_intent_filter_action));


            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_source), "scanner");
            //localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_label_type), "type_of_label");
            //localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_data), GS1_Decoder.ctrlFNC1 + "91PROD" + GS1_Decoder.ctrlGS + "9210.0.0.11" + GS1_Decoder.ctrlGS + "9344044"+ GS1_Decoder.ctrlGS);
            // localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_data), GS1_Decoder.ctrlFNC1 + "91PROD" + GS1_Decoder.ctrlGS + "92192.168.43.1" + GS1_Decoder.ctrlGS + "9344044"+ GS1_Decoder.ctrlGS);
            localIntent.putExtra(getString(R.string.zebra_datawedge_intent_key_data), GS1_Decoder.ctrlFNC1 + "94QRCODE_FOR_USER_1" + GS1_Decoder.ctrlGS);

            sendBroadcast(localIntent);
        });



        binding.loginButton.setOnClickListener(v -> {
            doLogin(editUser.getText().toString(), editPassword.getText().toString(), "");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (zebraBroadcastReceiver != null) unregisterReceiver(zebraBroadcastReceiver);
        } catch (Exception ex) {
            // do nothing - application is destroyed anyway
        }
    }

    private static String get_SHA_256_SecurePassword(@NonNull String passwordToHash,
                                                     @NonNull String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

}
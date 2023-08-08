package eu.ldaldx.mobile.zscanner;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import retrofit2.Call;

import eu.ldaldx.mobile.zscanner.databinding.ActivityLoginBinding;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private EditText editPassword;
    private EditText editUser;
    REST_APIInterface RESTApiInterface;

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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editPassword = (EditText) findViewById(R.id.editPassword);
        editPassword.setRawInputType(InputType.TYPE_NULL);
        editPassword.setFocusable(true);
        editPassword.setCursorVisible(true);

        editUser = (EditText) findViewById(R.id.editUser);
        //editUser.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editUser.setShowSoftInputOnFocus(false);
        //editUser.setFocusable(true);
        //editUser.setCursorVisible(true);
        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 1);

        createDataWedgeProfile(binding.getRoot().getContext());



        RESTApiInterface = REST_APIClient.getClient().create(REST_APIInterface.class);

        /* ======================== */
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
        /* ======================== */

        binding.loginButton.setOnClickListener(v -> {
         //   if (!validateData()) return;

            LoginRequestData loginRequestData = new LoginRequestData();



            loginRequestData.setUser("1");
            loginRequestData.setPassword("1");
            loginRequestData.setAction("doLogin");
            loginRequestData.setVersion(1);

            Call<LoginResponseData> callLogin = RESTApiInterface.doLogin(loginRequestData);

            callLogin.enqueue(new Callback<LoginResponseData>() {
                @Override
                public void onResponse(Call<LoginResponseData> call, Response<LoginResponseData> response) {
                    LoginResponseData lr = response.body();

                    if(lr == null) {
                        displayAlert(getString(R.string.loginBladSerwera), response.raw().toString());
                        return;
                    }


                    if (lr.getValid()) {
                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                        main.putExtra("session", lr.getSessionID());
                        startActivity(main);
                        return;
                    } else {
                        displayAlert("Błąd logowania", lr.getMessage());
                        return;
                    }
                } // callLogin.enqueue

                @Override
                public void onFailure(Call<LoginResponseData> call, Throwable t) {
                    call.cancel();
                    displayAlert(getString(R.string.loginBladPolaczenia), t.getCause().getMessage());
                }
            });


        });
    }




    private boolean validateData() {

        if( editPassword.length() == 0) {
            displayAlert("Błąd", "Hasło nie może być puste");
        }
         return true;
    }
}
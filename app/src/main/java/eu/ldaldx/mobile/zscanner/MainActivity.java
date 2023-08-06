package eu.ldaldx.mobile.zscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import eu.ldaldx.mobile.zscanner.databinding.ActivityLoginBinding;
import eu.ldaldx.mobile.zscanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements IMainListener {
    private ActivityMainBinding binding;
    private CustomMenu csMenu;

    public void displayAlert(String title, String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FrameLayout layout = (FrameLayout) findViewById(R.id.mainLayout);




        csMenu = new CustomMenu(layout, layout.getContext(), "Menu główne", this);



/*
        CustomMenu csmenu = new CustomMenu(layout, this, "Menu główne");
        csmenu.AddItem("1 - label");
        csmenu.AddItem("2 - label");
        csmenu.AddItem("3 - label");
        csmenu.AddItem("4 - label");
        csmenu.AddItem("5 - label");
        csmenu.AddItem("6 - label");
        csmenu.AddItem("7 - label");
        csmenu.AddItem("8 - label");
        csmenu.AddItem("9 - label");
*/





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




    }
}
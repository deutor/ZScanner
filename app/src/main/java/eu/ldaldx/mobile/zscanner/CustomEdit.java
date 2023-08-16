package eu.ldaldx.mobile.zscanner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatEditText;

public class CustomEdit extends AppCompatEditText implements IView {

    private Drawable background;
    private Drawable backgroundSelected;
    public CustomEdit(Context context) {
        super(context);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        setSingleLine();
        setPadding(2 ,1 ,2 ,1);
        setHighlightColor(Color.blue(255));

        setLongClickable(false);
        background = getResources().getDrawable(R.drawable.customedit_border, null);
        backgroundSelected= getResources().getDrawable(R.drawable.customedit_selected, null);
        setBackground(background);
    }
    public CustomEdit(Context context, int width, int height, int row, int col) {
        this(context);

        FrameLayout.LayoutParams frlp = new FrameLayout.LayoutParams( width, height);
        frlp.topMargin = row;
        frlp.leftMargin = col;
        setHint("");
        setLayoutParams(frlp);

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    setBackground(backgroundSelected);

                } else {
                    setBackground(background);
                }
            }
        });

    }

    public void setFocused() {
            super.requestFocus();
            super.requestFocusFromTouch();
    }


    public void setBoldItalic(boolean bold, boolean italic) {
        if(bold & italic) setTypeface(null, Typeface.BOLD_ITALIC);
        else {
            if(bold) setTypeface(null, Typeface.BOLD);
            else {
                if(italic) setTypeface(null, Typeface.ITALIC);
            } //not bold
        } // not bold&italic
    }

}

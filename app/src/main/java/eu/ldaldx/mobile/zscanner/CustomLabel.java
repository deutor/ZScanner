package eu.ldaldx.mobile.zscanner;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomLabel extends AppCompatTextView implements IView{
    public CustomLabel(Context context) {
        super(context);
        setClickable(false);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
    }


    public void setFocused() {

        super.requestFocus();
        super.requestFocusFromTouch();
    }


    public CustomLabel(Context context, int width, int height, int row, int col) {
        this(context);

        FrameLayout.LayoutParams frlp = new FrameLayout.LayoutParams( width, height);
        frlp.topMargin = row;
        frlp.leftMargin = col;
        setLayoutParams(frlp);
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

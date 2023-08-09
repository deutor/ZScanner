package eu.ldaldx.mobile.zscanner;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomLabel extends AppCompatTextView {


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

    public CustomLabel(Context context) {
        super(context);
        setClickable(false);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
    }
    public CustomLabel(Context context, int width, int height, int row, int col) {
        this(context);

        FrameLayout.LayoutParams frlp = new FrameLayout.LayoutParams( convertPixelsToDp(context, width), convertPixelsToDp(context, height));
        frlp.topMargin = convertPixelsToDp(context, row);
        frlp.leftMargin = convertPixelsToDp(context, col);
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

    private int convertPixelsToDp(Context context, int pixels) {
        float screenPixelDensity = context.getResources().getDisplayMetrics().density;
        float dpValue = pixels * screenPixelDensity;
        return Math.round(dpValue);
    }


}

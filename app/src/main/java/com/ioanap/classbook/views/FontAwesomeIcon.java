package com.ioanap.classbook.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by ioana on 3/15/2018.
 */

public class FontAwesomeIcon extends android.support.v7.widget.AppCompatTextView {

    public FontAwesomeIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontAwesomeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontAwesomeIcon(Context context) {
        super(context);
        init();
    }

    private void init() {
        // Font name should not contain "/".
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fa-regular-400.ttf");
        setTypeface(tf);
    }

}

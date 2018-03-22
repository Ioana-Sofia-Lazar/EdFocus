package com.ioanap.classbook.views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by ioana on 3/15/2018.
 */

public class CustomIcon extends AppCompatImageView {

    private Context mContext;

    public CustomIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public CustomIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public CustomIcon(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
    }

}

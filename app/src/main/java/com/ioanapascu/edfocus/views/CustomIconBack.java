package com.ioanapascu.edfocus.views;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.View;

import com.ioanapascu.edfocus.R;

/**
 * Created by Ioana Pascu on 3/15/2018.
 */

public class CustomIconBack extends android.support.v7.widget.AppCompatImageView
        implements View.OnClickListener {

    private Context mContext;

    public CustomIconBack(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setImageResource(R.drawable.ic_back);
        super.setOnClickListener(this);
        mContext = context;
        init();
    }

    public CustomIconBack(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setImageResource(R.drawable.ic_back);
        super.setOnClickListener(this);
        mContext = context;
        init();
    }

    public CustomIconBack(Context context) {
        super(context);
        super.setImageResource(R.drawable.ic_back);
        super.setOnClickListener(this);
        mContext = context;
        init();
    }

    private static Activity scanForActivity(Context mContext) {
        if (mContext == null)
            return null;
        else if (mContext instanceof Activity)
            return (Activity) mContext;
        else if (mContext instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) mContext).getBaseContext());

        return null;
    }

    private void init() {
    }

    @Override
    public void onClick(View v) {
        scanForActivity(mContext).finish();
    }
}

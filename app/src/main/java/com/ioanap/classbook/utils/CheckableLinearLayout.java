package com.ioanap.classbook.utils;

import android.content.Context;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by ioana on 3/14/2018.
 */

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    public CheckableLinearLayout(Context context) {
        super(context);
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public void toggle() {

    }
}

package com.ioanapascu.edfocus.teacher;

import android.os.Bundle;

import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;

public class FilesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this, false);
        setContentView(R.layout.activity_files);
    }

}

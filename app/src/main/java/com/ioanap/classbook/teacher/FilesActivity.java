package com.ioanap.classbook.teacher;

import android.os.Bundle;

import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;

public class FilesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this, false);
        setContentView(R.layout.activity_files);
    }

}

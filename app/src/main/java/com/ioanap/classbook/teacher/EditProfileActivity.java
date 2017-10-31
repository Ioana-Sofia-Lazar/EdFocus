package com.ioanap.classbook.teacher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.ioanap.classbook.R;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    private ImageView mCancelImageView, mSaveImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mCancelImageView = (ImageView) findViewById(R.id.image_cancel);
        mSaveImageView = (ImageView) findViewById(R.id.image_save);

        // on click listeners for toolbar buttons
        mCancelImageView.setOnClickListener(this);
        mSaveImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mCancelImageView) {
            finish();
        } else if (view == mSaveImageView) {
            // save information
            // TODO

            finish();
        }
    }
}

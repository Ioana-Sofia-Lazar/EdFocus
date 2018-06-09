package com.ioanapascu.edfocus.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.shared.CoursesActivity;

/**
 * Created by Ioana Pascu on 4/1/2018.
 */

public class NoCoursesDialog extends Dialog {

    // widgets
    private Button mManageCoursesBtn;
    private ImageView mCancelImg;
    private TextView mMessageText;

    // variables
    private Activity mActivity;
    private Dialog mDialog;
    private String mClassId;
    private int mTextResource;

    public NoCoursesDialog(Activity activity, String classId, int textResource) {
        super(activity);
        this.mActivity = activity;
        this.mClassId = classId;
        this.mTextResource = textResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_no_courses);

        mManageCoursesBtn = findViewById(R.id.btn_manage_courses);
        mCancelImg = findViewById(R.id.img_cancel);
        mMessageText = findViewById(R.id.text_message);

        mMessageText.setText(mTextResource);

        // redirect to courses page
        mManageCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mActivity.finish();
                dismiss();
                Intent myIntent = new Intent(getContext(), CoursesActivity.class);
                myIntent.putExtra("classId", mClassId);
                getContext().startActivity(myIntent);
            }
        });

        // x button click (cancel)
        mCancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}


package com.ioanap.classbook.teacher;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ioanap.classbook.R;

public class ClassTokenActivity extends AppCompatActivity implements View.OnClickListener {

    // widgets
    private TextView mTokenEditText;
    private ImageView mCopyIcon, mBackButton;
    private Button mManageBtn;

    private String mToken, mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_token);

        // widgets
        mTokenEditText = (TextView) findViewById(R.id.txt_token);
        mCopyIcon = (ImageView) findViewById(R.id.img_copy_icon);
        mManageBtn = (Button) findViewById(R.id.btn_manage);
        mBackButton = (ImageView) findViewById(R.id.img_back);

        // listeners
        mCopyIcon.setOnClickListener(this);
        mManageBtn.setOnClickListener(this);
        mBackButton.setOnClickListener(this);

        // get token and classId
        Intent myIntent = getIntent();
        mToken = myIntent.getStringExtra("token");
        mClassId = myIntent.getStringExtra("classId");

        // display token
        mTokenEditText.setText(mToken);
    }

    @Override
    public void onClick(View view) {
        if (view == mCopyIcon) {
            // copy token to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("classToken", mToken);
            clipboard.setPrimaryClip(clip);

            // show message toast
            Toast.makeText(ClassTokenActivity.this, "Token copied to Clipboard!",
                    Toast.LENGTH_LONG).show();
        }
        if (view == mManageBtn) {
            finish();
            // redirect to class activity
            Intent myIntent = new Intent(getApplicationContext(), ClassActivity.class);
            myIntent.putExtra("classId", mClassId);
            getApplicationContext().startActivity(myIntent);
        }
        if (view == mBackButton) {
            finish();
        }
    }
}

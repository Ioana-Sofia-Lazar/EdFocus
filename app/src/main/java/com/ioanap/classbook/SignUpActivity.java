package com.ioanap.classbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ioanap.classbook.model.User;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    private Context mContext;
    private Button mSignUpButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private TextView mSwitchToSignInTextView;

    private String mSelectedUserType;

    private void signUp() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();

        // check if all required fields are filled in
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            // empty e-mail or password
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // register user
        User user = new User(email, mSelectedUserType);
        registerNewUser(user, email, password);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mContext = SignUpActivity.this;

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_user_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_teacher:
                        mSelectedUserType = "teacher";
                        break;
                    case R.id.radio_button_parent:
                        mSelectedUserType = "parent";
                        break;
                    case R.id.radio_button_child:
                        mSelectedUserType = "child";
                        break;
                }
            }
        });

        mSignUpButton = (Button) findViewById(R.id.button_sign_up);
        mEmailEditText = (EditText) findViewById(R.id.edit_text_email);
        mPasswordEditText = (EditText) findViewById(R.id.edit_text_password);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.edit_text_confirm_password);
        mSwitchToSignInTextView = (TextView) findViewById(R.id.text_switch_to_sign_in);

        mSignUpButton.setOnClickListener(this);
        mSwitchToSignInTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mSignUpButton) {
            signUp();
        }
        if (view == mSwitchToSignInTextView) {
            // jump to Login activity
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

}

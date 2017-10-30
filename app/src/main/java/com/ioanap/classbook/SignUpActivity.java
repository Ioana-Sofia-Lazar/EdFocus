package com.ioanap.classbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ioanap.classbook.model.User;
import com.ioanap.classbook.utils.FirebaseUtils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    public static Activity signUpActivity;

    private Context mContext;
    private Button mSignUpButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private TextView mSwitchToSignInTextView;

    private FirebaseUtils firebaseUtils;

    private String selectedUserType;

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
        User user = new User(selectedUserType);
        firebaseUtils.registerNewUser(user, email, password);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mContext = SignUpActivity.this;
        firebaseUtils = new FirebaseUtils(mContext);
        signUpActivity = this;

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.userTypeRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.teacherRadioButton:
                        selectedUserType = "teacher";
                        break;
                    case R.id.parentRadioButton:
                        selectedUserType = "parent";
                        break;
                    case R.id.childRadioButton:
                        selectedUserType = "child";
                        break;
                }
            }
        });

        mSignUpButton = (Button) findViewById(R.id.signUpButton);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        mSwitchToSignInTextView = (TextView) findViewById(R.id.switchToSignInTextView);

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

    public static SignUpActivity getInstance() {
        return SignUpActivity.getInstance();
    }
}

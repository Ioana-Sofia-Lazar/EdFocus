package com.ioanap.classbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ioanap.classbook.model.User;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";
    private final float ALPHA = (float) 0.2; // opacity of user types that are not selected
    // choose user type included layout
    View mChooseUserTypeView;
    LinearLayout mChooseTeacherLayout, mChooseStudentLayout, mChooseParentLayout;
    ImageView mChooseTeacherImg, mChooseStudentImg, mChooseParentImg;
    TextView mChooseTeacherText, mChooseStudentText, mChooseParentText;
    // widgets
    private Context mContext;
    private Button mSignUpButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private TextView mSwitchToSignInTextView;
    private String mSelectedUserType;
    private ImageView mSeePasswordImg, mSeeConfirmPasswordImg;

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

        setupWidgets();

        // default user type selected is teacher
        mSelectedUserType = "teacher";
        setAlphasUserTypes((float) 1, ALPHA, ALPHA);

    }

    private void setupWidgets() {
        // widgets
        mSignUpButton = findViewById(R.id.button_sign_up);
        mEmailEditText = findViewById(R.id.edit_text_email);
        mPasswordEditText = findViewById(R.id.edit_text_password);
        mConfirmPasswordEditText = findViewById(R.id.edit_text_confirm_password);
        mSwitchToSignInTextView = findViewById(R.id.text_switch_to_sign_in);
        mSeePasswordImg = findViewById(R.id.img_see_password);
        mSeeConfirmPasswordImg = findViewById(R.id.img_see_confirm_password);

        // included layout
        mChooseUserTypeView = findViewById(R.id.choose_user_type);

        mChooseTeacherLayout = mChooseUserTypeView.findViewById(R.id.linear_layout_choose_teacher);
        mChooseTeacherImg = mChooseUserTypeView.findViewById(R.id.img_choose_teacher);
        mChooseTeacherText = mChooseUserTypeView.findViewById(R.id.text_choose_teacher);

        mChooseStudentLayout = mChooseUserTypeView.findViewById(R.id.linear_layout_choose_pupil);
        mChooseStudentImg = mChooseUserTypeView.findViewById(R.id.img_choose_pupil);
        mChooseStudentText = mChooseUserTypeView.findViewById(R.id.text_choose_pupil);

        mChooseParentLayout = mChooseUserTypeView.findViewById(R.id.linear_layout_choose_parent);
        mChooseParentImg = mChooseUserTypeView.findViewById(R.id.img_choose_parent);
        mChooseParentText = mChooseUserTypeView.findViewById(R.id.text_choose_parent);

        // listeners
        mSignUpButton.setOnClickListener(this);
        mSwitchToSignInTextView.setOnClickListener(this);
        mChooseTeacherLayout.setOnClickListener(this);
        mChooseStudentLayout.setOnClickListener(this);
        mChooseParentLayout.setOnClickListener(this);
        mSeePasswordImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
        mSeeConfirmPasswordImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mConfirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        mConfirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mSignUpButton) {
            signUp();
        }
        if (view == mSwitchToSignInTextView) {
            // jump to Login activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        if (view == mChooseTeacherLayout) {
            // user chose Teacher type
            setAlphasUserTypes((float) 1, ALPHA, ALPHA);
            mSelectedUserType = "teacher";
        }
        if (view == mChooseStudentLayout) {
            // user chose Student type
            setAlphasUserTypes(ALPHA, (float) 1, ALPHA);
            mSelectedUserType = "student";
        }
        if (view == mChooseParentLayout) {
            // user chose Parent type
            setAlphasUserTypes(ALPHA, ALPHA, (float) 1);
            mSelectedUserType = "parent";
        }

    }

    /**
     * Sets alphas for user type images and text. (If we select one of the 3 options, the other 2
     * options will become less visible)
     *
     * @param teacher
     * @param student
     * @param parent
     */
    private void setAlphasUserTypes(float teacher, float student, float parent) {
        mChooseTeacherImg.setAlpha(teacher);
        mChooseTeacherText.setAlpha(teacher);
        mChooseStudentImg.setAlpha(student);
        mChooseStudentText.setAlpha(student);
        mChooseParentImg.setAlpha(parent);
        mChooseParentText.setAlpha(parent);
    }

}

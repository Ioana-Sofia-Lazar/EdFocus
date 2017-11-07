package com.ioanap.classbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

    // widgets
    private Context mContext;
    private Button mSignUpButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private TextView mSwitchToSignInTextView;
    private String mSelectedUserType;

    // choose user type included layout
    View mChooseUserTypeView;
    LinearLayout mChooseTeacherLayout, mChoosePupilLayout, mChooseParentLayout;
    ImageView mChooseTeacherImg, mChoosePupilImg, mChooseParentImg;
    TextView mChooseTeacherText, mChoosePupilText, mChooseParentText;

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
        mSignUpButton = (Button) findViewById(R.id.button_sign_up);
        mEmailEditText = (EditText) findViewById(R.id.edit_text_email);
        mPasswordEditText = (EditText) findViewById(R.id.edit_text_password);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.edit_text_confirm_password);
        mSwitchToSignInTextView = (TextView) findViewById(R.id.text_switch_to_sign_in);

        // included layout
        mChooseUserTypeView = (View) findViewById(R.id.choose_user_type);

        mChooseTeacherLayout = (LinearLayout) mChooseUserTypeView.findViewById(R.id.linear_layout_choose_teacher);
        mChooseTeacherImg = (ImageView) mChooseUserTypeView.findViewById(R.id.img_choose_teacher);
        mChooseTeacherText = (TextView) mChooseUserTypeView.findViewById(R.id.text_choose_teacher);

        mChoosePupilLayout = (LinearLayout) mChooseUserTypeView.findViewById(R.id.linear_layout_choose_pupil);
        mChoosePupilImg = (ImageView) mChooseUserTypeView.findViewById(R.id.img_choose_pupil);
        mChoosePupilText = (TextView) mChooseUserTypeView.findViewById(R.id.text_choose_pupil);

        mChooseParentLayout = (LinearLayout) mChooseUserTypeView.findViewById(R.id.linear_layout_choose_parent);
        mChooseParentImg = (ImageView) mChooseUserTypeView.findViewById(R.id.img_choose_parent);
        mChooseParentText = (TextView) mChooseUserTypeView.findViewById(R.id.text_choose_parent);

        // listeners
        mSignUpButton.setOnClickListener(this);
        mSwitchToSignInTextView.setOnClickListener(this);
        mChooseTeacherLayout.setOnClickListener(this);
        mChoosePupilLayout.setOnClickListener(this);
        mChooseParentLayout.setOnClickListener(this);
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
        if (view == mChooseTeacherLayout) {
            // user chose Teacher type
            setAlphasUserTypes((float) 1, ALPHA, ALPHA);
            mSelectedUserType = "teacher";
        }
        if (view == mChoosePupilLayout) {
            // user chose Pupil type
            setAlphasUserTypes(ALPHA, (float) 1, ALPHA);
            mSelectedUserType = "child";
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
     * @param pupil
     * @param parent
     */
    private void setAlphasUserTypes(float teacher, float pupil, float parent) {
        mChooseTeacherImg.setAlpha(teacher);
        mChooseTeacherText.setAlpha(teacher);
        mChoosePupilImg.setAlpha(pupil);
        mChoosePupilText.setAlpha(pupil);
        mChooseParentImg.setAlpha(parent);
        mChooseParentText.setAlpha(parent);
    }

}

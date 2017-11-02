package com.ioanap.classbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ioanap.classbook.child.ChildProfileActivity;
import com.ioanap.classbook.parent.ParentProfileActivity;
import com.ioanap.classbook.teacher.TeacherDrawerActivity;
import com.ioanap.classbook.utils.FirebaseUtils;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SignInActivity";

    private Button mSignInButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mSwitchToSignUpTextView;
    private ProgressDialog mProgressDialog;
    private Context mContext;

    private FirebaseUtils mFirebaseUtils;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    /**
     * Gets the filled in values from the fields.
     * Shows progress dialog and signs in the user with the email and password he introduced (if correct).
     */
    private void signIn() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // empty e-mail or password
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.setMessage("Signing In...");
        mProgressDialog.show();

        // log in user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // signed in with given email and password
                        } else {
                            // toast error message
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * When a user is already signed in, we get his type from previously saved SharedPreferences on sign in.
     * Then we redirect him to his profile.
     */
    private void redirectAlreadyLoggedUser() {
        // get from SharedPreferences the type of user that was logged in
        SharedPreferences settings = getSharedPreferences("LoginInfo", 0);
        boolean logged = settings.getBoolean("logged", false);
        String userType = settings.getString("userType", "none");

        if(userType.equals("none") || !logged) {
            return;
        }

        Intent intent;

        if (userType.equals("teacher")) {
            intent = new Intent(mContext, TeacherDrawerActivity.class);
            mContext.startActivity(intent);
        } else if (userType.equals("parent")) {
            intent = new Intent(mContext, ParentProfileActivity.class);
            mContext.startActivity(intent);
        } else {
            intent = new Intent(mContext, ChildProfileActivity.class);
            mContext.startActivity(intent);
        }

        Log.d(TAG, "redirecting already logged " + userType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mContext = SignInActivity.this;
        mFirebaseUtils = new FirebaseUtils(mContext);

        setupFirebaseAuth();

        // if user is already logged in, redirect him to his profile
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            redirectAlreadyLoggedUser();
            finish();
        }

        mProgressDialog = new ProgressDialog(this);

        mSignInButton = (Button) findViewById(R.id.button_sign_in);
        mEmailEditText = (EditText) findViewById(R.id.edit_text_email);
        mPasswordEditText = (EditText) findViewById(R.id.edit_text_password);
        mSwitchToSignUpTextView = (TextView) findViewById(R.id.text_switch_to_sign_up);

        mSignInButton.setOnClickListener(this);
        mSwitchToSignUpTextView.setOnClickListener(this);
    }

    /**
     * Sets up listener for the FirebaseAuth Object
     * When user with verified email signs in (Auth statehas changed) he will be redirected to his profile
     */
    private void setupFirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    if (currentUser.isEmailVerified()) {
                        // logging in as user with verified email

                        mFirebaseUtils.userRedirect();

                        Log.i("signed in", currentUser.getUid());
                        Toast.makeText(SignInActivity.this, "Authenticated with: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        mFirebaseUtils.saveToSharedPreferences(false, "none");
                        Toast.makeText(SignInActivity.this, "Check your Email Inbox for a Verification Link", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // no user is logged in
                    mFirebaseUtils.saveToSharedPreferences(false, "none");
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mSignInButton) {
            signIn();
        }
        if (view == mSwitchToSignUpTextView) {
            // jump to sign up activity
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }
}

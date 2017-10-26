package com.ioanap.classbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.ioanap.classbook.utils.FirebaseUtils;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SignInActivity";

    private Button mSignInButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mSwitchToSignUpTextView;
    private ProgressDialog mProgressDialog;
    private Context mContext;

    private FirebaseUtils firebaseUtils;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

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

                        } else {
                            // toast error message
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mContext = SignInActivity.this;
        firebaseUtils = new FirebaseUtils(mContext);

        setupFirebaseAuth();

        // if user is already logged in redirect to activity
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseUtils.userRedirect();
            finish();
        }

        mProgressDialog = new ProgressDialog(this);

        mSignInButton = (Button) findViewById(R.id.signInButton);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mSwitchToSignUpTextView = (TextView) findViewById(R.id.switchToSignUpTextView);

        mSignInButton.setOnClickListener(this);
        mSwitchToSignUpTextView.setOnClickListener(this);
    }

    private void setupFirebaseAuth() {

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    if (currentUser.isEmailVerified()) {
                        firebaseUtils.userRedirect();
                        Log.i("signed in", currentUser.getUid());
                        Toast.makeText(SignInActivity.this, "Authenticated with: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();

                        finish();
                        // remove SignUpActivity from stack too, so when pressing back in the profile page we won't go back to signing up
                        SignUpActivity.signUpActivity.finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Check your Email Inbox for a Verification Link", Toast.LENGTH_LONG).show();
                    }
                } else {

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

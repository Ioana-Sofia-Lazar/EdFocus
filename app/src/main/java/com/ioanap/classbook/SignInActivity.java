package com.ioanap.classbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private Button signInButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView switchToSignUpTextView;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private void signIn() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // empty e-mail or password
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Signing In...");
        progressDialog.show();

        // log in user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // user is successfully logged in -- redirect him to his profile
                            Toast.makeText(SignInActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
                            SignUpActivity.userRedirect();
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

        firebaseAuth = FirebaseAuth.getInstance();

        // if user is already logged in redirect to activity
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            SignUpActivity.userRedirect();
        }

        progressDialog = new ProgressDialog(this);

        signInButton = (Button) findViewById(R.id.signInButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        switchToSignUpTextView = (TextView) findViewById(R.id.switchToSignUpTextView);

        signInButton.setOnClickListener(this);
        switchToSignUpTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == signInButton) {
            signIn();
        }
        if (view == switchToSignUpTextView) {
            // jump to sign up activity
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }
}

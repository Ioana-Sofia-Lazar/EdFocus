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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUpButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView switchToSignInTextView;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    /**
     * Redirect User according to its type: Teacher, Parent or Child.
     */
    public static void userRedirect() {
        // TODO
    }

    private void signUp() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // empty e-mail or password
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Signing Up...");
        progressDialog.show();

        // register user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // user is successfully registered and logged in
                            Toast.makeText(SignUpActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                        } else {
                            // toast error message
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        // if user is already logged in redirect to activity
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            userRedirect();
        }

        progressDialog = new ProgressDialog(this);

        signUpButton = (Button) findViewById(R.id.signUpButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        switchToSignInTextView = (TextView) findViewById(R.id.switchToSignInTextView);

        signUpButton.setOnClickListener(this);
        switchToSignInTextView.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view == signUpButton) {
            signUp();
        }
        if (view == switchToSignInTextView) {
            // jump to Login activity
            startActivity(new Intent(this, SignInActivity.class));
        }
    }
}

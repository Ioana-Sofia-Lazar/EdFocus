package com.ioanapascu.edfocus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.ioanapascu.edfocus.shared.DrawerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SignInActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SignInActivity";

    private Button mSignInButton;
    private RelativeLayout mGoogleSignInButton, mFacebookSignInButton;
    private EditText mEmailEditText, mPasswordEditText;
    private TextView mSwitchToSignUpTextView;
    private Context mContext;
    private ImageView mSeePasswordImg;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    /**
     * Gets the filled in values from the fields and sends them to the Base Activity that will
     * perform the sign in.
     */
    private void prepareSignIn() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // empty e-mail or password
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        signIn(email, password);

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

        startActivity(new Intent(mContext, DrawerActivity.class));

        Log.d(TAG, "redirecting already logged " + userType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signin);

        mContext = SignInActivity.this;

        setupFirebaseAuth();

        // if user is already logged in, redirect him to his profile
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            redirectAlreadyLoggedUser();
            finish();
        }

        mSignInButton = findViewById(R.id.button_sign_in);
        mGoogleSignInButton = findViewById(R.id.button_google_sign_in);
        mFacebookSignInButton = findViewById(R.id.button_facebook_sign_in);
        mEmailEditText = findViewById(R.id.edit_text_email);
        mPasswordEditText = findViewById(R.id.edit_text_password);
        mSwitchToSignUpTextView = findViewById(R.id.text_switch_to_sign_up);
        mSeePasswordImg = findViewById(R.id.img_see_password);

        // click listeners
        mSignInButton.setOnClickListener(this);
        mSwitchToSignUpTextView.setOnClickListener(this);
        mGoogleSignInButton.setOnClickListener(this);
        mFacebookSignInButton.setOnClickListener(this);
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

        setupFacebookSignIn();

    }

    public void setupFacebookSignIn() {
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("SignInActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");

                                    checkFirstFacebookSignIn(email);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

                mFacebookAccessToken = loginResult.getAccessToken();
                //handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

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

                    // check if user is logged in with facebook
                    Boolean loggedWithFacebook = false;
                    for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                        if (user.getProviderId().equals("facebook.com")) {
                            loggedWithFacebook = true;
                        }
                    }

                    // if user is logged in with Facebook he doesn't need to have email verified
                    if (currentUser.isEmailVerified() || loggedWithFacebook) {
                        // logging in as user with verified email

                        userRedirect();

                        Log.i("signed in", currentUser.getUid());
                        Toast.makeText(SignInActivity.this, "Authenticated with: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        saveToSharedPreferences(false, "none");
                        Toast.makeText(SignInActivity.this, "Check your Email Inbox for a Verification Link", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // no user is logged in
                    saveToSharedPreferences(false, "none");
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
            prepareSignIn();
        }
        if (view == mSwitchToSignUpTextView) {
            // jump to sign up activity
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        }
        if (view == mGoogleSignInButton) {
            googleSignIn();
        }
        if (view == mFacebookSignInButton) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        }
    }

}

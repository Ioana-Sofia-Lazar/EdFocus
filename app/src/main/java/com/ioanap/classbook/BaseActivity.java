package com.ioanap.classbook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ioanap.classbook.model.User;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.shared.DrawerActivity;
import com.ioanap.classbook.shared.NoInternetActivity;
import com.ioanap.classbook.utils.ChooseUserTypeDialog;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        ChooseUserTypeDialog.OnUserTypeSelectedListener {

    private static final String TAG = "BaseActivity";
    // request code for google sign in
    private static final int RC_GOOGLE_SIGN_IN = 2;
    // request code for facebook sign in
    private static final int RC_FACEBOOK_SIGN_IN = CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode();
    // signing in mode : facebook | google | email
    private static String MODE = "email";

    // firebase
    protected FirebaseAuth mAuth;
    protected FirebaseDatabase mFirebaseDatabase;
    protected DatabaseReference mRootRef, mUserRef, mUserAccountSettingsRef, mContactsRef, mRequestsRef, mClassesRef,
            mUserClassesRef, mClassTokensRef, mClassCoursesRef, mClassStudentsRef, mStudentClassesRef,
            mClassEventsRef, mStudentGradesRef, mStudentAbsencesRef, mUserParentsRef, mUserChildrenRef,
            mDeviceTokensRef, mRequestNotificationsRef, mEventNotificationsRef, mSettingsRef;
    protected String CURRENT_USER_ID;
    protected GoogleApiClient mGoogleApiClient;
    protected ProgressDialog mProgressDialog;

    // Facebook
    protected CallbackManager mCallbackManager;
    protected AccessToken mFacebookAccessToken;

    // Google sign in
    GoogleSignInAccount mGoogleAccount;
    String mUserType; // for users signing in with Google account

    private Context mContext;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity, boolean fullscreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_toolbar);

            // for fragments of drawer menu activity
            if (fullscreen) {
                window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();
        mUserRef = mRootRef.child("users");
        mUserAccountSettingsRef = mRootRef.child("userAccountSettings");
        mContactsRef = mRootRef.child("contacts");
        mRequestsRef = mRootRef.child("requests");
        mClassesRef = mRootRef.child("classes");
        mUserClassesRef = mRootRef.child("userClasses");
        mClassTokensRef = mRootRef.child("classTokens");
        mClassCoursesRef = mRootRef.child("classCourses");
        mClassStudentsRef = mRootRef.child("classStudents");
        mStudentClassesRef = mRootRef.child("studentClasses");
        mClassEventsRef = mRootRef.child("classEvents");
        mStudentGradesRef = mRootRef.child("studentGrades");
        mStudentAbsencesRef = mRootRef.child("studentAbsences");
        mUserParentsRef = mRootRef.child("userParents");
        mUserChildrenRef = mRootRef.child("userChildren");
        mDeviceTokensRef = mRootRef.child("deviceTokens");
        mRequestNotificationsRef = mRootRef.child("requestNotifications");
        mEventNotificationsRef = mRootRef.child("eventNotifications");
        mSettingsRef = mRootRef.child("settings");
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext);

        checkInternetConnection();

        if(mAuth.getCurrentUser() != null){
            CURRENT_USER_ID = mAuth.getCurrentUser().getUid();
        }

        setupGoogleSignIn();

    }

    protected void checkInternetConnection() {
        if (!isOnline()) {
            // show no Internet error activity
            startActivity(new Intent(BaseActivity.this, NoInternetActivity.class));
        }
    }

    /**
     * Checks if device is connected to the Internet
     */
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr != null ? conMgr.getActiveNetworkInfo() : null;

        return !(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable());
    }

    public void showProgressDialog(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            hideProgressDialog();

        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * Registers new user and on success adds his information to the database and sends a verification
     * link to his email address. The user will be then redirected to the login page.
     *
     * @param user      information to add to the database if register is successful
     * @param email     email for the user that will be registered
     * @param password  password for the user that will be registered
     */
    public void registerNewUser(final User user, String email, String password){
        showProgressDialog("Signing Up...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // save info to firebase
                            FirebaseUser currentUser = task.getResult().getUser();
                            user.setId(currentUser.getUid());
                            addUserInfo(user, new UserAccountSettings());

                            // send verification link to the email address
                            sendEmailVerification();

                            Toast.makeText(mContext, "User registered", Toast.LENGTH_SHORT).show();

                            mAuth.signOut();

                            // redirect to login
                            mContext.startActivity(new Intent(mContext, SignInActivity.class));
                        }
                        else {
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * Adds data to Firebase for the user with ID user.getId().
     *
     * @param user
     * @param saccountSettings
     */
    public void addUserInfo(User user, UserAccountSettings saccountSettings) {
        mUserRef.child(user.getId()).setValue(user);

        saccountSettings.setId(user.getId());
        saccountSettings.setEmail(user.getEmail());
        saccountSettings.setUserType(user.getUserType());
        mUserAccountSettingsRef.child(user.getId()).setValue(saccountSettings);

        // app settings
        Map<String, Object> settings = new HashMap<>();
        settings.put("email", true);
        settings.put("location", true);
        settings.put("phone", true);
        mSettingsRef.child(user.getId()).setValue(settings);
    }

    /**
     * Sends a verification link to the email address of the currently logged user.
     */
    public void sendEmailVerification(){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null){
            currentUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext, "Verification email sent to " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(mContext, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Saves the current state to SharedPreferences (whether there is or not a logged in user and his type)
     *
     * @param logged    tells whether there is a logged in user
     * @param userType  type of the logged in user
     */
    public void saveToSharedPreferences(Boolean logged, String userType) {
        SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", MODE_PRIVATE).edit();
        editor.putBoolean("logged", logged);
        editor.putString("userType", userType);
        editor.apply();
    }

    public String getCurrentUserId() {
        return CURRENT_USER_ID;
    }

    public String getCurrentUserType() {
        return getSharedPreferences("LoginInfo", 0).getString("userType", "none");
    }

    /**
     * Update "userAccountSettings" node for current user.
     *
     * @param lastName
     * @param firstName
     * @param description
     * @param location
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String lastName, String firstName, String description,
                                          String location, String phoneNumber, String profilePhoto,
                                          String displayName) {
        DatabaseReference ref = mUserAccountSettingsRef.child(CURRENT_USER_ID);

        if (lastName != null) {
            ref.child(mContext.getString(R.string.field_last_name)).setValue(lastName);
        }
        if (firstName != null) {
            ref.child(mContext.getString(R.string.field_first_name)).setValue(firstName);
        }
        if (description != null) {
            ref.child(mContext.getString(R.string.field_description)).setValue(description);
        }
        if (location != null) {
            ref.child(mContext.getString(R.string.field_location)).setValue(location);
        }
        if (phoneNumber != null) {
            ref.child(mContext.getString(R.string.field_phone_number)).setValue(phoneNumber);
        }
        if (profilePhoto != null) {
            ref.child(mContext.getString(R.string.field_profile_photo)).setValue(profilePhoto);
        }
        if (displayName != null) {
            ref.child(mContext.getString(R.string.field_display_name)).setValue(displayName);
        }

    }

    /**
     * Receive an image as a byte array, save image to Firebase Storage, get uri(reference) to
     * the image and then call "updateUserAccountSettings" to save reference in Firebase Database.
     *
     * @param bytes
     */
    public void uploadProfilePhoto(byte[] bytes) {
        // add photo to directory in firebase storage
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("photos/" + CURRENT_USER_ID + "/profilePhoto");
        UploadTask uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get image url
                Uri firebaseUri = taskSnapshot.getDownloadUrl();

                Log.d(TAG, "success - firebase download url: " + firebaseUri.toString());

                // save image url to firebase database
                updateUserAccountSettings(null, null, null, null, null, firebaseUri.toString(), null);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Could not upload photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Redirects the currently logged in user to his profile according to his type i.e Teacher,
     * Parent or Child.
     */
    public void userRedirect() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mUserRef.child(currentUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String userType;

                        if (user == null) {
                            // this is user's first sign in with Google and he doesn't have his data added yet
                            userType = mUserType;
                        } else {
                            userType = user.getUserType();
                        }
                        // save user type to Shared Preferences for future login
                        saveToSharedPreferences(true, userType);

                        Intent intent = new Intent(mContext, DrawerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(intent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "failed to redirect user");
                    }
                });

    }

    public void signIn(String email, String password) {
        showProgressDialog("Signing In...");

        // log in user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(BaseActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            saveDeviceToken(mAuth.getCurrentUser().getUid());
                            userRedirect();
                        } else {
                            // toast error message
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Signs currently logged user out and clears all activities from stack (except from the first
     * one i.e. SignInActivity)
     */
    public void signOut() {
        // remove device token for user who is digning out
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        mDeviceTokensRef.child(mAuth.getCurrentUser().getUid()).child(deviceToken)
                .removeValue();

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Facebook sign out
        LoginManager.getInstance().logOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });

        getSharedPreferences("LoginInfo", 0).edit().clear().apply();

        // jump to Sign In activity
        Intent intent = new Intent(mContext, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    protected void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void setupGoogleSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(BaseActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Connection Failed...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    /**
     * Checks if email is already in the database
     * If YES, then proceed to sign him in as he has been signed in before
     * If NO, then this is the first sign in (and using Google so add data to the db)
     *
     * @param email
     */
    private void checkFirstGoogleSignIn(String email) {
        // check if there already is an account with this email in the database

        Log.d(TAG, "checkFirstGoogleSignIn for " + email);

        mUserRef
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            // we already have a user for this e-mail
                            Log.d(TAG, "checkFirstGoogleSignIn: not first time");

                            firebaseAuthWithGoogle(false);

                        } else {
                            // this is the first sign in for this user
                            // show dialog to select the type of user
                            Log.d(TAG, "checkFirstGoogleSignIn: first time");

                            MODE = "google";
                            ChooseUserTypeDialog dialog = new ChooseUserTypeDialog();
                            dialog.show(getSupportFragmentManager(), "SelectUserType");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        MODE = "email";
                    }
                });
    }

    /**
     * Gets an associated firebase credential for the Google account and signs in with it.
     *
     * @param firstTime if this is the first time user signs in (and with Google) default info will
     *                  be added for him in the database
     */
    private void firebaseAuthWithGoogle(final Boolean firstTime) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + mGoogleAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(mGoogleAccount.getIdToken(), null);

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");

                            if (firstTime) {
                                // add user default data
                                User user = new User(mGoogleAccount.getEmail(), mUserType);
                                UserAccountSettings settings = new UserAccountSettings();

                                user.setId(mAuth.getCurrentUser().getUid());
                                // get info from Google account
                                settings.setFirstName(mGoogleAccount.getGivenName());
                                settings.setLastName(mGoogleAccount.getFamilyName());
                                settings.setDisplayName(mGoogleAccount.getGivenName() + " " + mGoogleAccount.getFamilyName());
                                settings.setProfilePhoto(mGoogleAccount.getPhotoUrl().toString());

                                addUserInfo(user, settings);
                            }

                            saveDeviceToken(mAuth.getCurrentUser().getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    /**
     * We get the user type chosen by the new user from the dialog and proceed to sign him in.
     */
    @Override
    public void getChosenUserType(String type) {
        Log.d(TAG, "getChosenUserType: " + type);

        if (type.equals("none")) {
            // action cancelled
            // Google sign out (otherwise user won't be able to choose another Google account after cancelling)
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {

                        }
                    });

            return;
        }

        mUserType = type;

        // if result comes from user that signed in for the first time using Google
        if (MODE.equals("google")){
            firebaseAuthWithGoogle(true);
        } else {
            // result comes from user that signed in for the first time using Facebook
            handleFacebookAccessToken(true);
        }

    }

    protected void handleFacebookAccessToken(final Boolean firstTime) {
        Log.d(TAG, "handleFacebookAccessToken:" + mFacebookAccessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(mFacebookAccessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            if (firstTime) {
                                // add user default data
                                User user = new User(currentUser.getEmail(), mUserType);
                                UserAccountSettings settings = new UserAccountSettings();

                                user.setId(mAuth.getCurrentUser().getUid());
                                // get info from Facebook account
                                settings.setFirstName(task.getResult().getUser().getDisplayName());
                                settings.setDisplayName(task.getResult().getUser().getDisplayName());
                                settings.setProfilePhoto(task.getResult().getUser().getPhotoUrl().toString());

                                addUserInfo(user, settings);
                            }

                            saveDeviceToken(currentUser.getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(BaseActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveDeviceToken(String userId) {
        // save device token
        Map<String, Object> token = new HashMap<>();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        token.put(deviceToken, deviceToken);
        mDeviceTokensRef.child(userId).updateChildren(token);
    }

    /**
     * Checks if email is already in the database
     * If YES, then proceed to sign him in as he has been signed in before
     * If NO, then this is the first sign in (and using Facebook so add data to the db)
     *
     * @param email
     */
    protected void checkFirstFacebookSignIn(String email) {
        // check if there already is an account with this email in the database

        Log.d(TAG, "checkFirstFacebookSignIn for " + email);

        mUserRef
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            // we already have a user for this e-mail
                            Log.d(TAG, "checkFirstFacebookSignIn: not first time");

                            handleFacebookAccessToken(false);

                        } else {
                            Log.d(TAG, "checkFirstFacebookSignIn: first time");

                            MODE = "facebook";
                            ChooseUserTypeDialog dialog = new ChooseUserTypeDialog();
                            dialog.show(getSupportFragmentManager(), "SelectUserType");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        MODE = "email";
                    }
                });
    }

    protected Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }

    /**
     * Hide keyboard when tapping outside of it (except from tapping inside an EditText)
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean handleReturn = super.dispatchTouchEvent(ev);

        View view = getCurrentFocus();

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if(view instanceof EditText){
            View innerView = getCurrentFocus();

            if (ev.getAction() == MotionEvent.ACTION_UP &&
                    !getLocationOnScreen((EditText) innerView).contains(x, y)) {

                InputMethodManager input = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }

        return handleReturn;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                mGoogleAccount = result.getSignInAccount();

                checkFirstGoogleSignIn(mGoogleAccount.getEmail());

            } else {
                Toast.makeText(getApplicationContext(), "Sign In with Google Account failed...", Toast.LENGTH_SHORT).show();
            }
        }

        // Result returned from facebook
        if (requestCode == RC_FACEBOOK_SIGN_IN) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void removeStudentFromClass(String studentId, String classId) {
        Map<String, Object> removeMap = new HashMap<>();

        // remove from classStudents
        removeMap.put("/classStudents/" + classId + "/" + studentId + "/", null);

        // remove from studentAbsences
        removeMap.put("/studentAbsences/" + classId + "/" + studentId + "/", null);

        // remove from studentGrades
        removeMap.put("/studentAbsences/" + classId + "/" + studentId + "/", null);

        // remove from userClasses
        removeMap.put("/userClasses/" + studentId + "/" + classId + "/", null);

        mRootRef.updateChildren(removeMap);

    }

    public void confirmContactRequest(String fromUserId, String requestType) {
        // delete request from database
        mRequestsRef.child(CURRENT_USER_ID).child(fromUserId).removeValue();

        // add each as a contact for the other
        Map<String, Object> contact = new HashMap<>();
        contact.put(fromUserId, fromUserId);
        mContactsRef.child(CURRENT_USER_ID).updateChildren(contact);

        contact = new HashMap<>();
        contact.put(CURRENT_USER_ID, CURRENT_USER_ID);
        mContactsRef.child(fromUserId).updateChildren(contact);

        // if parent wants to add current user as child
        if (requestType.equals("child")) {
            contact = new HashMap<>();
            contact.put(fromUserId, fromUserId);
            mUserParentsRef.child(CURRENT_USER_ID).updateChildren(contact);

            contact = new HashMap<>();
            contact.put(CURRENT_USER_ID, CURRENT_USER_ID);
            mUserChildrenRef.child(fromUserId).updateChildren(contact);
        }

        // if parent wants to add current user as parent
        if (requestType.equals("parent")) {
            contact = new HashMap<>();
            contact.put(fromUserId, fromUserId);
            mUserChildrenRef.child(CURRENT_USER_ID).updateChildren(contact);

            contact = new HashMap<>();
            contact.put(CURRENT_USER_ID, CURRENT_USER_ID);
            mUserParentsRef.child(fromUserId).updateChildren(contact);
        }
    }

    public void declineContactRequest(String fromUserId) {
        // delete request from database
        mRequestsRef.child(CURRENT_USER_ID).child(fromUserId).removeValue();
    }

    public void cancelRequestTo(String userId) {
        Map<String, Object> remove = new HashMap<>();
        remove.put(CURRENT_USER_ID, null);
        mRequestsRef.child(userId).updateChildren(remove);
    }

    /**
     * If year is 2018, month is 3, day is 21 returns 2018-03-21
     */
    public String getDateString(int year, int month, int day) {
        String date = "";
        date += year + "-";

        if (month < 10) date += "0" + month;
        else date += month;

        date += "-";

        if (day < 10) date += "0" + day;
        else date += day;

        return date;
    }

}

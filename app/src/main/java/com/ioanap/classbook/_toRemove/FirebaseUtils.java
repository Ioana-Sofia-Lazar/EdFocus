package com.ioanap.classbook._toRemove;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ioanap.classbook.R;
import com.ioanap.classbook.SignInActivity;
import com.ioanap.classbook.child.ChildProfileActivity;
import com.ioanap.classbook.model.User;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.parent.ParentProfileActivity;
import com.ioanap.classbook.teacher.TeacherDrawerActivity;

import java.util.concurrent.Executor;

import static android.content.Context.MODE_PRIVATE;

public class FirebaseUtils {

    private static final String TAG = "FirebaseUtils";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRootRef, mUserRef, mSettingsRef;
    private String userID;

    private Context mContext;

    private ProgressDialog mProgressDialog;

    private GoogleApiClient mGoogleApiClient;

    public FirebaseUtils(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();
        mUserRef = mRootRef.child("users");
        mSettingsRef = mRootRef.child("user_account_settings");
        mContext = context;
        mProgressDialog = new ProgressDialog(mContext);

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }

        setupGoogleSignIn();
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
        mProgressDialog.setMessage("Signing Up...");
        mProgressDialog.show();

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
     * @param settings
     */
    public void addUserInfo(User user, UserAccountSettings settings) {
        mUserRef.child(user.getId()).setValue(user);

        settings.setId(user.getId());
        settings.setEmail(user.getEmail());
        settings.setUserType(user.getUserType());
        mSettingsRef.child(user.getId()).setValue(settings);
    }

    public UserAccountSettings getUserAccountSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getting user account settings");

        Log.d(TAG, "dataSnapshot: " + dataSnapshot);

        UserAccountSettings settings = dataSnapshot.child(userID).getValue(UserAccountSettings.class);
        Log.d(TAG, "settings: " + settings);

        return settings;
    }

    /**
     * Update "user_account_settings" node for current user.
     *
     * @param name
     * @param description
     * @param location
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String name, String description, String location, String phoneNumber, String profilePhoto) {
        DatabaseReference ref = mSettingsRef.child(userID);

        if (name != null) {
            ref.child(mContext.getString(R.string.field_name)).setValue(name);
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
                .child("photos/" + userID + "/profilePhoto");
        UploadTask uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get image url
                Uri firebaseUri = taskSnapshot.getDownloadUrl();

                Log.d(TAG, "success - firebase download url: " + firebaseUri.toString());

                // save image url to firebase database
                updateUserAccountSettings(null, null, null, null, firebaseUri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Could not upload photo", Toast.LENGTH_SHORT).show();
            }
        });
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
        SharedPreferences.Editor editor = mContext.getSharedPreferences("LoginInfo", MODE_PRIVATE).edit();
        editor.putBoolean("logged", logged);
        editor.putString("userType", userType);
        editor.apply();
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
                        String userType = user.getUserType();

                        // save user type to Shared Preferences for future login
                        saveToSharedPreferences(true, userType);

                        // redirect user to corresponding type of profile and delete all previous
                        // activities from stack
                        Intent intent;
                        if (userType.equals("teacher")) {
                            intent = new Intent(mContext, TeacherDrawerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(intent);
                        } else if (userType.equals("parent")) {
                            intent = new Intent(mContext, ParentProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(intent);
                        } else {
                            intent = new Intent(mContext, ChildProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(intent);
                        }

                        Log.d(TAG, "redirecting as" + userType);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "failed to redirect user");
                    }
                });

    }

    public void signIn(String email, String password) {
        mProgressDialog.setMessage("Signing In...");
        mProgressDialog.show();

        // log in user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // signed in with given email and password
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
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });

        // jump to Sign In activity
        Intent intent = new Intent(mContext, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    // ======================== Google Sign In ============================

    private void setupGoogleSignIn() {
        //Log.d(TAG, "string: " + Resources.getSystem().getString(android.R.string.default_web_client_id));

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Resources.getSystem().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mContext, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(mContext, "Connection Failed...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }



}

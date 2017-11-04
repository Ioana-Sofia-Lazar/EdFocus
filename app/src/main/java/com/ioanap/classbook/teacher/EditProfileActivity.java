package com.ioanap.classbook.teacher;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.FirebaseUtils;
import com.ioanap.classbook.utils.UniversalImageLoader;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EditProfileActivity";

    private ImageView mCancelImageView, mSaveImageView, mEditProfilePhotoImageView;
    private TextView mEditProfilePhotoTextView;
    private EditText mNameEditText, mDescriptionEditText, mLocationEditText, mEmailEditText, mPhoneNumberEditText;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRootRef, mSettingsRef;
    private FirebaseUtils mFirebaseUtils;
    private Context mContext;

    private UserAccountSettings mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mContext = EditProfileActivity.this;

        setupWidgets();
        setupFirebase();

    }

    private void setupWidgets() {
        // widgets
        mEditProfilePhotoImageView = (ImageView) findViewById(R.id.edit_profile_photo);
        mEditProfilePhotoTextView = (TextView) findViewById(R.id.text_edit_profile_photo);
        mNameEditText = (EditText) findViewById(R.id.edit_text_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_text_description);
        mLocationEditText = (EditText) findViewById(R.id.edit_text_location);
        mEmailEditText = (EditText) findViewById(R.id.edit_text_email);
        mPhoneNumberEditText = (EditText) findViewById(R.id.edit_text_phone_number);

        // toolbar buttons
        mCancelImageView = (ImageView) findViewById(R.id.image_cancel);
        mSaveImageView = (ImageView) findViewById(R.id.image_save);

        // on click listeners for toolbar buttons
        mCancelImageView.setOnClickListener(this);
        mSaveImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mCancelImageView) {
            finish();
        }
        if (view == mSaveImageView) {
            // save information
            saveProfileSettings();

            finish();
        }
    }

    /**
     * Retrieves info entered by the user and saves it to the database.
     */
    private void saveProfileSettings() {
        String name = mNameEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
        String location = mLocationEditText.getText().toString();
        String phoneNumber = mPhoneNumberEditText.getText().toString();
        // String profilePhoto = "";

        if (!mSettings.getName().equals(name)) {
            mFirebaseUtils.updateUserAccountSettings(name, null, null, null, null);
        }
        if (!mSettings.getDescription().equals(description)) {
            mFirebaseUtils.updateUserAccountSettings(null, description, null, null, null);
        }
        if (!mSettings.getLocation().equals(location)) {
            mFirebaseUtils.updateUserAccountSettings(null, null, location, null, null);
        }
        if (!mSettings.getPhoneNumber().equals(phoneNumber)) {
            mFirebaseUtils.updateUserAccountSettings(null, null, null, phoneNumber, null);
        }

    }

    /**
     * Fill the widgets from the Profile with the information from Firebase
     *
     * @param settings
     */
    private void setEditProfileWidgets(UserAccountSettings settings) {
        mSettings = settings;

        mNameEditText.setText(settings.getName());
        mDescriptionEditText.setText(settings.getDescription());
        mLocationEditText.setText(settings.getLocation());
        mEmailEditText.setText(settings.getEmail());
        mPhoneNumberEditText.setText(settings.getPhoneNumber());

        setProfilePhoto(settings.getProfilePhoto());
    }

    private void setProfilePhoto(String url) {
        UniversalImageLoader.setImage(url, mEditProfilePhotoImageView, null, "");
    }

    /**
     * Firebase instances, references and listener for when updates in the user's settings are
     * made in the database.
     */
    private void setupFirebase() {
        Log.d(TAG, "setupFirebase");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();
        mSettingsRef = mRootRef.child("user_account_settings");
        mFirebaseUtils = new FirebaseUtils(mContext);

        mSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve user info
                UserAccountSettings settings = mFirebaseUtils.getUserAccountSettings(dataSnapshot);

                // setup widgets to display user info from the database
                setEditProfileWidgets(settings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

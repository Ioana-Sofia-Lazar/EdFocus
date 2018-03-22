package com.ioanap.classbook.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.UniversalImageLoader;

import java.util.HashMap;
import java.util.Map;

public class ViewTeacherProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ViewTeacherProfileFragm";

    private String userId;

    // widgets
    private Button mAddContactButton;
    private ImageView mProfilePhotoImageView;
    private TextView mNameTextView, mDescriptionTextView, mContactsTextView, mClassesTextView,
            mEmailTextView, mLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher_profile);

        // get user whose profile was tapped
        Intent myIntent = getIntent();
        userId = myIntent.getStringExtra("userId");
        Log.d(TAG, "viewing profile for " + userId);

        // widgets
        mProfilePhotoImageView = findViewById(R.id.image_profile_photo);
        mAddContactButton = findViewById(R.id.button_add_contact);
        mNameTextView = findViewById(R.id.text_name);
        mDescriptionTextView = findViewById(R.id.text_description);
        mContactsTextView = findViewById(R.id.text_contacts);
        mClassesTextView = findViewById(R.id.text_classes);
        mEmailTextView = findViewById(R.id.text_email);
        mLocationTextView = findViewById(R.id.text_location);

        showUserInfo();

        mAddContactButton.setOnClickListener(this);
    }

    private void showUserInfo() {
        // add listener for the settings of the user whose profile is being viewed
        mSettingsRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve user info
                UserAccountSettings settings = getUserAccountSettings(dataSnapshot);

                // setup widgets to display user info from the database
                setProfileWidgets(settings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setProfileWidgets(UserAccountSettings settings) {
        mNameTextView.setText(settings.getFirstName() + " " + settings.getLastName());
        mDescriptionTextView.setText(settings.getDescription());
        mContactsTextView.setText(String.valueOf(settings.getNoOfContacts()));
        mClassesTextView.setText(String.valueOf(settings.getNoOfClasses()));
        mEmailTextView.setText(settings.getEmail());
        mLocationTextView.setText(settings.getLocation());

        UniversalImageLoader.setImage(settings.getProfilePhoto(), mProfilePhotoImageView, null);

        // if there already is a request for this user, disable button
        checkRequestAlreadyExists();

        // todo
        // if he is aleary a contact for the current user
        checkAlreadyContact();
    }

    private void checkRequestAlreadyExists() {
        // see if the user whose profile is being viewed has a request from the current user already

        mRequestsRef.child(userId).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    disableButton("request sent");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // todo
    private boolean checkAlreadyContact() {
        // see if the user whose profile is being viewed is already a contact for the current user

        mContactsRef.child(userId).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    disableButton("your contact");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == mAddContactButton) {
            // create request in the database
            Map<String, Object> request = new HashMap<>();
            request.put("requestType", "contact");
            mRequestsRef.child(userId).child(userID).updateChildren(request);

            // show that request has been sent
            disableButton("request sent");
        }
    }

    private void disableButton(String mode) {
        mAddContactButton.setBackgroundColor(getResources().getColor(R.color.gray));
        mAddContactButton.setText(mode);
        mAddContactButton.setEnabled(false);
    }
}

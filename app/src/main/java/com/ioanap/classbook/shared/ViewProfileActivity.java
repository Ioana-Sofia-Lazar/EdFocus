package com.ioanap.classbook.shared;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.UniversalImageLoader;

public class ViewProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ViewTeacherProfileFragm";

    private String mUserId;

    // widgets
    private Button mAddContactButton, mAcceptRequestButton, mDeclineRequestButton, mCancelRequestButton;
    private ImageView mProfilePhotoImageView;
    private TextView mNameTextView, mDescriptionTextView, mContactsTextView, mClassesTextView,
            mEmailTextView, mLocationTextView, mUserTypeTextView;
    private LinearLayout mRequestSentLayout, mRequestReceivedLayout, mInfoLayout, mPrivateLayout;
    private GridLayout mInfoNumbersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(ViewProfileActivity.this, false);
        setContentView(R.layout.activity_view_teacher_profile);

        // get user whose profile was tapped
        Intent myIntent = getIntent();
        mUserId = myIntent.getStringExtra("userId");
        Log.d(TAG, "viewing profile for " + mUserId);

        // widgets
        mProfilePhotoImageView = findViewById(R.id.image_profile_photo);
        mAddContactButton = findViewById(R.id.button_add_contact);
        mAcceptRequestButton = findViewById(R.id.button_accept_request);
        mDeclineRequestButton = findViewById(R.id.button_decline_request);
        mCancelRequestButton = findViewById(R.id.button_cancel_request);
        mNameTextView = findViewById(R.id.text_name);
        mDescriptionTextView = findViewById(R.id.text_description);
        mContactsTextView = findViewById(R.id.text_contacts);
        mClassesTextView = findViewById(R.id.text_classes);
        mEmailTextView = findViewById(R.id.text_email);
        mLocationTextView = findViewById(R.id.text_location);
        mUserTypeTextView = findViewById(R.id.text_user_type);
        mRequestSentLayout = findViewById(R.id.layout_request_sent);
        mRequestReceivedLayout = findViewById(R.id.layout_request_received);
        mInfoLayout = findViewById(R.id.layout_info);
        mPrivateLayout = findViewById(R.id.layout_private);
        mInfoNumbersLayout = findViewById(R.id.layout_info_numbers);

        showUserInfo();

        mAddContactButton.setOnClickListener(this);
        mAcceptRequestButton.setOnClickListener(this);
        mDeclineRequestButton.setOnClickListener(this);
        mCancelRequestButton.setOnClickListener(this);
    }

    private void showUserInfo() {
        // add listener for the settings of the user whose profile is being viewed
        mSettingsRef.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve user info
                UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);

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
        mUserTypeTextView.setText(settings.getUserType());

        // set number of contacts
        mContactsRef.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mContactsTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // set number of classes
        mClassesRef.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mClassesTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mEmailTextView.setText(settings.getEmail());
        mLocationTextView.setText(settings.getLocation());

        UniversalImageLoader.setImage(settings.getProfilePhoto(), mProfilePhotoImageView, null);

        hideWidgets(0);
        checkRequestSent();
        checkRequestReceived();
        checkAlreadyContact();

    }

    private void checkRequestSent() {
        // see if the user whose profile is being viewed has a request from the current user already
        mRequestsRef.child(mUserId).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hideWidgets(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkRequestReceived() {
        // see if the current user has a request from the user whose profile is being viewed
        mRequestsRef.child(userID).child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hideWidgets(2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkAlreadyContact() {
        // see if the user whose profile is being viewed is already a contact for the current user
        mContactsRef.child(mUserId).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hideWidgets(3);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mAddContactButton) {
            addContact(mUserId);
            hideWidgets(1);
        } else if (view == mAcceptRequestButton) {
            confirmContactRequest(mUserId);
            hideWidgets(3);
        } else if (view == mDeclineRequestButton) {
            declineContactRequest(mUserId);
            hideWidgets(0);
        } else if (view == mCancelRequestButton) {
            cancelRequestTo(mUserId);
            hideWidgets(0);
        }
    }

    /**
     * Hides and shows widgets according to the status of the relationship between current user and viewed user
     *
     * @param mode 0 - not contacts, and no request from anyone to anyone     *
     *             1 - not contacts, request from current user to viewed user
     *             2 - not contacts, request from viewed user to current user
     *             3 - already contacts
     */
    private void hideWidgets(int mode) {
        switch (mode) {
            case 0:
                mAddContactButton.setVisibility(View.VISIBLE);
                mRequestSentLayout.setVisibility(View.GONE);
                mRequestReceivedLayout.setVisibility(View.GONE);
                mInfoLayout.setVisibility(View.GONE);
                mInfoNumbersLayout.setVisibility(View.GONE);
                mPrivateLayout.setVisibility(View.VISIBLE);
                break;
            case 1:
                mAddContactButton.setVisibility(View.GONE);
                mRequestSentLayout.setVisibility(View.VISIBLE);
                mRequestReceivedLayout.setVisibility(View.GONE);
                mInfoLayout.setVisibility(View.GONE);
                mInfoNumbersLayout.setVisibility(View.GONE);
                mPrivateLayout.setVisibility(View.VISIBLE);
                break;
            case 2:
                mAddContactButton.setVisibility(View.GONE);
                mRequestSentLayout.setVisibility(View.GONE);
                mRequestReceivedLayout.setVisibility(View.VISIBLE);
                mInfoLayout.setVisibility(View.GONE);
                mInfoNumbersLayout.setVisibility(View.GONE);
                mPrivateLayout.setVisibility(View.VISIBLE);
                break;
            case 3:
                mAddContactButton.setVisibility(View.GONE);
                mRequestSentLayout.setVisibility(View.GONE);
                mRequestReceivedLayout.setVisibility(View.GONE);
                mInfoLayout.setVisibility(View.VISIBLE);
                mInfoNumbersLayout.setVisibility(View.VISIBLE);
                mPrivateLayout.setVisibility(View.GONE);
                break;
        }
    }
}

package com.ioanapascu.edfocus.shared;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.UserAccountSettings;
import com.ioanapascu.edfocus.utils.UniversalImageLoader;

import java.util.HashMap;
import java.util.Map;

public class ViewProfileActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "ViewProfileActivity";

    private String mUserId, mUserType;

    // widgets
    private Button mAddContactButton, mAcceptRequestButton, mDeclineRequestButton, mCancelRequestButton;
    private ImageView mProfilePhotoImageView, mShowOptionsImg;
    private TextView mNameTextView, mDescriptionTextView, mContactsTextView, mClassesTextView,
            mEmailTextView, mLocationTextView, mPhoneTextView, mUserTypeTextView;
    private LinearLayout mRequestSentLayout, mRequestReceivedLayout, mInfoLayout, mPrivateLayout;
    private GridLayout mInfoNumbersLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(ViewProfileActivity.this, false);
        setContentView(R.layout.activity_view_profile);

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
        mPhoneTextView = findViewById(R.id.text_phone);
        mUserTypeTextView = findViewById(R.id.text_user_type);
        mRequestSentLayout = findViewById(R.id.layout_request_sent);
        mRequestReceivedLayout = findViewById(R.id.layout_request_received);
        mInfoLayout = findViewById(R.id.layout_info);
        mPrivateLayout = findViewById(R.id.layout_private);
        mInfoNumbersLayout = findViewById(R.id.layout_info_numbers);
        mToolbar = findViewById(R.id.toolbar);
        mShowOptionsImg = findViewById(R.id.img_show_options);

        showUserInfo();

        mAddContactButton.setOnClickListener(this);
        mAcceptRequestButton.setOnClickListener(this);
        mDeclineRequestButton.setOnClickListener(this);
        mCancelRequestButton.setOnClickListener(this);
        mShowOptionsImg.setOnClickListener(this);
    }

    private void showUserInfo() {
        // add listener for the settings of the user whose profile is being viewed
        mUserAccountSettingsRef.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve user info
                UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);

                mUserType = settings.getUserType();

                // setup widgets to display user info from the database
                setProfileWidgets(settings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(final UserAccountSettings settings) {
        mNameTextView.setText(String.format("%s %s", settings.getFirstName(), settings.getLastName()));
        mDescriptionTextView.setText(settings.getDescription());
        String capitalizedUserType = settings.getUserType().substring(0, 1).toUpperCase() + settings.getUserType().substring(1);
        mUserTypeTextView.setText(capitalizedUserType);

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
        mUserClassesRef.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mClassesTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // see if user being viewed has his information secret or unspecified
        mSettingsRef.child(CURRENT_USER_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("email").exists()) {
                    boolean isPublic = (boolean) dataSnapshot.child("email").getValue();
                    checkInfoUnspecified(mEmailTextView, settings.getEmail(), isPublic);
                }
                if (dataSnapshot.child("location").exists()) {
                    boolean isPublic = (boolean) dataSnapshot.child("location").getValue();
                    checkInfoUnspecified(mLocationTextView, settings.getLocation(), isPublic);
                }
                if (dataSnapshot.child("phone").exists()) {
                    boolean isPublic = (boolean) dataSnapshot.child("phone").getValue();
                    checkInfoUnspecified(mPhoneTextView, settings.getPhoneNumber(), isPublic);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UniversalImageLoader.setImage(settings.getProfilePhoto(), mProfilePhotoImageView, null);

        hideWidgets(0);
        checkRequestSent();
        checkRequestReceived();
        checkAlreadyContact();

    }

    private void checkInfoUnspecified(TextView textView, String text, Boolean isPublic) {
        if (text.equals("") || !isPublic) {
            textView.setTextColor(getResources().getColor(R.color.lightGray));
            textView.setText("Unspecified");
        } else {
            textView.setTextColor(getResources().getColor(R.color.gray));
            textView.setText(text);
        }
    }

    private void checkRequestSent() {
        // see if the user whose profile is being viewed has a request from the current user already
        mRequestsRef.child(mUserId).child(CURRENT_USER_ID).addValueEventListener(new ValueEventListener() {
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
        mRequestsRef.child(CURRENT_USER_ID).child(mUserId).addValueEventListener(new ValueEventListener() {
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
        mContactsRef.child(mUserId).child(CURRENT_USER_ID).addValueEventListener(new ValueEventListener() {
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
            addContactPossibilities();
            //hideWidgets(1);
        } else if (view == mAcceptRequestButton) {
            firebase.confirmContactRequest(mUserId, mUserType);
            hideWidgets(3);
        } else if (view == mDeclineRequestButton) {
            firebase.declineContactRequest(mUserId);
            hideWidgets(0);
        } else if (view == mCancelRequestButton) {
            firebase.cancelRequestTo(mUserId);
            hideWidgets(0);
        } else if (view == mShowOptionsImg) {
            showPopupMenu(view);
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_view_profile);
        popup.show();
    }

    public void addContactPossibilities() {
        if (getCurrentUserType().equals("teacher") && mUserType.equals("teacher")) addContact();
        if (getCurrentUserType().equals("teacher") && mUserType.equals("parent")) addContact();
        if (getCurrentUserType().equals("teacher") && mUserType.equals("student")) addContact();
        if (getCurrentUserType().equals("parent") && mUserType.equals("teacher")) addContact();
        if (getCurrentUserType().equals("parent") && mUserType.equals("parent")) addContact();
        if (getCurrentUserType().equals("parent") && mUserType.equals("student"))
            showParentAddStudentDialog();
        if (getCurrentUserType().equals("student") && mUserType.equals("teacher")) addContact();
        if (getCurrentUserType().equals("student") && mUserType.equals("parent"))
            showStudentAddParentDialog();
        if (getCurrentUserType().equals("student") && mUserType.equals("student")) addContact();
    }

    private void showStudentAddParentDialog() {
        final Dialog dialog = new Dialog(ViewProfileActivity.this);
        dialog.setContentView(R.layout.dialog_student_add_parent);

        // dialog widgets
        final TextView mAddParent = dialog.findViewById(R.id.text_add_parent);
        final TextView mAddContact = dialog.findViewById(R.id.text_add_conteact);

        // add this user as parent
        mAddParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact("parent");
                hideWidgets(1);

                dialog.dismiss();
            }
        });

        // add this user as simple contact
        mAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
                hideWidgets(1);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showParentAddStudentDialog() {
        final Dialog dialog = new Dialog(ViewProfileActivity.this);
        dialog.setContentView(R.layout.dialog_parent_add_student);

        // dialog widgets
        final TextView mAddChild = dialog.findViewById(R.id.text_add_child);
        final TextView mAddContact = dialog.findViewById(R.id.text_add_conteact);

        // add this user as child
        mAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact("child");
                hideWidgets(1);

                dialog.dismiss();
            }
        });

        // add this user as simple contact
        mAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
                hideWidgets(1);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addContact(String requestType) {
        // create request in the database
        Map<String, Object> request = new HashMap<>();
        request.put("requestType", requestType);
        mRequestsRef.child(mUserId).child(CURRENT_USER_ID).updateChildren(request);
    }

    private void addContact() {
        // create request in the database
        Map<String, Object> request = new HashMap<>();
        request.put("requestType", "contact");
        mRequestsRef.child(mUserId).child(CURRENT_USER_ID).updateChildren(request);
        hideWidgets(1);
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
                mShowOptionsImg.setVisibility(View.GONE);
                break;
            case 1:
                mAddContactButton.setVisibility(View.GONE);
                mRequestSentLayout.setVisibility(View.VISIBLE);
                mRequestReceivedLayout.setVisibility(View.GONE);
                mInfoLayout.setVisibility(View.GONE);
                mInfoNumbersLayout.setVisibility(View.GONE);
                mPrivateLayout.setVisibility(View.VISIBLE);
                mShowOptionsImg.setVisibility(View.GONE);
                break;
            case 2:
                mAddContactButton.setVisibility(View.GONE);
                mRequestSentLayout.setVisibility(View.GONE);
                mRequestReceivedLayout.setVisibility(View.VISIBLE);
                mInfoLayout.setVisibility(View.GONE);
                mInfoNumbersLayout.setVisibility(View.GONE);
                mPrivateLayout.setVisibility(View.VISIBLE);
                mShowOptionsImg.setVisibility(View.GONE);
                break;
            case 3:
                mAddContactButton.setVisibility(View.GONE);
                mRequestSentLayout.setVisibility(View.GONE);
                mRequestReceivedLayout.setVisibility(View.GONE);
                mInfoLayout.setVisibility(View.VISIBLE);
                mInfoNumbersLayout.setVisibility(View.VISIBLE);
                mPrivateLayout.setVisibility(View.GONE);
                mShowOptionsImg.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_remove_contact:
                showConfirmationDialog();
                return true;
            default:
                return false;
        }
    }

    private void showConfirmationDialog() {
        // show confirmation dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to delete this person from your contacts list?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebase.removeContact(firebase.getCurrentUserId(), mUserId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create and show alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }
}

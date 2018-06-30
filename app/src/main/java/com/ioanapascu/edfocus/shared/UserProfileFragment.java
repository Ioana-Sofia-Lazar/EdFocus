package com.ioanapascu.edfocus.shared;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.UserAccountSettings;
import com.ioanapascu.edfocus.others.UniversalImageLoader;
import com.ioanapascu.edfocus.utils.FirebaseUtils;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "UserProfileFragment";
    FirebaseUtils firebase;
    private OnFragmentInteractionListener mListener;
    // widgets
    private ImageView mProfilePhotoImageView, mEditProfileButton;
    private TextView mNameTextView, mDescriptionTextView, mContactsTextView, mClassesTextView,
            mEmailTextView, mLocationTextView, mPhoneTextView, mUserTypeTextView;
    private ProgressBar mProgressBar;
    private LinearLayout mEditProfileLayout;
    // variables
    private String mCurrentUserId;
    private ValueEventListener mUserSettingsListener;

    public UserProfileFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // widgets
        mProfilePhotoImageView = view.findViewById(R.id.image_profile_photo);
        mEditProfileButton = view.findViewById(R.id.button_edit_profile);
        mNameTextView = view.findViewById(R.id.text_name);
        mDescriptionTextView = view.findViewById(R.id.text_description);
        mContactsTextView = view.findViewById(R.id.text_contacts);
        mClassesTextView = view.findViewById(R.id.text_classes);
        mUserTypeTextView = view.findViewById(R.id.text_user_type);
        mEmailTextView = view.findViewById(R.id.text_email);
        mLocationTextView = view.findViewById(R.id.text_location);
        mPhoneTextView = view.findViewById(R.id.text_phone);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mEditProfileLayout = view.findViewById(R.id.layout_edit_profile);

        mEditProfileButton.setOnClickListener(this);
        mEditProfileLayout.setOnClickListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebase = new FirebaseUtils(getContext());
        setupFirebase();
    }

    private void setupFirebase() {
        Log.d(TAG, "setupFirebase");

        mCurrentUserId = firebase.getCurrentUserId();

        // add listener for the settings of the currently logged user
        mUserSettingsListener = new ValueEventListener() {
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
        };

        firebase.mSettingsRef.child(mCurrentUserId).addValueEventListener(mUserSettingsListener);

    }

    /**
     * Fill the widgets from the Profile with the information from Firebase
     */
    private void setProfileWidgets(UserAccountSettings settings) {
        mNameTextView.setText(String.format("%s %s", settings.getFirstName(), settings.getLastName()));
        mDescriptionTextView.setText(settings.getDescription());

        String capitalizedString = settings.getUserType().substring(0, 1).toUpperCase() +
                settings.getUserType().substring(1);
        mUserTypeTextView.setText(capitalizedString);

        // set number of contacts
        firebase.mContactsRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mContactsTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // set number of classes
        firebase.mUserClassesRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mClassesTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mEmailTextView.setText(settings.getEmail());

        checkUnspecified(mLocationTextView, settings.getLocation());
        checkUnspecified(mPhoneTextView, settings.getPhoneNumber());

        UniversalImageLoader.setImage(settings.getProfilePhoto(), mProfilePhotoImageView, mProgressBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        firebase.mSettingsRef.child(mCurrentUserId).removeEventListener(mUserSettingsListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebase.mSettingsRef.child(mCurrentUserId).removeEventListener(mUserSettingsListener);
    }

    @Override
    public void onClick(View view) {
        if (view == mEditProfileButton || view == mEditProfileLayout) {
            // jump to edit profile page
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
        }
    }

    private void checkUnspecified(TextView textView, String text) {
        if (text.equals("")) {
            textView.setTextColor(getResources().getColor(R.color.lightGray));
            textView.setText("Unspecified");
        } else {
            textView.setTextColor(getResources().getColor(R.color.gray));
            textView.setText(text);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

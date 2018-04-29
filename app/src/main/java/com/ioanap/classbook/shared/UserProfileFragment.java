package com.ioanap.classbook.shared;

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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.UniversalImageLoader;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "UserProfileFragment";

    private OnFragmentInteractionListener mListener;

    // widgets
    private ImageView mProfilePhotoImageView, mEditProfileButton;
    private TextView mNameTextView, mDescriptionTextView, mContactsTextView, mClassesTextView,
            mEmailTextView, mLocationTextView, mUserTypeTextView;

    // variables
    private DatabaseReference mRootRef, mSettingsRef, mContactsRef, mClassesRef;
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

        mEditProfileButton.setOnClickListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setupFirebase();

    }

    private void setupFirebase() {
        Log.d(TAG, "setupFirebase");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mSettingsRef = mRootRef.child("userAccountSettings");
        mClassesRef = mRootRef.child("classes");
        mContactsRef = mRootRef.child("contacts");
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        mSettingsRef.child(mCurrentUserId).addValueEventListener(mUserSettingsListener);

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
        mContactsRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mContactsTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // set number of classes
        mClassesRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
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

        setProfilePhoto(settings.getProfilePhoto());
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

    private void setProfilePhoto(String url) {
        UniversalImageLoader.setImage(url, mProfilePhotoImageView, null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mSettingsRef.child(mCurrentUserId).removeEventListener(mUserSettingsListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSettingsRef.child(mCurrentUserId).removeEventListener(mUserSettingsListener);
    }

    @Override
    public void onClick(View view) {
        if (view == mEditProfileButton) {
            // jump to edit profile page
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
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

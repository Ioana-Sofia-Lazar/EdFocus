package com.ioanap.classbook.teacher;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.UniversalImageLoader;

public class TeacherProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TeacherProfileFragment";

    private OnFragmentInteractionListener mListener;
    private BaseActivity mBaseActivity;

    //widgets
    private Button mEditProfileButton;
    private ImageView mProfilePhotoImageView;
    private TextView mNameTextView, mDescriptionTextView, mContactsTextView, mClassesTextView,
        mEmailTextView, mLocationTextView;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRootRef, mSettingsRef;
    private Context mContext;

    public TeacherProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // widgets
        mProfilePhotoImageView = (ImageView) view.findViewById(R.id.image_profile_photo);
        mEditProfileButton = (Button) view.findViewById(R.id.button_edit_profile);
        mNameTextView = (TextView) view.findViewById(R.id.text_name);
        mDescriptionTextView = (TextView) view.findViewById(R.id.text_description);
        mContactsTextView = (TextView) view.findViewById(R.id.text_contacts);
        mClassesTextView = (TextView) view.findViewById(R.id.text_classes);
        mEmailTextView = (TextView) view.findViewById(R.id.text_email);
        mLocationTextView = (TextView) view.findViewById(R.id.text_location);

        mEditProfileButton.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        setupFirebase();

    }

    private void setupFirebase() {
        Log.d(TAG, "setupFirebase");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();
        mSettingsRef = mRootRef.child("user_account_settings").child(mBaseActivity.getCurrentUserId());

        // add listener for the settings of the currently logged user
        mSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve user info
                Log.d(TAG, "setupfirebase - datasnapshot : " + dataSnapshot);
                UserAccountSettings settings = mBaseActivity.getUserAccountSettings(dataSnapshot);
                Log.d(TAG, "settings from db changed: " + settings);

                // setup widgets to display user info from the database
                setProfileWidgets(settings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Fill the widgets from the Profile with the information from Firebase
     *
     * @param settings
     */
    private void setProfileWidgets(UserAccountSettings settings) {
        mNameTextView.setText(settings.getFirstName() + " " + settings.getLastName());
        mDescriptionTextView.setText(settings.getDescription());
        mContactsTextView.setText(String.valueOf(settings.getNoOfContacts()));
        mClassesTextView.setText(String.valueOf(settings.getNoOfClasses()));
        mEmailTextView.setText(settings.getEmail());
        mLocationTextView.setText(settings.getLocation());

        setProfilePhoto(settings.getProfilePhoto());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) getActivity();
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

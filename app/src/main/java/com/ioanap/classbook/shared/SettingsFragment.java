package com.ioanap.classbook.shared;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;

/**
 * Created by ioana on 2/23/2018.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    // widgets
    Switch mLocationSwitch, mEmailSwitch, mPhoneSwitch;
    LinearLayout mChangePasswordLayout, mAccountLayout;
    TextView mEmailNobody, mEmailContacts, mLocationNobody, mLocationContacts, mPhoneNobody, mPhoneContacts;
    View mSeparator;

    // variables
    DatabaseReference mSettingsRef;
    String mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsRef = FirebaseDatabase.getInstance().getReference().child("settings");
        mUserId = ((DrawerActivity) getActivity()).getCurrentUserId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // widgets
        mEmailSwitch = view.findViewById(R.id.switch_email);
        mLocationSwitch = view.findViewById(R.id.switch_location);
        mPhoneSwitch = view.findViewById(R.id.switch_phone_number);
        mChangePasswordLayout = view.findViewById(R.id.layout_change_password);
        mAccountLayout = view.findViewById(R.id.layout_account);
        mSeparator = view.findViewById(R.id.separator);
        mEmailNobody = view.findViewById(R.id.text_email_nobody);
        mEmailContacts = view.findViewById(R.id.text_email_contacts);
        mLocationNobody = view.findViewById(R.id.text_location_nobody);
        mLocationContacts = view.findViewById(R.id.text_location_contacts);
        mPhoneNobody = view.findViewById(R.id.text_phone_nobody);
        mPhoneContacts = view.findViewById(R.id.text_phone_contacts);

        setupListeners();
        displaySettings();

        boolean showChangePassword = false;
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("password")) {
                // user is signed in using email and password
                showChangePassword = true;
            }
        }
        if (showChangePassword) {
            mAccountLayout.setVisibility(View.VISIBLE);
            mSeparator.setVisibility(View.VISIBLE);
        }
    }

    private void displaySettings() {
        mSettingsRef.child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEmailSwitch.setChecked((Boolean) dataSnapshot.child("email").getValue());
                mLocationSwitch.setChecked((Boolean) dataSnapshot.child("location").getValue());
                mPhoneSwitch.setChecked((Boolean) dataSnapshot.child("phone").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupListeners() {
        mChangePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show change password dialog
                // todo
            }
        });
        mEmailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSwitchOptions(mEmailSwitch, mEmailNobody, mEmailContacts);
                mSettingsRef.child(mUserId).child("email").setValue(isChecked);
            }
        });
        mLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSwitchOptions(mLocationSwitch, mLocationNobody, mLocationContacts);
                mSettingsRef.child(mUserId).child("location").setValue(isChecked);
            }
        });
        mPhoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSwitchOptions(mPhoneSwitch, mPhoneNobody, mPhoneContacts);
                mSettingsRef.child(mUserId).child("phone").setValue(isChecked);
            }
        });
    }

    private void toggleSwitchOptions(Switch aSwitch, TextView optionOff, TextView optionOn) {
        int lightGray = getResources().getColor(R.color.lightGray);
        int gray = getResources().getColor(R.color.gray);
        if (aSwitch.isChecked()) {
            optionOff.setTextColor(lightGray);
            optionOn.setTextColor(gray);
        } else {
            optionOff.setTextColor(gray);
            optionOn.setTextColor(lightGray);
        }
    }

    @Override
    public void onClick(View view) {
    }
}

package com.ioanap.classbook.shared;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

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
                showChangePasswordDialog();
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

    private void showChangePasswordDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_change_password);

        // dialog widgets
        final EditText oldPassText = dialog.findViewById(R.id.txt_old_pass);
        final EditText newPassText = dialog.findViewById(R.id.txt_new_pass);
        final EditText confirmPassText = dialog.findViewById(R.id.txt_confirm_pass);
        final Button confirmBtn = dialog.findViewById(R.id.btn_confirm);
        ImageView cancelImg = dialog.findViewById(R.id.img_cancel);

        // create course button click
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                final String oldPass = oldPassText.getText().toString();
                final String newPass = newPassText.getText().toString();
                String confirmPass = confirmPassText.getText().toString();

                // validation
                if (!newPass.equals(confirmPass)) {
                    confirmPassText.setError("Passwords do not match.");
                    return;
                }
                if (newPass.length() < 6) {
                    newPassText.setError("Password must be at least 6 characters long.");
                    return;
                }

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), oldPass);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Password successfully updated.", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(getActivity(), "An error has occurred. Please try again.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    oldPassText.setError("Password is incorrect.");
                                    Log.d(TAG, "Error auth failed");
                                }
                            }
                        });
            }
        });

        // x button click (cancel)
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}

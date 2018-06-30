package com.ioanapascu.edfocus.utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.User;
import com.ioanapascu.edfocus.model.UserAccountSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ioana Pascu on 6/17/2018.
 */

public class FirebaseUtils {
    public final FirebaseAuth mAuth;
    public final FirebaseDatabase mFirebaseDatabase;
    public final DatabaseReference mRootRef, mUsersRef, mUserAccountSettingsRef, mContactsRef, mRequestsRef, mClassesRef,
            mUserClassesRef, mClassTokensRef, mClassCoursesRef, mClassStudentsRef, mStudentClassesRef,
            mClassEventsRef, mStudentGradesRef, mStudentAbsencesRef, mUserParentsRef, mUserChildrenRef,
            mDeviceTokensRef, mSettingsRef, mFirstTimeRef, mNotificationsRef, mOnlineUsersRef, mLastSeenRef,
            mMessagesRef, mConversationsRef, mClassScheduleRef;
    public String mCurrentUserId;
    private Context mContext;

    public FirebaseUtils(Context context) {
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();
        mUsersRef = mRootRef.child("users");
        mUserAccountSettingsRef = mRootRef.child("userAccountSettings");
        mContactsRef = mRootRef.child("contacts");
        mRequestsRef = mRootRef.child("requests");
        mClassesRef = mRootRef.child("classes");
        mUserClassesRef = mRootRef.child("userClasses");
        mClassTokensRef = mRootRef.child("classTokens");
        mClassCoursesRef = mRootRef.child("classCourses");
        mClassStudentsRef = mRootRef.child("classStudents");
        mStudentClassesRef = mRootRef.child("studentClasses");
        mClassEventsRef = mRootRef.child("classEvents");
        mStudentGradesRef = mRootRef.child("studentGrades");
        mStudentAbsencesRef = mRootRef.child("studentAbsences");
        mClassScheduleRef = mRootRef.child("classSchedule");
        mUserParentsRef = mRootRef.child("userParents");
        mUserChildrenRef = mRootRef.child("userChildren");
        mDeviceTokensRef = mRootRef.child("deviceTokens");
        mSettingsRef = mRootRef.child("settings");
        mFirstTimeRef = mRootRef.child("firstTime");
        mNotificationsRef = mRootRef.child("notifications");
        mOnlineUsersRef = mRootRef.child("onlineUsers");
        mLastSeenRef = mRootRef.child("lastSeen");
        mMessagesRef = mRootRef.child("messages");
        mConversationsRef = mRootRef.child("conversations");

        mCurrentUserId = null;
        if (mAuth.getCurrentUser() != null) {
            mCurrentUserId = mAuth.getCurrentUser().getUid();
        }
    }

    /**
     * Adds data to Firebase for the user with ID user.getId().
     *
     * @param user
     * @param accountSettings
     */
    public void addUserInfo(User user, UserAccountSettings accountSettings) {
        mUsersRef.child(user.getId()).setValue(user);

        // user profile settings
        accountSettings.setId(user.getId());
        accountSettings.setEmail(user.getEmail());
        accountSettings.setUserType(user.getUserType());
        mUserAccountSettingsRef.child(user.getId()).setValue(accountSettings);

        // app settings
        Map<String, Object> settings = new HashMap<>();
        settings.put("email", true);
        settings.put("location", true);
        settings.put("phone", true);
        mSettingsRef.child(user.getId()).setValue(settings);

        // first time
        Map<String, Object> node = new HashMap<>();
        node.put(user.getId(), true);
        mFirstTimeRef.updateChildren(node);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public String getCurrentUserId() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        return null;
    }

    public void removeContact(String userId, String contactId) {
        // unmark as contact, parent and child
        mContactsRef.child(userId).child(contactId).setValue(null);
        mContactsRef.child(contactId).child(userId).setValue(null);
        mUserParentsRef.child(userId).child(contactId).setValue(null);
        mUserParentsRef.child(contactId).child(userId).setValue(null);
        mUserChildrenRef.child(userId).child(contactId).setValue(null);
        mUserChildrenRef.child(contactId).child(userId).setValue(null);
    }

    public void removeStudentFromClass(String studentId, String classId) {
        Map<String, Object> removeMap = new HashMap<>();

        // remove from classStudents
        removeMap.put("/classStudents/" + classId + "/" + studentId + "/", null);

        // remove from studentAbsences
        removeMap.put("/studentAbsences/" + classId + "/" + studentId + "/", null);

        // remove from studentGrades
        removeMap.put("/studentAbsences/" + classId + "/" + studentId + "/", null);

        // remove from userClasses
        removeMap.put("/userClasses/" + studentId + "/" + classId + "/", null);

        mRootRef.updateChildren(removeMap);

    }

    public void confirmContactRequest(String fromUserId, String requestType) {
        // delete request from database
        mRequestsRef.child(mCurrentUserId).child(fromUserId).removeValue();

        // add each as a contact for the other
        Map<String, Object> contact = new HashMap<>();
        contact.put(fromUserId, fromUserId);
        mContactsRef.child(mCurrentUserId).updateChildren(contact);

        contact = new HashMap<>();
        contact.put(mCurrentUserId, mCurrentUserId);
        mContactsRef.child(fromUserId).updateChildren(contact);

        // if parent wants to add current user as child
        if (requestType.equals("child")) {
            contact = new HashMap<>();
            contact.put(fromUserId, fromUserId);
            mUserParentsRef.child(mCurrentUserId).updateChildren(contact);

            contact = new HashMap<>();
            contact.put(mCurrentUserId, mCurrentUserId);
            mUserChildrenRef.child(fromUserId).updateChildren(contact);
        }

        // if parent wants to add current user as parent
        if (requestType.equals("parent")) {
            contact = new HashMap<>();
            contact.put(fromUserId, fromUserId);
            mUserChildrenRef.child(mCurrentUserId).updateChildren(contact);

            contact = new HashMap<>();
            contact.put(mCurrentUserId, mCurrentUserId);
            mUserParentsRef.child(fromUserId).updateChildren(contact);
        }
    }

    public void declineContactRequest(String fromUserId) {
        // delete request from database
        mRequestsRef.child(mCurrentUserId).child(fromUserId).removeValue();
    }

    public void cancelRequestTo(String userId) {
        Map<String, Object> remove = new HashMap<>();
        remove.put(mCurrentUserId, null);
        mRequestsRef.child(userId).updateChildren(remove);
    }

    /**
     * Update "userAccountSettings" node for current user.
     *
     * @param lastName
     * @param firstName
     * @param description
     * @param location
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String lastName, String firstName, String description,
                                          String location, String phoneNumber, String profilePhoto,
                                          String displayName) {
        DatabaseReference ref = mUserAccountSettingsRef.child(getCurrentUserId());

        if (lastName != null) {
            ref.child(mContext.getString(R.string.field_last_name)).setValue(lastName);
        }
        if (firstName != null) {
            ref.child(mContext.getString(R.string.field_first_name)).setValue(firstName);
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
        if (displayName != null) {
            ref.child(mContext.getString(R.string.field_display_name)).setValue(displayName);
        }

    }

    public String getCurrentUserType() {
        return mContext.getSharedPreferences("LoginInfo", 0).getString("userType", "none");
    }
}

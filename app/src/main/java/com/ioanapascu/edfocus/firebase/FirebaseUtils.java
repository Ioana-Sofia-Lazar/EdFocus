package com.ioanapascu.edfocus.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ioana Pascu on 6/17/2018.
 */

public class FirebaseUtils {
    public FirebaseAuth mAuth;
    public FirebaseDatabase mFirebaseDatabase;
    public DatabaseReference mRootRef, mUsersRef, mUserAccountSettingsRef, mContactsRef, mRequestsRef, mClassesRef,
            mUserClassesRef, mClassTokensRef, mClassCoursesRef, mClassStudentsRef, mStudentClassesRef,
            mClassEventsRef, mStudentGradesRef, mStudentAbsencesRef, mUserParentsRef, mUserChildrenRef,
            mDeviceTokensRef, mSettingsRef, mFirstTimeRef, mNotificationsRef, mOnlineUsersRef, mLastSeenRef,
            mMessagesRef, mConversationsRef;

    public FirebaseUtils() {
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
    }

    public String getCurrentUserId() {
        return mAuth.getCurrentUser().getUid();
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
}

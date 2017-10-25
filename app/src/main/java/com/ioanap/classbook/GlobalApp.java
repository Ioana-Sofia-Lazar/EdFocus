package com.ioanap.classbook;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.model.User;

public class GlobalApp extends Application {

    private static GlobalApp singleton;

    static FirebaseAuth firebaseAuth;
    static DatabaseReference mRootRef;
    static DatabaseReference mUserRef;

    public static GlobalApp getInstance() {
        return singleton;
    }

    /**
     * Redirect User according to its type: Teacher, Parent or Child.
     */
    public void userRedirect() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        mUserRef.child(currentUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String userType = user.getUserType();

                        Intent intent;

                        if (userType.equals("teacher")) {
                            intent = new Intent(getApplicationContext(), com.ioanap.classbook.teacher.ProfileActivity.class);
                            startActivity(intent);
                        } else if (userType.equals("parent")) {
                            intent = new Intent(getApplicationContext(), com.ioanap.classbook.parent.ProfileActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(getApplicationContext(), com.ioanap.classbook.child.ProfileActivity.class);
                            startActivity(intent);
                        }

                        Log.i("redirecting as", userType);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("error", "failed to redirect user");
                    }
                });

    }

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("users");

        singleton = this;
    }
}

package com.ioanap.classbook.shared;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Notification;
import com.ioanap.classbook.utils.NotificationsListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioana on 2/23/2018.
 */

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";

    // widgets
    RecyclerView mNotificationsRecycler;

    // variables
    NotificationsListAdapter mAdapter;
    List<Notification> mNotifications;
    DatabaseReference mNotificationsRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNotificationsRecycler = view.findViewById(R.id.recycler_notifications);

        mNotifications = new ArrayList<>();
        mNotificationsRef = FirebaseDatabase.getInstance().getReference().child("notifications");

        mAdapter = new NotificationsListAdapter(getContext(), mNotifications);
        mNotificationsRecycler.setAdapter(mAdapter);
        mNotificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        displayNotifications();

    }

    private void displayNotifications() {
        String userId = ((BaseActivity) getContext()).getCurrentUserId();

        mNotificationsRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mNotifications.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Notification notification = data.getValue(Notification.class);
                    mNotifications.add(notification);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

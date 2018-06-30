package com.ioanapascu.edfocus.shared;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Notification;
import com.ioanapascu.edfocus.others.NotificationsListAdapter;
import com.ioanapascu.edfocus.utils.FirebaseUtils;

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
    FirebaseUtils firebase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebase = new FirebaseUtils(getContext());
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

        mAdapter = new NotificationsListAdapter(getContext(), mNotifications);
        mNotificationsRecycler.setAdapter(mAdapter);
        mNotificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mNotificationsRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        displayNotifications();

    }

    private void displayNotifications() {
        String userId = firebase.getCurrentUserId();

        firebase.mNotificationsRef.child(userId).orderByChild("compareValue").addValueEventListener(new ValueEventListener() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // mark notifications as seen
        String userId = firebase.getCurrentUserId();

        firebase.mNotificationsRef.child(userId).orderByChild("seen").equalTo(false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            data.getRef().child("seen").setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}

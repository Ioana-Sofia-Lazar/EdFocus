package com.ioanap.classbook.parent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Child;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.ChildrenListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ioana Pascu on 4/27/2018.
 */

public class ChildrenFragment extends Fragment {

    private static final String TAG = "ChildrenFragment";

    // widgets
    private ListView mChildrenListView;

    // variables
    private ArrayList<Child> mChildren;
    private ArrayList<String> mChildrenIds;
    private ChildrenListAdapter mAdapter;
    private RelativeLayout mNoChildrenLayout;

    // db references
    private DatabaseReference mUserClassesRef, mUserChildrenRef, mSettingsRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserClassesRef = FirebaseDatabase.getInstance().getReference().child("userClasses");
        mUserChildrenRef = FirebaseDatabase.getInstance().getReference().child("userChildren");
        mSettingsRef = FirebaseDatabase.getInstance().getReference().child("userAccountSettings");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_children, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChildren = new ArrayList<>();
        mChildrenListView = view.findViewById(R.id.list_children);
        mNoChildrenLayout = view.findViewById(R.id.layout_no_children);

        mAdapter = new ChildrenListAdapter(getContext(), R.layout.row_child, mChildren);
        mChildrenListView.setAdapter(mAdapter);

        displayChildren();
    }

    private void displayChildren() {
        String currentUserId = ((BaseActivity) getActivity()).getCurrentUserId();

        mUserChildrenRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, String.valueOf(dataSnapshot));
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.exists()) return;
                    final String childId = data.getValue().toString();

                    // get child info
                    mSettingsRef.child(childId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);
                            final Child child = new Child(settings.getId(), settings.getFirstName() + " " +
                                    settings.getFirstName(), settings.getProfilePhoto(), new ArrayList<String>());

                            // get child classes
                            mUserClassesRef.child(childId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        child.setClassIds(new ArrayList<String>());
                                        mChildren.add(child);
                                        mAdapter.notifyDataSetChanged();
                                        return;
                                    }

                                    // hide empty state layout
                                    mNoChildrenLayout.setVisibility(View.GONE);

                                    // add classes ids list for this child
                                    List<String> classes = new ArrayList<>();
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        String classId = data.getValue().toString();

                                        classes.add(classId);
                                    }
                                    child.setClassIds(classes);
                                    mChildren.add(child);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}

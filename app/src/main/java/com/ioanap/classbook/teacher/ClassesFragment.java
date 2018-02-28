package com.ioanap.classbook.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Class;
import com.ioanap.classbook.utils.ClassesListAdapter;

import java.util.ArrayList;

/**
 * Created by ioana on 2/23/2018.
 */

public class ClassesFragment extends Fragment implements View.OnClickListener {

    //widgets
    private ListView mClassesListView;

    // variables
    private ArrayList<Class> mClasses;
    private ClassesListAdapter mClassesListAdapter;
    private RelativeLayout mNoClassesLayout;
    private FloatingActionButton mAddClassFab;

    // db reference
    private DatabaseReference mClassesRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClassesRef = FirebaseDatabase.getInstance().getReference().child("classes");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mClasses = new ArrayList<>();

        // widgets
        mClassesListView = (ListView) view.findViewById(R.id.list_classes);
        mNoClassesLayout = (RelativeLayout) view.findViewById(R.id.layout_no_classes);
        mAddClassFab = (FloatingActionButton) view.findViewById(R.id.fab_add_class);

        mClassesListAdapter = new ClassesListAdapter(getContext(), R.layout.row_class, mClasses);
        mClassesListView.setAdapter(mClassesListAdapter);

        // listeners
        mClassesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // go to page of class that was tapped
                String classId = mClasses.get(position).getId();

                Intent myIntent = new Intent(getContext(), ClassActivity.class);
                myIntent.putExtra("classId", classId);
                startActivity(myIntent);
            }
        });
        mAddClassFab.setOnClickListener(this);

        displayClasses();
    }

    private void displayClasses() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mClassesRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mClasses.clear();

                if (dataSnapshot.getChildrenCount() > 0) {
                    mNoClassesLayout.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Class aClass = data.getValue(Class.class);
                        mClasses.add(aClass);

                        mClassesListAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mAddClassFab) {
            startActivity(new Intent(getContext(), AddClassActivity.class));
        }
    }
}

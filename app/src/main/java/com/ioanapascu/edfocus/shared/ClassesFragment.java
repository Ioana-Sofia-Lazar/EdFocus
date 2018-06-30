package com.ioanapascu.edfocus.shared;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Class;
import com.ioanapascu.edfocus.others.ClassesListAdapter;
import com.ioanapascu.edfocus.student.ClassActivity_s;
import com.ioanapascu.edfocus.teacher.AddClassActivity;
import com.ioanapascu.edfocus.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ioana on 2/23/2018.
 */

public class ClassesFragment extends Fragment implements View.OnClickListener {

    // widgets
    private ListView mClassesListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // variables
    private ArrayList<Class> mClasses;
    private ClassesListAdapter mClassesListAdapter;
    private RelativeLayout mNoClassesLayout;
    private FloatingActionButton mAddClassFab;
    private String mUserType, mUserId;
    private FirebaseUtils firebase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebase = new FirebaseUtils(getContext());

        mUserType = firebase.getCurrentUserType();
        mUserId = firebase.getCurrentUserId();
    }

    /**
     * Hide and show widgets according to current user type
     */
    private void hideShowWidgets() {
        // only teacher can create a new class
        if (mUserType.equals("teacher")) {

        }
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
        mClassesListView = view.findViewById(R.id.list_classes);
        mNoClassesLayout = view.findViewById(R.id.layout_no_classes);
        mAddClassFab = view.findViewById(R.id.fab_add_class);

        hideShowWidgets();

        mClassesListAdapter = new ClassesListAdapter(getContext(), R.layout.row_class, mClasses);
        mClassesListView.setAdapter(mClassesListAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        // swipe to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayClasses();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // listeners
        mClassesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // go to page of class that was tapped
                String classId = mClasses.get(position).getId();

                // according to user type
                Intent myIntent = new Intent(getContext(), ClassActivity_s.class);
                if (mUserType.equals("teacher"))
                    myIntent = new Intent(getContext(), ClassActivity.class);

                myIntent.putExtra("classId", classId);
                startActivity(myIntent);
            }
        });
        mAddClassFab.setOnClickListener(this);

        displayClasses();
    }

    private void displayClasses() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebase.mUserClassesRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mClasses.clear();
                mClassesListAdapter.notifyDataSetChanged();

                if (dataSnapshot.getChildrenCount() > 0) {
                    mNoClassesLayout.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()) { // each class of this user
                        String classId = data.getKey();

                        firebase.mClassesRef.child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Class aClass = dataSnapshot.getValue(Class.class);
                                mClasses.add(aClass);

                                mClassesListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showEnrollToClassDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Add Course");
        dialog.setContentView(R.layout.dialog_enroll_to_class);

        // dialog widgets
        final EditText tokenText = dialog.findViewById(R.id.txt_token);
        Button enrollBtn = dialog.findViewById(R.id.btn_enroll);
        final TextView errorText = dialog.findViewById(R.id.txt_error);
        ImageView cancelImg = dialog.findViewById(R.id.img_cancel);

        // enroll button click
        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get token introduced by user
                String token = tokenText.getText().toString();

                // check if token exists in firebase
                firebase.mClassTokensRef.child(token).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            errorText.setVisibility(View.GONE);

                            // get classId associated with this token
                            String classId = dataSnapshot.child("classId").getValue().toString();

                            // enroll student to class
                            // add to userClasses
                            Map<String, Object> node = new HashMap<>();
                            node.put(classId, classId);
                            firebase.mUserClassesRef.child(mUserId).updateChildren(node);

                            // add to classStudents
                            node = new HashMap<>();
                            node.put(mUserId, mUserId);
                            firebase.mClassStudentsRef.child(classId).updateChildren(node);

                            dialog.dismiss();

                            Toast.makeText(getActivity(), "You have been successfully enrolled to the Class",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // token is not valid, show error message
                            errorText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public void onClick(View view) {
        if (view == mAddClassFab) {
            if (mUserType.equals("teacher")) {
                // teacher can add a new class
                startActivity(new Intent(getContext(), AddClassActivity.class));
            } else if (mUserType.equals("student")) {
                // student can enroll to a class
                showEnrollToClassDialog();
            }

        }
    }
}

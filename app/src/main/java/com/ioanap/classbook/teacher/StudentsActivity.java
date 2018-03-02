package com.ioanap.classbook.teacher;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.StudentsListAdapter;

import java.util.ArrayList;

public class StudentsActivity extends BaseActivity implements View.OnClickListener {

    // widgets
    ImageView mBackButton;
    RecyclerView mStudentsRecycler;
    EditText mSearchEditText;

    // variables
    private ArrayList<Contact> mStudents;
    private StudentsListAdapter mStudentsListAdapter;
    private String mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        // get class id
        mClassId = getIntent().getStringExtra("classId");

        mStudents = new ArrayList<>();

        // widgets
        mBackButton = findViewById(R.id.img_back);
        mStudentsRecycler = findViewById(R.id.recycler_students);
        mSearchEditText = findViewById(R.id.text_search);

        // listeners
        mBackButton.setOnClickListener(this);
        // filter students according to text that teacher enters
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // filter list according to user input
                filterStudentsList(s.toString());
            }
        });

        mStudentsListAdapter = new StudentsListAdapter(StudentsActivity.this, mStudents);
        mStudentsRecycler.setAdapter(mStudentsListAdapter);
        mStudentsRecycler.setLayoutManager(new LinearLayoutManager(StudentsActivity.this));

        displayStudents();
    }

    private void displayStudents() {
        mClassStudentsRef.child(mClassId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mStudents.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String contactId = data.getValue(String.class);

                    // for this student (user) id get info to display in the list
                    showStudentData(contactId);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void filterStudentsList(String text) {
        ArrayList<Contact> temp = new ArrayList();
        text = text.toLowerCase();

        for (Contact student : mStudents) {
            if (student.getName().toLowerCase().contains(text)) {
                temp.add(student);
            }
        }

        //update recycler view
        mStudentsListAdapter.updateLists(temp);
    }

    private void showStudentData(final String id) {
        mSettingsRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);

                Contact contact = new Contact();
                contact.setId(id);
                contact.setName(settings.getDisplayName());
                contact.setEmail(settings.getEmail());
                contact.setProfilePhoto(settings.getProfilePhoto());
                contact.setUserType(settings.getUserType());

                mStudents.add(contact);
                mStudentsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mBackButton) {
            finish();
        }
    }
}

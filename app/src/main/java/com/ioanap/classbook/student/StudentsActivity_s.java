package com.ioanap.classbook.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.shared.ViewProfileActivity;
import com.ioanap.classbook.utils.StudentsListAdapter_s;

import java.util.ArrayList;

public class StudentsActivity_s extends BaseActivity implements View.OnClickListener {

    // widgets
    private ListView mStudentsRecycler;
    private EditText mSearchEditText;
    private FloatingActionButton mAddStudentFab;

    // variables
    private ArrayList<Contact> mStudents;
    private StudentsListAdapter_s mStudentsListAdapter;
    private String mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(StudentsActivity_s.this, false);
        setContentView(R.layout.activity_students);

        // get class id
        mClassId = getIntent().getStringExtra("classId");

        mStudents = new ArrayList<>();

        // widgets
        mStudentsRecycler = findViewById(R.id.recycler_students);
        mSearchEditText = findViewById(R.id.text_search);
        mAddStudentFab = findViewById(R.id.fab_add_student);

        mStudentsListAdapter = new StudentsListAdapter_s(StudentsActivity_s.this,
                R.layout.row_student_s, mStudents, mClassId);
        mStudentsRecycler.setAdapter(mStudentsListAdapter);

        displayStudents();

        setupListeners();
    }

    public void setupListeners() {
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

        // list item click and long click events
        mStudentsRecycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clicking a student row redirects to his profile
                Intent myIntent = new Intent(StudentsActivity_s.this, ViewProfileActivity.class);
                myIntent.putExtra("userId", mStudents.get(position).getId());
                startActivity(myIntent);
            }
        });
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
        mUserAccountSettingsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public String getClassId() {
        return mClassId;
    }

    @Override
    public void onClick(View view) {
    }
}

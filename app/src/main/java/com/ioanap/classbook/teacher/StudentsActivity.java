package com.ioanap.classbook.teacher;

import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.ActionModeCallback;
import com.ioanap.classbook.utils.StudentsListAdapter;

import java.util.ArrayList;

public class StudentsActivity extends BaseActivity implements View.OnClickListener {

    // widgets
    ImageView mBackButton;
    ListView mStudentsRecycler;
    EditText mSearchEditText;

    // variables
    private ArrayList<Contact> mStudents;
    private StudentsListAdapter mStudentsListAdapter;
    private String mClassId;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        mActionMode = null;

        // get class id
        mClassId = getIntent().getStringExtra("classId");

        mStudents = new ArrayList<>();

        // widgets
        mBackButton = findViewById(R.id.img_back);
        mStudentsRecycler = findViewById(R.id.recycler_students);
        mSearchEditText = findViewById(R.id.text_search);

        mStudentsListAdapter = new StudentsListAdapter(StudentsActivity.this,
                R.layout.row_student, mStudents);
        mStudentsRecycler.setAdapter(mStudentsListAdapter);

        setupListeners();

        displayStudents();
    }

    public void setupListeners() {
        // toolbar back button
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

        // list item click and long click events
        mStudentsRecycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clicking item in select mode
                if (mActionMode != null) {
                    onListItemSelect(position);
                }
            }
        });
        mStudentsRecycler.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });
    }

    // selecting list item according to the current state of the activity
    private void onListItemSelect(int position) {
        // toggle item selection
        mStudentsListAdapter.toggleSelection(position);
        boolean hasSelectedItems = mStudentsListAdapter.getSelectedCount() > 0;
        if (hasSelectedItems && mActionMode == null) {
            // an item has just been selected, start the actionMode
            mActionMode = this.startSupportActionMode(new ActionModeCallback
                    (StudentsActivity.this, mStudentsListAdapter, mStudents));
        } else if (!hasSelectedItems && mActionMode != null) {
            // there are no selected items anymore, exit the action mode
            mActionMode.finish();
        }
        // set action mode title
        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(mStudentsListAdapter.getSelectedCount())
                    + " students selected");
        }
    }

    public void setNullActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
        }
    }

    /*
        // Delete selected rows
        public void deleteRows() {
            SparseBooleanArray selected = mStudentsListAdapter
                    .getSelectedIds();//Get selected ids
            //Loop all selected ids
            for (int i = (selected.size() - 1); i >= 0; i--) {
                if (selected.valueAt(i)) {
                    //If current id is selected remove the item via key
                    mStudents.remove(selected.keyAt(i));
                    mStudentsListAdapter.notifyDataSetChanged();//notify adapter

                }
            }
            Toast.makeText(getApplicationContext(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
            mActionMode.finish();//Finish action mode after use
        }
    */
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

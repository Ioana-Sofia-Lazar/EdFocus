package com.ioanap.classbook.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Person;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.PeopleListAdapter;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SearchActivity";

    // widgets
    private ImageView mBackButton, mClearText;
    private ListView mPeopleList;
    private EditText mSearchEditText;

    // variables
    private ArrayList<Person> mPeople;
    private PeopleListAdapter mPeopleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mPeople = new ArrayList<>();

        mBackButton = findViewById(R.id.image_back);
        mClearText = findViewById(R.id.image_clear);
        mPeopleList = findViewById(R.id.list_people);
        mSearchEditText = findViewById(R.id.edit_text_search);

        mBackButton.setOnClickListener(this);
        mClearText.setOnClickListener(this);

        setupSearchEditText();
        setupPeopleListView();
    }

    private void setupSearchEditText() {

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchForMatch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchForMatch(String keyword) {
        mPeople.clear();

        if (keyword.length() == 0) {
            return;
        }

        Query query = mSettingsRef.orderByChild(getString(R.string.field_display_name)).equalTo(keyword);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot match : dataSnapshot.getChildren()) {
                    UserAccountSettings settings = match.getValue(UserAccountSettings.class);

                    Person person = new Person(settings.getId(), settings.getDisplayName(),
                            settings.getUserType(), settings.getProfilePhoto());
                    mPeople.add(person);
                }

                // update people list view
                mPeopleListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupPeopleListView() {
        mPeopleListAdapter = new PeopleListAdapter(SearchActivity.this, R.layout.row_person_item, mPeople);

        mPeopleList.setAdapter(mPeopleListAdapter);

        mPeopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tappedUserId = mPeople.get(position).getId();

                // display tapped person's profile
                Intent myIntent = new Intent(getApplicationContext(), ViewTeacherProfileActivity.class);
                myIntent.putExtra("userId", tappedUserId);
                startActivity(myIntent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mBackButton) {
            finish();
        }
        if (view == mClearText) {
            // clear search box text
            mSearchEditText.setText("");
        }
    }
}

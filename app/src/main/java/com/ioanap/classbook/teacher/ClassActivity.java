package com.ioanap.classbook.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Class;
import com.ioanap.classbook.utils.UniversalImageLoader;

public class ClassActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ClassActivity";

    // variables
    private String mClassId;

    // widgets
    private CardView mCoursesCard, mScheduleCard, mStudentsCard;
    private TextView mClassNameText, mSchoolText;
    private ImageView mClassPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(ClassActivity.this, false);
        setContentView(R.layout.activity_class);

        // get id of class to display
        Intent myIntent = getIntent();
        mClassId = myIntent.getStringExtra("classId");

        // widgets
        mCoursesCard = findViewById(R.id.card_courses);
        mScheduleCard = findViewById(R.id.card_schedule);
        mStudentsCard = findViewById(R.id.card_students);
        mClassNameText = findViewById(R.id.txt_class_name);
        mSchoolText = findViewById(R.id.txt_school);
        mClassPhoto = findViewById(R.id.img_class_photo);

        // display class info from firebase
        displayClassInfo();

        // listeners
        mCoursesCard.setOnClickListener(this);
        mScheduleCard.setOnClickListener(this);
        mStudentsCard.setOnClickListener(this);
    }

    private void displayClassInfo() {
        mClassesRef.child(userID).child(mClassId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Class aClass = dataSnapshot.getValue(Class.class);

                mClassNameText.setText(aClass.getName());
                mSchoolText.setText(aClass.getSchool());
                UniversalImageLoader.setImage(aClass.getPhoto(), mClassPhoto, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mCoursesCard) {
            Intent myIntent = new Intent(getApplicationContext(), CoursesActivity.class);
            myIntent.putExtra("classId", mClassId);
            startActivity(myIntent);
        }
        if (view == mScheduleCard) {
            Intent myIntent = new Intent(getApplicationContext(), ScheduleActivity.class);
            myIntent.putExtra("classId", mClassId);
            startActivity(myIntent);
        }
        if (view == mStudentsCard) {
            Intent myIntent = new Intent(getApplicationContext(), StudentsActivity.class);
            myIntent.putExtra("classId", mClassId);
            startActivity(myIntent);
        }
    }
}

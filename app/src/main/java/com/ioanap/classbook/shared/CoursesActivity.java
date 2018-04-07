package com.ioanap.classbook.shared;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Course;
import com.ioanap.classbook.utils.CoursesListAdapter;

import java.util.ArrayList;

public class CoursesActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CoursesActivity";

    // widgets
    FloatingActionButton mAddCourseFab;
    ListView mCoursesListView;
    RelativeLayout mNoCoursesLayout;

    // variables
    private String mClassId, mUserType;
    private ArrayList<Course> mCourses;
    private CoursesListAdapter mCoursesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(CoursesActivity.this, false);
        setContentView(R.layout.activity_courses);

        mCourses = new ArrayList<>();
        mUserType = getCurrentUserType();

        // get id of class to display courses for
        Intent myIntent = getIntent();
        mClassId = myIntent.getStringExtra("classId");

        // widgets
        mAddCourseFab = findViewById(R.id.fab_add_course);
        mCoursesListView = findViewById(R.id.list_courses);
        mNoCoursesLayout = findViewById(R.id.layout_no_courses);

        mCoursesListAdapter = new CoursesListAdapter(CoursesActivity.this, R.layout.row_course,
                mCourses, mClassId, mUserType);
        mCoursesListView.setAdapter(mCoursesListAdapter);

        // listeners
        mAddCourseFab.setOnClickListener(this);

        // only teacher can add and edit courses
        if (mUserType.equals("teacher")) {
            mCoursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // clicking on a course opens dialog for editing it
                    showEditDialog(position);
                }
            });
            mAddCourseFab.setVisibility(View.VISIBLE);
        }

        displayCourses();
    }

    private void displayCourses() {
        mClassCoursesRef.child(mClassId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCourses.clear();

                if (dataSnapshot.getChildrenCount() > 0) {
                    mNoCoursesLayout.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Course course = data.getValue(Course.class);
                        mCourses.add(course);
                    }
                } else {
                    mNoCoursesLayout.setVisibility(View.VISIBLE);
                }

                mCoursesListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showEditDialog(int position) {
        final Course course = mCourses.get(position);

        final Dialog dialog = new Dialog(CoursesActivity.this);
        dialog.setContentView(R.layout.dialog_add_course);

        // dialog widgets
        final TextView titleTest = dialog.findViewById(R.id.txt_title);
        final EditText nameText = dialog.findViewById(R.id.txt_name);
        final EditText teacherText = dialog.findViewById(R.id.txt_teacher);
        final EditText descriptionText = dialog.findViewById(R.id.txt_description);
        Button createBtn = dialog.findViewById(R.id.btn_create);
        ImageView cancelImg = dialog.findViewById(R.id.img_cancel);

        titleTest.setText("Edit Course Info");
        createBtn.setText("Save");

        // set old values
        nameText.setText(course.getName());
        teacherText.setText(course.getTeacher());
        descriptionText.setText(course.getDescription());

        // create course button click
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                String name = nameText.getText().toString();
                String teacher = teacherText.getText().toString();
                String description = descriptionText.getText().toString();

                // save info at course id in firebase
                Course newCourse = new Course(course.getId(), name, teacher, description);

                // save to firebase
                mClassCoursesRef.child(mClassId).child(course.getId()).setValue(newCourse);
                dialog.dismiss();
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

    private void showAddDialog() {
        final Dialog dialog = new Dialog(CoursesActivity.this);
        dialog.setTitle("Add Course");
        dialog.setContentView(R.layout.dialog_add_course);

        // dialog widgets
        final EditText nameText = dialog.findViewById(R.id.txt_name);
        final EditText teacherText = dialog.findViewById(R.id.txt_teacher);
        final EditText descriptionText = dialog.findViewById(R.id.txt_description);
        Button createBtn = dialog.findViewById(R.id.btn_create);
        ImageView cancelImg = dialog.findViewById(R.id.img_cancel);

        // create course button click
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                String name = nameText.getText().toString();
                String teacher = teacherText.getText().toString();
                String description = descriptionText.getText().toString();

                // get id where to put the new course in firebase
                String courseId = mClassCoursesRef.child(CURRENT_USER_ID).push().getKey();
                Course course = new Course(courseId, name, teacher, description);

                // save to firebase
                mClassCoursesRef.child(mClassId).child(courseId).setValue(course);
                dialog.dismiss();
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
        if (view == mAddCourseFab) {
            showAddDialog();
        }
    }
}

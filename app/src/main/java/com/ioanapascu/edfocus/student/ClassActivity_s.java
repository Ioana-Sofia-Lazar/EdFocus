package com.ioanapascu.edfocus.student;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Class;
import com.ioanapascu.edfocus.model.UserAccountSettings;
import com.ioanapascu.edfocus.shared.CoursesActivity;
import com.ioanapascu.edfocus.shared.EventsActivity;
import com.ioanapascu.edfocus.shared.ViewProfileActivity;
import com.ioanapascu.edfocus.teacher.FilesActivity;
import com.ioanapascu.edfocus.teacher.ScheduleActivity;
import com.ioanapascu.edfocus.teacher.StudentActivity;
import com.ioanapascu.edfocus.utils.UniversalImageLoader;

public class ClassActivity_s extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ClassActivity";

    // variables
    private String mClassId, mStudentId;
    private ValueEventListener mClassInfoListener;

    // widgets
    private CardView mCoursesCard, mScheduleCard, mStudentsCard, mEventsCard, mActivityCard, mFilesCard;
    private TextView mClassNameText, mSchoolText;
    private ImageView mClassPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(ClassActivity_s.this, false);
        setContentView(R.layout.activity_class_s);

        // get id of class to display
        Intent myIntent = getIntent();
        mClassId = myIntent.getStringExtra("classId");
        mStudentId = myIntent.getStringExtra("studentId");

        // widgets
        mCoursesCard = findViewById(R.id.card_courses);
        mScheduleCard = findViewById(R.id.card_schedule);
        mStudentsCard = findViewById(R.id.card_students);
        mEventsCard = findViewById(R.id.card_events);
        mActivityCard = findViewById(R.id.card_activity);
        mFilesCard = findViewById(R.id.card_files);
        mClassNameText = findViewById(R.id.text_class_name);
        mSchoolText = findViewById(R.id.txt_school);
        mClassPhoto = findViewById(R.id.img_class_photo);

        // display class info from firebase
        displayClassInfo();

        // listeners
        mCoursesCard.setOnClickListener(this);
        mScheduleCard.setOnClickListener(this);
        mStudentsCard.setOnClickListener(this);
        mEventsCard.setOnClickListener(this);
        mActivityCard.setOnClickListener(this);
        mFilesCard.setOnClickListener(this);
        mClassNameText.setOnClickListener(this);
    }

    private void displayClassInfo() {
        mClassInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Class aClass = dataSnapshot.getValue(Class.class);

                if (aClass == null) {
                    ClassActivity_s.this.finish();
                    return;
                }

                mClassNameText.setText(aClass.getName());
                mSchoolText.setText(aClass.getSchool());
                UniversalImageLoader.setImage(aClass.getPhoto(), mClassPhoto, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mClassesRef.child(mClassId).addValueEventListener(mClassInfoListener);
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
            Intent myIntent = new Intent(getApplicationContext(), StudentsActivity_s.class);
            myIntent.putExtra("classId", mClassId);
            startActivity(myIntent);
        }
        if (view == mEventsCard) {
            Intent myIntent = new Intent(getApplicationContext(), EventsActivity.class);
            myIntent.putExtra("classId", mClassId);
            startActivity(myIntent);
        }
        if (view == mActivityCard) {
            Intent myIntent = new Intent(getApplicationContext(), StudentActivity.class);
            if (mStudentId != null) { // parent is looking at child's activity
                myIntent.putExtra("studentId", mStudentId);
            } else { // student is looking at own activity
                myIntent.putExtra("studentId", firebase.getCurrentUserId());
            }
            myIntent.putExtra("classId", mClassId);
            startActivity(myIntent);
        }
        if (view == mFilesCard) {
            Intent myIntent = new Intent(getApplicationContext(), FilesActivity.class);
            myIntent.putExtra("classId", mClassId);
            startActivity(myIntent);
        }
        if (view == mClassNameText) {
            showInfoDialog();
        }
    }

    private void showInfoDialog() {
        final Dialog dialog = new Dialog(ClassActivity_s.this);
        dialog.setContentView(R.layout.dialog_class_info);

        // dialog widgets
        final ImageView mTeacherPhoto = dialog.findViewById(R.id.img_teacher_photo);
        final ImageView mClassPhoto = dialog.findViewById(R.id.img_class_photo);
        final TextView mClassName = dialog.findViewById(R.id.text_class_name);
        final TextView mSchool = dialog.findViewById(R.id.text_school);
        final TextView mTeacherName = dialog.findViewById(R.id.text_teacher_name);
        final TextView mDescription = dialog.findViewById(R.id.text_description);
        final TextView mToken = dialog.findViewById(R.id.text_token);
        final ImageView mCopyIcon = dialog.findViewById(R.id.img_copy_icon);

        mClassesRef.child(mClassId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Class aClass = dataSnapshot.getValue(Class.class);

                mClassName.setText(aClass.getName());
                mSchool.setText(aClass.getSchool());
                mDescription.setText(aClass.getDescription());
                mToken.setText(aClass.getToken());
                UniversalImageLoader.setImage(aClass.getPhoto(), mClassPhoto, null);

                // get teacher info
                mUserAccountSettingsRef.child(aClass.getTeacherId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);

                        mTeacherName.setText(String.format("%s %s", settings.getFirstName(),
                                settings.getLastName()));
                        UniversalImageLoader.setImage(settings.getProfilePhoto(), mTeacherPhoto, null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // click on teacher's name or photo redirects to his profile
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ClassActivity_s.this, ViewProfileActivity.class);
                        intent.putExtra("userId", aClass.getTeacherId());
                        startActivity(intent);
                    }
                };
                mTeacherName.setOnClickListener(listener);
                mTeacherPhoto.setOnClickListener(listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCopyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // copy token to clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("classToken", mToken.getText());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }

                // show message toast
                Toast.makeText(ClassActivity_s.this, "Token copied to Clipboard!", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClassesRef.child(mClassId).removeEventListener(mClassInfoListener);
    }
}

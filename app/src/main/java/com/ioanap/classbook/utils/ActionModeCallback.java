package com.ioanap.classbook.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
import com.ioanap.classbook.teacher.AddMultipleGradesActivity;
import com.ioanap.classbook.teacher.ScheduleActivity;
import com.ioanap.classbook.teacher.StudentsActivity;
import com.ioanap.classbook.views.NoCoursesDialog;

import java.util.ArrayList;

/**
 * Created by ioana on 3/14/2018.
 */

public class ActionModeCallback implements ActionMode.Callback {

    DatabaseReference mClassCoursesRef;
    private Context mContext;
    private StudentsListAdapter mStudentsListAdapter;
    private ArrayList<Contact> mStudents;

    public ActionModeCallback(Context context, StudentsListAdapter studentsListAdapter, ArrayList<Contact> students) {
        this.mContext = context;
        this.mStudentsListAdapter = studentsListAdapter;
        this.mStudents = students;
        mClassCoursesRef = FirebaseDatabase.getInstance().getReference().child("classCourses");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // inflate menu
        mode.getMenuInflater().inflate(R.menu.menu_selected_students, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.option_remove).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_remove:
                ((StudentsActivity) mContext).removeStudents();
                return true;
            case R.id.option_add_grade:
                final String classId = ((StudentsActivity) mContext).getClassId();
                // if there are no courses for this class show error
                // otherwise activity to add grades
                mClassCoursesRef.child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            NoCoursesDialog dialog = new NoCoursesDialog((ScheduleActivity) mContext, classId,
                                    R.string.grades_no_courses_error);
                            dialog.show();
                        } else {
                            Intent intent = new Intent(mContext, AddMultipleGradesActivity.class);
                            intent.putStringArrayListExtra("selectedStudentsIds",
                                    ((StudentsActivity) mContext).getSelectedItemsIdsStrings());
                            intent.putExtra("classId", classId);
                            mContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            case R.id.option_mark_absent:
                //todo
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // when action mode is destroyed remove selection and set action mode to null
        mStudentsListAdapter.removeSelection();
        ((StudentsActivity) mContext).setNullActionMode();
    }
}
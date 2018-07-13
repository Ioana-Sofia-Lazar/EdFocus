package com.ioanapascu.edfocus.others;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.AbsenceDb;
import com.ioanapascu.edfocus.model.Contact;
import com.ioanapascu.edfocus.model.Course;
import com.ioanapascu.edfocus.model.ScheduleEntry;
import com.ioanapascu.edfocus.teacher.AddMultipleGradesActivity;
import com.ioanapascu.edfocus.teacher.ScheduleActivity;
import com.ioanapascu.edfocus.teacher.StudentsActivity;
import com.ioanapascu.edfocus.utils.FirebaseUtils;
import com.ioanapascu.edfocus.utils.Utils;
import com.ioanapascu.edfocus.views.NoCoursesDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by ioana on 3/14/2018.
 */

public class ActionModeCallback implements ActionMode.Callback {

    private Context mContext;
    private StudentsListAdapter mStudentsListAdapter;
    private ArrayList<Contact> mStudents;
    private ArrayList<String> mCourseNames, mCourseIds;
    private String mClassId;
    private FirebaseUtils firebase;

    public ActionModeCallback(Context context, StudentsListAdapter studentsListAdapter,
                              ArrayList<Contact> students, String classId) {
        this.mContext = context;
        this.mStudentsListAdapter = studentsListAdapter;
        this.mStudents = students;
        this.mClassId = classId;
        this.firebase = new FirebaseUtils(mContext);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // inflate menu
        mode.getMenuInflater().inflate(R.menu.menu_selected_students, menu);
        ((StudentsActivity) mContext).getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.cyan));

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
                // if there are no courses for this class show error
                // otherwise activity to add grades
                firebase.mClassCoursesRef.child(mClassId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            NoCoursesDialog dialog = new NoCoursesDialog((ScheduleActivity) mContext, mClassId,
                                    R.string.grades_no_courses_error);
                            dialog.show();
                        } else {
                            Intent intent = new Intent(mContext, AddMultipleGradesActivity.class);
                            intent.putStringArrayListExtra("selectedStudentsIds",
                                    ((StudentsActivity) mContext).getSelectedItemsIdsStrings());
                            intent.putExtra("classId", mClassId);
                            mContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            case R.id.option_mark_absent:
                // if there are no courses for this class show error
                // otherwise dialog to add absences
                firebase.mClassCoursesRef.child(mClassId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            NoCoursesDialog dialog = new NoCoursesDialog((ScheduleActivity) mContext, mClassId,
                                    R.string.absences_no_courses_error);
                            dialog.show();
                        } else {
                            showAddAbsencesDialog();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            default:
                return false;
        }
    }

    private void showAddAbsencesDialog() {
        final ArrayList<String> selectedStudentIds = ((StudentsActivity) mContext).getSelectedItemsIdsStrings();

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_add_absence);

        // dialog widgets
        final DatePicker datePicker = dialog.findViewById(R.id.date_picker);
        Button createBtn = dialog.findViewById(R.id.btn_create);
        ImageView cancelImg = dialog.findViewById(R.id.img_cancel);
        ImageView deleteImg = dialog.findViewById(R.id.btn_delete);
        final Spinner coursesSpinner = dialog.findViewById(R.id.spinner_courses);
        final CheckBox absentAllDayCB = dialog.findViewById(R.id.checkbox_absent_all_day);
        final CheckBox authorisedCB = dialog.findViewById(R.id.checkbox_authorised);
        TextView infoText = dialog.findViewById(R.id.text_authorised_info);
        TextView titleText = dialog.findViewById(R.id.text_title);

        authorisedCB.setVisibility(View.GONE);
        infoText.setVisibility(View.GONE);
        deleteImg.setVisibility(View.GONE);
        titleText.setText("Add Absences for Selected Students");

        populateSpinner(coursesSpinner);

        // add course button click
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                boolean absentAllDay = absentAllDayCB.isChecked();
                String courseId = mCourseIds.get(coursesSpinner.getSelectedItemPosition());
                Long date = Utils.yearMonthDayToMillis(datePicker.getYear(),
                        datePicker.getMonth(), datePicker.getDayOfMonth());

                // add absence for all selected students
                for (String studentId : selectedStudentIds) {
                    // get an array of the absences that need to be inserted
                    List<AbsenceDb> absences = new ArrayList<>();
                    if (absentAllDay) {
                        addAbsencesForAllDay(datePicker.getYear(), datePicker.getMonth(),
                                datePicker.getDayOfMonth(), studentId, date);
                    } else {
                        // add single absence for the selected course

                        // get id where to put the new absence in firebase
                        String absenceId = firebase.mStudentAbsencesRef.child(mClassId).child(studentId).push().getKey();
                        AbsenceDb absence = new AbsenceDb(absenceId, date, false, mClassId, courseId, studentId);

                        // save to firebase
                        firebase.mStudentAbsencesRef.child(mClassId).child(studentId).child(absenceId).setValue(absence);
                    }
                }

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

    private void addAbsencesForAllDay(int year, int month, int day,
                                      final String studentId, final Long date) {
        // find all courses from the day of the week that this date was
        final ArrayList<AbsenceDb> absences = new ArrayList<>();

        // get what day of the week the date was
        Date d = new GregorianCalendar(year, month, day).getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        String dayOfWeek = StudentsListAdapter.DAYS[c.get(Calendar.DAY_OF_WEEK)];

        // iterate all courses of this class for this day
        firebase.mClassScheduleRef.child(mClassId).child(dayOfWeek).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // create absence for each course in the schedule of that day
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ScheduleEntry entry = snapshot.getValue(ScheduleEntry.class);
                            String courseId = entry.getCourseId();

                            // get id where to put the new absence in firebase
                            String absenceId = firebase.mStudentAbsencesRef.child(mClassId).child(studentId)
                                    .push().getKey();
                            AbsenceDb absence = new AbsenceDb(absenceId, date, false, mClassId,
                                    courseId, studentId);

                            // save to firebase
                            firebase.mStudentAbsencesRef.child(mClassId).child(studentId).child(absenceId)
                                    .setValue(absence);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void populateSpinner(Spinner spinner) {
        mCourseNames = new ArrayList<>();
        mCourseIds = new ArrayList<>();

        // set adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, mCourseNames);

        // spinner row layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attach data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // get courses from firebase
        firebase.mClassCoursesRef.child(mClassId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCourseNames.clear();

                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Course course = data.getValue(Course.class);
                        mCourseNames.add(course.getName());
                        mCourseIds.add(course.getId());

                        dataAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        BaseActivity.setStatusBarGradient((StudentsActivity) mContext, false);
        // when action mode is destroyed remove selection and set action mode to null
        mStudentsListAdapter.removeSelection();
        ((StudentsActivity) mContext).setNullActionMode();
    }
}
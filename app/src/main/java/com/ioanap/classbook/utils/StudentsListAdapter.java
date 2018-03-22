package com.ioanap.classbook.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
import com.ioanap.classbook.model.Course;
import com.ioanap.classbook.model.GradeDb;
import com.ioanap.classbook.teacher.StudentsActivity;

import java.util.ArrayList;

public class StudentsListAdapter extends ArrayAdapter<Contact> implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "StudentsListAdapter";

    private ArrayList<Contact> mStudents;
    private Context mContext;
    private int mResource;
    private String mClassId;
    private SparseBooleanArray mSelectedItemsIds;
    private ArrayList<String> mCourseNames, mCourseIds;
    private DatabaseReference mClassCoursesRef, mStudentGradesRef;
    private int mPosition;

    public StudentsListAdapter(Context context, int resource, ArrayList<Contact> objects, String classId) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mStudents = objects;
        mSelectedItemsIds = new SparseBooleanArray();
        mClassId = classId;
        mClassCoursesRef = FirebaseDatabase.getInstance().getReference().child("classCourses");
        mStudentGradesRef = FirebaseDatabase.getInstance().getReference().child("studentGrades");
    }

    @Override
    public int getCount() {
        return mStudents.size();
    }

    @Nullable
    @Override
    public Contact getItem(int position) {
        return mStudents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // get student information
        String id = getItem(position).getId();
        String name = getItem(position).getName();
        String email = getItem(position).getEmail();
        String profilePhoto = getItem(position).getProfilePhoto();
        String userType = getItem(position).getUserType();

        // create the contact object with student's information
        Contact student = new Contact(id, name, email, profilePhoto, userType);

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mName = convertView.findViewById(R.id.text_name);
            holder.mStudentOptions = convertView.findViewById(R.id.text_student_options);
            holder.mProfilePhoto = convertView.findViewById(R.id.image_profile_photo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mName.setText(student.getName());
        UniversalImageLoader.setImage(student.getProfilePhoto(), holder.mProfilePhoto, null);
        holder.mStudentOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = position;
                showPopupMenu(v);
            }
        });

        // when in selection action mode, hide individual options menu for student
        if (((StudentsActivity) mContext).getActionMode() != null) {
            holder.mStudentOptions.setVisibility(View.GONE);
        } else {
            holder.mStudentOptions.setVisibility(View.VISIBLE);
        }

        // change style of the selected items in list view
        convertView
                .setBackgroundColor(mSelectedItemsIds.get(position) ?
                        mContext.getResources().getColor(R.color.colorAccent) : Color.TRANSPARENT);

        return convertView;
    }

    public void updateLists(ArrayList<Contact> students) {
        mStudents = students;
        notifyDataSetChanged();
    }

    // menu options for each student
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_student_options);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_add_grade:
                showAddGradeDialog();
                return true;
            case R.id.option_mark_absent:
                //todo
                return true;
            case R.id.option_view_profile:
                //todo
                return true;
            case R.id.option_remove:
                //todo
                return true;
            default:
                return false;
        }
    }

    private void showAddGradeDialog() {
        final String studentId = getItem(mPosition).getId();
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_grade);

        // dialog widgets
        final EditText gradeText = dialog.findViewById(R.id.text_grade);
        final EditText descriptionText = dialog.findViewById(R.id.text_description);
        final EditText nameText = dialog.findViewById(R.id.text_name);
        final DatePicker datePicker = dialog.findViewById(R.id.date_picker);
        Button createBtn = dialog.findViewById(R.id.btn_create);
        ImageView cancelImg = dialog.findViewById(R.id.img_cancel);
        final Spinner coursesSpinner = dialog.findViewById(R.id.spinner_courses);

        populateSpinner(coursesSpinner);

        // add course button click
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                String gradeVal = gradeText.getText().toString();
                String description = descriptionText.getText().toString();
                String name = nameText.getText().toString();
                String courseId = mCourseIds.get(coursesSpinner.getSelectedItemPosition());
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-"
                        + datePicker.getDayOfMonth();

                // get id where to put the new grade in firebase
                String gradeId = mStudentGradesRef.child(mClassId).child(studentId).push().getKey();
                GradeDb grade = new GradeDb(gradeId, name, gradeVal, date, description, mClassId,
                        courseId, studentId);

                // save to firebase
                mStudentGradesRef.child(mClassId).child(studentId).child(gradeId).setValue(grade);
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

    private void showAddAbsenceDialog() {
        //todo
        final String studentId = getItem(mPosition).getId();
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_grade);

        // dialog widgets
        final EditText gradeText = dialog.findViewById(R.id.text_grade);
        final EditText descriptionText = dialog.findViewById(R.id.text_description);
        final EditText nameText = dialog.findViewById(R.id.text_name);
        final DatePicker datePicker = dialog.findViewById(R.id.date_picker);
        Button createBtn = dialog.findViewById(R.id.btn_create);
        ImageView cancelImg = dialog.findViewById(R.id.img_cancel);
        final Spinner coursesSpinner = dialog.findViewById(R.id.spinner_courses);

        populateSpinner(coursesSpinner);

        // add course button click
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                String gradeVal = gradeText.getText().toString();
                String description = descriptionText.getText().toString();
                String name = nameText.getText().toString();
                String courseId = mCourseIds.get(coursesSpinner.getSelectedItemPosition());
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-"
                        + datePicker.getDayOfMonth();

                // get id where to put the new grade in firebase
                String gradeId = mStudentGradesRef.child(mClassId).child(studentId).push().getKey();
                GradeDb grade = new GradeDb(gradeId, name, gradeVal, date, description, mClassId,
                        courseId, studentId);

                // save to firebase
                mStudentGradesRef.child(mClassId).child(studentId).child(gradeId).setValue(grade);
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

    private void populateSpinner(Spinner spinner) {
        mCourseNames = new ArrayList<>();
        mCourseIds = new ArrayList<>();

        // set adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, mCourseNames);

        // spinner row layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attach data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // get courses from firebase
        mClassCoursesRef.child(mClassId).addValueEventListener(new ValueEventListener() {
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

    /***
     * Methods required for do selections, remove selections, etc.
     */

    // toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    // remove selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    // put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedItemsIds() {
        return mSelectedItemsIds;
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private static class ViewHolder {
        ImageView mProfilePhoto;
        TextView mName, mStudentOptions;
    }
}


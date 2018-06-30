package com.ioanapascu.edfocus.others;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Grade;
import com.ioanapascu.edfocus.model.GradeDb;
import com.ioanapascu.edfocus.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by ioana on 3/16/2018.
 */

public class StudentGradesStickyAdapter extends ArrayAdapter<Grade> implements StickyListHeadersAdapter {
    private final FirebaseUtils firebase;
    // StickyListHeadersAdapter needs header id's as long, so map course id to a long value.
    HashMap<String, Long> mHeaderIds;
    private ArrayList<Grade> mGrades;
    private Context mContext;
    private int mResource, mHeaderResource;
    private String mClassId, mStudentId, mUserType;

    public StudentGradesStickyAdapter(Context context, int resource, int headerResource, ArrayList<Grade> objects,
                                      HashMap<String, Long> headerIds, String classId, String studentId,
                                      String userType) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mHeaderResource = headerResource;
        mGrades = objects;
        mHeaderIds = headerIds;
        mClassId = classId;
        mStudentId = studentId;
        mUserType = userType;
        firebase = new FirebaseUtils(mContext);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Grade grade = getItem(position);

        StudentGradesStickyAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new StudentGradesStickyAdapter.ViewHolder();
            holder.mNameText = convertView.findViewById(R.id.text_name);
            holder.mDateText = convertView.findViewById(R.id.text_date);
            holder.mGradeText = convertView.findViewById(R.id.text_grade);
            holder.mDescriptionText = convertView.findViewById(R.id.text_description);
            holder.mEditIcon = convertView.findViewById(R.id.icon_edit);

            convertView.setTag(holder);
        } else {
            holder = (StudentGradesStickyAdapter.ViewHolder) convertView.getTag();
        }

        if (mUserType.equals("teacher")) holder.mEditIcon.setVisibility(View.VISIBLE);

        holder.mNameText.setText(grade.getName());
        holder.mDateText.setText(grade.getDate());
        holder.mGradeText.setText(grade.getGrade());
        holder.mDescriptionText.setText(grade.getDescription());
        holder.mEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditGradeDialog(position);
            }
        });

        return convertView;
    }

    private void showEditGradeDialog(final int position) {
        final String gradeId = getItem(position).getId();
        final Dialog editDialog = new Dialog(getContext());
        editDialog.setContentView(R.layout.dialog_add_grade);

        // dialog widgets
        TextView titleText = editDialog.findViewById(R.id.text_title);
        final TextView courseText = editDialog.findViewById(R.id.text_course);
        final EditText gradeText = editDialog.findViewById(R.id.text_grade);
        final EditText descriptionText = editDialog.findViewById(R.id.text_description);
        final EditText nameText = editDialog.findViewById(R.id.text_name);
        final DatePicker datePicker = editDialog.findViewById(R.id.date_picker);
        Button createBtn = editDialog.findViewById(R.id.btn_create);
        ImageView deleteBtn = editDialog.findViewById(R.id.btn_delete);
        ImageView cancelImg = editDialog.findViewById(R.id.img_cancel);
        final Spinner coursesSpinner = editDialog.findViewById(R.id.spinner_courses);

        // specific to using dialog layout for editing
        titleText.setText("Edit Grade");
        createBtn.setText("Save");
        // course can't be changed
        coursesSpinner.setVisibility(View.GONE);

        // load grade info and display it in widgets
        firebase.mStudentGradesRef.child(mClassId).child(mStudentId).child(gradeId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GradeDb grade = dataSnapshot.getValue(GradeDb.class);
                        gradeText.setText(grade.getGrade());
                        descriptionText.setText(grade.getDescription());
                        nameText.setText(grade.getName());

                        // get course name
                        firebase.mClassCoursesRef.child(mClassId).child(grade.getCourseId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                courseText.setText("Course: " + dataSnapshot.child("name").getValue());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        // parse date and set the picker
                        String date = grade.getDate();
                        String[] parts = date.split("-");
                        datePicker.updateDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1,
                                Integer.parseInt(parts[2]));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        // save changes button click
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                String gradeVal = gradeText.getText().toString();
                String description = descriptionText.getText().toString();
                String name = nameText.getText().toString();
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-"
                        + datePicker.getDayOfMonth();

                GradeDb grade = new GradeDb(gradeId, name, gradeVal, date, description, mClassId,
                        getItem(position).getCourseId(), mStudentId);

                // save to firebase
                firebase.mStudentGradesRef.child(mClassId).child(mStudentId).child(gradeId).setValue(grade);
                editDialog.dismiss();
            }
        });

        // delete grade button click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show confirmation dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this grade?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // delete from firebase
                                firebase.mStudentGradesRef.child(mClassId).child(mStudentId).child(gradeId).removeValue();
                                dialog.dismiss();
                                editDialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create and show alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });

        // x button click (cancel)
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.dismiss();
            }
        });

        editDialog.show();
    }

    @Override
    public int getCount() {
        return mGrades.size();
    }

    @Override
    public Grade getItem(int position) {
        return mGrades.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mHeaderResource, parent, false);
            holder.mText = convertView.findViewById(R.id.text_course);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as course name for this grade
        holder.mText.setText(getItem(position).getCourseName());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        // associated header id (grades are grouped by course)
        return mHeaderIds.get(getItem(position).getCourseId());
    }

    private class ViewHolder {
        TextView mNameText, mDateText, mGradeText, mDescriptionText;
        ImageView mEditIcon;
    }

    class HeaderViewHolder {
        TextView mText;
    }


}

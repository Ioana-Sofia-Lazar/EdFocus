package com.ioanap.classbook.utils;

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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Absence;
import com.ioanap.classbook.model.AbsenceDb;

import java.util.ArrayList;
import java.util.HashMap;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by ioana on 3/16/2018.
 */

public class StudentAbsencesStickyAdapter extends ArrayAdapter<Absence> implements StickyListHeadersAdapter {
    // StickyListHeadersAdapter needs header id's as long, so map course id to a long value.
    HashMap<String, Long> mHeaderIds;
    private ArrayList<Absence> mAbsences;
    private Context mContext;
    private int mResource, mHeaderResource;
    private String mClassId, mStudentId;
    private DatabaseReference mClassCoursesRef, mStudentAbsencesRef;

    public StudentAbsencesStickyAdapter(Context context, int resource, int headerResource, ArrayList<Absence> objects,
                                        HashMap<String, Long> headerIds, String classId, String studentId) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mHeaderResource = headerResource;
        mAbsences = objects;
        mHeaderIds = headerIds;
        mClassId = classId;
        mStudentId = studentId;
        mClassCoursesRef = FirebaseDatabase.getInstance().getReference().child("classCourses");
        mStudentAbsencesRef = FirebaseDatabase.getInstance().getReference().child("studentAbsences");
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Absence absence = getItem(position);

        StudentAbsencesStickyAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new StudentAbsencesStickyAdapter.ViewHolder();
            holder.mDateText = convertView.findViewById(R.id.text_date);
            holder.mStatusText = convertView.findViewById(R.id.text_status);
            holder.mEditIcon = convertView.findViewById(R.id.icon_edit);

            convertView.setTag(holder);
        } else {
            holder = (StudentAbsencesStickyAdapter.ViewHolder) convertView.getTag();
        }

        holder.mDateText.setText(absence.getDate());
        if (absence.isAuthorised()) {
            holder.mStatusText.setText("authorised");
            holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.cyan));
        } else {
            holder.mStatusText.setText("unauthorised");
            holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.gray));
        }
        holder.mEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditAbsenceDialog(position);
            }
        });

        return convertView;
    }

    private void showEditAbsenceDialog(final int position) {
        final String absenceId = getItem(position).getId();
        final Dialog editDialog = new Dialog(getContext());
        editDialog.setContentView(R.layout.dialog_add_absence);

        // dialog widgets
        TextView titleText = editDialog.findViewById(R.id.text_title);
        final TextView courseText = editDialog.findViewById(R.id.text_course);
        TextView infoText = editDialog.findViewById(R.id.text_info);
        final DatePicker datePicker = editDialog.findViewById(R.id.date_picker);
        Button createBtn = editDialog.findViewById(R.id.btn_create);
        ImageView deleteBtn = editDialog.findViewById(R.id.btn_delete);
        ImageView cancelImg = editDialog.findViewById(R.id.img_cancel);
        final Spinner coursesSpinner = editDialog.findViewById(R.id.spinner_courses);
        final CheckBox absentAllDayCB = editDialog.findViewById(R.id.checkbox_absent_all_day);
        final CheckBox authorisedCB = editDialog.findViewById(R.id.checkbox_authorised);

        // modify widgets for editing mode
        titleText.setText("Edit Absence");
        createBtn.setText("Save");
        infoText.setVisibility(View.GONE);
        absentAllDayCB.setVisibility(View.GONE);
        // course can't be changed
        coursesSpinner.setVisibility(View.GONE);

        // load absence info and display it in widgets
        mStudentAbsencesRef.child(mClassId).child(mStudentId).child(absenceId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AbsenceDb absence = dataSnapshot.getValue(AbsenceDb.class);
                        authorisedCB.setChecked(absence.isAuthorised());

                        // get course name
                        mClassCoursesRef.child(mClassId).child(absence.getCourseId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                courseText.setText("Course: " + dataSnapshot.child("name").getValue());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        // parse date and set the picker
                        String date = absence.getDate();
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
                boolean authorised = authorisedCB.isChecked();
                String courseId = getItem(position).getCourseId();
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-"
                        + datePicker.getDayOfMonth();

                AbsenceDb absence = new AbsenceDb(absenceId, date, authorised, mClassId, courseId, mStudentId);

                // save to firebase
                mStudentAbsencesRef.child(mClassId).child(mStudentId).child(absenceId).setValue(absence);
                editDialog.dismiss();
            }
        });

        // delete absence button click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show confirmation dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this absence?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // delete from firebase
                                mStudentAbsencesRef.child(mClassId).child(mStudentId).child(absenceId).removeValue();
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
        return mAbsences.size();
    }

    @Override
    public Absence getItem(int position) {
        return mAbsences.get(position);
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
            holder.text = convertView.findViewById(R.id.text_course);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as course name for this Absence
        holder.text.setText(getItem(position).getCourseName());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        // associated header id (absences are grouped by course)
        return mHeaderIds.get(getItem(position).getCourseId());
    }

    private class ViewHolder {
        TextView mStatusText, mDateText;
        ImageView mEditIcon;
    }

    class HeaderViewHolder {
        TextView text;
    }


}

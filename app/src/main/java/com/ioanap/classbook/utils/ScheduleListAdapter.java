package com.ioanap.classbook.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.ScheduleEntryAndCourse;

import java.util.ArrayList;

/**
 * Created by ioana on 11/3/2017.
 * Used for Search Activity.
 */

public class ScheduleListAdapter extends ArrayAdapter<ScheduleEntryAndCourse> {

    private static final String TAG = "ScheduleListAdapter";

    private Context mContext;
    private int mResource;
    private String mClassId, mDay;

    // firebase
    private DatabaseReference mScheduleRef;

    public ScheduleListAdapter(Context context, int resource, ArrayList<ScheduleEntryAndCourse> objects, String classId, String day) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mClassId = classId;
        mDay = day;
        mScheduleRef = FirebaseDatabase.getInstance().getReference().child("schedule");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // create object with entry information
        final ScheduleEntryAndCourse entry = getItem(position);

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mPeriod = (TextView) convertView.findViewById(R.id.text_period);
            holder.mCourseName = (TextView) convertView.findViewById(R.id.text_name);
            holder.mCourseTeacher = (TextView) convertView.findViewById(R.id.text_teacher);
            holder.mDeleteIcon = (ImageView) convertView.findViewById(R.id.img_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mPeriod.setText(entry.getEntry().getStartsAt() + " - " + entry.getEntry().getEndsAt());
        holder.mCourseName.setText(entry.getCourse().getName());
        holder.mCourseTeacher.setText(entry.getCourse().getTeacher());
        holder.mDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete entry
                Log.d("classid  day  entryid", mClassId + " " + mDay + " " + entry.getEntry().getId());
                mScheduleRef.child(mClassId).child(mDay).child(entry.getEntry().getId()).removeValue();
            }
        });

        return convertView;
    }

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView mPeriod, mCourseName, mCourseTeacher;
        ImageView mDeleteIcon;
    }

}

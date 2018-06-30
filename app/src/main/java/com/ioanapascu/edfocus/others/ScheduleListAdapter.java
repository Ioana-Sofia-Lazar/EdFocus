package com.ioanapascu.edfocus.others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.ScheduleEntryAndCourse;
import com.ioanapascu.edfocus.utils.FirebaseUtils;

import java.util.ArrayList;

/**
 * Created by ioana on 11/3/2017.
 * Used for Search Activity.
 */

public class ScheduleListAdapter extends ArrayAdapter<ScheduleEntryAndCourse> {

    private static final String TAG = "ScheduleListAdapter";

    private Context mContext;
    private int mResource;
    private String mClassId, mDay, mUserType;
    private FirebaseUtils firebase;

    public ScheduleListAdapter(Context context, int resource, ArrayList<ScheduleEntryAndCourse> objects,
                               String classId, String day, String userType) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mClassId = classId;
        mDay = day;
        mUserType = userType;
        firebase = new FirebaseUtils(mContext);
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
            holder.mPeriod = convertView.findViewById(R.id.text_period);
            holder.mCourseName = convertView.findViewById(R.id.text_name);
            holder.mCourseTeacher = convertView.findViewById(R.id.text_teacher);
            holder.mDeleteIcon = convertView.findViewById(R.id.img_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // only teacher can delete courses from schedule
        if (mUserType.equals("teacher")) {
            holder.mDeleteIcon.setVisibility(View.VISIBLE);
        }

        holder.mPeriod.setText(String.format("%s - %s", entry.getEntry().getStartsAt(), entry.getEntry().getEndsAt()));
        holder.mCourseName.setText(entry.getCourse().getName());
        holder.mCourseTeacher.setText(entry.getCourse().getTeacher());
        holder.mDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete entry
                firebase.mClassScheduleRef.child(mClassId).child(mDay).child(entry.getEntry().getId()).removeValue();
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

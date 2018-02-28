package com.ioanap.classbook.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Course;

import java.util.ArrayList;

public class CoursesListAdapter extends ArrayAdapter<Course> {

    private static final String TAG = "ClassesListAdapter";

    // variables
    private Context mContext;
    private int mResource;
    private String mClassId;

    // firebase
    private DatabaseReference mClassCoursesRef;

    public CoursesListAdapter(Context context, int resource, ArrayList<Course> objects, String classId) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mClassId = classId;
        mClassCoursesRef = FirebaseDatabase.getInstance().getReference().child("classCourses");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get course information
        final String id = getItem(position).getId();
        String name = getItem(position).getName();
        String teacher = getItem(position).getTeacher();
        String description = getItem(position).getDescription();

        // create the course object with the information
        Course course = new Course(id, name, teacher, description);

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.mTeacher = (TextView) convertView.findViewById(R.id.txt_teacher);
            holder.mDescription = (TextView) convertView.findViewById(R.id.txt_description);
            holder.mEdit = (ImageView) convertView.findViewById(R.id.img_edit);
            holder.mDelete = (ImageView) convertView.findViewById(R.id.img_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mName.setText(course.getName());
        holder.mTeacher.setText(course.getTeacher());
        holder.mDescription.setText(course.getDescription());

        // edit icon click
        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo
            }
        });

        // delete icon click
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete course with id
                mClassCoursesRef.child(mClassId).child(id).removeValue();

                // todo show dialog for confirmation
                // show dialog from adapter android -- google
            }
        });

        return convertView;
    }

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        ImageView mPhoto, mEdit, mDelete;
        TextView mName, mTeacher, mDescription;
    }

}

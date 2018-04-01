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
import com.ioanap.classbook.model.Class;

import java.util.ArrayList;

public class ClassesListAdapter extends ArrayAdapter<Class> {

    private static final String TAG = "ClassesListAdapter";

    private ArrayList<Class> mClasses;
    private Context mContext;
    private int mResource;
    private DatabaseReference mClassStudentsRef, mUserClassesRef;

    public ClassesListAdapter(Context context, int resource, ArrayList<Class> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mClassStudentsRef = FirebaseDatabase.getInstance().getReference().child("classStudents");
        mUserClassesRef = FirebaseDatabase.getInstance().getReference().child("userClasses");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Class aClass = getItem(position);

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mName = convertView.findViewById(R.id.txt_name);
            holder.mSchool = convertView.findViewById(R.id.txt_school);
            holder.mDescription = convertView.findViewById(R.id.txt_description);
            holder.mPhoto = convertView.findViewById(R.id.img_photo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mName.setText(aClass.getName());
        holder.mSchool.setText(aClass.getSchool());
        holder.mDescription.setText(aClass.getDescription());
        UniversalImageLoader.setImage(aClass.getPhoto(), holder.mPhoto, null);

        return convertView;
    }

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        ImageView mPhoto;
        TextView mName, mSchool, mDescription;
    }

}

package com.ioanap.classbook.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;

import java.util.ArrayList;

public class StudentsListAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "StudentsListAdapter";

    private ArrayList<Contact> mStudents;
    private Context mContext;
    private int mResource;
    private SparseBooleanArray mSelectedItemsIds;

    public StudentsListAdapter(Context context, int resource, ArrayList<Contact> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mStudents = objects;
        mSelectedItemsIds = new SparseBooleanArray();
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
    public View getView(int position, View convertView, ViewGroup parent) {
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
            holder.mProfilePhoto = convertView.findViewById(R.id.image_profile_photo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mName.setText(student.getName());
        UniversalImageLoader.setImage(student.getProfilePhoto(), holder.mProfilePhoto, null);

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

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private static class ViewHolder {
        ImageView mProfilePhoto;
        TextView mName;
    }
}


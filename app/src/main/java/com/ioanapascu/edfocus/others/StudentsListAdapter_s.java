package com.ioanapascu.edfocus.others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Contact;

import java.util.ArrayList;

// students list adapter for student type of user
public class StudentsListAdapter_s extends ArrayAdapter<Contact> {

    private static final String TAG = "StudentsListAdapter_s";

    private ArrayList<Contact> mStudents;
    private Context mContext;
    private int mResource;
    private String mClassId;

    public StudentsListAdapter_s(Context context, int resource, ArrayList<Contact> objects, String classId) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mStudents = objects;
        mClassId = classId;
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

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mName = convertView.findViewById(R.id.text_name);
            holder.mEmail = convertView.findViewById(R.id.text_email);
            holder.mProfilePhoto = convertView.findViewById(R.id.image_profile_photo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mName.setText(student.getName());
        holder.mEmail.setText(student.getEmail());
        UniversalImageLoader.setImage(student.getProfilePhoto(), holder.mProfilePhoto, null);

        return convertView;
    }

    public void updateLists(ArrayList<Contact> students) {
        mStudents = students;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView mProfilePhoto;
        TextView mName, mEmail;
    }
}


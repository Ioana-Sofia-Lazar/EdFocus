package com.ioanap.classbook.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Person;

import java.util.ArrayList;

/**
 * Created by ioana on 11/3/2017.
 */

public class PeopleListAdapter extends ArrayAdapter<Person> {

    private static final String TAG = "PeopleListAdapter";

    private ArrayList<Person> people;
    private Context mContext;
    private int mResource;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        ImageView mPersonProfilePhoto, mMessageContactImageView;
        TextView mPersonName, mPersonUserType;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public PeopleListAdapter(Context context, int resource, ArrayList<Person> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get person information
        String id = getItem(position).getId();
        String name = getItem(position).getName();
        String userType = getItem(position).getUserType();
        String profilePhoto = getItem(position).getProfilePhoto();

        // create the person object with the information
        Person person = new Person(id, name, userType, profilePhoto);

        ViewHolder holder;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mPersonName = (TextView) convertView.findViewById(R.id.text_person_name);
            holder.mPersonUserType = (TextView) convertView.findViewById(R.id.text_person_user_type);
            holder.mPersonProfilePhoto = (ImageView) convertView.findViewById(R.id.image_person_profile_photo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mPersonName.setText(person.getName());
        holder.mPersonUserType.setText(person.getUserType());
        UniversalImageLoader.setImage(person.getProfilePhoto(), holder.mPersonProfilePhoto, null);

        return convertView;
    }

}

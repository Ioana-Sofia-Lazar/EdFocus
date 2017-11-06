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
import com.ioanap.classbook.model.Contact;

import java.util.ArrayList;

/**
 * Created by ioana on 11/3/2017.
 */

public class ContactsCustomAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "ContactsCustomAdapter";

    private ArrayList<Contact> contacts;
    private Context mContext;
    private int mResource;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        ImageView mContactProfilePhoto, mMessageContactImageView;
        TextView mContactName, mContactEmail;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public ContactsCustomAdapter(Context context, int resource, ArrayList<Contact> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get contact information
        String name = getItem(position).getName();
        String email = getItem(position).getEmail();
        String profilePhoto = getItem(position).getProfilePhoto();

        // create the contact object with the information
        Contact contact = new Contact(name, email, profilePhoto);

        ViewHolder holder;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mContactName = (TextView) convertView.findViewById(R.id.text_contact_name);
            holder.mContactEmail = (TextView) convertView.findViewById(R.id.text_contact_email);
            holder.mContactProfilePhoto = (ImageView) convertView.findViewById(R.id.image_contact_profile_photo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mContactName.setText(contact.getName());
        holder.mContactEmail.setText(contact.getEmail());
        UniversalImageLoader.setImage(contact.getProfilePhoto(), holder.mContactProfilePhoto, null);

        return convertView;
    }

}

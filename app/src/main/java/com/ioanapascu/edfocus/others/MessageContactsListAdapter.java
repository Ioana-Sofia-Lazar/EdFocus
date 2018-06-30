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
import com.ioanapascu.edfocus.model.Contact;

import java.util.List;

/**
 * Lst adapter for the Recycler View that will contain Contacts and Requests.
 */

public class MessageContactsListAdapter extends ArrayAdapter<Contact> {

    private List<Contact> mContacts;
    private Context mContext;
    private int mResource;

    public MessageContactsListAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mContacts = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Contact contact = getItem(position);

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mContactName = convertView.findViewById(R.id.text_contact_name);
            holder.mContactProfilePhoto = convertView.findViewById(R.id.image_contact_photo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mContactName.setText(contact.getName());
        UniversalImageLoader.setImage(contact.getProfilePhoto(), holder.mContactProfilePhoto, null);

        return convertView;
    }

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        public TextView mContactName;
        public ImageView mContactProfilePhoto;
    }
}
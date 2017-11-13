package com.ioanap.classbook.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;

import java.util.ArrayList;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactViewHolder> {

    private ArrayList<Contact> mContacts = new ArrayList<>();
    private Context mContext;

    public ContactsListAdapter(Context context, ArrayList<Contact> contacts) {
        mContext = context;
        mContacts = contacts;
    }

    /**
     *
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.row_contact_item, parent, false);

        // Return a new holder instance
        ContactViewHolder viewHolder = new ContactViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, int position) {
        // Get the data model based on position
        Contact contact = mContacts.get(position);

        // Set item views based on your views and data model
        viewHolder.mContactName.setText(contact.getName());
        viewHolder.mContactEmail.setText(contact.getEmail());
        UniversalImageLoader.setImage(contact.getProfilePhoto(), viewHolder.mContactProfilePhoto, null);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView mContactName, mContactEmail;
        public ImageView mContactProfilePhoto;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ContactViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            mContactName = (TextView) itemView.findViewById(R.id.text_contact_name);
            mContactEmail = (TextView) itemView.findViewById(R.id.text_contact_email);
            mContactProfilePhoto = (ImageView) itemView.findViewById(R.id.image_contact_profile_photo);
        }
    }

    /**
     * Function to update the list.
     *
     * @param list
     */
    public void updateList(ArrayList<Contact> list){
        mContacts = list;
        notifyDataSetChanged();
    }
}

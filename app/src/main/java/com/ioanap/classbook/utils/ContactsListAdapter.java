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
import com.ioanap.classbook.model.Request;

import java.util.ArrayList;

/**
 * Lst adapter for the Recycler View that will contain Contacts and Requests.
 */

public class ContactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int VIEW_TYPE_CONTACT = 0;
    final int VIEW_TYPE_REQUEST = 1;
    final int VIEW_TYPE_REQUESTS_TITLE = 2;

    private ArrayList<Contact> mContacts = new ArrayList<>();
    private ArrayList<Request> mRequests = new ArrayList<>();
    private Context mContext;

    public ContactsListAdapter(Context context, ArrayList<Contact> contacts, ArrayList<Request> requests) {
        mContext = context;
        mContacts = contacts;
        mRequests = requests;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout and return a new holder instance
        if (viewType == VIEW_TYPE_CONTACT) {
            View contactView = inflater.inflate(R.layout.row_contact_item, parent, false);
            ContactViewHolder viewHolder = new ContactViewHolder(contactView);

            return viewHolder;
        }

        if (viewType == VIEW_TYPE_REQUESTS_TITLE) {
            View titleView = inflater.inflate(R.layout.row_title_item, parent, false);
            RequestsTitleViewHolder viewHolder = new RequestsTitleViewHolder(titleView);

            return viewHolder;
        }

        // viewType == VIEW_TYPE_REQUEST
        View requestView = inflater.inflate(R.layout.row_request_item, parent, false);
        RequestViewHolder viewHolder = new RequestViewHolder(requestView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof ContactViewHolder){
            ((ContactViewHolder) viewHolder).populate(mContacts.get(position));
        }

        if(viewHolder instanceof RequestsTitleViewHolder){
            ((RequestsTitleViewHolder) viewHolder).setTitle("Requests from other people");
        }

        if(viewHolder instanceof RequestViewHolder){
            ((RequestViewHolder) viewHolder).populate(mRequests.get(position - mContacts.size() - 1));
        }
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView mContactName, mContactEmail;
        public ImageView mContactProfilePhoto;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mContactName = (TextView) itemView.findViewById(R.id.text_contact_name);
            mContactEmail = (TextView) itemView.findViewById(R.id.text_contact_email);
            mContactProfilePhoto = (ImageView) itemView.findViewById(R.id.image_contact_profile_photo);
        }

        public void populate(Contact contact){
            mContactName.setText(contact.getName());
            mContactEmail.setText(contact.getEmail());
            UniversalImageLoader.setImage(contact.getProfilePhoto(), mContactProfilePhoto, null);
        }
    }

    public class RequestsTitleViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;

        public RequestsTitleViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.text_title);
        }

        public void setTitle(String title){
            mTitle.setText(title);
        }
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView mRequestName, mRequestText;
        public ImageView mRequstProfilePhoto;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mRequestName = (TextView) itemView.findViewById(R.id.text_request_name);
            mRequestText = (TextView) itemView.findViewById(R.id.text_request);
            mRequstProfilePhoto = (ImageView) itemView.findViewById(R.id.image_request_profile_photo);
        }

        public void populate(Request request){
            mRequestName.setText(request.getName());
            mRequestText.setText("Wants to add you as " + request.getRequestType());
            UniversalImageLoader.setImage(request.getProfilePhoto(), mRequstProfilePhoto, null);
        }
    }

    @Override
    public int getItemCount(){
        return mContacts.size() + mRequests.size() + 1;
    }

    @Override
    public int getItemViewType(int position){
        if (position < mContacts.size()){
            return VIEW_TYPE_CONTACT;
        }
        if (position == mContacts.size()) {
            return VIEW_TYPE_REQUESTS_TITLE;
        }
        if (position > mContacts.size()){
            return VIEW_TYPE_REQUEST;
        }

        return -1;
    }

    public void updateLists(ArrayList<Contact> contacts){
        mContacts = contacts;
        notifyDataSetChanged();
    }

}

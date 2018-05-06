package com.ioanap.classbook.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
import com.ioanap.classbook.model.RequestInfo;
import com.ioanap.classbook.shared.ViewProfileActivity;

import java.util.ArrayList;

/**
 * Lst adapter for the Recycler View that will contain Contacts and Requests.
 */

public class ContactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_CONTACT = 0;
    private final int VIEW_TYPE_REQUEST = 1;
    private final int VIEW_TYPE_REQUESTS_TITLE = 2;
    private final int VIEW_TYPE_NO_CONTACTS = 3;
    private final int VIEW_TYPE_NO_REQUESTS = 4;

    private ArrayList<Contact> mContacts = new ArrayList<>();
    private ArrayList<RequestInfo> mRequests = new ArrayList<>();
    private Context mContext;

    public ContactsListAdapter(Context context, ArrayList<Contact> contacts,
                               ArrayList<RequestInfo> requests) {
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

            return new ContactViewHolder(contactView);
        }

        if (viewType == VIEW_TYPE_REQUESTS_TITLE) {
            View titleView = inflater.inflate(R.layout.row_title_item, parent, false);

            return new RequestsTitleViewHolder(titleView);
        }

        if (viewType == VIEW_TYPE_REQUEST) {
            View requestView = inflater.inflate(R.layout.row_request_item, parent, false);

            return new RequestViewHolder(requestView);
        }

        if (viewType == VIEW_TYPE_NO_CONTACTS) {
            View requestView = inflater.inflate(R.layout.row_no_contacts, parent, false);

            return new NoContactsViewHolder(requestView);
        }

        // viewType == VIEW_TYPE_NO_REQUESTS
        View requestView = inflater.inflate(R.layout.row_no_requests, parent, false);

        return new NoRequestsViewHolder(requestView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if(viewHolder instanceof ContactViewHolder){
            ((ContactViewHolder) viewHolder).populate(mContacts.get(position));

            // clicking a user's profile pic or name sends you to their profile
            ((ContactViewHolder) viewHolder).mContactName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfile(mContacts.get(position).getId());
                }
            });
            ((ContactViewHolder) viewHolder).mContactProfilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfile(mContacts.get(position).getId());
                }
            });
        }

        if(viewHolder instanceof RequestsTitleViewHolder){
            ((RequestsTitleViewHolder) viewHolder).setTitle("Requests from other people");
        }

        if(viewHolder instanceof RequestViewHolder){
            // if no contacts, consider the "no contacts yet" message
            final int pos = mContacts.size() > 0 ? position - mContacts.size() - 1 : position - 2;

            ((RequestViewHolder) viewHolder).populate(mRequests.get(pos));

            // clicking a user's profile pic or name sends you to their profile
            ((RequestViewHolder) viewHolder).mRequestName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfile(mRequests.get(pos).getPersonId());
                }
            });
            ((RequestViewHolder) viewHolder).mRequestProfilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfile(mRequests.get(pos).getPersonId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int sizeC = mContacts.size(), sizeR = mRequests.size();

        // if no requests or no contacts show corresponding messages
        if (sizeC == 0) sizeC = 1;
        if (sizeR == 0) sizeR = 1;

        return sizeC + sizeR + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mContacts.size() > 0) {
            if (position < mContacts.size()) {
                return VIEW_TYPE_CONTACT;
            }
            if (position == mContacts.size()) {
                return VIEW_TYPE_REQUESTS_TITLE;
            }
            if (position > mContacts.size()) {
                if (mRequests.size() > 0) {
                    return VIEW_TYPE_REQUEST;
                } else {
                    return VIEW_TYPE_NO_REQUESTS;
                }
            }
        } else {
            if (position == 0) {
                return VIEW_TYPE_NO_CONTACTS;
            }
            if (position == 1) {
                return VIEW_TYPE_REQUESTS_TITLE;
            }
            if (position > mContacts.size()) {
                if (mRequests.size() > 0) {
                    return VIEW_TYPE_REQUEST;
                } else {
                    return VIEW_TYPE_NO_REQUESTS;
                }
            }
        }

        return -1;
    }

    public void updateLists(ArrayList<Contact> contacts) {
        mContacts = contacts;
        notifyDataSetChanged();
    }

    private void showProfile(String tappedUserId) {
        // display tapped person's profile
        Intent myIntent = new Intent(mContext, ViewProfileActivity.class);
        myIntent.putExtra("userId", tappedUserId);
        mContext.startActivity(myIntent);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView mContactName, mContactEmail;
        public ImageView mContactProfilePhoto;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mContactName = itemView.findViewById(R.id.text_contact_name);
            mContactEmail = itemView.findViewById(R.id.text_contact_email);
            mContactProfilePhoto = itemView.findViewById(R.id.image_contact_profile_photo);
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

            mTitle = itemView.findViewById(R.id.text_title);
        }

        public void setTitle(String title){
            mTitle.setText(title);
        }
    }

    public class NoRequestsViewHolder extends RecyclerView.ViewHolder {
        public NoRequestsViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class NoContactsViewHolder extends RecyclerView.ViewHolder {
        public NoContactsViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView mRequestName, mRequestText;
        public ImageView mRequestProfilePhoto;
        public Button mConfirmButton, mDeclineButton;

        private RequestInfo mRequest;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mRequestName = itemView.findViewById(R.id.text_request_name);
            mRequestText = itemView.findViewById(R.id.text_request);
            mRequestProfilePhoto = itemView.findViewById(R.id.image_request_profile_photo);
            mConfirmButton = itemView.findViewById(R.id.btn_confirm_request);
            mDeclineButton = itemView.findViewById(R.id.btn_delete_request);

            // request is confirmed
            mConfirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) mContext).confirmContactRequest(mRequest.getPersonId(), mRequest.getRequestType());
                }
            });

            // request is declined
            mDeclineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) mContext).declineContactRequest(mRequest.getPersonId());
                }
            });
        }

        public void populate(RequestInfo request) {
            mRequest = request;
            mRequestName.setText(request.getName());
            mRequestText.setText("Wants to add you as their " + request.getRequestType());
            UniversalImageLoader.setImage(request.getProfilePhoto(), mRequestProfilePhoto, null);
        }
    }

}

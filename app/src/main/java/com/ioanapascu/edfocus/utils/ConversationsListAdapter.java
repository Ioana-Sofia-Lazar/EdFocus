package com.ioanapascu.edfocus.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.firebase.FirebaseUtils;
import com.ioanapascu.edfocus.model.Conversation;
import com.ioanapascu.edfocus.shared.ConversationActivity;

import java.util.List;

/**
 * Created by Ioana Pascu on 5/6/2018.
 */

public class ConversationsListAdapter extends RecyclerView.Adapter<ConversationsListAdapter.MyViewHolder> {

    private FirebaseUtils firebase;
    private List<Conversation> mConversationsList;
    private Context mContext;
    private String mCurrentUserId;

    public ConversationsListAdapter(Context context, List<Conversation> notificationList, String currentUserId) {
        this.mConversationsList = notificationList;
        this.mContext = context;
        this.mCurrentUserId = currentUserId;
        this.firebase = new FirebaseUtils();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_conversation, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Conversation conversation = mConversationsList.get(position);

        holder.nameText.setText(conversation.getUserName());
        if (conversation.getFrom().equals(mCurrentUserId)) {
            holder.messageText.setText(String.format("Me: %s", conversation.getLastMessage()));
        } else {
            holder.messageText.setText(conversation.getLastMessage());
        }

        if (!conversation.isSeen() && !conversation.getFrom().equals(mCurrentUserId)) {
            holder.messageText.setTypeface(null, Typeface.BOLD);
            holder.messageText.setTextColor(mContext.getResources().getColor(R.color.cyan));
        } else {
            holder.messageText.setTypeface(null, Typeface.NORMAL);
            holder.messageText.setTextColor(mContext.getResources().getColor(R.color.gray));
        }

        holder.dateText.setText(Utils.formatMessageDate(conversation.getLastMessageDate()));

        UniversalImageLoader.setImage(conversation.getUserPhoto(), holder.profilePhoto, null);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mark conversation as seen
                if (!conversation.getFrom().equals(mCurrentUserId)) {
                    firebase.mConversationsRef.child(mCurrentUserId).child(conversation.getFrom()).child("seen").setValue(true);
                    firebase.mConversationsRef.child(conversation.getFrom()).child(mCurrentUserId).child("seen").setValue(true);
                }

                Intent intent = new Intent(mContext, ConversationActivity.class);
                intent.putExtra("userId", conversation.getUserId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mConversationsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, messageText, dateText;
        ImageView profilePhoto;

        MyViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.text_name);
            messageText = view.findViewById(R.id.text_message);
            dateText = view.findViewById(R.id.text_date);
            profilePhoto = view.findViewById(R.id.profile_photo);
        }
    }
}

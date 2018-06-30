package com.ioanapascu.edfocus.others;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Message;
import com.ioanapascu.edfocus.utils.Utils;

import java.util.List;

/**
 * Created by Ioana Pascu on 5/6/2018.
 */

public class MessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_FIRST_ROW = 3;

    private List<Message> mMessagesList;
    private List<String> mFirstRow;
    private Context mContext;
    private String mCurrentUserId;

    public MessagesListAdapter(Context context, List<String> firstRow, List<Message> messages, String currentUserId) {
        this.mMessagesList = messages;
        this.mContext = context;
        this.mCurrentUserId = currentUserId;
        this.mFirstRow = firstRow;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_sent_message, parent, false);
            return new MessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_received_message, parent, false);
            return new MessageViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_chat_first_row, parent, false);
        return new FirstRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof MessageViewHolder) {
            Message message = mMessagesList.get(position - 1);
            ((MessageViewHolder) viewHolder).bind(message);
        } else {
            ((FirstRowViewHolder) viewHolder).bind(mFirstRow.get(0));
        }

    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FIRST_ROW;
        } else {
            Message message = mMessagesList.get(position - 1);
            if (message.getFrom().equals(mCurrentUserId)) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size() + 1;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeSentText;

        MessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.txt_message);
            timeSentText = view.findViewById(R.id.txt_time_sent);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeSentText.setText(Utils.formatMessageDate(message.getTime()));
        }
    }

    class FirstRowViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        FirstRowViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.text);
            imageView = view.findViewById(R.id.img_icon);
        }

        void bind(String text) {
            textView.setText(text);
            if (!text.equals("Pull to load more messages...")) {
                imageView.setVisibility(View.GONE);
                textView.setText("No more messages to load.");
            }
        }
    }
}

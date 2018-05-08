package com.ioanap.classbook.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Notification;

import java.util.List;

/**
 * Created by Ioana Pascu on 5/6/2018.
 */

public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.MyViewHolder> {

    private List<Notification> mNotificationList;
    private Context mContext;

    public NotificationsListAdapter(Context context, List<Notification> notificationList) {
        this.mNotificationList = notificationList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notification notification = mNotificationList.get(position);

        holder.titleText.setText(notification.getTitle());
        holder.messageText.setText(notification.getMessage());
        holder.iconImage.setImageResource(notification.getIcon());

        if (!notification.isSeen()) {
            holder.containerLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, messageText;
        ImageView iconImage;
        RelativeLayout containerLayout;

        MyViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.text_title);
            messageText = view.findViewById(R.id.text_message);
            iconImage = view.findViewById(R.id.icon);
            containerLayout = view.findViewById(R.id.layout_container);
        }
    }
}

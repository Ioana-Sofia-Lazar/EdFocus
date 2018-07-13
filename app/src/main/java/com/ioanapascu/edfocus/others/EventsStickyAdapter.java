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
import com.ioanapascu.edfocus.model.Event;
import com.ioanapascu.edfocus.shared.EventsActivity;
import com.ioanapascu.edfocus.utils.Utils;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by ioana on 3/16/2018.
 */

public class EventsStickyAdapter extends ArrayAdapter<Event> implements StickyListHeadersAdapter {
    private ArrayList<Event> mEvents;
    private Context mContext;
    private int mResource, mHeaderResource;
    private String mUserType;

    public EventsStickyAdapter(Context context, int resource, int headerResource, ArrayList<Event> objects,
                               String userType) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mHeaderResource = headerResource;
        mEvents = objects;
        mUserType = userType;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);

        EventsStickyAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new EventsStickyAdapter.ViewHolder();
            holder.mDayNumber = convertView.findViewById(R.id.text_day_number);
            holder.mDayName = convertView.findViewById(R.id.text_day_name);
            holder.mEventName = convertView.findViewById(R.id.text_name);
            holder.mEventDescription = convertView.findViewById(R.id.text_description);
            holder.mTime = convertView.findViewById(R.id.text_time);
            holder.mLocation = convertView.findViewById(R.id.text_location);
            holder.mEditIcon = convertView.findViewById(R.id.icon_edit);

            convertView.setTag(holder);
        } else {
            holder = (EventsStickyAdapter.ViewHolder) convertView.getTag();
        }

        // only teacher can edit events
        if (mUserType.equals("teacher")) holder.mEditIcon.setVisibility(View.VISIBLE);

        holder.mDayNumber.setText(String.valueOf(Utils.millisToDay(event.getDate())));
        holder.mDayName.setText(String.valueOf(Utils.millisToDayName(event.getDate())));
        holder.mEventName.setText(event.getName());
        holder.mEventDescription.setText(event.getDescription());
        holder.mTime.setText(Utils.millisToTimeString(event.getDate()));
        holder.mLocation.setText(event.getLocation());
        holder.mEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EventsActivity) mContext).showEventDialog(position);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public Event getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mHeaderResource, parent, false);
            holder.mMonth = convertView.findViewById(R.id.text_month);
            holder.mYear = convertView.findViewById(R.id.text_year);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as month name for this event
        Event event = getItem(position);
        holder.mMonth.setText(Utils.millisToMonthName(event.getDate()));
        holder.mYear.setText(String.valueOf(Utils.millisToYear(event.getDate())));
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        // associated header id (events are grouped by month)
        return Utils.millisToMonth(getItem(position).getDate());
    }

    private class ViewHolder {
        TextView mDayNumber, mDayName, mEventName, mEventDescription, mTime, mLocation;
        ImageView mEditIcon;
    }

    class HeaderViewHolder {
        TextView mMonth, mYear;
    }


}

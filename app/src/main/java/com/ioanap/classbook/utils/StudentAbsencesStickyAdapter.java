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
import com.ioanap.classbook.model.Absence;

import java.util.ArrayList;
import java.util.HashMap;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by ioana on 3/16/2018.
 */

public class StudentAbsencesStickyAdapter extends ArrayAdapter<Absence> implements StickyListHeadersAdapter {
    // StickyListHeadersAdapter needs header id's as long, so map course id to a long value.
    HashMap<String, Long> mHeaderIds;
    private ArrayList<Absence> mAbsences;
    private Context mContext;
    private int mResource, mHeaderResource;

    public StudentAbsencesStickyAdapter(Context context, int resource, int headerResource, ArrayList<Absence> objects,
                                        HashMap<String, Long> headerIds) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mHeaderResource = headerResource;
        mAbsences = objects;
        mHeaderIds = headerIds;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Absence absence = getItem(position);

        StudentAbsencesStickyAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new StudentAbsencesStickyAdapter.ViewHolder();
            holder.mDateText = convertView.findViewById(R.id.text_date);
            holder.mStatusText = convertView.findViewById(R.id.text_status);
            holder.mEditIcon = convertView.findViewById(R.id.icon_edit);

            convertView.setTag(holder);
        } else {
            holder = (StudentAbsencesStickyAdapter.ViewHolder) convertView.getTag();
        }

        holder.mDateText.setText(absence.getDate());
        if (absence.isAuthorised()) {
            holder.mStatusText.setText("authorised");
            holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.cyan));
        } else {
            holder.mStatusText.setText("unauthorised");
            holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.gray));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mAbsences.size();
    }

    @Override
    public Absence getItem(int position) {
        return mAbsences.get(position);
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
            holder.text = convertView.findViewById(R.id.text_course);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as course name for this Absence
        holder.text.setText(getItem(position).getCourseName());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return mHeaderIds.get(getItem(position).getCourseId());
    }

    private class ViewHolder {
        TextView mStatusText, mDateText;
        ImageView mEditIcon;
    }

    class HeaderViewHolder {
        TextView text;
    }


}

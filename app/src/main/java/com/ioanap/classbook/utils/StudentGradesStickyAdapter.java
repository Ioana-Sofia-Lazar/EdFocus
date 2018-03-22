package com.ioanap.classbook.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Grade;

import java.util.ArrayList;
import java.util.HashMap;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by ioana on 3/16/2018.
 */

public class StudentGradesStickyAdapter extends ArrayAdapter<Grade> implements StickyListHeadersAdapter {
    // StickyListHeadersAdapter needs header id's as long, so map course id to a long value.
    HashMap<String, Long> mHeaderIds;
    private ArrayList<Grade> mGrades;
    private Context mContext;
    private int mResource, mHeaderResource;

    public StudentGradesStickyAdapter(Context context, int resource, int headerResource, ArrayList<Grade> objects,
                                      HashMap<String, Long> headerIds) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mHeaderResource = headerResource;
        mGrades = objects;
        mHeaderIds = headerIds;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Grade grade = getItem(position);

        StudentGradesStickyAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new StudentGradesStickyAdapter.ViewHolder();
            holder.mNameText = convertView.findViewById(R.id.text_name);
            holder.mDateText = convertView.findViewById(R.id.text_date);
            holder.mGradeText = convertView.findViewById(R.id.text_grade);
            holder.mDescriptionText = convertView.findViewById(R.id.text_description);

            convertView.setTag(holder);
        } else {
            holder = (StudentGradesStickyAdapter.ViewHolder) convertView.getTag();
        }

        holder.mNameText.setText(grade.getName());
        holder.mDateText.setText(grade.getDate());
        holder.mGradeText.setText(grade.getGrade());
        holder.mDescriptionText.setText(grade.getDescription());

        return convertView;
    }

    @Override
    public int getCount() {
        return mGrades.size();
    }

    @Override
    public Grade getItem(int position) {
        return mGrades.get(position);
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

        // set header text as course name for this grade
        holder.text.setText(getItem(position).getCourseName());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return mHeaderIds.get(getItem(position).getCourseId());
    }

    private class ViewHolder {
        TextView mNameText, mDateText, mGradeText, mDescriptionText;
    }

    class HeaderViewHolder {
        TextView text;
    }


}

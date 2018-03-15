package com.ioanap.classbook.utils;

/**
 * Created by ioana on 3/15/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Grade;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;

public class GradeCell extends SimpleCell<Grade, GradeCell.ViewHolder> {

    public GradeCell(@NonNull Grade item) {
        super(item);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.row_grade;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, View cellView) {
        return new ViewHolder(cellView);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Context context, Object o) {
        viewHolder.mNameText.setText(getItem().getName());
        viewHolder.mDateText.setText(getItem().getDate());
        viewHolder.mGradeText.setText(getItem().getGrade());
        viewHolder.mDescriptionText.setText(getItem().getDescription());
    }

    public static class ViewHolder extends SimpleViewHolder {
        TextView mNameText, mDateText, mGradeText, mDescriptionText;

        ViewHolder(View itemView) {
            super(itemView);
            mNameText = itemView.findViewById(R.id.text_name);
            mDateText = itemView.findViewById(R.id.text_date);
            mGradeText = itemView.findViewById(R.id.text_grade);
            mDescriptionText = itemView.findViewById(R.id.text_description);
        }
    }
}

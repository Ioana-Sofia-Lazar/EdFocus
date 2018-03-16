package com.ioanap.classbook.utils;

/**
 * Created by ioana on 3/15/2018.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Grade;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;
import com.jaychang.srv.Updatable;

public class GradeCell extends SimpleCell<Grade, GradeCell.ViewHolder> implements Updatable<Grade> {

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

        if (o != null) {
            // partial update
            if (o instanceof Bundle) {
                Bundle bundle = ((Bundle) o);
                for (String key : bundle.keySet()) {
                    if (key.equals("newId")) {
                        //viewHolder.mNameText.setText(bundle.getString(key));
                    } else if (key.equals("newName")) {
                        viewHolder.mNameText.setText(bundle.getString(key));
                    }
                }
            }
            return;
        }

        viewHolder.mNameText.setText(getItem().getName());
        viewHolder.mDateText.setText(getItem().getDate());
        viewHolder.mGradeText.setText(getItem().getGrade());
        viewHolder.mDescriptionText.setText(getItem().getDescription());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Grade newItem) {
        return getItem().getId().equals(newItem.getId());
    }

    /**
     * If getItem() is the same as newItem and areContentsTheSame() returns false, then the
     * cell needs to be updated, onBindViewHolder() will be called with this payload object.
     **/
    @Override
    public Object getChangePayload(@NonNull Grade newItem) {
        Bundle bundle = new Bundle();
        bundle.putString("newId", newItem.getId());
        bundle.putString("newName", newItem.getName());
        return bundle;
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

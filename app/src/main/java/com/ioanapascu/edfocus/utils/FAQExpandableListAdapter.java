package com.ioanapascu.edfocus.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ioanapascu.edfocus.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ioana Pascu on 5/6/2018.
 */

public class FAQExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mQuestions, mAnswers; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> mQAndAList;

    public FAQExpandableListAdapter(Context context) {
        this.mContext = context;
        // get questions from resource
        this.mQuestions = Arrays.asList(mContext.getResources().getStringArray(R.array.faq_questions));
        mQAndAList = new HashMap<>();

        // create the map - each question has one corresponding answer (from resource)
        String[] answers = mContext.getResources().getStringArray(R.array.faq_answers);
        for (int i = 0; i < answers.length; i++) {
            String string = answers[i];
            List<String> answer = Collections.singletonList(string);
            mQAndAList.put(mQuestions.get(i), answer);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mQAndAList.get(this.mQuestions.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_faq_answer, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.text_answer);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mQAndAList.get(this.mQuestions.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mQuestions.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mQuestions.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_faq_question, null);
        }

        TextView lblListHeader = convertView
                .findViewById(R.id.text_question);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

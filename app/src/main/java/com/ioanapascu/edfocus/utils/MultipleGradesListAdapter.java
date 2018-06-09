package com.ioanapascu.edfocus.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.GradeRow;

import java.util.List;

/**
 * Created by ioana on 11/3/2017.
 * Used for Search Activity.
 */

public class MultipleGradesListAdapter extends
        RecyclerView.Adapter<MultipleGradesListAdapter.MyViewHolder> {

    private static final String TAG = "MultipleGradesListAdapter";

    private List<GradeRow> mGrades;
    private Context mContext;
    private String mClassId;

    // firebase
    private DatabaseReference mStudentGradesRef;

    public MultipleGradesListAdapter(Context context, List<GradeRow> studentNames, String classId) {
        this.mContext = context;
        this.mGrades = studentNames;
        mStudentGradesRef = FirebaseDatabase.getInstance().getReference().child("studentGrades");
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        GradeRow grade = mGrades.get(position);
        holder.mStudentName.setText(grade.getStudentName());

        // save information entered in the edit textxs
        holder.mGrade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mGrades.get(position).setGrade(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.mNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mGrades.get(position).setNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mGrades.size();
    }

    public List<GradeRow> getGradesInfo() {
        return mGrades;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_multiple_grades, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mStudentName;
        public EditText mGrade, mNotes;

        public MyViewHolder(View view) {
            super(view);
            mStudentName = view.findViewById(R.id.text_student_name);
            mGrade = view.findViewById(R.id.text_grade);
            mNotes = view.findViewById(R.id.text_notes);
        }
    }
}
package com.ioanapascu.edfocus.others;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.GradeRow;
import com.ioanapascu.edfocus.utils.Utils;

import java.util.List;

/**
 * Created by ioana on 11/3/2017.
 * Used for Search Activity.
 */

public class MultipleGradesListAdapter extends
        RecyclerView.Adapter<MultipleGradesListAdapter.GradeViewHolder> {

    private static final String TAG = "MultipleGradesListAdapter";

    private List<GradeRow> mGrades;
    private Context mContext;
    private String mClassId;

    public MultipleGradesListAdapter(Context context, List<GradeRow> studentNames, String classId) {
        this.mContext = context;
        this.mGrades = studentNames;
    }

    @Override
    public void onBindViewHolder(GradeViewHolder holder, final int position) {
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
    public GradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_multiple_grades, parent, false);
        return new GradeViewHolder(v);
    }

    /**
     * View holder class
     */
    public class GradeViewHolder extends RecyclerView.ViewHolder {
        public TextView mStudentName;
        public EditText mGrade, mNotes;
        public TextInputLayout mGradeTil;

        public GradeViewHolder(View view) {
            super(view);
            mStudentName = view.findViewById(R.id.text_student_name);
            mGrade = view.findViewById(R.id.text_grade);
            mNotes = view.findViewById(R.id.text_notes);
            mGradeTil = view.findViewById(R.id.til_grade);
        }

        public boolean checkGradeField() {
            return Utils.toggleFieldError(mGradeTil, mGrade.getText().toString(), "*");
        }
    }
}
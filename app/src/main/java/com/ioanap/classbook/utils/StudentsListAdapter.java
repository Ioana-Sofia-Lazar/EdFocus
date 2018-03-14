package com.ioanap.classbook.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;

import java.util.ArrayList;

/**
 * Created by ioana on 3/2/2018.
 */

public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.StudentViewHolder> {
    public OnActivityAction onActivityAction;
    private ArrayList<Contact> mStudents = new ArrayList<>();
    private Context mContext;

    public StudentsListAdapter(Context context, ArrayList<Contact> students) {
        mContext = context;
        mStudents = students;
    }

    @Override
    public StudentsListAdapter.StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_student, parent, false);
        StudentViewHolder viewHolder = new StudentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StudentsListAdapter.StudentViewHolder holder, int position) {
        holder.bindStudent(mStudents.get(position));
    }

    public void updateLists(ArrayList<Contact> students) {
        mStudents = students;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    public interface OnActivityAction {
        void action();

    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView mProfilePhoto;
        TextView mName;

        private Context mContext;

        public StudentViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_name);
            mProfilePhoto = itemView.findViewById(R.id.image_profile_photo);
            mContext = itemView.getContext();

            // todo
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onActivityAction.action();
                    return false;
                }
            });
        }

        public void bindStudent(Contact student) {
            mName.setText(student.getName());
            UniversalImageLoader.setImage(student.getProfilePhoto(), mProfilePhoto, null);
        }
    }
}
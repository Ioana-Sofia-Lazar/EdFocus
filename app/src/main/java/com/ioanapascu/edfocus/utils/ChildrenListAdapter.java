package com.ioanapascu.edfocus.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Child;
import com.ioanapascu.edfocus.model.Class;
import com.ioanapascu.edfocus.shared.ViewProfileActivity;
import com.ioanapascu.edfocus.student.ClassActivity_s;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ioana Pascu on 4/27/2018.
 */

public class ChildrenListAdapter extends ArrayAdapter<Child> {

    private static final String TAG = "ChildrenListAdapter";

    // variables
    private DatabaseReference mClassesRef;
    private ArrayList<Child> mChildren;
    private Context mContext;
    private int mResource;

    public ChildrenListAdapter(Context context, int resource, ArrayList<Child> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mClassesRef = FirebaseDatabase.getInstance().getReference().child("classes");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Child child = getItem(position);

        final ChildrenListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ChildrenListAdapter.ViewHolder();
            holder.mNameText = convertView.findViewById(R.id.text_name);
            holder.mClassesLayout = convertView.findViewById(R.id.layout_classes);
            holder.mPhotoImage = convertView.findViewById(R.id.profile_photo);

            convertView.setTag(holder);
        } else {
            holder = (ChildrenListAdapter.ViewHolder) convertView.getTag();
        }

        holder.mNameText.setText(child.getName());
        UniversalImageLoader.setImage(child.getPhoto(), holder.mPhotoImage, null);

        // click on child photo and name redirects to his profile
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to profile of tapped child
                Intent myIntent = new Intent(getContext(), ViewProfileActivity.class);
                myIntent.putExtra("userId", child.getId());
                mContext.startActivity(myIntent);
            }
        };
        holder.mNameText.setOnClickListener(listener);
        holder.mPhotoImage.setOnClickListener(listener);

        // inflate classes as views in the layout
        holder.mClassesLayout.removeAllViews();

        // if child is not enrolled to any class
        if (child.getClassIds().size() == 0) {
            final TextView classTextView = new TextView(mContext);
            classTextView.setText("Your child is not enrolled to any classes yet.");
            classTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.mClassesLayout.addView(classTextView);
        }

        for (final String classId : child.getClassIds()) {
            // get class info
            mClassesRef.child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Class c = dataSnapshot.getValue(Class.class);

                    final TextView classTextView = new TextView(mContext);
                    classTextView.setText(c.getName());
                    classTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    classTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(getContext(), ClassActivity_s.class);
                            myIntent.putExtra("classId", classId);
                            myIntent.putExtra("studentId", child.getId());
                            mContext.startActivity(myIntent);
                        }
                    });

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 6, 0, 6);

                    holder.mClassesLayout.addView(classTextView, layoutParams);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        return convertView;
    }

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        CircleImageView mPhotoImage;
        TextView mNameText;
        LinearLayout mClassesLayout;
    }

}

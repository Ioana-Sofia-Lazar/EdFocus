package com.ioanap.classbook.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Class;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassesListAdapter extends ArrayAdapter<Class> {

    private static final String TAG = "ClassesListAdapter";

    private ArrayList<Class> mClasses;
    private Context mContext;
    private int mResource;

    public ClassesListAdapter(Context context, int resource, ArrayList<Class> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get class information
        final String classId = getItem(position).getId();
        String name = getItem(position).getName();
        String school = getItem(position).getSchool();
        String description = getItem(position).getDescription();
        String photo = getItem(position).getPhoto();
        final String token = getItem(position).getToken();

        // create the class object with the information
        Class aClass = new Class(classId, name, school, description, photo, token);

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mName = convertView.findViewById(R.id.txt_name);
            holder.mSchool = convertView.findViewById(R.id.txt_school);
            holder.mDescription = convertView.findViewById(R.id.txt_description);
            holder.mPhoto = convertView.findViewById(R.id.img_photo);
            holder.mDelete = convertView.findViewById(R.id.img_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mName.setText(aClass.getName());
        holder.mSchool.setText(aClass.getSchool());
        holder.mDescription.setText(aClass.getDescription());
        UniversalImageLoader.setImage(aClass.getPhoto(), holder.mPhoto, null);

        // class wil be deleted from class settings
        holder.mDelete.setVisibility(View.GONE);
        // delete icon click
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show confirmation dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this class?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteClassFromDb(classId, token);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create and show alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();

            }
        });

        return convertView;
    }

    private void deleteClassFromDb(String classId, String token) {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map deleteMultiple = new HashMap();
        // delete class from database
        deleteMultiple.put("classes/" + currentUser + "/" + classId, null);
        // delete class courses
        deleteMultiple.put("classCourses/" + classId, null);
        // delete class schedule
        deleteMultiple.put("schedule/" + classId, null);
        // delete class token
        deleteMultiple.put("classTokens/" + token, null);

        mRootRef.updateChildren(deleteMultiple);

    }

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        ImageView mPhoto, mDelete;
        TextView mName, mSchool, mDescription;
    }

}

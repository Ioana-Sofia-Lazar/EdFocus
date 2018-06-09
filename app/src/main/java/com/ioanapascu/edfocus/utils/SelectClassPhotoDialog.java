package com.ioanapascu.edfocus.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ioanapascu.edfocus.R;

public class SelectClassPhotoDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "SelectClassPhoto";

    private static final int PICKFILE_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    OnPhotoSelectedListener mOnPhotoSelectedListener;
    private TextView mSelectPhoto, mTakePhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_profile_photo, container, false);

        mSelectPhoto = view.findViewById(R.id.dialog_choose_photo);
        mTakePhoto = view.findViewById(R.id.dialog_take_photo);

        // on click listeners
        mSelectPhoto.setOnClickListener(this);
        mTakePhoto.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // when selecting an image from memory
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "selected image from memory - uri: " + selectedImageUri);

            // send uri to PostFragment and dismiss dialog
            mOnPhotoSelectedListener.getImagePath(selectedImageUri);
            getDialog().dismiss();
        }

        // when taking a new photo with camera
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "took photo");
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            // send bitmap to PostFragment and dismiss dialog
            mOnPhotoSelectedListener.getImageBitmap(bitmap);
            getDialog().dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mSelectPhoto) {
            // allow user to browse the phone's memory and choose photo
            Log.d(TAG, "accessing phone memory");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICKFILE_REQUEST_CODE);
        }
        if (view == mTakePhoto) {
            // open camera
            Log.d(TAG, "opening camera");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnPhotoSelectedListener = (OnPhotoSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach - ClassCastException");
        }

        super.onAttach(context);
    }

    public interface OnPhotoSelectedListener {
        void getImagePath(Uri imagePath);

        void getImageBitmap(Bitmap bitmap);
    }

}

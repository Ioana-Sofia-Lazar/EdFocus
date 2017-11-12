package com.ioanap.classbook.teacher;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.SelectProfilePhotoDialog;
import com.ioanap.classbook.utils.UniversalImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener,
            SelectProfilePhotoDialog.OnPhotoSelectedListener {

    private static final String TAG = "EditProfileActivity";
    private static final int REQUEST_CODE = 1;

    // widgets
    private ImageView mCancelImageView, mSaveImageView, mEditProfilePhotoImageView;
    private TextView mEditProfilePhotoTextView;
    private EditText mFirstNameEditText, mLastNameEditText, mDescriptionEditText, mLocationEditText, mEmailEditText, mPhoneNumberEditText;

    private Context mContext;

    // variables
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private UserAccountSettings mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mContext = EditProfileActivity.this;

        setupWidgets();
        setupFirebase();

    }

    private void setupWidgets() {
        // widgets
        mEditProfilePhotoImageView = (ImageView) findViewById(R.id.edit_profile_photo);
        mEditProfilePhotoTextView = (TextView) findViewById(R.id.text_edit_profile_photo);
        mFirstNameEditText = (EditText) findViewById(R.id.edit_text_first_name);
        mLastNameEditText = (EditText) findViewById(R.id.edit_text_last_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_text_description);
        mLocationEditText = (EditText) findViewById(R.id.edit_text_location);
        mEmailEditText = (EditText) findViewById(R.id.edit_text_email);
        mPhoneNumberEditText = (EditText) findViewById(R.id.edit_text_phone_number);

        // toolbar buttons
        mCancelImageView = (ImageView) findViewById(R.id.image_cancel);
        mSaveImageView = (ImageView) findViewById(R.id.image_save);

        // on click listeners for toolbar buttons
        mCancelImageView.setOnClickListener(this);
        mSaveImageView.setOnClickListener(this);

        mEditProfilePhotoTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mCancelImageView) {
            finish();
        }
        if (view == mSaveImageView) {
            // save information
            saveProfileSettings();

            finish();
        }
        if (view == mEditProfilePhotoTextView) {
            // verify permissions
            verifyPermissions();

        }
    }

    /**
     * Retrieves info entered by the user and saves it to the database.
     */
    private void saveProfileSettings() {
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
        String location = mLocationEditText.getText().toString();
        String phoneNumber = mPhoneNumberEditText.getText().toString();

        if (!mSettings.getLastName().equals(lastName)) {
            updateUserAccountSettings(lastName, null, null, null, null, null);
        }
        if (!mSettings.getFirstName().equals(firstName)) {
            updateUserAccountSettings(null, firstName, null, null, null, null);
        }
        if (!mSettings.getDescription().equals(description)) {
            updateUserAccountSettings(null, null, description, null, null, null);
        }
        if (!mSettings.getLocation().equals(location)) {
            updateUserAccountSettings(null, null, null, location, null, null);
        }
        if (!mSettings.getPhoneNumber().equals(phoneNumber)) {
            updateUserAccountSettings(null, null, null, null, phoneNumber, null);
        }

        if (mSelectedBitmap != null && mSelectedUri == null) {
            compressThenUploadNewPhoto(mSelectedBitmap);
        } else if (mSelectedBitmap == null && mSelectedUri != null) {
            compressThenUploadNewPhoto(mSelectedUri);
        }

    }

    /**
     * Fill the widgets from the Profile with the information from Firebase
     *
     * @param settings
     */
    private void setEditProfileWidgets(UserAccountSettings settings) {
        mSettings = settings;

        mFirstNameEditText.setText(settings.getFirstName());
        mLastNameEditText.setText(settings.getLastName());
        mDescriptionEditText.setText(settings.getDescription());
        mLocationEditText.setText(settings.getLocation());
        mEmailEditText.setText(settings.getEmail());
        mPhoneNumberEditText.setText(settings.getPhoneNumber());

        setProfilePhoto(settings.getProfilePhoto());
    }

    private void setProfilePhoto(String url) {
        UniversalImageLoader.setImage(url, mEditProfilePhotoImageView, null);
    }

    /**
     * Firebase instances, references and listener for when updates in the user's settings are
     * made in the database.
     */
    private void setupFirebase() {
        Log.d(TAG, "setupFirebase");

        mSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve user info
                UserAccountSettings settings = getUserAccountSettings(dataSnapshot);

                // setup widgets to display user info from the database
                setEditProfileWidgets(settings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Verifies permissions for accessing storage and camera.
     * Explicitly asks for permissions if they are not granted yet.
     */
    private void verifyPermissions() {
        Log.d(TAG, "asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED) {

            // all permissions granted - show select photo dialog
            SelectProfilePhotoDialog dialog = new SelectProfilePhotoDialog();
            dialog.show(getSupportFragmentManager(), "SelectPhoto");

        } else {
            // explicitly ask for permissions
            ActivityCompat.requestPermissions(EditProfileActivity.this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    /**
     * Image uri is going to show up here after the dialog has closed and the user has selected an image.
     *
     * @param imagePath
     */
    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image to imageview");
        UniversalImageLoader.setImage(imagePath.toString(), mEditProfilePhotoImageView, null);
        //assign to global variable
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    /**
     * Image bitmap is going to show up here after the dialog has closed and the user has taken a photo.
     *
     * @param bitmap
     */
    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to imageview");
        mEditProfilePhotoImageView.setImageBitmap(bitmap);
        //assign to a global variable
        mSelectedUri = null;
        mSelectedBitmap = bitmap;
    }

    /**
     * Compresses image (background task to prevent slowing down the UI).
     */
    public class ImageCompressionInBackground extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;

        public ImageCompressionInBackground(Bitmap bitmap) {
            if(bitmap != null){
                this.mBitmap = bitmap;
            }
        }

        // runs on the main thread, not in background
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(mContext, "Compressing image", Toast.LENGTH_SHORT).show();
            showProgressDialog("");
        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            Log.d(TAG, "doInBackground: started.");

            if(mBitmap == null){
                // we have a uri
                try {
                    // get bitmap from the uri (we need a bitmap for uploading)
                    mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), params[0]);
                } catch (IOException e){
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }
            }
            byte[] bytes = null;
            bytes = bitmapToBytes(mBitmap, 100);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);

            hideProgressDialog();

            // execute the upload to firebase task
            uploadProfilePhoto(bytes);
        }
    }

    private void compressThenUploadNewPhoto(Bitmap bitmap){
        Log.d(TAG, "uploadNewPhoto: uploading a new image bitmap to storage");
        ImageCompressionInBackground compress = new ImageCompressionInBackground(bitmap);
        Uri uri = null;
        compress.execute(uri);
    }

    private void compressThenUploadNewPhoto(Uri imagePath){
        Log.d(TAG, "uploadNewPhoto: uploading a new image uri to storage.");
        ImageCompressionInBackground resize = new ImageCompressionInBackground(null);
        resize.execute(imagePath);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream);
        return stream.toByteArray();
    }

}

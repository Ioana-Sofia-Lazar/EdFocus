package com.ioanapascu.edfocus.teacher;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Class;
import com.ioanapascu.edfocus.utils.SelectProfilePhotoDialog;
import com.ioanapascu.edfocus.utils.UniqueStringGenerator;
import com.ioanapascu.edfocus.utils.UniversalImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddClassActivity extends BaseActivity implements View.OnClickListener,
        SelectProfilePhotoDialog.OnPhotoSelectedListener {

    private static final String TAG = "AddClassActivity";
    private static final int REQUEST_CODE = 1;

    // widgets
    private ImageView mPhoto, mDeleteButton, mCopyIcon;
    private Button mCreateButton;
    private EditText mNameText, mSchoolText, mDescriptionText, mTokenText;
    private TextView mAddPhotoText, mTitleText, mInfoText;
    private LinearLayout mTokenLayout;

    // variables
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private String mClassId, mOldToken, mOldPhoto;
    private int mMode = 0; // 0 when creating new class, 1 when editing class

    public static byte[] bitmapToBytes(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(AddClassActivity.this, false);
        setContentView(R.layout.activity_add_class);

        // classId will be null if we are creating a new class or the id of the class that the user is editing
        mClassId = getIntent().getStringExtra("classId");

        // widgets
        mNameText = findViewById(R.id.txt_name);
        mSchoolText = findViewById(R.id.txt_school);
        mDescriptionText = findViewById(R.id.txt_description);
        mTitleText = findViewById(R.id.text_title);
        mInfoText = findViewById(R.id.text_info);
        mTokenText = findViewById(R.id.txt_token);
        mTokenLayout = findViewById(R.id.layout_token);
        mAddPhotoText = findViewById(R.id.txt_add_photo);
        mPhoto = findViewById(R.id.img_photo);
        mCreateButton = findViewById(R.id.btn_create);
        mDeleteButton = findViewById(R.id.btn_delete);
        mCopyIcon = findViewById(R.id.img_copy_icon);

        // hide widgets according to mode (create or edit)
        if (mClassId != null) {
            // edit mode
            mMode = 1;
            mInfoText.setText(R.string.class_token_info);
            mTitleText.setText("Edit Class");
            mCreateButton.setText("Save");
            displayClassInfo();
        } else {
            // create mode
            mTokenLayout.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.GONE);
        }

        // listeners
        mAddPhotoText.setOnClickListener(this);
        mCreateButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mCopyIcon.setOnClickListener(this);
    }

    private void displayClassInfo() {
        // load event info and display it in widgets
        mClassesRef.child(mClassId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Class aClass = dataSnapshot.getValue(Class.class);

                        mNameText.setText(aClass.getName());
                        mSchoolText.setText(aClass.getSchool());
                        mDescriptionText.setText(aClass.getDescription());
                        mTokenText.setText(aClass.getToken());
                        UniversalImageLoader.setImage(aClass.getPhoto(), mPhoto, null);
                        mOldToken = aClass.getToken();
                        mOldPhoto = aClass.getPhoto();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
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
            ActivityCompat.requestPermissions(AddClassActivity.this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    @Override
    public void onClick(View view) {
        if (view == mCopyIcon) {
            // copy token to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("classToken", mTokenText.getText());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }

            // show message toast
            Toast.makeText(AddClassActivity.this, "Token copied to Clipboard!",
                    Toast.LENGTH_LONG).show();
        }
        if (view == mAddPhotoText) {
            // verify permissions to access camera and storage
            verifyPermissions();
        }
        if (view == mCreateButton) {
            saveClass();
        }
        if (view == mDeleteButton) {
            // show confirmation dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete this class?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteClassFromDb(mClassId, mOldToken);
                            dialog.dismiss();
                            finish();
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
    }

    private void saveClass() {
        String name = mNameText.getText().toString();
        String school = mSchoolText.getText().toString();
        String description = mDescriptionText.getText().toString();

        if (mClassId == null) {
            // creating new class
            // get id where to put the new class in firebase
            mClassId = mClassesRef.push().getKey();
        }

        Class aClass = new Class(mClassId, name, school, description, mOldPhoto, mOldToken, CURRENT_USER_ID);

        // save to the database
        mClassesRef.child(mClassId).setValue(aClass);
        mUserClassesRef.child(CURRENT_USER_ID).child(mClassId).setValue(mClassId);

        // compress and save class photo to database
        if (mSelectedBitmap != null && mSelectedUri == null) {
            compressThenUploadPhoto(mSelectedBitmap);
        } else if (mSelectedBitmap == null && mSelectedUri != null) {
            compressThenUploadPhoto(mSelectedUri);
        }

        if (mMode == 0) {
            generateUniqueCode();
        } else {
            finish();
        }
    }

    private void deleteClassFromDb(final String classId, String token) {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // delete class from all students of the class
        mClassStudentsRef.child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) { // for each student of the class
                    // delete from user classes
                    mUserClassesRef.child(data.getKey()).child(classId).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Map<String, Object> deleteMultiple = new HashMap<>();
        // delete class information
        deleteMultiple.put("classes/" + classId, null);
        // delete class from this user (teacher)
        deleteMultiple.put("userClasses/" + currentUser + "/" + classId, null);
        // delete class courses
        deleteMultiple.put("classCourses/" + classId, null);
        // delete class schedule
        deleteMultiple.put("schedule/" + classId, null);
        // delete class token
        deleteMultiple.put("classTokens/" + token, null);
        // delete class events
        deleteMultiple.put("classEvents/" + classId, null);
        // delete class files
        deleteMultiple.put("classFiles/" + classId, null);
        // delete class students
        deleteMultiple.put("classStudents/" + classId, null);
        // delete student grades for this class
        deleteMultiple.put("studentGrades/" + classId, null);
        // delete student absences for this class
        deleteMultiple.put("studentAbsences/" + classId, null);

        mRootRef.updateChildren(deleteMultiple);

    }

    /**
     * Generates unique code for class, that the students will use to enroll to this class.
     */
    private void generateUniqueCode() {
        final String token = UniqueStringGenerator.nextString();

        // check if the code already exists
        mClassTokensRef.child(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // call method again
                    generateUniqueCode();
                } else {
                    // otherwise add token to db - class tokens
                    Map<String, Object> node = new HashMap<>();
                    node.put("classId", mClassId);
                    mClassTokensRef.child(token).updateChildren(node);

                    // add to classes ref
                    mClassesRef.child(mClassId).child("token").setValue(token);

                    redirectToActivity(token);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void redirectToActivity(String token) {
        // redirect to activity sending the token as extra
        Intent myIntent = new Intent(getApplicationContext(), ClassTokenActivity.class);
        myIntent.putExtra("token", token);
        myIntent.putExtra("classId", mClassId);
        startActivity(myIntent);

        finish();
    }

    private void compressThenUploadPhoto(Bitmap bitmap) {
        Log.d(TAG, "uploadPhoto: uploading a new image bitmap to storage");
        AddClassActivity.ImageCompressionInBackground compress = new AddClassActivity.ImageCompressionInBackground(bitmap);
        Uri uri = null;
        compress.execute(uri);
    }

    private void compressThenUploadPhoto(Uri imagePath) {
        Log.d(TAG, "uploadPhoto: uploading a new image uri to storage.");
        AddClassActivity.ImageCompressionInBackground resize = new AddClassActivity.ImageCompressionInBackground(null);
        resize.execute(imagePath);
    }

    /**
     * Image uri is going to show up here after the dialog has closed and the user has selected an image.
     *
     * @param imagePath
     */
    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image to imageview");
        UniversalImageLoader.setImage(imagePath.toString(), mPhoto, null);
        //assign to global variable
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    /**
     * Saves photo to the database.
     */
    private void uploadClassPhoto(byte[] bytes) {
        // add photo to directory in firebase storage
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("photos/" + CURRENT_USER_ID + "/classes/" + mClassId + "/classPhoto");
        UploadTask uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get image url
                Uri firebaseUri = taskSnapshot.getDownloadUrl();

                // save image url to firebase database
                mClassesRef.child(mClassId).child("photo").setValue(firebaseUri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Could not upload photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Image bitmap is going to show up here after the dialog has closed and the user has taken a photo.
     *
     * @param bitmap
     */
    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to imageview");
        mPhoto.setImageBitmap(bitmap);
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
            if (bitmap != null) {
                this.mBitmap = bitmap;
            }
        }

        // runs on the main thread, not in background
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            if (mBitmap == null) {
                // we have a uri
                try {
                    // get bitmap from the uri (we need a bitmap for uploading)
                    mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), params[0]);
                } catch (IOException e) {
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

            // upload to firebase
            uploadClassPhoto(bytes);
        }
    }
}

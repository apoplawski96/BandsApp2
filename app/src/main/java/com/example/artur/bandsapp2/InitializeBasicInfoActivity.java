package com.example.artur.bandsapp2;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class InitializeBasicInfoActivity extends AppCompatActivity {

    //Final variables
    private static final int PICK_IMAGE_REQUEST = 666;

    //Widgets
    EditText firstName, lastName, phoneNumber, city;
    Button submitButton;
    ImageView addPhotoImage;
    TextView uploadPhoto;

    //Firebase stuff
    DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String userID;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize_basic_info);

        //Widgets
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        city = (EditText) findViewById(R.id.city);
        submitButton = (Button) findViewById(R.id.submitButton);
        uploadPhoto = (TextView) findViewById(R.id.upload_photo);
        addPhotoImage = (ImageView) findViewById(R.id.roundedPhoto);

        //Firebase stuff
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
                SubmitDataToDatabase();
            }
        });

    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_REQUEST);
    }

    private void uploadFile(){

        if (filePath!=null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //Getting storage reference
            StorageReference ref = mStorageRef.child("avatars/" + userID + "/avatar." + getImageExt(filePath));

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image uploaded.", Toast.LENGTH_SHORT).show();
                            ImageUpload imageUpload = new ImageUpload(userID, taskSnapshot.getDownloadUrl().toString());
                            mDatabaseRef.child("Users").child(userID).child("Image Info").setValue(imageUpload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = ( (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount()  );
                            progressDialog.setMessage("Uploaded" + (int) progress + "%");
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK) && (data != null) && (data.getData() != null)){
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                addPhotoImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    //Method that returns image extension
    public String getImageExt (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void EmptyFieldToast(){
        Toast.makeText(this, "Please fill all the necessary fields", Toast.LENGTH_SHORT).show();
        return;
    }

    private void SubmitDataToDatabase(){
        String firstNameString = firstName.getText().toString().trim();
        String lastNameString = lastName.getText().toString().trim();
        String phoneNumberString = phoneNumber.getText().toString().trim();
        String cityString = city.getText().toString().trim();

        //User errors check
        if (TextUtils.isEmpty(firstNameString)) EmptyFieldToast();
        if (TextUtils.isEmpty(lastNameString)) EmptyFieldToast();

        mDatabaseRef.child("Users").child(userID).child("First Name").setValue(firstNameString);
        mDatabaseRef.child("Users").child(userID).child("Last Name").setValue(lastNameString);
        mDatabaseRef.child("Users").child(userID).child("Phone Number").setValue(phoneNumberString);
        mDatabaseRef.child("Users").child(userID).child("City").setValue(cityString);

        changeActivity();
    }

    void changeActivity(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ifReady = dataSnapshot.child("Users").child(userID).child("Ready to go").getValue().toString();
                if (ifReady.equals("Yes")){
                    startActivity(new Intent(InitializeBasicInfoActivity.this, ProfileViewActivity.class));
                } else{
                    startActivity(new Intent(InitializeBasicInfoActivity.this, GenresSearchDialogActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

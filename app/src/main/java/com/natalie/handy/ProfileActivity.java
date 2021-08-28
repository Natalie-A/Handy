package com.natalie.handy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import org.jetbrains.annotations.NotNull;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private Button postProfile;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseClient;
    //Declare an Instance of the Storage reference where we will upload the photo
    private StorageReference mStorageRef;
    // Declare an Instance of URI for getting the image from our phone, initialize it to null
    private Uri profileImageUri = null;
    // Declare and initialize a private final static int that will serve as our request code
    private final static int GALLERY_REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        imageButton = findViewById(R.id.post_profile_image);
        postProfile = findViewById(R.id.btn_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //We want to set the profile for specific, hence get the user id of the current user and assign it to a string variable
        final String userID = firebaseUser.getUid();
        //Initialize the database reference where you have your registered users and get the specific user reference using the user ID
        mDatabaseClient = FirebaseDatabase.getInstance().getReference().child("clients").child(userID);
        //Initialize the firebase storage reference where you will store the profile photo images
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        //set on click listener on the image button so as to allow users to pick their profile photo from their gallery
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an implicit intent for getting the images
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //set the type to images only
                galleryIntent.setType("image/*");
                //since we need results, use the method startActivityForResult() and pass the intent and request code you initialized
                startActivityForResult(galleryIntent, GALLERY_REQ);
            }
        });
        //on clicking the images we want to get the the profile photo,
        // then later save this on a database reference for a specific user
        postProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate to ensure that the profile image are not null
                if (profileImageUri != null) {
                    //create Storage reference node, inside profile_image storage reference where you will save the profile image
                    StorageReference profileImagePath = mStorageRef.child(profileImageUri.getLastPathSegment());
                    //call the putFile() method passing the profile image the user set on the storage reference where you are uploading the image
                    //further call addOnSuccessListener on the reference to listen if the upload task was successful,and get a snapshot of the task
                    profileImagePath.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload of the profile image was successful get the download url
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    //get the download url from your storage, use the methods getStorage() and getDownloadUrl()
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    //call the method addOnSuccessListener to determine if we got the download url
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //convert the uri to a string on success
                                            final String profileImage = uri.toString();
                                            // call the method push() to add values on the database reference of a specific user
                                            mDatabaseClient.push();
                                            //add the profilePhoto for the current user
                                            mDatabaseClient.child("profilePhoto").setValue(profileImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //show a toast to indicate the profile was updated
                                                        Toast.makeText(ProfileActivity.this, "Profile Inserted", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //override this method to get the profile image set it in the image button view
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ && resultCode == Activity.RESULT_OK) {
            //get the image selected by the user
            profileImageUri = data.getData();
            //set in the image button view
            imageButton.setImageURI(profileImageUri);
        }
    }
}
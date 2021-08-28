package com.natalie.handy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class UpdateProfileFragment extends Fragment {

    private ImageButton updateProfile;
    private Button save, cancel;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseClient;
    //Declare an Instance of the Storage reference where we will upload the photo
    private StorageReference mStorageRef;
    // Declare an Instance of URI for getting the image from our phone, initialize it to null
    private Uri profileImageUri = null;
    // Declare and initialize a private final static int that will serve as our request code
    private final static int GALLERY_REQ = 1;
    private String profile_url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        //Hooks
        updateProfile = (ImageButton) view.findViewById(R.id.update_photo);
        save = (Button) view.findViewById(R.id.btn_save);
        cancel = (Button) view.findViewById(R.id.btn_cancel);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //We want to set the profile for specific, hence get the user id of the current user and assign it to a string variable
        final String userID = firebaseUser.getUid();
        //Initialize the database reference where you have your registered users and get the specific user reference using the user ID
        mDatabaseClient = FirebaseDatabase.getInstance().getReference().child("clients").child(userID);

        mDatabaseClient.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                profile_url = snapshot.child("profilePhoto").getValue(String.class);
                Picasso.with(getActivity()).load(profile_url).into(updateProfile);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        //Initialize the firebase storage reference where you will store the profile photo images
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        //set on click listener on the image button so as to allow users to pick their profile photo from their gallery
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an implicit intent for getting the images
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //set the type to images only
                galleryIntent.setType("image/*");
                //since we need results, use the method startActivityForResult() and pass the intent and request code you initialized
                startActivityForResult(galleryIntent, GALLERY_REQ);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
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
                                            HashMap hashMap = new HashMap();
                                            hashMap.put("profilePhoto", profileImage);
                                            mDatabaseClient.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                    //show a toast to indicate the profile was updated
                                                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(getActivity(), MainActivity2.class);
                                                    startActivity(intent);
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
                    Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity3.class);
                startActivity(intent);
            }
        });
        return view;
    }

    //override this method to get the profile image set it in the image button view
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ && resultCode == Activity.RESULT_OK) {
            //get the image selected by the user
            profileImageUri = data.getData();
            //set in the image button view
            updateProfile.setImageURI(profileImageUri);
        }
    }
}
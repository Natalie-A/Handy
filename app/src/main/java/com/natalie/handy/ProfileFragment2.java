package com.natalie.handy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ProfileFragment2 extends Fragment {

    private TextInputEditText full_name, email_address, phone_number, location;
    private TextView rating;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Button reset_password, update_button, delete_button;
    private RatingBar rating_score;
    private Float ratingScore;
    private ImageView imageView;
    private AlertDialog.Builder reset_alert;
    //Declare an Instance of the database reference where we will be saving the profile photo and custom display name
    private DatabaseReference mDatabaseUser;
    //Declare an Instance of the Storage reference where we will upload the photo
    private StorageReference mStorageRef;
    // Declare an Instance of URI for getting the image from our phone, initialize it to null
    private Uri profileImageUri = null;
    // Declare and initialize a private final static int that will serve as our request code
    private final static int GALLERY_REQ = 1;

    private String nameFromDb, locationFromDb, phoneFromDb, profile_url, serviceFromDb;

    public ProfileFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ProgressDialog Dialog = new ProgressDialog(getContext());
        Dialog.setMessage("Loading...");
        Dialog.show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile2, container, false);
        //Hooks
        full_name = (TextInputEditText) view.findViewById(R.id.full_name);
        email_address = (TextInputEditText) view.findViewById(R.id.email);
        phone_number = (TextInputEditText) view.findViewById(R.id.phone);
        location = (TextInputEditText) view.findViewById(R.id.location);
        imageView = (ImageView) view.findViewById(R.id.profile_image);
        update_button = (Button) view.findViewById(R.id.btn_update);
        delete_button = (Button) view.findViewById(R.id.btn_delete);
        rating_score = (RatingBar) view.findViewById(R.id.rating_score);
        rating = (TextView) view.findViewById(R.id.rating);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //We want to set the profile for specific, hence get the user id of the current user and assign it to a string variable
        final String userID = firebaseUser.getUid();
        //Initialize the database reference where you have your registered users and get the specific user reference using the user ID
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("handypersons").child(userID);

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                nameFromDb = snapshot.child("full_name").getValue(String.class);
                locationFromDb = snapshot.child("location").getValue(String.class);
                phoneFromDb = snapshot.child("phone_number").getValue(String.class);
                profile_url = snapshot.child("profilePhoto").getValue(String.class);
                serviceFromDb = snapshot.child("service_offered").getValue(String.class);
                ratingScore = snapshot.child("rating_score").getValue(Float.class);
                Dialog.dismiss();
                full_name.setText(nameFromDb);
                location.setText(locationFromDb);
                phone_number.setText(phoneFromDb);
                rating_score.setRating(ratingScore);
                rating.setText("Rating Score:  " + ratingScore.toString());
                Picasso.with(getActivity()).load(profile_url).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
        email_address.setText(firebaseUser.getEmail());
        email_address.setEnabled(false);
        //Initialize the firebase storage reference where you will store the profile photo images
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        //update user details
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNameChanged() || isLocationChanged() || isPhoneChanged()) {
                    Toast.makeText(getActivity(), "Data has been updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Data is same and can not be updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset_alert = new AlertDialog.Builder(getActivity());
        reset_password = (Button) view.findViewById(R.id.btn_reset);
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_pop, null);
                //start alert dialog
                reset_alert.setTitle("Reset Password ?").setMessage("Please enter your email address to get the password reset link").setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //validate the email address
                        EditText email = view.findViewById(R.id.email);
                        if (email.getText().toString().isEmpty()) {
                            email.setError("Required Field");
                            return;
                        }
                        //send the reset link
                        firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Reset password email sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", null).setView(view).create().show();
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_alert.setTitle("Delete account?").setMessage("Deleting this account will result in complete data loss").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //this is where we delete the user
                        HashMap hashMap = new HashMap();
                        hashMap.put("accountStatus", "disabled");
                        mDatabaseUser.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                }).setNegativeButton("Cancel", null).create().show();
            }
        });
        return view;
    }

    private boolean isPhoneChanged() {
        if (!phoneFromDb.equals(phone_number.getText().toString())) {
            mDatabaseUser.child("phone_number").setValue(phone_number.getText().toString());
            phoneFromDb = phone_number.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isLocationChanged() {
        if (!locationFromDb.equals(location.getText().toString())) {
            mDatabaseUser.child("location").setValue(location.getText().toString());
            locationFromDb = location.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isNameChanged() {
        if (!nameFromDb.equals(full_name.getText().toString())) {
            mDatabaseUser.child("full_name").setValue(full_name.getText().toString());
            nameFromDb = full_name.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    //override this method to get the profile image set it in the image button view
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQ && resultCode == Activity.RESULT_OK) {
            //get the image selected by the user
            profileImageUri = data.getData();
            //set in the image view
            imageView.setImageURI(profileImageUri);
        }
    }
}
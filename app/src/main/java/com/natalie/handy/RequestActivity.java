package com.natalie.handy;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class RequestActivity extends AppCompatActivity {

    private TextView usernameTV, phoneTv;
    private ImageView profileImage;
    private Button btnCall, btnRequest;
    private DatabaseReference mDatabaseHandy, mDatabaseRequests;
    private String phoneNumber, user_id, clientID, request_date;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private EditText requestDate;
    // Declare the variables to hold the selected date(YEAR, MONTH, DAY)
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        //Getting the intents sent from main activity
        Intent intent = getIntent();
        final String handymanName = intent.getStringExtra(RecyclerViewAdapter.KEY_NAME);
        //initialize
        usernameTV = findViewById(R.id.usernameTextView);
        phoneTv = findViewById(R.id.handyman_phone_number);
        profileImage = findViewById(R.id.profileImageView);
        btnCall = findViewById(R.id.btn_call);
        btnRequest = findViewById(R.id.btn_request);
        requestDate = findViewById(R.id.request_date);

        requestDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                //6.4 Declare a data picker dialog to pick selected date
                DatePickerDialog datePickerDialog = new DatePickerDialog(RequestActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        requestDate.setText(dayOfMonth + "-" + (month + 1 + "-" + year));
                        request_date = (dayOfMonth + "-" + (month + 1 + "-" + year));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                //Show the date picker dialog
                datePickerDialog.show();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        clientID = firebaseUser.getUid();

        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("requests");

        usernameTV.setText(handymanName);
        mDatabaseHandy = FirebaseDatabase.getInstance().getReference().child("handypersons");
        mDatabaseHandy.orderByChild("full_name").equalTo(handymanName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    user_id = ds.getKey();
                    mDatabaseHandy.child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            phoneNumber = snapshot.child("phone_number").getValue().toString();
                            String profile_url = snapshot.child("profilePhoto").getValue().toString();
                            phoneTv.setText(phoneNumber);
                            Picasso.with(RequestActivity.this).load(profile_url).into(profileImage);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri phoneUri = Uri.parse("tel:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL, phoneUri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.d("ImplicitIntents", "Can't handle this intent!");
                }
            }
        });
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestDate.getText().toString().isEmpty()) {
                    Toast.makeText(RequestActivity.this, "Please select the date", Toast.LENGTH_SHORT).show();
                } else {
                    //insert values into db
                    Request request = new Request(clientID, user_id, "waitingForAccept", request_date);
                    mDatabaseRequests.push().setValue(request);
                    Toast.makeText(RequestActivity.this, "Request was made successfully", Toast.LENGTH_SHORT).show();
                    Intent open = new Intent(RequestActivity.this, MainActivity2.class);
                    startActivity(open);
                }
            }

        });
    }
}
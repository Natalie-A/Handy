package com.natalie.handy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HandymenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<Handyman> handymenArrayList;
    private String serviceKey;
    private DatabaseReference mDatabaseService, mDatabaseHandy;
    private HandymanRecyclerViewAdapter handymanRecyclerViewAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handymen);
        //Getting the intents sent from main activity
        Intent intent = getIntent();
        final String serviceName = intent.getStringExtra(RecyclerViewAdapter.KEY_NAME);
        //initialize
        recyclerView = findViewById(R.id.handymanRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        handymenArrayList = new ArrayList<>();

        mDatabaseService = FirebaseDatabase.getInstance().getReference().child("services");
        mDatabaseHandy = FirebaseDatabase.getInstance().getReference().child("handypersons");
        mDatabaseService.orderByChild("service_name").equalTo(serviceName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    serviceKey = childSnapshot.getKey();
                }
                mDatabaseService.child(serviceKey).child("handypersons").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String user_id = item.getKey();
                            mDatabaseHandy.child(user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if(snapshot.child("accountStatus").getValue(String.class).equals("enabled")){
                                        String name = snapshot.child("full_name").getValue().toString();
                                        String location = snapshot.child("location").getValue().toString();
                                        Float rating = snapshot.child("rating_score").getValue(Float.class);
                                        Handyman handyman = new Handyman(name, location, rating.toString());
                                        handymenArrayList.add(handyman);
                                        handymanRecyclerViewAdapter = new HandymanRecyclerViewAdapter(HandymenActivity.this, handymenArrayList);
                                        recyclerView.setAdapter(handymanRecyclerViewAdapter);
                                    }
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
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
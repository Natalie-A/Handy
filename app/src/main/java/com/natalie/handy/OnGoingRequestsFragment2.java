package com.natalie.handy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OnGoingRequestsFragment2 extends Fragment {

    private DatabaseReference mDatabaseRequests, mDatabaseClients;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String handypersonID, clientID, requestDate, name;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<OngoingRequest> ongoingRequestArrayList;
    private OngoingRequestAdapter ongoingRequestAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_going_requests2, container, false);

        //initialize
        recyclerView = view.findViewById(R.id.ongoingRequest);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        ongoingRequestArrayList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        handypersonID = firebaseUser.getUid();
        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("requests");
        mDatabaseClients = FirebaseDatabase.getInstance().getReference().child("clients");
        mDatabaseRequests.orderByChild("handymanId").equalTo(handypersonID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("status").getValue(String.class).equals("Accepted")){
                        clientID = ds.child("clientId").getValue().toString();
                        mDatabaseClients.child(clientID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                name = snapshot.child("full_name").getValue().toString();
                                requestDate = ds.child("requestDate").getValue().toString();
                                OngoingRequest ongoingRequest = new OngoingRequest(name,requestDate);
                                ongoingRequestArrayList.add(ongoingRequest);
                                ongoingRequestAdapter = new OngoingRequestAdapter(getContext(), ongoingRequestArrayList);
                                recyclerView.setAdapter(ongoingRequestAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }
}
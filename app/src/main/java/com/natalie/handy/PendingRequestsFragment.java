package com.natalie.handy;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PendingRequestsFragment extends Fragment {

    private DatabaseReference mDatabaseRequests, mDatabaseHandypersons;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String handypersonID, clientID, requestDate, name, phoneNumber;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<PendingRequest> pendingRequestArrayList;
    private PendingRequestsRecyclerViewAdapter viewAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ProgressDialog Dialog = new ProgressDialog(getContext());
        Dialog.setMessage("Loading...");
        Dialog.show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_requests, container, false);

        //initialize
        recyclerView = view.findViewById(R.id.pendingRequest);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        pendingRequestArrayList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        clientID = firebaseUser.getUid();
        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("requests");
        mDatabaseHandypersons = FirebaseDatabase.getInstance().getReference().child("handypersons");

        mDatabaseRequests.orderByChild("clientId").equalTo(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    Dialog.dismiss();
                    Toast.makeText(getContext(), "You have no requests", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("status").getValue(String.class).equals("waitingForAccept")) {
                        handypersonID = ds.child("handymanId").getValue().toString();
                        mDatabaseHandypersons.child(handypersonID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                Dialog.dismiss();
                                name = snapshot.child("full_name").getValue().toString();
                                phoneNumber = snapshot.child("phone_number").getValue().toString();
                                requestDate = ds.child("requestDate").getValue().toString();
                                PendingRequest pendingRequest = new PendingRequest(name, requestDate,phoneNumber);
                                pendingRequestArrayList.add(pendingRequest);
                                viewAdapter = new PendingRequestsRecyclerViewAdapter(getContext(), pendingRequestArrayList);
                                recyclerView.setAdapter(viewAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }else{
                        Dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return  view;
    }
}
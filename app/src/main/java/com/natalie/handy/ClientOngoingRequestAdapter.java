package com.natalie.handy;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientOngoingRequestAdapter extends RecyclerView.Adapter<ClientOngoingRequestAdapter.MyViewHolder> {
    private DatabaseReference mDatabaseRequests, mDatabaseHandypersons, mDatabaseRatings;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private AlertDialog.Builder rating_alert;
    private String clientID, handymanID;
    private Float sumRatings = Float.valueOf(0), totalRatings, avgRatings;

    Context context;
    ArrayList<OngoingRequest> arr;

    public ClientOngoingRequestAdapter(Context context, ArrayList<OngoingRequest> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ongoingrequests_items_client, parent, false);
        ClientOngoingRequestAdapter.MyViewHolder myViewHolder = new ClientOngoingRequestAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ClientOngoingRequestAdapter.MyViewHolder holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        rating_alert = new AlertDialog.Builder(context);

        clientID = firebaseUser.getUid();
        mDatabaseHandypersons = FirebaseDatabase.getInstance().getReference().child("handypersons");
        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("requests");
        mDatabaseRatings = FirebaseDatabase.getInstance().getReference().child("ratings");
        final OngoingRequest ongoingRequest = arr.get(position);
        holder.nameTextView.setText(ongoingRequest.getName());
        holder.dateTextView.setText(ongoingRequest.getDate());

        mDatabaseHandypersons.orderByChild("full_name").equalTo(ongoingRequest.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handymanID = dataSnapshot.getKey();
                            //use client id and handyman id to get the request id
                            mDatabaseRequests.orderByChild("client_handyman_status").equalTo(clientID + "_" + handymanID + "_accepted").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("status", "Cancelled");
                                        mDatabaseRequests.child(ds.getKey()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                //show a toast to indicate the request was accepted
                                                mDatabaseRequests.child(ds.getKey()).child("client_handyman_status").setValue(clientID + "_" + handymanID + "_cancelled");
                                                Toast.makeText(v.getContext(), "Request Updated", Toast.LENGTH_SHORT).show();
                                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                                Fragment myFragment = new HistoryFragment();
                                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, myFragment).addToBackStack(null).commit();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    holder.btnComplete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handymanID = dataSnapshot.getKey();
                            //use client id and handyman id to get the request id
                            mDatabaseRequests.orderByChild("client_handyman_status").equalTo(clientID + "_" + handymanID + "_accepted").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("status", "Completed");
                                        mDatabaseRequests.child(ds.getKey()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                //show a toast to indicate the request was accepted
                                                mDatabaseRequests.child(ds.getKey()).child("client_handyman_status").setValue(clientID + "_" + handymanID + "_completed");
                                                //open dialog and send rating score to ratings table
                                                View pop = LayoutInflater.from(context).inflate(R.layout.rating_pop, null);
                                                //start alert dialog
                                                rating_alert.setTitle("Rate Service").setMessage("Please rate the quality of service that was offered to you").setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //validate the email address
                                                        RatingBar bar = pop.findViewById(R.id.rating_bar);
                                                        //insert rating score to database and update handypersons rating score**
                                                        mDatabaseRatings.orderByChild("handymanId").equalTo(handymanID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                                if (snapshot.getChildrenCount() == 0) {
                                                                    sumRatings = bar.getRating();
                                                                    avgRatings = sumRatings;
                                                                    //Toast.makeText(pop.getContext(), String.valueOf(avgRatings), Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    for (DataSnapshot data : snapshot.getChildren()) {
                                                                        totalRatings = Float.valueOf(snapshot.getChildrenCount());
                                                                        Float rating = Float.parseFloat(data.child("ratingScore").getValue().toString());
                                                                        sumRatings += rating;
                                                                    }
                                                                    avgRatings = sumRatings / (totalRatings);
                                                                }
                                                                HashMap hashMap = new HashMap();
                                                                hashMap.put("rating_score", avgRatings);
                                                                mDatabaseHandypersons.child(handymanID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                                                    @Override
                                                                    public void onSuccess(Object o) {
                                                                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                                                        Fragment myFragment = new HistoryFragment();
                                                                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
                                                                        Toast.makeText(activity.getApplication(), "Thank you for your response", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                            }
                                                        });
                                                        Ratings ratings = new Ratings(clientID, handymanID, String.valueOf(bar.getRating()), ds.getKey());
                                                        mDatabaseRatings.push().setValue(ratings);
                                                    }
                                                }).setNegativeButton("Cancel", null).setView(pop).create().show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
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
    public int getItemCount() {
        return arr.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView, dateTextView;
        private Button btnCancel, btnComplete;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name2);
            dateTextView = (TextView) itemView.findViewById(R.id.requestDate3);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel2);
            btnComplete = (Button) itemView.findViewById(R.id.btn_complete);
        }
    }
}

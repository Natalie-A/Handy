package com.natalie.handy;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class PendingRequestsRecyclerViewAdapter extends RecyclerView.Adapter<PendingRequestsRecyclerViewAdapter.MyViewHolder> {
    private DatabaseReference mDatabaseRequests, mDatabaseHandypersons;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String handymanID, clientId;

    Context context;
    ArrayList<PendingRequest> arr;

    public PendingRequestsRecyclerViewAdapter(Context context, ArrayList<PendingRequest> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pending_request_items, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        clientId = firebaseUser.getUid();
        mDatabaseHandypersons = FirebaseDatabase.getInstance().getReference().child("handypersons");
        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("requests");
        final PendingRequest pendingRequest = arr.get(position);
        holder.nameTextView.setText(pendingRequest.getHandypersonName());
        holder.dateTextView.setText(pendingRequest.getRequestDate());
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri phoneUri = Uri.parse("tel:" + pendingRequest.getHandypersonPhone());
                Intent intent = new Intent(Intent.ACTION_DIAL, phoneUri);
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.d("ImplicitIntents", "Can't handle this intent!");
                }
            }
        });
        mDatabaseHandypersons.orderByChild("full_name").equalTo(pendingRequest.getHandypersonName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot handyperson: snapshot.getChildren()){
                    holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handymanID = handyperson.getKey();
                            mDatabaseRequests.orderByChild("client_handyman_status").equalTo(clientId+"_"+handymanID+"_waitingForAccept").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()){
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("status", "Cancelled");
                                        mDatabaseRequests.child(ds.getKey()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                //show a toast to indicate the request was accepted
                                                mDatabaseRequests.child(ds.getKey()).child("client_handyman_status").setValue(clientId+"_"+handymanID+"_cancelled");
                                                Toast.makeText(v.getContext(), "Request Updated", Toast.LENGTH_SHORT).show();
                                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                                Fragment myFragment = new HistoryFragment();
                                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
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
    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTextView, dateTextView;
        private Button btnCall, btnCancel;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.handyman_name);
            dateTextView = (TextView) itemView.findViewById(R.id.requestDate4);
            btnCall = (Button) itemView.findViewById(R.id.btn_call_handyperson);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel2);
        }
    }
}

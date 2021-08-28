package com.natalie.handy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<Service> serviceArrayList;
    private String serviceKey;
    private DatabaseReference mDatabaseService;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ProgressDialog Dialog = new ProgressDialog(getContext());
        Dialog.setMessage("Loading...");
        Dialog.show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        //init
        recyclerView = (RecyclerView) view.findViewById(R.id.servicesRecyclerView);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        mDatabaseService = FirebaseDatabase.getInstance().getReference().child("services");
        serviceArrayList = new ArrayList<>();
        mDatabaseService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Dialog.dismiss();
                    //serviceArrayList.add(new Service(item.child("service_name").getValue().toString()));
                    Service service = new Service(item.child("service_name").getValue().toString());
                    serviceArrayList.add(service);
                    recyclerViewAdapter = new RecyclerViewAdapter(getContext(), serviceArrayList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setHasFixedSize(true);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
        return view;
    }
}
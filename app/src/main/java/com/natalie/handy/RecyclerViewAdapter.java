package com.natalie.handy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>  {

    Context mContext;
    List<Service> arr;
    public static final String KEY_NAME = "name";

    public RecyclerViewAdapter(Context mContext, List<Service> arr) {
        this.mContext = mContext;
        this.arr = arr;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.service_items,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final Service currentService = arr.get(position);
        holder.serviceTextView.setText(currentService.getService_name());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Service service = arr.get(position);
                Intent skipIntent = new Intent(v.getContext(), HandymenActivity.class);
                //Use intent EXTRA to pas data from main activity to profile activity
                skipIntent.putExtra(KEY_NAME,service.getService_name());
                v.getContext().startActivity(skipIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView serviceTextView;
        private CardView cardView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            serviceTextView = (TextView) itemView.findViewById(R.id.service_name);
            cardView = (CardView) itemView.findViewById(R.id.service_selected);
        }
    }
}

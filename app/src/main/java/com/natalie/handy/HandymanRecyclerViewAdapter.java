package com.natalie.handy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HandymanRecyclerViewAdapter extends RecyclerView.Adapter<HandymanRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Handyman> arr;
    public static final String KEY_NAME = "name";

    public HandymanRecyclerViewAdapter(Context context, ArrayList<Handyman> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.handyman_items,parent,false);
        HandymanRecyclerViewAdapter.MyViewHolder myViewHolder = new HandymanRecyclerViewAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final Handyman currentHandyman = arr.get(position);
        holder.nameTextView.setText(currentHandyman.getHandymanName());
        holder.locationTextView.setText(currentHandyman.getHandymanLocation());
        holder.handymanRating.setRating(Float.parseFloat(currentHandyman.getRatingScore()));
        holder.ratingTextView.setText("Rating score: "+currentHandyman.getRatingScore());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handyman handyman = arr.get(position);
                Intent skipIntent = new Intent(v.getContext(), RequestActivity.class);
                //Use intent EXTRA to pas data from main activity to profile activity
                skipIntent.putExtra(KEY_NAME,handyman.getHandymanName());
                v.getContext().startActivity(skipIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView, locationTextView, ratingTextView;
        private CardView cardView;
        private RatingBar handymanRating;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.handymanName);
            locationTextView = (TextView) itemView.findViewById(R.id.handymanLocation);
            cardView = (CardView) itemView.findViewById(R.id.handymanSelected);
            handymanRating = (RatingBar) itemView.findViewById(R.id.rating_score2);
            ratingTextView = (TextView) itemView.findViewById(R.id.rating2);
        }
    }
}

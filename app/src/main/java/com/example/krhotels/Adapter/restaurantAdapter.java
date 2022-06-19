package com.example.krhotels.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.krhotels.DetailItemActivity;
import com.example.krhotels.Model.IndividualLocation2;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class restaurantAdapter extends RecyclerView.Adapter<restaurantAdapter.CustomViewHolder> {

    private Context context;
    private List<IndividualLocation2> listOfLocations;


    public restaurantAdapter(Context context, List<IndividualLocation2> listOfLocations) {
        this.context = context;
        this.listOfLocations = listOfLocations;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
       holder.item_name.setText(listOfLocations.get(position).getName());
       holder.item_address.setText(listOfLocations.get(position).getAddress());
       holder.item_distance.setText(listOfLocations.get(position).getDistance());
       holder.item_duration_driving.setText(listOfLocations.get(position).getDurationDriving());
       holder.item_duration_walking.setText(listOfLocations.get(position).getDurationWalking());
       Glide.with(context).load(listOfLocations.get(position).getImageUrl()).into(holder.item_image);


       holder.item_layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(context, DetailItemActivity.class);
               intent.putExtra("name", listOfLocations.get(position).getName());
               intent.putExtra("item", "restaurant");
               context.startActivity(intent);
           }
       });




    }

    @Override
    public int getItemCount() {
        return listOfLocations.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView item_name, item_address, item_duration_driving, item_duration_walking, item_distance;
        TextView item_rating;
        ImageView item_image;
        CardView item_layout;

        public CustomViewHolder(View view) {
            super(view);

            item_name = itemView.findViewById(R.id.item_name);
            item_address = itemView.findViewById(R.id.item_address);
            item_distance = itemView.findViewById(R.id.item_distance);
            item_duration_driving = itemView.findViewById(R.id.item_duration_driving);
            item_duration_walking = itemView.findViewById(R.id.item_duration_walking);
            item_rating = itemView.findViewById(R.id.item_rating);
            item_image = itemView.findViewById(R.id.item_image);
            item_layout = itemView.findViewById(R.id.item_layout);

        }
    }
    public void filterList(ArrayList<IndividualLocation2> filteredList) {
        listOfLocations = filteredList;
        notifyDataSetChanged();
    }
}
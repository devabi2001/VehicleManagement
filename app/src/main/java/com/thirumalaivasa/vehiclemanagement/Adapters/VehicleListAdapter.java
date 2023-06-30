package com.thirumalaivasa.vehiclemanagement.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.R;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;
import com.thirumalaivasa.vehiclemanagement.ViewVehicleActivity;

import java.util.ArrayList;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.VehicleListViewHolder> {

    Context context;
    ArrayList<VehicleData> vehicleDataArrayList;

    private final String TAG = "VehicleManagement";

    public VehicleListAdapter(Context context, ArrayList<VehicleData> vehicleDataArrayList) {
        this.context = context;
        this.vehicleDataArrayList = vehicleDataArrayList;

    }

    @NonNull
    @Override
    public VehicleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VehicleListViewHolder vvh;
        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_list_layout, parent, false);
        vvh = new VehicleListViewHolder(view);
        return vvh;
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleListViewHolder holder, int position) {

        int pos = position;
        String vehicleNumber = vehicleDataArrayList.get(pos).getRegistrationNumber();
        String vehicleClass = vehicleDataArrayList.get(pos).getVehicleClass();
        holder.licensePlate.setText(vehicleNumber);
        if(ImageData.getImage(vehicleNumber) != null) {
            Glide.with(context)
                    .load(ImageData.getImage(vehicleNumber))
                    .circleCrop()
                    .into(holder.vehicleImg);
        }else{
            if(vehicleClass.equalsIgnoreCase("LMV"))
                Glide.with(context).load(R.drawable.car_24).into(holder.vehicleImg);
            else if(vehicleClass.equalsIgnoreCase("LMTV"))
                Glide.with(context).load(R.drawable.bus_24).into(holder.vehicleImg);
            else if(vehicleClass.equalsIgnoreCase("2WN"))
                Glide.with(context).load(R.drawable.scooter_100).into(holder.vehicleImg);
            else if(vehicleClass.equalsIgnoreCase("LPV"))
                Glide.with(context).load(R.drawable.bus_24).into(holder.vehicleImg);
            else
                Glide.with(context).load(R.drawable.bus_24).into(holder.vehicleImg);
        }

        holder.vehicleCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewVehicleActivity.class);
            intent.putExtra("VehicleData", vehicleDataArrayList.get(pos));
            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return vehicleDataArrayList.size();
    }


    public static class VehicleListViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout vehicleCard;

        ImageView vehicleImg;

        TextView licensePlate;

        public VehicleListViewHolder(@NonNull View itemView) {
            super(itemView);

            vehicleCard = itemView.findViewById(R.id.vehicle_card_list);

            licensePlate = itemView.findViewById(R.id.vehicle_no_list);
            vehicleImg = itemView.findViewById(R.id.vehicle_img_list);
        }
    }


}

